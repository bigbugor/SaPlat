package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.SiteSurveyExpertAdvice;

import java.util.List;

public interface SiteSurveyExpertAdviceService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public SiteSurveyExpertAdvice findById(Object id);


    /**
     * 通过列名、值、逻辑获取第一个结果
     * @param columnName 列名
     * @param value 值
     * @param logic 逻辑
     * @return 结果
     */
    public SiteSurveyExpertAdvice findByColumn(String columnName, String value, String logic);

    /**
     * 通过项目id获取项目列表
     * @param projectId 项目ID
     * @return 结果
     */
    public List<SiteSurveyExpertAdvice> findListByProjectId(Long projectId);


    /**
     * 分页查询
     * @param siteSurveyExpertAdvice 模型
     * @param pageNumber 页数
     * @param pageSize 每页数目
     * @return 页码
     */
    public Page<SiteSurveyExpertAdvice> findPage(SiteSurveyExpertAdvice siteSurveyExpertAdvice, int pageNumber, int pageSize);


    /**
     * 通过列名、值获取第一个结果
     * @param columnNames 列名
     * @param values 值
     * @return 结果
     */
    public SiteSurveyExpertAdvice findByColumns(String[] columnNames, Object[] values);


    /**
     * find all model
     *
     * @return all <SiteSurveyExpertAdvice
     */
    public List<SiteSurveyExpertAdvice> findAll();


    /**
     * delete model by primary key
     *
     * @param id
     * @return success
     */
    public boolean deleteById(Object id);


    /**
     * delete model
     *
     * @param model
     * @return
     */
    public boolean delete(SiteSurveyExpertAdvice model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(SiteSurveyExpertAdvice model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(SiteSurveyExpertAdvice model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(SiteSurveyExpertAdvice model);


    public void join(Page<? extends Model> page, String joinOnField);
    public void join(Page<? extends Model> page, String joinOnField, String[] attrs);
    public void join(Page<? extends Model> page, String joinOnField, String joinName);
    public void join(Page<? extends Model> page, String joinOnField, String joinName, String[] attrs);
    public void join(List<? extends Model> models, String joinOnField);
    public void join(List<? extends Model> models, String joinOnField, String[] attrs);
    public void join(List<? extends Model> models, String joinOnField, String joinName);
    public void join(List<? extends Model> models, String joinOnField, String joinName, String[] attrs);
    public void join(Model model, String joinOnField);
    public void join(Model model, String joinOnField, String[] attrs);
    public void join(Model model, String joinOnField, String joinName);
    public void join(Model model, String joinOnField, String joinName, String[] attrs);

    public void keep(Model model, String... attrs);
    public void keep(List<? extends Model> models, String... attrs);
}