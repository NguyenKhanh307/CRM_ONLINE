import { useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiPlus, FiEdit2, FiTrash2, FiArrowLeft, FiX } from 'react-icons/fi';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { useAlert } from '@/shared/alert/useAlert';
import { useOpportunityStages } from '../hooks/useOpportunityStages';
import { useCreateStage, useUpdateStage, useDeleteStage } from '../hooks/useOpportunityStageMutations';
import type { OpportunityStageResult, OpportunityStagePayload } from '../services/opportunityStageService';

interface FormState {
    name: string;
    sortOrder: string;
    probability: string;
    isWon: boolean;
    isLost: boolean;
}

const EMPTY: FormState = { name: '', sortOrder: '0', probability: '0', isWon: false, isLost: false };

/** Chuyển stage → form state để sửa. */
const toForm = (s: OpportunityStageResult): FormState => ({
    name: s.name,
    sortOrder: String(s.sortOrder ?? 0),
    probability: String(s.probability ?? 0),
    isWon: !!s.isWon,
    isLost: !!s.isLost,
});

/** Trang quản lý giai đoạn pipeline cơ hội (CRUD opportunity_stages). */
const OpportunityPipelinePage = () => {
    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const { data: stages = [], isLoading } = useOpportunityStages();
    const createFn = useCreateStage();
    const updateFn = useUpdateStage();
    const deleteFn = useDeleteStage();

    const [modalOpen, setModalOpen] = useState(false);
    const [editId, setEditId] = useState<number | null>(null);
    const [form, setForm] = useState<FormState>(EMPTY);
    const [deleteId, setDeleteId] = useState<number | null>(null);

    const openCreate = () => { setEditId(null); setForm(EMPTY); setModalOpen(true); };
    const openEdit = (s: OpportunityStageResult) => { setEditId(s.id); setForm(toForm(s)); setModalOpen(true); };

    const submit = (e: FormEvent) => {
        e.preventDefault();
        if (!form.name.trim()) { showAlert('Tên giai đoạn không được để trống'); return; }
        if (form.isWon && form.isLost) { showAlert('Giai đoạn không thể vừa Thắng vừa Thua'); return; }
        const body: OpportunityStagePayload = {
            name: form.name.trim(),
            sortOrder: Number(form.sortOrder) || 0,
            probability: Number(form.probability) || 0,
            isWon: form.isWon,
            isLost: form.isLost,
        };
        const onSuccess = () => setModalOpen(false);
        const onError = () => showAlert('Có lỗi xảy ra khi lưu giai đoạn');
        if (editId == null) createFn.mutate(body, { onSuccess, onError });
        else updateFn.mutate({ id: editId, body }, { onSuccess, onError });
    };

    const saving = createFn.isPending || updateFn.isPending;
    const inp = 'w-full border border-gray-300 rounded-btn px-3 py-1.5 text-md text-text-main focus:outline-none focus:border-primary';
    const lbl = 'block text-sm font-medium text-gray-700 mb-1';

    return (
        <div className="p-6 bg-bg-main min-h-screen">
            <div className="flex items-center justify-between mb-4">
                <div className="flex items-center gap-3">
                    <button onClick={() => navigate('/co-hoi')} className="p-1.5 rounded hover:bg-gray-100 text-gray-500" title="Quay lại">
                        <FiArrowLeft size={18} />
                    </button>
                    <h1 className="text-xl font-semibold text-text-main">Quản lý giai đoạn pipeline</h1>
                </div>
                <button onClick={openCreate} className="flex items-center gap-1.5 px-3 py-1.5 rounded-btn bg-primary text-white text-md hover:opacity-90">
                    <FiPlus size={14} /> Thêm giai đoạn
                </button>
            </div>

            <p className="text-sm text-gray-500 mb-4">
                Trạng thái cơ hội (Đang mở / Thắng / Thua) được suy ra tự động theo giai đoạn được gắn — đánh dấu cờ
                <b> Thắng</b> hoặc <b>Thua</b> cho giai đoạn kết thúc tương ứng.
            </p>

            <div className="bg-white rounded-card p-4 shadow-sm">
                <table className="w-full text-table">
                    <thead>
                        <tr className="text-title font-semibold bg-gray-100 border-b-2 border-gray-300 text-left">
                            <th className="px-3 py-2 w-16">Thứ tự</th>
                            <th className="px-3 py-2">Tên giai đoạn</th>
                            <th className="px-3 py-2 w-28">Xác suất (%)</th>
                            <th className="px-3 py-2 w-24">Loại</th>
                            <th className="px-3 py-2 w-24 text-right"></th>
                        </tr>
                    </thead>
                    <tbody>
                        {isLoading ? (
                            <tr><td colSpan={5} className="px-3 py-6 text-center text-gray-400">Đang tải...</td></tr>
                        ) : stages.length === 0 ? (
                            <tr><td colSpan={5} className="px-3 py-6 text-center text-gray-400">Chưa có giai đoạn nào</td></tr>
                        ) : (
                            stages.map((s) => (
                                <tr key={s.id} className="border-b border-gray-200 hover:bg-gray-50">
                                    <td className="px-3 py-2">{s.sortOrder}</td>
                                    <td className="px-3 py-2 font-medium text-text-main">{s.name}</td>
                                    <td className="px-3 py-2">{s.probability}</td>
                                    <td className="px-3 py-2">
                                        {s.isWon ? <span className="px-2 py-0.5 rounded text-sm bg-green-100 text-green-700">Thắng</span>
                                            : s.isLost ? <span className="px-2 py-0.5 rounded text-sm bg-red-100 text-red-600">Thua</span>
                                            : <span className="px-2 py-0.5 rounded text-sm bg-blue-100 text-blue-700">Đang mở</span>}
                                    </td>
                                    <td className="px-3 py-2">
                                        <div className="flex gap-1 justify-end">
                                            <button onClick={() => openEdit(s)} className="p-1.5 rounded hover:bg-gray-100 text-gray-400 hover:text-primary" title="Sửa">
                                                <FiEdit2 size={14} />
                                            </button>
                                            <button onClick={() => setDeleteId(s.id)} className="p-1.5 rounded hover:bg-red-50 text-gray-400 hover:text-danger" title="Xóa">
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
                            <h2 className="text-lg font-semibold text-text-main">{editId == null ? 'Thêm giai đoạn' : 'Sửa giai đoạn'}</h2>
                            <button onClick={() => setModalOpen(false)} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                        </div>
                        <form onSubmit={submit} className="px-5 py-4 space-y-3">
                            <div>
                                <label className={lbl}>Tên giai đoạn <span className="text-danger">*</span></label>
                                <input className={inp} value={form.name} onChange={(e) => setForm(f => ({ ...f, name: e.target.value }))} />
                            </div>
                            <div className="grid grid-cols-2 gap-3">
                                <div>
                                    <label className={lbl}>Thứ tự</label>
                                    <input type="number" className={inp} value={form.sortOrder} onChange={(e) => setForm(f => ({ ...f, sortOrder: e.target.value }))} />
                                </div>
                                <div>
                                    <label className={lbl}>Xác suất (%)</label>
                                    <input type="number" min="0" max="100" className={inp} value={form.probability} onChange={(e) => setForm(f => ({ ...f, probability: e.target.value }))} />
                                </div>
                            </div>
                            <div className="flex gap-6 pt-1">
                                <label className="flex items-center gap-2 text-md text-text-main">
                                    <input type="checkbox" checked={form.isWon} onChange={(e) => setForm(f => ({ ...f, isWon: e.target.checked, isLost: e.target.checked ? false : f.isLost }))} />
                                    Giai đoạn Thắng
                                </label>
                                <label className="flex items-center gap-2 text-md text-text-main">
                                    <input type="checkbox" checked={form.isLost} onChange={(e) => setForm(f => ({ ...f, isLost: e.target.checked, isWon: e.target.checked ? false : f.isWon }))} />
                                    Giai đoạn Thua
                                </label>
                            </div>
                            <div className="flex justify-end gap-3 pt-2 border-t border-gray-100">
                                <button type="button" onClick={() => setModalOpen(false)} className="px-4 py-1.5 rounded-btn border border-gray-300 text-md text-text-main hover:bg-gray-50">Hủy</button>
                                <button type="submit" disabled={saving} className="px-4 py-1.5 rounded-btn bg-primary text-white text-md hover:opacity-90 disabled:opacity-50">
                                    {saving ? 'Đang lưu...' : 'Lưu'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {deleteId !== null && (
                <ConfirmModal
                    message="Bạn có chắc muốn xóa giai đoạn này? Các cơ hội đang gắn sẽ về trạng thái chưa có giai đoạn."
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

export default OpportunityPipelinePage;
