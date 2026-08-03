import { useState, useMemo } from 'react';
import { FiPlus, FiTrash2 } from 'react-icons/fi';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { ScrollFrame } from '@/shared/components/table/ScrollFrame';
import { toIdNameMap } from '@/shared/utils/lookup';
import { formatISODate } from '@/shared/utils/date';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import { useAuth } from '@/core/auth/useAuth';
import { useCustomerShares, useAssignCustomerShare, useRevokeCustomerShare } from '../../hooks/useCustomerShares';
import { AddShareModal } from './AddShareModal';
import type { CustomerSharePermission } from '../../types/customerTypes';

interface Props { customerId: number; }

const PERMISSION_LABELS: Record<CustomerSharePermission, string> = { view: 'Xem', edit: 'Sửa' };
const PERMISSION_CLASS: Record<CustomerSharePermission, string> = {
    view: 'bg-gray-100 text-gray-600',
    edit: 'bg-blue-100 text-blue-700',
};

// tab chia sẻ khách hàng cho user khác xem/sửa — bên cạnh owner chính
export function CustomerSharesTab({ customerId }: Props) {
    const { user } = useAuth();
    const { data = [], isLoading } = useCustomerShares(customerId);
    const { mutate: assignFn, isPending: isAssigning } = useAssignCustomerShare(customerId);
    const { mutate: revokeFn, isPending: isRevoking } = useRevokeCustomerShare(customerId);
    const { data: users = [] } = useActiveUsers();

    const options = useMemo(() => users.map(u => ({ id: u.id, label: u.fullName, sub: u.email })), [users]);
    const nameMap = useMemo(() => toIdNameMap(users, 'id', 'fullName'), [users]);
    const existingIds = useMemo(() => new Set(data.map(d => d.userId)), [data]);

    const [showAdd, setShowAdd] = useState(false);
    const [revokeTarget, setRevokeTarget] = useState<number | null>(null); // userId

    const handleAdd = (userId: number, permission: CustomerSharePermission) => {
        assignFn({ userId, permission, sharedBy: user?.id ?? null }, {
            onSuccess: () => setShowAdd(false),
        });
    };

    if (isLoading) return <div className="p-4 text-gray-400 text-sm">Đang tải...</div>;

    return (
        <div className="space-y-3">
            <div className="flex justify-end">
                <button onClick={() => setShowAdd(true)} className="flex items-center gap-1.5 px-3 py-1.5 rounded-btn bg-primary text-white text-sm hover:opacity-90">
                    <FiPlus size={14} /> Chia sẻ
                </button>
            </div>

            {data.length === 0 ? (
                <p className="text-sm text-gray-400 text-center py-6">Khách hàng này chưa được chia sẻ cho ai</p>
            ) : (
                <ScrollFrame visibleRows={10}>
                    <table className="w-full text-sm">
                        <thead>
                            <tr className="text-gray-500 text-left">
                                <th className="py-2 font-medium">Tên nhân viên</th>
                                <th className="py-2 font-medium">Quyền</th>
                                <th className="py-2 font-medium">Người chia sẻ</th>
                                <th className="py-2 font-medium">Ngày chia sẻ</th>
                                <th className="py-2 w-10"></th>
                            </tr>
                        </thead>
                        <tbody>
                            {data.map(item => (
                                <tr key={item.id} className="border-b border-gray-100 hover:bg-gray-50">
                                    <td className="py-2">{nameMap.get(item.userId) ?? '—'}</td>
                                    <td className="py-2">
                                        {item.permission ? (
                                            <span className={`inline-block px-2 py-0.5 rounded text-xs font-medium ${PERMISSION_CLASS[item.permission]}`}>
                                                {PERMISSION_LABELS[item.permission]}
                                            </span>
                                        ) : '—'}
                                    </td>
                                    <td className="py-2">{item.sharedBy != null ? (nameMap.get(item.sharedBy) ?? '—') : '—'}</td>
                                    <td className="py-2">{formatISODate(item.createdAt)}</td>
                                    <td className="py-2">
                                        <button onClick={() => setRevokeTarget(item.userId)} className="p-1 rounded hover:bg-red-50 text-gray-400 hover:text-danger">
                                            <FiTrash2 size={13} />
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </ScrollFrame>
            )}

            {showAdd && (
                <AddShareModal
                    title="Chia sẻ khách hàng"
                    confirmNoun="chia sẻ khách hàng này"
                    options={options}
                    existingIds={existingIds}
                    onAdd={handleAdd}
                    onClose={() => setShowAdd(false)}
                    isLoading={isAssigning}
                />
            )}

            {revokeTarget !== null && (
                <ConfirmModal
                    message="Thu hồi chia sẻ khách hàng này?"
                    confirmLabel="Xóa"
                    confirmDanger
                    isLoading={isRevoking}
                    onConfirm={() => revokeFn(revokeTarget, { onSuccess: () => setRevokeTarget(null) })}
                    onCancel={() => setRevokeTarget(null)}
                />
            )}
        </div>
    );
}
