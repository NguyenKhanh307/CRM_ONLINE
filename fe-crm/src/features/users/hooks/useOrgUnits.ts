import { useQuery } from '@tanstack/react-query';
import { orgUnitService } from '../services/orgUnitService';

/** Lấy danh sách đơn vị tổ chức — dùng cho dropdown đơn vị. */
export function useOrgUnits(enabled = true) {
    return useQuery({
        queryKey: ['org-units'],
        queryFn: () => orgUnitService.getList().then((r) => r.data.data.items),
        enabled,
    });
}
