import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { userService } from '../services/userService';
import type { RegisterEmployeePayload } from '../types/userTypes';

// đăng ký nhân viên mới — gửi email kích hoạt, báo danh sách người dùng làm mới sau khi thành công
export function useRegisterEmployee() {
    const { mutate: run, isPending } = useLiveMutation(
        (payload: RegisterEmployeePayload) => userService.registerEmployee(payload));

    const mutate: typeof run = (payload, callbacks) =>
        run(payload, { ...callbacks, onSuccess: (data) => { notify('users'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
