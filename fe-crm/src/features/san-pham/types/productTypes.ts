export interface UpdateProductPayload {
    name: string;
    categoryId: number | null;
    type: string;
    unit: string | null;
    basePrice: number | null;
    costPrice: number | null;
    vatRate: number | null;
    description: string | null;
    status: 'active' | 'inactive' | 'discontinued';
}

/** Payload tạo mới sản phẩm — POST /api/products. */
export interface CreateProductPayload {
    sku: string;
    name: string;
    categoryId: number | null;
    type: string;
    unit: string | null;
    basePrice: number | null;
    costPrice: number | null;
    vatRate: number | null;
    description: string | null;
    status: 'active' | 'inactive' | 'discontinued';
}

export interface ProductResult {
    id: number;
    sku: string;
    name: string;
    categoryId: number | null;
    type: string;
    unit: string | null;
    basePrice: number | null;
    costPrice: number | null;
    vatRate: number | null;
    description: string | null;
    status: 'active' | 'inactive' | 'discontinued';
    createdAt: string;
    updatedAt: string;
    // Tên khóa ngoại do BE resolve sẵn (INameResolver).
    categoryName: string | null;
    // Audit: BE tự đóng dấu (AuditInterceptor).
    createdBy: number | null;
    updatedBy: number | null;
    createdByName: string | null;
    updatedByName: string | null;
}
