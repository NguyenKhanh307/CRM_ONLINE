import { useCallback, useEffect, useRef, useState, type Dispatch, type SetStateAction } from 'react';
import { pricingService } from '@/features/chinh-sach-gia/services/pricingService';
import { formatNumber } from '@/shared/utils/number';
import type { LineItemRow, ProductOption } from './productLineItem';

/** Chờ bấy nhiêu mili giây sau lần gõ số lượng cuối cùng mới tra giá (gõ "10" không bắn 2 request). */
const DEBOUNCE_MS = 400;

/** Đơn giá + chiết khấu gốc của sản phẩm (khi không có chính sách giá, hoặc chính sách không phủ sản phẩm này). */
const basePriceOf = (productOptions: ProductOption[], productId: string) => {
    const opt = productOptions.find((o) => o.value === productId);
    return { unitPrice: opt?.price ?? 0, discountPct: 0 };
};

/**
 * Tra đơn giá dòng hàng theo chính sách giá đang chọn.
 *
 * Quy tắc: có chính sách và sản phẩm nằm trong chính sách (đủ `min_qty`) → **đơn giá/chiết khấu của
 * chính sách ghi đè** giá gốc; ngược lại → quay về giá gốc của sản phẩm.
 *
 * @param pricePolicyId  chính sách giá đang chọn (null = không áp dụng)
 * @param rows           danh sách dòng hàng hiện tại (chỉ đọc — để tính lại khi đổi chính sách)
 * @param productOptions danh sách sản phẩm (nguồn giá gốc)
 * @param onChange       setter dạng hàm của danh sách dòng hàng — bắt buộc là hàm cập nhật vì
 *                       kết quả tra giá về bất đồng bộ, patch theo mảng cũ sẽ ghi đè mất thao tác sau
 */
export function usePolicyPricing(
    pricePolicyId: number | null | undefined,
    rows: LineItemRow[],
    productOptions: ProductOption[],
    onChange: Dispatch<SetStateAction<LineItemRow[]>>,
) {
    /** Token của lần tra giá gần nhất theo từng dòng — phản hồi về trễ mà token cũ thì bỏ qua. */
    const tokens = useRef(new Map<string, number>());
    const timers = useRef(new Map<string, ReturnType<typeof setTimeout>>());
    /**
     * Lý do chính sách giá chưa áp cho từng dòng, hiển thị dưới ô hàng hóa.
     * Giữ ngoài `LineItemRow` để không lẫn vào payload gửi backend.
     */
    const [hints, setHints] = useState<Record<string, string>>({});

    const setHint = useCallback((rowId: string, text: string | null) => {
        setHints((prev) => {
            if (!text) {
                if (!(rowId in prev)) return prev;
                const next = { ...prev }; delete next[rowId]; return next;
            }
            return prev[rowId] === text ? prev : { ...prev, [rowId]: text };
        });
    }, []);

    // Neo tham số hay đổi vào ref để callback/effect không phải gắn lại mỗi lần render.
    const optionsRef = useRef(productOptions);
    optionsRef.current = productOptions;
    const policyRef = useRef(pricePolicyId);
    policyRef.current = pricePolicyId;
    const rowsRef = useRef(rows);
    rowsRef.current = rows;

    const patchRow = useCallback((rowId: string, patch: Partial<LineItemRow>) => {
        onChange((prev) => prev.map((r) => (r.id === rowId ? { ...r, ...patch } : r)));
    }, [onChange]);

    /** Tra giá cho một dòng rồi điền vào ô đơn giá / CK%. */
    const priceRow = useCallback((rowId: string, productId: string, quantity: number) => {
        // Bỏ chọn sản phẩm → dọn luôn chú thích cũ, đừng để treo lại trên dòng trống
        if (!productId) { setHint(rowId, null); return; }
        const token = (tokens.current.get(rowId) ?? 0) + 1;
        tokens.current.set(rowId, token);
        const policyId = policyRef.current;

        // Không có chính sách → giá gốc, không cần gọi API
        if (!policyId) {
            patchRow(rowId, basePriceOf(optionsRef.current, productId));
            setHint(rowId, null);
            return;
        }

        pricingService.resolve(policyId, Number(productId), quantity || 1)
            .then((res) => {
                if (tokens.current.get(rowId) !== token) return;   // đã có thao tác mới hơn
                const r = res.data.data;
                if (!r?.found || r.unitPrice == null) {
                    // Sản phẩm ngoài chính sách (hoặc chưa đủ số lượng tối thiểu) → về giá gốc,
                    // kèm lý do để người dùng không tưởng tính năng hỏng
                    patchRow(rowId, basePriceOf(optionsRef.current, productId));
                    setHint(rowId, r?.minQty != null
                        ? `Cần tối thiểu ${formatNumber(r.minQty)} để hưởng giá chính sách`
                        : 'Sản phẩm ngoài chính sách giá');
                    return;
                }
                const unitPrice = Number(r.unitPrice);
                // Chiết khấu backend trả về là số tiền trên một đơn vị → quy đổi sang % theo đơn giá
                const discountPct = r.discount != null && unitPrice > 0
                    ? +((Number(r.discount) / unitPrice) * 100).toFixed(2)
                    : 0;
                patchRow(rowId, { unitPrice, discountPct });
                setHint(rowId, null);
            })
            .catch(() => { /* lỗi mạng → giữ nguyên giá đang hiện, không phá dữ liệu người dùng */ });
    }, [patchRow, setHint]);

    // Xóa dòng → dọn chú thích của dòng đó
    useEffect(() => {
        setHints((prev) => {
            const alive = new Set(rows.map((r) => r.id));
            const stale = Object.keys(prev).filter((id) => !alive.has(id));
            if (stale.length === 0) return prev;
            const next = { ...prev };
            stale.forEach((id) => delete next[id]);
            return next;
        });
    }, [rows]);

    /** Bản debounce của priceRow — dùng cho ô số lượng. */
    const priceRowDebounced = useCallback((rowId: string, productId: string, quantity: number) => {
        clearTimeout(timers.current.get(rowId));
        timers.current.set(rowId, setTimeout(() => priceRow(rowId, productId, quantity), DEBOUNCE_MS));
    }, [priceRow]);

    // Dọn timer khi rời form
    useEffect(() => () => timers.current.forEach(clearTimeout), []);

    // Đổi chính sách giá → tính lại toàn bộ dòng đã chọn sản phẩm.
    //
    // Baseline lấy ngay lúc mount nên lần render đầu không tính lại — quan trọng với modal sửa:
    // mở một báo giá cũ mà tự tính lại là ghi đè mất giá đã chốt. Modal sửa nạp form (đặt
    // pricePolicyId) và nạp dòng hàng trong cùng một effect, mà React flush state trước khi phản hồi
    // mạng về, nên lúc effect này thấy chính sách đổi null→X thì danh sách dòng vẫn rỗng → không đụng
    // gì. ⚠️ Giữ nguyên thứ tự đó khi sửa các modal; đảo lại sẽ khiến mở form là giá tự nhảy.
    const prevPolicy = useRef(pricePolicyId);
    useEffect(() => {
        if (prevPolicy.current === pricePolicyId) return;
        prevPolicy.current = pricePolicyId;
        rowsRef.current.forEach((r) => { if (r.productId) priceRow(r.id, r.productId, r.quantity); });
    }, [pricePolicyId, priceRow]);

    return { priceRow, priceRowDebounced, hints };
}
