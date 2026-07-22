import { useState, useMemo } from 'react';
import { FiPlus, FiTrash2 } from 'react-icons/fi';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { ScrollFrame } from '@/shared/components/table/ScrollFrame';
import { toIdNameMap } from '@/shared/utils/lookup';
import { useCustomerList } from '@/features/khach-hang/hooks/useCustomerList';
import AddEntityModal from '../AddEntityModal';
import { usePolicyCustomers, useCreatePolicyCustomer, useDeletePolicyCustomer } from '../../hooks/usePolicyCustomers';

interface Props { policyId: number; }

export function PolicyCustomersTab({ policyId }: Props) {
    const { data = [], isLoading } = usePolicyCustomers(policyId);
    const { mutate: createFn, isPending: isCreating } = useCreatePolicyCustomer(policyId);
    const { mutate: deleteFn, isPending: isDeleting } = useDeletePolicyCustomer(policyId);

    const { data: customers = [] } = useCustomerList();
    const options = useMemo(() => customers.map((c) => ({ id: c.id, label: c.name, sub: c.code })), [customers]);
    const nameMap = useMemo(() => toIdNameMap(customers, 'id', 'name'), [customers]);
    const codeMap = useMemo(() => toIdNameMap(customers, 'id', 'code'), [customers]);
    const existingIds = useMemo(() => new Set(data.map(d => d.customerId)), [data]);

    const [showAdd, setShowAdd] = useState(false);
    const [deleteTarget, setDeleteTarget] = useState<number | null>(null);

    const handleAdd = (customerId: number) => {
        createFn({ pricePolicyId: policyId, customerId });
    };

    if (isLoading) return <div className="p-4 text-gray-400 text-sm">Đang tải...</div>;

    return (
        <div className="space-y-3">
            <div className="flex justify-end">
                <button onClick={() => setShowAdd(true)} className="flex items-center gap-1.5 px-3 py-1.5 rounded-btn bg-primary text-white text-sm hover:opacity-90">
                    <FiPlus size={14} /> Thêm khách hàng
                </button>
            </div>

            {data.length === 0 ? (
                <p className="text-sm text-gray-400 text-center py-6">Chưa có khách hàng nào trong chính sách này</p>
            ) : (
                <ScrollFrame visibleRows={10}>
                <table className="w-full text-sm">
                    <thead>
                        <tr className="text-gray-500 text-left">
                            <th className="py-2 font-medium">Tên khách hàng</th>
                            <th className="py-2 font-medium">Mã KH</th>
                            <th className="py-2 w-10"></th>
                        </tr>
                    </thead>
                    <tbody>
                        {data.map(item => (
                            <tr key={item.id} className="border-b border-gray-100 hover:bg-gray-50">
                                <td className="py-2">{nameMap.get(item.customerId) ?? '—'}</td>
                                <td className="py-2 text-gray-500">{codeMap.get(item.customerId) ?? '—'}</td>
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
                    title="Thêm khách hàng"
                    options={options}
                    existingIds={existingIds}
                    onAdd={handleAdd}
                    onClose={() => setShowAdd(false)}
                    isLoading={isCreating}
                />
            )}

            {deleteTarget !== null && (
                <ConfirmModal
                    message="Xóa khách hàng này khỏi chính sách?"
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
