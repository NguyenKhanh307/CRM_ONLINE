import { InfoRow } from '@/shared/components/detail/InfoRow';
import { formatCurrency, formatNumber } from '@/shared/utils/number';
import { formatISODate } from '@/shared/utils/date';
import type { CustomerResult } from '../types/customerTypes';

const TYPE_LABELS: Record<string, string> = {
    individual: 'Cá nhân', company: 'Công ty', agency: 'Đại lý',
};

interface CustomerInfoPanelProps {
    customer: CustomerResult;
}

// tab "Tổng quan" của trang chi tiết Khách hàng — toàn bộ trường nghiệp vụ, chia 2 cột
export const CustomerInfoPanel = ({ customer: c }: CustomerInfoPanelProps) => (
    <div className="grid grid-cols-1 md:grid-cols-2 gap-x-8">
        <div>
            <InfoRow label="Tên khách hàng" value={c.name} />
            <InfoRow label="Tên viết tắt" value={c.shortName} />
            <InfoRow label="Loại" value={TYPE_LABELS[c.type] ?? c.type} />
            <InfoRow label="Mã số thuế" value={c.taxCode} />
            <InfoRow label="Điện thoại" value={c.phone} />
            <InfoRow label="Email" value={c.email} />
            <InfoRow label="Website" value={c.website} />
            <InfoRow label="Địa chỉ" value={c.address} />
            <InfoRow label="Ngành nghề" value={c.industry} />
            <InfoRow label="Nguồn" value={c.source} />
        </div>
        <div>
            <InfoRow label="Người phụ trách" value={c.ownerName} />
            <InfoRow label="Xếp hạng" value={c.rating} />
            <InfoRow label="Số ngày được nợ" value={c.creditDays != null ? formatNumber(c.creditDays) : null} />
            <InfoRow label="Hạn mức nợ" value={c.creditLimit != null ? formatCurrency(c.creditLimit) : null} />
            <InfoRow label="Quy mô nhân sự" value={c.employeeSize} />
            <InfoRow label="Nhà phân phối" value={c.isDistributor ? 'Có' : 'Không'} />
            <InfoRow label="Người tạo" value={c.createdByName} />
            <InfoRow label="Ngày tạo" value={c.createdAt ? formatISODate(c.createdAt) : null} />
            <InfoRow label="Người sửa cuối" value={c.updatedByName} />
            <InfoRow label="Ngày sửa" value={c.updatedAt ? formatISODate(c.updatedAt) : null} />
        </div>
    </div>
);
