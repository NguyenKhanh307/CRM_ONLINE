import { useRef, useState } from 'react';
import { FiX, FiAlertTriangle } from 'react-icons/fi';
import { DialogFooter } from '@/shared/components/ModalFooter';
import { useDialogKeyboardNav } from '@/shared/keyboard/useDialogKeyboardNav';
import { useDuplicateCheck } from '@/shared/hooks/useDuplicateCheck';
import type { LeadResult } from '../types/leadTypes';

interface Props {
    lead: LeadResult;
    isLoading?: boolean;
    /** customerId = null → tạo khách hàng mới; ngược lại gắn cơ hội vào khách hàng đã có. */
    onConfirm: (customerId: number | null) => void;
    onCancel: () => void;
}

/**
 * Xác nhận chuyển đổi tiềm năng. Nếu có khách hàng trùng MST/email/SĐT, cho chọn dùng lại
 * khách hàng đó thay vì tạo bản ghi thứ hai cho cùng một công ty.
 */
export function ConvertLeadModal({ lead, isLoading, onConfirm, onCancel }: Props) {
    const ref = useRef<HTMLDivElement>(null);
    useDialogKeyboardNav(ref, { onCancel, autoFocus: 'none' });

    const { data: matches, isLoading: checking } = useDuplicateCheck({
        email: lead.email, phone: lead.phone, taxCode: lead.taxCode,
    });
    const customers = (matches ?? []).filter(m => m.module === 'customer');
    const [customerId, setCustomerId] = useState<number | null>(null);

    return (
        <div ref={ref} className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40" onClick={onCancel}>
            <div className="bg-white rounded-card shadow-lg w-full max-w-lg mx-4" onClick={e => e.stopPropagation()}>
                <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
                    <h2 className="text-lg font-semibold text-text-main">Chuyển đổi tiềm năng</h2>
                    <button onClick={onCancel} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                </div>

                <div className="px-5 py-4 space-y-3">
                    <p className="text-md text-text-main">
                        Tiềm năng <strong>{lead.name}</strong> sẽ được chuyển thành Khách hàng, Liên hệ và Cơ hội.
                        Sau khi chuyển đổi, tiềm năng bị khóa và không sửa được nữa.
                    </p>

                    {checking && <p className="text-sm text-gray-400">Đang kiểm tra khách hàng trùng...</p>}

                    {customers.length > 0 && (
                        <div className="rounded-btn border border-amber-300 bg-amber-50 p-3 space-y-2">
                            <div className="flex items-center gap-2 text-table font-medium text-amber-800">
                                <FiAlertTriangle size={14} />
                                Đã có khách hàng trùng thông tin — chọn cách xử lý
                            </div>

                            {customers.map(c => (
                                <label key={c.id} className="flex items-start gap-2 text-table text-text-main cursor-pointer">
                                    <input
                                        type="radio"
                                        name="convert-customer"
                                        checked={customerId === c.id}
                                        onChange={() => setCustomerId(c.id)}
                                        className="mt-0.5"
                                    />
                                    <span>
                                        Dùng khách hàng hiện có: <strong>{c.name}</strong>
                                        {c.code ? ` (${c.code})` : ''}
                                        <span className="text-gray-500"> — trùng {c.matchedField}</span>
                                    </span>
                                </label>
                            ))}

                            <label className="flex items-center gap-2 text-table text-text-main cursor-pointer">
                                <input
                                    type="radio"
                                    name="convert-customer"
                                    checked={customerId === null}
                                    onChange={() => setCustomerId(null)}
                                />
                                Tạo khách hàng mới
                            </label>
                        </div>
                    )}

                    <DialogFooter
                        className="flex justify-end gap-1.5 pt-2 border-t border-gray-100"
                        onCancel={onCancel}
                        onConfirm={() => onConfirm(customerId)}
                        confirmLabel="Chuyển đổi"
                        confirmDisabled={isLoading}
                        cancelDisabled={isLoading}
                    />
                </div>
            </div>
        </div>
    );
}
