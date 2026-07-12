package vn.com.be_crm.application.activity.query;

import vn.com.be_crm.application.activity.dto.ActivityResult;
import vn.com.be_crm.application.activity.mapper.ActivityCommandMapper;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.application.shared.lookup.INameResolver;
import vn.com.be_crm.application.shared.lookup.NameEnricher;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.activity.repository.IActivityRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách hoạt động có phân trang. */
public class ListActivityUseCase implements IUseCase<PageRequest, PageResult<ActivityResult>> {
    private final IActivityRepository repository;
    private final INameResolver names;
    /** @param repository port lưu trữ Activity @param names port tra tên khóa ngoại */
    public ListActivityUseCase(IActivityRepository repository, INameResolver names) { this.repository = repository; this.names = names; }
    /**
     * Lấy danh sách Activity theo tham số phân trang, kèm tên người phụ trách.
     * @param request tham số phân trang
     * @return PageResult chứa danh sách ActivityResult
     */
    @Override
    public PageResult<ActivityResult> execute(PageRequest request) {
        var page = repository.findAll(request);
        List<ActivityResult> items = page.getItems().stream().map(ActivityCommandMapper::toResult).collect(Collectors.toList());
        NameEnricher.apply(items, ActivityResult::getAssignedUserId, names::users, ActivityResult::setAssignedUserName);
        NameEnricher.apply(items, ActivityResult::getCreatedBy, names::users, ActivityResult::setCreatedByName);
        NameEnricher.apply(items, ActivityResult::getUpdatedBy, names::users, ActivityResult::setUpdatedByName);
        return PageResult.<ActivityResult>builder()
                .items(items)
                .total(page.getTotal()).page(page.getPage()).size(page.getSize()).build();
    }
}
