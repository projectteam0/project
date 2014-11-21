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
	bool RequestUpdateInfo(const std::string& strVersion,const std::string& strUUID);
public:
	void onUpdateRequestCompleted(cocos2d::network::HttpClient *sender, cocos2d::network::HttpResponse *response);
};

#endif // __UPDATEMGR_H__
