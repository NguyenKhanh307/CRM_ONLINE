import { useMemo } from 'react';
import { useProductList } from './useProductList';
import type { ProductResult } from '../types/productTypes';

/**
 * Map productId → sản phẩm, dùng để tra Mã hàng/Tên hàng/ĐVT cho bảng dòng hàng
 * (item backend chỉ trả `productId`).
 */
export function useProductMap(): Map<number, ProductResult> {
    const { data = [] } = useProductList();
    return useMemo(() => new Map(data.map(p => [p.id, p])), [data]);
}
