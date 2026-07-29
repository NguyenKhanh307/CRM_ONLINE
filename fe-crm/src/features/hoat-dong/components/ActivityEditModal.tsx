import { useRef, useState, type FormEvent, useEffect } from 'react';
import { ModalFooter } from '@/shared/components/ModalFooter';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { collectErrors } from '@/shared/utils/validators';
import { FieldError } from '@/shared/components/form/FormField';
import { FiX } from 'react-icons/fi';
import type { ActivityResult, UpdateActivityPayload } from '../types/activityTypes';
import { useUpdateActivity } from '../hooks/useUpdateActivity';
import { DateTimeInput } from '@/shared/components/form/DateTimeInput';

interface Props {
    item: ActivityResult | null;
    onClose: () => void;
}

const ACTIVITY_TYPES = ['call', 'email', 'meeting', 'task', 'note'];
const ACTIVITY_TYPE_LABELS: Record<string, string> = {
    call: 'Cuộc gọi', email: 'Email', meeting: 'Cuộc họp', task: 'Công việc', note: 'Ghi chú',
};
const ACTIVITY_STATUS_LABELS: Record<string, string> = {
    planned: 'Đã lên kế hoạch', in_progress: 'Đang thực hiện', done: 'Hoàn thành', cancelled: 'Đã hủy',
};
const ACTIVITY_STATUS_COLORS: Record<string, string> = {
    planned: 'bg-gray-100 text-gray-600', in_progress: 'bg-yellow-100 text-yellow-700',
    done: 'bg-green-100 text-green-700', cancelled: 'bg-red-100 text-red-600',
};

export function ActivityEditModal({ item, onClose }: Props) {
    const { mutate, isPending } = useUpdateActivity();
    const [form, setForm] = useState<UpdateActivityPayload>({
        type: 'call', subject: '', content: null, targetType: null,
        targetId: null, assignedUserId: null, dueAt: null,
        priority: 'medium', location: null, callDirection: null, callResult: null, callDuration: null,
    });

    useEffect(() => {
        if (!item) return;
        setForm({
            type: item.type, subject: item.subject, content: item.content, targetType: item.targetType,
            targetId: item.targetId, assignedUserId: item.assignedUserId,
            dueAt: item.dueAt ? item.dueAt.substring(0, 16) : null,
            priority: item.priority ?? 'medium', location: item.location,
            callDirection: item.callDirection, callResult: item.callResult, callDuration: item.callDuration,
        });
    }, [item]);

    const [errors, setErrors] = useState<Record<string, string>>({});
    const clearErr = (key: string) => setErrors((prev) => (prev[key] ? { ...prev, [key]: '' } : prev));

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
        const errs = collectErrors({
            subject: !form.subject.trim() ? 'Tiêu đề không được để trống' : null,
        });
        setErrors(errs);
        if (Object.keys(errs).length > 0) return;
        if (!(await confirmSave('hoạt động'))) return;
        mutate({ id: item.id, payload: form }, { onSuccess: onClose });
    };

    const inp = 'w-full border border-gray-300 rounded-btn px-3 py-1.5 text-md text-text-main focus:outline-none focus:border-primary';
    const lbl = 'block text-sm font-medium text-gray-700 mb-1';

    return (
        <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40" onClick={onClose}>
            <div className="bg-white rounded-card shadow-lg w-full max-w-lg mx-4 max-h-[90vh] overflow-y-auto" onClick={(e) => e.stopPropagation()}>
                <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
                    <h2 className="text-lg font-semibold text-text-main">Chỉnh sửa hoạt động</h2>
                    <button onClick={onClose} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                </div>
                <form ref={formRef} onSubmit={handleSubmit} noValidate className="px-5 py-4 space-y-3">
                    <div>
                        <label className={lbl}>Tiêu đề <span className="text-danger">*</span></label>
                        <FieldError error={errors.subject}>
                            <input className={inp} value={form.subject}
                                onChange={e => { setForm(f => ({ ...f, subject: e.target.value })); clearErr('subject'); }} />
                        </FieldError>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Loại</label>
                            <select className={inp} value={form.type} onChange={e => setForm(f => ({ ...f, type: e.target.value }))}>
                                {ACTIVITY_TYPES.map(t => <option key={t} value={t}>{ACTIVITY_TYPE_LABELS[t]}</option>)}
                            </select>
                        </div>
                        <div>
                            <label className={lbl}>Trạng thái (đổi qua hành động)</label>
                            <span className={`inline-block px-2 py-1.5 rounded text-sm font-medium ${ACTIVITY_STATUS_COLORS[item.status] ?? 'bg-gray-100 text-gray-600'}`}>
                                {ACTIVITY_STATUS_LABELS[item.status] ?? item.status}
                            </span>
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Mức ưu tiên</label>
                            <select className={inp} value={form.priority ?? 'medium'} onChange={e => setForm(f => ({ ...f, priority: e.target.value }))}>
                                <option value="low">Thấp</option>
                                <option value="medium">Trung bình</option>
                                <option value="high">Cao</option>
                            </select>
                        </div>
                        <div>
                            <label className={lbl}>Hạn chót</label>
                            <DateTimeInput value={form.dueAt ?? ''} onChange={v => setForm(f => ({ ...f, dueAt: v || null }))} />
                        </div>
                    </div>
                    <div>
                        <label className={lbl}>Địa điểm</label>
                        <input className={inp} value={form.location ?? ''} onChange={e => setForm(f => ({ ...f, location: e.target.value || null }))} />
                    </div>
                    {form.type === 'call' && (
                        <div className="grid grid-cols-3 gap-3">
                            <div>
                                <label className={lbl}>Hướng gọi</label>
                                <select className={inp} value={form.callDirection ?? ''} onChange={e => setForm(f => ({ ...f, callDirection: e.target.value || null }))}>
                                    <option value="">-- Chọn --</option>
                                    <option value="in">Gọi đến</option>
                                    <option value="out">Gọi đi</option>
                                </select>
                            </div>
                            <div>
                                <label className={lbl}>Kết quả</label>
                                <input className={inp} value={form.callResult ?? ''} onChange={e => setForm(f => ({ ...f, callResult: e.target.value || null }))} />
                            </div>
                            <div>
                                <label className={lbl}>Thời lượng (phút)</label>
                                <input type="number" min={0} className={inp} value={form.callDuration ?? ''} onChange={e => setForm(f => ({ ...f, callDuration: e.target.value ? +e.target.value : null }))} />
                            </div>
                        </div>
                    )}
                    <div>
                        <label className={lbl}>Nội dung</label>
                        <textarea className={inp} rows={3} value={form.content ?? ''} onChange={e => setForm(f => ({ ...f, content: e.target.value || null }))} />
                    </div>
                    <ModalFooter onCancel={onClose} saving={isPending} />
                </form>
            </div>
        </div>
    );
}
