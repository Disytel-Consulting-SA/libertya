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

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.openXpertya.util.Ini;

import com.Verisign.payment.PFProAPI;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class PP_PayFlowPro extends PaymentProcessor implements Serializable {

    /**
     * Constructor de la clase ...
     *
     */

    public PP_PayFlowPro() {
        super();
        m_pp = new PFProAPI();

        String path = Ini.getOXPHome() + File.separator + "lib";

        // Needs Certification File (not dustributed)

        File file = new File( path,"f73e89fd.0" );

        if( !file.exists()) {
            log.log( Level.SEVERE,"No cert file " + file.getAbsolutePath());
        }

        m_pp.SetCertPath( path );
    }    // PP_PayFowPro

    // Payment System                  */

    /** Descripción de Campos */

    private PFProAPI m_pp = null;

    /** Descripción de Campos */

    private boolean m_ok = false;

    /** Descripción de Campos */

    public final static String RESULT_OK = "0";

    /** Descripción de Campos */

    public final static String RESULT_DECLINED = "12";

    /** Descripción de Campos */

    public final static String RESULT_INVALID_NO = "23";

    /** Descripción de Campos */

    public final static String RESULT_INVALID_EXP = "24";

    /** Descripción de Campos */

    public final static String RESULT_INSUFFICIENT_FUNDS = "50";

    /** Descripción de Campos */

    public final static String RESULT_TIMEOUT_PROCESSOR = "104";

    /** Descripción de Campos */

    public final static String RESULT_TIMEOUT_HOST = "109";

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getVersion() {
        return "PayFlowPro " + m_pp.Version();
    }    // getVersion

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws IllegalArgumentException
     */

    public boolean processCC() throws IllegalArgumentException {
        log.fine( "processCC - " + p_mpp.getHostAddress() + " " + p_mpp.getHostPort() + ", Timeout=" + getTimeout() + "; Proxy=" + p_mpp.getProxyAddress() + " " + p_mpp.getProxyPort() + " " + p_mpp.getProxyLogon() + " " + p_mpp.getProxyPassword());

        //

        StringBuffer param = new StringBuffer();

        // Transaction Type

        if( p_mp.getTrxType().equals( MPayment.TRXTYPE_Sales )) {
            param.append( "TRXTYPE=" ).append( p_mp.getTrxType());
        } else {
            throw new IllegalArgumentException( "PP_PayFlowPro TrxType not supported - " + p_mp.getTrxType());
        }

        // Mandatory Fields

        param.append( "&TENDER=C" )                                                                     // CreditCard
            .append( "&ACCT=" ).append( MPaymentValidate.checkNumeric( p_mp.getCreditCardNumber()));    // CreditCard No
        param.append( "&EXPDATE=" );    // ExpNo

        String month = String.valueOf( p_mp.getCreditCardExpMM());

        if( month.length() == 1 ) {
            param.append( "0" );
        }

        param.append( month );

        int expYY = p_mp.getCreditCardExpYY();

        if( expYY > 2000 ) {
            expYY -= 2000;
        }

        String year = String.valueOf( expYY );

        if( year.length() == 1 ) {
            param.append( "0" );
        }

        param.append( year );
        param.append( "&AMT=" ).append( p_mp.getPayAmt());    // Amount

        // Optional Control Fields             - AuthCode & Orig ID

        param.append( createPair( "&AUTHCODE",p_mp.getVoiceAuthCode(),6 ));
        param.append( createPair( "&ORIGID",p_mp.getOrig_TrxID(),12 ));    // PNREF - returned

        // CVV

        param.append( createPair( "&CVV2",p_mp.getCreditCardVV(),4 ));

        // param.append(createPair("&SWIPE", p_mp.getXXX(), 80));                  //      Track 1+2

        // Address

        param.append( createPair( "&NAME",p_mp.getA_Name(),30 ));
        param.append( createPair( "&STREET",p_mp.getA_Street(),30 ));    // Street
        param.append( createPair( "&ZIP",p_mp.getA_Zip(),9 ));           // Zip 5-9

        // CITY 20, STATE 2,

        param.append( createPair( "&EMAIL",p_mp.getA_EMail(),64 ));    // EMail

        // Amex Fields
        // DESC, SHIPTOZIP, TAXAMT
        // param.append(createPair("&DESC", p_mp.getXXX(), 23));                   //      Description

        param.append( createPair( "&SHIPTOZIP",p_mp.getA_Zip(),6 ));    // Zip 6
        param.append( createPair( "&TAXAMT",p_mp.getTaxAmt(),10 ));     // Tax

        // Invoice No

        param.append( createPair( "&INVNUM",p_mp.getC_Invoice_ID(),9 ));

        // COMMENT1/2

        param.append( createPair( "&COMMENT1",p_mp.getC_Payment_ID(),128 ));    // Comment
        param.append( createPair( "&COMMENT2",p_mp.getC_BPartner_ID(),128 ));    // Comment2

        return process( param.toString());
    }    // processCC

    /**
     * Descripción de Método
     *
     *
     * @param parameter
     *
     * @return
     */

    public boolean process( String parameter ) {
        StringBuffer param = new StringBuffer( parameter );

        // Usr/Pwd

        param.append( "&PARTNER=" ).append( p_mpp.getPartnerID()).append( "&VENDOR=" ).append( p_mpp.getVendorID()).append( "&USER=" ).append( p_mpp.getUserID()).append( "&PWD=" ).append( p_mpp.getPassword());
        log.fine( "process -> " + param.toString());

        // Call the PayFlowPro client.

        int rc = m_pp.CreateContext( p_mpp.getHostAddress(),p_mpp.getHostPort(),getTimeout(),p_mpp.getProxyAddress(),p_mpp.getProxyPort(),p_mpp.getProxyLogon(),p_mpp.getProxyPassword());
        String response = m_pp.SubmitTransaction( param.toString());

        m_pp.DestroyContext();

        //

        log.fine( "process <- " + rc + " - " + response );
        p_mp.setR_Result( "" );
        p_mp.setR_Info( response );    // complete info

        StringTokenizer st = new StringTokenizer( response,"&",false );

        while( st.hasMoreTokens()) {
            String token = st.nextToken();
            int    pos   = token.indexOf( "=" );
            String name  = token.substring( 0,pos );
            String value = token.substring( pos + 1 );

            //

            if( name.equals( "RESULT" )) {
                p_mp.setR_Result( value );
                m_ok = RESULT_OK.equals( value );
            } else if( name.equals( "PNREF" )) {
                p_mp.setR_PnRef( value );
            } else if( name.equals( "RESPMSG" )) {
                p_mp.setR_RespMsg( value );
            } else if( name.equals( "AUTHCODE" )) {
                p_mp.setR_AuthCode( value );
            } else if( name.equals( "AVSADDR" )) {
                p_mp.setR_AvsAddr( value );
            } else if( name.equals( "AVSZIP" )) {
                p_mp.setR_AvsZip( value );
            } else if( name.equals( "IAVS" )) {         // N=YSA, Y=International
                ;
            } else if( name.equals( "CVV2MATCH" )) {    // Y/N X=not supported
                ;
            } else {
                log.log( Level.SEVERE,"process - Response unknown = " + token );
            }
        }

        // Probelms with rc (e.g. 0 with Result=24)

        return m_ok;
    }    // process

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isProcessedOK() {
        return m_ok;
    }    // isProcessedOK

    /**
     * Descripción de Método
     *
     *
     * @param name
     * @param value
     * @param maxLength
     *
     * @return
     */

    private String createPair( String name,BigDecimal value,int maxLength ) {
        if( value == null ) {
            return createPair( name,"0",maxLength );
        } else {
            return createPair( name,String.valueOf( value ),maxLength );
        }
    }    // createPair

    /**
     * Descripción de Método
     *
     *
     * @param name
     * @param value
     * @param maxLength
     *
     * @return
     */

    private String createPair( String name,int value,int maxLength ) {
        if( value == 0 ) {
            return "";
        } else {
            return createPair( name,String.valueOf( value ),maxLength );
        }
    }    // createPair

    /**
     * Descripción de Método
     *
     *
     * @param name
     * @param value
     * @param maxLength
     *
     * @return
     */

    private String createPair( String name,String value,int maxLength ) {

        // Nothing to say

        if( (name == null) || (name.length() == 0) || (value == null) || (value.length() == 0) ) {
            return "";
        }

        StringBuffer retValue = new StringBuffer( name );

        // optional [length]

        if( (value.indexOf( "&" ) != -1) || (value.indexOf( "=" ) != -1) ) {
            retValue.append( "[" ).append( value.length()).append( "]" );
        }

        //

        retValue.append( "=" );

        if( value.length() > maxLength ) {
            retValue.append( value.substring( 0,maxLength ));
        } else {
            retValue.append( value );
        }

        return retValue.toString();
    }    // createPair
}    // PP_PayFowPro



/*
 *  @(#)PP_PayFlowPro.java   02.07.07
 * 
 *  Fin del fichero PP_PayFlowPro.java
 *  
 *  Versión 2.2
 *
 */
