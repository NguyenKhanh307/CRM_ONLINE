/** State form thêm mới liên hệ — các field UI (id dạng string cho select). */
export interface ContactFormState {
    salutation: string;
    hoTen: string;
    title: string;
    department: string;
    customerId: string;
    assignedUserId: string;
    source: string;
    phone: string;
    email: string;
    zalo: string;
    gender: string;
    dateOfBirth: string;
    isPrimary: boolean;
}

export const INITIAL_CONTACT_FORM: ContactFormState = {
    salutation: '',
    hoTen: '',
    title: '',
    department: '',
    customerId: '',
    assignedUserId: '',
    source: '',
    phone: '',
    email: '',
    zalo: '',
    gender: '',
    dateOfBirth: '',
    isPrimary: false,
};

export const SALUTATION_OPTIONS = [
    { value: 'Anh', label: 'Anh' },
    { value: 'Chị', label: 'Chị' },
    { value: 'Ông', label: 'Ông' },
    { value: 'Bà', label: 'Bà' },
];

export const GENDER_OPTIONS = [
    { value: 'male', label: 'Nam' },
    { value: 'female', label: 'Nữ' },
    { value: 'other', label: 'Khác' },
];

export const SOURCE_OPTIONS = [
    { value: 'website', label: 'Website' },
    { value: 'gioi-thieu', label: 'Giới thiệu' },
    { value: 'dien-thoai', label: 'Điện thoại' },
    { value: 'email', label: 'Email' },
    { value: 'mxh', label: 'Mạng xã hội' },
    { value: 'khac', label: 'Khác' },
];
