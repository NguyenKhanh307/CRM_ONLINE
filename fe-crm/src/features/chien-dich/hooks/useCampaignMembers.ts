import { useLiveQuery } from '@/core/data/useLiveQuery';
import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { campaignService } from '../services/campaignService';
import type { CreateCampaignMemberPayload } from '../types/campaignTypes';

// lấy danh sách thành viên của một Chiến dịch
export function useCampaignMembers(campaignId: number) {
    return useLiveQuery(`campaign-members:${campaignId}`, () => campaignService.getMembers(campaignId).then(r => r.data.data), !!campaignId);
}

// thêm thành viên vào Chiến dịch — cùng lúc làm mới thống kê ROI
export function useCreateCampaignMember(campaignId: number) {
    const { mutate: run, isPending } = useLiveMutation(
        (payload: CreateCampaignMemberPayload) => campaignService.createMember(campaignId, payload));

    const mutate: typeof run = (payload, callbacks) =>
        run(payload, {
            ...callbacks,
            onSuccess: (data) => {
                notify(`campaign-members:${campaignId}`);
                notify(`campaign-stats:${campaignId}`);
                callbacks?.onSuccess?.(data);
            },
        });

    return { mutate, isPending };
}

// xóa thành viên khỏi Chiến dịch — cùng lúc làm mới thống kê ROI
export function useDeleteCampaignMember(campaignId: number) {
    const { mutate: run, isPending } = useLiveMutation((memberId: number) => campaignService.deleteMember(campaignId, memberId));

    const mutate: typeof run = (memberId, callbacks) =>
        run(memberId, {
            ...callbacks,
            onSuccess: (data) => {
                notify(`campaign-members:${campaignId}`);
                notify(`campaign-stats:${campaignId}`);
                callbacks?.onSuccess?.(data);
            },
        });

    return { mutate, isPending };
}
