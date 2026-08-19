import { useLiveQuery } from '@/core/data/useLiveQuery';
import { opportunityService } from '../services/opportunityService';

// nạp bảng Kanban cơ hội (cột = giai đoạn pipeline, thẻ = cơ hội)
export function useOpportunityBoard(q?: string) {
    return useLiveQuery(`opportunities:board:${q ?? ''}`, () => opportunityService.getBoard(q).then(r => r.data.data));
}
