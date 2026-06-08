import { useState, useRef, useEffect } from 'react';
import { FiSearch, FiCheck, FiChevronDown, FiChevronUp } from 'react-icons/fi';
import type { TrashModuleConfig } from '@/features/thung-rac/types/thungRacTypes';

interface Props {
    modules: TrashModuleConfig[];
    selectedId: string;
    onChange: (id: string) => void;
}

/** Dropdown chọn phân hệ hiển thị trong thùng rác — có ô tìm kiếm và checkmark. */
const ModuleSelectDropdown = ({ modules, selectedId, onChange }: Props) => {
    const [isOpen, setIsOpen] = useState(false);
    const [search, setSearch] = useState('');
    const containerRef = useRef<HTMLDivElement>(null);

    const selectedModule = modules.find(m => m.id === selectedId);

    const filtered = modules.filter(m =>
        m.label.toLowerCase().includes(search.toLowerCase())
    );

    useEffect(() => {
        const handler = (e: MouseEvent) => {
            if (containerRef.current && !containerRef.current.contains(e.target as Node)) {
                setIsOpen(false);
                setSearch('');
            }
        };
        document.addEventListener('mousedown', handler);
        return () => document.removeEventListener('mousedown', handler);
    }, []);

    const handleSelect = (id: string) => {
        onChange(id);
        setIsOpen(false);
        setSearch('');
    };

    return (
        <div ref={containerRef} className="relative">
            {/* Trigger button */}
            <button
                className="flex items-center gap-2 px-3 py-1.5 bg-white border border-gray-300 rounded-btn text-md text-text-main hover:border-primary transition-colors min-w-[160px] justify-between"
                onClick={() => setIsOpen(prev => !prev)}
            >
                <span className="font-medium">{selectedModule?.label ?? '—'}</span>
                {isOpen ? <FiChevronUp size={14} className="text-gray-400 shrink-0" /> : <FiChevronDown size={14} className="text-gray-400 shrink-0" />}
            </button>

            {/* Dropdown panel */}
            {isOpen && (
                <div className="absolute top-full left-0 mt-1 w-56 bg-white border border-gray-200 rounded-section shadow-lg z-30">
                    {/* Search input */}
                    <div className="flex items-center gap-2 px-3 py-2 border-b border-gray-100">
                        <FiSearch size={14} className="text-gray-400 shrink-0" />
                        <input
                            type="text"
                            className="flex-1 text-md outline-none placeholder:text-gray-400"
                            placeholder="Tìm kiếm"
                            value={search}
                            onChange={e => setSearch(e.target.value)}
                            autoFocus
                        />
                    </div>

                    {/* Module list */}
                    <ul className="max-h-64 overflow-y-auto py-1">
                        {filtered.length === 0 ? (
                            <li className="px-3 py-2 text-md text-gray-400">Không tìm thấy</li>
                        ) : (
                            filtered.map(m => (
                                <li
                                    key={m.id}
                                    className={`flex items-center justify-between px-3 py-2 cursor-pointer text-md hover:bg-gray-50 transition-colors ${m.id === selectedId ? 'text-primary' : 'text-text-main'}`}
                                    onClick={() => handleSelect(m.id)}
                                >
                                    <span>{m.label}</span>
                                    {m.id === selectedId && <FiCheck size={14} className="text-primary shrink-0" />}
                                </li>
                            ))
                        )}
                    </ul>
                </div>
            )}
        </div>
    );
};

export default ModuleSelectDropdown;
