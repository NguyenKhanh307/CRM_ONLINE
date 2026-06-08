package vn.com.be_crm.infrastructure.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.application.activity.command.*;
import vn.com.be_crm.application.activity.query.*;
import vn.com.be_crm.application.auth.command.*;
import vn.com.be_crm.application.auth.query.*;
import vn.com.be_crm.application.opportunity.command.*;
import vn.com.be_crm.application.opportunity.query.*;
import vn.com.be_crm.application.warehouse.command.*;
import vn.com.be_crm.application.warehouse.query.*;
import vn.com.be_crm.application.product.command.*;
import vn.com.be_crm.application.product.query.*;
import vn.com.be_crm.domain.activity.repository.IActivityRepository;
import vn.com.be_crm.domain.auth.repository.*;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityStageRepository;
import vn.com.be_crm.domain.product.repository.*;
import vn.com.be_crm.domain.warehouse.repository.*;

/**
 * Wire tất cả UseCase với Repository tương ứng qua @Bean.
 * Thêm @Bean theo từng module khi implement thêm.
 */
@Configuration
public class BeanConfig {

    // =====================================================================
    // MODULE AUTH — OrgUnit
    // =====================================================================

    /** @return CreateOrgUnitUseCase được inject IOrgUnitRepository */
    @Bean
    public CreateOrgUnitUseCase createOrgUnitUseCase(IOrgUnitRepository repo) {
        return new CreateOrgUnitUseCase(repo);
    }

    /** @return UpdateOrgUnitUseCase được inject IOrgUnitRepository */
    @Bean
    public UpdateOrgUnitUseCase updateOrgUnitUseCase(IOrgUnitRepository repo) {
        return new UpdateOrgUnitUseCase(repo);
    }

    /** @return DeleteOrgUnitUseCase được inject IOrgUnitRepository */
    @Bean
    public DeleteOrgUnitUseCase deleteOrgUnitUseCase(IOrgUnitRepository repo) {
        return new DeleteOrgUnitUseCase(repo);
    }

    /** @return GetOrgUnitUseCase được inject IOrgUnitRepository */
    @Bean
    public GetOrgUnitUseCase getOrgUnitUseCase(IOrgUnitRepository repo) {
        return new GetOrgUnitUseCase(repo);
    }

    /** @return ListOrgUnitUseCase được inject IOrgUnitRepository */
    @Bean
    public ListOrgUnitUseCase listOrgUnitUseCase(IOrgUnitRepository repo) {
        return new ListOrgUnitUseCase(repo);
    }

    // =====================================================================
    // MODULE AUTH — User
    // =====================================================================

    /** @return CreateUserUseCase được inject IUserRepository */
    @Bean
    public CreateUserUseCase createUserUseCase(IUserRepository repo) {
        return new CreateUserUseCase(repo);
    }

    /** @return UpdateUserUseCase được inject IUserRepository */
    @Bean
    public UpdateUserUseCase updateUserUseCase(IUserRepository repo) {
        return new UpdateUserUseCase(repo);
    }

    /** @return DeleteUserUseCase được inject IUserRepository */
    @Bean
    public DeleteUserUseCase deleteUserUseCase(IUserRepository repo) {
        return new DeleteUserUseCase(repo);
    }

    /** @return GetUserUseCase được inject IUserRepository */
    @Bean
    public GetUserUseCase getUserUseCase(IUserRepository repo) {
        return new GetUserUseCase(repo);
    }

    /** @return ListUserUseCase được inject IUserRepository */
    @Bean
    public ListUserUseCase listUserUseCase(IUserRepository repo) {
        return new ListUserUseCase(repo);
    }

    // =====================================================================
    // MODULE AUTH — Role
    // =====================================================================

    /** @return CreateRoleUseCase được inject IRoleRepository */
    @Bean
    public CreateRoleUseCase createRoleUseCase(IRoleRepository repo) {
        return new CreateRoleUseCase(repo);
    }

    /** @return UpdateRoleUseCase được inject IRoleRepository */
    @Bean
    public UpdateRoleUseCase updateRoleUseCase(IRoleRepository repo) {
        return new UpdateRoleUseCase(repo);
    }

    /** @return DeleteRoleUseCase được inject IRoleRepository */
    @Bean
    public DeleteRoleUseCase deleteRoleUseCase(IRoleRepository repo) {
        return new DeleteRoleUseCase(repo);
    }

    /** @return GetRoleUseCase được inject IRoleRepository */
    @Bean
    public GetRoleUseCase getRoleUseCase(IRoleRepository repo) {
        return new GetRoleUseCase(repo);
    }

    /** @return ListRoleUseCase được inject IRoleRepository */
    @Bean
    public ListRoleUseCase listRoleUseCase(IRoleRepository repo) {
        return new ListRoleUseCase(repo);
    }

    // =====================================================================
    // MODULE AUTH — Permission
    // =====================================================================

    /** @return CreatePermissionUseCase được inject IPermissionRepository */
    @Bean
    public CreatePermissionUseCase createPermissionUseCase(IPermissionRepository repo) {
        return new CreatePermissionUseCase(repo);
    }

    /** @return UpdatePermissionUseCase được inject IPermissionRepository */
    @Bean
    public UpdatePermissionUseCase updatePermissionUseCase(IPermissionRepository repo) {
        return new UpdatePermissionUseCase(repo);
    }

    /** @return DeletePermissionUseCase được inject IPermissionRepository */
    @Bean
    public DeletePermissionUseCase deletePermissionUseCase(IPermissionRepository repo) {
        return new DeletePermissionUseCase(repo);
    }

    /** @return GetPermissionUseCase được inject IPermissionRepository */
    @Bean
    public GetPermissionUseCase getPermissionUseCase(IPermissionRepository repo) {
        return new GetPermissionUseCase(repo);
    }

    /** @return ListPermissionUseCase được inject IPermissionRepository */
    @Bean
    public ListPermissionUseCase listPermissionUseCase(IPermissionRepository repo) {
        return new ListPermissionUseCase(repo);
    }

    // =====================================================================
    // MODULE AUTH — RolePermission & UserRole (N:N)
    // =====================================================================

    /** @return AssignRolePermissionUseCase được inject IRolePermissionRepository */
    @Bean
    public AssignRolePermissionUseCase assignRolePermissionUseCase(IRolePermissionRepository repo) {
        return new AssignRolePermissionUseCase(repo);
    }

