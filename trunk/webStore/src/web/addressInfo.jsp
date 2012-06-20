<!--
  - openXpertya (r), Tienda en-l&#xed;nea, Copyright (c) 2003-2007 ConSerTi S.L.
  - este fichero esta licenciado bajo los t&#xe9;rminos de la Licencia P&#xfa;blica openXpertya (LPO)
  - con aplicaci&#xf3;n directa del ADDENDUM A, secci&#xf3;n 6 (A.6) y posibilidad de relicenciamiento.
  - Licencia y m&#xe1;s informaci&#xf3;n en http://www.openxpertya.org/ayuda/Licencia.html
  - Informaci&#xf3;n de la direcci&#xf3;n de la tienda Web
  -->

<%@ page session="true" contentType="text/html; charset=iso-8859-1" errorPage="errorPage.jsp" %>
<%@ taglib uri="webStore.tld" prefix="cws" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<html>
	<head>
		<title><c:out value='${ctx.name}'/></title>
		<%@ include file="/WEB-INF/jspf/head.jspf" %>
		<script language="Javascript" type="text/JavaScript">
			function processOrder() {
				document.getElementById("submitDiv").style.display="none";
				document.getElementById("processingDiv").style.display="inline";
				window.top.location.replace('orderServlet');
			}
		</script>
	</head>
	<body onLoad="initClock()">
		<div id="wrap">
			<div id="header"><%@ include file="/WEB-INF/jspf/header.jspf" %></div>
			<div id="left"><%@ include file="/WEB-INF/jspf/left.jspf" %></div>
			<div id="right"><%@ include file="/WEB-INF/jspf/right.jspf" %></div>
			<div id="center">
				<h1>Datos de env&iacute;o</h1>
				<p align="justify">El pedido ser&aacute; enviado a la siguiente direcci&oacute;n.</p>
				<p align="justify">Si la direcci&oacute;n no es correcta deber&aacute; ponerse en contacto con el responsable para modificarla. Si confirma el pedido, &eacute;ste ser&aacute; enviado a la siguiente direcci&oacute;n:</p>
				<fieldset>
					<legend>Datos de env&iacute;o</legend>
					<ul>
						<li>A la atenci&oacute;n de: <c:out value="${webUser.name}"/></li>
						<li>Empresa: <c:out value="${webUser.company}"/></li>
						<li>Direcci&oacute;n: <c:out value="${webUser.address}"/>
							<c:if test='${(not empty webUser.address2)}'>
								<br /><c:out value="${webUser.address2}"/>
							</c:if>
						</li>
						<li>Ciudad: <c:out value="${webUser.city}"/></li>
						<li>C&oacute;digo Postal: <c:out value="${webUser.postal}"/></li>
						<li>Regi&oacute;n: 
							<c:if test='${(not empty webUser.address2)}'><c:out value="${webUser.regionName}"/>, </c:if>
							<c:out value="${webUser.countryName}"/>
						</li>
						<li>Tel&eacute;fono: <c:out value="${webUser.phone}"/></li>
						<li>Fax: <c:out value="${webUser.fax}"/></li>
						<li>
							<c:choose>
								<c:when test='${(webUser.creditCritical)}'><span class="error">Estado de la cuenta: <c:out value="${webUser.estadoCredito}"/></span></c:when>
								<c:otherwise>Estado de la cuenta: <c:out value="${webUser.estadoCredito}"/></c:otherwise>
							</c:choose>
						</li>
					</ul>
					
					<input name="Volver" type="button" value="Volver" onClick="window.top.location.replace('basket.jsp');" class="button">
					<input name="Confirmar" type="button" value="Confirmar " onClick="processOrder();" class="button">
					
					<p align="center">
						<div id="submitDiv">Ahora debe confirmar la direcci&oacute;n y m&eacute;todo de env&iacute;o, o contactar con nosotros para modificarlos.</div>
						<div id="processingDiv"><c:out value="${webUser.saveErrorMessage}"/>Procesando, espere un momento...</div>
					</p>	
				</fieldset>	
			</div>
			<div id="footer"><%@ include file="/WEB-INF/jspf/footer.jspf" %></div>
		</div>
	</body>
</html>
