import { QueryClient, MutationCache } from '@tanstack/react-query';
import { showGlobalAlert } from '@/shared/alert/alertBridge';

// đọc message lỗi theo format ApiResponse của backend từ AxiosError
const extractErrorMessage = (error: unknown): string => {
    const err = error as { response?: { status?: number; data?: { message?: string } } };
    return err?.response?.data?.message ?? 'Có lỗi xảy ra, vui lòng thử lại.';
};

// React Query client dùng chung toàn app
// - staleTime 30s: không re-fetch ngay sau khi có data
// - retry 1: thử lại 1 lần trước khi báo lỗi
// - MutationCache.onError: toast lỗi toàn cục — mutation lỗi không bao giờ bị nuốt lặng lẽ.
//   Opt-out cho luồng có UI lỗi riêng: useMutation({ meta: { suppressGlobalError: true } }).
//   Lỗi 401 bỏ qua (axios interceptor đã redirect /login)
export const queryClient = new QueryClient({
    mutationCache: new MutationCache({
        onError: (error, _variables, _context, mutation) => {
            if (mutation.meta?.suppressGlobalError) return;
            const status = (error as { response?: { status?: number } })?.response?.status;
            if (status === 401) return;
            showGlobalAlert(extractErrorMessage(error));
        },
    }),
    defaultOptions: {
        queries: {
            staleTime: 30_000,
            retry: 1,
            refetchOnWindowFocus: false,
        },
        mutations: {
            retry: 0,
        },
    },
});
