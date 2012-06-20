<!--
  - openXpertya (r), Tienda en-línea, Copyright (c) 2003-2007 ConSerTi S.L.
  - este fichero esta licenciado bajo los términos de la Licencia Pública openXpertya (LPO)
  - con aplicación directa del ADDENDUM A, sección 6 (A.6) y posibilidad de relicenciamiento.
  - Licencia y más información en http://www.openxpertya.org/ayuda/Licencia.html
  - Información de la dirección de la tienda Web
  -->

<%@ page session="true" contentType="text/html; charset=UTF-8" errorPage="errorPage.jsp" %>
<%@ taglib uri="webStore.tld" prefix="cws" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<c:if test='${empty webUser || !webUser.loggedIn}'>
	<c:redirect url='loginServlet?ForwardTo=emailVerify.jsp'/>
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
				<h1>Verifique su cuenta de correo electr&oacute;nico </h1>
				<c:if test='${not empty info.info}'>
					<p><strong><c:out value='${info.message}'/></strong></p>
				</c:if>
				<c:if test='${not webUser.EMailVerified}'>
					<form action="emailServlet" method="post" enctype="application/x-www-form-urlencoded" name="EMailVerification" target="_top">
						<input name="AD_Client_ID" type="hidden" value='<c:out value="${initParam.#AD_Client_ID}" default="0"/>'/>
						<input name="Source" type="hidden" value=""/>
						<input name="Info" type="hidden" value=""/>
						<input name="ForwardTo" type="hidden" value="emailVerify.jsp">         	
						
						<script language="Javascript">
							document.EMailVerification.Source.value=document.referrer;
							document.EMailVerification.Info.value=document.lastModified;
						</script>
						
						<p>Introduzca c&oacute;digo de verificaci&oacute;n <input name="VerifyCode" type="text" id="VerifyCode" class="textfield"> <input type="submit" name="Submit" value="Enviar" class="button"></p>
						<p>El c&oacute;digo de verificaci&oacute;n ser&aacute; enviado <input type="submit" name="ReSend" id="ReSend" value="Enviar c&oacute;digo de verificaci&oacute;n" class="button"></p>
						<p><c:out value="${webUser.passwordMessage}"/></p>
					</form>
				</c:if>
				<c:if test='${webUser.EMailVerified}'>
					<p>Gracias, su cuenta de correo ha sido verificada.</p>
				</c:if>
				<c:if test='${webUser.creditCritical}'>
					<p class="warning">&iexcl;<c:out value='${webUser.SOCreditStatus}'/>!</p>
				</c:if>			
			</div>
			<div id="footer"><%@ include file="/WEB-INF/jspf/footer.jspf" %></div>
		</div>
	</body>
</html>