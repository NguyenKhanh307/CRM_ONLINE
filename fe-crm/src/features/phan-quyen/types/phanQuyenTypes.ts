export interface PermissionItem {
    id: string;
    name: string;
    checked: boolean;
    roles: string[];
}

export interface ModulePermission {
    id: string;
    label: string;
    isOpen: boolean;
    permissions: PermissionItem[];
}
