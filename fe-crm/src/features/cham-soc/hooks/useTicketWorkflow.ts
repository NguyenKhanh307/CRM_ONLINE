import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { ticketService } from '../services/ticketService';
import type { ResolutionType } from '../types/ticketTypes';

// loại hành động chuyển trạng thái phiếu
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

// chạy hành động chuyển trạng thái phiếu: assign / start / resolve / approve / reject / receive /
// inspect / complete / close / reopen / csat
export function useTicketWorkflow() {
    const { mutate: run, isPending } = useLiveMutation(
        ({ id, action, toUserId, resolutionType, note, reason, score, comment }: ActionArgs) => {
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
        });

    const mutate: typeof run = (input, callbacks) =>
        run(input, {
            ...callbacks,
            onSuccess: (data) => {
                notify('tickets');
                notify(`ticket:${input.id}`);
                notify(`ticket-comments:${input.id}`);
                callbacks?.onSuccess?.(data);
            },
        });

    return { mutate, isPending };
}
