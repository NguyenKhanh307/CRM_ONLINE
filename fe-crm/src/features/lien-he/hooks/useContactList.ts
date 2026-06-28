import { useQuery } from '@tanstack/react-query';
import { contactService } from '../services/contactService';

/** Lấy danh sách liên hệ (phân trang). */
export function useContactList() {
    return useQuery({
        queryKey: ['contacts'],
        queryFn: () => contactService.getList({ page: 0, size: 500, sortBy: 'createdAt', sortDir: 'desc' }).then(r => r.data.data.items),
    });
}
