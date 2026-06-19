import { useMutation, useQueryClient } from '@tanstack/react-query';
import { userService } from '../services/userService';
import type { RegisterEmployeePayload } from '../types/userTypes';

/** Đăng ký nhân viên mới — gửi email kích hoạt. */
export function useRegisterEmployee() {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (payload: RegisterEmployeePayload) =>
            userService.registerEmployee(payload),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['users'] });
        },
    });
}
