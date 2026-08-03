import { FiRotateCcw } from 'react-icons/fi';
import { QUALIFY_THRESHOLD } from '../config/trackingDemoConfig';
import type { TrackedLead } from '../types/trackingTypes';

interface Props {
    lead: TrackedLead;
    onReset: () => void;
}

// nhãn trạng thái tiềm năng — khớp enum `LeadStatus` phía backend
const STATUS_LABELS: Record<string, string> = {
    new: 'Mới', contacting: 'Đang liên hệ', qualified: 'Đủ điều kiện',
    converted: 'Đã chuyển đổi', lost: 'Thất bại',
};

// bước 2 — phiên truy cập hiện tại: mã tiềm năng, điểm, tiến độ tới ngưỡng qualified
// thanh tiến trình cho thấy trực quan còn bao nhiêu điểm nữa thì backend tự chuyển trạng thái
export const SessionCard = ({ lead, onReset }: Props) => {
    const pct = Math.min(100, Math.round((lead.score / QUALIFY_THRESHOLD) * 100));
    const qualified = lead.status === 'qualified';
    const remaining = Math.max(0, QUALIFY_THRESHOLD + 1 - lead.score);

    return (
        <section className="bg-white rounded-xl shadow-sm border border-gray-200 p-5">
            <div className="flex items-baseline gap-2 mb-3">
                <span className="flex h-6 w-6 items-center justify-center rounded-full bg-blue-600 text-white text-xs font-bold">2</span>
                <h2 className="font-semibold text-gray-800">Phiên truy cập</h2>
                <button
                    type="button"
                    onClick={onReset}
                    className="ml-auto flex items-center gap-1.5 rounded border border-gray-300 px-2.5 py-1 text-xs text-gray-600 hover:bg-gray-50"
                    title="Xóa mã trên trình duyệt để demo lại với chiến dịch khác"
                >
                    <FiRotateCcw size={12} /> Đặt lại phiên
                </button>
            </div>

            <div className="ml-8 space-y-4">
                <div className="flex items-end justify-between">
                    <div>
                        <div className="text-xs uppercase tracking-wide text-gray-400">Mã tiềm năng (lưu trên trình duyệt)</div>
                        <div className="font-mono text-lg font-semibold text-gray-800">{lead.code}</div>
                    </div>
                    <div className="text-right">
                        <div className="text-xs uppercase tracking-wide text-gray-400">Điểm</div>
                        <div className={`text-2xl font-bold ${qualified ? 'text-green-600' : 'text-blue-600'}`}>{lead.score}</div>
                    </div>
                </div>

                <div>
                    <div className="mb-1 flex justify-between text-xs text-gray-500">
                        <span>Tiến độ tới ngưỡng đủ điều kiện</span>
                        <span>{lead.score} / {QUALIFY_THRESHOLD}</span>
                    </div>
                    <div className="h-2 overflow-hidden rounded-full bg-gray-100">
                        <div
                            className={`h-full rounded-full transition-all ${qualified ? 'bg-green-500' : 'bg-blue-500'}`}
                            style={{ width: `${pct}%` }}
                        />
                    </div>
                    <p className="mt-1 text-xs text-gray-400">
                        {qualified
                            ? 'Đã vượt ngưỡng — backend đã tự chuyển trạng thái và gửi thông báo.'
                            : `Còn ${remaining} điểm nữa là tự chuyển sang "Đủ điều kiện".`}
                    </p>
                </div>

                <div className="flex flex-wrap items-center gap-2 text-sm">
                    <span className="text-gray-500">Trạng thái:</span>
                    <span className={`rounded-full px-2.5 py-1 text-xs font-semibold ${
                        qualified ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-600'
                    }`}>
                        {STATUS_LABELS[lead.status] ?? lead.status}
                    </span>
                </div>

                <div>
                    <div className="mb-1 text-xs uppercase tracking-wide text-gray-400">Đoạn mã nhúng trên website (mô phỏng)</div>
                    <pre className="overflow-x-auto rounded bg-gray-900 p-3 text-xs text-green-400">
{`<script src="https://cdn.congty-demo.vn/tracking.js"></script>
<script>
  TNW.init({ leadCode: "${lead.code}" });   // đọc từ cookie, gửi kèm mọi sự kiện
</script>`}
                    </pre>
                </div>
            </div>
        </section>
    );
};
