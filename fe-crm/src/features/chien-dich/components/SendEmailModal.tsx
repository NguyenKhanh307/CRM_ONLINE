import { useRef, useState, type FormEvent } from 'react';
import { FiX } from 'react-icons/fi';
import { useAlert } from '@/shared/alert/useAlert';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { ModalFooter } from '@/shared/components/ModalFooter';
import { FormField } from '@/shared/components/form/FormField';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { collectErrors } from '@/shared/utils/validators';
import { useSendCampaignEmail } from '../hooks/useSendCampaignEmail';

interface Props {
    campaignId: number;
    open: boolean;
    onClose: () => void;
}

/** Modal soạn + gửi email hàng loạt cho thành viên chiến dịch. */
export function SendEmailModal({ campaignId, open, onClose }: Props) {
    const { showAlert } = useAlert();
    const { confirm } = useConfirm();
    const { mutateAsync, isPending } = useSendCampaignEmail(campaignId);
    const [subject, setSubject] = useState('');
    const [body, setBody] = useState('');
    const [errors, setErrors] = useState<Record<string, string>>({});

    const formRef = useRef<HTMLFormElement>(null);
    // enabled: open — modal render rỗng khi đóng.
    useFormKeyboardNav(formRef, {
        onSubmit: () => formRef.current?.requestSubmit(),
        onCancel: onClose,
        enabled: open,
    });

    if (!open) return null;

    const clearError = (key: string) =>
        setErrors((prev) => (prev[key] ? { ...prev, [key]: '' } : prev));

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();

        const found = collectErrors({
            subject: !subject.trim() ? 'Vui lòng nhập tiêu đề email' : null,
            body: !body.trim() ? 'Vui lòng nhập nội dung email' : null,
        });
        setErrors(found);
        if (Object.keys(found).length > 0) return;

        // Gửi hàng loạt ra ngoài hệ thống — không rút lại được, phải xác nhận.
        const ok = await confirm({
            message: 'Gửi email này tới tất cả thành viên chiến dịch? Email đã gửi không thu hồi được.',
            confirmLabel: 'Gửi email',
        });
        if (!ok) return;

        try {
            const sent = await mutateAsync({ subject: subject.trim(), body: body.trim() });
            showAlert(`Đã gửi ${sent} email cho thành viên chiến dịch`);
            setSubject(''); setBody('');
            onClose();
        } catch (err) {
            const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message ?? 'Gửi email thất bại';
            showAlert(msg);
        }
    };

    const inp = 'w-full border border-gray-300 rounded-btn px-3 py-1.5 text-md text-text-main focus:outline-none focus:border-primary';

    return (
        <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40" onClick={onClose}>
            <div className="bg-white rounded-card shadow-lg w-full max-w-lg mx-4" onClick={(e) => e.stopPropagation()}>
                <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
                    <h2 className="text-lg font-semibold text-text-main">Gửi email chiến dịch</h2>
                    <button onClick={onClose} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                </div>
                <form ref={formRef} onSubmit={handleSubmit} noValidate className="px-5 py-4 space-y-3">
                    <FormField label="Tiêu đề" required error={errors.subject}>
                        <input className={inp} value={subject}
                            onChange={e => { setSubject(e.target.value); clearError('subject'); }} />
                    </FormField>
                    <FormField
                        label="Nội dung (hỗ trợ HTML)"
                        required
                        error={errors.body}
                        hint="Email sẽ gửi tới tất cả thành viên có email hợp lệ và chưa hủy đăng ký."
                    >
                        <textarea className={inp} rows={6} value={body}
                            onChange={e => { setBody(e.target.value); clearError('body'); }} />
                    </FormField>
                    <ModalFooter onCancel={onClose} saving={isPending} saveLabel="Gửi email" />
                </form>
            </div>
        </div>
    );
}
