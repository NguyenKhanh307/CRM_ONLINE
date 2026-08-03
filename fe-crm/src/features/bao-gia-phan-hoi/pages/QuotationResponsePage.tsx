import { useEffect, useMemo, useState, type ReactNode } from 'react';
import { useParams, useSearchParams } from 'react-router-dom';
import { FiCheck, FiEdit3, FiX } from 'react-icons/fi';
import { formatCurrency } from '@/shared/utils/number';
import { formatISODate } from '@/shared/utils/date';
import {
    publicQuotationService,
    type PublicQuotationView,
    type RespondAction,
} from '../services/publicQuotationService';

// nhãn hành động phản hồi
const ACTION_META: Record<RespondAction, { label: string; color: string; icon: ReactNode; noteLabel: string }> = {
    accept: { label: 'Đồng ý', color: 'bg-success', icon: <FiCheck size={16} />, noteLabel: 'Lời nhắn (tùy chọn)' },
    adjust: { label: 'Điều chỉnh', color: 'bg-warning', icon: <FiEdit3 size={16} />, noteLabel: 'Nội dung cần điều chỉnh' },
    reject: { label: 'Không đồng ý', color: 'bg-danger', icon: <FiX size={16} />, noteLabel: 'Lý do không đồng ý' },
};

// nhãn trạng thái phản hồi đã lưu
const RESPONSE_LABELS: Record<string, string> = {
    accepted: 'Bạn đã ĐỒNG Ý báo giá này',
    adjust: 'Bạn đã gửi yêu cầu ĐIỀU CHỈNH',
    rejected: 'Bạn đã chọn KHÔNG ĐỒNG Ý báo giá này',
};

// map action trên link email (?action=agree) sang action gửi api
const QUERY_TO_ACTION: Record<string, RespondAction> = {
    agree: 'accept', accept: 'accept', adjust: 'adjust', reject: 'reject',
};

// trang công khai để khách hàng xem + phản hồi báo giá theo token (không cần đăng nhập)
const QuotationResponsePage = () => {
    const { token = '' } = useParams();
    const [searchParams] = useSearchParams();

    const [view, setView] = useState<PublicQuotationView | null>(null);
    const [loading, setLoading] = useState(true);
    const [errorMsg, setErrorMsg] = useState<string | null>(null);
    const [action, setAction] = useState<RespondAction>(() => QUERY_TO_ACTION[searchParams.get('action') ?? ''] ?? 'accept');
    const [note, setNote] = useState('');
    const [submitting, setSubmitting] = useState(false);
    const [submitted, setSubmitted] = useState(false);

    useEffect(() => {
        publicQuotationService.getByToken(token)
            .then((r) => setView(r.data.data))
            .catch((err) => setErrorMsg(
                (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                ?? 'Không tìm thấy báo giá hoặc liên kết đã hết hạn'))
            .finally(() => setLoading(false));
    }, [token]);

    const alreadyResponded = !!view?.customerResponse;

    const handleSubmit = async () => {
        setSubmitting(true);
        try {
            await publicQuotationService.respond(token, action, note.trim() || undefined);
            setSubmitted(true);
        } catch (err) {
            setErrorMsg((err as { response?: { data?: { message?: string } } })?.response?.data?.message
                ?? 'Không gửi được phản hồi, vui lòng thử lại');
        } finally {
            setSubmitting(false);
        }
    };

    const totalText = useMemo(() => view ? `${formatCurrency(view.total)} ${view.currency ?? ''}`.trim() : '', [view]);

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
                            {view.items.map((it, i) => (
                                <tr key={i} className="border-t border-gray-100">
                                    <td className="px-3 py-2">{it.productName}</td>
                                    <td className="px-3 py-2 text-center">{it.unit ?? ''}</td>
                                    <td className="px-3 py-2 text-right">{formatCurrency(it.quantity)}</td>
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

                <div className="text-right text-lg font-semibold text-text-main mb-4">Tổng cộng: {totalText}</div>
                {view.note && <div className="text-md text-gray-600 mb-4">Ghi chú: {view.note}</div>}

                {submitted ? (
                    <Banner className="bg-green-50 text-success">
                        Cảm ơn Quý khách! Phản hồi của Quý khách đã được gửi tới nhân viên phụ trách.
                    </Banner>
                ) : alreadyResponded ? (
                    <Banner className="bg-blue-50 text-primary">
                        {RESPONSE_LABELS[view.customerResponse ?? ''] ?? 'Quý khách đã phản hồi báo giá này.'}
                        {view.customerResponseNote && <div className="mt-1 text-gray-600">Nội dung: {view.customerResponseNote}</div>}
                    </Banner>
                ) : (
                    <div className="border-t border-gray-100 pt-4">
                        <div className="text-md font-medium text-gray-700 mb-2">Phản hồi báo giá</div>
                        <div className="flex gap-2 mb-3">
                            {(Object.keys(ACTION_META) as RespondAction[]).map((a) => (
                                <button
                                    key={a}
                                    type="button"
                                    onClick={() => setAction(a)}
                                    className={`flex items-center gap-1.5 px-4 py-2 rounded-btn text-md font-medium border ${
                                        action === a ? `${ACTION_META[a].color} text-white border-transparent`
                                            : 'bg-white text-gray-600 border-gray-300 hover:bg-gray-50'
                                    }`}
                                >
                                    {ACTION_META[a].icon}{ACTION_META[a].label}
                                </button>
                            ))}
                        </div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">{ACTION_META[action].noteLabel}</label>
                        <textarea
                            className="w-full border border-gray-300 rounded-btn px-3 py-2 text-md focus:outline-none focus:border-primary"
                            rows={3}
                            value={note}
                            onChange={(e) => setNote(e.target.value)}
                            placeholder={action === 'adjust' ? 'Mô tả nội dung Quý khách muốn điều chỉnh...' : 'Nhập lời nhắn (nếu có)...'}
                        />
                        {errorMsg && <div className="text-danger text-sm mt-2">{errorMsg}</div>}
                        <button
                            type="button"
                            onClick={handleSubmit}
                            disabled={submitting}
                            className="mt-3 px-5 py-2 rounded-btn bg-primary text-white text-md font-medium hover:opacity-90 disabled:opacity-50"
                        >
                            {submitting ? 'Đang gửi...' : 'Gửi phản hồi'}
                        </button>
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
