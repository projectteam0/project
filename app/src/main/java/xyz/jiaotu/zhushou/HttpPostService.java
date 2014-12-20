package xyz.jiaotu.zhushou;

/**
 * Created by lvpeng-g on 2014/12/20.
 */
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class HttpPostService {
    private static final String TAG = "HttpPostService";
    private static final String HTTP_CMD_POST_URL = "http://www.jiaotu.xyz/a/";
    private IHttpPostCmdCallback mHttpPostCmdCallback = null;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case ConstantUtil.WHAT_SUCCESS:{
                    if(mHttpPostCmdCallback != null){
                        Bundle bundle = msg.getData();
                        mHttpPostCmdCallback.onPostSucceeded(bundle.getString(ConstantUtil.KEY_CMD),bundle.getString(ConstantUtil.KEY_DATA));
                    }
                }
                break;
                case ConstantUtil.WHAT_FAILED:{
                    if(mHttpPostCmdCallback != null){
                        Bundle bundle = msg.getData();
                        mHttpPostCmdCallback.onPostFailed(bundle.getString(ConstantUtil.KEY_CMD),bundle.getString(ConstantUtil.KEY_ERRORNO),bundle.getString(ConstantUtil.KEY_DATA));
                    }
                }
                break;
                default:
                    break;
            }
        }
    };

    public void HttpPostData(final String cmd,final String uuid,final String data){
        new HttpAsyncPostTask(cmd,uuid,data).execute();
    }

    public HttpPostService(IHttpPostCmdCallback callback) {
        mHttpPostCmdCallback = callback;
        Log.i(TAG, "HttpPostService constructor");
    }

    public class HttpAsyncPostTask extends AsyncTask<String, Integer, Boolean> {
        private String mCmd;
        private final String mUuid;
        private String mData;
        private String mResultCode = ConstantUtil.POST_RESULT_SUCCESS;
        public HttpAsyncPostTask(final String cmdid,final String uuid,final String data){
            mCmd = cmdid;
            mUuid = uuid;
            mData = data;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try{
                // 新建HttpPost对象
                String strUrl = HTTP_CMD_POST_URL;
                if(mCmd.equals(ConstantUtil.HTTP_CMD_FEEDBACK))
                {
                    strUrl += "feedback.php";
                }
                HttpPost httpPost = new HttpPost(strUrl);
                List<NameValuePair> paramLists = new ArrayList<NameValuePair>();
                paramLists.add(new BasicNameValuePair("uuid",mUuid));
                paramLists.add(new BasicNameValuePair("content",URLEncoder.encode(mData,ConstantUtil.STR_UTF8)));
                // 设置字符集
                HttpEntity entity = new UrlEncodedFormEntity(paramLists, HTTP.UTF_8);
                // 设置参数实体
                httpPost.setEntity(entity);
                // 获取HttpClient对象
                DefaultHttpClient httpClient = new DefaultHttpClient();
                //连接超时
                httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, ConstantUtil.HTTP_TIMEOUT);
                //请求超时
                httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, ConstantUtil.HTTP_TIMEOUT);
                HttpResponse httpResponse = httpClient.execute(httpPost);
                if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                    String strResult = EntityUtils.toString(httpResponse.getEntity());
                    Log.i(TAG,"Post data back: " + strResult);
                    JSONObject resultinfo = new JSONObject(strResult);
                    mResultCode = resultinfo.getString(ConstantUtil.KEY_ERRORNO);
                    if( mResultCode.equals(ConstantUtil.POST_RESULT_SUCCESS)){
                        return true;
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
                return false;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                Log.i(TAG, "HttpPostService post to service successfully");
                Message message = mHandler.obtainMessage();
                message.what = ConstantUtil.WHAT_SUCCESS;
                Bundle bundle = new Bundle();
                bundle.putString(ConstantUtil.KEY_CMD,mCmd);
                bundle.putString(ConstantUtil.KEY_DATA, mData);
                message.setData(bundle);
                mHandler.sendMessage(message);
            }
            else {
                Log.i(TAG, "HttpPostService post to service failed");
                Message message = mHandler.obtainMessage();
                message.what = ConstantUtil.WHAT_FAILED;
                Bundle bundle = new Bundle();
                bundle.putString(ConstantUtil.KEY_CMD,mCmd);
                bundle.putString(ConstantUtil.KEY_ERRORNO,mResultCode);
                bundle.putString(ConstantUtil.KEY_DATA, mData);
                message.setData(bundle);
                mHandler.sendMessage(message);
            }
        }
    }
}
