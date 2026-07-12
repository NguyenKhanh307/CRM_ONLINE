/** Một thẻ cơ hội trên bảng Kanban — GET /api/opportunities/board. */
export interface BoardCard {
    id: number;
    code: string;
    name: string;
    customerName: string | null;
    ownerName: string | null;
    amount: number | null;
    expectedCloseDate: string | null;
    probability: number | null;
    stageId: number | null;
}

/**
 * Một cột (= giai đoạn pipeline) trên bảng Kanban.
 * BE trả cờ tên `won`/`lost` (không phải `isWon`) — xem BoardColumnResult.
 */
export interface BoardColumn {
    stageId: number;
    stageName: string;
    sortOrder: number;
    won: boolean;
    lost: boolean;
    /** Tổng số cơ hội thật ở giai đoạn này (có thể > cards.length). */
    total: number;
    sumAmount: number;
    /** Tối đa 50 thẻ mới cập nhật nhất. */
    cards: BoardCard[];
}

/** Payload đổi giai đoạn — POST /api/opportunities/{id}/stage. */
export interface ChangeStagePayload {
    stageId: number;
    winLossReason?: string | null;
}
