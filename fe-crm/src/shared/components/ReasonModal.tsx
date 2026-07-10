import { useRef, useState } from 'react';
import { FiX } from 'react-icons/fi';
import { DialogFooter } from '@/shared/components/ModalFooter';
import { useDialogKeyboardNav } from '@/shared/keyboard/useDialogKeyboardNav';

interface Props {
    title: string;
    label?: string;
    placeholder?: string;
    confirmLabel?: string;
    confirmDanger?: boolean;
    onConfirm: (reason: string) => void;
    onCancel: () => void;
}

/** Modal nhập một lý do/ý kiến dùng chung (từ chối báo giá, đánh mất tiềm năng...). */
export function ReasonModal({
    title,
    label = 'Lý do',
    placeholder = 'Nhập lý do...',
    confirmLabel = 'Xác nhận',
    confirmDanger = false,
    onConfirm,
    onCancel,
}: Props) {
    const [reason, setReason] = useState('');
    const ref = useRef<HTMLDivElement>(null);
    // Không tự focus nút — người dùng cần gõ lý do trước; textarea giữ autoFocus riêng.
    useDialogKeyboardNav(ref, { onCancel, autoFocus: 'none' });

    return (
        <div ref={ref} className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40" onClick={onCancel}>
            <div className="bg-white rounded-card shadow-lg w-full max-w-md mx-4" onClick={(e) => e.stopPropagation()}>
                <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
                    <h2 className="text-lg font-semibold text-text-main">{title}</h2>
                    <button onClick={onCancel} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                </div>
                <div className="px-5 py-4 space-y-3">
                    <label className="block text-sm font-medium text-gray-700">{label}</label>
                    <textarea
                        autoFocus
                        rows={3}
                        value={reason}
                        onChange={(e) => setReason(e.target.value)}
                        placeholder={placeholder}
                        className="w-full border border-gray-300 rounded-btn px-3 py-1.5 text-md text-text-main focus:outline-none focus:border-primary resize-none"
                    />
                    <DialogFooter
                        className="flex justify-end gap-1.5 pt-2 border-t border-gray-100"
                        onCancel={onCancel}
                        onConfirm={() => onConfirm(reason.trim())}
                        confirmLabel={confirmLabel}
                        confirmDanger={confirmDanger}
                    />
                </div>
            </div>
        </div>
    );
}
