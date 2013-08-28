package org.openXpertya.process;

import java.util.ArrayList;
import java.util.List;

import org.openXpertya.model.FiscalDocumentPrint;
import org.openXpertya.model.MControladorFiscal;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MPOS;
import org.openXpertya.model.MPOSJournal;
import org.openXpertya.model.MRefList;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.AUserAuthModel;
import org.openXpertya.util.Msg;
import org.openXpertya.util.UserAuthConstants;
import org.openXpertya.util.UserAuthData;
import org.openXpertya.util.Util;

public class OpenDrawerProcess extends SvrProcess {

	/** Letra a buscar */
	private static final String LETTER = "A";

	/** Clave del Tipo de Documento a buscar */
	private static final String DOCTYPE_BASE_KEY = MDocType.DOCTYPE_CustomerInvoice;
	
	/** ID de la config del TPV actual */
	private Integer posID;
	
	/** Punto de Venta Actual */
	private Integer posNumber;
	
	/** Nombre de usuario */
	private String userName;
	
	/** Clave */
	private String password;
	
	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();
            if( name.equals( "C_POS_ID" )) {
            	setPosID(para[i].getParameterAsInt());
            }
            else if(name.equals( "UserName" )){
            	setUserName((String)para[i].getParameter());
            }
            else if(name.equals( "Password" )){
            	setPassword((String)para[i].getParameter());
            }
        }
        
        // Obtener el punto de venta desde la config del tpv
        if(!Util.isEmpty(getPosID(), true)){
        	MPOS pos = MPOS.get(getCtx(), getPosID());
        	setPosNumber(pos.getPOSNumber());
        }
		// Si no existe el id del tpv significa que las cajas diarias están
		// activas, pero por las dudas se controla
        else if (MPOSJournal.isActivated()) {
			MPOSJournal posJournal = MPOSJournal.getCurrent();
			if(posJournal != null){
				setPosID(posJournal.getC_POS_ID());
				setPosNumber(MPOSJournal.getCurrentPOSNumber(null));
			}
        }
	}

	@Override
	protected String doIt() throws Exception {
		if(Util.isEmpty(getPosNumber(), true)){
			throw new Exception(Msg.getMsg(getCtx(), "CanGetPOSNumber"));
		}
		MDocType docType = MDocType.getDocType(getCtx(), DOCTYPE_BASE_KEY,
				LETTER, getPosNumber(), get_TrxName());
		// Si tiene configurado una impresora fiscal, entonces abro el cajón de
		// dinero
		if(docType != null){
			if(!Util.isEmpty(docType.getC_Controlador_Fiscal_ID(), true)){
				// Autorización de usuario
				validateUserAccess();
				// Apertura del cajón de dinero
				MControladorFiscal controladorFiscal = new MControladorFiscal(
						getCtx(), docType.getC_Controlador_Fiscal_ID(),
						get_TrxName());
				FiscalDocumentPrint fdp = new FiscalDocumentPrint();
				if(!fdp.openDrawer(docType.getC_Controlador_Fiscal_ID())){
					throw new Exception(Msg.getMsg(getCtx(), "OpenDrawerError")
							+ "."
							+ controladorFiscal.getName()
							+ (Util.isEmpty(fdp.getErrorMsg(), true) ? ""
									: "." + fdp.getErrorMsg()));
				}
			}
			else{
				throw new Exception(Msg.translate(getCtx(),
						"@DocTypeNotFiscalPrinterConfiguredError@ " + docType.getName()));
			}
		}
		// No existe el tipo de documento para el punto de venta, letra y tipo
		// de documento base
		else{
			throw new Exception(Msg.getMsg(
					getCtx(),
					"NotExistsDocForPOSNumberLetterDocBase",
					new Object[] {
							getPosNumber(),
							LETTER,
							MRefList.getListName(getCtx(),
									MDocType.DOCBASETYPE_AD_Reference_ID,
									DOCTYPE_BASE_KEY) }));
		}
		return null;
	}

	
	/**
	 * Validación del usuario para abrir el cajón de dinero
	 * @throws Exception
	 */
	protected void validateUserAccess() throws Exception{
		if (Util.isEmpty(getUserName(), true)
				&& Util.isEmpty(getPassword(), true)) {
			throw new Exception(Msg.getMsg(getCtx(), "MustInsertAuthorizedUser"));
		}
		else{
			UserAuthData userAuthData = new UserAuthData(getUserName(),getPassword());
			List<String> operations = new ArrayList<String>();
			operations.add(UserAuthConstants.OPEN_DRAWER_UID);
			userAuthData.setAuthOperations(operations);
			AUserAuthModel userAuthValidation = AUserAuthModel.get();
			CallResult authorized = userAuthValidation.validateAuthorization(userAuthData);
			if(authorized.isError()){
				throw new Exception(authorized.getMsg());
			}
		}
	}
	
	
	protected Integer getPosNumber() {
		return posNumber;
	}

	protected void setPosNumber(Integer posNumber) {
		this.posNumber = posNumber;
	}

	protected Integer getPosID() {
		return posID;
	}

	protected void setPosID(Integer posID) {
		this.posID = posID;
	}

	protected String getUserName() {
		return userName;
	}

	protected void setUserName(String userName) {
		this.userName = userName;
	}

	protected String getPassword() {
		return password;
	}

	protected void setPassword(String password) {
		this.password = password;
	}

}
