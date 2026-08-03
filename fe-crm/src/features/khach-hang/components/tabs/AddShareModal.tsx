import { useState, useMemo, useRef } from 'react';
import { FiX, FiSearch } from 'react-icons/fi';
import { ActionButton } from '@/shared/components/ActionButton';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useDialogKeyboardNav } from '@/shared/keyboard/useDialogKeyboardNav';
import { SHORTCUTS } from '@/shared/keyboard/shortcuts';
import type { CustomerSharePermission } from '../../types/customerTypes';

export interface SharePickerOption {
    id: number;
    label: string;
    sub?: string;
}

interface Props {
    title: string;
    confirmNoun: string;
    options: SharePickerOption[];
    existingIds: Set<number>;
    onAdd: (id: number, permission: CustomerSharePermission) => void;
    onClose: () => void;
    isLoading: boolean;
}

const PERMISSION_LABELS: Record<CustomerSharePermission, string> = { view: 'Xem', edit: 'Sửa' };

// modal chọn 1 nhân viên để chia sẻ khách hàng — mirror AddEntityModal nhưng có thêm ô chọn
// quyền (Xem/Sửa) áp dụng cho lượt chia sẻ sắp thêm
export function AddShareModal({ title, confirmNoun, options, existingIds, onAdd, onClose, isLoading }: Props) {
    const [search, setSearch] = useState('');
    const [permission, setPermission] = useState<CustomerSharePermission>('view');
    const { confirmCreate } = useConfirm();

    const ref = useRef<HTMLDivElement>(null);
    useDialogKeyboardNav(ref, { onCancel: onClose, autoFocus: 'none' });

    const candidates = useMemo(() => {
        const q = search.toLowerCase();
        return options.filter(o =>
            !existingIds.has(o.id) &&
            (o.label.toLowerCase().includes(q) || (o.sub ?? '').toLowerCase().includes(q))
        );
    }, [options, existingIds, search]);

    // hỏi xác nhận rồi thêm lượt chia sẻ với quyền đang chọn
    const handleAdd = async (opt: SharePickerOption) => {
        if (!(await confirmCreate(`"${opt.label}" — ${confirmNoun}`))) return;
        onAdd(opt.id, permission);
    };

    return (
        <div ref={ref} className="fixed inset-0 z-50 flex items-center justify-center bg-black/30">
            <div className="bg-white rounded-card shadow-xl w-full max-w-lg p-6">
                <div className="flex items-center justify-between mb-4">
                    <h2 className="text-lg font-semibold text-text-main">{title}</h2>
                    <button onClick={onClose} className="text-gray-400 hover:text-gray-600">
                        <FiX size={18} />
                    </button>
                </div>

                <div className="flex items-center gap-3 mb-3">
                    <div className="relative flex-1">
                        <FiSearch size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                        <input
                            type="text"
                            value={search}
                            onChange={e => setSearch(e.target.value)}
                            placeholder="Tìm kiếm..."
                            className="w-full border border-gray-300 rounded-btn pl-8 pr-3 py-2 text-md focus:outline-none focus:border-primary"
                        />
                    </div>
                    <select
                        data-form-field
                        value={permission}
                        onChange={e => setPermission(e.target.value as CustomerSharePermission)}
                        className="border border-gray-300 rounded-btn px-2 py-2 text-md focus:outline-none focus:border-primary"
                    >
                        {(Object.keys(PERMISSION_LABELS) as CustomerSharePermission[]).map(p => (
                            <option key={p} value={p}>{PERMISSION_LABELS[p]}</option>
                        ))}
                    </select>
                </div>

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
                            <ActionButton variant="primary" onClick={() => handleAdd(opt)} disabled={isLoading}>
                                Thêm
                            </ActionButton>
                        </div>
                    ))}
                </div>

                <div className="flex justify-end mt-4">
                    <ActionButton variant="secondary" dialogButton shortcut={SHORTCUTS.CANCEL.keys} onClick={onClose}>
                        Đóng
                    </ActionButton>
                </div>
            </div>
        </div>
    );
}
