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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayUtil;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @version    2.1, 02.07.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MAllocationLine extends X_C_AllocationLine {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_AllocationLine_ID
     * @param trxName
     */

    public MAllocationLine( Properties ctx,int C_AllocationLine_ID,String trxName ) {
        super( ctx,C_AllocationLine_ID,trxName );

        if( C_AllocationLine_ID == 0 ) {

            // setC_AllocationHdr_ID (0);

            setAmount( Env.ZERO );
            setDiscountAmt( Env.ZERO );
            setWriteOffAmt( Env.ZERO );
            setOverUnderAmt( Env.ZERO );
        }
    }    // MAllocationLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MAllocationLine( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MAllocationLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param parent
     */

    public MAllocationLine( MAllocationHdr parent ) {
        this( parent.getCtx(),0,parent.get_TrxName());
        setClientOrg( parent );
        setC_AllocationHdr_ID( parent.getC_AllocationHdr_ID());
        m_parent = parent;
        set_TrxName( parent.get_TrxName());
    }    // MAllocationLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param parent
     * @param Amount
     * @param DiscountAmt
     * @param WriteOffAmt
     * @param OverUnderAmt
     */

    public MAllocationLine( MAllocationHdr parent,BigDecimal Amount,BigDecimal DiscountAmt,BigDecimal WriteOffAmt,BigDecimal OverUnderAmt ) {
        this( parent );
        setAmount( Amount );
        setDiscountAmt( (DiscountAmt == null)
                        ?Env.ZERO
                        :DiscountAmt );
        setWriteOffAmt( (WriteOffAmt == null)
                        ?Env.ZERO
                        :WriteOffAmt );
        setOverUnderAmt( (OverUnderAmt == null)
                         ?Env.ZERO
                         :OverUnderAmt );
    }    // MAllocationLine

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MAllocationLine.class );

    /** Descripción de Campos */

    private MInvoice m_invoice = null;

    /** Descripción de Campos */

    private MAllocationHdr m_parent = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MAllocationHdr getParent() {
        if( m_parent == null ) {
            m_parent = new MAllocationHdr( getCtx(),getC_AllocationLine_ID(),get_TrxName());
        }

        return m_parent;
    }    // getParent

    /**
     * Descripción de Método
     *
     *
     * @param parent
     */

    protected void setParent( MAllocationHdr parent ) {
        m_parent = parent;
    }    // setParent

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_Currency_ID() {
        return getParent().getC_Currency_ID();
    }    // getC_Currency_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Timestamp getDateTrx() {
        return getParent().getDateTrx();
    }    // getDateTrx

    /**
     * Descripción de Método
     *
     *
     * @param C_BPartner_ID
     * @param C_Order_ID
     * @param C_Invoice_ID
     */

    public void setDocInfo( int C_BPartner_ID,int C_Order_ID,int C_Invoice_ID ) {
        setC_BPartner_ID( C_BPartner_ID );
        setC_Order_ID( C_Order_ID );
        setC_Invoice_ID( C_Invoice_ID );
    }    // setDocInfo

    /**
     * Descripción de Método
     *
     *
     * @param C_Payment_ID
     * @param C_CashLine_ID
     */

    public void setPaymentInfo( int C_Payment_ID,int C_CashLine_ID ) {
        if( C_Payment_ID != 0 ) {
            setC_Payment_ID( C_Payment_ID );
        }

        if( C_CashLine_ID != 0 ) {
            setC_CashLine_ID( C_CashLine_ID );
        }
    }    // setPaymentInfo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MInvoice getInvoice() {
        if( (m_invoice == null) && (getC_Invoice_ID() != 0) ) {
            m_invoice = new MInvoice( getCtx(),getC_Invoice_ID(),get_TrxName());
        }

        return m_invoice;
    }    // getInvoice

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isCashTrx() {
        if( (getC_BPartner_ID() == 0) && (getC_Invoice_ID() == 0) && (getC_Order_ID() == 0)

        // && getC_CashLine_ID() == 0

        && (getC_Payment_ID() != 0) ) {
            MPayment payment = new MPayment( getCtx(),getC_Payment_ID(),get_TrxName());

            if( payment.isCashTrx()) {
                return true;
            }
        }

        return false;
    }    // isCashTrx

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {
        if( !newRecord && ( is_ValueChanged( "C_BPartner_ID" ) || is_ValueChanged( "C_Invoice_ID" ))) {
            log.severe( "Cannot Change Business Partner or Invoice" );

            return false;
        }

        // Set BPartner/Order from Invoice

        if( (getC_BPartner_ID() == 0) && (getInvoice() != null) ) {
            setC_BPartner_ID( getInvoice().getC_BPartner_ID());
        }

        if( (getC_Order_ID() == 0) && (getInvoice() != null) ) {
            setC_Order_ID( getInvoice().getC_Order_ID());
        }

        if(Util.isEmpty(getLine_Description(), true)){
        	// Armar la descripción de la línea con:
        	// Nro del allocation hdr
        	// Fecha del allocation hdr
        	// Monto de la línea
        	// Identificadores de la factura
        	// Identificadores del cobro involucrado
			MAllocationHdr allocHdr = new MAllocationHdr(getCtx(),
					getC_AllocationHdr_ID(), get_TrxName());
        	StringBuffer description = new StringBuffer(allocHdr.getDocumentNo());
        	description.append("_");
			description.append((new SimpleDateFormat("yyyy-MM-dd"))
					.format(allocHdr.getDateTrx()));
        	description.append("_");
        	description.append(getAmount());
        	if(!Util.isEmpty(getC_Invoice_ID(), true)){
        		description.append("_");
        		MInvoice invoice = new MInvoice(getCtx(), getC_Invoice_ID(), get_TrxName());
				description.append(DisplayUtil.getDisplayByIdentifiers(
						getCtx(), invoice, MInvoice.Table_ID, get_TrxName()));
        	}
        	if(!Util.isEmpty(getC_Payment_ID(), true)){
        		description.append("_");
        		MPayment payment = new MPayment(getCtx(), getC_Payment_ID(), get_TrxName());
				description.append(DisplayUtil.getDisplayByIdentifiers(
						getCtx(), payment, MPayment.Table_ID, get_TrxName()));
        	}
        	if(!Util.isEmpty(getC_CashLine_ID(), true)){
        		description.append("_");
        		MCashLine cashLine = new MCashLine(getCtx(), getC_CashLine_ID(), get_TrxName());
				description.append(DisplayUtil.getDisplayByIdentifiers(
						getCtx(), cashLine, MCashLine.Table_ID, get_TrxName()));
        	}
        	if(!Util.isEmpty(getC_Invoice_Credit_ID(), true)){
        		description.append("_");
        		MInvoice invoiceCredit = new MInvoice(getCtx(), getC_Invoice_Credit_ID(), get_TrxName());
				description.append(DisplayUtil.getDisplayByIdentifiers(
						getCtx(), invoiceCredit, MInvoice.Table_ID, get_TrxName()));
        	}
        	setLine_Description(description.toString());
        }
        
        //

        return true;
    }    // beforeSave

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected boolean beforeDelete() {
        setIsActive( false );

        return processIt( true );
    }    // beforeDelete

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MAllocationLine[" );

        sb.append( getID());

        if( getC_Payment_ID() != 0 ) {
            sb.append( ",C_Payment_ID=" ).append( getC_Payment_ID());
        }

        if( getC_CashLine_ID() != 0 ) {
            sb.append( ",C_CashLine_ID=" ).append( getC_CashLine_ID());
        }

        if( getC_Invoice_ID() != 0 ) {
            sb.append( ",C_Invoice_ID=" ).append( getC_Invoice_ID());
        }

        if( getC_BPartner_ID() != 0 ) {
            sb.append( ",C_BPartner_ID=" ).append( getC_BPartner_ID());
        }

        sb.append( ", Amount=" ).append( getAmount()).append( ",Discount=" ).append( getDiscountAmt()).append( ",WriteOff=" ).append( getWriteOffAmt()).append( ",OverUnder=" ).append( getOverUnderAmt());
        sb.append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param reverse
     *
     * @return
     */

    protected boolean processIt( boolean reverse ) {
        log.fine( "Reverse=" + reverse + " - " + toString());

        int      C_Invoice_ID = getC_Invoice_ID();
        MInvoice invoice      = getInvoice();

        //

        int C_Payment_ID  = getC_Payment_ID();
        int C_CashLine_ID = getC_CashLine_ID();

        // Update Payment

        if( C_Payment_ID != 0 ) {
            MPayment payment = new MPayment( getCtx(),C_Payment_ID,get_TrxName());

            if( reverse ) {
                if( !payment.isCashTrx()) {
                    payment.setIsAllocated( false );
                    payment.save();
                }
            } else {
                if( payment.testAllocation()) {
                    payment.save();
                }
            }
        }

        // Payment - Invoice

        if( (C_Payment_ID != 0) && (invoice != null) ) {

            // Link to Invoice

            if( reverse ) {
                invoice.setC_Payment_ID( 0 );
                log.fine( "C_Payment_ID=" + C_Payment_ID + " Unlinked from C_Invoice_ID=" + C_Invoice_ID );
            } else if( invoice.isPaid()) {
                invoice.setC_Payment_ID( C_Payment_ID );
                log.fine( "C_Payment_ID=" + C_Payment_ID + " Linked to C_Invoice_ID=" + C_Invoice_ID );
            }

            // Link to Order

            String update = "UPDATE C_Order o " + "SET C_Payment_ID=" + ( reverse
                    ?"NULL "
                    :"(SELECT C_Payment_ID FROM C_Invoice WHERE C_Invoice_ID=" + C_Invoice_ID + ") " ) + "WHERE EXISTS (SELECT * FROM C_Invoice i " + "WHERE o.C_Order_ID=i.C_Order_ID AND i.C_Invoice_ID=" + C_Invoice_ID + ")";

            if( DB.executeUpdate( update,get_TrxName()) > 0 ) {
                log.fine( "C_Payment_ID=" + C_Payment_ID + ( reverse
                        ?" UnLinked from"
                        :" Linked to" ) + " order of C_Invoice_ID=" + C_Invoice_ID );
            }
        }

        // Cash - Invoice

        if( (C_CashLine_ID != 0) && (invoice != null) ) {

            // Link to Invoice

            if( reverse ) {
                invoice.setC_CashLine_ID( 0 );
                log.fine( "C_CashLine_ID=" + C_CashLine_ID + " Unlinked from C_Invoice_ID=" + C_Invoice_ID );
            } else {
                invoice.setC_CashLine_ID( C_CashLine_ID );
                log.fine( "C_CashLine_ID=" + C_CashLine_ID + " Linked to C_Invoice_ID=" + C_Invoice_ID );
            }

            // Link to Order

            String update = "UPDATE C_Order o " + "SET C_CashLine_ID=" + ( reverse
                    ?"NULL "
                    :"(SELECT C_CashLine_ID FROM C_Invoice WHERE C_Invoice_ID=" + C_Invoice_ID + ") " ) + "WHERE EXISTS (SELECT * FROM C_Invoice i " + "WHERE o.C_Order_ID=i.C_Order_ID AND i.C_Invoice_ID=" + C_Invoice_ID + ")";

            if( DB.executeUpdate( update,get_TrxName()) > 0 ) {
                log.fine( "C_CashLine_ID=" + C_CashLine_ID + ( reverse
                        ?" UnLinked from"
                        :" Linked to" ) + " order of C_Invoice_ID=" + C_Invoice_ID );
            }
        }

        // Update Balance / Credit used - Counterpart of MInvoice.completeIt

        if( invoice != null ) {
            invoice.testAllocation();

            if( !invoice.save( get_TrxName())) {
                log.log( Level.SEVERE,"Invoice not updated - " + invoice );
            }
        }

        return true;
    }    // processIt

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_BPartner_ID
     * @param trxName
     */

    public static void setIsPaid( Properties ctx,int C_BPartner_ID,String trxName ) {
        int    counter = 0;
        s_log.fine("En Malocationline.setIsPadid con Properties = = "+ ctx +"c_Bpartner_id= "+C_BPartner_ID+"String trxName "+ trxName); 
        String sql     = "SELECT * FROM C_Invoice " + "WHERE IsPaid='N' AND DocStatus IN ('CO','CL')";

        if( C_BPartner_ID > 1 ) {
            sql += " AND C_BPartner_ID=?";
        } else {
            sql += " AND AD_Client_ID=" + Env.getAD_Client_ID( ctx );
        }
        s_log.fine("En Malocationline.setIsPadid con sql = "+ sql); 
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,trxName );

            if( C_BPartner_ID > 1 ) {
                pstmt.setInt( 1,C_BPartner_ID );
            }

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MInvoice invoice = new MInvoice( ctx,rs,trxName );

                if( invoice.testAllocation()) {
                    if( invoice.save()) {
                        counter++;
                    }
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"setIsPaid",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        s_log.fine( "setIsPaid - #" + counter );

        /*  */

    }    // setIsPaid

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_BPartner_ID
     * @param trxName
     */

    public static void setIsAllocated( Properties ctx,int C_BPartner_ID,String trxName ) {
        int    counter = 0;
        String sql     = "SELECT * FROM C_Payment " + "WHERE IsAllocated='N' AND DocStatus IN ('CO','CL')";

        if( C_BPartner_ID > 1 ) {
            sql += " AND C_BPartner_ID=?";
        } else {
            sql += " AND AD_Client_ID=" + Env.getAD_Client_ID( ctx );
        }

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,trxName );

            if( C_BPartner_ID > 1 ) {
                pstmt.setInt( 1,C_BPartner_ID );
            }

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MPayment pay = new MPayment( ctx,rs,trxName );

                if( pay.testAllocation()) {
                    if( pay.save()) {
                        counter++;
                    }
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"setIsAllocated",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        s_log.fine( "setIsAllocated - #" + counter );
    }    // setIsAllocated

	public void setPaymentInvoiceInfo(int debitDocumentId) {
		setC_Invoice_Credit_ID(debitDocumentId);
		
	}
}    // MAllocationLine



/*
 *  @(#)MAllocationLine.java   02.07.07
 * 
 *  Fin del fichero MAllocationLine.java
 *  
 *  Versión 2.1
 *
 */
