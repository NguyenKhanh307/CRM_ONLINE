import { isEmpty } from './common';

// TẤT CẢ trường hợp báo lỗi liên quan tới giá bán/giá vốn trong app đều nằm ở đây.

// giá bán không được nhỏ hơn giá vốn
export const sellPriceError = (
    basePrice: string | number | null | undefined,
    costPrice: string | number | null | undefined,
): string | null => {
    if (isEmpty(basePrice) || isEmpty(costPrice)) return null;
    return Number(basePrice) < Number(costPrice) ? 'Giá bán không được nhỏ hơn giá vốn' : null;
};
