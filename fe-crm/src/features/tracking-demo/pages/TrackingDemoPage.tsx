import { useEffect, useMemo, useRef, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { UTM_PARAM } from '../config/trackingDemoConfig';
import { useTrackingCampaigns } from '../hooks/useTrackingCampaigns';
import { useTrackingSession } from '../hooks/useTrackingSession';
import { CampaignPicker } from '../components/CampaignPicker';
import { SessionCard } from '../components/SessionCard';
import { BehaviorPanel } from '../components/BehaviorPanel';
import { LeadContactForm } from '../components/LeadContactForm';
import { CrmEffectPanel } from '../components/CrmEffectPanel';

/**
 * Trang mô phỏng landing page có gắn web tracking (route công khai `/tracking-demo`).
 *
 * Kịch bản demo đi theo 4 bước đánh số: chọn chiến dịch → mở phiên → sinh hành vi → nộp form,
 * kèm bảng giải thích hệ quả tương ứng bên trong CRM.
 */
const TrackingDemoPage = () => {
    const [searchParams] = useSearchParams();
    const { data: campaigns = [], isLoading } = useTrackingCampaigns();
    const { lead, events, busy, message, restoring, start, track, submit, reset } = useTrackingSession();

    const [picked, setPicked] = useState<number | null>(null);
    /** Chặn tự mở phiên nhiều lần khi vào bằng link có sẵn utm_campaign. */
    const autoStartedRef = useRef(false);

    /** Mã chiến dịch trên URL — mô phỏng khách bấm vào quảng cáo thật. */
    const utmCode = searchParams.get(UTM_PARAM);

    // Vào bằng link quảng cáo: chọn sẵn chiến dịch và mở phiên luôn, không bắt bấm nút.
    useEffect(() => {
        if (restoring || lead || autoStartedRef.current || !utmCode || campaigns.length === 0) return;
        const match = campaigns.find((c) => c.code.toLowerCase() === utmCode.toLowerCase());
        if (!match) return;
        autoStartedRef.current = true;
        setPicked(match.id);
        void start(match.id);
    }, [restoring, lead, utmCode, campaigns, start]);

    /** Chiến dịch thực sự đã gắn vào tiềm năng (đọc ngược từ dữ liệu backend trả về). */
    const attached = useMemo(
        () => (lead?.campaignId ? campaigns.find((c) => c.id === lead.campaignId) ?? null : null),
        [lead, campaigns],
    );

    return (
        <div className="min-h-screen bg-gray-50 px-4 py-10">
            <div className="mx-auto max-w-3xl space-y-5">
                <header className="text-center">
                    <h1 className="text-2xl font-bold text-gray-800">Landing page demo — Web Tracking</h1>
                    <p className="mx-auto mt-1 max-w-xl text-sm text-gray-500">
                        Trang này đóng vai website công ty. Nó gắn mã theo dõi vào trình duyệt khách,
                        chấm điểm theo hành vi và đẩy tiềm năng thẳng vào CRM.
                    </p>
                </header>

                <CampaignPicker
                    campaigns={campaigns}
                    isLoading={isLoading}
                    value={picked}
                    onChange={setPicked}
                    locked={!!lead}
                    attached={attached}
                />

                {message && (
                    <div className="rounded-lg border border-blue-200 bg-blue-50 px-4 py-3 text-sm text-blue-800">{message}</div>
                )}

                {!lead ? (
                    <section className="rounded-xl border border-dashed border-gray-300 bg-white p-6 text-center">
                        <p className="mb-3 text-sm text-gray-500">
                            {restoring
                                ? 'Đang kiểm tra trình duyệt xem đã có mã theo dõi chưa...'
                                : 'Bấm để mô phỏng một khách truy cập website qua link quảng cáo ở trên.'}
                        </p>
                        <button
                            type="button"
                            disabled={busy || restoring}
                            onClick={() => void start(picked)}
                            className="rounded-lg bg-blue-600 px-5 py-2.5 text-sm font-semibold text-white hover:bg-blue-700 disabled:opacity-50"
                        >
                            Bắt đầu phiên truy cập
                        </button>
                    </section>
                ) : (
                    <>
                        <SessionCard lead={lead} onReset={reset} />
                        <BehaviorPanel events={events} busy={busy} onTrack={(a, l, p) => void track(a, l, p)} />
                        <LeadContactForm busy={busy} onSubmit={(form) => void submit(form)} />
                        <CrmEffectPanel hasCampaign={!!lead.campaignId} qualified={lead.status === 'qualified'} />
                    </>
                )}
            </div>
        </div>
    );
};

export default TrackingDemoPage;
