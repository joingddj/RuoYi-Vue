package com.baoli.sysmanage.controller;

import java.util.List;
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
import com.baoli.sysmanage.domain.BaoliBizCarmodel;
import com.baoli.sysmanage.service.IBaoliBizCarmodelService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 汽车型号Controller
 * 
 * @author niujs
 * @date 2024-03-12
 */
@RestController
@RequestMapping("/sysmanage/carmodel")
public class BaoliBizCarmodelController extends BaseController
{
    @Autowired
    private IBaoliBizCarmodelService baoliBizCarmodelService;

    /**
     * 查询汽车型号列表
     */
    @PreAuthorize("@ss.hasPermi('sysmanage:carmodel:list')")
    @GetMapping("/list")
    public TableDataInfo list(BaoliBizCarmodel baoliBizCarmodel)
    {
        startPage();
        List<BaoliBizCarmodel> list = baoliBizCarmodelService.selectBaoliBizCarmodelList(baoliBizCarmodel);
        return getDataTable(list);
    }

    /**
     * 导出汽车型号列表
     */
    @PreAuthorize("@ss.hasPermi('sysmanage:carmodel:export')")
    @Log(title = "汽车型号", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BaoliBizCarmodel baoliBizCarmodel)
    {
        List<BaoliBizCarmodel> list = baoliBizCarmodelService.selectBaoliBizCarmodelList(baoliBizCarmodel);
        ExcelUtil<BaoliBizCarmodel> util = new ExcelUtil<BaoliBizCarmodel>(BaoliBizCarmodel.class);
        util.exportExcel(response, list, "汽车型号数据");
    }

    /**
     * 获取汽车型号详细信息
     */
    @PreAuthorize("@ss.hasPermi('sysmanage:carmodel:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(baoliBizCarmodelService.selectBaoliBizCarmodelById(id));
    }

    /**
     * 新增汽车型号
     */
    @PreAuthorize("@ss.hasPermi('sysmanage:carmodel:add')")
    @Log(title = "汽车型号", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BaoliBizCarmodel baoliBizCarmodel)
    {
        return toAjax(baoliBizCarmodelService.insertBaoliBizCarmodel(baoliBizCarmodel));
    }

    /**
     * 修改汽车型号
     */
    @PreAuthorize("@ss.hasPermi('sysmanage:carmodel:edit')")
    @Log(title = "汽车型号", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BaoliBizCarmodel baoliBizCarmodel)
    {
        return toAjax(baoliBizCarmodelService.updateBaoliBizCarmodel(baoliBizCarmodel));
    }

    /**
     * 删除汽车型号
     */
    @PreAuthorize("@ss.hasPermi('sysmanage:carmodel:remove')")
    @Log(title = "汽车型号", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(baoliBizCarmodelService.deleteBaoliBizCarmodelByIds(ids));
    }
}
