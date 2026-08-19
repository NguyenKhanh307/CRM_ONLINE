import { useLiveQuery } from '@/core/data/useLiveQuery';
import type { PageParams } from '@/shared/types/api';
import { contactService } from '../services/contactService';

// danh sách liên hệ phân trang server-side (search `q` + tag lọc `status` trong params)
export function usePagedContactList(params: PageParams) {
    return useLiveQuery(`contacts:paged:${JSON.stringify(params)}`, () => contactService.getList(params).then(r => r.data.data));
}
