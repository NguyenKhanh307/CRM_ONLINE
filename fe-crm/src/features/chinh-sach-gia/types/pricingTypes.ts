export type PricePolicyStatus = 'active' | 'inactive' | 'expired';
export type DiscountType = 'percent' | 'amount';

/** Kết quả tra cứu giá theo chính sách giá cho một sản phẩm + số lượng (GET /api/pricing/resolve). */
export interface ResolvePriceResult {
    productId: number;
    /** Đơn giá niêm yết theo chính sách (null nếu không tìm thấy). */
    unitPrice: number | null;
    /** Chiết khấu trên một đơn vị (số tiền). */
    discount: number | null;
    /** true nếu sản phẩm có trong chính sách giá. */
    found: boolean;
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

export interface PricePolicyCustomerCategoryResult {
    id: number;
    pricePolicyId: number;
    categoryId: number;
    categoryName?: string;
}

export interface CreatePricePolicyCustomerCategoryPayload {
    pricePolicyId: number;
    categoryId: number;
}

export interface PricePolicyProductTypeResult {
    id: number;
    pricePolicyId: number;
    productTypeId: number;
    productTypeName?: string;
}

export interface CreatePricePolicyProductTypePayload {
    pricePolicyId: number;
    productTypeId: number;
}

export interface PricePolicyEmployeeResult {
    id: number;
    pricePolicyId: number;
    userId: number | null;
    unitId: number | null;
    userName?: string;
    unitName?: string;
}

export interface CreatePricePolicyEmployeePayload {
    pricePolicyId: number;
    userId: number | null;
    unitId: number | null;
}
