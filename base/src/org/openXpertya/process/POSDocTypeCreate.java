package org.openXpertya.process;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.model.MDocType;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.MOrgInfo;
import org.openXpertya.model.MSequence;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;


public class POSDocTypeCreate extends SvrProcess {

	/** Nombre de la categoría GL de factura a cliente */ 
	private static String GL_ARI_NAME = "Factura%Cliente";
	/** Nombre del tipo de documento de factura */
	private static String DT_INVOICE_NAME = "Factura";
	/** Nombre del tipo de documento de nota de débito */
	private static String DT_DEBIT_NOTE_NAME = "Nota de Debito";
	/** Nombre del tipo de documento de nota de crédito */
	private static String DT_CREDIT_NOTE_NAME = "Nota de Credito";
	
	/** Log de la aplicación */ 
	private static CLogger	log	= CLogger.getCLogger(POSDocTypeCreate.class);
	/** Letras de comprobantes */
	private static String[] letters = {"A", "B", "C"};
	/** Contexto de la aplicación */
	private Properties ctx = new Properties(Env.getCtx());
	/** Transacción que se utiliza para realizar las creaciones mediante interfaz estática
	 * externa */
	private String trxName = null;
	/** Número del punto de vento */ 
	private int p_PosNumber;
	/** Datos de los tipos de documentos que se van a generar */
	private List<DocTypeData> newDocTypes; 
	/** Organización de los tipos de documento a crear */
	private int orgID = 0;
	/** Parámetro: indica si los tipos de documentos creados debe tener activa la marca
	 *  de apertura/cierre por punto de venta
	 */
	private boolean openCloseByPos = false;

	/**
	 * CUIT de la organización carpeta parámetro o de la organización padre de
	 * la organización parámetro. Si la organización parámetro no tiene padre,
	 * entonces no se obtiene ningún CUIT. Esto permite tener tipos de documento
	 * por organizaciones padre, es decir el tipo de documento A001 se puede
	 * crear para distintas organizaciones carpeta, concatenando el cuit de la
	 * org padre en el campo clave del tipo de documento
	 */
	private String orgParentCUIT;
	
	@Override
	protected void prepare() {
		log.info( "POSDocTypeCreate.prepare()" );
		ProcessInfoParameter[] para = getParameter();
		for( int i = 0;i < para.length;i++ ) {
			String name = para[i].getParameterName();
			if( para[i].getParameter() == null )
                ;
            else if(name.equals("POSNumber"))
                p_PosNumber = ((BigDecimal)para[i].getParameter()).intValue();
			else if( name.equalsIgnoreCase( "open_close_by_pos" ))
				openCloseByPos = "Y".equals((String)para[ i ].getParameter());
            else if(name.equals("AD_Org_ID"))
                setOrgID(((BigDecimal)para[i].getParameter()).intValue());
		}
	}

	@Override
	protected String doIt() throws Exception {
		
		if(getP_PosNumber() < 1 || getP_PosNumber() > 9999)
			throw new IllegalArgumentException(parseMsg("@InvalidPOSNumber@ 1 - 9999"));
		
		String retMsg = null;
		int docTypesCreatedCount = 0;
		String docTypesCreatedNames = "";
		
		try {
			// Inicializar el CUIT a concatenar en la clave del tipo de
			// documento en caso que la org parámetro sea padre o tenga una org
			// padre distinta de 0
			initOrgParentCUIT();
			// Por cada tipo de documento a crear... 
			for (DocTypeData docTypeData : getNewDocTypes()) {
				// Solo se crea si se verifica que el mismo no existe 
				// en la compañia aún.
				if(!docTypeExists(docTypeData)) {
					// Se crea el tipo de documento y se guarda su nombre para
					// presentarlo en el informe final del proceso.
					createDocType(docTypeData);
					docTypesCreatedCount++;
					docTypesCreatedNames += docTypeData.getName() + ", ";
				}
			}

		} catch(Exception e) {
			// En caso de error se relanza la excepción con un mensaje adecuado.
			throw  new Exception(parseMsg("@POSDocTypeCreateError@ ") + e.getMessage());
		}
		
		// En caso de que no se haya creado ningún tipo de documento significa
		// que los mismos ya estaban dados de alta. Esto se indica con un mensaje de 
		// retorno adecuado.
		if(docTypesCreatedCount == 0)
			retMsg = "@DocTypesCreated@: 0 <br> " +
			         "@PoSDocTypesWasCreated@";

		// Si se crearon tipos de documento, entonces se confecciona un mensaje que indica
		// la cantidad y cuales fueron creados.
		else {
			// Se quita la última ", " del string de nombres de tipos de docs y 
			// se arma el mensaje de respuesta.
			docTypesCreatedNames = 
				docTypesCreatedNames.substring(0,docTypesCreatedNames.length() - 2);
	
			retMsg = "@POSDocTypeCreateOk@ <br>" + 
					 "@DocTypesCreated@: " + docTypesCreatedCount + "<br>" +
					 "[" + docTypesCreatedNames + "]";
		}
		return retMsg;
	}
	
