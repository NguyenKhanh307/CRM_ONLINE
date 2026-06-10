import { useState, type FormEvent, useEffect } from 'react';
import { FiX } from 'react-icons/fi';
import type { ActivityResult, UpdateActivityPayload } from '../types/activityTypes';
import { useUpdateActivity } from '../hooks/useUpdateActivity';

interface Props {
    item: ActivityResult | null;
    onClose: () => void;
}

const ACTIVITY_TYPES = ['call', 'email', 'meeting', 'task', 'note'];
const ACTIVITY_TYPE_LABELS: Record<string, string> = {
    call: 'Cuộc gọi', email: 'Email', meeting: 'Cuộc họp', task: 'Công việc', note: 'Ghi chú',
};
const ACTIVITY_STATUSES = ['pending', 'in_progress', 'done', 'cancelled'];
const ACTIVITY_STATUS_LABELS: Record<string, string> = {
    pending: 'Chờ xử lý', in_progress: 'Đang thực hiện', done: 'Hoàn thành', cancelled: 'Đã hủy',
};

export function ActivityEditModal({ item, onClose }: Props) {
    const { mutate, isPending } = useUpdateActivity();
    const [form, setForm] = useState<UpdateActivityPayload>({
        type: 'call', subject: '', content: null, targetType: null,
        targetId: null, assignedUserId: null, status: 'pending', dueAt: null,
    });

    useEffect(() => {
        if (!item) return;
        setForm({
            type: item.type, subject: item.subject, content: item.content, targetType: item.targetType,
            targetId: item.targetId, assignedUserId: item.assignedUserId, status: item.status,
            dueAt: item.dueAt ? item.dueAt.substring(0, 16) : null,
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
                    <h2 className="text-lg font-semibold text-text-main">Chỉnh sửa hoạt động</h2>
                    <button onClick={onClose} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                </div>
                <form onSubmit={handleSubmit} className="px-5 py-4 space-y-3">
                    <div>
                        <label className={lbl}>Tiêu đề <span className="text-danger">*</span></label>
                        <input className={inp} required value={form.subject} onChange={e => setForm(f => ({ ...f, subject: e.target.value }))} />
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Loại</label>
                            <select className={inp} value={form.type} onChange={e => setForm(f => ({ ...f, type: e.target.value }))}>
                                {ACTIVITY_TYPES.map(t => <option key={t} value={t}>{ACTIVITY_TYPE_LABELS[t]}</option>)}
                            </select>
                        </div>
                        <div>
                            <label className={lbl}>Trạng thái</label>
                            <select className={inp} value={form.status} onChange={e => setForm(f => ({ ...f, status: e.target.value }))}>
                                {ACTIVITY_STATUSES.map(s => <option key={s} value={s}>{ACTIVITY_STATUS_LABELS[s]}</option>)}
                            </select>
                        </div>
                    </div>
                    <div>
                        <label className={lbl}>Hạn chót</label>
                        <input type="datetime-local" className={inp} value={form.dueAt ?? ''} onChange={e => setForm(f => ({ ...f, dueAt: e.target.value || null }))} />
                    </div>
                    <div>
                        <label className={lbl}>Nội dung</label>
                        <textarea className={inp} rows={3} value={form.content ?? ''} onChange={e => setForm(f => ({ ...f, content: e.target.value || null }))} />
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
