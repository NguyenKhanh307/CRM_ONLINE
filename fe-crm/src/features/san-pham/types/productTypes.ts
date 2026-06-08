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
