import { useState, useEffect } from 'react';
import { FiX, FiDownload } from 'react-icons/fi';
import type { ExportColumn, ExportFormat } from './exportTypes';

interface ExportModalProps<T> {
    open: boolean;
    /** Toàn bộ cột khả dụng của phân hệ. */
    columns: ExportColumn<T>[];
    /** Số dòng sẽ được xuất. */
    rowCount: number;
    onClose: () => void;
    onExport: (selectedKeys: string[], format: ExportFormat) => void;
}

/**
 * Modal cho người dùng chọn cột + định dạng trước khi xuất file.
 * Mặc định tick toàn bộ cột.
 */
export function ExportModal<T>({ open, columns, rowCount, onClose, onExport }: ExportModalProps<T>) {
    const allKeys = columns.map(c => c.key);
    const [selected, setSelected] = useState<string[]>(allKeys);
    const [format, setFormat] = useState<ExportFormat>('xlsx');

    // Khi mở lại, reset về tick tất cả.
    useEffect(() => {
        if (open) {
            setSelected(columns.map(c => c.key));
            setFormat('xlsx');
        }
    }, [open, columns]);

    if (!open) return null;

    const toggle = (key: string) =>
        setSelected(prev => (prev.includes(key) ? prev.filter(k => k !== key) : [...prev, key]));

    const allChecked = selected.length === columns.length;

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/30">
            <div className="bg-white rounded-card shadow-xl w-[440px] max-w-[95vw]">
                <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
                    <span className="font-semibold text-base text-text-main">Xuất file</span>
                    <button onClick={onClose} className="p-1 rounded hover:bg-gray-100 text-gray-400 hover:text-gray-600">
                        <FiX size={18} />
                    </button>
                </div>

                <div className="px-5 py-4 space-y-4">
                    <div className="flex items-center justify-between">
                        <span className="text-sm text-gray-600">Chọn cột cần xuất</span>
                        <button
                            onClick={() => setSelected(allChecked ? [] : allKeys)}
                            className="text-sm text-primary hover:underline"
                        >
                            {allChecked ? 'Bỏ chọn tất cả' : 'Chọn tất cả'}
                        </button>
                    </div>

                    <div className="max-h-64 overflow-y-auto border border-gray-200 rounded-btn divide-y divide-gray-100">
                        {columns.map(col => (
                            <label
                                key={col.key}
                                className="flex items-center gap-2.5 px-3 py-2 cursor-pointer hover:bg-gray-50"
                            >
                                <input
                                    type="checkbox"
                                    checked={selected.includes(col.key)}
                                    onChange={() => toggle(col.key)}
                                    className="accent-primary"
                                />
                                <span className="text-sm text-text-main">{col.label}</span>
                            </label>
                        ))}
                    </div>

                    <div className="space-y-1.5">
                        <span className="text-sm text-gray-600">Định dạng</span>
                        <div className="flex gap-4">
                            <label className="flex items-center gap-1.5 cursor-pointer text-sm text-text-main">
                                <input
                                    type="radio"
                                    name="export-format"
                                    checked={format === 'xlsx'}
                                    onChange={() => setFormat('xlsx')}
                                    className="accent-primary"
                                />
                                Excel (.xlsx)
                            </label>
                            <label className="flex items-center gap-1.5 cursor-pointer text-sm text-text-main">
                                <input
                                    type="radio"
                                    name="export-format"
                                    checked={format === 'csv'}
                                    onChange={() => setFormat('csv')}
                                    className="accent-primary"
                                />
                                CSV (.csv)
                            </label>
                        </div>
                    </div>

                    <p className="text-sm text-gray-400">Sẽ xuất {rowCount} dòng</p>
                </div>

                <div className="flex justify-end gap-2 px-5 py-3 border-t border-gray-200">
                    <button
                        onClick={onClose}
                        className="px-4 py-1.5 rounded-btn border border-gray-300 text-sm text-text-main hover:bg-gray-50"
                    >
                        Hủy
                    </button>
                    <button
                        disabled={selected.length === 0}
                        onClick={() => onExport(allKeys.filter(k => selected.includes(k)), format)}
                        className="flex items-center gap-1.5 px-4 py-1.5 rounded-btn bg-primary text-white text-sm hover:opacity-90 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        <FiDownload size={14} />
                        Xuất file
                    </button>
                </div>
            </div>
        </div>
    );
}
