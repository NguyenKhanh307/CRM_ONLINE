import { FiX } from 'react-icons/fi';
import type { ColumnMeta } from '@/shared/types/table';

interface ColumnVisibilityPanelProps {
    columns: ColumnMeta[];
    onClose: () => void;
}

// panel ẩn/hiện cột bằng checkbox grid
export const ColumnVisibilityPanel = ({ columns, onClose }: ColumnVisibilityPanelProps) => {
    const dataCols = columns.filter((c) => c.id !== '__select__');

    return (
        <div className="absolute right-0 top-full mt-1 z-20 w-64 bg-white rounded-section border border-gray-200 shadow-lg">
            {/* Header */}
            <div className="flex items-center justify-between px-4 py-2.5 border-b border-gray-200">
                <span className="text-title font-semibold text-text-main">Cột hiển thị</span>
                <button onClick={onClose} className="text-gray-400 hover:text-gray-600 transition-colors">
                    <FiX size={15} />
                </button>
            </div>

            {/* Column grid */}
            <div className="px-4 py-3">
                <div className="grid grid-cols-2 gap-x-4 gap-y-1">
                    {dataCols.map((col) => (
                        <label
                            key={col.id}
                            className="flex items-center gap-2 text-table cursor-pointer py-0.5 select-none"
                        >
                            <input
                                type="checkbox"
                                checked={col.isVisible}
                                onChange={col.toggleVisibility}
                                className="w-3.5 h-3.5 accent-primary cursor-pointer"
                            />
                            <span className="truncate">{col.header}</span>
                        </label>
                    ))}
                </div>
            </div>
        </div>
    );
};
