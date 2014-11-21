#include "UpdateMgr.h"

UpdateMgr::UpdateMgr()
{
	
}

UpdateMgr::~UpdateMgr()
{
	
}

/*
http://www.nuanai.me/a/checkversion.php?version=1.0.0&uuid=de7cff1c51b3f1d64ccdc609d65c6346
version    app当前版本	 
uuid	用户唯一标示

  
返回值
{
 "errno"  : "0"/"1"/"-1"/"-2"
  "version":"1.1.1",                                        //最新版本号
  "uuid":"ddaabbcc",                                        //跟本地的比较一下，不一致就用这个
        "url": "http://www.nuanai.me/a/love.apk,  //下载地址                          
}
 errmsg :已是最新版本/需要升级/uuid错误/其他错误
*/
bool UpdateMgr::RequestUpdateInfo(const std::string& strVersion,const std::string& strUUID)
{
	std::string strUrl = "http://www.nuanai.me/a/checkversion.php?version=";
	strUrl += strVersion;
	strUrl += "&uuid=";
	strUrl + strUUID;

	cocos2d::network::HttpRequest* request = new  cocos2d::network::HttpRequest();
    request->setUrl(strUrl.c_str());
    request->setRequestType(cocos2d::network::HttpRequest::Type::GET);
    request->setResponseCallback(this, httpresponse_selector(UpdateMgr::onUpdateRequestCompleted));
    request->setTag("GET nuanai update info");
    cocos2d::network::HttpClient::getInstance()->send(request);
    request->release();
	return true;
}

void UpdateMgr::onUpdateRequestCompleted(cocos2d::network::HttpClient *sender, cocos2d::network::HttpResponse *response)
{
	if (!response) {
        return;
         
    }
     
    if (0 != strlen(response->getHttpRequest()->getTag())) {

    }
     
    long statusCode = response->getResponseCode();
    char statusString[64] = {};
     
    sprintf(statusString, "HTTP Status Code: %ld, tag = %s",statusCode,response->getHttpRequest()->getTag());
    printf("%s\n",statusString);
    if (!response->isSucceed()) {
        return;
         
    }
     
    std::vector<char>* buffer = response->getResponseData();
    printf("Http Test, dump data: ");
    for (unsigned int i = 0 ; i < buffer->size();i++) {
        printf("%c",(*buffer)[i]);
    }
    printf("\n");
}