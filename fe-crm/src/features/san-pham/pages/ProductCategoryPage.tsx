import { useRef, useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiPlus, FiEdit2, FiTrash2, FiArrowLeft, FiX } from 'react-icons/fi';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { ActionButton } from '@/shared/components/ActionButton';
import { ModalFooter } from '@/shared/components/ModalFooter';
import { FormField } from '@/shared/components/form/FormField';
import { useAlert } from '@/shared/alert/useAlert';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { collectErrors, nonNegativeError } from '@/shared/utils/validators';
import { useProductCategories } from '../hooks/useProductCategories';
import { useCreateProductCategory, useUpdateProductCategory, useDeleteProductCategory } from '../hooks/useProductCategoryMutations';
import type { ProductCategoryResult } from '../services/productCategoryService';

interface FormState {
    code: string;
    name: string;
    sortOrder: string;
    isActive: boolean;
}

const EMPTY: FormState = { code: '', name: '', sortOrder: '0', isActive: true };

/** Chuyển danh mục → form state để sửa. */
const toForm = (c: ProductCategoryResult): FormState => ({
    code: c.code,
    name: c.name,
    sortOrder: String(c.sortOrder ?? 0),
    isActive: c.isActive,
});

/** Trang quản lý danh mục sản phẩm (CRUD product_categories). */
const ProductCategoryPage = () => {
    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const { data: categories = [], isLoading } = useProductCategories();
    const createFn = useCreateProductCategory();
    const updateFn = useUpdateProductCategory();
    const deleteFn = useDeleteProductCategory();

    const [modalOpen, setModalOpen] = useState(false);
    const [editId, setEditId] = useState<number | null>(null);
    const [form, setForm] = useState<FormState>(EMPTY);
    const [deleteId, setDeleteId] = useState<number | null>(null);
    const [errors, setErrors] = useState<Record<string, string>>({});

    const { confirmCreate, confirmSave } = useConfirm();

    const formRef = useRef<HTMLFormElement>(null);
    // enabled: modalOpen — form chỉ tồn tại khi modal mở.
    useFormKeyboardNav(formRef, {
        onSubmit: () => formRef.current?.requestSubmit(),
        onCancel: () => setModalOpen(false),
        enabled: modalOpen,
    });

    const openCreate = () => { setEditId(null); setForm(EMPTY); setErrors({}); setModalOpen(true); };
    const openEdit = (c: ProductCategoryResult) => { setEditId(c.id); setForm(toForm(c)); setErrors({}); setModalOpen(true); };

    const clearError = (key: string) =>
        setErrors((prev) => (prev[key] ? { ...prev, [key]: '' } : prev));

    const submit = async (e: FormEvent) => {
        e.preventDefault();

        const found = collectErrors({
            code: editId == null && !form.code.trim() ? 'Vui lòng nhập mã danh mục' : null,
            name: !form.name.trim() ? 'Vui lòng nhập tên danh mục' : null,
            sortOrder: nonNegativeError(form.sortOrder, 'Thứ tự'),
        });
        setErrors(found);
        if (Object.keys(found).length > 0) return;

        const ok = editId == null ? await confirmCreate('danh mục') : await confirmSave('danh mục');
        if (!ok) return;

        const onSuccess = () => setModalOpen(false);
        const onError = () => showAlert('Có lỗi xảy ra khi lưu danh mục');
        if (editId == null) {
            createFn.mutate({
                code: form.code.trim(), name: form.name.trim(),
                sortOrder: Number(form.sortOrder) || 0, isActive: form.isActive,
            }, { onSuccess, onError });
        } else {
            updateFn.mutate({
                id: editId,
                body: { name: form.name.trim(), sortOrder: Number(form.sortOrder) || 0, isActive: form.isActive },
            }, { onSuccess, onError });
        }
    };

    const saving = createFn.isPending || updateFn.isPending;
    const inp = 'w-full border border-gray-300 rounded-btn px-3 py-1.5 text-md text-text-main focus:outline-none focus:border-primary';

    return (
        // Không min-h-screen: MainLayout đã h-screen overflow-hidden, <main> là vùng cuộn duy nhất.
        <div className="p-6 bg-bg-main">
            <div className="flex items-center justify-between mb-4">
                <div className="flex items-center gap-3">
                    <button onClick={() => navigate('/san-pham')} className="p-1.5 rounded hover:bg-gray-100 text-gray-500" title="Quay lại">
                        <FiArrowLeft size={18} />
                    </button>
                    <h1 className="text-xl font-semibold text-text-main">Quản lý danh mục sản phẩm</h1>
                </div>
                <ActionButton variant="primary" icon={FiPlus} onClick={openCreate}>
                    Thêm danh mục
                </ActionButton>
            </div>

            <div className="bg-white rounded-card p-4 shadow-sm">
                <table className="w-full text-table">
                    <thead>
                        <tr className="text-title font-semibold bg-gray-100 border-b-2 border-gray-300 text-left">
                            <th className="px-3 py-2 w-28">Mã</th>
                            <th className="px-3 py-2">Tên danh mục</th>
                            <th className="px-3 py-2 w-20">Thứ tự</th>
                            <th className="px-3 py-2 w-28">Trạng thái</th>
                            <th className="px-3 py-2 w-24 text-right"></th>
                        </tr>
                    </thead>
                    <tbody>
                        {isLoading ? (
                            <tr><td colSpan={5} className="px-3 py-6 text-center text-gray-400">Đang tải...</td></tr>
                        ) : categories.length === 0 ? (
                            <tr><td colSpan={5} className="px-3 py-6 text-center text-gray-400">Chưa có danh mục nào</td></tr>
                        ) : (
                            categories.map((c) => (
                                <tr key={c.id} className="border-b border-gray-200 hover:bg-gray-50">
                                    <td className="px-3 py-2 text-gray-500">{c.code}</td>
                                    <td className="px-3 py-2 font-medium text-text-main">{c.name}</td>
                                    <td className="px-3 py-2">{c.sortOrder}</td>
                                    <td className="px-3 py-2">
                                        {c.isActive
                                            ? <span className="px-2 py-0.5 rounded text-sm bg-green-100 text-green-700">Đang hoạt động</span>
                                            : <span className="px-2 py-0.5 rounded text-sm bg-gray-100 text-gray-600">Ngừng hoạt động</span>}
                                    </td>
                                    <td className="px-3 py-2">
                                        <div className="flex gap-1 justify-end">
                                            <button onClick={() => openEdit(c)} className="p-1.5 rounded hover:bg-gray-100 text-gray-400 hover:text-primary" title="Sửa">
                                                <FiEdit2 size={14} />
                                            </button>
                                            <button onClick={() => setDeleteId(c.id)} className="p-1.5 rounded hover:bg-red-50 text-gray-400 hover:text-danger" title="Xóa">
                                                <FiTrash2 size={14} />
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>

            {modalOpen && (
                <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40" onClick={() => setModalOpen(false)}>
                    <div className="bg-white rounded-card shadow-lg w-full max-w-md mx-4" onClick={(e) => e.stopPropagation()}>
                        <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
                            <h2 className="text-lg font-semibold text-text-main">{editId == null ? 'Thêm danh mục' : 'Sửa danh mục'}</h2>
                            <button onClick={() => setModalOpen(false)} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                        </div>
                        <form ref={formRef} onSubmit={submit} noValidate className="px-5 py-4 space-y-3">
                            <FormField label="Mã danh mục" required error={errors.code}>
                                <input className={inp} value={form.code} disabled={editId != null}
                                    onChange={(e) => { setForm(f => ({ ...f, code: e.target.value })); clearError('code'); }} />
                            </FormField>
                            <FormField label="Tên danh mục" required error={errors.name}>
                                <input className={inp} value={form.name}
                                    onChange={(e) => { setForm(f => ({ ...f, name: e.target.value })); clearError('name'); }} />
                            </FormField>
                            <FormField label="Thứ tự" error={errors.sortOrder}>
                                <input type="number" min={0} className={inp} value={form.sortOrder}
                                    onChange={(e) => { setForm(f => ({ ...f, sortOrder: e.target.value })); clearError('sortOrder'); }} />
                            </FormField>
                            <label className="flex items-center gap-2 text-md text-text-main pt-1">
                                <input type="checkbox" checked={form.isActive}
                                    onChange={(e) => setForm(f => ({ ...f, isActive: e.target.checked }))} />
                                Đang hoạt động
                            </label>
                            <ModalFooter onCancel={() => setModalOpen(false)} saving={saving} />
                        </form>
                    </div>
                </div>
            )}

            {deleteId !== null && (
                <ConfirmModal
                    message="Bạn có chắc muốn xóa danh mục này? Các sản phẩm đang gắn danh mục này sẽ chuyển về chưa phân loại."
                    confirmLabel="Xóa"
                    confirmDanger
                    isLoading={deleteFn.isPending}
                    onConfirm={() => deleteFn.mutate(deleteId, { onSuccess: () => setDeleteId(null) })}
                    onCancel={() => setDeleteId(null)}
                />
            )}
        </div>
    );
};

export default ProductCategoryPage;
