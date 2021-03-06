package io.jboot.admin.service.provider;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.NotificationService;
import io.jboot.admin.service.entity.model.Notification;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Bean
@Singleton
@JbootrpcService
public class NotificationServiceImpl extends JbootServiceBase<Notification> implements NotificationService {

    @Override
    public Page<Notification> findPage(Notification notification, int pageNumber, int pageSize) {
        Columns columns = Columns.create();

        if (StrKit.notBlank(notification.getName())) {
            columns.like("name", "%" + notification.getName() + "%");
        }
        if (StrKit.notBlank(notification.getContent())) {
            columns.like("content", "%" + notification.getContent() + "%");
        }
        if (notification.getReceiverID() != null) {
            columns.eq("receiverID", notification.getReceiverID());
        }
        if (notification.getStatus() != null) {
            columns.eq("status", notification.getStatus());
        }
        columns.eq("isEnable", true);
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "status asc");
    }

    @Override
    public Page<Notification> findPage(Notification notification, Date[] dates, int pageNumber, int pageSize) {
        Columns columns = Columns.create();

        if (StrKit.notBlank(notification.getName())) {
            columns.like("name", "%" + notification.getName() + "%");
        }
        if (StrKit.notBlank(notification.getContent())) {
            columns.like("content", "%" + notification.getContent() + "%");
        }
        if (notification.getReceiverID() != null) {
            columns.eq("receiverID", notification.getReceiverID());
        }
        if (notification.getStatus() != null) {
            columns.eq("status", notification.getStatus());
        }
        if (StrKit.notNull(dates[0])){
            columns.ge("createTime", dates[0]);
        }
        if (StrKit.notNull(dates[1])){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dates[1]);
            calendar.add(Calendar.DAY_OF_MONTH, 1);  //利用Calendar 实现 Date日期+1天
            dates[1] = calendar.getTime();
            columns.le("createTime", dates[1]);
        }

        columns.eq("isEnable", true);
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "status asc");
    }

    @Override
    public int findMessageByUserID(Object id) {
        List<Notification> list = DAO.findListByColumn("receiverID", id);
        int size = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getStatus() == 0) {
                size++;
            }
        }
        return size;
    }

    @Override
    public boolean haveReadAll(Object id) {
        List<Notification> list = DAO.findListByColumn("receiverID", id);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getStatus() != 1) {
                list.get(i).setStatus(1);
                if (!update(list.get(i))) {
                    return false;
                }
            }
        }
        return true;
    }
}