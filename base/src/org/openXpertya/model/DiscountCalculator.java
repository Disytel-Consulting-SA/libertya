package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.apache.ecs.xhtml.code;
import org.openXpertya.model.ProductMatching.MatchingCompareType;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * <p>
 * Calculador centralizado de Descuentos de la Aplicación. El motivo de
 * existencia de esta clase es la abstracción de la lógica de búsqueda,
 * determinación y aplicación de descuentos existentes en el sistema.
 * </p>
 * 
 * <p>
 * Cualquier módulo del sistema que requiera la aplicación de descuentos debería
 * utilizar esta clase como punto de entrada para el cálculo, a fin de obtener
 * una manera homogénea de calcular los descuentos para artículos. A su vez, si
 * se amplía la lógica de búsqueda y aplicación de descuentos, los clientes que
 * utilicen esta clase obtendrán estos cambios sin necesidad de recodificar
 * absolutamente nada. Cualquier ampliación o corrección en base a los
 * descuentos se hará dentro de esta clase (o inclusive sus clases asociadas) y
 * los clientes no se enterarán de los mismos, salvo algún caso especial en el
 * cual sea necesario refactorizar por completo el funcionamiento.
 * </p>
 * 
 * <p>
 * Para realizar la aplicación de descuentos a un documento se debe asociar el
 * documento a este calculador de descuento mediante el método
 * {@link #setDocument(IDocument)}. Al utilizar cualquiera de los métodos de
 * aplicación de descuentos, los mismos impactarán en el documento asociado. Si
 * al momento de invocar la aplicación de descuentos este calculador no posee un
 * documento asociado entonces se producirá un error en tiempo de ejecución.
 * </p>
 * 
 * @author Franco Bonafine
 */
public class DiscountCalculator {

	/**
	 * Tipo de descuento general. Cada descuento general agregado a un
	 * calculador de descuentos contiene la clase o tipo al cual pertenece el
	 * descuento.
	 */
	public enum GeneralDiscountKind {
		GeneralDiscountSchema,
		PaymentMedium,
		ManualGeneralDiscount;

		/**
		 * @return La referencia del tipo interno de descuento a partir de este
		 *         tipo de descuento general.
		 */
		private DiscountKind toDiscountKind() {
			switch (this) {
			case GeneralDiscountSchema:
				return DiscountKind.GeneralDiscountSchema;
			case PaymentMedium:
				return DiscountKind.PaymentMedium;
			case ManualGeneralDiscount:
				return DiscountKind.ManualGeneralDiscount;
			}
			return null;
		}
	}
	
	/** ID del descuento de Entidad Comercial */
	private static final Integer BPARTNER_DISCOUNT_ID = 0;
	/** ID de los descuentos internos (combos y promociones). Es -1 ya que no es 
	 * necesario que sea accedido desde los clientes de esta clase. */
	private static final Integer INTERNAL_DISCOUNT_ID = -1;
	
	/** Logger de la aplicación */
	private CLogger log = CLogger.getCLogger(DiscountCalculator.class);
	
	/** Contexto de la aplicación */
	private Properties ctx = Env.getCtx();
	
	/** Decuento asociado a la Entidad Comercial */
	private Discount bPartnerDiscount = null;

	/** Descuento Neto de la Entidad Comercial */
	private BigDecimal bPartnerFlatDiscount = null;
	
	/** Configuración de contexto de uso del esquema de descuento de la entidad comercial. Debe llevar los valores que contiene la columna
	 * DiscountContext de {@link MBPartner} accedido por medio de
	 * {@link MBPartner#getDiscountContext()}. */
	private String bpartnerDiscountContext;
	
	/** Descuentos Generales. Clave: ID del descuento, 
	 *  Valor: Estructura que contiene los datos del descuento */
	private Map<Integer, Discount> generalDiscounts = null;
	
	/** Siguiente valor para el ID de un descuento agregado. 
	 *  El ID 0 está reservado para el descuento de la EC */
	private Integer nextDiscountID = 1;
	
	/** Documento asociado a este calculador de descuentos */
	private IDocument document = null;

	/** ID de la moneda en la que se expresan los importes calculados. 
	 * Es utilizada para obtener las preciciones de redondeos */
	private int currencyID = Env.getContextAsInt(Env.getCtx(), "$C_Currency_ID");

	/** Transacción utilizada para las operaciones de actualización en la BD */
	private String trxName = null;
	
	/** Descuento manual general */
	private Discount manualGeneralDiscount = null;
	
	/**
	 * Indica si los métodos que realizan el escalado de importes debe o no
	 * realizar dicha operación. Este atributo es utilizado internamente para
	 * cálculos sucesivos de descuentos y no puede ser asignado desde un cliente
	 * externo
	 */
	private boolean applyScale = true;
	
	/** Lista de combos de artículos válidos para la fecha del documento */
	private List<MCombo> validCombos = null;

	/** Lista de descuentos por promociones válidas para la fecha del documento */
	private List<Discount> promotionDiscounts = null;
	
	/** Lista de descuentos por combos válidos para la fecha del documento */
	private List<Discount> comboDiscounts = null;
	
	/**
	 * Fecha del documento asociado. Utilizada para determinar si la fecha
	 * devuelta por {@link IDocument#getDate()} ha cambiado en cuyo caso se
	 * deben recargar los combos y promociones válidos para esa nueva fecha
	 */
	private Date documentDate = null;

	/** Configuración de descuentos utilizada por este calculador */
	private MDiscountConfig discountConfig = null;
	
	/**
	 * Contexto de uso. Debe llevar los valores que contiene la columna
	 * DiscountContext de {@link MBPartner} accedido por medio de
	 * {@link MBPartner#getDiscountContext()}.
	 */
	private String context;

	/**
	 * Indica si el calculador debe asumir que existirá al menos un descuento
	 * general agregado para realizar el cálculo de descuento.
	 */
	private boolean assumeGeneralDiscountAdded = false;
	
	/**
	 * Crea un nuevo calculador de descuentos vacío. Por defecto este calculador
	 * no aplicará descuentos hasta que se cargue un descuento de Entidad
	 * Comercial mediante el método {@link #loadBPartnerDiscount(int, boolean)}
	 * o {@link #loadBPartnerDiscount(MDiscountSchema, BigDecimal)}, o algún
	 * descuento general.
	 * 
     * @param context
	 *            contexto de uso del calculador de descuentos. Ver valores en
	 *            columna DiscountContext de {@link MBPartner}.
	 */
	public static DiscountCalculator create(String context) {
		return new DiscountCalculator(context);
	}
	
	/**
	 * Crea un nuevo calculador de descuentos a partir de un calculador de
	 * descuentos existente, asociando un nuevo documento. El nuevo calculador
	 * tendrá el mismo estado (descuentos asociados, descuentos de EC, combos,
	 * promociones, etc) que el parámetro <code>discountCalculator</code> con la
	 * salvedad de que el documento asociado no será el que tenía
	 * <code>discountCalculator</code> sino que será <code>document</code>.
	 * 
	 * @param document
	 *            Documento asociado al calculador de descuentos
	 * @param discountCalculator
	 *            Calculador de descuento del cual se copiará el estado.
	 */
	public static DiscountCalculator create(IDocument document,
			DiscountCalculator discountCalculator) {
		DiscountCalculator ndc = create(document, discountCalculator.getContext());
		ndc.bPartnerDiscount = discountCalculator.bPartnerDiscount;
		ndc.bPartnerFlatDiscount = discountCalculator.bPartnerFlatDiscount;
		ndc.manualGeneralDiscount = discountCalculator.manualGeneralDiscount;
		ndc.ctx = discountCalculator.ctx;
		ndc.currencyID = discountCalculator.currencyID;
		ndc.nextDiscountID = discountCalculator.nextDiscountID;
		ndc.trxName = discountCalculator.trxName;
		ndc.getGeneralDiscounts().putAll(discountCalculator.getGeneralDiscounts());
		if (discountCalculator.getValidCombos() != null) {
			ndc.validCombos = new ArrayList<MCombo>();
			ndc.getValidCombos().addAll(discountCalculator.getValidCombos());
		}
		if(discountCalculator.getPromotionDiscounts() != null){
			ndc.promotionDiscounts = new ArrayList<Discount>();
			ndc.getPromotionDiscounts().addAll(discountCalculator.getPromotionDiscounts());
		}
		return ndc;
	}

	/**
	 * Crea un nuevo calculador de descuentos vacío asociado a un documento al
	 * cual se le aplicarán futuros descuentos. Por defecto este calculador no
	 * aplicará descuentos hasta que se cargue un descuento de Entidad Comercial
	 * mediante el método {@link #loadBPartnerDiscount(int, boolean)} o
	 * {@link #loadBPartnerDiscount(MDiscountSchema, BigDecimal)}, o algún
	 * descuento general.
	 * 
	 * @param document
	 *            Documento asociado al calculador de descuentos
	 * @param context
	 *            contexto de uso del calculador de descuentos. Ver valores en
	 *            columna DiscountContext de {@link MBPartner}.                
	 */
	public static DiscountCalculator create(IDocument document, String context) {
		DiscountCalculator discountCalculator = create(context);
		discountCalculator.setDocument(document);
		return discountCalculator;
	}

	/**
	 * Crea un nuevo calculador de descuentos a partir de una Entidad Comercial.
	 * Obtiene el esquema de descuento asociado a la EC, dependiendo si es una
	 * transacción de ventas o no.
	 * 
	 * @param bPartnerID
	 *            ID de Entidad Comercial
	 * @param isSOTrx
	 *            Indica si es una transacc
	 * @param context
	 *            contexto de uso del calculador de descuentos. Ver valores en
	 *            columna DiscountContext de {@link MBPartner}.
	 * @param bpartnerDiscountContext
	 *            contexto de uso del esquema de descuento de la entidad
	 *            comercial
	 */
	public static DiscountCalculator create(int bPartnerID, boolean isSOTrx, String context, String bpartnerDiscountContext) {
		DiscountCalculator dc = create(context);
		dc.loadBPartnerDiscount(bPartnerID, isSOTrx, bpartnerDiscountContext);
		return dc;
	}

	/**
	 * Crea un nuevo calculador de descuentos a partir de los datos de una
	 * Entidad Comercial. Asigna el Esquema de Descuento y el porcentaje de
	 * descuento neto de la Entidad Comercial a utilizar para el cálculo de
	 * descuentos.
	 * 
	 * @param discountSchema
	 *            Esquema de Descuento a asignar
	 * @param flatDiscount
	 *            Procentaje de descuento neto de la Entidad Comercial
	 *            (utilizado en esquemas de descuento cuyo % está determinado
	 *            por la EC).
	 * @param context
	 *            contexto de uso del calculador de descuentos. Ver valores en
	 *            columna DiscountContext de {@link MBPartner}.
	 * @param bpartnerDiscountContext
	 *            contexto de uso del esquema de descuento de la entidad
	 *            comercial
	 */
	public static DiscountCalculator create(MDiscountSchema discountSchema, BigDecimal flatDiscount, String context, String bpartnerDiscountContext) {
		DiscountCalculator dc = create(context);
		dc.loadBPartnerDiscount(discountSchema, flatDiscount, bpartnerDiscountContext);
		return dc;
	}

	/**
	 * Constructor privado de la clase. Las nuevas instancias se deben crear
	 * mediante los métodos de clase <code>DiscountCalculator.create(...)</code>
	 * .
	 * 
	 * @param context
	 *            contexto de uso del calculador de descuentos. Ver valores en
	 *            columna DiscountContext de {@link MBPartner}.
	 */
	private DiscountCalculator(String context) {
		super();
		this.discountConfig = MDiscountConfig.get(Env.getAD_Org_ID(ctx));
		this.generalDiscounts = new HashMap<Integer, Discount>();
		this.promotionDiscounts = new ArrayList<Discount>();
		this.comboDiscounts = new ArrayList<Discount>();
		this.context = context;
	}
	
	/**
	 * Calcula el precio con descuento a partir de la aplicación de los esquemas
	 * de descuento determinados por la forma de aplicación de descuentos de la
	 * compañía. Devuelve el precio descontado según los esquemas de descuento
	 * aplicados. El parámetro <code>qty</code> solo es utilizado para
	 * determinar la aplicación de esquemas basados en cantidad.
	 * 
	 * @param price
	 *            Precio original
	 * @param qty
	 *            Cantidad origen para el cálculo. Solo se utiliza para
	 *            determinar esquemas o cortes aplicables basados en cantidad
	 *            (no influye en el precio descontado)
	 * @param productCategoryID
	 *            Subfamilia de Artículo. Si es distinta de cero se buscan
	 *            descuentos para esta subfamilia (o sin filtro de subfamilia).
	 *            Si es cero se filtra por la subfamilia del artículo.
	 * @param productID
	 *            Artículo al cual se aplica el descuento. Puede ser cero.
	 * @param date
	 *            Fecha de aplicación. Utilizada para determinar esquemas de
	 *            descuento según su día de aplicación.
	 * @return El precio devuelto es el valor de <code>price</code> reducido en
	 *         un porcentaje determinado por los esquemas de descuento
	 *         aplicados. Si no se aplican descuentos devuelve el mismo valor de
	 *         <code>price</code>.
	 */
	public BigDecimal calculatePrice(BigDecimal price, BigDecimal qty,
			int productCategoryID, int productID, Date date) {
		
		// Por defecto el precio devuelto es el precio original
		BigDecimal discountedPrice = price;
		
		// Se determina si el esquema de descuento de EC asignado es aplicable
		// y en caso de serlo se obtiene el precio descontado a partir de la 
		// aplicación del esquema.
		if (hasBPartnerDiscount()
				&& isBPartnerDocumentDiscountApplicable(
						getBPartnerDiscountSchema().getDiscountContextType(),
						getBpartnerDiscountContext())) {
			discountedPrice = getBPartnerDiscountSchema().calculatePrice(
					qty, 
					price, 
					productID, 
					productCategoryID, 
					getBPartnerFlatDiscount(), 
					date
			);
		}
		
		// TODO: Implementar la aplicación de Descuentos Generales luego de definir
		//       como obtenerlos. 
		
		return discountedPrice;
	}

	/**
	 * <p>
	 * Calcula el importe total de descuento del documento a partir de un
	 * Esquema de Descuento determinado y un importe base de aplicación.
	 * </p>
	 * <p>
	 * El documento recibido como parámetro NO sufre modificaciones.
	 * </p>
	 * <p>
	 * Este método solo calcula el importe del descuento y lo devuelve como
	 * valor de retorno.
	 * </p>
	 * 
	 * @param document
	 *            Documento sobre el cual se calcula el descuento
	 * @param discountSchema
	 *            Esquema de Descuento a aplicar
	 * @param baseAmt
	 *            Importe base de aplicación para el cálculo de proporciones
	 *            dentro del documento. Si es <code>null</code> se toma como
	 *            importe base el importe total del documento sin contemplar
	 *            descuentos a nivel de documento
	 * @return El valor del descuento o {@link BigDecimal#ZERO} si el esquema no
	 *         existe o el resultado de la aplicación del esquema es justamente
	 *         cero.
	 * @throws IllegalArgumentException
	 *             si <code>document</code> es <code>null</code>.
	 */
	protected BigDecimal calculateDiscount(IDocument document, MDiscountSchema discountSchema, BigDecimal baseAmt) {
		BigDecimal discountAmt = BigDecimal.ZERO; // Importe del descuento retornado
		
		// Se requiere un documento para poder realizar el cálculo
		if (document == null) {
			throw new IllegalArgumentException("Document must be not null");
		}
		
		// Valida que el esquema exista
		if (discountSchema == null) {
			return BigDecimal.ZERO;
		}
	
		// Si el importe base recibido es null entonces se toma el importe total
		// del documento como base
		if (baseAmt == null) {
			baseAmt = document.getLinesTotalAmt();
		}
		
		// El importe base y el total del documento debe ser mayor que cero para que
		// exista algún descuento.
		if (baseAmt.compareTo(BigDecimal.ZERO) <= 0
				|| document.getLinesTotalAmt().compareTo(BigDecimal.ZERO) == 0) {
			return BigDecimal.ZERO;
		}
		
		BigDecimal lineDiscountAmt = null; // Importe del descuento de la línea

		// Calcula el descuento de cada línea y lo suma al total
		for (IDocumentLine documentLine : document.getDocumentLines()) {
			lineDiscountAmt = calculateDiscount(documentLine, discountSchema, baseAmt, false);
			discountAmt = discountAmt.add(lineDiscountAmt);
		}
				
		return scaleAmount(discountAmt);
	}

	/**
	 * <p>
	 * Calcula el importe total de descuento del documento asociado a este
	 * calculador a partir de un Esquema de Descuento determinado y un importe
	 * base de aplicación.
	 * </p>
	 * <p>
	 * El documento asociado a este calculador NO sufre modificaciones.
	 * </p>
	 * <p>
	 * Este método solo calcula el importe del descuento y lo devuelve como
	 * valor de retorno.
	 * </p>
	 * 
	 * @param discountSchema
	 *            Esquema de Descuento a aplicar
	 * @param baseAmt
	 *            Importe base de aplicación para el cálculo de proporciones
	 *            dentro del documento. Si es <code>null</code> se toma como
	 *            importe base el importe total del documento sin contemplar
	 *            descuentos a nivel de documento
	 * @return El valor del descuento o {@link BigDecimal#ZERO} si el esquema no
	 *         existe o el resultado de la aplicación del esquema es justamente
	 *         cero.
	 * @throws IllegalStateException
	 *             si {@link #getDocument()} devuelve <code>null</code>.
	 */

	public BigDecimal calculateDiscount(MDiscountSchema discountSchema, BigDecimal baseAmt) {
		validateAssociatedDocument();
		return calculateDiscount(getDocument(), discountSchema, baseAmt);
	}

	/**
	 * <p>
	 * Calcula el importe total de descuento de una línea de dcoumento a partir
	 * de un Esquema de Descuento determinado y un importe base de aplicación.
	 * </p>
	 * <p>
	 * La línea de documento parámetro NO sufre modificaciones.
	 * </p>
	 * <p>
	 * Este método solo calcula el importe del descuento y lo devuelve como
	 * valor de retorno.
	 * </p>
	 * 
	 * @param documentLine
	 *            Línea de documento sobre la cual se calcula el descuento
	 * @param discountSchema
	 *            Esquema de Descuento a aplicar
	 * @param baseAmt
	 *            Importe base de aplicación para el cálculo de proporciones
	 *            dentro del documento. Si es <code>null</code> se toma como
	 *            importe base el importe total del documento al que pertenece
	 *            la línea, sin contemplar descuentos a nivel de documento
	 * @param netDiscount
	 *            Indica si el descuento debe contemplar el importe de impuesto
	 *            de la línea o solo el neto de línea. Si es <code>true</code>
	 *            se calcula el descuento en base al importe determinado por
	 *            {@link IDocumentLine#getPriceList()}, si es <code>false</code>
	 *            en base a {@link IDocumentLine#getTaxedPriceList()}.
	 * 
	 * @return El valor del descuento o {@link BigDecimal#ZERO} si el esquema no
	 *         existe o el resultado de la aplicación del esquema es justamente
	 *         cero.
	 */
	protected BigDecimal calculateDiscount(IDocumentLine documentLine,
			MDiscountSchema discountSchema, BigDecimal baseAmt, boolean netDiscount) {
		
		// Importe total del documento con impuestos. Utilizado para calcular la
		// proporción del baseAmt que también tiene incluido el impuesto 
		BigDecimal documentTotalAmt = documentLine.getDocument().getLinesTotalAmt(); 
		
		// Valida que el esquema exista
		if (discountSchema == null) {
			return BigDecimal.ZERO;
		}
		
		// Si el importe base recibido es null entonces se toma el importe total
		// del documento como base.
		if (baseAmt == null) {
			baseAmt = documentTotalAmt;
		}
		
		// El importe base y el total del documento debe ser mayor que cero para que
		// exista algún descuento.
		if (baseAmt.compareTo(BigDecimal.ZERO) <= 0
				|| documentTotalAmt.compareTo(BigDecimal.ZERO) == 0) {
			return BigDecimal.ZERO;
		}
		
		// Obtiene la proporción entre el importe base de aplicación y el
		// importe total del documento que contiene la línea. Esta proporción es
		// necesaria para calcular el precio correspondiente de la línea
		// respecto del importe base. (e.d., cuanto representa la línea dentro
		// del importe base a aplicar)
		BigDecimal ratio = baseAmt.divide(documentTotalAmt, 20,
				BigDecimal.ROUND_HALF_EVEN);

		// Precio de la línea. Se obtiene teniendo en cuenta el nivel del
		// esquema de descuento.
		BigDecimal price = null;
		// Cantidad de la línea sobre la que se calcula el descuento.
		BigDecimal qty   = null;
		// Los descuentos a nivel de línea se calculan a partir del precio de
		// tarifa de la línea
		if (discountSchema.isLineLevel()) {
			price = documentLine.getPriceList();
			// Para los esquemas a nivel de línea se toma la cantidad disponible a fin de
			// no acumular descuentos a nivel de línea.
			qty = documentLine.getAvailableQty();
		// Los descuentos a nivel de documento se calculan a partir del precio
		// actual de la línea tomando la cantidad total de la misma.
		} else if (discountSchema.isDocumentLevel()) {
			price = documentLine.getPrice();
			qty = documentLine.getQty();
		}
		 
		BigDecimal discountedPrice = null;     // Precio de línea descontado
		BigDecimal lineDiscountAmt     = null; // Importe de descuento de la línea.
		
		// Se obtiene la proporción del precio de la línea. Si la proporción
		// es 1 entonces el precio utilizado para el cálculo será el mismo que
		// el precio original de la línea
		price = scalePrice(price.multiply(ratio));
		// Calcula el precio descontado
		discountedPrice = 
			scalePrice(
				discountSchema.calculatePrice(
						qty, 
						price, 
						documentLine.getProductID(), 
						0, 
						getBPartnerFlatDiscount(), 
						documentLine.getDocument().getDate()
				)
			);
		
		// Si hay que calcular el descuento con impuestos, se suman los impuestos al
		// precio descontado.
		if (!netDiscount) {
			price = documentLine.getTaxedAmount(price);
			discountedPrice = documentLine.getTaxedAmount(discountedPrice);
		}
		
		// Calcula el descuento/recargo de la línea
		lineDiscountAmt = price.subtract(discountedPrice).multiply(qty);
		
		return scaleAmount(lineDiscountAmt);
	}

	/**
	 * Calcula el importe de un descuento a partir del precio y el porcentaje de
	 * descuento.
	 * 
	 * @param price
	 *            Precio original
	 * @param discountPercentage
	 *            % de descuento
	 * @return Importe del descuento.
	 */
	protected BigDecimal calculateDiscount(BigDecimal price, BigDecimal discountPercentage) {
		BigDecimal discountAmt = BigDecimal.ZERO;
		if (price != null && discountPercentage != null) {
			BigDecimal discountRate = discountPercentage.divide(
					BigDecimal.valueOf(100), 20, BigDecimal.ROUND_HALF_EVEN);
			discountAmt = scaleAmount(price.multiply(discountRate));
		}
		return discountAmt;
	}
	
	/**
	 * Aplica los descuentos a nivel de documento asociados a este calculador.
	 * Se actualiza el importe de descuento del documento mediante la invocación
	 * de {@link IDocument#setTotalDocumentDiscount(BigDecimal)}.
	 */
	private void applyDocumentLevelDiscounts() {
		BigDecimal totalDocumentDiscount = BigDecimal.ZERO;
		BigDecimal totalBPartnerDiscount = BigDecimal.ZERO;
		BigDecimal totalManualDiscount = BigDecimal.ZERO;
		BigDecimal netDiscountAmt  = null;
		BigDecimal discountBaseAmt = null;
		BigDecimal discountAmt     = null;
		BigDecimal lineDiscountAmt = null;   // Importe de descuento neto
		 									 // (sin impuestos) de una línea. 
		
		// Obtiene los descuentos a nivel de documento y resetea el importe
		// calculado de cada uno.
		List<Discount> documentLevelDiscounts = getDocumentLevelDiscounts();
		clearDiscountAmounts(documentLevelDiscounts);
		
		// Aplica cada uno de los descuentos a cada línea y va sumando
		// los importes de descuentos.
		for (IDocumentLine documentLine : getDocument().getDocumentLines()) {
			lineDiscountAmt = BigDecimal.ZERO;
			setApplyScale(false);
			for (Discount discount : documentLevelDiscounts) {
				// Calcula el descuento NETO (sin impuestos) para sumarlo al
				// descuento total neto de la línea
				netDiscountAmt = calculateDiscount(
						documentLine, 
						discount.getDiscountSchema(), 
						discount.getBaseAmt(), 
						true
				);
				
				// Si el descuento es distinto de cero implica que el descuento
				// se aplicó sobre la línea.
				if (netDiscountAmt.compareTo(BigDecimal.ZERO) != 0) {
					// Acumula el descuento al descuento de la línea actual
					lineDiscountAmt = lineDiscountAmt.add(netDiscountAmt);
					
					// Calcula el importe base del descuento calculado. Ya que el descuento
					// afecta al total de unidades de la línea, la base de cálculo
					// del descuento es entonces el precio actual multiplicado
					// por la cantidad de la línea.
					discountBaseAmt = documentLine.getPrice()
							.multiply(documentLine.getQty())
							.multiply(discount.getApplicationRatio(getDocument()));
					
					// Suma el importe de descuento calculado al importe total
					// acumulado del descuento.
					discount.addAmount(
							documentLine.getTaxedAmount(netDiscountAmt), 
							documentLine.getTaxRate(),
							documentLine.getTaxedAmount(discountBaseAmt));
									
					// Si el documento está configurado para que se le asigne el
					// descuento total neto, entonces se suma el descuento neto
					// calculado. Si no se acumula el descuento bruto (con impuestos)
					
					// Dto con impuestos
					discountAmt = documentLine.getTaxedAmount(netDiscountAmt); 
					if (getDocument().isCalculateNetDiscount()) {
						totalDocumentDiscount = totalDocumentDiscount
								.add(netDiscountAmt);
					} else {
						totalDocumentDiscount = totalDocumentDiscount
								.add(discountAmt);
					}
					
					// Asigna el importe de descuento de EC si es un descuento de EC
					// (solo puede haber uno así que el descuento calculado aquí es
					// el total de descuento de la EC).
					if (discount.isBPartnerDiscount()) {
						totalBPartnerDiscount = totalBPartnerDiscount.add(discountAmt);
						getDocument().setTotalBPartnerDiscount(totalBPartnerDiscount);
					}
					
					// Asigna el importe de descuento manual general si es un
					// descuento manual general
					// (solo puede haber uno así que el descuento calculado aquí es
					// el total de descuento manual general).
					if(discount.isManualGeneralDiscount()){
						totalManualDiscount = totalManualDiscount.add(discountAmt);
						getDocument().setTotalManualGeneralDiscount(discountAmt);
					}
				}
				
			}
			setApplyScale(true);
			// Asigna el importe de descuento neto de la línea
			documentLine.setDocumentDiscountAmt(scaleAmount(lineDiscountAmt));
		}
		// Asigna el importe de descuento total al documento (neto o total según
		// lo indicado por isCalculateNetDiscount())
		getDocument().setTotalDocumentDiscount(scaleAmount(totalDocumentDiscount));
		// Fix: Asigna el importe de descuento de la entidad comercial aquí ya
		// que cuando quedaba remanente el descuento de la entidad comercial
		// anterior, porque al for de Discounts no entra si no hay ninguno. 
		getDocument().setTotalBPartnerDiscount(totalBPartnerDiscount);
		getDocument().setTotalManualGeneralDiscount(totalManualDiscount);
		// Si hubo un descuento/recargo se asigna el ID del cargo para
		// descuentos a nivel de documento.
		if (totalDocumentDiscount.compareTo(BigDecimal.ZERO) != 0) {
			getDocument().setDocumentDiscountChargeID(getDocumentDiscountChargeID());
		}
	}

	/**
	 * Aplica los descuentos de documento asociados a este calculador. 
	 * Se actualiza el importe de descuento del documento asociado a este
	 * calculador mediante la invocación de
	 * {@link IDocument#setTotalDocumentDiscount(BigDecimal)}.
	 */
	public void applyDocumentHeaderDiscounts() {
		validateAssociatedDocument();
		if (!hasDiscountConfig()) {
			return;
		}
		// Aplicación de esquemas a nivel de documento.
		applyDocumentLevelDiscounts();
	}

	/**
	 * Aplica una lista de descuentos a las líneas del documento asociado a este
	 * calculador. Se actualizan los importes de descuentos de cada línea que
	 * sufre algún descuento.
	 */
	private void applyLineDiscounts(List<Discount> discounts) {
		BigDecimal netDiscountAmt   = null;  // Importe temporal de descuento NETO de una línea 
		BigDecimal lineDiscountAmt  = null;  // Importe de descuento neto
											 // (sin impuestos) de una línea. 
		BigDecimal discountBaseAmt  = null;  // Importe base del descuento calculado.
		BigDecimal lineBonusAmt     = null;  // Importe de descuento aplicado como bonificación.
		BigDecimal lineToPriceAmt   = null;  // Importe de descuento aplicado como dto. al precio.
		
		// Resetea el importe calculado de cada descuento.
		clearDiscountAmounts(discounts);
		
		for (IDocumentLine documentLine : getDocument().getDocumentLines()) {
			lineDiscountAmt = BigDecimal.ZERO;
			lineBonusAmt    = BigDecimal.ZERO;
			lineToPriceAmt  = BigDecimal.ZERO;
			setApplyScale(false);
			// Itera por los descuentos para aplicarlos a la línea.
			for (Discount discount : discounts) {
				// Si la línea no tiene cantidad disonible para aplicación de descuentos
				// entonces no hay descuento que aplicar.
				if (documentLine.getAvailableQty().compareTo(BigDecimal.ZERO) == 0) {
					break;
				}

				// Calcula el importe de descuento NETO para la línea según el descuento.
				netDiscountAmt = calculateDiscount(
						documentLine, 
						discount.getDiscountSchema(), 
						discount.getBaseAmt(), 
						true
				);
				
				// Si el descuento es distinto de cero implica que el descuento
				// se aplicó sobre la línea.
				if (netDiscountAmt.compareTo(BigDecimal.ZERO) != 0) {
					// Calcula el importe base del descuento calculado. Ya que el descuento
					// afecta al total de unidades libres de la línea, la base de cálculo
					// del descuento es entonces el precio unitario de lista multiplicado
					// por la cantidad disponible (para aplicación de descuento) de la línea.
					discountBaseAmt = documentLine.getPriceList().multiply(
							documentLine.getAvailableQty());
					// Suma el importe calculado al importe total de descuento de la línea.
					lineDiscountAmt = lineDiscountAmt.add(netDiscountAmt);

					// Suma el importe de descuento calculado (con impuestos) al importe total
					// acumulado del descuento.
					discount.addAmount(
							documentLine.getTaxedAmount(netDiscountAmt), 
							documentLine.getTaxRate(),
							documentLine.getTaxedAmount(discountBaseAmt));

					// Los descuentos de este tipo se
					// aplican sobre el total de unidades disponibles o no se
					// aplican, por eso se asigna como cantidad descontada el
					// total de unidades que tiene esta línea.
					documentLine.setDiscountedQty(documentLine.getQty());

					// Dependiendo del tipo de aplicación del descuento se acumula
					// el descuento calculado para la línea en la variable de
					// bonificaciones o descuentos al precio.
					if (discount.isBonusApplication()) {
						lineBonusAmt = lineBonusAmt.add(lineDiscountAmt);
					} else if (discount.isToPriceApplication()) {
						lineToPriceAmt = lineToPriceAmt.add(lineDiscountAmt);
					}
				}
				
			}
			setApplyScale(true);
			// Asigna los importes de descuento de la línea y recalcula el precio unitario.
			if (lineDiscountAmt.compareTo(BigDecimal.ZERO) != 0) {
				// Descuentos al precio
				documentLine.setLineDiscountAmt(documentLine.getLineDiscountAmt()
						.add(lineToPriceAmt));
				// Descuentos por bonificaciones.
				documentLine.setLineBonusAmt(documentLine.getLineBonusAmt()
						.add(lineBonusAmt));
				calculateDocumentLinePrice(documentLine);
			}
		}
	}
	
	/**
	 * Aplica los combos válidos sobre las líneas del documento asociado.
	 */
	private void applyCombos() {
		// Si aún no se han cargado los combos o cambió la fecha del documento
		// entonces se recargan los combos válidos para la fecha del documento.
		if (getValidCombos() == null || getValidCombos().isEmpty()
				|| documentDateChanged()) {
			loadCombos();
		}
		
		// Resetea la lista de descuentos por combos para realizar un nuevo
		// cálculo.
		getComboDiscounts().clear();
		
		// Crea el matching de artículos en el documento para la determinación
		// de que combos aplicar.
		ProductMatching documentProductMatching = createProductMatching();
		ProductMatching comboProductMatching = null;
		
		boolean applyCombo;
		// Se recorren los combos válidos para buscar aplicaciones en las líneas
		// del documento.
		for (MCombo combo : getValidCombos()) {
			// Obtiene el matching de artículos del combo para compararlos con
			// el matching de artículos del documento y poder determinar si el
			// documento incluye el combo o no.
			comboProductMatching = combo.getProductMatching();
			do {
				// Mediante el match determina si se debe aplicar o no el combo.
				applyCombo = documentProductMatching.match(comboProductMatching,
						MatchingCompareType.CONTAINS); 
				if (applyCombo) {
					// Aplica el combo y actualiza las cantidades disponibles
					// del matching del pedido para la evaluación del resto de
					// los combos.
					applyCombo(combo);
					documentProductMatching.reduceQty(comboProductMatching);
				}
			// El combo actual se sigue aplicando mientras se encuentren
			// instancias del mismo dentro de las líneas del documento. (un
			// combo puede aparecer mas de una vez en el documento)	
			} while (applyCombo);
		}
	}

	/**
	 * Aplica el combo a las líneas del documento asociado. Asume que el
	 * documento contiene suficientes líneas y cantidades para que el combo sea
	 * aplicado correctamente.
	 * 
	 * @param combo
	 *            Combo a aplicar.
	 */
	private void applyCombo(MCombo combo) {
		IDocumentLine documentLine    = null;
		BigDecimal totalDiscountedQty = null;
		BigDecimal discountedQty      = null;
		BigDecimal netDiscountAmt     = null;
		BigDecimal discountBaseAmt    = null;
		BigDecimal lineDiscountAmt    = null;
		Discount discount             = null;
		BigDecimal discountQtyRemains = null;
		
		// Crea el descuento que almacena los datos del combo aplicado.
		discount = new Discount(combo);
		
		// Por cada línea del combo, busca líneas en el documento para aplicar el descuento
		// hasta que se cubra la cantidad total de la línea del combo.
		for (MComboLine comboLine : combo.getLines(false)) {
			totalDiscountedQty = BigDecimal.ZERO;
			do {
				// Busca una línea del documento que matchee con la línea del combo.
				documentLine = findDocumentLine(comboLine.getM_Product_ID());
				// Determina la cantidad real descontada dependiendo si la línea
				// del documento cubre toda la línea del combo o no.
				if (documentLine.getAvailableQty().compareTo(comboLine.getQty()) < 0) {
					// Determino la cantidad restante del combo. Si la cantidad
					// disponible de la línea es menor que la cantidad restante
					// del combo a aplicar, entonces tomo esa cantidad
					// disponible, sino el rstante de aplicación del combo
					discountQtyRemains = comboLine.getQty().subtract(totalDiscountedQty);
					if(documentLine.getAvailableQty().compareTo(discountQtyRemains) < 0){
						discountedQty = documentLine.getAvailableQty();
					}
					else{
						discountedQty = discountQtyRemains;
					}
				} else {
					discountedQty = comboLine.getQty();
				}
				// Calcula el importe de descuento si es que la línea del combo
				// tiene un descuento asociado.
				if (comboLine.getDiscount().compareTo(BigDecimal.ZERO) != 0) {
					// Calcula el importe de descuento unitario NETO
					netDiscountAmt = calculateDiscount(
							documentLine.getPriceList(),
							comboLine.getDiscount());
					// Obtiene el importe de descuento de la línea
					lineDiscountAmt = netDiscountAmt.multiply(discountedQty);
					// Obtiene el importe base de cálculo del descuento
					discountBaseAmt = documentLine.getPriceList().multiply(discountedQty);
					
					// Si la aplicación del combo es bonificación entonces suma
					// el importe de descuento calculado al importe de
					// bonificación de la línea actual.
					if (combo.isBonusApplication()) {
						documentLine.setLineBonusAmt(documentLine
								.getLineBonusAmt().add(lineDiscountAmt));

					// Si no es bonificación, entonces es descuento
					// "al precio". Suma el descuento al importe total de
					// descuento al precio de la línea
					} else {
						documentLine.setLineDiscountAmt(documentLine
								.getLineDiscountAmt().add(lineDiscountAmt));
					}
					
					// Suma el total descontado al acumulado que almacena el
					// descuento por el combo. Con esto se obtiene
					// el importe total del descuento aplicado
					// por esta instancia del combo, a su vez discriminado por
					// tasa de impuesto (ya que el combo puede involucrar
					// artículos con diferentes tasas de impuestos)
					discount.addAmount(
							documentLine.getTaxedAmount(lineDiscountAmt),
							documentLine.getTaxRate(), 
							documentLine.getTaxedAmount(discountBaseAmt));
				}

				// Calcula el nuevo precio unitario de la línea (precio promedio).
				calculateDocumentLinePrice(documentLine);
				
				// Actualiza las cantidades ya descontadas de esta línea para
				// que no vuelvan a sufrir algún otro descuento ya sea por combo
				// o por cualquier otro medio.
				documentLine.setDiscountedQty(documentLine.getDiscountedQty()
						.add(discountedQty));
				// Computa la cantidad total descontada, mientras no llegue a
				// cubrir la cantidad de la línea del combo seguirá buscando
				// líneas del pedido para llegar a descontar exactamente la
				// misma cantidad que está configurada en la línea del combo.
				totalDiscountedQty = totalDiscountedQty.add(discountedQty);
			
			} while (totalDiscountedQty.compareTo(comboLine.getQty()) < 0);
		}
		
		// Agrega el descuento calculado por la aplicación del combo.
		getComboDiscounts().add(discount);
		
		log.fine("Applied Combo: " + combo.getName());
	}
	
	/**
	 * Aplica las promociones válidas sobre las líneas del documento asociado.
	 */
	private void applyPromotions() {
		// Si aún no se han cargado las promociones o cambió la fecha del documento
		// entonces se recargan las promociones válidas para la fecha del documento.
		if (getPromotionDiscounts() == null
				|| getPromotionDiscounts().isEmpty() || documentDateChanged()) {
			loadPromotions();
		}
		
		// Aplica los descuentos por promociones.
		applyLineDiscounts(getPromotionDiscounts());
	}

	/**
	 * Busca una línea del documento cuyo artículo tenga el ID
	 * <code>productID</code> la cantidad disponible para aplicación de
	 * descuentos sea mayor que cero.
	 * 
	 * @param productID
	 *            ID de artículo
	 * @return La línea o <code>null</code> si no se encuentra ninguna.
	 */
	private IDocumentLine findDocumentLine(int productID) {
		IDocumentLine foundLine = null;
		for (IDocumentLine documentLine : getDocument().getDocumentLines()) {
			if (documentLine.getProductID() == productID
					&& documentLine.getAvailableQty().compareTo(BigDecimal.ONE) >= 0) {
				
				foundLine = documentLine;
				break;
			}
		}
		return foundLine;
	}

	/**
	 * Calcula y asigna el precio unitario de una línea de documento a partir de
	 * sus importes de descuentos.
	 * 
	 * @param documentLine Línea de documento a modificar.
	 */
	private void calculateDocumentLinePrice(IDocumentLine documentLine) {
		BigDecimal lineDiscountAmt = documentLine.getLineDiscountAmt();
		BigDecimal lineBonusAmt    = documentLine.getLineBonusAmt();
		// Cálculo del nuevo precio. Se calcula primero el precio de lista de la
		// línea, luego se le restan los importes de descuentos al precio y por
		// bonificaciones, y finalmente se divide el resulta por la cantidad de
		// la línea para obtener el precio unitario de la misma.
		// Formula:
		//
		//   (PriceList * Qty - LineBonusAmt - LineDiscountAmt) / Qty.
		//
		BigDecimal newPrice = (documentLine.getPriceList()
				.multiply(documentLine.getQty()).subtract(lineBonusAmt)
				.subtract(lineDiscountAmt)).divide(documentLine.getQty(), 20,
				BigDecimal.ROUND_HALF_EVEN);
		documentLine.setPrice(scalePrice(newPrice));
	}

	/**
	 * @return Un nuevo matching con los artículos y cantidades disponibles del
	 *         documento asociado.
	 */
	private ProductMatching createProductMatching() {
		ProductMatching productMatching = new ProductMatching();
		// Agrega el artículo y su cantidad disponible de cada línea del
		// documento.
		for (IDocumentLine documentLine : getDocument().getDocumentLines()) {
			productMatching.addProduct(documentLine.getProductID(),
					documentLine.getAvailableQty());
		}
		return productMatching;
	}

	/**
	 * Aplica los descuentos de línea de documento asociados a este calculador.
	 * Los descuentos de línea evaluados son:
	 * <ul>
	 * <li>Combos</li>
	 * <li>Promociones</li>
	 * <li>Esquemas de Descuento a nivel Línea</li>
	 * </ul>
	 * <p>
	 * El orden de aplicación y la superposición de estos descuentos es obtenida
	 * desde la configuración de la compañía.
	 * </p>
	 * <p>
	 * Para cada línea que sufra un descuento se actualiza el precio sin
	 * impuestos de la misma.
	 * </p>
	 */
	public void applyDocumentLineDiscounts() {
		validateAssociatedDocument();
		if (!hasDiscountConfig()) {
			return;
		}
		
		// Resetea las cantidades disponibles e importes de las líneas para
		// realizar un nuevo cálculo de combos, promociones y descuentos a 
		// nivel de línea.
		for (IDocumentLine documentLine : getDocument().getDocumentLines()) {
			documentLine.setDiscountedQty(BigDecimal.ZERO);
			documentLine.setLineBonusAmt(BigDecimal.ZERO);
			documentLine.setLineDiscountAmt(BigDecimal.ZERO);
			calculateDocumentLinePrice(documentLine);
		}

		// Obtiene las prioridades de aplicación de las diferentes clases de
		// descuentos en las líneas y las aplica en su correspondiente orden.
		for (String discountKind : getDiscountConfig().getLineDiscountsList()) {
			if (MDiscountConfig.DISCOUNT_ProductsCombo.equals(discountKind)) {
				// Aplicación de combos
				applyCombos();
			} else if (MDiscountConfig.DISCOUNT_Promotion.equals(discountKind)) {
				// Aplicación de promociones
				applyPromotions();
			} else if (MDiscountConfig.DISCOUNT_BPartnerDiscountSchema.equals(discountKind)) {
				// Aplicación de descuentos (esquemas) a nivel de línea.
				applyLineDiscounts(getLineLevelDiscounts());
			}
		}
	}
	
	/**
	 * <p>
	 * Aplica todos los descuentos asociados a este calculador sobre el
	 * documento asociado. Se aplicarán primero los descuentos de línea
	 * y luego los que tiene nivel de documento.
	 * </p>
	 * <p>
	 * Se actualizan los importes de las líneas del documento y también el
	 * importe de descuento a nivel documento.
	 * </p>
	 */
	public void applyDiscounts() {
		validateAssociatedDocument();

		// Aplica los descuentos de línea de documento.
		applyDocumentLineDiscounts();
		// Aplica los descuentos del encabezado del documento.
		applyDocumentHeaderDiscounts();
	}
	
	/**
	 * @return Devuelve el Esquema de Descuento de la Entidad Comercial
	 */
	public MDiscountSchema getBPartnerDiscountSchema() {
		return getBPartnerDiscount() != null ? getBPartnerDiscount().getDiscountSchema()
				: null;
	}

	/**
	 * @return Devuelve el porcentaje de descuento neto asignado a la
	 * entidad comercial.
	 */
	public BigDecimal getBPartnerFlatDiscount() {
		return bPartnerFlatDiscount;
	}

	/**
	 * @return Devuelve el contexto de la aplicación. Por defecto es
	 * {@link Env#getCtx()}
	 */
	public Properties getCtx() {
		return ctx;
	}

	/**
	 * Asigna el contexto a utilizar en instanciaciones de objetos del modelo
	 * @param ctx Contexto a asignar
	 */
	public void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

	/**
	 * @return El documento asociado a este calculador de descuentos.
	 */
	public IDocument getDocument() {
		return document;
	}

	/**
	 * Asigna el documento asociado a este calculador de descuento. Las
	 * aplicaciones de descuentos que no especifiquen un documento en su
	 * invocación serán aplicadas sobre este documento que se está asociando.
	 * 
	 * @param document
	 *            Documento a asociar
	 */
	public void setDocument(IDocument document) {
		this.document = document;
		this.documentDate = document != null ? document.getDate() : null;
	}

	/**
	 * @return El ID de la moneda en la que trabaja este calcular de descuento
	 */
	public int getCurrencyID() {
		return currencyID;
	}

	/**
	 * Asigna la moneda en la que están expresados los importes con los que
	 * trabaja este calculador de descuentos. Es utilizada para aplicar
	 * redondeos según la precisión de la moneda
	 * 
	 * @param currencyID
	 *            ID de moneda a asignar
	 */
	public void setCurrencyID(int currencyID) {
		if (currencyID > 0) {
			this.currencyID = currencyID;
		}
	}

	/**
	 * Indica si se debe aplicar el esquema de descuento de la EC basándose en
	 * la configuración actual de aplicación de descuentos y en los esquemas
	 * generales y de EC asociados a este calculador.
	 */
	public boolean isBPartnerDocumentDiscountApplicable() {
		return 
			hasDiscountConfig() 
			&& (
			// Se aplican todos los esquemas
			getDiscountConfig().isApplyAllDocumentDiscount()
			
			// Primero el esquema de EC
			|| MDiscountConfig.DISCOUNT_BPartnerDiscountSchema
					.equals(getDiscountConfig().getDocumentDiscount1())
			
			// Primero los esquemas generales, luego EC y NO hay esquemas generales asociados
			|| (MDiscountConfig.DISCOUNT_GeneralDiscountSchema
					.equals(getDiscountConfig().getDocumentDiscount1())
					
					&& MDiscountConfig.DISCOUNT_BPartnerDiscountSchema
					.equals(getDiscountConfig().getDocumentDiscount2())
					
					&& !hasGeneralDiscounts()
					
					&& !isAssumeGeneralDiscountAdded())
			);		
	}

	
	/**
	 * Indica si se debe aplicar el esquema de descuento de la EC basándose en
	 * la configuración actual de aplicación de descuentos y en los esquemas
	 * generales y de EC asociados a este calculador.
	 * @param discountContextType
	 *            tipo de contexto del esquema de descuento
	 * @param bpartnerDiscountContextOfUse
	 *            la configuración de la entidad comercial con respecto al
	 *            esquema de descuento configurado debe ser el mismo contexto
	 *            configurado en este calculador
	 */
	public boolean isBPartnerDocumentDiscountApplicable(String discountContextType, String bpartnerDiscountContextOfUse) {
		return 
			isBPartnerDocumentDiscountApplicable()
			&&
			// El contexto donde estamos es Compra o el contexto donde estoy
			// trabajando es Cobro y el tipo de contexto del esquema pasado
			// como parámetro debe ser Financiero obligatoriamente ya que al
			// momento del Cobro sólo se pueden usar esquemas de descuento
			// Financieros 
			discountContextTypeValidation(discountContextType)
			&&
			// El contexto de uso del esquema de descuento de la entidad
			// comercial debe ser igual al contexto del calculador
			(bpartnerDiscountContextOfUse == null || bpartnerDiscountContextOfUse != null
					&& bpartnerDiscountContextOfUse.equals(getContext()));
	}
	
	
	/**
	 * @return Indica si el esquema de EC es aplicable, independientemente de si
	 *         es un esquema a nivel de línea o uno a nivel de documento.
	 */
	public boolean isBPartnerDiscountApplicable() {
		return
			hasDiscountConfig()
			&&
			// Existe el esquema de EC
		 	hasBPartnerDiscount() 
				// Si es a nivel documento tiene que ser aplicable según la 
				// configuración de descuentos a nivel de documento.
				&& ((getBPartnerDiscount().isDocumentLevel() 
							&& isBPartnerDocumentDiscountApplicable()) 
				// O si es a nivel de línea, la configuración de dtos a este nivel debe
				// incluir en la lista de prioridades al esquema de entidad comercial.
					|| (getBPartnerDiscount().isLineLevel() 
							&& getDiscountConfig().getLineDiscountsList()
									.contains(MDiscountConfig.DISCOUNT_BPartnerDiscountSchema)));
	}

	/**
	 * @param discountContextType
	 *            tipo de contexto del esquema de descuento
	 * @param bpartnerDiscountContextOfUse
	 *            la configuración de la entidad comercial con respecto al
	 *            esquema de descuento configurado debe ser el mismo contexto
	 *            configurado en este calculador
	 * @return Indica si el esquema de EC es aplicable, independientemente de si
	 *         es un esquema a nivel de línea o uno a nivel de documento.
	 */
	public boolean isBPartnerDiscountApplicable(String discountContextType, String bpartnerDiscountContextOfUse) {
		return
			hasDiscountConfig()
			&&
			// Existe el esquema de EC
		 	hasBPartnerDiscount() 
			// Si es a nivel documento tiene que ser aplicable según la 
			// configuración de descuentos a nivel de documento.
				&& ((getBPartnerDiscount().isDocumentLevel() && isBPartnerDocumentDiscountApplicable(
						discountContextType, bpartnerDiscountContextOfUse)) 
			// O si es a nivel de línea, la configuración de dtos a este nivel debe
			// incluir en la lista de prioridades al esquema de entidad comercial.
				|| (getBPartnerDiscount().isLineLevel() 
						&& getDiscountConfig().getLineDiscountsList()
								.contains(MDiscountConfig.DISCOUNT_BPartnerDiscountSchema)))
			&&
			// El contexto donde estamos en Compra o el contexto donde estoy
			// trabajando es Cobro y el tipo de contexto del esquema pasado
			// como parámetro debe ser Financiero obligatoriamente ya que al
			// momento del Cobro sólo se pueden usar esquemas de descuento
			// Financieros 
			discountContextTypeValidation(discountContextType)
			&&
			// El contexto de uso del esquema de descuento de la entidad
			// comercial debe ser igual al contexto del calculador
			(bpartnerDiscountContextOfUse == null || bpartnerDiscountContextOfUse != null
					&& bpartnerDiscountContextOfUse.equals(getContext()));
	}
	
	
	/**
	 * Indica si se deben aplicar los esquemas de descuento generales basándose
	 * en la configuración actual de aplicación de descuentos y en 
	 * los esquemas generales y de EC asociados a este calculador.
	 */
	public boolean isGeneralDocumentDiscountApplicable() {
		return
			hasDiscountConfig() 
			&& (
			// Se aplican todos los esquemas
			getDiscountConfig().isApplyAllDocumentDiscount()

			// Primero el esquemas generales
			|| MDiscountConfig.DISCOUNT_GeneralDiscountSchema
					.equals(getDiscountConfig().getDocumentDiscount1())

			// Primero el esquema de EC, luego Generales y NO hay un esquema de EC asociado
			|| (MDiscountConfig.DISCOUNT_BPartnerDiscountSchema
					.equals(getDiscountConfig().getDocumentDiscount1())
		
				&& MDiscountConfig.DISCOUNT_GeneralDiscountSchema
					.equals(getDiscountConfig().getDocumentDiscount2())
		
				&& (!hasBPartnerDiscount() || getBPartnerDiscount().isLineLevel()))
			);	
	}

	
	/**
	 * Indica si se deben aplicar los esquemas de descuento generales basándose
	 * en la configuración actual de aplicación de descuentos y en los esquemas
	 * generales y de EC asociados a este calculador y en el tipo de contexto
	 * del esquema de descuento en particular
	 * 
	 * @param discountContextType
	 *            tipo de contexto del esquema de descuento
	 */
	public boolean isGeneralDocumentDiscountApplicable(String discountContextType) {
		return 
			isGeneralDocumentDiscountApplicable() 
			&&
			// El contexto donde estamos en Compra o el contexto donde estoy
			// trabajando es Cobro y el tipo de contexto del esquema pasado
			// como parámetro debe ser Financiero obligatoriamente ya que al
			// momento del Cobro sólo se pueden usar esquemas de descuento
			// Financieros 
			discountContextTypeValidation(discountContextType);
	}

	/**
	 * @param discountContextType
	 *            tipo de contexto de descuento
	 * @return true si la validación del tipo de contexto parámetro de un
	 *         esquema de descuento específico con respecto al contexto donde
	 *         está trabajando el calculador de descuento son compatibles
	 */
	public boolean discountContextTypeValidation(String discountContextType){
		return (getContext().equals(MBPartner.DISCOUNTCONTEXT_Bill) || getContext()
				.equals(MBPartner.DISCOUNTCONTEXT_Receipt)
				&& discountContextType
						.equals(MDiscountSchema.DISCOUNTCONTEXTTYPE_Financial));
	}

	/**
	 * Asigna el Esquema de Descuento y el porcentaje de descuento neto de la
	 * Entidad Comercial a utilizar para el cálculo de descuentos, a partir de
	 * un ID de Entidad Comercial obteniendo estos datos directamente de la EC
	 * indicada.
	 * 
	 * @param bPartnerID
	 *            ID de la Entidad Comercial de la cual se obtiene los datos que
	 *            serán utilizados para el cálculo de descuentos.
	 * @param isSOTrx
	 *            Necesario para determinar si se obtiene el esquema de
	 *            descuento configurado para Cliente (
	 *            <code>isSOTrx = true</code> ) o Proveedor (
	 *            <code>isSOTrx = false</code>).
	 * @param bpartnerDiscountContext
	 *            contexto de uso del esquema de descuento de entidad comercial
	 */
	public void loadBPartnerDiscount(int bPartnerID, boolean isSOTrx, String bpartnerDiscountContext) {
        int discountSchemaID = 0;        // ID del esquema de descuento de la EC
        BigDecimal flatDiscount = null;  // % de descuento neto de la EC
		
        // Obtiene los datos necesarios de la EC
        String sql = 
        	"SELECT M_DiscountSchema_ID, " +
        	       "PO_DiscountSchema_ID, " +
        	       "FlatDiscount " + 
        	 "FROM C_BPartner " +
        	 "WHERE C_BPartner_ID = ?";
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = DB.prepareStatement(sql);
            pstmt.setInt(1, bPartnerID);
            rs = pstmt.executeQuery();

            if( rs.next()) {
                // Obtiene el ID de esquema de descuento dependiendo si es o no
            	// transacción de ventas.
            	discountSchemaID = 
                	rs.getInt(isSOTrx ? "M_DiscountSchema_ID" : "PO_DiscountSchema_ID");
                // Obtiene el % de descuento neto
            	flatDiscount = rs.getBigDecimal("FlatDiscount");
            }
        } catch(Exception e) {
            log.log(Level.SEVERE,"Error loading BPartner Data. SQL="+sql, e);
        } finally {
        	try {
        		if (rs != null) rs.close();
        		if (pstmt != null) pstmt.close();
        	} catch (SQLException e) { }
        }
        
        // Obtiene el esquema de descuento si la EC tenía uno asignado
        MDiscountSchema discountSchema = null;
        if (discountSchemaID > 0) {
        	// Debug: se instancia mediante new para evitar cacheos, descomentar
        	// la línea que utiliza el método get, el cual cachea
        	// los esquemas agilizando la obtención del mismo.
        	
        	// discountSchema = MDiscountSchema.get(getCtx(), discountSchemaID);
        	discountSchema = new MDiscountSchema(getCtx(), discountSchemaID, null);
        }
        // Asegura un valor numérico para el % de descuento neto
        if( flatDiscount == null ) {
            flatDiscount = BigDecimal.ZERO;
        }

        // Carga los datos de la Entidad Comercial
        loadBPartnerDiscount(discountSchema, flatDiscount, bpartnerDiscountContext);
	}

	/**
	 * Asigna el Esquema de Descuento y el porcentaje de descuento neto de la
	 * Entidad Comercial a utilizar para el cálculo de descuentos.
	 * 
	 * @param discountSchema
	 *            Esquema de Descuento a asignar
	 * @param flatDiscount
	 *            Procentaje de descuento neto de la Entidad Comercial
	 *            (utilizado en esquemas de descuento cuyo % está determinado
	 *            por la EC).
	 * @param bpartnerDiscountContext
	 *            contexto de uso del esquema de descuento de la de entidad
	 *            comercial
	 */
	public void loadBPartnerDiscount(MDiscountSchema discountSchema, BigDecimal flatDiscount, String bpartnerDiscountContext) {
		loadBPartnerDiscount(discountSchema, null, flatDiscount, bpartnerDiscountContext);
	}

	/**
	 * Asigna el Esquema de Descuento y el porcentaje de descuento neto de la
	 * Entidad Comercial a utilizar para el cálculo de descuentos. El cálculo se
	 * realiza por el monto base pasado como parámetro, el cual puede ser null y
	 * en ese caso se tomará el total del documento.
	 * 
	 * @param discountSchema
	 *            Esquema de Descuento a asignar
	 * @param baseAmt
	 *            monto base a contemplar por el esquema de descuento de entidad
	 *            comercial
	 * @param flatDiscount
	 *            Procentaje de descuento neto de la Entidad Comercial
	 *            (utilizado en esquemas de descuento cuyo % está determinado
	 *            por la EC).
	 * @param bpartnerDiscountContext
	 *            contexto de uso del esquema de descuento de la entidad
	 *            comercial
	 */
	public void loadBPartnerDiscount(MDiscountSchema discountSchema, BigDecimal baseAmt, BigDecimal flatDiscount, String bpartnerDiscountContext) {
		// Asigna el esquema. Es necesario que el esquema de descuento exista en la BD. 
		// Si no existe no se tiene en cuenta.
		MDiscountSchema bPartnerDiscountSchema = 
			discountSchema !=null 
				&& discountSchema.getM_DiscountSchema_ID() == 0 ? null : discountSchema;
		
		// Crea el descuento y lo asigna como descuento de la entidad comercial.
		// El ID del descuento es 0 ya que es el número reservado para el
		// descuento de EC. El importe base es null indicando que se tome el
		// total del documento a la hora de aplicar el descuento.
		if (bPartnerDiscountSchema != null) {
			this.bPartnerDiscount = new Discount(BPARTNER_DISCOUNT_ID, null,
					bPartnerDiscountSchema, baseAmt, DiscountKind.BPartnerDiscountSchema);
		} else {
			this.bPartnerDiscount = null;
		}
		
		// Asigna el % de descuento.
		this.bPartnerFlatDiscount = 
			flatDiscount == null ? BigDecimal.ZERO : flatDiscount;
		// Contexto de uso del esquema de la entidad comercial
		setBpartnerDiscountContext(bpartnerDiscountContext);
	}

	/**
	 * @return Devuelve los descuentos generales asociados a este Calculador.
	 */
	private Map<Integer, Discount> getGeneralDiscounts() {
		return generalDiscounts;
	}
	
	/**
	 * Devuelve los esquemas de descuento (EC y Generales) que son aplicables
	 * según la configuración actual de descuentos y cuyo nivel de acumulación
	 * es igual a <code>level</code>.
	 * 
	 * @param level
	 *            Nivel de acumulación
	 * @return Una lista con las instancias de los descuentos.
	 */
	private List<Discount> getLevelDiscounts(String level) {
		List<Discount> discounts = new ArrayList<Discount>();

		if (level == null) {
			return discounts;
		}
		
		// Agrega el descuento manual general en caso que exista
		if (hasManualGeneralDiscount()
				&& level.equals(MDiscountSchema.CUMULATIVELEVEL_Document)) {
			discounts.add(getManualGeneralDiscount());
		}
		
		// Agrega el descuento de la EC si es aplicable y si es un
		// descuento cuyo nivel es igual al nivel parámetro
		if (hasBPartnerDiscount() && (!level.equals(MDiscountSchema.CUMULATIVELEVEL_Document) 
				|| isBPartnerDocumentDiscountApplicable(getBPartnerDiscountSchema().getDiscountContextType(), getBpartnerDiscountContext())) 
			&& level.equals(getBPartnerDiscount().getLevel())) {
			discounts.add(getBPartnerDiscount());
		}
		
		// Agrega los descuentos generales con el nivel indicado solo
		// si los descuentos generales son aplicables según la configuración.
		if ((!level.equals(MDiscountSchema.CUMULATIVELEVEL_Document) 
				|| isGeneralDocumentDiscountApplicable()) 
			 && hasGeneralDiscounts()) {
			
			for (Discount generalDiscount : getGeneralDiscounts().values()) {
				if (level.equals(generalDiscount.getLevel())) {
					// Solo se tienen en cuenta los descuentos cuyo importe base
					// de aplicación sea mayor que cero o sea null (lo cual
					// implica que el importe base se toma como el importe total
					// del documento al cual se aplica el descuento)
					if (generalDiscount.getBaseAmt() == null 
							|| generalDiscount.getBaseAmt().compareTo(BigDecimal.ZERO) > 0) {
						discounts.add(generalDiscount);
					}
				}
			}
		}
		
		return discounts;
	}
	
	/**
	 * Devuelve los esquemas de descuento (EC y Generales) que son aplicables
	 * según la configuración actual de descuentos y son a nivel de documento
	 * 
	 * @return Una lista con las instancias de los descuentos a nivel de documento.	 
	 */
	private List<Discount> getDocumentLevelDiscounts() {
		return getLevelDiscounts(MDiscountSchema.CUMULATIVELEVEL_Document);
	}

	/**
	 * Devuelve los esquemas de descuento (EC y Generales) que son aplicables
	 * según la configuración actual de descuentos y son a nivel de línea de
	 * documento
	 * 
	 * @return Una lista con las instancias de los descuentos a nivel de línea.
	 */
	private List<Discount> getLineLevelDiscounts() {
		return getLevelDiscounts(MDiscountSchema.CUMULATIVELEVEL_Line);
	}

	/**
	 * Agrega un Esquema de Descuento General. El esquema se aplicará
	 * tomando como importe base el importe total del documento en el
	 * momento de la aplicación. 
	 * 
	 * @param generalDiscountSchema
	 *            Esquema de Descuento asociado al descuento general agregado
	 * @param generalDiscountKind
	 *            Tipo del descuento general agregado
	 * @return El ID del descuento agregado si se pudo agregar correctamente.
	 *         Mediante este ID luego los clientes podrán actualizar los datos
	 *         del descuento. Devuelve <code>null</code> si
	 *         <code>generalDiscountSchema</code> es <code>null</code> o no es
	 *         un esquema de ámbito general (
	 *         <code>{@link MDiscountSchema#isGeneralScope()} == false</code>)
	 */
	public Integer addGeneralDiscount(MDiscountSchema generalDiscountSchema,
			GeneralDiscountKind generalDiscountKind) {
		return addGeneralDiscount(generalDiscountSchema, generalDiscountKind, null, null);
	}

	/**
	 * Agrega un nuevo Descuento General. El esquema del descuento asociado se
	 * aplicará sobre el importe base indicado por <code>baseAmt</code>.
	 * 
	 * @param generalDiscountSchema
	 *            Esquema de Descuento asociado al descuento general agregado
	 * @param generalDiscountKind
	 *            Tipo del descuento general agregado
	 * @param baseAmt
	 *            Importe base para la aplicación. Si es <code>null</code> se
	 *            tomará el total del documento como base al momento de la
	 *            aplicación del esquema
	 * @return El ID del descuento agregado si se pudo agregar correctamente.
	 *         Mediante este ID luego los clientes podrán actualizar los datos
	 *         del descuento. Devuelve <code>null</code> si
	 *         <code>generalDiscountSchema</code> es <code>null</code> o no es
	 *         un esquema de ámbito general (
	 *         <code>{@link MDiscountSchema#isGeneralScope()} == false</code>)
	 */
	public Integer addGeneralDiscount(MDiscountSchema generalDiscountSchema,
			GeneralDiscountKind generalDiscountKind, BigDecimal baseAmt) {
		return addGeneralDiscount(generalDiscountSchema, generalDiscountKind, baseAmt, null);
	}

	/**
	 * Agrega un nuevo Descuento General. El esquema del descuento asociado se
	 * aplicará sobre el importe base indicado por <code>baseAmt</code>.
	 * 
	 * @param generalDiscountSchema
	 *            Esquema de Descuento asociado al descuento general agregado
	 * @param generalDiscountKind
	 *            Tipo del descuento general agregado
	 * @param baseAmt
	 *            Importe base para la aplicación. Si es <code>null</code> se
	 *            tomará el total del documento como base al momento de la
	 *            aplicación del esquema
	 * @param discountDescription
	 *            Breve descripción del descuento agregado (puede ser
	 *            <code>null</code>). Esta descripción será la que efectivamente
	 *            se guarde en la BD si se le indica a este calculador que
	 *            persista los descuentos aplicados al documento.
	 * @return El ID del descuento agregado si se pudo agregar correctamente.
	 *         Mediante este ID luego los clientes podrán actualizar los datos
	 *         del descuento. Devuelve <code>null</code> si
	 *         <code>generalDiscountSchema</code> es <code>null</code> o no es
	 *         un esquema de ámbito general (
	 *         <code>{@link MDiscountSchema#isGeneralScope()} == false</code>)
	 */
	public Integer addGeneralDiscount(MDiscountSchema generalDiscountSchema,
			GeneralDiscountKind generalDiscountKind, BigDecimal baseAmt,
			String discountDescription) {
		
		// Valida que el esquema existe y sea de ámbito general
		if (generalDiscountSchema == null
				|| !generalDiscountSchema.isGeneralScope()) {
			return null;
		}
		
		// Obtiene el ID que tendrá el nuevo descuento
		Integer discountID = null;
		synchronized (this) {
			discountID = nextDiscountID;
			nextDiscountID++;
		}
		// Crea la estructura del nuevo descuento y lo agrega al conjunto de
		// descuentos generales asociados.
		Discount discount = new Discount(discountID, discountDescription,
				generalDiscountSchema, baseAmt, generalDiscountKind.toDiscountKind());
		
		getGeneralDiscounts().put(discountID, discount);
		return discountID;
	}

	/**
	 * Suma un importe base determinado al importe base actual de un de
	 * descuento asociado a este calculador
	 * 
	 * @param discountID
	 *            ID del Descuento al cual se le quiere incrementar el importe
	 *            base de aplicación
	 * @param baseAmt
	 *            Importe base a sumar
	 * @return <code>true</code> si el importe base fue actualizado,
	 *         <code>false</code> caso contrario (<code>baseAmt</code> =
	 *         <code>null</code> o no existe un descuento con el ID indicado)
	 */
	public boolean addDiscountBaseAmount(Integer discountID, BigDecimal baseAmt) {
		boolean added = false;
		if (baseAmt != null && containsDiscount(discountID)) {
			Discount discount = getDiscount(discountID);
			BigDecimal currentBaseAmt = discount.getBaseAmt();
			currentBaseAmt = currentBaseAmt == null ? BigDecimal.ZERO : currentBaseAmt;
			discount.setBaseAmt(currentBaseAmt.add(baseAmt));
			// Se resetea el importe calculado ya que si cambió el importe base,
			// entonces es necesario recalcular el importe del descuento
			discount.resetAmount();
			added = true;
		}
		return added;
	}
	
	/**
	 * Resta un importe base determinado al importe base actual de un
	 * descuento general asociado a este calculador
	 * 
	 * @param discountID
	 *            ID del Descuento al cual se le quiere decrementar el importe
	 *            base de aplicación
	 * @param baseAmt
	 *            Importe base a restar
	 * @return <code>true</code> si el importe base fue actualizado,
	 *         <code>false</code> caso contrario (<code>baseAmt</code> =
	 *         <code>null</code> o no existe un descuento con el ID indicado)
	 */
	public boolean subtractDiscountBaseAmount(Integer discountID, BigDecimal baseAmt) {
		return addDiscountBaseAmount(discountID, baseAmt != null ? baseAmt.negate() : null);
	}

	/**
	 * Elimina un Descuento General de la lista de descuentos generales
	 * asociados a este calculador
	 * 
	 * @param discountID
	 *            ID del descuento general a eliminar
	 */
	public void removeGeneralDiscount(Integer discountID) {
		getGeneralDiscounts().remove(discountID);
	}

	/**
	 * @return Indica si este calculador tiene o no descuentos
	 *         generales asociados.
	 */
	public boolean hasGeneralDiscounts() {
		boolean contains = false;
		for (Discount generalDiscount : getGeneralDiscounts().values()) {
			if (generalDiscount.getBaseAmt() == null
					|| generalDiscount.getBaseAmt().compareTo(BigDecimal.ZERO) != 0) {
				contains = true;
				break;
			}
		}
		return contains;
	}

	/**
	 * @return Indica si este calculador tiene o no un descuento de entidad
	 *         comercial asociado.
	 */
	public boolean hasBPartnerDiscount() {
		return getBPartnerDiscount() != null;
	}
	
	/**
	 * @return Indica si este calculador tiene o no un descuento manual general
	 */
	public boolean hasManualGeneralDiscount() {
		return getManualGeneralDiscount() != null;
	}

	/**
	 * Redondea un precio o costo tomando la precisión de costeo de la moneda
	 * asociada a este calcular.
	 * 
	 * @param price
	 *            Precio o costo a redondear
	 * @return Precio o costo con su nueva escala.
	 */
	public BigDecimal scalePrice(BigDecimal price) {
		BigDecimal newPrice = price;
		if (isApplyScale()) {
			int scale = MCurrency.get(getCtx(), getCurrencyID()).getCostingPrecision();
			newPrice = price.setScale(scale, BigDecimal.ROUND_HALF_UP);
		}
		return newPrice;
	}
	
	/**
	 * Redondea un importe tomando la precisión
	 * estándar de importes de la moneda asociada a este calcular.
	 * 
	 * @param amount
	 *            Importe a redondear
	 * @return Importe con su nueva escala.
	 */
	public BigDecimal scaleAmount(BigDecimal amount) {
		BigDecimal newAmount = amount;
		if (isApplyScale()) {
			int scale = MCurrency.get(getCtx(), getCurrencyID()).getStdPrecision();
			newAmount = amount.setScale(scale, BigDecimal.ROUND_HALF_UP);
		}
		return newAmount;
	}

	/**
	 * @return Indica si los métodos de escalado deben hacer o no la operación
	 *         de escala.
	 */
	private boolean isApplyScale() {
		return applyScale;
	}

	/**
	 * @param applyScale
	 *            <code>true</code> para que los métodos de escalado surtan
	 *            efecto, <code>false</code> caso contrario
	 */
	private void setApplyScale(boolean applyScale) {
		this.applyScale = applyScale;
	}

	/**
	 * Resetea el estado de este calculador de descuento borrando todas las
	 * referencias a esquemas de descuentos generales y de EC, al documento y
	 * demás objetos necesarios para los cálculos.
	 */
	public void reset() {
		setApplyScale(true);
		this.bPartnerDiscount = null;
		this.bPartnerFlatDiscount = BigDecimal.ZERO;
		this.setBpartnerDiscountContext(null);
		this.nextDiscountID = 1;
		setDocument(null);
		setCurrencyID(Env.getContextAsInt(Env.getCtx(), "$C_Currency_ID"));
		setCtx(Env.getCtx());
		getGeneralDiscounts().clear();
		setManualGeneralDiscount(null);
	}

	/**
	 * Indica si este calculador contiene asociado un descuento cuyo ID es igual
	 * a <code>discountID</code>
	 * 
	 * @param discountID
	 *            ID del descuento a consultar
	 * @return <code>true</code> en caso de contenerlo, <code>false</code> caso
	 *         contrario.
	 */
	public boolean containsDiscount(Integer discountID) {
		return getDiscount(discountID) != null;
	}

	/**
	 * Asigna la descripción de un descuento asociado a este calculador.
	 * 
	 * @param discountID
	 *            ID del descuento asociado
	 * @param description
	 *            Descripción a asignar
	 */
	public void setDiscountDescription(Integer discountID, String description) {
		if (containsDiscount(discountID)) {
			getDiscount(discountID).setDescription(
				description != null && description.trim().length() == 0 ? 
						null : description.trim());
		}
	}
	
	/**
	 * @return El descuento de la EC
	 */
	private Discount getBPartnerDiscount() {
		return bPartnerDiscount;
	}

	/**
	 * Devuelve la instancia interna del descuento a partir de un ID. Busca en
	 * los descuentos generales y en el descuento de EC asociado.
	 * 
	 * @param discountID
	 *            ID de descuento a devolver.
	 * @return La instancia del descuento o <code>null</code> si no existe un
	 *         descuento con el ID indicado
	 */
	private Discount getDiscount(Integer discountID) {
		Discount discount = null;
		if (discountID != null) {
			if (hasBPartnerDiscount() && getBPartnerDiscount().getId().equals(discountID)) {
				discount = getBPartnerDiscount();
			} else if (getGeneralDiscounts().containsKey(discountID)) {
				discount = getGeneralDiscounts().get(discountID);
			}
		}
		return discount;
	}

	/**
	 * Reinicia a cero el importe calculado de cada descuento de la lista,
	 * dejándolo listo para ser utilizado en un nuevo cálculo
	 * 
	 * @param discounts
	 *            Lista de descuentos a resetear
	 */
	private void clearDiscountAmounts(List<Discount> discounts) {
		for (Discount discount : discounts) {
			discount.resetAmount();
		}
	}

	/**
	 * @return El nombre de la transacción utilizada para las operaciones de
	 *         actualización a la base de datos (guardado de descuentos
	 *         aplicados, etc.)
	 */
	public String getTrxName() {
		return trxName;
	}

	/**
	 * Asigna el nombre de la transacción utilizada para las operaciones de
	 * actualización a la base de datos (guardado de descuentos aplicados, etc.)
	 * 
	 * @param trxName
	 *            Nombre de la transacción a asignar. <code>null</code> para no
	 *            utilizar transacción.
	 */
	public void setTrxName(String trxName) {
		this.trxName = trxName;
	}
	
	/**
	 * @return La lista de descuentos aplicados y calculados por este calculador.
	 */
	private List<Discount> getAppliedDiscounts() {
		List<Discount> appliedDiscounts = new ArrayList<Discount>();

		// Agrega el descuento manual general si es que el mismo ha sido aplicado
		if (hasManualGeneralDiscount() 
				&& getManualGeneralDiscount().amount.compareTo(BigDecimal.ZERO) != 0) {
			// Asigna una descripción por defecto si no tiene una asignada.
			if (getManualGeneralDiscount().getDescription() == null) {
				getManualGeneralDiscount().setDescription(Msg.translate(getCtx(), "GeneralDiscountCharge"));
			}
			appliedDiscounts.add(getManualGeneralDiscount());
		}
		
		// Agrega el descuento de EC si es que el mismo ha sido aplicado
		if (getBPartnerDiscount() != null 
				&& getBPartnerDiscount().amount.compareTo(BigDecimal.ZERO) != 0) {
			// Asigna una descripción por defecto si no tiene una asignada.
			if (getBPartnerDiscount().getDescription() == null) {
				getBPartnerDiscount().setDescription(Msg.translate(getCtx(), "BPartnerDiscountShort"));
			}
			appliedDiscounts.add(getBPartnerDiscount());
		}
		
		// Agrega los descuentos generales aplicados.
		String generalDiscountDefaultDescription = Msg.translate(getCtx(), "GeneralDiscount");
		for (Discount generalDiscount : getGeneralDiscounts().values()) {
			if (generalDiscount.amount.compareTo(BigDecimal.ZERO) != 0) {
				// Descripción por defecto.
				if (generalDiscount.getDescription() == null) {
					generalDiscount.setDescription(generalDiscountDefaultDescription);
				}
				appliedDiscounts.add(generalDiscount);
			}
		}
		
		// Agrega los descuentos por combos aplicados.
		appliedDiscounts.addAll(getComboDiscounts());
		
		// Agrega los descuentos por promociones.
		for (Discount promotionDiscount : getPromotionDiscounts()) {
			if (promotionDiscount.amount.compareTo(BigDecimal.ZERO) != 0) {
				appliedDiscounts.add(promotionDiscount);
			}
		}
		
		return appliedDiscounts;
	}

	public void updateManualGeneralDiscount(BigDecimal percentage){
		// Si el porcentaje es 0 no se crea ningun descuento de tipo general, si
		// existe uno seteado, se elimina
		if(percentage.compareTo(BigDecimal.ZERO) == 0 && getManualGeneralDiscount() != null){
			setManualGeneralDiscount(null);
		}
		else{
			// Si no existe ninguno se crea
			if(getManualGeneralDiscount() == null){
				setManualGeneralDiscount(createManualGeneralDiscount());
			}
			// Se setea el porcentaje pasado como parámetro
			getManualGeneralDiscount().getDiscountSchema().setFlatDiscount(percentage);
		}
	}
	
	
	public Discount createManualGeneralDiscount() {
		// Creo el esquema de descuento dummy (sin guardar) para el descuento manual general
		MDiscountSchema discountGeneral = new MDiscountSchema(getCtx(), 0, null);
		discountGeneral.setDiscountType(MDiscountSchema.DISCOUNTTYPE_FlatPercent);
		discountGeneral.setDiscountContextType(MDiscountSchema.DISCOUNTCONTEXTTYPE_Commercial);
		discountGeneral.setFlatDiscount(BigDecimal.ZERO);
		discountGeneral.setIsGeneralScope(true);
		Integer discountID = null;
		synchronized (this) {
			discountID = nextDiscountID;
			nextDiscountID++;
		}
		return new Discount(discountID, null,
				discountGeneral, null, DiscountKind.ManualGeneralDiscount);
	}
	
	/**
	 * Guarda en la Base de Datos los descuentos que fueron aplicados al
	 * documento asociado a este calculador.
	 * 
	 * @param trxName
	 *            Transacción de la BD a utilizar. Si es <code>null</code> se
	 *            utilizará la transacción determinada por {@link #getTrxName()}.
	 * 
	 * @return <code>true</code> si todos los descuentos fueron guardados
	 *         correctamente, <code>false</code> si alguno falló.
	 */
	public boolean saveDiscounts(String trxName) {
		/*
		 * PRECONDICION: Se asumen que el documento no contiene ningún descuento
		 * guardado en la BD. Si así fuera, hay que recodificar este método
		 * definiendo una politica de borrado de descuentos esxistentes para
		 * luego crear los recientemente calculados por esta clase
		 */
		boolean saveOk = true;
		trxName = trxName == null ? getTrxName() : trxName;
		
		for (Discount appliedDiscount : getAppliedDiscounts()) {
			// Si no se puede guardar aborta la operación y devuelve false.
			if (!appliedDiscount.save(trxName)) {
				saveOk = false;
				break;
			}
		}
		
		return saveOk;
	}

	/**
	 * @return El ID del Cargo que se asocia al documento si es que se
	 *         computaron descuentos a nivel de documentos.
	 */
	private int getDocumentDiscountChargeID() {
		return getDiscountConfig().getDocumentDiscountCharge_ID();
	}

	/**
	 * Valida que exista un documento asociado. Si no existe dispara una
	 * excepción en tiempo de ejecución.
	 */
	private void validateAssociatedDocument() {
		if (getDocument() == null) {
			throw new IllegalStateException(
					"Associated Document is null. An IDocument instance is needed to perform this operation");
		}
	}
	
	/**
	 * @return La lista de combos válidos para la fecha del documento.
	 */
	private List<MCombo> getValidCombos() {
		return validCombos;
	}

	/**
	 * @return La lista de descuentos por promociones.
	 */
	private List<Discount> getPromotionDiscounts() {
		return promotionDiscounts;
	}
	
	/**
	 * @return La lista de descuentos por combos.
	 */
	private List<Discount> getComboDiscounts() {
		return comboDiscounts;
	}
	
	/**
	 * Carga en memoria los combos válidos para la fecha del documento asociado.
	 * Luego de ejecutar este método, no se recargarán los combos desde la Base
	 * de Datos a menos que la fecha del documento asociado cambie, o que se
	 * vuelva a invocar este método.
	 */
	public void loadCombos() {
		validateAssociatedDocument();
		validCombos = MCombo.getValidFor(getDocument().getDate(), getCtx(), getTrxName(), true);
		this.documentDate = getDocument().getDate();
	}

	/**
	 * Carga en memoria las promociones válidas para la fecha del documento asociado.
	 * Luego de ejecutar este método, no se recargarán las promociones desde la Base
	 * de Datos a menos que la fecha del documento asociado cambie, o que se
	 * vuelva a invocar este método.
	 */
	public void loadPromotions() {
		validateAssociatedDocument();
		// Obtiene las promociones válidas para la fecha del documento y actualiza la fecha
		//guardada.
		List<MPromotion> validPromos = MPromotion.getValidFor(getDocument()
				.getDate(), getCtx(), getTrxName(), true);
		this.documentDate = getDocument().getDate();
		
		// Crea los descuentos por promoción para cada promoción. A partir de
		// esta lista luego se aplicarán las promociones y se guardaran los
		// importes de cada descuento para ser almacenado en la BD.
		getPromotionDiscounts().clear();
		Discount discount = null;
		for (MPromotion promo : validPromos) {
			discount = new Discount(promo);
			getPromotionDiscounts().add(discount);
		}		
	}

	/**
	 * Carga en memoria todos los descuentos (Combos, Promociones, Etc)
	 * configurados para la fecha del documento asociado. A partir de la
	 * invocación de este método, luego los cálculos y aplicaciones de
	 * descuentos no requieren acceso a la base de datos lo cual agiliza
	 * considerablemente el proceso, siempre y cuando la fecha del documento
	 * asociado no se modifique ya que en ese caso es necesario recargar los
	 * descuentos configurados para la nueva fecha del documento.
	 */
	public void loadConfiguredDiscounts() {
		loadCombos();
		loadPromotions();
	}
	
	/**
	 * @return La configuración de descuentos utilizada.
	 */
	public MDiscountConfig getDiscountConfig() {
		return discountConfig;
	}

	/**
	 * Asigna una nueva configuración de descuentos para una organización
	 * particular.
	 * 
	 * @param orgID
	 *            ID de la organización cuya configuración de descuentos se
	 *            quiere utilizar
	 */
	public void setDiscountConfig(int orgID) {
		if (!hasDiscountConfig() || orgID != getDiscountConfig().getAD_Org_ID()) {
			discountConfig = MDiscountConfig.get(orgID);
		}
	}
	
	/**
	 * @return Indica si la fecha del documento asociado a cambiado luego de la
	 *         última asignación del documento mediante
	 *         {@link #setDocument(IDocument)}
	 */
	private boolean documentDateChanged() {
		return documentDate != null && documentDate.compareTo(getDocument().getDate()) != 0;
	}
	
	/**
	 * @return Indica si el calculador tiene o no una configuración de descuento
	 *         asociada. Si no tiene ninguna entonces se aplicará ningún tipo de
	 *         descuento.
	 */
	public boolean hasDiscountConfig() {
		return getDiscountConfig() != null;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getContext() {
		return context;
	}

	public void setBpartnerDiscountContext(String bpartnerDiscountContext) {
		this.bpartnerDiscountContext = bpartnerDiscountContext;
	}

	public String getBpartnerDiscountContext() {
		return bpartnerDiscountContext;
	}

	/**
	 * @return Indica si el calculador asumirá que existe al menos un descuento
	 *         general para realizar el cálculo, a pesar de que no exista
	 *         ninguna realmente asociado.
	 */
	public boolean isAssumeGeneralDiscountAdded() {
		return assumeGeneralDiscountAdded;
	}

	/**
	 * @param assumeGeneralDiscountAdded
	 *            el valor que indica si el calculador asumirá que existe al
	 *            menos un descuento general para realizar el cálculo, a pesar
	 *            de que no exista ninguna realmente asociado.
	 */
	public void setAssumeGeneralDiscountAdded(boolean assumeGeneralDiscountAdded) {
		this.assumeGeneralDiscountAdded = assumeGeneralDiscountAdded;
	}

	public void setManualGeneralDiscount(Discount manualGeneralDiscount) {
		this.manualGeneralDiscount = manualGeneralDiscount;
	}

	public Discount getManualGeneralDiscount() {
		return manualGeneralDiscount;
	}

	/**
	 * Interfaz que debe implementar cualquier clase que requiera ser manipulada
	 * por un calculador de descuentos. Esta interfaz representa el encabezado
	 * del Documento. A su vez, las líneas del documento deben respetar la
	 * interfaz {@link IDocumentLine}.
	 */
	public interface IDocument {

		/**
		 * @return El importe total de las líneas del documento <b>incluyendo
		 *         impuestos</b>. No incluye descuentos aplicados a nivel de
		 *         documento, pero si aquellos aplicados a nivel de línea.
		 */
		public BigDecimal getLinesTotalAmt();
		
		/**
		 * @return La lista de líneas asociadas a este documento.
		 */
		public List<IDocumentLine> getDocumentLines();

		/**
		 * @return La fecha de transacción del documento.
		 */
		public Date getDate();

		/**
		 * Asigna el importe total de descuentos a nivel de documento. Si es un
		 * descuento <code>discountAmount</code> es positivo, si es un recargo
		 * <code>discountAmount</code> es negativo.
		 * 
		 * @param discountAmount
		 *            Importe total de descuentos de documento a asignar
		 */
		public void setTotalDocumentDiscount(BigDecimal discountAmount);

		/**
		 * Asigna el importe total de descuento de la Entidad Comercial para el
		 * documento.
		 * <p>
		 * NOTA: el importe que aquí se setea está incluído dentro del importe
		 * que se asigna mediante {@link #setTotalDocumentDiscount(BigDecimal)}.
		 * Este método solo es necesario que tenga lógica si el cliente requiere
		 * diferenciar cual fue el importe total de descuento de la EC, del
		 * importe total de descuento por otros esquemas generales. En caso de
		 * no ser necesaria esta diferenciación, simplemente implementar este
		 * método vacío.
		 * </p>
		 * 
		 * @param discountAmount
		 *            Importe de descuento calculado a partir de un esquema
		 *            asociado a la Entidad Comercial del documento.
		 */
		public void setTotalBPartnerDiscount(BigDecimal discountAmount);

		/**
		 * Asigna el importe de descuento manual general para el documento
		 * <p>
		 * NOTA: el importe que aquí se setea está incluído dentro del importe
		 * que se asigna mediante {@link #setTotalDocumentDiscount(BigDecimal)}.
		 * Este método solo es necesario que tenga lógica si el cliente requiere
		 * diferenciar cual fue el importe total de descuento manual general,
		 * del importe total de descuento por otros esquemas generales. En caso
		 * de no ser necesaria esta diferenciación, simplemente implementar este
		 * método vacío.
		 * </p>
		 * 
		 * @param discountAmount
		 *            Importe de descuento calculado a partir de un esquema
		 *            manual general dummy creado
		 */
		public void setTotalManualGeneralDiscount(BigDecimal discountAmount);
		
		/**
		 * Asigna el ID del Cargo indicado para el importe total de descuento
		 * calculado para el documento. Solo se asigna el cargo si el importe
		 * total del descuento es distinto de cero.
		 * 
		 * @param chargeID
		 *            ID del cargo a asignar.
		 */
		public void setDocumentDiscountChargeID(int chargeID);
		
		/**
		 * Indica si este documento requiere que se calculen descuentos NETOS
		 * sin impuestos, o descuentos totales incluyendo impuestos.
		 * 
		 * @return <code>true</code> para indicar al calculador que calcule
		 *         descuentos netos. En este caso el calculador asigna mediante
		 *         {@link #setTotalDocumentDiscount(BigDecimal)} el descuento
		 *         total neto del documento. <code>false</code> para indicar al
		 *         calculador que calcule descuentos totales con impuestos. Aquí
		 *         asigna mediante {@link #setTotalDocumentDiscount(BigDecimal)}
		 *         el descuento total con impuestos del documento.
		 */
		public boolean isCalculateNetDiscount();

		/**
		 * <p>
		 * Asigna las referencias de este documento al objeto
		 * <code>documentDiscount</code> que contiene un descuento de documento
		 * aplicado que será guardado en la base de datos.
		 * </p>
		 * Si este documento representa:
		 * <ul>
		 * <li>Un Pedido: debe asignar su ID mediante
		 * {@link MDocumentDiscount#setC_Order_ID(int)}.</li>
		 * <li>Una Factura: debe asignar su ID mediante
		 * {@link MDocumentDiscount#setC_Invoice_ID(int)}.</li>
		 * <li>Ambos (Factura y Pedido): debe asignar ambos IDs mediante los
		 * métodos descriptos anteriormente.</li>
		 * </ul>
		 * 
		 * @param documentDiscount
		 *            Descuento de documento creado por este calculador
		 */
		public void setDocumentReferences(MDocumentDiscount documentDiscount);
	}

	/**
	 * Línea del Documento a ser manipulado por el {@link DiscountCalculator}.
	 * Las líneas del documento deben implementar esta interfaz para poder ser
	 * administradas por el {@link DiscountCalculator}, permitiendo calcular
	 * descuentos por línea e inclusive modificar y guardar los cambios en caso
	 * de que la clase soporte persistencia.
	 */
	public interface IDocumentLine {

		/**
		 * @return El importe total de esta línea de documento <b>incluyendo
		 *         impuestos y descuentos de línea</b>.
		 */
		public BigDecimal getTotalAmt();

		/**
		 * @return El precio unitario de tarifa de la línea, sin impuestos.
		 */
		public BigDecimal getPriceList();
		
		/**
		 * @return El precio unitario actual de la línea, sin impuestos.
		 */
		public BigDecimal getPrice();
		
		/**
		 * @return La cantidad de esta línea.
		 */
		public BigDecimal getQty();

		/**
		 * @return El ID del artículo asociado a esta línea. Si la línea no
		 *         tiene un artículo asociado devuelve <code>0</code>.
		 */
		public int getProductID();

		/**
		 * @return El documento que contiene esta línea de documento
		 */
		public IDocument getDocument();
		
		/**
		 * Asigna el precio unitario actual neto de la línea (sin impuestos).
		 * @param newPrice Nuevo precio a asignar
		 */
		public void setPrice(BigDecimal newPrice);

		/**
		 * Asigna el importe de descuento a nivel documento de esta línea. Al
		 * calcular descuentos a nivel de documento se calcula por cada línea
		 * cual es el importe de descuento para dicha línea. Ese importe es el
		 * que se asigna mediante este método, el cual NO debe afectar el neto
		 * de línea ya que solo se tiene en cuenta para el cálculo correcto de
		 * impuestos.
		 * 
		 * @param discountAmt
		 *            Importe de descuento a asignar. (positivo implica
		 *            descuento, negativo recargo)
		 */
		public void setDocumentDiscountAmt(BigDecimal discountAmt);

		/**
		 * Asigna la cantidad de esta línea que ya ha sufrido algún descuento
		 * por Combo o Promoción.
		 * 
		 * @param discountedQty
		 *            Cantidad a asignar. Si es mayor que {@link #getQty()}
		 *            entonces asigna el valor de {@link #getQty()} en vez de
		 *            {@link code discountedQty}.
		 */
		public void setDiscountedQty(BigDecimal discountedQty);

		/**
		 * @return La cantidad de esta línea que ya ha sufrido algún descuento
		 *         por Combo o Promoción.
		 */
		public BigDecimal getDiscountedQty();
		
		/**
		 * @return La cantidad de esta línea que está disponible para la
		 *         aplicación de Combos o Promociones. El valor devuelto está
		 *         entre cero y la cantidad de la línea.
		 */
		public BigDecimal getAvailableQty();
		
		/**
		 * @return El importe neto de descuento "al precio" de la línea.
		 */
		public BigDecimal getLineDiscountAmt();

		/**
		 * Asigna el importe neto de descuento "al precio" de la línea.
		 * @param lineDiscountAmt Importe a asignar.
		 */
		public void setLineDiscountAmt(BigDecimal lineDiscountAmt);

		/**
		 * @return El importe neto de bonificación de la línea.
		 */
		public BigDecimal getLineBonusAmt();

		/**
		 * Asigna el importe neto de bonificación de la línea.
		 * @param lineBonusAmt Importe a asignar
		 */
		public void setLineBonusAmt(BigDecimal lineBonusAmt);
		
		/**
		 * @return La tasa de impuesto de la línea.
		 */
		public BigDecimal getTaxRate();

		/**
		 * Calcula el impuesto de un importe y devuelve la suma de ambos. En
		 * caso de que esta línea contenga el impuesto incluido en el precio,
		 * entonces devuelve el mismo valor de <code>amount</code>.
		 * 
		 * @param amount
		 *            Importe neto (o bruto si tiene el impuesto incluído la
		 *            línea)
		 * @return El importe con el impuesto incluido.
		 */
		public BigDecimal getTaxedAmount(BigDecimal amount);

		/**
		 * @return Indica si el precio de la línea contiene el impuesto incluído
		 *         en el mismo o no.
		 */
		public boolean isTaxIncluded();
	}

	/**
	 * Estructura interna que mantiene la información de un descuento asociado a
	 * este calculador.
	 */
	private class Discount {
		
		/** ID del descuento */
		private Integer id = null;
		/** Descripción del descuento */
		private String description = null;
		/** Esquema de descuento asociado */
		private MDiscountSchema discountSchema = null;
		/** Importe base de aplicación para el cálculo del importe del descuento, 
		 *  utilizado para calcular la proporción sobre el importe total. 
		 * (normalmente solo utilizado para descuentos generales) */
		private BigDecimal baseAmt = null;
		/** Importe base del descuento calculado. */
		private BigDecimal discountBaseAmt = null;
		/** Importe calculado del descuento */
		private BigDecimal amount = BigDecimal.ZERO;
		/** Tipo de descuento */
		private DiscountKind kind = null;
		/** Forma de aplicación del descuento */
		private String application = null;
		/** Por cada tasa de impuesto contiene el importe de descuento para la tasa.
		 *  La clave es la tasa, el valor es el importe de descuento */
		private Map<BigDecimal, BigDecimal> amountsByTax;
		/** Por cada tasa de impuesto contiene el importe base del descuento calculado
		 * para la tasa. La clave es la tasa, el valor es el importe base */
		private Map<BigDecimal, BigDecimal> discountBaseAmtByTax;
	
		/**
		 * Constructor básico de Descuentos
		 * @param id
		 * @param description
		 * @param discountSchema
		 * @param baseAmt
		 */
		public Discount(Integer id, String description,
				MDiscountSchema discountSchema, BigDecimal baseAmt, DiscountKind kind) {
			super();
			this.id = id;
			this.description = description;
			this.discountSchema = discountSchema;
			this.baseAmt = baseAmt;
			this.kind = kind;
			if (isLineLevel() && discountSchema != null) {
				this.application = discountSchema.getDiscountApplication();
			}
			this.amountsByTax = new HashMap<BigDecimal, BigDecimal>();
			this.discountBaseAmtByTax = new HashMap<BigDecimal, BigDecimal>();
		}
		
		/**
		 * Crea un descuento a partir de una promoción.
		 * 
		 * @param promotion
		 *            Promoción origen.
		 */
		public Discount(MPromotion promotion) {
			this(INTERNAL_DISCOUNT_ID, promotion.getName(),
					promotion.getDiscountSchema(), null, DiscountKind.Promotion);
			this.application = promotion.getDiscountApplication();
		}

		/**
		 * Crea un descuento a partir de un combo.
		 * 
		 * @param combo
		 *            Combo origen.
		 */
		public Discount(MCombo combo) {
			this(INTERNAL_DISCOUNT_ID, combo.getName(), null, null,
					DiscountKind.Combo);
			this.application = combo.getDiscountApplication();
		}

		/**
		 * @return El nivel de aplicación de este descuento determinado por el
		 *         esquema asociado.
		 */
		public String getLevel() {
			String level = null;
			if (getKind() == DiscountKind.Combo || getKind() == DiscountKind.Promotion) {
				level = MDiscountSchema.CUMULATIVELEVEL_Line;
			} else if (getDiscountSchema() != null) {
				level = getDiscountSchema().getCumulativeLevel();
			} else {
				level = MDiscountSchema.CUMULATIVELEVEL_Line;
			}
			return level;
		}

		@Override
		public String toString() {
			return "Discount[Schema=" + getDiscountSchema().toString() + ",baseAmt="
					+ getBaseAmt() + ",Amt=" + getAmount() + "]"; 
		}

		/**
		 * @return Indica si este descuento se debe aplicar como una
		 *         bonificación a la línea.
		 */
		public boolean isBonusApplication() {
			return getApplication() != null
					&& getApplication()
							.equals(MDiscountSchema.DISCOUNTAPPLICATION_Bonus);
		}

		/**
		 * @return Indica si este descuento se debe aplicar como un descuento al
		 *         precio de la línea.
		 */
		public boolean isToPriceApplication() {
			return getApplication() == null
					|| getApplication()
					.equals(MDiscountSchema.DISCOUNTAPPLICATION_DiscountToPrice);
			
		}
		
		/**
		 * @return Indica si este descuento se aplica a nivel de líneas o no.
		 */
		public boolean isLineLevel() {
			return getKind() == DiscountKind.Combo
					|| (getDiscountSchema() != null && getDiscountSchema()
							.getCumulativeLevel().equals(
									MDiscountSchema.CUMULATIVELEVEL_Line));
		}
		
		/**
		 * @return Indica si este descuento se aplica a nivel de encabezado de documento.
		 */
		public boolean isDocumentLevel() {
			return getDiscountSchema() != null && getDiscountSchema()
					.getCumulativeLevel().equals(
							MDiscountSchema.CUMULATIVELEVEL_Document);
		}
		
		/**
		 * @return Indica si este es un descuento de Entidad Comercial.
		 */
		public boolean isBPartnerDiscount() {
			return this == getBPartnerDiscount();
		}
		
		/**
		 * @return Indica si este es un descuento de Entidad Comercial.
		 */
		public boolean isManualGeneralDiscount() {
			return this == getManualGeneralDiscount();
		}

		/**
		 * @return El ID del esquema de descuento asociado a este descuento o
		 *         cero en caso de que no tenga ningún esquema de descuento
		 *         asociado.
		 */
		public int getDiscountSchemaID() {
			return getDiscountSchema() == null ? 0 : getDiscountSchema()
					.getM_DiscountSchema_ID();
		}

		/**
		 * Resetea el importe del descuento para ser recalculado.
		 */
		public void resetAmount() {
			amount = BigDecimal.ZERO;
			discountBaseAmt = BigDecimal.ZERO;
			amountsByTax.clear();
			discountBaseAmtByTax.clear();
		}

		/**
		 * Suma un importe neto de descuento a este descuento. Se debe indicar
		 * la tasa de impuesto para el importe de descuento.
		 * 
		 * @param amt
		 *            Importe total del descuento (incluyendo impuestos)
		 * @param taxRate
		 *            Tasa de impuesto asociada al importe neto que se suma
		 * @param discountBaseAmt
		 *            Importe base del descuento calculado (con impuestos)
		 */
		public void addAmount(BigDecimal amt, BigDecimal taxRate,
				BigDecimal discountBaseAmt) {
			if (amount == null) {
				amount = BigDecimal.ZERO;
			}
			if (this.discountBaseAmt == null) {
				this.discountBaseAmt = BigDecimal.ZERO;
			}
			// Suma el importe de descuento al importe total. Idem para la base
			// del descuento.
			amount = amount.add(amt);
			this.discountBaseAmt = this.discountBaseAmt.add(discountBaseAmt);
			
			// Acumula el importe y la base en las Maps según la tasa.
			if (!amountsByTax.containsKey(taxRate)) {
				amountsByTax.put(taxRate, amt);
				discountBaseAmtByTax.put(taxRate, discountBaseAmt);
			} else {
				BigDecimal currentAmountByTax = amountsByTax.get(taxRate);
				BigDecimal currentDiscountBaseAmtByTax = discountBaseAmtByTax
						.get(taxRate);
				amountsByTax.put(taxRate, currentAmountByTax.add(amt));
				discountBaseAmtByTax.put(taxRate,
						currentDiscountBaseAmtByTax.add(discountBaseAmt));
			}
		}

		/**
		 * Calcula y devuelve la proporción de aplicación de este descuento,
		 * basándose en el importe base de aplicación.
		 * 
		 * @param document
		 *            Documento para la cual se quiere calcular la proporción
		 * @return Valor entre 0 y 1. Si es 1 implica que la base de aplicación
		 *         es igual al importe total del documento
		 */
		public BigDecimal getApplicationRatio(IDocument document) {
			BigDecimal appBaseAmt = getBaseAmt();
			BigDecimal documentTotalAmt = document.getLinesTotalAmt(); 
			if (appBaseAmt == null) {
				appBaseAmt = documentTotalAmt;
			}
			return appBaseAmt.divide(documentTotalAmt, 20,
					BigDecimal.ROUND_HALF_EVEN);
		}
		
		/**
		 * Guarda este descuento en la BD como un C_DocumentDiscount.
		 * 
		 * @param trxName
		 *            Transacción de la BD
		 * @return <code>true</code> si la operación de guardado se efectua
		 *         correctamente, <code>false</code> si hubo error.
		 */
		public boolean save(String trxName) {
			MDocumentDiscount documentDiscount = null;
		
			// Crea el descuento del documento
			documentDiscount = new MDocumentDiscount(getCtx(), 0, trxName);
			// Asigna las referencias al documento
			getDocument().setDocumentReferences(documentDiscount);
			// Asigna los importes y demás datos del descuento
			documentDiscount.setDiscountBaseAmt(scaleAmount(getDiscountBaseAmt()));
			documentDiscount.setDiscountAmt(scaleAmount(getAmount()));
			documentDiscount.setCumulativeLevel(getLevel());
			documentDiscount.setDescription(getDescription());
			if(getDiscountSchemaID() > 0){
				documentDiscount.setM_DiscountSchema_ID(getDiscountSchemaID());
			}
			documentDiscount.setDiscountApplication(getApplication());
			documentDiscount.setTaxRate(null);
			documentDiscount.setDiscountKind(getKind().toDocumentDiscountKind());
			
			// Si no se puede guardar aborta la operación y devuelve false.
			if (!documentDiscount.save()) {
				return false;
			}
	
			// Guarda cada descuento discriminado por tasa como un descuento de documento.
			MDocumentDiscount tDocumentDiscount = null;
			BigDecimal tAmount = null;
			BigDecimal tDiscountBaseAmount = null;
			for (BigDecimal taxRate : amountsByTax.keySet()) {
				tAmount = amountsByTax.get(taxRate);
				tDiscountBaseAmount = discountBaseAmtByTax.get(taxRate);
				tDocumentDiscount = new MDocumentDiscount(documentDiscount, taxRate);
				tDocumentDiscount.setDiscountAmt(scaleAmount(tAmount));
				tDocumentDiscount.setDiscountBaseAmt(scaleAmount(tDiscountBaseAmount));
				if (!tDocumentDiscount.save()) {
					return false;
				}
			}
			
			return true;
		}
		
		// Getters y Setters
		
		/**
		 * @return el valor de description
		 */
		public String getDescription() {
			return description;
		}

		/**
		 * @param description el valor de description a asignar
		 */
		public void setDescription(String description) {
			this.description = description;
		}

		/**
		 * @return el valor de id
		 */
		public Integer getId() {
			return id;
		}

		/**
		 * @return el valor de discountSchema
		 */
		public MDiscountSchema getDiscountSchema() {
			return discountSchema;
		}

		/**
		 * @return el valor de baseAmt
		 */
		public BigDecimal getBaseAmt() {
			return baseAmt;
		}
		
		/**
		 * @param baseAmt el valor de baseAmt a asignar
		 */
		public void setBaseAmt(BigDecimal baseAmt) {
			this.baseAmt = baseAmt;
		}

		/**
		 * @return el valor de discountBaseAmt
		 */
		public BigDecimal getDiscountBaseAmt() {
			return discountBaseAmt;
		}

		/**
		 * @return el valor de amount
		 */
		public BigDecimal getAmount() {
			return amount;
		}

		/**
		 * @return el valor de type
		 */
		public DiscountKind getKind() {
			return kind;
		}

		/**
		 * @return el valor de application
		 */
		public String getApplication() {
			return application;
		}
	}
	
	/**
	 * Tipo de descuento interno.
	 */
	private enum DiscountKind {
		Combo,
		Promotion,
		BPartnerDiscountSchema,
		GeneralDiscountSchema,
		ManualGeneralDiscount,
		PaymentMedium;
		
		private String toDocumentDiscountKind() {
			String documentDiscountKind = null;
			switch (this) {
			case Combo:
				documentDiscountKind = MDocumentDiscount.DISCOUNTKIND_ProductsCombo;
				break;
			case Promotion:
				documentDiscountKind = MDocumentDiscount.DISCOUNTKIND_Promotion;
				break;
			case BPartnerDiscountSchema:
				documentDiscountKind = MDocumentDiscount.DISCOUNTKIND_BPartnerDiscountSchema;
				break;
			case GeneralDiscountSchema:
				documentDiscountKind = MDocumentDiscount.DISCOUNTKIND_GeneralDiscountSchema;
				break;
			case ManualGeneralDiscount:
				documentDiscountKind = MDocumentDiscount.DISCOUNTKIND_ManualGeneralDiscount;
				break;
			case PaymentMedium:
				documentDiscountKind = MDocumentDiscount.DISCOUNTKIND_PaymentMedium;
				break;
			}
			return documentDiscountKind;
		}
	}

}
