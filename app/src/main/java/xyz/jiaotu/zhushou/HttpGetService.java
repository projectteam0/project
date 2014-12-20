package xyz.jiaotu.zhushou;

/**
 * Created by lvpeng-g on 2014/12/20.
 */
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class HttpGetService {
    private static final String TAG = "HttpGetService";
    private static final String HTTP_CMD_GET_URL = "http://www.jiaotu.xyz/a/";
    private IHttpGetCmdCallback mHttpGetCmdCallback = null;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case ConstantUtil.WHAT_SUCCESS:{
                    if(mHttpGetCmdCallback != null){
                        Bundle bundle = msg.getData();
                        if(bundle.getString(ConstantUtil.KEY_CMD).equals(ConstantUtil.HTTP_CMD_GETGAMELIST)){
                            ArrayList list = bundle.getParcelableArrayList(ConstantUtil.KEY_LIST);
                            ArrayList<GameInfo> listItems = (ArrayList<GameInfo>)list.get(0);
                            mHttpGetCmdCallback.onHttpGetGameInfoList(listItems);
                        }
                        else if(bundle.getString(ConstantUtil.KEY_CMD).equals(ConstantUtil.HTTP_CMD_UPDATE)){
                            mHttpGetCmdCallback.onHttpGetUpdateInfo(bundle.getString("version"),bundle.getString("uuid"),bundle.getString("url"));
                        }
                    }
                }
                    break;
                case ConstantUtil.WHAT_FAILED:{
                    if(mHttpGetCmdCallback != null){
                        Bundle bundle = msg.getData();
                        mHttpGetCmdCallback.onHttpGetCmdFailed(bundle.getString(ConstantUtil.KEY_CMD));
                    }
                }
                    break;
                default:
                    break;
            }
        }
    };

    public HttpGetService(IHttpGetCmdCallback callback) {
        mHttpGetCmdCallback = callback;
    }

    public void HttpGetData(final String cmd,final String uuid,HttpGetParam context){
        new HttpAsyncGetTask(cmd,uuid,context).execute();
    }

    public class HttpAsyncGetTask extends AsyncTask<String, Integer, Boolean> {
        private String mGetResualtData;
        private String mCmd;
        private final String mUuid;
        private final HttpGetParam mContext;
        public HttpAsyncGetTask(final String cmd,final String uuid,HttpGetParam context){
            mCmd = cmd;
            mUuid = uuid;
            mContext =context;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try{
                String strUrl = HTTP_CMD_GET_URL;
                if(ConstantUtil.HTTP_CMD_GETGAMELIST == mCmd){
                    strUrl += "getlist.php?uuid=";
                    strUrl += mUuid + "&start=" + mContext.mStartIndex;
                }
                else if(ConstantUtil.HTTP_CMD_UPDATE == mCmd){
                    strUrl += "checkversion.php?version=" + mContext.mVersion;
                    strUrl += "&uuid=" + mUuid;
                }

                HttpGet httpGet = new HttpGet(strUrl);
                DefaultHttpClient client = new DefaultHttpClient();
                // 请求超时
                client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, ConstantUtil.HTTP_TIMEOUT);
                // 读取超时
                client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, ConstantUtil.HTTP_TIMEOUT);
                HttpResponse httpResponse = client.execute(httpGet);
                if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                    String strResult = "";
                    HttpEntity entity = httpResponse.getEntity();
                    if(entity != null)
                    {
                        strResult = EntityUtils.toString(entity,"utf-8");
                    }
                    JSONObject resultinfo = new JSONObject(strResult);
                    if(resultinfo.getInt(ConstantUtil.KEY_ERRORNO) == 0){
                        if(ConstantUtil.HTTP_CMD_GETGAMELIST == mCmd) {
                            mGetResualtData = resultinfo.getString(ConstantUtil.KEY_DATA);
                        }
                        else{
                            mGetResualtData = strResult;
                        }
                        Log.i(TAG,mGetResualtData);
                        return true;
                    }
                }
            }catch(Exception e){
                return false;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                Message message = mHandler.obtainMessage();
                message.what = ConstantUtil.WHAT_SUCCESS;
                Bundle bundle = ProtocolPacket.ParseGetResultBundle(mCmd,mGetResualtData);
                message.setData(bundle);
                mHandler.sendMessage(message);
            }
            else{
                Message message = mHandler.obtainMessage();
                message.what = ConstantUtil.WHAT_FAILED;
                Bundle bundle = new Bundle();
                bundle.putString(ConstantUtil.KEY_CMD,mCmd);
                message.setData(bundle);
                mHandler.sendMessage(message);
            }
        }
    }
}
