package com.ruoyi.business.controller;

import java.util.List;
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
import com.ruoyi.business.domain.BusZdjrzbry;
import com.ruoyi.business.service.IBusZdjrzbryService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 重大节日值班人员Controller
 * 
 * @author yaowei
 * @date 2021-02-24
 */
@RestController
@RequestMapping("/business/zdjrzbry")
public class BusZdjrzbryController extends BaseController
{
    @Autowired
    private IBusZdjrzbryService busZdjrzbryService;

    /**
     * 查询重大节日值班人员列表
     */
    @PreAuthorize("@ss.hasPermi('business:zdjrzbry:list')")
    @GetMapping("/list")
    public TableDataInfo list(BusZdjrzbry busZdjrzbry)
    {
        startPage();
        List<BusZdjrzbry> list = busZdjrzbryService.selectBusZdjrzbryList(busZdjrzbry);
        return getDataTable(list);
    }

    /**
     * 导出重大节日值班人员列表
     */
    @PreAuthorize("@ss.hasPermi('business:zdjrzbry:export')")
    @Log(title = "重大节日值班人员", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(BusZdjrzbry busZdjrzbry)
    {
        List<BusZdjrzbry> list = busZdjrzbryService.selectBusZdjrzbryList(busZdjrzbry);
        ExcelUtil<BusZdjrzbry> util = new ExcelUtil<BusZdjrzbry>(BusZdjrzbry.class);
        return util.exportExcel(list, "zdjrzbry");
    }

    /**
     * 获取重大节日值班人员详细信息
     */
    @PreAuthorize("@ss.hasPermi('business:zdjrzbry:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return AjaxResult.success(busZdjrzbryService.selectBusZdjrzbryById(id));
    }

    /**
     * 新增重大节日值班人员
     */
    @PreAuthorize("@ss.hasPermi('business:zdjrzbry:add')")
    @Log(title = "重大节日值班人员", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BusZdjrzbry busZdjrzbry)
    {
        return toAjax(busZdjrzbryService.insertBusZdjrzbry(busZdjrzbry));
    }

    /**
     * 修改重大节日值班人员
     */
    @PreAuthorize("@ss.hasPermi('business:zdjrzbry:edit')")
    @Log(title = "重大节日值班人员", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BusZdjrzbry busZdjrzbry)
    {
        return toAjax(busZdjrzbryService.updateBusZdjrzbry(busZdjrzbry));
    }

    /**
     * 删除重大节日值班人员
     */
    @PreAuthorize("@ss.hasPermi('business:zdjrzbry:remove')")
    @Log(title = "重大节日值班人员", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(busZdjrzbryService.deleteBusZdjrzbryByIds(ids));
    }
}
