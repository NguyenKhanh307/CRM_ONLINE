import { useState } from 'react';
import { FiX, FiHelpCircle } from 'react-icons/fi';
import type { SortingState } from '@tanstack/react-table';
import type { ColumnMeta } from '@/shared/types/table';

interface SortPanelProps {
    columns: ColumnMeta[];
    sorting: SortingState;
    onApply: (s: SortingState) => void;
    onClose: () => void;
}

/**
 * Panel sắp xếp đa cột.
 * Khi "Automatic sorting" bật → áp dụng ngay.
 * Khi tắt → chờ nhấn Apply.
 */
export const SortPanel = ({ columns, sorting, onApply, onClose }: SortPanelProps) => {
    const sortableCols = columns.filter((c) => c.canSort && c.id !== '__select__');
    const [local, setLocal] = useState<SortingState>(sorting);
    const [autoSort, setAutoSort] = useState(false);

    const apply = (next: SortingState) => {
        setLocal(next);
        if (autoSort) onApply(next);
    };

    const setDir = (id: string, desc: boolean) => {
        const exists = local.find((s) => s.id === id);
        const next = exists
            ? local.map((s) => (s.id === id ? { id, desc } : s))
            : [...local, { id, desc }];
        apply(next);
    };

    const addField = (id: string) => {
        if (local.find((s) => s.id === id)) return;
        apply([...local, { id, desc: false }]);
    };

    const removeField = (id: string) => {
        apply(local.filter((s) => s.id !== id));
    };

    const handleAutoToggle = () => {
        const next = !autoSort;
        setAutoSort(next);
        if (next) onApply(local);
    };

    const availableToAdd = sortableCols.filter((c) => !local.find((s) => s.id === c.id));

    return (
        <div className="absolute right-0 top-full mt-1 z-20 w-[420px] bg-white rounded-section border border-gray-200 shadow-lg">
            {/* Header */}
            <div className="flex items-center justify-between px-4 py-2.5 border-b border-gray-200">
                <span className="flex items-center gap-1.5 text-title font-semibold text-text-main">
                    Sort by fields
                    <FiHelpCircle size={13} className="text-gray-400" />
                </span>
                <label className="flex items-center gap-2 text-table text-gray-500 cursor-pointer select-none">
                    Automatic sorting
                    <button
                        role="switch"
                        aria-checked={autoSort}
                        onClick={handleAutoToggle}
                        className={`relative w-8 h-4 rounded-full transition-colors ${autoSort ? 'bg-primary' : 'bg-gray-300'}`}
                    >
                        <span
                            className={`absolute top-0.5 w-3 h-3 bg-white rounded-full shadow transition-transform ${autoSort ? 'translate-x-4' : 'translate-x-0.5'}`}
                        />
                    </button>
                </label>
            </div>

            {/* Sort rows */}
            <div className="px-4 py-3 space-y-2 max-h-60 overflow-y-auto">
                {local.length === 0 && (
                    <p className="text-table text-gray-400 text-center py-2">
                        Chưa có cột sắp xếp nào.
                    </p>
                )}
                {local.map((s) => {
                    const col = sortableCols.find((c) => c.id === s.id);
                    return (
                        <div key={s.id} className="flex items-center gap-2">
                            <span className="text-gray-300 text-sm select-none">⠿</span>
                            {/* Field label */}
                            <span className="text-table flex-1 truncate text-text-main font-medium">{col?.header ?? s.id}</span>
                            {/* A→Z */}
                            <button
                                onClick={() => setDir(s.id, false)}
                                className={`text-sm px-3 py-1 rounded-btn border font-medium transition-colors ${!s.desc ? 'bg-primary text-white border-primary' : 'border-gray-300 text-text-main hover:border-primary hover:text-primary'}`}
                            >
                                A → Z
                            </button>
                            {/* Z→A */}
                            <button
                                onClick={() => setDir(s.id, true)}
                                className={`text-sm px-3 py-1 rounded-btn border font-medium transition-colors ${s.desc ? 'bg-primary text-white border-primary' : 'border-gray-300 text-text-main hover:border-primary hover:text-primary'}`}
                            >
                                Z → A
                            </button>
                            <button
                                onClick={() => removeField(s.id)}
                                className="text-gray-400 hover:text-danger flex-shrink-0 transition-colors"
                            >
                                <FiX size={15} />
                            </button>
                        </div>
                    );
                })}
            </div>

            {/* Add field */}
            {availableToAdd.length > 0 && (
                <div className="px-4 pb-3">
                    <select
                        value=""
                        onChange={(e) => { if (e.target.value) addField(e.target.value); }}
                        className="text-table border border-gray-300 rounded-btn px-3 py-1.5 w-40 focus:outline-none focus:border-primary cursor-pointer"
                    >
                        <option value="">Choose field ▾</option>
                        {availableToAdd.map((c) => (
                            <option key={c.id} value={c.id}>{c.header}</option>
                        ))}
                    </select>
                </div>
            )}

            {/* Footer */}
            <div className="flex items-center justify-end gap-2 px-4 py-2.5 border-t border-gray-100">
                <button
                    onClick={onClose}
                    className="text-table px-4 py-1.5 rounded-btn border border-gray-300 hover:bg-gray-50 transition-colors"
                >
                    Cancel
                </button>
                <button
                    onClick={() => { onApply(local); onClose(); }}
                    className="text-table px-4 py-1.5 rounded-btn bg-primary text-white hover:opacity-90 transition-opacity"
                >
                    Apply
                </button>
            </div>
        </div>
    );
};
