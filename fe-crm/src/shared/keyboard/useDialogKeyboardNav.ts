import { useEffect, useRef, type RefObject } from 'react';
import { useContainerKeydown, useRafFocus } from './focusHelpers';
import { ARROW_ALL, ARROW_NEXT } from './shortcuts';

/** Nút footer tham gia điều hướng — đánh dấu bằng `data-dialog-button`. */
const BUTTON_SELECTOR = '[data-dialog-button]:not([disabled])';

interface Options {
    /** Gọi khi nhấn Esc. */
    onCancel?: () => void;
    /**
     * Ô/nút được focus khi popup mở.
     * `'confirm'` = nút cuối (thường là nút xác nhận), `'cancel'` = nút đầu,
     * `'none'` = không tự focus (dùng khi popup có ô nhập cần focus trước).
     */
    autoFocus?: 'confirm' | 'cancel' | 'none';
    enabled?: boolean;
}

/**
 * Điều hướng bàn phím cho popup xác nhận: mũi tên ↑↓ trần đổi qua lại giữa các nút footer.
 *
 * Chỉ bắt phím khi focus đang nằm trên một nút — nếu người dùng đang gõ trong ô nhập
 * (textarea lý do, select người nhận…) thì mũi tên vẫn thuộc về ô đó.
 * `Enter` không cần xử lý: trình duyệt tự kích hoạt nút đang focus.
 */
export function useDialogKeyboardNav<T extends HTMLElement>(
    ref: RefObject<T | null>,
    { onCancel, autoFocus = 'confirm', enabled = true }: Options = {},
) {
    const cbRef = useRef({ onCancel });
    cbRef.current = { onCancel };

    useRafFocus(() => {
        const buttons = ref.current?.querySelectorAll<HTMLElement>(BUTTON_SELECTOR);
        if (!buttons?.length) return null;
        return autoFocus === 'cancel' ? buttons[0] : buttons[buttons.length - 1];
    }, enabled && autoFocus !== 'none');

    // Esc gắn ở document: popup mở với autoFocus='none' chưa có gì được focus bên trong,
    // listener trên container sẽ không bao giờ nhận được phím.
    useEffect(() => {
        if (!enabled) return;
        const onEsc = (e: KeyboardEvent) => {
            if (e.key === 'Escape') cbRef.current.onCancel?.();
        };
        document.addEventListener('keydown', onEsc);
        return () => document.removeEventListener('keydown', onEsc);
    }, [enabled]);

    useContainerKeydown(ref, (e) => {
        const container = ref.current;
        if (!container) return;

        // Popup thường mở ra do chính một phím Enter. Nếu người dùng GIỮ Enter,
        // keydown lặp lại sẽ bấm luôn nút vừa được focus — đúng cái popup sinh ra để ngăn.
        if (e.key === 'Enter' && e.repeat) {
            e.preventDefault();
            return;
        }

        if (!ARROW_ALL.includes(e.key) || e.altKey || e.ctrlKey || e.metaKey) return;

        const target = e.target as HTMLElement;
        // Đang gõ trong ô nhập → mũi tên thuộc về ô đó, không đổi nút.
        if (!target.matches(BUTTON_SELECTOR)) return;

        const buttons = Array.from(container.querySelectorAll<HTMLElement>(BUTTON_SELECTOR));
        const i = buttons.indexOf(target);
        if (i === -1 || buttons.length < 2) return;

        e.preventDefault();
        const step = ARROW_NEXT.includes(e.key) ? 1 : -1;
        // Đi vòng tròn để hai nút luôn với tới nhau bằng bất kỳ mũi tên nào.
        buttons[(i + step + buttons.length) % buttons.length].focus();
    }, enabled);
}
