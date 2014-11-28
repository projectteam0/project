#ifndef __UPDATEMGR_H__
#define __UPDATEMGR_H__
/*记得要引头文件*/
#include "extensions/cocos-ext.h"
#include "network/HttpClient.h"
class UpdateMgr : public cocos2d::Ref
{
public:
	UpdateMgr();
	~UpdateMgr();
public:
	bool RequestUpdateInfo(const std::string& strVersion);
	bool RequestLoveEnd();
	bool RequestLoveStart();
	bool RequestFeedback(const std::string& data);
public:
	void onUpdateRequestCompleted(cocos2d::network::HttpClient *sender, cocos2d::network::HttpResponse *response);
	void onLoveEndCompleted(cocos2d::network::HttpClient *sender, cocos2d::network::HttpResponse *response);
	void onLoveStartCompleted(cocos2d::network::HttpClient *sender, cocos2d::network::HttpResponse *response);
	void onFeedbackCompleted(cocos2d::network::HttpClient *sender, cocos2d::network::HttpResponse *response);
private:
	void ProcessUpdateInfo(const std::string& strErrno,const std::string& strVersion,const std::string& strUuid,const std::string& strUrl);

private:
	std::string m_uuid;
};

#endif // __UPDATEMGR_H__
