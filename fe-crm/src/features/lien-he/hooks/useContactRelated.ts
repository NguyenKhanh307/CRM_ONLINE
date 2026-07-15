import { useQuery } from '@tanstack/react-query';
import { contactService } from '../services/contactService';

/** Lấy bản ghi liên quan của một liên hệ (trang chi tiết). */
export function useContactRelated(id: number | undefined) {
    return useQuery({
        queryKey: ['contact', id, 'related'],
        queryFn: () => contactService.getRelated(id as number).then(r => r.data.data),
        enabled: id != null && !Number.isNaN(id),
    });
}
