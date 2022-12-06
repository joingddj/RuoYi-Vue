package invest.lixinger.index.fundamental.US.spx;

import invest.lixinger.index.fundamental.US.spx.VO.indexFundamentalSPXResult_DataVO;
import invest.lixinger.index.fundamental.US.spx.VO.indexFundamentalSPXResult_RootVO;
import invest.lixinger.utils.netRequest;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Map;

/**
 * 获取单一日期的 沪深A股 信号
 */
public class request_indexFundamentalSPX {
    public static void main(String[] args) throws IOException, ParseException {
        requestIndexFundamentalSPX();
    }
    public static void requestIndexFundamentalSPX() throws IOException, ParseException {
        InputStream inputStream = request_indexFundamentalSPX.class.getClassLoader().getResourceAsStream("indexReqParam.yml");
        Map indexReqParam = new Yaml().load(inputStream);
        String indexFundamentalUSURL = (String) indexReqParam.get("indexFundamentalUSURL");
        String paramJson = getParam_indexFundamentalSPX.getSingleIndexParamJson();
        String resultJson = netRequest.jsonNetPost(indexFundamentalUSURL, paramJson);
//        String resultJson = "{\"code\":1,\"message\":\"success\",\"data\":[{\"date\":\"2022-12-02T00:00:00-05:00\",\"pb\":{\"y10\":{\"median\":{\"avgv\":3.452500457588621,\"cv\":3.9582935307597733,\"cvpos\":0.8574265289912629,\"maxpv\":4.820882080304971,\"maxv\":4.820882080304971,\"minv\":2.255502629886086,\"q2v\":3.1038810921014326,\"q5v\":3.4014468079680604,\"q8v\":3.7151915283179724}},\"y20\":{\"median\":{\"avgv\":3.0088461672971345,\"cv\":3.9582935307597733,\"cvpos\":0.9287132644956314,\"maxpv\":4.820882080304971,\"maxv\":4.820882080304971,\"minv\":1.3401328501481131,\"q2v\":2.477033999838583,\"q5v\":2.9862786344624244,\"q8v\":3.453419894537252}}},\"pe_ttm\":{\"y10\":{\"median\":{\"avgv\":21.898328172580854,\"cv\":23.214058510400417,\"cvpos\":0.6302621127879269,\"maxpv\":34.97472063137363,\"maxv\":34.97472063137363,\"minv\":15.485150653943995,\"q2v\":20.35979445183871,\"q5v\":22.085512855128364,\"q8v\":24.395473400745026}},\"y20\":{\"median\":{\"avgv\":20.863246023433156,\"cv\":23.214058510400417,\"cvpos\":0.7791898332009531,\"maxpv\":34.97472063137363,\"maxv\":34.97472063137363,\"minv\":9.726286111412536,\"q2v\":18.035982283782886,\"q5v\":20.587518489172687,\"q8v\":23.38132346694033}}},\"ps_ttm\":{\"y10\":{\"median\":{\"avgv\":2.5025832709401694,\"cv\":2.7167001105534645,\"cvpos\":0.76131850675139,\"maxpv\":3.7644098770150123,\"maxv\":3.7644098770150123,\"minv\":1.609736886087626,\"q2v\":2.074471653755756,\"q5v\":2.53039801557482,\"q8v\":2.7642555713507524}},\"y20\":{\"median\":{\"avgv\":2.0396446971704942,\"cv\":2.7167001105534645,\"cvpos\":0.880659253375695,\"maxpv\":3.7644098770150123,\"maxv\":3.7644098770150123,\"minv\":0.7334881061901561,\"q2v\":1.5782836621513079,\"q5v\":1.8430251569644684,\"q8v\":2.5943043967607835}}},\"stockCode\":\".INX\"},{\"date\":\"2022-12-01T00:00:00-05:00\",\"pb\":{\"y10\":{\"median\":{\"avgv\":3.4522995866302533,\"cv\":3.9611496900925784,\"cvpos\":0.8581644815256257,\"maxpv\":4.820882080304971,\"maxv\":4.820882080304971,\"minv\":2.255502629886086,\"q2v\":3.103693784928273,\"q5v\":3.401432830677196,\"q8v\":3.7151915283179724}},\"y20\":{\"median\":{\"avgv\":3.0086576352551444,\"cv\":3.9611496900925784,\"cvpos\":0.9290963257199603,\"maxpv\":4.820882080304971,\"maxv\":4.820882080304971,\"minv\":1.3401328501481131,\"q2v\":2.477033999838583,\"q5v\":2.985959395612624,\"q8v\":3.4533219494852956}}},\"pe_ttm\":{\"y10\":{\"median\":{\"avgv\":21.89780564266115,\"cv\":23.332796399961246,\"cvpos\":0.6535558204211362,\"maxpv\":34.97472063137363,\"maxv\":34.97472063137363,\"minv\":15.485150653943995,\"q2v\":20.358670791522762,\"q5v\":22.083860921215063,\"q8v\":24.397656623772534}},\"y20\":{\"median\":{\"avgv\":20.86277922190675,\"cv\":23.332796399961246,\"cvpos\":0.7938430983118173,\"maxpv\":34.97472063137363,\"maxv\":34.97472063137363,\"minv\":9.726286111412536,\"q2v\":18.035982283782886,\"q5v\":20.58699101249679,\"q8v\":23.38132346694033}}},\"ps_ttm\":{\"y10\":{\"median\":{\"avgv\":2.5024982364526345,\"cv\":2.706793899751001,\"cvpos\":0.7493047278506159,\"maxpv\":3.7644098770150123,\"maxv\":3.7644098770150123,\"minv\":1.609736886087626,\"q2v\":2.0733203318984197,\"q5v\":2.530383995673124,\"q8v\":2.7643758499341393}},\"y20\":{\"median\":{\"avgv\":2.039510254078083,\"cv\":2.706793899751001,\"cvpos\":0.8746772591857,\"maxpv\":3.7644098770150123,\"maxv\":3.7644098770150123,\"minv\":0.7334881061901561,\"q2v\":1.5782836621513079,\"q5v\":1.8428481227810933,\"q8v\":2.5942603663989505}}},\"stockCode\":\".INX\"},{\"date\":\"2022-11-30T00:00:00-05:00\",\"pb\":{\"y10\":{\"median\":{\"avgv\":3.451683881559607,\"cv\":3.9745745044108545,\"cvpos\":0.8593563766388558,\"maxpv\":4.820882080304971,\"maxv\":4.820882080304971,\"minv\":2.255502629886086,\"q2v\":3.103542616130383,\"q5v\":3.401413472414295,\"q8v\":3.71513234887816}},\"y20\":{\"median\":{\"avgv\":3.0084684610635186,\"cv\":3.9745745044108545,\"cvpos\":0.929678188319428,\"maxpv\":4.820882080304971,\"maxv\":4.820882080304971,\"minv\":1.3401328501481131,\"q2v\":2.477033999838583,\"q5v\":2.985640156762823,\"q8v\":3.4531502774473317}}},\"pe_ttm\":{\"y10\":{\"median\":{\"avgv\":21.895494992103732,\"cv\":23.51835478305886,\"cvpos\":0.6940802542709575,\"maxpv\":34.97472063137363,\"maxv\":34.97472063137363,\"minv\":15.485150653943995,\"q2v\":20.35863205399124,\"q5v\":22.07949224323945,\"q8v\":24.397656623772534}},\"y20\":{\"median\":{\"avgv\":20.862288652457284,\"cv\":23.51835478305886,\"cvpos\":0.819030591974573,\"maxpv\":34.97472063137363,\"maxv\":34.97472063137363,\"minv\":9.726286111412536,\"q2v\":18.035982283782886,\"q5v\":20.586463535820894,\"q8v\":23.38132346694033}}},\"ps_ttm\":{\"y10\":{\"median\":{\"avgv\":2.502075672061711,\"cv\":2.7375863034642185,\"cvpos\":0.7791021056813667,\"maxpv\":3.7644098770150123,\"maxv\":3.7644098770150123,\"minv\":1.609736886087626,\"q2v\":2.072738889995227,\"q5v\":2.5300656817799965,\"q8v\":2.7643758499341393}},\"y20\":{\"median\":{\"avgv\":2.03937772505213,\"cv\":2.7375863034642185,\"cvpos\":0.8895510528406834,\"maxpv\":3.7644098770150123,\"maxv\":3.7644098770150123,\"minv\":0.7334881061901561,\"q2v\":1.5782836621513079,\"q5v\":1.8426710885977182,\"q8v\":2.5942559923078954}}},\"stockCode\":\".INX\"},{\"date\":\"2022-11-29T00:00:00-05:00\",\"pb\":{\"y10\":{\"median\":{\"avgv\":3.451069864391028,\"cv\":3.8563766614026833,\"cvpos\":0.8394914580850219,\"maxpv\":4.820882080304971,\"maxv\":4.820882080304971,\"minv\":2.255502629886086,\"q2v\":3.1033018659032443,\"q5v\":3.401360688264383,\"q8v\":3.714454159257943}},\"y20\":{\"median\":{\"avgv\":3.0081450856149434,\"cv\":3.8563766614026833,\"cvpos\":0.919745729042511,\"maxpv\":4.820882080304971,\"maxv\":4.820882080304971,\"minv\":1.3401328501481131,\"q2v\":2.4762075411609037,\"q5v\":2.985467551363599,\"q8v\":3.4530735043697773}}},\"pe_ttm\":{\"y10\":{\"median\":{\"avgv\":21.893120951892605,\"cv\":22.491516982024436,\"cvpos\":0.547477155343663,\"maxpv\":34.97472063137363,\"maxv\":34.97472063137363,\"minv\":15.485150653943995,\"q2v\":20.357452091685484,\"q5v\":22.07670052308081,\"q8v\":24.397656623772534}},\"y20\":{\"median\":{\"avgv\":20.862509406218678,\"cv\":22.491516982024436,\"cvpos\":0.7131505760826381,\"maxpv\":34.97472063137363,\"maxv\":34.97472063137363,\"minv\":9.726286111412536,\"q2v\":18.035982283782886,\"q5v\":20.586463535820894,\"q8v\":23.38132346694033}}},\"ps_ttm\":{\"y10\":{\"median\":{\"avgv\":2.50164007861909,\"cv\":2.685898978129877,\"cvpos\":0.7278506158124751,\"maxpv\":3.7644098770150123,\"maxv\":3.7644098770150123,\"minv\":1.609736886087626,\"q2v\":2.072322489480139,\"q5v\":2.5291912472121068,\"q8v\":2.7643758499341393}},\"y20\":{\"median\":{\"avgv\":2.039112682314221,\"cv\":2.685898978129877,\"cvpos\":0.8639253079062376,\"maxpv\":3.7644098770150123,\"maxv\":3.7644098770150123,\"minv\":0.7334881061901561,\"q2v\":1.5781136777471496,\"q5v\":1.8423979577960212,\"q8v\":2.5941091669382397}}},\"stockCode\":\".INX\"},{\"date\":\"2022-11-28T00:00:00-05:00\",\"pb\":{\"y10\":{\"median\":{\"avgv\":3.450485304164852,\"cv\":3.8499556488858766,\"cvpos\":0.8379022646007152,\"maxpv\":4.820882080304971,\"maxv\":4.820882080304971,\"minv\":2.255502629886086,\"q2v\":3.1020064828441916,\"q5v\":3.4012494920212597,\"q8v\":3.714451770231214}},\"y20\":{\"median\":{\"avgv\":3.0079765851032656,\"cv\":3.8499556488858766,\"cvpos\":0.918935028809855,\"maxpv\":4.820882080304971,\"maxv\":4.820882080304971,\"minv\":1.3401328501481131,\"q2v\":2.4762075411609037,\"q5v\":2.9854470173902268,\"q8v\":3.452878819874247}}},\"pe_ttm\":{\"y10\":{\"median\":{\"avgv\":21.904132210735987,\"cv\":22.69677006232498,\"cvpos\":0.5665474771553437,\"maxpv\":34.97472063137363,\"maxv\":34.97472063137363,\"minv\":15.485150653943995,\"q2v\":20.35601353472017,\"q5v\":22.075013853831834,\"q8v\":24.397656623772534}},\"y20\":{\"median\":{\"avgv\":20.862185805190506,\"cv\":22.69677006232498,\"cvpos\":0.7301808066759388,\"maxpv\":34.97472063137363,\"maxv\":34.97472063137363,\"minv\":9.726286111412536,\"q2v\":18.035982283782886,\"q5v\":20.585331226029,\"q8v\":23.38132346694033}}},\"ps_ttm\":{\"y10\":{\"median\":{\"avgv\":2.501224265407543,\"cv\":2.6696744113229256,\"cvpos\":0.7091775923718713,\"maxpv\":3.7644098770150123,\"maxv\":3.7644098770150123,\"minv\":1.609736886087626,\"q2v\":2.0721827407605633,\"q5v\":2.5281810602599175,\"q8v\":2.7643758499341393}},\"y20\":{\"median\":{\"avgv\":2.0389841987433397,\"cv\":2.6696744113229256,\"cvpos\":0.8545599046294456,\"maxpv\":3.7644098770150123,\"maxv\":3.7644098770150123,\"minv\":0.7334881061901561,\"q2v\":1.5781136777471496,\"q5v\":1.8423289559249072,\"q8v\":2.593667049250752}}},\"stockCode\":\".INX\"},{\"date\":\"2022-11-25T00:00:00-05:00\",\"pb\":{\"y10\":{\"median\":{\"avgv\":3.4494720652929813,\"cv\":3.9301102679468434,\"cvpos\":0.8546465448768864,\"maxpv\":4.820882080304971,\"maxv\":4.820882080304971,\"minv\":2.255502629886086,\"q2v\":3.101492511818306,\"q5v\":3.401162385258152,\"q8v\":3.713514110459324}},\"y20\":{\"median\":{\"avgv\":3.0073914424518424,\"cv\":3.9301102679468434,\"cvpos\":0.9273088381330685,\"maxpv\":4.820882080304971,\"maxv\":4.820882080304971,\"minv\":1.3401328501481131,\"q2v\":2.4755558146649927,\"q5v\":2.9850412020346795,\"q8v\":3.452817947309607}}},\"pe_ttm\":{\"y10\":{\"median\":{\"avgv\":21.900147924443136,\"cv\":22.813851798671465,\"cvpos\":0.5814138204924544,\"maxpv\":34.97472063137363,\"maxv\":34.97472063137363,\"minv\":15.485150653943995,\"q2v\":20.353874674388255,\"q5v\":22.066962946190362,\"q8v\":24.395473400745026}},\"y20\":{\"median\":{\"avgv\":20.863889610802193,\"cv\":22.813851798671465,\"cvpos\":0.7416087388282026,\"maxpv\":34.97472063137363,\"maxv\":34.97472063137363,\"minv\":9.726286111412536,\"q2v\":18.035982283782886,\"q5v\":20.58699101249679,\"q8v\":23.38729918797487}}},\"ps_ttm\":{\"y10\":{\"median\":{\"avgv\":2.5004638409446205,\"cv\":2.7121594945084277,\"cvpos\":0.7553613979348689,\"maxpv\":3.7644098770150123,\"maxv\":3.7644098770150123,\"minv\":1.609736886087626,\"q2v\":2.0713589669429657,\"q5v\":2.5256110425226366,\"q8v\":2.7642555713507524}},\"y20\":{\"median\":{\"avgv\":2.0384439308570195,\"cv\":2.7121594945084277,\"cvpos\":0.877656405163853,\"maxpv\":3.7644098770150123,\"maxv\":3.7644098770150123,\"minv\":0.7334881061901561,\"q2v\":1.5772455143268278,\"q5v\":1.841849540825598,\"q8v\":2.5933962746764587}}},\"stockCode\":\".INX\"},{\"date\":\"2022-11-23T00:00:00-05:00\",\"pb\":{\"y10\":{\"median\":{\"avgv\":3.448859623356583,\"cv\":3.893279998709703,\"cvpos\":0.8478951548848292,\"maxpv\":4.820882080304971,\"maxv\":4.820882080304971,\"minv\":2.255502629886086,\"q2v\":3.1013572443641055,\"q5v\":3.4009428612377794,\"q8v\":3.7126541467117193}},\"y20\":{\"median\":{\"avgv\":3.0072081815133127,\"cv\":3.893279998709703,\"cvpos\":0.923917361938816,\"maxpv\":4.820882080304971,\"maxv\":4.820882080304971,\"minv\":1.3401328501481131,\"q2v\":2.4755558146649927,\"q5v\":2.98482939922759,\"q8v\":3.4528092758727658}}},\"pe_ttm\":{\"y10\":{\"median\":{\"avgv\":21.897969738018116,\"cv\":22.8161873625572,\"cvpos\":0.5822081016679904,\"maxpv\":34.97472063137363,\"maxv\":34.97472063137363,\"minv\":15.485150653943995,\"q2v\":20.34792477844941,\"q5v\":22.065087545351233,\"q8v\":24.395473400745026}},\"y20\":{\"median\":{\"avgv\":20.863502329334892,\"cv\":22.8161873625572,\"cvpos\":0.7421533571712357,\"maxpv\":34.97472063137363,\"maxv\":34.97472063137363,\"minv\":9.726286111412536,\"q2v\":18.035982283782886,\"q5v\":20.586463535820894,\"q8v\":23.38729918797487}}},\"ps_ttm\":{\"y10\":{\"median\":{\"avgv\":2.500038814675901,\"cv\":2.7041513554769594,\"cvpos\":0.7486100079428117,\"maxpv\":3.7644098770150123,\"maxv\":3.7644098770150123,\"minv\":1.609736886087626,\"q2v\":2.0707157794953694,\"q5v\":2.5250174589279286,\"q8v\":2.7642555713507524}},\"y20\":{\"median\":{\"avgv\":2.0383101243895614,\"cv\":2.7041513554769594,\"cvpos\":0.8742550655542313,\"maxpv\":3.7644098770150123,\"maxv\":3.7644098770150123,\"minv\":0.7334881061901561,\"q2v\":1.5772455143268278,\"q5v\":1.8416376056116248,\"q8v\":2.5931378879144336}}},\"stockCode\":\".INX\"},{\"date\":\"2022-11-22T00:00:00-05:00\",\"pb\":{\"y10\":{\"median\":{\"avgv\":3.4486831259874995,\"cv\":3.8650896439010882,\"cvpos\":0.8410806515693285,\"maxpv\":4.820882080304971,\"maxv\":4.820882080304971,\"minv\":2.255502629886086,\"q2v\":3.1011938302082336,\"q5v\":3.4009299286665398,\"q8v\":3.7126541467117193}},\"y20\":{\"median\":{\"avgv\":3.0068955936784554,\"cv\":3.8650896439010882,\"cvpos\":0.9205403257846643,\"maxpv\":4.820882080304971,\"maxv\":4.820882080304971,\"minv\":1.3401328501481131,\"q2v\":2.4755156585689075,\"q5v\":2.9846950917521364,\"q8v\":3.4526791659373144}}},\"pe_ttm\":{\"y10\":{\"median\":{\"avgv\":21.897605076531004,\"cv\":22.839623883094507,\"cvpos\":0.5883988875645609,\"maxpv\":34.97472063137363,\"maxv\":34.97472063137363,\"minv\":15.485150653943995,\"q2v\":20.344516753542273,\"q5v\":22.065005578570123,\"q8v\":24.397656623772534}},\"y20\":{\"median\":{\"avgv\":20.86379290754828,\"cv\":22.839623883094507,\"cvpos\":0.7465236392530791,\"maxpv\":34.97472063137363,\"maxv\":34.97472063137363,\"minv\":9.726286111412536,\"q2v\":18.035982283782886,\"q5v\":20.586463535820894,\"q8v\":23.38820446039908}}},\"ps_ttm\":{\"y10\":{\"median\":{\"avgv\":2.4999577533014765,\"cv\":2.671540835807507,\"cvpos\":0.7123559793404847,\"maxpv\":3.7644098770150123,\"maxv\":3.7644098770150123,\"minv\":1.609736886087626,\"q2v\":2.0700472175254867,\"q5v\":2.5250050471039884,\"q8v\":2.7643758499341393}},\"y20\":{\"median\":{\"avgv\":2.0380373240260665,\"cv\":2.671540835807507,\"cvpos\":0.8561779896702424,\"maxpv\":3.7644098770150123,\"maxv\":3.7644098770150123,\"minv\":0.7334881061901561,\"q2v\":1.577131098594689,\"q5v\":1.8412402282449514,\"q8v\":2.592950325272711}}},\"stockCode\":\".INX\"},{\"date\":\"2022-11-21T00:00:00-05:00\",\"pb\":{\"y10\":{\"median\":{\"avgv\":3.4480867936483577,\"cv\":3.7912685012875817,\"cvpos\":0.8263806118394914,\"maxpv\":4.820882080304971,\"maxv\":4.820882080304971,\"minv\":2.255502629886086,\"q2v\":3.101188816094985,\"q5v\":3.4009094350433187,\"q8v\":3.7120414135771833}},\"y20\":{\"median\":{\"avgv\":3.0065895996154395,\"cv\":3.7912685012875817,\"cvpos\":0.9131903059197457,\"maxpv\":4.820882080304971,\"maxv\":4.820882080304971,\"minv\":1.3401328501481131,\"q2v\":2.475097455495347,\"q5v\":2.9845737308522113,\"q8v\":3.4525911709122408}}},\"pe_ttm\":{\"y10\":{\"median\":{\"avgv\":21.92134792834878,\"cv\":22.82713484685617,\"cvpos\":0.5852205005959475,\"maxpv\":34.97472063137363,\"maxv\":34.97472063137363,\"minv\":15.485150653943995,\"q2v\":20.33871321045968,\"q5v\":22.060825078519873,\"q8v\":24.397656623772534}},\"y20\":{\"median\":{\"avgv\":20.86409350716284,\"cv\":22.82713484685617,\"cvpos\":0.7437425506555423,\"maxpv\":34.97472063137363,\"maxv\":34.97472063137363,\"minv\":9.726286111412536,\"q2v\":18.035982283782886,\"q5v\":20.586463535820894,\"q8v\":23.388613194821247}}},\"ps_ttm\":{\"y10\":{\"median\":{\"avgv\":2.499540586632717,\"cv\":2.628461805757264,\"cvpos\":0.655145013905443,\"maxpv\":3.7644098770150123,\"maxv\":3.7644098770150123,\"minv\":1.609736886087626,\"q2v\":2.069465133441727,\"q5v\":2.524498601543041,\"q8v\":2.7643758499341393}},\"y20\":{\"median\":{\"avgv\":2.0377733753225318,\"cv\":2.628461805757264,\"cvpos\":0.8275725069527214,\"maxpv\":3.7644098770150123,\"maxv\":3.7644098770150123,\"minv\":0.7334881061901561,\"q2v\":1.5770958410419307,\"q5v\":1.841187186227032,\"q8v\":2.592940244759138}}},\"stockCode\":\".INX\"}]}";
        indexFundamentalSPXResult_RootVO resultObj = (indexFundamentalSPXResult_RootVO) getResult_indexFundamentalSPX.getResultObj(resultJson);
        double result = calculateFundamentalSPX(resultObj);
        System.out.println("综合百分位为：" + new DecimalFormat("0.00%").format(result));
//
        detailPositionSPX(resultObj);
        targetChangeSPX(resultObj);
    }

