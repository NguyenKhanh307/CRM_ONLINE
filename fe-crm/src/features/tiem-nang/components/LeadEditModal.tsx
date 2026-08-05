import { useRef, useState, type FormEvent, useEffect } from 'react';
import { collectErrors, emailError, phoneError, taxCodeError } from '@/shared/utils/validators';
import { FieldError } from '@/shared/components/form/FormField';
import { ModalFooter } from '@/shared/components/ModalFooter';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { FiUserPlus, FiX } from 'react-icons/fi';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { RecordPicker } from '@/shared/components/form/RecordPicker';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import type { LeadResult, UpdateLeadPayload } from '../types/leadTypes';
import { useUpdateLead } from '../hooks/useUpdateLead';
import { useCampaignList } from '@/features/chien-dich/hooks/useCampaignList';
import { SOURCE_OPTIONS } from '../config/leadOptions';
import { LeadItemsPanel } from './LeadItemsPanel';

interface Props {
    item: LeadResult | null;
    onClose: () => void;
}

const LEAD_STATUS_OPTIONS = [
    { value: 'new', label: 'Mới' },
    { value: 'contacting', label: 'Đang liên hệ' },
    { value: 'converted', label: 'Đã chuyển đổi' },
];

export function LeadEditModal({ item, onClose }: Props) {
    const { mutate, isPending } = useUpdateLead();
    const { data: campaigns } = useCampaignList();
    const { data: users = [] } = useActiveUsers();
    const userOptions = users.map((u) => ({ value: String(u.id), label: u.fullName }));
    const [form, setForm] = useState<UpdateLeadPayload>({
        name: '', ownerId: null, contactId: null, convertedOpportunityId: null, campaignId: null,
        source: null, phone: null, email: null, note: null,
        companyName: null, leadType: null, taxCode: null, website: null, industry: null,
        status: 'new',
    });

    useEffect(() => {
        if (!item) return;
        setForm({
            name: item.name, ownerId: item.ownerId, contactId: item.contactId,
            convertedOpportunityId: item.convertedOpportunityId, campaignId: item.campaignId, source: item.source,
            phone: item.phone, email: item.email, note: item.note,
            companyName: item.companyName, leadType: item.leadType,
            taxCode: item.taxCode, website: item.website, industry: item.industry,
            status: item.status,
        });
    }, [item]);

    const [errors, setErrors] = useState<Record<string, string>>({});
    // hàm xóa lỗi của một ô ngay khi người dùng gõ lại
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

    // hàm lưu — lỗi hiện đỏ dưới ô, popup xác nhận chỉ mở khi dữ liệu đã hợp lệ
    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        // bước kiểm tra dữ liệu
        const errs = collectErrors({
            email: emailError(form.email),
            phone: phoneError(form.phone),
            taxCode: taxCodeError(form.taxCode),
        });
        setErrors(errs);
        if (Object.keys(errs).length > 0) return;

        // bước hỏi xác nhận rồi gọi api lưu
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
                            <label className={lbl}>Trạng thái</label>
                            <SearchableSelect
                                value={form.status}
                                onChange={v => setForm(f => ({ ...f, status: v || 'new' }))}
                                options={LEAD_STATUS_OPTIONS}
                            />
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
                        <div>
                            <label className={lbl}>Liên hệ liên kết</label>
                            <div className="flex items-center gap-1.5">
                                <div className="flex-1">
                                    <RecordPicker
                                        module="contact"
                                        value={form.contactId ? String(form.contactId) : ''}
                                        onChange={v => setForm(f => ({ ...f, contactId: v ? Number(v) : null }))}
                                        fallbackLabel={item.contactName}
                                    />
                                </div>
                                <button
                                    type="button"
                                    title="Tạo liên hệ mới (mở tab mới)"
                                    onClick={() => window.open('/lien-he/them-moi', '_blank')}
                                    className="shrink-0 p-2 rounded-btn border border-gray-300 text-gray-500 hover:border-primary hover:text-primary"
                                >
                                    <FiUserPlus size={14} />
                                </button>
                            </div>
                        </div>
                    </div>
                    <div>
                        <label className={lbl}>Cơ hội đã chuyển đổi</label>
                        <RecordPicker
                            module="opportunity"
                            value={form.convertedOpportunityId ? String(form.convertedOpportunityId) : ''}
                            onChange={v => setForm(f => ({ ...f, convertedOpportunityId: v ? Number(v) : null }))}
                            placeholder="— Chưa gán —"
                        />
                    </div>
                    <div>
                        <label className={lbl}>Chiến dịch nguồn</label>
                        <select className={inp} value={form.campaignId ?? ''} onChange={e => setForm(f => ({ ...f, campaignId: e.target.value ? +e.target.value : null }))}>
                            <option value="">-- Không gắn chiến dịch --</option>
                            {/* chiến dịch ngoài phạm vi lookup (lọc owner/năm/500 dòng) vẫn phải hiện,
                                nếu không ô sẽ trông như chưa gắn chiến dịch dù DB có dữ liệu */}
                            {form.campaignId != null && !campaigns?.some(c => c.id === form.campaignId) && (
                                <option value={form.campaignId}>{item.campaignName ?? `#${form.campaignId}`}</option>
                            )}
                            {campaigns?.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                        </select>
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
                    <div>
                        <label className={lbl}>Ngành nghề</label>
                        <input className={inp} value={form.industry ?? ''} onChange={e => setForm(f => ({ ...f, industry: e.target.value || null }))} />
                    </div>
                    <div>
                        <label className={lbl}>Ghi chú</label>
                        <textarea className={inp} rows={2} value={form.note ?? ''} onChange={e => setForm(f => ({ ...f, note: e.target.value || null }))} />
                    </div>
                    <div>
                        <label className={lbl}>Sản phẩm quan tâm</label>
                        <LeadItemsPanel leadId={item.id} />
                    </div>
                    <ModalFooter onCancel={onClose} saving={isPending} />
                </form>
            </div>
        </div>
    );
}
