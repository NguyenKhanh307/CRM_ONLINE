package vn.com.be_crm.application.dashboard.dto;

import java.util.List;

/**
 * Một hàng của biểu đồ thanh ngang xếp chồng theo nhóm (nhân viên / đơn vị).
 *
 * @param group    tên nhóm (vd tên nhân viên)
 * @param segments các phần trạng thái trong nhóm
 */
public record GroupedStatusRow(String group, List<DonutSegment> segments) {
}
