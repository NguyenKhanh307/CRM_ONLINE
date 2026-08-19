import { useLiveQuery } from '@/core/data/useLiveQuery';
import { slaPolicyService } from '../services/slaPolicyService';

// lấy danh sách chính sách SLA
export function useSlaPolicies() {
    return useLiveQuery('sla-policies', () => slaPolicyService.getList().then((r) => r.data.data));
}
