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



package org.openXpertya.grid;

import java.beans.PropertyChangeEvent;
import java.beans.VetoableChangeListener;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.openXpertya.apps.ADialog;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.model.MBankStatement;
import org.openXpertya.model.MBankStatementLine;
import org.openXpertya.model.MLookup;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.MTab;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VCreateFromStatement extends VCreateFrom implements VetoableChangeListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param mTab
     */

    protected VCreateFromStatement( MTab mTab ) {
        super( mTab );
        log.info( "" );
    }    // VCreateFromStatement

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    protected boolean dynInit() throws Exception {
        if( p_mTab.getValue( "C_BankStatement_ID" ) == null ) {
            ADialog.error( 0,this,"SaveErrorRowNotFound" );

            return false;
        }

        setTitle( Msg.translate( Env.getCtx(),"C_BankStatement_ID" ) + " .. " + Msg.translate( Env.getCtx(),"CreateFrom" ));
        parameterStdPanel.setVisible( false );

        int     AD_Column_ID = 4917;    // C_BankStatement.C_BankAccount_ID
        MLookup lookup       = MLookupFactory.get( Env.getCtx(),p_WindowNo,0,AD_Column_ID,DisplayType.TableDir );

        bankAccountField = new VLookup( "C_BankAccount_ID",true,true,true,lookup );
        bankAccountField.addVetoableChangeListener( this );

        // Set Default

        int C_BankAccount_ID = Env.getContextAsInt( Env.getCtx(),p_WindowNo,"C_BankAccount_ID" );

        bankAccountField.setValue( new Integer( C_BankAccount_ID ));

        // initial Loading

        loadBankAccount( C_BankAccount_ID );

        return true;
    }    // dynInit

    /**
     * Descripción de Método
     *
     *
     * @param C_BPartner_ID
     */

    protected void initBPDetails( int C_BPartner_ID ) {}    // initDetails

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void vetoableChange( PropertyChangeEvent e ) {
        log.config( e.getPropertyName() + "=" + e.getNewValue());

        // BankAccount

        if( e.getPropertyName() == "C_BankAccount_ID" ) {
            int C_BankAccount_ID = (( Integer )e.getNewValue()).intValue();

            loadBankAccount( C_BankAccount_ID );
        }

        tableChanged( null );
    }    // vetoableChange

    /**
     * Descripción de Método
     *
     *
     * @param C_BankAccount_ID
     */

    private void loadBankAccount( int C_BankAccount_ID ) {
        log.config( "C_BankAccount_ID=" + C_BankAccount_ID );

        List<Payment> data = new ArrayList<Payment>();
        
        StringBuffer sql = new StringBuffer(); 
        sql.append("SELECT ")
           .append(   "p.DateTrx, ")
           .append(   "p.C_Payment_ID, ")
           .append(	  "p.DocumentNo, ")
           .append(   "p.C_Currency_ID, ")
           .append(   "c.ISO_Code, ")
           .append(   "p.PayAmt, ")
           .append(   "currencyConvert(p.PayAmt,p.C_Currency_ID,ba.C_Currency_ID,?,null,p.AD_Client_ID,p.AD_Org_ID) AS ConvertedAmt, ")
           .append(   "bp.Name AS BPartnerName ")
           
           .append("FROM C_BankAccount ba ")
           .append("INNER JOIN C_Payment_v p ON (p.C_BankAccount_ID=ba.C_BankAccount_ID) ")
           .append("INNER JOIN C_Currency c ON (p.C_Currency_ID=c.C_Currency_ID) ")
           .append("INNER JOIN C_BPartner bp ON (p.C_BPartner_ID=bp.C_BPartner_ID) ")
        		   
           .append("WHERE p.Processed='Y' ")
           .append(  "AND p.IsReconciled='N' ")
           .append(  "AND p.DocStatus IN ('CO','CL','RE') ")
           .append(  "AND p.PayAmt<>0 ")
           .append(  "AND p.C_BankAccount_ID=? ")
           .append(  "AND NOT EXISTS ")
           .append(      "(SELECT * FROM C_BankStatementLine l ")
           // Voided Bank Statements have 0 StmtAmt
           .append(       "WHERE p.C_Payment_ID=l.C_Payment_ID AND l.StmtAmt <> 0)");

        // Get StatementDate

        Timestamp ts = ( Timestamp )p_mTab.getValue( "StatementDate" );

        if( ts == null ) {
            ts = new Timestamp( System.currentTimeMillis());
        }
    	PreparedStatement pstmt = null;
    	ResultSet rs 			= null;

        try {
            pstmt = DB.prepareStatement( sql.toString());
            pstmt.setTimestamp( 1,ts );
            pstmt.setInt( 2,C_BankAccount_ID );
            rs = pstmt.executeQuery();

            while( rs.next()) {
                Payment payment = new Payment();
                
                payment.selected = false;
                payment.paymentID = rs.getInt("C_Payment_ID");
                payment.dateTrx = rs.getTimestamp("DateTrx");
                payment.documentNo = rs.getString("DocumentNo");
                payment.currencyID = rs.getInt("C_Currency_ID");
                payment.currencyISO = rs.getString("ISO_Code");
                payment.payAmt = rs.getBigDecimal("PayAmt");
                payment.convertedAmt = rs.getBigDecimal("ConvertedAmt");
                payment.bPartnerName = rs.getString("BPartnerName");                
                
                data.add(payment);
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql.toString(),e );
    	} finally {
    		try {
	    		if (rs != null) rs.close();
	    		if (pstmt != null) pstmt.close();
    		}	catch (Exception e) {}
    	}

    	loadTable(data);
    }    // loadBankAccount

    /**
     * Descripción de Método
     *
     */

    protected void info() {
        DecimalFormat format = DisplayType.getNumberFormat( DisplayType.Amount );
        BigDecimal total = new BigDecimal( 0.0 );
        int        count = 0;

        for(SourceEntity sourceEntity : getSelectedSourceEntities()) {
            total = total.add(((Payment)sourceEntity).payAmt);
            count++;
        }

        statusBar.setStatusLine( String.valueOf( count ) + " - " + Msg.getMsg( Env.getCtx(),"Sum" ) + "  " + format.format( total ));
    }    // infoStatement

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected void save() throws CreateFromSaveException {
        log.config( "" );

        // fixed values

        int C_BankStatement_ID = (( Integer )p_mTab.getValue( "C_BankStatement_ID" )).intValue();
        MBankStatement bs = new MBankStatement( Env.getCtx(),C_BankStatement_ID, getTrxName());

        log.config( bs.toString());

        // Lines
        for (SourceEntity sourceEntity : getSelectedSourceEntities()) {
        	Payment payment = (Payment)sourceEntity;	
            Timestamp trxDate = payment.dateTrx;
            int C_Payment_ID = payment.paymentID;
            int C_Currency_ID = payment.currencyID;
            BigDecimal TrxAmt  = payment.payAmt;
            BigDecimal StmtAmt = payment.convertedAmt;

            //

            log.fine( "Line Date=" + trxDate + ", Payment=" + C_Payment_ID + ", Currency=" + C_Currency_ID + ", Amt=" + TrxAmt );

            //
            MPayment pay = new MPayment( Env.getCtx(),C_Payment_ID, getTrxName());
            MBankStatementLine bsl = new MBankStatementLine( bs );

            bsl.setStatementLineDate( trxDate );
            bsl.setPayment(pay);
            
            if( !bsl.save()) {
                throw new CreateFromSaveException(
             		   "@StatementLineSaveError@ (@C_Paymenty_ID@ # " + payment.documentNo + "):<br>" + 
             		   CLogger.retrieveErrorAsString()
             	);

            }
        }        // for all rows
    }    // save
    
	@Override
	protected CreateFromTableModel createTableModelInstance() {
		return new PaymentTableModel();
	}

	/**
     * Entidad Oríden: Pagos
     */
    protected class Payment extends SourceEntity {
    	/** ID del pago */
    	protected int paymentID = 0;
    	/** Nro de Documento del pago */
    	protected String documentNo = null;
    	/** Fecha de transacción */
    	protected Timestamp dateTrx = null;
        /** Nombre o descripción de la EC asociada al pago */
    	protected String bPartnerName = null;
    	/** ID de la moneda en la que se encuentra expresada el monto
    	 * del pago */
    	protected int currencyID = 0;
    	/** Código ISO de la moneda en la que se encuentra expresada el monto
    	 * del pago */
    	protected String currencyISO = null;
        /** Importe del pago expresado en la moneda currencyID */
    	protected BigDecimal payAmt = BigDecimal.ZERO;
        /** Importe del pago convertido a la moneda de la cuenta bancaria a la que pertence*/
    	protected BigDecimal convertedAmt = BigDecimal.ZERO;
    
    }
    
    /**
     * Modelo de tabla para presentación de los pagos de una cuenta bancaria.
     */
    protected class PaymentTableModel extends CreateFromTableModel {

		// Constantes de índices de las columnas en la grilla.
    	public static final int COL_IDX_DATETRX    = 1;
    	public static final int COL_IDX_DOCUMENTNO = 2;
    	public static final int COL_IDX_BPARTNER   = 3;
    	public static final int COL_IDX_CURRENCY   = 4;
    	public static final int COL_IDX_AMT        = 5;
    	public static final int COL_IDX_CONVAMT    = 6;
    	    	
		@Override
		protected void setColumnClasses() {
	        setColumnClass(COL_IDX_DATETRX, Timestamp.class);
	        setColumnClass(COL_IDX_DOCUMENTNO, String.class);
	        setColumnClass(COL_IDX_BPARTNER, String.class);
	        setColumnClass(COL_IDX_CURRENCY, String.class);
	        setColumnClass(COL_IDX_AMT, BigDecimal.class);
	        setColumnClass(COL_IDX_CONVAMT, BigDecimal.class);
		}

		@Override
		protected void setColumnNames() {
	        setColumnName(COL_IDX_DATETRX, Msg.translate( Env.getCtx(),"Date" ));
	        setColumnName(COL_IDX_DOCUMENTNO, Msg.getElement( Env.getCtx(),"C_Payment_ID" ));
	        setColumnName(COL_IDX_BPARTNER, Msg.translate( Env.getCtx(),"C_BPartner_ID" ));
	        setColumnName(COL_IDX_CURRENCY, Msg.translate( Env.getCtx(),"C_Currency_ID" ));
	        setColumnName(COL_IDX_AMT, Msg.translate( Env.getCtx(),"Amount" ));
	        setColumnName(COL_IDX_CONVAMT, Msg.translate( Env.getCtx(),"ConvertedAmount" ));
		}

		@Override
		public Object getValueAt(int rowIndex, int colIndex) {
			Payment payment = getPayment(rowIndex);
			Object value = null;
			switch (colIndex) {
				case COL_IDX_DATETRX:
					value = payment.dateTrx; break;
				case COL_IDX_DOCUMENTNO:
					value = payment.documentNo; break;
				case COL_IDX_BPARTNER:
					value = payment.bPartnerName; break;
				case COL_IDX_CURRENCY:
					value = payment.currencyISO; break;
				case COL_IDX_AMT:
					value = payment.payAmt; break;
				case COL_IDX_CONVAMT:
					value = payment.convertedAmt; break;
				default:
					value = super.getValueAt(rowIndex, colIndex); break;
			}
			return value;
		}

		/**
		 * Devuelve el pago ubicado en una determinada fila
		 * @param rowIndex Índice de la fila
		 * @return {@link Payment}
		 */
		public Payment getPayment(int rowIndex) {
			return (Payment)getSourceEntity(rowIndex);
		}
    }
}    // VCreateFromStatement



/*
 *  @(#)VCreateFromStatement.java   02.07.07
 * 
 *  Fin del fichero VCreateFromStatement.java
 *  
 *  Versión 2.2
 *
 */
