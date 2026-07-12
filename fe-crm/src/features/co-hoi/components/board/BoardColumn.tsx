import { useDroppable } from '@dnd-kit/core';
import { useNavigate } from 'react-router-dom';
import { formatCurrency, formatNumber } from '@/shared/utils/number';
import { OpportunityCard } from './OpportunityCard';
import type { BoardColumn as BoardColumnData } from '../../types/boardTypes';

interface BoardColumnProps {
    column: BoardColumnData;
    onOpenCard: (id: number) => void;
}

/** Màu vạch đầu cột theo tính chất giai đoạn: thắng = xanh lá, thua = đỏ, còn lại = xanh dương. */
const headBar = (col: BoardColumnData) =>
    col.won ? 'bg-green-500' : col.lost ? 'bg-red-500' : 'bg-primary';

/** Một cột của bảng Kanban = một giai đoạn pipeline; nhận thẻ được thả vào. */
export const BoardColumn = ({ column, onOpenCard }: BoardColumnProps) => {
    const navigate = useNavigate();
    const { setNodeRef, isOver } = useDroppable({ id: column.stageId });
    const hidden = column.total - column.cards.length;

    return (
        <div className="w-[280px] shrink-0 flex flex-col bg-gray-50 rounded-card border border-gray-200">
            <div className="p-3 border-b border-gray-200">
                <div className={`h-1 w-full rounded-full mb-2 ${headBar(column)}`} />
                <div className="flex items-center justify-between">
                    <span className="text-table font-medium text-text-main truncate">{column.stageName}</span>
                    <span className="text-sm text-gray-500 shrink-0">{formatNumber(column.total)}</span>
                </div>
                <div className="text-sm text-gray-500">{formatCurrency(column.sumAmount)}</div>
            </div>

            <div
                ref={setNodeRef}
                className={`flex-1 overflow-y-auto p-2 space-y-2 transition-colors ${isOver ? 'bg-blue-50' : ''}`}
                style={{ maxHeight: 'calc(100vh - 260px)', minHeight: 120 }}
            >
                {column.cards.map(card => (
                    <OpportunityCard key={card.id} card={card} onOpen={onOpenCard} />
                ))}

                {column.cards.length === 0 && (
                    <div className="py-6 text-center text-sm text-gray-400">Kéo cơ hội vào đây</div>
                )}

                {hidden > 0 && (
                    <button
                        onClick={() => navigate('/co-hoi')}
                        className="w-full py-2 text-sm text-primary hover:underline"
                    >
                        Còn {formatNumber(hidden)} cơ hội — xem trong danh sách
                    </button>
                )}
            </div>
        </div>
    );
};