    /** @return RevokeRolePermissionUseCase được inject IRolePermissionRepository */
    @Bean
    public RevokeRolePermissionUseCase revokeRolePermissionUseCase(IRolePermissionRepository repo) {
        return new RevokeRolePermissionUseCase(repo);
    }

    /** @return AssignUserRoleUseCase được inject IUserRoleRepository */
    @Bean
    public AssignUserRoleUseCase assignUserRoleUseCase(IUserRoleRepository repo) {
        return new AssignUserRoleUseCase(repo);
    }

    /** @return RevokeUserRoleUseCase được inject IUserRoleRepository */
    @Bean
    public RevokeUserRoleUseCase revokeUserRoleUseCase(IUserRoleRepository repo) {
        return new RevokeUserRoleUseCase(repo);
    }

    // =====================================================================
    // MODULE ACTIVITY
    // =====================================================================

    /** @return CreateActivityUseCase được inject IActivityRepository */
    @Bean public CreateActivityUseCase createActivityUseCase(IActivityRepository r) { return new CreateActivityUseCase(r); }
    /** @return UpdateActivityUseCase được inject IActivityRepository */
    @Bean public UpdateActivityUseCase updateActivityUseCase(IActivityRepository r) { return new UpdateActivityUseCase(r); }
    /** @return DeleteActivityUseCase được inject IActivityRepository */
    @Bean public DeleteActivityUseCase deleteActivityUseCase(IActivityRepository r) { return new DeleteActivityUseCase(r); }
    /** @return GetActivityUseCase được inject IActivityRepository */
    @Bean public GetActivityUseCase getActivityUseCase(IActivityRepository r) { return new GetActivityUseCase(r); }
    /** @return ListActivityUseCase được inject IActivityRepository */
    @Bean public ListActivityUseCase listActivityUseCase(IActivityRepository r) { return new ListActivityUseCase(r); }

    // =====================================================================
    // MODULE PRODUCT
    // =====================================================================

    /** @return CreateProductCategoryUseCase */
    @Bean public CreateProductCategoryUseCase createProductCategoryUseCase(IProductCategoryRepository r) { return new CreateProductCategoryUseCase(r); }
    /** @return UpdateProductCategoryUseCase */
    @Bean public UpdateProductCategoryUseCase updateProductCategoryUseCase(IProductCategoryRepository r) { return new UpdateProductCategoryUseCase(r); }
    /** @return DeleteProductCategoryUseCase */
    @Bean public DeleteProductCategoryUseCase deleteProductCategoryUseCase(IProductCategoryRepository r) { return new DeleteProductCategoryUseCase(r); }
    /** @return GetProductCategoryUseCase */
    @Bean public GetProductCategoryUseCase getProductCategoryUseCase(IProductCategoryRepository r) { return new GetProductCategoryUseCase(r); }
    /** @return ListProductCategoryUseCase */
    @Bean public ListProductCategoryUseCase listProductCategoryUseCase(IProductCategoryRepository r) { return new ListProductCategoryUseCase(r); }

    /** @return CreateProductUseCase */
    @Bean public CreateProductUseCase createProductUseCase(IProductRepository r) { return new CreateProductUseCase(r); }
    /** @return UpdateProductUseCase */
    @Bean public UpdateProductUseCase updateProductUseCase(IProductRepository r) { return new UpdateProductUseCase(r); }
    /** @return DeleteProductUseCase */
    @Bean public DeleteProductUseCase deleteProductUseCase(IProductRepository r) { return new DeleteProductUseCase(r); }
    /** @return GetProductUseCase */
    @Bean public GetProductUseCase getProductUseCase(IProductRepository r) { return new GetProductUseCase(r); }
    /** @return ListProductUseCase */
    @Bean public ListProductUseCase listProductUseCase(IProductRepository r) { return new ListProductUseCase(r); }

    // =====================================================================
    // MODULE OPPORTUNITY STAGES
    // =====================================================================
    /** @return CreateOpportunityStageUseCase */
    @Bean public CreateOpportunityStageUseCase createOpportunityStageUseCase(IOpportunityStageRepository r) { return new CreateOpportunityStageUseCase(r); }
    /** @return UpdateOpportunityStageUseCase */
    @Bean public UpdateOpportunityStageUseCase updateOpportunityStageUseCase(IOpportunityStageRepository r) { return new UpdateOpportunityStageUseCase(r); }
    /** @return DeleteOpportunityStageUseCase */
    @Bean public DeleteOpportunityStageUseCase deleteOpportunityStageUseCase(IOpportunityStageRepository r) { return new DeleteOpportunityStageUseCase(r); }
    /** @return GetOpportunityStageUseCase */
    @Bean public GetOpportunityStageUseCase getOpportunityStageUseCase(IOpportunityStageRepository r) { return new GetOpportunityStageUseCase(r); }
    /** @return ListOpportunityStageUseCase */
    @Bean public ListOpportunityStageUseCase listOpportunityStageUseCase(IOpportunityStageRepository r) { return new ListOpportunityStageUseCase(r); }

    // =====================================================================
    // MODULE WAREHOUSE
    // =====================================================================
    /** @return CreateWarehouseUseCase */
    @Bean public CreateWarehouseUseCase createWarehouseUseCase(IWarehouseRepository r) { return new CreateWarehouseUseCase(r); }
    /** @return UpdateWarehouseUseCase */
    @Bean public UpdateWarehouseUseCase updateWarehouseUseCase(IWarehouseRepository r) { return new UpdateWarehouseUseCase(r); }
    /** @return DeleteWarehouseUseCase */
    @Bean public DeleteWarehouseUseCase deleteWarehouseUseCase(IWarehouseRepository r) { return new DeleteWarehouseUseCase(r); }
    /** @return GetWarehouseUseCase */
    @Bean public GetWarehouseUseCase getWarehouseUseCase(IWarehouseRepository r) { return new GetWarehouseUseCase(r); }
    /** @return ListWarehouseUseCase */
    @Bean public ListWarehouseUseCase listWarehouseUseCase(IWarehouseRepository r) { return new ListWarehouseUseCase(r); }
    /** @return UpsertInventoryStockUseCase */
    @Bean public UpsertInventoryStockUseCase upsertInventoryStockUseCase(IInventoryStockRepository r) { return new UpsertInventoryStockUseCase(r); }
    /** @return GetInventoryStockUseCase */
    @Bean public GetInventoryStockUseCase getInventoryStockUseCase(IInventoryStockRepository r) { return new GetInventoryStockUseCase(r); }
    /** @return ListInventoryStockUseCase */
    @Bean public ListInventoryStockUseCase listInventoryStockUseCase(IInventoryStockRepository r) { return new ListInventoryStockUseCase(r); }
    /** @return AssignWarehousePermissionUseCase */
    @Bean public AssignWarehousePermissionUseCase assignWarehousePermissionUseCase(IWarehousePermissionRepository r) { return new AssignWarehousePermissionUseCase(r); }
    /** @return RevokeWarehousePermissionUseCase */
    @Bean public RevokeWarehousePermissionUseCase revokeWarehousePermissionUseCase(IWarehousePermissionRepository r) { return new RevokeWarehousePermissionUseCase(r); }

