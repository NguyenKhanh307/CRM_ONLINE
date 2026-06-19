import { useMutation, useQueryClient } from '@tanstack/react-query';
import { warehouseService } from '../services/warehouseService';

/** Xóa kho hàng — invalidate danh sách sau khi thành công. */
export function useDeleteWarehouse() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => warehouseService.remove(id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['warehouses'] }),
    });
}
