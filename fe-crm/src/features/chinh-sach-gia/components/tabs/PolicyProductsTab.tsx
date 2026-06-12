import { useState, type FormEvent } from 'react';
import { FiPlus, FiTrash2, FiEdit2, FiX } from 'react-icons/fi';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { usePolicyProducts, useCreatePolicyProduct, useUpdatePolicyProduct, useDeletePolicyProduct } from '../../hooks/usePolicyProducts';
import type { PricePolicyProductResult, CreatePricePolicyProductPayload, UpdatePricePolicyProductPayload, DiscountType } from '../../types/pricingTypes';

interface Props { policyId: number; }

const EMPTY_CREATE: Omit<CreatePricePolicyProductPayload, 'pricePolicyId'> = {
    productId: 0, price: null, discountType: null, discountValue: null, minQty: null,
};

export function PolicyProductsTab({ policyId }: Props) {
    const { data = [], isLoading } = usePolicyProducts(policyId);
    const { mutate: createFn, isPending: isCreating } = useCreatePolicyProduct(policyId);
    const { mutate: updateFn, isPending: isUpdating } = useUpdatePolicyProduct(policyId);
    const { mutate: deleteFn, isPending: isDeleting } = useDeletePolicyProduct(policyId);

    const [showAdd, setShowAdd] = useState(false);
    const [addForm, setAddForm] = useState(EMPTY_CREATE);
    const [editTarget, setEditTarget] = useState<PricePolicyProductResult | null>(null);
    const [editForm, setEditForm] = useState<UpdatePricePolicyProductPayload>({ price: null, discountType: null, discountValue: null, minQty: null });
    const [deleteTarget, setDeleteTarget] = useState<number | null>(null);

    const inp = 'w-full border border-gray-300 rounded-btn px-2 py-1 text-sm text-text-main focus:outline-none focus:border-primary';

    const handleAdd = (e: FormEvent) => {
        e.preventDefault();
        createFn({ pricePolicyId: policyId, ...addForm }, {
            onSuccess: () => { setShowAdd(false); setAddForm(EMPTY_CREATE); },
        });
    };

    const openEdit = (item: PricePolicyProductResult) => {
        setEditTarget(item);
        setEditForm({ price: item.price, discountType: item.discountType, discountValue: item.discountValue, minQty: item.minQty });
    };

    const handleEdit = (e: FormEvent) => {
        e.preventDefault();
        if (!editTarget) return;
        updateFn({ id: editTarget.id, payload: editForm }, { onSuccess: () => setEditTarget(null) });
    };

    if (isLoading) return <div className="p-4 text-gray-400 text-sm">Đang tải...</div>;

    return (
        <div className="space-y-3">
            <div className="flex justify-end">
                <button
                    onClick={() => setShowAdd(v => !v)}
                    className="flex items-center gap-1.5 px-3 py-1.5 rounded-btn bg-primary text-white text-sm hover:opacity-90"
                >
                    <FiPlus size={14} /> Thêm sản phẩm
                </button>
            </div>

            {showAdd && (
                <form onSubmit={handleAdd} className="bg-gray-50 border border-gray-200 rounded-card p-3 grid grid-cols-5 gap-2 items-end">
                    <div>
                        <label className="block text-xs text-gray-500 mb-0.5">ID Sản phẩm <span className="text-danger">*</span></label>
                        <input className={inp} type="number" required min={1} value={addForm.productId || ''} onChange={e => setAddForm(f => ({ ...f, productId: Number(e.target.value) }))} />
                    </div>
                    <div>
                        <label className="block text-xs text-gray-500 mb-0.5">Giá</label>
                        <input className={inp} type="number" min={0} value={addForm.price ?? ''} onChange={e => setAddForm(f => ({ ...f, price: e.target.value ? Number(e.target.value) : null }))} />
                    </div>
                    <div>
                        <label className="block text-xs text-gray-500 mb-0.5">Loại giảm giá</label>
                        <select className={inp} value={addForm.discountType ?? ''} onChange={e => setAddForm(f => ({ ...f, discountType: (e.target.value as DiscountType) || null }))}>
                            <option value="">—</option>
                            <option value="percent">%</option>
                            <option value="amount">Số tiền</option>
                        </select>
                    </div>
                    <div>
                        <label className="block text-xs text-gray-500 mb-0.5">Giá trị giảm</label>
                        <input className={inp} type="number" min={0} value={addForm.discountValue ?? ''} onChange={e => setAddForm(f => ({ ...f, discountValue: e.target.value ? Number(e.target.value) : null }))} />
                    </div>
                    <div>
                        <label className="block text-xs text-gray-500 mb-0.5">SL tối thiểu</label>
                        <input className={inp} type="number" min={0} value={addForm.minQty ?? ''} onChange={e => setAddForm(f => ({ ...f, minQty: e.target.value ? Number(e.target.value) : null }))} />
                    </div>
                    <div className="col-span-5 flex justify-end gap-2 pt-1">
                        <button type="button" onClick={() => setShowAdd(false)} className="px-3 py-1 text-sm border border-gray-300 rounded-btn hover:bg-gray-50">Hủy</button>
                        <button type="submit" disabled={isCreating} className="px-3 py-1 text-sm bg-primary text-white rounded-btn hover:opacity-90 disabled:opacity-50">
                            {isCreating ? 'Đang thêm...' : 'Thêm'}
                        </button>
                    </div>
                </form>
            )}

            {data.length === 0 ? (
                <p className="text-sm text-gray-400 text-center py-6">Chưa có sản phẩm nào trong chính sách này</p>
            ) : (
                <table className="w-full text-sm">
                    <thead>
                        <tr className="border-b border-gray-200 text-gray-500 text-left">
                            <th className="pb-2 font-medium">ID SP</th>
                            <th className="pb-2 font-medium">Tên sản phẩm</th>
                            <th className="pb-2 font-medium">Giá</th>
                            <th className="pb-2 font-medium">Giảm giá</th>
                            <th className="pb-2 font-medium">SL tối thiểu</th>
                            <th className="pb-2 w-16"></th>
                        </tr>
                    </thead>
                    <tbody>
                        {data.map(item => (
                            <tr key={item.id} className="border-b border-gray-100 hover:bg-gray-50">
                                <td className="py-2 text-gray-600">{item.productId}</td>
                                <td className="py-2">{item.productName ?? '—'}</td>
                                <td className="py-2">{item.price != null ? item.price.toLocaleString() : '—'}</td>
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
            )}

            {editTarget && (
                <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40" onClick={() => setEditTarget(null)}>
                    <div className="bg-white rounded-card shadow-lg w-full max-w-md mx-4" onClick={e => e.stopPropagation()}>
                        <div className="flex items-center justify-between px-5 py-3 border-b border-gray-200">
                            <h3 className="font-semibold text-text-main">Chỉnh sửa sản phẩm</h3>
                            <button onClick={() => setEditTarget(null)} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={16} /></button>
                        </div>
                        <form onSubmit={handleEdit} className="px-5 py-4 grid grid-cols-2 gap-3">
                            <div>
                                <label className="block text-sm text-gray-600 mb-1">Giá</label>
                                <input className={inp} type="number" min={0} value={editForm.price ?? ''} onChange={e => setEditForm(f => ({ ...f, price: e.target.value ? Number(e.target.value) : null }))} />
                            </div>
                            <div>
                                <label className="block text-sm text-gray-600 mb-1">SL tối thiểu</label>
                                <input className={inp} type="number" min={0} value={editForm.minQty ?? ''} onChange={e => setEditForm(f => ({ ...f, minQty: e.target.value ? Number(e.target.value) : null }))} />
                            </div>
                            <div>
                                <label className="block text-sm text-gray-600 mb-1">Loại giảm giá</label>
                                <select className={inp} value={editForm.discountType ?? ''} onChange={e => setEditForm(f => ({ ...f, discountType: (e.target.value as DiscountType) || null }))}>
                                    <option value="">—</option>
                                    <option value="percent">%</option>
                                    <option value="amount">Số tiền</option>
                                </select>
                            </div>
                            <div>
                                <label className="block text-sm text-gray-600 mb-1">Giá trị giảm</label>
                                <input className={inp} type="number" min={0} value={editForm.discountValue ?? ''} onChange={e => setEditForm(f => ({ ...f, discountValue: e.target.value ? Number(e.target.value) : null }))} />
                            </div>
                            <div className="col-span-2 flex justify-end gap-2 pt-1 border-t border-gray-100">
                                <button type="button" onClick={() => setEditTarget(null)} className="px-3 py-1.5 text-sm border border-gray-300 rounded-btn hover:bg-gray-50">Hủy</button>
                                <button type="submit" disabled={isUpdating} className="px-3 py-1.5 text-sm bg-primary text-white rounded-btn hover:opacity-90 disabled:opacity-50">
                                    {isUpdating ? 'Đang lưu...' : 'Lưu'}
                                </button>
                            </div>
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
