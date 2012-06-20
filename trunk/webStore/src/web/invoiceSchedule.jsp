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
	<c:redirect url='loginServlet?ForwardTo=vencimientos.jsp'/>
</c:if>

<html>
	<head>
		<title><c:out value='${ctx.name}'/></title>
		<%@ include file="/WEB-INF/jspf/head.jspf" %>
		<script language="JavaScript" src="CalendarPopup.js"></script>
		<script language="JavaScript">
			var cal = new CalendarPopup(); 	
			cal.showYearNavigation();
		</script>
	</head>
	<body onLoad="initClock()">
		<jsp:useBean id="ahora" class="java.util.Date"/>        
		<c:set var="Parcial" value="0" />
		<c:set var="Total" value="0" />
		<c:set var="Fecha" value='${ahora}'/>
		<div id="wrap">
			<div id="header"><%@ include file="/WEB-INF/jspf/header.jspf" %></div>
			<div id="left"><%@ include file="/WEB-INF/jspf/left.jspf" %></div>
			<div id="right"><%@ include file="/WEB-INF/jspf/right.jspf" %></div>
			<div id="center">
				<form action="invoicesServlet" method="post" enctype="application/x-www-form-urlencoded" name="search" id="search">
					<fieldset>
					<legend>B&uacute;squeda detallada:</legend>
					Formas de pago: <cws:invoicesList/><br />
					<label for="fechaMinimaPedido" class="labelfix">Con fecha desde</label>
					<input type="text" id="fechaMinimaPedido" name="fechaMinimaVencimiento" size="10" maxlength="10" value="<c:out value='${ctx.fechaMinimaVencimiento}'/>" class="textfield"> <a href="#" onClick="cal.select(document.search.fechaMinimaVencimiento,'fecha1','yyyy-MM-dd'); return false;" name="fecha1" id="fecha1"><img src="date.png" width="16" height="16" align="absmiddle" alt="Seleccionar fecha" title="Seleccionar fecha" border="0"></a>
					<label for="fechaMaximaPedido" class="labelfix">hasta</label>
					<input type="text" id="fechaMaximaPedido" name="fechaMaximaVencimiento" size="10" maxlength="10" value="<c:out value='${ctx.fechaMaximaVencimiento}'/>" class="textfield"> <a href="#" onClick="cal.select(document.search.fechaMaximaVencimiento,'fecha2','yyyy-MM-dd'); return false;" name="fecha2" id="fecha2"><img src="date.png" width="16" height="16" align="absmiddle" alt="Seleccionar fecha" title="Seleccionar fecha" border="0"></a><br />
					<input type="submit" name="Submit" value="Buscar" class="button">
				</form>
				<c:choose>
					<c:when test='${not empty info.vencimientos}'>
						<h1>Vencimientos pendientes </h1>
						<c:if test='${not empty info.info}'>
							<p><strong><c:out value='${info.message}'/></strong></p>
						</c:if>
						<!--[if IE]> <div style="width: 100%;"> <![endif]-->
						<table class="tablelist">
							<tr> 
								<th>Documento</th>
								<th>Forma de pago</th>
								<th>Factura</th>
								<th>Fecha de vencimiento</th>
								<th>Importe al vencimiento</th>
							</tr>
							<c:forEach items='${info.vencimientos}' var='vencimiento'> 
								<c:if test='${(vencimiento.dueDate != Fecha) && (Fecha != "")}'>
									<tr> 
										<td align="right"><strong>&raquo;</strong></td>
										<td align="right" colspan="2"><em><strong>Vencimiento total a fecha de <fmt:formatDate value='${Fecha}'/>:</strong></em></td>
										<td align="right" colspan="2"><strong><em><fmt:formatNumber value='${Total}' type="currency" currencySymbol=""/> &euro;</em></strong></td>
										<td>&nbsp;</td>
									</tr>
									<c:set var="Total" value="0" />
									<c:choose>
										<c:when test='${(vencimiento.vencido)}'><tr class="row1"></c:when>
										<c:otherwise><tr class="row0"></c:otherwise>
									</c:choose> 
										<td><c:out value='${vencimiento.c_InvoicePaySchedule_ID}'/></td>
										<td align="center"><c:out value='${vencimiento.formaDePago}'/></td>
										<td><a href="invoiceLines.jsp?C_Invoice_ID=<c:out value='${vencimiento.c_Invoice_ID}'/>"><c:out value='${vencimiento.factura_N}'/></a></td>
										<td align="center"><fmt:formatDate value='${vencimiento.dueDate}'/></td> 
										<td align="right"><fmt:formatNumber value='${vencimiento.importeTotal}' type="currency" currencySymbol=""/> &euro;</td>
									</tr>
									<c:set var="Fecha" value='${vencimiento.dueDate}'/>
									<c:set var="Parcial" value='${vencimiento.importeTotal}'/>
									<c:set var="Total" value='${Total + Parcial}'/>
								</c:if>
							</c:forEach>
							<tr> 
								<td align="right"><strong>&raquo;</strong></td>
								<c:if test='${Fecha != null}'>
									<td align="right" colspan="2"><em><strong>Vencimiento total a fecha de <fmt:formatDate value='${Fecha}'/>:</strong></em></td>
								</c:if>
								<td align="right" colspan="2"><strong><em><fmt:formatNumber value='${Total}' type="currency" currencySymbol=""/>&nbsp;&euro;</em></strong></td>
								<td>&nbsp;</td>
							</tr>
					  </table>
					  <!--[if IE]> </div> <![endif]-->
					</c:when>
					<c:otherwise>
						<p align="center">No hay ning&uacute;n vencimiento pendiente.</p>
					</c:otherwise>
				</c:choose>
			</div>
			<div id="footer"><%@ include file="/WEB-INF/jspf/footer.jspf" %></div>
		</div>
	</body>
</html>