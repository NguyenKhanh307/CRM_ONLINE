import { useLiveQuery } from '@/core/data/useLiveQuery';
import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { trashService } from '../services/trashService';
import type { TrashModule } from '../types/thungRacTypes';

// map route-slug của thùng rác sang key danh sách chính của module (dùng khi khôi phục)
const TRASH_TO_LIST_KEY: Record<TrashModule, string> = {
    'chien-dich': 'campaigns',
    'tiem-nang': 'leads',
    'lien-he': 'contacts',
    'khach-hang': 'customers',
    'co-hoi': 'opportunities',
    'bao-gia': 'quotations',
    'don-hang': 'orders',
    'hoa-don': 'invoices',
    'san-pham': 'products',
    'cham-soc': 'tickets',
};

// lấy danh sách bản ghi đã xóa của một module (phân trang)
export function useDeletedItems(module: TrashModule, page = 0, size = 20) {
    return useLiveQuery(
        `trash:${module}:${page}:${size}`,
        () => trashService.getDeleted(module, { page, size }).then(r => r.data.data),
    );
}

// khôi phục bản ghi đã xóa từ thùng rác — báo mới cả tab thùng rác lẫn danh sách chính (bản ghi hiện lại)
export function useRestore(module: TrashModule) {
    const { mutate: run, isPending } = useLiveMutation((id: number) => trashService.restore(module, id));

    const mutate: typeof run = (id, callbacks) =>
        run(id, {
            ...callbacks,
            onSuccess: (data) => {
                notify(`trash:${module}`);
                notify(TRASH_TO_LIST_KEY[module]);
                callbacks?.onSuccess?.(data);
            },
        });

    return { mutate, isPending };
}

// xóa vĩnh viễn bản ghi khỏi thùng rác — báo tab thùng rác làm mới sau khi thành công
export function usePurge(module: TrashModule) {
    const { mutate: run, isPending } = useLiveMutation((id: number) => trashService.purge(module, id));

    const mutate: typeof run = (id, callbacks) =>
        run(id, { ...callbacks, onSuccess: (data) => { notify(`trash:${module}`); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
