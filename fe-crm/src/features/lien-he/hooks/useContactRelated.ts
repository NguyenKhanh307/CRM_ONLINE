import { useLiveQuery } from '@/core/data/useLiveQuery';
import { contactService } from '../services/contactService';

// lấy bản ghi liên quan của một liên hệ (trang chi tiết)
export function useContactRelated(id: number | undefined) {
    const enabled = id != null && !Number.isNaN(id);
    return useLiveQuery(`contact:${id}:related`, () => contactService.getRelated(id as number).then(r => r.data.data), enabled);
}
