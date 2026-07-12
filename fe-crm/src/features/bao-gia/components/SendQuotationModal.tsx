import { useEffect, useState, type FormEvent } from 'react';
import { FiX } from 'react-icons/fi';
import { useAlert } from '@/shared/alert/useAlert';
import { RichTextEditor } from '@/shared/components/RichTextEditor';
import { useQuotationEmailDraft } from '../hooks/useQuotationEmailDraft';
import { useQuotationWorkflow } from '../hooks/useQuotationWorkflow';

interface Props {
    /** ID báo giá cần gửi (null = đóng). */
    quotationId: number | null;
    onClose: () => void;
}

/**
 * Modal soạn nội dung email báo giá trước khi gửi cho khách.
 * Nội dung mặc định được nạp từ BE (GET /email-draft) để hiển thị sẵn cho người dùng sửa.
 * 3 nút phản hồi (Đồng ý/Điều chỉnh/Không đồng ý) do BE tự chèn vào cuối khi gửi.
 */
export function SendQuotationModal({ quotationId, onClose }: Props) {
    const open = quotationId !== null;
    const { showAlert } = useAlert();
    const { data: draft, isLoading } = useQuotationEmailDraft(quotationId, open);
    const { mutate: workflowFn, isPending } = useQuotationWorkflow();
    const [to, setTo] = useState('');
    const [cc, setCc] = useState('');
    const [bcc, setBcc] = useState('');
    const [subject, setSubject] = useState('');
    const [body, setBody] = useState('');

    // Nạp nội dung mặc định vào ô soạn khi draft tải xong (người dùng vẫn sửa được người nhận).
    useEffect(() => {
        if (draft) {
            setTo(draft.toEmail ?? '');
            setSubject(draft.subject ?? '');
            setBody(draft.body ?? '');
        }
    }, [draft]);

    if (!open) return null;

    const handleSubmit = (e: FormEvent) => {
        e.preventDefault();
        if (!to.trim()) { showAlert('Vui lòng nhập email người nhận'); return; }
        if (!subject.trim() || !body.trim()) { showAlert('Vui lòng nhập tiêu đề và nội dung email'); return; }
        workflowFn(
            {
                id: quotationId,
                action: 'send',
                emailPayload: {
                    to: to.trim(),
                    cc: cc.trim() || undefined,
                    bcc: bcc.trim() || undefined,
                    subject: subject.trim(),
                    body,
                },
            },
            {
                onSuccess: (res: unknown) => {
                    const email = (res as { data?: { data?: { sentToEmail?: string | null } } })?.data?.data?.sentToEmail;
                    showAlert(email ? `Đã gửi báo giá tới email: ${email}` : 'Đã gửi báo giá cho khách hàng');
                    onClose();
                },
                onError: (err: unknown) => {
                    const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                        ?? 'Gửi báo giá thất bại';
                    showAlert(msg);
                },
            },
        );
    };

    const inp = 'w-full border border-gray-300 rounded-btn px-3 py-1.5 text-md text-text-main focus:outline-none focus:border-primary';
    const lbl = 'block text-sm font-medium text-gray-700 mb-1';

    return (
        <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40" onClick={onClose}>
            <div className="bg-white rounded-card shadow-lg w-full max-w-2xl mx-4 max-h-[90vh] overflow-y-auto" onClick={(e) => e.stopPropagation()}>
                <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
                    <h2 className="text-lg font-semibold text-text-main">Soạn email báo giá</h2>
                    <button onClick={onClose} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                </div>
                <form onSubmit={handleSubmit} className="px-5 py-4 space-y-3">
                    <div>
                        <label className={lbl}>Người nhận</label>
                        <input
                            className={inp}
                            value={to}
                            onChange={e => setTo(e.target.value)}
                            placeholder={isLoading ? 'Đang tải...' : 'email@congty.com'}
                        />
                        {draft && !draft.toEmail && !to && (
                            <p className="text-sm text-danger mt-1">Báo giá chưa có email khách hàng/liên hệ — hãy nhập địa chỉ người nhận.</p>
                        )}
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>CC</label>
                            <input className={inp} value={cc} onChange={e => setCc(e.target.value)}
                                placeholder="Nhiều email cách nhau bởi dấu phẩy" />
                        </div>
                        <div>
                            <label className={lbl}>BCC</label>
                            <input className={inp} value={bcc} onChange={e => setBcc(e.target.value)}
                                placeholder="Nhiều email cách nhau bởi dấu phẩy" />
                        </div>
                    </div>
                    <div>
                        <label className={lbl}>Tiêu đề</label>
                        <input className={inp} value={subject} onChange={e => setSubject(e.target.value)} />
                    </div>
                    <div>
                        <label className={lbl}>Nội dung</label>
                        <RichTextEditor value={body} onChange={setBody} height={320} />
                        <p className="text-sm text-gray-500 mt-1">3 nút phản hồi (Đồng ý / Điều chỉnh / Không đồng ý) và file PDF báo giá sẽ được tự động thêm vào email khi gửi.</p>
                    </div>
                    <div className="flex justify-end gap-3 pt-2 border-t border-gray-100">
                        <button type="button" onClick={onClose} className="px-4 py-1.5 rounded-btn border border-gray-300 text-md text-text-main hover:bg-gray-50">Hủy</button>
                        <button type="submit" disabled={isPending || isLoading} className="px-4 py-1.5 rounded-btn bg-primary text-white text-md hover:opacity-90 disabled:opacity-50">
                            {isPending ? 'Đang gửi...' : 'Gửi email'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
