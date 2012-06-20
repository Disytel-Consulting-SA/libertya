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
import java.sql.SQLException;
import java.util.logging.Level;

import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ImportProductBegin extends SvrProcess {

    /**
     * Constructor de la clase ...
     *
     */

    public ImportProductBegin() {
        super();
    }    // ImportProduct

    /** Descripción de Campos */

    private int m_AD_Client_ID = 0;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( name.equals( "AD_Client_ID" )) {
                m_AD_Client_ID = para[ i ].getParameterAsInt();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
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
        String       clientCheck = " AND AD_Client_ID=" + m_AD_Client_ID;

        // ****    Prepare ****

        sql = new StringBuffer( "DELETE I_Product " + "WHERE I_IsImported='Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.info( "doIt - Delete Old Impored =" + no );
        sql = new StringBuffer( "DELETE I_Product " + "WHERE (Value is null) and (UPC is null)" );
        no = DB.executeUpdate( sql.toString());
        log.info( "doIt - Delete when Value and EAN = null ->" + no );

        // Set Client, Org, IaActive, Created/Updated,     ProductType Name

        sql = new StringBuffer( "UPDATE I_Product " + "SET AD_Client_ID = COALESCE (AD_Client_ID, " ).append( m_AD_Client_ID ).append( ")," + " AD_Org_ID = COALESCE (AD_Org_ID, 0)," + " IsActive = COALESCE (IsActive, 'Y')," + " Created = COALESCE (Created, SysDate)," + " CreatedBy = COALESCE (CreatedBy, 0)," + " Updated = COALESCE (Updated, SysDate)," + " UpdatedBy = COALESCE (UpdatedBy, 0)," + " ProductType = COALESCE (ProductType, 'I')," + " I_ErrorMsg = NULL," + " I_IsImported = 'N' " + "WHERE I_IsImported<>'Y' OR I_IsImported IS NULL" );
        no = DB.executeUpdate( sql.toString());
        log.info( "doIt - Reset=" + no );

        // Set Optional BPartner

        sql = new StringBuffer( "UPDATE I_Product i " + "SET C_BPartner_ID=(SELECT C_BPartner_ID FROM C_BPartner p" + " WHERE i.BPartner_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) " + "WHERE C_BPartner_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.info( "doIt - BPartner=" + no );

        //

        sql = new StringBuffer( "UPDATE I_Product " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid BPartner,' " + "WHERE C_BPartner_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.warning( "doIt - Invalid BPartner=" + no );
        }

        // Elimina caracteres no alfanumericos (Solo quedan digitos y letras)

        sql = new StringBuffer( "SELECT I_Product_ID ,Value FROM I_Product" );

        PreparedStatement pstmt_value = DB.prepareStatement( sql.toString());
        ResultSet         rs_value    = pstmt_value.executeQuery();
        char[]            arrayCaracteres;

        try {
            while( rs_value.next()) {
                StringBuffer newValue = new StringBuffer( "" );

                arrayCaracteres = rs_value.getString( 2 ).toCharArray();

                for( int i = 0;i < arrayCaracteres.length;i++ ) {
                    if( Character.isLetter( arrayCaracteres[ i ] ) || Character.isDigit( arrayCaracteres[ i ] )) {
                        newValue.append( Character.toUpperCase( arrayCaracteres[ i ] ));
                    }
                }

                no = DB.executeUpdate( "UPDATE I_Product SET value='" + newValue + "' WHERE I_Product_ID = " + rs_value.getInt( 1 ));
            }

            rs_value.close();
            pstmt_value.close();
            pstmt_value = null;
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"doIt",e );

            throw new Exception( "doIt",e );
        }

        // ****    Find Product
        // EAN/UPC

        sql = new StringBuffer( "UPDATE I_Product i " + "SET M_Product_ID=(SELECT M_Product_ID FROM M_Product p" + " WHERE i.UPC=p.UPC AND i.AD_Client_ID=p.AD_Client_ID) " + "WHERE M_Product_ID IS NULL" + " AND I_IsImported='N'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.info( "doIt - Product Existing UPC=" + no );

        // Value

        sql = new StringBuffer( "UPDATE I_Product i " + "SET M_Product_ID=(SELECT M_Product_ID FROM M_Product p" + " WHERE i.Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) " + "WHERE M_Product_ID IS NULL" + " AND I_IsImported='N'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.info( "doIt - Product Existing Value=" + no );

        // BP ProdNo

        sql = new StringBuffer( "UPDATE I_Product i " + "SET M_Product_ID=(SELECT M_Product_ID FROM M_Product_po p" + " WHERE i.C_BPartner_ID=p.C_BPartner_ID" + " AND i.VendorProductNo=p.VendorProductNo AND i.AD_Client_ID=p.AD_Client_ID) " + "WHERE M_Product_ID IS NULL" + " AND I_IsImported='N'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.info( "doIt - Product Existing Vendor ProductNo=" + no );

        // Copy From Product if Import does not have value

        String[] strFields = new String[] {
            "Value","Name","Description","DocumentNote","Help","UPC","SKU","Classification","ProductType","Discontinued","DiscontinuedBy","ImageURL","DescriptionURL"
        };

        for( int i = 0;i < strFields.length;i++ ) {
            sql = new StringBuffer( "UPDATE I_PRODUCT i " + "SET " ).append( strFields[ i ] ).append( " = (SELECT " ).append( strFields[ i ] ).append( " FROM M_Product p" + " WHERE i.M_Product_ID=p.M_Product_ID AND i.AD_Client_ID=p.AD_Client_ID) " + "WHERE M_Product_ID IS NOT NULL" + " AND " ).append( strFields[ i ] ).append( " IS NULL" + " AND I_IsImported='N'" ).append( clientCheck );
            no = DB.executeUpdate( sql.toString());

            if( no != 0 ) {
                log.fine( "doIt - " + strFields[ i ] + " - default from existing Product=" + no );
            }
        }

        String[] numFields = new String[] {
            "C_UOM_ID","M_Product_Category_ID","Volume","Weight","ShelfWidth","ShelfHeight","ShelfDepth","UnitsPerPallet","M_Product_Family_ID"
        };

        for( int i = 0;i < numFields.length;i++ ) {
            sql = new StringBuffer( "UPDATE I_PRODUCT i " + "SET " ).append( numFields[ i ] ).append( " = (SELECT " ).append( numFields[ i ] ).append( " FROM M_Product p" + " WHERE i.M_Product_ID=p.M_Product_ID AND i.AD_Client_ID=p.AD_Client_ID) " + "WHERE M_Product_ID IS NOT NULL" + " AND (" ).append( numFields[ i ] ).append( " IS NULL OR " ).append( numFields[ i ] ).append( "=0)" + " AND I_IsImported='N'" ).append( clientCheck );
            no = DB.executeUpdate( sql.toString());

            if( no != 0 ) {
                log.fine( "doIt - " + numFields[ i ] + " default from existing Product=" + no );
            }
        }

        // Copy From Product_PO if Import does not have value

        String[] strFieldsPO = new String[] {
            "UPC","PriceEffective","VendorProductNo","VendorCategory","Manufacturer","Discontinued","DiscontinuedBy"
        };

        for( int i = 0;i < strFieldsPO.length;i++ ) {
            sql = new StringBuffer( "UPDATE I_PRODUCT i " + "SET " ).append( strFieldsPO[ i ] ).append( " = (SELECT " ).append( strFieldsPO[ i ] ).append( " FROM M_Product_PO p" + " WHERE i.M_Product_ID=p.M_Product_ID AND i.C_BPartner_ID=p.C_BPartner_ID AND i.AD_Client_ID=p.AD_Client_ID) " + "WHERE M_Product_ID IS NOT NULL AND C_BPartner_ID IS NOT NULL" + " AND " ).append( strFieldsPO[ i ] ).append( " IS NULL" + " AND I_IsImported='N'" ).append( clientCheck );
            no = DB.executeUpdate( sql.toString());

            if( no != 0 ) {
                log.fine( "doIt - " + strFieldsPO[ i ] + " default from existing Product PO=" + no );
            }
        }

        String[] numFieldsPO = new String[] {
            "C_UOM_ID","C_Currency_ID","PriceList","PricePO","RoyaltyAmt","Order_Min","Order_Pack","CostPerOrder","DeliveryTime_Promised"
        };

        for( int i = 0;i < numFieldsPO.length;i++ ) {
            sql = new StringBuffer( "UPDATE I_PRODUCT i " + "SET " ).append( numFieldsPO[ i ] ).append( " = (SELECT " ).append( numFieldsPO[ i ] ).append( " FROM M_Product_PO p" + " WHERE i.M_Product_ID=p.M_Product_ID AND i.C_BPartner_ID=p.C_BPartner_ID AND i.AD_Client_ID=p.AD_Client_ID) " + "WHERE M_Product_ID IS NOT NULL AND C_BPartner_ID IS NOT NULL" + " AND (" ).append( numFieldsPO[ i ] ).append( " IS NULL OR " ).append( numFieldsPO[ i ] ).append( "=0)" + " AND I_IsImported='N'" ).append( clientCheck );
            no = DB.executeUpdate( sql.toString());

            if( no != 0 ) {
                log.fine( "doIt - " + numFieldsPO[ i ] + " default from existing Product PO=" + no );
            }
        }

        // Set UOM (System/own)

        sql = new StringBuffer( "UPDATE I_Product i " + "SET X12DE355 = " + "(SELECT X12DE355 FROM C_UOM u WHERE u.IsDefault='Y' AND u.AD_Client_ID IN (0,i.AD_Client_ID) AND ROWNUM=1) " + "WHERE X12DE355 IS NULL AND C_UOM_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.finest( "doIt - Set UOM Default=" + no );

        //

        sql = new StringBuffer( "UPDATE I_Product i " + "SET C_UOM_ID = (SELECT C_UOM_ID FROM C_UOM u WHERE u.X12DE355=i.X12DE355 AND u.AD_Client_ID IN (0,i.AD_Client_ID)) " + "WHERE C_UOM_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.info( "doIt - Set UOM=" + no );

        //

        sql = new StringBuffer( "UPDATE I_Product " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid UOM, ' " + "WHERE C_UOM_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.warning( "doIt - Invalid UOM=" + no );
        }

        // Set Product Category (own)

        sql = new StringBuffer( "UPDATE I_Product " + "SET ProductCategory_Value=(SELECT Value FROM M_Product_Category" + " WHERE IsDefault='Y' AND AD_Client_ID=" ).append( m_AD_Client_ID ).append( " AND ROWNUM=1) " + "WHERE ProductCategory_Value IS NULL AND M_Product_Category_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.finest( "doIt - Set Category Default=" + no );

        //

        sql = new StringBuffer( "UPDATE I_Product i " + "SET M_Product_Category_ID=(SELECT M_Product_Category_ID FROM M_Product_Category c" + " WHERE i.ProductCategory_Value=c.Value AND i.AD_Client_ID=c.AD_Client_ID) " + "WHERE M_Product_Category_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.info( "doIt - Set Category=" + no );

        //

        sql = new StringBuffer( "UPDATE I_Product " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid ProdCategorty,' " + "WHERE M_Product_Category_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.warning( "doIt - Invalid Category=" + no );
        }

        // Set Currency

        sql = new StringBuffer( "UPDATE I_Product i " + "SET ISO_Code=(SELECT ISO_Code FROM C_Currency c" + " INNER JOIN C_AcctSchema a ON (a.C_Currency_ID=c.C_Currency_ID)" + " INNER JOIN AD_ClientInfo fo ON (a.C_AcctSchema_ID=fo.C_AcctSchema1_ID)" + " WHERE fo.AD_Client_ID=i.AD_Client_ID) " + "WHERE C_Currency_ID IS NULL AND ISO_Code IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.finest( "doIt - Set Currency Default=" + no );

        //

        sql = new StringBuffer( "UPDATE I_Product i " + "SET C_Currency_ID=(SELECT C_Currency_ID FROM C_Currency c" + " WHERE i.ISO_Code=c.ISO_Code AND c.AD_Client_ID IN (0,i.AD_Client_ID)) " + "WHERE C_Currency_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.info( "doIt- Set Currency=" + no );

        //

        sql = new StringBuffer( "UPDATE I_Product " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Currency,' " + "WHERE C_Currency_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.warning( "doIt - Invalid Currency=" + no );
        }

        // Verify ProductType

        sql = new StringBuffer( "UPDATE I_Product " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid ProductType,' " + "WHERE ProductType NOT IN ('I','S')" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.warning( "doIt - Invalid ProductType=" + no );
        }

        // Unique UPC/Value

        sql = new StringBuffer( "UPDATE I_Product i " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Value not unique,' " + "WHERE I_IsImported<>'Y'" + " AND Value IN (SELECT Value FROM I_Product pr WHERE i.AD_Client_ID=pr.AD_Client_ID GROUP BY Value HAVING COUNT(*) > 1)" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.warning( "doIt - Not Unique Value=" + no );
        }

        //

        sql = new StringBuffer( "UPDATE I_Product i " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=UPC not unique,' " + "WHERE I_IsImported<>'Y'" + " AND UPC IN (SELECT UPC FROM I_Product pr WHERE i.AD_Client_ID=pr.AD_Client_ID GROUP BY UPC HAVING COUNT(*) > 1)" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.warning( "doIt - Not Unique UPC=" + no );
        }

        // Mandatory Value

        sql = new StringBuffer( "UPDATE I_Product i " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=No Mandatory Value,' " + "WHERE Value IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.warning( "doIt - No Mandatory Value=" + no );
        }

        // Vendor Product No
        // sql = new StringBuffer ("UPDATE I_Product i "
        // + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=No Mandatory VendorProductNo,' "
        // + "WHERE I_IsImported<>'Y'"
        // + " AND VendorProductNo IS NULL AND (C_BPartner_ID IS NOT NULL OR BPartner_Value IS NOT NULL)").append(clientCheck);
        // no = DB.executeUpdate(sql.toString());
        // log.info(log.l3_Util, "doIt - No Mandatory VendorProductNo=" + no);

        sql = new StringBuffer( "UPDATE I_Product " + "SET VendorProductNo=Value " + "WHERE C_BPartner_ID IS NOT NULL AND VendorProductNo IS NULL" + " AND I_IsImported='N'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.info( "doIt - VendorProductNo Set to Value=" + no );

        //

        sql = new StringBuffer( "UPDATE I_Product i " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=VendorProductNo not unique,' " + "WHERE I_IsImported<>'Y'" + " AND C_BPartner_ID IS NOT NULL" + " AND (C_BPartner_ID, VendorProductNo) IN " + " (SELECT C_BPartner_ID, VendorProductNo FROM I_Product pr WHERE i.AD_Client_ID=pr.AD_Client_ID GROUP BY C_BPartner_ID, VendorProductNo HAVING COUNT(*) > 1)" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.warning( "doIt - Not Unique VendorProductNo=" + no );
        }

        // Set Name where null

        sql = new StringBuffer( "UPDATE I_Product P SET Name = " + "(SELECT (C.name || ' ' || F.name || ' ' || I.value) as name FROM I_Product I " + "INNER JOIN M_Product_Category C ON C.M_Product_Category_ID=I.M_Product_Category_ID " + "INNER JOIN M_Product_Family F ON F.M_Product_Family_ID=I.M_Product_Family_ID " + "WHERE I.I_Product_id=P.I_Product_id) " + "WHERE (NAME IS NULL) AND (I_IsImported<>'Y' OR I_IsImported IS NULL)" );
        no = DB.executeUpdate( sql.toString());

        return "";
    }    // doIt
}    // ImportProduct



/*
 *  @(#)ImportProductBegin.java   02.07.07
 * 
 *  Fin del fichero ImportProductBegin.java
 *  
 *  Versión 2.2
 *
 */
