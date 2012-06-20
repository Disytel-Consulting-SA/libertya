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
	<c:redirect url='loginServlet?ForwardTo=rfqs.jsp'/>
</c:if>

<html>
	<head>
		<title><c:out value='${ctx.name}'/></title>
		<%@ include file="/WEB-INF/jspf/head.jspf" %>
	</head>
	<body onLoad="initClock()">
		<!-- Set RfQ ID and get RfQ		-->
		<c:set target='${info}' property='id' value='${param.C_RfQ_ID}' />
		<c:set var='rfqResponse' value='${info.rfQResponse}' />
		<c:if test='${empty rfqResponse}'>
			<c:set target='${info}' property='message' value='No RfQ Response' />
			<c:redirect url='rfqs.jsp'/>
		</c:if>
		<c:set var='rfq' value='${rfqResponse.rfQ}' />
		<c:if test='${empty rfq}'>
		  <c:set target='${info}' property='message' value='RfQ not found' />
		  <c:redirect url='rfqs.jsp'/>
		</c:if>
		<div id="wrap">
			<div id="header"><%@ include file="/WEB-INF/jspf/header.jspf" %></div>
			<div id="left"><%@ include file="/WEB-INF/jspf/left.jspf" %></div>
			<div id="right"><%@ include file="/WEB-INF/jspf/right.jspf" %></div>
			<div id="center">
				<h1>Los detalles de mi gesti&oacute;n del pedido <c:out value='${rfq.name}'/></h1>
				<c:if test='${not empty info.info}'>
					<p><strong><c:out value='${info.message}'/></strong></p>
				</c:if>
				<form action="rfqServlet" method="post" enctype="application/x-www-form-urlencoded" name="RfQResponse">
				<input name="C_RfQ_ID" type="hidden" value="<c:out value='${rfqResponse.c_RfQ_ID}'/>"/>
				<input name="C_RfQResponse_ID" type="hidden" value="<c:out value='${rfqResponse.c_RfQResponse_ID}'/>"/>
				<!--[if IE]> <div style="width: 100%;"> <![endif]-->
				<table class="tablelist">
					<tr> 
						<th>Nombre</th>
						<th>Descipci&oacute;n</th>
						<th>Detalles</th>
						<th>Respuesta</th>
						<th>Comienzo</th>
						<th>Entrega</th>
					</tr>
					<tr> 
						<td><c:out value='${rfq.name}'/></td>
						<td><c:out value='${rfq.description}'/></td>
						<td><c:out value='${rfq.help}'/> <c:if test='${rfq.pdfAttachment}'><br><a href="rfqServlet/RfQ_<c:out value='${rfq.c_RfQ_ID}'/>.pdf?C_RfQ_ID=<c:out value='${rfq.c_RfQ_ID}'/>" target="_blank"><img src="pdf.png" width="16" height="16" align="absmiddle" alt="Obtener fichero en formato electr&oacute;nico" title="Obtener fichero en formato electr&oacute;nico" border="0"></a></c:if></td>
						<td>Por <fmt:formatDate value='${rfq.dateResponse}' dateStyle='short'/></td>
						<td><fmt:formatDate value='${rfq.dateWorkStart}' dateStyle='short'/></td>
						<td><fmt:formatDate value='${rfq.dateWorkComplete}' dateStyle='short'/> - <c:out value='${rfq.deliveryDays}'/> d&iacute;as</td>
					</tr>
					<tr> 
						<td><input name="Name" type="text" id="Name" value="<c:out value='${rfqResponse.name}'/>" class="textfield"></td>
						<td><textarea name="Description" rows="3" id="Description" class="textfield"><c:out value='${rfqResponse.description}'/></textarea></td>
						<td><textarea name="Help" rows="3" id="Help" class="textfield"><c:out value='${rfqResponse.help}'/></textarea></td>
						<td><fmt:formatDate value='${rfqResponse.dateResponse}' dateStyle='short'/><br>Total: <input name="Price" type="text" id="Price" value="<fmt:formatNumber value='${rfqResponse.price}' type='currency' currencySymbol=''/>" size="15" class="textfield"></td>
						<td><input name="DateWorkStart" type="text" id="DateWorkStart" value="<fmt:formatDate value='${rfqResponse.dateWorkStart}' dateStyle='short'/>" size="12" class="textfield"></td>
						<td><input name="DateWorkComplete" type="text" id="DateWorkComplete" value="<fmt:formatDate value='${rfqResponse.dateWorkComplete}' dateStyle='short'/>" size="12" class="textfield"> - <input name="DeliveryDays" type="text" id="DeliveryDays" value="<c:out value='${rfqResponse.deliveryDays}'/>" size="4" maxlength="4" class="textfield"> d&iacute;as</td>
					</tr>
				</table>
				<h3>Lineas</h3>
				<table class="tablelist">
					<tr> 
						<th>#</th>
						<th>Producto</th>
						<th>Descripci&oacute;n</th>
						<th>Detalles</th>
						<th>Comienzo</th>
						<th>Entrega</th>
					</tr>
					<c:forEach items='${rfqResponse.lines}' var='line'> 
						<c:set var='rfqLine' value='${line.rfQLine}' />
						<tr> 
							<td><c:out value='${rfqLine.line}'/></td>
							<td><c:out value='${rfqLine.productDetailHTML}' escapeXml='false'/></td>
							<td><c:out value='${rfqLine.description}'/><br><textarea name="Description_<c:out value='${line.c_RfQResponseLine_ID}'/>" rows="3" id="Description"><c:out value='${line.description}'/></textarea></td>
							<td><c:out value='${rfqLine.help}'/><br><textarea name="Help_<c:out value='${line.c_RfQResponseLine_ID}'/>" rows="3" id="Help"><c:out value='${line.help}'/></textarea></td>
							<td><fmt:formatDate value='${rfqLine.dateWorkStart}' dateStyle='short'/><br><input name="DateWorkStart_<c:out value='${line.c_RfQResponseLine_ID}'/>" type="text" id="DateWorkStart" value="<fmt:formatDate value='${line.dateWorkStart}' dateStyle='short'/>" size="12"></td>
							<td><fmt:formatDate value='${rfqLine.dateWorkComplete}' dateStyle='short'/> - <c:out value='${rfqLine.deliveryDays}'/> d&iacute;as<br><input name="DateWorkComplete_<c:out value='${line.c_RfQResponseLine_ID}'/>" type="text" id="DateWorkComplete" value="<fmt:formatDate value='${line.dateWorkComplete}' dateStyle='short'/>" size="12"> - <input name="DeliveryDays_<c:out value='${line.c_RfQResponseLine_ID}'/>" type="text" id="DeliveryDays" value="<c:out value='${line.deliveryDays}'/>" size="4" maxlength="4"> d&iacute;as</td>
						</tr>
						<tr> 
							<td colspan="2">&nbsp;</td>
							<td colspan="4">
								<table class="tablelist">
								<tr>
									<th>Unidad</th>
									<th>Cantidad</th>
									<c:if test='${not rfq.quoteTotalAmtOnly}'>
										<th>Precio</th>
										<th>Descuento</th>
									</c:if>
								</tr>
								<c:forEach items='${line.qtys}' var='qty'> 
									<c:set var='rfqQty' value='${qty.rfQLineQty}'/>
									<tr>
										<td><c:out value='${rfqQty.uomName}'/></td>
										<td><c:out value='${rfqQty.qty}'/></td>
										<c:if test='${not rfq.quoteTotalAmtOnly}'>
											<td><input name="Price_<c:out value='${qty.c_RfQResponseLineQty_ID}'/>" type="text" id="Price" value="<fmt:formatNumber value='${qty.price}' type='currency' currencySymbol=''/>" size="15" class="textfield"></td>
											<td><input name="Discount_<c:out value='${qty.c_RfQResponseLineQty_ID}'/>" type="text" id="Discount" value="<c:out value='${qty.discount}'/>" size="15" class="textfield"></td>
										</c:if>
									</tr>
								</c:forEach>
							  </table>
							</td>
						</tr>
					</c:forEach> 
				</table>
				<!--[if IE]> </div> <![endif]-->
				<p><input type="checkbox" name="IsComplete" value="IsComplete" id="IsComplete"> <label for="IsComplete">Completado</label> <input name="Submit" type="submit" id="Submit" value="Enviar" class="button"></p>
				</form>			
			</div>
			<div id="footer"><%@ include file="/WEB-INF/jspf/footer.jspf" %></div>
		</div>
	</body>
</html>