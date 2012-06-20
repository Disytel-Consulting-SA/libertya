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



package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.openXpertya.model.MProductUpcInstance;
import org.openXpertya.model.X_I_ProductInstance;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ImportProductInstance extends SvrProcess {

    /** Descripción de Campos */

    private int m_AD_Client_ID = 0;

    /** Descripción de Campos */

    private boolean m_deleteOldImported = false;

    /** Descripción de Campos */

    private int m_AD_Org_ID = 0;

    /** Descripción de Campos */

    private Timestamp m_DateValue = null;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( name.equals( "AD_Client_ID" )) {
                m_AD_Client_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "DeleteOldImported" )) {
                m_deleteOldImported = "Y".equals( para[ i ].getParameter());
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
        }

        if( m_DateValue == null ) {
            m_DateValue = new Timestamp( System.currentTimeMillis());
        }
    }    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws java.lang.Exception
     */

    protected String doIt() throws java.lang.Exception {
        StringBuffer sql         = null;
        int          no          = 0;
        String       clientCheck = " AND AD_Client_ID=" + getAD_Client_ID();//m_AD_Client_ID;
        String errorInstanceNotFound   = "'"+getMsg("InstanceNotFound")+". '";
        String errorProductNotFound   = "'"+getMsg("ProductNotFound")+". '";
        String       securityCheck = " AND AD_Client_ID=" + m_AD_Client_ID + " AND CreatedBy=" + getAD_User_ID() + " AND IsActive = 'Y' ";

        // ****    Prepare ****

        // Delete Old Imported

        if( m_deleteOldImported ) {
            sql = new StringBuffer( "DELETE I_ProductInstance " + "WHERE I_IsImported='Y'" ).append( clientCheck );
            no = DB.executeUpdate( sql.toString());
            log.info( "Delete Old Impored =" + no );
        }        

        // Set Client, Org, IaActive, Created/Updated
        sql = new StringBuffer( "UPDATE I_ProductInstance " + "SET AD_Client_ID = COALESCE (AD_Client_ID, " ).append( m_AD_Client_ID ).append( ")," + " AD_Org_ID = COALESCE (AD_Org_ID, 0)," + " IsActive = COALESCE (IsActive, 'Y')," + " Created = COALESCE (Created, current_timestamp)," + " CreatedBy = COALESCE (CreatedBy, 0)," + " Updated = COALESCE (Updated, current_timestamp)," + " UpdatedBy = COALESCE (UpdatedBy, 0)," + " I_ErrorMsg = ''," + " I_IsImported = 'N' " + "WHERE I_IsImported<>'Y' OR I_IsImported IS NULL" );
        no = DB.executeUpdate( sql.toString());
        log.info( "Reset=" + no );
        	
        // ****    Find Product
        // Value
        sql = new StringBuffer( "UPDATE I_ProductInstance i " + "SET M_Product_ID=(SELECT M_Product_ID FROM M_Product p" + " WHERE i.product_value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) " + "WHERE M_Product_ID IS NULL" + " AND I_IsImported='N'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.info( "Product Existing Value=" + no );
        
        // No instance detection	
        sql = new StringBuffer( 
        	" UPDATE I_ProductInstance " + 
        	" SET I_IsImported = 'E', " +
        	"     I_ErrorMsg = COALESCE(I_ErrorMsg,'') || " + errorProductNotFound +
        	" WHERE M_Product_ID IS NULL AND " +
        	"       I_IsImported<>'Y'" ).append( securityCheck );
        no = DB.executeUpdate( sql.toString());
        if( no != 0 ) {
            log.warning( "Product not found = " + no );
        }

        // ****    Find Instance
        // Value
        
        sql = new StringBuffer( "UPDATE I_ProductInstance i " + "SET M_AttributeSetInstance_ID=(SELECT M_AttributeSetInstance_ID FROM M_AttributeSetInstance a" + " WHERE i.instance_description=a.Description AND i.AD_Client_ID=a.AD_Client_ID) " + "WHERE M_AttributeSetInstance_ID IS NULL" + " AND I_IsImported='N'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.info( "Product Instance Existing Value=" + no );
        
        // No instance detection
        sql = new StringBuffer( 
        	" UPDATE I_ProductInstance " + 
        	" SET I_IsImported = 'E', " +
        	"     I_ErrorMsg = COALESCE(I_ErrorMsg,'') || " + errorInstanceNotFound +
        	" WHERE M_AttributeSetInstance_ID IS NULL AND " +
        	"       I_IsImported<>'Y'" ).append( securityCheck );
        no = DB.executeUpdate( sql.toString());
        if( no != 0 ) {
            log.warning( "Product Instance not found = " + no );
        }
              
        // Unique UPC/Value

        /*sql = new StringBuffer( "UPDATE I_ProductInstance i " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'"+ getMsg("ImportProductNotUniqueValue")+". ' " + "WHERE I_IsImported<>'Y'" + " AND Value IN (SELECT Value FROM I_ProductInstance pr WHERE i.AD_Client_ID=pr.AD_Client_ID GROUP BY Value HAVING COUNT(*) > 1)" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.warning( "Not Unique Value=" + no );
        }*/

        //

        sql = new StringBuffer( "UPDATE I_ProductInstance i " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'"+ getMsg("ImportProductNotUniqueUPC")+". ' "  + "WHERE I_IsImported<>'Y'" + " AND UPC IN (SELECT UPC FROM I_ProductInstance pr WHERE i.AD_Client_ID=pr.AD_Client_ID GROUP BY UPC HAVING COUNT(*) > 1)" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.warning( "Not Unique UPC=" + no );
        }

        // -------------------------------------------------------------------
        int noInsert   = 0;
        Connection conn = DB.createConnection( false,Connection.TRANSACTION_READ_COMMITTED );
        
        try {
        sql = new StringBuffer( "SELECT I_ProductInstance_ID, M_Product_ID, M_AttributeSetInstance_ID, UPC " + "FROM I_ProductInstance WHERE I_IsImported='N'" ).append( clientCheck );
        log.finer("sql.toString= "+sql.toString());
        PreparedStatement pstmt = DB.prepareStatement( sql.toString());
        ResultSet         rs    = pstmt.executeQuery();
        
        PreparedStatement pstmt_setImported = conn.prepareStatement( "UPDATE I_ProductInstance SET I_IsImported='Y', Updated=current_timestamp, Processed='Y' WHERE I_ProductInstance_ID=?" );
     
        while( rs.next()) {
        	log.finer("Una instancia................................................");
            int     I_ProductInstance_ID  = rs.getInt( 1 );
            int     M_Product_ID  = rs.getInt( 2 );
            int     M_AttributeSetInstance_ID = rs.getInt( 3 );
            int     UPC = rs.getInt( 4 );
            
            log.fine( "I_ProductInstance_ID..=" + I_ProductInstance_ID + ", M_Product_ID..=" + M_Product_ID + ", M_AttributeSetInstance_ID..=" + M_AttributeSetInstance_ID + ", UPC..=" + UPC );
            
	        // Insert Product UPC Instance from Import
            
            X_I_ProductInstance iP = new X_I_ProductInstance( getCtx(),I_ProductInstance_ID,null );
            MProductUpcInstance p = new MProductUpcInstance( iP );

            if( p.save()) {
                M_Product_ID = p.getM_Product_ID();
                log.finer( "Insert ProductInstance" );
                noInsert++;
            } else {
                sql = new StringBuffer( "UPDATE I_ProductInstance i " + "SET I_IsImported='Y', I_ErrorMsg=I_ErrorMsg||" ).append( DB.TO_STRING(getMsg("ImportProductSaveError") + ": " + CLogger.retrieveErrorAsString() )).append( "WHERE I_ProductInstance_ID=" ).append( I_ProductInstance_ID );
                DB.executeUpdate( sql.toString());

                continue;
            }
            pstmt_setImported.setInt( 1,I_ProductInstance_ID );
            no = pstmt_setImported.executeUpdate();
            conn.commit();
	
        }
        rs.close();
        pstmt.close();
        
        pstmt_setImported.close();

        //

        conn.close();
        conn = null;
	    } catch( SQLException e ) {
	        try {
	            if( conn != null ) {
	                conn.close();
	            }
	
	            conn = null;
	        } catch( SQLException ex ) {
	        }
	
	        log.log( Level.SEVERE,"doIt",e );
	
	        throw new Exception( "doIt",e );
	    } finally {
	        if( conn != null ) {
	            conn.close();
	        }
	
	        conn = null;
	    }

        return "";
    }    // doIt
    
    protected String getMsg(String msg) {
    	return Msg.translate(getCtx(), msg);
    }
}    // ImportProduct



/*
 *  @(#)ImportProduct.java   02.07.07
 * 
 *  Fin del fichero ImportProduct.java
 *  
 *  Versión 2.2
 *
 */