    // =====================================================================
    // MODULE CUSTOMER
    // =====================================================================
    /** @return CreateCustomerUseCase */
    @Bean public vn.com.be_crm.application.customer.command.CreateCustomerUseCase createCustomerUseCase(vn.com.be_crm.domain.customer.repository.ICustomerRepository r) { return new vn.com.be_crm.application.customer.command.CreateCustomerUseCase(r); }
    /** @return UpdateCustomerUseCase */
    @Bean public vn.com.be_crm.application.customer.command.UpdateCustomerUseCase updateCustomerUseCase(vn.com.be_crm.domain.customer.repository.ICustomerRepository r) { return new vn.com.be_crm.application.customer.command.UpdateCustomerUseCase(r); }
    /** @return DeleteCustomerUseCase */
    @Bean public vn.com.be_crm.application.customer.command.DeleteCustomerUseCase deleteCustomerUseCase(vn.com.be_crm.domain.customer.repository.ICustomerRepository r) { return new vn.com.be_crm.application.customer.command.DeleteCustomerUseCase(r); }
    /** @return GetCustomerUseCase */
    @Bean public vn.com.be_crm.application.customer.query.GetCustomerUseCase getCustomerUseCase(vn.com.be_crm.domain.customer.repository.ICustomerRepository r) { return new vn.com.be_crm.application.customer.query.GetCustomerUseCase(r); }
    /** @return ListCustomerUseCase */
    @Bean public vn.com.be_crm.application.customer.query.ListCustomerUseCase listCustomerUseCase(vn.com.be_crm.domain.customer.repository.ICustomerRepository r) { return new vn.com.be_crm.application.customer.query.ListCustomerUseCase(r); }
    /** @return AssignCustomerShareUseCase */
    @Bean public vn.com.be_crm.application.customer.command.AssignCustomerShareUseCase assignCustomerShareUseCase(vn.com.be_crm.domain.customer.repository.ICustomerShareRepository r) { return new vn.com.be_crm.application.customer.command.AssignCustomerShareUseCase(r); }
    /** @return RevokeCustomerShareUseCase */
    @Bean public vn.com.be_crm.application.customer.command.RevokeCustomerShareUseCase revokeCustomerShareUseCase(vn.com.be_crm.domain.customer.repository.ICustomerShareRepository r) { return new vn.com.be_crm.application.customer.command.RevokeCustomerShareUseCase(r); }
    /** @return ListCustomerShareUseCase */
    @Bean public vn.com.be_crm.application.customer.query.ListCustomerShareUseCase listCustomerShareUseCase(vn.com.be_crm.domain.customer.repository.ICustomerShareRepository r) { return new vn.com.be_crm.application.customer.query.ListCustomerShareUseCase(r); }
    /** @return CreateInventoryCheckUseCase */
    @Bean public vn.com.be_crm.application.customer.command.CreateInventoryCheckUseCase createInventoryCheckUseCase(vn.com.be_crm.domain.customer.repository.IInventoryCheckRepository r) { return new vn.com.be_crm.application.customer.command.CreateInventoryCheckUseCase(r); }
    /** @return UpdateInventoryCheckUseCase */
    @Bean public vn.com.be_crm.application.customer.command.UpdateInventoryCheckUseCase updateInventoryCheckUseCase(vn.com.be_crm.domain.customer.repository.IInventoryCheckRepository r) { return new vn.com.be_crm.application.customer.command.UpdateInventoryCheckUseCase(r); }
    /** @return DeleteInventoryCheckUseCase */
    @Bean public vn.com.be_crm.application.customer.command.DeleteInventoryCheckUseCase deleteInventoryCheckUseCase(vn.com.be_crm.domain.customer.repository.IInventoryCheckRepository r) { return new vn.com.be_crm.application.customer.command.DeleteInventoryCheckUseCase(r); }
    /** @return GetInventoryCheckUseCase */
    @Bean public vn.com.be_crm.application.customer.query.GetInventoryCheckUseCase getInventoryCheckUseCase(vn.com.be_crm.domain.customer.repository.IInventoryCheckRepository r) { return new vn.com.be_crm.application.customer.query.GetInventoryCheckUseCase(r); }
    /** @return ListInventoryCheckUseCase */
    @Bean public vn.com.be_crm.application.customer.query.ListInventoryCheckUseCase listInventoryCheckUseCase(vn.com.be_crm.domain.customer.repository.IInventoryCheckRepository r) { return new vn.com.be_crm.application.customer.query.ListInventoryCheckUseCase(r); }
    /** @return CreateInventoryCheckItemUseCase */
    @Bean public vn.com.be_crm.application.customer.command.CreateInventoryCheckItemUseCase createInventoryCheckItemUseCase(vn.com.be_crm.domain.customer.repository.IInventoryCheckItemRepository r) { return new vn.com.be_crm.application.customer.command.CreateInventoryCheckItemUseCase(r); }
    /** @return UpdateInventoryCheckItemUseCase */
    @Bean public vn.com.be_crm.application.customer.command.UpdateInventoryCheckItemUseCase updateInventoryCheckItemUseCase(vn.com.be_crm.domain.customer.repository.IInventoryCheckItemRepository r) { return new vn.com.be_crm.application.customer.command.UpdateInventoryCheckItemUseCase(r); }
    /** @return DeleteInventoryCheckItemUseCase */
    @Bean public vn.com.be_crm.application.customer.command.DeleteInventoryCheckItemUseCase deleteInventoryCheckItemUseCase(vn.com.be_crm.domain.customer.repository.IInventoryCheckItemRepository r) { return new vn.com.be_crm.application.customer.command.DeleteInventoryCheckItemUseCase(r); }
    /** @return ListInventoryCheckItemUseCase */
    @Bean public vn.com.be_crm.application.customer.query.ListInventoryCheckItemUseCase listInventoryCheckItemUseCase(vn.com.be_crm.domain.customer.repository.IInventoryCheckItemRepository r) { return new vn.com.be_crm.application.customer.query.ListInventoryCheckItemUseCase(r); }

