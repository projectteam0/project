package xyz.jiaotu.zhushou;

/**
 * Created by lvpeng-g on 2014/12/20.
 */
public interface IHttpPostCmdCallback {
    public void onPostSucceeded(String cmd,String packet);
    public void onPostFailed(String cmd,String error,String packet);
}