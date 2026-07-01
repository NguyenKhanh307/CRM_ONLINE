import { useState } from 'react';
import { FiX } from 'react-icons/fi';
import { SearchableSelect, type SelectOption } from '@/shared/components/SearchableSelect';

interface Props {
    userOptions: SelectOption[];
    onConfirm: (toUserId: number) => void;
    onCancel: () => void;
}

/** Modal chọn nhân viên để giao xử lý phiếu. */
export function AssignTicketModal({ userOptions, onConfirm, onCancel }: Props) {
    const [userId, setUserId] = useState('');
    return (
        <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40" onClick={onCancel}>
            <div className="bg-white rounded-card shadow-lg w-full max-w-md mx-4" onClick={(e) => e.stopPropagation()}>
                <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
                    <h2 className="text-lg font-semibold text-text-main">Giao xử lý phiếu</h2>
                    <button onClick={onCancel} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                </div>
                <div className="px-5 py-4 space-y-3">
                    <label className="block text-sm font-medium text-gray-700">Nhân viên xử lý</label>
                    <SearchableSelect value={userId} onChange={setUserId} options={userOptions} placeholder="Chọn nhân viên" />
                    <div className="flex justify-end gap-3 pt-2 border-t border-gray-100">
                        <button type="button" onClick={onCancel} className="px-4 py-1.5 rounded-btn border border-gray-300 text-md text-text-main hover:bg-gray-50">Hủy</button>
                        <button type="button" disabled={!userId} onClick={() => onConfirm(Number(userId))}
                            className="px-4 py-1.5 rounded-btn bg-primary text-white text-md hover:opacity-90 disabled:opacity-50">Giao</button>
                    </div>
                </div>
            </div>
        </div>
    );
}
