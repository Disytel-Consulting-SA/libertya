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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

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

public class MInvoiceTax extends X_C_InvoiceTax {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public static MInvoiceTax get(Properties ctx, Integer invoiceID, Integer taxID, String trxName){
		if(invoiceID == 0){
			return null;
		}
		String sql = "SELECT * FROM c_invoicetax WHERE c_invoice_id = ? AND c_tax_id = ?";
		MInvoiceTax invoiceTax = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, trxName);
			ps.setInt(1, invoiceID);
			ps.setInt(2, taxID);
			rs = ps.executeQuery();
			if(rs.next()){
				invoiceTax = new MInvoiceTax(ctx, rs, trxName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if(ps != null) ps = null;
				if(rs != null) rs = null;
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return invoiceTax;
	}
	
	/**
     * Descripción de Método
     *
     *
     * @param line
     * @param precision
     * @param oldTax
     * @param trxName
     *
     * @return
     */

    public static MInvoiceTax get( MInvoiceLine line,int precision,boolean oldTax,String trxName ) {
        MInvoiceTax retValue = null;

        if( (line == null) || (line.getC_Invoice_ID() == 0) ) {
            return null;
        }

        int C_Tax_ID = line.getC_Tax_ID();

        if( oldTax && line.is_ValueChanged( "C_Tax_ID" )) {
            Object old = line.get_ValueOld( "C_Tax_ID" );

            if( old == null ) {
                return null;
            }

            C_Tax_ID = (( Integer )old ).intValue();
        }

        if( C_Tax_ID == 0 ) {
            s_log.warning( "get C_Tax_ID=0" );

            return null;
        }

        String sql = "SELECT * FROM C_InvoiceTax WHERE C_Invoice_ID=? AND C_Tax_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,trxName );
            pstmt.setInt( 1,line.getC_Invoice_ID());
            pstmt.setInt( 2,C_Tax_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = new MInvoiceTax( line.getCtx(),rs,trxName );
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

        if( retValue != null ) {
            retValue.set_TrxName( trxName );
            retValue.setPrecision( precision );
            s_log.fine( "get (old=" + oldTax + ") " + retValue );

            return retValue;
        }

        // Create New

        retValue = new MInvoiceTax( line.getCtx(),0,trxName );
        retValue.set_TrxName( trxName );
        retValue.setClientOrg( line );
        retValue.setC_Invoice_ID( line.getC_Invoice_ID());
        retValue.setC_Tax_ID( line.getC_Tax_ID());
        retValue.setPrecision( precision );
        retValue.setIsTaxIncluded( line.isTaxIncluded());
        retValue.setIsPerceptionsIncluded( line.isPerceptionsIncluded());
        s_log.fine( "get (new) " + retValue );

        return retValue;
    }    // get
    
    
    public static List<MInvoiceTax> getTaxesFromInvoice(MInvoice invoice, boolean manualInvoiceTaxes) throws Exception{
    	List<MInvoiceTax> invoiceTaxes = new ArrayList<MInvoiceTax>();
    	StringBuffer sql = new StringBuffer("SELECT it.* FROM c_invoicetax it");
		sql.append(" INNER JOIN c_tax t ON t.c_tax_id = it.c_tax_id ");
		sql.append(" INNER JOIN c_taxcategory tc ON t.c_taxcategory_id = tc.c_taxcategory_id ");
    	sql.append(" WHERE it.c_invoice_id = ? ");
    	sql.append(" AND tc.ismanual = '"+(manualInvoiceTaxes?"Y":"N")+"' ");
    	PreparedStatement ps = DB.prepareStatement(sql.toString(), invoice.get_TrxName());
    	ps.setInt(1, invoice.getID());
    	ResultSet rs = ps.executeQuery();
    	while(rs.next()){
    		invoiceTaxes.add(new MInvoiceTax(invoice.getCtx(), rs, invoice.get_TrxName()));
    	}
    	rs.close();
    	ps.close();
    	return invoiceTaxes;
    }

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MInvoiceTax.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param ignored
     * @param trxName
     */

    public MInvoiceTax( Properties ctx,int ignored,String trxName ) {
        super( ctx,0,trxName );

        if( ignored != 0 ) {
            throw new IllegalArgumentException( "Multi-Key" );
        }

        setTaxAmt( Env.ZERO );
        setTaxBaseAmt( Env.ZERO );
        setIsTaxIncluded( false );
        setIsPerceptionsIncluded( false );
    }    // MInvoiceTax

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MInvoiceTax( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MInvoiceTax

    /** Descripción de Campos */

    private MTax m_tax = null;

    /** Descripción de Campos */

    private Integer m_precision = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private int getPrecision() {
        if( m_precision == null ) {
            return 2;
        }

        return m_precision.intValue();
    }    // getPrecision

    /**
     * Descripción de Método
     *
     *
     * @param precision
     */

    protected void setPrecision( int precision ) {
        m_precision = new Integer( precision );
    }    // setPrecision

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected MTax getTax() {
        if( m_tax == null ) {
            m_tax = MTax.get( getCtx(),getC_Tax_ID(),get_TrxName());
        }

        return m_tax;
    }    // getTax

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean calculateTaxFromLines() {
        BigDecimal taxBaseAmt = Env.ZERO;
        BigDecimal taxAmt     = Env.ZERO;

        //

        boolean documentLevel = getTax().isDocumentLevel();
        MTax    tax           = getTax();

        //
        // Obtengo la suma de taxamt de las lineas de la factura
        // FB - PROBLEMAS DE REDONDEOS
        // El método getTaxAmtFromLines() suma los TaxAmt de cada línea. Ese
		// TaxAmt tiene una presición de 2 decimales, es decir que al momento de
		// calcular y guardar la línea puede haber sufrido redondeos que hacen
		// que se pierdan algunos centavos. Este problema salta cuando el precio
		// de un artículo con impuesto resulta en 3 o mas decimales y sobre todo
		// cuando por ejemplo se separa una línea de cantidad 5 en líneas de
		// cantidad 1. En las líneas de cantidad 1 se recorta el tercer dígito
		// del TaxAmt, por lo tanto al sumar los 5 TaxAmt da un número mayor
		// (dado que el redondeo es HALF_UP) que al sumar los 5 Netos y luego
		// calcular el impuesto.
        //
		// Dado que este método se agregó para los casos en que es necesario
		// modificar el importe de impuesto para facturas de proveedores, 
        // se va a IGNORAR para el cálculo de impuestos de facturas
		// de ventas de modo que el impuesto no sufra redondeos por líneas y
		// SIEMPRE resulte en el mismo valor final, ya sea si la factura tiene
		// 10 líneas de cantidad 1 o una línea con cantidad 10 (siempre con el
		// mismo artículo)
        // >> OLD
        // BigDecimal taxAmtFromLines = getTaxAmtFromLines();
        // << NEW
        MInvoice invoice = MInvoice.get(getCtx(), getC_Invoice_ID(), get_TrxName());
        BigDecimal taxAmtFromLines = BigDecimal.ZERO;
        if (!invoice.isSOTrx()) {
        	taxAmtFromLines = getTaxAmtFromLines();
        }
        // >> END
        boolean isSetTaxAmt = taxAmtFromLines.compareTo(BigDecimal.ZERO) != 0; 
        
        String sql = "SELECT COALESCE(SUM("+getSqlInvoiceLineCalcForTaxBaseAmt()+"),0.0) FROM C_InvoiceLine WHERE C_Invoice_ID=? AND C_Tax_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_Invoice_ID());
            pstmt.setInt( 2,getC_Tax_ID());

            ResultSet rs = pstmt.executeQuery();

            if ( rs.next()) {
                //BigDecimal baseAmt = rs.getBigDecimal( 1 );
                //BigDecimal amt     = rs.getBigDecimal( 2 );
            	taxBaseAmt = rs.getBigDecimal( 1 );
                //

                if( !documentLevel ) {    // calculate line tax
                	if(!isSetTaxAmt){
                		taxAmt = tax.calculateTax( taxBaseAmt,isTaxIncluded(),isPerceptionsIncluded(),getPrecision());
                	}
                	else{
                		taxAmt = taxAmtFromLines;
                	}
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"setTaxBaseAmt",e );
            taxBaseAmt = null;
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        if( taxBaseAmt == null ) {
            return false;
        }

        // Calculate Tax

        if( documentLevel ) {
        	if(!isSetTaxAmt){
                taxAmt = tax.calculateTax( taxBaseAmt,isTaxIncluded(),getPrecision());
        	}
        	else{
        		taxAmt = taxAmtFromLines;
        	}
        }
        
        setTaxAmt( taxAmt );

        // Set Base

        if( (isTaxIncluded() && !isPerceptionsIncluded()) || (isTaxIncluded() && !invoice.isSOTrx()) ) {
        	setTaxBaseAmt( taxBaseAmt.subtract( taxAmt ));
        } else {
            setTaxBaseAmt( taxBaseAmt );
        }

        return true;
    }    // calculateTaxFromLines

    /**
	 * Obtiene el cálculo del importe base de impuestos aplicado a las líneas
	 * del pedido al realizar consultas SQL
	 * 
	 * @return
	 */
    public String getSqlInvoiceLineCalcForTaxBaseAmt(){
    	return (isPerceptionsIncluded() ? "LineNetAmount" : "LineNetAmt")+"-DocumentDiscountAmt";
    }
    
    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MInvoiceTax[" );

        sb.append( "C_Invoice_ID=" ).append( getC_Invoice_ID()).append( ",C_Tax_ID=" ).append( getC_Tax_ID()).append( ", Base=" ).append( getTaxBaseAmt()).append( ",Tax=" ).append( getTaxAmt()).append( "]" );

        return sb.toString();
    }    // toString
    
    public boolean save(){
    	boolean retValue = super.save();
    	if (!retValue)
    	{
    		log.saveError("Error", CLogger.retrieveErrorAsString());
     		return false;
    	}
    	if (this.getTax().isCategoriaManual())
    	{
    		MInvoice factura = new MInvoice(getCtx(), getC_Invoice_ID(), get_TrxName());
    		factura.setSkipExtraValidations(true);
    		factura.setSkipModelValidations(true);
    		factura.calculateTotal();
    		retValue = factura.save();
    		if(!retValue){
    			log.saveError("Error", CLogger.retrieveErrorAsString());
         		return false;
    		}
    	}
    	return retValue;
    }  // save
    
    
    public boolean delete (boolean force) {  
    	int id = getC_Invoice_ID();
    	boolean retValue = super.delete(force);
   		MInvoice factura = new MInvoice(getCtx(),id , get_TrxName());
   		factura.calculateTotal();
    	if(!factura.save()){
    		log.saveError("Error", CLogger.retrieveErrorAsString());
    		return false;
    	}    	
    	return retValue;
    } //delete
    
    
    private BigDecimal getTaxAmtFromLines(){
    	String sql = "SELECT COALESCE(SUM(taxAmt),0.0) FROM C_InvoiceLine WHERE C_Invoice_ID=? AND C_Tax_ID=?";
        PreparedStatement pstmt = null;
        BigDecimal taxAmt = BigDecimal.ZERO; 
        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_Invoice_ID());
            pstmt.setInt( 2,getC_Tax_ID());

            ResultSet rs = pstmt.executeQuery();

            if ( rs.next()) {
            	taxAmt = 	rs.getBigDecimal( 1 );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getTaxAmtFromLines",e );
        }
        return taxAmt;
    }
}    // MInvoiceTax



/*
 *  @(#)MInvoiceTax.java   02.07.07
 * 
 *  Fin del fichero MInvoiceTax.java
 *  
 *  Versión 2.2
 *
 */

