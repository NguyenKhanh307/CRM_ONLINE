import { useQuery } from '@tanstack/react-query';
import { quotationService } from '../services/quotationService';

export function useQuotationList() {
    return useQuery({
        queryKey: ['quotations'],
        queryFn: () => quotationService.getList({ page: 0, size: 100 }).then(r => r.data.data.items),
    });
}
