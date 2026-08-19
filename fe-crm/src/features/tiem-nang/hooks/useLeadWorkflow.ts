import type { AxiosResponse } from 'axios';
import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { leadService } from '../services/leadService';
import type { ApiResponse } from '@/shared/types/api';
import type { LeadResult } from '../types/leadTypes';
import type { CustomerResult } from '@/features/khach-hang/types/customerTypes';

// loại hành động chuyển trạng thái tiềm năng
export type LeadAction = 'claim' | 'convert';

interface WorkflowInput {
    id: number;
    action: LeadAction;
    reason?: string;
}

// claim trả về tiềm năng, convert trả về khách hàng vừa tạo — hai kiểu response khác nhau
type WorkflowOutput = AxiosResponse<ApiResponse<LeadResult>> | AxiosResponse<ApiResponse<CustomerResult>>;

// chạy hành động chuyển trạng thái tiềm năng: claim (nhân viên tự nhận chăm sóc) / convert
// (chuyển thành khách hàng chính thức — tạo Khách hàng, re-link Cơ hội, xóa mềm tiềm năng)
export function useLeadWorkflow() {
    const { mutate: run, isPending } = useLiveMutation<WorkflowInput, WorkflowOutput>((input) => {
        switch (input.action) {
            case 'claim': return leadService.claim(input.id);
            case 'convert': return leadService.convert(input.id);
        }
    });

    const mutate: typeof run = (input, callbacks) =>
        run(input, {
            ...callbacks,
            onSuccess: (data) => {
                notify('leads');
                notify(`lead:${input.id}`);
                if (input.action === 'convert') {
                    notify('customers');
                    notify('opportunities');
                }
                callbacks?.onSuccess?.(data);
            },
        });

    return { mutate, isPending };
}
