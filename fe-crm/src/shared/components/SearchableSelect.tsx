import { useState, useRef, useEffect } from 'react';
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

/**
 * Custom select với ô tìm kiếm và highlight lựa chọn hiện tại.
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
    const containerRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        const handler = (e: MouseEvent) => {
            if (containerRef.current && !containerRef.current.contains(e.target as Node)) {
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

            {/* Dropdown */}
            {open && (
                <div className="absolute z-50 mt-1 w-full bg-white border border-gray-200 rounded-section shadow-lg">
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
                </div>
            )}
        </div>
    );
};
