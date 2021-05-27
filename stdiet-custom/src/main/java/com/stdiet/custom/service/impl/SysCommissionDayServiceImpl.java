package com.stdiet.custom.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.stdiet.common.core.domain.AjaxResult;
import com.stdiet.common.utils.DateUtils;
import com.stdiet.common.utils.StringUtils;
import com.stdiet.common.utils.bean.ObjectUtils;
import com.stdiet.custom.domain.*;
import com.stdiet.custom.dto.request.SysOrderCommision;
import com.stdiet.custom.dto.response.EveryMonthTotalAmount;
import com.stdiet.custom.mapper.SysCommisionMapper;
import com.stdiet.custom.mapper.SysOrderMapper;
import com.stdiet.custom.mapper.SysOrderPauseMapper;
import com.stdiet.custom.service.ISysCommissionDayService;
import com.stdiet.custom.service.ISysOrderNutritionistReplaceRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
public class SysCommissionDayServiceImpl implements ISysCommissionDayService {

    @Autowired
    private SysCommisionMapper sysCommisionMapper;

    @Autowired
    private SysOrderMapper sysOrderMapper;

    @Autowired
    private SysOrderPauseMapper sysOrderPauseMapper    ;

    @Autowired
    private ISysOrderNutritionistReplaceRecordService sysOrderNutritionistReplaceRecordService;

    //2021-04-13开始使用新的算法计算服务结束日期，直接加上对应天数
    public static final LocalDate newVersionServerDateStartDate = DateUtils.stringToLocalDate("2021-04-13", "yyyy-MM-dd");

    @Override
    public List<SysCommissionDayDetail> calculateCommissionByDay(SysCommision sysCommision){
        List<SysCommissionDayDetail> result = new ArrayList<>();
        //查询用户
        List<SysCommision> list = sysCommisionMapper.getAfterSaleAndNutri(sysCommision);
        //合计
        SysCommissionDayDetail total = new SysCommissionDayDetail();
        total.setTotalCommissionAmount(new BigDecimal(0));
        total.setTotalHasSentCommissionAmount(new BigDecimal(0));
        total.setTotalNotSentCommissionAmount(new BigDecimal(0));
        total.setNextMonthCommission(new BigDecimal(0));
        if(list != null && list.size() > 0){
            sysCommision.setUserId(null);  //由于存在售后、营养师更换问题，不能根据营养师或售后查询订单
            Map<Long, List<SysOrderCommisionDayDetail>> orderDetailMap = getOrderByList(sysCommision, true);
            SysCommissionDayDetail sysCommissionDayDetail = null;
            for(SysCommision commision : list){
                sysCommissionDayDetail = new SysCommissionDayDetail();
                sysCommissionDayDetail.setUserId(commision.getUserId());
                sysCommissionDayDetail.setNickName(commision.getUserName());
                sysCommissionDayDetail.setPostId(commision.getPostId());
                sysCommissionDayDetail.setPostName(commision.getPostName());
                dealServerOrderCommissionDetail(sysCommision, orderDetailMap.get(sysCommissionDayDetail.getUserId()), sysCommissionDayDetail);
                result.add(sysCommissionDayDetail);
                //统计所以用户总提成、已发放提成、未发放提成
                total.setTotalCommissionAmount(total.getTotalCommissionAmount().add(sysCommissionDayDetail.getTotalCommissionAmount()));
                total.setTotalHasSentCommissionAmount(total.getTotalHasSentCommissionAmount().add(sysCommissionDayDetail.getTotalHasSentCommissionAmount()));
                total.setTotalNotSentCommissionAmount(total.getTotalNotSentCommissionAmount().add(sysCommissionDayDetail.getTotalNotSentCommissionAmount()));
                total.setNextMonthCommission(total.getNextMonthCommission().add(sysCommissionDayDetail.getNextMonthCommission()));
            }
        }
        total.setPostName("胜唐体控");
        total.setNickName("合计");
        result.add(total);
        return result;
    }

    /**
     * 根据订单计算该笔订单的服务到期时间(开始时间按照食谱开始时间，不能按照提成计算开始时间)
     * @param sysOrder 订单对象
     * @return
     */
    @Override
    public LocalDate getServerEndDate(SysOrder sysOrder){
        LocalDate serverEndDate = null;
        if(sysOrder != null && sysOrder.getStartTime() != null){
            //服务开始时间，食谱开始时间
            LocalDate serverStartDate = DateUtils.dateToLocalDate(sysOrder.getStartTime());

            //计算基础服务到期时间（不包含暂停）
            serverEndDate = getBaseServerEndDate(sysOrder);

            List<SysOrderPause> pausesList = new ArrayList<>();
            if(sysOrder.getOrderId() != null){
                SysOrderPause sysOrderPause = new SysOrderPause();
                sysOrderPause.setOrderId(sysOrder.getOrderId());
                sysOrderPause.setCusId(sysOrder.getCusId());
                pausesList = sysOrderPauseMapper.getPauseListByCusIdAndOrderId(sysOrderPause);
            }
            //每年每月暂停天数，key为年份加月份，如:2021年1月=20211
            Map<String, Integer> everyYearMonthPauseDay = getEveryYearMonthPauseDay(pausesList, serverStartDate, serverEndDate);
            //该笔订单暂停总天数
            int pauseTotalDay = getTotalByMap(everyYearMonthPauseDay);
            //服务到期时间加上暂停时间
            serverEndDate = serverEndDate.plusDays(pauseTotalDay);
        }
        return serverEndDate;
    }

    /**
     * 计算基础服务到期时间，食谱计划、提成计算服务到期时间共用方法（不包含暂停）
     * @param sysOrder
     * @return
     */
    public LocalDate getBaseServerEndDate(SysOrder sysOrder){
        LocalDate serverEndDate = null;
        //服务开始时间，食谱开始时间或提成计算开始时间
        LocalDate serverStartDate = DateUtils.dateToLocalDate(sysOrder.getStartTime());
        //成交时间
        LocalDate orderDate = DateUtils.dateToLocalDate(sysOrder.getOrderTime());
        //大于界限时间
        if(ChronoUnit.DAYS.between(newVersionServerDateStartDate, orderDate) >= 0){
            //订单总服务天数
            long serverDay = sysOrder.getServeTimeId() != null ? sysOrder.getServeTimeId() : 0L;
            serverDay += sysOrder.getGiveServeDay() != null ? sysOrder.getGiveServeDay().intValue() : 0;
            serverEndDate = serverStartDate.plusDays(serverDay - 1);
        }else{
            //订单总服务月数
            int serverMonth = sysOrder.getServeTimeId() != null ? sysOrder.getServeTimeId().intValue()/30 : 0;
            //服务天数(不满一个月的零头)
            int serverSmallDay = sysOrder.getServeTimeId().intValue()%30 - (serverMonth > 0 ? 0 : 1);
            //赠送时长
            int giveDay = sysOrder.getGiveServeDay() != null ? sysOrder.getGiveServeDay().intValue() : 0;
            //服务到期时间（加赠送时间，不加暂停时间）
            serverEndDate = serverStartDate.plusMonths(serverMonth).plusDays(giveDay+serverSmallDay);
        }
        return serverEndDate;
    }

