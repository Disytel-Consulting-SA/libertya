<!--
  - openXpertya (r), Tienda en-l&#xef;&#xbf;&#xbd;ea, Copyright (c) 2003-2007 ConSerTi S.L.
  - este fichero esta licenciado bajo los t&#xef;&#xbf;&#xbd;minos de la Licencia Pblica openXpertya (LPO)
  - con aplicaci&#xef;&#xbf;&#xbd; directa del ADDENDUM A, secci&#xef;&#xbf;&#xbd; 6 (A.6) y posibilidad de relicenciamiento.
  - Licencia y m&#xef;&#xbf;&#xbd; informaci&#xef;&#xbf;&#xbd; en http://www.openxpertya.org/ayuda/Licencia.html
  - Informaci&#xef;&#xbf;&#xbd; de la direcci&#xef;&#xbf;&#xbd; de la tienda Web
  -->

<%@ page session="true" contentType="text/html; charset=iso-8859-1" errorPage="errorPage.jsp" %>
<%@ taglib uri="webStore.tld" prefix="cws" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://jsptags.com/tags/navigation/pager" prefix="pg" %>

<c:if test='${empty webUser || !webUser.loggedIn}'>
	<c:redirect url='loginServlet?ForwardTo=orders.jsp'/>
</c:if>

