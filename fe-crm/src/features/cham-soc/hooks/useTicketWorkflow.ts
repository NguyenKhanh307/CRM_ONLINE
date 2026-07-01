import { useMutation, useQueryClient } from '@tanstack/react-query';
import { ticketService } from '../services/ticketService';
import type { ResolutionType } from '../types/ticketTypes';

/** Loại hành động chuyển trạng thái phiếu. */
export type TicketAction =
    | 'assign' | 'start' | 'resolve' | 'approve' | 'reject'
    | 'receive' | 'inspect' | 'complete' | 'close' | 'reopen' | 'csat';

interface ActionArgs {
    id: number;
    action: TicketAction;
    toUserId?: number;
    resolutionType?: ResolutionType;
    note?: string;
    reason?: string;
    score?: number;
    comment?: string;
}

/**
 * Hook thực hiện các hành động chuyển trạng thái phiếu:
 * assign / start / resolve / approve / reject / receive / inspect / complete / close / reopen / csat.
 */
export function useTicketWorkflow() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: ({ id, action, toUserId, resolutionType, note, reason, score, comment }: ActionArgs) => {
            switch (action) {
                case 'assign': return ticketService.assign(id, toUserId as number);
                case 'start': return ticketService.start(id);
                case 'resolve': return ticketService.resolve(id, resolutionType, note);
                case 'approve': return ticketService.approve(id, note);
                case 'reject': return ticketService.reject(id, reason);
                case 'receive': return ticketService.receive(id);
                case 'inspect': return ticketService.inspect(id);
                case 'complete': return ticketService.complete(id, resolutionType, note);
                case 'close': return ticketService.close(id);
                case 'reopen': return ticketService.reopen(id);
                case 'csat': return ticketService.csat(id, score as number, comment);
            }
        },
        onSuccess: (_res, { id }) => {
            qc.invalidateQueries({ queryKey: ['tickets'] });
            qc.invalidateQueries({ queryKey: ['ticket', id] });
            qc.invalidateQueries({ queryKey: ['ticket-comments', id] });
        },
    });
}
