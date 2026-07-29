import { useMemo, useRef, useState } from 'react';
import { collectErrors, dateRangeError, pastDateError } from '@/shared/utils/validators';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useNavigate } from 'react-router-dom';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { FormPageHeader } from '@/shared/components/form/FormPageHeader';
import { FormSection } from '@/shared/components/form/FormSection';
import { FieldRow } from '@/shared/components/form/FieldRow';
import { inputCls } from '@/shared/components/form/formStyles';
import { DateInput } from '@/shared/components/form/DateInput';
import { useAlert } from '@/shared/alert/useAlert';
import { useAuth } from '@/core/auth/useAuth';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import { useCreateCampaign } from '../hooks/useCreateCampaign';
import { CAMPAIGN_TYPE_LABELS } from '../config/campaignColumns';
import type { CreateCampaignPayload } from '../types/campaignTypes';

interface HeaderState {
    code: string; name: string; type: string; channel: string;
    startDate: string; endDate: string;
    budget: string; actualCost: string; targetSize: string; expectedRevenue: string;
    ownerId: string; description: string;
}

const TYPE_OPTIONS = Object.entries(CAMPAIGN_TYPE_LABELS).map(([value, label]) => ({ value, label }));

const initialState = (ownerId: string): HeaderState => ({
    code: '', name: '', type: 'other', channel: '', startDate: '', endDate: '',
    budget: '', actualCost: '', targetSize: '', expectedRevenue: '', ownerId, description: '',
});

/** Trang thêm Chiến dịch mới (layout AMIS). */
const CampaignAddPage = () => {
    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const { confirmCreate } = useConfirm();
    const { user } = useAuth();
    const defaultOwnerId = user ? String(user.id) : '';
    const [form, setForm] = useState<HeaderState>(() => initialState(defaultOwnerId));
    const { mutate, isPending } = useCreateCampaign();
    const { data: users = [] } = useActiveUsers();

    const userOptions = useMemo(() => users.map((u) => ({ value: String(u.id), label: u.fullName })), [users]);
    const [errors, setErrors] = useState<Record<string, string>>({});

    /** Cập nhật form và xóa lỗi của đúng những field vừa gõ. */
    const set = (patch: Partial<HeaderState>) => {
        setForm((p) => ({ ...p, ...patch }));
        setErrors((e) => {
            const next = { ...e };
            Object.keys(patch).forEach((k) => delete next[k]);
            return next;
        });
    };
    const reset = () => setForm(initialState(defaultOwnerId));
    const num = (v: string): number | null => (v === '' ? null : Number(v));

    /** Kiem tra bat buoc + bien (khop rang buoc backend) - tra map field->loi. */
    const validate = (): Record<string, string> =>
        collectErrors({
            code: !form.code.trim() ? 'Mã Chiến dịch không được để trống' : null,
            name: !form.name.trim() ? 'Tên Chiến dịch không được để trống' : null,
            startDate: pastDateError(form.startDate, 'Ngày bắt đầu'),
            endDate: pastDateError(form.endDate, 'Ngày kết thúc')
                ?? dateRangeError(form.startDate, form.endDate, 'ngày bắt đầu', 'Ngày kết thúc'),
        });

    const submit = async (andNew: boolean) => {
        // Loi nhap lieu hien do duoi o; popup xac nhan chi mo khi du lieu da hop le.
        const errs = validate();
        setErrors(errs);
        if (Object.keys(errs).length > 0) return;

        const payload: CreateCampaignPayload = {
            code: form.code.trim(),
            name: form.name.trim(),
            type: form.type || 'other',
            channel: form.channel || null,
            startDate: form.startDate || null,
            endDate: form.endDate || null,
            budget: num(form.budget),
            actualCost: num(form.actualCost),
            targetSize: num(form.targetSize),
            expectedRevenue: num(form.expectedRevenue),
            ownerId: form.ownerId ? Number(form.ownerId) : null,
            description: form.description || null,
        };
        if (!(await confirmCreate('chiến dịch'))) return;
        mutate(payload, {
            onSuccess: () => {
                if (andNew) { reset(); showAlert('Đã lưu Chiến dịch thành công'); }
                else navigate('/chien-dich');
            },
            onError: (err: unknown) => {
                const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                    ?? 'Có lỗi xảy ra khi lưu Chiến dịch';
                showAlert(msg);
            },
        });
    };

    const formRef = useRef<HTMLDivElement>(null);
    useFormKeyboardNav(formRef, { onSubmit: () => submit(false) });

    return (
        <div ref={formRef} className="p-6 bg-bg-main min-h-[calc(100vh-50px)]">
            <FormPageHeader title="Thêm Chiến dịch" saving={isPending}
                onCancel={() => navigate(-1)} onSave={() => submit(false)} onSaveAndNew={() => submit(true)} />

            <div className="bg-white rounded-card shadow-sm p-6 space-y-8">
                <FormSection title="Thông tin chung">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <div className="space-y-4">
                            <FieldRow label="Mã chiến dịch" required error={errors.code}>
                                <input type="text" value={form.code} onChange={(e) => set({ code: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Tên chiến dịch" required error={errors.name}>
                                <input type="text" value={form.name} onChange={(e) => set({ name: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Loại">
                                <SearchableSelect value={form.type} onChange={(v) => set({ type: v })} options={TYPE_OPTIONS} />
                            </FieldRow>
                            <FieldRow label="Kênh">
                                <input type="text" value={form.channel} onChange={(e) => set({ channel: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Người phụ trách">
                                <SearchableSelect value={form.ownerId} onChange={(v) => set({ ownerId: v })} options={userOptions} />
                            </FieldRow>
                        </div>
                        <div className="space-y-4">
                            <FieldRow label="Ngày bắt đầu" error={errors.startDate}>
                                <DateInput value={form.startDate} onChange={(v) => set({ startDate: v })} />
                            </FieldRow>
                            <FieldRow label="Ngày kết thúc" error={errors.endDate}>
                                <DateInput value={form.endDate} onChange={(v) => set({ endDate: v })} />
                            </FieldRow>
                            <FieldRow label="Ngân sách">
                                <input type="number" min={0} value={form.budget} onChange={(e) => set({ budget: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Quy mô mục tiêu">
                                <input type="number" min={0} value={form.targetSize} onChange={(e) => set({ targetSize: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Doanh số kỳ vọng">
                                <input type="number" min={0} value={form.expectedRevenue} onChange={(e) => set({ expectedRevenue: e.target.value })} className={inputCls} />
                            </FieldRow>
                        </div>
                    </div>
                </FormSection>

                <FormSection title="Thông tin mô tả">
                    <FieldRow label="Mô tả" alignTop>
                        <textarea rows={3} value={form.description} onChange={(e) => set({ description: e.target.value })} className={`${inputCls} resize-none`} />
                    </FieldRow>
                </FormSection>
            </div>
        </div>
    );
};

export default CampaignAddPage;
