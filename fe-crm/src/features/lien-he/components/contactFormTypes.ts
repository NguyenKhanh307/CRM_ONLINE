/** State form thêm mới liên hệ — các field UI (hoDem/ten tách riêng, id dạng string cho select). */
export interface ContactFormState {
    salutation: string;
    hoDem: string;
    ten: string;
    title: string;
    department: string;
    position: string;
    customerId: string;
    assignedUserId: string;
    source: string;
    mobilePhone: string;
    officePhone: string;
    email: string;
    workEmail: string;
    personalEmail: string;
    zalo: string;
    address: string;
    doNotCall: boolean;
    doNotEmail: boolean;
    gender: string;
    dateOfBirth: string;
    isPrimary: boolean;
}

export const INITIAL_CONTACT_FORM: ContactFormState = {
    salutation: '',
    hoDem: '',
    ten: '',
    title: '',
    department: '',
    position: '',
    customerId: '',
    assignedUserId: '',
    source: '',
    mobilePhone: '',
    officePhone: '',
    email: '',
    workEmail: '',
    personalEmail: '',
    zalo: '',
    address: '',
    doNotCall: false,
    doNotEmail: false,
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
