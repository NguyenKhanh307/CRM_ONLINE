import { useState, useMemo } from 'react';
import { FiPlus, FiTrash2 } from 'react-icons/fi';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { ScrollFrame } from '@/shared/components/table/ScrollFrame';
import { toIdNameMap } from '@/shared/utils/lookup';
import { useProductCategories } from '@/features/san-pham/hooks/useProductCategories';
import AddEntityModal from '../AddEntityModal';
import { usePolicyProductTypes, useCreatePolicyProductType, useDeletePolicyProductType } from '../../hooks/usePolicyProductTypes';

interface Props { policyId: number; }

export function PolicyProductTypesTab({ policyId }: Props) {
    const { data = [], isLoading } = usePolicyProductTypes(policyId);
    const { mutate: createFn, isPending: isCreating } = useCreatePolicyProductType(policyId);
    const { mutate: deleteFn, isPending: isDeleting } = useDeletePolicyProductType(policyId);

    const { data: categories = [] } = useProductCategories();
    const options = useMemo(() => categories.map((c) => ({ id: c.id, label: c.name })), [categories]);
    const nameMap = useMemo(() => toIdNameMap(categories, 'id', 'name'), [categories]);
    const existingIds = useMemo(() => new Set(data.map(d => d.productTypeId)), [data]);

    const [showAdd, setShowAdd] = useState(false);
    const [deleteTarget, setDeleteTarget] = useState<number | null>(null);

    const handleAdd = (productTypeId: number) => {
        createFn({ pricePolicyId: policyId, productTypeId });
    };

    if (isLoading) return <div className="p-4 text-gray-400 text-sm">Đang tải...</div>;

    return (
        <div className="space-y-3">
            <div className="flex justify-end">
                <button onClick={() => setShowAdd(true)} className="flex items-center gap-1.5 px-3 py-1.5 rounded-btn bg-primary text-white text-sm hover:opacity-90">
                    <FiPlus size={14} /> Thêm loại sản phẩm
                </button>
            </div>

            {data.length === 0 ? (
                <p className="text-sm text-gray-400 text-center py-6">Chưa có loại sản phẩm nào trong chính sách này</p>
            ) : (
                <ScrollFrame visibleRows={10}>
                <table className="w-full text-sm">
                    <thead>
                        <tr className="text-gray-500 text-left">
                            <th className="py-2 font-medium">Tên loại</th>
                            <th className="py-2 w-10"></th>
                        </tr>
                    </thead>
                    <tbody>
                        {data.map(item => (
                            <tr key={item.id} className="border-b border-gray-100 hover:bg-gray-50">
                                <td className="py-2">{nameMap.get(item.productTypeId) ?? '—'}</td>
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
                    title="Thêm loại sản phẩm"
                    confirmNoun="loại sản phẩm vào chính sách giá"
                    options={options}
                    existingIds={existingIds}
                    onAdd={handleAdd}
                    onClose={() => setShowAdd(false)}
                    isLoading={isCreating}
                />
            )}

            {deleteTarget !== null && (
                <ConfirmModal
                    message="Xóa loại sản phẩm này khỏi chính sách?"
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
