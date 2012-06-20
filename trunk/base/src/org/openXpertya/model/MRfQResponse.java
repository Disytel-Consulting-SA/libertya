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
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.print.ReportEngine;
import org.openXpertya.util.DB;
import org.openXpertya.util.EMail;
import org.openXpertya.util.Env;
import org.openXpertya.util.TimeUtil;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MRfQResponse extends X_C_RfQResponse {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_RfQResponse_ID
     * @param trxName
     */

    public MRfQResponse( Properties ctx,int C_RfQResponse_ID,String trxName ) {
        super( ctx,C_RfQResponse_ID,trxName );

        if( C_RfQResponse_ID == 0 ) {
            setIsComplete( false );
            setIsSelectedWinner( false );
            setIsSelfService( false );
            setPrice( Env.ZERO );
            setProcessed( false );
            setProcessing( false );
        }
    }    // MRfQResponse

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MRfQResponse( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MRfQResponse

    /**
     * Constructor de la clase ...
     *
     *
     * @param rfq
     * @param subscriber
     */

    public MRfQResponse( MRfQ rfq,MRfQTopicSubscriber subscriber ) {
        this( rfq,subscriber,subscriber.getC_BPartner_ID(),subscriber.getC_BPartner_Location_ID(),subscriber.getAD_User_ID());
    }    // MRfQResponse

    /**
     * Constructor de la clase ...
     *
     *
     * @param rfq
     * @param partner
     */

    public MRfQResponse( MRfQ rfq,MBPartner partner ) {
        this( rfq,null,partner.getC_BPartner_ID(),partner.getPrimaryC_BPartner_Location_ID(),partner.getPrimaryAD_User_ID());
    }    // MRfQResponse

    /**
     * Constructor de la clase ...
     *
     *
     * @param rfq
     * @param subscriber
     * @param C_BPartner_ID
     * @param C_BPartner_Location_ID
     * @param AD_User_ID
     */

    public MRfQResponse( MRfQ rfq,MRfQTopicSubscriber subscriber,int C_BPartner_ID,int C_BPartner_Location_ID,int AD_User_ID ) {
        this( rfq.getCtx(),0,rfq.get_TrxName());
        setClientOrg( rfq );
        setC_RfQ_ID( rfq.getC_RfQ_ID());
        setC_Currency_ID( rfq.getC_Currency_ID());
        setName( rfq.getName());
        m_rfq = rfq;

        // Subscriber info

        setC_BPartner_ID( C_BPartner_ID );
        setC_BPartner_Location_ID( C_BPartner_Location_ID );
        setAD_User_ID( AD_User_ID );

        // Create Lines

        MRfQLine[] lines = rfq.getLines();

        for( int i = 0;i < lines.length;i++ ) {
            if( !lines[ i ].isActive()) {
                continue;
            }

            // Product on "Only" list

            if( (subscriber != null) &&!subscriber.isIncluded( lines[ i ].getM_Product_ID())) {
                continue;
            }

            //

            if( getID() == 0 ) {    // save Response
                save();
            }

            MRfQResponseLine line = new MRfQResponseLine( this,lines[ i ] );

            // line is not saved (dumped) if there are no Qtys

        }
    }                               // MRfQResponse

    /** Descripción de Campos */

    private MRfQ m_rfq = null;

    /** Descripción de Campos */

    private MRfQResponseLine[] m_lines = null;

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public MRfQResponseLine[] getLines( boolean requery ) {
        if( (m_lines != null) &&!requery ) {
            return m_lines;
        }

        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM C_RfQResponseLine " + "WHERE C_RfQResponse_ID=? AND IsActive='Y'";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_RfQResponse_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MRfQResponseLine( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getLines",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        m_lines = new MRfQResponseLine[ list.size()];
        list.toArray( m_lines );

        return m_lines;
    }    // getLines

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MRfQResponseLine[] getLines() {
        return getLines( false );
    }    // getLines

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MRfQ getRfQ() {
        if( m_rfq == null ) {
            m_rfq = MRfQ.get( getCtx(),getC_RfQ_ID(),get_TrxName());
        }

        return m_rfq;
    }    // getRfQ

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MRfQResponse[" );

        sb.append( getID()).append( ",Complete=" ).append( isComplete()).append( ",Winner=" ).append( isSelectedWinner()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean sendRfQ() {
        MUser to = MUser.get( getCtx(),getAD_User_ID());

        if( (to.getID() == 0) || (to.getEMail() == null) || (to.getEMail().length() == 0) ) {
            log.log( Level.SEVERE,"No User or no EMail - " + to );

            return false;
        }

        MClient client = MClient.get( getCtx());

        //

        String message = getDescription();

        if( (message == null) || (message.length() == 0) ) {
            message = getHelp();
        } else if( getHelp() != null ) {
            message += "\n" + getHelp();
        }

        if( message == null ) {
            message = getName();
        }

        //

        EMail email = new EMail( client,null,to,"RfQ: " + getName(),message );

        email.addAttachment( createPDF());

        if( EMail.SENT_OK.equals( email.send())) {
            setDateInvited( new Timestamp( System.currentTimeMillis()));
            save();

            return true;
        }

        return false;
    }    // sendRfQ

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public File createPDF() {
        return createPDF( null );
    }    // getPDF

    /**
     * Descripción de Método
     *
     *
     * @param file
     *
     * @return
     */

    public File createPDF( File file ) {
        ReportEngine re = ReportEngine.get( getCtx(),ReportEngine.RFQ,getC_RfQResponse_ID());

        if( re == null ) {
            return null;
        }

        return re.getPDF( file );
    }    // getPDF

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String checkComplete() {
        if( isComplete()) {
            setIsComplete( false );
        }

        MRfQ rfq = getRfQ();

        // Is RfQ Total valid

        String error = rfq.checkQuoteTotalAmtOnly();

        if( (error != null) && (error.length() > 0) ) {
            return error;
        }

        // Do we have Total Amount ?

        if( rfq.isQuoteTotalAmt() || rfq.isQuoteTotalAmtOnly()) {
            BigDecimal amt = getPrice();

            if( (amt == null) || (Env.ZERO.compareTo( amt ) >= 0) ) {
                return "No Total Amount";
            }
        }

        // Do we have an amount/qty for all lines

        if( rfq.isQuoteAllLines()) {
            MRfQResponseLine[] lines = getLines( false );

            for( int i = 0;i < lines.length;i++ ) {
                MRfQResponseLine line = lines[ i ];

                if( !line.isActive()) {
                    return "Line " + line.getRfQLine().getLine() + ": Not Active";
                }

                boolean               validAmt = false;
                MRfQResponseLineQty[] qtys     = line.getQtys( false );

                for( int j = 0;j < qtys.length;j++ ) {
                    MRfQResponseLineQty qty = qtys[ j ];

                    if( !qty.isActive()) {
                        continue;
                    }

                    BigDecimal amt = qty.getNetAmt();

                    if( (amt != null) && (Env.ZERO.compareTo( amt ) < 0) ) {
                        validAmt = true;

                        break;
                    }
                }

                if( !validAmt ) {
                    return "Line " + line.getRfQLine().getLine() + ": No Amount";
                }
            }
        }

        // Do we have an amount for all line qtys

        if( rfq.isQuoteAllQty()) {
            MRfQResponseLine[] lines = getLines( false );

            for( int i = 0;i < lines.length;i++ ) {
                MRfQResponseLine      line = lines[ i ];
                MRfQResponseLineQty[] qtys = line.getQtys( false );

                for( int j = 0;j < qtys.length;j++ ) {
                    MRfQResponseLineQty qty = qtys[ j ];

                    if( !qty.isActive()) {
                        return "Line " + line.getRfQLine().getLine() + " Qty=" + qty.getRfQLineQty().getQty() + ": Not Active";
                    }

                    BigDecimal amt = qty.getNetAmt();

                    if( (amt == null) || (Env.ZERO.compareTo( amt ) >= 0) ) {
                        return "Line " + line.getRfQLine().getLine() + " Qty=" + qty.getRfQLineQty().getQty() + ": No Amount";
                    }
                }
            }
        }

        setIsComplete( true );

        return null;
    }    // checkComplete

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isQuoteTotalAmtOnly() {
        return getRfQ().isQuoteTotalAmtOnly();
    }    // isQuoteTotalAmtOnly

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {

        // Calculate Complete Date (also used to verify)

        if( (getDateWorkStart() != null) && (getDeliveryDays() != 0) ) {
            setDateWorkComplete( TimeUtil.addDays( getDateWorkStart(),getDeliveryDays()));

            // Calculate Delivery Days

        } else if( (getDateWorkStart() != null) && (getDeliveryDays() == 0) && (getDateWorkComplete() != null) ) {
            setDeliveryDays( TimeUtil.getDaysBetween( getDateWorkStart(),getDateWorkComplete()));

            // Calculate Start Date

        } else if( (getDateWorkStart() == null) && (getDeliveryDays() != 0) && (getDateWorkComplete() != null) ) {
            setDateWorkStart( TimeUtil.addDays( getDateWorkComplete(),getDeliveryDays() * -1 ));
        }

        return true;
    }    // beforeSave
}    // MRfQResponse



/*
 *  @(#)MRfQResponse.java   02.07.07
 * 
 *  Fin del fichero MRfQResponse.java
 *  
 *  Versión 2.2
 *
 */
