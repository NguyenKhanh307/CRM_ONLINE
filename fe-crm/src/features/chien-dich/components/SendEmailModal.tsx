import { useEffect, useRef, useState, type FormEvent } from 'react';
import { FiX } from 'react-icons/fi';
import { useAlert } from '@/shared/alert/useAlert';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { RichTextEditor } from '@/shared/components/RichTextEditor';
import { ModalFooter } from '@/shared/components/ModalFooter';
import { FormField } from '@/shared/components/form/FormField';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { collectErrors } from '@/shared/utils/validators';
import { UTM_PARAM } from '@/features/tracking-demo/config/trackingDemoConfig';
import { useSendCampaignEmail } from '../hooks/useSendCampaignEmail';

interface Props {
    campaignId: number;
    // mã chiến dịch — dùng để dựng link tracking-demo gắn đúng nguồn
    campaignCode: string;
    open: boolean;
    onClose: () => void;
}

// nội dung mẫu ban đầu: mời khách trải nghiệm, kèm link landing page gắn mã chiến dịch
const buildDefaultBody = (campaignCode: string): string => {
    const trackingUrl = `${window.location.origin}/tracking-demo?${UTM_PARAM}=${campaignCode}`;
    return `<p>Xin chào,</p><p>Mời bạn tìm hiểu thêm giải pháp của chúng tôi tại đây: <a href="${trackingUrl}">${trackingUrl}</a></p><p>Trân trọng.</p>`;
};

// modal soạn + gửi email hàng loạt cho thành viên chiến dịch
export function SendEmailModal({ campaignId, campaignCode, open, onClose }: Props) {
    const { showAlert } = useAlert();
    const { confirm } = useConfirm();
    const { mutateAsync, isPending } = useSendCampaignEmail(campaignId);
    const [subject, setSubject] = useState('');
    const [body, setBody] = useState('');
    const [errors, setErrors] = useState<Record<string, string>>({});

    const formRef = useRef<HTMLFormElement>(null);
    // enabled: open — modal render rỗng khi đóng. Hook tự né Enter/mũi tên trong TinyMCE (.tox).
    useFormKeyboardNav(formRef, {
        onSubmit: () => formRef.current?.requestSubmit(),
        onCancel: onClose,
        enabled: open,
    });

    // mở modal lần đầu (nội dung còn trống) -> nạp sẵn mẫu kèm link tracking-demo, người dùng vẫn sửa được
    useEffect(() => {
        if (open && !body) setBody(buildDefaultBody(campaignCode));
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [open]);

    if (!open) return null;

    // xóa lỗi của một ô ngay khi người dùng gõ lại
    const clearError = (key: string) =>
        setErrors((prev) => (prev[key] ? { ...prev, [key]: '' } : prev));

    // gửi email hàng loạt cho thành viên chiến dịch
    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();

        const found = collectErrors({
            subject: !subject.trim() ? 'Vui lòng nhập tiêu đề email' : null,
            body: !body.trim() ? 'Vui lòng nhập nội dung email' : null,
        });
        setErrors(found);
        if (Object.keys(found).length > 0) return;

        // gửi hàng loạt ra ngoài hệ thống — không rút lại được, phải xác nhận
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
            <div className="bg-white rounded-card shadow-lg w-full max-w-2xl mx-4 max-h-[90vh] overflow-y-auto" onClick={(e) => e.stopPropagation()}>
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
                        label="Nội dung"
                        required
                        error={errors.body}
                        hint="Email sẽ gửi tới tất cả thành viên có email hợp lệ và chưa hủy đăng ký. Đã kèm sẵn link trang landing page gắn đúng mã chiến dịch — có thể sửa lại nội dung tùy ý."
                    >
                        <RichTextEditor value={body} onChange={(html) => { setBody(html); clearError('body'); }} height={280} />
                    </FormField>
                    <ModalFooter onCancel={onClose} saving={isPending} saveLabel="Gửi email" />
                </form>
            </div>
        </div>
    );
}
