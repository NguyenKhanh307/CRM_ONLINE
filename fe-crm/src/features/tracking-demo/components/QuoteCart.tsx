import { FiShoppingCart, FiX } from 'react-icons/fi';
import type { PublicProduct } from '../types/trackingTypes';

interface Props {
    products: PublicProduct[];
    quantities: Record<number, number>;
    onRemove: (productId: number) => void;
    onRequestQuote: () => void;
}

// bước "giỏ yêu cầu báo giá" — tóm tắt sản phẩm khách đã chọn ở ProductCatalog, chưa gửi
// backend gì cả cho tới khi bấm "Yêu cầu báo giá" (cuộn xuống form liên hệ)
export const QuoteCart = ({ products, quantities, onRemove, onRequestQuote }: Props) => {
    const productMap = new Map(products.map((p) => [p.id, p]));
    const ids = Object.keys(quantities).map(Number);

    return (
        <section className="bg-white rounded-xl shadow-sm border border-gray-200 p-5">
            <div className="flex items-baseline gap-2 mb-1">
                <span className="flex h-6 w-6 items-center justify-center rounded-full bg-blue-600 text-white text-xs font-bold">2</span>
                <h2 className="font-semibold text-gray-800">Sản phẩm đã chọn</h2>
            </div>

            {ids.length === 0 ? (
                <p className="ml-8 py-4 text-sm text-gray-400">
                    Chưa chọn sản phẩm nào — bấm "Chọn báo giá" ở các thẻ sản phẩm phía trên.
                </p>
            ) : (
                <div className="ml-8 space-y-2">
                    <ul className="divide-y divide-gray-100 rounded border border-gray-200">
                        {ids.map((id) => {
                            const p = productMap.get(id);
                            if (!p) return null;
                            return (
                                <li key={id} className="flex items-center gap-3 px-3 py-2 text-sm">
                                    <span className="flex-1 truncate text-gray-700">{p.name}</span>
                                    <span className="text-gray-400">x{quantities[id]}</span>
                                    <button
                                        type="button"
                                        onClick={() => onRemove(id)}
                                        className="text-gray-400 hover:text-red-500"
                                        title="Bỏ chọn"
                                    >
                                        <FiX size={14} />
                                    </button>
                                </li>
                            );
                        })}
                    </ul>
                    <button
                        type="button"
                        onClick={onRequestQuote}
                        className="flex w-full items-center justify-center gap-2 rounded-lg bg-blue-600 py-2.5 text-sm font-semibold text-white hover:bg-blue-700"
                    >
                        <FiShoppingCart size={14} />
                        Yêu cầu báo giá {ids.length} sản phẩm
                    </button>
                </div>
            )}
        </section>
    );
};
