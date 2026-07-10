/**
 * Nguồn sự thật duy nhất cho phím tắt.
 *
 * Mỗi phím tắt khai báo một lần ở đây, rồi được ba nơi tiêu thụ:
 * - `PageShortcutsProvider` khớp phím qua `matchesShortcut`
 * - `ShortcutsPopup` sinh bảng hiển thị từ `description`
 * - `ActionButton` hiển thị nhãn qua `keys`
 *
 * Đổi một phím tắt chỉ cần sửa ở file này.
 */
export interface ShortcutDef {
    /** Nhãn hiển thị cho người dùng, ví dụ ['Alt', 'N']. */
    keys: string[];
    /** `KeyboardEvent.code` — dùng mã phím vật lý để không lệ thuộc layout bàn phím. */
    code?: string;
    alt?: boolean;
    ctrl?: boolean;
    shift?: boolean;
    /** Dòng mô tả trong bảng phím tắt. */
    description: string;
}

export const SHORTCUTS = {
    CREATE:   { keys: ['Alt', 'N'],           code: 'KeyN',      alt: true,               description: 'Tạo mới' },
    SEARCH:   { keys: ['Alt', 'F'],           code: 'KeyF',      alt: true,               description: 'Tìm kiếm nhanh' },
    SEARCH_2: { keys: ['Ctrl', 'K'],          code: 'KeyK',      ctrl: true,              description: 'Tìm kiếm nhanh' },
    HOME:     { keys: ['Alt', 'H'],           code: 'KeyH',      alt: true,               description: 'Về trang chủ' },
    BACK:     { keys: ['Alt', '←'],           code: 'ArrowLeft', alt: true,               description: 'Quay lại trang trước' },
    HELP:     { keys: ['Ctrl', '/'],          code: 'Slash',     ctrl: true,              description: 'Mở danh sách phím tắt' },
    LOGOUT:   { keys: ['Ctrl', 'Shift', 'L'], code: 'KeyL',      ctrl: true, shift: true, description: 'Đăng xuất' },
    SAVE:     { keys: ['Ctrl', 'S'],          code: 'KeyS',      ctrl: true,              description: 'Lưu thay đổi' },
    CANCEL:   { keys: ['Esc'],                                                            description: 'Đóng popup / modal' },
    CONFIRM:  { keys: ['Enter'],                                                          description: 'Xác nhận' },
} satisfies Record<string, ShortcutDef>;

/** Phím tắt trong form nhập liệu — không có handler toàn cục, chỉ để hiển thị. */
export const FORM_SHORTCUTS: ShortcutDef[] = [
    { keys: ['Enter'], description: 'Sang ô kế tiếp (ô cuối thì lưu)' },
    { keys: ['↑', '↓'], description: 'Di chuyển giữa các ô' },
    { keys: ['←', '→'], description: 'Sang ô kế khi con trỏ ở đầu/cuối ô' },
    { keys: ['↑↓←→'], description: 'Trong popup: đổi giữa các nút' },
];

/** Thứ tự hiển thị trong bảng phím tắt toàn cục. */
export const GLOBAL_SHORTCUTS: ShortcutDef[] = [
    SHORTCUTS.SEARCH_2, SHORTCUTS.SEARCH, SHORTCUTS.CREATE, SHORTCUTS.HOME,
    SHORTCUTS.HELP, SHORTCUTS.LOGOUT, SHORTCUTS.CANCEL, SHORTCUTS.SAVE, SHORTCUTS.BACK,
];

/**
 * Khớp chính xác cả modifier — `Alt+N` không nhận khi đang giữ thêm `Ctrl` hoặc `Shift`.
 * Phím tắt không có `code` (Esc, Enter) không dùng hàm này; chúng được bắt bằng `e.key`.
 */
export const matchesShortcut = (e: KeyboardEvent, s: ShortcutDef): boolean =>
    !!s.code
    && e.code === s.code
    && e.altKey === !!s.alt
    && e.ctrlKey === !!s.ctrl
    && e.shiftKey === !!s.shift;

/** Mũi tên đi tới / lùi trong chuỗi điều hướng. */
export const ARROW_NEXT = ['ArrowDown', 'ArrowRight'];
export const ARROW_PREV = ['ArrowUp', 'ArrowLeft'];
/** Mũi tên ngang — cần kiểm tra biên con trỏ trước khi rời ô. */
export const ARROW_HORIZONTAL = ['ArrowLeft', 'ArrowRight'];
export const ARROW_ALL = [...ARROW_NEXT, ...ARROW_PREV];
