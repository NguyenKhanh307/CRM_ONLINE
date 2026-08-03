import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult } from '@/shared/types/api';
import type { DeletedItemRow, TrashModule } from '../types/thungRacTypes';

const ENDPOINTS: Record<TrashModule, string> = {
    'chien-dich': '/api/campaigns',
    'tiem-nang':  '/api/leads',
    'lien-he':    '/api/contacts',
    'khach-hang': '/api/customers',
    'co-hoi':     '/api/opportunities',
    'bao-gia':    '/api/quotations',
    'don-hang':   '/api/orders',
    'hoa-don':    '/api/invoices',
    'san-pham':   '/api/products',
    'cham-soc':   '/api/tickets',
};

export const trashService = {
    getDeleted: (module: TrashModule, params: { page?: number; size?: number }) =>
        axiosInstance.get<ApiResponse<PageResult<DeletedItemRow>>>(
            `${ENDPOINTS[module]}/deleted`, { params }
        ),
    restore: (module: TrashModule, id: number) =>
        axiosInstance.post(`${ENDPOINTS[module]}/${id}/restore`),
    purge: (module: TrashModule, id: number) =>
        axiosInstance.delete(`${ENDPOINTS[module]}/${id}/purge`),
};
