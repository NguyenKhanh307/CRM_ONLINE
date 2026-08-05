import { useCallback, useEffect, useRef, useState } from 'react';
import { QUALIFY_THRESHOLD, REQUEST_QUOTE_POINTS, STORAGE_KEY } from '../config/trackingDemoConfig';
import { trackingService, type RequestQuotePayload } from '../services/trackingService';
import type { LeadFormState, QuoteRequestItem, SessionEvent, TrackedLead } from '../types/trackingTypes';

// giờ phút hiện tại cho nhật ký hành vi
const now = () => new Date().toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit', second: '2-digit' });

// quản lý một phiên truy cập web tracking: mở phiên, ghi hành vi, nộp form, đặt lại
// mã tiềm năng lưu ở `localStorage` — đóng tab mở lại vẫn là cùng một khách, đúng như
// cookie tracking thật; muốn demo lại từ đầu thì gọi `reset()`
export function useTrackingSession() {
    const [lead, setLead] = useState<TrackedLead | null>(null);
    const [events, setEvents] = useState<SessionEvent[]>([]);
    const [busy, setBusy] = useState(false);
    const [message, setMessage] = useState<string | null>(null);
    // đã thử khôi phục phiên cũ từ localStorage hay chưa (tránh nháy giao diện lúc mới tải)
    const [restoring, setRestoring] = useState(true);
    // chặn StrictMode gọi effect 2 lần -> tránh tạo 2 tiềm năng song song
    const restoredRef = useRef(false);

    const apply = useCallback((data: TrackedLead | null) => {
        if (!data) return;
        // vượt ngưỡng lần đầu KHÔNG còn tự đổi trạng thái (đã bỏ qualified) — chỉ còn thông báo
        // cho người phụ trách + quản lý, nên so điểm cũ/mới để biết vừa vượt ngưỡng hay chưa
        setLead((prev) => {
            const prevScore = prev?.score ?? 0;
            if (prevScore <= QUALIFY_THRESHOLD && data.score > QUALIFY_THRESHOLD) {
                setMessage(
                    `Tiềm năng ${data.code} đã đạt ${data.score} điểm (ngưỡng ${QUALIFY_THRESHOLD}). `
                    + 'Người phụ trách và quản lý trực tiếp vừa nhận thông báo trong CRM.',
                );
            }
            return data;
        });
        if (data.code) localStorage.setItem(STORAGE_KEY, data.code);
    }, []);

    // Khôi phục phiên cũ nếu trình duyệt đã có mã; chưa có thì chờ khách chọn chiến dịch.
    useEffect(() => {
        if (restoredRef.current) return;
        restoredRef.current = true;
        const saved = localStorage.getItem(STORAGE_KEY);
        if (!saved) { setRestoring(false); return; }
        trackingService
            .visit(saved, null)
            .then((r) => apply(r.data.data))
            .catch(() => setMessage('Không kết nối được máy chủ tracking.'))
            .finally(() => setRestoring(false));
    }, [apply]);

    // mở phiên mới, gắn chiến dịch nguồn đọc từ `utm_campaign`
    const start = useCallback(async (campaignId: number | null) => {
        setBusy(true);
        try {
            const r = await trackingService.visit(localStorage.getItem(STORAGE_KEY), campaignId);
            apply(r.data.data);
        } finally {
            setBusy(false);
        }
    }, [apply]);

    // yêu cầu báo giá: gửi thông tin liên hệ + các sản phẩm đã chọn, ghi lead_items bên backend
    const requestQuote = useCallback(async (form: LeadFormState, items: QuoteRequestItem[]) => {
        if (!lead) return;
        setBusy(true);
        try {
            const payload: RequestQuotePayload = { code: lead.code, ...form, items };
            const prevScore = lead.score;
            const r = await trackingService.requestQuote(payload);
            apply(r.data.data);
            setEvents((prev) => [
                { label: `Yêu cầu báo giá ${items.length} sản phẩm`, points: REQUEST_QUOTE_POINTS, at: now() },
                ...prev,
            ]);
            // apply() đã tự hiện thông báo nếu vừa vượt ngưỡng điểm — không thì hiện lời cảm ơn thường
            const newScore = r.data.data?.score ?? prevScore;
            if (!(prevScore <= QUALIFY_THRESHOLD && newScore > QUALIFY_THRESHOLD)) {
                setMessage(`Cảm ơn bạn! Yêu cầu báo giá đã được ghi nhận (+${REQUEST_QUOTE_POINTS} điểm).`);
            }
        } finally {
            setBusy(false);
        }
    }, [lead, apply]);

    // ghi nhận lượt xem chi tiết một sản phẩm (im lặng, không set message tránh spam banner)
    const trackView = useCallback(async (productId: number) => {
        if (!lead) return;
        try {
            const r = await trackingService.viewProduct({ code: lead.code, productId });
            apply(r.data.data);
        } catch {
            // bỏ qua lỗi — chỉ là ghi nhận hành vi, không chặn việc xem chi tiết sản phẩm
        }
    }, [lead, apply]);

    // quên khách hiện tại để demo lại từ đầu với chiến dịch khác
    const reset = useCallback(() => {
        localStorage.removeItem(STORAGE_KEY);
        setLead(null);
        setEvents([]);
        setMessage(null);
    }, []);

    return { lead, events, busy, message, restoring, start, requestQuote, trackView, reset };
}
