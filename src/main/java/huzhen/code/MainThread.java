package huzhen.code;

import huzhen.code.readForm.MapAllpath;
import huzhen.subwayPkg.pojo.BaseSubway;
import huzhen.tableTable.TimeTableMaker;
import huzhen.tableTable.TimeTableReader;
import huzhen.utils.*;

import java.io.*;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static huzhen.code.readForm.MapTimeWalk.getMapWalk;
import static huzhen.utils.RouteChangeStationUtil.Route2ChangeStation;

/**
 * Created by hu on 2016/11/3.
 *
 *  OD反推
 */
public class MainThread {

    private static int IN_STATION;//进站加等待时间记150s
    private static int OUT_STATION;//出站时间记90s
    //    public  int CHANGE_STATION_SMALL = 0;//换乘时间
    public static int count =0;


    public static Map<String,String> linestation2staion= null;
    public static Map<String,String> linesO2D= null;
    public static Map<String, List<BaseSubway>> lbs =null;
    public static Map<String,String> mapWalk =null;

    public static void main(String[] args) throws IOException, ParseException {

        //为了计算运行程序所用时间
        Long start =System.currentTimeMillis();

        //读取配置文件，给IN_STATION和OUT_STATION赋值
        try {
            PropertiesUtil propertiesUtil = new PropertiesUtil("runner.properties");
            IN_STATION = Integer.parseInt(propertiesUtil.readValue("IN_STATION"));
            OUT_STATION = Integer.parseInt(propertiesUtil.readValue("OUT_STATION"));
        } catch (IOException e) {
            System.out.println("配置文件读取错误!");
            e.printStackTrace();
        }

        /**
         *              -------------开始-------------
         */


        linestation2staion= TimeTableReader.getLinestation2staion();
        linesO2D =TimeTableReader.getLinesO2D();
        lbs = new TimeTableMaker().makeSubwayList();
        mapWalk =getMapWalk();



        String path1 = "conf\\part\\part-m-00.txt";
//        String path1 = "conf\\part-m\\tos\\part-r-20161031";
//        String path1 ="conf\\part-oneday\\part-r-20160725";
        Scanner scan1 = new Scanner(new FileInputStream(new File(path1)), CodingDetector.checkTxtCode(path1));
//        String path3 = "conf\\part-oneday\\resultNew\\par-r-2016072";
        String path3 ="conf\\part\\result_tos_20161101_test";
        BufferedWriter bw = new BufferedWriter(new FileWriter(path3));

//        BufferedWriter bwAm = new BufferedWriter(new FileWriter("conf\\part-m\\tos\\timeDiv\\result_tos_20161031_am"));
//        BufferedWriter bwPm = new BufferedWriter(new FileWriter("conf\\part-m\\tos\\timeDiv\\result_tos_20161031_pm"));
//        BufferedWriter bwCi = new BufferedWriter(new FileWriter("conf\\part-m\\tos\\timeDiv\\result_tos_20161031_ci"));
//        BufferedWriter bwPing = new BufferedWriter(new FileWriter("conf\\part-m\\tos\\timeDiv\\result_tos_20161031_ping"));




        while (scan1.hasNext()) {

            //020004938,2016-10-31T18:09:14.000Z,1268008000,2016-10-31T18:41:31.000Z,1260039000
//            String line0 =scan1.nextLine();
//            String[] strings0 =line0.split(",");
//            String line =strings0[0]+","+strings0[1]+",line1,"+strings0[2]+",jindu1,weidu1,"+strings0[3]+",line2,"+strings0[4]+",jindu1,weidu1";



            //326816804,2015-09-16T16:53:59.000Z,地铁五号线,黄贝岭,114.1310382,22.54886497,2015-09-16T17:02:01.000Z,地铁二号线,新秀,114.1444922,22.55086697
            String line = scan1.nextLine();
            String[] strings = line.split(",");

            long time1 = TimeConvert.HourToSeconds(strings[1].substring(11, 19));//16:53:59
            long time2 = TimeConvert.HourToSeconds(strings[6].substring(11, 19));
            double realTime = (time2 - time1) / 60.0;
            String cardNo = strings[0];

            //读第二张表，将OD站点转化为编号，再转化为序号
            String string_O2 =null;
            String string_D2 =null;
            if (FunctionsUtils.isContainsChinese(strings[3])){
                /**
                 *  站点为中文名称
                 */
                string_O2 = NoNameExUtil.Name2Ser(strings[3]);//"黄贝岭"-->string_O:1263037000-->56
                string_D2 = NoNameExUtil.Name2Ser(strings[8]);//"新秀"-->string_D:1260039000-->57
            }else {
                /**
                 * 站点为编号
                 */
                string_O2 = NoNameExUtil.NO2Ser((strings[3]));//"黄贝岭"-->string_O:1263037000-->56
                string_D2 = NoNameExUtil.NO2Ser((strings[8]));//"新秀"-->string_D:1260039000-->57
            }



//            /**
//             *  =======================分时间段输出=====================
//             */
//            if (strings[1].substring(11, 19).compareTo("09:30:00") >=0 && strings[1].substring(11, 19).compareTo("16:30:00") <=0 ) {//平峰
//                String funPing =CheckAllPathNew(cardNo, string_O2, string_D2, realTime, strings);
//                funPing =cardNo+","+string_O2+"-"+string_D2+","+funPing;
//                bwPing.write(funPing);
//                bwPing.newLine();
//                bwPing.flush();
//                System.out.println(funPing);
//            }else if (strings[1].substring(11, 19).compareTo("21:30:00") >=0 && strings[1].substring(11, 19).compareTo("23:59:59") <=0){//平峰
//                String funPing =CheckAllPathNew(cardNo, string_O2, string_D2, realTime, strings);
//                funPing =cardNo+","+string_O2+"-"+string_D2+","+funPing;
//                bwPing.write(funPing);
//                bwPing.newLine();
//                bwPing.flush();
//            }else if (strings[1].substring(11, 19).compareTo("06:00:00") >=0 && strings[1].substring(11, 19).compareTo("09:30:00") <=0 ){//早高峰
//                String funAm =CheckAllPathNew(cardNo, string_O2, string_D2, realTime, strings);
//                funAm =cardNo+","+string_O2+"-"+string_D2+","+funAm;
//                bwAm.write(funAm);
//                bwAm.newLine();
//                bwAm.flush();
//            }else if (strings[1].substring(11, 19).compareTo("16:30:00") >=0 && strings[1].substring(11, 19).compareTo("20:00:00") <=0){//晚高峰
//                String funPm =CheckAllPathNew(cardNo, string_O2, string_D2, realTime, strings);
//                funPm =cardNo+","+string_O2+"-"+string_D2+","+funPm;
//                bwPm.write(funPm);
//                bwPm.newLine();
//                bwPm.flush();
//            }else if (strings[1].substring(11, 19).compareTo("20:00:00") >=0 && strings[1].substring(11, 19).compareTo("21:30:00") <=0){//次高峰
//                String funCi =CheckAllPathNew(cardNo, string_O2, string_D2, realTime, strings);
//                funCi =cardNo+","+string_O2+"-"+string_D2+","+funCi;
//                bwCi.write(funCi);
//                bwCi.newLine();
//                bwCi.flush();
//            }



            /**
             * ==========================正常输出========================
             * 读第三张表allpathNew.txt，输出结果
             */
            String fun =CheckAllPathNew(cardNo, string_O2, string_D2, realTime, strings);

            fun = cardNo+","+fun;
            System.out.println(fun);
//            System.out.println(count);
            bw.write(fun);
            bw.newLine();
            bw.flush();
        }
        System.out.println("time:"+(System.currentTimeMillis() -start)/1000/60.0+"min");
        bw.close();
    }







