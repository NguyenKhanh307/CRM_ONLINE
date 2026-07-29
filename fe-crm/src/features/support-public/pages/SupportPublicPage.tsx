import { useEffect, useState, type ReactNode } from 'react';
import { useParams } from 'react-router-dom';
import { FiStar } from 'react-icons/fi';
import { formatISODate } from '@/shared/utils/date';
import { TYPE_LABELS, STATUS_LABELS, STATUS_COLORS, PRIORITY_LABELS } from '@/features/cham-soc/config/ticketEnums';
import { publicTicketService, type PublicTicketView } from '../services/publicTicketService';

const CSAT_STATUSES = new Set(['resolved', 'closed']);

/** Trang công khai để khách xem trạng thái phiếu chăm sóc + tự đánh giá hài lòng theo mã phiếu. */
const SupportPublicPage = () => {
    const { code = '' } = useParams();

    const [view, setView] = useState<PublicTicketView | null>(null);
    const [loading, setLoading] = useState(true);
    const [errorMsg, setErrorMsg] = useState<string | null>(null);
    const [score, setScore] = useState(0);
    const [comment, setComment] = useState('');
    const [submitting, setSubmitting] = useState(false);

    const load = () => {
        setLoading(true);
        publicTicketService.getByCode(code)
            .then((r) => setView(r.data.data))
            .catch((err) => setErrorMsg(
                (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                ?? 'Không tìm thấy phiếu hoặc mã không đúng'))
            .finally(() => setLoading(false));
    };

    useEffect(load, [code]);

    const handleSubmit = async () => {
        if (score === 0) return;
        setSubmitting(true);
        try {
            const r = await publicTicketService.submitCsat(code, score, comment.trim() || undefined);
            setView(r.data.data);
        } catch (err) {
            setErrorMsg((err as { response?: { data?: { message?: string } } })?.response?.data?.message
                ?? 'Không gửi được đánh giá, vui lòng thử lại');
        } finally {
            setSubmitting(false);
        }
    };

    if (loading) return <Centered>Đang tải...</Centered>;
    if (errorMsg && !view) return <Centered><span className="text-danger">{errorMsg}</span></Centered>;
    if (!view) return null;

    const canCsat = CSAT_STATUSES.has(view.status);

    return (
        <div className="min-h-screen bg-bg-main py-8 px-4">
            <div className="max-w-xl mx-auto bg-white rounded-card shadow-sm p-6">
                <h1 className="text-xl font-semibold text-text-main mb-1">Phiếu {view.code}</h1>
                <p className="text-md text-gray-600 mb-4">{TYPE_LABELS[view.type] ?? view.type}</p>

                <div className="space-y-2 text-md mb-5">
                    <Row label="Trạng thái">
                        <span className={`inline-block px-2 py-0.5 rounded text-sm font-medium ${STATUS_COLORS[view.status] ?? 'bg-gray-100 text-gray-600'}`}>
                            {STATUS_LABELS[view.status] ?? view.status}
                        </span>
                    </Row>
                    <Row label="Độ ưu tiên">{PRIORITY_LABELS[view.priority] ?? view.priority}</Row>
                    <Row label="Tiêu đề">{view.subject}</Row>
                    {view.description && <Row label="Mô tả">{view.description}</Row>}
                    <Row label="Ngày tạo">{formatISODate(view.createdAt)}</Row>
                    {view.slaDueAt && <Row label="Hạn giải quyết">{formatISODate(view.slaDueAt)}</Row>}
                    {view.resolvedAt && <Row label="Đã giải quyết">{formatISODate(view.resolvedAt)}</Row>}
                    {view.closedAt && <Row label="Đã đóng">{formatISODate(view.closedAt)}</Row>}
                </div>

                <div className="border-t border-gray-100 pt-4">
                    <h2 className="text-md font-semibold text-text-main mb-2">Đánh giá hài lòng</h2>
                    {view.satisfactionScore != null ? (
                        <div className="space-y-1">
                            <div className="flex items-center gap-1 text-warning">
                                {[1, 2, 3, 4, 5].map((n) => (
                                    <FiStar key={n} size={18} className={n <= (view.satisfactionScore ?? 0) ? 'fill-current' : ''} />
                                ))}
                                <span className="text-text-main ml-1">{view.satisfactionScore}/5</span>
                            </div>
                            {view.satisfactionComment && <p className="text-md text-gray-600">{view.satisfactionComment}</p>}
                            <p className="text-sm text-gray-400">Cảm ơn Quý khách đã đánh giá!</p>
                        </div>
                    ) : canCsat ? (
                        <div className="space-y-2">
                            <div className="flex items-center gap-1">
                                {[1, 2, 3, 4, 5].map((n) => (
                                    <button key={n} type="button" onClick={() => setScore(n)} className="text-warning">
                                        <FiStar size={22} className={n <= score ? 'fill-current' : ''} />
                                    </button>
                                ))}
                            </div>
                            <textarea
                                rows={3}
                                value={comment}
                                onChange={(e) => setComment(e.target.value)}
                                placeholder="Nhận xét (tùy chọn)..."
                                className="w-full border border-gray-300 rounded-btn px-3 py-2 text-md focus:outline-none focus:border-primary resize-none"
                            />
                            {errorMsg && <div className="text-danger text-sm">{errorMsg}</div>}
                            <button
                                type="button"
                                onClick={handleSubmit}
                                disabled={submitting || score === 0}
                                className="px-5 py-2 rounded-btn bg-primary text-white text-md font-medium hover:opacity-90 disabled:opacity-50"
                            >
                                {submitting ? 'Đang gửi...' : 'Gửi đánh giá'}
                            </button>
                        </div>
                    ) : (
                        <p className="text-md text-gray-400">Đánh giá được sau khi phiếu đã giải quyết / đóng.</p>
                    )}
                </div>
            </div>
        </div>
    );
};

/** Dòng nhãn - giá trị. */
const Row = ({ label, children }: { label: string; children: ReactNode }) => (
    <div className="flex gap-2">
        <span className="text-gray-500 w-32 flex-shrink-0">{label}</span>
        <span className="text-text-main">{children}</span>
    </div>
);

/** Khung căn giữa cho trạng thái tải/lỗi. */
const Centered = ({ children }: { children: ReactNode }) => (
    <div className="min-h-screen bg-bg-main flex items-center justify-center text-gray-500 text-md">{children}</div>
);

export default SupportPublicPage;
