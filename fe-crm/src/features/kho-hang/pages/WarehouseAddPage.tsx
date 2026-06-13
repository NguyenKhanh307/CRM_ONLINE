import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FormPageHeader } from '@/shared/components/form/FormPageHeader';
import { FormSection } from '@/shared/components/form/FormSection';
import { FieldRow } from '@/shared/components/form/FieldRow';
import { inputCls } from '@/shared/components/form/formStyles';
import { useAlert } from '@/shared/alert/useAlert';
import { useCreateWarehouse } from '../hooks/useCreateWarehouse';
import type { CreateWarehousePayload } from '../types/warehouseTypes';

interface FormState {
    code: string;
    name: string;
    address: string;
    isActive: boolean;
}

const INITIAL: FormState = { code: '', name: '', address: '', isActive: true };

/** Trang thêm kho hàng mới — form full-page (layout AMIS). */
const WarehouseAddPage = () => {
    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const [form, setForm] = useState<FormState>(INITIAL);
    const { mutate, isPending } = useCreateWarehouse();

    const set = (patch: Partial<FormState>) => setForm((p) => ({ ...p, ...patch }));

    const submit = (andNew: boolean) => {
        if (!form.code.trim()) { showAlert('Mã kho không được để trống'); return; }
        if (!form.name.trim()) { showAlert('Tên kho không được để trống'); return; }
        const payload: CreateWarehousePayload = {
            code: form.code.trim(),
            name: form.name.trim(),
            address: form.address || null,
            isActive: form.isActive,
        };
        mutate(payload, {
            onSuccess: () => {
                if (andNew) { setForm(INITIAL); showAlert('Đã lưu kho hàng thành công'); }
                else navigate('/kho-hang');
            },
            onError: (err: unknown) => {
                const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                    ?? 'Có lỗi xảy ra khi lưu kho hàng';
                showAlert(msg);
            },
        });
    };

    return (
        <div className="p-6 bg-bg-main min-h-[calc(100vh-50px)]">
            <FormPageHeader title="Thêm Kho hàng" saving={isPending}
                onCancel={() => navigate(-1)} onSave={() => submit(false)} onSaveAndNew={() => submit(true)} />

            <div className="bg-white rounded-card shadow-sm p-6">
                <FormSection title="Thông tin chung">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <div className="space-y-4">
                            <FieldRow label="Mã kho" required>
                                <input type="text" value={form.code} onChange={(e) => set({ code: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Tên kho" required>
                                <input type="text" value={form.name} onChange={(e) => set({ name: e.target.value })} className={inputCls} />
                            </FieldRow>
                        </div>
                        <div className="space-y-4">
                            <FieldRow label="Địa chỉ" alignTop>
                                <textarea rows={2} value={form.address} onChange={(e) => set({ address: e.target.value })} className={`${inputCls} resize-none`} />
                            </FieldRow>
                            <label className="flex items-center gap-2 cursor-pointer w-fit ml-[160px]">
                                <input type="checkbox" checked={form.isActive} onChange={(e) => set({ isActive: e.target.checked })} className="w-4 h-4 accent-primary" />
                                <span className="text-md text-text-main">Đang hoạt động</span>
                            </label>
                        </div>
                    </div>
                </FormSection>
            </div>
        </div>
    );
};

export default WarehouseAddPage;