    //将OD站点与表allpathNew.txt 匹配，找出相应OD路径，并输出
    public static String CheckAllPathNew(String cardNo, String string_O2, String string_D2, double realTime, String[] strings) throws ParseException {

        boolean flag_oneChange = false;//是否有一次换乘满足条件
        boolean flag_twoChange = false;//
        boolean flag_threeChange = false;//
        boolean flag_fourChange =false;//是否有四次换乘满足条件

        int isOneChanged = 0;
        int isTwoChange = 0;
        int isThreeChange = 0;
        int isFourChange = 0;



        String key = string_O2 + "-" + string_D2;//key:56,57
        List<String> value = MapAllpath.getAllpath().get(key);//value:[56 57 #0 T 0.00000000  1.76666667 ]

        int valueNo;
        for (valueNo = 0; valueNo < value.size(); valueNo++) {


            /**
             * 若只有一条路径，则直接输出
             */
            if (value.size() == 1) {
                String fun = funtionPrintData(cardNo,value.get(0));                                                                             //----------直接输出
                return fun;
            }



            /**
             * 若有多条路径，则分别计算
             */
            String line3 =value.get(valueNo);

            String[] strings_allpath = line3.split("#");//56 57 #0 T 0.00000000  1.76666667
            String[] strings_allpath1 = strings_allpath[0].split(" ");
            String[] strings_allpath2 = strings_allpath[1].split(" ");
            double time = Double.parseDouble(strings_allpath2[strings_allpath2.length - 1]);


            /**
             * 若OD在同一条线路上
             *      若有直达的路径
             *          满足直达条件：输出直达路径
             *          不满足直达条件：选择时间最接近的路径输出
             *      没有直达路径
             *          选择时间最接近的路径输出
             */
            if (linesO2D.get(NoNameExUtil.Ser2NO(string_O2) + NoNameExUtil.Ser2NO(string_D2)) !=null) {

                if (strings_allpath2[0].equals("0")) {
                    String station2station = NoNameExUtil.Ser2NO(strings_allpath1[0]) + NoNameExUtil.Ser2NO(strings_allpath1[1]);
                    String lineNo = linestation2staion.get(station2station);
                    Boolean flagLine = ODPathBack_isOneNo(strings, lineNo);

                    if (flagLine) {
                        String fun = funtionPrintData(cardNo, line3);                                                                                      //----------直接输出
                        return fun;
                    } else {
                        String closeLine = CloseTimeLine(value, time, realTime);
                        String fun = funtionPrintData(cardNo, closeLine);
                        return fun;
                    }
                } else {
                    String closeLine = CloseTimeLine(value, time, realTime);
                    String fun = funtionPrintData(cardNo, closeLine);
                    return fun;
                }
            } else {
                /**
                 * 若OD不在同一条线路上
                 *  O点在哪一条线上，D点在哪一条线上                                                                --------处理一次换乘代码
                 */

                /**
                 * 若经一次换乘的路线不在相邻，则会出错，因此，先对allpathNew排序，将一次换乘的排在最前
                 *      已经按照顺序排好 表allpathNew2.txt
                 *
                 * 若两条路径均满足换乘条件，则选择一条时间更接近的输出
                 */
                if (strings_allpath2[0].equals("1")) {
                    /**
                     *  有一次换乘路径的 --遍历所有
                     *      有满足条件的一次换乘路径：输出时间最接近的那条路径
                     *      没有满足条件的换乘路径：
                     *          是最后一条路径：选择时间最接近的那条路径输出
                     *          不是最后一条路径：继续多次换乘判断
                     */
                    boolean flag_isOneChange =false;
                    if (isOneChanged == 1) {
                        continue;
                    }

                    double closeTime = time;
                    String closeLine = line3;

                    int m;
                    for (m = 0; m < value.size(); m++) {//56 57 #0 T 0.00000000  1.76666667
                        if (value.get(m).split("#")[1].split(" ")[0].equals("1")) {
                            String[] stations = value.get(m).split("#")[0].split(" ");

                            String changeStations = Route2ChangeStation(value.get(m).split("#")[0].replaceAll(" ", "-"), linestation2staion);

                            flag_oneChange = ODPathBack_isOneChange(stations, changeStations, strings);

                            if (flag_oneChange) {
                                String[] times = value.get(m).split("#")[1].split(" ");
                                if (Double.parseDouble(times[times.length - 1]) <= realTime && Double.parseDouble(times[times.length - 1]) >= closeTime) {
                                    flag_oneChange = false;
                                    flag_isOneChange =true;
                                    closeTime = Double.parseDouble(times[times.length - 1]);
                                    closeLine = value.get(m);
                                    continue;
                                }
                            }
                        }
                    }
                    if (flag_isOneChange) {
                        String fun = funtionPrintData(cardNo, closeLine);
                        return fun;
                    } else {
                        if (valueNo == value.size() - 1 || m ==value.size()) {
                            closeLine = CloseTimeLine(value, time, realTime);
                            String fun = funtionPrintData(cardNo, closeLine);
                            return fun;
                        } else {
                            isOneChanged = 1;
                            continue;
                        }
                    }
                } else if (strings_allpath2[0].equals("2")) {
                    /**
                     * 两次换乘判断
                     *      有满足两次换乘的路径：选取时间最接近的那条路径输出
                     *      不满足两次换乘路径：
                     *          是最后一条路径：选择时间最接近的一条路径输出
                     *          不是最后一条路径：继续多次判断
                     */
                    boolean flag_isTwoChange =false;

                    if (isTwoChange == 1) {
                        continue;
                    }

                    double closeTime = time;
                    String closeLine = line3;

                    int m;
                    for (m = 0; m < value.size(); m++) {//56 57 #0 T 0.00000000  1.76666667
                        if (value.get(m).split("#")[1].split(" ")[0].equals("2")) {
                            String[] stations = value.get(m).split("#")[0].split(" ");

                            String changeStations = Route2ChangeStation(value.get(m).split("#")[0].replaceAll(" ", "-"), linestation2staion);

                            flag_twoChange = ODPathBack_isTwoChange(stations, changeStations, strings);

                            if (flag_twoChange) {
                                String[] times = value.get(m).split("#")[1].split(" ");
                                if (Double.parseDouble(times[times.length - 1]) <= realTime && Double.parseDouble(times[times.length - 1]) >= closeTime) {
                                    flag_twoChange = false;
                                    flag_isTwoChange =true;
                                    closeTime = Double.parseDouble(times[times.length - 1]);
                                    closeLine = value.get(m);
                                    continue;
                                }
                            }
                        }
                    }
                    if (flag_isTwoChange) {
                        String fun = funtionPrintData(cardNo, closeLine);
                        return fun;
                    } else {
                        if (valueNo == value.size() - 1 || m ==value.size()) {
                            closeLine = CloseTimeLine(value, time, realTime);
                            String fun = funtionPrintData(cardNo, closeLine);
                            return fun;
                        } else {
                            isTwoChange = 1;
                            continue;
                        }
                    }
                } else if (strings_allpath2[0].equals("3")) {
                    /**
                     * 三次换乘判断   --遍历所有路径
                     *      有满足三次换乘的路径：选取时间最接近的那条路径输出
                     *      不满足三次换乘路径：
                     *          是最后一条路径：选择时间最接近的一条路径输出
                     *          不是最后一条路径：继续多次判断
                     */

                    boolean flag_isThreeChange =false;
                    if (isThreeChange == 1) {
                        continue;
                    }

                    double closeTime = time;
                    String closeLine = line3;

                    int m;
                    for (m = 0; m < value.size(); m++) {//56 57 #0 T 0.00000000  1.76666667
                        if (value.get(m).split("#")[1].split(" ")[0].equals("3")) {
                            String[] stations = value.get(m).split("#")[0].split(" ");

                            String changeStations = Route2ChangeStation(value.get(m).split("#")[0].replaceAll(" ", "-"), linestation2staion);

                            flag_threeChange = ODPathBack_isThreeChange(stations, changeStations, strings);

                            if (flag_threeChange) {
                                String[] times = value.get(m).split("#")[1].split(" ");
                                if (Double.parseDouble(times[times.length - 1]) <= realTime && Double.parseDouble(times[times.length - 1]) >= closeTime) {
                                    flag_threeChange = false;
                                    flag_isThreeChange =true;
                                    closeTime = Double.parseDouble(times[times.length - 1]);
                                    closeLine = value.get(m);
                                    continue;
                                }
                            }
                        }
                    }
                    if (flag_isThreeChange) {
                        String fun = funtionPrintData(cardNo, closeLine);
                        return fun;
                    } else {
                        if (valueNo == value.size() - 1 || m ==value.size()) {
                            closeLine = CloseTimeLine(value, time, realTime);
                            String fun = funtionPrintData(cardNo, closeLine);
                            return fun;
                        } else {
                            isThreeChange = 1;
                            continue;
                        }
                    }
                } else if (strings_allpath2[0].equals("4")) {

                    /**
                     * 四次换乘判断   --遍历所有路径
                     *      有满足四次换乘的路径：选取时间最接近的那条路径输出
                     *      不满足四次换乘路径：
                     *          是最后一条路径：选择时间最接近的一条路径输出
                     *          不是最后一条路径：继续多次判断
                     */


                    boolean flag_isFourChange =false;
                    if (isFourChange == 1) {
                        continue;
                    }

                    double closeTime = time;
                    String closeLine = line3;

                    int m;
                    for (m = 0; m < value.size(); m++) {//56 57 #0 T 0.00000000  1.76666667
                        if (value.get(m).split("#")[1].split(" ")[0].equals("4")) {
                            String[] stations = value.get(m).split("#")[0].split(" ");

                            String changeStations = Route2ChangeStation(value.get(m).split("#")[0].replaceAll(" ", "-"), linestation2staion);

                            flag_fourChange = ODPathBack_isFourChange(stations, changeStations, strings);

                            if (flag_fourChange) {
                                String[] times = value.get(m).split("#")[1].split(" ");
                                if (Double.parseDouble(times[times.length - 1]) <= realTime && Double.parseDouble(times[times.length - 1]) >= closeTime) {
                                    flag_fourChange = false;
                                    flag_isFourChange =true;
                                    closeTime = Double.parseDouble(times[times.length - 1]);
                                    closeLine = value.get(m);
                                    continue;
                                }
                            }
                        }
                    }
                    if (flag_isFourChange == true) {
                        String fun = funtionPrintData(cardNo, closeLine);
                        return fun;
                    } else {
                        if (valueNo == value.size() - 1 || m ==value.size()) {
                            closeLine = CloseTimeLine(value, time, realTime);
                            String fun = funtionPrintData(cardNo, closeLine);
                            return fun;
                        } else {
                            isFourChange = 1;
                            continue;
                        }
                    }
                } else {
                    /**
                     * 如果四次换乘依然不成功，则直接输出时间最接近的一条路径
                     */
                    double closeTime = time;
                    String closeLine = line3;
                    closeLine = CloseTimeLine(value, time, realTime);
                    String fun = funtionPrintData(cardNo, closeLine);
                    return fun;
                }
            }
        }
        String closeLine = value.get(0).toString();
        String fun = funtionPrintData(cardNo, closeLine);
        return fun;
//        return null;
    }

