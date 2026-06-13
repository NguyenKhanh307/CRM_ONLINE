export interface UpdateContactPayload {
    customerId: number | null;
    assignedUserId: number | null;
    salutation?: string | null;
    fullName: string;
    title?: string | null;
    department?: string | null;
    position: string | null;
    email: string | null;
    workEmail?: string | null;
    personalEmail?: string | null;
    zalo?: string | null;
    source?: string | null;
    gender: string | null;
    dateOfBirth: string | null;
    address: string | null;
    doNotCall?: boolean;
    doNotEmail?: boolean;
    isPrimary: boolean;
}

/** Số điện thoại tạo kèm liên hệ. */
export interface ContactPhonePayload {
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
    position: string | null;
    email: string | null;
    workEmail: string | null;
    personalEmail: string | null;
    zalo: string | null;
    source: string | null;
    gender: string | null;
    dateOfBirth: string | null;
    address: string | null;
    doNotCall: boolean;
    doNotEmail: boolean;
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
    position: string | null;
    email: string | null;
    workEmail: string | null;
    personalEmail: string | null;
    zalo: string | null;
    source: string | null;
    gender: string | null;
    dateOfBirth: string | null;
    address: string | null;
    doNotCall: boolean;
    doNotEmail: boolean;
    isPrimary: boolean;
    createdAt: string;
    updatedAt: string;
}
