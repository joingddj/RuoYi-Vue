package com.stdiet.custom.service;

import java.util.List;
import com.stdiet.custom.domain.SysOrderPause;

/**
 * 订单服务暂停Service接口
 *
 * @author wonder
 * @date 2021-01-07
 */
public interface ISysOrderPauseService
{
    /**
     * 查询订单服务暂停
     *
     * @param id 订单服务暂停ID
     * @return 订单服务暂停
     */
    public SysOrderPause selectSysOrderPauseById(Long id);

    /**
     * 查询订单服务暂停列表
     *
     * @param sysOrderPause 订单服务暂停
     * @return 订单服务暂停集合
     */
    public List<SysOrderPause> selectSysOrderPauseList(SysOrderPause sysOrderPause);

    /**
     * 新增订单服务暂停
     *
     * @param sysOrderPause 订单服务暂停
     * @return 结果
     */
    public int insertSysOrderPause(SysOrderPause sysOrderPause);

    /**
     * 修改订单服务暂停
     *
     * @param sysOrderPause 订单服务暂停
     * @return 结果
     */
    public int updateSysOrderPause(SysOrderPause sysOrderPause);

    /**
     * 批量删除订单服务暂停
     *
     * @param ids 需要删除的订单服务暂停ID
     * @return 结果
     */
    public int deleteSysOrderPauseByIds(Long[] ids);

    /**
     * 删除订单服务暂停信息
     *
     * @param id 订单服务暂停ID
     * @return 结果
     */
    public int deleteSysOrderPauseById(Long id);

    /**
     * 根据订单ID、时间范围查询数量
     * @param sysOrderPause
     * @return
     */
    int getCountByOrderIdAndPauseDate(SysOrderPause sysOrderPause);

    /**
     * 根据订单ID删除暂停记录
     * @param orderIds
     * @return
     */
    int deletePauseByOrderId(Long[] orderIds);

    /**
     * 根据订单ID获取暂停记录
     * @param orderId
     * @return
     */
    List<SysOrderPause> getPauseListByOrderId(Long orderId);

    long selectNearMainOrderIdByCusId(Long cusId);
}