export interface UpdateContactPayload {
    customerId: number | null;
    assignedUserId: number | null;
    salutation?: string | null;
    fullName: string;
    title?: string | null;
    department?: string | null;
    email: string | null;
    zalo?: string | null;
    source?: string | null;
    gender: string | null;
    dateOfBirth: string | null;
    isPrimary: boolean;
}

/** Số điện thoại tạo kèm liên hệ. */
export interface ContactPhonePayload {
    phone: string;
    phoneType: 'mobile' | 'office' | 'home' | 'other';
    isPrimary: boolean;
}

/** Số điện thoại trả về từ GET /api/contacts/{id}/phones. */
export interface ContactPhoneResult {
    id: number;
    contactId: number;
    phone: string;
    phoneType: 'mobile' | 'office' | 'home' | 'other';
    isPrimary: boolean;
}

/** Payload tạo mới liên hệ — POST /api/contacts (kèm phones[]). */
export interface CreateContactPayload {
    customerId: number | null;
    assignedUserId: number | null;
    salutation: string | null;
    fullName: string;
    title: string | null;
    department: string | null;
    email: string | null;
    zalo: string | null;
    source: string | null;
    gender: string | null;
    dateOfBirth: string | null;
    isPrimary: boolean;
    phones: ContactPhonePayload[];
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
