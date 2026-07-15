import { useQuery } from '@tanstack/react-query';
import { contactService } from '../services/contactService';

/** Lấy chi tiết một liên hệ theo ID (trang chi tiết). */
export function useContactDetail(id: number | undefined) {
    return useQuery({
        queryKey: ['contact', id],
        queryFn: () => contactService.getById(id as number).then(r => r.data.data),
        enabled: id != null && !Number.isNaN(id),
    });
}