    /**
     * 输出，并跳出循环
     *
     * @param cardNo   卡号
     * @throws java.io.IOException
     */
    public static String funtionPrintData(String cardNo,String strings){

        count++;

        String stringCon = ConvertOD(strings);
//        stringCon =cardNo + "," + stringCon;
//        System.out.println("count:"+count);
//        System.out.println("hello");
//        System.out.println(cardNo + "," + stringCon);

        return stringCon;
    }


    /**
     *  判断四次换乘是否成立  #4
     */
    public static boolean ODPathBack_isFourChange(String[] stations,String changeStations,String[] strings) throws ParseException{
        boolean flag = false;

        String date1 = strings[1].substring(11, 19);//O站出发时间2015-09-16T12:09:47.000Z-->08:21:15
        String date2 = strings[6].substring(11, 19);//D站到达时间2015-09-16T12:28:05.000Z-->09:09:30
        date1 = TimeConvert.Second2Hour(TimeConvert.HourToSeconds(date1) + 8 * 60 * 60 + IN_STATION);//刷卡进站+等待时间150秒
        date2 = TimeConvert.Second2Hour(TimeConvert.HourToSeconds(date2) + 8 * 60 * 60 - OUT_STATION);//出站刷卡时间90秒

        String[] odChangeStations =changeStations.split("-");
        String string_O2 =odChangeStations[0];
        String string_D2 =odChangeStations[1];
        String changeStation1 =odChangeStations[2];
        String changeStation2 =odChangeStations[3];
        String changeStation3 =odChangeStations[4];
        String changeStation4 =odChangeStations[5];

        String stationNext1 =null;
        String stationNext2 =null;
        String stationNext3 =null;
        for (int i =0;i<stations.length-1;i++) {
            if (stations[i].equals(NoNameExUtil.NO2Ser(changeStation1))) {
                stationNext1 = NoNameExUtil.Ser2NO(stations[i + 1]);
            }
            if (stations[i].equals(NoNameExUtil.NO2Ser(changeStation2))) {
                stationNext2 = NoNameExUtil.Ser2NO(stations[i + 1]);
            }
            if (stations[i].equals(NoNameExUtil.NO2Ser(changeStation3))) {
                stationNext3 = NoNameExUtil.Ser2NO(stations[i + 1]);
            }
        }

        String station2stationFirst = NoNameExUtil.Ser2NO(stations[0])+ NoNameExUtil.Ser2NO(stations[1]);
        String lineNo_First = linestation2staion.get(station2stationFirst);
        String station2stationLast = NoNameExUtil.Ser2NO(stations[stations.length - 2])+ NoNameExUtil.Ser2NO(stations[stations.length - 1]);
        String lineNo_Last = linestation2staion.get(station2stationLast);
        String station2stationSecond =changeStation1+stationNext1;
        String lineNo_Second =linestation2staion.get(station2stationSecond);
        String station2stationThird =changeStation2+stationNext2;
        String lineNo_Third =linestation2staion.get(station2stationThird);
        String station2stationForth =changeStation3+stationNext3;
        String lineNo_Forth =linestation2staion.get(station2stationForth);

        Date timeo_change_first = null;//从第一个换乘站出发的时间
        Date timed_change_first = null;//到达第一个换成站的时间
        Date timeo_change_second = null;
        Date timed_change_second = null;
        Date timeo_change_third = null;
        Date timed_change_third = null;

        Date timeo_change_last =null;
        Date timed_change_last =null;



        /**
         * 第一次换乘        到达第一个换乘点的时间    timed_change_first
         */


        timed_change_first =timed_change(lineNo_First,string_O2, TimeConvert.String2Date(date1),changeStation1);

        /**
         *  第一次换乘        换乘点changeStation1的换乘时间
         */
        int CHANGE_STATION_SMALL1 =change_station_walkTime(changeStation1,lineNo_First,lineNo_Second);


        timeo_change_first = TimeConvert.String2Date(TimeConvert.Second2Hour(TimeConvert.HourToSeconds(TimeConvert.Date2String(timed_change_first)) + 8 * 60 * 60 + CHANGE_STATION_SMALL1));


        /**
         *     找到第二个换乘站到达的时间 timed_change_second
         */

        timed_change_second =timed_change(lineNo_Second,changeStation1,timeo_change_first,changeStation2);
        /**
         * 第二个换点点changeStation2  的换乘时间      CHANGE_STATION_SMALL2
         */
        int CHANGE_STATION_SMALL2 =change_station_walkTime(changeStation2,lineNo_Second,lineNo_Third);


        timeo_change_second = TimeConvert.String2Date(TimeConvert.Second2Hour(TimeConvert.HourToSeconds(TimeConvert.Date2String(timed_change_second)) + 8 * 60 * 60 + CHANGE_STATION_SMALL2));


        /***
         * 由第二个换乘点出发时间，可计算计算第三个换乘点
         */

        timed_change_third =timed_change(lineNo_Third,changeStation2,timeo_change_second,changeStation3);


        /**
         * 计算最后一个换乘changeStation3 的换乘时间     CHANGE_STATION_SMALL_LAST
         */

        int CHANGE_STATION_SMALL3 =change_station_walkTime(changeStation3,lineNo_Third,lineNo_Forth);


        timeo_change_third = TimeConvert.String2Date(TimeConvert.Second2Hour(TimeConvert.HourToSeconds(TimeConvert.Date2String(timed_change_third)) + 8 * 60 * 60 + CHANGE_STATION_SMALL3));


        /***
         * 由第3个换乘点出发时间，可计算计算第4个换乘点,记最后一个换乘点的 到达时间
         */

        timed_change_last =timed_change(lineNo_Forth,changeStation3,timeo_change_third,changeStation4);

        /**
         * 计算最后一个换乘changeStation3 的换乘时间     CHANGE_STATION_SMALL_LAST
         */

        int CHANGE_STATION_SMALL_LAST =change_station_walkTime(changeStation4,lineNo_Forth,lineNo_Last);


        /**
         * 最后一次换乘    从最后一个换乘点出发的时间    timeo_change_last
         */
        timeo_change_last =timeo_change(lineNo_Last,string_D2, TimeConvert.String2Date(date2), changeStation4);


        /**
         *
         */
        if (TimeConvert.HourToSeconds(TimeConvert.Date2String(timeo_change_last)) - TimeConvert.HourToSeconds(TimeConvert.Date2String(timed_change_last)) >= CHANGE_STATION_SMALL_LAST) {
            flag = true;
        }
        return flag;
    }


