import { keepPreviousData, useQuery } from '@tanstack/react-query';
import type { PageParams } from '@/shared/types/api';
import { contactService } from '../services/contactService';

/** Danh sách liên hệ phân trang server-side (search `q` + tag lọc `status` trong PageParams). */
export function usePagedContactList(params: PageParams) {
    return useQuery({
        queryKey: ['contacts', 'paged', params],
        queryFn: () => contactService.getList(params).then(r => r.data.data),
        placeholderData: keepPreviousData,
    });
}
