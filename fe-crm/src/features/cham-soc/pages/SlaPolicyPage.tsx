import { useRef, useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiPlus, FiEdit2, FiTrash2, FiArrowLeft, FiX } from 'react-icons/fi';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { ActionButton } from '@/shared/components/ActionButton';
import { ModalFooter } from '@/shared/components/ModalFooter';
import { FormField } from '@/shared/components/form/FormField';
import { useAlert } from '@/shared/alert/useAlert';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { collectErrors, positiveError } from '@/shared/utils/validators';
import { PRIORITY_LABELS, PRIORITY_COLORS, PRIORITY_OPTIONS } from '../config/ticketEnums';
import { useSlaPolicies } from '../hooks/useSlaPolicies';
import { useCreateSlaPolicy, useUpdateSlaPolicy, useDeleteSlaPolicy } from '../hooks/useSlaPolicyMutations';
import type { SlaPolicyResult } from '../services/slaPolicyService';

interface FormState {
    code: string;
    name: string;
    priority: string;
    firstResponseHours: string;
    resolutionHours: string;
    isActive: boolean;
}

const EMPTY: FormState = { code: '', name: '', priority: 'medium', firstResponseHours: '4', resolutionHours: '24', isActive: true };

// chuyển chính sách -> form state để sửa
const toForm = (p: SlaPolicyResult): FormState => ({
    code: p.code,
    name: p.name,
    priority: p.priority,
    firstResponseHours: String(p.firstResponseHours),
    resolutionHours: String(p.resolutionHours),
    isActive: p.isActive,
});

