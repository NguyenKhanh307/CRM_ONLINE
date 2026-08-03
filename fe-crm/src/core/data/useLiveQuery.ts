import { useEffect, useRef, useState } from 'react';
import { subscribe } from './dataBus';

// thay useQuery — tự tải dữ liệu khi mount/đổi key, tự tải lại khi có notify() cùng key.
// enabled=false: chưa có đủ điều kiện để gọi api (vd id chưa xác định) — không tải, không loading.
export function useLiveQuery<T>(key: string, fetcher: () => Promise<T>, enabled = true) {
    const [data, setData] = useState<T | undefined>(undefined);
    const [isLoading, setIsLoading] = useState(enabled);
    const fetcherRef = useRef(fetcher);
    fetcherRef.current = fetcher; // luôn gọi bản fetcher mới nhất, không cần đưa vào dependency

    // bước tải dữ liệu — không xóa data cũ khi bắt đầu tải, tránh nháy trắng lúc đổi trang
    const load = () => {
        setIsLoading(true);
        fetcherRef.current().then(setData).finally(() => setIsLoading(false));
    };

    useEffect(() => {
        if (!enabled) return;
        load();
        // bước đăng ký nghe thông báo đổi dữ liệu
        return subscribe(key, load);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [key, enabled]);

    return { data, isLoading, refetch: load };
}
