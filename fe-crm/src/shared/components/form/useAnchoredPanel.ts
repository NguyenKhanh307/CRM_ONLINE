import { useState, useRef, useEffect, useCallback, useLayoutEffect } from 'react';

// toạ độ + bề rộng để đặt panel nổi theo phần tử trigger (position: fixed)
export interface PanelRect {
    top: number;
    left: number;
    width: number;
}

// khoảng cách tối thiểu tới mép màn hình khi phải lật panel lên trên/kéo vào trong — cùng giá trị
// dùng ở SearchableSelect
const EDGE_GAP = 8;

// tính top đã lật đúng (nếu cần) từ bounding box trigger + chiều cao panel — hàm thuần, dùng lại ở
// cả `updateRect()` (mỗi lần cuộn/resize) lẫn layout effect (lần đo đầu tiên). Chỉ lật lên khi panel
// KHÔNG vừa phía dưới VÀ phía trên rộng hơn phía dưới — tránh kẹp cứng lên mép trên màn hình (che
// mất chính ô đang bấm) khi cả hai phía đều không đủ chỗ cho panel cao (vd Calendar ~300px+, cao hơn
// hẳn panel SearchableSelect ~200px mà công thức lật ban đầu chỉ kiểm chứng với panel đó)
function computeTop(trigger: DOMRect, height: number): number {
    const spaceBelow = window.innerHeight - EDGE_GAP - (trigger.bottom + 4);
    const spaceAbove = trigger.top - 4 - EDGE_GAP;
    const openUp = height > spaceBelow && spaceAbove > spaceBelow;
    return openUp ? Math.max(EDGE_GAP, trigger.top - height - 4) : trigger.bottom + 4;
}

// hook dùng chung để mở một panel nổi (portal, position: fixed) bám theo phần tử trigger
// tách từ pattern của SearchableSelect để tái dùng cho DateInput/DateTimeInput
// trả về ref gắn vào container trigger, ref gắn vào panel, trạng thái mở và toạ độ
export function useAnchoredPanel() {
    const [open, setOpen] = useState(false);
    const [rect, setRect] = useState<PanelRect | null>(null);
    const containerRef = useRef<HTMLDivElement>(null);
    const panelRef = useRef<HTMLDivElement>(null);
    // bounding box của trigger tại lần đo gần nhất — dùng để lật panel lên trên nếu tràn đáy
    const triggerRectRef = useRef<DOMRect | null>(null);
    // kích thước panel đã đo được lần gần nhất (đo ở layout effect) — dùng lại trong updateRect()
    // để tính đúng vị trí lật ngay từ đầu mỗi lần cuộn/resize, thay vì luôn reset về mặc định "phía
    // dưới" rồi chờ layout effect sửa lại (effect đó chỉ chạy lại theo left/width, cuộn dọc thuần túy
    // không đổi 2 giá trị này nên panel "rớt" về vị trí mặc định giữa lúc đang mở)
    const panelSizeRef = useRef<{ height: number; width: number } | null>(null);

    // tính lại vị trí panel từ bounding box của trigger — dùng kích thước panel đã biết (nếu có) để
    // lật đúng ngay từ lần tính đầu, chưa đo được thì tạm đặt phía dưới (layout effect sẽ đo + sửa)
    const updateRect = useCallback(() => {
        const el = containerRef.current;
        if (!el) return;
        const r = el.getBoundingClientRect();
        triggerRectRef.current = r;
        const size = panelSizeRef.current;
        if (!size) {
            setRect({ top: r.bottom + 4, left: r.left, width: r.width });
            return;
        }
        const top = computeTop(r, size.height);
        const maxLeft = window.innerWidth - EDGE_GAP - size.width;
        const left = Math.min(Math.max(EDGE_GAP, r.left), Math.max(EDGE_GAP, maxLeft));
        setRect({ top, left, width: r.width });
    }, []);

    // cập nhật vị trí khi mở + khi cuộn/resize để panel luôn bám theo trigger
    useEffect(() => {
        if (!open) return;
        updateRect();
        const onScrollOrResize = () => updateRect();
        window.addEventListener('scroll', onScrollOrResize, true);
        window.addEventListener('resize', onScrollOrResize);
        return () => {
            window.removeEventListener('scroll', onScrollOrResize, true);
            window.removeEventListener('resize', onScrollOrResize);
        };
    }, [open, updateRect]);

    // đóng thì xóa cache kích thước — mở lại lần sau đo lại từ đầu (nội dung panel có thể đã đổi)
    useEffect(() => {
        if (!open) panelSizeRef.current = null;
    }, [open]);

    // Sau khi panel đã render ở vị trí mặc định, đo kích thước thật rồi lật lên trên nếu tràn đáy
    // màn hình + kéo vào trong nếu tràn mép phải. Chạy trước khi trình duyệt vẽ để không nhấp nháy.
    // Không đưa `rect.top/left` vào dependency để tránh vòng lặp vô hạn.
    useLayoutEffect(() => {
        if (!open || !rect) return;
        const panelEl = panelRef.current;
        const trigger = triggerRectRef.current;
        if (!panelEl || !trigger) return;
        const panelRect = panelEl.getBoundingClientRect();
        const height = panelRect.height;
        const width = panelRect.width;
        panelSizeRef.current = { height, width };

        const top = computeTop(trigger, height);
        const maxLeft = window.innerWidth - EDGE_GAP - width;
        const left = Math.min(Math.max(EDGE_GAP, trigger.left), Math.max(EDGE_GAP, maxLeft));

        if (top !== rect.top || left !== rect.left) {
            setRect((prev) => (prev ? { ...prev, top, left } : prev));
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [open, rect?.left, rect?.width]);

    // click-outside: đóng khi click ngoài cả trigger lẫn panel (panel nằm ở portal)
    useEffect(() => {
        const handler = (e: MouseEvent) => {
            const target = e.target as Node;
            if (
                containerRef.current && !containerRef.current.contains(target) &&
                panelRef.current && !panelRef.current.contains(target)
            ) {
                setOpen(false);
            }
        };
        document.addEventListener('mousedown', handler);
        return () => document.removeEventListener('mousedown', handler);
    }, []);

    return { open, setOpen, rect, containerRef, panelRef };
}
