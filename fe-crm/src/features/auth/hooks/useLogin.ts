import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@/core/auth/useAuth';
import { authService, type LoginPayload } from '../services/authService';

/**
 * Mutation hook xử lý đăng nhập.
 * Khi thành công: lưu session qua AuthContext rồi redirect về trang chủ.
 */
export const useLogin = () => {
    const { login } = useAuth();
    const navigate = useNavigate();

    return useMutation({
        mutationFn: (payload: LoginPayload) => authService.login(payload),
        onSuccess: ({ data }) => {
            login(data.token, data.user);
            navigate('/', { replace: true });
        },
    });
};