<html>
	<head>
		<title><c:out value='${ctx.name}'/></title>
		<%@ include file="/WEB-INF/jspf/head.jspf" %>
		<script language="JavaScript" SRC="CalendarPopup.js"></script>

		<script language="JavaScript">
			var cal = new CalendarPopup(); 	
			cal.showYearNavigation();
		</script>
			
		<script language="JavaScript1.2">
		function checkAll() {
			if(document.estado.Marcar.value == "Marcar todos") {
				document.estado.Marcar.value = "Desmarcar todos";
				for(var i=0;i<document.estado.elements.length;i++){
					if(document.estado.elements[i].type == "checkbox"){
						document.estado.elements[i].checked = true;	
					}
				} 
			}
			else if(document.estado.Marcar.value == "Desmarcar todos") {
				document.estado.Marcar.value = "Marcar todos";
				for(var i=0;i<document.estado.elements.length;i++){
					if(document.estado.elements[i].type == "checkbox"){
						document.estado.elements[i].checked = false;	
					}
				} 
			}
		}
		</script>
	</head>
	<body onLoad="initClock()">
		<div id="wrap">
			<div id="header"><%@ include file="/WEB-INF/jspf/header.jspf" %></div>
			<div id="left"><%@ include file="/WEB-INF/jspf/left.jspf" %></div>
			<div id="right"><%@ include file="/WEB-INF/jspf/right.jspf" %></div>
			<div id="center">
				<form action="ordersServlet" method="post" enctype="application/x-www-urlencoded" name="estado" id="estado">
					<fieldset>
						<legend>B&uacute;squeda detallada:</legend>
						<c:choose>
							<c:when test='${ctx.Cerrado == "CL"}'><input name="Cerrado" type="checkbox"  id="Cerrado" value="CL" checked="checked"></c:when>
							<c:otherwise><input name="Cerrado" type="checkbox"  id="Cerrado" value="CL"></c:otherwise>
						</c:choose>
						<label for="Cerrado" class="labelfix">Cerrado</label>
						<c:choose>
							<c:when test='${ctx.Completo == "CO"}'><input name="Completo" type="checkbox"  id="Completo" value="CO" checked="checked"></c:when>
							<c:otherwise><input name="Completo" type="checkbox"  id="Completo" value="CO"></c:otherwise>
						</c:choose>
						<label for="Completo" class="labelfix">Completo</label>
						<c:choose>
							<c:when test='${ctx.Borrador == "DR"}'><input name="Borrador" type="checkbox"  id="Borrador" value="DR" checked="checked"></c:when>
							<c:otherwise><input name="Borrador" type="checkbox"  id="Borrador" value="DR"></c:otherwise>
						</c:choose>
						<label for="Borrador" class="labelfix">Borrador</label>
						<c:choose>
							<c:when test='${ctx.Aprobado == "AP"}'><input name="Aprobado" type="checkbox"  id="Aprobado" value="AP" checked="checked"></c:when>
							<c:otherwise><input name="Aprobado" type="checkbox"  id="Aprobado" value="AP"></c:otherwise>
						</c:choose>
						<label for="Aprobado" class="labelfix">Aprobado</label><br />
						<c:choose>
							<c:when test='${ctx.NoAprobado == "NA"}'><input name="NoAprobado" type="checkbox"  id="NoAprobado" value="NA" checked="checked"></c:when>
							<c:otherwise><input name="NoAprobado" type="checkbox"  id="NoAprobado" value="NA"></c:otherwise>		
						</c:choose>
						<label for="NoAprobado" class="labelfix">No aprobado</label>
						<c:choose>
							<c:when test='${ctx.Anulado == "VO"}'><input name="Anulado" type="checkbox"  id="Anulado" value="VO" checked="checked"></c:when>
							<c:otherwise><input name="Anulado" type="checkbox"  id="Anulado" value="VO"></c:otherwise>
						</c:choose>
						<label for="Anulado" class="labelfix">Anulado</label>
						<c:choose>
							<c:when test='${ctx.Inactivo == "IN"}'><input name="Inactivo" type="checkbox"  id="Inactivo" value="IN" checked="checked"></c:when>
							<c:otherwise><input name="Inactivo" type="checkbox"  id="Inactivo" value="IN"></c:otherwise>
						</c:choose>
						<label for="Inactivo" class="labelfix">Inactivo</label>
						<c:choose>
							<c:when test='${ctx.Revertido == "RE"}'><input name="Revertido" type="checkbox"  id="Revertido" value="RE" checked="checked"></c:when>
							<c:otherwise><input name="Revertido" type="checkbox"  id="Revertido" value="RE"></c:otherwise>
						</c:choose>
						<label for="Revertido" class="labelfix">Revertido</label><br />
						<c:choose>
							<c:when test='${ctx.Desconocido == "??"}'><input name="Desconocido" type="checkbox"  id="Desconocido" value="??" checked="checked"></c:when>
							<c:otherwise><input name="Desconocido" type="checkbox"  id="Desconocido" value="??"></c:otherwise>
						</c:choose>
						<label for="Desconocido" class="labelfix">Desconocido</label>
						<c:choose>
							<c:when test='${ctx.EsperandoConfirmacion == "WC"}'><input name="EsperandoConfirmacion" type="checkbox"  id="EsperandoConfirmacion" value="WC" checked="checked"></c:when>
							<c:otherwise><input name="EsperandoConfirmacion" type="checkbox"  id="EsperandoConfirmacion" value="WC"></c:otherwise>
						</c:choose>
						<label for="EsperandoConfirmacion" class="labelfix">Esperando confirmaci&oacute;n</label>
						<c:choose>
							<c:when test='${ctx.EnProceso == "IP"}'><input name="EnProceso" type="checkbox"  id="EnProceso" value="IP" checked="checked"></c:when>
							<c:otherwise><input name="EnProceso" type="checkbox"  id="EnProceso" value="IP"></c:otherwise>
						</c:choose>
						<label for="EnProceso" class="labelfix">En proceso</label>
						<c:choose>
							<c:when test='${ctx.PagosEnEspera == "WP"}'><input name="PagosEnEspera" type="checkbox"  id="PagosEnEspera" value="WP" checked="checked"></c:when>
							<c:otherwise><input name="PagosEnEspera" type="checkbox"  id="PagosEnEspera" value="WP"></c:otherwise>
						</c:choose>
						<label for="PagosEnEspera" class="labelfix">Pagos en espera</label><br />	
						<label for="fechaMinimaPedido" class="labelfix">Con fecha desde</label>
						<input type="text" id="fechaMinimaPedido" name="fechaMinimaPedido" size="10" maxlength="10" value="<c:out value='${ctx.fechaMinimaPedido}'/>" class="textfield"> <a href="#" onClick="cal.select(document.estado.fechaMinimaPedido,'fecha1','yyyy-MM-dd'); return false;" name="fecha1" id="fecha1"><img src="date.png" width="16" height="16" align="absmiddle" alt="Seleccionar fecha" title="Seleccionar fecha" border="0"></a>
						<label for="fechaMaximaPedido" class="labelfix">hasta</label>
						<input type="text" id="fechaMaximaPedido" name="fechaMaximaPedido" size="10" maxlength="10" value="<c:out value='${ctx.fechaMaximaPedido}'/>" class="textfield"> <a href="#" onClick="cal.select(document.estado.fechaMaximaPedido,'fecha2','yyyy-MM-dd'); return false;" name="fecha2" id="fecha2"><img src="date.png" width="16" height="16" align="absmiddle" alt="Seleccionar fecha" title="Seleccionar fecha" border="0"></a><br />
						
						<input type="submit" name="Submit" value="Buscar" class="button"> 
						<input type="button" name="Marcar" value="Marcar todos" onClick="checkAll()" class="button">
					</fieldset>
				</form>
				<h1>Mis pedidos</h1>
				<c:if test='${not empty info.info}'>
					<p><strong><c:out value='${info.message}'/></strong></p>
				</c:if> 
				<!--[if IE]> <div style="width: 100%;"> <![endif]-->
				<table class="tablelist">
					<tr>  
						<th align="right">Documento</th>
						<th align="center">Estado</th>
						<th align="center">Fecha</th>
						<th align="right">Total l&iacute;neas</th>
						<th align="right">Total global</th>
						<th align="center">Acci&oacute;n</th>
					</tr>
					<pg:pager maxIndexPages="10" export="currentPageNumber=pageNumber" maxPageItems="25">  
						<c:forEach items='${info.orders}' var='order' varStatus='status'>
							<pg:item> 
								<c:choose>
									<c:when test="${status.count % 2 == 0}"><tr class="row1"></c:when>
									<c:otherwise><tr class="row0"></c:otherwise>
								</c:choose>
								<td align="right"><a href="orderDetails.jsp?C_Order_ID=<c:out value='${order.c_Order_ID}'/>"><c:out value='${order.documentNo}'/></a></td>
								<td align="center"><c:out value='${order.docStatusName}'/></td>
								<td align="center"><fmt:formatDate value='${order.dateOrdered}'/></td>
								<td align="right"><fmt:formatNumber value='${order.totalLines}' type="currency" currencySymbol=""/> &euro;</td>
								<td align="right"><fmt:formatNumber value='${order.grandTotal}' type="currency" currencySymbol=""/> &euro;</td>
								<td align="center"> 
									<c:choose>
										<c:when test='${order.docStatus=="WP"}'><strong>Pendiente de autorizar: <fmt:formatNumber value='${order.grandTotal}' type="currency" currencySymbol=""/> &euro;</strong></c:when>
										<c:when test='${order.docStatus=="VO"}'><span class="wrong">Pedido anulado</span></c:when>
										<c:when test='${order.docStatus=="CO"}'><span class="correct">Pedido completado</span></c:when>
										<c:otherwise><strong>Pedido procesado</strong></c:otherwise>
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
