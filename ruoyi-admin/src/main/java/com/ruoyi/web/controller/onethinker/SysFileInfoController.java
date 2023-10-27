package com.ruoyi.web.controller.onethinker;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;

import com.ruoyi.onethinker.domain.SysFileInfo;
import com.ruoyi.onethinker.dto.SysFileInfoReqDTO;
import com.ruoyi.onethinker.service.ISysFileInfoService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件信息Controller
 *
 * @author yangyouqi
 * @date 2023-10-25
 */
@RestController
@RequestMapping("/onethinker/file")
public class SysFileInfoController extends BaseController {
    @Autowired
    private ISysFileInfoService sysFileInfoService;

    /**
     * 查询文件信息列表
     */
    @PreAuthorize("@ss.hasPermi('onethinker:file:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysFileInfo sysFileInfo) {
        startPage();
        List<SysFileInfo> list = sysFileInfoService.selectSysFileInfoList(sysFileInfo);
        return getDataTable(list);
    }

    /**
     * 获取文件信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('onethinker:file:query')")
    @GetMapping(value = "/{fileId}")
    public AjaxResult getInfo(@PathVariable("fileId") String fileId) {
        return success(sysFileInfoService.selectSysFileInfoByFileId(fileId));
    }

    /**
     * 新增文件信息
     */
    @PreAuthorize("@ss.hasPermi('onethinker:file:add')")
    @Log(title = "文件信息", businessType = BusinessType.INSERT)
    @PostMapping(value = "/upload")
    public AjaxResult upload(SysFileInfoReqDTO sysFileInfoReqDTO) {
        return AjaxResult.success(sysFileInfoService.upload(sysFileInfoReqDTO));
    }

    /**
     * 删除文件信息
     */
    @PreAuthorize("@ss.hasPermi('onethinker:file:remove')")
    @Log(title = "文件信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{fileIds}")
    public AjaxResult remove(@PathVariable String[] fileIds) {
        return toAjax(sysFileInfoService.deleteSysFileInfoByFileIds(fileIds));
    }
}