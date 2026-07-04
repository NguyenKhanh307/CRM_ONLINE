import { useState, type FormEvent } from 'react';
import { FiX } from 'react-icons/fi';
import { useAlert } from '@/shared/alert/useAlert';
import { useSendCampaignEmail } from '../hooks/useSendCampaignEmail';

interface Props {
    campaignId: number;
    open: boolean;
    onClose: () => void;
}

/** Modal soạn + gửi email hàng loạt cho thành viên chiến dịch. */
export function SendEmailModal({ campaignId, open, onClose }: Props) {
    const { showAlert } = useAlert();
    const { mutateAsync, isPending } = useSendCampaignEmail(campaignId);
    const [subject, setSubject] = useState('');
    const [body, setBody] = useState('');

    if (!open) return null;

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        if (!subject.trim() || !body.trim()) { showAlert('Vui lòng nhập tiêu đề và nội dung email'); return; }
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
    const lbl = 'block text-sm font-medium text-gray-700 mb-1';

    return (
        <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40" onClick={onClose}>
            <div className="bg-white rounded-card shadow-lg w-full max-w-lg mx-4" onClick={(e) => e.stopPropagation()}>
                <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
                    <h2 className="text-lg font-semibold text-text-main">Gửi email chiến dịch</h2>
                    <button onClick={onClose} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                </div>
                <form onSubmit={handleSubmit} className="px-5 py-4 space-y-3">
                    <div>
                        <label className={lbl}>Tiêu đề</label>
                        <input className={inp} value={subject} onChange={e => setSubject(e.target.value)} />
                    </div>
                    <div>
                        <label className={lbl}>Nội dung (hỗ trợ HTML)</label>
                        <textarea className={inp} rows={6} value={body} onChange={e => setBody(e.target.value)} />
                    </div>
                    <p className="text-sm text-gray-500">Email sẽ gửi tới tất cả thành viên có email hợp lệ và chưa hủy đăng ký.</p>
                    <div className="flex justify-end gap-3 pt-2 border-t border-gray-100">
                        <button type="button" onClick={onClose} className="px-4 py-1.5 rounded-btn border border-gray-300 text-md text-text-main hover:bg-gray-50">Hủy</button>
                        <button type="submit" disabled={isPending} className="px-4 py-1.5 rounded-btn bg-primary text-white text-md hover:opacity-90 disabled:opacity-50">
                            {isPending ? 'Đang gửi...' : 'Gửi email'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