    // =====================================================================
    // MODULE CONTACT
    // =====================================================================
    /** @return CreateContactUseCase */
    @Bean public vn.com.be_crm.application.contact.command.CreateContactUseCase createContactUseCase(vn.com.be_crm.domain.contact.repository.IContactRepository r) { return new vn.com.be_crm.application.contact.command.CreateContactUseCase(r); }
    /** @return UpdateContactUseCase */
    @Bean public vn.com.be_crm.application.contact.command.UpdateContactUseCase updateContactUseCase(vn.com.be_crm.domain.contact.repository.IContactRepository r) { return new vn.com.be_crm.application.contact.command.UpdateContactUseCase(r); }
    /** @return DeleteContactUseCase */
    @Bean public vn.com.be_crm.application.contact.command.DeleteContactUseCase deleteContactUseCase(vn.com.be_crm.domain.contact.repository.IContactRepository r) { return new vn.com.be_crm.application.contact.command.DeleteContactUseCase(r); }
    /** @return GetContactUseCase */
    @Bean public vn.com.be_crm.application.contact.query.GetContactUseCase getContactUseCase(vn.com.be_crm.domain.contact.repository.IContactRepository r) { return new vn.com.be_crm.application.contact.query.GetContactUseCase(r); }
    /** @return ListContactUseCase */
    @Bean public vn.com.be_crm.application.contact.query.ListContactUseCase listContactUseCase(vn.com.be_crm.domain.contact.repository.IContactRepository r) { return new vn.com.be_crm.application.contact.query.ListContactUseCase(r); }
    /** @return CreateContactPhoneUseCase */
    @Bean public vn.com.be_crm.application.contact.command.CreateContactPhoneUseCase createContactPhoneUseCase(vn.com.be_crm.domain.contact.repository.IContactPhoneRepository r) { return new vn.com.be_crm.application.contact.command.CreateContactPhoneUseCase(r); }
    /** @return UpdateContactPhoneUseCase */
    @Bean public vn.com.be_crm.application.contact.command.UpdateContactPhoneUseCase updateContactPhoneUseCase(vn.com.be_crm.domain.contact.repository.IContactPhoneRepository r) { return new vn.com.be_crm.application.contact.command.UpdateContactPhoneUseCase(r); }
    /** @return DeleteContactPhoneUseCase */
    @Bean public vn.com.be_crm.application.contact.command.DeleteContactPhoneUseCase deleteContactPhoneUseCase(vn.com.be_crm.domain.contact.repository.IContactPhoneRepository r) { return new vn.com.be_crm.application.contact.command.DeleteContactPhoneUseCase(r); }
    /** @return ListContactPhoneUseCase */
    @Bean public vn.com.be_crm.application.contact.query.ListContactPhoneUseCase listContactPhoneUseCase(vn.com.be_crm.domain.contact.repository.IContactPhoneRepository r) { return new vn.com.be_crm.application.contact.query.ListContactPhoneUseCase(r); }

    // =====================================================================
    // MODULE LEAD
    // =====================================================================
    /** @return CreateLeadUseCase */
    @Bean public vn.com.be_crm.application.lead.command.CreateLeadUseCase createLeadUseCase(vn.com.be_crm.domain.lead.repository.ILeadRepository r) { return new vn.com.be_crm.application.lead.command.CreateLeadUseCase(r); }
    /** @return UpdateLeadUseCase */
    @Bean public vn.com.be_crm.application.lead.command.UpdateLeadUseCase updateLeadUseCase(vn.com.be_crm.domain.lead.repository.ILeadRepository r) { return new vn.com.be_crm.application.lead.command.UpdateLeadUseCase(r); }
    /** @return DeleteLeadUseCase */
    @Bean public vn.com.be_crm.application.lead.command.DeleteLeadUseCase deleteLeadUseCase(vn.com.be_crm.domain.lead.repository.ILeadRepository r) { return new vn.com.be_crm.application.lead.command.DeleteLeadUseCase(r); }
    /** @return GetLeadUseCase */
    @Bean public vn.com.be_crm.application.lead.query.GetLeadUseCase getLeadUseCase(vn.com.be_crm.domain.lead.repository.ILeadRepository r) { return new vn.com.be_crm.application.lead.query.GetLeadUseCase(r); }
    /** @return ListLeadUseCase */
    @Bean public vn.com.be_crm.application.lead.query.ListLeadUseCase listLeadUseCase(vn.com.be_crm.domain.lead.repository.ILeadRepository r) { return new vn.com.be_crm.application.lead.query.ListLeadUseCase(r); }
    /** @return CreateLeadActivityUseCase */
    @Bean public vn.com.be_crm.application.lead.command.CreateLeadActivityUseCase createLeadActivityUseCase(vn.com.be_crm.domain.lead.repository.ILeadActivityRepository r) { return new vn.com.be_crm.application.lead.command.CreateLeadActivityUseCase(r); }
    /** @return UpdateLeadActivityUseCase */
    @Bean public vn.com.be_crm.application.lead.command.UpdateLeadActivityUseCase updateLeadActivityUseCase(vn.com.be_crm.domain.lead.repository.ILeadActivityRepository r) { return new vn.com.be_crm.application.lead.command.UpdateLeadActivityUseCase(r); }
    /** @return DeleteLeadActivityUseCase */
    @Bean public vn.com.be_crm.application.lead.command.DeleteLeadActivityUseCase deleteLeadActivityUseCase(vn.com.be_crm.domain.lead.repository.ILeadActivityRepository r) { return new vn.com.be_crm.application.lead.command.DeleteLeadActivityUseCase(r); }
    /** @return ListLeadActivityUseCase */
    @Bean public vn.com.be_crm.application.lead.query.ListLeadActivityUseCase listLeadActivityUseCase(vn.com.be_crm.domain.lead.repository.ILeadActivityRepository r) { return new vn.com.be_crm.application.lead.query.ListLeadActivityUseCase(r); }
    /** @return CreateLeadAttachmentUseCase */
    @Bean public vn.com.be_crm.application.lead.command.CreateLeadAttachmentUseCase createLeadAttachmentUseCase(vn.com.be_crm.domain.lead.repository.ILeadAttachmentRepository r) { return new vn.com.be_crm.application.lead.command.CreateLeadAttachmentUseCase(r); }
    /** @return UpdateLeadAttachmentUseCase */
    @Bean public vn.com.be_crm.application.lead.command.UpdateLeadAttachmentUseCase updateLeadAttachmentUseCase(vn.com.be_crm.domain.lead.repository.ILeadAttachmentRepository r) { return new vn.com.be_crm.application.lead.command.UpdateLeadAttachmentUseCase(r); }
    /** @return DeleteLeadAttachmentUseCase */
    @Bean public vn.com.be_crm.application.lead.command.DeleteLeadAttachmentUseCase deleteLeadAttachmentUseCase(vn.com.be_crm.domain.lead.repository.ILeadAttachmentRepository r) { return new vn.com.be_crm.application.lead.command.DeleteLeadAttachmentUseCase(r); }
    /** @return ListLeadAttachmentUseCase */
    @Bean public vn.com.be_crm.application.lead.query.ListLeadAttachmentUseCase listLeadAttachmentUseCase(vn.com.be_crm.domain.lead.repository.ILeadAttachmentRepository r) { return new vn.com.be_crm.application.lead.query.ListLeadAttachmentUseCase(r); }
    /** @return CreateLeadTransferUseCase */
    @Bean public vn.com.be_crm.application.lead.command.CreateLeadTransferUseCase createLeadTransferUseCase(vn.com.be_crm.domain.lead.repository.ILeadTransferRepository r) { return new vn.com.be_crm.application.lead.command.CreateLeadTransferUseCase(r); }
    /** @return UpdateLeadTransferUseCase */
    @Bean public vn.com.be_crm.application.lead.command.UpdateLeadTransferUseCase updateLeadTransferUseCase(vn.com.be_crm.domain.lead.repository.ILeadTransferRepository r) { return new vn.com.be_crm.application.lead.command.UpdateLeadTransferUseCase(r); }
    /** @return DeleteLeadTransferUseCase */
    @Bean public vn.com.be_crm.application.lead.command.DeleteLeadTransferUseCase deleteLeadTransferUseCase(vn.com.be_crm.domain.lead.repository.ILeadTransferRepository r) { return new vn.com.be_crm.application.lead.command.DeleteLeadTransferUseCase(r); }
    /** @return ListLeadTransferUseCase */
    @Bean public vn.com.be_crm.application.lead.query.ListLeadTransferUseCase listLeadTransferUseCase(vn.com.be_crm.domain.lead.repository.ILeadTransferRepository r) { return new vn.com.be_crm.application.lead.query.ListLeadTransferUseCase(r); }

