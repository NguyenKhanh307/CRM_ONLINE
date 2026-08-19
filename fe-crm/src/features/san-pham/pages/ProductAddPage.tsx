import { useMemo, useRef, useState } from 'react';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useNavigate } from 'react-router-dom';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { FormPageHeader } from '@/shared/components/form/FormPageHeader';
import { FormSection } from '@/shared/components/form/FormSection';
import { FieldRow } from '@/shared/components/form/FieldRow';
import { inputCls } from '@/shared/components/form/formStyles';
import { useAlert } from '@/shared/alert/useAlert';
import { collectErrors, nonNegativeError, percentError, sellPriceError, validateOrWarn } from '@/shared/utils/validators';
import { useProductCategories } from '../hooks/useProductCategories';
import { useCreateProduct } from '../hooks/useCreateProduct';
import type { CreateProductPayload } from '../types/productTypes';

const TYPE_OPTIONS = [
    { value: 'goods', label: 'Vật tư hàng hóa' },
    { value: 'service', label: 'Dịch vụ' },
];

const STATUS_OPTIONS = [
    { value: 'active', label: 'Đang kinh doanh' },
    { value: 'inactive', label: 'Ngừng hoạt động' },
    { value: 'discontinued', label: 'Ngừng kinh doanh' },
];

interface FormState {
    sku: string; name: string; categoryId: string; type: string;
    unit: string;
    basePrice: string; costPrice: string; vatRate: string;
    description: string; status: 'active' | 'inactive' | 'discontinued';
}

const INITIAL: FormState = {
    sku: '', name: '', categoryId: '', type: 'goods', unit: '',
    basePrice: '', costPrice: '', vatRate: '', description: '', status: 'active',
};

const num = (s: string): number | null => (s.trim() ? Number(s) : null);

/** Trang thêm sản phẩm mới — form full-page (layout AMIS). */
const ProductAddPage = () => {
    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const { confirmCreate } = useConfirm();
    const [form, setForm] = useState<FormState>(INITIAL);
    const [errors, setErrors] = useState<Record<string, string>>({});
    const { mutate, isPending } = useCreateProduct();
    const { data: categories = [] } = useProductCategories();

    const categoryOptions = useMemo(() => categories.map((c) => ({ value: String(c.id), label: c.name })), [categories]);

    /** Xóa lỗi của field khi người dùng gõ lại. */
    const set = (patch: Partial<FormState>) => {
        setForm((p) => ({ ...p, ...patch }));
        setErrors((e) => {
            const next = { ...e };
            Object.keys(patch).forEach((k) => delete next[k]);
            return next;
        });
    };

    /** Kiểm tra biên (khớp ràng buộc backend) — trả map field→lỗi. */
    const validate = (): Record<string, string> =>
        collectErrors({
            sku: !form.sku.trim() ? 'Mã SKU không được để trống' : null,
            name: !form.name.trim() ? 'Tên hàng hóa không được để trống' : null,
            basePrice: nonNegativeError(form.basePrice, 'Giá bán') ?? sellPriceError(form.basePrice, form.costPrice),
            costPrice: nonNegativeError(form.costPrice, 'Giá vốn'),
            vatRate: percentError(form.vatRate, 'Thuế VAT'),
        });

    const submit = async () => {
        const errs = validate();
        setErrors(errs);
        if (!validateOrWarn(errs, showAlert)) return;
        const payload: CreateProductPayload = {
            sku: form.sku.trim(),
            name: form.name.trim(),
            categoryId: form.categoryId ? Number(form.categoryId) : null,
            type: form.type,
            unit: form.unit || null,
            basePrice: num(form.basePrice),
            costPrice: num(form.costPrice),
            vatRate: num(form.vatRate),
            description: form.description || null,
            status: form.status,
        };
        if (!(await confirmCreate('hàng hóa'))) return;
        mutate(payload, {
            onSuccess: () => navigate('/san-pham'),
            onError: (err: unknown) => {
                const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                    ?? 'Có lỗi xảy ra khi lưu sản phẩm';
                showAlert(msg);
            },
        });
    };

    const formRef = useRef<HTMLDivElement>(null);
    useFormKeyboardNav(formRef, { onSubmit: () => submit() });

    return (
        <div ref={formRef} className="p-6 bg-bg-main min-h-[calc(100vh-50px)]">
            <FormPageHeader title="Thêm Sản phẩm" saving={isPending}
                onCancel={() => navigate(-1)} onSave={() => submit()} />

            <div className="bg-white rounded-card shadow-sm p-6 space-y-8">
                <FormSection title="Thông tin chung">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <div className="space-y-4">
                            <FieldRow label="Mã SKU" required error={errors.sku}>
                                <input type="text" value={form.sku} onChange={(e) => set({ sku: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Tên hàng hóa" required error={errors.name}>
                                <input type="text" value={form.name} onChange={(e) => set({ name: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Danh mục">
                                <SearchableSelect value={form.categoryId} onChange={(v) => set({ categoryId: v })} options={categoryOptions} />
                            </FieldRow>
                        </div>
                        <div className="space-y-4">
                            <FieldRow label="Loại">
                                <SearchableSelect value={form.type} onChange={(v) => set({ type: v })} options={TYPE_OPTIONS} />
                            </FieldRow>
                            <FieldRow label="Đơn vị tính">
                                <input type="text" value={form.unit} onChange={(e) => set({ unit: e.target.value })} className={inputCls} />
                            </FieldRow>
                        </div>
                    </div>
                </FormSection>

                <FormSection title="Giá">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <div className="space-y-4">
                            <FieldRow label="Giá bán" error={errors.basePrice}>
                                <input type="number" min={0} value={form.basePrice} onChange={(e) => set({ basePrice: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Giá vốn" error={errors.costPrice}>
                                <input type="number" min={0} value={form.costPrice} onChange={(e) => set({ costPrice: e.target.value })} className={inputCls} />
                            </FieldRow>
                        </div>
                        <div className="space-y-4">
                            <FieldRow label="Thuế VAT (%)" error={errors.vatRate}>
                                <input type="number" min={0} max={100} value={form.vatRate} onChange={(e) => set({ vatRate: e.target.value })} className={inputCls} />
                            </FieldRow>
                        </div>
                    </div>
                </FormSection>

                <FormSection title="Khác">
                    <div className="space-y-4">
                        <FieldRow label="Mô tả" alignTop>
                            <textarea rows={3} value={form.description} onChange={(e) => set({ description: e.target.value })} className={`${inputCls} resize-none`} />
                        </FieldRow>
                        <FieldRow label="Trạng thái">
                            <SearchableSelect value={form.status} onChange={(v) => set({ status: v as FormState['status'] })} options={STATUS_OPTIONS} />
                        </FieldRow>
                    </div>
                </FormSection>
            </div>
        </div>
    );
};

export default ProductAddPage;
