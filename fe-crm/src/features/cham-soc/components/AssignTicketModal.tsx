import { useRef, useState } from 'react';
import { FiX } from 'react-icons/fi';
import { SearchableSelect, type SelectOption } from '@/shared/components/SearchableSelect';
import { DialogFooter } from '@/shared/components/ModalFooter';
import { useDialogKeyboardNav } from '@/shared/keyboard/useDialogKeyboardNav';

interface Props {
    userOptions: SelectOption[];
    onConfirm: (toUserId: number) => void;
    onCancel: () => void;
}

/**
 * Modal chọn nhân viên để giao xử lý phiếu.
 * Bản thân modal đã là bước xác nhận nên không chồng thêm popup; Esc đóng,
 * 4 mũi tên đổi qua lại giữa hai nút footer.
 */
export function AssignTicketModal({ userOptions, onConfirm, onCancel }: Props) {
    const [userId, setUserId] = useState('');
    const ref = useRef<HTMLDivElement>(null);
    useDialogKeyboardNav(ref, { onCancel, autoFocus: 'none' });

    return (
        <div ref={ref} className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40" onClick={onCancel}>
            <div className="bg-white rounded-card shadow-lg w-full max-w-md mx-4" onClick={(e) => e.stopPropagation()}>
                <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
                    <h2 className="text-lg font-semibold text-text-main">Giao xử lý phiếu</h2>
                    <button onClick={onCancel} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                </div>
                <div className="px-5 py-4 space-y-3">
                    <label className="block text-sm font-medium text-gray-700">Nhân viên xử lý</label>
                    <SearchableSelect value={userId} onChange={setUserId} options={userOptions} placeholder="Chọn nhân viên" />
                </div>
                <DialogFooter
                    onCancel={onCancel}
                    onConfirm={() => onConfirm(Number(userId))}
                    confirmLabel="Giao"
                    confirmDisabled={!userId}
                />
            </div>
        </div>
    );
}
