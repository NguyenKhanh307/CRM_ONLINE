import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { ticketService } from '../services/ticketService';

// xóa mềm phiếu — báo danh sách làm mới sau khi thành công
export function useDeleteTicket() {
    const { mutate: run, isPending } = useLiveMutation((id: number) => ticketService.remove(id));

    const mutate: typeof run = (id, callbacks) =>
        run(id, { ...callbacks, onSuccess: (data) => { notify('tickets'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
