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



import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import javax.swing.JOptionPane;



import org.openXpertya.model.MProduct;
import org.openXpertya.model.MTab;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ProductPriceGen extends SvrProcess {

    /**
     * Constructor de la clase ...
     *
     */

    public ProductPriceGen() {
    	 super();
    	 
    	 
       
    }    // ProductPriceGen

    /** Descripción de Campos */

    private int AD_Client_ID = 103;

    /** Descripción de Campos */

    private int M_PriceList_Version_ID;

    /** Descripción de Campos */

    private int AD_Org_ID;

    /** Descripción de Campos */

    private int User;

    /** Descripción de Campos */

    private StringBuffer infoReturn;
    private MTab m_curTab;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
    	log.info( " currupio Estoy ProductGen.prepare" );
        ProcessInfoParameter[] para = getParameter();
        //JOptionPane.showMessageDialog( null,"En ProducpriceGen, prepare() con los siguentes parametros"+"\n"+para,"..Fin", JOptionPane.INFORMATION_MESSAGE );

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "AD_Client_ID" )) {
                AD_Client_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "M_PriceList_Version_ID" )) {
                M_PriceList_Version_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "AD_Org_ID" )) {
                AD_Org_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "User" )) {
                User = para[ i ].getParameterAsInt();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }
         
        infoReturn = new StringBuffer( "" );
    }    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected String doIt() {
        deleteMProductPrice();
        insertMProductPrice();

        return infoReturn.toString();
    }    // doIt

    /**
     * Descripción de Método
     *
     */

    public void deleteMProductPrice() {
    	log.info( "currupio Estoy ProductGen.deleteMProductPrice con M_PriceList_Version_ID= "+ M_PriceList_Version_ID );
        try {
            StringBuffer sql = new StringBuffer( "DELETE FROM M_ProductPrice" );

            sql.append( " WHERE M_Product_ID IN (SELECT M_Product_ID FROM T_ProductPricing WHERE updatePrice = 'Y')" );
            sql.append( " AND M_PriceList_Version_ID = " + M_PriceList_Version_ID );
            DB.executeUpdate( sql.toString());
        } catch( Exception e ) {
            log.log( Level.SEVERE,"ProductPriceGen - deleteMProductPrice; " + e );
        }
        //JOptionPane.showMessageDialog( null,"deleteMProductPrice, para la version = "+ M_PriceList_Version_ID,null, JOptionPane.INFORMATION_MESSAGE );
    }    // deleteMProductPrice

    /**
     * Descripción de Método
     *
     */

    public void insertMProductPrice() {
    	log.info( " currupio Estoy ProductGen.insertMProductPrice" );
  
        PreparedStatement pstmt = null;
        ResultSet         rs;
        StringBuffer      sql;
        StringBuffer      i_sql;

        try {
            sql = new StringBuffer( "SELECT M_Product_ID, SelectedPrice, PriceStd, PriceLimit " );
            sql.append( " FROM T_ProductPricing WHERE updatePrice= 'Y'" );
            pstmt = DB.prepareStatement( sql.toString());
            rs    = pstmt.executeQuery();

            while( rs.next()) {
            	
            	//JOptionPane.showMessageDialog( null,"Insertado un producto en insertMProductPrice, para la version = "+ M_PriceList_Version_ID+"con M_Product_ID="+rs.getInt( 1 )+" SelectedPrice= "+rs.getInt( 2 )+"PriceStd= "+rs.getInt( 2 )+"PriceLimit= "+rs.getInt( 2 ),null, JOptionPane.INFORMATION_MESSAGE );
                MProduct prod = new MProduct( getCtx(),rs.getInt( 1 ),null );

                infoReturn.append( "<tr><td>" + rs.getInt( 1 ) + "</td><td>" + prod.getName() + "</td><td>" + rs.getBigDecimal( 2 ) + "</td></tr>" );
                i_sql = new StringBuffer( "INSERT INTO M_ProductPrice" );
                i_sql.append( " (M_PriceList_Version_ID, M_Product_ID, AD_Client_ID, AD_Org_ID, IsActive," );
                i_sql.append( " Created, CreatedBy, Updated , UpdatedBy, PriceList, PriceStd, PriceLimit) VALUES (" );
                i_sql.append( M_PriceList_Version_ID + ", " + rs.getInt( 1 ) + ", " + AD_Client_ID + ", " + AD_Org_ID + ", 'Y', SysDate, " );
                i_sql.append( User + ", SysDate, " + User + ", " + rs.getBigDecimal( 2 ) + ", " + rs.getBigDecimal( 3 ) + ", " + rs.getBigDecimal( 4 ) + ")" );
                //JOptionPane.showMessageDialog( null,"En ProducpriceGen, insertMProductPrice() con los siguentes parametros"+"\n"+i_sql.toString(),"..Fin", JOptionPane.INFORMATION_MESSAGE );
                DB.executeUpdate( i_sql.toString());
                //JOptionPane.showMessageDialog( null,"Insertado un producto en insertMProductPrice, para la version = "+ M_PriceList_Version_ID,null, JOptionPane.INFORMATION_MESSAGE );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"ProductPriceGen - insertMProductPrice; " + e );
        }
    }    // insertMProductPrice
}    // ProductPriceGen



/*
 *  @(#)ProductPriceGen.java   02.07.07
 * 
 *  Fin del fichero ProductPriceGen.java
 *  
 *  Versión 2.2
 *
 */
