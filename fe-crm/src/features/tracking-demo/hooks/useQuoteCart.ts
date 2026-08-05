import { useCallback, useMemo, useState } from 'react';
import type { QuoteRequestItem } from '../types/trackingTypes';

// giỏ "yêu cầu báo giá" trên landing page — chỉ ở phía trình duyệt, chưa gửi backend cho tới khi
// khách bấm "Yêu cầu báo giá" (submit form liên hệ)
export function useQuoteCart() {
    const [quantities, setQuantities] = useState<Record<number, number>>({});

    const toggle = useCallback((productId: number) => {
        setQuantities((prev) => {
            if (prev[productId] != null) {
                const next = { ...prev };
                delete next[productId];
                return next;
            }
            return { ...prev, [productId]: 1 };
        });
    }, []);

    const setQuantity = useCallback((productId: number, quantity: number) => {
        setQuantities((prev) => (prev[productId] == null ? prev : { ...prev, [productId]: Math.max(1, quantity) }));
    }, []);

    const remove = useCallback((productId: number) => {
        setQuantities((prev) => {
            const next = { ...prev };
            delete next[productId];
            return next;
        });
    }, []);

    const clear = useCallback(() => setQuantities({}), []);

    const items = useMemo<QuoteRequestItem[]>(
        () => Object.entries(quantities).map(([productId, quantity]) => ({ productId: Number(productId), quantity })),
        [quantities],
    );

    return { quantities, toggle, setQuantity, remove, clear, items, count: items.length };
}
