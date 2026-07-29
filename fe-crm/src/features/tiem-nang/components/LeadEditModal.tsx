import { useRef, useState, type FormEvent, useEffect } from 'react';
import { collectErrors, emailError, nonNegativeError, phoneError, taxCodeError } from '@/shared/utils/validators';
import { FieldError } from '@/shared/components/form/FormField';
import { ModalFooter } from '@/shared/components/ModalFooter';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { FiX } from 'react-icons/fi';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import type { LeadResult, UpdateLeadPayload } from '../types/leadTypes';
import { useUpdateLead } from '../hooks/useUpdateLead';
import { useCampaignList } from '@/features/chien-dich/hooks/useCampaignList';
import { SOURCE_OPTIONS } from '../config/leadOptions';

interface Props {
    item: LeadResult | null;
    onClose: () => void;
}

const LEAD_STATUS_LABELS: Record<string, string> = {
    new: 'Mới', contacting: 'Đang liên hệ', qualified: 'Đủ điều kiện', converted: 'Đã chuyển đổi', lost: 'Thất bại',
};
const LEAD_STATUS_COLORS: Record<string, string> = {
    new: 'bg-gray-100 text-gray-600', contacting: 'bg-blue-100 text-blue-700', qualified: 'bg-green-100 text-green-700',
    converted: 'bg-emerald-100 text-emerald-700', lost: 'bg-red-100 text-red-600',
};

