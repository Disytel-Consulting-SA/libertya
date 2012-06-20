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
				<h1>Cesta de la compra </h1>
				<c:if test='${empty webBasket}'> 
					<p>Cesta vac&iacute;a (tiempo m&aacute;ximo para la transacci&oacute;n finalizado)</p> 
					<p><em>Por favor, vuelva atr&aacute;s, refresque la p&aacute;gina y a&ntilde;ada productos de nuevo.</em></p>
				</c:if>
				<c:if test='${not empty webBasket}'>
					<form action="basketServlet" method="post" enctype="application/x-www-form-urlencoded" name="basket" id="basket" >
					<c:set var="Cantidad" value="0" />
					<!--[if IE]> <div style="width: 100%;"> <![endif]-->
					<table class="tablelist">
						<tr> 
							<th align="left">Producto</th>
							<th align="right">Precio</th>
							<th align="center">Cantidad</th>
							<th align="right">Total</th>
							<th align="center">Acci&oacute;n</th>
						</tr>
							<c:forEach items='${webBasket.lines}' var='line' varStatus='status'>
								<c:set var="Cantidad" value='${Cantidad + line.quantity}' /> 
								<c:choose>
									<c:when test="${status.count % 2 == 0}"><tr class="row1"></c:when>
									<c:otherwise><tr class="row0"></c:otherwise>
								</c:choose>  
								<td align="left"><c:out value='${line.name}'/></td>
								<td align="right"><fmt:formatNumber value='${line.price}' type="currency" currencySymbol=""/> &euro;</td>
								<td align="center"><fmt:formatNumber value='${line.quantity}'/></td>
								<td align="right"><fmt:formatNumber value='${line.total}' type="currency" currencySymbol=""/> &euro;</td>
								<td align="center"><input type="submit" name="Delete_<c:out value='${line.line}'/>" value="Borrar l&iacute;nea" class="button"></td>
							</tr>
							</c:forEach> 
						<tr> 
							<th>L&iacute;neas: <c:out value='${webBasket.lineCount}'/></th>
							<th>Productos:</th>
							<th><fmt:formatNumber value='${Cantidad}'/></th>
							<th><fmt:formatNumber value='${webBasket.total}' type="currency" currencySymbol=""/> &euro;</th>
							<th><input name="Checkout" id="Checkout" value="Confirmar pedido" onClick="window.top.location.replace('<c:out value='https://${ctx.context}/'/>checkOutServlet');" type="button" class="button"></th>
						</tr>
					</table>
					<!--[if IE]> </div> <![endif]-->
					</form>
				</c:if>
				<p><input name="Back" type="button" id="Back" value="Vuelta a seleccionar m&aacute;s productos" onClick="window.top.location.replace('index.jsp');" class="button"></p>			
			</div>
			<div id="footer"><%@ include file="/WEB-INF/jspf/footer.jspf" %></div>
		</div>
	</body>
</html>