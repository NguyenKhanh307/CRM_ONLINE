import { useLiveQuery } from '@/core/data/useLiveQuery';
import { contactService } from '../services/contactService';

// lấy toàn bộ liên hệ (tối đa 500 dòng) — dùng cho dropdown chọn liên hệ
export function useContactList() {
    return useLiveQuery('contacts', () =>
        contactService.getList({ page: 0, size: 500, sortBy: 'createdAt', sortDir: 'desc' }).then(r => r.data.data.items));
}
