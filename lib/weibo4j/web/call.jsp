<%@ page contentType="text/html;charset=utf-8" %>
<%@ page language="java" import="weibo4j.*" %>
<%@ page language="java" import="weibo4j.http.*" %>

<jsp:useBean id="weboauth" scope="session" class="weibo4j.examples.WebOAuth" />
<%
if("1".equals(request.getParameter("opt")))
{
	RequestToken resToken=weboauth.request("http://localhost:8080/callback.jsp");
	if(resToken!=null){
		out.println(resToken.getToken());
		out.println(resToken.getTokenSecret());
		session.setAttribute("resToken",resToken);
		extractResponse.sendRedirect(resToken.getAuthorizationURL());

	}else{
		out.println("request error");
		}
}else{
%>
<a href="call.jsp?opt=1">请点击进行OAuth认证</a>   
<%}%>