	private void initOrgParentCUIT(){
		String cuit = "";
		MOrgInfo orgInfo = null;
		// Si la organización parámetro es 0, entonces no busco cuit porque es a
		// nivel compañía
		if(getOrgID() > 0){
			// Si la organización parámetro es organización carpeta, entonces
			// tomar el CUIT de esta
			MOrg org = MOrg.get(getCtx(), getOrgID());
			if(org.isSummary()){
				orgInfo = MOrgInfo.get(getCtx(), getOrgID());				
			}
			// Si es organización concreta, obtener la organización padre de la
			// organización parámetro
			else{
				Integer orgParentID = MOrg.getOrgParentID(getCtx(), getOrgID(),
						get_TrxName());
				// Si el padre es una organización distinta de 0, entonces
				// obtengo el cuit de ese
				if(orgParentID > 0){
					orgInfo = MOrgInfo.get(getCtx(), orgParentID);
				}
			}
		}
		if(orgInfo != null){
			cuit = Util.isEmpty(orgInfo.getCUIT(), true) ? "" : orgInfo
					.getCUIT().replace("-", "").replace(" ", "");
		}
		setOrgParentCUIT(cuit);
	}
	
	/**
	 * Genera los tipos de documentos para la operación del punto de venta
	 * pasado como parametro. Los tipos de doc se generan para una determinada
	 * compañia.
	 * @param clientID Compañia a la que pertenecerán los tipos de documento.
	 * @param posNumber Punto de venta de los tipos de documentos.
	 * @param trxName Nombre de la transacción a utilizar en el proceso.
	 * @return Mensaje de respuesta del proceso que genera los tipos de documentos.
	 * @throws Exception Cuando ocurre algún error en la generación.
	 */
	public static String createPOSDocTypes(int clientID, int posNumber, String trxName) throws Exception {
		POSDocTypeCreate posDocTypeCreate = new POSDocTypeCreate();
		posDocTypeCreate.setP_PosNumber(posNumber);
		posDocTypeCreate.setAD_Client_ID(clientID);
		posDocTypeCreate.set_TrxName(trxName);
		String rsp = posDocTypeCreate.doIt();
		return posDocTypeCreate.parseMsg(rsp);
	}

	/**
	 * @return Returns the p_PosNumber.
	 */
	protected int getP_PosNumber() {
		return p_PosNumber;
	}

	/**
	 * @param number The p_PosNumber to set.
	 */
	protected void setP_PosNumber(int number) {
		p_PosNumber = number;
	}

	/**
	 * @return Returns the p_ClientID.
	 */
	protected int getAD_Client_ID() {
		return Env.getAD_Client_ID(ctx);
	}

	/**
	 * @param p_clientid The p_ClientID to set.
	 */
	protected void setAD_Client_ID(int clientID) {
		Env.setContext(ctx, "#AD_Client_ID", clientID);
	}
	
	private int getGLCategoryID(String name) {
		int categoryID = DB.getSQLValue(get_TrxName(), 
				"SELECT GL_Category_ID FROM GL_Category WHERE AD_Client_ID = ? AND Name ILIKE ? ",
				getAD_Client_ID(), name
		);
		return categoryID;
	}
	
	/**
	 * @return El número de punto de venta formateado a 4 dígitos:
	 * Ej: punto de venta 11 -> 0011.
	 */
	protected String getFormattedPosNumber() {
		return MDocType.formatPosNumber(getP_PosNumber());
	}
	
	protected String parseMsg(String msg) {
		return Msg.parseTranslation(ctx, msg);
	}
	
