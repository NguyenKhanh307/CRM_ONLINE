import { useEffect, useRef, useState } from 'react';
import { useGoogleLogin } from '../hooks/useGoogleLogin';

/** Phản hồi credential (ID token) từ Google Identity Services. */
interface GoogleCredentialResponse {
    credential: string;
}

interface GoogleIdApi {
    initialize(config: {
        client_id: string;
        callback: (resp: GoogleCredentialResponse) => void;
    }): void;
    renderButton(parent: HTMLElement, options: Record<string, unknown>): void;
}

declare global {
    interface Window {
        google?: { accounts?: { id?: GoogleIdApi } };
    }
}

const CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID as string | undefined;

/**
 * Nút "Đăng nhập bằng Google" — render nút chính chủ của Google Identity Services.
 * Nhận ID token qua callback rồi gửi lên BE để xác thực.
 */
export const GoogleLoginButton = () => {
    const containerRef = useRef<HTMLDivElement>(null);
    const { mutate } = useGoogleLogin();
    const [notReady, setNotReady] = useState(false);

    // Neo mutate qua ref để callback của Google luôn gọi bản mới nhất mà effect không phải chạy lại.
    const mutateRef = useRef(mutate);
    mutateRef.current = mutate;

    useEffect(() => {
        if (!CLIENT_ID) {
            setNotReady(true);
            return;
        }
        let cancelled = false;

        // Script GSI tải async → chờ tới khi window.google.accounts.id sẵn sàng.
        const tryRender = () => {
            if (cancelled) return;
            const idApi = window.google?.accounts?.id;
            const el = containerRef.current;
            if (!idApi || !el) return false;
            idApi.initialize({
                client_id: CLIENT_ID,
                callback: (resp) => mutateRef.current({ idToken: resp.credential }),
            });
            idApi.renderButton(el, {
                type: 'standard',
                theme: 'outline',
                size: 'large',
                text: 'signin_with',
                shape: 'rectangular',
                width: el.offsetWidth || 320,
            });
            return true;
        };

        if (tryRender()) return;
        const timer = setInterval(() => {
            if (tryRender()) clearInterval(timer);
        }, 200);
        // Sau 5s vẫn chưa có script → báo không khả dụng.
        const stopTimer = setTimeout(() => {
            clearInterval(timer);
            if (!cancelled && !window.google?.accounts?.id) setNotReady(true);
        }, 5000);

        return () => {
            cancelled = true;
            clearInterval(timer);
            clearTimeout(stopTimer);
        };
    }, []);

    if (notReady) {
        return (
            <p className="text-center text-sm text-gray-400">
                Đăng nhập Google chưa được cấu hình.
            </p>
        );
    }

    return <div ref={containerRef} className="flex justify-center" />;
};
