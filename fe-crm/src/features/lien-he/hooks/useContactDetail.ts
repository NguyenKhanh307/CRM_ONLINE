import { useLiveQuery } from '@/core/data/useLiveQuery';
import { contactService } from '../services/contactService';

// lấy chi tiết một liên hệ theo ID (trang chi tiết)
export function useContactDetail(id: number | undefined) {
    const enabled = id != null && !Number.isNaN(id);
    return useLiveQuery(`contact:${id}`, () => contactService.getById(id as number).then(r => r.data.data), enabled);
}
