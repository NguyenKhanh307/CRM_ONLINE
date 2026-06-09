import { useState, useEffect } from 'react';
import { FiX } from 'react-icons/fi';
import type { RoleGroup } from '@/features/phan-quyen/types/phanQuyenTypes';

interface Props {
    mode: 'create' | 'edit';
    group?: RoleGroup;
    onClose: () => void;
    onSubmit: (data: { code?: string; name: string; description?: string }) => void;
    isLoading: boolean;
}

/** Modal tạo mới hoặc chỉnh sửa tên/mô tả nhóm. */
const GroupFormModal = ({ mode, group, onClose, onSubmit, isLoading }: Props) => {
    const [code, setCode] = useState('');
    const [name, setName] = useState('');
    const [description, setDescription] = useState('');

    useEffect(() => {
        if (mode === 'edit' && group) {
            setName(group.name);
            setDescription(group.description ?? '');
        }
    }, [mode, group]);

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (!name.trim()) return;
        onSubmit({
            ...(mode === 'create' ? { code: code.trim().toUpperCase() } : {}),
            name: name.trim(),
            description: description.trim() || undefined,
        });
    };

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/30">
            <div className="bg-white rounded-card shadow-xl w-full max-w-md p-6">
                {/* Header */}
                <div className="flex items-center justify-between mb-5">
                    <h2 className="text-lg font-semibold text-text-main">
                        {mode === 'create' ? 'Tạo nhóm mới' : 'Chỉnh sửa nhóm'}
                    </h2>
                    <button onClick={onClose} className="text-gray-400 hover:text-gray-600">
                        <FiX size={18} />
                    </button>
                </div>

                <form onSubmit={handleSubmit} className="space-y-4">
                    {mode === 'create' && (
                        <div>
                            <label className="block text-md text-text-main font-medium mb-1">
                                Mã nhóm <span className="text-danger">*</span>
                            </label>
                            <input
                                type="text"
                                value={code}
                                onChange={e => setCode(e.target.value)}
                                placeholder="VD: SALES_STAFF"
                                className="w-full border border-gray-300 rounded-btn px-3 py-2 text-md focus:outline-none focus:border-primary"
                                required
                                maxLength={20}
                            />
                        </div>
                    )}

                    <div>
                        <label className="block text-md text-text-main font-medium mb-1">
                            Tên nhóm <span className="text-danger">*</span>
                        </label>
                        <input
                            type="text"
                            value={name}
                            onChange={e => setName(e.target.value)}
                            placeholder="VD: Nhân viên kinh doanh"
                            className="w-full border border-gray-300 rounded-btn px-3 py-2 text-md focus:outline-none focus:border-primary"
                            required
                            maxLength={40}
                        />
                    </div>

                    <div>
                        <label className="block text-md text-text-main font-medium mb-1">Mô tả</label>
                        <textarea
                            value={description}
                            onChange={e => setDescription(e.target.value)}
                            placeholder="Mô tả ngắn về nhóm này..."
                            rows={2}
                            maxLength={50}
                            className="w-full border border-gray-300 rounded-btn px-3 py-2 text-md focus:outline-none focus:border-primary resize-none"
                        />
                    </div>

                    {/* Actions */}
                    <div className="flex justify-end gap-2 pt-2">
                        <button
                            type="button"
                            onClick={onClose}
                            className="px-4 py-1.5 text-md text-gray-600 border border-gray-300 rounded-btn hover:bg-gray-50 transition-colors"
                        >
                            Hủy
                        </button>
                        <button
                            type="submit"
                            disabled={isLoading || !name.trim()}
                            className="px-4 py-1.5 text-md bg-primary text-white rounded-btn hover:bg-blue-600 disabled:opacity-50 transition-colors"
                        >
                            {isLoading ? 'Đang lưu...' : 'Lưu'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default GroupFormModal;
