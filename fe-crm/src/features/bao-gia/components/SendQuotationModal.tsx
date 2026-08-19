import { useEffect, useRef, useState, type FormEvent } from 'react';
import { FiX, FiEye } from 'react-icons/fi';
import { useAlert } from '@/shared/alert/useAlert';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { RichTextEditor } from '@/shared/components/RichTextEditor';
import { ModalFooter } from '@/shared/components/ModalFooter';
import { ActionButton } from '@/shared/components/ActionButton';
import { FormField } from '@/shared/components/form/FormField';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { collectErrors, emailError } from '@/shared/utils/validators';
import { useQuotationEmailDraft } from '../hooks/useQuotationEmailDraft';
import { useQuotationWorkflow } from '../hooks/useQuotationWorkflow';
import { quotationService } from '../services/quotationService';

interface Props {
    // ID báo giá cần gửi (null = đóng)
    quotationId: number | null;
    onClose: () => void;
}

// modal soạn nội dung email báo giá trước khi gửi cho khách. Nội dung mặc định được nạp từ
// BE (GET /email-draft) để hiển thị sẵn cho người dùng sửa; 3 nút phản hồi (Đồng ý/Điều
// chỉnh/Không đồng ý) do BE tự chèn vào cuối khi gửi
export function SendQuotationModal({ quotationId, onClose }: Props) {
    const open = quotationId !== null;
    const { showAlert } = useAlert();
    const { confirm } = useConfirm();
    const { data: draft, isLoading } = useQuotationEmailDraft(quotationId, open);
    const { mutate: workflowFn, isPending } = useQuotationWorkflow();
    const [to, setTo] = useState('');
    const [subject, setSubject] = useState('');
    const [body, setBody] = useState('');
    const [errors, setErrors] = useState<Record<string, string>>({});
    const [previewing, setPreviewing] = useState(false);

    const formRef = useRef<HTMLFormElement>(null);
    // enabled: open — modal render rỗng khi đóng. Hook tự né Enter/mũi tên trong TinyMCE (.tox).
    useFormKeyboardNav(formRef, {
        onSubmit: () => formRef.current?.requestSubmit(),
        onCancel: onClose,
        enabled: open,
    });

    // Nạp nội dung mặc định vào ô soạn khi draft tải xong (người dùng vẫn sửa được người nhận).
    useEffect(() => {
        if (draft) {
            setTo(draft.toEmail ?? '');
            setSubject(draft.subject ?? '');
            setBody(draft.body ?? '');
        }
    }, [draft]);

    if (!open) return null;

    // tải PDF báo giá và mở ở tab mới — endpoint cần JWT nên không dùng <a href> trực tiếp
    const handlePreviewPdf = async () => {
        setPreviewing(true);
        try {
            const res = await quotationService.getPdfPreview(quotationId as number);
            const url = URL.createObjectURL(res.data);
            window.open(url, '_blank');
            // Thu hồi sau một nhịp để tab mới kịp nạp xong nội dung.
            setTimeout(() => URL.revokeObjectURL(url), 10000);
        } catch {
            showAlert('Không tải được PDF xem trước');
        } finally {
            setPreviewing(false);
        }
    };

    // xóa lỗi của một ô ngay khi người dùng gõ lại
    const clearError = (key: string) =>
        setErrors((prev) => (prev[key] ? { ...prev, [key]: '' } : prev));

    // validate rồi hỏi xác nhận trước khi gửi email thật cho khách
    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();

        const found = collectErrors({
            to: !to.trim() ? 'Vui lòng nhập email người nhận' : emailError(to),
            subject: !subject.trim() ? 'Vui lòng nhập tiêu đề email' : null,
            body: !body.trim() ? 'Vui lòng nhập nội dung email' : null,
        });
        setErrors(found);
        if (Object.keys(found).length > 0) return;

        // Gửi ra ngoài cho khách hàng — không rút lại được, phải xác nhận.
        const ok = await confirm({
            message: `Gửi báo giá tới ${to.trim()}? Email đã gửi không thu hồi được.`,
            confirmLabel: 'Gửi email',
        });
        if (!ok) return;

        workflowFn(
            {
                id: quotationId,
                action: 'send',
                emailPayload: {
                    to: to.trim(),
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

    return (
        <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40" onClick={onClose}>
            <div className="bg-white rounded-card shadow-lg w-full max-w-2xl mx-4 max-h-[90vh] overflow-y-auto" onClick={(e) => e.stopPropagation()}>
                <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
                    <h2 className="text-lg font-semibold text-text-main">Soạn email báo giá</h2>
                    <div className="flex items-center gap-1.5">
                        <ActionButton type="button" variant="outline" icon={FiEye} onClick={handlePreviewPdf} disabled={previewing}>
                            {previewing ? 'Đang tải...' : 'Xem trước PDF'}
                        </ActionButton>
                        <button onClick={onClose} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                    </div>
                </div>
                <form ref={formRef} onSubmit={handleSubmit} noValidate className="px-5 py-4 space-y-3">
                    <FormField
                        label="Người nhận"
                        required
                        error={errors.to}
                        hint={draft && !draft.toEmail && !to
                            ? 'Báo giá chưa có email khách hàng/liên hệ — hãy nhập địa chỉ người nhận.'
                            : undefined}
                    >
                        <input
                            className={inp}
                            value={to}
                            onChange={e => { setTo(e.target.value); clearError('to'); }}
                            placeholder={isLoading ? 'Đang tải...' : 'email@congty.com'}
                        />
                    </FormField>
                    <FormField label="Tiêu đề" required error={errors.subject}>
                        <input className={inp} value={subject}
                            onChange={e => { setSubject(e.target.value); clearError('subject'); }} />
                    </FormField>
                    <FormField
                        label="Nội dung"
                        required
                        error={errors.body}
                        hint="Nút 'Xem chi tiết báo giá' và file PDF báo giá sẽ được tự động thêm vào email khi gửi."
                    >
                        <RichTextEditor value={body} onChange={(v) => { setBody(v); clearError('body'); }} height={320} />
                    </FormField>
                    <ModalFooter onCancel={onClose} saving={isPending || isLoading} saveLabel="Gửi email" />
                </form>
            </div>
        </div>
    );
}
