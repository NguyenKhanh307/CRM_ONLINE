import { keepPreviousData, useQuery } from '@tanstack/react-query';
import type { PageParams } from '@/shared/types/api';
import { campaignService } from '../services/campaignService';

/** Danh sách chiến dịch phân trang server-side (search `q` + tag lọc `status` trong PageParams). */
export function usePagedCampaignList(params: PageParams) {
    return useQuery({
        queryKey: ['campaigns', 'paged', params],
        queryFn: () => campaignService.getList(params).then(r => r.data.data),
        placeholderData: keepPreviousData,
    });
}
