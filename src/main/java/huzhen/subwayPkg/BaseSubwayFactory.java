package huzhen.subwayPkg;


import huzhen.subwayPkg.pojo.BaseSubway;
import huzhen.tableTable.TimeTableReader;
import huzhen.tableTable.pojo.TimeTableStation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by hu on 2016/11/3.
 * 用于发车
 * 每辆车是否需要一个唯一标示？
 */
public class BaseSubwayFactory {

    /**
     * 时间自增
     *
     * @param date
     * @param timeSpan 以秒为单位
     * @return
     */
    public Date addTime(Date date, Integer timeSpan) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, timeSpan);
        return calendar.getTime();
    }

    /**
     * 用于生成一辆新的车辆信息，需要线路编号，上下行。
     * 测试
     *
     * @param subwayNO     车次编号
     * @param lineNo       线路编号
     * @param eachTimeSpan 较时刻表的偏移量
     * @return
     */
    public BaseSubway getSubway(Integer subwayNO, String lineNo, Integer eachTimeSpan) {
        BaseSubway subway;
        subway = new BaseSubway();

        String lineSubwayNO = lineNo + subwayNO;
        subway.setSubwayNO(lineSubwayNO);

        //路径list
        List<TimeTableStation> ls = new ArrayList<TimeTableStation>();

        //用于深拷贝的临时站点变量，不然会出现浅拷贝，所有车次的时间都一样
        TimeTableStation temp;
        for (TimeTableStation tts : TimeTableReader.getLineTimeTable(lineNo)) {
            temp = new TimeTableStation(tts.getStation(), addTime(tts.getDate(), eachTimeSpan));
            ls.add(temp);
        }
        subway.setStations(ls);
        return subway;
    }

    public static void main(String[] args) {

        BaseSubway bs = new BaseSubwayFactory().getSubway(11, "2610", 1000);
        bs.getPassengerNum();
    }
}