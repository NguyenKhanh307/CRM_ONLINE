import { useRef, useState } from 'react';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { collectErrors } from '@/shared/utils/validators';
import { useNavigate } from 'react-router-dom';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { useMemo } from 'react';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { RecordPicker } from '@/shared/components/form/RecordPicker';
import type { RecordModule } from '@/shared/lookup/useRecordSearch';
import { FormPageHeader } from '@/shared/components/form/FormPageHeader';
import { FormSection } from '@/shared/components/form/FormSection';
import { FieldRow } from '@/shared/components/form/FieldRow';
import { inputCls } from '@/shared/components/form/formStyles';
import { DateTimeInput } from '@/shared/components/form/DateTimeInput';
import { useAlert } from '@/shared/alert/useAlert';
import { useAuth } from '@/core/auth/useAuth';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import { useCreateActivity } from '../hooks/useCreateActivity';
import type { CreateActivityPayload } from '../types/activityTypes';

const TYPE_OPTIONS = [
    { value: 'call', label: 'Gọi điện' },
    { value: 'task', label: 'Công việc' },
    { value: 'meeting', label: 'Cuộc họp' },
    { value: 'email', label: 'Email' },
    { value: 'note', label: 'Ghi chú' },
];
const PRIORITY_OPTIONS = [
    { value: 'low', label: 'Thấp' },
    { value: 'medium', label: 'Trung bình' },
    { value: 'high', label: 'Cao' },
];
const TARGET_TYPE_OPTIONS = [
    { value: 'customer', label: 'Khách hàng' },
    { value: 'lead', label: 'Tiềm năng' },
    { value: 'opportunity', label: 'Cơ hội' },
    { value: 'contact', label: 'Liên hệ' },
    { value: 'order', label: 'Đơn hàng' },
];
const CALL_DIRECTION_OPTIONS = [
    { value: 'in', label: 'Gọi đến' },
    { value: 'out', label: 'Gọi đi' },
];

interface FormState {
    type: string; subject: string; content: string; priority: string;
    assignedUserId: string; targetType: string; targetId: string;
    relatedType: string; relatedId: string; location: string; dueAt: string;
    callDirection: string; callResult: string; callDuration: string;
}

// state khởi tạo — người thực hiện mặc định là user đang đăng nhập
const initialState = (assignedUserId: string): FormState => ({
    type: 'call', subject: '', content: '', priority: 'medium',
    assignedUserId, targetType: '', targetId: '', relatedType: '', relatedId: '',
    location: '', dueAt: '', callDirection: '', callResult: '', callDuration: '',
});

const num = (s: string): number | null => (s.trim() ? Number(s) : null);

