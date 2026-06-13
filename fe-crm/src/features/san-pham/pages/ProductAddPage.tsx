import { useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { FormPageHeader } from '@/shared/components/form/FormPageHeader';
import { FormSection } from '@/shared/components/form/FormSection';
import { FieldRow } from '@/shared/components/form/FieldRow';
import { inputCls } from '@/shared/components/form/formStyles';
import { useAlert } from '@/shared/alert/useAlert';
import { useProductCategories } from '../hooks/useProductCategories';
import { useCreateProduct } from '../hooks/useCreateProduct';
import type { CreateProductPayload } from '../types/productTypes';

const TYPE_OPTIONS = [
    { value: 'goods', label: 'Hàng hóa' },
    { value: 'service', label: 'Dịch vụ' },
    { value: 'combo', label: 'Combo' },
    { value: 'material', label: 'Nguyên vật liệu' },
    { value: 'finished_goods', label: 'Thành phẩm' },
    { value: 'description_only', label: 'Chỉ mô tả' },
];

interface FormState {
    sku: string; name: string; categoryId: string; type: string; brand: string; origin: string;
    unit: string; secondaryUnit: string; conversionRate: string;
    composition: string; yarnCount: string; color: string; fabricWidth: string; weightGsm: string;
    basePrice: string; costPrice: string; vatRate: string; barcode: string;
    description: string; isDiscontinued: boolean; isActive: boolean;
}

const INITIAL: FormState = {
    sku: '', name: '', categoryId: '', type: 'goods', brand: '', origin: '', unit: '', secondaryUnit: '',
    conversionRate: '', composition: '', yarnCount: '', color: '', fabricWidth: '', weightGsm: '',
    basePrice: '', costPrice: '', vatRate: '', barcode: '', description: '', isDiscontinued: false, isActive: true,
};

const num = (s: string): number | null => (s.trim() ? Number(s) : null);

/** Trang thêm sản phẩm mới — form full-page (layout AMIS). */
const ProductAddPage = () => {
    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const [form, setForm] = useState<FormState>(INITIAL);
    const { mutate, isPending } = useCreateProduct();
    const { data: categories = [] } = useProductCategories();

    const categoryOptions = useMemo(() => categories.map((c) => ({ value: String(c.id), label: c.name })), [categories]);

    const set = (patch: Partial<FormState>) => setForm((p) => ({ ...p, ...patch }));

    const submit = (andNew: boolean) => {
        if (!form.sku.trim()) { showAlert('SKU không được để trống'); return; }
        if (!form.name.trim()) { showAlert('Tên hàng hóa không được để trống'); return; }
        const payload: CreateProductPayload = {
            sku: form.sku.trim(),
            name: form.name.trim(),
            categoryId: form.categoryId ? Number(form.categoryId) : null,
            type: form.type,
            unit: form.unit || null,
            secondaryUnit: form.secondaryUnit || null,
            conversionRate: num(form.conversionRate),
            composition: form.composition || null,
            yarnCount: form.yarnCount || null,
            color: form.color || null,
            fabricWidth: num(form.fabricWidth),
            weightGsm: num(form.weightGsm),
            brand: form.brand || null,
            origin: form.origin || null,
            basePrice: num(form.basePrice),
            costPrice: num(form.costPrice),
            vatRate: num(form.vatRate),
            barcode: form.barcode || null,
            description: form.description || null,
            isDiscontinued: form.isDiscontinued,
            isActive: form.isActive,
        };
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

    return (
        <div className="p-6 bg-bg-main min-h-[calc(100vh-50px)]">
            <FormPageHeader title="Thêm Sản phẩm" saving={isPending}
                onCancel={() => navigate(-1)} onSave={() => submit(false)} onSaveAndNew={() => submit(true)} />

            <div className="bg-white rounded-card shadow-sm p-6 space-y-8">
                <FormSection title="Thông tin chung">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <div className="space-y-4">
                            <FieldRow label="Mã SKU" required>
                                <input type="text" value={form.sku} onChange={(e) => set({ sku: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Tên hàng hóa" required>
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
                            <FieldRow label="Thương hiệu">
                                <input type="text" value={form.brand} onChange={(e) => set({ brand: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Xuất xứ">
                                <input type="text" value={form.origin} onChange={(e) => set({ origin: e.target.value })} className={inputCls} />
                            </FieldRow>
                        </div>
                    </div>
                </FormSection>

                <FormSection title="Đơn vị tính">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <div className="space-y-4">
                            <FieldRow label="Đơn vị chính">
                                <input type="text" value={form.unit} onChange={(e) => set({ unit: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Đơn vị phụ">
                                <input type="text" value={form.secondaryUnit} onChange={(e) => set({ secondaryUnit: e.target.value })} className={inputCls} />
                            </FieldRow>
                        </div>
                        <FieldRow label="Tỷ lệ quy đổi">
                            <input type="number" value={form.conversionRate} onChange={(e) => set({ conversionRate: e.target.value })} className={inputCls} />
                        </FieldRow>
                    </div>
                </FormSection>

                <FormSection title="Thông số kỹ thuật">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <div className="space-y-4">
                            <FieldRow label="Thành phần">
                                <input type="text" value={form.composition} onChange={(e) => set({ composition: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Chỉ số sợi">
                                <input type="text" value={form.yarnCount} onChange={(e) => set({ yarnCount: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Màu sắc">
                                <input type="text" value={form.color} onChange={(e) => set({ color: e.target.value })} className={inputCls} />
                            </FieldRow>
                        </div>
                        <div className="space-y-4">
                            <FieldRow label="Khổ vải (cm)">
                                <input type="number" value={form.fabricWidth} onChange={(e) => set({ fabricWidth: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Định lượng (g/m²)">
                                <input type="number" value={form.weightGsm} onChange={(e) => set({ weightGsm: e.target.value })} className={inputCls} />
                            </FieldRow>
                        </div>
                    </div>
                </FormSection>

                <FormSection title="Giá">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <div className="space-y-4">
                            <FieldRow label="Giá bán">
                                <input type="number" value={form.basePrice} onChange={(e) => set({ basePrice: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Giá vốn">
                                <input type="number" value={form.costPrice} onChange={(e) => set({ costPrice: e.target.value })} className={inputCls} />
                            </FieldRow>
                        </div>
                        <div className="space-y-4">
                            <FieldRow label="Thuế VAT (%)">
                                <input type="number" value={form.vatRate} onChange={(e) => set({ vatRate: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Mã vạch">
                                <input type="text" value={form.barcode} onChange={(e) => set({ barcode: e.target.value })} className={inputCls} />
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
