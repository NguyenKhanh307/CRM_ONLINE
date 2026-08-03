import { useState } from 'react';
import { useAlert } from '@/shared/alert/useAlert';

// đọc message lỗi theo format ApiResponse của backend từ AxiosError
function extractErrorMessage(err: unknown): string {
    const e = err as { response?: { status?: number; data?: { message?: string } } };
    return e?.response?.data?.message ?? 'Có lỗi xảy ra, vui lòng thử lại.';
}

export interface MutateCallbacks<TOutput> {
    onSuccess?: (data: TOutput) => void;
    onError?: (err: unknown) => void;
}

// thay useMutation — gọi API ghi dữ liệu. Không truyền onError riêng thì tự hiện toast lỗi
// chung (trừ lỗi 401, axios đã tự chuyển sang trang đăng nhập rồi nên khỏi báo thêm)
export function useLiveMutation<TInput, TOutput>(mutationFn: (input: TInput) => Promise<TOutput>) {
    const [isPending, setIsPending] = useState(false);
    const { showAlert } = useAlert();

    async function mutate(input: TInput, callbacks?: MutateCallbacks<TOutput>): Promise<TOutput> {
        setIsPending(true);
        try {
            // bước gọi api
            const data = await mutationFn(input);
            callbacks?.onSuccess?.(data);
            return data;
        } catch (err) {
            // bước báo lỗi
            const status = (err as { response?: { status?: number } })?.response?.status;
            if (callbacks?.onError) callbacks.onError(err);
            else if (status !== 401) showAlert(extractErrorMessage(err));
            throw err;
        } finally {
            setIsPending(false);
        }
    }

    return { mutate, isPending };
}
