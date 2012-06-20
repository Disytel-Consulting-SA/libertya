<!--
  - openXpertya (r), Tienda en-l�ea, Copyright (c) 2003-2007 ConSerTi S.L.
  - este fichero esta licenciado bajo los t�minos de la Licencia Pblica openXpertya (LPO)
  - con aplicaci� directa del ADDENDUM A, secci� 6 (A.6) y posibilidad de relicenciamiento.
  - Licencia y m� informaci� en http://www.openxpertya.org/ayuda/Licencia.html
  - Informaci� de la direcci� de la tienda Web
  -->

<%@ page session="true" contentType="text/html; charset=iso-8859-1" errorPage="errorPage.jsp" %>
<%@ taglib uri="webStore.tld" prefix="cws" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<c:if test='${empty webUser || !webUser.loggedIn}'>
	<c:redirect url='loginServlet?ForwardTo=notes.jsp'/>
</c:if>

<html>
	<head>
		<title><c:out value='${ctx.name}'/></title>
		<%@ include file="/WEB-INF/jspf/head.jspf" %>
	</head>
	<body onLoad="initClock()">
		<div id="wrap">
			<div id="header"><%@ include file="/WEB-INF/jspf/header.jspf" %></div>
			<div id="left"><%@ include file="/WEB-INF/jspf/left.jspf" %></div>
			<div id="right"><%@ include file="/WEB-INF/jspf/right.jspf" %></div>
			<div id="center">
				<h1>Mis noticias y mensajes</h1>
				<c:if test='${not empty info.info}'>
					<p><strong><c:out value='${info.message}'/></strong></p>
				</c:if>
				<h3>Flujo de trabajo</h3>
				<!--[if IE]> <div style="width: 100%;"> <![endif]-->
				<table class="tablelist">
					<tr>
						<th>Creada<br><em>Prioridad</em></th>
						<th>Siguiente paso<br><em>Descripci&oacute;n</em></th>
						<th>Historial</th>
						<th>Respuesta</th>
					</tr>
					<c:forEach items='${info.activities}' var='act'> 
					<tr> 
						<form action="workflowServlet" method="post" enctype="application/x-www-form-urlencoded" name="Activity">
						<input name="AD_WF_Activity_ID" type="hidden" value="<c:out value='${act.AD_WF_Activity_ID}'/>"/>
						<td><fmt:formatDate value='${act.created}'/><br><em><c:out value='${act.priority}'/></em></td>
						<td>
							<c:out value='${act.nodeName}'/><br>
							<em><c:out value='${act.nodeDescription}'/></em>
							<c:if test='${act.pdfAttachment}'><br>
								<a href="workflowServlet/W_<c:out value='${act.AD_WF_Activity_ID}'/>.pdf?AD_WF_Activity_ID=<c:out value='${act.AD_WF_Activity_ID}'/>" target="_blank"><img src="pdf.png" width="16" height="16" align="absmiddle" alt="Obtener fichero en formato electr&oacute;nico" title="Obtener fichero en formato electr&oacute;nico" border="0"></a>
							</c:if>
						</td>
						<td><c:out value='${act.historyHTML}' escapeXml='false'/></td>
						<td>
							<textarea name="textMsg" cols="30" rows="3" id="textMsg" class="textfield"></textarea><br>
							<cws:workflow activityID="${act.AD_WF_Activity_ID}" />
							<input type="submit" name="Submit" value="Enviar" class="button">
						</td>
						</form>
					</tr>
					</c:forEach> 
				</table>
				<h3>Noticias</h3>
				<table class="tablelist">
					<tr> 
						<th>Creada</th>
						<th>Mensaje</th>
						<th>Referencia</th>
						<th>Descripci&oacute;n</th>
						<th>Respuesta</th>
					</tr>
					<c:forEach items='${info.notes}' var='note'> 
					<tr> 
						<form action="noteServlet" method="post" enctype="application/x-www-form-urlencoded" name="Notice">
						<input name="AD_Note_ID" type="hidden" value="<c:out value='${note.AD_Note_ID}'/>"/>
						<td><fmt:formatDate value='${note.created}'/></td>
						<td><c:out value='${note.message}'/></td>
						<td>
							<c:out value='${note.reference}'/> 
							<c:if test='${note.pdfAttachment}'>
								<a href="noteServlet/N_<c:out value='${note.created}'/>.pdf?AD_Note_ID=<c:out value='${note.AD_Note_ID}'/>" target="_blank"><img src="pdf.png" width="16" height="16" align="absmiddle" alt="Obtener fichero en formato electr&oacute;nico" title="Obtener fichero en formato electr&oacute;nico" border="0"></a>
							</c:if>
						</td>
						<td><c:out value='${note.description}'/></td>
						
						
						<td>
							<input name="Processed" type="checkbox" id="Processed" value="Processed">Con conocimiento <input name="Update" type="submit" id="Update" value="Actualizar" class="button">
						</td>
						</form>
					</tr>
					</c:forEach> 
				</table>	
				<!--[if IE]> </div> <![endif]-->		
			</div>
			<div id="footer"><%@ include file="/WEB-INF/jspf/footer.jspf" %></div>
		</div>
	</body>
</html>
