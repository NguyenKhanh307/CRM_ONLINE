import { useMutation, useQueryClient } from '@tanstack/react-query';
import { quotationService } from '@/features/bao-gia/services/quotationService';

/**
 * Hook clone báo giá từ cơ hội (POST /api/quotations/from-opportunity/{id}).
 * Tạo báo giá mới → làm mới danh sách báo giá + cơ hội (báo giá đầu tiên đặt primary).
 */
export function useCreateQuotationFromOpportunity() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (opportunityId: number) => quotationService.fromOpportunity(opportunityId),
        onSuccess: (_d, opportunityId) => {
            ['quotations', 'opportunities'].forEach(
                (key) => qc.invalidateQueries({ queryKey: [key] }));
            qc.invalidateQueries({ queryKey: ['opportunity', opportunityId] });
        },
    });
}
