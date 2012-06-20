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
	<c:redirect url='loginServlet?ForwardTo=proposals.jsp'/>
</c:if>

<html>
	<head>
		<title><c:out value='${ctx.name}'/></title>
		<%@ include file="/WEB-INF/jspf/head.jspf" %>
	</head>
	<body onLoad="initClock()">
		<!-- Set Order ID and get Invoice		-->
		<c:set target='${info}' property='id' value='${param.C_Order_ID}' />
		<c:set var='order' value='${info.order}' />
		<c:if test='${empty order}'>
			<c:set target='${info}' property='message' value='Pedido no encontrado' />
			<c:redirect url='proposals.jsp'/>
		</c:if>
		<div id="wrap">
			<div id="header"><%@ include file="/WEB-INF/jspf/header.jspf" %></div>
			<div id="left"><%@ include file="/WEB-INF/jspf/left.jspf" %></div>
			<div id="right"><%@ include file="/WEB-INF/jspf/right.jspf" %></div>
			<div id="center">
				<h1>Detalles del presupuesto
				  <c:out value='${order.documentNo}'/></h1>
				<c:if test='${not empty info.info}'>
					<p><strong><c:out value='${info.message}'/></strong></p>
				</c:if>
				<!--[if IE]> <div style="width: 100%;"> <![endif]-->
				<table class="tablelist">
					<tr> 
						<th>Documento</th>
						<th>Descripci&oacute;n</th>
						<th>Estado</th>
						<th>Fecha</th>
						<th>Total l&iacute;neas</th>
						<th>Total global</th>
						<th>Acci&oacute;n</th>
					</tr>
					<tr>
						<td><c:out value='${order.documentNo}'/></td>
						<td><c:out value='${order.description}'/></td>
						<td><c:out value='${order.docStatusName}'/></td>
					  <td><fmt:formatDate value='${order.dateOrdered}'/></td>
						<td><fmt:formatNumber value='${order.totalLines}' type="currency" currencySymbol=""/> &euro;</td>
						<td><strong>
					  <fmt:formatNumber value='${order.grandTotal}' type="currency" currencySymbol=""/> &euro;</strong></td>
						<td> 
						<c:choose>
							<c:when test='${order.docStatus=="IP"}'>
								<input name="Void" id="Void" value="Anular" onClick="window.top.location.replace('<c:out value='https://${ctx.context}/'/>proposalServlet?C_Order_ID=<c:out value='${order.c_Order_ID}'/>&DocAction=VO');" type="button" class="button">
							</c:when>
							<c:when test='${order.docStatus=="WP"}'>
								Pendiente de autorizar: <fmt:formatNumber value='${order.grandTotal}' type="currency" currencySymbol=""/>&euro;
							</c:when>
							<c:when test='${order.docStatus=="VO"}'>Anulado</c:when>
							<c:when test='${order.docStatus=="CO"}'>Completado</c:when>
							<c:otherwise>Procesado</c:otherwise>
						</c:choose>
					  </td>
					</tr>
				</table>
				<h3>L&iacute;neas</h3>
				<table class="tablelist">
					<tr> 
						<th>L&iacute;nea</th>
						<th>Nombre</th>
						<th>Descripci&oacute;n</th>
						<th>Cantidad</th>
						<th>Precio</th>
						<th>Neto l&iacute;nea</th>
					</tr>
					<c:forEach items='${order.lines}' var='line' varStatus='status'>
						<c:choose>
							<c:when test="${status.count % 2 == 0}"><tr class="row1"></c:when>
							<c:otherwise><tr class="row0"></c:otherwise>
						</c:choose> 
						<td><c:out value='${line.line}'/></td>
						<td><c:out value='${line.name}'/></td>
						<td><c:out value='${line.descriptionText}'/></td>
						<td><fmt:formatNumber value='${line.qtyOrdered}' /></td>
						<td><fmt:formatNumber value='${line.priceActual}' type="currency" currencySymbol=""/> &euro;</td>
						<td><strong>
						  <fmt:formatNumber value='${line.lineNetAmt}' type="currency" currencySymbol=""/> &euro;</strong></td>
					</tr>
					</c:forEach> 
				</table>
				<!--[if IE]> </div> <![endif]-->			
			</div>
			<div id="footer"><%@ include file="/WEB-INF/jspf/footer.jspf" %></div>
		</div>
	</body>
</html> 
