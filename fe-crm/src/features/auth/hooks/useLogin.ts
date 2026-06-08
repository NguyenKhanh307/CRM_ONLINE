import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@/core/auth/useAuth';
import { authService, type LoginPayload } from '../services/authService';

export const useLogin = () => {
    const { login } = useAuth();
    const navigate = useNavigate();

    return useMutation({
        mutationFn: (payload: LoginPayload) => authService.login(payload),
        onSuccess: ({ data }) => {
            const { token, id, email, fullName, roles } = data.data;
            login(token, { id, email, fullName, roles });
            navigate('/', { replace: true });
        },
    });
};
