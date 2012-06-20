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
	<c:redirect url='loginServlet?ForwardTo=advertisements.jsp'/>
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
				<h1>Mis anuncios</h1>
				<c:if test='${not empty info.info}'>
					<p><strong><c:out value='${info.message}'/></strong></p>
				</c:if>
				<!--[if IE]> <div style="width: 100%;"> <![endif]-->
				<table class="tablelist">
					<c:forEach items='${info.advertisements}' var='ad'> 
						<tr>
							<td colspan="2">
								<a href="<c:out value='${ad.clickTargetURL}'/>" target="_blank"> 
								<img src="<c:out value='${ad.imageURL}'/>" alt="&lt;c:out value='${ad.name}'/&gt;" border="0" align="left"></a> 
								<img src="<c:out value='${ad.webParam2}'/>" alt="&lt;c:out value='${ad.webParam1}'/&gt;" border="0" align="right">
								<strong>
							  <c:out value='${ad.description}'/></strong><br>
								<a href="request.jsp&SalesRep_ID=<c:out value='${ad.salesRep_ID}'/>">Contacto</a><br>
								<a href="basketServlet?M_Product_ID=1000018&SalesRep_ID=<c:out value='${ad.salesRep_ID}'/>">Compre el siguiente paso</a><br>
								<em><c:out value='${ad.webParam3}'/></em>
								<p><c:out value='${ad.adText}' escapeXml='false'/></p>
								<c:out value='${ad.webParam4}'/>
						  </td>
						</tr>
						<form action="advertisementServlet" method="post" enctype="application/x-www-form-urlencoded" name="advertisement" target="_top" id="advertisement">
						<tr> 
							<th align="left">Nombre</th>
							<td><input name="Name" type="text" value="<c:out value='${ad.name}'/>" class="textfield"></td>
						</tr>
						<tr> 
							<th align="left">Descripci&oacute;n</th>
							<td><input name="Description" type="text" value="<c:out value='${ad.description}'/>" class="textfield"></td>
						</tr>
						<tr> 
							<th align="left">URL de la imagen</th>
							<td><input name="ImageURL" type="text" value="<c:out value='${ad.imageURL}'/>" class="textfield"></td>
						</tr>
						<tr> 
							<th align="left">Texto</th>
							<td><textarea name="AdText" cols="80" rows="8" id="AdText" class="textfield"><c:out value='${ad.imageURL}'/></textarea></td>
						</tr>
						<tr> 
							<th align="left">Destino al pulsar</th>
							<td><input name="ClickTargetURL" type="text" value="<c:out value='${ad.clickTargetURL}'/>" class="textfield"></td>
						</tr>
						<tr>
							<th>&nbsp;</th>
							<td>
							<input name="W_Advertisement_ID" type="hidden" id="W_Advertisement_ID" value="<c:out value='${ad.w_Advertisement_ID}'/>">
							<input name="Reset" type="reset" id="Reset" value="Borrar" class="button">
							<input type="submit" name="Submit" value="Enviar" class="button">
							</td>
						</tr>
						<tr> 
							<td colspan="2"><hr></td>
						</tr>
						</form>
						<c:forEach items='${pair}' var='ad.clickCountWeek'> 
							<tr> 
								<th><c:out value='${pair.key}'/></th>
								<td><c:out value='${pair.name}'/></td>
							</tr>
						</c:forEach> 
					</c:forEach>
				</table>
				<!--[if IE]> </div> <![endif]-->		
			</div>
			<div id="footer"><%@ include file="/WEB-INF/jspf/footer.jspf" %></div>
		</div>
	</body>
</html>