	/**
	 * Crea la secuencia y el tipo de documento para el punto de venta.
	 * @param  Datos del tipo de documento a crear.
	 * @return El ID del tipo de documento creado.
	 * @throws Exception cuando no se puede crear la secuencia o el
	 * tipo de documento.
	 */
	private int createDocType(DocTypeData dtd)  throws Exception {
        MSequence sequence;
        
        String posNumber = getFormattedPosNumber();
        BigDecimal posNro = new BigDecimal(getP_PosNumber());
        BigDecimal resto;
        int divideTimes = 0;
        // Se obtiene el número inicial de la secuencia.
        // Ej. 500000001 (el 5 corresponde al punto de venta) esto se hace
        // así dado que el campo es de tipo int y los ceros a la izquierda
        // serían quitados.
        BigDecimal currentNext;
        String prefix;
        // El punto de venta puede terminar en más de 1 cero
        // Ej. si letra es A y punto de venta es 40,
        // prefix = A00 y currentNext = 4000000000
        // El prefijo se calcula con 3 digitos y el currentNext con 10 digitos
        do {
        	divideTimes++;
        	resto = posNro.remainder(BigDecimal.TEN);
        	posNro = posNro.divide(BigDecimal.TEN, 2, BigDecimal.ROUND_DOWN);
		} while (resto.compareTo(BigDecimal.ZERO) == 0);
        
		prefix = dtd.getLetter()
				+ posNumber.substring(0, posNumber.length() - divideTimes);
		currentNext = new BigDecimal(posNumber.substring(posNumber.length()
				- divideTimes, posNumber.length())
				+ "00000001");
        
        // Se crea la secuencia con numeración automática.
		sequence = new MSequence(ctx, getAD_Client_ID(), dtd.getName(),
				currentNext, 1, 1, get_TrxName());
		sequence.setClientOrg(Env.getAD_Client_ID(ctx), getOrgID());
        sequence.setPrefix(prefix);
        sequence.setIsAutoSequence(true);
        // Se guarda la secuencia.
        if( !sequence.save()) {
        	log.severe("Sequence NOT created - " + dtd.getName());
        	throw new Exception(parseMsg("@SequenceCreateError@ (" + dtd.getName() + ")"));
         }
        
        // Se crea el tipo de documento.
        MDocType dt = new MDocType( ctx ,dtd.getDocBaseType(), dtd.getName(), get_TrxName());
        dt.setClientOrg(Env.getAD_Client_ID(ctx), getOrgID());
        // Se asigna el nombre de impresión.
        if( (dtd.getPrintName() != null) && (dtd.getPrintName().length() > 0) )
            dt.setPrintName(dtd.getPrintName());    // Defaults to name
        // Se asigna la categoría GL.
        dt.setGL_Category_ID(dtd.getGlCategoryID());
        // Se asigna el signo por el tipo de documento (1 o -1)
        dt.setsigno_issotrx(dtd.getSigno());       
        // Se determina si es transacción de ventas.
        dt.setIsSOTrx();
        // Se asigna el tipo de comprobante a imprimir por controladores fiscal.
        dt.setFiscalDocument(dtd.getFiscalDocument());
        // Se asigna la secuencia al documento.
        dt.setIsDocNoControlled(true);
        dt.setDocNoSequence_ID(sequence.getAD_Sequence_ID());
        // Se asigna la clave unica.
        dt.setDocTypeKey(dtd.getDocTypeKey());
        // Se indica que el tipo de documento es Fiscal (validaciones de localización)
        dt.setdocsubtypeinv(dt.DOCSUBTYPEINV_Fiscal);
        dt.setIsFiscalDocument(true);
        dt.setopen_close_by_pos(openCloseByPos);
        // Se guarda el tipo de documento.
        if(!dt.save()) {
			log.severe("DocType NOT created - " + dtd.getName());
			throw new Exception(parseMsg("@DocTypeCreateError@ (" + dtd.getName() + ")"));
        }

        return dt.getC_DocType_ID();
	}

	
	/**
	 * Indica si un tipo de documento ya existe en la BD.
	 */
	private boolean docTypeExists(DocTypeData dtd) {
		String sql = "SELECT C_DocType_ID FROM C_DocType WHERE AD_Client_ID = ? AND DocTypeKey = ?";
		Integer docTypeID = (Integer) DB.getSQLObject(get_TrxName(), sql,
				new Object[] { getAD_Client_ID(), dtd.getDocTypeKey() });
		return docTypeID != null;
	}

