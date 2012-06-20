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
				<h1>Partner Info</h1>
				<c:if test='${not empty info.info}'>
					<p><strong><c:out value='${info.message}'/></strong></p>
				</c:if>
				<p>Please check with our partners:</p>
				
				<!-- Start Copy HERE		-->
				<p>
				<c:forEach items='${info.allAds}' var='ad'>
					<c:out value='${ad.description}'/>: <a href="#<c:out value='${ad.salesRep_ID}'/>"><c:out value='${ad.name}'/></a><br>
				</c:forEach>
				</p>
				
				<!-- Start Ads HERE		-->
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<c:forEach items='${info.allAds}' var='ad'> 
					<tr> 
						<td> 
							<a name="<c:out value='${ad.salesRep_ID}'/>"></a>
							<a href="http://www.openXpertya.org/wstore/click?<c:out value='${ad.clickTargetURL}'/>" target="_blank"> 
							<img src="<c:out value='${ad.imageURL}'/>" alt="<c:out value='${ad.name}'/>" border="0" align="left"></a> 
							<img src="<c:out value='${ad.webParam2}'/>" alt="<c:out value='${ad.webParam1}'/>" border="0" align="right"> 
							&nbsp; <strong><c:out value='${ad.description}'/></strong>
							<br>
							&nbsp; <a href="http://www.openXpertya.org/wstore/request.jsp?SalesRep_ID=<c:out value='${ad.salesRep_ID}'/>">Contact</a>
							<br>
							&nbsp; <a href="http://www.openXpertya.org/wstore/basketServlet?M_Product_ID=1000018&SalesRep_ID=<c:out value='${ad.salesRep_ID}'/>">Buy Next Step</a>
							<br>
							&nbsp; <em><c:out value='${ad.webParam3}' escapeXml='false'/></em>
							<p><c:out value='${ad.adText}' escapeXml='false'/></p>
							<p><c:out value='${ad.webParam4}' escapeXml='false'/></p>
						</td>
					</tr>
				</c:forEach> 
				</table>
				<!-- End Copy HERE		-->			
			</div>
			<div id="footer"><%@ include file="/WEB-INF/jspf/footer.jspf" %></div>
		</div>
	</body>
</html>