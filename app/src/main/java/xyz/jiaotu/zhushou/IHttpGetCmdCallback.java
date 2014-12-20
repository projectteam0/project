package xyz.jiaotu.zhushou;
import java.util.ArrayList;
/**
 * Created by lvpeng-g on 2014/12/20.
 */
public interface IHttpGetCmdCallback {
    public void onHttpGetCmdSucceeded(String cmd,String data);
    public void onHttpGetCmdFailed(String cmd);
    public void onHttpGetGameInfoList(ArrayList<GameInfo> listItems);
    public void onHttpGetUpdateInfo(String version,String uuid,String url);
}
