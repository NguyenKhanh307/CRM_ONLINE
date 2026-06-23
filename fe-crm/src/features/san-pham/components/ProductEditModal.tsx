import { useState, type FormEvent, useEffect } from 'react';
import { FiX } from 'react-icons/fi';
import type { ProductResult, UpdateProductPayload } from '../types/productTypes';
import { useUpdateProduct } from '../hooks/useUpdateProduct';

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
    const [form, setForm] = useState<UpdateProductPayload>({
        name: '',
        categoryId: null,
        type: '',
        unit: null,
        basePrice: null,
        costPrice: null,
        vatRate: null,
        barcode: null,
        description: null,
        isDiscontinued: false,
        isActive: true,
        secondaryUnit: null, conversionRate: null, brand: null, origin: null,
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
            barcode: item.barcode,
            description: item.description,
            isDiscontinued: item.isDiscontinued,
            isActive: item.isActive,
            secondaryUnit: item.secondaryUnit, conversionRate: item.conversionRate,
            brand: item.brand, origin: item.origin,
        });
    }, [item]);

    if (!item) return null;

    const handleSubmit = (e: FormEvent) => {
        e.preventDefault();
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
                <form onSubmit={handleSubmit} className="px-5 py-4 space-y-3">
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
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Giá bán</label>
                            <input type="number" className={inp} value={form.basePrice ?? ''} onChange={e => setForm(f => ({ ...f, basePrice: e.target.value ? +e.target.value : null }))} />
                        </div>
                        <div>
                            <label className={lbl}>Giá vốn</label>
                            <input type="number" className={inp} value={form.costPrice ?? ''} onChange={e => setForm(f => ({ ...f, costPrice: e.target.value ? +e.target.value : null }))} />
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Thuế VAT (%)</label>
                            <input type="number" className={inp} value={form.vatRate ?? ''} onChange={e => setForm(f => ({ ...f, vatRate: e.target.value ? +e.target.value : null }))} />
                        </div>
                        <div>
                            <label className={lbl}>Mã vạch</label>
                            <input className={inp} value={form.barcode ?? ''} onChange={e => setForm(f => ({ ...f, barcode: e.target.value || null }))} />
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Thương hiệu</label>
                            <input className={inp} value={form.brand ?? ''} onChange={e => setForm(f => ({ ...f, brand: e.target.value || null }))} />
                        </div>
                        <div>
                            <label className={lbl}>Xuất xứ</label>
                            <input className={inp} value={form.origin ?? ''} onChange={e => setForm(f => ({ ...f, origin: e.target.value || null }))} />
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Đơn vị phụ</label>
                            <input className={inp} value={form.secondaryUnit ?? ''} onChange={e => setForm(f => ({ ...f, secondaryUnit: e.target.value || null }))} />
                        </div>
                        <div>
                            <label className={lbl}>Tỷ lệ quy đổi</label>
                            <input type="number" className={inp} value={form.conversionRate ?? ''} onChange={e => setForm(f => ({ ...f, conversionRate: e.target.value ? +e.target.value : null }))} />
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
                    <div className="flex justify-end gap-3 pt-2 border-t border-gray-100">
                        <button type="button" onClick={onClose} className="px-4 py-1.5 rounded-btn border border-gray-300 text-md text-text-main hover:bg-gray-50">Hủy</button>
                        <button type="submit" disabled={isPending} className="px-4 py-1.5 rounded-btn bg-primary text-white text-md hover:opacity-90 disabled:opacity-50">
                            {isPending ? 'Đang lưu...' : 'Lưu'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
