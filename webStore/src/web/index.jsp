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

<cws:priceList priceList_ID="0"/>

<html>
	<head>
		<title><c:out value='${ctx.name}'/></title>
		<%@ include file="/WEB-INF/jspf/head.jspf" %>
		<script language="Javascript" type="text/JavaScript">
			function clearSearch() {
				document.search.SearchString.value = "";
				document.search.M_Product_Category_ID.value = "-1"; 
				document.search.precioMinimo.value = "0"; 
				document.search.precioMaximo.value = "0";
				document.search.EnStock.value = "EnStock";
				document.search.Orden.value = "Todo";
				document.search.submit(); 
			}
			
			function openWindow( URL ) {
				window.open( URL, "Details", "width=640, height=480, scrollbars=yes, menubar=no, location=no, resizable=no" ); 
			}
		</script>
	</head>
	<body onLoad="initClock();">
		<div id="wrap">
			<div id="header"><%@ include file="/WEB-INF/jspf/header.jspf" %></div>
			<div id="left"><%@ include file="/WEB-INF/jspf/left.jspf" %></div>
			<div id="right"><%@ include file="/WEB-INF/jspf/right.jspf" %></div>
			<div id="center">
			<c:choose>
				<c:when test='${empty webUser || !webUser.loggedIn}'><p><em>Para poder buscar productos debe abrir sesi&oacute;n primero.</em></p></c:when>
				<c:otherwise>
					<c:out value='${ctx.webParam2}' escapeXml='false'/>
					<form action="productServlet" method="post" enctype="application/x-www-form-urlencoded" name="search" id="search">
						<fieldset>
							<legend>B&uacute;squeda detallada:</legend>
							<label for="SearchString">Nombre</label>
							<input type="text" name="SearchString" id="SearchString" value="<c:out value='${ctx.SearchString}' escapeXml='false'/>" class="textfield">
							<br />
							<cws:productCategoryList/>
							<br />
							<label for="precioMinimo">Precio desde</label>
							<input type="text" name="precioMinimo" id="precioMinimo" value="<c:out value='${ctx.precioMinimo}' escapeXml='false' default='0'/>" onKeypress="if (event.keyCode > 57 || event.keyCode < 48) event.returnValue = false;" class="textfield">
							<label for="precioMaximo" class="labelfix">hasta</label>
							<input type="text" name="precioMaximo" id="precioMaximo" value="<c:out value='${ctx.precioMaximo}' escapeXml='false' default='0'/>" onKeypress="if (event.keyCode > 57 || event.keyCode < 48) event.returnValue = false;" class="textfield">
							<br />
							<c:choose>
								<c:when test='${ctx.EnStock == "ConDisponibilidad"}'>
									<input type="Radio" name="EnStock" id="ConDisponibilidad" value="ConDisponibilidad" checked="checked">
									<label for="ConDisponibilidad" class="labelfix">Con disponibilidad</label>
									<input type="Radio" name="EnStock" id="EnStock" value="EnStock">
									<label for="EnStock" class="labelfix">En stock</label>
									<input type="Radio" name="EnStock" id="Todo" value="Todo">
									<label for="Todo" class="labelfix">Todos</label>
								</c:when>  
								<c:when test='${ctx.EnStock == "EnStock"}'>
									<input type="Radio" name="EnStock" id="ConDisponibilidad" value="ConDisponibilidad">
									<label for="ConDisponibilidad" class="labelfix">Con disponibilidad</label>
									<input type="Radio" name="EnStock" id="EnStock" value="EnStock" checked="checked">
									<label for="EnStock" class="labelfix">En stock</label>
									<input type="Radio" name="EnStock" id="Todo" value="Todo">
									<label for="Todo" class="labelfix">Todos</label>
								</c:when> 
								<c:otherwise>
									<input type="Radio" name="EnStock" id="ConDisponibilidad" value="ConDisponibilidad">
									<label for="ConDisponibilidad" class="labelfix">Con disponibilidad</label>
									<input type="Radio" name="EnStock" id="EnStock" value="EnStock">
									<label for="EnStock" class="labelfix">En stock</label>
									<input type="Radio" name="EnStock" id="Todo" value="Todo" checked="checked">
									<label for="Todo" class="labelfix">Todos</label>
								</c:otherwise>
							</c:choose>
							<br />
							<c:choose>
								<c:when test='${ctx.Orden == "Precio"}'>     
									<input type="Radio" name="Orden" id="Precio" value="Precio" checked="checked">
									<label for="Precio" class="labelfix">Ordenar por precio</label>
									<input type="Radio" name="Orden" id="Alfabetico" value="Ref">
									<label for="Alfabetico" class="labelfix">Ordenar por referencia</label>
								</c:when>        
								<c:otherwise> 
									<input type="Radio" name="Orden" id="Precio" value="Precio">
									<label for="Precio" class="labelfix">Ordenar por precio</label>
									<input type="Radio" name="Orden" id="Alfabetico" value="Ref" checked="checked">
									<label for="Alfabetico" class="labelfix">Ordenar por referencia</label>
								</c:otherwise>
							</c:choose>
							<br />
							<input type="button" name="Clean" id="Reset" value="Limpiar" onClick="clearSearch();">
							<input type="submit" name="Submit" id="Submit" value="Buscar">		
						</fieldset>
						</form>
						
						<c:choose>
							<c:when test='${priceList.notAllPrices}'><p><em>No podemos mostrar todos los productos, introduzca criterios de b&uacute;squeda para limitar la selecci&oacute;n</em></p></c:when>        
							<c:otherwise><p>&nbsp;</p></c:otherwise>
						</c:choose>
						<c:if test='${priceList.noLines}'><p><em>No se encontraron productos, introduzca criterios de b&uacute;squeda.</em></p></c:if>
						
						<pg:pager maxIndexPages="10" export="currentPageNumber=pageNumber" maxPageItems="25">
						<form action="basketServlet" method="post" enctype="application/x-www-form-urlencoded" name="products" id="products">
							<input name="M_PriceList_ID" type="hidden" value="<c:out value='${priceList.priceList_ID}'/>">
							<input name="M_PriceList_Version_ID" type="hidden" value="<c:out value='${priceList.priceList_Version_ID}'/>">
							<!--[if IE]> <div style="width: 100%;"> <![endif]-->
							<table class="tablelist">
								<tr> 
									<th align="right">Modelo</th>
									<th align="left">Producto</th>
									<th align="right">Mi precio</th>
									<th align="center">Disponibilidad</th>
									<c:if test='${empty ctx.isCatalog || ctx.isCatalog == 0}'>
										<th align="center">Cantidad</th>
										<th align="center">Acci&oacute;n</th>
									</c:if>
								</tr>
								<c:forEach items='${priceList.prices}' var='product' varStatus='status' > 
									<pg:item>
										<c:choose>
											<c:when test="${status.count % 2 == 0}"><tr class="row1"></c:when>
											<c:otherwise><tr class="row0"></c:otherwise>
										</c:choose>
											<td align="right"><a href="javascript:openWindow('productDetails.jsp?M_Product_ID=<c:out value='${product.id}'/>');" title="<c:out value='${product.name}'/>"><input name="Name_<c:out value='${product.id}'/>" type="hidden" value="<c:out value='${product.name}'/>"><c:out value='${product.value}'/></a></td>
											<td align="left"><a href="javascript:openWindow('productDetails.jsp?M_Product_ID=<c:out value='${product.id}'/>');" title="<c:out value='${product.name}'/>"><c:out value='${product.name}'/></a></td>
											<td align="right"><input name="Price_<c:out value='${product.id}'/>" type="hidden" value="<c:out value='${product.price}'/>"> <fmt:formatNumber value='${product.price}' type="currency" currencySymbol=""/> &euro;</td>
											<td align="center">
												<c:set var='disponibilidad' value='${product.disponible}' />
												<c:choose>
													<c:when test='${disponibilidad > 0}'><img src="tagGreen.png" width="16" height="16" align="absmiddle" alt="Producto en stock" title="El producto est&#xc3;&#xa1; actualmente en stock" border="0"> <c:out value='${disponibilidad}'/></c:when>	
													<c:when test='${disponibilidad == 0}'><img src="tagRed.png" width="16" height="16" align="absmiddle" alt="Producto fuera de stock" title="El producto no est&#xc3;&#xa1; actualmente en stock" border="0"> <c:out value='${disponibilidad}'/></c:when>
													<c:otherwise><img src="tagYellow.png" width="16" height="16" align="absmiddle" alt="Producto con disponibilidad limitada" title="El producto tiene actualmente una disponibilidad limitada" border="0"> <fmt:formatNumber value='${-1 * disponibilidad}'/></c:otherwise>
												</c:choose>
											</td>
											<c:if test='${empty ctx.isCatalog || ctx.isCatalog == 0}'>
												<td align="center"><input name="Qty_<c:out value='${product.id}'/>" type="text" id="qty_<c:out value='${product.id}'/>" value="1" size="4" maxlength="4" class="textfield"></td>
												<td align="center"><input name="Add_<c:out value='${product.id}'/>" type="submit" id="Add_<c:out value='${product.id}'/>" value="A&ntilde;adir" class="button"> </td>
											</c:if>
										</tr>
									</pg:item> 
								</c:forEach> 
							</table>
							<!--[if IE]> </div> <![endif]-->
						</form>
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

						<p align="center">
							<span class="mini">
								<c:choose>
									<c:when test='${not empty priceList.name}'>Lista de Precios: <c:out value='${priceList.name}'/></c:when>
									<c:when test='${not empty priceList.priceCount}'>(<c:out value='${priceList.priceCount}'/>)</c:when>
									<c:when test='${not empty priceList.searchInfo}'> - <c:out value='${priceList.searchInfo}'/></c:when>
								</c:choose>	
							</span>
						</p>
					</c:otherwise>
				</c:choose>			
			</div>
			<div id="footer"><%@ include file="/WEB-INF/jspf/footer.jspf" %></div>
		</div>
	</body>
</html>
