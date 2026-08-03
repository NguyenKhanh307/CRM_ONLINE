import { useQuery } from '@tanstack/react-query';
import { opportunityService } from '../services/opportunityService';

// nạp bảng Kanban cơ hội (cột = giai đoạn pipeline, thẻ = cơ hội)
export function useOpportunityBoard(q?: string) {
    return useQuery({
        queryKey: ['opportunities', 'board', q ?? ''],
        queryFn: () => opportunityService.getBoard(q).then(r => r.data.data),
    });
}
