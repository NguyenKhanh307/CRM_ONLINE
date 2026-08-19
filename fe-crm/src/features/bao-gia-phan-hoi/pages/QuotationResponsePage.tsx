import { useEffect, useMemo, useState, type ReactNode } from 'react';
import { useParams } from 'react-router-dom';
import { FiCheck, FiEdit3, FiTrash2, FiX } from 'react-icons/fi';
import { formatCurrency, formatNumber } from '@/shared/utils/number';
import { formatISODate } from '@/shared/utils/date';
import { useConfirm } from '@/shared/confirm/useConfirm';
import {
    publicQuotationService,
    type PublicQuotationLine,
    type PublicQuotationView,
} from '../services/publicQuotationService';

// nhãn trạng thái phản hồi đã lưu — dùng lại nguyên bản (nhánh "adjust" giờ là "đề xuất chờ xử lý")
const RESPONSE_LABELS: Record<string, string> = {
    accepted: 'Bạn đã ĐỒNG Ý báo giá này',
    adjust: 'Bạn đã gửi yêu cầu CHỈNH SỬA — nhân viên phụ trách sẽ xem xét và phản hồi',
    rejected: 'Bạn đã chọn KHÔNG ĐỒNG Ý báo giá này',
};

// chế độ hiển thị của khối hành động
type ActionMode = 'idle' | 'reject' | 'edit';

interface EditRow extends PublicQuotationLine {
    // số lượng gốc (không đổi) — dùng để nội suy tỉ lệ "Thành tiền" khi đổi SL (ước tính, số chính
    // thức do BE tính lại khi nhân viên tạo báo giá mới)
    originalQuantity: number;
}

