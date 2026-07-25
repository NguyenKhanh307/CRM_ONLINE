import { useState, useMemo, useRef, type FormEvent } from 'react';
import { FiPlus, FiTrash2, FiEdit2, FiX } from 'react-icons/fi';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { ModalFooter } from '@/shared/components/ModalFooter';
import { FormField } from '@/shared/components/form/FormField';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { collectErrors, nonNegativeError, percentError } from '@/shared/utils/validators';
import { ScrollFrame } from '@/shared/components/table/ScrollFrame';
import { toIdNameMap } from '@/shared/utils/lookup';
import { formatNumber } from '@/shared/utils/number';
import { useProductList } from '@/features/san-pham/hooks/useProductList';
import AddEntityModal from '../AddEntityModal';
import { usePolicyProducts, useCreatePolicyProduct, useUpdatePolicyProduct, useDeletePolicyProduct } from '../../hooks/usePolicyProducts';
import type { PricePolicyProductResult, UpdatePricePolicyProductPayload, DiscountType } from '../../types/pricingTypes';

interface Props { policyId: number; }

export function PolicyProductsTab({ policyId }: Props) {
    const { data = [], isLoading } = usePolicyProducts(policyId);
    const { mutate: createFn, isPending: isCreating } = useCreatePolicyProduct(policyId);
    const { mutate: updateFn, isPending: isUpdating } = useUpdatePolicyProduct(policyId);
    const { mutate: deleteFn, isPending: isDeleting } = useDeletePolicyProduct(policyId);

    const { data: products = [] } = useProductList();
    const options = useMemo(() => products.map((p) => ({ id: p.id, label: `${p.sku} — ${p.name}`, sub: p.name })), [products]);
    const nameMap = useMemo(() => toIdNameMap(products, 'id', 'name'), [products]);
    const existingIds = useMemo(() => new Set(data.map(d => d.productId)), [data]);

    const [showAdd, setShowAdd] = useState(false);
    const [editTarget, setEditTarget] = useState<PricePolicyProductResult | null>(null);
    const [editForm, setEditForm] = useState<UpdatePricePolicyProductPayload>({ price: null, discountType: null, discountValue: null, minQty: null });
    const [deleteTarget, setDeleteTarget] = useState<number | null>(null);
    const [errors, setErrors] = useState<Record<string, string>>({});

    const { confirmSave } = useConfirm();

    const editFormRef = useRef<HTMLFormElement>(null);
    // enabled: !!editTarget — modal chỉ render khi có dòng đang sửa.
    useFormKeyboardNav(editFormRef, {
        onSubmit: () => editFormRef.current?.requestSubmit(),
        onCancel: () => setEditTarget(null),
        enabled: !!editTarget,
    });

    const inp = 'w-full border border-gray-300 rounded-btn px-2 py-1 text-sm text-text-main focus:outline-none focus:border-primary';

    const handleAdd = (productId: number) => {
        createFn({ pricePolicyId: policyId, productId, price: null, discountType: null, discountValue: null, minQty: null });
    };

    const openEdit = (item: PricePolicyProductResult) => {
        setEditTarget(item);
        setErrors({});
        setEditForm({ price: item.price, discountType: item.discountType, discountValue: item.discountValue, minQty: item.minQty });
    };

    const clearError = (key: string) =>
        setErrors((prev) => (prev[key] ? { ...prev, [key]: '' } : prev));

    const handleEdit = async (e: FormEvent) => {
        e.preventDefault();
        if (!editTarget) return;

        const found = collectErrors({
            price: nonNegativeError(editForm.price, 'Giá'),
            minQty: nonNegativeError(editForm.minQty, 'SL tối thiểu'),
            discountValue: editForm.discountType === 'percent'
                ? percentError(editForm.discountValue, 'Giá trị giảm')
                : nonNegativeError(editForm.discountValue, 'Giá trị giảm'),
        });
        setErrors(found);
        if (Object.keys(found).length > 0) return;

        if (!(await confirmSave('dòng sản phẩm'))) return;

        updateFn({ id: editTarget.id, payload: editForm }, { onSuccess: () => setEditTarget(null) });
    };

    if (isLoading) return <div className="p-4 text-gray-400 text-sm">Đang tải...</div>;

    return (
        <div className="space-y-3">
            <div className="flex justify-end">
                <button
                    onClick={() => setShowAdd(true)}
                    className="flex items-center gap-1.5 px-3 py-1.5 rounded-btn bg-primary text-white text-sm hover:opacity-90"
                >
                    <FiPlus size={14} /> Thêm sản phẩm
                </button>
            </div>

            {data.length === 0 ? (
                <p className="text-sm text-gray-400 text-center py-6">Chưa có sản phẩm nào trong chính sách này</p>
            ) : (
                <ScrollFrame visibleRows={10}>
                <table className="w-full text-sm">
                    <thead>
                        <tr className="text-gray-500 text-left">
                            <th className="py-2 font-medium">Tên sản phẩm</th>
                            <th className="py-2 font-medium">Giá</th>
                            <th className="py-2 font-medium">Giảm giá</th>
                            <th className="py-2 font-medium">SL tối thiểu</th>
                            <th className="py-2 w-16"></th>
                        </tr>
                    </thead>
                    <tbody>
                        {data.map(item => (
                            <tr key={item.id} className="border-b border-gray-100 hover:bg-gray-50">
                                <td className="py-2">{nameMap.get(item.productId) ?? '—'}</td>
                                <td className="py-2">{item.price != null ? formatNumber(item.price) : '—'}</td>
                                <td className="py-2">
                                    {item.discountValue != null
                                        ? `${item.discountValue}${item.discountType === 'percent' ? '%' : ' đ'}`
                                        : '—'}
                                </td>
                                <td className="py-2">{item.minQty ?? '—'}</td>
                                <td className="py-2">
                                    <div className="flex gap-1 justify-end">
                                        <button onClick={() => openEdit(item)} className="p-1 rounded hover:bg-gray-100 text-gray-400 hover:text-primary"><FiEdit2 size={13} /></button>
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
                    title="Thêm sản phẩm"
                    confirmNoun="sản phẩm vào chính sách giá"
                    options={options}
                    existingIds={existingIds}
                    onAdd={handleAdd}
                    onClose={() => setShowAdd(false)}
                    isLoading={isCreating}
                />
            )}

            {editTarget && (
                <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40" onClick={() => setEditTarget(null)}>
                    <div className="bg-white rounded-card shadow-lg w-full max-w-md mx-4" onClick={e => e.stopPropagation()}>
                        <div className="flex items-center justify-between px-5 py-3 border-b border-gray-200">
                            <h3 className="font-semibold text-text-main">Chỉnh sửa sản phẩm</h3>
                            <button onClick={() => setEditTarget(null)} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={16} /></button>
                        </div>
                        <form ref={editFormRef} onSubmit={handleEdit} noValidate className="px-5 py-4 grid grid-cols-2 gap-3">
                            <FormField label="Giá" error={errors.price}>
                                <input className={inp} type="number" min={0} value={editForm.price ?? ''}
                                    onChange={e => { setEditForm(f => ({ ...f, price: e.target.value ? Number(e.target.value) : null })); clearError('price'); }} />
                            </FormField>
                            <FormField label="SL tối thiểu" error={errors.minQty}>
                                <input className={inp} type="number" min={0} value={editForm.minQty ?? ''}
                                    onChange={e => { setEditForm(f => ({ ...f, minQty: e.target.value ? Number(e.target.value) : null })); clearError('minQty'); }} />
                            </FormField>
                            <FormField label="Loại giảm giá">
                                <select className={inp} value={editForm.discountType ?? ''}
                                    onChange={e => { setEditForm(f => ({ ...f, discountType: (e.target.value as DiscountType) || null })); clearError('discountValue'); }}>
                                    <option value="">—</option>
                                    <option value="percent">%</option>
                                    <option value="amount">Số tiền</option>
                                </select>
                            </FormField>
                            <FormField label="Giá trị giảm" error={errors.discountValue}>
                                <input className={inp} type="number" min={0} value={editForm.discountValue ?? ''}
                                    onChange={e => { setEditForm(f => ({ ...f, discountValue: e.target.value ? Number(e.target.value) : null })); clearError('discountValue'); }} />
                            </FormField>
                            <ModalFooter
                                className="col-span-2 flex justify-end gap-1.5 pt-1 border-t border-gray-100"
                                onCancel={() => setEditTarget(null)}
                                saving={isUpdating}
                            />
                        </form>
                    </div>
                </div>
            )}

            {deleteTarget !== null && (
                <ConfirmModal
                    message="Xóa sản phẩm này khỏi chính sách?"
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
