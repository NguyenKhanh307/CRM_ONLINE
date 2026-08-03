import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { campaignService } from '../services/campaignService';
import type { CreateCampaignMemberPayload } from '../types/campaignTypes';

// lấy danh sách thành viên của một Chiến dịch
export function useCampaignMembers(campaignId: number) {
    return useQuery({
        queryKey: ['campaign-members', campaignId],
        queryFn: () => campaignService.getMembers(campaignId).then(r => r.data.data),
        enabled: !!campaignId,
    });
}

// thêm thành viên vào Chiến dịch — cùng lúc làm mới thống kê ROI
export function useCreateCampaignMember(campaignId: number) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: CreateCampaignMemberPayload) => campaignService.createMember(campaignId, payload),
        onSuccess: () => {
            qc.invalidateQueries({ queryKey: ['campaign-members', campaignId] });
            qc.invalidateQueries({ queryKey: ['campaign-stats', campaignId] });
        },
    });
}

// xóa thành viên khỏi Chiến dịch — cùng lúc làm mới thống kê ROI
export function useDeleteCampaignMember(campaignId: number) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (memberId: number) => campaignService.deleteMember(campaignId, memberId),
        onSuccess: () => {
            qc.invalidateQueries({ queryKey: ['campaign-members', campaignId] });
            qc.invalidateQueries({ queryKey: ['campaign-stats', campaignId] });
        },
    });
}
