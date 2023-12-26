package com.ruoyi.system.components;

import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.domain.entity.order.OrderRefund;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.Ids;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.system.domain.dto.ApplyAmountFrozenDTO;
import com.ruoyi.system.mapper.OrderRefundMapper;
import com.ruoyi.system.service.MerchantAmountService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderTools {

	public static String orderId() {
		return Ids.getId().toUpperCase();
	}

	public static void listTask(List<String> orderIds) {

	}

	public static List<OrderRefund> listRefund(List<String> orderIds) {
		final OrderRefundMapper mapper = SpringUtils.getBean(OrderRefundMapper.class);
		return mapper.selectList(new QueryWrapper<OrderRefund>().lambda().in(OrderRefund::getOrderId, orderIds));
	}

	public static String frozen(long price, String merchantId, String orderId) {
		final MerchantAmountService service = SpringUtils.getBean(MerchantAmountService.class);
		ApplyAmountFrozenDTO dto = new ApplyAmountFrozenDTO();
		dto.setAmount(price);
		dto.setMerchantId(merchantId);
		dto.setOrderId(orderId);
		try {
			String frozenId = service.applyAmountFrozen(dto);
			log.info("MerchantAmountService.applyAmountFrozen {} {}", dto, frozenId);
			return frozenId;
		} catch (Throwable e) {
			String trace = Ids.getId();
			log.error("MerchantAmountService.applyAmountFrozen_error {} {}", trace, dto, e);
			throw new ServiceException("冻结用户资金失败 trace: " + trace);
		}
	}

}