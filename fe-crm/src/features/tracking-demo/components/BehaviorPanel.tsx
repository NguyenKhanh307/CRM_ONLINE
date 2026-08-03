import { ScrollFrame } from '@/shared/components/table/ScrollFrame';
import { TRACKING_ACTIONS } from '../config/trackingDemoConfig';
import type { SessionEvent } from '../types/trackingTypes';

interface Props {
    events: SessionEvent[];
    busy: boolean;
    onTrack: (action: string, label: string, points: number) => void;
}

// bước 3 — hành vi trên website, mỗi nút ghi một dòng `lead_tracking_events` ở backend
// và cộng điểm; nhật ký bên dưới cho thấy đúng thứ tự đã xảy ra trong phiên
export const BehaviorPanel = ({ events, busy, onTrack }: Props) => (
    <section className="bg-white rounded-xl shadow-sm border border-gray-200 p-5">
        <div className="flex items-baseline gap-2 mb-1">
            <span className="flex h-6 w-6 items-center justify-center rounded-full bg-blue-600 text-white text-xs font-bold">3</span>
            <h2 className="font-semibold text-gray-800">Hành vi trên website</h2>
        </div>
        <p className="ml-8 mb-3 text-sm text-gray-500">
            Hành vi càng thể hiện ý định mua thì điểm càng cao. Bấm thử vài nút để thấy điểm dồn lên.
        </p>

        <div className="ml-8 space-y-4">
            <div className="grid grid-cols-1 gap-2 sm:grid-cols-2">
                {TRACKING_ACTIONS.map((a) => (
                    <button
                        key={a.action}
                        type="button"
                        disabled={busy}
                        onClick={() => onTrack(a.action, a.label, a.points)}
                        className="flex items-center justify-between rounded-lg border border-gray-300 px-3 py-2 text-left text-sm text-gray-700 hover:border-blue-400 hover:bg-blue-50 disabled:opacity-50"
                    >
                        <span>
                            {a.label}
                            <span className="block text-xs text-gray-400">{a.hint}</span>
                        </span>
                        <span className="font-semibold text-blue-600">+{a.points}</span>
                    </button>
                ))}
            </div>

            <div>
                <div className="mb-1 text-xs uppercase tracking-wide text-gray-400">
                    Nhật ký phiên ({events.length} sự kiện)
                </div>
                {events.length === 0 ? (
                    <p className="rounded border border-dashed border-gray-200 px-3 py-4 text-center text-sm text-gray-400">
                        Chưa có hành vi nào — bấm một nút phía trên.
                    </p>
                ) : (
                    <ScrollFrame maxHeight={180} className="rounded border border-gray-200">
                        <ul className="divide-y divide-gray-100">
                            {events.map((e, i) => (
                                <li key={i} className="flex items-center gap-3 px-3 py-2 text-sm">
                                    <span className="font-mono text-xs text-gray-400">{e.at}</span>
                                    <span className="flex-1 truncate text-gray-700">{e.label}</span>
                                    <span className="font-semibold text-blue-600">+{e.points}</span>
                                </li>
                            ))}
                        </ul>
                    </ScrollFrame>
                )}
            </div>
        </div>
    </section>
);
