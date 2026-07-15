import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@/core/auth/useAuth';
import { authService, type GoogleLoginPayload } from '../services/authService';

/** Đăng nhập bằng Google — gửi ID token lên BE, lưu session và điều hướng. */
export const useGoogleLogin = () => {
    const { login } = useAuth();
    const navigate = useNavigate();

    return useMutation({
        mutationFn: (payload: GoogleLoginPayload) => authService.googleLogin(payload),
        onSuccess: ({ data }) => {
            const { token, id, email, fullName, roles, permissions } = data.data;
            login(token, { id, email, fullName, roles, permissions });
            navigate('/', { replace: true });
        },
    });
};
