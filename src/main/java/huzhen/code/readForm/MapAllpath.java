package huzhen.code.readForm;

import huzhen.code.pojo.AllPathRoute;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by hu on 2016/11/3.
 *
 * 读取allpathNew2.txt 表格     此表是UtilsProject中做的 1 将allpath.txt 用记事本打开复制粘贴 可得到 alpathNew.txt 然后由代码排序换乘次数
 *  过去所有OD间的可能路径
 *  包括读取到最后一条路径Line  后面可以借鉴 *************
 *
 *
 *  2017-01-01
 *  allpathNew3.txt 是自己生成的路径  SubwayAllPath项目生成的结果
 */
public class MapAllpath {
    /**
     * 新线开通后  新线开通后的路径 1，2，3，4，5，7，9，11
     */
    private static List<String> list =new ArrayList<String>();
    private static Map<String,List<String>> mapAllpath =new HashMap<String,List<String>>();
    private static AllPathRoute allPath;
    public static Map<String,List<String>> getAllpath(){
        return mapAllpath;
    }

    public void ReadAllPath() throws IOException{


        String path ="allpathNew8.txt";//新线开通后1、2、3、4、5、7、9、11 不包括 福邻
//        String path ="allpathNew8p.txt";//新线开通后1、2、3、4、5、7、9、11 包括 福邻

        InputStream is =this.getClass().getClassLoader().getResourceAsStream(path);
        Scanner scan=new Scanner(is,"UTF-8");

        String line =scan.nextLine();

        String[] strings =line.split("#");
        String[] strings1 =strings[0].split(" ");
        String o2d =strings1[0]+"-"+strings1[strings1.length-1];

        allPath =new AllPathRoute(line,o2d);
        list.add(line);

        while (scan.hasNext()){
            line =scan.nextLine();
            strings =line.split("#");
            strings1 =strings[0].split(" ");
            o2d =strings1[0]+"-"+strings1[strings1.length-1];

            if (list==null){
                list =new ArrayList<String>();
                list.add(allPath.getString());
            }
            if (allPath.getO2d().equals(o2d)){
                list.add(line);
            }else {
                mapAllpath.put(allPath.getO2d(),list);
                list =null;
                allPath.setString(line);
                allPath.setO2d(o2d);
            }
        }

        //最后一条数据
        if (list !=null){
            mapAllpath.put(allPath.getO2d(),list);
        }else {
            list =new ArrayList<>();
            list.add(allPath.getString());
            mapAllpath.put(allPath.getO2d(),list);
        }
    }





    /**
     * 读取文件
     */
    public static MapAllpath mmm;
    static {
        mmm =new MapAllpath();
        try {
            mmm.ReadAllPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {

        Map<String,List<String>> mapAllpath =getAllpath();

        System.out.println(mapAllpath.get("167-64"));
    }

}
