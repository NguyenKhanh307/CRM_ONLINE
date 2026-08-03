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

// modal chọn nhân viên để giao xử lý phiếu — bản thân modal đã là bước xác nhận nên không chồng
// thêm popup; Esc đóng, 4 mũi tên đổi qua lại giữa hai nút footer
export function AssignTicketModal({ userOptions, onConfirm, onCancel }: Props) {
    const [userId, setUserId] = useState('');
    const [error, setError] = useState<string | null>(null);
    const ref = useRef<HTMLDivElement>(null);
    useDialogKeyboardNav(ref, { onCancel, autoFocus: 'none' });

    const handleConfirm = () => {
        if (!userId) { setError('Vui lòng chọn nhân viên xử lý'); return; }
        onConfirm(Number(userId));
    };

    return (
        <div ref={ref} className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40" onClick={onCancel}>
            <div className="bg-white rounded-card shadow-lg w-full max-w-md mx-4" onClick={(e) => e.stopPropagation()}>
                <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
                    <h2 className="text-lg font-semibold text-text-main">Giao xử lý phiếu</h2>
                    <button onClick={onCancel} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                </div>
                <div className="px-5 py-4 space-y-3">
                    <label className="block text-sm font-medium text-gray-700">Nhân viên xử lý</label>
                    <SearchableSelect value={userId} onChange={(v) => { setUserId(v); setError(null); }} options={userOptions} placeholder="Chọn nhân viên" />
                    {error && <p className="text-xs text-danger">{error}</p>}
                </div>
                <DialogFooter
                    onCancel={onCancel}
                    onConfirm={handleConfirm}
                    confirmLabel="Giao"
                />
            </div>
        </div>
    );
}
