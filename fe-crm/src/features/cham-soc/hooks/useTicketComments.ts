import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { ticketService } from '../services/ticketService';

/** Lấy danh sách ghi chú / lịch sử của một phiếu. */
export function useTicketComments(ticketId: number | undefined) {
    return useQuery({
        queryKey: ['ticket-comments', ticketId],
        queryFn: () => ticketService.getComments(ticketId as number).then(r => r.data.data),
        enabled: ticketId != null,
    });
}

/** Thêm ghi chú (note) cho phiếu — invalidate danh sách ghi chú. */
export function useCreateTicketComment(ticketId: number) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: { content: string; isInternal?: boolean }) =>
            ticketService.createComment(ticketId, payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['ticket-comments', ticketId] }),
    });
}
