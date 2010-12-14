<%@ page contentType="text/html;charset=utf-8" %>
<%@ page language="java" import="weibo4j.http.*" %>
<%@ page language="java" import="weibo4j.*" %>

<jsp:useBean id="weboauth" scope="session" class="weibo4j.examples.WebOAuth" />
<%
	
	String verifier=request.getParameter("oauth_verifier");
	
	if(verifier!=null)
	{
	System.out.println("oauth:"+verifier);
		RequestToken resToken=(RequestToken) session.getAttribute("resToken");

		if(resToken!=null)
		{
			AccessToken accessToken=weboauth.requstAccessToken(resToken,verifier);
				if(accessToken!=null)
				{
					//out.println(accessToken.getToken());
					//out.println(accessToken.getTokenSecret());
					//第二个参数每次只能用一次，发表微博内容不能重复，如果重复发会返回400错误
					//这个accessToken不用每次访问都重新取，可以存到session里面用
					weboauth.update(accessToken,"web方式发表微博4");
					out.println("发表成功");				
				}else
					{
					out.println("access token request error");
					}
		
		}
		else
			{
			out.println("request token session error");
			}
	}
	else
		{
		out.println("verifier String error");
		}

%>   