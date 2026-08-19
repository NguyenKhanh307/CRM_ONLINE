export interface UpdateContactPayload {
    customerId: number | null;
    assignedUserId: number | null;
    salutation?: string | null;
    fullName: string;
    title?: string | null;
    department?: string | null;
    email: string | null;
    zalo?: string | null;
    phone?: string | null;
    source?: string | null;
    gender: string | null;
    dateOfBirth: string | null;
    isPrimary: boolean;
}

/** Payload tạo mới liên hệ — POST /api/contacts. */
export interface CreateContactPayload {
    customerId: number | null;
    assignedUserId: number | null;
    salutation: string | null;
    fullName: string;
    title: string | null;
    department: string | null;
    email: string | null;
    zalo: string | null;
    phone: string | null;
    source: string | null;
    gender: string | null;
    dateOfBirth: string | null;
    isPrimary: boolean;
}

export interface ContactResult {
    id: number;
    customerId: number | null;
    assignedUserId: number | null;
    salutation: string | null;
    fullName: string;
    title: string | null;
    department: string | null;
    email: string | null;
    zalo: string | null;
    phone: string | null;
    source: string | null;
    gender: string | null;
    dateOfBirth: string | null;
    isPrimary: boolean;
    createdAt: string;
    updatedAt: string;
    // Tên khóa ngoại do BE resolve sẵn (INameResolver).
    customerName: string | null;
    assignedUserName: string | null;
    // Audit: BE tự đóng dấu (AuditInterceptor).
    createdBy: number | null;
    updatedBy: number | null;
    createdByName: string | null;
    updatedByName: string | null;
}
