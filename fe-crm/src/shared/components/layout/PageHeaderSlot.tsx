import { useEffect, useState, type ReactNode } from 'react';
import { createPortal } from 'react-dom';

/**
 * Đẩy tiêu đề + nhóm nút hành động của trang lên khoảng trống giữa `Header` chung.
 * Dùng portal (không phải Context) để nhóm nút giữ nguyên state/handler của page.
 *
 * Render đầu tiên trả `null` vì host chỉ tìm được sau khi Header đã mount;
 * effect chạy ngay sau commit nên slot được lấp ở frame kế tiếp.
 */
export const PageHeaderSlot = ({ children }: { children: ReactNode }) => {
    const [host, setHost] = useState<HTMLElement | null>(null);

    useEffect(() => setHost(document.getElementById('page-header-slot')), []);

    return host ? createPortal(children, host) : null;
};
