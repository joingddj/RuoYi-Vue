package com.jjpt.business.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 考试主对象 el_exam
 * 
 * @author ruoyi
 * @date 2024-05-18
 */
public class ElExam extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private String id;

    /** 考试名称 */
    @Excel(name = "考试名称")
    private String title;

    /** 考试描述 */
    @Excel(name = "考试描述")
    private String content;

    /** 1公开2部门3定员 */
    @Excel(name = "1公开2部门3定员")
    private Long openType;

    /** 考试状态 */
    @Excel(name = "考试状态")
    private Long state;

    /** 是否限时 */
    @Excel(name = "是否限时")
    private Long timeLimit;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endTime;

    /** 总分数 */
    @Excel(name = "总分数")
    private Long totalScore;

    /** 总时长（分钟） */
    @Excel(name = "总时长", readConverterExp = "分=钟")
    private Long totalTime;

    /** 及格分数 */
    @Excel(name = "及格分数")
    private Long qualifyScore;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 部门ID */
    @Excel(name = "部门ID")
    private Long deptId;

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
    }
    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }
    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }
    public void setOpenType(Long openType) 
    {
        this.openType = openType;
    }

    public Long getOpenType() 
    {
        return openType;
    }
    public void setState(Long state) 
    {
        this.state = state;
    }

    public Long getState() 
    {
        return state;
    }
    public void setTimeLimit(Long timeLimit) 
    {
        this.timeLimit = timeLimit;
    }

    public Long getTimeLimit() 
    {
        return timeLimit;
    }
    public void setStartTime(Date startTime) 
    {
        this.startTime = startTime;
    }

    public Date getStartTime() 
    {
        return startTime;
    }
    public void setEndTime(Date endTime) 
    {
        this.endTime = endTime;
    }

    public Date getEndTime() 
    {
        return endTime;
    }
    public void setTotalScore(Long totalScore) 
    {
        this.totalScore = totalScore;
    }

    public Long getTotalScore() 
    {
        return totalScore;
    }
    public void setTotalTime(Long totalTime) 
    {
        this.totalTime = totalTime;
    }

    public Long getTotalTime() 
    {
        return totalTime;
    }
    public void setQualifyScore(Long qualifyScore) 
    {
        this.qualifyScore = qualifyScore;
    }

    public Long getQualifyScore() 
    {
        return qualifyScore;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("title", getTitle())
            .append("content", getContent())
            .append("openType", getOpenType())
            .append("state", getState())
            .append("timeLimit", getTimeLimit())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("totalScore", getTotalScore())
            .append("totalTime", getTotalTime())
            .append("qualifyScore", getQualifyScore())
            .append("userId", getUserId())
            .append("deptId", getDeptId())
            .toString();
    }
}
