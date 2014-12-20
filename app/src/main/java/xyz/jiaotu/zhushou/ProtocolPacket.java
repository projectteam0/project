package xyz.jiaotu.zhushou;

/**
 * Created by lvpeng-g on 2014/12/20.
 */
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Bundle;

public class ProtocolPacket {
    public static Bundle ParseGetResultBundle(String cmd,String strJson){
        Bundle bundle = new Bundle();
        bundle.putString(ConstantUtil.KEY_CMD,cmd);
        if(cmd.equals(ConstantUtil.HTTP_CMD_GETGAMELIST)){
            ArrayList<GameInfo> gameinfolist = GetGameInfoListFromData(strJson);
            //须定义一个list用于在budnle中传递需要传递的ArrayList<Object>,这个是必须要的
            ArrayList bundlelist = new ArrayList();
            bundlelist.add(gameinfolist);
            bundle.putParcelableArrayList(ConstantUtil.KEY_LIST, bundlelist);
        }
        else if(cmd.equals(ConstantUtil.HTTP_CMD_UPDATE))
        {
            UpdateInfo updateInfo = GetUpdateInfoFromData(strJson);
            bundle.putString("version",updateInfo.mVersion);
            bundle.putString("uuid",updateInfo.mUuid);
            bundle.putString("url",updateInfo.mUrl);
        }
        return bundle;
    }

//    获取列表接口：
//    http://www.jiaotu.xyz/a/getlist.php?uuid=de7cff1c51b3f1d64ccdc609d65c6346&start=0
//    uuid	用户唯一标示
//    start是game_id，从零开始
//    返回值
//   {
//        "errno"  : "0"/"1"/"-1",
//        [{game_id:0,
//            "icon":"http://www.jiaotu.xyz/icon/0.jpg",
//           "url":"http://www.jiaotu.xyz/a/play.php?game_id=0
//        "title": "12345",
//                "type":"益智类", "count": 124}, {},,,,....]
//    }
//    每次最多返回10条
//    errmsg :正常/uuid错误/其他错误
//    win: 最大值是100， 服务器内部有错误的时候是-1.
    public static ArrayList<GameInfo> GetGameInfoListFromData(String jsonData){
        ArrayList<GameInfo> listItems = new ArrayList<GameInfo>();
        try {
            if(jsonData.length() > 0 && !jsonData.equals(ConstantUtil.EMPTY_GAMEINFO_LIST)){
                JSONArray arr = new JSONArray(jsonData);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject gameinfo = (JSONObject) arr.get(i);
                    int gameid = gameinfo.getInt("game_id");
                    String gamename = gameinfo.getString("title");
                    String gameicon =  gameinfo.getString("icon");
                    String gameurl =  gameinfo.getString("url");
                    String gametype =  gameinfo.getString("type");
                    int count =  gameinfo.getInt("count");
                    GameInfo iteminfo = new GameInfo(gameid,gamename,gameurl,gameicon,gametype,count);
                    listItems.add(iteminfo);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listItems;
    }

    public static UpdateInfo GetUpdateInfoFromData(String jsonData){
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            String version = jsonObject.getString("version");
            String uuid = jsonObject.getString("uuid");
            String url = jsonObject.getString("url");
            UpdateInfo updateinfo = new UpdateInfo(version, uuid, url);
            return updateinfo;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return new UpdateInfo("1.0.0", "", "");
    }
}
