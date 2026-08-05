import { FiCheck, FiInfo, FiX } from 'react-icons/fi';
import { formatCurrency } from '@/shared/utils/number';
import type { PublicProduct } from '../types/trackingTypes';

interface Props {
    product: PublicProduct;
    selected: boolean;
    quantity?: number;
    onToggle: (productId: number) => void;
    onClose: () => void;
}

// popup chi tiết sản phẩm — mở khi khách click vào một thẻ sản phẩm trên landing page.
// Việc mở popup này đã tự động ghi sản phẩm vào lead_items (interestType=viewed) ở phía gọi,
// popup chỉ hiển thị lại chi tiết + giải thích hệ quả bên trong CRM
export const ProductDetailModal = ({ product, selected, quantity, onToggle, onClose }: Props) => (
    <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40 px-4" onClick={onClose}>
        <div
            className="w-full max-w-md rounded-xl bg-white shadow-lg"
            onClick={(e) => e.stopPropagation()}
        >
            <div className="flex items-center justify-between border-b border-gray-200 px-5 py-4">
                <h2 className="font-semibold text-gray-800">Bạn đã xem: {product.name}</h2>
                <button onClick={onClose} className="rounded p-1 text-gray-500 hover:bg-gray-100">
                    <FiX size={18} />
                </button>
            </div>

            <div className="space-y-2 px-5 py-4 text-sm">
                <Row label="Mã SKU" value={product.sku} />
                <Row label="Danh mục" value={product.categoryName ?? '—'} />
                <Row label="Đơn vị tính" value={product.unit ?? '—'} />
                <Row label="Giá bán" value={formatCurrency(product.basePrice)} />
                {product.description && (
                    <div>
                        <div className="text-gray-500">Mô tả</div>
                        <p className="mt-0.5 text-gray-700">{product.description}</p>
                    </div>
                )}
            </div>

            <div className="mx-5 mb-4 flex gap-2 rounded-lg border border-blue-200 bg-blue-50/60 p-3 text-xs text-blue-800">
                <FiInfo className="mt-0.5 shrink-0" size={14} />
                <p>
                    Thao tác này mô phỏng việc tiềm năng xem sản phẩm trên landing page thật — hệ thống đã lưu
                    sản phẩm này vào hồ sơ tiềm năng (mục "Sản phẩm quan tâm"). Khi bạn bấm "Yêu cầu báo giá",
                    sale và quản lý phụ trách sẽ nhận được thông báo trong CRM kèm danh sách mặt hàng bạn muốn báo giá.
                </p>
            </div>

            <div className="flex items-center justify-between gap-2 border-t border-gray-200 px-5 py-4">
                <button
                    type="button"
                    onClick={onClose}
                    className="rounded-lg border border-gray-300 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
                >
                    Đóng
                </button>
                <button
                    type="button"
                    onClick={() => onToggle(product.id)}
                    className={`flex items-center gap-1.5 rounded-lg px-4 py-2 text-sm font-semibold transition-colors ${
                        selected ? 'bg-blue-600 text-white hover:bg-blue-700' : 'border border-blue-400 text-blue-600 hover:bg-blue-50'
                    }`}
                >
                    {selected ? <FiCheck size={14} /> : null}
                    {selected ? `Đã chọn (x${quantity ?? 1})` : 'Chọn báo giá'}
                </button>
            </div>
        </div>
    </div>
);

const Row = ({ label, value }: { label: string; value: string }) => (
    <div className="flex items-center justify-between gap-3">
        <span className="text-gray-500">{label}</span>
        <span className="font-medium text-gray-800">{value}</span>
    </div>
);
