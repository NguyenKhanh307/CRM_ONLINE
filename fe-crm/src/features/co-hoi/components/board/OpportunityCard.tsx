import { useDraggable } from '@dnd-kit/core';
import { FiUser, FiCalendar } from 'react-icons/fi';
import { formatCurrency } from '@/shared/utils/number';
import { formatISODate } from '@/shared/utils/date';
import type { BoardCard } from '../../types/boardTypes';

interface OpportunityCardProps {
    card: BoardCard;
    onOpen: (id: number) => void;
}

/** Nội dung thẻ — tách riêng để dùng lại cho DragOverlay (thẻ "bay" theo con trỏ). */
export const CardBody = ({ card }: { card: BoardCard }) => (
    <div className="bg-white rounded-btn border border-gray-200 p-3 shadow-sm space-y-1.5">
        <div className="flex items-start justify-between gap-2">
            <span className="text-table font-medium text-text-main line-clamp-2">{card.name}</span>
            <span className="text-sm text-gray-400 shrink-0">{card.code}</span>
        </div>
        {card.customerName && <div className="text-sm text-gray-500 truncate">{card.customerName}</div>}
        <div className="text-table font-medium text-primary">{formatCurrency(card.amount)}</div>
        <div className="flex items-center gap-3 text-sm text-gray-400">
            {card.ownerName && (
                <span className="flex items-center gap-1 truncate">
                    <FiUser size={11} /> {card.ownerName}
                </span>
            )}
            {card.expectedCloseDate && (
                <span className="flex items-center gap-1 shrink-0">
                    <FiCalendar size={11} /> {formatISODate(card.expectedCloseDate)}
                </span>
            )}
        </div>
    </div>
);

/**
 * Thẻ cơ hội kéo được trên bảng Kanban.
 * Kéo quá 5px mới tính là kéo (PointerSensor ở trang) → click vẫn mở được trang chi tiết.
 */
export const OpportunityCard = ({ card, onOpen }: OpportunityCardProps) => {
    const { attributes, listeners, setNodeRef, isDragging } = useDraggable({ id: card.id });

    return (
        <div
            ref={setNodeRef}
            {...listeners}
            {...attributes}
            onClick={() => onOpen(card.id)}
            className={`cursor-grab active:cursor-grabbing focus:outline-none focus-visible:ring-2 focus-visible:ring-primary rounded-btn ${
                isDragging ? 'opacity-40' : ''
            }`}
        >
            <CardBody card={card} />
        </div>
    );
};
