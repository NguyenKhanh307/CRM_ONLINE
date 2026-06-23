import { useEffect, useRef, useState } from 'react';
import axiosInstance from '@/core/axios/axiosInstance';

/** Khóa lưu mã tiềm năng web (dạng TNW...) trên máy khách. */
const STORAGE_KEY = 'tnw_lead_code';

interface TrackedLead {
    code: string;
    score: number;
    status: string;
}

/** Các nút hành động mô phỏng — mỗi nút cộng điểm cho tiềm năng tương ứng. */
const ACTIONS: { action: string; label: string; points: number }[] = [
    { action: 'view_pricing', label: 'Xem bảng giá', points: 15 },
    { action: 'download_brochure', label: 'Tải brochure', points: 20 },
    { action: 'watch_demo', label: 'Xem video demo', points: 10 },
    { action: 'request_quote', label: 'Yêu cầu báo giá', points: 25 },
    { action: 'subscribe', label: 'Đăng ký nhận tin', points: 10 },
];

const SUBMIT_POINTS = 30;

const TrackingDemoPage = () => {
    const [lead, setLead] = useState<TrackedLead | null>(null);
    const [busy, setBusy] = useState(false);
    const [form, setForm] = useState({ name: '', companyName: '', email: '', phone: '', note: '' });
    const [message, setMessage] = useState<string | null>(null);
    const visitedRef = useRef(false);

    const applyLead = (data: TrackedLead) => {
        setLead(data);
        if (data?.code) localStorage.setItem(STORAGE_KEY, data.code);
        if (data?.status === 'qualified') {
            setMessage(`Tiềm năng ${data.code} đã đạt ${data.score} điểm — đủ điều kiện (qualified)! Đội ngũ phụ trách đã được thông báo.`);
        }
    };

    // Lần truy cập đầu: sinh/khôi phục mã TNW và tạo record tiềm năng ẩn danh.
    // visitedRef chống StrictMode gọi effect 2 lần (tránh tạo 2 lead song song → trùng mã).
    useEffect(() => {
        if (visitedRef.current) return;
        visitedRef.current = true;
        const code = localStorage.getItem(STORAGE_KEY);
        axiosInstance
            .post('/api/tracking/visit', { code })
            .then((r) => applyLead(r.data.data))
            .catch(() => setMessage('Không kết nối được máy chủ tracking.'));
    }, []);

    const sendScore = async (action: string, label: string, points: number) => {
        if (!lead || busy) return;
        setBusy(true);
        try {
            const r = await axiosInstance.post('/api/tracking/score', { code: lead.code, action, label, points });
            applyLead(r.data.data);
        } finally {
            setBusy(false);
        }
    };

    const submitForm = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!lead || busy) return;
        setBusy(true);
        try {
            const r = await axiosInstance.post('/api/tracking/submit', {
                code: lead.code, ...form, points: SUBMIT_POINTS,
            });
            applyLead(r.data.data);
            setMessage(`Cảm ơn bạn! Thông tin đã được ghi nhận (+${SUBMIT_POINTS} điểm).`);
        } finally {
            setBusy(false);
        }
    };

    const set = (patch: Partial<typeof form>) => setForm((f) => ({ ...f, ...patch }));
    const inputCls = 'w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-500';
    const qualified = lead?.status === 'qualified';

    return (
        <div className="min-h-screen bg-gray-50 py-10 px-4">
            <div className="max-w-2xl mx-auto space-y-6">
                <header className="text-center">
                    <h1 className="text-2xl font-bold text-gray-800">Demo Website — Web Tracking</h1>
                    <p className="text-sm text-gray-500 mt-1">Trang mô phỏng gắn mã tracking vào trình duyệt khách truy cập.</p>
                </header>

                {/* Tracking status card */}
                <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-5">
                    <div className="flex items-center justify-between">
                        <div>
                            <div className="text-xs text-gray-400 uppercase">Mã tiềm năng (cookie)</div>
                            <div className="text-lg font-mono font-semibold text-gray-800">{lead?.code ?? '...'}</div>
                        </div>
                        <div className="text-right">
                            <div className="text-xs text-gray-400 uppercase">Điểm</div>
                            <div className={`text-2xl font-bold ${qualified ? 'text-green-600' : 'text-blue-600'}`}>{lead?.score ?? 0}</div>
                        </div>
                    </div>
                    {qualified && (
                        <div className="mt-3 inline-block bg-green-100 text-green-700 text-xs font-semibold px-2.5 py-1 rounded-full">
                            ✔ Qualified (&gt; 50 điểm)
                        </div>
                    )}
                    <pre className="mt-4 bg-gray-900 text-green-400 text-xs rounded p-3 overflow-x-auto">
{`<script>
  // Tracking snippet (mô phỏng)
  window.TNW_LEAD = "${lead?.code ?? ''}";
</script>`}
                    </pre>
                </div>

                {message && (
                    <div className="bg-blue-50 border border-blue-200 text-blue-800 text-sm rounded-lg px-4 py-3">{message}</div>
                )}

                {/* Action buttons */}
                <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-5">
                    <h2 className="font-semibold text-gray-700 mb-3">Hành vi người dùng (bấm để cộng điểm)</h2>
                    <div className="flex flex-wrap gap-2">
                        {ACTIONS.map((a) => (
                            <button
                                key={a.action}
                                disabled={busy || !lead}
                                onClick={() => sendScore(a.action, a.label, a.points)}
                                className="px-3 py-2 rounded-lg border border-gray-300 text-sm text-gray-700 hover:bg-blue-50 hover:border-blue-400 disabled:opacity-50"
                            >
                                {a.label} <span className="text-blue-600 font-semibold">+{a.points}</span>
                            </button>
                        ))}
                    </div>
                </div>

                {/* Contact form */}
                <form onSubmit={submitForm} className="bg-white rounded-xl shadow-sm border border-gray-200 p-5 space-y-3">
                    <h2 className="font-semibold text-gray-700">Form liên hệ (nộp để cập nhật thông tin +{SUBMIT_POINTS} điểm)</h2>
                    <div className="grid grid-cols-2 gap-3">
                        <input className={inputCls} placeholder="Họ tên" value={form.name} onChange={(e) => set({ name: e.target.value })} />
                        <input className={inputCls} placeholder="Công ty" value={form.companyName} onChange={(e) => set({ companyName: e.target.value })} />
                        <input className={inputCls} placeholder="Email" value={form.email} onChange={(e) => set({ email: e.target.value })} />
                        <input className={inputCls} placeholder="Số điện thoại" value={form.phone} onChange={(e) => set({ phone: e.target.value })} />
                    </div>
                    <textarea className={inputCls} rows={2} placeholder="Ghi chú" value={form.note} onChange={(e) => set({ note: e.target.value })} />
                    <button
                        type="submit"
                        disabled={busy || !lead}
                        className="w-full bg-blue-600 text-white rounded-lg py-2.5 text-sm font-semibold hover:bg-blue-700 disabled:opacity-50"
                    >
                        Gửi thông tin
                    </button>
                </form>
            </div>
        </div>
    );
};

export default TrackingDemoPage;