    // =====================================================================
    // MODULE OPPORTUNITY
    // =====================================================================
    /** @return CreateOpportunityUseCase */
    @Bean public vn.com.be_crm.application.opportunity.command.CreateOpportunityUseCase createOpportunityUseCase(vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository r) { return new vn.com.be_crm.application.opportunity.command.CreateOpportunityUseCase(r); }
    /** @return UpdateOpportunityUseCase */
    @Bean public vn.com.be_crm.application.opportunity.command.UpdateOpportunityUseCase updateOpportunityUseCase(vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository r) { return new vn.com.be_crm.application.opportunity.command.UpdateOpportunityUseCase(r); }
    /** @return DeleteOpportunityUseCase */
    @Bean public vn.com.be_crm.application.opportunity.command.DeleteOpportunityUseCase deleteOpportunityUseCase(vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository r) { return new vn.com.be_crm.application.opportunity.command.DeleteOpportunityUseCase(r); }
    /** @return GetOpportunityUseCase */
    @Bean public vn.com.be_crm.application.opportunity.query.GetOpportunityUseCase getOpportunityUseCase(vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository r) { return new vn.com.be_crm.application.opportunity.query.GetOpportunityUseCase(r); }
    /** @return ListOpportunityUseCase */
    @Bean public vn.com.be_crm.application.opportunity.query.ListOpportunityUseCase listOpportunityUseCase(vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository r) { return new vn.com.be_crm.application.opportunity.query.ListOpportunityUseCase(r); }
    /** @return CreateOpportunityItemUseCase */
    @Bean public vn.com.be_crm.application.opportunity.command.CreateOpportunityItemUseCase createOpportunityItemUseCase(vn.com.be_crm.domain.opportunity.repository.IOpportunityItemRepository r) { return new vn.com.be_crm.application.opportunity.command.CreateOpportunityItemUseCase(r); }
    /** @return UpdateOpportunityItemUseCase */
    @Bean public vn.com.be_crm.application.opportunity.command.UpdateOpportunityItemUseCase updateOpportunityItemUseCase(vn.com.be_crm.domain.opportunity.repository.IOpportunityItemRepository r) { return new vn.com.be_crm.application.opportunity.command.UpdateOpportunityItemUseCase(r); }
    /** @return DeleteOpportunityItemUseCase */
    @Bean public vn.com.be_crm.application.opportunity.command.DeleteOpportunityItemUseCase deleteOpportunityItemUseCase(vn.com.be_crm.domain.opportunity.repository.IOpportunityItemRepository r) { return new vn.com.be_crm.application.opportunity.command.DeleteOpportunityItemUseCase(r); }
    /** @return ListOpportunityItemUseCase */
    @Bean public vn.com.be_crm.application.opportunity.query.ListOpportunityItemUseCase listOpportunityItemUseCase(vn.com.be_crm.domain.opportunity.repository.IOpportunityItemRepository r) { return new vn.com.be_crm.application.opportunity.query.ListOpportunityItemUseCase(r); }

