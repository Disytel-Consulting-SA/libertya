<!--
  - openXpertya (r), Tienda en-l�ea, Copyright (c) 2003-2007 ConSerTi S.L.
  - este fichero esta licenciado bajo los t�minos de la Licencia Pblica openXpertya (LPO)
  - con aplicaci� directa del ADDENDUM A, secci� 6 (A.6) y posibilidad de relicenciamiento.
  - Licencia y m� informaci� en http://www.openxpertya.org/ayuda/Licencia.html
  - Informaci� de la direcci� de la tienda Web
  -->

<%@ page language="java" import="java.sql.*" %>
<%@ page session="true" contentType="text/html; charset=iso-8859-1" errorPage="errorPage.jsp" %>
<%@ taglib uri="webStore.tld" prefix="cws" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<c:if test='${empty webUser || !webUser.loggedIn}'>
	<c:redirect url='loginServlet?ForwardTo=registrations.jsp'/>
</c:if>

<html>
	<head>
		<title><c:out value='${ctx.name}'/></title>
		<%@ include file="/WEB-INF/jspf/head.jspf" %>
	</head>
	<body onLoad="initClock()">
		<!-- Set Registration ID and get Registration (may not exist) -->
		<c:set target='${info}' property='id' value='0' />
		<c:if test='${not empty param.A_Registration_ID}'>
			<c:set target='${info}' property='id' value='${param.A_Registration_ID}' />
		</c:if>  
		<c:set var='registration' value='${info.registration}' />
		<c:if test='${empty registration}'>
			<c:set target='${info}' property='message' value='Registration not found' />
			<c:redirect url='registrations.jsp'/>
		</c:if>	
		<div id="wrap">
			<div id="header"><%@ include file="/WEB-INF/jspf/header.jspf" %></div>
			<div id="left"><%@ include file="/WEB-INF/jspf/left.jspf" %></div>
			<div id="right"><%@ include file="/WEB-INF/jspf/right.jspf" %></div>
			<div id="center">
				<h1>Registro</h1>
				<c:if test='${not empty info.info}'>
					<p><strong><c:out value='${info.message}'/></strong></p>
				</c:if>
				<form action="registrationServlet" method="post" enctype="application/x-www-form-urlencoded" name="registration" id="registration">
				<!--[if IE]> <div style="width: 100%;"> <![endif]-->
				<table class="tablelist">
					<tr> 
						<th>Nombre</th>
						<td><input name="Name" type="text" id="Name" value="<c:out value='${registration.name}'/>" size="60" maxlength="60" class="textfield"></td> 
					</tr>
					<tr> 
						<th>Descripci&oacute;n</th>
						<td><input name="Description" type="text" id="Description" value="<c:out value='${registration.description}'/>" size="60" maxlength="255" class="textfield"></td>
					</tr>
					<tr>
						<th>Fecha del servicio</th>
						<td><input name="AssetServiceDate" type="text" id="AssetServiceDate" value="<fmt:formatDate value='${registration.assetServiceDate}'/>" size="20" class="textfield"></td>
					</tr>
					<tr>
						<th>En producci&oacute;n</th>
						<td><input name="IsInProduction" type="checkbox" id="IsInProduction" value="IsInProduction" checked="<c:if test='${registration.inProduction}'>checked</c:if>"></td>
					</tr>
					<tr> 
						<th>Permite publicaci&oacute;n</th>
						<td><input name="IsAllowPublish" type="checkbox" id="IsAllowPublish" value="IsAllowPublish" checked="<c:if test='${registration.allowPublish}'>checked</c:if>"></td>
					</tr>
					<c:forEach items='${registration.values}' var='rvalue'> 
						<tr>
							<th><c:out value='${rvalue.registrationAttributeDescription}'/></th>
							<td><input name="<c:out value='${rvalue.registrationAttribute}'/>" type="text" id="<c:out value='${rvalue.registrationAttribute}'/>" value="<c:out value='${rvalue.name}'/>" size="30" maxlength="60" class="textfield"></td>
						</tr>
					</c:forEach> 
					<tr>
						<th>&nbsp;</th>
						<td>
							<input name="A_Registration_ID" type="hidden" id="A_Registration_ID" value="<c:out value='${registration.a_Registration_ID}'/>">
							<input name="Reset" type="reset" id="Reset" value="Borrar" class="button"> 
							<input type="submit" name="Submit" value="Enviar" class="button">
						</td>
					</tr>
				</table>
				<!--[if IE]> </div> <![endif]-->
				</form>
			</div>
			<div id="footer"><%@ include file="/WEB-INF/jspf/footer.jspf" %></div>
		</div>
	</body>
</html>