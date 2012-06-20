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
	<c:redirect url='loginServlet?ForwardTo=assets.jsp'/>
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
				<h1>Mis activos</h1>
				<c:if test='${not empty info.info}'>
					<p><strong><c:out value='${info.message}'/></strong></p>
				</c:if>
				<c:if test='${not webUser.EMailVerified}'>
					<form action="emailServlet" method="post" enctype="application/x-www-form-urlencoded" name="EMailVerification" target="_top">
					<input name="AD_Client_ID" type="hidden" value='<c:out value="${initParam.#AD_Client_ID}" default="0"/>'/>
					<input name="Source" type="hidden" value=""/>
					<input name="Info" type="hidden" value=""/>
					<input name="ForwardTo" type="hidden" value="assets.jsp"> 
					
					<script language="Javascript">
						document.EMailVerification.Source.value=document.referrer;
						document.EMailVerification.Info.value=document.lastModified;
					</script>
					
					<p><strong>Para acceder a sus activos:</strong></p>
					<p>Introduzca el c&oacute;digo de verificaci&oacute;n <input name="VerifyCode" type="text" id="VerifyCode" value="Introducir e-mail" class="textfield"> <input type="submit" name="Submit" value="Enviar" class="button"></p>
					<p>El código de verificaci&oacute;n ser&aacute; enviado a <strong><c:out value='${webUser.email}'/></strong> <input type="submit" name="ReSend" id="ReSend" value="Enviar código de verificaci&oacute;n" class="button"></p>
					<p><c:out value="${webUser.passwordMessage}"/></p>
					</form>
				</c:if>
				<!--[if IE]> <div style="width: 100%;"> <![endif]-->
				<table class="tablelist">
					<tr> 
						<th>Nombre</th>
						<th>Descripci&oacute;n</th>
						<th>Fecha l&iacute;mite</th>
						<th>Versi&oacute;n, N&uacute;mero de serie</th>
						<th>Env&iacute;o</th>
						<th>Archivo</th>
					</tr>
					<c:forEach items='${info.assets}' var='asset'> 
					<tr> 
						<td><c:out value='${asset.name}'/></td>
						<td><c:out value='${asset.description}'/></td>
						<td><fmt:formatDate value='${asset.guaranteeDate}'/></td>
						<td><c:out value='${asset.versionNo}'/><br><c:out value='${asset.serNo}'/></td>
						<td><c:out value='${asset.downloadName}'/></td>
						<td>
							<c:if test='${asset.downloadable}'>
								<a href="<c:out value='http://${ctx.context}/'/>assetServlet/<c:out value='${asset.downloadName}'/>.zip?Asset_ID=<c:out value='${asset.a_Asset_ID}'/>" target="_blank"><img src="download.png" width="16" height="16" align="absmiddle" alt="Descargar" title="Descargar <c:out value='${addlDL}'/>" border="0"></a></c:if>
							<c:if test='${not asset.downloadable}'>
								No disponible
							</c:if> 
						</td>
						<td>
						<c:if test='${asset.downloadable}'>
							<c:forEach items='${asset.downloadURLs}' var='addlDL'>
								<a href="<c:out value='http://${ctx.context}/'/>assetServlet/<c:out value='${addlDL}'/>.zip?Asset_ID=<c:out value='${asset.a_Asset_ID}'/>&PD=<c:out value='${addlDL}'/>" target="_blank"><img src="download.png" width="16" height="16" align="absmiddle" alt="Descargar" title="Descargar <c:out value='${addlDL}'/>" border="0"></a></c:forEach>
						</c:if>
						<c:if test='${not asset.downloadable}'>
							No disponible
						</c:if> 
						</td>
					</tr>
					</c:forEach>
				</table>
				<!--[if IE]> </div> <![endif]-->
				<c:if test='${webUser.creditCritical}'>
					<p><c:out value='${webUser.SOCreditStatus}'/></p>
				</c:if>
				<p><span class="mini">Para bajarse el archivo, pulse en el link o haga click con el bot&oacute;n derecho y seleccione &quot;Guardar destino como...&quot;</span></p>			
			</div>
			<div id="footer"><%@ include file="/WEB-INF/jspf/footer.jspf" %></div>
		</div>
	</body>
</html>