    /**
     *  判断三次换乘是否成立   #3
     */
    public static boolean ODPathBack_isThreeChange(String[] stations,String changeStations,String[] strings) throws ParseException{
        boolean flag = false;

        String date1 = strings[1].substring(11, 19);//O站出发时间2015-09-16T12:09:47.000Z-->08:21:15
        String date2 = strings[6].substring(11, 19);//D站到达时间2015-09-16T12:28:05.000Z-->09:09:30
        date1 = TimeConvert.Second2Hour(TimeConvert.HourToSeconds(date1) + 8 * 60 * 60 + IN_STATION);//刷卡进站+等待时间150秒
        date2 = TimeConvert.Second2Hour(TimeConvert.HourToSeconds(date2) + 8 * 60 * 60 - OUT_STATION);//出站刷卡时间90秒

        String[] odChangeStations =changeStations.split("-");
        String string_O2 =odChangeStations[0];
        String string_D2 =odChangeStations[1];
        String changeStation1 =odChangeStations[2];
        String changeStation2 =odChangeStations[3];
        String changeStation3 =odChangeStations[4];
        String stationNext1 =null;
        String stationNext2 =null;
        for (int i =0;i<stations.length-1;i++){
            if (stations[i].equals(NoNameExUtil.NO2Ser(changeStation1))){
                stationNext1 = NoNameExUtil.Ser2NO(stations[i + 1]);
            }
            if (stations[i].equals(NoNameExUtil.NO2Ser(changeStation2))) {
                stationNext2 = NoNameExUtil.Ser2NO(stations[i + 1]);
            }
        }

        String station2stationFirst = NoNameExUtil.Ser2NO(stations[0])+ NoNameExUtil.Ser2NO(stations[1]);
        String lineNo_First = linestation2staion.get(station2stationFirst);
        String station2stationLast = NoNameExUtil.Ser2NO(stations[stations.length - 2])+ NoNameExUtil.Ser2NO(stations[stations.length - 1]);
        String lineNo_Last = linestation2staion.get(station2stationLast);
        String station2stationSecond =changeStation1+stationNext1;
        String lineNo_Second =linestation2staion.get(station2stationSecond);
        String station2stationThird =changeStation2+stationNext2;
        String lineNo_Third =linestation2staion.get(station2stationThird);

        Date timeo_change_first = null;//从第一个换乘站出发的时间
        Date timed_change_first = null;//到达第一个换乘站的时间
        Date timeo_change_second = null;
        Date timed_change_second = null;
        Date timeo_change_last =null;
        Date timed_change_last =null;

        /**
         * 第一次换乘        到达第一个换乘点的时间    timed_change_first
         */

        timed_change_first =timed_change(lineNo_First,string_O2, TimeConvert.String2Date(date1),changeStation1);


        /**
         *  第一次换乘        换乘点changeStation1的换乘时间
         */
        int CHANGE_STATION_SMALL1 =change_station_walkTime(changeStation1,lineNo_First,lineNo_Second);



        /**
         * 第一次换乘        第一个换乘点出发时间          timeo_change_first
         *
         */
        timeo_change_first = TimeConvert.String2Date(TimeConvert.Second2Hour(TimeConvert.HourToSeconds(TimeConvert.Date2String(timed_change_first)) + 8 * 60 * 60 + CHANGE_STATION_SMALL1));


        /**
         * 第一个换乘站出发的时间      timeo_change_first    找到第二个换乘站到达的时间 timed_change_second
         */

        timed_change_second =timed_change(lineNo_Second,changeStation1,timeo_change_first,changeStation2);




        /**
         * 第二个换点点changeStation2  的换乘时间      CHANGE_STATION_SMALL2
         */

        int CHANGE_STATION_SMALL2 =change_station_walkTime(changeStation2,lineNo_Second,lineNo_Third);


        timeo_change_second = TimeConvert.String2Date(TimeConvert.Second2Hour(TimeConvert.HourToSeconds(TimeConvert.Date2String(timed_change_second)) + 8 * 60 * 60 + CHANGE_STATION_SMALL2));


        /***
         * 由第二个换乘点出发时间，可计算计算第三个换乘点,记最后一个换乘点的 到达时间
         */

        timed_change_last =timed_change(lineNo_Third,changeStation2,timeo_change_second,changeStation3);



        /**
         * 计算最后一个换乘changeStation3 的换乘时间     CHANGE_STATION_SMALL_LAST
         */


        int CHANGE_STATION_SMALL_LAST =change_station_walkTime(changeStation3,lineNo_Third,lineNo_Last);



        /**
         * 最后一次换乘    从最后一个换乘点出发的时间    timeo_change_last
         */

        timeo_change_last =timeo_change(lineNo_Last,string_D2, TimeConvert.String2Date(date2), changeStation3);



        /**
         * 若两个时间差满足条件（相当于两次换乘），则返回flag =true;
         */
        if (TimeConvert.HourToSeconds(TimeConvert.Date2String(timeo_change_last)) - TimeConvert.HourToSeconds(TimeConvert.Date2String(timed_change_last)) >= CHANGE_STATION_SMALL_LAST) {
            flag = true;
        }
        return flag;
    }


