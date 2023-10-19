package com.xinyu.idol.service;

import com.xinyu.idol.pojo.entity.ContentEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyu.idol.pojo.entity.vo.AddContentVo;

import java.util.List;

/**
 * <p>
 * 资源冗余大表 服务类
 * </p>
 *
 * @author yu.li
 * @since 2023-10-17
 */
public interface IContentService extends IService<ContentEntity> {

    List testList();

    void addContent(AddContentVo addContentVo);





}
