package org.openXpertya.util;

/**
 * Definición de constantes de nombres de tareas cuyo tiempo es medido mediante
 * {@link TimeStatsLogger}.
 */
public interface MeasurableTask {

	/** Tarea TPV: Inicio de TPV */
	public final String POS_INIT = "Iniciar TPV";
	
	/** Tarea TPV: Agregar un artículo al pedido */
	public final String POS_ADD_PRODUCT = "Agregar artículo al pedido (previamente cargado)";
	
	/** Tarea TPV: Cargar un artículo desde el InfoProduct*/
	public final String POS_LOAD_PRODUCT_FROM_INFOPRODUCT = "Cargar artículo desde el InfoProduct";

	/** Tarea TPV: Buscar un artículo */
	public final String POS_SEARCH_PRODUCT = "Buscar artículo";
	/** Tarea TPV: Buscar un artículo y mostrar el InfoProduct */
	public final String POS_SEARCH_PRODUCT_SHOW_INFOPRODUCT = "Buscar artículo y mostrar InfoProduct";

	/** Tarea TPV: Agregar un pedido de cliente */
	public final String POS_ADD_CUSTOMER_ORDER = "Agregar un pedido de cliente";
	/** Tarea General: Buscar/Refrescar pedidos en InfoOrder */
	public final String REFRESH_INFO_ORDER = "Buscar/Refrescar pedidos en InfoOrder";
	/** Tarea TPV: Buscar un pedido de cliente por nro (ventana principal) */
	public final String POS_SEARCH_CUSTOMER_ORDER = "Buscar un pedido de cliente por nro (ventana principal)";
	
	/** Tarea General: Buscar/Refrescar Entidades Comerciales en InfoBPartner */
	public final String REFRESH_INFO_BPARTNER = "Buscar/Refrescar Entidades Comerciales en InfoBPartner";
	/** Tarea TPV: Cargar Entidad Comercial */
	public final String POS_LOAD_BPARTNER = "Cargar Entidad Comercial";
	
	/** Tarea TPV: Habilitar pestaña de cobros */
	public final String POS_GOTO_PAYMENTS = "Habilitar/Mostrar pestaña de cobros";
	/** Tarea TPV: Agregar un medio de cobro */
	public final String POS_ADD_PAYMENT = "Agregar medio de cobro";
	
	/** Tarea TPV: Guardar y completar documentos (sin imprimir la factura) */
	public final String POS_SAVE_DOCUMENTS = "Guardar y completar documentos (sin imprimir el ticket/factura)";

	/** Tarea: Imprimir factura mediante controlador fiscal */
	public final String PRINT_FISCAL_INVOICE = "Imprimir factura mediante controlador fiscal";
	/** Tarea: Imprimir el ticket TPV */
	public final String POS_PRINT_TICKET = "Imprimir el ticket TPV";
	
	/** Tarea TPV: Guardar y completar documentos, imprimir factura y restablecer interfaz para nuevo pedido */
	public final String POS_COMPLETE_ORDER = "Guardar y completar documentos, imprimir ticket/factura y restablecer interfaz para nuevo pedido";
}