    // =====================================================================
    // MODULE QUOTATION
    // =====================================================================
    /** @return CreateQuotationUseCase */
    @Bean public vn.com.be_crm.application.quotation.command.CreateQuotationUseCase createQuotationUseCase(vn.com.be_crm.domain.quotation.repository.IQuotationRepository r) { return new vn.com.be_crm.application.quotation.command.CreateQuotationUseCase(r); }
    /** @return UpdateQuotationUseCase */
    @Bean public vn.com.be_crm.application.quotation.command.UpdateQuotationUseCase updateQuotationUseCase(vn.com.be_crm.domain.quotation.repository.IQuotationRepository r) { return new vn.com.be_crm.application.quotation.command.UpdateQuotationUseCase(r); }
    /** @return DeleteQuotationUseCase */
    @Bean public vn.com.be_crm.application.quotation.command.DeleteQuotationUseCase deleteQuotationUseCase(vn.com.be_crm.domain.quotation.repository.IQuotationRepository r) { return new vn.com.be_crm.application.quotation.command.DeleteQuotationUseCase(r); }
    /** @return GetQuotationUseCase */
    @Bean public vn.com.be_crm.application.quotation.query.GetQuotationUseCase getQuotationUseCase(vn.com.be_crm.domain.quotation.repository.IQuotationRepository r) { return new vn.com.be_crm.application.quotation.query.GetQuotationUseCase(r); }
    /** @return ListQuotationUseCase */
    @Bean public vn.com.be_crm.application.quotation.query.ListQuotationUseCase listQuotationUseCase(vn.com.be_crm.domain.quotation.repository.IQuotationRepository r) { return new vn.com.be_crm.application.quotation.query.ListQuotationUseCase(r); }
    /** @return CreateQuotationItemUseCase */
    @Bean public vn.com.be_crm.application.quotation.command.CreateQuotationItemUseCase createQuotationItemUseCase(vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository r) { return new vn.com.be_crm.application.quotation.command.CreateQuotationItemUseCase(r); }
    /** @return UpdateQuotationItemUseCase */
    @Bean public vn.com.be_crm.application.quotation.command.UpdateQuotationItemUseCase updateQuotationItemUseCase(vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository r) { return new vn.com.be_crm.application.quotation.command.UpdateQuotationItemUseCase(r); }
    /** @return DeleteQuotationItemUseCase */
    @Bean public vn.com.be_crm.application.quotation.command.DeleteQuotationItemUseCase deleteQuotationItemUseCase(vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository r) { return new vn.com.be_crm.application.quotation.command.DeleteQuotationItemUseCase(r); }
    /** @return ListQuotationItemUseCase */
    @Bean public vn.com.be_crm.application.quotation.query.ListQuotationItemUseCase listQuotationItemUseCase(vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository r) { return new vn.com.be_crm.application.quotation.query.ListQuotationItemUseCase(r); }
    /** @return CreateQuotationApprovalUseCase */
    @Bean public vn.com.be_crm.application.quotation.command.CreateQuotationApprovalUseCase createQuotationApprovalUseCase(vn.com.be_crm.domain.quotation.repository.IQuotationApprovalRepository r) { return new vn.com.be_crm.application.quotation.command.CreateQuotationApprovalUseCase(r); }
    /** @return UpdateQuotationApprovalUseCase */
    @Bean public vn.com.be_crm.application.quotation.command.UpdateQuotationApprovalUseCase updateQuotationApprovalUseCase(vn.com.be_crm.domain.quotation.repository.IQuotationApprovalRepository r) { return new vn.com.be_crm.application.quotation.command.UpdateQuotationApprovalUseCase(r); }
    /** @return DeleteQuotationApprovalUseCase */
    @Bean public vn.com.be_crm.application.quotation.command.DeleteQuotationApprovalUseCase deleteQuotationApprovalUseCase(vn.com.be_crm.domain.quotation.repository.IQuotationApprovalRepository r) { return new vn.com.be_crm.application.quotation.command.DeleteQuotationApprovalUseCase(r); }
    /** @return ListQuotationApprovalUseCase */
    @Bean public vn.com.be_crm.application.quotation.query.ListQuotationApprovalUseCase listQuotationApprovalUseCase(vn.com.be_crm.domain.quotation.repository.IQuotationApprovalRepository r) { return new vn.com.be_crm.application.quotation.query.ListQuotationApprovalUseCase(r); }

