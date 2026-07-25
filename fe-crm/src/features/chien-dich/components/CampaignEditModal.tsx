import { useRef, useState, type FormEvent, useEffect } from 'react';
import { collectErrors, dateRangeError } from '@/shared/utils/validators';
import { FieldError } from '@/shared/components/form/FormField';
import { ModalFooter } from '@/shared/components/ModalFooter';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { FiX } from 'react-icons/fi';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { DateInput } from '@/shared/components/form/DateInput';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import type { CampaignResult, UpdateCampaignPayload } from '../types/campaignTypes';
import { useUpdateCampaign } from '../hooks/useUpdateCampaign';
import { CAMPAIGN_STATUS_LABELS, CAMPAIGN_TYPE_LABELS } from '../config/campaignColumns';

interface Props {
    item: CampaignResult | null;
    onClose: () => void;
}

const TYPE_OPTIONS = Object.entries(CAMPAIGN_TYPE_LABELS).map(([value, label]) => ({ value, label }));

export function CampaignEditModal({ item, onClose }: Props) {
    const { mutateAsync, isPending } = useUpdateCampaign();
    const { data: users = [] } = useActiveUsers();
    const [form, setForm] = useState<UpdateCampaignPayload>({
        name: '', type: 'other', channel: null, startDate: null, endDate: null,
        budget: null, actualCost: null, targetSize: null, expectedRevenue: null, ownerId: null, description: null,
    });

    useEffect(() => {
        if (!item) return;
        setForm({
            name: item.name, type: item.type, channel: item.channel,
            startDate: item.startDate, endDate: item.endDate,
            budget: item.budget, actualCost: item.actualCost, targetSize: item.targetSize,
            expectedRevenue: item.expectedRevenue, ownerId: item.ownerId, description: item.description,
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

    const num = (v: string): number | null => (v === '' ? null : Number(v));
    const userOptions = users.map((u) => ({ value: String(u.id), label: u.fullName }));

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        // Lỗi nhập liệu hiện đỏ dưới ô; popup xác nhận chỉ mở khi dữ liệu đã hợp lệ.
        const errs = collectErrors({
            endDate: dateRangeError(form.startDate, form.endDate, 'ngày bắt đầu', 'Ngày kết thúc'),
        });
        setErrors(errs);
        if (Object.keys(errs).length > 0) return;

        if (!(await confirmSave('chiến dịch'))) return;
        await mutateAsync({ id: item.id, payload: form });
        onClose();
    };

    const inp = 'w-full border border-gray-300 rounded-btn px-3 py-1.5 text-md text-text-main focus:outline-none focus:border-primary';
    const lbl = 'block text-sm font-medium text-gray-700 mb-1';

    return (
        <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40" onClick={onClose}>
            <div className="bg-white rounded-card shadow-lg w-full max-w-2xl mx-4 max-h-[90vh] overflow-y-auto" onClick={(e) => e.stopPropagation()}>
                <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
                    <h2 className="text-lg font-semibold text-text-main">Chỉnh sửa Chiến dịch</h2>
                    <button onClick={onClose} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                </div>
                <form ref={formRef} onSubmit={handleSubmit} noValidate className="px-5 py-4 space-y-3">
                    <div>
                        <label className={lbl}>Trạng thái (đổi qua hành động)</label>
                        <span className="inline-block px-2 py-1.5 rounded text-sm font-medium bg-gray-100 text-gray-600">
                            {CAMPAIGN_STATUS_LABELS[item.status] ?? item.status}
                        </span>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Tên chiến dịch</label>
                            <input className={inp} value={form.name ?? ''} onChange={e => setForm(f => ({ ...f, name: e.target.value }))} />
                        </div>
                        <div>
                            <label className={lbl}>Loại</label>
                            <SearchableSelect value={form.type ?? ''} onChange={v => setForm(f => ({ ...f, type: v }))} options={TYPE_OPTIONS} />
                        </div>
                        <div>
                            <label className={lbl}>Kênh</label>
                            <input className={inp} value={form.channel ?? ''} onChange={e => setForm(f => ({ ...f, channel: e.target.value || null }))} />
                        </div>
                        <div>
                            <label className={lbl}>Người phụ trách</label>
                            <SearchableSelect value={form.ownerId ? String(form.ownerId) : ''} onChange={v => setForm(f => ({ ...f, ownerId: v ? Number(v) : null }))} options={userOptions} fallbackLabel={item.ownerName} />
                        </div>
                        <div>
                            <label className={lbl}>Ngày bắt đầu</label>
                            <DateInput value={form.startDate ?? ''} onChange={v => setForm(f => ({ ...f, startDate: v || null }))} />
                        </div>
                        <div>
                            <label className={lbl}>Ngày kết thúc</label>
                            <FieldError error={errors.endDate}>
                                <DateInput value={form.endDate ?? ''} onChange={v => { setForm(f => ({ ...f, endDate: v || null })); clearError('endDate'); }} />
                            </FieldError>
                        </div>
                        <div>
                            <label className={lbl}>Ngân sách</label>
                            <input type="number" className={inp} value={form.budget ?? ''} onChange={e => setForm(f => ({ ...f, budget: num(e.target.value) }))} />
                        </div>
                        <div>
                            <label className={lbl}>Chi phí thực tế</label>
                            <input type="number" className={inp} value={form.actualCost ?? ''} onChange={e => setForm(f => ({ ...f, actualCost: num(e.target.value) }))} />
                        </div>
                        <div>
                            <label className={lbl}>Quy mô mục tiêu</label>
                            <input type="number" className={inp} value={form.targetSize ?? ''} onChange={e => setForm(f => ({ ...f, targetSize: num(e.target.value) }))} />
                        </div>
                        <div>
                            <label className={lbl}>Doanh số kỳ vọng</label>
                            <input type="number" className={inp} value={form.expectedRevenue ?? ''} onChange={e => setForm(f => ({ ...f, expectedRevenue: num(e.target.value) }))} />
                        </div>
                    </div>
                    <div>
                        <label className={lbl}>Mô tả</label>
                        <textarea className={inp} rows={2} value={form.description ?? ''} onChange={e => setForm(f => ({ ...f, description: e.target.value || null }))} />
                    </div>
                    <ModalFooter onCancel={onClose} saving={isPending} />
                </form>
            </div>
        </div>
    );
}