    /**
     *  判断两次换乘是否成立  #2
     */
    public static boolean ODPathBack_isTwoChange(String[] stations,String changeStations, String[] strings) throws ParseException{
        boolean flag = false;

        String date1 = strings[1].substring(11, 19);//O站出发时间2015-09-16T12:09:47.000Z-->08:21:15
        String date2 = strings[6].substring(11, 19);//D站到达时间2015-09-16T12:28:05.000Z-->09:09:30
        date1 = TimeConvert.Second2Hour(TimeConvert.HourToSeconds(date1) + 8 * 60 * 60 + IN_STATION);//刷卡进站+等待时间150秒
        date2 = TimeConvert.Second2Hour(TimeConvert.HourToSeconds(date2) + 8 * 60 * 60 - OUT_STATION);//出站刷卡时间90秒

        String[] odChangeStations =changeStations.split("-");
        String string_O2 =odChangeStations[0];
        String string_D2 =odChangeStations[1];
        String changeStation1 =odChangeStations[2];
        String changeStation2 =odChangeStations[3];

        String stationNext =null;
        for (int i =0;i<stations.length-1;i++){
            if (stations[i].equals(NoNameExUtil.NO2Ser(changeStation1))){
                stationNext = NoNameExUtil.Ser2NO(stations[i + 1]);
                break;
            }
        }

        String station2stationFirst = NoNameExUtil.Ser2NO(stations[0])+ NoNameExUtil.Ser2NO(stations[1]);
        String lineNo_First = linestation2staion.get(station2stationFirst);
        String station2stationLast = NoNameExUtil.Ser2NO(stations[stations.length - 2])+ NoNameExUtil.Ser2NO(stations[stations.length - 1]);
        String lineNo_Last = linestation2staion.get(station2stationLast);
        String station2stationSecond =changeStation1+stationNext;
        String lineNo_Second =linestation2staion.get(station2stationSecond);

        Date timeo_change_first = null;
        Date timed_change_first = null;
        Date timeo_change_last =null;
        Date timed_change_last =null;

        /**
         * 第一次换乘        到达第一个换乘点的时间    timed_change_first
         */
        timed_change_first =timed_change(lineNo_First,string_O2, TimeConvert.String2Date(date1),changeStation1);


        /**
         *  第一次换乘        换乘点changeStation1的换乘时间
         */

        int CHANGE_STATION_SMALL1 =change_station_walkTime(changeStation1,lineNo_First,lineNo_Second);


        /**
         * 第一次换乘        第一个换乘点出发时间          timeo_change_first
         *
         */
        timeo_change_first = TimeConvert.String2Date(TimeConvert.Second2Hour(TimeConvert.HourToSeconds(TimeConvert.Date2String(timed_change_first)) + 8 * 60 * 60 + CHANGE_STATION_SMALL1));


        /**
         * 第一个换乘站出发的时间      timeo_change_first    找到第二个换乘站到达的时间 timed_change_second
         */

        timed_change_last =timed_change(lineNo_Second,changeStation1,timeo_change_first,changeStation2);


        /**
         * 计算最后一个换乘changeStation3 的换乘时间     CHANGE_STATION_SMALL_LAST
         */


        int CHANGE_STATION_SMALL_LAST =change_station_walkTime(changeStation2,lineNo_Second,lineNo_Last);



        /**
         * 最后一次换乘    从最后一个换乘点出发的时间    timeo_change_last
         */
        timeo_change_last =timeo_change(lineNo_Last, string_D2, TimeConvert.String2Date(date2), changeStation2);



        if (TimeConvert.HourToSeconds(TimeConvert.Date2String(timeo_change_last)) - TimeConvert.HourToSeconds(TimeConvert.Date2String(timed_change_last)) >= CHANGE_STATION_SMALL_LAST) {
            flag = true;
        }
        return flag;
    }