	/**
	 * @return Retorna una lista con los tipos de documentos que 
	 * se deben generar.
	 */
	public List<DocTypeData> getNewDocTypes() {
		if(newDocTypes == null) {
			newDocTypes = new ArrayList<DocTypeData>();
			String docTypeName;
			String printName;
			String posNumber = getFormattedPosNumber();
			final int GL_ARI = getGLCategoryID(GL_ARI_NAME);
			
			// Se instancian los tipos de documentos para las letras indicadas.
			for (int i = 0; i < letters.length; i++) {
				String letter = letters[i];

				//////////////////////////////////////////////////////////////
				// Tipos de Doc para Facturas.
				docTypeName = DT_INVOICE_NAME + " " + letter + "-" + posNumber; 
				printName   = DT_INVOICE_NAME + " " + letter;
				newDocTypes.add(new DocTypeData(getOrgID(), getOrgParentCUIT(),
						docTypeName, printName, MDocType.DOCBASETYPE_ARInvoice,
						GL_ARI, MDocType.SIGNO_ISSOTRX_1, letter, 
						MDocType.FISCALDOCUMENT_Invoice,
						MDocType.DOCTYPE_CustomerInvoice
				));
				//////////////////////////////////////////////////////////////
				// Tipos de Doc para Notas de Débito.
				docTypeName = DT_DEBIT_NOTE_NAME + " " + letter + "-" + posNumber; 
				printName   = DT_DEBIT_NOTE_NAME + " " + letter;
				newDocTypes.add(new DocTypeData(getOrgID(), getOrgParentCUIT(),
						docTypeName, printName, MDocType.DOCBASETYPE_ARInvoice,
						GL_ARI, MDocType.SIGNO_ISSOTRX_1, letter, 
						MDocType.FISCALDOCUMENT_DebitNote,
						MDocType.DOCTYPE_CustomerDebitNote
				));
				//////////////////////////////////////////////////////////////
				// Tipos de Doc para Notas de Crédito.
				docTypeName = DT_CREDIT_NOTE_NAME + " " + letter + "-" + posNumber; 
				printName   = DT_CREDIT_NOTE_NAME + " " + letter;
				newDocTypes.add(new DocTypeData(getOrgID(), getOrgParentCUIT(),
						docTypeName, printName, MDocType.DOCBASETYPE_ARCreditMemo,
						GL_ARI, MDocType.SIGNO_ISSOTRX__1, letter,
						MDocType.FISCALDOCUMENT_CreditNote,
						MDocType.DOCTYPE_CustomerCreditNote
				));
			}
		}
		return newDocTypes;
	}
	
	@Override
	protected String get_TrxName() {
		if (trxName == null || trxName.length() == 0)
			return super.get_TrxName();
		else
			return trxName;
	}
	
	protected void set_TrxName(String trxName) {
		this.trxName = trxName;
	}
	
	protected int getOrgID() {
		return orgID;
	}

	protected void setOrgID(int orgID) {
		this.orgID = orgID;
	}

	protected String getOrgParentCUIT() {
		return orgParentCUIT;
	}

	protected void setOrgParentCUIT(String orgParentCUIT) {
		this.orgParentCUIT = orgParentCUIT;
	}

	/**
	 * POJO para tipos de documentos a crear.
	 */
	private class DocTypeData {
		private String name;
		private String printName;
		private String docBaseType;
		private int glCategoryID;
		private String signo;
		private String letter;
		private String fiscalDocument;
		private String docTypeOriginalKey;
		private int orgID;
		private String orgCUIT;
	
		/**
		 * Constructor de la clase.
		 * 
		 * @param orgID
		 *            id de la organización del tipo de doc
		 * @param orgCUIT
		 *            cuit de la organización a concatenar en la clave del tipo
		 *            de doc
		 * @param name
		 *            Nombre del tipo de documento y secuencia.
		 * @param printName
		 *            Nombre de impresión.
		 * @param docBaseType
		 *            Tipo de documento base.
		 * @param glCategoryID
		 *            Categoría GL.
		 * @param signo
		 *            Signo del tipo de documento.
		 * @param letter
		 *            Letra del tipo de documento.
		 * @param fiscalDocument
		 *            Tipo de comprobante.
		 * @param docTypeOriginalKey
		 *            Clave base para el tipo de documento.
		 */
		public DocTypeData(int orgID, String orgCUIT, String name, String printName, String docBaseType, int glCategoryID, String signo, String letter, String fiscalDocument, String docTypeOriginalKey) {
			super();
			this.name = name;
			this.printName = printName;
			this.docBaseType = docBaseType;
			this.glCategoryID = glCategoryID;
			this.signo = signo;
			this.letter = letter;
			this.fiscalDocument = fiscalDocument;
			this.docTypeOriginalKey = docTypeOriginalKey;
			this.orgID = orgID;
			this.orgCUIT = orgCUIT;
		}
		
		public String getDocTypeKey() {
			return getDocTypeOriginalKey() + getLetter() + getFormattedPosNumber() + getOrgCUIT();
		}

		public String getDocBaseType() {
			return docBaseType;
		}

		public int getGlCategoryID() {
			return glCategoryID;
		}

		public String getLetter() {
			return letter;
		}

		public String getName() {
			return name;
		}

		public String getPrintName() {
			return printName;
		}

		public String getSigno() {
			return signo;
		}

		public String getFiscalDocument() {
			return fiscalDocument;
		}

		public String getDocTypeOriginalKey() {
			return docTypeOriginalKey;
		}

		public void setDocTypeOriginalKey(String docTypeOriginalKey) {
			this.docTypeOriginalKey = docTypeOriginalKey;
		}
		
		public int getOrgID(){
			return this.orgID;
		}
		
		public String getOrgCUIT(){
			return this.orgCUIT;
		}
	}
}
