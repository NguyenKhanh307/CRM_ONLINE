import { useMutation, useQueryClient } from '@tanstack/react-query';
import { warehouseService } from '../services/warehouseService';
import type { UpdateWarehousePayload } from '../types/warehouseTypes';

export function useUpdateWarehouse() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: ({ id, payload }: { id: number; payload: UpdateWarehousePayload }) =>
            warehouseService.update(id, payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['warehouses'] }),
    });
}
