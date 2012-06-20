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
	<c:redirect url='loginServlet?ForwardTo=invoices.jsp'/>
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
				<h1>Facturas y comisiones</h1>
				<c:if test='${not empty info.info}'>
					<p><strong><c:out value='${info.message}'/></strong></p>
				</c:if>
				<!--[if IE]> <div style="width: 100%;"> <![endif]-->
				<table class="tablelist">
					<tr> 
						<th align="right">Documento</th>
						<th align="left">Descripci&oacute;n</th>
						<th align="center">Fecha</th>
						<th align="right">Total l&iacute;neas</th>
						<th align="right">Total global</th>
						<th align="center">Fichero</th>
						<th align="center">Estado</th>
					</tr>
					<c:forEach items='${info.commissionedInvoices}' var='invoice'> 
						<tr> 
							<td align="right"><a href="invoiceLines.jsp?C_Invoice_ID=<c:out value='${invoice.c_Invoice_ID}'/>"><c:out value='${invoice.documentNo}'/></a></td>
							<td align="left"><c:out value='${invoice.description}'/></td>
							<td align="center"><fmt:formatDate value='${invoice.dateInvoiced}'/></td>
							<td align="right"><fmt:formatNumber value='${invoice.totalLines}' type="currency" currencySymbol=""/></td>
							<td align="right"><c:out value='${invoice.currencyISO}'/><fmt:formatNumber value='${invoice.grandTotal}' type="currency" currencySymbol=""/></td>
							<td align="center"><a href="invoiceServlet/I_<c:out value='${invoice.documentNo}'/>.pdf?Invoice_ID=<c:out value='${invoice.c_Invoice_ID}'/>" target="_blank"><img src="pdf.png" width="16" height="16" align="absmiddle" alt="Obtener fichero en formato electr&oacute;nico" title="Obtener fichero en formato electr&oacute;nico" border="0"></a></td>
							<td align="center">
								<c:if test='${invoice.paid}'>
									<span class="correct">Pagado</span>
								</c:if>
								<c:if test='${not invoice.paid}'>
									<span class="warning">No pagado</span>
								</c:if>
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
