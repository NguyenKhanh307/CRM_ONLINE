import { QueryClient } from '@tanstack/react-query';

/**
 * React Query client dùng chung toàn app.
 * - staleTime 30s: không re-fetch ngay sau khi có data.
 * - retry 1: thử lại 1 lần trước khi báo lỗi.
 */
export const queryClient = new QueryClient({
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
