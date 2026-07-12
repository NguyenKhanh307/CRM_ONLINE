import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
    DndContext, DragOverlay, PointerSensor, useSensor, useSensors,
    type DragEndEvent, type DragStartEvent,
} from '@dnd-kit/core';
import { FiList, FiSearch, FiSliders } from 'react-icons/fi';
import { PageHeaderSlot } from '@/shared/components/layout/PageHeaderSlot';
import { ActionButton } from '@/shared/components/ActionButton';
import { ReasonModal } from '@/shared/components/ReasonModal';
import { useOpportunityBoard } from '../hooks/useOpportunityBoard';
import { useChangeOpportunityStage } from '../hooks/useChangeOpportunityStage';
import { BoardColumn } from '../components/board/BoardColumn';
import { CardBody } from '../components/board/OpportunityCard';
import type { BoardCard } from '../types/boardTypes';

/** Thẻ vừa kéo vào cột "thua" — giữ lại để hỏi lý do trước khi gọi API. */
interface PendingLostMove {
    id: number;
    stageId: number;
}

/**
 * Bảng Kanban cơ hội: cột = giai đoạn pipeline, thẻ = cơ hội.
 * Kéo thẻ sang cột khác = đổi giai đoạn; trạng thái (open/won/lost) vẫn do BE suy ra từ giai đoạn.
 */
const OpportunityBoardPage = () => {
    const navigate = useNavigate();
    const [search, setSearch] = useState('');
    const [debounced, setDebounced] = useState('');
    const [draggingCard, setDraggingCard] = useState<BoardCard | null>(null);
    const [pendingLost, setPendingLost] = useState<PendingLostMove | null>(null);

    useEffect(() => {
        const t = setTimeout(() => setDebounced(search.trim()), 350);
        return () => clearTimeout(t);
    }, [search]);

    const { data: columns = [], isLoading } = useOpportunityBoard(debounced || undefined);
    const { mutate: changeStage } = useChangeOpportunityStage(debounced || undefined);

    // Kéo quá 5px mới tính là kéo → click (không di chuột) vẫn mở được trang chi tiết
    const sensors = useSensors(useSensor(PointerSensor, { activationConstraint: { distance: 5 } }));

    const cardsById = useMemo(() => {
        const map = new Map<number, BoardCard>();
        columns.forEach(col => col.cards.forEach(c => map.set(c.id, c)));
        return map;
    }, [columns]);

    const handleDragStart = (e: DragStartEvent) => {
        setDraggingCard(cardsById.get(Number(e.active.id)) ?? null);
    };

    const handleDragEnd = (e: DragEndEvent) => {
        setDraggingCard(null);
        if (!e.over) return;

        const id = Number(e.active.id);
        const stageId = Number(e.over.id);
        const card = cardsById.get(id);
        if (!card || card.stageId === stageId) return;

        // Kéo vào giai đoạn "thua" thì phải ghi lý do (hủy modal = không đổi giai đoạn)
        const target = columns.find(c => c.stageId === stageId);
        if (target?.lost) {
            setPendingLost({ id, stageId });
            return;
        }
        changeStage({ id, stageId });
    };

    return (
        <div className="p-6 bg-bg-main">
            <PageHeaderSlot>
                <h1 className="text-lg font-semibold text-text-main truncate">Bảng cơ hội</h1>
                <div className="flex items-center gap-1.5">
                    <div className="relative">
                        <FiSearch size={13} className="absolute left-2 top-1/2 -translate-y-1/2 text-gray-400" />
                        <input
                            value={search}
                            onChange={e => setSearch(e.target.value)}
                            placeholder="Tìm theo mã / tên cơ hội"
                            className="pl-7 pr-2 py-1 w-56 border border-gray-300 rounded-btn text-table focus:outline-none focus:border-primary"
                        />
                    </div>
                    <ActionButton variant="secondary" icon={FiSliders} onClick={() => navigate('/co-hoi/pipeline')}>
                        Giai đoạn
                    </ActionButton>
                    <ActionButton variant="secondary" icon={FiList} onClick={() => navigate('/co-hoi')}>
                        Danh sách
                    </ActionButton>
                </div>
            </PageHeaderSlot>

            {isLoading ? (
                <div className="text-gray-400">Đang tải...</div>
            ) : columns.length === 0 ? (
                <div className="text-gray-500">
                    Chưa có giai đoạn pipeline nào —{' '}
                    <button onClick={() => navigate('/co-hoi/pipeline')} className="text-primary hover:underline">
                        tạo giai đoạn
                    </button>
                </div>
            ) : (
                <DndContext sensors={sensors} onDragStart={handleDragStart} onDragEnd={handleDragEnd}>
                    <div className="flex gap-3 overflow-x-auto pb-2">
                        {columns.map(col => (
                            <BoardColumn key={col.stageId} column={col} onOpenCard={id => navigate(`/co-hoi/${id}`)} />
                        ))}
                    </div>

                    {/* Bắt buộc: refetch giữa lúc kéo sẽ remount cột, thẻ đang kéo phải sống ở overlay */}
                    <DragOverlay>{draggingCard && <div className="w-[264px]"><CardBody card={draggingCard} /></div>}</DragOverlay>
                </DndContext>
            )}

            {pendingLost && (
                <ReasonModal
                    title="Đánh dấu cơ hội thất bại"
                    label="Lý do thua"
                    placeholder="Nhập lý do thua..."
                    confirmLabel="Xác nhận"
                    confirmDanger
                    onConfirm={reason => {
                        changeStage({ ...pendingLost, winLossReason: reason || null });
                        setPendingLost(null);
                    }}
                    onCancel={() => setPendingLost(null)}
                />
            )}
        </div>
    );
};

export default OpportunityBoardPage;
