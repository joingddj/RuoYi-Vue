package com.ruoyi.bean;

import cn.hutool.core.lang.Dict;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @author yangyouqi
 * @date 2024/5/15
 */
@Data
@Accessors(chain = true)
public class FileInfo implements Serializable {

    /**
     * 文件id
     */
    private String id;

    /**
     * 文件访问地址
     */
    private String url;

    /**
     * 文件大小，单位字节
     */
    private Long size;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 原始文件名
     */
    private String originalFilename;

    /**
     * 基础存储路径
     */
    private String basePath;

    /**
     * 存储路径
     */
    private String path;

    /**
     * 文件扩展名
     */
    private String ext;

    /**
     * MIME 类型
     */
    private String contentType;

    /**
     * 存储平台
     */
    private String platform;

    /**
     * 缩略图访问路径
     */
    private String thUrl;

    /**
     * 缩略图名称
     */
    private String thFilename;

    /**
     * 缩略图大小，单位字节
     */
    private Long thSize;

    /**
     * 缩略图 MIME 类型
     */
    private String thContentType;

    /**
     * 文件所属对象id
     */
    private String objectId;

    /**
     * 文件所属对象类型，例如用户头像，评价图片
     */
    private String objectType;

    /**
     * 文件元数据
     */
    private Map<String, String> metadata;

    /**
     * 文件用户元数据
     */
    private Map<String, String> userMetadata;

    /**
     * 缩略图元数据
     */
    private Map<String, String> thMetadata;

    /**
     * 缩略图用户元数据
     */
    private Map<String, String> thUserMetadata;

    /**
     * 附加属性字典
     */
    private Dict attr;

    /**
     * 上传ID，仅在手动分片上传时使用
     */
    private String uploadId;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;

}