// trang thêm hoạt động mới — form full-page (layout AMIS)
const ActivityAddPage = () => {
    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const { confirmCreate } = useConfirm();
    const { user } = useAuth();
    const defaultUserId = user ? String(user.id) : '';
    const [form, setForm] = useState<FormState>(() => initialState(defaultUserId));
    const { mutate, isPending } = useCreateActivity();
    const { data: users = [] } = useActiveUsers();

    const userOptions = useMemo(() => users.map((u) => ({ value: String(u.id), label: u.fullName })), [users]);

    const [errors, setErrors] = useState<Record<string, string>>({});

    // hàm cập nhật form và xóa lỗi của đúng những field vừa gõ
    const set = (patch: Partial<FormState>) => {
        setForm((p) => ({ ...p, ...patch }));
        setErrors((e) => {
            const next = { ...e };
            Object.keys(patch).forEach((k) => delete next[k]);
            return next;
        });
    };

    // hàm kiểm tra bắt buộc + biên (khớp ràng buộc backend) — trả map field->lỗi
    const validate = (): Record<string, string> =>
        collectErrors({
            subject: !form.subject.trim() ? 'Tiêu đề không được để trống' : null,
        });

    // hàm lưu — lỗi hiện đỏ dưới ô, popup xác nhận chỉ mở khi dữ liệu đã hợp lệ
    const submit = async (andNew: boolean) => {
        // bước kiểm tra dữ liệu
        const errs = validate();
        setErrors(errs);
        if (Object.keys(errs).length > 0) return;

        const payload: CreateActivityPayload = {
            type: form.type,
            subject: form.subject.trim(),
            content: form.content || null,
            priority: form.priority || null,
            targetType: form.targetType || null,
            targetId: num(form.targetId),
            relatedType: form.relatedType || null,
            relatedId: num(form.relatedId),
            location: form.location || null,
            callDirection: form.type === 'call' ? (form.callDirection || null) : null,
            callResult: form.type === 'call' ? (form.callResult || null) : null,
            callDuration: form.type === 'call' ? num(form.callDuration) : null,
            assignedUserId: form.assignedUserId ? Number(form.assignedUserId) : null,
            dueAt: form.dueAt ? `${form.dueAt}:00` : null,
        };
        // bước hỏi xác nhận rồi mới gọi api lưu
        if (!(await confirmCreate('hoạt động'))) return;
        mutate(payload, {
            onSuccess: () => {
                if (andNew) { setForm(initialState(defaultUserId)); showAlert('Đã lưu hoạt động thành công'); }
                else navigate('/hoat-dong');
            },
            onError: (err: unknown) => {
                const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                    ?? 'Có lỗi xảy ra khi lưu hoạt động';
                showAlert(msg);
            },
        });
    };

    const formRef = useRef<HTMLDivElement>(null);
    useFormKeyboardNav(formRef, { onSubmit: () => submit(false) });

    return (
        <div ref={formRef} className="p-6 bg-bg-main min-h-[calc(100vh-50px)]">
            <FormPageHeader title="Thêm Hoạt động" saving={isPending}
                onCancel={() => navigate(-1)} onSave={() => submit(false)} onSaveAndNew={() => submit(true)} />

            <div className="bg-white rounded-card shadow-sm p-6 space-y-8">
                <FormSection title="Thông tin chung">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <div className="space-y-4">
                            <FieldRow label="Loại hoạt động" required>
                                <SearchableSelect value={form.type} onChange={(v) => set({ type: v })} options={TYPE_OPTIONS} />
                            </FieldRow>
                            <FieldRow label="Tiêu đề" required error={errors.subject}>
                                <input type="text" value={form.subject} onChange={(e) => set({ subject: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Mức ưu tiên">
                                <SearchableSelect value={form.priority} onChange={(v) => set({ priority: v })} options={PRIORITY_OPTIONS} />
                            </FieldRow>
                        </div>
                        <div className="space-y-4">
                            <FieldRow label="Người thực hiện">
                                <SearchableSelect value={form.assignedUserId} onChange={(v) => set({ assignedUserId: v })} options={userOptions} />
                            </FieldRow>
                        </div>
                    </div>
                    <div className="mt-4">
                        <FieldRow label="Nội dung" alignTop>
                            <textarea rows={3} value={form.content} onChange={(e) => set({ content: e.target.value })} className={`${inputCls} resize-none`} />
                        </FieldRow>
                    </div>
                </FormSection>

                <FormSection title="Đối tượng liên quan">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <div className="space-y-4">
                            <FieldRow label="Loại đối tượng">
                                {/* Đổi loại thì xóa id cũ — id của phân hệ khác gắn lại là dữ liệu rác */}
                                <SearchableSelect value={form.targetType}
                                    onChange={(v) => set({ targetType: v, targetId: '' })} options={TARGET_TYPE_OPTIONS} />
                            </FieldRow>
                            <FieldRow label="Đối tượng">
                                <RecordPicker module={form.targetType as RecordModule | ''} value={form.targetId}
                                    onChange={(v) => set({ targetId: v })} />
                            </FieldRow>
                        </div>
                        <div className="space-y-4">
                            <FieldRow label="Loại liên quan">
                                <SearchableSelect value={form.relatedType}
                                    onChange={(v) => set({ relatedType: v, relatedId: '' })} options={TARGET_TYPE_OPTIONS} />
                            </FieldRow>
                            <FieldRow label="Bản ghi liên quan">
                                <RecordPicker module={form.relatedType as RecordModule | ''} value={form.relatedId}
                                    onChange={(v) => set({ relatedId: v })} />
                            </FieldRow>
                        </div>
                    </div>
                </FormSection>

                <FormSection title="Chi tiết">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <FieldRow label="Địa điểm">
                            <input type="text" value={form.location} onChange={(e) => set({ location: e.target.value })} className={inputCls} />
                        </FieldRow>
                        <FieldRow label="Thời hạn">
                            <DateTimeInput value={form.dueAt} onChange={(v) => set({ dueAt: v })} />
                        </FieldRow>
                    </div>
                </FormSection>

                {form.type === 'call' && (
                    <FormSection title="Thông tin cuộc gọi">
                        <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                            <div className="space-y-4">
                                <FieldRow label="Hướng gọi">
                                    <SearchableSelect value={form.callDirection} onChange={(v) => set({ callDirection: v })} options={CALL_DIRECTION_OPTIONS} />
                                </FieldRow>
                                <FieldRow label="Thời lượng (phút)">
                                    <input type="number" min={0} value={form.callDuration} onChange={(e) => set({ callDuration: e.target.value })} className={inputCls} />
                                </FieldRow>
                            </div>
                            <FieldRow label="Kết quả">
                                <input type="text" value={form.callResult} onChange={(e) => set({ callResult: e.target.value })} className={inputCls} />
                            </FieldRow>
                        </div>
                    </FormSection>
                )}
            </div>
        </div>
    );
};

export default ActivityAddPage;
