import { useEffect, useRef, type RefObject } from 'react';

// phần tử khớp selector và đang hiển thị, theo thứ tự DOM
export const getVisibleElements = (container: HTMLElement, selector: string): HTMLElement[] =>
    Array.from(container.querySelectorAll<HTMLElement>(selector)).filter(el => el.offsetParent !== null);

// đưa focus vào một phần tử và bôi đen nội dung sẵn có (nếu là ô text)
export const focusAndSelect = (el: HTMLElement) => {
    el.focus();
    if (el instanceof HTMLInputElement || el instanceof HTMLTextAreaElement) {
        // ô number/date không cho select() trong một số trình duyệt
        try { el.select(); } catch { /* bỏ qua */ }
    }
};

// focus một phần tử sau khi DOM đã commit — cần requestAnimationFrame vì modal render sau overlay một nhịp
export const useRafFocus = (
    pick: () => HTMLElement | null | undefined,
    enabled: boolean,
    // bôi đen nội dung sẵn có — form cần, popup thì không
    select = false,
) => {
    const pickRef = useRef(pick);
    pickRef.current = pick;

    useEffect(() => {
        if (!enabled) return;
        const id = requestAnimationFrame(() => {
            const el = pickRef.current();
            if (!el) return;
            if (select) focusAndSelect(el); else el.focus();
        });
        return () => cancelAnimationFrame(id);
    }, [enabled, select]);
};

// gắn listener keydown lên container, tự gỡ khi unmount hoặc khi enabled tắt
export const useContainerKeydown = <T extends HTMLElement>(
    ref: RefObject<T | null>,
    handler: (e: KeyboardEvent) => void,
    enabled: boolean,
) => {
    const handlerRef = useRef(handler);
    handlerRef.current = handler;

    useEffect(() => {
        const container = ref.current;
        if (!container || !enabled) return;
        const listener = (e: KeyboardEvent) => handlerRef.current(e);
        container.addEventListener('keydown', listener);
        return () => container.removeEventListener('keydown', listener);
    }, [ref, enabled]);
};