// trang công khai để khách hàng xem + phản hồi báo giá theo mã báo giá (không cần đăng nhập)
const QuotationResponsePage = () => {
    const { code = '' } = useParams();
    const { confirm } = useConfirm();

    const [view, setView] = useState<PublicQuotationView | null>(null);
    const [loading, setLoading] = useState(true);
    const [errorMsg, setErrorMsg] = useState<string | null>(null);
    const [submitting, setSubmitting] = useState(false);
    const [submitted, setSubmitted] = useState(false);
    const [submittedAction, setSubmittedAction] = useState<'accepted' | 'rejected' | 'adjust' | null>(null);

    const [mode, setMode] = useState<ActionMode>('idle');
    const [rejectNote, setRejectNote] = useState('');
    const [editRows, setEditRows] = useState<EditRow[]>([]);
    const [editNote, setEditNote] = useState('');

    useEffect(() => {
        publicQuotationService.getByCode(code)
            .then((r) => setView(r.data.data))
            .catch((err) => setErrorMsg(
                (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                ?? 'Không tìm thấy báo giá hoặc liên kết đã hết hạn'))
            .finally(() => setLoading(false));
    }, [code]);

    const alreadyResponded = !!view?.customerResponse;

    // bấm "Chỉnh sửa" — nạp dòng hàng hiện tại vào state chỉnh sửa cục bộ
    const startEdit = () => {
        if (!view) return;
        setEditRows(view.items.map((it) => ({ ...it, originalQuantity: it.quantity })));
        setEditNote('');
        setMode('edit');
    };

    const cancelMode = () => {
        setMode('idle');
        setRejectNote('');
    };

    const changeQuantity = (id: number, quantity: number) => {
        setEditRows((rows) => rows.map((r) => (r.id === id ? { ...r, quantity } : r)));
    };

    const removeRow = (id: number) => {
        setEditRows((rows) => rows.filter((r) => r.id !== id));
    };

    // ước tính lại "Thành tiền" theo tỉ lệ số lượng mới/gốc — số chính thức do BE tính lại khi
    // nhân viên bấm "Tạo báo giá mới theo yêu cầu khách"
    const estimatedAmount = (r: EditRow) =>
        r.originalQuantity > 0 ? r.amount * (r.quantity / r.originalQuantity) : r.amount;

    const editTotal = useMemo(() => editRows.reduce((s, r) => s + estimatedAmount(r), 0), [editRows]);

    // Đồng ý — gửi ngay sau khi xác nhận
    const handleAccept = async () => {
        const ok = await confirm({ message: 'Xác nhận Đồng ý báo giá này?', confirmLabel: 'Đồng ý' });
        if (!ok) return;
        setSubmitting(true);
        try {
            await publicQuotationService.respond(code, 'accept');
            setSubmittedAction('accepted');
            setSubmitted(true);
        } catch (err) {
            setErrorMsg((err as { response?: { data?: { message?: string } } })?.response?.data?.message
                ?? 'Không gửi được phản hồi, vui lòng thử lại');
        } finally {
            setSubmitting(false);
        }
    };

    // Từ chối — nhập lý do tùy chọn rồi xác nhận
    const handleReject = async () => {
        const ok = await confirm({
            message: 'Xác nhận Không đồng ý báo giá này?',
            confirmLabel: 'Không đồng ý',
            confirmDanger: true,
        });
        if (!ok) return;
        setSubmitting(true);
        try {
            await publicQuotationService.respond(code, 'reject', rejectNote.trim() || undefined);
            setSubmittedAction('rejected');
            setSubmitted(true);
        } catch (err) {
            setErrorMsg((err as { response?: { data?: { message?: string } } })?.response?.data?.message
                ?? 'Không gửi được phản hồi, vui lòng thử lại');
        } finally {
            setSubmitting(false);
        }
    };

    // Chỉnh sửa — gửi đề xuất (không áp thẳng vào báo giá thật) rồi xác nhận trước khi gửi
    const handleSubmitEdit = async () => {
        if (editRows.length === 0) {
            setErrorMsg('Vui lòng giữ lại ít nhất 1 dòng hàng');
            return;
        }
        const ok = await confirm({
            message: 'Xác nhận lưu và gửi đề xuất chỉnh sửa này tới nhân viên phụ trách?',
            confirmLabel: 'Gửi',
        });
        if (!ok) return;
        setSubmitting(true);
        try {
            await publicQuotationService.proposeAdjustment(
                code,
                editRows.map((r) => ({ id: r.id, quantity: r.quantity })),
                editNote.trim() || undefined,
            );
            setSubmittedAction('adjust');
            setSubmitted(true);
        } catch (err) {
            setErrorMsg((err as { response?: { data?: { message?: string } } })?.response?.data?.message
                ?? 'Không gửi được đề xuất, vui lòng thử lại');
        } finally {
            setSubmitting(false);
        }
    };

    const totalText = useMemo(() => view ? formatCurrency(view.total) : '', [view]);

    if (loading) return <Centered>Đang tải báo giá...</Centered>;
    if (errorMsg && !view) return <Centered><span className="text-danger">{errorMsg}</span></Centered>;
    if (!view) return null;

    return (
        <div className="min-h-screen bg-bg-main py-8 px-4">
            <div className="max-w-3xl mx-auto bg-white rounded-card shadow-sm p-6">
                <h1 className="text-xl font-semibold text-text-main mb-1">Báo giá {view.code}</h1>
                <div className="text-md text-gray-600 mb-4">
                    Kính gửi <b>{view.contactName || view.customerName || 'Quý khách'}</b>
                    {view.customerName && <> — {view.customerName}</>}
                </div>

                <div className="grid grid-cols-2 gap-2 text-md text-gray-700 mb-4">
                    <div>Ngày báo giá: <b>{view.quoteDate ? formatISODate(view.quoteDate) : '—'}</b></div>
                    <div>Hiệu lực đến: <b>{view.validUntil ? formatISODate(view.validUntil) : '—'}</b></div>
                </div>

                {mode !== 'edit' ? (
                    <div className="overflow-x-auto border border-gray-200 rounded-btn mb-4">
                        <table className="w-full text-md">
                            <thead className="bg-gray-50 text-gray-600">
                                <tr>
                                    <th className="text-left px-3 py-2">Sản phẩm</th>
                                    <th className="text-center px-3 py-2">ĐVT</th>
                                    <th className="text-right px-3 py-2">SL</th>
                                    <th className="text-right px-3 py-2">Đơn giá</th>
                                    <th className="text-right px-3 py-2">Chiết khấu</th>
                                    <th className="text-right px-3 py-2">Thành tiền</th>
                                </tr>
                            </thead>
                            <tbody>
                                {view.items.map((it) => (
                                    <tr key={it.id} className="border-t border-gray-100">
                                        <td className="px-3 py-2">{it.productName}</td>
                                        <td className="px-3 py-2 text-center">{it.unit ?? ''}</td>
                                        <td className="px-3 py-2 text-right">{formatNumber(it.quantity)}</td>
                                        <td className="px-3 py-2 text-right">{formatCurrency(it.unitPrice)}</td>
                                        <td className="px-3 py-2 text-right">{formatCurrency(it.discount)}</td>
                                        <td className="px-3 py-2 text-right">{formatCurrency(it.amount)}</td>
                                    </tr>
                                ))}
                                {view.items.length === 0 && (
                                    <tr><td colSpan={6} className="px-3 py-4 text-center text-gray-400">Không có dòng hàng</td></tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                ) : (
                    <div className="overflow-x-auto border border-gray-200 rounded-btn mb-4">
                        <table className="w-full text-md">
                            <thead className="bg-gray-50 text-gray-600">
                                <tr>
                                    <th className="text-left px-3 py-2">Sản phẩm</th>
                                    <th className="text-center px-3 py-2">ĐVT</th>
                                    <th className="text-right px-3 py-2" style={{ width: 110 }}>SL</th>
                                    <th className="text-right px-3 py-2">Đơn giá</th>
                                    <th className="text-right px-3 py-2">Thành tiền (ước tính)</th>
                                    <th className="text-center px-3 py-2" style={{ width: 50 }} />
                                </tr>
                            </thead>
                            <tbody>
                                {editRows.map((it) => (
                                    <tr key={it.id} className="border-t border-gray-100">
                                        <td className="px-3 py-2">{it.productName}</td>
                                        <td className="px-3 py-2 text-center">{it.unit ?? ''}</td>
                                        <td className="px-3 py-2 text-right">
                                            <input
                                                type="number" min={1}
                                                className="w-24 border border-gray-300 rounded-btn px-2 py-1 text-right focus:outline-none focus:border-primary"
                                                value={it.quantity}
                                                onChange={(e) => changeQuantity(it.id, e.target.value ? +e.target.value : 0)}
                                            />
                                        </td>
                                        <td className="px-3 py-2 text-right">{formatCurrency(it.unitPrice)}</td>
                                        <td className="px-3 py-2 text-right">{formatCurrency(estimatedAmount(it))}</td>
                                        <td className="px-3 py-2 text-center">
                                            <button type="button" onClick={() => removeRow(it.id)}
                                                className="p-1.5 rounded hover:bg-red-50 text-gray-400 hover:text-danger" title="Xóa dòng">
                                                <FiTrash2 size={14} />
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                                {editRows.length === 0 && (
                                    <tr><td colSpan={6} className="px-3 py-4 text-center text-gray-400">Không còn dòng hàng nào</td></tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                )}

                <div className="text-right text-lg font-semibold text-text-main mb-4">
                    Tổng cộng: {mode === 'edit' ? formatCurrency(editTotal) + ' (ước tính)' : totalText}
                </div>
                {view.note && mode !== 'edit' && <div className="text-md text-gray-600 mb-4">Ghi chú: {view.note}</div>}

                {submitted ? (
                    <Banner className="bg-green-50 text-success">
                        {submittedAction === 'adjust'
                            ? 'Cảm ơn Quý khách! Đề xuất chỉnh sửa đã được gửi tới nhân viên phụ trách xem xét.'
                            : 'Cảm ơn Quý khách! Phản hồi của Quý khách đã được gửi tới nhân viên phụ trách.'}
                    </Banner>
                ) : alreadyResponded ? (
                    <Banner className="bg-blue-50 text-primary">
                        {RESPONSE_LABELS[view.customerResponse ?? ''] ?? 'Quý khách đã phản hồi báo giá này.'}
                        {view.customerResponseNote && view.customerResponse !== 'adjust' && (
                            <div className="mt-1 text-gray-600">Nội dung: {view.customerResponseNote}</div>
                        )}
                    </Banner>
                ) : mode === 'edit' ? (
                    <div className="border-t border-gray-100 pt-4">
                        <label className="block text-sm font-medium text-gray-700 mb-1">Ghi chú (tùy chọn)</label>
                        <textarea
                            className="w-full border border-gray-300 rounded-btn px-3 py-2 text-md focus:outline-none focus:border-primary"
                            rows={2} value={editNote} onChange={(e) => setEditNote(e.target.value)}
                            placeholder="Mô tả thêm về đề xuất chỉnh sửa (nếu có)..."
                        />
                        {errorMsg && <div className="text-danger text-sm mt-2">{errorMsg}</div>}
                        <div className="flex gap-2 mt-3">
                            <button type="button" onClick={handleSubmitEdit} disabled={submitting}
                                className="px-5 py-2 rounded-btn bg-primary text-white text-md font-medium hover:opacity-90 disabled:opacity-50">
                                {submitting ? 'Đang gửi...' : 'Gửi'}
                            </button>
                            <button type="button" onClick={cancelMode} disabled={submitting}
                                className="px-5 py-2 rounded-btn border border-gray-300 text-gray-600 text-md font-medium hover:bg-gray-50 disabled:opacity-50">
                                Hủy
                            </button>
                        </div>
                    </div>
                ) : mode === 'reject' ? (
                    <div className="border-t border-gray-100 pt-4">
                        <label className="block text-sm font-medium text-gray-700 mb-1">Lý do không đồng ý (tùy chọn)</label>
                        <textarea
                            className="w-full border border-gray-300 rounded-btn px-3 py-2 text-md focus:outline-none focus:border-primary"
                            rows={3} value={rejectNote} onChange={(e) => setRejectNote(e.target.value)}
                            placeholder="Nhập lý do (nếu có)..."
                        />
                        {errorMsg && <div className="text-danger text-sm mt-2">{errorMsg}</div>}
                        <div className="flex gap-2 mt-3">
                            <button type="button" onClick={handleReject} disabled={submitting}
                                className="px-5 py-2 rounded-btn bg-danger text-white text-md font-medium hover:opacity-90 disabled:opacity-50">
                                {submitting ? 'Đang gửi...' : 'Xác nhận không đồng ý'}
                            </button>
                            <button type="button" onClick={cancelMode} disabled={submitting}
                                className="px-5 py-2 rounded-btn border border-gray-300 text-gray-600 text-md font-medium hover:bg-gray-50 disabled:opacity-50">
                                Hủy
                            </button>
                        </div>
                    </div>
                ) : (
                    <div className="border-t border-gray-100 pt-4">
                        <div className="text-md font-medium text-gray-700 mb-2">Phản hồi báo giá</div>
                        {errorMsg && <div className="text-danger text-sm mb-2">{errorMsg}</div>}
                        <div className="flex gap-2">
                            <button type="button" onClick={handleAccept} disabled={submitting}
                                className="flex items-center gap-1.5 px-4 py-2 rounded-btn text-md font-medium border bg-success text-white border-transparent hover:opacity-90 disabled:opacity-50">
                                <FiCheck size={16} />Đồng ý
                            </button>
                            <button type="button" onClick={startEdit} disabled={submitting}
                                className="flex items-center gap-1.5 px-4 py-2 rounded-btn text-md font-medium border bg-warning text-white border-transparent hover:opacity-90 disabled:opacity-50">
                                <FiEdit3 size={16} />Chỉnh sửa
                            </button>
                            <button type="button" onClick={() => setMode('reject')} disabled={submitting}
                                className="flex items-center gap-1.5 px-4 py-2 rounded-btn text-md font-medium border bg-danger text-white border-transparent hover:opacity-90 disabled:opacity-50">
                                <FiX size={16} />Không đồng ý
                            </button>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

// khung căn giữa cho trạng thái tải/lỗi
const Centered = ({ children }: { children: ReactNode }) => (
    <div className="min-h-screen bg-bg-main flex items-center justify-center text-gray-500 text-md">{children}</div>
);

// banner thông báo trạng thái
const Banner = ({ children, className }: { children: ReactNode; className: string }) => (
    <div className={`rounded-btn px-4 py-3 text-md ${className}`}>{children}</div>
);

export default QuotationResponsePage;
