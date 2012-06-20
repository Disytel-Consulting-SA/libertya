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
		<!-- Set Shipment ID and get Shipment	-->
		<c:set target='${info}' property='id' value='${param.M_InOut_ID}' />
		<c:set var='shipment' value='${info.envio}' />
		<c:if test='${empty shipment}'>
			<c:set target='${info}' property='message' value='Envío no encontrado' />
			<c:redirect url='shipmentLines.jsp'/>
		</c:if>
		<div id="wrap">
			<div id="header"><%@ include file="/WEB-INF/jspf/header.jspf" %></div>
			<div id="left"><%@ include file="/WEB-INF/jspf/left.jspf" %></div>
			<div id="right"><%@ include file="/WEB-INF/jspf/right.jspf" %></div>
			<div id="center">
				<h1>Detalles del albar&aacute;n de env&iacute;o <c:out value='${shipment.documentNo}'/></h1>
				<c:if test='${not empty info.info}'>
					<p><c:out value='${info.message}'/></p>
				</c:if>
				<!--[if IE]> <div style="width: 100%;"> <![endif]-->
				<table class="tablelist">
					<tr> 
						<th>Documento</th>
						<th>Estado</th>
						<th>Descripci&oacute;n</th>
						<th>M&eacute;todo de Env&iacute;o</th>
						<th>Bultos</th>
						<th>Fecha Envío</th>
					</tr>
					<tr>
						<td><c:out value='${shipment.documentNo}'/></td>
						<td><c:out value='${shipment.docStatusName}'/></td>
						<td><c:out value='${shipment.description}'/></td>
						<td>
							<c:choose>
								<c:when test='${shipment.deliveryViaRule=="P"}'>Recogida por el socio</c:when>
								<c:when test='${shipment.deliveryViaRule=="S"}'>Env&iacute;o por transporte</c:when>
								<c:when test='${shipment.deliveryViaRule=="D"}'>Entrega por nuestros medios</c:when>
								<c:otherwise><c:out value='${shipment.deliveryViaRule}'/>M&eacute;todos de entrega alternativos</c:otherwise>
							</c:choose>
					  </td>
						<td><fmt:formatNumber value='${shipment.noPackages}'/></td>
						<td><fmt:formatDate value='${shipment.movementDate}'/></td> 
					</tr>
				</table>
				<h3>Lineas del albar&aacute;n de env&iacute;o</h3>
				<table class="tablelist">
					<tr> 
						<th>L&iacute;nea</th>
						<th>Nombre</th>
						<th>Descripci&oacute;n</th>
						<th>UPC/EAN</th>
						<th>Cantidad enviada</th>
						<th>Cantidad pedida</th>
					</tr>
					<c:forEach items='${shipment.lines}' var='line' varStatus='status'>
						<c:set var='producto' value='${line.product}'/> 
						<c:choose>
							<c:when test="${status.count % 2 == 0}"><tr class="row1"></c:when>
							<c:otherwise><tr class="row0"></c:otherwise>
						</c:choose>
							<td><c:out value='${line.line}'/></td>
							<td><c:out value='${producto.name}'/></td>
							<td><c:out value='${producto.description}'/></td>
							<td><c:out value='${producto.UPC}'/></td>
							<td><fmt:formatNumber value='${line.movementQty}'/></td>
							<td><fmt:formatNumber value='${line.qtyEntered}'/></td>
						</tr>
					</c:forEach> 
				</table>
				<!--[if IE]> </div> <![endif]-->			
			</div>
			<div id="footer"><%@ include file="/WEB-INF/jspf/footer.jspf" %></div>
		</div>
	</body>
</html>