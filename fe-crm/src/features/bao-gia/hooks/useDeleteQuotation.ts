import { useMutation, useQueryClient } from '@tanstack/react-query';
import { quotationService } from '../services/quotationService';

// xóa báo giá — invalidate danh sách sau khi thành công
export function useDeleteQuotation() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => quotationService.remove(id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['quotations'] }),
    });
}
