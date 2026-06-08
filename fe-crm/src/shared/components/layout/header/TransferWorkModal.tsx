import { useState } from 'react';
import { FiHelpCircle, FiMoreHorizontal, FiX, FiUser } from 'react-icons/fi';

const MOCK_USERS = [
    { id: 1, name: 'Ika Ika' },
    { id: 2, name: 'Nguyễn Văn A' },
    { id: 3, name: 'Trần Thị B' },
    { id: 4, name: 'Lê Văn C' },
];

interface ModalProps {
    onClose: () => void;
}

const TransferWorkModal = ({ onClose }: ModalProps) => {
    const [fromUser, setFromUser] = useState(MOCK_USERS[0].id);
    const [toUser, setToUser] = useState<number | ''>('');

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/30">
            <div className="bg-white rounded-card shadow-xl w-[520px] max-w-[95vw]">
                {/* Title bar */}
                <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
                    <span className="font-semibold text-lg text-text-main">
                        Bàn giao toàn bộ công việc
                    </span>
                    <div className="flex items-center gap-2">
                        <button className="p-1 rounded hover:bg-gray-100 text-gray-400 hover:text-gray-600">
                            <FiHelpCircle size={16} />
                        </button>
                        <button
                            onClick={onClose}
                            className="p-1 rounded hover:bg-gray-100 text-gray-400 hover:text-gray-600"
                        >
                            <FiX size={18} />
                        </button>
                    </div>
                </div>

                <div className="px-5 py-4 space-y-4">
                    {/* Warning box */}
                    <div className="border border-orange-300 bg-orange-50 rounded-section px-4 py-3 text-sm text-orange-800 leading-relaxed">
                        Chức năng này sẽ chuyển toàn bộ đối tượng (Tiềm năng, Liên hệ, Khách hàng,
                        Đối thủ) và toàn bộ công việc dở dang (Cơ hội, Báo giá, Đơn hàng, Hóa đơn,
                        Trả lại hàng bán, Nhiệm vụ, Lịch hẹn, Cuộc gọi, Thẻ tư vấn, Thẻ chăm sóc,
                        Đơn hàng NPP, Trả hàng NPP, Yêu cầu mua hàng, Tuyến vận chuyển) của người
                        bàn giao sang cho người được bàn giao.
                    </div>

                    {/* User selectors */}
                    <div className="flex gap-4">
                        <div className="flex-1 space-y-1.5">
                            <label className="text-sm text-gray-600">Người bàn giao</label>
                            <div className="relative">
                                <select
                                    value={fromUser}
                                    onChange={e => setFromUser(Number(e.target.value))}
                                    className="w-full appearance-none px-3 py-2 border border-gray-300 rounded-btn text-sm text-text-main bg-white focus:outline-none focus:border-primary"
                                >
                                    {MOCK_USERS.map(u => (
                                        <option key={u.id} value={u.id}>{u.name}</option>
                                    ))}
                                </select>
                                <span className="absolute right-2 top-1/2 -translate-y-1/2 pointer-events-none flex items-center gap-1 text-gray-400">
                                    <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                                    </svg>
                                    <FiUser size={14} />
                                </span>
                            </div>
                        </div>

                        <div className="flex-1 space-y-1.5">
                            <label className="text-sm text-gray-600">Người nhận bàn giao</label>
                            <div className="relative">
                                <select
                                    value={toUser}
                                    onChange={e => setToUser(e.target.value === '' ? '' : Number(e.target.value))}
                                    className="w-full appearance-none px-3 py-2 border border-gray-300 rounded-btn text-sm text-text-main bg-white focus:outline-none focus:border-primary"
                                >
                                    <option value="">- Chọn người dùng -</option>
                                    {MOCK_USERS.filter(u => u.id !== fromUser).map(u => (
                                        <option key={u.id} value={u.id}>{u.name}</option>
                                    ))}
                                </select>
                                <span className="absolute right-2 top-1/2 -translate-y-1/2 pointer-events-none flex items-center gap-1 text-gray-400">
                                    <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                                    </svg>
                                    <FiUser size={14} />
                                </span>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Footer */}
                <div className="flex justify-end gap-2 px-5 py-3 border-t border-gray-200">
                    <button
                        onClick={onClose}
                        className="px-4 py-1.5 rounded-btn border border-gray-300 text-sm text-text-main hover:bg-gray-50"
                    >
                        Hủy
                    </button>
                    <button
                        disabled={!toUser}
                        className="px-4 py-1.5 rounded-btn bg-primary text-white text-sm hover:bg-blue-600 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        Bàn giao
                    </button>
                </div>
            </div>
        </div>
    );
};

export const TransferWorkButton = () => {
    const [open, setOpen] = useState(false);

    return (
        <>
            <button
                onClick={() => setOpen(true)}
                className="p-2 rounded hover:bg-gray-100 text-gray-500 hover:text-text-main"
                title="Bàn giao công việc"
            >
                <FiMoreHorizontal size={18} />
            </button>
            {open && <TransferWorkModal onClose={() => setOpen(false)} />}
        </>
    );
};
