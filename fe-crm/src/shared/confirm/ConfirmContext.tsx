import { createContext, useCallback, useRef, useState, type ReactNode } from 'react';
import { ConfirmModal } from '@/shared/components/ConfirmModal';

export interface ConfirmOptions {
    message: string;
    confirmLabel?: string;
    confirmDanger?: boolean;
}

interface ConfirmContextValue {
    /** Mở popup xác nhận, trả về true nếu người dùng đồng ý. */
    confirm: (options: ConfirmOptions) => Promise<boolean>;
    /** Xác nhận trước khi thêm mới một bản ghi. `noun` là danh từ phân hệ, ví dụ 'tiềm năng'. */
    confirmCreate: (noun: string) => Promise<boolean>;
    /** Xác nhận trước khi lưu thay đổi một bản ghi. */
    confirmSave: (noun: string) => Promise<boolean>;
    /** Xác nhận trước khi xóa một bản ghi — nút xác nhận màu đỏ, focus sẵn nút Hủy. */
    confirmDelete: (noun: string) => Promise<boolean>;
}

export const ConfirmContext = createContext<ConfirmContextValue | null>(null);

/**
 * Provider bọc toàn app — cung cấp confirm() dạng Promise cho mọi component con.
 * Dùng lại ConfirmModal sẵn có, để luồng submit chờ được câu trả lời của người dùng.
 */
export const ConfirmProvider = ({ children }: { children: ReactNode }) => {
    const [options, setOptions] = useState<ConfirmOptions | null>(null);
    const resolveRef = useRef<((value: boolean) => void) | null>(null);

    const confirm = useCallback((opts: ConfirmOptions) => {
        setOptions(opts);
        return new Promise<boolean>((resolve) => { resolveRef.current = resolve; });
    }, []);

    const confirmCreate = useCallback(
        (noun: string) => confirm({ message: `Xác nhận thêm mới ${noun}?` }), [confirm]);
    const confirmSave = useCallback(
        (noun: string) => confirm({ message: `Xác nhận lưu thay đổi ${noun}?` }), [confirm]);
    const confirmDelete = useCallback(
        (noun: string) => confirm({ message: `Xác nhận xóa ${noun}?`, confirmLabel: 'Xóa', confirmDanger: true }),
        [confirm]);

    /** Đóng popup và trả kết quả cho lời gọi confirm() đang chờ. */
    const settle = useCallback((accepted: boolean) => {
        setOptions(null);
        resolveRef.current?.(accepted);
        resolveRef.current = null;
    }, []);

    return (
        <ConfirmContext.Provider value={{ confirm, confirmCreate, confirmSave, confirmDelete }}>
            {children}
            {options !== null && (
                <ConfirmModal
                    message={options.message}
                    confirmLabel={options.confirmLabel}
                    confirmDanger={options.confirmDanger}
                    onConfirm={() => settle(true)}
                    onCancel={() => settle(false)}
                />
            )}
        </ConfirmContext.Provider>
    );
};
