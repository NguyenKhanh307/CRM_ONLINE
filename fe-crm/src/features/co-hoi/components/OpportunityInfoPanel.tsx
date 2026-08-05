import { useMemo } from 'react';
import { InfoRow } from '@/shared/components/detail/InfoRow';
import { RecordItemsPanel } from '@/shared/components/table/RecordItemsPanel';
import { getLineItemPanelColumns } from '@/shared/components/table/lineItemPanelColumns';
import { useProductMap } from '@/features/san-pham/hooks/useProductMap';
import { formatCurrency } from '@/shared/utils/number';
import { formatISODate } from '@/shared/utils/date';
import { useOpportunityItems } from '../hooks/useOpportunityItems';
import type { OpportunityResult } from '../types/opportunityTypes';

interface OpportunityInfoPanelProps {
    opportunity: OpportunityResult;
}

// tab "Tổng quan" của trang chi tiết Cơ hội — thông tin chung + bảng dòng hàng (chỉ xem)
export const OpportunityInfoPanel = ({ opportunity: o }: OpportunityInfoPanelProps) => {
    const { data: items = [], isLoading } = useOpportunityItems(o.id);
    const productMap = useProductMap();
    const columns = useMemo(() => getLineItemPanelColumns(productMap), [productMap]);

    return (
        <div className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-x-8">
                <div>
                    <InfoRow label="Tên cơ hội" value={o.name} />
                    <InfoRow label="Loại cơ hội" value={o.opportunityType} />
                    <InfoRow label="Khách hàng" value={o.customerName} />
                    <InfoRow label="Liên hệ" value={o.contactName} />
                    <InfoRow label="Giai đoạn" value={o.stageName} />
                    <InfoRow label="Chiến dịch" value={o.campaignName} />
                    <InfoRow label="Nguồn" value={o.source} />
                </div>
                <div>
                    <InfoRow label="Người phụ trách" value={o.ownerName} />
                    <InfoRow label="Giá trị" value={o.amount != null ? formatCurrency(o.amount) : null} />
                    <InfoRow label="Lý do thắng/thua" value={o.winLossReason} />
                    <InfoRow label="Người tạo" value={o.createdByName} />
                    <InfoRow label="Ngày tạo" value={o.createdAt ? formatISODate(o.createdAt) : null} />
                    <InfoRow label="Người sửa cuối" value={o.updatedByName} />
                    <InfoRow label="Ngày sửa" value={o.updatedAt ? formatISODate(o.updatedAt) : null} />
                </div>
            </div>

            {o.description && <InfoRow label="Mô tả" value={o.description} />}

            <RecordItemsPanel
                title={o.code}
                columns={columns}
                rows={items}
                isLoading={isLoading}
                emptyText="Cơ hội này chưa có dòng hàng nào"
                visibleRows={5}
            />
        </div>
    );
};
