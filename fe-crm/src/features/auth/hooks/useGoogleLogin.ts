import { useLiveMutation } from '@/core/data/useLiveMutation';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@/core/auth/useAuth';
import { authService, type GoogleLoginPayload } from '../services/authService';

// đăng nhập bằng Google — gửi id token lên BE, lưu session và điều hướng
export const useGoogleLogin = () => {
    const { login } = useAuth();
    const navigate = useNavigate();
    const { mutate: run, isPending } = useLiveMutation(
        (payload: GoogleLoginPayload) => authService.googleLogin(payload));

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
