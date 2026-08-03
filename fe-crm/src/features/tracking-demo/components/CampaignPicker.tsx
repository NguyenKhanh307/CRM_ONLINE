import { FiExternalLink } from 'react-icons/fi';
import { buildLandingUrl } from '../config/trackingDemoConfig';
import type { PublicCampaign } from '../types/trackingTypes';

interface Props {
    campaigns: PublicCampaign[];
    isLoading: boolean;
    // chiến dịch đang chọn (chưa mở phiên)
    value: number | null;
    onChange: (id: number | null) => void;
    // đã mở phiên -> khóa lựa chọn, chỉ hiện chiến dịch đã gắn
    locked: boolean;
    // chiến dịch thực sự đã gắn vào tiềm năng (sau khi mở phiên)
    attached: PublicCampaign | null;
}

// bước 1 — chọn chiến dịch nguồn, kèm url quảng cáo minh họa: đổi lựa chọn thì `utm_campaign`
// trên url đổi theo, cho thấy đúng cách web tracking thật quy nguồn tiềm năng
export const CampaignPicker = ({ campaigns, isLoading, value, onChange, locked, attached }: Props) => {
    const selected = campaigns.find((c) => c.id === (locked ? attached?.id : value)) ?? attached ?? null;
    const url = buildLandingUrl(selected?.code ?? null);

    return (
        <section className="bg-white rounded-xl shadow-sm border border-gray-200 p-5">
            <div className="flex items-baseline gap-2 mb-1">
                <span className="flex h-6 w-6 items-center justify-center rounded-full bg-blue-600 text-white text-xs font-bold">1</span>
                <h2 className="font-semibold text-gray-800">Chiến dịch nguồn</h2>
            </div>
            <p className="text-sm text-gray-500 mb-3 ml-8">
                Khách bấm vào quảng cáo của chiến dịch nào thì tiềm năng sinh ra được quy về chiến dịch đó —
                đây là gốc của báo cáo ROI (<code className="text-xs">campaign_id</code> chảy tiếp sang cơ hội, đơn hàng, hóa đơn).
            </p>

            <div className="ml-8 space-y-3">
                {locked ? (
                    <div className="flex items-center gap-2 text-sm">
                        <span className="text-gray-500">Đã gắn:</span>
                        {attached ? (
                            <span className="inline-flex items-center gap-1.5 rounded-full bg-green-100 px-3 py-1 font-medium text-green-700">
                                {attached.code} — {attached.name}
                            </span>
                        ) : (
                            <span className="inline-flex items-center rounded-full bg-gray-100 px-3 py-1 text-gray-500">
                                Không có (khách vào thẳng, không qua chiến dịch)
                            </span>
                        )}
                    </div>
                ) : (
                    <select
                        value={value ?? ''}
                        disabled={isLoading}
                        onChange={(e) => onChange(e.target.value ? Number(e.target.value) : null)}
                        className="w-full rounded border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none disabled:bg-gray-50"
                    >
                        <option value="">
                            {isLoading ? 'Đang tải chiến dịch...' : '— Không qua chiến dịch (khách vào thẳng) —'}
                        </option>
                        {campaigns.map((c) => (
                            <option key={c.id} value={c.id}>{c.code} — {c.name}</option>
                        ))}
                    </select>
                )}

                {!isLoading && campaigns.length === 0 && !locked && (
                    <p className="text-sm text-amber-600">
                        Chưa có chiến dịch nào ở trạng thái "Đang chạy" hoặc "Đã lên lịch" — vẫn demo được, tiềm năng sẽ không gắn nguồn.
                    </p>
                )}

                <div>
                    <div className="mb-1 flex items-center gap-1.5 text-xs uppercase tracking-wide text-gray-400">
                        <FiExternalLink size={12} /> Link quảng cáo tương ứng
                    </div>
                    <code className="block break-all rounded bg-gray-900 px-3 py-2 text-xs text-green-400">{url}</code>
                    <p className="mt-1 text-xs text-gray-400">
                        Mở trang này kèm <code>?utm_campaign=&lt;mã&gt;</code> thì chiến dịch được chọn sẵn và phiên tự bắt đầu.
                    </p>
                </div>
            </div>
        </section>
    );
};
