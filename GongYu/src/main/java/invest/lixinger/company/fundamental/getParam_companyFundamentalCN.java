package invest.lixinger.company.fundamental;


import com.alibaba.fastjson.JSON;
import invest.lixinger.company.fsTypeOfCompany.VO.fsTypeOfCompanyCNResult_DataVO;
import invest.lixinger.company.fsTypeOfCompany.VO.fsTypeOfCompanyCNResult_RootVO;
import invest.lixinger.company.fsTypeOfCompany.getParam_fsTypeOfCompanyCN;
import invest.lixinger.company.fsTypeOfCompany.getResult_fsTypeOfCompanyCN;
import invest.lixinger.company.fsTypeOfCompany.request_fsTypeOfCompanyCN;
import invest.lixinger.company.fundamental.VO.companyFundamentalCNParam_RootVO;
import invest.lixinger.utils.getResult_NoHoliday;
import invest.lixinger.utils.netRequest;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static invest.lixinger.company.fsTypeOfCompany.request_fsTypeOfCompanyCN.requestFsTypeOfCompanyCN;

public class getParam_companyFundamentalCN {
    public static String getParamCompanyFundamentalCN() throws IOException, ParseException {
        InputStream inputStream = getParam_companyFundamentalCN.class.getClassLoader().getResourceAsStream("indexReqParam.yml");
        Map indexReqParam = new Yaml().load(inputStream);
        String token = (String) indexReqParam.get("token");
        List<String> companyFundamentalMetricsList = (List<String>) indexReqParam.get("companyFundamentalMetricsList");

        String dateYml = (String) indexReqParam.get("enddate");
        String date = getResult_NoHoliday.getResult_NoHoliday(Integer.parseInt(dateYml.substring(0, 4)));

        companyFundamentalCNParam_RootVO companyFundamentalCNParam_rootVO = new companyFundamentalCNParam_RootVO();
        companyFundamentalCNParam_rootVO.setToken(token);
        companyFundamentalCNParam_rootVO.setDate(date);
        companyFundamentalCNParam_rootVO.setMetricsList(companyFundamentalMetricsList);

        fsTypeOfCompanyCNResult_RootVO vo = requestFsTypeOfCompanyCN();
        List<fsTypeOfCompanyCNResult_DataVO> listVO = vo.getData();
        List<String> listStockCodes = new ArrayList<>();

        for (fsTypeOfCompanyCNResult_DataVO fsTypeOfCompanyCNResult_dataVO : listVO) {
            listStockCodes.add(fsTypeOfCompanyCNResult_dataVO.getStockCode());
        }
        companyFundamentalCNParam_rootVO.setStockCodes(listStockCodes);
        return JSON.toJSONString(companyFundamentalCNParam_rootVO);
    }

    public static void main(String[] args) throws IOException, ParseException {
        System.out.println(getParamCompanyFundamentalCN());
    }


}