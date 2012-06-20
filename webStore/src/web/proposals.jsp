<!--
  - openXpertya (r), Tienda en-l�ea, Copyright (c) 2003-2007 ConSerTi S.L.
  - este fichero esta licenciado bajo los t�minos de la Licencia Pblica openXpertya (LPO)
  - con aplicaci� directa del ADDENDUM A, secci� 6 (A.6) y posibilidad de relicenciamiento.
  - Licencia y m� informaci� en http://www.openxpertya.org/ayuda/Licencia.html
  - Informaci� de la direcci� de la tienda Web
  -->

<%@ page session="true" contentType="text/html; charset=iso-8859-1" errorPage="errorPage.jsp" %>
<%@ taglib uri="webStore.tld" prefix="cws" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://jsptags.com/tags/navigation/pager" prefix="pg" %>

<c:if test='${empty webUser || !webUser.loggedIn}'>
	<c:redirect url='loginServlet?ForwardTo=proposals.jsp'/>
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
		<div id="wrap">
			<div id="header"><%@ include file="/WEB-INF/jspf/header.jspf" %></div>
			<div id="left"><%@ include file="/WEB-INF/jspf/left.jspf" %></div>
			<div id="right"><%@ include file="/WEB-INF/jspf/right.jspf" %></div>
			<div id="center">
				<form action="proposalsServlet" method="post" enctype="application/x-www-urlencoded" name="estado" id="estado">
					<fieldset>
						<legend>B&uacute;squeda detallada:</legend>
						<label for="fechaMinimaPresupuesto" class="labelfix">Con fecha desde</label>
						<input type="text" id="fechaMinimaPresupuesto" name="fechaMinimaPresupuesto" size="10" maxlength="10" value="<c:out value='${ctx.fechaMinimaPresupuesto}'/>" class="textfield"> <a href="#" onClick="cal.select(document.estado.fechaMinimaPresupuesto,'fecha1','yyyy-MM-dd'); return false;" name="fecha1" id="fecha1"><img src="date.png" width="16" height="16" align="absmiddle" alt="Seleccionar fecha" title="Seleccionar fecha" border="0"></a>
						<label for="fechaMaximaPresupuesto" class="labelfix">hasta</label>
						<input type="text" id="fechaMaximaPresupuesto" name="fechaMaximaPresupuesto" size="10" maxlength="10" value="<c:out value='${ctx.fechaMaximaPresupuesto}'/>" class="textfield"> <a href="#" onClick="cal.select(document.estado.fechaMaximaPresupuesto,'fecha2','yyyy-MM-dd'); return false;" name="fecha2" id="fecha2"><img src="date.png" width="16" height="16" align="absmiddle" alt="Seleccionar fecha" title="Seleccionar fecha" border="0"></a><br />
						<input type="submit" name="Submit" value="Buscar" class="button">
					</fieldset> 
				</form>
				
				<h1>Mis presupuestos</h1>
				<c:if test='${not empty info.info}'>
					<p><strong><c:out value='${info.message}'/></strong></p>
				</c:if>
				<!--[if IE]> <div style="width: 100%;"> <![endif]--> 
				<table class="tablelist">
					<tr>  
						<th align="right">Documento</th>
						<th align="center">Fecha</th>
						<th align="right">Total l&iacute;neas</th>
						<th align="right">Total global</th>
						<th align="center">Acci&oacute;n</th>
					</tr>			
				<pg:pager maxIndexPages="10" export="currentPageNumber=pageNumber" maxPageItems="25">  
					<c:forEach items='${info.proposals}' var='order' varStatus='status'>
						<pg:item> 
							<c:choose>
								<c:when test="${status.count % 2 == 0}"><tr class="row1"></c:when>
								<c:otherwise><tr class="row0"></c:otherwise>
							</c:choose>   
					  		<td align="right"><a href="proposalDetails.jsp?C_Order_ID=<c:out value='${order.c_Order_ID}'/>"><c:out value='${order.documentNo}'/></a></td>
					  		<td align="center"><fmt:formatDate value='${order.dateOrdered}'/></td>
					  		<td align="right"><fmt:formatNumber value='${order.totalLines}' type="currency" currencySymbol=""/> &euro;</td>
					  		<td align="right"><fmt:formatNumber value='${order.grandTotal}' type="currency" currencySymbol=""/> &euro;</td>
					  		<td align="center">
								<c:choose>
									<c:when test='${order.docStatus == "VO"}'><span class="wrong">Presupuesto anulado</span></c:when>
									<c:when test='${order.docStatus == "CO"}'><span class="correct">Presupuesto completado</span></c:when>
									<c:otherwise>
										<input name="Void" id="Void" value="Anular" onClick="window.top.location.replace('<c:out value='https://${ctx.context}/'/>proposalServlet?C_Order_ID=<c:out value='${order.c_Order_ID}'/>&DocAction=VO');" type="button" class="button">
										<input name="Confirm" id="Confirm" value="Confirmar" onClick="window.top.location.replace('<c:out value='https://${ctx.context}/'/>proposalServlet?C_Order_ID=<c:out value='${order.c_Order_ID}'/>');" type="button" class="button">
									</c:otherwise>
								</c:choose>  
							</td>
						</tr>
						</pg:item> 
					</c:forEach> 
				</table>
				<!--[if IE]> </div> <![endif]-->
				<p align="center">
					<span class="pager">
						<pg:index> 
							<pg:prev><a href="<%= pageUrl %>">&laquo; Anterior</a></pg:prev> 
							<pg:pages>
								<%
									if (pageNumber.intValue() < 10) { %> <% } 
									if (pageNumber == currentPageNumber) { %><%= pageNumber %><% }
									else { %><a href="<%= pageUrl %>"><%= pageNumber %></a><% }
								%>
							</pg:pages>
							<pg:next><a href="<%= pageUrl %>">Siguiente &raquo;</a></pg:next>
						</pg:index>
					</span>
				</p>
				</pg:pager>			
			</div>
			<div id="footer"><%@ include file="/WEB-INF/jspf/footer.jspf" %></div>
		</div>
	</body>
</html>