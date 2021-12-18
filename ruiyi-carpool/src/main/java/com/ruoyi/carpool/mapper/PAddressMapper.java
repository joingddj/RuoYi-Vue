package com.ruoyi.carpool.mapper;

import java.util.List;
import com.ruoyi.carpool.domain.PAddress;

/**
 * 地址详情Mapper接口
 * 
 * @author ruoyi
 * @date 2021-12-18
 */
public interface PAddressMapper 
{
    /**
     * 查询地址详情
     * 
     * @param id 地址详情主键
     * @return 地址详情
     */
    public PAddress selectPAddressById(Long id);

    /**
     * 查询地址详情列表
     * 
     * @param pAddress 地址详情
     * @return 地址详情集合
     */
    public List<PAddress> selectPAddressList(PAddress pAddress);

    /**
     * 新增地址详情
     * 
     * @param pAddress 地址详情
     * @return 结果
     */
    public int insertPAddress(PAddress pAddress);

    /**
     * 修改地址详情
     * 
     * @param pAddress 地址详情
     * @return 结果
     */
    public int updatePAddress(PAddress pAddress);

    /**
     * 删除地址详情
     * 
     * @param id 地址详情主键
     * @return 结果
     */
    public int deletePAddressById(Long id);

    /**
     * 批量删除地址详情
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePAddressByIds(Long[] ids);
}
