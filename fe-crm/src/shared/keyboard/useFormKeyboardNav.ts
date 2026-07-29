import { useRef, type RefObject } from 'react';
import { focusAndSelect, getVisibleElements, useContainerKeydown, useRafFocus } from './focusHelpers';
import { ARROW_NEXT, ARROW_PREV, SHORTCUTS, matchesShortcut } from './shortcuts';

/**
 * Các ô nhập được tính vào chuỗi điều hướng.
 * `[data-form-field]` cho phép control tự dựng (SearchableSelect) tham gia.
 */
const FIELD_SELECTOR =
    'input:not([type=hidden]):not([disabled]), textarea:not([disabled]), select:not([disabled]), [data-form-field]';

/** Kiểu input mà con trỏ chạy được bên trong (có selectionStart). */
const TEXT_INPUT_TYPES = new Set(['text', 'search', 'url', 'tel', 'password', 'email', 'number']);

interface Options {
    /** Gọi khi Enter ở ô cuối, hoặc khi nhấn Ctrl+S. */
    onSubmit?: () => void;
    /** Gọi khi nhấn Esc. */
    onCancel?: () => void;
    /** Tự focus ô đầu tiên khi mount. Mặc định bật. */
    autoFocus?: boolean;
    /**
     * Chỉ gắn listener khi form đã render. Modal chỉnh sửa render rỗng lúc chưa có bản ghi,
     * nên phải truyền `!!item` để hook gắn lại khi form xuất hiện.
     */
    enabled?: boolean;
}

/**
 * Con trỏ đã chạm biên ô và không bôi đen text?
 * Dùng để mũi tên dọc trong textarea chỉ rời ô khi không còn gì để di chuyển bên trong.
 */
const isCaretAtEdge = (el: HTMLElement, toEnd: boolean): boolean => {
    const isText = el instanceof HTMLTextAreaElement
        || (el instanceof HTMLInputElement && TEXT_INPUT_TYPES.has(el.type));
    if (!isText) return true;

    const input = el as HTMLInputElement | HTMLTextAreaElement;
    // Ô number không cho đọc selectionStart → coi như luôn ở biên.
    let start: number | null;
    let end: number | null;
    try {
        start = input.selectionStart;
        end = input.selectionEnd;
    } catch {
        return true;
    }
    if (start === null || end === null) return true;
    if (start !== end) return false; // đang bôi đen → nhường cho con trỏ

    return toEnd ? start === input.value.length : start === 0;
};

/**
 * Điều hướng bàn phím trong một form (trang thêm mới hoặc modal chỉnh sửa).
 *
 * - Tự focus ô đầu tiên khi mở form.
 * - `Enter` chuyển sang ô kế tiếp; ở ô cuối thì submit.
 * - `↑` `↓` đổi focus (trong textarea chỉ đổi khi con trỏ đã ở dòng đầu/cuối).
 * - `Ctrl+S` lưu, `Esc` hủy.
 *
 * Enter được bỏ qua trong textarea và trình soạn thảo TinyMCE — ở đó Enter phải xuống dòng,
 * người dùng rời ô bằng `↓`.
 */
export function useFormKeyboardNav<T extends HTMLElement>(
    ref: RefObject<T | null>,
    { onSubmit, onCancel, autoFocus = true, enabled = true }: Options = {},
) {
    // Neo callback vào ref để listener không phải gắn lại mỗi lần render.
    const cbRef = useRef({ onSubmit, onCancel });
    cbRef.current = { onSubmit, onCancel };

    useRafFocus(() => ref.current?.querySelector<HTMLElement>(FIELD_SELECTOR), autoFocus && enabled, true);

    useContainerKeydown(ref, (e) => {
        const container = ref.current;
        if (!container) return;
        const target = e.target as HTMLElement;

        if (e.key === 'Escape') {
            cbRef.current.onCancel?.();
            return;
        }

        if (matchesShortcut(e, SHORTCUTS.SAVE)) {
            e.preventDefault();
            cbRef.current.onSubmit?.();
            return;
        }

        const fields = () => getVisibleElements(container, FIELD_SELECTOR);

        const isNext = ARROW_NEXT.includes(e.key);
        const isPrev = ARROW_PREV.includes(e.key);
        if ((isNext || isPrev) && !e.altKey && !e.ctrlKey && !e.metaKey) {
            // Mũi tên trong TinyMCE thuộc về trình soạn thảo.
            if (target.isContentEditable || target.closest('.tox')) return;

            // Mũi tên dọc trong textarea nhường cho con trỏ khi chưa chạm biên ô (đổi dòng trước).
            const checkEdge = target.tagName === 'TEXTAREA';
            if (checkEdge && !isCaretAtEdge(target, isNext)) return;

            // preventDefault chặn ô number tăng/giảm giá trị và <select> đổi option.
            e.preventDefault();
            const list = fields();
            const i = list.indexOf(target);
            if (i === -1) return;
            const next = list[i + (isNext ? 1 : -1)];
            if (next) focusAndSelect(next);
            return;
        }

        if (e.key === 'Enter' && !e.shiftKey && !e.ctrlKey && !e.altKey) {
            // Enter phải xuống dòng trong textarea và trong TinyMCE.
            if (target.tagName === 'TEXTAREA' || target.isContentEditable || target.closest('.tox')) return;

            const list = fields();
            const i = list.indexOf(target);
            if (i === -1) return;

            e.preventDefault();
            if (i === list.length - 1) cbRef.current.onSubmit?.();
            else focusAndSelect(list[i + 1]);
        }
    }, enabled);
}
