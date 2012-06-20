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
				<h1>Ayuda</h1>
				<h3>Nuevos usuarios</h3>
				<p>Si a&uacute;n no dispone de una cuenta para el acceso al sistema openXpertya de compra en tiempo real, puede crear su nueva cuenta cuando haga su primer pedido o pulsando el bot&oacute;n de <strong>Login</strong> o en el enlace de Bienvenida. Tras entrar su direcci&oacute;n de correo electr&oacute;nico y una contrase&ntilde;a se muestra el di&aacute;logo de creaci&oacute;n de nuevos usuarios. Aqu&iacute; debe introducir su contrase&ntilde;a dos veces y la otra informaci&oacute;n opcional y obligatoria. Con posterioridad, nuestro personal de asistencia deber&aacute; asociar esa cuenta de compra en la tienda en l&iacute;nea con su cuenta habitual de socio. El primer pedido realizado, puede verse demorado en tanto este proceso no sea completado por nuestro personal y no le sea asignada la cuenta creada a su cuenta habitual de socio.</p>
				<h3>C&oacute;mo cambiar la contrase&ntilde;a, nombres, direcciones, etc.</h3>
				<p>Para cambiar su contrase&ntilde;a, entre con la contrase&ntilde;a actual que est&aacute; utilizando. Tras haber entrado, haga click en el enlace de Bienvenida para obtener la pantalla de <strong>Actualizaci&oacute;n de datos de usuario</strong>. Introduzca su contrase&ntilde;a vieja y la nueva y actualice el resto de la informaci&oacute;n si es necesario. Si no recordase en alg&uacute;n momento su contrase&ntilde;a, el sistema openXpertya le enviar&aacute; la contrase&ntilde;a actual a su cuenta de correo electr&oacute;nico. Le aconsejamos que la cambie tan pronto como pueda entrar de nuevo en el sistema.</p>
				<h3>Problemas con las formas de pago o estado de cr&eacute;dito</h3>
				<p>Si su pedido no ha sido aprobado con su forma de pago por defecto, puede ir a su pantalla de pedidos y vaciar o disminuir el pedido correspondiente. Si lo desea, puede enviarnos tambi&eacute;n la informaci&oacute;n necesaria para procesar este pedido bajo otra forma de pago o solicitar una ampliaci&oacute;n de su l&iacute;nea de cr&eacute;dito. Esto &uacute;ltimo puede demorar la aprobaci&oacute;n de los pedidos en curso que sobrepasen el l&iacute;mite de cr&eacute;dito concedido, hasta en tanto sea estudiada su solicitud.</p>
				<h3>Otros problemas, dudas o posibilidades</h3>
				<p><strong>openXpertya</strong> es una apliaci&oacute;n inteligente de gesti&oacute;n empresarial global que se ajusta gradualmente a su conducta y constumbres. Con el tiempo aprender&aacute; a utilizarlo, no solo para la realizaci&oacute;n de pedidos, sino para consultar todo su historial de facturas y env&iacute;os. No obstante, si en el transcurso del uso de la aplicaci&oacute;n no entiende alg&uacute;n punto o tiene alguna otra duda relacionada con sus posibilidades o capacidades, no dude en consultar directamente o solicitar informaci&oacute;n detallada al respecto. Para esto debe de ser lo m&aacute;s expl&iacute;cito posible en sus dudas o problemas. Una pregunta concreta y correctamente realizada a menudo obtiene una respuesta correcta y apropiada, pero una pregunta abstracta o poco detallada podr&iacute;a obtener respuestas vagas que no solucionar&iacute;an sus dudas. Incluiremos en la ayuda en l&iacute;nea todas aquellas preguntas y respuestas que consideremos interesantes para la soluci&oacute;n de las dudas o problemas de la colectividad de socios.</p>
				<p>Tambi&eacute;n existe la posibilidad de obtener la formaci&oacute;n personalizada necesaria para la correcta utilizaci&oacute;n del sistema en fechas concretas. Consulte con su comercial habitual si considera que esto puede ser necesario.</p>			
			</div>
			<div id="footer"><%@ include file="/WEB-INF/jspf/footer.jspf" %></div>
		</div>
	</body>
</html>