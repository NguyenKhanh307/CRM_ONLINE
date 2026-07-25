import { useRef, useState, type FormEvent } from 'react';
import { FiTrash2, FiPlus } from 'react-icons/fi';
import { formatISODate } from '@/shared/utils/date';
import { ScrollFrame } from '@/shared/components/table/ScrollFrame';
import { ActionButton } from '@/shared/components/ActionButton';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { collectErrors, emailError, phoneError } from '@/shared/utils/validators';
import { useCampaignMembers, useCreateCampaignMember, useDeleteCampaignMember } from '../hooks/useCampaignMembers';

interface Props {
    campaignId: number;
}

const MEMBER_STATUS_LABELS: Record<string, string> = {
    pending: 'Chờ gửi', sent: 'Đã gửi', opened: 'Đã mở', clicked: 'Đã click',
    bounced: 'Gửi lỗi', responded: 'Đã phản hồi', unsubscribed: 'Hủy đăng ký',
};
const MEMBER_STATUS_COLORS: Record<string, string> = {
    pending: 'bg-gray-100 text-gray-600', sent: 'bg-blue-100 text-blue-700',
    opened: 'bg-indigo-100 text-indigo-700', clicked: 'bg-purple-100 text-purple-700',
    bounced: 'bg-red-100 text-red-600', responded: 'bg-green-100 text-green-700',
    unsubscribed: 'bg-gray-200 text-gray-500',
};

/** Bảng khách hàng của chiến dịch (bảng `campaign_members`) — thêm nhanh (tên/email/sđt) + xóa. */
export function CampaignMembersTable({ campaignId }: Props) {
    const { data: members = [], isLoading } = useCampaignMembers(campaignId);
    const { mutate: createMember, isPending: isCreating } = useCreateCampaignMember(campaignId);
    const { mutate: deleteMember } = useDeleteCampaignMember(campaignId);
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [phone, setPhone] = useState('');
    const [errors, setErrors] = useState<Record<string, string>>({});

    const { confirmCreate, confirmDelete } = useConfirm();

    const formRef = useRef<HTMLFormElement>(null);
    useFormKeyboardNav(formRef, {
        onSubmit: () => formRef.current?.requestSubmit(),
        autoFocus: false, // form phụ trên trang chi tiết — không cướp focus khi mở tab
    });

    const clearError = (key: string) =>
        setErrors((prev) => (prev[key] ? { ...prev, [key]: '' } : prev));

    const handleAdd = async (e: FormEvent) => {
        e.preventDefault();

        const found = collectErrors({
            name: !name.trim() && !email.trim() ? 'Nhập tên hoặc email' : null,
            email: emailError(email),
            phone: phoneError(phone),
        });
        setErrors(found);
        if (Object.keys(found).length > 0) return;

        if (!(await confirmCreate('khách hàng vào chiến dịch'))) return;

        createMember(
            { leadId: null, contactId: null, name: name.trim() || null, email: email.trim() || null, phone: phone.trim() || null },
            { onSuccess: () => { setName(''); setEmail(''); setPhone(''); } },
        );
    };

    const handleDelete = async (id: number, label: string) => {
        if (!(await confirmDelete(`"${label}" khỏi chiến dịch`))) return;
        deleteMember(id);
    };

    const inp = 'border border-gray-300 rounded-btn px-3 py-1.5 text-md focus:outline-none focus:border-primary';
    const errCls = (key: string) => (errors[key] ? 'border-danger' : '');

    return (
        <div className="space-y-4">
            <form ref={formRef} onSubmit={handleAdd} noValidate className="flex flex-wrap items-start gap-2">
                <div>
                    <input className={`${inp} ${errCls('name')}`} placeholder="Tên" value={name}
                        onChange={e => { setName(e.target.value); clearError('name'); }} />
                    {errors.name && <p className="text-xs text-danger mt-1">{errors.name}</p>}
                </div>
                <div>
                    <input className={`${inp} ${errCls('email')}`} placeholder="Email" value={email}
                        onChange={e => { setEmail(e.target.value); clearError('email'); clearError('name'); }} />
                    {errors.email && <p className="text-xs text-danger mt-1">{errors.email}</p>}
                </div>
                <div>
                    <input className={`${inp} ${errCls('phone')}`} placeholder="Số điện thoại" value={phone}
                        onChange={e => { setPhone(e.target.value); clearError('phone'); }} />
                    {errors.phone && <p className="text-xs text-danger mt-1">{errors.phone}</p>}
                </div>
                <ActionButton variant="primary" type="submit" icon={FiPlus} disabled={isCreating}>
                    Thêm khách hàng
                </ActionButton>
            </form>

            <ScrollFrame visibleRows={10} headBg="gray" className="border border-gray-200 rounded-section">
                <table className="w-full text-table">
                    <thead>
                        <tr className="text-title font-semibold text-left">
                            <th className="px-3 py-2">Tên</th>
                            <th className="px-3 py-2">Email</th>
                            <th className="px-3 py-2">SĐT</th>
                            <th className="px-3 py-2">Trạng thái</th>
                            <th className="px-3 py-2">Ngày gửi</th>
                            <th className="px-3 py-2 w-12"></th>
                        </tr>
                    </thead>
                    <tbody>
                        {isLoading && <tr><td colSpan={6} className="px-3 py-4 text-center text-gray-400">Đang tải...</td></tr>}
                        {!isLoading && members.length === 0 && (
                            <tr><td colSpan={6} className="px-3 py-4 text-center text-gray-400">Chưa có khách hàng nào</td></tr>
                        )}
                        {members.map((m, i) => (
                            <tr key={m.id} className={`border-b border-gray-200 ${i % 2 ? 'bg-gray-50' : 'bg-white'}`}>
                                <td className="px-3 py-2">{m.name ?? '—'}</td>
                                <td className="px-3 py-2">{m.email ?? '—'}</td>
                                <td className="px-3 py-2">{m.phone ?? '—'}</td>
                                <td className="px-3 py-2">
                                    <span className={`inline-block px-2 py-0.5 rounded text-sm font-medium ${MEMBER_STATUS_COLORS[m.status] ?? 'bg-gray-100 text-gray-600'}`}>
                                        {MEMBER_STATUS_LABELS[m.status] ?? m.status}
                                    </span>
                                </td>
                                <td className="px-3 py-2">{m.sentAt ? formatISODate(m.sentAt) : '—'}</td>
                                <td className="px-3 py-2">
                                    <button className="p-1.5 rounded hover:bg-red-50 text-gray-400 hover:text-danger"
                                        title="Xóa" onClick={() => handleDelete(m.id, m.name ?? m.email ?? 'khách hàng này')}>
                                        <FiTrash2 size={14} />
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </ScrollFrame>
        </div>
    );
}
