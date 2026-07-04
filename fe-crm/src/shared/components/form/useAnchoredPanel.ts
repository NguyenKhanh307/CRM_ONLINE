import { useState, useRef, useEffect, useCallback } from 'react';

/** Toạ độ + bề rộng để đặt panel nổi theo phần tử trigger (position: fixed). */
export interface PanelRect {
    top: number;
    left: number;
    width: number;
}

/**
 * Hook dùng chung để mở một panel nổi (portal, position: fixed) bám theo phần tử trigger.
 * Tách từ pattern của SearchableSelect để tái dùng cho DateInput/DateTimeInput.
 * Trả về ref gắn vào container trigger, ref gắn vào panel, trạng thái mở và toạ độ.
 */
export function useAnchoredPanel() {
    const [open, setOpen] = useState(false);
    const [rect, setRect] = useState<PanelRect | null>(null);
    const containerRef = useRef<HTMLDivElement>(null);
    const panelRef = useRef<HTMLDivElement>(null);

    /** Tính lại vị trí panel từ bounding box của trigger. */
    const updateRect = useCallback(() => {
        const el = containerRef.current;
        if (!el) return;
        const r = el.getBoundingClientRect();
        setRect({ top: r.bottom + 4, left: r.left, width: r.width });
    }, []);

    // Cập nhật vị trí khi mở + khi cuộn/resize để panel luôn bám theo trigger.
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

    // Click-outside: đóng khi click ngoài cả trigger lẫn panel (panel nằm ở portal).
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
