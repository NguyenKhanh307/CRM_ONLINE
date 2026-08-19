import { useLiveMutation } from '@/core/data/useLiveMutation';
import { useNavigate } from 'react-router-dom';
import { activationService } from '../services/activationService';
import type { ActivateAccountPayload } from '@/features/users/types/userTypes';

// kích hoạt tài khoản — đặt mật khẩu lần đầu rồi điều hướng /login
export function useActivateAccount() {
    const navigate = useNavigate();
    const { mutate: run, isPending } = useLiveMutation(
        (payload: ActivateAccountPayload) => activationService.activate(payload));

    const mutate: typeof run = (payload, callbacks) =>
        run(payload, {
            ...callbacks,
            onSuccess: (data) => {
                navigate('/login', { replace: true, state: { activated: true } });
                callbacks?.onSuccess?.(data);
            },
        });

    return { mutate, isPending };
}
