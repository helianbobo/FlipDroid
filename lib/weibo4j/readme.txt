
新浪微博API（java版）
============================
修改事项：
1、接口返回结果采用json对象方式封装
2、修改WeiboResponse中空字符串("")调用判断的bug
3、修改了JSONObject中返回空对象的问题
4、增加了直接文件上传接口直接

常用接口实例：
（使用前先修改 Weibo.java 中 
 	public static final String CONSUMER_KEY = "";
	public static final String CONSUMER_SECRET = "";
 填写成实际申请的 CONSUMER_KEY 及 CONSUMER_SECRET）

1、获取最新公共微博列表
	参考：weibo4j.examples.GetTimelines 中的getPublicTimeline()部分

2、发表微博
	参考：weibo4j.examples.Update 中的updateStatus(...)部分

3、发表评论
	参考：weibo4j.examples.Update 中的updateComment(...)部分

4、删除评论
	参考：weibo4j.examples.Update 中的destroyComment(...)部分

5、发送私人消息
	参考：weibo4j.examples.DirectMessage 类

6、发表带图片的微博
	参考：weibo4j.examples.OAuthUploadByFile 中的uploadStatus(status,file)部分

7、更新用户头像
	参考：weibo4j.examples.OAuthUpdateProfile类

8、OAuth桌面应用访问
	步骤： 1> 在weibo4j.examples.OAuthUpdate 访问认证的URL，得到pin
		  2> 在weibo4j.examples.OAuthUpdate 	输入pin，然后得到AccessToken
		  3> 即可访问用户的微博，参考：weibo4j.examples.OAuthUpdateTwo

9、OAuth WEB应用访问
	步骤： 
					1> 在weibo4j.examples.WebOAuth 带backurl参数请求OAuth认证，获得RequestToken
				  2> 在callback 的地址里面，接受到oauth_verifier参数，然后再发一次请求，即可获得AccessToken
				  3> 用AccessToken访问用户的微博
	实例说明：
	
		在web目录下面有两个jsp页面：
		call.jsp 
					是发送request的请求，在第九行的参数“http://localhost:8080/callback.jsp”是回调地址
				  当获取成功后将RequestToken置入session，并重定向到用户认证地址
	  callback.jsp 
	  			接收到oauth_verifier参数，从session里面拿到RequestToken，再请求获取AccessToken
	    		获取到后即可对用户微博进行操作，本例中是发表微博
	
	测试环境：
		
		本例中可以将call.jsp和  callback.jsp直接放到tomcat的根目录下面：webapps\ROOT
		并将实例项目编译好的classes文件和lib目录拷贝到ROOT\WEB-INF下面
		重启tomcat，访问http://localhost:8080/call.jsp?opt=1	
	注：也可以打成war包，但注意call.jsp里面的callback参数需要做相应修改


=============================================================================
其他接口在weibo4j.Weibo类中定义，调用方式请参考weibo4j.examples，下面是完整的接口列表：

获取下行数据集(timeline)接口  方法名：
statuses/public_timeline 最新公共微博      方法名：getPublicTimeline
statuses/friends_timeline 最新关注人微博 (别名: statuses/home_timeline)  方法名：getFriendsTimeline 
statuses/user_timeline 用户发表微博列表   方法名：getUserTimeline(String id, Paging paging)
statuses/mentions 最新 @用户的    方法名： getMentions()
statuses/comments 单条评论列表(按微博)   方法名：getComments()

微博访问接口
statuses/show 获取单条   方法名：showStatus(long id)

statuses/update 发表微博   方法名：updateStatus(String status)
statuses/upload 发表微博及图片 方法名：uploadStatus(String status,File file)
statuses/destroy 删除 方法名： destroyStatus(long statusId)

statuses/comment 评论 方法名： destroyComment(long commentId)
statuses/comment_destroy 删除评论  方法名：destroyComment

私信接口
direct_messages 我的私信列表 方法名：getDirectMessages() 分页 getDirectMessages(Paging paging)
direct_messages/sent 我发送的私信列表 方法名：getSentDirectMessages()
direct_messages/new 发送私信 方法名：sendDirectMessage(String id,String text)
direct_messages/destroy 删除一条私信 方法名：destroyDirectMessage(int id)
关注接口
friendships/create 关注某用户 方法名：createFriendship(String id)或 createFriendship(String id, boolean follow)
friendships/destroy 取消关注 方法名： destroyFriendship(String id)
friendships/exists 是否关注某用户 方法名：existsFriendship(String userA, String userB)

friends/ids 关注列表 方法名： getFriendsIDs(long cursor)
followers/ids 粉丝列表 方法名：getFollowersIDs(long cursor)
账号接口
account/verify_credentials 验证身份是否合法 方法名：verifyCredentials()
account/rate_limit_status 查看当前频率限制 方法名：rateLimitStatus()

account/update_profile_image 更改头像 方法名：updateProfileImage(File image)
account/update_profile 更改资料 方法名：User updateProfile(String name, String email, String url, String location, String description)

收藏接口
favorites 收藏列表 方法名：getFavorites()
favorites/create 添加收藏 方法名：createFavorite(long id)
favorites/destroy 删除收藏 方法名：destroyFavorite(long id)

