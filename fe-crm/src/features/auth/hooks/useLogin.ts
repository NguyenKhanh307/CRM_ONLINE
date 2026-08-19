import { useLiveMutation } from '@/core/data/useLiveMutation';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@/core/auth/useAuth';
import { authService, type LoginPayload } from '../services/authService';

// đăng nhập — gọi api login, lưu token và điều hướng
export const useLogin = () => {
    const { login } = useAuth();
    const navigate = useNavigate();
    const { mutate: run, isPending } = useLiveMutation((payload: LoginPayload) => authService.login(payload));

    const mutate: typeof run = (payload, callbacks) =>
        run(payload, {
            ...callbacks,
            onSuccess: (res) => {
                const { token, id, email, fullName, avatarUrl, roles, permissions } = res.data.data;
                login(token, { id, email, fullName, avatarUrl, roles, permissions });
                navigate('/', { replace: true });
                callbacks?.onSuccess?.(res);
            },
        });

    return { mutate, isPending };
};
