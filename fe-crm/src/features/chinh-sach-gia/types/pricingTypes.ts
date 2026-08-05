export type PricePolicyStatus = 'active' | 'inactive' | 'expired';
export type DiscountType = 'percent' | 'amount';

// kết quả tra cứu giá theo chính sách giá cho một sản phẩm + số lượng (GET /api/pricing/resolve)
export interface ResolvePriceResult {
    productId: number;
    // đơn giá niêm yết theo chính sách (null nếu không tìm thấy)
    unitPrice: number | null;
    // chiết khấu trên một đơn vị (số tiền)
    discount: number | null;
    // true nếu sản phẩm có trong chính sách giá và số lượng đã đạt ngưỡng
    found: boolean;
    // số lượng tối thiểu của dòng chính sách; null khi sản phẩm không có trong chính sách
    minQty: number | null;
}

export interface PricePolicyResult {
    id: number;
    code: string;
    name: string;
    type: string | null;
    priority: number | null;
    startDate: string | null;
    endDate: string | null;
    status: PricePolicyStatus;
    createdBy: number | null;
    createdAt: string;
    updatedAt: string;
}

export interface CreatePricePolicyPayload {
    code: string;
    name: string;
    type: string | null;
    priority: number | null;
    startDate: string | null;
    endDate: string | null;
    status: PricePolicyStatus;
}

export interface UpdatePricePolicyPayload {
    name: string;
    type: string | null;
    priority: number | null;
    startDate: string | null;
    endDate: string | null;
    status: PricePolicyStatus;
}

export interface PricePolicyProductResult {
    id: number;
    pricePolicyId: number;
    productId: number;
    productName?: string;
    productCode?: string;
    price: number | null;
    discountType: DiscountType | null;
    discountValue: number | null;
    minQty: number | null;
}

export interface CreatePricePolicyProductPayload {
    pricePolicyId: number;
    productId: number;
    price: number | null;
    discountType: DiscountType | null;
    discountValue: number | null;
    minQty: number | null;
}

export interface UpdatePricePolicyProductPayload {
    price: number | null;
    discountType: DiscountType | null;
    discountValue: number | null;
    minQty: number | null;
}

export interface PricePolicyCustomerResult {
    id: number;
    pricePolicyId: number;
    customerId: number;
    customerName?: string;
    customerCode?: string;
}

export interface CreatePricePolicyCustomerPayload {
    pricePolicyId: number;
    customerId: number;
}

// danh mục sản phẩm trong chính sách giá — chỉ là marker "chọn nhanh": chọn 1 danh mục thì be tự
// bulk-seed toàn bộ sản phẩm thuộc danh mục vào price_policy_products (giá để trống), sửa giá
// từng dòng ở tab "Sản phẩm" như bình thường. không mang field giá/chiết khấu nào
export interface PricePolicyProductCategoryResult {
    id: number;
    pricePolicyId: number;
    categoryId: number;
    categoryName?: string;
}

export interface CreatePricePolicyProductCategoryPayload {
    pricePolicyId: number;
    categoryId: number;
}
