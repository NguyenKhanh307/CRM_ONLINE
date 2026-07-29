import { useMemo, useRef, useState, type FormEvent, useEffect } from 'react';
import { collectErrors, nonNegativeError, percentError, sellPriceError } from '@/shared/utils/validators';
import { FieldError } from '@/shared/components/form/FormField';
import { ModalFooter } from '@/shared/components/ModalFooter';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { FiX } from 'react-icons/fi';
import type { ProductResult, UpdateProductPayload } from '../types/productTypes';
import { useUpdateProduct } from '../hooks/useUpdateProduct';
import { useProductCategories } from '../hooks/useProductCategories';

interface Props {
    item: ProductResult | null;
    onClose: () => void;
}

/** Các loại hàng hóa — khớp enum ProductType ở backend. */
const PRODUCT_TYPE_OPTIONS: { value: string; label: string }[] = [
    { value: 'goods', label: 'Vật tư hàng hóa' },
    { value: 'service', label: 'Dịch vụ' },
];

export function ProductEditModal({ item, onClose }: Props) {
    const { mutate, isPending } = useUpdateProduct();
    const { data: categories = [] } = useProductCategories();
    const categoryOptions = useMemo(() => categories.map((c) => ({ value: String(c.id), label: c.name })), [categories]);
    const [form, setForm] = useState<UpdateProductPayload>({
        name: '',
        categoryId: null,
        type: '',
        unit: null,
        basePrice: null,
        costPrice: null,
        vatRate: null,
        description: null,
        isDiscontinued: false,
        isActive: true,
    });

    useEffect(() => {
        if (!item) return;
        setForm({
            name: item.name,
            categoryId: item.categoryId,
            type: item.type,
            unit: item.unit,
            basePrice: item.basePrice,
            costPrice: item.costPrice,
            vatRate: item.vatRate,
            description: item.description,
            isDiscontinued: item.isDiscontinued,
            isActive: item.isActive,
        });
    }, [item]);

    const [errors, setErrors] = useState<Record<string, string>>({});
    /** Xoa loi cua mot o ngay khi nguoi dung go lai. */
    const clearError = (key: string) =>
        setErrors((prev) => (prev[key] ? { ...prev, [key]: '' } : prev));

    const { confirmSave } = useConfirm();
    const formRef = useRef<HTMLFormElement>(null);
    useFormKeyboardNav(formRef, {
        onSubmit: () => formRef.current?.requestSubmit(),
        onCancel: onClose,
        enabled: !!item,
    });

    if (!item) return null;

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        // Lỗi nhập liệu hiện đỏ dưới ô; popup xác nhận chỉ mở khi dữ liệu đã hợp lệ.
        const errs = collectErrors({
            basePrice: nonNegativeError(form.basePrice, 'Giá bán') ?? sellPriceError(form.basePrice, form.costPrice),
            costPrice: nonNegativeError(form.costPrice, 'Giá vốn'),
            vatRate: percentError(form.vatRate, 'Thuế VAT'),
        });
        setErrors(errs);
        if (Object.keys(errs).length > 0) return;

        if (!(await confirmSave('hàng hóa'))) return;
        mutate({ id: item.id, payload: form }, { onSuccess: onClose });
    };

    const inp = 'w-full border border-gray-300 rounded-btn px-3 py-1.5 text-md text-text-main focus:outline-none focus:border-primary';
    const lbl = 'block text-sm font-medium text-gray-700 mb-1';

    return (
        <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40" onClick={onClose}>
            <div className="bg-white rounded-card shadow-lg w-full max-w-lg mx-4 max-h-[90vh] overflow-y-auto" onClick={(e) => e.stopPropagation()}>
                <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
                    <h2 className="text-lg font-semibold text-text-main">Chỉnh sửa sản phẩm</h2>
                    <button onClick={onClose} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                </div>
                <form ref={formRef} onSubmit={handleSubmit} noValidate className="px-5 py-4 space-y-3">
                    <div>
                        <label className={lbl}>Tên sản phẩm <span className="text-danger">*</span></label>
                        <input className={inp} required value={form.name} onChange={e => setForm(f => ({ ...f, name: e.target.value }))} />
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Loại</label>
                            <select className={inp} value={form.type} onChange={e => setForm(f => ({ ...f, type: e.target.value }))}>
                                {PRODUCT_TYPE_OPTIONS.map(o => (
                                    <option key={o.value} value={o.value}>{o.label}</option>
                                ))}
                            </select>
                        </div>
                        <div>
                            <label className={lbl}>Đơn vị</label>
                            <input className={inp} value={form.unit ?? ''} onChange={e => setForm(f => ({ ...f, unit: e.target.value || null }))} />
                        </div>
                    </div>
                    <div>
                        <label className={lbl}>Danh mục</label>
                        <SearchableSelect
                            value={form.categoryId != null ? String(form.categoryId) : ''}
                            onChange={(v) => setForm(f => ({ ...f, categoryId: v ? Number(v) : null }))}
                            options={categoryOptions}
                            fallbackLabel={item.categoryName}
                        />
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Giá bán</label>
                            <FieldError error={errors.basePrice}>
                                <input type="number" min={0} className={inp} value={form.basePrice ?? ''} onChange={e => { setForm(f => ({ ...f, basePrice: e.target.value ? +e.target.value : null })); clearError('basePrice'); }} />
                            </FieldError>
                        </div>
                        <div>
                            <label className={lbl}>Giá vốn</label>
                            <FieldError error={errors.costPrice}>
                                <input type="number" min={0} className={inp} value={form.costPrice ?? ''} onChange={e => { setForm(f => ({ ...f, costPrice: e.target.value ? +e.target.value : null })); clearError('costPrice'); }} />
                            </FieldError>
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Thuế VAT (%)</label>
                            <FieldError error={errors.vatRate}>
                                <input type="number" min={0} max={100} className={inp} value={form.vatRate ?? ''} onChange={e => { setForm(f => ({ ...f, vatRate: e.target.value ? +e.target.value : null })); clearError('vatRate'); }} />
                            </FieldError>
                        </div>
                    </div>
                    <div>
                        <label className={lbl}>Mô tả</label>
                        <textarea className={inp} rows={2} value={form.description ?? ''} onChange={e => setForm(f => ({ ...f, description: e.target.value || null }))} />
                    </div>
                    <div className="flex gap-6">
                        <label className="flex items-center gap-2 text-md text-text-main cursor-pointer">
                            <input type="checkbox" className="w-4 h-4 accent-primary" checked={form.isActive} onChange={e => setForm(f => ({ ...f, isActive: e.target.checked }))} />
                            Đang bán
                        </label>
                        <label className="flex items-center gap-2 text-md text-text-main cursor-pointer">
                            <input type="checkbox" className="w-4 h-4 accent-primary" checked={form.isDiscontinued} onChange={e => setForm(f => ({ ...f, isDiscontinued: e.target.checked }))} />
                            Ngừng kinh doanh
                        </label>
                    </div>
                    <ModalFooter onCancel={onClose} saving={isPending} />
                </form>
            </div>
        </div>
    );
}
