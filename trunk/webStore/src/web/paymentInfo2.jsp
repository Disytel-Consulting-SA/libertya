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
	<c:redirect url='loginServlet?ForwardTo=paymentInfo2.jsp'/>
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
					<h1>Gracias por realizar su presupuesto con nosotros</h1>
					<!--[if IE]> <div style="width: 100%;"> <![endif]-->
					<table class="tablelist">
						<tr> 
							<th>Presupuesto</th>
							<th>L&iacute;neas</th>
							<th>Total L&iacute;neas</th>
							<th>Portes</th>
							<th>Impuestos</th>
							<th>Total Global </th>
						</tr>
						<tr> 
							<td><a href="proposalDetails.jsp?C_Order_ID=<c:out value='${webOrder.c_Order_ID}'/>">
						  <c:out value='${webOrder.documentNo}'/></a></td>
							<td><fmt:formatNumber value='${webOrder.cuentaLineas}'/></td>
							<td><fmt:formatNumber value='${webOrder.totalLines}' type="currency" currencySymbol=""/> &euro;</td>
							<td><fmt:formatNumber value='${webOrder.freightAmt}' type="currency" currencySymbol=""/> &euro;</td>
							<td><fmt:formatNumber value='${webOrder.taxAmt}' type="currency" currencySymbol=""/> &euro;</td>
							<td><strong>
						  <fmt:formatNumber value='${webOrder.grandTotal}' type="currency" currencySymbol=""/> &euro;</strong></td>
						</tr>
					</table>
					<h3>Detalles del presupuesto realizado</h3>
					<table class="tablelist">
						<tr> 
							<th>M&eacute;todo de env&iacute;o</th>
							<th>Env&iacute;o</th>
							<th>Forma de Pago</th>
							<th>Estado</th>
							<th>Descripci&oacute;n</th>
						</tr>
						<tr> 
							<td>
								<c:choose>
									<c:when test='${webOrder.deliveryViaRule=="P"}'>Recogida por el socio</c:when>
									<c:when test='${webOrder.deliveryViaRule=="S"}'>Env&iacute;o por transporte</c:when>
									<c:when test='${webOrder.deliveryViaRule=="D"}'>Entrega por nuestros medios</c:when>
									<c:otherwise><c:out value='${webOrder.deliveryViaRule}'/>- M&eacute;todos de entrega alternativos</c:otherwise>
								</c:choose>
						  </td>
							<td> 
								<c:choose>
									<c:when test='${webOrder.deliveryRule=="A"}'>Seg&uacute;n disponibilidad</c:when>
									<c:when test='${webOrder.deliveryRule=="O"}'>S&oacute;lo si presupuesto completo</c:when>
									<c:when test='${webOrder.deliveryRule=="L"}'>Si l&iacute;nea completa</c:when>
									<c:when test='${webOrder.deliveryRule=="R"}'>Cuando se reciba</c:when>
									<c:when test='${webOrder.deliveryRule=="F"}'>Sacar lo que haya</c:when>
									<c:otherwise>Otras pol�icas de env&iacute;o</c:otherwise>
								</c:choose>
						  </td>
							<td>
								<c:choose>
									<c:when test='${webOrder.paymentRule=="P"}'>A cr&eacute;dito</c:when>
									<c:when test='${webOrder.paymentRule=="B"}'>En efectivo</c:when>
									<c:when test='${webOrder.paymentRule=="S"}'>Pago mediante cheque</c:when>
									<c:when test='${webOrder.paymentRule=="K"}'>Tarjeta de cr&eacute;dito</c:when>
									<c:when test='${webOrder.paymentRule=="D"}'>D&eacute;bito directo</c:when>
									<c:when test='${webOrder.paymentRule=="D"}'>Dep&oacute;sito directo. Prepago</c:when>
									<c:otherwise><c:out value='${webOrder.paymentRule}'/>Otros m&eacute;todos de pago</c:otherwise>
								</c:choose>
							</td>
							<td> 
								<c:choose>
									<c:when test='${order.docStatus=="IP"}'>
										<input name="Void" id="Void" value="Anular el presupuesto" onClick="window.top.location.replace('<c:out value='https://${ctx.context}/'/>proposalServlet?C_Order_ID=<c:out value='${webOrder.c_Order_ID}'/>&DocAction=VO');" type="button" class="button">
									</c:when>
									<c:when test='${webOrder.docStatus=="WP"}'>Pendiente de autorizar: <fmt:formatNumber value='${webOrder.grandTotal}' type="currency" currencySymbol=""/>&euro;</c:when>
									<c:when test='${webOrder.docStatus=="VO"}'>Anulado</c:when>
									<c:when test='${webOrder.docStatus=="CO"}'>Completado</c:when>
									<c:otherwise>En progreso</c:otherwise>
								</c:choose>
							</td>
							<td><c:out value='${webOrder.description}'/></td>
						</tr>
					</table>
					<!--[if IE]> </div> <![endif]-->
					<h1>Su presupuesto ha sido procesado correctamente</h1>
					<h2 align="center">Para ver los detalles del mismo, pulse sobre el n&uacute;mero de presupuesto.</h2>
				</c:if>
				<c:if test='${empty webOrder}'>
					<p class="warning">No existe el presupuesto a procesar. Trate de introducir de nuevo su presupuesto.</p>
				</c:if>			
			</div>
			<div id="footer"><%@ include file="/WEB-INF/jspf/footer.jspf" %></div>
		</div>
	</body>
</html>