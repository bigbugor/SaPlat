package io.jboot.admin.service.provider;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.*;
import io.jboot.admin.service.entity.model.*;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Bean
@Singleton
@JbootrpcService
public class ManagementServiceImpl extends JbootServiceBase<Management> implements ManagementService {
    @Inject
    private NotificationService notificationService;

    @Inject
    private RoleService roleService;

    @Inject
    private UserService userService;

    @Inject
    private OrganizationService organizationService;

    @Inject
    private UserRoleService userRoleService;

    /**
     * 装配单个实体对象的数据
     *
     * @param model
     * @return
     */
    public Management fitModel(Management model) {
        if (null != model && model.getSuperiorID() > 0) {
            Management tmp = findById(model.getSuperiorID());
            if (tmp != null) {
                model.setSuperiorName(tmp.getName());
            }
        }
        return model;
    }

    /**
     * 获取所有数据
     *
     * @param isEnable 根据指定的isEnable的值来获取数据
     * @return
     */
    @Override
    public List<Management> findAll(boolean isEnable) {
        return DAO.findListByColumn("isEnable", isEnable);
    }

    @Override
    public boolean useOrUnuse(Management management) {
        return Db.tx(() -> {
            Organization organization=organizationService.findById(management.getOrgID());
            User user = userService.findByUserIdAndUserSource(organization.getId(), 1);
            Role role = roleService.findByName("管理机构");
            UserRole userRole = userRoleService.findByUserIdAndRoleId(user.getId(), role.getId());
            if(userRole==null){
                return false;
            }
            userRole.setIsEnable(management.getIsEnable());
            Role _role = roleService.findByName("管理机构立项");
            UserRole _userRole = userRoleService.findByUserIdAndRoleId(user.getId(), _role.getId());
            if(_userRole!=null){
                _userRole.setIsEnable(management.getIsEnable());
                if(!userRoleService.update(_userRole)){
                    return false;
                }
            }
            return userRoleService.update(userRole) && management.update();
        });
    }

    @Override
    public Management findById(Object id) {
        return fitModel(DAO.findFirstByColumn("id", id));
    }

    @Override
    public Management findByOrgId(Long orgId) {
        return fitModel(DAO.findFirstByColumn("orgID", orgId));
    }

    @Override
    public Management findByCreateUserID(Object createUserID) {
        return DAO.findFirstByColumn("createUserID", createUserID);
    }

    @Override
    public boolean saveOrUpdate(Management model, Auth auth) {
        return Db.tx(() -> model.saveOrUpdate() && auth.saveOrUpdate());
    }

    @Override
    public boolean saveOrUpdate(Management model, Auth auth, Notification notification) {
        return Db.tx(() -> model.saveOrUpdate() && auth.saveOrUpdate() && notificationService.saveOrUpdate(notification));
    }

    @Override
    public boolean isExisted(String name) {
        return findByName(name) != null;
    }


    @Override
    public Management findByName(String name) {
        return fitModel(DAO.findFirstByColumn("name", name));
    }


    @Override
    public Page<Management> findPage(Management management, int pageNumber, int pageSize) {
        Columns columns = Columns.create();

        if (StrKit.notBlank(management.getName())) {
            columns.like("name", "%" + management.getName() + "%");
        }
        if (StrKit.notBlank(management.getResponsibility())) {
            columns.like("responsibility", "%" + management.getResponsibility() + "%");
        }
        if (StrKit.notBlank(management.getManager())){
            columns.like("manager", "%" + management.getManager() + "%");
        }
        if (StrKit.notNull(management.getIsEnable())){
            columns.eq("isEnable", management.getIsEnable());
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id");
    }

    @Override
    public List<Management> findListByColumns(Columns columns) {
        return DAO.findListByColumns(columns);
    }
}