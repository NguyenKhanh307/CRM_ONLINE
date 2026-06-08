import { createContext, useState, useCallback, type ReactNode } from 'react';
import { AlertModal } from '@/shared/components/AlertModal';

interface AlertContextValue {
    showAlert: (message: string) => void;
}

export const AlertContext = createContext<AlertContextValue | null>(null);

/**
 * Provider bọc toàn app — cung cấp showAlert() cho mọi component con.
 */
export const AlertProvider = ({ children }: { children: ReactNode }) => {
    const [message, setMessage] = useState<string | null>(null);

    const showAlert = useCallback((msg: string) => setMessage(msg), []);
    const handleClose = useCallback(() => setMessage(null), []);

    return (
        <AlertContext.Provider value={{ showAlert }}>
            {children}
            {message !== null && (
                <AlertModal message={message} onClose={handleClose} />
            )}
        </AlertContext.Provider>
    );
};