    /**
     * 计算订单提成详情
     * @param sysCommision
     */
    @Override
    public AjaxResult calculateOrderCommissionDetail(SysCommision sysCommision){
        AjaxResult result = AjaxResult.error("参数错误");
        if(sysCommision.getUserId() == null){
            return result;
        }
        //分页查询2021年1月份之后所有订单
        Map<Long, List<SysOrderCommisionDayDetail>> orderUserMap = getOrderByList(sysCommision, false);
        if(orderUserMap == null || !orderUserMap.containsKey(sysCommision.getUserId())){
            return result;
        }
        //查询用户
        List<SysCommision> list = sysCommisionMapper.getAfterSaleAndNutri(sysCommision);
        if(list == null || list.size() == 0){
            return result;
        }
        Long userId = list.get(0).getUserId();
        Long postId = list.get(0).getPostId();
        //获取每个月的成交总额度
        List<EveryMonthTotalAmount> everyMonthTotalAmountList = sysOrderMapper.getTotalAmountByUserId(sysCommision);
        if(everyMonthTotalAmountList == null || everyMonthTotalAmountList.size() == 0){
            return result;
        }
        Map<String, BigDecimal> everyMonthTotalAmountMap = new TreeMap<>(new MyComparator());
        for (EveryMonthTotalAmount everyMonthTotalAmount : everyMonthTotalAmountList) {
            everyMonthTotalAmountMap.put(everyMonthTotalAmount.getYearMonth(), everyMonthTotalAmount.getTotalAmount());
        }
        //获取每个月的提成比例以及计算提成
        Map<String, Float> rateMap = getRateByAmount(userId, postId, everyMonthTotalAmountMap);
        //总服务金额
        BigDecimal totalServerAmount = BigDecimal.valueOf(0);
        //总提成金额
        BigDecimal totalCommission = BigDecimal.valueOf(0);
        //已发放提成金额
        BigDecimal totalSendCommission = BigDecimal.valueOf(0);
        //未发放提成金额
        BigDecimal totalNotSendCommission = BigDecimal.valueOf(0);
        //根据用户ID获取对应订单列表
        List<SysOrderCommisionDayDetail> orderDetailList = orderUserMap.get(userId);
        for(SysOrderCommisionDayDetail sysOrderCommisionDayDetail : orderDetailList){
            //处理订单提成
            dealCommissionByOrderCommisionDayDetail(sysOrderCommisionDayDetail, everyMonthTotalAmountMap, rateMap);
            totalCommission = totalCommission.add(sysOrderCommisionDayDetail.getOrderCommission());
            totalServerAmount = totalServerAmount.add(sysOrderCommisionDayDetail.getOrderAmount());
            totalSendCommission = totalSendCommission.add(sysOrderCommisionDayDetail.getHasSendOrderCommission());
            totalNotSendCommission = totalNotSendCommission.add(sysOrderCommisionDayDetail.getNotHasSendOrderCommission());
        }
        result = AjaxResult.success();
        int total = sysOrderMapper.selectSimpleOrderMessageCount(sysCommision);
        result.put("total", total);
        result.put("list", orderDetailList);
        result.put("totalServerAmount", totalServerAmount);
        result.put("totalCommission", totalCommission);
        result.put("totalSendCommission", totalSendCommission);
        result.put("totalNotSendCommission", totalNotSendCommission);
        return result;
    }

    /**
     * 根据用户ID统计出该用户在该月所有订单的服务数量、服务总天数、服务订单总额、暂停总天数
     * **/
    public void dealServerOrderCommissionDetail(SysCommision commisionParam, List<SysOrderCommisionDayDetail> orderDetailList, SysCommissionDayDetail sysCommissionDayDetail){
        //总提成
        sysCommissionDayDetail.setTotalCommissionAmount(BigDecimal.valueOf(0));
        //总共已发提成
        sysCommissionDayDetail.setTotalHasSentCommissionAmount(BigDecimal.valueOf(0));
        //总共未发提成
        sysCommissionDayDetail.setTotalNotSentCommissionAmount(BigDecimal.valueOf(0));
        //提成发放计划
        sysCommissionDayDetail.setSendDetailList(new ArrayList<>());
        //下月应发提成
        sysCommissionDayDetail.setNextMonthCommission(BigDecimal.valueOf(0));

        if(orderDetailList == null || orderDetailList.size() == 0 ){
            return;
        }

        //总提成
        BigDecimal totalCommissionAmount = BigDecimal.valueOf(0);
        //已发放提成
        BigDecimal totalHasSentCommissionAmount = BigDecimal.valueOf(0);
        //未发放提成
        BigDecimal totalNotHasSentCommissionAmount = BigDecimal.valueOf(0);
        //已发放提成记录
        List<Map<String, Object>> hasSendYearMonthDetailList = new ArrayList<>();
        //未发放提成计划
        List<Map<String, Object>> notHasSendYearMonthDetailList = new ArrayList<>();

        //获取每个月的成交总额度
        commisionParam.setUserId(sysCommissionDayDetail.getUserId());
        List<EveryMonthTotalAmount> everyMonthTotalAmountList = sysOrderMapper.getTotalAmountByUserId(commisionParam);
        if(everyMonthTotalAmountList == null || everyMonthTotalAmountList.size() == 0){
            return;
        }
        Map<String, BigDecimal> everyMonthTotalAmountMap = new TreeMap<>(new MyComparator());
        for (EveryMonthTotalAmount everyMonthTotalAmount : everyMonthTotalAmountList) {
            everyMonthTotalAmountMap.put(everyMonthTotalAmount.getYearMonth(), everyMonthTotalAmount.getTotalAmount());
        }
        //获取每个月的提成比例以及计算提成
        Map<String, Float> rateMap = getRateByAmount(sysCommissionDayDetail.getUserId(), sysCommissionDayDetail.getPostId(), everyMonthTotalAmountMap);

        //存在提成的年月
        Set<String> commissionYearMonthSet = new TreeSet<>(new MyComparator());
        for (SysOrderCommisionDayDetail sysOrderCommisionDayDetail : orderDetailList) {
            //处理订单提成
            dealCommissionByOrderCommisionDayDetail(sysOrderCommisionDayDetail, everyMonthTotalAmountMap, rateMap);
            //合并每个订单的年月日
            commissionYearMonthSet.addAll(sysOrderCommisionDayDetail.getEveryYearMonthServerCommission().keySet());
            //合计总提成
            totalCommissionAmount = totalCommissionAmount.add(sysOrderCommisionDayDetail.getOrderCommission());
            //合计已发放提成
            totalHasSentCommissionAmount = totalHasSentCommissionAmount.add(sysOrderCommisionDayDetail.getHasSendOrderCommission());
            //合计未发放提成
            totalNotHasSentCommissionAmount = totalNotHasSentCommissionAmount.add(sysOrderCommisionDayDetail.getNotHasSendOrderCommission());
        }

        for (String yearMonth : commissionYearMonthSet) {
            Map<String, Object> map = new HashMap<>();
            BigDecimal yearMonthCommiss = BigDecimal.valueOf(0);
            for (SysOrderCommisionDayDetail sysOrderCommisionDayDetail : orderDetailList) {
                if(sysOrderCommisionDayDetail.getEveryYearMonthServerCommission().containsKey(yearMonth)){
                    yearMonthCommiss = yearMonthCommiss.add(sysOrderCommisionDayDetail.getEveryYearMonthServerCommission().get(yearMonth));
                }
            }
            map.put("yearMonth", yearMonth);
            map.put("yearMonthCommission", yearMonthCommiss);
            if(isSendCommissionByYearMonth(yearMonth)){
                hasSendYearMonthDetailList.add(map);
            }else{
                notHasSendYearMonthDetailList.add(map);
            }
        }

        sysCommissionDayDetail.setTotalCommissionAmount(totalCommissionAmount);
        sysCommissionDayDetail.setTotalHasSentCommissionAmount(totalHasSentCommissionAmount);
        sysCommissionDayDetail.setTotalNotSentCommissionAmount(totalNotHasSentCommissionAmount);
        sysCommissionDayDetail.setSendDetailList(notHasSendYearMonthDetailList);
        sysCommissionDayDetail.setNextMonthCommission(notHasSendYearMonthDetailList.size() > 0 ? (BigDecimal)notHasSendYearMonthDetailList.get(0).get("yearMonthCommission") : new BigDecimal(0));


    }

