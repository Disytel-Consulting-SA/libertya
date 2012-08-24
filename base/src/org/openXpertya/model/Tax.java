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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogMgt;
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

public class Tax {

    /** Descripción de Campos */

    static private CLogger log = CLogger.getCLogger( Tax.class );
    

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_Product_ID
     * @param C_Charge_ID
     * @param billDate
     * @param shipDate
     * @param AD_Org_ID
     * @param M_Warehouse_ID
     * @param billC_BPartner_Location_ID
     * @param shipC_BPartner_Location_ID
     * @param IsSOTrx
     * @return
     */

    public static int get( Properties ctx,int M_Product_ID,int C_Charge_ID,Timestamp billDate,Timestamp shipDate,int AD_Org_ID,int M_Warehouse_ID,int billC_BPartner_Location_ID,int shipC_BPartner_Location_ID,boolean IsSOTrx) {
        if( M_Product_ID != 0 ) {
            return getProduct( ctx,M_Product_ID,billDate,shipDate,AD_Org_ID,M_Warehouse_ID,billC_BPartner_Location_ID,shipC_BPartner_Location_ID,IsSOTrx);
        } else if( C_Charge_ID != 0 ) {
            return getCharge( ctx,C_Charge_ID,billDate,shipDate,AD_Org_ID,M_Warehouse_ID,billC_BPartner_Location_ID,shipC_BPartner_Location_ID,IsSOTrx );
        } else {
            return getExemptTax( ctx,AD_Org_ID );
        }
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_Charge_ID
     * @param billDate
     * @param shipDate
     * @param AD_Org_ID
     * @param M_Warehouse_ID
     * @param billC_BPartner_Location_ID
     * @param shipC_BPartner_Location_ID
     * @param IsSOTrx
     *
     * @return
     */

    public static int getCharge( Properties ctx,int C_Charge_ID,Timestamp billDate,Timestamp shipDate,int AD_Org_ID,int M_Warehouse_ID,int billC_BPartner_Location_ID,int shipC_BPartner_Location_ID,boolean IsSOTrx) {
        String variable              = "";
        int    C_TaxCategory_ID      = 0;
        int    shipFromC_Location_ID = 0;
        int    shipToC_Location_ID   = 0;
        int    billFromC_Location_ID = 0;
        int    billToC_Location_ID   = 0;
        int	   C_BPartner_ID		 = 0;
        String IsTaxExempt           = null;

        try {

            // Get all at once

            String sql = "SELECT c.C_TaxCategory_ID, o.C_Location_ID, il.C_Location_ID, b.IsTaxExempt,"
            			+ " w.C_Location_ID, sl.C_Location_ID " 
            			+ ", b.C_BPartner_ID " 
            			+ "FROM C_Charge c, AD_OrgInfo o," 
            			+ " C_BPartner_Location il INNER JOIN C_BPartner b ON (il.C_BPartner_ID=b.C_BPartner_ID)," 
            			+ " M_Warehouse w, C_BPartner_Location sl " 
            			+ "WHERE c.C_Charge_ID=?" 
            			+ " AND o.AD_Org_ID=?" 
            			+ " AND il.C_BPartner_Location_ID=?" 
            			+ " AND w.M_Warehouse_ID=?"
            			+ " AND sl.C_BPartner_Location_ID=?";
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,C_Charge_ID );
            pstmt.setInt( 2,AD_Org_ID );
            pstmt.setInt( 3,billC_BPartner_Location_ID );
            pstmt.setInt( 4,M_Warehouse_ID );
            pstmt.setInt( 5,shipC_BPartner_Location_ID );

            ResultSet rs    = pstmt.executeQuery();
            boolean   found = false;

            if( rs.next()) {
                C_TaxCategory_ID      = rs.getInt( 1 );
                billFromC_Location_ID = rs.getInt( 2 );
                billToC_Location_ID   = rs.getInt( 3 );
                IsTaxExempt           = rs.getString( 4 );
                shipFromC_Location_ID = rs.getInt( 5 );
                shipToC_Location_ID   = rs.getInt( 6 );
                C_BPartner_ID         = rs.getInt("C_BPartner_ID");
                found                 = true;
            }

            rs.close();
            pstmt.close();

            //

            if( !found ) {
                log.log( Level.SEVERE,"getCharge - Not found - C_Charge_ID=" + C_Charge_ID );

                return 0;
            } else if( "Y".equals( IsTaxExempt )) {
                return getExemptTax( ctx,AD_Org_ID );
            }
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getCharge",e );

            return 0;
        }

        // Reverese for PO

        if( !IsSOTrx ) {
            int temp = billFromC_Location_ID;

            billFromC_Location_ID = billToC_Location_ID;
            billToC_Location_ID   = temp;
            temp                  = shipFromC_Location_ID;
            shipFromC_Location_ID = shipToC_Location_ID;
            shipToC_Location_ID   = temp;
        }

        //

        log.fine( "getCharge - C_TaxCategory_ID=" + C_TaxCategory_ID + ", billFromC_Location_ID=" + billFromC_Location_ID + ", billToC_Location_ID=" + billToC_Location_ID + ", shipFromC_Location_ID=" + shipFromC_Location_ID + ", shipToC_Location_ID=" + shipToC_Location_ID );

        return get( ctx,C_TaxCategory_ID,IsSOTrx,shipDate,shipFromC_Location_ID,shipToC_Location_ID,billDate,billFromC_Location_ID,billToC_Location_ID, C_BPartner_ID );
    }    // getCharge

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_Product_ID
     * @param billDate
     * @param shipDate
     * @param AD_Org_ID
     * @param M_Warehouse_ID
     * @param billC_BPartner_Location_ID
     * @param shipC_BPartner_Location_ID
     * @param IsSOTrx
     *
     * @return
     */