    // =====================================================================
    // MODULE ORDER
    // =====================================================================
    /** @return CreateOrderUseCase */
    @Bean public vn.com.be_crm.application.order.command.CreateOrderUseCase createOrderUseCase(vn.com.be_crm.domain.order.repository.IOrderRepository r) { return new vn.com.be_crm.application.order.command.CreateOrderUseCase(r); }
    /** @return UpdateOrderUseCase */
    @Bean public vn.com.be_crm.application.order.command.UpdateOrderUseCase updateOrderUseCase(vn.com.be_crm.domain.order.repository.IOrderRepository r) { return new vn.com.be_crm.application.order.command.UpdateOrderUseCase(r); }
    /** @return DeleteOrderUseCase */
    @Bean public vn.com.be_crm.application.order.command.DeleteOrderUseCase deleteOrderUseCase(vn.com.be_crm.domain.order.repository.IOrderRepository r) { return new vn.com.be_crm.application.order.command.DeleteOrderUseCase(r); }
    /** @return GetOrderUseCase */
    @Bean public vn.com.be_crm.application.order.query.GetOrderUseCase getOrderUseCase(vn.com.be_crm.domain.order.repository.IOrderRepository r) { return new vn.com.be_crm.application.order.query.GetOrderUseCase(r); }
    /** @return ListOrderUseCase */
    @Bean public vn.com.be_crm.application.order.query.ListOrderUseCase listOrderUseCase(vn.com.be_crm.domain.order.repository.IOrderRepository r) { return new vn.com.be_crm.application.order.query.ListOrderUseCase(r); }
    /** @return CreateOrderItemUseCase */
    @Bean public vn.com.be_crm.application.order.command.CreateOrderItemUseCase createOrderItemUseCase(vn.com.be_crm.domain.order.repository.IOrderItemRepository r) { return new vn.com.be_crm.application.order.command.CreateOrderItemUseCase(r); }
    /** @return UpdateOrderItemUseCase */
    @Bean public vn.com.be_crm.application.order.command.UpdateOrderItemUseCase updateOrderItemUseCase(vn.com.be_crm.domain.order.repository.IOrderItemRepository r) { return new vn.com.be_crm.application.order.command.UpdateOrderItemUseCase(r); }
    /** @return DeleteOrderItemUseCase */
    @Bean public vn.com.be_crm.application.order.command.DeleteOrderItemUseCase deleteOrderItemUseCase(vn.com.be_crm.domain.order.repository.IOrderItemRepository r) { return new vn.com.be_crm.application.order.command.DeleteOrderItemUseCase(r); }
    /** @return ListOrderItemUseCase */
    @Bean public vn.com.be_crm.application.order.query.ListOrderItemUseCase listOrderItemUseCase(vn.com.be_crm.domain.order.repository.IOrderItemRepository r) { return new vn.com.be_crm.application.order.query.ListOrderItemUseCase(r); }
    /** @return CreateOrderRevenueRecordUseCase */
    @Bean public vn.com.be_crm.application.order.command.CreateOrderRevenueRecordUseCase createOrderRevenueRecordUseCase(vn.com.be_crm.domain.order.repository.IOrderRevenueRecordRepository r) { return new vn.com.be_crm.application.order.command.CreateOrderRevenueRecordUseCase(r); }
    /** @return UpdateOrderRevenueRecordUseCase */
    @Bean public vn.com.be_crm.application.order.command.UpdateOrderRevenueRecordUseCase updateOrderRevenueRecordUseCase(vn.com.be_crm.domain.order.repository.IOrderRevenueRecordRepository r) { return new vn.com.be_crm.application.order.command.UpdateOrderRevenueRecordUseCase(r); }
    /** @return DeleteOrderRevenueRecordUseCase */
    @Bean public vn.com.be_crm.application.order.command.DeleteOrderRevenueRecordUseCase deleteOrderRevenueRecordUseCase(vn.com.be_crm.domain.order.repository.IOrderRevenueRecordRepository r) { return new vn.com.be_crm.application.order.command.DeleteOrderRevenueRecordUseCase(r); }
    /** @return ListOrderRevenueRecordUseCase */
    @Bean public vn.com.be_crm.application.order.query.ListOrderRevenueRecordUseCase listOrderRevenueRecordUseCase(vn.com.be_crm.domain.order.repository.IOrderRevenueRecordRepository r) { return new vn.com.be_crm.application.order.query.ListOrderRevenueRecordUseCase(r); }
    /** @return CreateOrderPaymentScheduleUseCase */
    @Bean public vn.com.be_crm.application.order.command.CreateOrderPaymentScheduleUseCase createOrderPaymentScheduleUseCase(vn.com.be_crm.domain.order.repository.IOrderPaymentScheduleRepository r) { return new vn.com.be_crm.application.order.command.CreateOrderPaymentScheduleUseCase(r); }
    /** @return UpdateOrderPaymentScheduleUseCase */
    @Bean public vn.com.be_crm.application.order.command.UpdateOrderPaymentScheduleUseCase updateOrderPaymentScheduleUseCase(vn.com.be_crm.domain.order.repository.IOrderPaymentScheduleRepository r) { return new vn.com.be_crm.application.order.command.UpdateOrderPaymentScheduleUseCase(r); }
    /** @return DeleteOrderPaymentScheduleUseCase */
    @Bean public vn.com.be_crm.application.order.command.DeleteOrderPaymentScheduleUseCase deleteOrderPaymentScheduleUseCase(vn.com.be_crm.domain.order.repository.IOrderPaymentScheduleRepository r) { return new vn.com.be_crm.application.order.command.DeleteOrderPaymentScheduleUseCase(r); }
    /** @return ListOrderPaymentScheduleUseCase */
    @Bean public vn.com.be_crm.application.order.query.ListOrderPaymentScheduleUseCase listOrderPaymentScheduleUseCase(vn.com.be_crm.domain.order.repository.IOrderPaymentScheduleRepository r) { return new vn.com.be_crm.application.order.query.ListOrderPaymentScheduleUseCase(r); }
    /** @return CreateOrderDeliveryTrackingUseCase */
    @Bean public vn.com.be_crm.application.order.command.CreateOrderDeliveryTrackingUseCase createOrderDeliveryTrackingUseCase(vn.com.be_crm.domain.order.repository.IOrderDeliveryTrackingRepository r) { return new vn.com.be_crm.application.order.command.CreateOrderDeliveryTrackingUseCase(r); }
    /** @return UpdateOrderDeliveryTrackingUseCase */
    @Bean public vn.com.be_crm.application.order.command.UpdateOrderDeliveryTrackingUseCase updateOrderDeliveryTrackingUseCase(vn.com.be_crm.domain.order.repository.IOrderDeliveryTrackingRepository r) { return new vn.com.be_crm.application.order.command.UpdateOrderDeliveryTrackingUseCase(r); }
    /** @return DeleteOrderDeliveryTrackingUseCase */
    @Bean public vn.com.be_crm.application.order.command.DeleteOrderDeliveryTrackingUseCase deleteOrderDeliveryTrackingUseCase(vn.com.be_crm.domain.order.repository.IOrderDeliveryTrackingRepository r) { return new vn.com.be_crm.application.order.command.DeleteOrderDeliveryTrackingUseCase(r); }
    /** @return ListOrderDeliveryTrackingUseCase */
    @Bean public vn.com.be_crm.application.order.query.ListOrderDeliveryTrackingUseCase listOrderDeliveryTrackingUseCase(vn.com.be_crm.domain.order.repository.IOrderDeliveryTrackingRepository r) { return new vn.com.be_crm.application.order.query.ListOrderDeliveryTrackingUseCase(r); }