    // 计算综合百分位
    public static double calculateFundamentalSPX(indexFundamentalSPXResult_RootVO resultObj) {
        indexFundamentalSPXResult_DataVO vo = resultObj.getData().get(0);
        double pe_10_cvpos = vo.getPe_ttm().getY10().getMedian().getCvpos();
        double pb_10_cvpos = vo.getPb().getY10().getMedian().getCvpos();
        double ps_10_cvpos = resultObj.getData().get(0).getPs_ttm().getY10().getMedian().getCvpos();

        double pe_20_cvpos = vo.getPe_ttm().getY20().getMedian().getCvpos();
        double pb_20_cvpos = vo.getPb().getY20().getMedian().getCvpos();
        double ps_20_cvpos = vo.getPs_ttm().getY20().getMedian().getCvpos();
        return (pe_10_cvpos + pb_10_cvpos + ps_10_cvpos + pe_20_cvpos + pb_20_cvpos + ps_20_cvpos) / 6;
    }

    // 查看10年以及20年的细节
    public static void detailPositionSPX(indexFundamentalSPXResult_RootVO resultObj) {
        indexFundamentalSPXResult_DataVO vo = resultObj.getData().get(0);
        System.out.println("20年 pe 百分位为：" + new DecimalFormat("0.00%").format(vo.getPe_ttm().getY20().getMedian().getCvpos()));
        System.out.println("20年 pb 百分位为：" + new DecimalFormat("0.00%").format(vo.getPb().getY20().getMedian().getCvpos()));
        System.out.println("20年 ps 百分位为：" + new DecimalFormat("0.00%").format(vo.getPs_ttm().getY20().getMedian().getCvpos()));
        System.out.println();
        System.out.println("10年 pe 百分位为：" + new DecimalFormat("0.00%").format(vo.getPe_ttm().getY10().getMedian().getCvpos()));
        System.out.println("10年 pb 百分位为：" + new DecimalFormat("0.00%").format(vo.getPb().getY10().getMedian().getCvpos()));
        System.out.println("10年 ps 百分位为：" + new DecimalFormat("0.00%").format(vo.getPs_ttm().getY10().getMedian().getCvpos()));
        System.out.println();

    }

