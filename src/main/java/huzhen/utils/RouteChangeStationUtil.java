package huzhen.utils;

import huzhen.code.readForm.MapAllpath;
import huzhen.tableTable.TimeTableReader;

import java.util.List;
import java.util.Map;


/**
 * Created by hu on 2016/11/3.
 *
 *  路径 和 换乘站  相互转换
 *  route:  45-46-47-48-49-50-60-61-62-63-64-28
 *  changeStations:1260026000-1268003000-1261006000
 */
public class RouteChangeStationUtil {



    /**
     *
     *       Map<String,String> linestation2staion =TimeTableReader.getLinestation2staion();
     *
     *  45-46-47-48-49-50-60-61-62-63-64-28
     *  1260026000-1268003000-1261006000
     *
     *
     */
    public static String Route2ChangeStation(String route,Map<String,String> linestation2staion){


        String[] serStations =route.split("-");
        String changeStations =NoNameExUtil.Ser2NO(serStations[0])+"-"+NoNameExUtil.Ser2NO(serStations[serStations.length -1]);

        for(int i =1;i<serStations.length -1;i++){
            String theStation =NoNameExUtil.Ser2NO(serStations[i]);
            String preStation =NoNameExUtil.Ser2NO(serStations[i-1]);
            String nextStation =NoNameExUtil.Ser2NO(serStations[i+1]);

            String preLine =linestation2staion.get(preStation+theStation);
            String nextLine =linestation2staion.get(theStation+nextStation);

            if (!preLine.equals(nextLine)){
                changeStations =changeStations+"-"+theStation;
            }
        }
        return changeStations;
    }

    /**
     * 有误差
     * 所有路径里面可以能不包括此换乘路径，因为实际乘车多选择换乘少的路径，不看时间
     *  1260026000-1268003000-1261006000
     *  45-46-47-48-49-50-60-61-62-63-64-28
     */
    public static String ChangeStation2Route(String changeStations,Map<String,List<String>> mapAllpath,Map<String,String> linestation2staion){

        String route =null;

        String[] stations =changeStations.split("-");
        String ODKey =NoNameExUtil.NO2Ser(stations[0])+"-"+NoNameExUtil.NO2Ser(stations[1]);

        if (mapAllpath.containsKey(ODKey)){

            List<String> routesList =mapAllpath.get(ODKey);

            for(int i =0;i<routesList.size();i++){

                String serRoute =routesList.get(i).split("#")[0].replaceAll(" ","-");
                serRoute =serRoute.substring(0,serRoute.length() -1);

                String routeChangeStations =Route2ChangeStation(serRoute,linestation2staion);
                if (routeChangeStations.equals(changeStations)){
//                    route =serRoute.replaceAll(",","-");
                    route = serRoute;
                    break;
                }
            }
            /**
             * allpathNew.txt中可能没有此换乘路径
             */
            if(route ==null){

            }
        }
        return route;
    }


    public static void main(String[] args) {

        System.out.println(TimeTableReader.getLinestation2staion().get("12620190001267022000"));

        Map<String,String> linestation2staion =TimeTableReader.getLinestation2staion();
        //String changeStations =Route2ChangeStation("45-46-47-48-49-50-60-61-62-63-64-28",linestation2staion);
        String changeStations =Route2ChangeStation("45 46 47 48 49 50 60 61 62 63 64 28".replace(" ","-"),linestation2staion);
        System.out.println("changeStations:"+changeStations);
        System.out.println("------------------");


        Map<String,List<String>> mapAllpath = MapAllpath.getAllpath();
        String route =ChangeStation2Route("1268034000-1260034000-1268016000",mapAllpath,linestation2staion);
        System.out.println("route:"+route);
        System.out.println("--------------------");

//        String route2 =ChangeStation2Route("1260026000-1268003000","1260026000-1268003000-1261006000",mapAllpath);
//        System.out.println("route2:"+route2);


    }
}
