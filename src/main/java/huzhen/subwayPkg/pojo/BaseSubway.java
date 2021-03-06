package huzhen.subwayPkg.pojo;

import huzhen.tableTable.pojo.TimeTableStation;

import java.util.List;

/**
 * Created by hu on 2016/11/3.
 */
public class BaseSubway {

    //当前车上的总人数
    private Double passengerNum;

    //当前车的班次
    private String subwayNO;

    //该地铁所有运行的站点
    private List<TimeTableStation> stations;

    /**
     * 初始化乘客人数为0
     */
    public BaseSubway(){
        passengerNum = Double.valueOf(0);
    }

    public void setPassengerNum(Double passengerNum) {
        this.passengerNum = passengerNum;
    }


    public Double getPassengerNum() {
        return passengerNum;
    }

    public void setStations(List<TimeTableStation> stations) {
        this.stations = stations;
    }

    public List<TimeTableStation> getStations() {
        return stations;
    }

    public void setSubwayNO(String subwayNO) {
        this.subwayNO = subwayNO;
    }

    public String getSubwayNO() {
        return subwayNO;
    }
}
