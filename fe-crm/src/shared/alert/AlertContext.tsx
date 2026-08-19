import { createContext, useState, useCallback, useEffect, type ReactNode } from 'react';
import { AlertModal } from '@/shared/components/AlertModal';
import { setAlertHandler } from './alertBridge';
// context cung cấp showAlert() cho các component con trong cây React
interface AlertContextValue {
    showAlert: (message: string) => void;
}

export const AlertContext = createContext<AlertContextValue | null>(null);

// provider bọc toàn app — cung cấp showAlert() cho mọi component con
export const AlertProvider = ({ children }: { children: ReactNode }) => {
    const [message, setMessage] = useState<string | null>(null);

    const showAlert = useCallback((msg: string) => setMessage(msg), []);
    const handleClose = useCallback(() => setMessage(null), []);

    // đăng ký showAlert vào bridge để code ngoài cây React (MutationCache) hiện được alert
    useEffect(() => {
        setAlertHandler(showAlert);
        return () => setAlertHandler(null);
    }, [showAlert]);

    return (
        <AlertContext.Provider value={{ showAlert }}>
            {children}
            {message !== null && (
                <AlertModal message={message} onClose={handleClose} />
            )}
        </AlertContext.Provider>
    );
};