    /**
     *  判断一次换乘是否成立  #1
     */
    //判断两个不同线的两个站点是否为直接换乘
    public static boolean ODPathBack_isOneChange(String[] stations,String changeStations, String[] strings) throws ParseException{
        boolean flag = false;

        String date1 = strings[1].substring(11, 19);//O站出发时间2015-09-16T12:09:47.000Z-->08:21:15
        String date2 = strings[6].substring(11, 19);//D站到达时间2015-09-16T12:28:05.000Z-->09:09:30
        date1 = TimeConvert.Second2Hour(TimeConvert.HourToSeconds(date1) + 8 * 60 * 60 + IN_STATION);//刷卡进站+等待时间150秒
        date2 = TimeConvert.Second2Hour(TimeConvert.HourToSeconds(date2) + 8 * 60 * 60 - OUT_STATION);//出站刷卡时间90秒

        String[] odChangeStations =changeStations.split("-");
        String string_O2 =odChangeStations[0];
        String string_D2 =odChangeStations[1];
        String changeStation =odChangeStations[2];

        String station2stationFirst = NoNameExUtil.Ser2NO(stations[0])+ NoNameExUtil.Ser2NO(stations[1]);
        String lineNo_First = linestation2staion.get(station2stationFirst);
        String station2stationLast = NoNameExUtil.Ser2NO(stations[stations.length - 2])+ NoNameExUtil.Ser2NO(stations[stations.length - 1]);
        String lineNo_Last = linestation2staion.get(station2stationLast);


        Date timeo_change = null;
        Date timed_change = null;


        /**
         *
         */
        timed_change =timed_change(lineNo_First,string_O2, TimeConvert.String2Date(date1),changeStation);

        timeo_change =timeo_change(lineNo_Last,string_D2, TimeConvert.String2Date(date2), changeStation);


        int CHANGE_STATION_SMALL =change_station_walkTime(changeStation,lineNo_First,lineNo_Last);



        if (TimeConvert.HourToSeconds(TimeConvert.Date2String(timeo_change)) - TimeConvert.HourToSeconds(TimeConvert.Date2String(timed_change)) >= CHANGE_STATION_SMALL) {
            flag = true;
        }
        return flag;
    }


