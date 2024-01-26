package com.ruoyi.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.callback.dto.Called1100910018DTO;
import com.ruoyi.system.domain.GroupInfo;
import com.ruoyi.system.domain.base.PageBaseDTO;
import com.ruoyi.system.domain.dto.GroupPageQueryDTO;
import com.ruoyi.system.domain.dto.GroupPageQueryExportDTO;
import com.ruoyi.system.domain.vo.GroupInfoVO;
import com.ruoyi.system.domain.vo.GroupPageInfoVO;
import com.ruoyi.system.domain.vo.GroupResourceVO;
import com.ruoyi.system.mapper.GroupInfoMapper;
import com.ruoyi.system.openapi.model.output.ExtTgSelectGroupVO;
import com.ruoyi.system.service.GroupInfoService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 群基础信息(GroupInfo)表服务实现类
 *
 * @author 朱少波
 * @since 2024-01-15 09:26:48
 */
@Service("groupInfoService")
public class GroupInfoServiceImpl extends ServiceImpl<GroupInfoMapper, GroupInfo> implements GroupInfoService {


    public List<GroupInfo> existGroup(List<String> groupSerialNos) {
        return baseMapper.selectList(
                new LambdaQueryWrapper<GroupInfo>()
                        .in(GroupInfo::getGroupSerialNo, groupSerialNos));
    }

    @Override
    public Page<GroupPageInfoVO> groupPage(GroupPageQueryDTO dto) {
        Page page = new Page<>(dto.getPage(), dto.getLimit());
        if (dto instanceof GroupPageQueryExportDTO) {
            page.setSearchCount(false);
        }
        return baseMapper.page(page, dto);
    }

    @Override
    public List<GroupInfo> limitQuery(PageBaseDTO dto) {
        Page page = new Page<>(dto.getPage(), dto.getLimit());
        if (dto instanceof GroupPageQueryExportDTO) {
            page.setSearchCount(false);
        }
        return baseMapper.pageAll(page).getRecords();
    }

    @Override
    public List<GroupInfo> saveImportGroup(List<GroupResourceVO> resourceList) {
        if (CollUtil.isEmpty(resourceList)) {
            return new ArrayList<>();
        }
        List<GroupInfo> existingGroups = existGroup(resourceList.stream().map(GroupResourceVO::getGroupSerialNo)
                .collect(Collectors.toList()));

        Map<String, GroupInfo> map = CollUtil.isNotEmpty(existingGroups) ?
                existingGroups.stream().collect(Collectors.toMap(GroupInfo::getGroupSerialNo, Function.identity(), (v1, v2) -> v1))
                : new HashMap<>();

        List<GroupInfo> result = new ArrayList<>();
        List<GroupInfo> updates = new ArrayList<>();
        List<GroupInfo> adds = new ArrayList<>();
        List<String> newGroupSerialNos = new ArrayList<>();
        for (GroupResourceVO groupResourceVO : resourceList) {
            if (map.containsKey(groupResourceVO.getGroupSerialNo())) {
                GroupInfo old = map.get(groupResourceVO.getGroupSerialNo());
                result.add(old);
                GroupInfo groupInfo = new GroupInfo();
                groupInfo.setGroupId(old.getGroupId());
                groupInfo.setGroupSerialNo(groupResourceVO.getGroupSerialNo());
                groupInfo.setRegistrationTime(groupResourceVO.getRegistrationTime());
                groupInfo.setGroupInviteLink(groupResourceVO.getGroupInviteLink());
                groupInfo.setUpdateTime(LocalDateTime.now());
                updates.add(groupInfo);
                continue;
            }

            GroupInfo groupInfo = new GroupInfo();
            groupInfo.setGroupId(IdWorker.getIdStr());
            groupInfo.setGroupSerialNo(groupResourceVO.getGroupSerialNo());
            groupInfo.setCreateType(10);
            groupInfo.setRegistrationTime(groupResourceVO.getRegistrationTime());
            groupInfo.setGroupInviteLink(groupResourceVO.getGroupInviteLink());
            groupInfo.setCreateTime(LocalDateTime.now());
            groupInfo.setUpdateTime(LocalDateTime.now());
            //防止导入的群数据重复
            if (newGroupSerialNos.contains(groupResourceVO.getGroupSerialNo())) {
                continue;
            }
            newGroupSerialNos.add(groupResourceVO.getGroupSerialNo());
            result.add(groupInfo);
            adds.add(groupInfo);
        }
        if (CollUtil.isNotEmpty(adds)) {
            saveBatch(adds);
        }
        if (CollUtil.isNotEmpty(updates)) {
            updateBatchById(updates);
        }
        return result;
    }

    @Override
    public List<GroupInfoVO> selectGroup(Integer registrationDay, Integer groupNum, List<String> countryCode,
                                         List<String> excludeGroupId, Integer botAdmin, String groupRange, Integer groupType) {
        return baseMapper.selectGroup(registrationDay == null ? null :
                LocalDateTime.now().plusDays(-registrationDay), groupNum, countryCode, excludeGroupId, botAdmin, groupRange, groupType);
    }