    /**
     * 处理每个订单的提成，包括已发放、未发放提成
     * @param sysOrderCommisionDayDetail 订单服务详情对象
     * @param everyMonthTotalAmountMap 每个月成交总额
     * @param rateMap 提成比例
     */
    public void dealCommissionByOrderCommisionDayDetail(SysOrderCommisionDayDetail sysOrderCommisionDayDetail, Map<String, BigDecimal> everyMonthTotalAmountMap, Map<String, Float> rateMap){
        String yearMonth = sysOrderCommisionDayDetail.getOrderTime().getYear() + "" + sysOrderCommisionDayDetail.getOrderTime().getMonth().getValue();
        //该笔订单当月的成交总额
        sysOrderCommisionDayDetail.setMonthOrderTotalAmount(everyMonthTotalAmountMap.get(yearMonth));
        //该笔订单对应提成比例
        sysOrderCommisionDayDetail.setCommissionRate(rateMap.get(yearMonth) == null ? rateMap.get("19001") : rateMap.get(yearMonth));
        //计算该笔订单总提成
        sysOrderCommisionDayDetail.setOrderCommission(getMoney(sysOrderCommisionDayDetail.getOrderAmount().doubleValue() * sysOrderCommisionDayDetail.getCommissionRate() / 100D));
        //每年每月提成
        Map<String, BigDecimal> everyYearMonthServerCommission = new TreeMap<>(new MyComparator());
        //每年每月提成是否发放状态
        Map<String, Boolean> everyYearMonthCommissionSendFlag = new TreeMap<>(new MyComparator());
        //当前订单的提成总和，用于最后一个月相减
        BigDecimal currentOrderCommission = BigDecimal.valueOf(0);
        //已发放提成金额
        BigDecimal totalSendCommission = BigDecimal.valueOf(0);
        //未发放提成金额
        BigDecimal totalNotSendCommission = BigDecimal.valueOf(0);

        for (String everyMonth : sysOrderCommisionDayDetail.getEveryYearMonthServerMoney().keySet()) {
            if(everyMonth.equals(sysOrderCommisionDayDetail.getServerEndDate().getYear()+""+sysOrderCommisionDayDetail.getServerEndDate().getMonth().getValue())){
                //最后一个月的提成直接相减，避免误差
                everyYearMonthServerCommission.put(everyMonth, sysOrderCommisionDayDetail.getOrderCommission().subtract(currentOrderCommission));
            }else{
                everyYearMonthServerCommission.put(everyMonth, getMoney(sysOrderCommisionDayDetail.getEveryYearMonthServerMoney().get(everyMonth).doubleValue() * sysOrderCommisionDayDetail.getCommissionRate() / 100D));
            }
            //判断是否已发放
            if(isSendCommissionByYearMonth(everyMonth)){
                everyYearMonthCommissionSendFlag.put(everyMonth, true);
                totalSendCommission = totalSendCommission.add(everyYearMonthServerCommission.get(everyMonth));
            }else{
                everyYearMonthCommissionSendFlag.put(everyMonth, false);
                totalNotSendCommission = totalNotSendCommission.add(everyYearMonthServerCommission.get(everyMonth));
            }
            currentOrderCommission = currentOrderCommission.add(everyYearMonthServerCommission.get(everyMonth));
        }

        sysOrderCommisionDayDetail.setEveryYearMonthServerCommission(everyYearMonthServerCommission);
        sysOrderCommisionDayDetail.setEveryYearMonthCommissionSendFlag(everyYearMonthCommissionSendFlag);
        sysOrderCommisionDayDetail.setHasSendOrderCommission(totalSendCommission);
        sysOrderCommisionDayDetail.setNotHasSendOrderCommission(totalNotSendCommission);
    }

    /**判断该月提成是否已发放*/
    public boolean isSendCommissionByYearMonth(String yearMonth){
        LocalDate localDate = LocalDate.of(Integer.parseInt(yearMonth.substring(0,4)), Integer.parseInt(yearMonth.substring(4)), 1);
        LocalDate nowDate = LocalDate.now();
        long betweenMonth = ChronoUnit.MONTHS.between(localDate, nowDate);
        if(betweenMonth > 1){
            return true;
        }else if(betweenMonth == 1){
            //判断目前是否大于15号
            return nowDate.getDayOfMonth() > 15 ? true : false;
        }
        return false;
    }

