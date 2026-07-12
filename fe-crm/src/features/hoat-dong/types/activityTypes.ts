export interface UpdateActivityPayload {
    type: string;
    subject: string;
    content: string | null;
    priority?: string | null;
    targetType: string | null;
    targetId: number | null;
    relatedType?: string | null;
    relatedId?: number | null;
    location?: string | null;
    callDirection?: string | null;
    callResult?: string | null;
    callDuration?: number | null;
    assignedUserId: number | null;
    // status: KHÔNG gửi — đổi qua hành động start/complete/cancel.
    dueAt: string | null;
}

/** Payload tạo mới hoạt động — POST /api/activities. */
export interface CreateActivityPayload {
    type: string;
    subject: string;
    content: string | null;
    priority: string | null;
    targetType: string | null;
    targetId: number | null;
    relatedType: string | null;
    relatedId: number | null;
    location: string | null;
    callDirection: string | null;
    callResult: string | null;
    callDuration: number | null;
    assignedUserId: number | null;
    // status: KHÔNG gửi — hoạt động luôn tạo ở trạng thái 'planned'.
    dueAt: string | null;
}

export interface ActivityResult {
    id: number;
    type: string;
    subject: string;
    content: string | null;
    priority: string | null;
    targetType: string | null;
    targetId: number | null;
    relatedType: string | null;
    relatedId: number | null;
    location: string | null;
    callDirection: string | null;
    callResult: string | null;
    callDuration: number | null;
    assignedUserId: number | null;
    status: string;
    dueAt: string | null;
    completedAt: string | null;
    createdAt: string;
    updatedAt: string;
    // Tên khóa ngoại do BE resolve sẵn (INameResolver).
    assignedUserName: string | null;
    // Audit: BE tự đóng dấu (AuditInterceptor).
    createdBy: number | null;
    updatedBy: number | null;
    createdByName: string | null;
    updatedByName: string | null;
}
