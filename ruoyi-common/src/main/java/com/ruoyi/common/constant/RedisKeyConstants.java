package com.ruoyi.common.constant;

/**
 * @author LAM
 * @date 2023/9/20 15:07
 */
public class RedisKeyConstants {

    /**
     * 用户等级配置数据
     * */
    public static final String USER_LEVEL_CONFIG_DATA = "user_level_config_data";
    /**
     * 员工等级配置数据
     * */
    public static final String STAFF_LEVEL_CONFIG_DATA = "staff_level_config_data";
    /**
     * 平台文本内容
     * */
    public static final String PLATFORM_TEXT_CONTENT = "platform_text_content:";
    /**
     * 平台礼物
     * */
    public static final String PLATFORM_GIFT = "platform_gift";
    /**
     * 平台充值配置
     * */
    public static final String PLATFORM_RECHARGE_CONFIG = "platform_recharge_config";

    /**
     * 支付回调完成事件（防止重复消费）
     * */
    public static final String PAYMENT_PAY_SUCCESS = "payment_pay_success:";
    /**
     * 退款回调完成事件（防止重复消费）
     * */
    public static final String PAYMENT_REFUND_SUCCESS = "payment_refund_success:";

    /**
     * banner图数据
     * */
    public static final String APP_BANNER_DATA = "app_banner_data:";

    /**
     * 订单支付信息
     * */
    public static final String ORDER_PAY_INFO = "order_pay_info:p-";
}