import axios from 'axios';
import { useLiveQuery } from '@/core/data/useLiveQuery';
import type { SelectOption } from '@/shared/components/SearchableSelect';

interface VietQrBank {
    name: string;
    shortName: string;
    code: string;
}

// danh sách ngân hàng VN cho ô chọn "Ngân hàng" (đợt thanh toán hóa đơn) — API công khai VietQR,
// không cần key. Gọi thẳng axios (KHÔNG dùng axiosInstance — instance đó gắn baseURL trỏ về backend
// nội bộ). Lỗi mạng -> trả mảng rỗng, không throw (degrade an toàn, ô vẫn hiện chỉ tạm không chọn
// được ngân hàng mới) — danh sách gần như không đổi, không có staleTime nên mỗi lần mount sẽ gọi lại.
export function useVietQrBanks() {
    const { data, isFetching } = useLiveQuery('vietqr-banks', async (): Promise<SelectOption[]> => {
        try {
            const r = await axios.get<{ data: VietQrBank[] }>('https://api.vietqr.io/v2/banks');
            return r.data.data.map((b) => ({ value: b.shortName, label: b.shortName }));
        } catch {
            return [];
        }
    });
    return { options: data ?? [], loading: isFetching };
}
