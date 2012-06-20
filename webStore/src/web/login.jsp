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
				<h1>Informaci&oacute;n del usuario</h1>
				<form action="loginServlet" method="post" enctype="application/x-www-form-urlencoded" name="Login" target="_top" onSubmit="checkForm(this, new Array ('EMail','Password'));">
					<input name="AD_Client_ID" type="hidden" value='<c:out value="${initParam.#AD_Client_ID}" default="0"/>'/>
					<input name="Source" type="hidden" value=""/>
					<input name="Info" type="hidden" value=""/>
					<input name="Mode" type="hidden" value=""/>
					
					<script language="Javascript">
						document.Login.Source.value=document.referrer;
						document.Login.Info.value=document.lastModified;
					</script>
				
					<fieldset>
						<legend>Usuario</legend>
						<label for="EMail" class="labelrequired">Correo electr&oacute;nico</label>
						<input type="text" name="EMail" id="ID_EMail" value="<c:out value='${webUser.email}'/>" class="mandatory">
						<input type="checkbox" name="validated" value="validated" id="validated" checked="<c:if test='${webUser.EMailVerified}'>checked</c:if>" disabled="disabled">
						<label for="validated" class="labelfix">Direcci&oacute;n validada</label>
						<br />
						<label for="Password" class="labelrequired">Contrase&ntilde;a</label>
						<input type="password" name="Password" id="ID_Password" value="" class="mandatory">
						<br />
						<label for="Login"></label>
						<input type="submit" name="Login" id="Login" value="Acceder" onClick="document.Login.Mode.value='Login';" class="button">  
						<c:if test="${not empty webUser.passwordMessage}"> 
							<input type="submit" name="SendEMail" id="SendEMail" value="Enviar contrase&ntilde;a" onClick="document.Login.Mode.value='SendEMail';" class="button">
						</c:if>
						<c:if test="${not empty webUser && webUser.contactID != 0 || webUser.loggedIn}">
							<br />
							<label for="PasswordNew" class="labelrequired">Confirmar contrase&ntilde;a</label>
							<input type="password" name="PasswordNew" id="ID_PasswordNew" value="" class="mandatory">
							<br />
							<label for="Name" class="labelrequired">Nombre</label>
							<input type="text" name="Name" id="ID_Name" value="<c:out value='${webUser.name}'/>" class="mandatory">
							<br />
							<label for="Company">Compa&ntilde;&iacute;a</label>
							<input type="text" name="Company" id="ID_Company" value="<c:out value='${webUser.company}'/>" class="textfield">
							<br />
							<label for="Title">T&iacute;tulo</label>
							<input type="text" name="Title" id="ID_Title" value="<c:out value='${webUser.title}'/>" class="textfield">
							<br />
							<label for="Address" class="labelrequired">Direcci&oacute;n 1 </label>
							<input type="text" name="Address" id="ID_Address" value="<c:out value='${webUser.address}'/>" class="mandatory">
							<br />
							<label for="Address2">Direcci&oacute;n 2 </label>
							<input type="text" name="Address2" id="ID_Address2" value="<c:out value='${webUser.address2}'/>" class="textfield">
							<br />
							<cws:location countryID='${webUser.countryID}' regionID='${webUser.regionID}' regionName='${webUser.regionName}' city='${webUser.city}' postal='${webUser.postal}' />
							<label for="Phone">Tel&eacute;fono</label>
							<input type="text" name="Phone" id="ID_Phone" value="<c:out value='${webUser.phone}'/>" class="textfield">
							<br />
							<label for="Fax">Fax</label>
							<input type="text" name="Fax" id="ID_Fax" value="<c:out value='${webUser.fax}'/>" class="textfield">
							<br />
							<label for="AddressConfirm"></label>
							<input type="hidden" name="AddressConfirm" id="AddressConfirm" value="N">
							<input type="reset" name="Reset" id="Reset" value="Borrar" class="button">
							<input type="submit" name="Submit" id="Submit" value="Enviar" onClick="document.Login.Mode.value='Submit';checkForm(this, new Array ('EMail','Password','PasswordNew','Name','Address'));" class="button">
							<span class="warning"><c:out value="${webUser.saveErrorMessage}"/></span>
						</c:if>
					</fieldset>
				</form>
				<p align="center"><span class="mini">Por favor introduzca todos los datos <strong>obligatorios</strong>.</span></p>			
			</div>
			<div id="footer"><%@ include file="/WEB-INF/jspf/footer.jspf" %></div>
		</div>
	</body>
</html>