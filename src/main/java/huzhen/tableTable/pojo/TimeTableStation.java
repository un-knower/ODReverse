package huzhen.tableTable.pojo;

import java.util.Date;

/**
 * Created by hu on 2016/11/3.
 *
 *  每个站点的到站时间
 */
public class TimeTableStation {

    private Date date;

    private String station;

    public void setDate(Date date) {
        this.date = date;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public Date getDate() {
        return date;
    }

    public String getStation() {
        return station;
    }

    public TimeTableStation(String str,Date da){
        this.station = str;
        this.date = da;
    }
}
