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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MDocTypeCounter extends X_C_DocTypeCounter {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_DocType_ID
     *
     * @return
     */

    public static int getCounterDocType_ID( Properties ctx,int C_DocType_ID ) {

        // Direct Relationship

        MDocTypeCounter dtCounter = getCounterDocType( ctx,C_DocType_ID );

        if( dtCounter != null ) {
            if( !dtCounter.isCreateCounter() ||!dtCounter.isValid()) {
                return -1;
            }

            return dtCounter.getCounter_C_DocType_ID();
        }

        // Indirect Relationship

        int      Counter_C_DocType_ID = 0;
        MDocType dt                   = MDocType.get( ctx,C_DocType_ID );

        if( !dt.isCreateCounter()) {
            return -1;
        }

        String cDocBaseType = getCounterDocBaseType( dt.getDocBaseType());

        if( cDocBaseType == null ) {
            return 0;
        }

        MDocType[] counters = MDocType.getOfDocBaseType( ctx,cDocBaseType );

        for( int i = 0;i < counters.length;i++ ) {
            MDocType counter = counters[ i ];

            if( counter.isDefaultCounterDoc()) {
                Counter_C_DocType_ID = counter.getC_DocType_ID();

                break;
            }

            if( counter.isDefault()) {
                Counter_C_DocType_ID = counter.getC_DocType_ID();
            } else if( i == 0 ) {
                Counter_C_DocType_ID = counter.getC_DocType_ID();
            }
        }

        return Counter_C_DocType_ID;
    }    // getCounterDocType_ID

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_DocType_ID
     *
     * @return
     */

    public static MDocTypeCounter getCounterDocType( Properties ctx,int C_DocType_ID ) {
        Integer         key      = new Integer( C_DocType_ID );
        MDocTypeCounter retValue = ( MDocTypeCounter )s_counter.get( key );

        if( retValue != null ) {
            return retValue;
        }

        // Direct Relationship

        MDocTypeCounter   temp  = null;
        String            sql   = "SELECT * FROM C_DocTypeCounter WHERE C_DocType_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,C_DocType_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next() && (retValue == null) ) {
                retValue = new MDocTypeCounter( ctx,rs,null );

                if( !retValue.isCreateCounter() ||!retValue.isValid()) {
                    temp     = retValue;
                    retValue = null;
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"getCounterDocType",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        if( retValue != null ) {    // valid
            return retValue;
        }

        if( temp != null ) {    // invalid
            return temp;
        }

        return null;    // nothing found
    }                   // getCounterDocType

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_DocTypeCounter_ID
     * @param trxName
     *
     * @return
     */

    public static MDocTypeCounter get( Properties ctx,int C_DocTypeCounter_ID,String trxName ) {
        Integer         key      = new Integer( C_DocTypeCounter_ID );
        MDocTypeCounter retValue = ( MDocTypeCounter )s_cache.get( key );

        if( retValue != null ) {
            return retValue;
        }

        retValue = new MDocTypeCounter( ctx,C_DocTypeCounter_ID,trxName );

        if( retValue.getID() != 0 ) {
            s_cache.put( key,retValue );
        }

        return retValue;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param DocBaseType
     *
     * @return
     */

    public static String getCounterDocBaseType( String DocBaseType ) {
        if( DocBaseType == null ) {
            return null;
        }

        String retValue = null;

        // SO/PO

        if( MDocType.DOCBASETYPE_SalesOrder.equals( DocBaseType )) {
            retValue = MDocType.DOCBASETYPE_PurchaseOrder;
        } else if( MDocType.DOCBASETYPE_PurchaseOrder.equals( DocBaseType )) {
            retValue = MDocType.DOCBASETYPE_SalesOrder;

            // AP/AR Invoice

        } else if( MDocType.DOCBASETYPE_APInvoice.equals( DocBaseType )) {
            retValue = MDocType.DOCBASETYPE_ARInvoice;
        } else if( MDocType.DOCBASETYPE_ARInvoice.equals( DocBaseType )) {
            retValue = MDocType.DOCBASETYPE_APInvoice;

            // Shipment

        } else if( MDocType.DOCBASETYPE_MaterialDelivery.equals( DocBaseType )) {
            retValue = MDocType.DOCBASETYPE_MaterialReceipt;
        } else if( MDocType.DOCBASETYPE_MaterialReceipt.equals( DocBaseType )) {
            retValue = MDocType.DOCBASETYPE_MaterialDelivery;

            // AP/AR CreditMemo

        } else if( MDocType.DOCBASETYPE_APCreditMemo.equals( DocBaseType )) {
            retValue = MDocType.DOCBASETYPE_ARCreditMemo;
        } else if( MDocType.DOCBASETYPE_ARCreditMemo.equals( DocBaseType )) {
            retValue = MDocType.DOCBASETYPE_APCreditMemo;

            // Receipt / Payment

        } else if( MDocType.DOCBASETYPE_ARReceipt.equals( DocBaseType )) {
            retValue = MDocType.DOCBASETYPE_APPayment;
        } else if( MDocType.DOCBASETYPE_APPayment.equals( DocBaseType )) {
            retValue = MDocType.DOCBASETYPE_ARReceipt;

            //

        } else {
            s_log.log( Level.SEVERE,"getCounterDocBaseType for " + DocBaseType + ": None found" );
        }

        return retValue;
    }    // getCounterDocBaseType

    /** Descripción de Campos */

    private static CCache s_cache = new CCache( "C_DocTypeCounter",20 );

    /** Descripción de Campos */

    private static CCache s_counter = new CCache( "C_DocTypeCounter",20 );

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MDocTypeCounter.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_DocTypeCounter_ID
     * @param trxName
     */

    public MDocTypeCounter( Properties ctx,int C_DocTypeCounter_ID,String trxName ) {
        super( ctx,C_DocTypeCounter_ID,trxName );

        if( C_DocTypeCounter_ID == 0 ) {
            setIsCreateCounter( true );    // Y
            setIsValid( false );
        }
    }                                      // MDocTypeCounter

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MDocTypeCounter( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MDocTypeCounter

    /**
     * Descripción de Método
     *
     *
     * @param C_DocType_ID
     */

    public void setC_DocType_ID( int C_DocType_ID ) {
        super.setC_DocType_ID( C_DocType_ID );

        if( isValid()) {
            setIsValid( false );
        }
    }    // setC_DocType_ID

    /**
     * Descripción de Método
     *
     *
     * @param Counter_C_DocType_ID
     */

    public void setCounter_C_DocType_ID( int Counter_C_DocType_ID ) {
        super.setCounter_C_DocType_ID( Counter_C_DocType_ID );

        if( isValid()) {
            setIsValid( false );
        }
    }    // setCounter_C_DocType_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MDocType getDocType() {
        MDocType dt = null;

        if( getC_DocType_ID() > 0 ) {
            dt = MDocType.get( getCtx(),getC_DocType_ID());

            if( dt.getID() == 0 ) {
                dt = null;
            }
        }

        return dt;
    }    // getDocType

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MDocType getCounterDocType() {
        MDocType dt = null;

        if( getCounter_C_DocType_ID() > 0 ) {
            dt = MDocType.get( getCtx(),getCounter_C_DocType_ID());

            if( dt.getID() == 0 ) {
                dt = null;
            }
        }

        return dt;
    }    // getCounterDocType

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String validate() {
        MDocType dt = getDocType();

        if( dt == null ) {
            log.log( Level.SEVERE,"No DocType=" + getC_DocType_ID());
            setIsValid( false );

            return "No Document Type";
        }

        MDocType c_dt = getCounterDocType();

        if( c_dt == null ) {
            log.log( Level.SEVERE,"No Counter DocType=" + getCounter_C_DocType_ID());
            setIsValid( false );

            return "No Counter Document Type";
        }

        //

        String dtBT   = dt.getDocBaseType();
        String c_dtBT = c_dt.getDocBaseType();

        log.fine( dtBT + " -> " + c_dtBT );

        // SO / PO

        if(( MDocType.DOCBASETYPE_SalesOrder.equals( dtBT ) && MDocType.DOCBASETYPE_PurchaseOrder.equals( c_dtBT )) || ( MDocType.DOCBASETYPE_SalesOrder.equals( c_dtBT ) && MDocType.DOCBASETYPE_PurchaseOrder.equals( dtBT ))) {
            setIsValid( true );

            // AP/AR Invoice

        } else if(( MDocType.DOCBASETYPE_APInvoice.equals( dtBT ) && MDocType.DOCBASETYPE_ARInvoice.equals( c_dtBT )) || ( MDocType.DOCBASETYPE_APInvoice.equals( c_dtBT ) && MDocType.DOCBASETYPE_ARInvoice.equals( dtBT ))) {
            setIsValid( true );

            // Shipment

        } else if(( MDocType.DOCBASETYPE_MaterialDelivery.equals( dtBT ) && MDocType.DOCBASETYPE_MaterialReceipt.equals( c_dtBT )) || ( MDocType.DOCBASETYPE_MaterialDelivery.equals( c_dtBT ) && MDocType.DOCBASETYPE_MaterialReceipt.equals( dtBT ))) {
            setIsValid( true );

            // AP/AR CreditMemo

        } else if(( MDocType.DOCBASETYPE_APCreditMemo.equals( dtBT ) && MDocType.DOCBASETYPE_ARCreditMemo.equals( c_dtBT )) || ( MDocType.DOCBASETYPE_APCreditMemo.equals( c_dtBT ) && MDocType.DOCBASETYPE_ARCreditMemo.equals( dtBT ))) {
            setIsValid( true );

            // Receipt / Payment

        } else if(( MDocType.DOCBASETYPE_ARReceipt.equals( dtBT ) && MDocType.DOCBASETYPE_APPayment.equals( c_dtBT )) || ( MDocType.DOCBASETYPE_ARReceipt.equals( c_dtBT ) && MDocType.DOCBASETYPE_APPayment.equals( dtBT ))) {
            setIsValid( true );
        } else {
            log.warning( "NOT - " + dtBT + " -> " + c_dtBT );
            setIsValid( false );

            return "Not valid";
        }

        // Counter should have document numbering

        if( !c_dt.isDocNoControlled()) {
            return "Counter Document Type should be automatically Document Number controlled";
        }

        return null;
    }    // validate

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MDocTypeCounter[" );

        sb.append( getID()).append( "," ).append( getName()).append( ",C_DocType_ID=" ).append( getC_DocType_ID()).append( ",Counter=" ).append( getCounter_C_DocType_ID()).append( ",DocAction=" ).append( getDocAction()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {
        if( getAD_Org_ID() != 0 ) {
            setAD_Org_ID( 0 );
        }

        if( !newRecord && ( is_ValueChanged( "C_DocType_ID" ) || is_ValueChanged( "Counter_C_DocType_ID" ))) {
            setIsValid( false );
        }

        // try to validate

        if( !isValid()) {
            validate();
        }

        return true;
    }    // beforeSave
}    // MDocTypeCounter



/*
 *  @(#)MDocTypeCounter.java   02.07.07
 * 
 *  Fin del fichero MDocTypeCounter.java
 *  
 *  Versión 2.2
 *
 */
