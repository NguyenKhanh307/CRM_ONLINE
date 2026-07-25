import { useCallback, useEffect, useRef, useState } from 'react';
import { QUALIFY_THRESHOLD, STORAGE_KEY, SUBMIT_POINTS } from '../config/trackingDemoConfig';
import { trackingService, type SubmitTrackingPayload } from '../services/trackingService';
import type { LeadFormState, SessionEvent, TrackedLead } from '../types/trackingTypes';

/** Giờ phút hiện tại cho nhật ký hành vi. */
const now = () => new Date().toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit', second: '2-digit' });

/**
 * Quản lý một phiên truy cập web tracking: mở phiên, ghi hành vi, nộp form, đặt lại.
 *
 * Mã tiềm năng lưu ở `localStorage` — đóng tab mở lại vẫn là cùng một khách,
 * đúng như cookie tracking thật. Muốn demo lại từ đầu thì gọi `reset()`.
 */
export function useTrackingSession() {
    const [lead, setLead] = useState<TrackedLead | null>(null);
    const [events, setEvents] = useState<SessionEvent[]>([]);
    const [busy, setBusy] = useState(false);
    const [message, setMessage] = useState<string | null>(null);
    /** Đã thử khôi phục phiên cũ từ localStorage hay chưa (tránh nháy giao diện lúc mới tải). */
    const [restoring, setRestoring] = useState(true);
    /** Chặn StrictMode gọi effect 2 lần → tránh tạo 2 tiềm năng song song. */
    const restoredRef = useRef(false);

    const apply = useCallback((data: TrackedLead | null) => {
        if (!data) return;
        setLead(data);
        if (data.code) localStorage.setItem(STORAGE_KEY, data.code);
        if (data.status === 'qualified') {
            setMessage(
                `Tiềm năng ${data.code} đã đạt ${data.score} điểm (ngưỡng ${QUALIFY_THRESHOLD}) → tự chuyển sang "qualified". `
                + 'Người phụ trách và quản lý trực tiếp vừa nhận thông báo trong CRM.',
            );
        }
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

    /** Mở phiên mới, gắn chiến dịch nguồn đọc từ `utm_campaign`. */
    const start = useCallback(async (campaignId: number | null) => {
        setBusy(true);
        try {
            const r = await trackingService.visit(localStorage.getItem(STORAGE_KEY), campaignId);
            apply(r.data.data);
        } finally {
            setBusy(false);
        }
    }, [apply]);

    /** Ghi một hành vi và cộng điểm. */
    const track = useCallback(async (action: string, label: string, points: number) => {
        if (!lead) return;
        setBusy(true);
        try {
            const r = await trackingService.score(lead.code, action, label, points);
            apply(r.data.data);
            setEvents((prev) => [{ label, points, at: now() }, ...prev]);
        } finally {
            setBusy(false);
        }
    }, [lead, apply]);

    /** Nộp form liên hệ: điền thông tin thật vào tiềm năng ẩn danh. */
    const submit = useCallback(async (form: LeadFormState) => {
        if (!lead) return;
        setBusy(true);
        try {
            const payload: SubmitTrackingPayload = { code: lead.code, ...form, points: SUBMIT_POINTS };
            const r = await trackingService.submit(payload);
            apply(r.data.data);
            setEvents((prev) => [{ label: 'Nộp form liên hệ', points: SUBMIT_POINTS, at: now() }, ...prev]);
            if (r.data.data?.status !== 'qualified') {
                setMessage(`Cảm ơn bạn! Thông tin đã được ghi nhận (+${SUBMIT_POINTS} điểm).`);
            }
        } finally {
            setBusy(false);
        }
    }, [lead, apply]);

    /** Quên khách hiện tại để demo lại từ đầu với chiến dịch khác. */
    const reset = useCallback(() => {
        localStorage.removeItem(STORAGE_KEY);
        setLead(null);
        setEvents([]);
        setMessage(null);
    }, []);

    return { lead, events, busy, message, restoring, start, track, submit, reset };
}