export function LeadEditModal({ item, onClose }: Props) {
    const { mutate, isPending } = useUpdateLead();
    const { data: campaigns } = useCampaignList();
    const { data: users = [] } = useActiveUsers();
    const userOptions = users.map((u) => ({ value: String(u.id), label: u.fullName }));
    const [form, setForm] = useState<UpdateLeadPayload>({
        name: '', ownerId: null, customerId: null, contactId: null, campaignId: null,
        source: null, estimatedValue: null, phone: null, email: null, note: null,
        companyName: null, leadType: null, title: null, department: null,
        taxCode: null, website: null, industry: null, doNotCall: false, doNotEmail: false,
    });

    useEffect(() => {
        if (!item) return;
        setForm({
            name: item.name, ownerId: item.ownerId, customerId: item.customerId,
            contactId: item.contactId, campaignId: item.campaignId, source: item.source,
            estimatedValue: item.estimatedValue, phone: item.phone, email: item.email, note: item.note,
            companyName: item.companyName, leadType: item.leadType, title: item.title,
            department: item.department, taxCode: item.taxCode, website: item.website,
            industry: item.industry, doNotCall: item.doNotCall, doNotEmail: item.doNotEmail,
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
            email: emailError(form.email),
            phone: phoneError(form.phone),
            taxCode: taxCodeError(form.taxCode),
            estimatedValue: nonNegativeError(form.estimatedValue, 'Giá trị ước tính'),
        });
        setErrors(errs);
        if (Object.keys(errs).length > 0) return;

        if (!(await confirmSave('tiềm năng'))) return;
        mutate({ id: item.id, payload: form }, { onSuccess: onClose });
    };

    const inp = 'w-full border border-gray-300 rounded-btn px-3 py-1.5 text-md text-text-main focus:outline-none focus:border-primary';
    const lbl = 'block text-sm font-medium text-gray-700 mb-1';

    return (
        <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40" onClick={onClose}>
            <div className="bg-white rounded-card shadow-lg w-full max-w-lg mx-4 max-h-[90vh] overflow-y-auto" onClick={(e) => e.stopPropagation()}>
                <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
                    <h2 className="text-lg font-semibold text-text-main">Chỉnh sửa tiềm năng</h2>
                    <button onClick={onClose} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                </div>
                <form ref={formRef} onSubmit={handleSubmit} noValidate className="px-5 py-4 space-y-3">
                    <div>
                        <label className={lbl}>Tên <span className="text-danger">*</span></label>
                        <input className={inp} required value={form.name} onChange={e => setForm(f => ({ ...f, name: e.target.value }))} />
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Điện thoại</label>
                            <FieldError error={errors.phone}>
                                <input className={inp} value={form.phone ?? ''} onChange={e => { setForm(f => ({ ...f, phone: e.target.value || null })); clearError('phone'); }} />
                            </FieldError>
                        </div>
                        <div>
                            <label className={lbl}>Email</label>
                            <FieldError error={errors.email}>
                                <input type="email" className={inp} value={form.email ?? ''} onChange={e => { setForm(f => ({ ...f, email: e.target.value || null })); clearError('email'); }} />
                            </FieldError>
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Trạng thái (tự động/hành động)</label>
                            <span className={`inline-block px-2 py-1.5 rounded text-sm font-medium ${LEAD_STATUS_COLORS[item.status] ?? 'bg-gray-100 text-gray-600'}`}>
                                {LEAD_STATUS_LABELS[item.status] ?? item.status}
                            </span>
                        </div>
                        <div>
                            <label className={lbl}>Nguồn</label>
                            <SearchableSelect value={form.source ?? ''} onChange={v => setForm(f => ({ ...f, source: v || null }))} options={SOURCE_OPTIONS} />
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Người phụ trách</label>
                            <SearchableSelect
                                value={form.ownerId ? String(form.ownerId) : ''}
                                onChange={v => setForm(f => ({ ...f, ownerId: v ? Number(v) : null }))}
                                options={userOptions}
                                fallbackLabel={item.ownerName}
                            />
                        </div>
                    </div>
                    <div>
                        <label className={lbl}>Chiến dịch nguồn</label>
                        <select className={inp} value={form.campaignId ?? ''} onChange={e => setForm(f => ({ ...f, campaignId: e.target.value ? +e.target.value : null }))}>
                            <option value="">-- Không gắn chiến dịch --</option>
                            {/* Chiến dịch ngoài phạm vi lookup (lọc owner/năm/500 dòng) vẫn phải hiện,
                                nếu không ô sẽ trông như chưa gắn chiến dịch dù DB có dữ liệu. */}
                            {form.campaignId != null && !campaigns?.some(c => c.id === form.campaignId) && (
                                <option value={form.campaignId}>{item.campaignName ?? `#${form.campaignId}`}</option>
                            )}
                            {campaigns?.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                        </select>
                    </div>
                    <div>
                        <label className={lbl}>Giá trị dự kiến</label>
                        <FieldError error={errors.estimatedValue}>
                            <input type="number" min={0} className={inp} value={form.estimatedValue ?? ''} onChange={e => { setForm(f => ({ ...f, estimatedValue: e.target.value ? +e.target.value : null })); clearError('estimatedValue'); }} />
                        </FieldError>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Chức danh</label>
                            <input className={inp} value={form.title ?? ''} onChange={e => setForm(f => ({ ...f, title: e.target.value || null }))} />
                        </div>
                        <div>
                            <label className={lbl}>Phòng ban</label>
                            <input className={inp} value={form.department ?? ''} onChange={e => setForm(f => ({ ...f, department: e.target.value || null }))} />
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Tên tổ chức</label>
                            <input className={inp} value={form.companyName ?? ''} onChange={e => setForm(f => ({ ...f, companyName: e.target.value || null }))} />
                        </div>
                        <div>
                            <label className={lbl}>Loại tiềm năng</label>
                            <input className={inp} value={form.leadType ?? ''} onChange={e => setForm(f => ({ ...f, leadType: e.target.value || null }))} />
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Mã số thuế</label>
                            <FieldError error={errors.taxCode}>
                                <input className={inp} value={form.taxCode ?? ''} onChange={e => { setForm(f => ({ ...f, taxCode: e.target.value || null })); clearError('taxCode'); }} />
                            </FieldError>
                        </div>
                        <div>
                            <label className={lbl}>Website</label>
                            <input className={inp} value={form.website ?? ''} onChange={e => setForm(f => ({ ...f, website: e.target.value || null }))} />
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Ngành nghề</label>
                            <input className={inp} value={form.industry ?? ''} onChange={e => setForm(f => ({ ...f, industry: e.target.value || null }))} />
                        </div>
                        <div className="flex items-end gap-4 pb-1">
                            <label className="flex items-center gap-2 cursor-pointer">
                                <input type="checkbox" className="w-4 h-4 accent-primary" checked={form.doNotCall ?? false} onChange={e => setForm(f => ({ ...f, doNotCall: e.target.checked }))} />
                                <span className="text-sm text-text-main">Không gọi</span>
                            </label>
                            <label className="flex items-center gap-2 cursor-pointer">
                                <input type="checkbox" className="w-4 h-4 accent-primary" checked={form.doNotEmail ?? false} onChange={e => setForm(f => ({ ...f, doNotEmail: e.target.checked }))} />
                                <span className="text-sm text-text-main">Không email</span>
                            </label>
                        </div>
                    </div>
                    <div>
                        <label className={lbl}>Ghi chú</label>
                        <textarea className={inp} rows={2} value={form.note ?? ''} onChange={e => setForm(f => ({ ...f, note: e.target.value || null }))} />
                    </div>
                    <ModalFooter onCancel={onClose} saving={isPending} />
                </form>
            </div>
        </div>
    );
}