    /**
     *  判断是否满足同一条线不换乘的情况    #0
     */
    public static boolean ODPathBack_isOneNo(String[] strings, String lineNo) throws ParseException {

        boolean flag = false;//326816865,2015-09-16T08:21:15.000Z,地铁二号线,新秀,114.1444922,22.55086697,2015-09-16T09:09:30.000Z,地铁二号线,后海,113.9352052,22.52049297
        String date1 = strings[1].substring(11, 19);//O站出发时间2015-09-16T12:09:47.000Z-->08:21:15
        String date2 = strings[6].substring(11, 19);//D站到达时间2015-09-16T12:28:05.000Z-->09:09:30

        date1 = TimeConvert.Second2Hour(TimeConvert.HourToSeconds(date1) + 8 * 60 * 60 + IN_STATION);//刷卡进站+等待时间150秒
        date2 = TimeConvert.Second2Hour(TimeConvert.HourToSeconds(date2) + 8 * 60 * 60 - OUT_STATION);//出站刷卡时间90秒
        String string_O = strings[3];//O站点
        String string_D = strings[8];//D站点
        int lineNo_O = 0;
        int lineNo_D = 0;


        for (int i = 0; i < lbs.get(lineNo).size()-1; i++) {
            for (int j = 0; j < lbs.get(lineNo).get(i).getStations().size(); j++) {
                if (lbs.get(lineNo).get(i).getStations().get(j).getStation().equals((string_O)) &&
                        lbs.get(lineNo).get(i).getStations().get(j).getDate().before(TimeConvert.String2Date(date1)) &&
                        lbs.get(lineNo).get(i + 1).getStations().get(j).getDate().after(TimeConvert.String2Date(date1))) {
                    lineNo_O = i + 1;//O第18列车，第0站
                }
                if (lbs.get(lineNo).get(i).getStations().get(j).getStation().equals((string_D)) &&
                        lbs.get(lineNo).get(i).getStations().get(j).getDate().before(TimeConvert.String2Date(date2)) &&
                        lbs.get(lineNo).get(i + 1).getStations().get(j).getDate().after(TimeConvert.String2Date(date2))) {
                    lineNo_D = i ;//D第18列车，第20站
                }
            }
        }
        if (lineNo_O == lineNo_D) {
            flag = true;
        }
        return flag;
    }


