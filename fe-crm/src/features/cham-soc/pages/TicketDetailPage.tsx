import { useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { FiArrowLeft, FiEdit2, FiStar } from 'react-icons/fi';
import { useAlert } from '@/shared/alert/useAlert';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { ReasonModal } from '@/shared/components/ReasonModal';
import { ScrollFrame } from '@/shared/components/table/ScrollFrame';
import { formatISODate } from '@/shared/utils/date';
import { formatNumber } from '@/shared/utils/number';
import { toIdNameMap, lookupName } from '@/shared/utils/lookup';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import { useCustomerList } from '@/features/khach-hang/hooks/useCustomerList';
import { useProductList } from '@/features/san-pham/hooks/useProductList';
import { useTicket } from '../hooks/useTicket';
import { useTicketReturnItems } from '../hooks/useTicketReturnItems';
import { useTicketWorkflow, type TicketAction } from '../hooks/useTicketWorkflow';
import { TicketWorkflowButtons } from '../components/TicketWorkflowButtons';
import { TicketTimeline } from '../components/TicketTimeline';
import { TicketEditModal } from '../components/TicketEditModal';
import { AssignTicketModal } from '../components/AssignTicketModal';
import { ResolutionModal } from '../components/ResolutionModal';
import {
    TYPE_LABELS, TYPE_COLORS, STATUS_LABELS, STATUS_COLORS, PRIORITY_LABELS, PRIORITY_COLORS,
    CHANNEL_LABELS, REASON_LABELS, RESOLUTION_LABELS,
} from '../config/ticketEnums';

/** Ô thông tin nhãn - giá trị. */
const Info = ({ label, value }: { label: string; value: React.ReactNode }) => (
    <div className="flex gap-2 text-md">
        <span className="text-gray-500 w-32 flex-shrink-0">{label}</span>
        <span className="text-text-main">{value ?? '—'}</span>
    </div>
);

const Badge = ({ labels, colors, v }: { labels: Record<string, string>; colors: Record<string, string>; v: string }) => (
    <span className={`inline-block px-2 py-0.5 rounded text-sm font-medium ${colors[v] ?? 'bg-gray-100 text-gray-600'}`}>{labels[v] ?? v}</span>
);

const TicketDetailPage = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const ticketId = id ? Number(id) : undefined;
    const { showAlert } = useAlert();
    const { confirmSave } = useConfirm();

    const { data: ticket, isLoading } = useTicket(ticketId);
    const { data: returnItems = [] } = useTicketReturnItems(ticketId);
    const { mutate: runWorkflow } = useTicketWorkflow();
    const { data: users = [] } = useActiveUsers();
    const { data: customers = [] } = useCustomerList();
    const { data: products = [] } = useProductList();

    const userMap = useMemo(() => toIdNameMap(users, 'id', 'fullName'), [users]);
    const customerMap = useMemo(() => toIdNameMap(customers, 'id', 'name'), [customers]);
    const productMap = useMemo(() => toIdNameMap(products, 'id', 'name'), [products]);
    const userOptions = useMemo(() => users.map((u) => ({ value: String(u.id), label: u.fullName })), [users]);
    const customerOptions = useMemo(() => customers.map((c) => ({ value: String(c.id), label: c.name })), [customers]);
    const productOptions = useMemo(() => products.map((p) => ({ value: String(p.id), label: `${p.sku} — ${p.name}` })), [products]);

    const [editOpen, setEditOpen] = useState(false);
    const [assignOpen, setAssignOpen] = useState(false);
    const [rejectOpen, setRejectOpen] = useState(false);
    const [resolveKind, setResolveKind] = useState<'resolve' | 'complete' | null>(null);
    const [csatScore, setCsatScore] = useState(0);
    const [csatComment, setCsatComment] = useState('');

    if (isLoading) return <div className="p-6 text-gray-400">Đang tải...</div>;
    if (!ticket) return <div className="p-6 text-gray-400">Không tìm thấy phiếu.</div>;

    const onError = (err: unknown) => showAlert((err as { response?: { data?: { message?: string } } })?.response?.data?.message ?? 'Không thực hiện được hành động');
    const isReturn = ticket.type === 'return' || ticket.type === 'exchange';

    /** Điều phối hành động — hành động cần input mở modal, còn lại gọi trực tiếp. */
    const handleAction = (action: TicketAction) => {
        if (action === 'assign') { setAssignOpen(true); return; }
        if (action === 'reject') { setRejectOpen(true); return; }
        if (action === 'resolve') { setResolveKind('resolve'); return; }
        if (action === 'complete') { setResolveKind('complete'); return; }
        runWorkflow({ id: ticket.id, action }, { onError });
    };

    const canCsat = ticket.status === 'resolved' || ticket.status === 'closed';

    return (
        <div className="p-6 bg-bg-main space-y-4">
            <button onClick={() => navigate('/cham-soc')} className="flex items-center gap-1.5 text-md text-gray-500 hover:text-primary">
                <FiArrowLeft size={15} /> Quay lại danh sách
            </button>

            {/* Header */}
            <div className="bg-white rounded-card shadow-sm p-5">
                <div className="flex items-start justify-between">
                    <div>
                        <div className="flex items-center gap-2 mb-1">
                            <h1 className="text-xl font-semibold text-text-main">{ticket.code}</h1>
                            <Badge labels={TYPE_LABELS} colors={TYPE_COLORS} v={ticket.type} />
                            <Badge labels={STATUS_LABELS} colors={STATUS_COLORS} v={ticket.status} />
                            <Badge labels={PRIORITY_LABELS} colors={PRIORITY_COLORS} v={ticket.priority} />
                            {ticket.isOverdue && <span className="inline-block px-2 py-0.5 rounded text-sm font-medium bg-red-100 text-red-600">Quá hạn SLA</span>}
                        </div>
                        <p className="text-md text-text-main">{ticket.subject}</p>
                    </div>
                    <button onClick={() => setEditOpen(true)} className="flex items-center gap-1.5 px-3 py-1.5 rounded-btn border border-gray-300 text-md text-gray-600 hover:bg-gray-50">
                        <FiEdit2 size={14} /> Sửa
                    </button>
                </div>
                <div className="mt-4 pt-4 border-t border-gray-100">
                    <TicketWorkflowButtons ticket={ticket} onAction={handleAction} />
                </div>
            </div>

            <div className="grid grid-cols-3 gap-4">
                {/* Thông tin */}
                <div className="col-span-2 space-y-4">
                    <div className="bg-white rounded-card shadow-sm p-5 space-y-2">
                        <h2 className="text-md font-semibold text-text-main mb-3">Thông tin</h2>
                        <Info label="Khách hàng" value={ticket.customerId ? lookupName(customerMap, ticket.customerId) : '—'} />
                        <Info label="Liên hệ" value={ticket.contactName ?? '—'} />
                        <Info label="Người xử lý" value={ticket.assignedUserId ? lookupName(userMap, ticket.assignedUserId) : '—'} />
                        <Info label="Sản phẩm" value={ticket.productId ? lookupName(productMap, ticket.productId) : '—'} />
                        <Info label="Hóa đơn" value={ticket.invoiceId
                            ? <button type="button" className="text-primary hover:underline" onClick={() => navigate('/hoa-don')}>#{ticket.invoiceId}</button>
                            : '—'} />
                        <Info label="Kênh" value={CHANNEL_LABELS[ticket.channel]} />
                        <Info label="Lý do" value={ticket.reason ? REASON_LABELS[ticket.reason] : '—'} />
                        <Info label="Hình thức GQ" value={ticket.resolutionType ? RESOLUTION_LABELS[ticket.resolutionType] : '—'} />
                        <Info label="Ghi chú GQ" value={ticket.resolutionNote} />
                        <Info label="Mô tả" value={ticket.description} />
                    </div>

                    {isReturn && (
                        <div className="bg-white rounded-card shadow-sm p-5">
                            <h2 className="text-md font-semibold text-text-main mb-3">Hàng trả / đổi</h2>
                            <ScrollFrame visibleRows={6}>
                                <table className="w-full text-md">
                                    <thead>
                                        <tr className="text-left text-gray-500">
                                            {['Sản phẩm', 'Số lượng', 'Đơn giá', 'Thành tiền', 'Lý do', 'Tình trạng'].map((h, i) => (
                                                <th key={h} className={`py-2 font-medium ${i === 0 ? 'pr-2' : 'px-2'}`}>{h}</th>
                                            ))}
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {returnItems.map(it => (
                                            <tr key={it.id} className="border-b border-gray-100">
                                                <td className="py-1.5 pr-2">{it.productId ? lookupName(productMap, it.productId) : '—'}</td>
                                                <td className="py-1.5 px-2">{formatNumber(it.quantity)}</td>
                                                <td className="py-1.5 px-2">{formatNumber(it.unitPrice)} đ</td>
                                                <td className="py-1.5 px-2">{formatNumber(it.amount)} đ</td>
                                                <td className="py-1.5 px-2">{it.reason ? REASON_LABELS[it.reason] : '—'}</td>
                                                <td className="py-1.5 px-2">{it.conditionNote ?? '—'}</td>
                                            </tr>
                                        ))}
                                        {returnItems.length === 0 && <tr><td colSpan={6} className="py-3 text-center text-gray-400">Chưa có dòng hàng nào</td></tr>}
                                    </tbody>
                                </table>
                            </ScrollFrame>
                        </div>
                    )}

                    <div className="bg-white rounded-card shadow-sm p-5">
                        <h2 className="text-md font-semibold text-text-main mb-3">Lịch sử / trao đổi</h2>
                        <TicketTimeline ticketId={ticket.id} />
                    </div>
                </div>

                {/* SLA + CSAT */}
                <div className="space-y-4">
                    <div className="bg-white rounded-card shadow-sm p-5 space-y-2">
                        <h2 className="text-md font-semibold text-text-main mb-3">SLA</h2>
                        <Info label="Phản hồi đầu" value={ticket.firstResponseAt ? formatISODate(ticket.firstResponseAt) : '—'} />
                        <Info label="Hạn giải quyết" value={ticket.slaDueAt ? formatISODate(ticket.slaDueAt) : '—'} />
                        <Info label="Đã giải quyết" value={ticket.resolvedAt ? formatISODate(ticket.resolvedAt) : '—'} />
                        <Info label="Đã đóng" value={ticket.closedAt ? formatISODate(ticket.closedAt) : '—'} />
                    </div>

                    <div className="bg-white rounded-card shadow-sm p-5 space-y-3">
                        <h2 className="text-md font-semibold text-text-main">Đánh giá hài lòng (CSAT)</h2>
                        {ticket.satisfactionScore != null ? (
                            <div className="space-y-1">
                                <div className="flex items-center gap-1 text-warning">
                                    {[1, 2, 3, 4, 5].map(n => <FiStar key={n} size={16} className={n <= (ticket.satisfactionScore ?? 0) ? 'fill-current' : ''} />)}
                                    <span className="text-text-main ml-1">{ticket.satisfactionScore}/5</span>
                                </div>
                                {ticket.satisfactionComment && <p className="text-md text-gray-600">{ticket.satisfactionComment}</p>}
                            </div>
                        ) : canCsat ? (
                            <div className="space-y-2">
                                <div className="flex items-center gap-1">
                                    {[1, 2, 3, 4, 5].map(n => (
                                        <button key={n} type="button" onClick={() => setCsatScore(n)} className="text-warning">
                                            <FiStar size={20} className={n <= csatScore ? 'fill-current' : ''} />
                                        </button>
                                    ))}
                                </div>
                                <textarea rows={2} value={csatComment} onChange={(e) => setCsatComment(e.target.value)} placeholder="Nhận xét..."
                                    className="w-full border border-gray-300 rounded-btn px-3 py-1.5 text-md focus:outline-none focus:border-primary resize-none" />
                                <button type="button" disabled={csatScore === 0}
                                    onClick={async () => {
                                        if (!(await confirmSave('đánh giá'))) return;
                                        runWorkflow({ id: ticket.id, action: 'csat', score: csatScore, comment: csatComment.trim() },
                                            { onSuccess: () => showAlert('Đã ghi nhận đánh giá'), onError });
                                    }}
                                    className="px-3 py-1.5 rounded-btn bg-primary text-white text-md hover:opacity-90 disabled:opacity-50">Gửi đánh giá</button>
                            </div>
                        ) : (
                            <p className="text-md text-gray-400">Đánh giá được sau khi phiếu đã giải quyết / đóng.</p>
                        )}
                    </div>
                </div>
            </div>

            {editOpen && (
                <TicketEditModal ticket={ticket} customerOptions={customerOptions}
                    userOptions={userOptions} productOptions={productOptions} onClose={() => setEditOpen(false)} />
            )}
            {assignOpen && (
                <AssignTicketModal userOptions={userOptions}
                    onCancel={() => setAssignOpen(false)}
                    onConfirm={(toUserId) => { runWorkflow({ id: ticket.id, action: 'assign', toUserId }, { onError }); setAssignOpen(false); }} />
            )}
            {rejectOpen && (
                <ReasonModal title="Từ chối trả/đổi" label="Lý do từ chối" confirmLabel="Từ chối" confirmDanger
                    onCancel={() => setRejectOpen(false)}
                    onConfirm={(reason) => { runWorkflow({ id: ticket.id, action: 'reject', reason }, { onError }); setRejectOpen(false); }} />
            )}
            {resolveKind && (
                <ResolutionModal title={resolveKind === 'resolve' ? 'Giải quyết phiếu' : 'Hoàn tất xử lý'}
                    onCancel={() => setResolveKind(null)}
                    onConfirm={(resolutionType, note) => { runWorkflow({ id: ticket.id, action: resolveKind, resolutionType, note }, { onError }); setResolveKind(null); }} />
            )}
        </div>
    );
};

export default TicketDetailPage;
