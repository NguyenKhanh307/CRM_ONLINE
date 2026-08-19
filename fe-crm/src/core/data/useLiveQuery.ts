import { useEffect, useRef, useState } from 'react';
import { subscribe } from './dataBus';

// hook tải dữ liệu từ API, tự động lắng nghe thông báo đổi dữ liệu và polling định kỳ
// polling dùng để refresh dữ liệu khi có người khác thay đổi, tránh stale data. Nếu không muốn polling thì không truyền pollMs
export function useLiveQuery<T>(key: string, fetcher: () => Promise<T>, enabled = true, pollMs?: number) {
    const [data, setData] = useState<T | undefined>(undefined);
    const [isFetching, setIsFetching] = useState(enabled);
    const [error, setError] = useState<unknown>(undefined);
    const hasLoadedRef = useRef(false); // đã có data lần nào chưa — phân biệt tải lần đầu với tải lại
    const fetcherRef = useRef(fetcher);
    fetcherRef.current = fetcher; // luôn gọi bản fetcher mới nhất, không cần đưa vào dependency

    // bước tải dữ liệu — không xóa data cũ khi bắt đầu tải, tránh nháy trắng lúc đổi trang
    const load = () => {
        setIsFetching(true);
        // gọi fetcher, nếu thành công thì setData, nếu lỗi thì setError
        fetcherRef.current()
            .then((d) => { setData(d); setError(undefined); hasLoadedRef.current = true; })
            .catch((err) => setError(err))
            .finally(() => setIsFetching(false));
    };

    useEffect(() => {
        if (!enabled) return;
        load();
        // bước đăng ký nghe thông báo đổi dữ liệu
        const unsubscribe = subscribe(key, load);
        // bước polling định kỳ (chỉ khi có truyền pollMs)
        const timer = pollMs ? setInterval(load, pollMs) : undefined;
        return () => { unsubscribe(); if (timer) clearInterval(timer); };
    }, [key, enabled, pollMs]);

    // isLoading: chỉ true ở lần tải đầu tiên (chưa từng có data) — tải lại về sau chỉ báo qua isFetching
    return { data, isLoading: isFetching && !hasLoadedRef.current, isFetching, isError: error !== undefined, error, refetch: load };
}
