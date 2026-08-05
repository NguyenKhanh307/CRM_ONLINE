import { useMemo, useState } from 'react';
import { FiCheck, FiMinus, FiPlus, FiSearch } from 'react-icons/fi';
import { ScrollFrame } from '@/shared/components/table/ScrollFrame';
import { formatCurrency } from '@/shared/utils/number';
import { ProductDetailModal } from './ProductDetailModal';
import type { PublicProduct } from '../types/trackingTypes';

interface Props {
    products: PublicProduct[];
    isLoading: boolean;
    quantities: Record<number, number>;
    onToggle: (productId: number) => void;
    onQuantityChange: (productId: number, quantity: number) => void;
    // ghi nhận lượt xem chi tiết sản phẩm (mở popup) vào hồ sơ tiềm năng
    onView: (productId: number) => void;
}

// bước "duyệt sản phẩm" của landing page — khách chọn sản phẩm quan tâm để đưa vào giỏ yêu cầu
// báo giá; hiển thị dạng thẻ, không có ảnh minh họa vì `products` chưa có cột image_url
export const ProductCatalog = ({ products, isLoading, quantities, onToggle, onQuantityChange, onView }: Props) => {
    const [q, setQ] = useState('');
    const [category, setCategory] = useState('');
    // sản phẩm đang mở popup chi tiết
    const [viewingProduct, setViewingProduct] = useState<PublicProduct | null>(null);

    const openDetail = (p: PublicProduct) => {
        setViewingProduct(p);
        onView(p.id);
    };

    const categories = useMemo(
        () => Array.from(new Set(products.map((p) => p.categoryName).filter((c): c is string => !!c))).sort(),
        [products],
    );

    const filtered = useMemo(() => {
        const kw = q.trim().toLowerCase();
        return products.filter((p) => {
            if (category && p.categoryName !== category) return false;
            if (!kw) return true;
            return p.name.toLowerCase().includes(kw) || p.sku.toLowerCase().includes(kw);
        });
    }, [products, q, category]);

    return (
        <section className="bg-white rounded-xl shadow-sm border border-gray-200 p-5">
            <div className="flex items-baseline gap-2 mb-1">
                <span className="flex h-6 w-6 items-center justify-center rounded-full bg-blue-600 text-white text-xs font-bold">1</span>
                <h2 className="font-semibold text-gray-800">Sản phẩm &amp; dịch vụ</h2>
            </div>
            <p className="ml-8 mb-3 text-sm text-gray-500">
                Chọn những sản phẩm bạn quan tâm — mỗi lượt chọn sẽ được gắn thẳng vào hồ sơ tiềm năng của bạn trong CRM.
            </p>

            <div className="ml-8 mb-3 flex flex-col gap-2 sm:flex-row">
                <div className="relative flex-1">
                    <FiSearch className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={14} />
                    <input
                        value={q}
                        onChange={(e) => setQ(e.target.value)}
                        placeholder="Tìm theo tên hoặc mã SKU..."
                        className="w-full rounded border border-gray-300 py-2 pl-8 pr-3 text-sm focus:border-blue-500 focus:outline-none"
                    />
                </div>
                {categories.length > 0 && (
                    <select
                        value={category}
                        onChange={(e) => setCategory(e.target.value)}
                        className="rounded border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none"
                    >
                        <option value="">Tất cả danh mục</option>
                        {categories.map((c) => (
                            <option key={c} value={c}>{c}</option>
                        ))}
                    </select>
                )}
            </div>

            <div className="ml-8">
                {isLoading ? (
                    <p className="py-6 text-center text-sm text-gray-400">Đang tải sản phẩm...</p>
                ) : filtered.length === 0 ? (
                    <p className="py-6 text-center text-sm text-gray-400">Không tìm thấy sản phẩm phù hợp.</p>
                ) : (
                    <ScrollFrame maxHeight={420}>
                        <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
                            {filtered.map((p) => {
                                const selected = quantities[p.id] != null;
                                return (
                                    <div
                                        key={p.id}
                                        onClick={() => openDetail(p)}
                                        className={`cursor-pointer rounded-lg border p-3 transition-colors ${
                                            selected ? 'border-blue-400 bg-blue-50/60' : 'border-gray-200'
                                        }`}
                                    >
                                        <div className="flex items-start justify-between gap-2">
                                            <div className="min-w-0">
                                                <div className="truncate font-medium text-gray-800">{p.name}</div>
                                                <div className="text-xs text-gray-400">
                                                    {p.sku}{p.categoryName ? ` · ${p.categoryName}` : ''}
                                                </div>
                                            </div>
                                            <div className="shrink-0 text-right text-sm font-semibold text-blue-600">
                                                {formatCurrency(p.basePrice)}
                                                {p.unit && <span className="block text-xs font-normal text-gray-400">/{p.unit}</span>}
                                            </div>
                                        </div>
                                        {p.description && (
                                            <p className="mt-1 line-clamp-2 text-xs text-gray-500">{p.description}</p>
                                        )}
                                        <div className="mt-2 flex items-center justify-between" onClick={(e) => e.stopPropagation()}>
                                            <button
                                                type="button"
                                                onClick={() => onToggle(p.id)}
                                                className={`flex items-center gap-1.5 rounded-lg px-3 py-1.5 text-xs font-semibold transition-colors ${
                                                    selected
                                                        ? 'bg-blue-600 text-white hover:bg-blue-700'
                                                        : 'border border-gray-300 text-gray-700 hover:border-blue-400 hover:bg-blue-50'
                                                }`}
                                            >
                                                {selected ? <FiCheck size={12} /> : null}
                                                {selected ? 'Đã chọn' : 'Chọn báo giá'}
                                            </button>
                                            {selected && (
                                                <div className="flex items-center gap-1">
                                                    <button
                                                        type="button"
                                                        onClick={() => onQuantityChange(p.id, (quantities[p.id] ?? 1) - 1)}
                                                        className="flex h-6 w-6 items-center justify-center rounded border border-gray-300 text-gray-500 hover:bg-gray-50"
                                                    >
                                                        <FiMinus size={10} />
                                                    </button>
                                                    <span className="w-6 text-center text-sm text-gray-700">{quantities[p.id]}</span>
                                                    <button
                                                        type="button"
                                                        onClick={() => onQuantityChange(p.id, (quantities[p.id] ?? 1) + 1)}
                                                        className="flex h-6 w-6 items-center justify-center rounded border border-gray-300 text-gray-500 hover:bg-gray-50"
                                                    >
                                                        <FiPlus size={10} />
                                                    </button>
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                );
                            })}
                        </div>
                    </ScrollFrame>
                )}
            </div>

            {viewingProduct && (
                <ProductDetailModal
                    product={viewingProduct}
                    selected={quantities[viewingProduct.id] != null}
                    quantity={quantities[viewingProduct.id]}
                    onToggle={onToggle}
                    onClose={() => setViewingProduct(null)}
                />
            )}
        </section>
    );
};
