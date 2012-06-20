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

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MBPBankAccount extends X_C_BP_BankAccount {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_BP_BankAccount_ID
     * @param trxName
     */

    public MBPBankAccount( Properties ctx,int C_BP_BankAccount_ID,String trxName ) {
        super( ctx,C_BP_BankAccount_ID,trxName );

        if( C_BP_BankAccount_ID == 0 ) {

            // setC_BPartner_ID (0);

            setIsACH( false );
        }
    }    // MBP_BankAccount

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MBPBankAccount( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MBP_BankAccount

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param bp
     * @param bpc
     * @param location
     */

    public MBPBankAccount( Properties ctx,MBPartner bp,MUser bpc,MLocation location ) {
        super( ctx,0,bp.get_TrxName());
        setIsACH( false );

        //

        setC_BPartner_ID( bp.getC_BPartner_ID());

        //

        setA_Name( bpc.getName());
        setA_EMail( bpc.getEMail());

        //

        setA_Street( location.getAddress1());
        setA_City( location.getCity());
        setA_Zip( location.getPostal());
        setA_State( location.getRegionName( true ));
        setA_Country( location.getCountryName());
    }    // MBP_BankAccount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MBP_BankAccount[" ).append( getID()).append( ", Name=" ).append( getA_Name()).append( "]" );

        return sb.toString();
    }    // toString
}    // MBPBankAccount



/*
 *  @(#)MBPBankAccount.java   02.07.07
 * 
 *  Fin del fichero MBPBankAccount.java
 *  
 *  Versión 2.2
 *
 */
