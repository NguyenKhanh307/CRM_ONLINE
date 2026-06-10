export interface UpdateActivityPayload {
    type: string;
    subject: string;
    content: string | null;
    targetType: string | null;
    targetId: number | null;
    assignedUserId: number | null;
    status: string;
    dueAt: string | null;
}

export interface ActivityResult {
    id: number;
    type: string;
    subject: string;
    content: string | null;
    targetType: string | null;
    targetId: number | null;
    assignedUserId: number | null;
    status: string;
    dueAt: string | null;
    completedAt: string | null;
    createdAt: string;
    updatedAt: string;
}