    // 到达目标点位需要的幅度
    public static void targetChangeSPX(indexFundamentalSPXResult_RootVO resultObj) {
        indexFundamentalSPXResult_DataVO vo = resultObj.getData().get(0);
        //当前分位点的具体数值
        double pe_20_cv = vo.getPe_ttm().getY20().getMedian().getCv();
        double pb_20_cv = vo.getPb().getY20().getMedian().getCv();
        double ps_20_cv = vo.getPs_ttm().getY20().getMedian().getCv();

        double pe_10_cv = vo.getPe_ttm().getY10().getMedian().getCv();
        double pb_10_cv = vo.getPb().getY10().getMedian().getCv();
        double ps_10_cv = vo.getPs_ttm().getY10().getMedian().getCv();
        //80分位点的具体数值
        double pe_20_q8v = vo.getPe_ttm().getY20().getMedian().getQ8v();
        double pb_20_q8v = vo.getPb().getY20().getMedian().getQ8v();
        double ps_20_q8v = vo.getPs_ttm().getY20().getMedian().getQ8v();

        double pe_10_q8v = vo.getPe_ttm().getY10().getMedian().getQ8v();
        double pb_10_q8v = vo.getPb().getY10().getMedian().getQ8v();
        double ps_10_q8v = vo.getPs_ttm().getY10().getMedian().getQ8v();

        double result_q8v = ((pe_10_q8v - pe_10_cv) / pe_10_cv + (pb_10_q8v - pb_10_cv) / pb_10_cv + (ps_10_q8v - ps_10_cv) / ps_10_cv + (pe_20_q8v - pe_20_cv) / pe_20_cv + (pb_20_q8v - pb_20_cv) / pb_20_cv + (ps_20_q8v - ps_20_cv) / ps_20_cv) / 6;
        System.out.println("到达80分位点还需要的幅度为：" + new DecimalFormat("0.00%").format(result_q8v));
        //20分位点的具体数值
        double pe_20_q2v = vo.getPe_ttm().getY20().getMedian().getQ2v();
        double pb_20_q2v = vo.getPb().getY20().getMedian().getQ2v();
        double ps_20_q2v = vo.getPs_ttm().getY20().getMedian().getQ2v();

        double pe_10_q2v = vo.getPe_ttm().getY10().getMedian().getQ2v();
        double pb_10_q2v = vo.getPb().getY10().getMedian().getQ2v();
        double ps_10_q2v = vo.getPs_ttm().getY10().getMedian().getQ2v();

        double result_q2v = ((pe_10_q2v - pe_10_cv) / pe_10_cv + (pb_10_q2v - pb_10_cv) / pb_10_cv + (ps_10_q2v - ps_10_cv) / ps_10_cv + (pe_20_q2v - pe_20_cv) / pe_20_cv + (pb_20_q2v - pb_20_cv) / pb_20_cv + (ps_20_q2v - ps_20_cv) / ps_20_cv) / 6;
        System.out.println("到达20分位点还需要的幅度为：" + new DecimalFormat("0.00%").format(result_q2v));

    }

}