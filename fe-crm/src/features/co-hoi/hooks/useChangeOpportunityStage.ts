import { useMutation, useQueryClient } from '@tanstack/react-query';
import { opportunityService } from '../services/opportunityService';
import type { BoardColumn } from '../types/boardTypes';

interface ChangeStageVars {
    id: number;
    stageId: number;
    winLossReason?: string | null;
}

/** Chuyển thẻ sang cột mới trong dữ liệu cache: cập nhật cả số lượng và tổng tiền của 2 cột. */
function moveCard(columns: BoardColumn[], id: number, toStageId: number): BoardColumn[] {
    const card = columns.flatMap(c => c.cards).find(c => c.id === id);
    if (!card) return columns;
    const amount = card.amount ?? 0;

    return columns.map(col => {
        if (col.cards.some(c => c.id === id) && col.stageId !== toStageId) {
            return {
                ...col,
                cards: col.cards.filter(c => c.id !== id),
                total: Math.max(0, col.total - 1),
                sumAmount: col.sumAmount - amount,
            };
        }
        if (col.stageId === toStageId && !col.cards.some(c => c.id === id)) {
            return {
                ...col,
                cards: [{ ...card, stageId: toStageId }, ...col.cards],
                total: col.total + 1,
                sumAmount: col.sumAmount + amount,
            };
        }
        return col;
    });
}

/**
 * Đổi giai đoạn của một cơ hội (kéo-thả trên Kanban), cập nhật lạc quan và tự rollback khi lỗi.
 * @param q Từ khóa tìm kiếm hiện tại của bảng — phải khớp queryKey đang hiển thị.
 */
export function useChangeOpportunityStage(q?: string) {
    const qc = useQueryClient();
    const key = ['opportunities', 'board', q ?? ''];

    return useMutation({
        mutationFn: ({ id, stageId, winLossReason }: ChangeStageVars) =>
            opportunityService.changeStage(id, { stageId, winLossReason }),
        onMutate: async ({ id, stageId }) => {
            await qc.cancelQueries({ queryKey: key });
            const previous = qc.getQueryData<BoardColumn[]>(key);
            qc.setQueryData<BoardColumn[]>(key, cols => (cols ? moveCard(cols, id, stageId) : cols));
            return { previous };
        },
        onError: (_err, _vars, ctx) => {
            if (ctx?.previous) qc.setQueryData(key, ctx.previous);
        },
        // Làm mới cả bảng Kanban lẫn danh sách cơ hội (queryKey ['opportunities'] khớp tiền tố)
        onSettled: () => qc.invalidateQueries({ queryKey: ['opportunities'] }),
    });
}
