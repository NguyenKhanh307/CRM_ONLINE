import { useRef, useState } from 'react';
import { FiX } from 'react-icons/fi';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { DialogFooter } from '@/shared/components/ModalFooter';
import { useDialogKeyboardNav } from '@/shared/keyboard/useDialogKeyboardNav';
import { RESOLUTION_OPTIONS } from '../config/ticketEnums';
import type { ResolutionType } from '../types/ticketTypes';

interface Props {
    title: string;
    onConfirm: (resolutionType: ResolutionType | undefined, note: string) => void;
    onCancel: () => void;
}

/**
 * Modal chọn hình thức giải quyết + ghi chú (dùng cho resolve / complete).
 * Bản thân modal đã là bước xác nhận nên không chồng thêm popup; Esc đóng,
 * 4 mũi tên đổi qua lại giữa hai nút footer.
 */
export function ResolutionModal({ title, onConfirm, onCancel }: Props) {
    const [resolutionType, setResolutionType] = useState('');
    const [note, setNote] = useState('');
    const ref = useRef<HTMLDivElement>(null);
    useDialogKeyboardNav(ref, { onCancel, autoFocus: 'none' });

    return (
        <div ref={ref} className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40" onClick={onCancel}>
            <div className="bg-white rounded-card shadow-lg w-full max-w-md mx-4" onClick={(e) => e.stopPropagation()}>
                <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
                    <h2 className="text-lg font-semibold text-text-main">{title}</h2>
                    <button onClick={onCancel} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                </div>
                <div className="px-5 py-4 space-y-3">
                    <label className="block text-sm font-medium text-gray-700">Hình thức giải quyết</label>
                    <SearchableSelect value={resolutionType} onChange={setResolutionType} options={RESOLUTION_OPTIONS} placeholder="Chọn hình thức" />
                    <label className="block text-sm font-medium text-gray-700">Ghi chú</label>
                    <textarea rows={3} value={note} onChange={(e) => setNote(e.target.value)}
                        className="w-full border border-gray-300 rounded-btn px-3 py-1.5 text-md text-text-main focus:outline-none focus:border-primary resize-none" />
                </div>
                <DialogFooter
                    onCancel={onCancel}
                    onConfirm={() => onConfirm((resolutionType || undefined) as ResolutionType | undefined, note.trim())}
                    confirmLabel="Xác nhận"
                />
            </div>
        </div>
    );
}
