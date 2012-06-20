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
	<c:redirect url='loginServlet?ForwardTo=payments.jsp'/>
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
				<h1>Pagos realizados</h1>
				<c:if test='${not empty info.info}'>
					<p><strong><c:out value='${info.message}'/></strong></p>
				</c:if>
				<!--[if IE]> <div style="width: 100%;"> <![endif]-->
				<table class="tablelist">
					<tr> 
						<th>Documento</th>
						<th>Estado</th>
						<th>Factura</th>
						<th>Fecha</th>
						<th>Cantidad</th>
						<th>Detalles</th>
					</tr>
				<c:forEach items='${info.payments}' var='payment' varStatus='status'>
				<c:choose><c:when test="${status.count % 2 == 0}"><tr class="row1"></c:when>
				<c:otherwise><tr class="row0"></c:otherwise></c:choose>    
						<td><c:out value='${payment.documentNo}'/></td>
						<td><c:out value='${payment.docStatusName}'/></td>
						<td>
							<c:choose>
								<c:when test='${empty payment.c_Invoice_ID || payment.c_Invoice_ID==0}'>Sin asignaci&oacute;n</c:when>
								<c:otherwise><a href="invoiceLines.jsp?C_Invoice_ID=<c:out value='${payment.c_Invoice_ID}'/>"><c:out value='${payment.c_Invoice_ID}'/></c:otherwise>
							</c:choose>
						</td>
						<td><fmt:formatDate value='${payment.dateTrx}'/></td>
						<td><fmt:formatNumber value='${payment.payAmt}' type="currency" currencySymbol=""/> &euro;</td>
						<td><c:out value='${payment.description}'/></td>
					</tr>
				</c:forEach> 
				</table>
				<!--[if IE]> </div> <![endif]-->
			</div>
			<div id="footer"><%@ include file="/WEB-INF/jspf/footer.jspf" %></div>
		</div>
	</body>
</html>