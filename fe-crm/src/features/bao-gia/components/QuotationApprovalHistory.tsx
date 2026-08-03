import { ScrollFrame } from '@/shared/components/table/ScrollFrame';
import { formatISODate } from '@/shared/utils/date';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import type { QuotationApprovalResult } from '../types/quotationTypes';

const STATUS_LABEL: Record<string, string> = {
    pending: 'Chờ duyệt',
    approved: 'Đã duyệt',
    rejected: 'Từ chối',
};
const STATUS_CLASS: Record<string, string> = {
    pending: 'bg-yellow-100 text-yellow-700',
    approved: 'bg-green-100 text-green-700',
    rejected: 'bg-red-100 text-red-600',
};

// định dạng ngày giờ đầy đủ (dd/mm/yyyy HH:mm) từ chuỗi ISO
function formatDateTime(iso: string | null): string {
    if (!iso) return '';
    const time = new Date(iso).toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
    return `${formatISODate(iso)} ${time}`;
}

interface Props {
    approvals: QuotationApprovalResult[] | undefined;
}

// lịch sử phê duyệt báo giá — mỗi lần "Gửi duyệt" tạo một bước mới; ghi lại người duyệt,
// trạng thái và ghi chú/lý do (kể cả lý do từ chối để nhân viên biết cần sửa gì)
export const QuotationApprovalHistory = ({ approvals }: Props) => {
    const { data: users } = useActiveUsers();
    const userName = (id: number | null) => users?.find(u => u.id === id)?.fullName ?? (id ? `#${id}` : '—');

    const items = approvals ?? [];
    if (items.length === 0) {
        return <div className="py-8 text-center text-sm text-gray-400">Chưa có lượt gửi duyệt nào.</div>;
    }

    return (
        <ScrollFrame visibleRows={6} className="pr-1">
            <ul className="space-y-3">
                {items.map(a => (
                    <li key={a.id} className="border-b border-gray-100 pb-3 last:border-0">
                        <div className="flex items-center gap-2 flex-wrap">
                            <span className="text-table font-medium text-text-main">Cấp {a.level}</span>
                            <span className={`px-1.5 py-0.5 rounded text-sm ${STATUS_CLASS[a.status] ?? 'bg-gray-100 text-gray-600'}`}>
                                {STATUS_LABEL[a.status] ?? a.status}
                            </span>
                        </div>
                        <div className="text-sm text-gray-500 mt-0.5 flex flex-wrap gap-x-3">
                            <span>Gửi lúc {formatDateTime(a.createdAt)}</span>
                            {a.approvedAt && <span>Xử lý lúc {formatDateTime(a.approvedAt)} bởi {userName(a.approverId)}</span>}
                        </div>
                        {a.comment && (
                            <div className="text-sm text-text-main mt-1 bg-gray-50 rounded px-2 py-1">
                                {a.status === 'rejected' ? 'Lý do từ chối: ' : 'Ghi chú: '}{a.comment}
                            </div>
                        )}
                    </li>
                ))}
            </ul>
        </ScrollFrame>
    );
};
