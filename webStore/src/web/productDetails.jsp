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

<html>
	<head>
		<title><c:out value='${ctx.name}'/></title>
		<%@ include file="/WEB-INF/jspf/head.jspf" %>
	</head>
	<body>
		<div id="productDetails">
			<c:choose>
				<c:when test='${empty webUser || !webUser.loggedIn}'><p><em>Para poder ver productos debe abrir sesi&oacute;n primero.</em></p></c:when>
				<c:otherwise>
					<!-- Set Product ID and get Product	-->
					<c:set target='${info}' property='id' value='${param.M_Product_ID}' />
					<c:set var='producto' value='${info.producto}' />
					<c:if test='${empty producto}'>
						<c:set target='${info}' property='message' value='Detalle de producto no encontrado' />
						<c:redirect url='productDetails.jsp'/>
					</c:if>
					
					<h1>Detalles del producto <c:out value='${producto.value}'/></h1>
					
					<c:if test='${not empty info.info}'>
						<p><c:out value='${info.message}'/></p>
					</c:if>
					
					<p>Modelo: <c:out value='${producto.value}'/></p>
					<p>Nombre: <c:out value='${producto.name}'/></p>
					<p>Descripci&oacute;n: <c:out value='${producto.description}'/></p>
					
					<h3>Detalles identificativos</h3>
					<p>UPC/EAN: <c:out value='${producto.UPC}'/></p>
					<p>Marca: <c:out value='${producto.classification}'/></p>
					<p>
						En venta:
						<c:set var='vendible' value='${producto.sold}' />
						<c:choose>
							<c:when test='${vendible}' ><img src="ok.png" width="16" height="16" align="absmiddle" alt="Producto en venta" title="Este producto se encuentra en venta" border="0"></c:when>	
							<c:otherwise><img src="no.png" width="16" height="16" align="absmiddle" alt="Producto no en venta" title="Este producto no se encuentra actualmente en venta" border="0"></c:otherwise>
						</c:choose>
					</p>
					<p>
						Obsolescencia:
						<c:set var='obsoleto' value='${producto.discontinued}' />
						<c:choose>
							<c:when test='${obsoleto}' ><img src="no.png" width="16" height="16" align="absmiddle" alt="Producto obsoleto" title="Este producto se encuentra obsoleto" border="0"></c:when>	
							<c:otherwise><img src="ok.png" width="16" height="16" align="absmiddle" alt="Producto no obsoleto" title="Este producto no se encuentra obsoleto" border="0"></c:otherwise>
						</c:choose>				
					</p>
					
					<h3>Características adicionales</h3>
					<p>Notas del producto: <c:out value='${producto.documentNote}'/></p>	
					
					<h3>Avisos adicionales</h3>			
					<p>Ayuda del producto: <c:out value='${producto.help}'/></p>
					
					<p align="center"><a href="javascript:window.close();" title="Cerrar esta ventana de detalle de producto">Cerrar ventana</a></p>
				</c:otherwise>
			</c:choose>
		</div>
	</body>
</html>