    /**
     *    不同换乘站点  每次换乘用的时间
     * @param changeStation     换乘站
     * @param lineNo_0          换乘前的线路2600
     * @param lineNo_D          换乘后的线路2611
     * @return                  此次换乘用的时间
     */
    public static int change_station_walkTime(String changeStation,String lineNo_0,String lineNo_D){

        int CHANGE_STATION_SMALL =0;

        String key =changeStation+","+lineNo_0.substring(0,3)+","+lineNo_D.substring(0,3);
        String value =mapWalk.get(key);

        if (lineNo_0.substring(3).equals("0") && lineNo_D.substring(3).equals("0")){
            CHANGE_STATION_SMALL =(int)((Double.parseDouble(value.split(",")[0]))*60);
        }else if (lineNo_0.substring(3).equals("0") && lineNo_D.substring(3).equals("1")){
            CHANGE_STATION_SMALL =(int)((Double.parseDouble(value.split(",")[1]))*60);
        }else if (lineNo_0.substring(3).equals("1") && lineNo_D.substring(3).equals("0")){
            CHANGE_STATION_SMALL =(int)((Double.parseDouble(value.split(",")[2]))*60);
        }else if (lineNo_0.substring(3).equals("1") && lineNo_D.substring(3).equals("1")){
            CHANGE_STATION_SMALL =(int)((Double.parseDouble(value.split(",")[3]))*60);
        }
        return CHANGE_STATION_SMALL;
    }


    /**
     *  从此站点o出发到达下一站点的时间
     *  string_O2 changeStation_first  都是编号
     */
    public static Date timed_change(String lineNo_first,String string_O2,Date date1,String changeStation_first) throws ParseException {

        int lineNo_first_no =0;
        Date timed_change_first =null;
        boolean flag = false;
        int num =0;
        for (int i = 0; i < lbs.get(lineNo_first).size(); i++) {
            for (int j = 0; j < lbs.get(lineNo_first).get(i).getStations().size(); j++) {
                if (i !=lbs.get(lineNo_first).size() -1 &&lbs.get(lineNo_first).get(i).getStations().get(j).getStation().equals(string_O2)
                        && lbs.get(lineNo_first).get(i).getStations().get(j).getDate().before(date1)
                        && lbs.get(lineNo_first).get(i + 1).getStations().get(j).getDate().after(date1)) {
                    lineNo_first_no = i + 1;
                    flag =true;
                    num =1;
                }else if(i ==lbs.get(lineNo_first).size() -1 && num ==0 ) {
                    num =1;
                    flag =true;
                    lineNo_first_no =i;
                }
                if (flag ==true &&lbs.get(lineNo_first).get(lineNo_first_no).getStations().get(j).getStation().equals(changeStation_first)) {
                    timed_change_first = lbs.get(lineNo_first).get(lineNo_first_no).getStations().get(j).getDate();
                    flag =false;
                    break;
                }
            }
        }
        return timed_change_first;
    }


    /**
     * 由D站点推出前一个换乘站点的出发时间
     * string_D2 changeStation_first  都是编号
     */
    public static Date timeo_change(String lineNo_first,String string_O2,Date date1,String changeStation_first) throws ParseException {

        int lineNo_first_no =0;
        Date timed_change_first =null;
        boolean flag = false;
        int num =0;
        for (int i = 0; i < lbs.get(lineNo_first).size(); i++) {
            for (int j = 0; j < lbs.get(lineNo_first).get(i).getStations().size(); j++) {
                if (i != lbs.get(lineNo_first).size() - 1 && lbs.get(lineNo_first).get(i).getStations().get(j).getStation().equals(string_O2)
                        && lbs.get(lineNo_first).get(i).getStations().get(j).getDate().before(date1)
                        && lbs.get(lineNo_first).get(i + 1).getStations().get(j).getDate().after(date1)) {
                    lineNo_first_no = i;
                    flag = true;
                    num = 1;
                    break;
                } else if (i == lbs.get(lineNo_first).size() - 1 && num == 0) {
                    num = 1;
                    flag = true;
                    lineNo_first_no = i - 1;
                    break;
                }
            }
            if (flag ==true){
                break;
            }
        }
        if (flag ==true){
            for (int k = 0; k < lbs.get(lineNo_first).get(lineNo_first_no).getStations().size(); k++){
                if (lbs.get(lineNo_first).get(lineNo_first_no).getStations().get(k).getStation().equals(changeStation_first)) {
                    timed_change_first = lbs.get(lineNo_first).get(lineNo_first_no).getStations().get(k).getDate();
                    flag =false;
                    break;
                }
            }
        }
        return timed_change_first;
    }


    /**
     *  转化为最后的结果，每个站点以字符串表示
     */
    public static String ConvertOD(String string){

//        System.out.println("string:"+string);
        String[] strings = string.split("#")[0].split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            sb.append(NoNameExUtil.Ser2NO(strings[i]));
            sb.append("-");
        }
        return sb.toString().substring(0, sb.toString().length() - 1);
    }


    /**
     * 找出所有OD路径中的时间最接近实际时间的一条路径
     * @param value     所有路径的List
     */
    public static String CloseTimeLine(List<String> value,double time,double realTime){

        double closeTime =time;
        String closeLine =value.get(0).toString();

        for(int m =0;m<value.size();m++){//56 57 #0 T 0.00000000  1.76666667
            String[] times =value.get(m).split("#")[1].split(" ");
            if (Double.parseDouble(times[times.length-1]) <=realTime && Double.parseDouble(times[times.length-1]) >=closeTime){
                closeTime =Double.parseDouble(times[times.length-1]);
                closeLine =value.get(m);
                continue;
            }
        }
        return closeLine;
    }


}
