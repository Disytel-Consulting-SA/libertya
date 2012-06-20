/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.lang.Integer;

import javax.swing.JOptionPane;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CalloutBankStatement extends CalloutEngine {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param mTab
     * @param mField
     * @param value
     *
     * @return
     */

    public String bankAccount( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        if( value == null ) {
            return "";
        }

        int          C_BankAccount_ID = (( Integer )value ).intValue();
        MBankAccount ba               = MBankAccount.get( ctx,C_BankAccount_ID );

        mTab.setValue( "BeginningBalance",ba.getCurrentBalance());

        return "";
    }    // bankAccount

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param mTab
     * @param mField
     * @param value
     *
     * @return
     */

    public String amount( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        if( isCalloutActive()) {
            return "";
        }

        setCalloutActive( true );

        // Get Stmt & Trx

        
        
        BigDecimal stmt = ( BigDecimal )mTab.getValue( "StmtAmt" );

        if( stmt == null ) {
            stmt = Env.ZERO;
        }

        BigDecimal trx = ( BigDecimal )mTab.getValue( "TrxAmt" );

        if( trx == null ) {
            trx = Env.ZERO;
        }

        BigDecimal bd = stmt.subtract( trx );

        // Charge - calculate Interest

        if( mField.getColumnName().equals( "ChargeAmt" )) {
            BigDecimal charge = ( BigDecimal )value;

            if( charge == null ) {
                charge = Env.ZERO;
            }

            bd = bd.subtract( charge );

            // log.trace(log.l5_DData, "Interest (" + bd + ") = Stmt(" + stmt + ") - Trx(" + trx + ") - Charge(" + charge + ")");

            mTab.setValue( "InterestAmt",bd );
        }

        // Calculate Charge

        else {
            BigDecimal interest = ( BigDecimal )mTab.getValue( "InterestAmt" );

            if( interest == null ) {
                interest = Env.ZERO;
            }

            bd = bd.subtract( interest );

            // log.trace(log.l5_DData, "Charge (" + bd + ") = Stmt(" + stmt + ") - Trx(" + trx + ") - Interest(" + interest + ")");

            mTab.setValue( "ChargeAmt",bd );
        }

        setCalloutActive( false );

        return "";
    }    // amount

    public String amt( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        if( isCalloutActive()) {
            return "";
        }

        setCalloutActive( true );

        BigDecimal trxAmt = (BigDecimal)mTab.getValue("TrxAmt");
        trxAmt = (trxAmt == null ? BigDecimal.ZERO : trxAmt);
        
        BigDecimal chargeAmt = (BigDecimal)mTab.getValue("ChargeAmt");
        chargeAmt = (chargeAmt == null ? BigDecimal.ZERO : chargeAmt);
         
        BigDecimal stmtAmt = trxAmt.add(chargeAmt);
        
        mTab.setValue("StmtAmt", stmtAmt);
        
        setCalloutActive( false );

        return "";
    }
    
    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param mTab
     * @param mField
     * @param value
     *
     * @return
     */
    public String cuentaOK(Properties ctx,int WindowNo,MTab mTab,MField mField,Object value)
    {
    	 if( isCalloutActive()) {
             return "";
         }

         setCalloutActive( true );
    		
    		int parcial1=0;
    		int parcial2=0;
    		String prueba = mTab.get_ValueAsString("Oficina")+mTab.get_ValueAsString("Sucursal")+mTab.get_ValueAsString("DC")+mTab.getValue("CC");
    	
    		String Num_cuenta = prueba;
    		mTab.setValue( "AccountNo",Num_cuenta );
    		if (Num_cuenta.length()!=20)
    		{	
    			JOptionPane.showMessageDialog( null,"Numero de cuanta invalido",null, JOptionPane.INFORMATION_MESSAGE );
    			mTab.setValue( "AccountNo","" );
    			mTab.setValue("Oficina","");
    			mTab.setValue("Sucursal","");
    			mTab.setValue("Oficina","");
    			mTab.setValue("DC","");
    			mTab.setValue("CC","");
    			setCalloutActive( false );
    			return "";
    			
    		}
    			
    		String parte1= Num_cuenta.substring(0,8);
    		String parte2= Num_cuenta.substring(10,20);
    		//Calculo del primer digito de control.
    		 int[]  pesos = {1,2,4,8,5,10,9,7,3,6};
    		 int j=0;
    		 int aux=1;
    		 for(int i=2;i<=9; i++)
    		 {
    			 aux = Integer.parseInt(parte1.substring(j,j+1)); 
    			 parcial1= parcial1 + (aux*pesos[i]);
    			 j++;	 
    		 }
    		 int resto= parcial1%11;
    		 int digito1=11-resto;
    		 if (digito1==11)
    			 digito1=0;
    		 else if (digito1==10)
    			 	digito1=1; 
    		 //Calculo del segundo digito de Control.
    		 int k=0;
    		 int aux1;
    		 for(int i=0;i<=9; i++)
    		 {
    			 aux1 = Integer.parseInt(parte2.substring(k,k+1));
    			 parcial2= parcial2 + (aux1*pesos[i]);
    			 k++;	 
    		 }
    		 int resto1=parcial2%11;
    		 int digito2=11-resto1;
    		 if (digito2==11)
    			 digito2=0;
    		 else if (digito2==10)
    			 	digito2=1;
    		 
    		 if (digito1!=Integer.parseInt(Num_cuenta.substring(8,9)) || digito2!=Integer.parseInt(Num_cuenta.substring(9,10)))
    		 {	
    			 JOptionPane.showMessageDialog( null,"Numero de cuenta invalido",null, JOptionPane.INFORMATION_MESSAGE );
    			 mTab.setValue( "AccountNo","" );
     			 mTab.setValue("Oficina","");
     			 mTab.setValue("Sucursal","");
     			 mTab.setValue("Oficina","");
     			 mTab.setValue("DC","");
     			 mTab.setValue("CC","");
    			 
    		 }
    		 
    		 setCalloutActive( false );	
    		 return "";
    	   		 
    }		

    public String payment( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        Integer C_Payment_ID = ( Integer )value;

        if( (C_Payment_ID == null) || (C_Payment_ID.intValue() == 0) ) {
            return "";
        }

        //

        BigDecimal stmt = ( BigDecimal )mTab.getValue( "StmtAmt" );

        if( stmt == null ) {
            stmt = Env.ZERO;
        }

        String sql = "SELECT PayAmt FROM C_Payment_v WHERE C_Payment_ID=?";    // 1

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,C_Payment_ID.intValue());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                BigDecimal bd = rs.getBigDecimal( 1 );

                mTab.setValue( "TrxAmt",bd );

                if( stmt.compareTo( Env.ZERO ) == 0 ) {
                    mTab.setValue( "StmtAmt",bd );
                }
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"BankStmt_Payment",e );

            return e.getLocalizedMessage();
        }

        // Recalculate Amounts

        amount( ctx,WindowNo,mTab,mField,value );

        return "";
    }    // payment
}    // CalloutBankStatement



/*
 *  @(#)CalloutBankStatement.java   02.07.07
 * 
 *  Fin del fichero CalloutBankStatement.java
 *  
 *  Versión 2.2
 *
 */
