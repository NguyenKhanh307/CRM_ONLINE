import { useState, useMemo } from 'react';
import { FiX, FiSearch } from 'react-icons/fi';

export interface PickerOption {
    id: number;
    label: string;
    sub?: string;
}

interface Props {
    title: string;
    options: PickerOption[];
    existingIds: Set<number>;
    onAdd: (id: number) => void;
    onClose: () => void;
    isLoading: boolean;
}

/** Modal chọn 1 thực thể để thêm vào chính sách giá (mô phỏng AddMemberModal phân quyền). */
const AddEntityModal = ({ title, options, existingIds, onAdd, onClose, isLoading }: Props) => {
    const [search, setSearch] = useState('');

    const candidates = useMemo(() => {
        const q = search.toLowerCase();
        return options.filter(o =>
            !existingIds.has(o.id) &&
            (o.label.toLowerCase().includes(q) || (o.sub ?? '').toLowerCase().includes(q))
        );
    }, [options, existingIds, search]);

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/30">
            <div className="bg-white rounded-card shadow-xl w-full max-w-lg p-6">
                {/* Header */}
                <div className="flex items-center justify-between mb-4">
                    <h2 className="text-lg font-semibold text-text-main">{title}</h2>
                    <button onClick={onClose} className="text-gray-400 hover:text-gray-600">
                        <FiX size={18} />
                    </button>
                </div>

                {/* Search */}
                <div className="relative mb-3">
                    <FiSearch size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                    <input
                        type="text"
                        value={search}
                        onChange={e => setSearch(e.target.value)}
                        placeholder="Tìm kiếm..."
                        className="w-full border border-gray-300 rounded-btn pl-8 pr-3 py-2 text-md focus:outline-none focus:border-primary"
                    />
                </div>

                {/* List */}
                <div className="max-h-72 overflow-y-auto border border-gray-200 rounded-section">
                    {candidates.length === 0 && (
                        <p className="p-4 text-sm text-gray-400 text-center">
                            {search ? 'Không tìm thấy mục phù hợp.' : 'Tất cả mục đã được thêm.'}
                        </p>
                    )}
                    {candidates.map((opt, idx) => (
                        <div
                            key={opt.id}
                            className={`flex items-center justify-between px-4 py-2.5 ${idx % 2 === 0 ? 'bg-white' : 'bg-gray-50'} border-b border-gray-100 last:border-0`}
                        >
                            <div>
                                <p className="text-md font-medium text-text-main">{opt.label}</p>
                                {opt.sub && <p className="text-sm text-gray-400">{opt.sub}</p>}
                            </div>
                            <button
                                onClick={() => onAdd(opt.id)}
                                disabled={isLoading}
                                className="px-3 py-1 text-sm bg-primary text-white rounded-btn hover:opacity-90 disabled:opacity-50 transition-colors"
                            >
                                Thêm
                            </button>
                        </div>
                    ))}
                </div>

                <div className="flex justify-end mt-4">
                    <button
                        onClick={onClose}
                        className="px-4 py-1.5 text-md text-gray-600 border border-gray-300 rounded-btn hover:bg-gray-50 transition-colors"
                    >
                        Đóng
                    </button>
                </div>
            </div>
        </div>
    );
};

export default AddEntityModal;
