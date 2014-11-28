#include "UpdateMgr.h"
#include "base/CCUserDefault.h"
#include "base/ccUTF8.h"

UpdateMgr::UpdateMgr()
{
	
}

UpdateMgr::~UpdateMgr()
{
	
}

bool UpdateMgr::RequestUpdateInfo(const std::string& strVersion)
{
	std::string strUrl = "http://www.nuanai.me/a/checkversion.php?version=";
	strUrl += strVersion;
	strUrl += "&uuid=";
	strUrl += m_uuid;

	cocos2d::network::HttpRequest* request = new  cocos2d::network::HttpRequest();
    request->setUrl(strUrl.c_str());
    request->setRequestType(cocos2d::network::HttpRequest::Type::GET);
    request->setResponseCallback(this, httpresponse_selector(UpdateMgr::onUpdateRequestCompleted));
    request->setTag("GET nuanai update info");
    cocos2d::network::HttpClient::getInstance()->send(request);
    request->release();
	return true;
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

void UpdateMgr::onUpdateRequestCompleted(cocos2d::network::HttpClient *sender, cocos2d::network::HttpResponse *response)
{
	if (!response) {
        return;     
    }
     
    if (0 != strlen(response->getHttpRequest()->getTag())) {

    }
     
    long statusCode = response->getResponseCode();
    if (200 != statusCode || !response->isSucceed()) {
        return;
    }
     
    std::vector<char>* buffer = response->getResponseData();
	std::string strResponse;
    for (unsigned int i = 0 ; i < buffer->size() ; i++) {
		char c = (*buffer)[i];
		strResponse += c;
    }

	rapidjson::Document d1;
	d1.Parse<0>(strResponse.c_str());

	//获取JSon数据
	std::string strErrno,strVersion,strUuid,strUrl;
	const rapidjson::Value& vErrno = d1["errno"];
	if(vErrno.IsString())
	{
		strErrno = vErrno.GetString();
	}

	const rapidjson::Value& vVersion = d1["version"];
	if(vErrno.IsString())
	{
		strVersion = vVersion.GetString();
	}

	const rapidjson::Value& vUuid = d1["uuid"];
	if(vErrno.IsString())
	{
		m_uuid = vUuid.GetString();
	}

	const rapidjson::Value& vUrl = d1["url"];
	if(vErrno.IsString())
	{
		strUrl = vUrl.GetString();
	}

	RequestLoveStart();
	RequestLoveEnd();
	
	RequestFeedback("测试");
}

/*爱爱结束接口：

http://www.nuanai.me/a/loveend.php?uuid=de7cff1c51b3f1d64ccdc609d65c6346&pk=1
uuid	用户唯一标示
带上pk=1就是pk模式。

返回值
{
"errno"  : "0"/"1"/"-1"
"win":100,                                        //击败百分比
"duration": 12345                               //持续时间，秒
"competitor":12345                        //对手持续时间，秒    
}
errmsg :正常/uuid错误/其他错误
win: 最大值是100， 服务器内部有错误的时候是-1.
修改一下，去掉  "result":1                         //pk结果，1表示赢了，0表示输了    客户端自己比较吧，谁大就谁赢了
*/


bool UpdateMgr::RequestLoveEnd()
{
	std::string strUrl = "http://www.nuanai.me/a/loveend.php?pk=1&uuid=";
	strUrl += m_uuid;

	cocos2d::network::HttpRequest* request = new  cocos2d::network::HttpRequest();
	request->setUrl(strUrl.c_str());
	request->setRequestType(cocos2d::network::HttpRequest::Type::GET);
	request->setResponseCallback(this, httpresponse_selector(UpdateMgr::onLoveEndCompleted));
	request->setTag("GET nuanai end info");
	cocos2d::network::HttpClient::getInstance()->send(request);
	request->release();
	return true;
}

void UpdateMgr::onLoveEndCompleted(cocos2d::network::HttpClient *sender, cocos2d::network::HttpResponse *response)
{
	if (!response) {
		return;     
	}

	if (0 != strlen(response->getHttpRequest()->getTag())) {

	}

	long statusCode = response->getResponseCode();
	if (200 != statusCode || !response->isSucceed()) {
		return;
	}

	std::vector<char>* buffer = response->getResponseData();
	std::string strResponse;
	for (unsigned int i = 0 ; i < buffer->size() ; i++) {
		char c = (*buffer)[i];
		strResponse += c;
	}

	rapidjson::Document d1;
	d1.Parse<0>(strResponse.c_str());

	//获取JSon数据
	std::string strErrno;
	int percent = 50;
	int competitor = 1;
	int result = 0;
	int duration = 1;
	const rapidjson::Value& vErrno = d1["errno"];
	if(vErrno.IsString())
	{
		strErrno = vErrno.GetString();
	}

	const rapidjson::Value& vWin = d1["win"];
	if (vWin.IsInt())
	{
		percent = vWin.GetInt();
	}

	const rapidjson::Value& vcompetitor = d1["competitor"];
	if (vcompetitor.IsInt())
	{
		competitor = vcompetitor.GetInt();
	}

	const rapidjson::Value& vresult = d1["result"];
	if (vresult.IsInt())
	{
		result = vresult.GetInt();
	}

	const rapidjson::Value& vduration = d1["duration"];
	if (vduration.IsInt())
	{
		duration = vduration.GetInt();
	}
}

/*
获取人数接口：

http://www.nuanai.me/a/lovestart.php?uuid=de7cff1c51b3f1d64ccdc609d65c6346
uuid	用户唯一标示


返回值
{
"errno"  : "0"/"1"/"-1"
"now":345,                                        //当前人数

}
errmsg :正常/uuid错误/其他错误
*/

bool UpdateMgr::RequestLoveStart()
{
	std::string strUrl = "http://www.nuanai.me/a/lovestart.php?uuid=";
	strUrl += m_uuid;

	cocos2d::network::HttpRequest* request = new  cocos2d::network::HttpRequest();
	request->setUrl(strUrl.c_str());
	request->setRequestType(cocos2d::network::HttpRequest::Type::GET);
	request->setResponseCallback(this, httpresponse_selector(UpdateMgr::onLoveStartCompleted));
	request->setTag("GET nuanai start info");
	cocos2d::network::HttpClient::getInstance()->send(request);
	request->release();
	return true;
}

void UpdateMgr::onLoveStartCompleted(cocos2d::network::HttpClient *sender, cocos2d::network::HttpResponse *response)
{
	if (!response) {
		return;     
	}

	if (0 != strlen(response->getHttpRequest()->getTag())) {

	}

	long statusCode = response->getResponseCode();
	if (200 != statusCode || !response->isSucceed()) {
		return;
	}

	std::vector<char>* buffer = response->getResponseData();
	std::string strResponse;
	for (unsigned int i = 0 ; i < buffer->size() ; i++) {
		char c = (*buffer)[i];
		strResponse += c;
	}

	rapidjson::Document d1;
	d1.Parse<0>(strResponse.c_str());

	//获取JSon数据
	std::string strErrno;
	int nowPerson = 50;
	const rapidjson::Value& vErrno = d1["errno"];
	if(vErrno.IsString())
	{
		strErrno = vErrno.GetString();
	}

	const rapidjson::Value& vNow = d1["now"];
	if (vNow.IsInt())
	{
		nowPerson = vNow.GetInt();
	}
}

/*
上传用户反馈的接口

http://www.nuanai.me/a/feedback.php

post数据,  
uuid=de7cff1c51b3f1d64ccdc609d65c6346&content=a1bcdeee
uuid	用户唯一标示
content 用utf-8编码
content最多1024个字
返回值
{
"errno"  : "0"/"1"/"-1"  
}
errmsg :正常/uuid错误/其他错误

*/

bool UpdateMgr::RequestFeedback(const std::string& data)
{
	std::string strUrl = "http://www.nuanai.me/a/feedback.php";

	std::string content = "uuid="+m_uuid+"&content="+data;

	cocos2d::network::HttpRequest* request = new  cocos2d::network::HttpRequest();
	request->setUrl(strUrl.c_str());
	request->setRequestType(cocos2d::network::HttpRequest::Type::POST);
	request->setRequestData(content.c_str(), content.size());
	request->setResponseCallback(this, httpresponse_selector(UpdateMgr::onFeedbackCompleted));
	request->setTag("Post nuanai  info");
	cocos2d::network::HttpClient::getInstance()->send(request);
	request->release();
	return true;
}

void UpdateMgr::onFeedbackCompleted(cocos2d::network::HttpClient *sender, cocos2d::network::HttpResponse *response)
{
	if (!response) {
		return;     
	}

	if (0 != strlen(response->getHttpRequest()->getTag())) {

	}

	long statusCode = response->getResponseCode();
	if (200 != statusCode || !response->isSucceed()) {
		return;
	}

	std::vector<char>* buffer = response->getResponseData();
	std::string strResponse;
	for (unsigned int i = 0 ; i < buffer->size() ; i++) {
		char c = (*buffer)[i];
		strResponse += c;
	}
}

void UpdateMgr::ProcessUpdateInfo(const std::string& strErrno,const std::string& strVersion,const std::string& strUuid,const std::string& strUrl)
{
	if(!strUuid.empty())
	{
		cocos2d::UserDefault::getInstance()->setStringForKey("uuid",strUuid);
	}
}