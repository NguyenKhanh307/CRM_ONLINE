import { useState, useMemo } from 'react';
import { FiPlus, FiTrash2, FiUsers } from 'react-icons/fi';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { ActionButton } from '@/shared/components/ActionButton';
import { ScrollFrame } from '@/shared/components/table/ScrollFrame';
import { toIdNameMap } from '@/shared/utils/lookup';
import { useAlert } from '@/shared/alert/useAlert';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import AddEntityModal from '../AddEntityModal';
import { usePolicyEmployees, useCreatePolicyEmployee, useDeletePolicyEmployee } from '../../hooks/usePolicyEmployees';

interface Props { policyId: number; }

export function PolicyEmployeesTab({ policyId }: Props) {
    const { data = [], isLoading } = usePolicyEmployees(policyId);
    const { mutate: createFn, mutateAsync: createFnAsync, isPending: isCreating } = useCreatePolicyEmployee(policyId);
    const { mutate: deleteFn, isPending: isDeleting } = useDeletePolicyEmployee(policyId);
    const { showAlert } = useAlert();
    const { confirm } = useConfirm();

    const { data: users = [] } = useActiveUsers();
    const options = useMemo(() => users.map((u) => ({ id: u.id, label: u.fullName, sub: u.email })), [users]);
    const nameMap = useMemo(() => toIdNameMap(users, 'id', 'fullName'), [users]);
    const existingIds = useMemo(
        () => new Set(data.map(d => d.userId).filter((id): id is number => id != null)),
        [data]
    );

    const [showAdd, setShowAdd] = useState(false);
    const [deleteTarget, setDeleteTarget] = useState<number | null>(null);
    const [isAddingAll, setIsAddingAll] = useState(false);

    const handleAdd = (userId: number) => {
        createFn({ pricePolicyId: policyId, userId });
    };

    const handleAddAllActive = async () => {
        const targets = users.filter(u => !existingIds.has(u.id));
        if (targets.length === 0) {
            showAlert('Không có nhân viên đang hoạt động nào chưa áp dụng chính sách này.');
            return;
        }
        if (!(await confirm({
            message: `Thêm ${targets.length} nhân viên đang hoạt động vào chính sách giá?`,
            confirmLabel: 'Thêm tất cả',
        }))) return;

        setIsAddingAll(true);
        try {
            await Promise.all(targets.map(u => createFnAsync({ pricePolicyId: policyId, userId: u.id })));
            showAlert(`Đã thêm ${targets.length} nhân viên vào chính sách giá.`);
        } finally {
            setIsAddingAll(false);
        }
    };

    if (isLoading) return <div className="p-4 text-gray-400 text-sm">Đang tải...</div>;

    return (
        <div className="space-y-3">
            <div className="flex justify-end gap-2">
                <ActionButton variant="secondary" type="button" icon={FiUsers} disabled={isAddingAll} onClick={handleAddAllActive}>
                    Thêm tất cả nhân viên đang hoạt động
                </ActionButton>
                <button onClick={() => setShowAdd(true)} className="flex items-center gap-1.5 px-3 py-1.5 rounded-btn bg-primary text-white text-sm hover:opacity-90">
                    <FiPlus size={14} /> Thêm
                </button>
            </div>

            {data.length === 0 ? (
                <p className="text-sm text-gray-400 text-center py-6">Chưa có nhân viên nào áp dụng chính sách này</p>
            ) : (
                <ScrollFrame visibleRows={10}>
                    <table className="w-full text-sm">
                        <thead>
                            <tr className="text-gray-500 text-left">
                                <th className="py-2 font-medium">Tên nhân viên</th>
                                <th className="py-2 font-medium">Email</th>
                                <th className="py-2 w-10"></th>
                            </tr>
                        </thead>
                        <tbody>
                            {data.map(item => (
                                <tr key={item.id} className="border-b border-gray-100 hover:bg-gray-50">
                                    <td className="py-2">{item.userId != null ? (nameMap.get(item.userId) ?? '—') : '—'}</td>
                                    <td className="py-2 text-gray-500">{item.userId != null ? (users.find(u => u.id === item.userId)?.email ?? '—') : '—'}</td>
                                    <td className="py-2">
                                        <button onClick={() => setDeleteTarget(item.id)} className="p-1 rounded hover:bg-red-50 text-gray-400 hover:text-danger"><FiTrash2 size={13} /></button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </ScrollFrame>
            )}

            {showAdd && (
                <AddEntityModal
                    title="Thêm nhân viên"
                    confirmNoun="nhân viên vào chính sách giá"
                    options={options}
                    existingIds={existingIds}
                    onAdd={handleAdd}
                    onClose={() => setShowAdd(false)}
                    isLoading={isCreating}
                />
            )}

            {deleteTarget !== null && (
                <ConfirmModal
                    message="Xóa nhân viên này khỏi chính sách?"
                    confirmLabel="Xóa"
                    confirmDanger
                    isLoading={isDeleting}
                    onConfirm={() => deleteFn(deleteTarget, { onSuccess: () => setDeleteTarget(null) })}
                    onCancel={() => setDeleteTarget(null)}
                />
            )}
        </div>
    );
}
