import { useEffect, useRef, useState } from 'react';
import { FiMessageSquare, FiSend, FiX } from 'react-icons/fi';
import { inputCls } from '@/shared/components/form/formStyles';
import { useAskCopilot } from './useAskCopilot';
import type { ChatMessage } from './copilotTypes';

/**
 * Trợ lý AI Copilot — bong bóng chat nổi góc phải-dưới, hiện trên mọi trang.
 * Hỏi tiếng Việt về dữ liệu CRM (doanh thu, tỷ lệ thắng, tình hình khách hàng...).
 */
export const CopilotWidget = () => {
    const [open, setOpen] = useState(false);
    const [messages, setMessages] = useState<ChatMessage[]>([]);
    const [input, setInput] = useState('');
    const { mutate, isPending } = useAskCopilot();
    const scrollRef = useRef<HTMLDivElement>(null);

    // Tự cuộn xuống tin nhắn mới nhất.
    useEffect(() => {
        scrollRef.current?.scrollTo({ top: scrollRef.current.scrollHeight, behavior: 'smooth' });
    }, [messages, isPending]);

    /** Gửi câu hỏi hiện tại lên trợ lý. */
    const handleSend = () => {
        const question = input.trim();
        if (!question || isPending) return;
        setMessages((prev) => [...prev, { role: 'user', text: question }]);
        setInput('');
        mutate(question, {
            onSuccess: (res) => setMessages((prev) => [...prev, { role: 'assistant', text: res.answer }]),
            onError: (err: unknown) => {
                const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                    ?? 'Không gọi được trợ lý AI. Vui lòng thử lại sau.';
                setMessages((prev) => [...prev, { role: 'assistant', text: `⚠️ ${msg}` }]);
            },
        });
    };

    /** Enter để gửi, Shift+Enter để xuống dòng. */
    const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            handleSend();
        }
    };

    return (
        <>
            {/* Bong bóng mở/đóng */}
            <button
                onClick={() => setOpen((v) => !v)}
                aria-label="Trợ lý AI"
                className="fixed bottom-6 right-6 z-[9999] w-14 h-14 rounded-full bg-primary text-white
                           shadow-lg flex items-center justify-center hover:opacity-90 transition-opacity"
            >
                {open ? <FiX size={24} /> : <FiMessageSquare size={24} />}
            </button>

            {/* Panel chat */}
            {open && (
                <div className="fixed bottom-24 right-6 z-[9999] w-[360px] max-w-[calc(100vw-3rem)] h-[480px]
                                max-h-[calc(100vh-8rem)] bg-white rounded-card shadow-2xl border border-gray-200
                                flex flex-col overflow-hidden">
                    <div className="px-4 py-3 bg-primary text-white flex items-center gap-2">
                        <FiMessageSquare size={18} />
                        <span className="text-md font-semibold">Trợ lý AI</span>
                    </div>

                    <div ref={scrollRef} className="flex-1 overflow-y-auto p-3 space-y-3 bg-bg-main">
                        {messages.length === 0 && (
                            <p className="text-sm text-gray-500 text-center mt-6 px-3">
                                Hỏi tôi về dữ liệu CRM — ví dụ: “Doanh thu quý này so quý trước?”,
                                “Tỷ lệ thắng bao nhiêu?”, “Khách hàng ABC đang thế nào?”
                            </p>
                        )}
                        {messages.map((m, i) => (
                            <div key={i} className={m.role === 'user' ? 'flex justify-end' : 'flex justify-start'}>
                                <div
                                    className={
                                        'max-w-[85%] px-3 py-2 rounded-card text-table whitespace-pre-wrap break-words ' +
                                        (m.role === 'user'
                                            ? 'bg-primary text-white'
                                            : 'bg-white text-text-main border border-gray-200')
                                    }
                                >
                                    {m.text}
                                </div>
                            </div>
                        ))}
                        {isPending && (
                            <div className="flex justify-start">
                                <div className="px-3 py-2 rounded-card text-table bg-white text-gray-400 border border-gray-200">
                                    Đang soạn...
                                </div>
                            </div>
                        )}
                    </div>

                    <div className="p-2 border-t border-gray-200 flex items-end gap-2 bg-white">
                        <textarea
                            value={input}
                            onChange={(e) => setInput(e.target.value)}
                            onKeyDown={handleKeyDown}
                            rows={2}
                            placeholder="Nhập câu hỏi..."
                            className={inputCls + ' resize-none'}
                        />
                        <button
                            onClick={handleSend}
                            disabled={isPending || !input.trim()}
                            aria-label="Gửi"
                            className="shrink-0 w-9 h-9 rounded-btn bg-primary text-white flex items-center justify-center
                                       hover:opacity-90 disabled:opacity-40 transition-opacity"
                        >
                            <FiSend size={16} />
                        </button>
                    </div>
                </div>
            )}
        </>
    );
};
