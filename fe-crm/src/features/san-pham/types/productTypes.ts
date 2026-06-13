export interface UpdateProductPayload {
    name: string;
    categoryId: number | null;
    type: string;
    unit: string | null;
    basePrice: number | null;
    costPrice: number | null;
    vatRate: number | null;
    barcode: string | null;
    description: string | null;
    isDiscontinued: boolean;
    isActive: boolean;
}

/** Payload tạo mới sản phẩm — POST /api/products. */
export interface CreateProductPayload {
    sku: string;
    name: string;
    categoryId: number | null;
    type: string;
    unit: string | null;
    secondaryUnit: string | null;
    conversionRate: number | null;
    composition: string | null;
    yarnCount: string | null;
    color: string | null;
    fabricWidth: number | null;
    weightGsm: number | null;
    brand: string | null;
    origin: string | null;
    basePrice: number | null;
    costPrice: number | null;
    vatRate: number | null;
    barcode: string | null;
    description: string | null;
    isDiscontinued: boolean;
    isActive: boolean;
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
    barcode: string | null;
    description: string | null;
    isDiscontinued: boolean;
    isActive: boolean;
    createdAt: string;
    updatedAt: string;
}