    /**
     * 根据用户成交总额获取对应比例，再计算提成
     * */
    public Map<String, Float> getRateByAmount(Long userId, Long postId, Map<String, BigDecimal> amountMap){
        SysCommision tmpQueryCom = new SysCommision();
        tmpQueryCom.setUserId(userId);
        tmpQueryCom.setPostId(postId);
        List<SysCommision> tmpComList = sysCommisionMapper.selectSysCommisionList(tmpQueryCom);
        Map<String, Float> rateMap = new TreeMap<>(new MyComparator());
        //取第一个区间为默认提成比例
        rateMap.put("19001", (tmpComList != null && tmpComList.size() > 0) ? tmpComList.get(0).getRate() : 0.0F);
        //按比例开始时间分类
        Map<String, List<SysCommision>> rateYearMonthMap = getRateMapByStartTime(tmpComList);
        for(String yearMonth : amountMap.keySet()){
            BigDecimal orderAmount = amountMap.get(yearMonth);
            rateMap.put(yearMonth, 0F);
            List<SysCommision> yearMonthRateList = null;
            for (String rateMonth : rateYearMonthMap.keySet()) {
                if(Long.parseLong(yearMonth) >= Long.parseLong(rateMonth)){
                    yearMonthRateList = rateYearMonthMap.get(rateMonth);
                }else{
                    break;
                }
            }
            if(yearMonthRateList != null && yearMonthRateList.size() > 0){
                for (int i = 0; i < yearMonthRateList.size(); i++) {
                    SysCommision com = yearMonthRateList.get(i);
                    double cAmount = com.getAmount().floatValue();
                    Long rateStartYearMonth = null;
                    if(com.getStartTime() != null){
                        rateStartYearMonth = Long.parseLong(DateUtils.dateToLocalDate(com.getStartTime()).getYear() + "" + DateUtils.dateToLocalDate(com.getStartTime()).getMonth().getValue());
                    }else{
                        rateStartYearMonth = 19001L;
                    }

                    if (orderAmount.floatValue() <= cAmount && i == 0 && Long.parseLong(yearMonth) >= rateStartYearMonth) {
                        // 第一条规则
                        rateMap.put(yearMonth,com.getRate());
                        break;
                    } else if (i == yearMonthRateList.size() - 1 && orderAmount.floatValue() > cAmount && Long.parseLong(yearMonth) >= rateStartYearMonth) {
                        // 最后一条规则
                        rateMap.put(yearMonth,com.getRate());
                        break;
                    } else if (cAmount < orderAmount.floatValue() && orderAmount.floatValue() <= yearMonthRateList.get(i + 1).getAmount().floatValue() && Long.parseLong(yearMonth) >= rateStartYearMonth) {
                        // 中间规则
                        rateMap.put(yearMonth,yearMonthRateList.get(i + 1).getRate());
                        break;
                    }
                }
            }
        }
        return rateMap;
    }

    /**
     * 根据比例开始执行时间进行分类
     * @param tmpComList
     * @return
     */
    @Override
    public Map<String, List<SysCommision>> getRateMapByStartTime(List<SysCommision> tmpComList){
        Map<String, List<SysCommision>> result = new TreeMap<>(new MyComparator());
        for (SysCommision sysCommision : tmpComList) {
            String rateStartYearMonth = null;
            if(sysCommision.getStartTime() != null){
                rateStartYearMonth = DateUtils.dateToLocalDate(sysCommision.getStartTime()).getYear() + "" + DateUtils.dateToLocalDate(sysCommision.getStartTime()).getMonth().getValue();
            }else{
                rateStartYearMonth = 19001+"";
            }
            if(result.containsKey(rateStartYearMonth)){
                result.get(rateStartYearMonth).add(sysCommision);
            }else{
                List<SysCommision> list = new ArrayList<>();
                list.add(sysCommision);
                result.put(rateStartYearMonth, list);
            }
        }
        return result;
    }


    /**
     * 查询2021年1月份之后所有订单，对订单进行处理，得出每笔订单的相关信息
     * @param sysCommision 是否
     * @param cutOrderFlag 是否根据售后、营养师更换记录进行订单切割计算提成
     * @return
     */
    public Map<Long, List<SysOrderCommisionDayDetail>> getOrderByList(SysCommision sysCommision, Boolean cutOrderFlag){
        //查询2021年1月份之后所有订单
        List<SysOrder> orderList = sysOrderMapper.selectSimpleOrderMessage(sysCommision);
        //查询所有订单营养师、售后转移记录
        Map<Long, List<SysOrderNutritionistReplaceRecord>> replaceRecordMap = dealNutritionistReplaceRecord(sysOrderNutritionistReplaceRecordService.getSysOrderReplaceRecordByOrderId(null));
        /*if(cutOrderFlag){
            //查询所有订单营养师、售后转移记录
            replaceRecordMap = dealNutritionistReplaceRecord(sysOrderNutritionistReplaceRecordService.getSysOrderReplaceRecordByOrderId(null));
        }*/
        //根据用户ID查询所有替换记录
        /*List<SysOrderNutritionistReplaceRecord> usertReplaceRecordList = null;
        if(replaceOrderFlag != null && replaceOrderFlag && sysCommision.getUserId() != null){
             usertReplaceRecordList = sysOrderNutritionistReplaceRecordService.getSysOrderReplaceRecordByUserId(sysCommision.getUserId());
        }*/
        //整理出每个用户对应的订单List
        Map<Long, List<SysOrderCommisionDayDetail>> userOrderResultMap = new HashMap<>();
        for (SysOrder sysOrder : orderList) {
            //提成开始时间为空、售后人员ID为空、营养师ID都为空、订单金额为空，都视为异常订单
            if(sysOrder.getOrderTime() == null || sysOrder.getCommissStartTime() == null || sysOrder.getServeTimeId() == null
                    || (sysOrder.getAfterSaleId() == null && sysOrder.getNutritionistId() == null)
                    || sysOrder.getAmount() == null){
                //System.out.println("客户："+ sysOrder.getCustomer() +",营养师："+sysOrder.getNutritionist() + ",售后" + sysOrder.getAfterSale());
                continue;
            }
            List<SysOrderCommisionDayDetail> orderCommisionDayDetailList = new ArrayList<>();
            //将服务结束时间设置为空，因为提成的结束时间需要重新计算
            sysOrder.setServerEndTime(null);
            //判断是否存在营养师、售后更换记录
            if(replaceRecordMap.containsKey(sysOrder.getOrderId()) && replaceRecordMap.get(sysOrder.getOrderId()).size() > 0){
                //将订单根据更换记录切割成多个订单
                SysOrderCommisionDayDetail sysOrderCommisionDayDetail = statisticsOrderMessage(sysOrder, sysCommision.getServerScopeStartTime(), sysCommision.getServerScopeEndTime());
                List<SysOrderCommisionDayDetail> muchCommisionDayDetailList = cutOrderByReplaceRecord(sysOrder, sysCommision, sysOrderCommisionDayDetail, replaceRecordMap.get(sysOrder.getOrderId()));
                if(sysCommision.getUserId() != null){
                    for (SysOrderCommisionDayDetail detail : muchCommisionDayDetailList) {
                        if((detail.getAfterSaleId() != null && detail.getAfterSaleId().longValue() == sysCommision.getUserId()) || (detail.getNutritionistId() != null && detail.getNutritionistId().longValue() == sysCommision.getUserId())){
                            orderCommisionDayDetailList.add(detail);
                            break;
                        }
                    }
                }else{
                    orderCommisionDayDetailList.addAll(muchCommisionDayDetailList);
                }
            }else{
                SysOrderCommisionDayDetail commisionDetail = statisticsOrderMessage(sysOrder, sysCommision.getServerScopeStartTime(), sysCommision.getServerScopeEndTime());
                orderCommisionDayDetailList.add(commisionDetail);
            }
            if(orderCommisionDayDetailList != null){
                for (SysOrderCommisionDayDetail detail : orderCommisionDayDetailList) {
                    if(detail.getAfterSaleId() != null && detail.getAfterSaleId() > 0L){
                        addUserOrderResultMap(detail.getAfterSaleId(), detail, userOrderResultMap);
                    }
                    if(detail.getNutritionistId() != null && detail.getNutritionistId() > 0L){
                        addUserOrderResultMap(detail.getNutritionistId(), detail, userOrderResultMap);
                    }
                    if(detail.getAfterSaleId() != null && detail.getAfterSaleId().longValue() == 257L){
                        System.out.println(detail.getOrderId() + "-" + detail.getOrderAmount());
                    }
                }
            }
        }
        return userOrderResultMap;
    }

