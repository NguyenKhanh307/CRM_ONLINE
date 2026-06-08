import { useRef, useEffect, useState } from 'react';
import { FiHelpCircle } from 'react-icons/fi';

const SHORTCUTS = [
    { keys: ['Ctrl', 'K'], description: 'Tìm kiếm nhanh' },
    { keys: ['Alt', 'N'], description: 'Tạo mới' },
    { keys: ['Alt', 'H'], description: 'Về trang chủ' },
    { keys: ['Ctrl', '/'], description: 'Mở danh sách phím tắt' },
    { keys: ['Ctrl', 'Shift', 'L'], description: 'Đăng xuất' },
    { keys: ['Esc'], description: 'Đóng popup / hủy thao tác' },
    { keys: ['Ctrl', 'S'], description: 'Lưu thay đổi' },
    { keys: ['Alt', 'ArrowLeft'], description: 'Quay lại trang trước' },
];

interface Props {
    onClose: () => void;
}

const ShortcutsPopup = ({ onClose }: Props) => {
    const ref = useRef<HTMLDivElement>(null);

    useEffect(() => {
        const handler = (e: MouseEvent) => {
            if (ref.current && !ref.current.contains(e.target as Node)) {
                onClose();
            }
        };
        document.addEventListener('mousedown', handler);
        return () => document.removeEventListener('mousedown', handler);
    }, [onClose]);

    return (
        <div
            ref={ref}
            className="absolute right-0 top-full mt-2 w-[320px] bg-white rounded-card shadow-lg border border-gray-200 z-50"
        >
            <div className="px-4 py-3 border-b border-gray-100">
                <span className="font-semibold text-md text-text-main">Phím tắt</span>
            </div>
            <ul className="py-2">
                {SHORTCUTS.map(({ keys, description }) => (
                    <li
                        key={description}
                        className="flex items-center justify-between px-4 py-2 hover:bg-gray-50"
                    >
                        <span className="text-sm text-gray-600">{description}</span>
                        <div className="flex items-center gap-1">
                            {keys.map((k, i) => (
                                <span key={i} className="flex items-center gap-1">
                                    <kbd className="px-1.5 py-0.5 text-[11px] font-mono bg-gray-100 border border-gray-300 rounded text-gray-700">
                                        {k}
                                    </kbd>
                                    {i < keys.length - 1 && (
                                        <span className="text-gray-400 text-xs">+</span>
                                    )}
                                </span>
                            ))}
                        </div>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export const ShortcutsButton = () => {
    const [open, setOpen] = useState(false);

    return (
        <div className="relative">
            <button
                onClick={() => setOpen(v => !v)}
                className={`p-2 rounded hover:bg-gray-100 ${open ? 'text-primary' : 'text-gray-500 hover:text-text-main'}`}
                title="Phím tắt"
            >
                <FiHelpCircle size={18} />
            </button>
            {open && <ShortcutsPopup onClose={() => setOpen(false)} />}
        </div>
    );
};
