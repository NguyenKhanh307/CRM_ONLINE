import { createContext, useContext, useEffect, useRef, useState, type ReactNode, type RefObject } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@/core/auth/useAuth';
import { SHORTCUTS, matchesShortcut } from './shortcuts';

/** Hành động mà trang hiện tại đăng ký cho phím tắt toàn cục. */
export interface PageActions {
    /** Gắn với Alt+N. Trang không có khái niệm "thêm mới" thì bỏ trống. */
    onCreate?: () => void;
}

interface Ctx {
    actionsRef: RefObject<PageActions>;
    shortcutsOpen: boolean;
    setShortcutsOpen: (v: boolean | ((prev: boolean) => boolean)) => void;
}

const PageShortcutsContext = createContext<Ctx | null>(null);

/** Truy cập context; ném lỗi rõ ràng nếu dùng ngoài provider. */
const useCtx = () => {
    const ctx = useContext(PageShortcutsContext);
    if (!ctx) throw new Error('usePageShortcuts phải nằm trong <PageShortcutsProvider>');
    return ctx;
};

/**
 * Đăng ký hành động của trang hiện tại cho phím tắt toàn cục.
 * Hành động được ghi vào ref nên không gây re-render, và tự gỡ khi trang unmount.
 */
export const usePageShortcuts = (actions: PageActions) => {
    const { actionsRef } = useCtx();
    useEffect(() => {
        actionsRef.current = actions;
        return () => { actionsRef.current = {}; };
    });
};

/** Trạng thái popup phím tắt — để Ctrl+/ mở được nó từ bất kỳ đâu. */
export const useShortcutsPopup = () => {
    const { shortcutsOpen, setShortcutsOpen } = useCtx();
    return { shortcutsOpen, setShortcutsOpen };
};

/** Đưa con trỏ vào ô tìm kiếm của bảng trên trang hiện tại. */
const focusTableSearch = () => {
    const el = document.querySelector<HTMLInputElement>('[data-table-search]');
    if (!el) return false;
    el.focus();
    el.select();
    return true;
};

/**
 * Lắng nghe phím tắt toàn cục ở một chỗ duy nhất.
 * Định nghĩa phím nằm ở `shortcuts.ts` — cả bảng hiển thị lẫn nhãn trên nút đều đọc từ đó.
 */
export const PageShortcutsProvider = ({ children }: { children: ReactNode }) => {
    const navigate = useNavigate();
    const { logout } = useAuth();
    const actionsRef = useRef<PageActions>({});
    const [shortcutsOpen, setShortcutsOpen] = useState(false);

    useEffect(() => {
        const handler = (e: KeyboardEvent) => {
            const t = e.target as HTMLElement | null;
            const typing = !!t && (t.tagName === 'INPUT' || t.tagName === 'TEXTAREA' || t.isContentEditable);

            // Tìm kiếm nhanh — vẫn nhận phím ngay cả khi đang gõ trong ô khác.
            if (matchesShortcut(e, SHORTCUTS.SEARCH) || matchesShortcut(e, SHORTCUTS.SEARCH_2)) {
                if (focusTableSearch()) e.preventDefault();
                return;
            }

            if (matchesShortcut(e, SHORTCUTS.LOGOUT)) {
                e.preventDefault();
                logout();
                return;
            }

            if (matchesShortcut(e, SHORTCUTS.HELP)) {
                e.preventDefault();
                setShortcutsOpen(v => !v);
                return;
            }

            // Các phím còn lại nhường cho ô nhập khi người dùng đang gõ.
            if (typing) return;

            if (matchesShortcut(e, SHORTCUTS.CREATE)) {
                const onCreate = actionsRef.current.onCreate;
                if (onCreate) { e.preventDefault(); onCreate(); }
                return;
            }

            if (matchesShortcut(e, SHORTCUTS.HOME)) {
                e.preventDefault();
                navigate('/dashboard');
                return;
            }

            if (matchesShortcut(e, SHORTCUTS.BACK)) {
                e.preventDefault();
                navigate(-1);
            }
        };

        document.addEventListener('keydown', handler);
        return () => document.removeEventListener('keydown', handler);
    }, [navigate, logout]);

    return (
        <PageShortcutsContext.Provider value={{ actionsRef, shortcutsOpen, setShortcutsOpen }}>
            {children}
        </PageShortcutsContext.Provider>
    );
};
