package xyz.jiaotu.zhushou;

/**
 * Created by lvpeng-g on 2014/12/20.
 */
public class ConstantUtil {
    ///< 字符串
    public static final String STR_PREFS_NAME = "prefsfile";
    public static final String STR_UTF8 = "utf8";
    public static final String EMPTY_GAMEINFO_LIST = "null\n";
    public static final String POST_RESULT_SUCCESS = "0";
    public static final String POST_RESULT_FAILED = "1"; //uuid错误

    ///<命令
    public static final String HTTP_CMD_UNKNOWN = "unknown";
    public static final String HTTP_CMD_GETGAMELIST = "list"; ///<游戏信息列表
    public static final String HTTP_CMD_UPDATE = "update"; ///<升级
    public static final String HTTP_CMD_FEEDBACK = "feedback"; ///<反馈

    ///<消息参数
    public static final int WHAT_SUCCESS = 0;
    public static final int WHAT_FAILED = -1;

    ///< 常量
    public static final int HTTP_TIMEOUT = 20000; /// 20秒

    ///<关键字
    public static final String KEY_CMD = "cmd";
    public static final String KEY_LIST = "list";
    public static final String KEY_ERRORNO = "errno";
    public static final String KEY_DATA = "data";
}
