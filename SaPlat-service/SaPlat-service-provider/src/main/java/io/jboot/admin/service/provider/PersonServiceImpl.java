package io.jboot.admin.service.provider;

import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import io.jboot.admin.service.api.*;
import io.jboot.admin.service.entity.model.AffectedGroup;
import io.jboot.admin.service.entity.model.Files;
import io.jboot.admin.service.entity.model.Person;
import io.jboot.admin.service.entity.model.User;
import io.jboot.admin.service.entity.status.system.UserStatus;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Bean
@Singleton
@JbootrpcService
public class PersonServiceImpl extends JbootServiceBase<Person> implements PersonService {

    @Inject
    private UserService userService;

    @Inject
    private AffectedGroupService affectedGroupService;

    @Inject
    private UserRoleService userRoleService;

    @Inject
    private RoleService roleService;

    UserServiceImpl userServiceNew = new UserServiceImpl();

    /**
     * 装配完善Page对象中所有对象的数据
     *
     * @param page
     * @return
     */
    public Page<Person> fitPage(Page<Person> page) {
        if (page != null) {
            List<Person> tList = page.getList();
            for (Person item : tList) {
                fitModel(item);
            }
        }
        return page;
    }

    /**
     * 装配单个实体对象的数据
     *
     * @param model
     * @return
     */
    public Person fitModel(Person model) {
        User user = new User();
        user.setUserID(model.getId());
        // 0 代表个人
        user.setUserSource(0);
        model.setUser(userServiceNew.findModel(user));
        return model;
    }

    @Override
    public Page<Person> findPage(Person person, int pageNumber, int pageSize) {
        Columns columns = Columns.create();

        if (StrKit.notBlank(person.getName())) {
            columns.like("name", "%" + person.getName() + "%");
        }
        if (StrKit.notBlank(person.getPhone())) {
            columns.like("phone", "%" + person.getPhone() + "%");
        }
        return fitPage(DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc"));
    }

    @Override
    public Page<Person> findPage(Person person, Date[] dates, int pageNumber, int pageSize) {
        Columns columns = Columns.create();

        if (StrKit.notBlank(person.getName())) {
            columns.like("name", "%" + person.getName() + "%");
        }
        if (StrKit.notNull(person.getIsEnable())) {
            columns.eq("isEnable", person.getIsEnable());
        }
        if (StrKit.notNull(dates[0])) {
            columns.ge("createTime", dates[0]);
        }
        if (StrKit.notNull(dates[1])) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dates[1]);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            dates[1] = calendar.getTime();
            columns.le("createTime", dates[1]);
        }
        return fitPage(DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc"));
    }

    @Override
    public boolean savePerson(Person model, User user, Long[] roles) {
        return Db.tx(() -> {
            if (!save(model)) {
                return false;
            }
            user.setUserID(model.getId());
            return userService.saveUser(user, roles);
        });
    }

    @Override
    public boolean useOrunuse(Person person) {
        return Db.tx(() -> {
            User user = userService.findByUserIdAndUserSource(person.getId(), 0);
            if (person.getIsEnable()) {
                user.setStatus(UserStatus.USED);
                user.setRemark("启用系统用户");
            } else {
                user.setStatus(UserStatus.LOCKED);
                user.setRemark("锁定系统用户");
            }
            user.setLastAccessTime(new Date());
            return userService.update(user) && person.update();
        });
    }

    /**
     * 分页查询 项目审查人员 信息
     *
     * @param projectID 项目编号
     * @return 页
     */
    @Override
    public Page<Person> findPageByProjectID(Long projectID, int pageNumber, int pageSize) {
        Kv c;
        SqlPara sqlPara = null;
        c = Kv.by("ID", projectID);
        sqlPara = Db.getSqlPara("app-project.invitedExpert-by-projectID", c);
        return fitPage(DAO.paginate(pageNumber, pageSize, sqlPara));
    }

    @Override
    public Person findByUser(User user) {
        return fitModel(findById(user.getUserID()));
    }

    @Override
    public Person findByName(String name) {
        return fitModel(DAO.findFirstByColumn("name", name));
    }

    @Override
    public boolean update(Person person, User user) {
        return true;
    }

    @Override
    public boolean update(Person person, User user, AffectedGroup affectedGroup, Files files, Files fileNow) {
        return Db.tx(() -> {
            if (files != null && !files.update()) {
                return false;
            }
            if (fileNow != null && !fileNow.update()) {
                return false;
            }
            return person.update() && user.update() && affectedGroupService.saveOrUpdate(affectedGroup);
        });
    }
}