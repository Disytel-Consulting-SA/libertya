<!--
  - openXpertya (r), Tienda en-l�nea, Copyright (c) 2003-2007 ConSerTi S.L.
  - este fichero esta licenciado bajo los t�rminos de la Licencia P�blica openXpertya (LPO)
  - con aplicaci�n directa del ADDENDUM A, secci�n 6 (A.6) y posibilidad de relicenciamiento.
  - Licencia y m�s informaci�n en http://www.openxpertya.org/ayuda/Licencia.html
  - Informaci�n de la direcci�n de la tienda Web
  -->

<%@ page session="true" contentType="text/html; charset=iso-8859-1" errorPage="errorPage.jsp" %>
<%@ taglib uri="webStore.tld" prefix="cws" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<c:if test='${empty webUser || !webUser.loggedIn}'>
	<c:redirect url='loginServlet?ForwardTo=info.jsp'/>
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
				<h1>Boletines - &Aacute;reas de inter&eacute;s</h1>
				<c:if test='${not empty info.info}'>
				<p><strong><c:out value='${info.message}'/></strong></p>
				</c:if>
				<!--[if IE]> <div style="width: 100%;"> <![endif]-->
				<table class="tablelist">
				<tr> 
					<th>&Aacute;reas de inter&eacute;s</th>
					<th>Descripci&oacute;n</th>
					<th>Suscripci&oacute;n</th>
				</tr>
				<c:forEach items='${info.interests}' var='interest'> 
					<tr> 
						<td><c:out value='${interest.name}'/></td>
						<td><c:out value='${interest.description}'/>&nbsp;</td>
						<td>
						<c:choose>
						<c:when test='${interest.subscribed}'>
							<fmt:formatDate value='${interest.subscribeDate}'/> <input type="button" name="UnSubscribe_<c:out value='${interest.r_InterestArea_ID}'/>" value="Baja" onClick="window.top.location.replace('infoServlet?mode=unsubscribe&area=<c:out value='${interest.r_InterestArea_ID}'/>&contact=<c:out value='${info.user_ID}'/>');" class="button">
						</c:when>
						<c:otherwise>
							<input type="button" name="Subscribe_<c:out value='${interest.r_InterestArea_ID}'/>" value="Alta" onClick="window.top.location.replace('infoServlet?mode=subscribe&area=<c:out value='${interest.r_InterestArea_ID}'/>&contact=<c:out value='${info.user_ID}'/>');" class="button">
						</c:otherwise>
						</c:choose>
					  </td>
					</tr>
				</c:forEach> 
				</table>
				<!--[if IE]> </div> <![endif]-->
			</div>
			<div id="footer"><%@ include file="/WEB-INF/jspf/footer.jspf" %></div>
		</div>
	</body>
</html>