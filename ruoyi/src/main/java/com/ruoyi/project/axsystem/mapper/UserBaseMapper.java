package com.ruoyi.project.axsystem.mapper;

import java.util.List;
import com.ruoyi.project.axsystem.domain.UserBase;

/**
 * 用户基础信息Mapper接口
 * 
 * @author joy
 * @date 2020-05-18
 */
public interface UserBaseMapper 
{
    /**
     * 查询用户基础信息
     * 
     * @param uid 用户基础信息ID
     * @return 用户基础信息
     */
    public UserBase selectUserBaseById(Long uid);

    /**
     * 查询用户基础信息列表
     * 
     * @param userBase 用户基础信息
     * @return 用户基础信息集合
     */
    public List<UserBase> selectUserBaseList(UserBase userBase);

    /**
     * 新增用户基础信息
     * 
     * @param userBase 用户基础信息
     * @return 结果
     */
    public int insertUserBase(UserBase userBase);

    /**
     * 修改用户基础信息
     * 
     * @param userBase 用户基础信息
     * @return 结果
     */
    public int updateUserBase(UserBase userBase);

    /**
     * 删除用户基础信息
     * 
     * @param uid 用户基础信息ID
     * @return 结果
     */
    public int deleteUserBaseById(Long uid);

    /**
     * 批量删除用户基础信息
     * 
     * @param uids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUserBaseByIds(Long[] uids);
}
