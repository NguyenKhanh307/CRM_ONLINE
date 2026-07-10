import { useRef, useEffect } from 'react';
import { FiHelpCircle } from 'react-icons/fi';
import { Kbd } from '@/shared/components/Kbd';
import { useShortcutsPopup } from '@/shared/keyboard/PageShortcutsProvider';
import { FORM_SHORTCUTS, GLOBAL_SHORTCUTS, type ShortcutDef } from '@/shared/keyboard/shortcuts';

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

    // Esc đóng popup — khớp với dòng "Đóng popup" trong chính bảng này.
    useEffect(() => {
        const handler = (e: KeyboardEvent) => { if (e.key === 'Escape') onClose(); };
        document.addEventListener('keydown', handler);
        return () => document.removeEventListener('keydown', handler);
    }, [onClose]);

    const row = ({ keys, description }: ShortcutDef) => (
        <li
            key={`${description}-${keys.join('')}`}
            className="flex items-center justify-between px-4 py-2 hover:bg-gray-50"
        >
            <span className="text-sm text-gray-600">{description}</span>
            <Kbd keys={keys} />
        </li>
    );

    return (
        <div
            ref={ref}
            className="absolute right-0 top-full mt-2 w-[340px] bg-white rounded-card shadow-lg border border-gray-200 z-50"
        >
            <div className="px-4 py-3 border-b border-gray-100">
                <span className="font-semibold text-md text-text-main">Phím tắt</span>
            </div>
            <ul className="py-2">{GLOBAL_SHORTCUTS.map(row)}</ul>
            <div className="px-4 py-2 border-t border-gray-100">
                <span className="text-sm font-medium text-gray-500">Trong form nhập liệu</span>
            </div>
            <ul className="pb-2">{FORM_SHORTCUTS.map(row)}</ul>
        </div>
    );
};

export const ShortcutsButton = () => {
    const { shortcutsOpen, setShortcutsOpen } = useShortcutsPopup();

    return (
        <div className="relative">
            <button
                onClick={() => setShortcutsOpen(v => !v)}
                className={`p-2 rounded hover:bg-gray-100 ${shortcutsOpen ? 'text-primary' : 'text-gray-500 hover:text-text-main'}`}
                title="Phím tắt (Ctrl + /)"
            >
                <FiHelpCircle size={18} />
            </button>
            {shortcutsOpen && <ShortcutsPopup onClose={() => setShortcutsOpen(false)} />}
        </div>
    );
};
