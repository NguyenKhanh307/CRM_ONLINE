import { useLiveQuery } from '@/core/data/useLiveQuery';
import type { PageParams } from '@/shared/types/api';
import { campaignService } from '../services/campaignService';

// danh sách chiến dịch phân trang server-side (search `q` + tag lọc `status` trong PageParams)
export function usePagedCampaignList(params: PageParams) {
    return useLiveQuery(`campaigns:paged:${JSON.stringify(params)}`, () => campaignService.getList(params).then(r => r.data.data));
}
