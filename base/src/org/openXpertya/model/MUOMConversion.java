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

import java.awt.Point;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Ini;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MUOMConversion extends X_C_UOM_Conversion {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_UOM_ID
     * @param C_UOM_To_ID
     * @param qty
     *
     * @return
     */

    static public BigDecimal convert( Properties ctx,int C_UOM_ID,int C_UOM_To_ID,BigDecimal qty ) {
        if( (qty == null) || qty.equals( Env.ZERO ) || (C_UOM_ID == C_UOM_To_ID) ) {
            return qty;
        }

        BigDecimal retValue = getRate( ctx,C_UOM_ID,C_UOM_To_ID );

        if( retValue != null ) {
            MUOM uom = MUOM.get( ctx,C_UOM_To_ID );

            if( uom != null ) {
                return uom.round( retValue.multiply( qty ),true );
            }

            return retValue.multiply( qty );
        }

        return null;
    }    // convert

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_UOM_ID
     * @param C_UOM_To_ID
     *
     * @return
     */

    static public BigDecimal getRate( Properties ctx,int C_UOM_ID,int C_UOM_To_ID ) {

        // nothing to do

        if( C_UOM_ID == C_UOM_To_ID ) {
            return Env.ONE;
        }

        //

        Point p = new Point( C_UOM_ID,C_UOM_To_ID );

        // get conversion

        BigDecimal retValue = getRate( ctx,p );

        return retValue;
    }    // convert

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_UOM_ID
     * @param qty
     *
     * @return
     */

    static public int convertToMinutes( Properties ctx,int C_UOM_ID,BigDecimal qty ) {
        if( qty == null ) {
            return 0;
        }

        int C_UOM_To_ID = MUOM.getMinute_UOM_ID( ctx );

        if( C_UOM_ID == C_UOM_To_ID ) {
            return qty.intValue();
        }

        //

        BigDecimal result = convert( ctx,C_UOM_ID,C_UOM_To_ID,qty );

        if( result == null ) {
            return 0;
        }

        return result.intValue();
    }    // convert

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param startDate
     * @param C_UOM_ID
     * @param qty
     *
     * @return
     */

    static public Timestamp getEndDate( Properties ctx,Timestamp startDate,int C_UOM_ID,BigDecimal qty ) {
        GregorianCalendar endDate = new GregorianCalendar();

        endDate.setTime( startDate );

        //

        int minutes = MUOMConversion.convertToMinutes( ctx,C_UOM_ID,qty );

        endDate.add( Calendar.MINUTE,minutes );

        //

        Timestamp retValue = new Timestamp( endDate.getTimeInMillis());

        // log.config( "TimeUtil.getEndDate", "Start=" + startDate
        // + ", Qty=" + qty + ", End=" + retValue);

        return retValue;
    }    // startDate

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param p
     *
     * @return
     */

    static private BigDecimal getRate( Properties ctx,Point p ) {
        BigDecimal retValue = null;

        if( Ini.isClient()) {
            if( s_conversions == null ) {
                createRates( ctx );
            }

            retValue = ( BigDecimal )s_conversions.get( p );
        } else {
            retValue = getRate( p.x,p.y );
        }

        if( retValue != null ) {
            return retValue;
        }

        // try to derive

        return deriveRate( ctx,p.x,p.y );
    }    // getConversion

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     */

    private static void createRates( Properties ctx ) {
        s_conversions = new CCache( "C_UOMConversion",20 );

        //

        String sql = MRole.getDefault( ctx,false ).addAccessSQL( "SELECT C_UOM_ID, C_UOM_To_ID, MultiplyRate, DivideRate " + "FROM C_UOM_Conversion " + "WHERE IsActive='Y' AND M_Product_ID IS NULL","C_UOM_Conversion",MRole.SQL_NOTQUALIFIED,MRole.SQL_RO );

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );
            ResultSet         rs    = pstmt.executeQuery();

            while( rs.next()) {
                Point      p  = new Point( rs.getInt( 1 ),rs.getInt( 2 ));
                BigDecimal mr = rs.getBigDecimal( 3 );
                BigDecimal dr = rs.getBigDecimal( 4 );

                if( mr != null ) {
                    s_conversions.put( p,mr );
                }

                // reverse

                if( (dr == null) && (mr != null) ) {
                    dr = Env.ONE.divide( mr,BigDecimal.ROUND_HALF_UP );
                }

                if( dr != null ) {
                    s_conversions.put( new Point( p.y,p.x ),dr );
                }
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            s_log.log( Level.SEVERE,"UOMConversion.createRates",e );
        }
    }    // createRatess

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_UOM_ID
     * @param C_UOM_To_ID
     *
     * @return
     */

    public static BigDecimal deriveRate( Properties ctx,int C_UOM_ID,int C_UOM_To_ID ) {
        if( C_UOM_ID == C_UOM_To_ID ) {
            return Env.ONE;
        }

        // get Info

        MUOM from = MUOM.get( ctx,C_UOM_ID );
        MUOM to   = MUOM.get( ctx,C_UOM_To_ID );

        if( (from == null) || (to == null) ) {
            return null;
        }

        // Time - Minute

        if( from.isMinute()) {
            if( to.isHour()) {
                return new BigDecimal( 1.0 / 60.0 );
            }

            if( to.isDay()) {
                return new BigDecimal( 1.0 / 1440.0 );      // 24 * 60
            }

            if( to.isWorkDay()) {
                return new BigDecimal( 1.0 / 480.0 );       // 8 * 60
            }

            if( to.isWeek()) {
                return new BigDecimal( 1.0 / 10080.0 );     // 7 * 24 * 60
            }

            if( to.isMonth()) {
                return new BigDecimal( 1.0 / 43200.0 );     // 30 * 24 * 60
            }

            if( to.isWorkMonth()) {
                return new BigDecimal( 1.0 / 9600.0 );      // 4 * 5 * 8 * 60
            }

            if( to.isYear()) {
                return new BigDecimal( 1.0 / 525600.0 );    // 365 * 24 * 60
            }
        }

        // Time - Hour

        if( from.isHour()) {
            if( to.isMinute()) {
                return new BigDecimal( 60.0 );
            }

            if( to.isDay()) {
                return new BigDecimal( 1.0 / 24.0 );
            }

            if( to.isWorkDay()) {
                return new BigDecimal( 1.0 / 8.0 );
            }

            if( to.isWeek()) {
                return new BigDecimal( 1.0 / 168.0 );     // 7 * 24
            }

            if( to.isMonth()) {
                return new BigDecimal( 1.0 / 720.0 );     // 30 * 24
            }

            if( to.isWorkMonth()) {
                return new BigDecimal( 1.0 / 160.0 );     // 4 * 5 * 8
            }

            if( to.isYear()) {
                return new BigDecimal( 1.0 / 8760.0 );    // 365 * 24
            }
        }

        // Time - Day

        if( from.isDay()) {
            if( to.isMinute()) {
                return new BigDecimal( 1440.0 );         // 24 * 60
            }

            if( to.isHour()) {
                return new BigDecimal( 24.0 );
            }

            if( to.isWorkDay()) {
                return new BigDecimal( 3.0 );            // 24 / 8
            }

            if( to.isWeek()) {
                return new BigDecimal( 1.0 / 7.0 );      // 7
            }

            if( to.isMonth()) {
                return new BigDecimal( 1.0 / 30.0 );     // 30
            }

            if( to.isWorkMonth()) {
                return new BigDecimal( 1.0 / 20.0 );     // 4 * 5
            }

            if( to.isYear()) {
                return new BigDecimal( 1.0 / 365.0 );    // 365
            }
        }

        // Time - WorkDay

        if( from.isWorkDay()) {
            if( to.isMinute()) {
                return new BigDecimal( 480.0 );          // 8 * 60
            }

            if( to.isHour()) {
                return new BigDecimal( 8.0 );            // 8
            }

            if( to.isDay()) {
                return new BigDecimal( 1.0 / 3.0 );      // 24 / 8
            }

            if( to.isWeek()) {
                return new BigDecimal( 1.0 / 5 );        // 5
            }

            if( to.isMonth()) {
                return new BigDecimal( 1.0 / 20.0 );     // 4 * 5
            }

            if( to.isWorkMonth()) {
                return new BigDecimal( 1.0 / 20.0 );     // 4 * 5
            }

            if( to.isYear()) {
                return new BigDecimal( 1.0 / 240.0 );    // 4 * 5 * 12
            }
        }

        // Time - Week

        if( from.isWeek()) {
            if( to.isMinute()) {
                return new BigDecimal( 10080.0 );       // 7 * 24 * 60
            }

            if( to.isHour()) {
                return new BigDecimal( 168.0 );         // 7 * 24
            }

            if( to.isDay()) {
                return new BigDecimal( 7.0 );
            }

            if( to.isWorkDay()) {
                return new BigDecimal( 5.0 );
            }

            if( to.isMonth()) {
                return new BigDecimal( 1.0 / 4.0 );     // 4
            }

            if( to.isWorkMonth()) {
                return new BigDecimal( 1.0 / 4.0 );     // 4
            }

            if( to.isYear()) {
                return new BigDecimal( 1.0 / 50.0 );    // 50
            }
        }

        // Time - Month

        if( from.isMonth()) {
            if( to.isMinute()) {
                return new BigDecimal( 43200.0 );       // 30 * 24 * 60
            }

            if( to.isHour()) {
                return new BigDecimal( 720.0 );         // 30 * 24
            }

            if( to.isDay()) {
                return new BigDecimal( 30.0 );          // 30
            }

            if( to.isWorkDay()) {
                return new BigDecimal( 20.0 );          // 4 * 5
            }

            if( to.isWeek()) {
                return new BigDecimal( 4.0 );           // 4
            }

            if( to.isWorkMonth()) {
                return new BigDecimal( 1.5 );           // 30 / 20
            }

            if( to.isYear()) {
                return new BigDecimal( 1.0 / 12.0 );    // 12
            }
        }

        // Time - WorkMonth

        if( from.isWorkMonth()) {
            if( to.isMinute()) {
                return new BigDecimal( 9600.0 );         // 4 * 5 * 8 * 60
            }

            if( to.isHour()) {
                return new BigDecimal( 160.0 );          // 4 * 5 * 8
            }

            if( to.isDay()) {
                return new BigDecimal( 20.0 );           // 4 * 5
            }

            if( to.isWorkDay()) {
                return new BigDecimal( 20.0 );           // 4 * 5
            }

            if( to.isWeek()) {
                return new BigDecimal( 4.0 );            // 4
            }

            if( to.isMonth()) {
                return new BigDecimal( 20.0 / 30.0 );    // 20 / 30
            }

            if( to.isYear()) {
                return new BigDecimal( 1.0 / 12.0 );     // 12
            }
        }

        // Time - Year

        if( from.isYear()) {
            if( to.isMinute()) {
                return new BigDecimal( 518400.0 );    // 12 * 30 * 24 * 60
            }

            if( to.isHour()) {
                return new BigDecimal( 8640.0 );      // 12 * 30 * 24
            }

            if( to.isDay()) {
                return new BigDecimal( 365.0 );       // 365
            }

            if( to.isWorkDay()) {
                return new BigDecimal( 240.0 );       // 12 * 4 * 5
            }

            if( to.isWeek()) {
                return new BigDecimal( 50.0 );        // 52
            }

            if( to.isMonth()) {
                return new BigDecimal( 12.0 );        // 12
            }

            if( to.isWorkMonth()) {
                return new BigDecimal( 12.0 );        // 12
            }
        }

        //

        return null;
    }    // deriveRate

    /**
     * Descripción de Método
     *
     *
     * @param C_UOM_ID
     * @param C_UOM_To_ID
     *
     * @return
     */

    public static BigDecimal getRate( int C_UOM_ID,int C_UOM_To_ID ) {
        return convert( C_UOM_ID,C_UOM_To_ID,GETRATE,false );
    }    // getConversion

    /**
     * Descripción de Método
     *
     *
     * @param C_UOM_From_ID
     * @param C_UOM_To_ID
     * @param qty
     * @param StdPrecision
     *
     * @return
     */

    public static BigDecimal convert( int C_UOM_From_ID,int C_UOM_To_ID,BigDecimal qty,boolean StdPrecision ) {

        // Nothing to do

        if( (qty == null) || qty.equals( Env.ZERO ) || (C_UOM_From_ID == C_UOM_To_ID) ) {
            return qty;
        }

        //

        BigDecimal retValue  = null;
        int        precision = 2;
        String     sql       = "SELECT c.MultiplyRate, uomTo.StdPrecision, uomTo.CostingPrecision " + "FROM C_UOM_Conversion c" + " INNER JOIN C_UOM uomTo ON (c.C_UOM_TO_ID=uomTo.C_UOM_ID) " + "WHERE c.IsActive='Y' AND c.C_UOM_ID=? AND c.C_UOM_TO_ID=? "    // #1/2
                               + " AND c.M_Product_ID IS NULL" + "ORDER BY c.AD_Client_ID DESC, c.AD_Org_ID DESC";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,C_UOM_From_ID );
            pstmt.setInt( 2,C_UOM_To_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue  = rs.getBigDecimal( 1 );
                precision = rs.getInt( StdPrecision
                                       ?2
                                       :3 );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"convert",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        if( retValue == null ) {
            s_log.info( "convert - NOT found - FromUOM=" + C_UOM_From_ID + ", ToUOM=" + C_UOM_To_ID );

            return null;
        }

        // Just get Rate

        if( GETRATE.equals( qty )) {
            return retValue;
        }

        // Calculate & Scale

        retValue = retValue.multiply( qty );

        if( retValue.scale() > precision ) {
            retValue = retValue.setScale( precision,BigDecimal.ROUND_HALF_UP );
        }

        return retValue;
    }    // convert

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_Product_ID
     * @param C_UOM_To_ID
     * @param qtyPrice
     *
     * @return
     */

    static public BigDecimal convertProductTo( Properties ctx,int M_Product_ID,int C_UOM_To_ID,BigDecimal qtyPrice ) {
        if( (qtyPrice == null) || qtyPrice.equals( Env.ZERO )) {
            return qtyPrice;
        }

        BigDecimal retValue = getProductRateTo( ctx,M_Product_ID,C_UOM_To_ID );

        if( retValue != null ) {
            if( Env.ONE.compareTo( retValue ) == 0 ) {
                return qtyPrice;
            }

            MUOM uom = MUOM.get( ctx,C_UOM_To_ID );

            if( uom != null ) {
                return uom.round( retValue.multiply( qtyPrice ),true );
            }

            return retValue.multiply( qtyPrice );
        }

        return null;
    }    // convertProductTo

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_Product_ID
     * @param C_UOM_To_ID
     *
     * @return
     */

    static public BigDecimal getProductRateTo( Properties ctx,int M_Product_ID,int C_UOM_To_ID ) {
        MUOMConversion[] rates = getProductConversions( ctx,M_Product_ID );

        if( rates.length == 0 ) {
            s_log.fine( "getProductRateTo - none found" );

            return null;
        }

        for( int i = 0;i < rates.length;i++ ) {
            MUOMConversion rate = rates[ i ];

            if( rate.getC_UOM_To_ID() == C_UOM_To_ID ) {
                return rate.getMultiplyRate();
            }
        }

        s_log.fine( "getProductRateTo - none applied" );

        return null;
    }    // getProductRateTo

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_Product_ID
     * @param C_UOM_To_ID
     * @param qtyPrice
     *
     * @return
     */

    static public BigDecimal convertProductFrom( Properties ctx,int M_Product_ID,int C_UOM_To_ID,BigDecimal qtyPrice ) {

        // No conversion

        if( (qtyPrice == null) || qtyPrice.equals( Env.ZERO ) || (C_UOM_To_ID == 0) || (M_Product_ID == 0) ) {
            s_log.fine( "No Conversion - QtyPrice=" + qtyPrice );

            return qtyPrice;
        }

        BigDecimal retValue = getProductRateFrom( ctx,M_Product_ID,C_UOM_To_ID );

        if( retValue != null ) {
            if( Env.ONE.compareTo( retValue ) == 0 ) {
                return qtyPrice;
            }

            MUOM uom = MUOM.get( ctx,C_UOM_To_ID );

            if( uom != null ) {
                return uom.round( retValue.multiply( qtyPrice ),true );
            }

            return retValue.multiply( qtyPrice );
        }

        s_log.fine( "No Rate M_Product_ID=" + M_Product_ID );

        return null;
    }    // convertProductFrom

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_Product_ID
     * @param C_UOM_To_ID
     *
     * @return
     */

    static public BigDecimal getProductRateFrom( Properties ctx,int M_Product_ID,int C_UOM_To_ID ) {
        MUOMConversion[] rates = getProductConversions( ctx,M_Product_ID );

        if( rates.length == 0 ) {
            s_log.fine( "getProductRateFrom - none found" );

            return null;
        }

        for( int i = 0;i < rates.length;i++ ) {
            MUOMConversion rate = rates[ i ];

            if( rate.getC_UOM_To_ID() == C_UOM_To_ID ) {
                return rate.getDivideRate();
            }
        }

        s_log.fine( "None applied" );

        return null;
    }    // getProductRateFrom

    static public MUOMConversion[] getProductConversions( Properties ctx,int M_Product_ID) {
    	return getProductConversions(ctx, M_Product_ID, false);
    }
    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_Product_ID
     *
     * @return
     */

    static public MUOMConversion[] getProductConversions( Properties ctx,int M_Product_ID, boolean reload) {
        Integer          key    = new Integer( M_Product_ID );
        MUOMConversion[] result = ( MUOMConversion[] )s_conversionProduct.get( key );

        if( result != null && !reload ) {
            return result;
        }

        ArrayList list = new ArrayList();

        // Add default conversion

        MUOMConversion defRate = new MUOMConversion( MProduct.get( ctx,M_Product_ID ));

        list.add( defRate );

        //

        String sql = "SELECT * FROM C_UOM_Conversion c " + "WHERE c.M_Product_ID=?" + " AND EXISTS (SELECT * FROM M_Product p " + "WHERE c.M_Product_ID=p.M_Product_ID AND c.C_UOM_ID=p.C_UOM_ID)" + " AND c.IsActive='Y'";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,M_Product_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MUOMConversion( ctx,rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"getProductConversions",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        // Convert & save

        result = new MUOMConversion[ list.size()];
        list.toArray( result );
        s_conversionProduct.put( key,result );
        s_log.fine( "getProductConversions - M_Product_ID=" + M_Product_ID + " #" + result.length );

        return result;
    }    // getProductConversions
    
    /**
     * Get uom conversions of client
     * @param ctx
     * @param trxName
     * @return
     */
    public static List<MUOMConversion> getOfClient(Properties ctx,String trxName){
    	//script sql
    	String sql = "SELECT * FROM c_uom_conversion WHERE ad_client_id = ?"; 
    		
    	List<MUOMConversion> list = new ArrayList<MUOMConversion>();
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
			ps = DB.prepareStatement(sql, trxName);
			//set ad_client
			ps.setInt(1, Env.getAD_Client_ID(ctx));
			rs = ps.executeQuery();
			
			while(rs.next()){
				list.add(new MUOMConversion(ctx,rs,trxName));				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				ps.close();
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return list;
    }

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MUOMConversion.class );

    /** Descripción de Campos */

    private static BigDecimal GETRATE = new BigDecimal( 123.456 );

    /** Descripción de Campos */

    private static CCache s_conversions = null;

    /** Descripción de Campos */

    private static CCache s_conversionProduct = new CCache( "C_UOMConversion",20 );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_UOM_Conversion_ID
     * @param trxName
     */

    public MUOMConversion( Properties ctx,int C_UOM_Conversion_ID,String trxName ) {
        super( ctx,C_UOM_Conversion_ID,trxName );
    }    // MUOMConversion

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MUOMConversion( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MUOMConversion

    /**
     * Constructor de la clase ...
     *
     *
     * @param parent
     */

    public MUOMConversion( MUOM parent ) {
        this( parent.getCtx(),0,parent.get_TrxName());
        setClientOrg( parent );
        setC_UOM_ID( parent.getC_UOM_ID());
        setM_Product_ID( 0 );

        //

        setC_UOM_To_ID( parent.getC_UOM_ID());
        setMultiplyRate( Env.ONE );
        setDivideRate( Env.ONE );
    }    // MUOMConversion

    /**
     * Constructor de la clase ...
     *
     *
     * @param parent
     */

    public MUOMConversion( MProduct parent ) {
        this( parent.getCtx(),0,parent.get_TrxName());
        setClientOrg( parent );
        setC_UOM_ID( parent.getC_UOM_ID());
        setM_Product_ID( parent.getM_Product_ID());

        //

        setC_UOM_To_ID( parent.getC_UOM_ID());
        setMultiplyRate( Env.ONE );
        setDivideRate( Env.ONE );
    }    // MUOMConversion

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {

        // From - To is the same

        if( getC_UOM_ID() == getC_UOM_To_ID()) {
            log.saveError( "Error",Msg.parseTranslation( getCtx(),"@C_UOM_ID@ = @C_UOM_ID@" ));

            return false;
        }

        // Nothing to convert

        if( getMultiplyRate().compareTo( Env.ZERO ) <= 0 ) {
            log.saveError( "Error",Msg.parseTranslation( getCtx(),"@MultiplyRate@ <= 0" ));

            return false;
        }

        // Enforce Product UOM

        if( (getM_Product_ID() != 0) && ( newRecord || is_ValueChanged( "M_Product_ID" ))) {
            MProduct product = MProduct.get( getCtx(),getM_Product_ID());

            if( product.getC_UOM_ID() != getC_UOM_ID()) {
                MUOM uom = MUOM.get( getCtx(),product.getC_UOM_ID());

                log.saveError( "ProductUOMConversionUOMError",uom.getName());

                return false;
            }
        }

        // The Product UoM needs to be the smallest UoM - Multiplier  must be > 0

        if( (getM_Product_ID() != 0) && (getDivideRate().compareTo( Env.ONE ) < 0) ) {
            log.saveError( "ProductUOMConversionRateError","" );

            return false;
        }

        return true;
    }    // beforeSave

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MUOMConversion[" );

        sb.append( getID()).append( "-C_UOM_ID=" ).append( getC_UOM_ID()).append( ",C_UOM_To_ID=" ).append( getC_UOM_To_ID()).append( ",M_Product_ID=" ).append( getM_Product_ID()).append( "-Multiply=" ).append( getMultiplyRate()).append( "/Divide=" ).append( getDivideRate()).append( "]" );

        return sb.toString();
    }    // toString
}    // UOMConversion



/*
 *  @(#)MUOMConversion.java   02.07.07
 * 
 *  Fin del fichero MUOMConversion.java
 *  
 *  Versión 2.2
 *
 */
