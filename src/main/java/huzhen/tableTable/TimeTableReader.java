package huzhen.tableTable;

import huzhen.tableTable.pojo.TimeTableStation;
import huzhen.utils.NoNameExUtil;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by hu on 2016/11/3.
 *
 *  读取列车时刻表
 */
public class TimeTableReader {

    private static Map<String,List<TimeTableStation>> lineTimeTable;

    //存放两个相邻站点属于那条线,station+station:linename
    private static Map<String,String> linestation2staion = new HashMap<String, String>();
    //存放任意两个站点属于哪条线，因为有可能重合，此处只能用作判断任意两个站点是否属于同一条线
    private static Map<String,String> linesO2D = new HashMap<String, String>();
    /**
     * 判断是否是周末
     * @return
     */
    private static boolean isWeekend(){
        Date today = new Date();
        Calendar cal=Calendar.getInstance();
        cal.setTime(today);
        int week=cal.get(Calendar.DAY_OF_WEEK)-1;
        if(week ==6 || week==0){//0代表周日，6代表周六
            return true;
        }
        return false;
    }




    /**
     * 读取时刻表
     */
    public static TimeTableReader ttr;
    static {
        ttr =new TimeTableReader();
        try {
            ttr.ReadLineTimeTable();
            ttr.ReadLineByStation2Staion();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取发车时刻，线路为数字，后面0(上行)1(下行)
     * @return
     */
    public boolean ReadLineTimeTable() throws IOException{
        Map<String,List<TimeTableStation>> mlm = new HashMap<String, List<TimeTableStation>>();

        //1号线
        mlm.put("2680", readTimeTable("一号线上行(工作日).csv"));
        mlm.put("2681", readTimeTable("一号线下行(工作日).csv"));
        //2号线
        mlm.put("2600", readTimeTable("二号线上行(工作日).csv"));
        mlm.put("2601", readTimeTable("二号线下行(工作日).csv"));
        //3号线
        mlm.put("2610", readTimeTable("三号线上行(工作日).csv"));
        mlm.put("2611", readTimeTable("三号线下行(工作日).csv"));
        //4号线
        mlm.put("2620", readTimeTable("四号线上行(工作日).csv"));
        mlm.put("2621", readTimeTable("四号线下行(工作日).csv"));
        //5号线
        mlm.put("2630", readTimeTable("五号线上行(工作日).csv"));
        mlm.put("2631", readTimeTable("五号线下行(工作日).csv"));
        //11号线
        mlm.put("2410", readTimeTable("十一号线上行(工作日).csv"));
        mlm.put("2411", readTimeTable("十一号线下行(工作日).csv"));
        //7号线
        mlm.put("2650", readTimeTable("七号线上行(工作日).csv"));
        mlm.put("2651", readTimeTable("七号线下行(工作日).csv"));
        //9号线
        mlm.put("2670", readTimeTable("九号线上行(工作日).csv"));
        mlm.put("2671", readTimeTable("九号线下行(工作日).csv"));

        lineTimeTable =  mlm;
        return false;
    }

    /**
     * 读取时刻表
     * @return
     */
    public List<TimeTableStation> readTimeTable(String LineName){
        String path =LineName;
        List<TimeTableStation> mSD = new ArrayList<TimeTableStation>();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        try {
            InputStream is =this.getClass().getClassLoader().getResourceAsStream(path);
            Scanner scan = new Scanner(is, "UTF-8");
            scan.nextLine();//第一行信息，为标题信息,应该先跳过
            while (scan.hasNext()) {
                String line = scan.nextLine();
                String[] lines = line.split(",");
                //将车站名转成编号，时间转成Date
//                mSD.add(new TimeTableStation(lines[0], format.parse(lines[1])));
                mSD.add(new TimeTableStation(NoNameExUtil.Name2NO(lines[0]), format.parse(lines[1])));//站点名称-站点编号
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return mSD;
    }

    public static void ReadLineByStation2Staion(){
        String linename;
        List<TimeTableStation> list;
        //不需要处理的小线
        LinkedList<String> lls = new LinkedList<String>(){
            {
                add("260a0");
                add("260a1");
                add("262a0");
            }
        };

        for(Map.Entry<String,List<TimeTableStation>> meslt:lineTimeTable.entrySet()){
            list = meslt.getValue();
            linename = meslt.getKey();
            if(lls.contains(linename)){
                continue;
            }
            for(int i=0;i<list.size()-1;i++){
                linestation2staion.put(list.get(i).getStation()+list.get(i+1).getStation(),linename);
            }
            for(int m=0;m<list.size();m++){
                for(int n =0;n<list.size();n++){
                    if (m!=n){
                        linesO2D.put(list.get(m).getStation()+list.get(n).getStation(),linename);
                    }
                }
            }
        }
    }

    public static Map<String,String> getLinestation2staion(){
        return linestation2staion;
    }
    public static Map<String,String> getLinesO2D(){
        return linesO2D;
    }
    public static List<TimeTableStation> getLineTimeTable(String lineName){
        return lineTimeTable.get(lineName);
    }

    public static void main(String[] args){

        System.out.println(TimeTableReader.getLinestation2staion().get("12620190001267022000"));
        System.out.println(TimeTableReader.getLinesO2D().get("12620190001267022000"));
    }
}
