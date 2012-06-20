<!--
  - openXpertya (r), Tienda en-línea, Copyright (c) 2003-2007 ConSerTi S.L.
  - este fichero esta licenciado bajo los términos de la Licencia Pública openXpertya (LPO)
  - con aplicación directa del ADDENDUM A, sección 6 (A.6) y posibilidad de relicenciamiento.
  - Licencia y más información en http://www.openxpertya.org/ayuda/Licencia.html
  - Información de la dirección de la tienda Web
  -->

<%@ page session="true" contentType="text/html; charset=iso-8859-1" errorPage="errorPage.jsp" %>
<%@ taglib uri="webStore.tld" prefix="cws" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<c:if test='${empty webUser || !webUser.loggedIn}'>
	<c:redirect url='loginServlet?ForwardTo=shipments.jsp'/>
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
				<h1>Mis env&iacute;os</h1>
				<c:if test='${not empty info.info}'>
					<p><strong><c:out value='${info.message}'/></strong></p>
				</c:if>
				<!--[if IE]> <div style="width: 100%;"> <![endif]-->
				<table class="tablelist">
					<tr>
						<th>Documento</th>
						<th>Estado</th>
						<th>L&iacute;neas del albar&aacute;n </th>
						<th>Seguimiento</th>
						<th>M&eacute;todo de env&iacute;o</th>
						<th>Fecha de env&iacute;o</th>
					</tr>
					<c:forEach items='${info.shipments}' var='shipment' varStatus='status'>
						<c:choose>
							<c:when test="${status.count % 2 == 0}"><tr class="row1"></c:when>
							<c:otherwise><tr class="row0"></c:otherwise>
						</c:choose>  
						<td><a href="shipmentLines.jsp?M_InOut_ID=<c:out value='${shipment.m_InOut_ID}'/>"><c:out value='${shipment.documentNo}'/></a></td>
						<td><c:out value='${shipment.docStatusName}'/></td>
						<td><c:out value='${shipment.summary}'/></td>
						<td><c:out value='${shipment.trackingNo}'/></td>
						<td>
						<c:choose>
							<c:when test='${shipment.deliveryViaRule=="P"}'>Recogida por el socio</c:when>
							<c:when test='${shipment.deliveryViaRule=="S"}'>Env&iacute;o por transporte</c:when>
							<c:when test='${shipment.deliveryViaRule=="D"}'>Entrega por nuestros medios</c:when>
							<c:otherwise><c:out value='${shipment.deliveryViaRule}'/>M&eacute;todos de entrega alternativos</c:otherwise>
						</c:choose>
						</td>
						<td><fmt:formatDate value='${shipment.movementDate}'/></td>
					</tr>
					</c:forEach> 
				</table>
				<!--[if IE]> </div> <![endif]-->		
			</div>
			<div id="footer"><%@ include file="/WEB-INF/jspf/footer.jspf" %></div>
		</div>
	</body>
</html>