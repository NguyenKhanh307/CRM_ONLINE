import { useMemo, useState } from 'react';
import { FiPlus, FiTrash2 } from 'react-icons/fi';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { ScrollFrame } from '@/shared/components/table/ScrollFrame';
import { toIdNameMap } from '@/shared/utils/lookup';
import { useProductCategories } from '@/features/san-pham/hooks/useProductCategories';
import AddEntityModal from '../AddEntityModal';
import {
    usePolicyProductCategories, useCreatePolicyProductCategory, useDeletePolicyProductCategory,
} from '../../hooks/usePolicyProductCategories';

interface Props { policyId: number; }

/**
 * Danh mục sản phẩm trong chính sách giá — chỉ là marker "chọn nhanh": chọn 1 danh mục thì BE tự
 * bulk-seed toàn bộ sản phẩm thuộc danh mục vào tab "Sản phẩm" (giá để trống), người dùng qua đó
 * đặt giá/chiết khấu cho từng sản phẩm như bình thường. Không có gì để sửa ở đây.
 */
export function PolicyProductCategoriesTab({ policyId }: Props) {
    const { data = [], isLoading } = usePolicyProductCategories(policyId);
    const { mutate: createFn, isPending: isCreating } = useCreatePolicyProductCategory(policyId);
    const { mutate: deleteFn, isPending: isDeleting } = useDeletePolicyProductCategory(policyId);

    const { data: categories = [] } = useProductCategories();
    const options = useMemo(() => categories.map((c) => ({ id: c.id, label: c.name })), [categories]);
    const nameMap = useMemo(() => toIdNameMap(categories, 'id', 'name'), [categories]);
    const existingIds = useMemo(() => new Set(data.map(d => d.categoryId)), [data]);

    const [showAdd, setShowAdd] = useState(false);
    const [deleteTarget, setDeleteTarget] = useState<number | null>(null);

    const handleAdd = (categoryId: number) => {
        createFn({ pricePolicyId: policyId, categoryId });
    };

    if (isLoading) return <div className="p-4 text-gray-400 text-sm">Đang tải...</div>;

    return (
        <div className="space-y-3">
            <div className="flex items-center justify-between">
                <p className="text-sm text-gray-400">
                    Thêm danh mục sẽ tự động đưa các sản phẩm thuộc danh mục vào tab "Sản phẩm" (chưa có giá) —
                    vào đó để đặt giá/chiết khấu cho từng sản phẩm.
                </p>
                <button
                    onClick={() => setShowAdd(true)}
                    className="flex items-center gap-1.5 px-3 py-1.5 rounded-btn bg-primary text-white text-sm hover:opacity-90 shrink-0"
                >
                    <FiPlus size={14} /> Thêm danh mục
                </button>
            </div>

            {data.length === 0 ? (
                <p className="text-sm text-gray-400 text-center py-6">Chưa có danh mục sản phẩm nào trong chính sách này</p>
            ) : (
                <ScrollFrame visibleRows={10}>
                <table className="w-full text-sm">
                    <thead>
                        <tr className="text-gray-500 text-left">
                            <th className="py-2 font-medium">Danh mục sản phẩm</th>
                            <th className="py-2 w-16"></th>
                        </tr>
                    </thead>
                    <tbody>
                        {data.map(item => (
                            <tr key={item.id} className="border-b border-gray-100 hover:bg-gray-50">
                                <td className="py-2">{nameMap.get(item.categoryId) ?? '—'}</td>
                                <td className="py-2">
                                    <div className="flex justify-end">
                                        <button onClick={() => setDeleteTarget(item.id)} className="p-1 rounded hover:bg-red-50 text-gray-400 hover:text-danger"><FiTrash2 size={13} /></button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
                </ScrollFrame>
            )}

            {showAdd && (
                <AddEntityModal
                    title="Thêm danh mục sản phẩm"
                    confirmNoun="danh mục vào chính sách giá"
                    options={options}
                    existingIds={existingIds}
                    onAdd={handleAdd}
                    onClose={() => setShowAdd(false)}
                    isLoading={isCreating}
                />
            )}

            {deleteTarget !== null && (
                <ConfirmModal
                    message="Xóa danh mục này khỏi chính sách? (Các sản phẩm đã được thêm vào tab Sản phẩm sẽ vẫn giữ nguyên.)"
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
