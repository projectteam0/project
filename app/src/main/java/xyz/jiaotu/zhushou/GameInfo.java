package xyz.jiaotu.zhushou;

/**
 * Created by lvpeng-g on 2014/12/20.
 */
public class GameInfo {
    public final int mGameId;
    public final String mGameName;
    public final String mUrl;
    public final String mIconUrl;
    public final String mType;
    public final int mCount;
    public GameInfo(int gameId,String gameName,String Url,String iconUrl,String type,int count){
        mGameId = gameId;
        mGameName = gameName;
        mUrl = Url;
        mIconUrl = iconUrl;
        mType = type;
        mCount = count;
    }
}
