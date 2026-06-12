import { useState, type FormEvent } from 'react';
import { FiPlus, FiTrash2 } from 'react-icons/fi';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { usePolicyEmployees, useCreatePolicyEmployee, useDeletePolicyEmployee } from '../../hooks/usePolicyEmployees';

interface Props { policyId: number; }

export function PolicyEmployeesTab({ policyId }: Props) {
    const { data = [], isLoading } = usePolicyEmployees(policyId);
    const { mutate: createFn, isPending: isCreating } = useCreatePolicyEmployee(policyId);
    const { mutate: deleteFn, isPending: isDeleting } = useDeletePolicyEmployee(policyId);

    const [showAdd, setShowAdd] = useState(false);
    const [userId, setUserId] = useState('');
    const [unitId, setUnitId] = useState('');
    const [deleteTarget, setDeleteTarget] = useState<number | null>(null);

    const inp = 'w-full border border-gray-300 rounded-btn px-2 py-1 text-sm focus:outline-none focus:border-primary';

    const handleAdd = (e: FormEvent) => {
        e.preventDefault();
        createFn({
            pricePolicyId: policyId,
            userId: userId ? Number(userId) : null,
            unitId: unitId ? Number(unitId) : null,
        }, {
            onSuccess: () => { setShowAdd(false); setUserId(''); setUnitId(''); },
        });
    };

    if (isLoading) return <div className="p-4 text-gray-400 text-sm">Đang tải...</div>;

    return (
        <div className="space-y-3">
            <div className="flex justify-end">
                <button onClick={() => setShowAdd(v => !v)} className="flex items-center gap-1.5 px-3 py-1.5 rounded-btn bg-primary text-white text-sm hover:opacity-90">
                    <FiPlus size={14} /> Thêm nhân viên
                </button>
            </div>

            {showAdd && (
                <form onSubmit={handleAdd} className="bg-gray-50 border border-gray-200 rounded-card p-3 grid grid-cols-3 gap-2 items-end">
                    <div>
                        <label className="block text-xs text-gray-500 mb-0.5">ID Nhân viên</label>
                        <input className={inp} type="number" min={1} value={userId} onChange={e => setUserId(e.target.value)} />
                    </div>
                    <div>
                        <label className="block text-xs text-gray-500 mb-0.5">ID Phòng ban</label>
                        <input className={inp} type="number" min={1} value={unitId} onChange={e => setUnitId(e.target.value)} />
                    </div>
                    <div className="flex gap-2">
                        <button type="button" onClick={() => setShowAdd(false)} className="px-3 py-1 text-sm border border-gray-300 rounded-btn hover:bg-gray-50">Hủy</button>
                        <button type="submit" disabled={isCreating} className="px-3 py-1 text-sm bg-primary text-white rounded-btn hover:opacity-90 disabled:opacity-50">
                            {isCreating ? '...' : 'Thêm'}
                        </button>
                    </div>
                </form>
            )}

            {data.length === 0 ? (
                <p className="text-sm text-gray-400 text-center py-6">Chưa có nhân viên nào trong chính sách này</p>
            ) : (
                <table className="w-full text-sm">
                    <thead>
                        <tr className="border-b border-gray-200 text-gray-500 text-left">
                            <th className="pb-2 font-medium">ID NV</th>
                            <th className="pb-2 font-medium">Tên nhân viên</th>
                            <th className="pb-2 font-medium">Phòng ban</th>
                            <th className="pb-2 w-10"></th>
                        </tr>
                    </thead>
                    <tbody>
                        {data.map(item => (
                            <tr key={item.id} className="border-b border-gray-100 hover:bg-gray-50">
                                <td className="py-2 text-gray-600">{item.userId ?? '—'}</td>
                                <td className="py-2">{item.userName ?? '—'}</td>
                                <td className="py-2">{item.unitName ?? (item.unitId ? `ID: ${item.unitId}` : '—')}</td>
                                <td className="py-2">
                                    <button onClick={() => setDeleteTarget(item.id)} className="p-1 rounded hover:bg-red-50 text-gray-400 hover:text-danger"><FiTrash2 size={13} /></button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
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
