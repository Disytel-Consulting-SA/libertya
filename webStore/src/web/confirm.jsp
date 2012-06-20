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
	<c:redirect url='loginServlet'/>
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
				<h1>Confirmaci&oacute;n de pagos</h1>
				<!--[if IE]> <div style="width: 100%;"> <![endif]-->
				<table class="tablelist">
					<tr>
						<th align="right">Pagos</th>
						<th align="center">Factura</th>
						<th align="left">Detalles</th>
						<th align="right">Total</th>
					</tr>
					<tr> 
						<td align="right"><c:out value='${payment.documentNo}'/></td>
						<td align="center">
							<c:choose>
								<c:when test='${not empty webOrder}'>
									<a href="invoiceServlet/I_<c:out value='${webOrder.invoiceInfo}'/>.pdf?Invoice_ID=<c:out value='${webOrder.invoice_ID}'/>" target="_blank"><img src="pdf.png" width="16" height="16" align="absmiddle" alt="Obtener factura en formato electr&oacute;nico" title="Obtener factura en formato electr&oacute;nico" border="0"></a> 
									<c:out value='${webOrder.invoiceInfo}'/>
							  </c:when>  
								<c:otherwise>
									&nbsp;
								</c:otherwise>  
							</c:choose>
						</td>
						<td align="left"><c:out value='${payment.r_AuthCode}'/></td>
						<td align="right"><c:out value='${payment.currencyISO}'/> <fmt:formatNumber value='${payment.payAmt}' type="currency" currencySymbol=""/></td>
					</tr>
				</table>
				<!--[if IE]> </div> <![endif]-->
				<form action="assets.jsp" method="post" enctype="application/x-www-form-urlencoded" name="confirm" id="confirm">
					Si el documento lo permite, por favor compruebe sus <input type="submit" name="Submit" value="activos" class="button"> para informaci&oacute;n para bajarse el fichero correspondiente. 
				</form>
				
				<!-- Remove Info -->
				<c:remove var='payment' />
				<c:remove var='webOrder' />
				
				<!--[if IE]> <div style="width: 100%;"> <![endif]-->
				<table class="tablelist">
					<tr> 
						<th align="left">&Aacute;reas de inter&eacute;s</th>
						<th align="left">Descripci&oacute;n</th>
						<th align="center">Suscripci&oacute;n</th>
					</tr>
					<c:forEach items='${info.interests}' var='interest'> 
						<tr> 
							<td align="left"><c:out value='${interest.name}'/></td>
							<td align="left"><c:out value='${interest.description}'/></td>
							<td align="center">
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