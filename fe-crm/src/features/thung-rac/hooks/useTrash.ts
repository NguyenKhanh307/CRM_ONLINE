import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { trashService } from '../services/trashService';
import type { TrashModule } from '../types/thungRacTypes';

// map route-slug của thùng rác sang queryKey danh sách chính của module (dùng khi khôi phục)
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
    return useQuery({
        queryKey: ['trash', module, page, size],
        queryFn: () =>
            trashService.getDeleted(module, { page, size }).then(r => r.data.data),
    });
}

// khôi phục bản ghi đã xóa từ thùng rác — làm mới cả tab thùng rác lẫn danh sách chính (bản ghi hiện lại)
export function useRestore(module: TrashModule) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => trashService.restore(module, id),
        onSuccess: () => {
            qc.invalidateQueries({ queryKey: ['trash', module] });
            qc.invalidateQueries({ queryKey: [TRASH_TO_LIST_KEY[module]] });
        },
    });
}

// xóa vĩnh viễn bản ghi khỏi thùng rác
export function usePurge(module: TrashModule) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => trashService.purge(module, id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['trash', module] }),
    });
}
