package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class CalloutCash extends CalloutEngine {

	
	public CalloutCash(){
		super();
	}
	
	/**
	 * Validaciones iniciales
	 * @param value
	 * @return booleano que determina si todo estuvo bien o no
	 */
	
	protected boolean prepare(Object value){
		
		//Si no hay nada en el value
		if(value == null){
			return false;
		}
		
		//Si lo que hay en value es 0 (puede ser por organizacion = *)
		Integer valor = (Integer)value;
		if(valor.intValue() == 0){
			return false;
		}
		
		if( isCalloutActive()) {
            return false;
        }
		
		setCalloutActive(true);
	
		return true;
	}
	
	/**
	 * Realiza la consulta sql
	 * @return la sentencia sql
	 */
	
	protected String getSql(){
		return new String("select dateacct,endingbalance "+
				"from c_cash "+
				"where (isactive = 'Y') and (c_cashbook_id = ?) and (ad_org_id = ?) and docstatus IN ('CO','CL') "+
				"order by dateacct desc");
	}
	
	/**
	 * Callout para cuando se cambia el libro de caja, se deposite en saldo inicial lo que tenia 
	 * la caja anterior (por fecha)
	 * @param ctx
	 * @param WindowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @return
	 */
	
	public String saldoAnteriorLibro( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
		
		//Validaciones iniciales
		
		if(!this.prepare(value)){
			return "";
		}
		
		
		int cashBookID = (Integer)value;
		String cashBookType = null;
		if (cashBookID > 0)
		{
			// Setear el nombre correspondiente
			X_C_CashBook aCashBook = new X_C_CashBook(ctx, (Integer)value, null);
			mTab.setValue("Name", aCashBook.getName() + " " + getDateTime());
			
			// Asigna el Tipo de Libro de Caja a  partir del CashBookType
			cashBookType = aCashBook.getCashBookType();
			mTab.setValue("CashBookType", cashBookType);
		}
		
		// Cargar el saldo inicial
		// Por defecto el saldo inicial es cero
		BigDecimal balance = BigDecimal.ZERO;
		
		// Para libros que son de tipo Caja General, se carga el saldo inicial
		// a partir del saldo del libro diario anterior.
		if (MCashBook.CASHBOOKTYPE_GeneralCashBook.equals(cashBookType)) {
			String sql = this.getSql();
			
			try {
				PreparedStatement pst = DB.prepareStatement( sql );
				//Primer Parámetro : el libro de caja cambiado
				pst.setInt(1, cashBookID);
				//Segundo Parámetro : la organización 
				Integer orgId = (Integer)mTab.getField("ad_org_id").getValue();
				pst.setInt(2, orgId.intValue());
				
				ResultSet rs = pst.executeQuery();
				
				if(rs.next()){
					//Saldo inicial : el saldo final de la caja anterior
					balance = rs.getBigDecimal("endingbalance");
				}
				
				rs.close();
				pst.close();
				
			}catch(Exception e){
				setCalloutActive(false);
				return "";
			}
		}
		
		mTab.setValue("BeginningBalance", balance);
		
		setCalloutActive(false);
		
		return "";
	}
	
	/**
	 * Callout para cuando se cambia la organización, se deposite en saldo inicial lo que tenia 
	 * la caja anterior (por fecha)
	 * @param ctx
	 * @param WindowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @return
	 */
	
	public String saldoAnteriorOrg( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
		
		//Validaciones iniciales
		
		if(!this.prepare(value)){
			return "";
		}
		
		Integer valor = (Integer)value;
		
		// Si cambia la organización, setear la caja general que existe en las preferences
		String preferenceValue = MPreference.GetCustomPreferenceValue("C_CashBook_ID", Env.getAD_Client_ID(ctx),
				valor, null, true);
		if(!Util.isEmpty(preferenceValue)){
			mTab.setValue("C_CashBook_ID", Integer.parseInt(preferenceValue));
		}
		
		String sql = this.getSql();
		
		try {
			PreparedStatement pst = DB.prepareStatement( sql );
			//Primer Parámetro : el libro de caja
			Integer bookId = (Integer)mTab.getField("c_cashbook_id").getValue();
			pst.setInt(1, bookId.intValue());
			//Segundo Parámetro : la organización cambiada
			pst.setInt(2, valor.intValue());
			
			ResultSet rs = pst.executeQuery();
			
			if(rs.next()){
				//Saldo inicial : el saldo final de la caja anterior
				mTab.setValue("BeginningBalance",rs.getBigDecimal("endingbalance"));	
			}
			else{
				//Saldo inicial : 0 (no hay libros anteriores)
				mTab.setValue("BeginningBalance",new BigDecimal(0));
			}
			
			rs.close();
			pst.close();
			
		}catch(Exception e){
			setCalloutActive(false);
			return "";
		}
		
		setCalloutActive(false);
		
		return "";
	}
	
	
	/**
	 * Retorna el dia actual en formato anio mes dia
	 */
    private String getDateTime() {
        /* Modificado para que sugiera el nombre con el mismo formato que 
         * se genera en MCash al crearlo a partir de su CashBook.
         * -> método public MCash(MCashBook cashBook).
    	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
        */
        return DisplayType.getDateFormat( DisplayType.Date ).format(new Date());
    }

}