    public static int getProduct( Properties ctx,int M_Product_ID,Timestamp billDate,Timestamp shipDate,int AD_Org_ID,int M_Warehouse_ID,int billC_BPartner_Location_ID,int shipC_BPartner_Location_ID,boolean IsSOTrx) {
        String variable              = "";
        int    C_TaxCategory_ID      = 0;
        int    shipFromC_Location_ID = 0;
        int    shipToC_Location_ID   = 0;
        int    billFromC_Location_ID = 0;
        int    billToC_Location_ID   = 0;
        int    C_BPartner_ID		 = 0;
        String IsTaxExempt           = null;

        try {

            // Get all at once

            String sql = "SELECT p.C_TaxCategory_ID, o.C_Location_ID, il.C_Location_ID, b.IsTaxExempt,"
            		+ " w.C_Location_ID, sl.C_Location_ID "
            		+ ", b.C_BPartner_ID "
            		+ "FROM M_Product p, AD_OrgInfo o," 
            		+ " C_BPartner_Location il INNER JOIN C_BPartner b ON (il.C_BPartner_ID=b.C_BPartner_ID)," 
            		+ " M_Warehouse w, C_BPartner_Location sl " 
            		+ "WHERE p.M_Product_ID=?" 
            		+ " AND o.AD_Org_ID=?" 
            		+ " AND il.C_BPartner_Location_ID=?" 
            		+ " AND w.M_Warehouse_ID=?" 
            		+ " AND sl.C_BPartner_Location_ID=?";
            
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,M_Product_ID );
            pstmt.setInt( 2,AD_Org_ID );
            pstmt.setInt( 3,billC_BPartner_Location_ID );
            pstmt.setInt( 4,M_Warehouse_ID );
            pstmt.setInt( 5,shipC_BPartner_Location_ID );

            ResultSet rs    = pstmt.executeQuery();
            boolean   found = false;

            if( rs.next()) {
                C_TaxCategory_ID      = rs.getInt( 1 );
                billFromC_Location_ID = rs.getInt( 2 );
                billToC_Location_ID   = rs.getInt( 3 );
                IsTaxExempt           = rs.getString( 4 );
                shipFromC_Location_ID = rs.getInt( 5 );
                shipToC_Location_ID   = rs.getInt( 6 );
                C_BPartner_ID 		  = rs.getInt("C_BPartner_ID");
                found                 = true;
            }

            rs.close();
            pstmt.close();

            //

            if( found && "Y".equals( IsTaxExempt )) {
                log.fine( "getProduct - Business Partner is Tax exempt" );

                return getExemptTax( ctx,AD_Org_ID );
            } else if( found ) {
                if( !IsSOTrx ) {
                    int temp = billFromC_Location_ID;

                    billFromC_Location_ID = billToC_Location_ID;
                    billToC_Location_ID   = temp;
                    temp                  = shipFromC_Location_ID;
                    shipFromC_Location_ID = shipToC_Location_ID;
                    shipToC_Location_ID   = temp;
                }

                log.fine( "getProduct - C_TaxCategory_ID=" + C_TaxCategory_ID + ", billFromC_Location_ID=" + billFromC_Location_ID + ", billToC_Location_ID=" + billToC_Location_ID + ", shipFromC_Location_ID=" + shipFromC_Location_ID + ", shipToC_Location_ID=" + shipToC_Location_ID );

                return get( ctx,C_TaxCategory_ID,IsSOTrx,shipDate,shipFromC_Location_ID,shipToC_Location_ID,billDate,billFromC_Location_ID,billToC_Location_ID, C_BPartner_ID, M_Product_ID );
            }

            // ----------------------------------------------------------------

            // Detail for error isolation

            // M_Product_ID                            ->      C_TaxCategory_ID

            sql = "SELECT C_TaxCategory_ID FROM M_Product " + "WHERE M_Product_ID=?";
            variable = "M_Product_ID";
            pstmt    = DB.prepareStatement( sql );
            pstmt.setInt( 1,M_Product_ID );
            rs    = pstmt.executeQuery();
            found = false;

            if( rs.next()) {
                C_TaxCategory_ID = rs.getInt( 1 );
                found            = true;
            }

            rs.close();
            pstmt.close();
   
            if( C_TaxCategory_ID == 0 ) {
            	/*
            	 * Disytel - Matias Cap
                 * Modificación del mensaje de error a guardar, estaba hardcodeado
                 */
                log.saveError( "TaxCriteriaNotFound",Msg.translate( ctx,variable ) + ( found
                        ?""
                        :" ( "+Msg.getMsg(Env.getCtx(), "ProductNotFoundWithParameter", new Object[]{M_Product_ID})+" )" ));
                
                //Fin de la modificación
                return 0;
            }

            log.fine( "getProduct - C_TaxCategory_ID=" + C_TaxCategory_ID );

            // AD_Org_ID                                       ->      billFromC_Location_ID

            sql = "SELECT C_Location_ID FROM AD_OrgInfo " + "WHERE AD_Org_ID=?";
            variable = "AD_Org_ID";
            pstmt    = DB.prepareStatement( sql );
            pstmt.setInt( 1,AD_Org_ID );
            rs    = pstmt.executeQuery();
            found = false;

            if( rs.next()) {
                billFromC_Location_ID = rs.getInt( 1 );
                found                 = true;
            }

            rs.close();
            pstmt.close();

            if( billFromC_Location_ID == 0 ) {
            	/*
            	 * Disytel - Matias Cap
                 * Modificación del mensaje de error a guardar, estaba hardcodeado
                 */
                log.saveError( "TaxCriteriaNotFound",Msg.translate( Env.getAD_Language( ctx ),variable ) + ( found
                        ?""
                        :" ( "+Msg.getMsg(Env.getCtx(), "LocationOrgNotFoundWithParameter", new Object[]{AD_Org_ID})+" )" ));
                
                //Fin de la modificación
                return 0;
            }

            // billC_BPartner_Location_ID  ->  billToC_Location_ID

            sql = "SELECT l.C_Location_ID, b.IsTaxExempt " + "FROM C_BPartner_Location l INNER JOIN C_BPartner b ON (l.C_BPartner_ID=b.C_BPartner_ID) " + "WHERE C_BPartner_Location_ID=?";
            variable = "BillTo_ID";
            pstmt    = DB.prepareStatement( sql );
            pstmt.setInt( 1,billC_BPartner_Location_ID );
            rs    = pstmt.executeQuery();
            found = false;

            if( rs.next()) {
                billToC_Location_ID = rs.getInt( 1 );
                IsTaxExempt         = rs.getString( 2 );
                found               = true;
            }

            rs.close();
            pstmt.close();

            if( billToC_Location_ID == 0 ) {
            	/*
            	 * Disytel - Matias Cap
                 * Modificación del mensaje de error a guardar, estaba hardcodeado
                 */
                log.saveError( "TaxCriteriaNotFound",Msg.translate( Env.getAD_Language( ctx ),variable ) + ( found
                        ?""
                        :" ( "+Msg.getMsg(Env.getCtx(), "LocationBPartnerNotFoundWithParameter", new Object[]{billC_BPartner_Location_ID})+" )" ));

                //Fin de la modificación
                return 0;
            }

            if( "Y".equals( IsTaxExempt )) {
                return getExemptTax( ctx,AD_Org_ID );
            }

            // Reverse for PO

            if( !IsSOTrx ) {
                int temp = billFromC_Location_ID;

                billFromC_Location_ID = billToC_Location_ID;
                billToC_Location_ID   = temp;
            }

            log.fine( "getProduct - billFromC_Location_ID = " + billFromC_Location_ID );
            log.fine( "getProduct - billToC_Location_ID = " + billToC_Location_ID );

            // -----------------------------------------------------------------

            // M_Warehouse_ID                          ->      shipFromC_Location_ID

            sql = "SELECT C_Location_ID FROM M_Warehouse " + "WHERE M_Warehouse_ID=?";
            variable = "M_Warehouse_ID";
            pstmt    = DB.prepareStatement( sql );
            pstmt.setInt( 1,M_Warehouse_ID );
            rs    = pstmt.executeQuery();
            found = false;

            if( rs.next()) {
                shipFromC_Location_ID = rs.getInt( 1 );
                found                 = true;
            }

            rs.close();
            pstmt.close();

            if( shipFromC_Location_ID == 0 ) {
            	/*
            	 * Disytel - Matias Cap
                 * Modificación del mensaje de error a guardar, estaba hardcodeado
                 */
                log.saveError( "TaxCriteriaNotFound",Msg.translate( Env.getAD_Language( ctx ),variable ) + ( found
                        ?""
                        :" ( "+Msg.getMsg(Env.getCtx(), "WarehouseNotFoundWithParameter", new Object[]{M_Warehouse_ID}) +" )" ));
                
                //Fin de la modificación
                return 0;
            }

            // shipC_BPartner_Location_ID      ->      shipToC_Location_ID

            sql = "SELECT C_Location_ID FROM C_BPartner_Location " + "WHERE C_BPartner_Location_ID=?";
            variable = "C_BPartner_Location_ID";
            pstmt    = DB.prepareStatement( sql );
            pstmt.setInt( 1,shipC_BPartner_Location_ID );
            rs    = pstmt.executeQuery();
            found = false;

            if( rs.next()) {
                shipToC_Location_ID = rs.getInt( 1 );
                found               = true;
            }

            rs.close();
            pstmt.close();

            if( shipToC_Location_ID == 0 ) {
            	/*
            	 * Disytel - Matias Cap
                 * Modificación del mensaje de error a guardar, estaba hardcodeado
                 */
                log.saveError( "TaxCriteriaNotFound",Msg.translate( Env.getAD_Language( ctx ),variable ) + ( found
                        ?""
                        :" ( "+Msg.getMsg(Env.getCtx(), "LocationBPartnerNotFoundWithParameter", new Object[]{shipC_BPartner_Location_ID})+ " )" ));

                //Fin de la modificación
                return 0;
            }

            // Reverse for PO

            if( !IsSOTrx ) {
                int temp = shipFromC_Location_ID;

                shipFromC_Location_ID = shipToC_Location_ID;
                shipToC_Location_ID   = temp;
            }

            log.fine( "getProduct - shipFromC_Location_ID = " + shipFromC_Location_ID );
            log.fine( "getProduct - shipToC_Location_ID = " + shipToC_Location_ID );
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"getProduct (" + variable + ")",e );
        }

        return get( ctx,C_TaxCategory_ID,IsSOTrx,shipDate,shipFromC_Location_ID,shipToC_Location_ID,billDate,billFromC_Location_ID,billToC_Location_ID, C_BPartner_ID, M_Product_ID);
    }    // getProduct

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_Org_ID
     *
     * @return
     */

    private static int getExemptTax( Properties ctx,int AD_Org_ID ) {
        int    C_Tax_ID = 0;
        String sql      = "SELECT t.C_Tax_ID " + "FROM C_Tax t" + " INNER JOIN AD_Org o ON (t.AD_Client_ID=o.AD_Client_ID) " + "WHERE t.IsTaxExempt='Y' AND o.AD_Org_ID=? " + "ORDER BY t.Rate DESC";
        boolean found = false;

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,AD_Org_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                C_Tax_ID = rs.getInt( 1 );
                found    = true;
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"getExemptTax",e );
        }

        log.fine( "getExemptTax - TaxExempt=Y - C_Tax_ID=" + C_Tax_ID );

        if( C_Tax_ID == 0 ) {
        	/*
        	 * Disytel - Matias Cap
             * Modificación del mensaje de error a guardar, estaba hardcodeado
             */
            log.saveError( "TaxCriteriaNotFound",Msg.getMsg( ctx,"TaxNoExemptFound" ) + ( found
                    ?""
                    :" ( "+Msg.getMsg(Env.getCtx(), "TaxExemptOrgNotFoundWithParameter", new Object[]{AD_Org_ID})+" )" ));
            
          //Fin de la modificación
        }

        return C_Tax_ID;
    }    // getExemptTax

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_TaxCategory_ID
     * @param IsSOTrx
     * @param shipDate
     * @param shipFromC_Locction_ID
     * @param shipToC_Location_ID
     * @param billDate
     * @param billFromC_Location_ID
     * @param billToC_Location_ID
     * @return
     */
    protected static int get( Properties ctx,int C_TaxCategory_ID,boolean IsSOTrx,Timestamp shipDate,int shipFromC_Locction_ID,int shipToC_Location_ID,Timestamp billDate,int billFromC_Location_ID,int billToC_Location_ID, int C_BPartner_ID ) {
    	return get(ctx, C_TaxCategory_ID, IsSOTrx, shipDate, shipFromC_Locction_ID, shipToC_Location_ID, billDate, billFromC_Location_ID, billToC_Location_ID, C_BPartner_ID, 0);
    }
    
    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_TaxCategory_ID
     * @param IsSOTrx
     * @param shipDate
     * @param shipFromC_Locction_ID
     * @param shipToC_Location_ID
     * @param billDate
     * @param billFromC_Location_ID
     * @param billToC_Location_ID
     * @param C_Product_ID
     * 
     * @return
     */
    protected static int get( Properties ctx,int C_TaxCategory_ID,boolean IsSOTrx,Timestamp shipDate,int shipFromC_Locction_ID,int shipToC_Location_ID,Timestamp billDate,int billFromC_Location_ID,int billToC_Location_ID, int C_BPartner_ID, int C_Product_ID ) {

        // C_TaxCategory contains CommodityCode

        // API to Tax Vendor comes here

        if( CLogMgt.isLevelFine()) {
            log.info( "get(Detail) - Category=" + C_TaxCategory_ID + ", SOTrx=" + IsSOTrx );
            log.config( "get(Detail) - BillFrom=" + billFromC_Location_ID + ", BillTo=" + billToC_Location_ID + ", BillDate=" + billDate );
        }
        
        MLocation lFrom = new MLocation( ctx,billFromC_Location_ID,null );
        MLocation lTo   = new MLocation( ctx,billToC_Location_ID,null );       
        
        MTax[]    taxes = MTax.getAll( ctx );
        
        log.finer( "From=" + lFrom );
        log.finer( "To=" + lTo );

        // Indeos
        
        // Cargamos la entidad comercial, para comprobar si requiere impuestos extra
        MBPartner bpartner = new MBPartner(ctx, C_BPartner_ID, null);	// Sin transaccion
        
        // Se carga el articulo en caso de que el id sea válido
        MProduct product = null;
        if(C_Product_ID > 0)
        	product = new MProduct(ctx, C_Product_ID, null);
        
        // Comprobamos si hay impuesto para estos paises y estas regiones
        int C_Tax_ID= getTax(taxes, C_TaxCategory_ID, lFrom, lTo, IsSOTrx, billDate, bpartner, product);
        return C_Tax_ID;
    }    // get
    
    
    /**
     * Obtenemos la tasa usando un sistema de puntuacion de coincidencias.
     * Cada coincidencia en localizacion de la organizacion suma un punto
     * Se ha elegido este sistema en lugar de if encadenados ya que es mas 
     * claro y escalable.
     * 
     * @param taxes Tasas
     * @param C_TaxCategory_ID Categoria del impuesto
     * @param lFrom From Loation
     * @param lTo To Location
     * @param IsSOTrx es Venta??
     * @param billDate Fecha de facturacion.
     * @param bpartner Entidad comercial de facturacion.
     * @return ID de la tasa
     */
    private static int getTax(MTax[] taxes, int C_TaxCategory_ID, MLocation lFrom, MLocation lTo, boolean IsSOTrx, Timestamp billDate, MBPartner bpartner, MProduct product)	{
        int[] ranking = new int[taxes.length];
    	for( int i = 0;i < taxes.length;i++ ) {
            MTax tax = taxes[ i ];
            
            // inicializamos su puntuacion
            int score = 0;
            ranking[i] = score;
            
            log.finest( tax.toString());

            // Descargamos las que no cumplan los requisitos minimos

            if( (tax.getC_TaxCategory_ID() != C_TaxCategory_ID) ||!tax.isActive()) {
            	ranking[i] = -1;
                continue;
            }

			// Disytel - FB - Comentado por funcionamiento incorrecto. Ignoraba
			// los impuestos carpeta cuando en realidad estos deben ser tenidos
			// en cuenta por el ranking. La intensión de este código era ignorar
			// los hijos pero tampoco deben ser ignorados, ya que los mismos
			// pueden ser aplicados por sí mismos
            // 
            // Descartamos las que sean de tipo carpeta, ya que obtendremos su correspondencia
            // mediante el impuesto padre.
            //if (tax.isSummary())	{
            //	continue;
            //}
            // ---
            
            if( IsSOTrx && MTax.SOPOTYPE_PurchaseTax.equals( tax.getSOPOType())) {
                continue;
            }

            if( !IsSOTrx && MTax.SOPOTYPE_SalesTax.equals( tax.getSOPOType())) {
                continue;
            }

            if (tax.getValidFrom().after( billDate )) {
            	continue;
            }

            log.finest( "From Country - " + ( (tax.getC_Country_ID() == lFrom.getC_Country_ID()) || (tax.getC_Country_ID() == 0) ) );
            log.finest( "From Region - " + ( (tax.getC_Region_ID() == lFrom.getC_Region_ID()) || (tax.getC_Region_ID() == 0) ) );
            log.finest( "To Country - " + ( (tax.getTo_Country_ID() == lTo.getC_Country_ID()) || (tax.getTo_Country_ID() == 0) ) );
            log.finest( "To Region - " + ( (tax.getTo_Region_ID() == lTo.getC_Region_ID()) || (tax.getTo_Region_ID() == 0) ) );
            log.finest( "Date valid - " + ( !tax.getValidFrom().after( billDate )));

            // Comprobamos paises
            if(tax.getC_Country_ID() == lFrom.getC_Country_ID() && tax.getC_Region_ID() == 0)	{
            	score++;
            }
            // Hasta
            if ( tax.getTo_Country_ID() == lTo.getC_Country_ID() && tax.getTo_Region_ID() == 0)	{
            	score ++;
            }            	
            
            // Comprobamos regiones
            if (tax.getC_Region_ID() == lFrom.getC_Region_ID() )	{
            	score++;
            }
            // Hasta
            if (tax.getTo_Region_ID() == lTo.getC_Region_ID())	{
            	score++;
            }
            
            // Si se basa en codigos postales 
	        if(tax.isPostal())	{ 

	        	// Comprobamos codigos postales
	            MTaxPostal[] postals = tax.getPostals( false );
	
	            for( int j = 0;j < postals.length;j++ ) {
	                MTaxPostal postal = postals[ j ];
	
	                if( postal.isActive())	{
		                // De
	                	if (postal.getPostal().startsWith( lFrom.getPostal()))	{
		                	score++;
		                }
	                	// Hacia
		                if ( postal.getPostal_To().startsWith( lTo.getPostal())) {
		                    score++;
		                }
	                }
                }	// for Country 
            }
	        
	        // ** Modificaciones por Franco Bonafine **
	        // Req: Aplicación de impuestos
	        
	        // Comprobamos el grupo de la entidad comercial
	        if(tax.getC_BP_Group_ID() == bpartner.getC_BP_Group_ID())
	        	score ++;
	        
	        // Comprobamos la familia del producto
	        if( product != null && product.getM_Product_Category_ID() == tax.getM_Product_Category_ID())
	        	score ++;

	        // Comprobamos el producto
	        if( product != null && product.getM_Product_ID() == tax.getM_Product_ID())
	        	score ++;
	        
	        // ** Fin de modificaciones FB. **
	        // Asignanos la posicion en el ranking.
	        ranking[i] = score;
	        log.finest("Tax: " + tax.getName() + " - score: " + score);
	        
		}// for all postals	
    	
    	int maxscore = 0;
    	MTax tax = null;
    	
    	// Buscamos la tasa con mayor puntuacion.
    	for (int i=0; i < taxes.length; i++)	{
    		// Si es superior, guarda su puntuacion y su C_Tax_ID
    		if (ranking[i] > maxscore)	{
    			maxscore = ranking[i];
    			tax = taxes[i];
    		}
    		
    		// Si es igual, comprueba si este esta como predeterminado
    		else if (ranking[i] == maxscore)	{
    			if (tax == null || taxes[i].isDefault())	{
    				tax = taxes[i];
    			}
    		}
    	}
    	// Si no econtramos coincidencia
    	if (tax == null)	{
    		log.log(Level.SEVERE, "Tax not Found");
    		return 0;
    	}

    	// Comprobamos si la entidad comercial requiere impuestos compuestos
    	if (bpartner.isCompoundtax())	{
    		// Si tiene, devolvemos el impuesto compuesto que tenga configurado.
    		// No volvemos a comprobar si es correcto, dejamos esto en manos del administrador
    		// TODO: Calibrar si es necesario comprobarlo de nuevo.
    		if (tax.getParent_Tax_ID() != 0)	{
    			return tax.getParent_Tax_ID();
    		}
    		// Si no tiene configurado, entendemos que no existe impuesto compuesto par este tipo de tasa.
    		else {
    			log.log(Level.FINE, "La entidad comercial requiere impuesto compuesto pero no hay ninguno configurado para la tasa: " + tax.getName());
    		}
    	}
    	
    	
        // Devolvemos el ID de la tasa obtenida.
        return tax.getC_Tax_ID();
    }    

    public static int getExemptTax(int adClient) {
        int    C_Tax_ID = 0;
        String sql      = "SELECT t.C_Tax_ID " + "FROM C_Tax t WHERE t.IsTaxExempt='Y' AND AD_Client_ID = " + adClient; 
        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );
            ResultSet rs = pstmt.executeQuery();

            if( rs.next())
                C_Tax_ID = rs.getInt( 1 );

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"getExemptTax",e );
        }

        return C_Tax_ID;
    }    // getExemptTax
    
    
}    // Tax



/*
 *  @(#)Tax.java   02.07.07
 * 
 *  Fin del fichero Tax.java
 *  
 *  Versión 2.2
 *
 */