    /**
     * 对于存在售后、营养师更换的订单进行订单切割
     * @param sysOrder
     * @param sysCommision
     * @param sysOrderCommisionDayDetail
     * @param replaceRecordList
     * @return
     */
    public List<SysOrderCommisionDayDetail> cutOrderByReplaceRecord(SysOrder sysOrder, SysCommision sysCommision, SysOrderCommisionDayDetail sysOrderCommisionDayDetail, List<SysOrderNutritionistReplaceRecord> replaceRecordList){
        List<SysOrderCommisionDayDetail> sysOrderCommisionDayDetailList = new ArrayList<>();

        List<SysOrderNutritionistReplaceRecord> nutritionistRecord = new ArrayList<>();
        List<SysOrderNutritionistReplaceRecord> afterSaleRecord = new ArrayList<>();

        //售后和营养师分类
        for (SysOrderNutritionistReplaceRecord sysOrderRecord : replaceRecordList) {
            if (sysOrderRecord.getNutritionistId() != null && sysOrderRecord.getNutritionistId().longValue() > 0 &&
                    sysOrder.getNutritionistId() != null && sysOrder.getNutritionistId().longValue() != sysOrderRecord.getNutritionistId()) {
                nutritionistRecord.add(sysOrderRecord);
            }
            if (sysOrderRecord.getAfterSaleId() != null && sysOrderRecord.getAfterSaleId().longValue() > 0
                && sysOrder.getAfterSaleId().longValue() != sysOrderRecord.getAfterSaleId()) {
                afterSaleRecord.add(sysOrderRecord);
            }
        }

        //原始订单数据
        Long orderNutritionistId = sysOrder.getNutritionistId();
        Long orderAfterSaleId = sysOrder.getAfterSaleId();
        LocalDate serverStartDate = sysOrderCommisionDayDetail.getServerStartDate();
        LocalDate serverEndDate = sysOrderCommisionDayDetail.getServerEndDate();


        //营养师、售后记录不能都为0
        if(nutritionistRecord.size() == 0){
            sysOrder.setCommissStartTime(DateUtils.localDateToDate(serverStartDate));
            sysOrder.setServerEndTime(DateUtils.localDateToDate(serverEndDate));
            sysOrder.setAfterSaleId(null);
            sysOrderCommisionDayDetailList.add(statisticsOrderMessage(sysOrder, sysCommision.getServerScopeStartTime(), sysCommision.getServerScopeEndTime()));
        }
        if(afterSaleRecord.size() == 0){
            sysOrder.setCommissStartTime(DateUtils.localDateToDate(serverStartDate));
            sysOrder.setServerEndTime(DateUtils.localDateToDate(serverEndDate));
            sysOrder.setNutritionistId(null);
            sysOrderCommisionDayDetailList.add(statisticsOrderMessage(sysOrder, sysCommision.getServerScopeStartTime(), sysCommision.getServerScopeEndTime()));
        }

        Map<String, SysOrderCommisionDayDetail> existMap = new HashMap<>();
        for(int i = 0; i < 2; i++){
            List<SysOrderNutritionistReplaceRecord> nutritAfterSaleRecord = i == 0 ? nutritionistRecord : afterSaleRecord;

            if(nutritAfterSaleRecord.size() > 0){
                //第一天更换记录的开始时间
                LocalDate firstRecordStartTime = DateUtils.dateToLocalDate(nutritAfterSaleRecord.get(0).getStartTime());
                if(ChronoUnit.DAYS.between(firstRecordStartTime, serverEndDate) < 0){
                    firstRecordStartTime = serverEndDate;
                }
                //更换的开始时间大于实际订单开始时间，则需要截断时间生成一个新订单
                if(ChronoUnit.DAYS.between(serverStartDate, firstRecordStartTime) > 0){
                    sysOrder.setCommissStartTime(DateUtils.localDateToDate(serverStartDate));
                    sysOrder.setServerEndTime(DateUtils.localDateToDate(firstRecordStartTime.minusDays(1)));
                    sysOrder.setNutritionistId(i == 0 ? orderNutritionistId : null);
                    sysOrder.setAfterSaleId(i == 1 ? orderAfterSaleId : null);
                    //需要设置金额、服务时长、赠送时长
                    sysOrder.setServeTimeId(ChronoUnit.DAYS.between(DateUtils.dateToLocalDate(sysOrder.getCommissStartTime()), DateUtils.dateToLocalDate(sysOrder.getServerEndTime()))+1);
                    sysOrder.setAmount(getMoney(sysOrderCommisionDayDetail.getDayMoney().doubleValue() * sysOrder.getServeTimeId()));
                    sysOrder.setGiveServeDay(0);
                    sysOrderCommisionDayDetailList.add(statisticsOrderMessage(sysOrder, sysCommision.getServerScopeStartTime(), sysCommision.getServerScopeEndTime()));
                }
                for (int index = 0; index < nutritAfterSaleRecord.size(); index++) {
                    SysOrderNutritionistReplaceRecord record  = nutritAfterSaleRecord.get(index);

                    LocalDate recordStartTime = DateUtils.dateToLocalDate(record.getStartTime());
                    if(ChronoUnit.DAYS.between(recordStartTime, serverStartDate) > 0){
                        recordStartTime = serverStartDate;
                    }
                    if((i == 0 && record.getNutritionistId() == null) || (i == 1 && record.getAfterSaleId() == null)
                            || ChronoUnit.DAYS.between(recordStartTime, serverEndDate) < 0){
                        continue;
                    }
                    //获取下一个记录的开始时间，如果不存在则直接取服务结束时间
                    LocalDate nextRecordStartTime = nutritAfterSaleRecord.size() > index+1 ? DateUtils.dateToLocalDate(nutritAfterSaleRecord.get(index+1).getStartTime()) : null;
                    if(nextRecordStartTime == null || ChronoUnit.DAYS.between(recordStartTime, serverEndDate) < 0){
                        nextRecordStartTime = serverEndDate;
                    }
                    if(ChronoUnit.DAYS.between(recordStartTime, nextRecordStartTime) <= 0){
                        continue;
                    }
                    sysOrder.setCommissStartTime(DateUtils.localDateToDate(recordStartTime));
                    sysOrder.setServerEndTime(DateUtils.localDateToDate(nextRecordStartTime.minusDays(1)));
                    sysOrder.setNutritionistId(i == 0 ? record.getNutritionistId() : null);
                    sysOrder.setAfterSaleId(i == 1 ? record.getAfterSaleId() : null);
                    //需要设置金额、服务时长、赠送时长
                    sysOrder.setServeTimeId(ChronoUnit.DAYS.between(DateUtils.dateToLocalDate(sysOrder.getCommissStartTime()), DateUtils.dateToLocalDate(sysOrder.getServerEndTime()))+1);
                    sysOrder.setAmount(getMoney(sysOrderCommisionDayDetail.getDayMoney().doubleValue() * sysOrder.getServeTimeId()));
                    sysOrder.setGiveServeDay(0);

                    //判断是否已存在相同时间范围的记录
                    String key = DateUtils.dateTime(sysOrder.getCommissStartTime()) + DateUtils.dateTime(sysOrder.getServerEndTime());
                    if(existMap.containsKey(key)){
                        try{
                            SysOrderCommisionDayDetail newSysOrderCommisionDayDetail = ObjectUtils.getObjectByObject(existMap.get(key), SysOrderCommisionDayDetail.class);
                            newSysOrderCommisionDayDetail.setNutritionistId(i == 0 ? record.getNutritionistId() : null);
                            newSysOrderCommisionDayDetail.setAfterSaleId(i == 1 ? record.getAfterSaleId() : null);
                            sysOrderCommisionDayDetailList.add(newSysOrderCommisionDayDetail);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        existMap.put(key, statisticsOrderMessage(sysOrder, sysCommision.getServerScopeStartTime(), sysCommision.getServerScopeEndTime()));
                        sysOrderCommisionDayDetailList.add(existMap.get(key));
                    }
                }
            }
        }
        /**for (SysOrderCommisionDayDetail c : sysOrderCommisionDayDetailList) {
            System.out.println(c.getOrderId() + "-" + c.getNutritionistId() + "-" + c.getAfterSaleId() + "-"+
                    DateUtils.localDateToString(c.getServerStartDate(),"yyyy-MM-dd") + "-" +
                    DateUtils.localDateToString(c.getServerEndDate(),"yyyy-MM-dd") +
                    "-" + c.getDayMoney().doubleValue() + "-" + c.getOrderAmount().doubleValue());
        }**/
        return sysOrderCommisionDayDetailList;
    }

    /**
     * 处理订单的营养师、售后更换记录，以订单ID为单位整理
     * @param list
     * @return
     */
    public Map<Long, List<SysOrderNutritionistReplaceRecord>> dealNutritionistReplaceRecord(List<SysOrderNutritionistReplaceRecord> list){
        Map<Long, List<SysOrderNutritionistReplaceRecord>> orderReplaceRecordMap = new HashMap<>();
        if(list != null && list.size() > 0){
            for (SysOrderNutritionistReplaceRecord record : list) {
                if(orderReplaceRecordMap.containsKey(record.getOrderId())){
                    orderReplaceRecordMap.get(record.getOrderId()).add(record);
                }else{
                    List<SysOrderNutritionistReplaceRecord> recordList = new ArrayList<>();
                    recordList.add(record);
                    orderReplaceRecordMap.put(record.getOrderId(), recordList);
                }
            }
        }
        return orderReplaceRecordMap;
    }

    /**
     * 根据用户ID将订单添加到对应用户的订单List
     * */
    public void addUserOrderResultMap(Long id, SysOrderCommisionDayDetail sysOrderCommisionDayDetail, Map<Long, List<SysOrderCommisionDayDetail>> map){
        if(map.containsKey(id)){
            map.get(id).add(sysOrderCommisionDayDetail);
        }else{
            List<SysOrderCommisionDayDetail> orderMessageMapList = new ArrayList<>();
            orderMessageMapList.add(sysOrderCommisionDayDetail);
            map.put(id, orderMessageMapList);
        }
    }

    /**
     * 统计每笔订单的服务开始时间、结束时间、每年每月服务天数、服务金额、服务暂停天数等信息
     * */
    public SysOrderCommisionDayDetail statisticsOrderMessage(SysOrder sysOrder, String serverScopeStartTime, String serverScopeEndTime){
        //提成计算开始时间（与食谱计划开始时间可能不同）
        LocalDate serverStartDate = DateUtils.dateToLocalDate(sysOrder.getCommissStartTime());
        //提成计算需要按照提成计算开始时间
        sysOrder.setStartTime(sysOrder.getCommissStartTime());
        //订单总服务月数
        int serverMonth = sysOrder.getServeTimeId().intValue()/30;
        //服务天数(不满一个月的零头)
        //int serverSmallDay = sysOrder.getServeTimeId().intValue()%30 - (serverMonth > 0 ? 0 : 1);
        //赠送时长
        int giveDay = sysOrder.getGiveServeDay().intValue();
        //服务到期时间（加赠送时间，不加暂停时间）
        LocalDate serverEndDate = sysOrder.getServerEndTime() == null ? getBaseServerEndDate(sysOrder) : DateUtils.dateToLocalDate(sysOrder.getServerEndTime());
        //订单金额
        BigDecimal orderAmount = sysOrder.getAmount();
        //查询暂停列表
        SysOrderPause pauseParam = new SysOrderPause();
        pauseParam.setOrderId(sysOrder.getOrderId());
        pauseParam.setCusId(sysOrder.getCusId());
        List<SysOrderPause> pauseList = sysOrderPauseMapper.getPauseListByCusIdAndOrderId(pauseParam);
        //每年每月暂停天数，key为年份加月份，如:2021年1月=20211
        Map<String, Integer> everyYearMonthPauseDay = getEveryYearMonthPauseDay(pauseList, serverStartDate, serverEndDate);
        //该笔订单暂停总天数
        int pauseTotalDay = getTotalByMap(everyYearMonthPauseDay);
        //服务到期时间加上暂停时间
        serverEndDate = sysOrder.getServerEndTime() == null ? serverEndDate.plusDays(pauseTotalDay) : serverEndDate;
        //计算每年每月服务天数
        Map<String, Integer> everyYearMonthServerDay = getEveryYearMonthDayCount(serverStartDate, serverEndDate, everyYearMonthPauseDay);
        //服务总天数
        int serverDay = getTotalByMap(everyYearMonthServerDay);
        //每天对应金额
        BigDecimal dayMoney = getMoney(orderAmount.doubleValue()/serverDay);
        //每年每月对于金额
        Map<String, BigDecimal> everyYearMonthServerMoney = getEveryMonthServerMoney(everyYearMonthServerDay, orderAmount, dayMoney, serverEndDate);

        SysOrderCommisionDayDetail sysOrderCommisionDayDetail = new SysOrderCommisionDayDetail();
        sysOrderCommisionDayDetail.setOrderId(sysOrder.getOrderId());
        sysOrderCommisionDayDetail.setOrderTime(DateUtils.dateToLocalDateTime(sysOrder.getOrderTime()));
        sysOrderCommisionDayDetail.setName(sysOrder.getCustomer());
        sysOrderCommisionDayDetail.setServerStartDate(serverStartDate);
        sysOrderCommisionDayDetail.setServerEndDate(serverEndDate);
        sysOrderCommisionDayDetail.setServerMonth(serverMonth);
        sysOrderCommisionDayDetail.setGiveDay(giveDay);
        sysOrderCommisionDayDetail.setPauseTotalDay(pauseTotalDay);
        sysOrderCommisionDayDetail.setOrderAmount(orderAmount);
        sysOrderCommisionDayDetail.setServerDay(serverDay);
        sysOrderCommisionDayDetail.setDayMoney(dayMoney);
        sysOrderCommisionDayDetail.setEveryYearMonthPauseDay(everyYearMonthPauseDay);
        sysOrderCommisionDayDetail.setEveryYearMonthServerDay(everyYearMonthServerDay);
        sysOrderCommisionDayDetail.setEveryYearMonthServerMoney(everyYearMonthServerMoney);
        sysOrderCommisionDayDetail.setAfterSaleId(sysOrder.getAfterSaleId());
        sysOrderCommisionDayDetail.setNutritionistId(sysOrder.getNutritionistId());

        if(StringUtils.isNotEmpty(serverScopeStartTime) && StringUtils.isNotEmpty(serverScopeEndTime)){
            LocalDate realStartTime = DateUtils.stringToLocalDate(serverScopeStartTime, "yyyy-MM-dd");
            LocalDate realEndTime = DateUtils.stringToLocalDate(serverScopeEndTime, "yyyy-MM-dd");
            //计算该时间范围内的暂停时间
            Map<String, Integer> realEveryYearMonthPauseDay = getRealEveryYearMonthPauseDay(pauseList, serverStartDate, serverEndDate, realStartTime, realEndTime);
            //暂停总天数
            int realPauseTotalDay = getTotalByMap(realEveryYearMonthPauseDay);
            //计算每年每月服务天数
            Map<String, Integer> realEveryYearMonthServerDay = getRealEveryYearMonthDayCount(serverStartDate, serverEndDate, realStartTime, realEndTime, everyYearMonthPauseDay);
            //服务总天数
            int realServerDay = getTotalByMap(realEveryYearMonthServerDay);
            //每年每月对于金额
            Map<String, BigDecimal> realEveryYearMonthServerMoney = getRealEveryMonthServerMoney(realEveryYearMonthServerDay, orderAmount, dayMoney, serverEndDate, everyYearMonthServerDay, everyYearMonthServerMoney);
            //服务时间范围内暂停天数
            sysOrderCommisionDayDetail.setPauseTotalDay(realPauseTotalDay);
            sysOrderCommisionDayDetail.setEveryYearMonthPauseDay(realEveryYearMonthPauseDay);
            sysOrderCommisionDayDetail.setServerDay(realServerDay);
            sysOrderCommisionDayDetail.setEveryYearMonthServerDay(realEveryYearMonthServerDay);
            sysOrderCommisionDayDetail.setEveryYearMonthServerMoney(realEveryYearMonthServerMoney);
            sysOrderCommisionDayDetail.setOrderAmount(getBigDecimalTotalByMap(realEveryYearMonthServerMoney));
        }
        return sysOrderCommisionDayDetail;
    }

    /**
     * 获取真正服务时间范围内的每年每月暂停天数
     * @Param list 暂停记录集合
     * */
    public Map<String, Integer> getRealEveryYearMonthPauseDay(List<SysOrderPause> list, LocalDate serverStartDate, LocalDate serverEndDate, LocalDate realStartDate, LocalDate realEndDate){
        Map<String, Integer> pauseMap = new TreeMap<>(new MyComparator());
        if(ChronoUnit.DAYS.between(realEndDate, serverStartDate) > 0 || ChronoUnit.DAYS.between(serverEndDate, realStartDate) > 0){
            return pauseMap;
        }
        //更新服务开始时间
        if(ChronoUnit.DAYS.between(serverStartDate,realStartDate) > 0){
            serverStartDate =  realStartDate;
        }
        //更新服务结束时间
        if(ChronoUnit.DAYS.between(realEndDate,serverEndDate) > 0){
            //serverEndDate =  realEndDate;
        }
        return getEveryYearMonthPauseDay(list, serverStartDate, serverEndDate);
    }

    /**
     * 获取每年每月暂停天数
     * @Param list 暂停记录集合
     * */
    public Map<String, Integer> getEveryYearMonthPauseDay(List<SysOrderPause> list, LocalDate serverStartDate, LocalDate serverEndDate){
        Map<String, Integer> pauseMap = new TreeMap<>(new MyComparator());
        if(list == null){
            return pauseMap;
        }
        for (SysOrderPause sysOrderPause : list) {
            if(sysOrderPause.getPauseStartDate() == null || sysOrderPause.getPauseEndDate() == null){
                continue;
            }
            LocalDate pauseStartDate = DateUtils.dateToLocalDate(sysOrderPause.getPauseStartDate());
            LocalDate pauseEndDate = DateUtils.dateToLocalDate(sysOrderPause.getPauseEndDate());
            //判断暂停时间段是否在服务周期范围内
            if(ChronoUnit.DAYS.between(pauseEndDate, serverStartDate) > 0 || ChronoUnit.DAYS.between(serverEndDate, pauseStartDate) > 0){
                continue;
            }
            if(ChronoUnit.DAYS.between(pauseStartDate, serverStartDate) > 0){
                pauseStartDate = serverStartDate;
            }
            if(ChronoUnit.DAYS.between(serverEndDate, pauseEndDate) > 0){
                //pauseEndDate = serverEndDate;
            }
            //根据暂停记录获取该条记录在每年每月的暂停天数
            Map<String, Integer> orderYearMonthPauseDay = getEveryYearMonthDayCount(pauseStartDate, pauseEndDate, null);
            int totalDay = 0;
            //每条暂停记录的暂停天数进行汇总
            for (String key : orderYearMonthPauseDay.keySet()) {
                totalDay += orderYearMonthPauseDay.get(key).intValue();
                if(pauseMap.containsKey(key)){
                    pauseMap.put(key, pauseMap.get(key) + orderYearMonthPauseDay.get(key));
                }else{
                    pauseMap.put(key, orderYearMonthPauseDay.get(key));
                }
            }
            //服务到期时间刷新
            serverEndDate = serverEndDate.plusDays(totalDay);
        }
        return pauseMap;
    }

    /**
     * 获取Map集合中Value的总和
     * */
    public int getTotalByMap(Map<String, Integer> map){
        int total = 0;
        for(String key : map.keySet()){
            total += map.get(key).intValue();
        }
        return total;
    }

    /**
     * 获取Map<String, BigDecimal>集合中BigDecimal的总和
     * */
    public BigDecimal getBigDecimalTotalByMap(Map<String, BigDecimal> map){
        BigDecimal totalBigDecimal = BigDecimal.valueOf(0);
        for(String key : map.keySet()){
            totalBigDecimal = totalBigDecimal.add(map.get(key));
        }
        return totalBigDecimal;
    }

    /**
     * 获取订单服务时间范围中每年每月服务天数，减去当月暂停天数
     * @Param server_start_date 服务开始时间
     * @Param server_end_date   服务到期时间
     * @Param pauseDayMap       每年每月暂停天数Map
     * */
    public Map<String, Integer> getEveryMonthServerDay(LocalDate server_start_date, LocalDate server_end_date, Map<String, Integer> pauseDayMap){
        return getEveryYearMonthDayCount(server_start_date, server_end_date, pauseDayMap);
    }

    /**
     * 获取订单服务时间范围中每年每月服务金额
     * @Param everyMonthServerDay 每年每月服务天数
     * @Param orderMoney 订单总额
     * @Param dayMoney 每天对于金额
     * @Param serverEndTime 订单服务结束时间
     * */
    public Map<String, BigDecimal> getEveryMonthServerMoney(Map<String, Integer> everyMonthServerDay, BigDecimal orderMoney, BigDecimal dayMoney, LocalDate serverEndTime){
        Map<String, BigDecimal > everyMonthServerMoney = new TreeMap<>(new MyComparator());
        Set<String> keySet = everyMonthServerDay.keySet();
        BigDecimal total = BigDecimal.valueOf(0);
        for(String key : keySet){
            //判断是否为最后一个月
            if(key.equals(serverEndTime.getYear()+""+serverEndTime.getMonth().getValue())){
                //由于小数保留问题，最后一个月的金额等于总额减去前几个月金额，避免总数不一致
                everyMonthServerMoney.put(key, orderMoney.subtract(total));
            }else{
                everyMonthServerMoney.put(key, getMoney(everyMonthServerDay.get(key) * dayMoney.doubleValue()));
                total = total.add(everyMonthServerMoney.get(key));
            }
        }
        return everyMonthServerMoney;
    }

    /**
     * 获取订单服务时间范围中每年每月服务金额
     * @Param everyMonthServerDay 真正每年每月服务天数
     * @Param orderMoney 订单总额
     * @Param dayMoney 每天对于金额
     * @Param serverEndTime 订单服务结束时间
     * @Param everyYearMonthServerDay 整个订单的每年每月服务天数
     * @Param everyYearMonthServerMoney 整个订单的每年每月服务金额
     * */
    public Map<String, BigDecimal> getRealEveryMonthServerMoney(Map<String, Integer> realEveryMonthServerDay, BigDecimal orderMoney, BigDecimal dayMoney, LocalDate serverEndTime,
                                                                Map<String, Integer> everyYearMonthServerDay, Map<String, BigDecimal> everyYearMonthServerMoney){
        Map<String, BigDecimal > everyMonthServerMoney = new TreeMap<>(new MyComparator());
        Set<String> keySet = realEveryMonthServerDay.keySet();
        BigDecimal total = null;
        String lastMonth = serverEndTime.getYear()+""+serverEndTime.getMonth().getValue();
        for(String key : keySet){
            //判断是否为最后一个月，以及实际这个月的服务时间是否等于该整个订单最后一个月的天数
            if(key.equals(lastMonth) && realEveryMonthServerDay.get(key).intValue() == everyYearMonthServerDay.get(key).intValue() ){
                //由于小数保留问题，最后一个月的金额等于总额减去前几个月金额，避免总数不一致
                total = BigDecimal.valueOf(0);
                //获取该笔订单除最后一个月的金额总和
                for (String orderYearMonth : everyYearMonthServerMoney.keySet()) {
                    if(!orderYearMonth.equals(lastMonth)){
                        total = total.add(everyYearMonthServerMoney.get(orderYearMonth));
                    }
                }
                everyMonthServerMoney.put(key, orderMoney.subtract(total));
            }else{
                everyMonthServerMoney.put(key, getMoney(realEveryMonthServerDay.get(key) * dayMoney.doubleValue()));
            }
        }
        return everyMonthServerMoney;
    }

    /**
     * 根据订单服务开始日期、订单服务结束日期、营养师或售后实际开始时间、营养师或售后实际结束时间，统计出实际时间范围内每年每月对应的天数
     * */
    public Map<String, Integer> getRealEveryYearMonthDayCount(LocalDate startDate, LocalDate endDate, LocalDate realStartDate, LocalDate realEndDate, Map<String, Integer> lessDayMap){
        Map<String, Integer> everyYearMonthServerDay = new TreeMap<>(new MyComparator());
        if(ChronoUnit.DAYS.between(realEndDate, startDate) > 0 || ChronoUnit.DAYS.between(endDate, realStartDate) > 0){
            return everyYearMonthServerDay;
        }
        //更新服务开始时间
        if(ChronoUnit.DAYS.between(startDate,realStartDate) > 0){
            startDate =  realStartDate;
        }
        //更新服务结束时间
        if(ChronoUnit.DAYS.between(realEndDate,endDate) > 0){
            endDate =  realEndDate;
        }
        return getEveryYearMonthDayCount(startDate, endDate, lessDayMap);
    }


    /**
     * 根据订单开始日期、订单结束日期统计出时间范围内每年每月对应的天数
     * */
    public Map<String, Integer> getEveryYearMonthDayCount(LocalDate startDate, LocalDate endDate, Map<String, Integer> lessDayMap){
        Map<String, Integer> everyYearMonthServerDay = new TreeMap<>(new MyComparator());
        //每月开始第一天
        LocalDate everyMonthFirstDate = startDate;
        //每月最后一天
        LocalDate everyMonthLastDate = everyMonthFirstDate.with(TemporalAdjusters.lastDayOfMonth());
        int day = 0;
        boolean breakFlag = false;
        //写1000防止死循环
        for(int i = 0; i < 1000; i++){
            if(ChronoUnit.DAYS.between(everyMonthLastDate, endDate) > 0){
                day = Period.between(everyMonthFirstDate, everyMonthLastDate).getDays() + 1;
            }else{
                day = Period.between(everyMonthFirstDate, endDate).getDays() + 1;
                breakFlag = true;
            }
            String key = everyMonthFirstDate.getYear()+""+everyMonthFirstDate.getMonth().getValue();
            day -= (lessDayMap == null || lessDayMap.get(key) == null) ? 0 : lessDayMap.get(key);
            everyYearMonthServerDay.put(key, day >= 0 ? day : 0);
            everyMonthFirstDate = (everyMonthFirstDate.plusMonths(1));
            everyMonthFirstDate = everyMonthFirstDate.of(everyMonthFirstDate.getYear(), everyMonthFirstDate.getMonthValue(), 1);
            everyMonthLastDate = everyMonthFirstDate.with(TemporalAdjusters.lastDayOfMonth());
            if(breakFlag){
                break;
            }
        }
        return everyYearMonthServerDay;
    }

    /**
     * 根据服务月数、赠送时长、暂停天数计算出服务截止日期
     * **/
    public LocalDate getServerEndDate(LocalDate server_start_date, int server_month, int give_daye, int pauseDayCount){
        return server_start_date.plusMonths(server_month).plusDays(give_daye + pauseDayCount);
    }

    /**
     * double转为BigDecimal，保留2位小数，四舍五入
     * */
    public BigDecimal getMoney(Double money){
        return new BigDecimal(money.toString()).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 集合排序key值比较器
     * */
    class MyComparator implements Comparator<String>{

        @Override
        public int compare(String o1, String o2) {
            if(o1.substring(0,4).equals(o2.substring(0,4))){
                return Integer.parseInt(o1.substring(4)) - Integer.parseInt(o2.substring(4));
            }else{
                return Integer.parseInt(o1.substring(0,4)) - Integer.parseInt(o2.substring(0,4));
            }
        }
    }

}
