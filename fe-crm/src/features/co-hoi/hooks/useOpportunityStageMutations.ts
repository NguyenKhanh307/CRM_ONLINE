import { useMutation, useQueryClient } from '@tanstack/react-query';
import { opportunityStageService, type OpportunityStagePayload } from '../services/opportunityStageService';

// invalidate cache danh sách giai đoạn sau mỗi thay đổi
const useInvalidateStages = () => {
    const qc = useQueryClient();
    return () => qc.invalidateQueries({ queryKey: ['opportunity-stages'] });
};

// tạo giai đoạn pipeline mới
export function useCreateStage() {
    const invalidate = useInvalidateStages();
    return useMutation({
        mutationFn: (body: OpportunityStagePayload) => opportunityStageService.create(body),
        onSuccess: invalidate,
    });
}

// cập nhật giai đoạn pipeline
export function useUpdateStage() {
    const invalidate = useInvalidateStages();
    return useMutation({
        mutationFn: ({ id, body }: { id: number; body: OpportunityStagePayload }) =>
            opportunityStageService.update(id, body),
        onSuccess: invalidate,
    });
}

// xóa giai đoạn pipeline
export function useDeleteStage() {
    const invalidate = useInvalidateStages();
    return useMutation({
        mutationFn: (id: number) => opportunityStageService.remove(id),
        onSuccess: invalidate,
    });
}