    // =====================================================================
    // MODULE PRICING
    // =====================================================================
    /** @return CreatePricePolicyUseCase */
    @Bean public vn.com.be_crm.application.pricing.command.CreatePricePolicyUseCase createPricePolicyUseCase(vn.com.be_crm.domain.pricing.repository.IPricePolicyRepository r) { return new vn.com.be_crm.application.pricing.command.CreatePricePolicyUseCase(r); }
    /** @return UpdatePricePolicyUseCase */
    @Bean public vn.com.be_crm.application.pricing.command.UpdatePricePolicyUseCase updatePricePolicyUseCase(vn.com.be_crm.domain.pricing.repository.IPricePolicyRepository r) { return new vn.com.be_crm.application.pricing.command.UpdatePricePolicyUseCase(r); }
    /** @return DeletePricePolicyUseCase */
    @Bean public vn.com.be_crm.application.pricing.command.DeletePricePolicyUseCase deletePricePolicyUseCase(vn.com.be_crm.domain.pricing.repository.IPricePolicyRepository r) { return new vn.com.be_crm.application.pricing.command.DeletePricePolicyUseCase(r); }
    /** @return GetPricePolicyUseCase */
    @Bean public vn.com.be_crm.application.pricing.query.GetPricePolicyUseCase getPricePolicyUseCase(vn.com.be_crm.domain.pricing.repository.IPricePolicyRepository r) { return new vn.com.be_crm.application.pricing.query.GetPricePolicyUseCase(r); }
    /** @return ListPricePolicyUseCase */
    @Bean public vn.com.be_crm.application.pricing.query.ListPricePolicyUseCase listPricePolicyUseCase(vn.com.be_crm.domain.pricing.repository.IPricePolicyRepository r) { return new vn.com.be_crm.application.pricing.query.ListPricePolicyUseCase(r); }
    /** @return CreatePricePolicyProductUseCase */
    @Bean public vn.com.be_crm.application.pricing.command.CreatePricePolicyProductUseCase createPricePolicyProductUseCase(vn.com.be_crm.domain.pricing.repository.IPricePolicyProductRepository r) { return new vn.com.be_crm.application.pricing.command.CreatePricePolicyProductUseCase(r); }
    /** @return UpdatePricePolicyProductUseCase */
    @Bean public vn.com.be_crm.application.pricing.command.UpdatePricePolicyProductUseCase updatePricePolicyProductUseCase(vn.com.be_crm.domain.pricing.repository.IPricePolicyProductRepository r) { return new vn.com.be_crm.application.pricing.command.UpdatePricePolicyProductUseCase(r); }
    /** @return DeletePricePolicyProductUseCase */
    @Bean public vn.com.be_crm.application.pricing.command.DeletePricePolicyProductUseCase deletePricePolicyProductUseCase(vn.com.be_crm.domain.pricing.repository.IPricePolicyProductRepository r) { return new vn.com.be_crm.application.pricing.command.DeletePricePolicyProductUseCase(r); }
    /** @return ListPricePolicyProductUseCase */
    @Bean public vn.com.be_crm.application.pricing.query.ListPricePolicyProductUseCase listPricePolicyProductUseCase(vn.com.be_crm.domain.pricing.repository.IPricePolicyProductRepository r) { return new vn.com.be_crm.application.pricing.query.ListPricePolicyProductUseCase(r); }
    /** @return CreatePricePolicyCustomerUseCase */
    @Bean public vn.com.be_crm.application.pricing.command.CreatePricePolicyCustomerUseCase createPricePolicyCustomerUseCase(vn.com.be_crm.domain.pricing.repository.IPricePolicyCustomerRepository r) { return new vn.com.be_crm.application.pricing.command.CreatePricePolicyCustomerUseCase(r); }
    /** @return DeletePricePolicyCustomerUseCase */
    @Bean public vn.com.be_crm.application.pricing.command.DeletePricePolicyCustomerUseCase deletePricePolicyCustomerUseCase(vn.com.be_crm.domain.pricing.repository.IPricePolicyCustomerRepository r) { return new vn.com.be_crm.application.pricing.command.DeletePricePolicyCustomerUseCase(r); }
    /** @return ListPricePolicyCustomerUseCase */
    @Bean public vn.com.be_crm.application.pricing.query.ListPricePolicyCustomerUseCase listPricePolicyCustomerUseCase(vn.com.be_crm.domain.pricing.repository.IPricePolicyCustomerRepository r) { return new vn.com.be_crm.application.pricing.query.ListPricePolicyCustomerUseCase(r); }
    /** @return CreatePricePolicyCustomerCategoryUseCase */
    @Bean public vn.com.be_crm.application.pricing.command.CreatePricePolicyCustomerCategoryUseCase createPricePolicyCustomerCategoryUseCase(vn.com.be_crm.domain.pricing.repository.IPricePolicyCustomerCategoryRepository r) { return new vn.com.be_crm.application.pricing.command.CreatePricePolicyCustomerCategoryUseCase(r); }
    /** @return DeletePricePolicyCustomerCategoryUseCase */
    @Bean public vn.com.be_crm.application.pricing.command.DeletePricePolicyCustomerCategoryUseCase deletePricePolicyCustomerCategoryUseCase(vn.com.be_crm.domain.pricing.repository.IPricePolicyCustomerCategoryRepository r) { return new vn.com.be_crm.application.pricing.command.DeletePricePolicyCustomerCategoryUseCase(r); }
    /** @return ListPricePolicyCustomerCategoryUseCase */
    @Bean public vn.com.be_crm.application.pricing.query.ListPricePolicyCustomerCategoryUseCase listPricePolicyCustomerCategoryUseCase(vn.com.be_crm.domain.pricing.repository.IPricePolicyCustomerCategoryRepository r) { return new vn.com.be_crm.application.pricing.query.ListPricePolicyCustomerCategoryUseCase(r); }
    /** @return CreatePricePolicyProductTypeUseCase */
    @Bean public vn.com.be_crm.application.pricing.command.CreatePricePolicyProductTypeUseCase createPricePolicyProductTypeUseCase(vn.com.be_crm.domain.pricing.repository.IPricePolicyProductTypeRepository r) { return new vn.com.be_crm.application.pricing.command.CreatePricePolicyProductTypeUseCase(r); }
    /** @return DeletePricePolicyProductTypeUseCase */
    @Bean public vn.com.be_crm.application.pricing.command.DeletePricePolicyProductTypeUseCase deletePricePolicyProductTypeUseCase(vn.com.be_crm.domain.pricing.repository.IPricePolicyProductTypeRepository r) { return new vn.com.be_crm.application.pricing.command.DeletePricePolicyProductTypeUseCase(r); }
    /** @return ListPricePolicyProductTypeUseCase */
    @Bean public vn.com.be_crm.application.pricing.query.ListPricePolicyProductTypeUseCase listPricePolicyProductTypeUseCase(vn.com.be_crm.domain.pricing.repository.IPricePolicyProductTypeRepository r) { return new vn.com.be_crm.application.pricing.query.ListPricePolicyProductTypeUseCase(r); }
    /** @return CreatePricePolicyEmployeeUseCase */
    @Bean public vn.com.be_crm.application.pricing.command.CreatePricePolicyEmployeeUseCase createPricePolicyEmployeeUseCase(vn.com.be_crm.domain.pricing.repository.IPricePolicyEmployeeRepository r) { return new vn.com.be_crm.application.pricing.command.CreatePricePolicyEmployeeUseCase(r); }
    /** @return DeletePricePolicyEmployeeUseCase */
    @Bean public vn.com.be_crm.application.pricing.command.DeletePricePolicyEmployeeUseCase deletePricePolicyEmployeeUseCase(vn.com.be_crm.domain.pricing.repository.IPricePolicyEmployeeRepository r) { return new vn.com.be_crm.application.pricing.command.DeletePricePolicyEmployeeUseCase(r); }
    /** @return ListPricePolicyEmployeeUseCase */
    @Bean public vn.com.be_crm.application.pricing.query.ListPricePolicyEmployeeUseCase listPricePolicyEmployeeUseCase(vn.com.be_crm.domain.pricing.repository.IPricePolicyEmployeeRepository r) { return new vn.com.be_crm.application.pricing.query.ListPricePolicyEmployeeUseCase(r); }
}
