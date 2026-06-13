import { useMutation, useQueryClient } from '@tanstack/react-query';
import { warehouseService } from '../services/warehouseService';
import type { CreateWarehousePayload } from '../types/warehouseTypes';

/** Tạo mới kho hàng — invalidate danh sách sau khi thành công. */
export function useCreateWarehouse() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: CreateWarehousePayload) => warehouseService.create(payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['warehouses'] }),
    });
}
