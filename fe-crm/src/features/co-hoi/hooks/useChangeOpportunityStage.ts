import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { opportunityService } from '../services/opportunityService';

interface ChangeStageVars {
    id: number;
    stageId: number;
    winLossReason?: string | null;
}

// đổi giai đoạn của một cơ hội (kéo-thả trên Kanban) — lớp data ở đây không có cache dùng chung
// để đọc/ghi trực tiếp như React Query, nên cập nhật lạc quan + rollback được chuyển xuống state
// cục bộ của OpportunityBoardPage; hook này chỉ lo gọi API + báo làm mới sau khi thành công
export function useChangeOpportunityStage() {
    const { mutate: run, isPending } = useLiveMutation(
        ({ id, stageId, winLossReason }: ChangeStageVars) => opportunityService.changeStage(id, { stageId, winLossReason }));

    // notify('opportunities') đánh thức mọi key con — cả bảng Kanban ('opportunities:board:...')
    // lẫn danh sách cơ hội ('opportunities:paged:...') — kèm trang chi tiết nếu đang mở
    const mutate: typeof run = (input, callbacks) =>
        run(input, {
            ...callbacks,
            onSuccess: (data) => { notify('opportunities'); notify(`opportunity:${input.id}`); callbacks?.onSuccess?.(data); },
        });

    return { mutate, isPending };
}
