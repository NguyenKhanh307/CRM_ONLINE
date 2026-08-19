import { useState } from 'react';
import { FiPlus } from 'react-icons/fi';
import { ActionButton } from '@/shared/components/ActionButton';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { formatISODate } from '@/shared/utils/date';
import { useProductList } from '@/features/san-pham/hooks/useProductList';
import { useProductMap } from '@/features/san-pham/hooks/useProductMap';
import { useLeadItems } from '../hooks/useLeadItems';
import { leadService } from '../services/leadService';

interface Props {
    leadId: number;
}

const INTEREST_LABELS: Record<string, string> = {
    viewed: 'Đã xem',
    requested_quote: 'Yêu cầu báo giá',
};

// tab "Sản phẩm quan tâm" ở trang chi tiết Tiềm năng — bảng lead_items (chỉ tạo + liệt kê)
export const LeadItemsPanel = ({ leadId }: Props) => {
    const { data: items = [], isLoading } = useLeadItems(leadId);
    const { data: products = [] } = useProductList();
    const productMap = useProductMap();
    const { mutate: createItem, isPending } = useLiveMutation(
        (payload: { productId: number; interestType: 'viewed' | 'requested_quote'; note?: string | null }) =>
            leadService.createItem(leadId, payload),
    );

    const [adding, setAdding] = useState(false);
    const [productId, setProductId] = useState('');
    const [interestType, setInterestType] = useState<'viewed' | 'requested_quote'>('viewed');
    const [note, setNote] = useState('');

    const productOptions = products.map(p => ({ value: String(p.id), label: `${p.sku} — ${p.name}` }));

    const submit = () => {
        if (!productId) return;
        createItem(
            { productId: Number(productId), interestType, note: note || null },
            {
                onSuccess: () => {
                    notify(`lead-items:${leadId}`);
                    setAdding(false);
                    setProductId('');
                    setInterestType('viewed');
                    setNote('');
                },
            },
        );
    };

    return (
        <div className="space-y-3">
            {!adding ? (
                <ActionButton variant="secondary" icon={FiPlus} onClick={() => setAdding(true)}>
                    Thêm sản phẩm quan tâm
                </ActionButton>
            ) : (
                <div className="border border-gray-200 rounded-btn p-3 space-y-2">
                    <div className="grid grid-cols-2 gap-2">
                        <SearchableSelect value={productId} onChange={setProductId} options={productOptions} placeholder="Chọn sản phẩm" />
                        <SearchableSelect
                            value={interestType}
                            onChange={v => setInterestType((v || 'viewed') as 'viewed' | 'requested_quote')}
                            options={[{ value: 'viewed', label: 'Đã xem' }, { value: 'requested_quote', label: 'Yêu cầu báo giá' }]}
                        />
                    </div>
                    <input
                        className="w-full border border-gray-300 rounded-btn px-3 py-1.5 text-md text-text-main focus:outline-none focus:border-primary"
                        placeholder="Ghi chú (tùy chọn)"
                        value={note}
                        onChange={e => setNote(e.target.value)}
                    />
                    <div className="flex justify-end gap-1.5">
                        <ActionButton onClick={() => setAdding(false)}>Hủy</ActionButton>
                        <ActionButton variant="primary" disabled={!productId || isPending} onClick={submit}>Lưu</ActionButton>
                    </div>
                </div>
            )}

            {isLoading ? (
                <div className="text-sm text-gray-400 py-6 text-center">Đang tải...</div>
            ) : items.length === 0 ? (
                <div className="text-sm text-gray-400 py-6 text-center">Chưa có sản phẩm quan tâm nào</div>
            ) : (
                <table className="w-full text-sm">
                    <thead>
                        <tr className="text-left text-gray-500 border-b border-gray-100">
                            <th className="py-1.5 pr-2 font-medium">Sản phẩm</th>
                            <th className="py-1.5 pr-2 font-medium">Mức độ quan tâm</th>
                            <th className="py-1.5 pr-2 font-medium">Ghi chú</th>
                            <th className="py-1.5 pr-2 font-medium">Ngày</th>
                        </tr>
                    </thead>
                    <tbody>
                        {items.map(it => (
                            <tr key={it.id} className="border-b border-gray-50">
                                <td className="py-1.5 pr-2 text-text-main">{productMap.get(it.productId)?.name ?? `#${it.productId}`}</td>
                                <td className="py-1.5 pr-2 text-text-main">{INTEREST_LABELS[it.interestType] ?? it.interestType}</td>
                                <td className="py-1.5 pr-2 text-gray-500">{it.note ?? '—'}</td>
                                <td className="py-1.5 pr-2 text-gray-500">{formatISODate(it.createdAt)}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </div>
    );
};
