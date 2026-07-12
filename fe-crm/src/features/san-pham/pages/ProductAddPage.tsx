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
import { collectErrors, nonNegativeError, percentError, sellPriceError } from '@/shared/utils/validators';
import { useProductCategories } from '../hooks/useProductCategories';
import { useCreateProduct } from '../hooks/useCreateProduct';
import type { CreateProductPayload } from '../types/productTypes';

const TYPE_OPTIONS = [
    { value: 'goods', label: 'Vật tư hàng hóa' },
    { value: 'service', label: 'Dịch vụ' },
];

interface FormState {
    sku: string; name: string; categoryId: string; type: string;
    unit: string;
    basePrice: string; costPrice: string; vatRate: string;
    description: string; isDiscontinued: boolean; isActive: boolean;
}

const INITIAL: FormState = {
    sku: '', name: '', categoryId: '', type: 'goods', unit: '',
    basePrice: '', costPrice: '', vatRate: '', description: '', isDiscontinued: false, isActive: true,
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

    const submit = async (andNew: boolean) => {
        const errs = validate();
        setErrors(errs);
        if (Object.keys(errs).length > 0) return;
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
            isDiscontinued: form.isDiscontinued,
            isActive: form.isActive,
        };
        if (!(await confirmCreate('hàng hóa'))) return;
        mutate(payload, {
            onSuccess: () => {
                if (andNew) { setForm(INITIAL); showAlert('Đã lưu sản phẩm thành công'); }
                else navigate('/san-pham');
            },
            onError: (err: unknown) => {
                const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                    ?? 'Có lỗi xảy ra khi lưu sản phẩm';
                showAlert(msg);
            },
        });
    };

    const formRef = useRef<HTMLDivElement>(null);
    useFormKeyboardNav(formRef, { onSubmit: () => submit(false) });

    return (
        <div ref={formRef} className="p-6 bg-bg-main min-h-[calc(100vh-50px)]">
            <FormPageHeader title="Thêm Sản phẩm" saving={isPending}
                onCancel={() => navigate(-1)} onSave={() => submit(false)} onSaveAndNew={() => submit(true)} />

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
                        <div className="flex items-center gap-8 ml-[160px]">
                            <label className="flex items-center gap-2 cursor-pointer">
                                <input type="checkbox" checked={form.isActive} onChange={(e) => set({ isActive: e.target.checked })} className="w-4 h-4 accent-primary" />
                                <span className="text-md text-text-main">Đang kinh doanh</span>
                            </label>
                            <label className="flex items-center gap-2 cursor-pointer">
                                <input type="checkbox" checked={form.isDiscontinued} onChange={(e) => set({ isDiscontinued: e.target.checked })} className="w-4 h-4 accent-primary" />
                                <span className="text-md text-text-main">Ngừng theo dõi</span>
                            </label>
                        </div>
                    </div>
                </FormSection>
            </div>
        </div>
    );
};

export default ProductAddPage;
