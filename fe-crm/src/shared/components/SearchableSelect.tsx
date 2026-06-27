import { useState, useRef, useEffect, useCallback } from 'react';
import { createPortal } from 'react-dom';
import { FiSearch, FiCheck, FiChevronDown, FiChevronUp } from 'react-icons/fi';

export interface SelectOption {
    value: string;
    label: string;
}

interface SearchableSelectProps {
    options: SelectOption[];
    value: string;
    onChange: (value: string) => void;
    placeholder?: string;
    searchPlaceholder?: string;
    className?: string;
}

/** Toạ độ + bề rộng để đặt panel dropdown theo nút trigger (position: fixed). */
interface PanelRect {
    top: number;
    left: number;
    width: number;
}

/**
 * Custom select với ô tìm kiếm và highlight lựa chọn hiện tại.
 * Panel options render qua portal (position: fixed) để luôn nổi trên section/bảng/modal,
 * không bị overflow-hidden hay stacking context của component cha cắt/che.
 */
export const SearchableSelect = ({
    options,
    value,
    onChange,
    placeholder = '— Không chọn —',
    searchPlaceholder = 'Tìm kiếm',
    className = '',
}: SearchableSelectProps) => {
    const [open, setOpen] = useState(false);
    const [search, setSearch] = useState('');
    const [rect, setRect] = useState<PanelRect | null>(null);
    const containerRef = useRef<HTMLDivElement>(null);
    const panelRef = useRef<HTMLDivElement>(null);

    /** Tính lại vị trí panel từ bounding box của nút trigger. */
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
                setSearch('');
            }
        };
        document.addEventListener('mousedown', handler);
        return () => document.removeEventListener('mousedown', handler);
    }, []);

    const filtered = options.filter((o) =>
        o.label.toLowerCase().includes(search.toLowerCase()),
    );

    const selectedLabel = options.find((o) => o.value === value)?.label ?? '';

    const handleSelect = (optValue: string) => {
        onChange(optValue);
        setOpen(false);
        setSearch('');
    };

    const baseCls =
        'w-full border border-gray-300 rounded-btn px-3 py-1.5 text-md text-text-main bg-white ' +
        'focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-colors';

    return (
        <div ref={containerRef} className={`relative ${className}`}>
            {/* Trigger */}
            <button
                type="button"
                onClick={() => setOpen((prev) => !prev)}
                className={`${baseCls} flex items-center justify-between pr-8 text-left`}
            >
                <span className={selectedLabel ? 'text-text-main' : 'text-gray-400'}>
                    {selectedLabel || placeholder}
                </span>
                <span className="absolute inset-y-0 right-3 flex items-center text-gray-400 pointer-events-none">
                    {open ? <FiChevronUp size={14} /> : <FiChevronDown size={14} />}
                </span>
            </button>

            {/* Dropdown — portal ra body, position: fixed, z cao hơn cả modal (z-[9999]) */}
            {open && rect && createPortal(
                <div
                    ref={panelRef}
                    style={{ position: 'fixed', top: rect.top, left: rect.left, width: rect.width }}
                    className="z-[10000] bg-white border border-gray-200 rounded-section shadow-lg"
                >
                    {/* Search */}
                    <div className="flex items-center border-b border-gray-200 px-3 py-2 gap-2">
                        <input
                            autoFocus
                            type="text"
                            value={search}
                            onChange={(e) => setSearch(e.target.value)}
                            placeholder={searchPlaceholder}
                            className="flex-1 text-md text-text-main placeholder-gray-400 outline-none bg-transparent"
                        />
                        <FiSearch size={14} className="text-gray-400 flex-shrink-0" />
                    </div>

                    {/* Options */}
                    <ul className="max-h-52 overflow-y-auto py-1">
                        {filtered.length === 0 ? (
                            <li className="px-3 py-2 text-sm text-gray-400">Không tìm thấy</li>
                        ) : (
                            filtered.map((opt) => {
                                const isSelected = opt.value === value;
                                return (
                                    <li
                                        key={opt.value}
                                        onClick={() => handleSelect(opt.value)}
                                        className={`flex items-center justify-between px-3 py-2 cursor-pointer text-md hover:bg-gray-50 ${
                                            isSelected ? 'text-primary' : 'text-text-main'
                                        }`}
                                    >
                                        <span>{opt.label}</span>
                                        {isSelected && (
                                            <FiCheck size={14} className="text-primary flex-shrink-0" />
                                        )}
                                    </li>
                                );
                            })
                        )}
                    </ul>
                </div>,
                document.body,
            )}
        </div>
    );
};
