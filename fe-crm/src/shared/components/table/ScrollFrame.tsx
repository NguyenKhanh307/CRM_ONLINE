import type { CSSProperties, ReactNode } from 'react';
import { tableScrollMaxHeight } from './tableMetrics';

interface ScrollFrameProps {
    /** Số dòng thấy được mà không cần cuộn (bỏ qua nếu truyền `maxHeight`). */
    visibleRows?: number;
    /** Chiều cao tối đa (px) — dùng khi dòng cao không đều (timeline, danh sách thẻ). */
    maxHeight?: number;
    /** Nền header dính — phải khớp nền bảng, nếu không sẽ thấy dòng chạy xuyên qua header. */
    headBg?: 'white' | 'gray';
    className?: string;
    children: ReactNode;
}

/**
 * Header dính của bảng bên trong khung cuộn.
 * Viết literal (không nội suy chuỗi) vì Tailwind quét class tĩnh trong mã nguồn —
 * class dựng bằng template string sẽ không được sinh ra.
 */
const STICKY_HEAD: Record<'white' | 'gray', string> = {
    white: '[&_thead_th]:sticky [&_thead_th]:top-0 [&_thead_th]:z-10 [&_thead_th]:bg-white [&_thead_th]:shadow-[inset_0_-1px_0_0_#e5e7eb]',
    gray: '[&_thead_th]:sticky [&_thead_th]:top-0 [&_thead_th]:z-10 [&_thead_th]:bg-gray-100 [&_thead_th]:shadow-[inset_0_-2px_0_0_#d1d5db]',
};

/**
 * Khung cuộn riêng cho một khối danh sách trên trang chi tiết.
 * Danh sách dài cuộn trong khung của nó thay vì kéo dài trang và đẩy thanh cuộn chính.
 *
 * Header của bảng bên trong tự dính — component dùng không phải sửa từng thẻ `<th>`.
 * Dùng `shadow` inset thay `border-b` vì `border-collapse` vẽ viền theo bảng nên viền
 * trôi mất khi cuộn (bẫy đã gặp ở `RecordItemsPanel`).
 */
export const ScrollFrame = ({
    visibleRows = 10,
    maxHeight,
    headBg = 'white',
    className = '',
    children,
}: ScrollFrameProps) => {
    const style: CSSProperties = { maxHeight: maxHeight ?? tableScrollMaxHeight(visibleRows) };

    return (
        <div className={`overflow-auto ${STICKY_HEAD[headBg]} ${className}`} style={style}>
            {children}
        </div>
    );
};