// trang quản lý chính sách SLA (CRUD sla_policies)
const SlaPolicyPage = () => {
    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const { data: policies = [], isLoading } = useSlaPolicies();
    const createFn = useCreateSlaPolicy();
    const updateFn = useUpdateSlaPolicy();
    const deleteFn = useDeleteSlaPolicy();

    const [modalOpen, setModalOpen] = useState(false);
    const [editId, setEditId] = useState<number | null>(null);
    const [form, setForm] = useState<FormState>(EMPTY);
    const [deleteId, setDeleteId] = useState<number | null>(null);
    const [errors, setErrors] = useState<Record<string, string>>({});

    const { confirmCreate, confirmSave } = useConfirm();

    const formRef = useRef<HTMLFormElement>(null);
    // enabled: modalOpen — form chỉ tồn tại khi modal mở
    useFormKeyboardNav(formRef, {
        onSubmit: () => formRef.current?.requestSubmit(),
        onCancel: () => setModalOpen(false),
        enabled: modalOpen,
    });

    const openCreate = () => { setEditId(null); setForm(EMPTY); setErrors({}); setModalOpen(true); };
    const openEdit = (p: SlaPolicyResult) => { setEditId(p.id); setForm(toForm(p)); setErrors({}); setModalOpen(true); };

    const clearError = (key: string) =>
        setErrors((prev) => (prev[key] ? { ...prev, [key]: '' } : prev));

    const submit = async (e: FormEvent) => {
        e.preventDefault();

        const found = collectErrors({
            code: editId == null && !form.code.trim() ? 'Vui lòng nhập mã chính sách' : null,
            name: !form.name.trim() ? 'Vui lòng nhập tên chính sách' : null,
            firstResponseHours: positiveError(form.firstResponseHours, 'Hạn phản hồi đầu'),
            resolutionHours: positiveError(form.resolutionHours, 'Hạn giải quyết'),
        });
        setErrors(found);
        if (Object.keys(found).length > 0) return;

        const ok = editId == null ? await confirmCreate('chính sách SLA') : await confirmSave('chính sách SLA');
        if (!ok) return;

        const onSuccess = () => setModalOpen(false);
        const onError = () => showAlert('Có lỗi xảy ra khi lưu chính sách SLA');
        const shared = {
            name: form.name.trim(),
            priority: form.priority,
            firstResponseHours: Number(form.firstResponseHours) || 0,
            resolutionHours: Number(form.resolutionHours) || 0,
            isActive: form.isActive,
        };
        if (editId == null) createFn.mutate({ code: form.code.trim(), ...shared }, { onSuccess, onError });
        else updateFn.mutate({ id: editId, body: shared }, { onSuccess, onError });
    };

    const saving = createFn.isPending || updateFn.isPending;
    const inp = 'w-full border border-gray-300 rounded-btn px-3 py-1.5 text-md text-text-main focus:outline-none focus:border-primary';

    return (
        // không min-h-screen: MainLayout đã h-screen overflow-hidden, <main> là vùng cuộn duy nhất
        <div className="p-6 bg-bg-main">
            <div className="flex items-center justify-between mb-4">
                <div className="flex items-center gap-3">
                    <button onClick={() => navigate('/cham-soc')} className="p-1.5 rounded hover:bg-gray-100 text-gray-500" title="Quay lại">
                        <FiArrowLeft size={18} />
                    </button>
                    <h1 className="text-xl font-semibold text-text-main">Quản lý chính sách SLA</h1>
                </div>
                <ActionButton variant="primary" icon={FiPlus} onClick={openCreate}>
                    Thêm chính sách
                </ActionButton>
            </div>

            <div className="bg-white rounded-card p-4 shadow-sm">
                <table className="w-full text-table">
                    <thead>
                        <tr className="text-title font-semibold bg-gray-100 border-b-2 border-gray-300 text-left">
                            <th className="px-3 py-2 w-28">Mã</th>
                            <th className="px-3 py-2">Tên chính sách</th>
                            <th className="px-3 py-2 w-28">Độ ưu tiên</th>
                            <th className="px-3 py-2 w-36">Hạn phản hồi đầu (giờ)</th>
                            <th className="px-3 py-2 w-32">Hạn giải quyết (giờ)</th>
                            <th className="px-3 py-2 w-28">Trạng thái</th>
                            <th className="px-3 py-2 w-24 text-right"></th>
                        </tr>
                    </thead>
                    <tbody>
                        {isLoading ? (
                            <tr><td colSpan={7} className="px-3 py-6 text-center text-gray-400">Đang tải...</td></tr>
                        ) : policies.length === 0 ? (
                            <tr><td colSpan={7} className="px-3 py-6 text-center text-gray-400">Chưa có chính sách SLA nào</td></tr>
                        ) : (
                            policies.map((p) => (
                                <tr key={p.id} className="border-b border-gray-200 hover:bg-gray-50">
                                    <td className="px-3 py-2 text-gray-500">{p.code}</td>
                                    <td className="px-3 py-2 font-medium text-text-main">{p.name}</td>
                                    <td className="px-3 py-2 whitespace-nowrap">
                                        <span className={`px-2 py-0.5 rounded text-sm ${PRIORITY_COLORS[p.priority] ?? 'bg-gray-100 text-gray-600'}`}>
                                            {PRIORITY_LABELS[p.priority] ?? p.priority}
                                        </span>
                                    </td>
                                    <td className="px-3 py-2">{p.firstResponseHours}</td>
                                    <td className="px-3 py-2">{p.resolutionHours}</td>
                                    <td className="px-3 py-2 whitespace-nowrap">
                                        {p.isActive
                                            ? <span className="px-2 py-0.5 rounded text-sm bg-green-100 text-green-700">Đang hoạt động</span>
                                            : <span className="px-2 py-0.5 rounded text-sm bg-gray-100 text-gray-600">Ngừng hoạt động</span>}
                                    </td>
                                    <td className="px-3 py-2">
                                        <div className="flex gap-1 justify-end">
                                            <button onClick={() => openEdit(p)} className="p-1.5 rounded hover:bg-gray-100 text-gray-400 hover:text-primary" title="Sửa">
                                                <FiEdit2 size={14} />
                                            </button>
                                            <button onClick={() => setDeleteId(p.id)} className="p-1.5 rounded hover:bg-red-50 text-gray-400 hover:text-danger" title="Xóa">
                                                <FiTrash2 size={14} />
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>

            {modalOpen && (
                <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40" onClick={() => setModalOpen(false)}>
                    <div className="bg-white rounded-card shadow-lg w-full max-w-md mx-4" onClick={(e) => e.stopPropagation()}>
                        <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
                            <h2 className="text-lg font-semibold text-text-main">{editId == null ? 'Thêm chính sách SLA' : 'Sửa chính sách SLA'}</h2>
                            <button onClick={() => setModalOpen(false)} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                        </div>
                        <form ref={formRef} onSubmit={submit} noValidate className="px-5 py-4 space-y-3">
                            <FormField label="Mã chính sách" required error={errors.code}>
                                <input className={inp} value={form.code} disabled={editId != null}
                                    onChange={(e) => { setForm(f => ({ ...f, code: e.target.value })); clearError('code'); }} />
                            </FormField>
                            <FormField label="Tên chính sách" required error={errors.name}>
                                <input className={inp} value={form.name}
                                    onChange={(e) => { setForm(f => ({ ...f, name: e.target.value })); clearError('name'); }} />
                            </FormField>
                            <FormField label="Độ ưu tiên" required>
                                <select className={inp} value={form.priority}
                                    onChange={(e) => setForm(f => ({ ...f, priority: e.target.value }))}>
                                    {PRIORITY_OPTIONS.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
                                </select>
                            </FormField>
                            <div className="grid grid-cols-2 gap-3">
                                <FormField label="Hạn phản hồi đầu (giờ)" required error={errors.firstResponseHours}>
                                    <input type="number" min={1} className={inp} value={form.firstResponseHours}
                                        onChange={(e) => { setForm(f => ({ ...f, firstResponseHours: e.target.value })); clearError('firstResponseHours'); }} />
                                </FormField>
                                <FormField label="Hạn giải quyết (giờ)" required error={errors.resolutionHours}>
                                    <input type="number" min={1} className={inp} value={form.resolutionHours}
                                        onChange={(e) => { setForm(f => ({ ...f, resolutionHours: e.target.value })); clearError('resolutionHours'); }} />
                                </FormField>
                            </div>
                            <label className="flex items-center gap-2 text-md text-text-main pt-1">
                                <input type="checkbox" checked={form.isActive}
                                    onChange={(e) => setForm(f => ({ ...f, isActive: e.target.checked }))} />
                                Đang hoạt động
                            </label>
                            <ModalFooter onCancel={() => setModalOpen(false)} saving={saving} />
                        </form>
                    </div>
                </div>
            )}

            {deleteId !== null && (
                <ConfirmModal
                    message="Bạn có chắc muốn xóa chính sách SLA này?"
                    confirmLabel="Xóa"
                    confirmDanger
                    isLoading={deleteFn.isPending}
                    onConfirm={() => deleteFn.mutate(deleteId, { onSuccess: () => setDeleteId(null) })}
                    onCancel={() => setDeleteId(null)}
                />
            )}
        </div>
    );
};

export default SlaPolicyPage;
