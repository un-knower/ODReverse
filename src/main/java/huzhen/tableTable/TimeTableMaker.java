package huzhen.tableTable;

import huzhen.subwayPkg.BaseSubwayFactory;
import huzhen.subwayPkg.pojo.BaseSubway;
import huzhen.tableTable.pojo.TimeTableStation;
import huzhen.utils.PropertiesUtil;
import huzhen.utils.TimeConvert;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by hu on 2016/11/3.
 */
public class TimeTableMaker {

    //运行截止时间
    private static String deadtime;

    //地铁工厂
    private static BaseSubwayFactory baseSubwayFactory;


    /**
     * 读取配置文件
     */
    public static TimeTableMaker ttm;
    static {
        ttm =new TimeTableMaker();
        ttm.ReadProperties();
    }

    public void ReadProperties(){
        try {
            PropertiesUtil propertiesUtil = new PropertiesUtil("runner.properties");
            deadtime = propertiesUtil.readValue("deadtime");

        } catch (IOException e) {
            System.out.println("配置文件读取错误!");
            e.printStackTrace();
        }
        baseSubwayFactory = new BaseSubwayFactory();
    }

    /**
     * 时间自增
     * @param date
     * @param timeSpan 以秒为单位
     * @return
     */
    public Date addTime(Date date,Integer timeSpan){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND,timeSpan);
        return calendar.getTime();
    }

    /**
     *  添加列车发车时间间隔
     */
    public Map<String,List<BaseSubway>> makeSubwayList() throws ParseException {
//        List<List<BaseSubway>> lbs = new ArrayList<List<BaseSubway>>();
        Map<String,List<BaseSubway>> lbs =new HashMap<String,List<BaseSubway>>();
        //5号线
        lbs.put("2630",makeOneLineSubway("2630", 327));//这里加的时间在后面没有用上
        lbs.put("2631",makeOneLineSubway("2631", 360));
        //3号线
        lbs.put("2610",makeOneLineSubway("2610", 409));
        lbs.put("2611",makeOneLineSubway("2611", 428));
        //4号线
        lbs.put("2620",makeOneLineSubway("2620", 186));
        lbs.put("2621",makeOneLineSubway("2621", 246));
        //1号线
        lbs.put("2680",makeOneLineSubway("2680", 180));
        lbs.put("2681",makeOneLineSubway("2681", 180));
        //2号线
        lbs.put("2600",makeOneLineSubway("2600", 360));
        lbs.put("2601",makeOneLineSubway("2601", 360));
        //11号线
        lbs.put("2410",makeOneLineSubway("2410", 600));
        lbs.put("2411",makeOneLineSubway("2411", 600));
        //9号线
        lbs.put("2670",makeOneLineSubway("2670", 600));
        lbs.put("2671",makeOneLineSubway("2671", 600));
        //7号线
        lbs.put("2650",makeOneLineSubway("2650", 600));
        lbs.put("2651",makeOneLineSubway("2651", 600));

        return lbs;
    }

    /**
     * 生成单条线的车次
     * @param lineName 线路编号
     * @param timeSpan 发车间隔
     * @return
     */
    private List<BaseSubway> makeOneLineSubway(String lineName,Integer timeSpan) throws ParseException {
        //发车时间
        Date start;

        List<TimeTableStation> lm = TimeTableReader.getLineTimeTable(lineName);

        start = lm.get(0).getDate();

        //发车班次计数
        Integer subwayNO = 0;

        //生成的车次list
        List<BaseSubway> subwayList = new ArrayList<BaseSubway>();

        //暂时记录当前时间偏移量，因为subwayNO从0开始，自乘的话会出现一直为0的BUG
        Integer eachTimeSpan=0;

        //在发车截止时间前的车都发出去
        while(checkDeadTime(start)){
            //计算每次发车的时间与时刻表的差
            subwayList.add(baseSubwayFactory.getSubway(subwayNO, lineName, eachTimeSpan));

            /**
             *根据早晚高峰 制作的列车时刻表
             */
            timeSpan =timeSpanForm(start,lineName,timeSpan);

            eachTimeSpan +=timeSpan;

            //车次号自增
            subwayNO++;
            //发车时间自增
            start = addTime(start,timeSpan);
        }
        return subwayList;
    }

    /**
     * 检查列车目前是否已经运行到了截止时间,用于停止循环计算
     * @param date
     * @return
     */
    public boolean checkDeadTime(Date date){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        try {
            Date deadTimeDate = format.parse(deadtime);
            //比较列车是否在断面时间之前
            if(date.before(deadTimeDate)){
                return true;
            } else {
                return false;
            }
        }catch (Exception e){
            System.out.println("配置的断面时间有误!");
            e.printStackTrace();
        }
        return false;
    }

    /**
     *  读取列车发车时刻间隔表timeIntervalForLine.csv
     * @return
     */
    public Map<String,String> readTimeIntervalForLine(){

        Map<String,String> mapTimeInter = new HashMap<String, String>();

        String pathTimeInter ="timeIntervalForLine.csv";
        InputStream is =this.getClass().getClassLoader().getResourceAsStream(pathTimeInter);

        Scanner scanTimeInter =new Scanner(is,"UTF-8");

        scanTimeInter.nextLine();
        while(scanTimeInter.hasNext()){
            String line = scanTimeInter.nextLine();
            String[] strings =line.split(",");

            String key =strings[1];
            String value =strings[2]+","+strings[3]+","+strings[4]+","+strings[5];
            mapTimeInter.put(key, value);
        }

        return mapTimeInter;
    }

    public  Integer timeSpanForm(Date start,String lineName,int timeSpan) throws ParseException {

        Map<String,String> mapTimeInter =readTimeIntervalForLine();

        if (start.before(TimeConvert.String2Date("7:00:00"))){//平峰
            timeSpan =Integer.parseInt(mapTimeInter.get(lineName).split(",")[3]);
        }else if (start.before(TimeConvert.String2Date("9:30:00"))){//早高峰
            timeSpan =Integer.parseInt(mapTimeInter.get(lineName).split(",")[0]);
        }else if (start.before(TimeConvert.String2Date("16:30:00"))){//平峰
            timeSpan =Integer.parseInt(mapTimeInter.get(lineName).split(",")[3]);
        }else if (start.before(TimeConvert.String2Date("20:00:00"))){//晚高峰
            timeSpan =Integer.parseInt(mapTimeInter.get(lineName).split(",")[1]);
        }else if (start.before(TimeConvert.String2Date("21:30:00"))){//次高峰
            timeSpan =Integer.parseInt(mapTimeInter.get(lineName).split(",")[2]);
        }else if (start.before(TimeConvert.String2Date("23:00:00"))){//平峰
            timeSpan =Integer.parseInt(mapTimeInter.get(lineName).split(",")[3]);
        }
        return timeSpan;


    }

    public static void main(String[] args) {
        /**
         * libs.size():16
         * 2680:228 --2681:228          1号线
         * 2600:139 --2601:139          2号线
         * 2610:132 --2611:131          3号线
         * 2620:212 --2621:223          4号线
         * 2630:142 --2631:139          5号线
         * 2410:99  --2411:99           11号线
         * 2650:99  --2651:99           7号线
         * 2670:99  --2671:99           9号线
         */
        try {

            Map<String, List<BaseSubway>> lbs = new TimeTableMaker().makeSubwayList();

            System.out.println("lbs.size():"+lbs.size());
            System.out.println("lbs.get(\"2680\").size():"+lbs.get("2681").size());
            System.out.println("lbs.get(\"2600\").size():"+lbs.get("2601").size());
            System.out.println("lbs.get(\"2610\").size():"+lbs.get("2611").size());
            System.out.println("lbs.get(\"2620\").size():"+lbs.get("2621").size());
            System.out.println("lbs.get(\"2630\").size():"+lbs.get("2631").size());
            System.out.println("lbs.get(\"2410\").size():"+lbs.get("2411").size());
            System.out.println("lbs.get(\"2650\").size():"+lbs.get("2651").size());
            System.out.println("lbs.get(\"2670\").size():"+lbs.get("2671").size());

            System.out.println("hello");

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }
}
