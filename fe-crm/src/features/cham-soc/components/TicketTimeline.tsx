import { useState } from 'react';
import { FiSend, FiSettings, FiMessageSquare } from 'react-icons/fi';
import { formatISODate } from '@/shared/utils/date';
import { useTicketComments, useCreateTicketComment } from '../hooks/useTicketComments';

interface Props {
    ticketId: number;
    // phiếu đã đóng — chỉ mở lại mới ghi chú tiếp được
    closed?: boolean;
}

// dòng thời gian ghi chú / lịch sử phiếu: phân biệt system (audit) và note (người nhập)
export function TicketTimeline({ ticketId, closed = false }: Props) {
    const { data: comments = [], isLoading } = useTicketComments(ticketId);
    const { mutate: addNote, isPending } = useCreateTicketComment(ticketId);
    const [content, setContent] = useState('');
    const [isInternal, setIsInternal] = useState(true);

    const submit = () => {
        if (!content.trim()) return;
        addNote({ content: content.trim(), isInternal }, { onSuccess: () => setContent('') });
    };

    return (
        <div className="space-y-4">
            {closed ? (
                <p className="text-md text-gray-400">Phiếu đã đóng — mở lại để ghi chú tiếp.</p>
            ) : (
                <div className="space-y-2">
                    <textarea rows={2} value={content} onChange={(e) => setContent(e.target.value)}
                        placeholder="Nhập trao đổi / ghi chú..."
                        className="w-full border border-gray-300 rounded-btn px-3 py-1.5 text-md text-text-main focus:outline-none focus:border-primary resize-none" />
                    <div className="flex items-center justify-between">
                        <label className="flex items-center gap-2 text-sm text-gray-600">
                            <input type="checkbox" checked={isInternal} onChange={(e) => setIsInternal(e.target.checked)} />
                            Ghi chú nội bộ
                        </label>
                        <button type="button" onClick={submit} disabled={isPending || !content.trim()}
                            className="flex items-center gap-1.5 px-3 py-1.5 rounded-btn bg-primary text-white text-md font-medium hover:opacity-90 disabled:opacity-50">
                            <FiSend size={14} /> Gửi
                        </button>
                    </div>
                </div>
            )}

            {isLoading ? (
                <p className="text-gray-400 text-md">Đang tải lịch sử...</p>
            ) : (
                // khung cuộn riêng — lịch sử audit + trao đổi dài không kéo dài trang
                <ul className="space-y-3 overflow-auto pr-1 max-h-[420px]">
                    {comments.map(c => (
                        <li key={c.id} className="flex gap-3">
                            <div className={`mt-0.5 flex-shrink-0 w-7 h-7 rounded-full flex items-center justify-center ${c.type === 'system' ? 'bg-gray-100 text-gray-500' : 'bg-blue-50 text-primary'}`}>
                                {c.type === 'system' ? <FiSettings size={14} /> : <FiMessageSquare size={14} />}
                            </div>
                            <div className="flex-1 min-w-0">
                                <div className="flex items-center gap-2 text-sm text-gray-500">
                                    <span className="font-medium text-text-main">
                                        {c.type === 'system' ? 'Hệ thống' : (c.authorName ?? 'Người dùng')}
                                    </span>
                                    {c.type === 'note' && (
                                        <span className={`px-1.5 py-0.5 rounded text-xs font-medium ${c.isInternal ? 'bg-gray-100 text-gray-500' : 'bg-green-100 text-green-700'}`}>
                                            {c.isInternal ? 'Nội bộ' : 'Gửi khách'}
                                        </span>
                                    )}
                                    <span>{formatISODate(c.createdAt)}</span>
                                </div>
                                <p className="text-md text-text-main whitespace-pre-wrap">{c.content}</p>
                            </div>
                        </li>
                    ))}
                    {comments.length === 0 && <li className="text-gray-400 text-md">Chưa có ghi chú nào</li>}
                </ul>
            )}
        </div>
    );
}