    @Override
    public void syncGroupInfo(List<GroupInfo> groupInfoList, List<ExtTgSelectGroupVO> utInfos) {
        if (CollUtil.isEmpty(utInfos)) {
            return;
        }
        Map<String, ExtTgSelectGroupVO> utInfoMap =
                utInfos.stream().filter(p->StrUtil.isNotBlank(p.getInviteLink()))
                        .collect(Collectors.toMap(ExtTgSelectGroupVO::getChatroomSerialNo, p -> p));
        List<GroupInfo> updates = groupInfoList.stream().filter(p -> utInfoMap.containsKey(p.getGroupSerialNo()))
                .map(p -> {
                    ExtTgSelectGroupVO vo = utInfoMap.get(p.getGroupSerialNo());
                    GroupInfo update = new GroupInfo();
                    update.setGroupId(p.getGroupId());
                    update.setGroupInviteLink(vo.getInviteLink());
                    update.setUpdateTime(LocalDateTime.now());
                    return update;
                }).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(updates)) {
            updateBatchById(updates);
        }

    }

    @Override
    public GroupInfo saveExternalGroup(String groupSerialNo, String groupName, String groupInviteLink) {
        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setGroupId(IdWorker.getIdStr());
        groupInfo.setGroupSerialNo(groupSerialNo);
        groupInfo.setGroupName(groupName);
        groupInfo.setGroupInviteLink(groupInviteLink);
        groupInfo.setCreateType(20);
        groupInfo.setCreateTime(LocalDateTime.now());
        baseMapper.insert(groupInfo);
        return groupInfo;
    }

    @Override
    public GroupInfo getGroupBySerialNo(String serialNo) {
        if (StrUtil.isBlank(serialNo)) {
            return null;
        }
        return baseMapper.selectOne(new LambdaQueryWrapper<GroupInfo>()
                .eq(GroupInfo::getGroupSerialNo, serialNo).or().eq(GroupInfo::getOldGroupSerialNo, serialNo)
                .last(" limit 1"));
    }

    @Override
    public GroupInfo getGroupBySerialNo(String oldSerialNo, String newSerialNo) {
        return baseMapper.selectOne(new LambdaQueryWrapper<GroupInfo>()
                .in(GroupInfo::getGroupSerialNo, Optional.ofNullable(oldSerialNo).orElse(""),
                        Optional.ofNullable(newSerialNo).orElse("")).last(" limit 1"));
    }


    @Override
    public GroupInfo changeGroupSerialNo(String oldGroupSerialNo, String newGroupSerialNo) {
        GroupInfo groupBySerialNo = getGroupBySerialNo(oldGroupSerialNo, newGroupSerialNo);
        if (groupBySerialNo == null) {
            return null;
        }
        if (StrUtil.isNotBlank(newGroupSerialNo)) {
            GroupInfo groupInfo = new GroupInfo();
            groupInfo.setGroupId(groupBySerialNo.getGroupId());
            groupInfo.setGroupSerialNo(newGroupSerialNo);
            groupInfo.setOldGroupSerialNo(oldGroupSerialNo);
            baseMapper.updateById(groupInfo);
        }
        return groupBySerialNo;
    }

    @Override
    public void updateGroupSerialNo(String groupId, String oldGroupSerialNo, String newGroupSerialNo) {
        if (StrUtil.isNotBlank(newGroupSerialNo)) {
            GroupInfo groupInfo = new GroupInfo();
            groupInfo.setGroupId(groupId);
            groupInfo.setOldGroupSerialNo(oldGroupSerialNo);
            groupInfo.setGroupSerialNo(newGroupSerialNo);
            baseMapper.updateById(groupInfo);
        }
    }

    @Override
    public void updateGroupInfo(Called1100910018DTO.Called1100910018ChatroomInfoDTO dto) {
        if (dto == null) {
            return;
        }

        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setGroupName(dto.getChatroomName());
        groupInfo.setGroupRemark(dto.getRemark());
        groupInfo.setGroupAbout(dto.getAbout());
        groupInfo.setGroupLink(dto.getLink());
        groupInfo.setGroupPrivateLink(dto.getPrivateLink());
        groupInfo.setGroupType(dto.getType());
        groupInfo.setIsChannel(dto.getIsChannel() == null ? null : dto.getIsChannel() ? 1 : 0);
        groupInfo.setMemberCount(dto.getMemberCount());
        groupInfo.setKickedCount(dto.getKickedCount());
        groupInfo.setBannedCount(dto.getBannedCount());
        groupInfo.setOnlineMemberCount(dto.getOnlineMemberCount());
        groupInfo.setUpdateTime(LocalDateTime.now());
        baseMapper.update(groupInfo, new LambdaQueryWrapper<GroupInfo>().eq(GroupInfo::getGroupSerialNo, dto.getChatroomSerialNo()));
    }

    @Override
    public void updateName(String groupId, String groupName) {
        GroupInfo groupInfo = baseMapper.selectById(groupId);
        if (groupInfo == null || ObjectUtil.equal(groupName, groupInfo.getGroupName())) {
            return;
        }

        GroupInfo update = new GroupInfo();
        update.setGroupId(groupId);
        update.setGroupName(groupName);
        baseMapper.updateById(update);
    }

    @Override
    public void updateNameByGroupSerialNo(String groupSerialNo, String groupName) {
        GroupInfo update = new GroupInfo();
        update.setGroupName(groupName);
        baseMapper.update(update, new LambdaQueryWrapper<GroupInfo>().eq(GroupInfo::getGroupSerialNo, groupSerialNo));

    }
}