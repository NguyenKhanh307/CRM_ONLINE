import { useQuery } from '@tanstack/react-query';
import { contactService } from '../services/contactService';

export function useContactList() {
    return useQuery({
        queryKey: ['contacts'],
        queryFn: () => contactService.getList({ page: 0, size: 100 }).then(r => r.data.data.items),
    });
}
