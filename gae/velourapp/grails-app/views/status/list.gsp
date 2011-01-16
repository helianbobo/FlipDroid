<%@ page import="velour.server.Status" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Status List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New Status</g:link></span>
        </div>
        <div class="body">
            <h1>Status List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	        <g:sortableColumn property="id" title="Id" />
                        
                   	        <g:sortableColumn property="bmiddle_pic" title="Bmiddlepic" />
                        
                   	        <g:sortableColumn property="created_at" title="Createdat" />
                        
                   	        <g:sortableColumn property="original_pic" title="Originalpic" />
                        
                   	        <g:sortableColumn property="statusId" title="Status Id" />
                        
                   	        <g:sortableColumn property="text" title="Text" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${statusInstanceList}" status="i" var="statusInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${statusInstance.id}">${fieldValue(bean:statusInstance, field:'id')}</g:link></td>
                        
                            <td>${fieldValue(bean:statusInstance, field:'bmiddle_pic')}</td>
                        
                            <td>${fieldValue(bean:statusInstance, field:'created_at')}</td>
                        
                            <td>${fieldValue(bean:statusInstance, field:'original_pic')}</td>
                        
                            <td>${fieldValue(bean:statusInstance, field:'statusId')}</td>
                        
                            <td>${fieldValue(bean:statusInstance, field:'text')}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${statusInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
