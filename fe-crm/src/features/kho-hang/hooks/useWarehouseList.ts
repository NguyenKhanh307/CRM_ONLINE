import { useQuery } from '@tanstack/react-query';
import { warehouseService } from '../services/warehouseService';

export function useWarehouseList() {
    return useQuery({
        queryKey: ['warehouses'],
        queryFn: () => warehouseService.getList({ page: 0, size: 100 }).then(r => r.data.data.items),
    });
}
