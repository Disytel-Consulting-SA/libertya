<!--
  - openXpertya (r), Tienda en-l�ea, Copyright (c) 2003-2007 ConSerTi S.L.
  - este fichero esta licenciado bajo los t�minos de la Licencia Pblica openXpertya (LPO)
  - con aplicaci� directa del ADDENDUM A, secci� 6 (A.6) y posibilidad de relicenciamiento.
  - Licencia y m� informaci� en http://www.openxpertya.org/ayuda/Licencia.html
  - Informaci� de la direcci� de la tienda Web
  -->

<%@ page session="true" contentType="text/html; charset=UTF-8" errorPage="errorPage.jsp" %>
<%@ taglib uri="webStore.tld" prefix="cws" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<c:if test='${empty webUser || !webUser.loggedIn}'>
	<c:redirect url='loginServlet?ForwardTo=requests.jsp'/>
</c:if>

<html>
	<head>
		<title><c:out value='${ctx.name}'/></title>
		<%@ include file="/WEB-INF/jspf/head.jspf" %>
	</head>
	<body onLoad="initClock()">
	<!-- Set Request ID and get Request		-->
	<c:set target='${info}' property='id' value='${param.R_Request_ID}' />
	<c:set var='request' value='${info.request}' />
	<c:if test='${empty request}'>
		<c:set target='${info}' property='message' value='Request not found' />
		<c:redirect url='requests.jsp'/>
	</c:if>	
		<div id="wrap">
			<div id="header"><%@ include file="/WEB-INF/jspf/header.jspf" %></div>
			<div id="left"><%@ include file="/WEB-INF/jspf/left.jspf" %></div>
			<div id="right"><%@ include file="/WEB-INF/jspf/right.jspf" %></div>
			<div id="center">
				<h1>Mensaje<c:out value='${request.documentNo}'/></h1>
				<c:if test='${not empty info.info}'>
					<p><c:out value='${info.message}'/></p>
				</c:if>
				<p><a href="request.jsp">Nuevo mensaje</a></p>
				<!--[if IE]> <div style="width: 100%;"> <![endif]-->
				<table class="tablelist">
					<tr>
						<th>Sumario</th>
						<td colspan="3"><c:out value='${request.summary}'/></td>
					</tr>
					<tr>
						<th>Tipo</th>
						<td><c:out value='${request.requestTypeName}'/></td>
						<th>Grupo:</th>
						<td><c:out value='${request.groupName}'/></td>
					</tr>
					<tr>
						<th>Categor&iacute;a</th>
						<td><c:out value='${request.categoryName}'/></td>
						<th>Importancia:</th>
						<td><c:out value='${request.priorityUserText}'/></td>
					</tr>
					<tr>
						<th>Estado</th>
						<td><c:out value='${request.statusName}'/></td>
						<th>Prioridad:</th>
						<td><c:out value='${request.priorityText}'/></td>
					</tr>
					<tr>
						<th>Resoluci&oacute;n</th>
						<td><c:out value='${request.resolutionName}'/></td>
						<th><c:out value='${request.dueTypeText}'/></th>
						<td><fmt:formatDate value='${request.dateNextAction}'/></td>
					</tr>
					<tr>
						<th>Creado</th>
						<td><fmt:formatDate value='${request.created}'/></td>
						<td><c:out value='${request.createdByName}'/></td>
						<td><c:out value='${request.confidentialText}'/></td>
					</tr>
					<tr>
						<th>Resultado</th>
						<td colspan="3"><c:out value='${request.result}'/></td>
					</tr>
					<tr>
						<th>Archivos adjuntos</th>
						<td colspan="3">
							<c:if test='${not empty request.attachment}'>
							<c:out value='${request.attachment.textMsg}'/>
							<c:forEach items='${request.attachment.entries}' var='entry'>
								<a href="requestServlet?R_Request_ID=<c:out value='${request.r_Request_ID}'/>&AttachmentIndex=<c:out value='${entry.index}'/>" target="_blank"><c:out value='${entry.name}'/></a> - 
							</c:forEach>
							</c:if>
						</td>
					</tr>
				</table>
				<c:if test='${request.processed}'>
					<h3>Respuesta</h3>
					<form method="post" name="Request" action="requestServlet" enctype="application/x-www-form-urlencoded" onSubmit="checkForm(this, new Array ('Summary'));">
					<input name="Source" type="hidden" value=""/>
					<input name="Info" type="hidden" value=""/>
					
					<script language="Javascript">
						document.Request.Source.value=document.referrer;
						document.Request.Info.value=document.lastModified;
					</script>
					
					<input name="ForwardTo" type="hidden" value="<c:out value='${param.ForwardTo}'/>"/>
					<input name="SalesRep_ID" type="hidden" value="<c:out value='${webUser.salesRep_ID}'/>"/>
					<input name="R_Request_ID" type="hidden" id="R_Request_ID" value="<c:out value='${request.r_Request_ID}'/>">
					
					<table width="70%" border="0" align="center" cellpadding="5" cellspacing="0">
						<tr> 
							<td width="50%" align="right">De</td>
							<td width="50%" align="left"><c:out value='${webUser.email}'/></td>
						</tr>
						<tr> 
							<td><label id="ID_Summary" for="Summary">Respuesta/Continuaci&oacute;n:</label></td>
							<td><input name="Confidential" type="checkbox" id="Confidential" value="Confidential"> Informaci&oacute;n confidencial</td>
						</tr>
						<tr> 
							<td colspan="2" align="left"> <textarea name="Summary" cols="80" rows="8" id="ID_Summary" class="textfield"></textarea> </td>
						</tr>
						<tr> 
							<td colspan="2" align="center"><span class="mini">1500 caracteres m&aacute;ximo. Los archivos mayores ser&aacute;n a&ntilde;adidos como adjuntos.</span></td>
						</tr>
						<tr> 
						  <td align="right"> <input name="Reset" type="reset" value="Borrar" class="button" /></td>
							<td align="left"> <input name="Submit" type="submit" value="Enviar" class="button"> </td>
						</tr>
					</table>
					</form>
					<form action="requestServlet" method="post" enctype="multipart/form-data" name="fileLoad" id="fileLoad">
					<input name="R_Request_ID" type="hidden" id="R_Request_ID" value="<c:out value='${request.r_Request_ID}'/>">
			  
					<table border="0" align="center" cellpadding="5" cellspacing="0">
						<tr>
							<td align="right"><label for="file">Archivo: </label></td>
							<td align="left"><input name="file" type="file" id="file" size="40" class="textfield"></td>
							<td align="right"><input type="submit" name="Submit" value="Subir" class="button"></td>
						</tr>
					</table>
					</form>
				</c:if>
				<h3>Historial</h3>
				<table class="tablelist">
					<tr>
						<th width="40%">Creado</th>
						<th width="20%">Por</th>
						<th width="40%">Resultado</th>
					</tr>
					<c:forEach items='${request.updatesCustomer}' var='update'> 
						<tr>
							<td><fmt:formatDate value='${update.created}'/></td>
							<td><c:out value='${update.createdByName}'/></td>
							<td><c:out value='${update.result}'/></td>
						</tr>
					</c:forEach> 
				</table>
				<br>
				<table class="tablelist">
					<tr>
						<th width="40%">Actualizado</th>
						<th width="20%">Por</th>
						<th width="40%">Antiguo valor</th>
					</tr>
					<c:forEach items='${request.actions}' var='action'> 
						<tr> 
						  <td><fmt:formatDate value='${action.created}'/></td>
							<td><c:out value='${action.createdByName}'/></td>
							<td><c:out value='${action.changesHTML}' escapeXml='false'/></td>
						</tr>
					</c:forEach> 
				</table>
				<!--[if IE]> </div> <![endif]-->			
			</div>
			<div id="footer"><%@ include file="/WEB-INF/jspf/footer.jspf" %></div>
		</div>
	</body>
</html>