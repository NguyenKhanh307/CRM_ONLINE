import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { activationService } from '../services/activationService';
import type { ActivateAccountPayload } from '@/features/users/types/userTypes';

export function useActivateAccount() {
    const navigate = useNavigate();
    return useMutation({
        mutationFn: (payload: ActivateAccountPayload) =>
            activationService.activate(payload),
        onSuccess: () => {
            navigate('/login', { replace: true, state: { activated: true } });
        },
    });
}
