import { useMemo } from 'react';
import { InfoRow } from '@/shared/components/detail/InfoRow';
import { RecordItemsPanel } from '@/shared/components/table/RecordItemsPanel';
import { getLineItemPanelColumns } from '@/shared/components/table/lineItemPanelColumns';
import { useProductMap } from '@/features/san-pham/hooks/useProductMap';
import { formatCurrency, formatNumber } from '@/shared/utils/number';
import { formatISODate } from '@/shared/utils/date';
import { useOpportunityItems } from '../hooks/useOpportunityItems';
import type { OpportunityResult } from '../types/opportunityTypes';

interface OpportunityInfoPanelProps {
    opportunity: OpportunityResult;
}

/** Tab "Tổng quan" của trang chi tiết Cơ hội — thông tin chung + bảng dòng hàng (chỉ xem). */
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
                    <InfoRow label="Nguồn" value={o.source} />
                </div>
                <div>
                    <InfoRow label="Người phụ trách" value={o.ownerName} />
                    <InfoRow label="Giá trị" value={o.amount != null ? formatCurrency(o.amount) : null} />
                    <InfoRow label="Doanh số kỳ vọng" value={o.expectedRevenue != null ? formatCurrency(o.expectedRevenue) : null} />
                    <InfoRow label="Xác suất thắng" value={o.probability != null ? `${formatNumber(o.probability)}%` : null} />
                    <InfoRow label="Ngày dự kiến chốt" value={o.expectedCloseDate ? formatISODate(o.expectedCloseDate) : null} />
                    <InfoRow label="Lý do thắng/thua" value={o.winLossReason} />
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
