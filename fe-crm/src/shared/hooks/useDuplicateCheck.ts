import { useEffect, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse } from '@/shared/types/api';
import type { DuplicateCheckParams, DuplicateMatch } from '@/shared/types/duplicate';

// gọi API dò trùng email/SĐT/MST
export const duplicateService = {
    check: (params: DuplicateCheckParams) =>
        axiosInstance.get<ApiResponse<DuplicateMatch[]>>('/api/duplicates/check', { params }),
};

// dò bản ghi trùng email/SĐT/MST khi người dùng đang nhập (debounce 500ms)
// kết quả chỉ để CẢNH BÁO — không chặn lưu; bỏ trống hết tham số thì không gọi API
export function useDuplicateCheck(params: DuplicateCheckParams) {
    const { email, phone, taxCode, excludeModule, excludeId } = params;
    const [debounced, setDebounced] = useState({ email, phone, taxCode });

    useEffect(() => {
        const t = setTimeout(() => setDebounced({ email, phone, taxCode }), 500);
        return () => clearTimeout(t);
    }, [email, phone, taxCode]);

    const hasValue = Boolean(debounced.email || debounced.phone || debounced.taxCode);

    return useQuery({
        queryKey: ['duplicates', debounced, excludeModule, excludeId],
        queryFn: () => duplicateService.check({
            email: debounced.email || undefined,
            phone: debounced.phone || undefined,
            taxCode: debounced.taxCode || undefined,
            excludeModule,
            excludeId,
        }).then(r => r.data.data),
        enabled: hasValue,
        staleTime: 30_000,
    });
}
