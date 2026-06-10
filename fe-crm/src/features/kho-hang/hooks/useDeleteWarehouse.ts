import { useMutation, useQueryClient } from '@tanstack/react-query';
import { warehouseService } from '../services/warehouseService';

export function useDeleteWarehouse() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => warehouseService.remove(id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['warehouses'] }),
    });
}
