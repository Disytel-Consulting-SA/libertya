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
	<c:redirect url='loginServlet?ForwardTo=proposalInfo.jsp'/>
</c:if>
<c:if test='${empty payment}'>
	<c:redirect url='index.jsp'/>
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
				<c:if test='${not empty webOrder}'>
					<h1>Su presupuesto ha sido procesado correctamente</h1>
					<!--[if IE]> <div style="width: 100%;"> <![endif]-->
					<table class="tablelist">
						<tr> 
							<th align="right">Documento</th>
							<th align="center">L&iacute;neas</th>
							<th align="right">Total L&iacute;neas</th>
							<th align="right">Portes</th>
							<th align="right">Impuestos</th>
							<th align="right">Total Global</th>
						</tr>
						<tr> 
							<td align="right"><c:out value='${webOrder.documentNo}'/></td>
							<td align="center"><fmt:formatNumber value='${webOrder.cuentaLineas}'/></td>
							<td align="right"><fmt:formatNumber value='${webOrder.totalLines}' type="currency" currencySymbol=""/> &euro;</td>
							<td align="right"><fmt:formatNumber value='${webOrder.freightAmt}' type="currency" currencySymbol=""/> &euro;</td>
							<td align="right"><fmt:formatNumber value='${webOrder.taxAmt}' type="currency" currencySymbol=""/> &euro;</td>
							<td align="right"><fmt:formatNumber value='${webOrder.grandTotal}' type="currency" currencySymbol=""/> &euro;</td>
						</tr>
					</table>
					<!--[if IE]> </div> <![endif]-->
					<p>Para ver los detalles del mismo, haga <a href="proposalDetails.jsp?C_Order_ID=<c:out value='${webOrder.c_Order_ID}'/>">click aqu&iacute;</a>.</p>
					<p>Para volver al listado completo de presupuestos, haga <a href="proposals.jsp">click aqu&iacute;</a>.</p>
				</c:if>
				<c:if test='${empty webOrder}'>
					<h1>Ha ocurrido un error mientras se procesaba su pedido</h1>
					<p class="warning">No existe el presupuesto a procesar. Trate de introducir de nuevo su presupuesto.</p>
				</c:if>			
			</div>
			<div id="footer"><%@ include file="/WEB-INF/jspf/footer.jspf" %></div>
		</div>
	</body>
</html>