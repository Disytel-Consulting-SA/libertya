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
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.process.DocAction;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MMatchPO extends X_M_MatchPO {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_OrderLine_ID
     * @param C_InvoiceLine_ID
     * @param trxName
     *
     * @return
     */

    public static MMatchPO[] get( Properties ctx,int C_OrderLine_ID,int C_InvoiceLine_ID,String trxName ) {
        if( (C_OrderLine_ID == 0) || (C_InvoiceLine_ID == 0) ) {
            return new MMatchPO[]{};
        }

        //

        String sql = "SELECT * FROM M_MatchPO WHERE C_OrderLine_ID=? AND C_InvoiceLine_ID=?";
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,trxName );
            pstmt.setInt( 1,C_OrderLine_ID );
            pstmt.setInt( 2,C_InvoiceLine_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MMatchPO( ctx,rs,trxName ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"get",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        MMatchPO[] retValue = new MMatchPO[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // get

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MMatchPO.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_MatchPO_ID
     * @param trxName
     */

    public MMatchPO( Properties ctx,int M_MatchPO_ID,String trxName ) {
        super( ctx,M_MatchPO_ID,trxName );

        if( M_MatchPO_ID == 0 ) {

            // setC_OrderLine_ID (0);
            // setDateTrx (new Timestamp(System.currentTimeMillis()));
            // setM_InOutLine_ID (0);
            // setM_Product_ID (0);
            // setQty (Env.ZERO);

            setPosted( false );
            setProcessed( false );
            setProcessing( false );
        }
    }    // MMatchPO

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MMatchPO( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MMatchPO

    /**
     * Constructor de la clase ...
     *
     *
     * @param sLine
     * @param dateTrx
     * @param qty
     */

    public MMatchPO( MInOutLine sLine,Timestamp dateTrx,BigDecimal qty ) {
        this( sLine.getCtx(),0,sLine.get_TrxName());
        setClientOrg( sLine );
        setM_InOutLine_ID( sLine.getM_InOutLine_ID());
        setC_OrderLine_ID( sLine.getC_OrderLine_ID());

        if( dateTrx != null ) {
            setDateTrx( dateTrx );
        }
        setC_Project_ID(sLine.getC_Project_ID());
        setM_Product_ID( sLine.getM_Product_ID());
        setQty( qty );
        setProcessed( true );    // auto
    }                            // MMatchPO

    /**
     * Constructor de la clase ...
     *
     *
     * @param iLine
     * @param dateTrx
     * @param qty
     */

    public MMatchPO( MInvoiceLine iLine,Timestamp dateTrx,BigDecimal qty ) {
        this( iLine.getCtx(),0,iLine.get_TrxName());
        setClientOrg( iLine );
        setC_InvoiceLine_ID( iLine.getC_InvoiceLine_ID());

        if( iLine.getC_OrderLine_ID() != 0 ) {
            setC_OrderLine_ID( iLine.getC_OrderLine_ID());
        }

        if( dateTrx != null ) {
            setDateTrx( dateTrx );
        }
        setC_Project_ID(iLine.getC_Project_ID());
        setM_Product_ID( iLine.getM_Product_ID());
        setQty( qty );
        setProcessed( true );    // auto
    }                            // MMatchPO

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {

        // Set Trx Date

        if( getDateTrx() == null ) {
            setDateTrx( new Timestamp( System.currentTimeMillis()));
        }

        // Set Acct Date

        if( getDateAcct() == null ) {
            Timestamp ts = getNewerDateAcct();

            if( ts == null ) {
                ts = getDateTrx();
            }

            setDateAcct( ts );
        }

        return true;
    }    // beforeSave

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     * @param success
     *
     * @return
     */

    protected boolean afterSave( boolean newRecord,boolean success ) {
        if( success && (getC_OrderLine_ID() != 0) ) {
            MOrderLine orderLine = new MOrderLine( getCtx(),getC_OrderLine_ID(),get_TrxName());
            MOrder order = new MOrder( getCtx(),orderLine.getC_Order_ID(),get_TrxName());
            if( getM_InOutLine_ID() != 0 ) {
                orderLine.setQtyDelivered( orderLine.getQtyDelivered().add( getQtyMovement()));
                orderLine.setQtyReserved( orderLine.getQtyReserved().subtract( getQtyMovement()));
            }

            if( getC_InvoiceLine_ID() != 0 ) {
            	// Verificar el tipo de documento base del tipo de documento de la factura
            	MInvoiceLine line = new MInvoiceLine(getCtx(), getC_InvoiceLine_ID(), get_TrxName()); 
            	MInvoice invoice = new MInvoice(getCtx(), line.getC_Invoice_ID(), get_TrxName());
        		MDocType docType = new MDocType(getCtx(), invoice.getC_DocTypeTarget_ID(), get_TrxName());
				boolean isDebit = !docType.getDocBaseType().equals(
						MDocType.DOCBASETYPE_APCreditMemo);
				if(isDebit){
					orderLine.setQtyInvoiced( orderLine.getQtyInvoiced().add( getQty()));
				}
				else{
					// Actualizar la mercadería entregada por el proveedor con
					// la cantidad ésta ya que estamos bajo un crédito que
					// tenemos con el proveedor por un pedido realizado
					// anteriormente, esto significa que la cantiada entregada
					// se incrementa por el solo hecho de que si nos hizo tal
					// crédito es porque no ingresa la mercadería que debería
					// ingresar
					orderLine.setQtyDelivered( orderLine.getQtyDelivered().add( getQty() ));
					orderLine.setQtyReserved( orderLine.getQtyReserved().subtract( getQty() ));
					// Actualizar el pendiente de entrega del stock ya que esa
					// mercadería no va a entrar
					// Actualizar el stock y el pendiente de entrega para ese producto
					success = success
							&& MStorage.add(getCtx(), order
									.getM_Warehouse_ID(),
									MStorage.getM_Locator_ID(order
											.getM_Warehouse_ID(), line
											.getM_Product_ID(), line
											.getM_AttributeSetInstance_ID(),
											line.getQtyInvoiced(),
											get_TrxName()), line
											.getM_Product_ID(), line
											.getM_AttributeSetInstance_ID(),
									line.getM_AttributeSetInstance_ID(),
									BigDecimal.ZERO, BigDecimal.ZERO, line
											.getQtyInvoiced().negate(),
									get_TrxName());
				}
            }

            return orderLine.save( get_TrxName());
        }

        return success;
    }    // afterSave

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private Timestamp getNewerDateAcct() {
        Timestamp orderDate   = null;
        Timestamp invoiceDate = null;
        Timestamp shipDate    = null;
        String    sql         = "SELECT i.DateAcct " + "FROM C_InvoiceLine il" + " INNER JOIN C_Invoice i ON (i.C_Invoice_ID=il.C_Invoice_ID) " + "WHERE C_InvoiceLine_ID=?";
        PreparedStatement pstmt = null;

        if( getC_InvoiceLine_ID() != 0 ) {
            try {
                pstmt = DB.prepareStatement( sql );
                pstmt.setInt( 1,getC_InvoiceLine_ID());

                ResultSet rs = pstmt.executeQuery();

                if( rs.next()) {
                    invoiceDate = rs.getTimestamp( 1 );
                }

                rs.close();
                pstmt.close();
                pstmt = null;
            } catch( Exception e ) {
                log.log( Level.SEVERE,sql,e );
            }
        }

        //

        sql = "SELECT io.DateAcct " + "FROM M_InOutLine iol" + " INNER JOIN M_InOut io ON (io.M_InOut_ID=iol.M_InOut_ID) " + "WHERE iol.M_InOutLine_ID=?";

        if( getM_InOutLine_ID() != 0 ) {
            try {
                pstmt = DB.prepareStatement( sql );
                pstmt.setInt( 1,getM_InOutLine_ID());

                ResultSet rs = pstmt.executeQuery();

                if( rs.next()) {
                    shipDate = rs.getTimestamp( 1 );
                }

                rs.close();
                pstmt.close();
                pstmt = null;
            } catch( Exception e ) {
                log.log( Level.SEVERE,sql,e );
            }
        }

        //

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        // Assuming that order date is always earlier

        if( invoiceDate == null ) {
            return shipDate;
        }

        if( shipDate == null ) {
            return invoiceDate;
        }

        if( invoiceDate.after( shipDate )) {
            return invoiceDate;
        }

        return shipDate;
    }    // getNewerDateAcct

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected boolean beforeDelete() {
        if( isPosted()) {
            if( !MPeriod.isOpen( getCtx(),getDateTrx(),MDocType.DOCBASETYPE_MatchPO )) {
                return false;
            }

            setPosted( false );

            return MFactAcct.delete( Table_ID,getID(),get_TrxName()) >= 0;
        }

        return true;
    }    // beforeDelete

    /**
     * Descripción de Método
     *
     *
     * @param success
     *
     * @return
     */

    protected boolean afterDelete( boolean success ) {

        // Order Delivered/Invoiced
        // (Reserved in VMatch and MInOut.completeIt)

        if( success && (getC_OrderLine_ID() != 0) ) {
            MOrderLine orderLine = new MOrderLine( getCtx(),getC_OrderLine_ID(),get_TrxName());

            if( getM_InOutLine_ID() != 0 ) {
                orderLine.setQtyDelivered( orderLine.getQtyDelivered().subtract( getQtyMovement()));
            }

            if( getC_InvoiceLine_ID() != 0 ) {
                orderLine.setQtyInvoiced( orderLine.getQtyInvoiced().subtract( getQty()));
            }

            return orderLine.save( get_TrxName());
        }

        return success;
    }    // afterDelete
    
    /**
     * @return Devuelve la cantidad realmente movida en la línea de remito asociada a este match.
     * Esto es, si salió mercadería (Devolución a Proveedor) la cantidad que devuelve es negativa,
     * mientras que si entró mercadería (Remito de Entrada) la cantidad es positiva.
     */
    private BigDecimal getQtyMovement() {
    	BigDecimal qty = getQty();
    	if (getM_InOutLine_ID() > 0) {
    		Integer inOutID = DB.getSQLValue(get_TrxName(), "SELECT M_InOut_ID FROM M_InOutLine WHERE M_InOutLine_ID = ?", getM_InOutLine_ID());
    		if (inOutID > 0) {
    			MInOut inOut = new MInOut(getCtx(), inOutID, get_TrxName());
    			// Salida de mercadería
    			if (inOut.getMovementType().endsWith("-")) {
    				qty = qty.negate();
    			}
    		}
    	}
    	return qty;
    }
}    // MMatchPO



/*
 *  @(#)MMatchPO.java   02.07.07
 * 
 *  Fin del fichero MMatchPO.java
 *  
 *  Versión 2.2
 *
 */
