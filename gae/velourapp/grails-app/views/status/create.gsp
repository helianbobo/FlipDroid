<%@ page import="velour.server.Status" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Create Status</title>         
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">Status List</g:link></span>
        </div>
        <div class="body">
            <h1>Create Status</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${statusInstance}">
            <div class="errors">
                <g:renderErrors bean="${statusInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" method="post" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="bmiddle_pic">Bmiddlepic:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:statusInstance,field:'bmiddle_pic','errors')}">
                                    <g:textField name="bmiddle_pic" value="${statusInstance?.bmiddle_pic}" />
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="created_at">Createdat:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:statusInstance,field:'created_at','errors')}">
                                    <g:datePicker name="created_at" precision="day" value="${statusInstance?.created_at}" />
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="original_pic">Originalpic:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:statusInstance,field:'original_pic','errors')}">
                                    <g:textField name="original_pic" value="${statusInstance?.original_pic}" />
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="statusId">Status Id:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:statusInstance,field:'statusId','errors')}">
                                    <g:textField name="statusId" value="${statusInstance?.statusId}" />
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="text">Text:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:statusInstance,field:'text','errors')}">
                                    <g:textField name="text" value="${statusInstance?.text}" />
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="thumbnail_pic">Thumbnailpic:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:statusInstance,field:'thumbnail_pic','errors')}">
                                    <g:textField name="thumbnail_pic" value="${statusInstance?.thumbnail_pic}" />
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="user">User:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:statusInstance,field:'user','errors')}">
                                    
                                </td>
                            </tr> 
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><input class="save" type="submit" value="Create" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
