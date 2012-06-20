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

<cws:priceList priceList_ID="0"/> 

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
				<h1>Condiciones generales de venta</h1>
				<h3>Aplicaci&oacute;n</h3>
				<p>Las presentes condiciones generales lo son &uacute;nicamente para la venta en l&iacute;nea por parte de EMPRESA., con domicilio en ........ CIF..... para su utilizaci&oacute;n por parte de sus socios. </p>
				<p>Las ventas en l&iacute;nea realizadas desde en esta p&aacute;gina web a trav&eacute;s de nuestro sistema de compra en tiempo real (sistema <strong>openXpertya</strong>) est&aacute;n sujetas a las presentes condiciones generales, que prevalecen sobre cualesquiera otras disposiciones particulares. Toda compra realizada por nuestros clientes implica la aceptaci&oacute;n plena y sin reservas de estas condiciones generales de venta. </p>
				<h3>Pedidos</h3>
				<p>Los pedidos introducidos en nuestra p&aacute;gina web se consideran en firme desde el momento de su validaci&oacute;n en l&iacute;nea por parte del comprador. El comprador declara conocer y aceptar las condiciones generales de venta por el hecho de confirmar su pedido en l&iacute;nea. </p>
				<h3>Precios</h3>
				<p>Los precios de venta de los productos son aquellos de la tarifa en vigor en el momento de la introducci&oacute;n del pedido, excluyendo los portes. La fecha de la &uacute;ltima modificaci&oacute;n de la tarifa a utilizar aparece siempre en la ventana de selecci&oacute;n de productos.<br>Las tarifas de precios de venta de los productos pueden ser modificados en cualquier momento, sin aviso previo, pasando a indicar el sistema la fecha de la nueva tarifa de precios. </p>
				<h3>Pago de los productos</h3>
				<p>Salvo acuerdo en contra, o solicitud del cliente, el pago de los productos se realiza de acuerdo con las caracter&iacute;sticas y formas de pago generales del cliente y del tipo de producto en cada caso.</p>
				<h3>Entregas y Devoluciones</h3>
				<p>La entrega de los productos se considera realizada desde su puesta a disposici&oacute;n del cliente. Es responsabilidad del cliente la comprobaci&oacute;n de la adecuaci&oacute;n de los productos servidos respecto del pedido efectuado. Cualquier reclamaci&oacute;n relativa a la naturaleza o especificaciones de los productos deber&aacute; realizarse como m&aacute;ximo en los ocho (8) d&iacute;as siguientes a la entrega. </p>      
				<p>Toda devoluci&oacute;n de productos deber&aacute; ser previamente acordada con nuestro departamento de posventa y siempre de acuerdo con las normas generales de aplicaci&oacute;n. En caso de ser aceptada dicha devoluci&oacute;n, el producto deber&aacute; ser remitido en los embalajes originales y a portes pagados. </p>
				<h3>Garant&iacute;as</h3>
				<p>Los productos vendidos est&aacute;n sujetos a las condiciones de garant&iacute;a de sus respectivos fabricantes. Los productos manipulados o reparados por el comprador o terceras personas est&aacute;n excluidos de estas garant&iacute;as. </p>
				<h3>Jurisdicci&oacute;n</h3>
				<p>Las presentes condiciones generales de venta se rigen por el derecho espa&ntilde;ol, y para cualquier conflicto las partes acuerdan regirse por los tribunales de Oviedo.</p>			
			</div>
			<div id="footer"><%@ include file="/WEB-INF/jspf/footer.jspf" %></div>
		</div>
	</body>
</html>