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

<c:if test='${empty webUser || !webUser.loggedIn}'>
  <c:redirect url='loginServlet?ForwardTo=request.jsp&SalesRep_ID=${param.SalesRep_ID}'/> 
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
				<h1>Nuevo mensaje</h1>
				<form method="post" name="Request" action="requestServlet" enctype="application/x-www-form-urlencoded" onSubmit="checkForm(this, new Array ('Summary'));">
				<input name="Source" type="hidden" value=""/>
				<input name="Info" type="hidden" value=""/>
				
				<script language="Javascript">
					document.Request.Source.value=document.referrer;
					document.Request.Info.value=document.lastModified;
				</script>
				
				<input name="ForwardTo" type="hidden" value="<c:out value='${param.ForwardTo}'/>"/>
				
				<c:if test='${not empty param.SalesRep_ID}'>
					<input name="SalesRep_ID" type="hidden" value="<c:out value='${param.SalesRep_ID}'/>"/>
				</c:if>
			
				<c:if test='${empty param.SalesRep_ID}'>
					<input name="SalesRep_ID" type="hidden" value="<c:out value='${webUser.salesRep_ID}'/>"/>
				</c:if>
				
				<table width="80%" border="0" align="center" cellpadding="5" cellspacing="0">
					<tr> 
						<td align="right" width="50%">De</td>
						<td align="left" width="50%"><c:out value='${webUser.email}'/></td>
					</tr>
					<tr> 
						<td align="right"><label id="ID_RequestType" for="RequestType">Tipo de petici&oacute;n</label></td>
						<td align="left"><cws:requestType/></td>
					</tr>
					<tr> 
						<td><label id="ID_Summary" for="Summary">Pregunta/Cuesti&oacute;n/Petici&oacute;n:</label></td>
						<td><input name="Confidential" type="checkbox" id="Confidential" value="Confidential"> Informaci&oacute;n confidencial</td>
					</tr>
					<tr> 
						<td colspan="2" align="left"><textarea name="Summary" cols="80" rows="8" id="ID_Summary" class="textfield"></textarea></td>
					</tr>
					<tr> 
						<td colspan="2" align="center"><span class="mini">1500 caracteres m&aacute;ximo. A&ntilde;ada los archivos adjuntos despu&eacute;s de enviar.</span></td>
					</tr>
					<tr> 
					  <td align="right"> <input name="Reset" type="reset" value="Borrar" class="button" /></td>
						<td> <input name="Submit" type="submit" value="Enviar" class="button"> </td>
					</tr>
				</table>
				</form>
			</div>
			<div id="footer"><%@ include file="/WEB-INF/jspf/footer.jspf" %></div>
		</div>
	</body>
</html>