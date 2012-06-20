/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2005 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c)  2003-2005 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.wstore;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import java.math.BigDecimal;

import org.openXpertya.model.MProductCategory;
import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.0, 22.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class PriceList {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_Client_ID
     * @param M_PriceList_ID
     * @param searchString
     * @param productCategory
     * @param allRecords
     *
     * @return
     */

    public static PriceList get( Properties ctx,int AD_Client_ID,int M_PriceList_ID,String searchString,String productCategory,boolean allRecords,String minimumPrice,String maximumPrice ) {

        // Search Parameter

        String search = searchString;

        if( (search != null) && ( (search.length() == 0) || search.equals( "%" ))) {
            search = null;
        }

        if( search != null ) {
            if( !search.endsWith( "%" )) {
                search += "%";
            }

            if( !search.startsWith( "%" )) {
                search = "%" + search;
            }

            search = search.toUpperCase();
        }

        int M_Product_Category_ID = 0;

        try {
            if( (productCategory != null) && (productCategory.length() > 0) ) {
                M_Product_Category_ID = Integer.parseInt( productCategory );
            }
        } catch( Exception e ) {
        }

        if( M_Product_Category_ID < 0 ) {
            M_Product_Category_ID = 0;
        }

        int minPrice = 0;
        
        try {
            if( (minimumPrice != null) && (minimumPrice.length() > 0) ) {
            	minPrice = Integer.parseInt( minimumPrice );
            }
        } catch( Exception e ) {
        }
        
        if( minPrice < 0 ) {
        	minPrice = 0;
        }

        int maxPrice = 0;
        
        try {
            if( (maximumPrice != null) && (maximumPrice.length() > 0) ) {
            	maxPrice = Integer.parseInt( maximumPrice );
            }
        } catch( Exception e ) {
        }
        
        if( maxPrice < 0 ) {
        	maxPrice = 0;
        }

        // Search Price List Cache

        String    key      = String.valueOf( AD_Client_ID ) + "_" + M_PriceList_ID;
        PriceList retValue = null;

        if( (search == null) && (M_Product_Category_ID == 0) && allRecords ) {
            retValue = ( PriceList )s_cache.get( key );
        }

        // create New

        if( retValue == null ) {
            retValue = new PriceList( ctx,AD_Client_ID,M_PriceList_ID,search,M_Product_Category_ID,allRecords,minPrice,maxPrice );

            if( (search == null) && (M_Product_Category_ID == 0) && allRecords ) {
                s_cache.put( key,retValue );
            }
        }     
        
        return retValue;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_Client_ID
     * @param M_PriceList_ID
     * @param searchString
     * @param productCategory
     * @param allRecords
     *
     * @return
     */

    public static PriceList get( Properties ctx,int AD_Client_ID,int M_PriceList_ID,String searchString,String productCategory,boolean allRecords,String inStock,String order,String minimumPrice,String maximumPrice ) {

        // Search Parameter

        String search = searchString;

        if( (search != null) && ( (search.length() == 0) || search.equals( "%" ))) {
            search = null;
        }

        if( search != null ) {
            if( !search.endsWith( "%" )) {
                search += "%";
            }

            if( !search.startsWith( "%" )) {
                search = "%" + search;
            }

            search = search.toUpperCase();
        }

        int M_Product_Category_ID = 0;

        try {
            if( (productCategory != null) && (productCategory.length() > 0) ) {
                M_Product_Category_ID = Integer.parseInt( productCategory );
            }
        } catch( Exception e ) {
        }

        if( M_Product_Category_ID < 0 ) {
            M_Product_Category_ID = 0;
        }

        int minPrice = 0;
        
        try {
            if( (minimumPrice != null) && (minimumPrice.length() > 0) ) {
            	minPrice = Integer.parseInt( minimumPrice );
            }
        } catch( Exception e ) {
        	minPrice = 0;
        }
        
        if( minPrice < 0 ) {
        	minPrice = 0;
        }
        
        int maxPrice = 0;
        
        try {
            if( (maximumPrice != null) && (maximumPrice.length() > 0) ) {
            	maxPrice = Integer.parseInt( maximumPrice );
            }
        } catch( Exception e ) {
        	maxPrice = 1000000;
        }
        
        if( maxPrice < 0 ) {
        	maxPrice = 0;
        }
        // Search Price List Cache

        String    key      = String.valueOf( AD_Client_ID ) + "_" + M_PriceList_ID;
        PriceList retValue = null;

        if( (search == null) && (M_Product_Category_ID == 0) && allRecords ) {
            retValue = ( PriceList )s_cache.get( key );
        }

        // create New

        if( retValue == null ) {
        	if( (inStock != null) && (order != null ) ) {
        		retValue = new PriceList( ctx,AD_Client_ID,M_PriceList_ID,search,M_Product_Category_ID,allRecords,inStock,order,minPrice,maxPrice );
        	}
        	else {
        		retValue = new PriceList( ctx,AD_Client_ID,M_PriceList_ID,search,M_Product_Category_ID,allRecords,minPrice,maxPrice );
        	}
        	
            if( (search == null) && (M_Product_Category_ID == 0) && allRecords ) {
                s_cache.put( key,retValue );
            }
        }

        return retValue;
    }    // get
    
    /** Descripción de Campos */

    private static CCache s_cache = new CCache( "PriceList",5,60 );    // 1h Cache

    /** Descripción de Campos */

    public static int MAX_LINES = 50;

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param AD_Client_ID
     * @param M_PriceList_ID
     * @param searchString
     * @param M_Product_Category_ID
     * @param allRecords
     */

    private PriceList( Properties ctx,int AD_Client_ID,int M_PriceList_ID,String searchString,int M_Product_Category_ID,boolean allRecords,int minPrice,int maxPrice ) {
        log.finer( "AD_Client_ID=" + AD_Client_ID + ", M_PriceList_ID=" + M_PriceList_ID + ", Search=" + searchString + ",M_Product_Category_ID=" + M_Product_Category_ID + ", All=" + allRecords );
        m_ctx = ctx;

        // Get Price List

        if( getM_PriceList_ID( AD_Client_ID,M_PriceList_ID ) == 0 ) {
            if( getM_PriceList_ID( AD_Client_ID,0 ) == 0 ) {
                return;
            }
        }

        // Get Price List Version

        getM_PriceList_Version_ID( m_PriceList_ID,new Timestamp( System.currentTimeMillis()));
        loadProducts( searchString,M_Product_Category_ID,allRecords,minPrice,maxPrice );
    }    // PriceList

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param AD_Client_ID
     * @param M_PriceList_ID
     * @param searchString
     * @param M_Product_Category_ID
     * @param allRecords
     */

    private PriceList( Properties ctx,int AD_Client_ID,int M_PriceList_ID,String searchString,int M_Product_Category_ID,boolean allRecords,String inStock,String order,int minPrice,int maxPrice ) {
        log.finer( "AD_Client_ID=" + AD_Client_ID + ", M_PriceList_ID=" + M_PriceList_ID + ", Search=" + searchString + ",M_Product_Category_ID=" + M_Product_Category_ID + ", All=" + allRecords );
        m_ctx = ctx;

        // Get Price List

        if( getM_PriceList_ID( AD_Client_ID,M_PriceList_ID ) == 0 ) {
            if( getM_PriceList_ID( AD_Client_ID,0 ) == 0 ) {
                return;
            }
        }

        // Get Price List Version

        getM_PriceList_Version_ID( m_PriceList_ID,new Timestamp( System.currentTimeMillis()));
        loadProducts( searchString,M_Product_Category_ID,allRecords,inStock,order,minPrice,maxPrice );
    }    // PriceList
    
    /** Descripción de Campos */

    public static final String NAME = "priceList";

    /** Descripción de Campos */

    private CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    //private String m_name = "Not found";
    private String m_name = "No encontrada";

    /** Descripción de Campos */

    private String m_description;

    /** Descripción de Campos */

    private String m_currency;

    /** Descripción de Campos */

    private String m_curSymbol;

    /** Descripción de Campos */

    private String m_AD_Language;

    /** Descripción de Campos */

    private boolean m_taxIncluded;

    /** Descripción de Campos */

    private int m_PriceList_ID = 0;

    /** Descripción de Campos */

    private int m_PriceList_Version_ID = 0;

    /** Descripción de Campos */

    private String m_searchInfo = "";

    /** Descripción de Campos */

    private boolean m_notAllPrices = false;

    /** Descripción de Campos */

    private ArrayList m_prices = new ArrayList();

    /** Descripción de Campos */

    private Properties m_ctx;

    /**
     * Descripción de Método
     *
     *
     * @param AD_Client_ID
     * @param M_PriceList_ID
     *
     * @return
     */

    private int getM_PriceList_ID( int AD_Client_ID,int M_PriceList_ID ) {
        String sql = "SELECT M_PriceList_ID, pl.Name, pl.Description, pl.IsTaxIncluded,"    // 1..4
                     + " c.ISO_Code, c.CurSymbol, cc.AD_Language  "                                                                                                                                                                                            // 5..7
                     //+ "FROM M_PriceList pl" + " INNER JOIN C_Currency c ON (pl.C_Currency_ID=c.C_Currency_ID)" + " LEFT OUTER JOIN C_Country cc ON (c.C_Currency_ID=cc.C_Currency_ID AND ROWNUM=1) " + "WHERE pl.IsActive='Y'" + " AND pl.AD_Client_ID=?";    // #1
                     + "FROM M_PriceList pl" + " INNER JOIN C_Currency c ON (pl.C_Currency_ID=c.C_Currency_ID)" + " LEFT OUTER JOIN C_Country cc ON (c.C_Currency_ID=cc.C_Currency_ID) " + "WHERE pl.IsActive='Y'" + " AND pl.AD_Client_ID=?";    // #1

        if( M_PriceList_ID != 0 ) {
            sql += " AND pl.M_PriceList_ID=?";    // #2
        } else {
            sql += " ORDER BY pl.IsDefault DESC";
        }
        
        sql += " LIMIT 1";

        m_PriceList_ID = 0;

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,AD_Client_ID );

            if( M_PriceList_ID != 0 ) {
                pstmt.setInt( 2,M_PriceList_ID );
            }

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                m_PriceList_ID = rs.getInt( 1 );
                m_name         = rs.getString( 2 );
                m_description  = rs.getString( 3 );
                m_taxIncluded  = "Y".equals( rs.getString( 4 ));
                m_currency     = rs.getString( 5 );
                m_curSymbol    = rs.getString( 6 );
                m_AD_Language  = rs.getString( 7 );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getM_PriceList_ID",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }
        
        return m_PriceList_ID;
    }    // getM_PriceList_ID

    /**
     * Descripción de Método
     *
     *
     * @param M_PriceList_ID
     * @param day
     *
     * @return
     */

    private int getM_PriceList_Version_ID( int M_PriceList_ID,Timestamp day ) {
        String sql = "SELECT plv.M_PriceList_Version_ID, plv.Name, plv.Description, plv.ValidFrom "    // 1..4
                     + "FROM M_PriceList_Version plv " + "WHERE plv.M_PriceList_ID=?"    // #1
                     + " AND plv.ValidFrom <=? "                                         // #2
                     + "ORDER BY plv.ValidFrom DESC";
        PreparedStatement pstmt = null;

        m_PriceList_Version_ID = 0;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,M_PriceList_ID );
            pstmt.setTimestamp( 2,day );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                m_PriceList_Version_ID = rs.getInt( 1 );
                m_name                 = rs.getString( 2 );
                m_description          = rs.getString( 3 );

                // m_validFrom = rs.getTimestamp(4);

            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getM_PriceList_Version_ID",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        return m_PriceList_Version_ID;
    }    // getM_PriceList_Version_ID

    /**
     * Descripción de Método
     *
     *
     * @param searchString
     * @param M_Product_Category_ID
     * @param allRecords
     */

    private void loadProducts( String searchString,int M_Product_Category_ID,boolean allRecords,int minPrice,int maxPrice ) {

        // Set Search String

        log.finer( "loadProducts - M_PriceList_Version_ID=" + m_PriceList_Version_ID + ", Search=" + searchString + ", M_Product_Category_ID=" + M_Product_Category_ID );
        m_searchInfo = "";

        if( searchString != null ) {
            m_searchInfo = searchString;
        }

        if( M_Product_Category_ID != 0 ) {
            if( m_searchInfo.length() != 0 ) {
                m_searchInfo += " - ";
            }

            m_searchInfo += MProductCategory.get( m_ctx,M_Product_Category_ID, null ).getName();
        }

        m_prices.clear();
        m_notAllPrices = false;

        //

        String sql = "SELECT p.M_Product_ID, p.Value, p.Name, p.Description, p.Help, p.DocumentNote, p.ImageURL, p.DescriptionURL, pp.Pricelist, uom.Name, uom.UOMSymbol"                                                                                                                                                                                        // 9..11
                     + " FROM M_ProductPrice pp"
                     + " INNER JOIN M_Product p ON (pp.M_Product_ID = p.M_Product_ID AND p.IsActive = 'Y' AND p.IsSold=  'Y')"
                     + " INNER JOIN C_UOM uom ON (p.C_UOM_ID = uom.C_UOM_ID)"
                     + " WHERE pp.M_PriceList_Version_ID = ?"    // #1
                     + " AND pp.PriceStd > 0 "
                     + " AND p.IsSelfService='Y'";       
        
        if( searchString != null ) {
            sql += " AND (UPPER(p.Value) LIKE ? ";    // #2
            sql += " OR UPPER(p.Name) LIKE ? ";    // #3
            sql += " OR UPPER(p.Description) LIKE ? ) ";    // #4
        }

        if( M_Product_Category_ID != 0 ) {
            sql += " AND p.M_Product_Category_ID = ? ";    // #5
        }

		if( minPrice >= 0 ) {
			sql += " AND pp.Pricelist > ?";    //  #6
		}
		
		if( maxPrice > 0) {
			sql += " AND pp.Pricelist < ?";    //  #7
		}
        
        if( !allRecords && (searchString == null) && (M_Product_Category_ID == 0) ) {
            sql += " AND p.IsWebStoreFeatured = 'Y' ";
            m_notAllPrices = true;
        }

        sql += "ORDER BY p.M_Product_Category_ID, p.Value";

        // log.fine("loadProducts - " + sql);

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );

            int index = 1;

            pstmt.setInt( index++,m_PriceList_Version_ID );

            if( searchString != null ) {
                pstmt.setString( index++,searchString );
                pstmt.setString( index++,searchString );
                pstmt.setString( index++,searchString );
            }

            if( M_Product_Category_ID != 0 ) {
                pstmt.setInt( index++,M_Product_Category_ID );
            }

            if( minPrice >= 0 ) {
                pstmt.setInt( index++,minPrice );
            }

            if( maxPrice > 0 ) {
                pstmt.setInt( index++,maxPrice );
            }
            
            ResultSet rs = pstmt.executeQuery();
            int       no = 0;

            while( rs.next()) {
                m_prices.add( new PriceListProduct( rs.getInt( 1 ),rs.getString( 2 ),rs.getString( 3 ),rs.getString( 4 ),rs.getString( 5 ),rs.getString( 6 ),rs.getString( 7 ),rs.getString( 8 ),rs.getBigDecimal( 9 ),rs.getString( 10 ),rs.getString( 11 )));

                // if not all records limit list

                if( !allRecords && (++no > MAX_LINES) ) {
                    m_notAllPrices = true;

                    break;
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"load",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        log.fine( "load #" + m_prices.size() + ", Search=" + m_searchInfo );
    }    // load

    /**
     * Descripción de Método
     *
     *
     * @param searchString
     * @param M_Product_Category_ID
     * @param allRecords
     */

    private void loadProducts( String searchString,int M_Product_Category_ID,boolean allRecords,String inStock,String order,int minPrice,int maxPrice ) {

        // Set Search String

        log.finer( "loadProducts - M_PriceList_Version_ID=" + m_PriceList_Version_ID + ", Search=" + searchString + ", M_Product_Category_ID=" + M_Product_Category_ID );
        m_searchInfo = "";

        if( searchString != null ) {
            m_searchInfo = searchString;
        }

        if( M_Product_Category_ID != 0 ) {
            if( m_searchInfo.length() != 0 ) {
                m_searchInfo += " - ";
            }

            m_searchInfo += MProductCategory.get( m_ctx,M_Product_Category_ID, null ).getName();
        }

        m_prices.clear();
        m_notAllPrices = false;

        //

        String sql = "SELECT p.M_Product_ID, p.Value, p.Name, p.Description, p.Help, p.DocumentNote, p.ImageURL, p.DescriptionURL, pp.Pricelist, uom.Name, uom.UOMSymbol"                                                                                                                                                                                        // 9..11
                     + " FROM M_ProductPrice pp"
                     + " INNER JOIN M_Product p ON (pp.M_Product_ID = p.M_Product_ID AND p.IsActive = 'Y' AND p.IsSold=  'Y')"
                     + " INNER JOIN C_UOM uom ON (p.C_UOM_ID = uom.C_UOM_ID)"
                     + " WHERE pp.M_PriceList_Version_ID = ?"    // #1
                     + " AND pp.PriceStd > 0 "
                     + " AND p.IsSelfService='Y'";       
        
        if( searchString != null ) {
            sql += " AND (UPPER(p.Value) LIKE ? ";    // #2
            sql += " OR UPPER(p.Name) LIKE ? ";    // #3
            sql += " OR UPPER(p.Description) LIKE ? ) ";    // #4
        }

        if( M_Product_Category_ID != 0 ) {
            sql += " AND p.M_Product_Category_ID = ? ";    // #5
        }

		if( minPrice >= 0 ) {
			sql += " AND pp.Pricelist > ?";    //  #6
		}
		
		if( maxPrice > 0) {
			sql += " AND pp.Pricelist < ?";    //  #7
		}
        
        if( !allRecords && (searchString == null) && (M_Product_Category_ID == 0) ) {
            sql += " AND p.IsWebStoreFeatured = 'Y' ";
            m_notAllPrices = true;
        }

		if( order != null ) {
			if (order.compareTo("Price") == 0 ) {
				sql += "ORDER BY pp.Pricelist, p.M_Product_Category_ID, p.Value, p.Name";
			}
			else {
				sql += "ORDER BY p.M_Product_Category_ID, p.Name, p.Value, pp.Pricelist";
			}
		}

        // log.fine("loadProducts - " + sql);

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );

            int index = 1;

            pstmt.setInt( index++,m_PriceList_Version_ID );

            if( searchString != null ) {
                pstmt.setString( index++,searchString );
                pstmt.setString( index++,searchString );
                pstmt.setString( index++,searchString );
            }

            if( M_Product_Category_ID != 0 ) {
                pstmt.setInt( index++,M_Product_Category_ID );
            }

            if( minPrice >= 0 ) {
                pstmt.setInt( index++,minPrice );
            }

            if( maxPrice > 0 ) {
                pstmt.setInt( index++,maxPrice );
            }
            
            ResultSet rs = pstmt.executeQuery();
            int       no = 0;

            while( rs.next()) {
            	PriceListProduct product = new PriceListProduct( rs.getInt( 1 ),rs.getString( 2 ),rs.getString( 3 ),rs.getString( 4 ),rs.getString( 5 ),rs.getString( 6 ),rs.getString( 7 ),rs.getString( 8 ),rs.getBigDecimal( 9 ),rs.getString( 10 ),rs.getString( 11 ) );
            	
            	BigDecimal available = product.getDisponible();
            	
        		BigDecimal zero = new BigDecimal("0");
            	
            	if( inStock.compareTo("inStock") == 0 ) {
					if( available.compareTo(zero) == 1 ) {
						// si el stock es mayor que cero o sea, si hay de verdad
						m_prices.add( product );
						
						if( !allRecords && (++no > MAX_LINES) ) {
						   m_notAllPrices = true;
						   
						   break;
						}
					}
				}
				else if( inStock.compareTo("available") == 0 ) {
					if( available.compareTo(zero) != 0 ) {
						// si el stock es distinto de cero o sea, si hay disponibilidad
						m_prices.add( product );
						
						if( !allRecords && (++no > MAX_LINES) ) {
						   m_notAllPrices = true;

						   break;
						}
					}
				}
				else {
					// if not all records limit list
					m_prices.add( product );
					
	                if( !allRecords && (++no > MAX_LINES) ) {
	                    m_notAllPrices = true;

	                    break;
	                }
				}
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"load",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        log.fine( "load #" + m_prices.size() + ", Search=" + m_searchInfo );
    }    // load
    
    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "PriceList[" );

        sb.append( m_prices.size()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getPriceCount() {
        return m_prices.size();
    }    // getPriceCount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isNoLines() {
        return getPriceCount() == 0;
    }    // getPriceCount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isNotAllPrices() {
        return m_notAllPrices;
    }    // isNotAllPrices

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ArrayList getPrices() {
        return m_prices;
    }    // getPrices

    /**
     * Descripción de Método
     *
     *
     * @param M_Product_ID
     *
     * @return
     */

    public PriceListProduct getPriceListProduct( int M_Product_ID ) {
        for( int i = 0;i < m_prices.size();i++ ) {
            PriceListProduct plp = ( PriceListProduct )m_prices.get( i );

            if( plp.getId() == M_Product_ID ) {
                return plp;
            }
        }

        return null;
    }    // getPriceListProduct

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getSearchInfo() {
        return m_searchInfo;
    }    // getSearchInfo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getName() {
        return m_name;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDescription() {
        return m_description;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getCurrency() {
        return m_currency;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getCurSymbol() {
        return m_curSymbol;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getAD_Language() {
        return m_AD_Language;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isTaxIncluded() {
        return m_taxIncluded;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getPriceList_ID() {
        return m_PriceList_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getPriceList_Version_ID() {
        return m_PriceList_Version_ID;
    }
}    // PriceList



/*
 *  @(#)PriceList.java   22.03.06
 * 
 *  Fin del fichero PriceList.java
 *  
 *  Versión 2.0
 *
 */