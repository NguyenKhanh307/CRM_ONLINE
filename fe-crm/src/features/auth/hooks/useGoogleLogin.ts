import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@/core/auth/useAuth';
import { authService, type GoogleLoginPayload } from '../services/authService';

// đăng nhập bằng Google — gửi id token lên BE, lưu session và điều hướng
export const useGoogleLogin = () => {
    const { login } = useAuth();
    const navigate = useNavigate();

    return useMutation({
        mutationFn: (payload: GoogleLoginPayload) => authService.googleLogin(payload),
        onSuccess: ({ data }) => {
            const { token, id, email, fullName, avatarUrl, roles, permissions } = data.data;
            login(token, { id, email, fullName, avatarUrl, roles, permissions });
            navigate('/', { replace: true });
        },
    });
};
