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
import java.io.FileWriter;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.process.DocAction;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class MPaySelectionCheck extends X_C_PaySelectionCheck {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_Payment_ID
     * @param trxName
     *
     * @return
     */

    public static MPaySelectionCheck getOfPayment( Properties ctx,int C_Payment_ID,String trxName ) {
        MPaySelectionCheck retValue = null;
        String             sql      = "SELECT * FROM C_PaySelectionCheck WHERE C_Payment_ID=?";
        int                count    = 0;
        PreparedStatement  pstmt    = null;

        try {
            pstmt = DB.prepareStatement( sql,trxName );
            pstmt.setInt( 1,C_Payment_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MPaySelectionCheck psc = new MPaySelectionCheck( ctx,rs,trxName );

                if( retValue == null ) {
                    retValue = psc;
                } else if( !retValue.isProcessed() && psc.isProcessed()) {
                    retValue = psc;
                }

                count++;
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"getOfPayment",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        if( count > 1 ) {
            s_log.warning( "getOfPayment -  more then one for C_Payment_ID=" + C_Payment_ID );
        }

        return retValue;
    }    // getOfPayment

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_Payment_ID
     * @param trxName
     *
     * @return
     */

    public static MPaySelectionCheck createForPayment( Properties ctx,int C_Payment_ID,String trxName ) {
        if( C_Payment_ID == 0 ) {
            return null;
        }

        MPayment payment = new MPayment( ctx,C_Payment_ID,null );

        // Map Payment Rule <- Tender Type

        String PaymentRule = MPaySelectionCheck.PAYMENTRULE_Check;

        if( payment.getTenderType().equals( MPayment.TENDERTYPE_CreditCard )) {
            PaymentRule = MPaySelectionCheck.PAYMENTRULE_CreditCard;
        } else if( payment.getTenderType().equals( MPayment.TENDERTYPE_DirectDebit )) {
            PaymentRule = MPaySelectionCheck.PAYMENTRULE_DirectDebit;
        } else if( payment.getTenderType().equals( MPayment.TENDERTYPE_DirectDeposit )) {
            PaymentRule = MPaySelectionCheck.PAYMENTRULE_DirectDeposit;
        }

        // else if (payment.getTenderType().equals(MPayment.TENDERTYPE_Check))
        // PaymentRule = MPaySelectionCheck.PAYMENTRULE_Check;

        // Create new PaySelection

        MPaySelection ps = new MPaySelection( ctx,0,trxName );

        ps.setC_BankAccount_ID( payment.getC_BankAccount_ID());
        ps.setName( Msg.translate( ctx,"C_Payment_ID" ) + ": " + payment.getDocumentNo());
        ps.setDescription( payment.getDescription());
        ps.setPayDate( payment.getDateTrx());
        ps.setTotalAmt( payment.getPayAmt());
        ps.setIsApproved( true );
        ps.save();

        // Create new PaySelection Line

        MPaySelectionLine psl = null;

        if( payment.getC_Invoice_ID() != 0 ) {
            psl = new MPaySelectionLine( ps,10,PaymentRule );
            psl.setC_Invoice_ID( payment.getC_Invoice_ID());
            psl.setIsSOTrx( payment.isReceipt());
            psl.setOpenAmt( payment.getPayAmt().add( payment.getDiscountAmt()));
            psl.setPayAmt( payment.getPayAmt());
            psl.setDiscountAmt( payment.getDiscountAmt());
            psl.setDifferenceAmt( Env.ZERO );
            psl.save();
        }

        // Create new PaySelection Check

        MPaySelectionCheck psc = psc = new MPaySelectionCheck( ps,PaymentRule );

        psc.setC_BPartner_ID( payment.getC_BPartner_ID());
        psc.setC_Payment_ID( payment.getC_Payment_ID());
        psc.setIsReceipt( payment.isReceipt());
        psc.setPayAmt( payment.getPayAmt());
        psc.setDiscountAmt( payment.getDiscountAmt());
        psc.setQty( 1 );
        psc.setDocumentNo( payment.getDocumentNo());
        psc.setProcessed( true );
        psc.save();

        // Update optional Line

        if( psl != null ) {
            psl.setC_PaySelectionCheck_ID( psc.getC_PaySelectionCheck_ID());
            psl.setProcessed( true );
            psl.save();
        }

        // Indicate Done

        ps.setProcessed( true );
        ps.save();

        return psc;
    }    // createForPayment

    /**
     * Descripción de Método
     *
     *
     * @param C_PaySelection_ID
     * @param PaymentRule
     * @param startDocumentNo
     * @param trxName
     *
     * @return
     */

    static public MPaySelectionCheck[] get( int C_PaySelection_ID,String PaymentRule,int startDocumentNo,String trxName ) {
        s_log.fine( "get - C_PaySelection_ID=" + C_PaySelection_ID + ", PaymentRule=" + PaymentRule + ", startDocumentNo=" + startDocumentNo );

        ArrayList list  = new ArrayList();
        int       docNo = startDocumentNo;
        String    sql   = "SELECT * FROM C_PaySelectionCheck " + "WHERE C_PaySelection_ID=? AND PaymentRule=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql,trxName );

            pstmt.setInt( 1,C_PaySelection_ID );
            pstmt.setString( 2,PaymentRule );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MPaySelectionCheck check = new MPaySelectionCheck( Env.getCtx(),rs,trxName );

                // Set new Check Document No - saved in confirmPrint

                check.setDocumentNo( String.valueOf( docNo++ ));
                list.add( check );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            s_log.log( Level.SEVERE,"get",e );
        }

        // convert to Array

        MPaySelectionCheck[] retValue = new MPaySelectionCheck[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // createPayments

    /**
     * Descripción de Método
     *
     *
     * @param checks
     * @param file
     *
     * @return
     */

    public static int exportToFile( MPaySelectionCheck[] checks,File file ) {
        if( (checks == null) || (checks.length == 0) ) {
            return 0;
        }

        // Must be a file

        if( file.isDirectory()) {
            s_log.log( Level.SEVERE,"exportToFile - file is directory - " + file.getAbsolutePath());

            return 0;
        }

        // delete if exists

        try {
            if( file.exists()) {
                file.delete();
            }
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"exportToFile - could not delete - " + file.getAbsolutePath(),e );
        }

        char         x       = '"';    // ease
        int          noLines = 0;
        StringBuffer line    = null;

        try {
            FileWriter fw = new FileWriter( file );

            // write header

            line = new StringBuffer();
            line.append( x ).append( "Value" ).append( x ).append( "," ).append( x ).append( "Name" ).append( x ).append( "," ).append( x ).append( "Contact" ).append( x ).append( "," ).append( x ).append( "Addr1" ).append( x ).append( "," ).append( x ).append( "Addr2" ).append( x ).append( "," ).append( x ).append( "City" ).append( x ).append( "," ).append( x ).append( "State" ).append( x ).append( "," ).append( x ).append( "ZIP" ).append( x ).append( "," ).append( x ).append( "Country" ).append( x ).append( "," ).append( x ).append( "ReferenceNo" ).append( x ).append( "," ).append( x ).append( "DocumentNo" ).append( x ).append( "," ).append( x ).append( "PayDate" ).append( x ).append( "," ).append( x ).append( "Currency" ).append( x ).append( "," ).append( x ).append( "PayAmount" ).append( x ).append( "," ).append( x ).append( "Comment" ).append( x ).append( Env.NL );
            fw.write( line.toString());
            noLines++;

            // write lines

            for( int i = 0;i < checks.length;i++ ) {
                MPaySelectionCheck mpp = checks[ i ];

                if( mpp == null ) {
                    continue;
                }

                // BPartner Info

                String bp[] = getBPartnerInfo( mpp.getC_BPartner_ID());

                // Comment - list of invoice document no

                StringBuffer        comment = new StringBuffer();
                MPaySelectionLine[] psls    = mpp.getPaySelectionLines( false );

                for( int l = 0;l < psls.length;l++ ) {
                    if( l > 0 ) {
                        comment.append( ", " );
                    }

                    comment.append( psls[ l ].getInvoice().getDocumentNo());
                }

                line = new StringBuffer();
                line.append( x ).append( bp[ BP_VALUE ] ).append( x ).append( "," )    // Value
                    .append( x ).append( bp[ BP_NAME ] ).append( x ).append( "," )    // Name
                    .append( x ).append( bp[ BP_CONTACT ] ).append( x ).append( "," )    // Contact
                    .append( x ).append( bp[ BP_ADDR1 ] ).append( x ).append( "," )    // Addr1
                    .append( x ).append( bp[ BP_ADDR2 ] ).append( x ).append( "," )    // Addr2
                    .append( x ).append( bp[ BP_CITY ] ).append( x ).append( "," )    // City
                    .append( x ).append( bp[ BP_REGION ] ).append( x ).append( "," )    // State
                    .append( x ).append( bp[ BP_POSTAL ] ).append( x ).append( "," )    // ZIP
                    .append( x ).append( bp[ BP_COUNTRY ] ).append( x ).append( "," )    // Country
                    .append( x ).append( bp[ BP_REFNO ] ).append( x ).append( "," )    // ReferenceNo

                // Payment Info

                .append( x ).append( mpp.getDocumentNo()).append( x ).append( "," )    // DocumentNo
                    .append( mpp.getParent().getPayDate()).append( "," )                                                                       // PayDate
                    .append( x ).append( MCurrency.getISO_Code( Env.getCtx(),mpp.getParent().getC_Currency_ID())).append( x ).append( "," )    // Currency
                    .append( mpp.getPayAmt()).append( "," )                 // PayAmount
                    .append( x ).append( comment.toString()).append( x )    // Comment
                    .append( Env.NL );
                fw.write( line.toString());
                noLines++;
            }                                                               // write line

            fw.flush();
            fw.close();
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"exportToFile",e );
        }

        return noLines;
    }    // exportToFile

    /**
     * Descripción de Método
     *
     *
     * @param C_BPartner_ID
     *
     * @return
     */

    private static String[] getBPartnerInfo( int C_BPartner_ID ) {
        String[] bp  = new String[ 10 ];
        String   sql = "SELECT bp.Value, bp.Name, c.Name AS Contact, " + "a.Address1, a.Address2, a.City, r.Name AS Region, a.Postal, " + "cc.Name AS Country, bp.ReferenceNo " + "FROM C_BPartner bp, AD_User c, C_BPartner_Location l, C_Location a, C_Region r, C_Country cc " + "WHERE bp.C_BPartner_ID=?"    // #1
                       + " AND bp.C_BPartner_ID=c.C_BPartner_ID(+)" + " AND bp.C_BPartner_ID=l.C_BPartner_ID" + " AND l.C_Location_ID=a.C_Location_ID" + " AND a.C_Region_ID=r.C_Region_ID(+)" + " AND a.C_Country_ID=cc.C_Country_ID " + "ORDER BY l.IsBillTo DESC";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql,null );

            pstmt.setInt( 1,C_BPartner_ID );

            ResultSet rs = pstmt.executeQuery();

            //

            if( rs.next()) {
                bp[ BP_VALUE ] = rs.getString( 1 );

                if( bp[ BP_VALUE ] == null ) {
                    bp[ BP_VALUE ] = "";
                }

                bp[ BP_NAME ] = rs.getString( 2 );

                if( bp[ BP_NAME ] == null ) {
                    bp[ BP_NAME ] = "";
                }

                bp[ BP_CONTACT ] = rs.getString( 3 );

                if( bp[ BP_CONTACT ] == null ) {
                    bp[ BP_CONTACT ] = "";
                }

                bp[ BP_ADDR1 ] = rs.getString( 4 );

                if( bp[ BP_ADDR1 ] == null ) {
                    bp[ BP_ADDR1 ] = "";
                }

                bp[ BP_ADDR2 ] = rs.getString( 5 );

                if( bp[ BP_ADDR2 ] == null ) {
                    bp[ BP_ADDR2 ] = "";
                }

                bp[ BP_CITY ] = rs.getString( 6 );

                if( bp[ BP_CITY ] == null ) {
                    bp[ BP_CITY ] = "";
                }

                bp[ BP_REGION ] = rs.getString( 7 );

                if( bp[ BP_REGION ] == null ) {
                    bp[ BP_REGION ] = "";
                }

                bp[ BP_POSTAL ] = rs.getString( 8 );

                if( bp[ BP_POSTAL ] == null ) {
                    bp[ BP_POSTAL ] = "";
                }

                bp[ BP_COUNTRY ] = rs.getString( 9 );

                if( bp[ BP_COUNTRY ] == null ) {
                    bp[ BP_COUNTRY ] = "";
                }

                bp[ BP_REFNO ] = rs.getString( 10 );

                if( bp[ BP_REFNO ] == null ) {
                    bp[ BP_REFNO ] = "";
                }
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            s_log.log( Level.SEVERE,"getBPartnerInfo",e );
        }

        return bp;
    }    // getBPartnerInfo

    /**
     * Descripción de Método
     *
     *
     * @param checks
     * @param batch
     *
     * @return
     */

    public static int confirmPrint( MPaySelectionCheck[] checks,MPaymentBatch batch ) {
        int lastDocumentNo = 0;

        for( int i = 0;i < checks.length;i++ ) {
            MPaySelectionCheck check   = checks[ i ];
            MPayment           payment = new MPayment( check.getCtx(),check.getC_Payment_ID(),null );

            // Existing Payment

            if( check.getC_Payment_ID() != 0 ) {

                // Update check number

                if( check.getPaymentRule().equals( PAYMENTRULE_Check )) {
                    payment.setCheckNo( check.getDocumentNo());

                    if( !payment.save()) {
                        s_log.log( Level.SEVERE,"Payment not saved: " + payment );
                    }
                }
            } else    // New Payment
            {
                payment = new MPayment( check.getCtx(),0,null );

                //

                if( check.getPaymentRule().equals( PAYMENTRULE_Check )) {
                    payment.setBankCheck( check.getParent().getC_BankAccount_ID(),false,check.getDocumentNo());
                } else if( check.getPaymentRule().equals( PAYMENTRULE_CreditCard )) {
                    payment.setTenderType( MPayment.TENDERTYPE_CreditCard );
                } else if( check.getPaymentRule().equals( PAYMENTRULE_DirectDeposit ) || check.getPaymentRule().equals( PAYMENTRULE_DirectDeposit )) {
                    payment.setBankACH( check.getParent().getC_BankAccount_ID(),false );
                } else {
                    s_log.log( Level.SEVERE,"Unsupported Payment Rule=" + check.getPaymentRule());

                    continue;
                }

                payment.setTrxType( MPayment.TRXTYPE_CreditPayment );
                payment.setAmount( check.getParent().getC_Currency_ID(),check.getPayAmt());
                payment.setDiscountAmt( check.getDiscountAmt());
                payment.setDateTrx( check.getParent().getPayDate());
                payment.setC_BPartner_ID( check.getC_BPartner_ID());

                // Link to Batch

                if( batch != null ) {
                    if( batch.getC_PaymentBatch_ID() == 0 ) {
                        batch.save();    // new
                    }

                    payment.setC_PaymentBatch_ID( batch.getC_PaymentBatch_ID());
                }

                // Link to Invoice

                MPaySelectionLine[] psls = check.getPaySelectionLines( false );

                s_log.fine( "confirmPrint - " + check + " (#SelectionLines=" + psls.length + ")" );

                if( (check.getQty() == 1) && (psls != null) && (psls.length == 1) ) {
                    MPaySelectionLine psl = psls[ 0 ];

                    s_log.fine( "Map to Invoice " + psl );

                    //

                    payment.setC_Invoice_ID( psl.getC_Invoice_ID());
                    payment.setDiscountAmt( psl.getDiscountAmt());
                    payment.setWriteOffAmt( psl.getDifferenceAmt());

                    BigDecimal overUnder = psl.getOpenAmt().subtract( psl.getPayAmt()).subtract( psl.getDiscountAmt()).subtract( psl.getDifferenceAmt());

                    payment.setOverUnderAmt( overUnder );
                } else {
                    payment.setDiscountAmt( Env.ZERO );
                }

                payment.setWriteOffAmt( Env.ZERO );

                if( !payment.save()) {
                    s_log.log( Level.SEVERE,"Payment not saved: " + payment );
                }

                //

                int C_Payment_ID = payment.getID();

                if( C_Payment_ID < 1 ) {
                    s_log.log( Level.SEVERE,"Payment not created=" + check );
                } else {
                    check.setC_Payment_ID( C_Payment_ID );
                    check.save();    // Payment process needs it

                    // Should start WF

                    payment.processIt( DocAction.ACTION_Complete );

                    if( !payment.save()) {
                        s_log.log( Level.SEVERE,"Payment not saved: " + payment );
                    }
                }
            }    // new Payment

            // Get Check Document No

            try {
                int no = Integer.parseInt( check.getDocumentNo());

                if( lastDocumentNo < no ) {
                    lastDocumentNo = no;
                }
            } catch( NumberFormatException ex ) {
                s_log.log( Level.SEVERE,"DocumentNo=" + check.getDocumentNo(),ex );
            }

            check.setIsPrinted( true );
            check.setProcessed( true );

            if( !check.save()) {
                s_log.log( Level.SEVERE,"Check not saved: " + check );
            }
        }    // all checks

        s_log.fine( "Last Document No = " + lastDocumentNo );

        return lastDocumentNo;
    }    // confirmPrint

    /** Descripción de Campos */

    static private CLogger s_log = CLogger.getCLogger( MPaySelectionCheck.class );

    /** Descripción de Campos */

    private static final int BP_VALUE = 0;

    /** Descripción de Campos */

    private static final int BP_NAME = 1;

    /** Descripción de Campos */

    private static final int BP_CONTACT = 2;

    /** Descripción de Campos */

    private static final int BP_ADDR1 = 3;

    /** Descripción de Campos */

    private static final int BP_ADDR2 = 4;

    /** Descripción de Campos */

    private static final int BP_CITY = 5;

    /** Descripción de Campos */

    private static final int BP_REGION = 6;

    /** Descripción de Campos */

    private static final int BP_POSTAL = 7;

    /** Descripción de Campos */

    private static final int BP_COUNTRY = 8;

    /** Descripción de Campos */

    private static final int BP_REFNO = 9;

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_PaySelectionCheck_ID
     * @param trxName
     */

    public MPaySelectionCheck( Properties ctx,int C_PaySelectionCheck_ID,String trxName ) {
        super( ctx,C_PaySelectionCheck_ID,trxName );

        if( C_PaySelectionCheck_ID == 0 ) {

            // setC_PaySelection_ID (0);
            // setC_BPartner_ID (0);
            // setPaymentRule (null);

            setPayAmt( Env.ZERO );
            setDiscountAmt( Env.ZERO );
            setIsPrinted( false );
            setIsReceipt( false );
            setQty( 0 );
        }
    }    // MPaySelectionCheck

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MPaySelectionCheck( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MPaySelectionCheck

    /**
     * Constructor de la clase ...
     *
     *
     * @param line
     * @param PaymentRule
     */

    public MPaySelectionCheck( MPaySelectionLine line,String PaymentRule ) {
        this( line.getCtx(),0,line.get_TrxName());
        setClientOrg( line );
        setC_PaySelection_ID( line.getC_PaySelection_ID());
        setC_BPartner_ID( line.getInvoice().getC_BPartner_ID());
        setPaymentRule( PaymentRule );

        //

        setIsReceipt( line.isSOTrx());
        setPayAmt( line.getPayAmt());
        setDiscountAmt( line.getDiscountAmt());
        setQty( 1 );
    }    // MPaySelectionCheck

    /**
     * Constructor de la clase ...
     *
     *
     * @param ps
     * @param PaymentRule
     */

    public MPaySelectionCheck( MPaySelection ps,String PaymentRule ) {
        this( ps.getCtx(),0,ps.get_TrxName());
        setClientOrg( ps );
        setC_PaySelection_ID( ps.getC_PaySelection_ID());
        setPaymentRule( PaymentRule );
    }    // MPaySelectionCheck

    /** Descripción de Campos */

    private MPaySelection m_parent = null;

    /** Descripción de Campos */

    private MPaySelectionLine[] m_lines = null;

    /**
     * Descripción de Método
     *
     *
     * @param line
     */

    public void addLine( MPaySelectionLine line ) {
        if( getC_BPartner_ID() != line.getInvoice().getC_BPartner_ID()) {
            throw new IllegalArgumentException( "Line for fifferent BPartner" );
        }

        //

        if( isReceipt() == line.isSOTrx()) {
            setPayAmt( getPayAmt().add( line.getPayAmt()));
            setDiscountAmt( getDiscountAmt().add( line.getDiscountAmt()));
        } else {
            setPayAmt( getPayAmt().subtract( line.getPayAmt()));
            setDiscountAmt( getDiscountAmt().subtract( line.getDiscountAmt()));
        }

        setQty( getQty() + 1 );
    }    // addLine

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MPaySelection getParent() {
        if( m_parent == null ) {
            m_parent = new MPaySelection( getCtx(),getC_PaySelection_ID(),get_TrxName());
        }

        return m_parent;
    }    // getParent

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MPaymentCheck[" );

        sb.append( getID()).append( "-" ).append( getDocumentNo()).append( "-" ).append( getPayAmt()).append( ",PaymetRule=" ).append( getPaymentRule()).append( ",Qty=" ).append( getQty()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public MPaySelectionLine[] getPaySelectionLines( boolean requery ) {
        if( (m_lines != null) &&!requery ) {
            return m_lines;
        }

        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM C_PaySelectionLine WHERE C_PaySelectionCheck_ID=? ORDER BY Line";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_PaySelectionCheck_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MPaySelectionLine( getCtx(),rs,get_TrxName()));
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

        //

        m_lines = new MPaySelectionLine[ list.size()];
        list.toArray( m_lines );

        return m_lines;
    }    // getPaySelectionLines
}    // MPaySelectionCheck



/*
 *  @(#)MPaySelectionCheck.java   02.07.07
 * 
 *  Fin del fichero MPaySelectionCheck.java
 *  
 *  Versión 2.2
 *
 */
