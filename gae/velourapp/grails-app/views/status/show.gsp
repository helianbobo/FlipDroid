<%@ page import="velour.server.Status" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Show Status</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">Status List</g:link></span>
            <span class="menuButton"><g:link class="create" action="create">New Status</g:link></span>
        </div>
        <div class="body">
            <h1>Show Status</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
                <table>
                    <tbody>

                    
                        <tr class="prop">
                            <td valign="top" class="name">Id:</td>
                            
                            <td valign="top" class="value">${fieldValue(bean:statusInstance, field:'id')}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name">Bmiddlepic:</td>
                            
                            <td valign="top" class="value">${fieldValue(bean:statusInstance, field:'bmiddle_pic')}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name">Createdat:</td>
                            
                            <td valign="top" class="value">${fieldValue(bean:statusInstance, field:'created_at')}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name">Originalpic:</td>
                            
                            <td valign="top" class="value">${fieldValue(bean:statusInstance, field:'original_pic')}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name">Status Id:</td>
                            
                            <td valign="top" class="value">${fieldValue(bean:statusInstance, field:'statusId')}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name">Text:</td>
                            
                            <td valign="top" class="value">${fieldValue(bean:statusInstance, field:'text')}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name">Thumbnailpic:</td>
                            
                            <td valign="top" class="value">${fieldValue(bean:statusInstance, field:'thumbnail_pic')}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name">User:</td>
                            
                            <td valign="top" class="value">${fieldValue(bean:statusInstance, field:'user')}</td>
                            
                        </tr>
                    
                    </tbody>
                </table>
            </div>
            <div class="buttons">
                <g:form>
                    <input type="hidden" name="id" value="${statusInstance?.id}" />
                    <span class="button"><g:actionSubmit class="edit" value="Edit" /></span>
                    <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete" /></span>
                </g:form>
            </div>
        </div>
    </body>
</html>
