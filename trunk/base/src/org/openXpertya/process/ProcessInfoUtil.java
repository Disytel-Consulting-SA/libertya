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
import java.util.ArrayList;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Trx;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ProcessInfoUtil {

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( ProcessInfoUtil.class );

    /**
     * Descripción de Método
     *
     *
     * @param pi
     */

    public static void setSummaryFromDB( ProcessInfo pi ) {

        // s_log.fine("setSummaryFromDB - AD_PInstance_ID=" + pi.getAD_PInstance_ID());
        //

        int sleepTime = 2000;    // 2 secomds
        int noRetry   = 5;       // 10 seconds total

        //

        String SQL = "SELECT Result, ErrorMsg FROM AD_PInstance " + "WHERE AD_PInstance_ID=?" + " AND Result IS NOT NULL";

        try {
            PreparedStatement pstmt = DB.prepareStatement( SQL,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY );

            for( int noTry = 0;noTry < noRetry;noTry++ ) {
                pstmt.setInt( 1,pi.getAD_PInstance_ID());

                ResultSet rs = pstmt.executeQuery();

                if( rs.next()) {

                    // we have a result

                    int i = rs.getInt( 1 );

                    if( i == 1 ) {
                        pi.setSummary( Msg.getMsg( Env.getCtx(),"Success" ));
                    } else {
                        pi.setSummary( Msg.getMsg( Env.getCtx(),"Failure" ),true );
                    }

                    String Message = rs.getString( 2 );

                    rs.close();
                    pstmt.close();

                    //

                    if( Message != null ) {
                        pi.addSummary( "  (" + Msg.parseTranslation( Env.getCtx(),Message ) + ")" );
                    }

                    // s_log.fine("setSummaryFromDB - " + Message);

                    return;
                }

                rs.close();

                // sleep

                try {
                    s_log.fine( "setSummaryFromDB - sleeping" );
                    Thread.sleep( sleepTime );
                } catch( InterruptedException ie ) {
                    s_log.log( Level.SEVERE,"setSummaryFromDB - Sleep Thread",ie );
                }
            }

            pstmt.close();
        } catch( SQLException e ) {
            s_log.log( Level.SEVERE,"setSummaryFromDB",e );
            pi.setSummary( e.getLocalizedMessage(),true );

            return;
        }

        pi.setSummary( Msg.getMsg( Env.getCtx(),"Timeout" ),true );
    }    // setSummaryFromDB

    /**
     * Descripción de Método
     *
     *
     * @param pi
     */

    public static void setLogFromDB( ProcessInfo pi ) {

        // s_log.fine("setLogFromDB - AD_PInstance_ID=" + pi.getAD_PInstance_ID());

        String sql = "SELECT Log_ID, P_ID, P_Date, P_Number, P_Msg " + "FROM AD_PInstance_Log " + "WHERE AD_PInstance_ID=? " + "ORDER BY Log_ID";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,pi.getAD_PInstance_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {

                // int Log_ID, int P_ID, Timestamp P_Date, BigDecimal P_Number, String P_Msg

                pi.addLog( rs.getInt( 1 ),rs.getInt( 2 ),rs.getTimestamp( 3 ),rs.getBigDecimal( 4 ),rs.getString( 5 ));
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            s_log.log( Level.SEVERE,"setLogFromDB",e );
        }
    }    // getLogFromDB

    /**
     * Descripción de Método
     *
     *
     * @param pi
     */

    public static void saveLogToDB( ProcessInfo pi ) {
        ProcessInfoLog[] logs = pi.getLogs();

        if( (logs == null) || (logs.length == 0) ) {

            // s_log.fine("saveLogToDB - No Log");

            return;
        }

        if( pi.getAD_PInstance_ID() == 0 ) {

            // s_log.log(Level.WARNING,"saveLogToDB - not saved - AD_PInstance_ID==0");

            return;
        }

        for( int i = 0;i < logs.length;i++ ) {
            StringBuffer sql = new StringBuffer( "INSERT INTO AD_PInstance_Log " + "(AD_PInstance_ID, Log_ID, P_Date, P_ID, P_Number, P_Msg)" + " VALUES (" );

            sql.append( pi.getAD_PInstance_ID()).append( "," ).append( logs[ i ].getLog_ID()).append( "," );

            if( logs[ i ].getP_Date() == null ) {
                sql.append( "NULL," );
            } else {
                sql.append( DB.TO_DATE( logs[ i ].getP_Date(),false )).append( "," );
            }

            if( logs[ i ].getP_ID() == 0 ) {
                sql.append( "NULL," );
            } else {
                sql.append( logs[ i ].getP_ID()).append( "," );
            }

            if( logs[ i ].getP_Number() == null ) {
                sql.append( "NULL," );
            } else {
                sql.append( logs[ i ].getP_Number()).append( "," );
            }

            if( logs[ i ].getP_Msg() == null ) {
                sql.append( "NULL)" );
            } else {
                sql.append( DB.TO_STRING( logs[ i ].getP_Msg(),2000 )).append( ")" );
            }

            //

            DB.executeUpdate( sql.toString());
        }

        pi.setLogList( null );    // otherwise log entries are twice
    }                             // saveLogToDB

	/**
	 * Incorpora un elemento s al array array.  Omite duplicados
	 * @param array
	 * @param s
	 * @return
	 */
	public static ProcessInfoParameter[] addToArray(ProcessInfoParameter[] array, ProcessInfoParameter s)
	{
		/* Ya existe el parámetro en el array? En ese caso omitir incorporación. */
		if (array != null) {
			for (ProcessInfoParameter param : array) 
				if (param.getParameterName().equals(s.getParameterName()))
					return array;
		}
		
		/* Ya tenía parametros?  Si es null se debe a que no contenia parametros */
		ProcessInfoParameter[] ans = new ProcessInfoParameter[array==null ? 1 : array.length + 1];
		
		/* Si hay un único parametro, asignarlo a ans y devolverlo */
		if (ans.length == 1)
		{
			ans[0] = s;
			return ans;
		}
			
		/*  En caso contrario, concatear este ultimo */
		System.arraycopy(array, 0, ans, 0, array.length);
		ans[ans.length - 1] = s;
		return ans;
	}
    
    
    /**
     * Descripción de Método
     *
     *
     * @param pi
     */
    public static void setParameterFromDB( ProcessInfo pi, String trxName ) {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT p.ParameterName,"                                   // 1
                         + " p.P_String,p.P_String_To, p.P_Number,p.P_Number_To,"    // 2/3 4/5
                         + " p.P_Date,p.P_Date_To, p.Info,p.Info_To, "               // 6/7 8/9
                         + " i.AD_Client_ID, i.AD_Org_ID, i.AD_User_ID "             // 10..12
                         + "FROM AD_PInstance_Para p" + " INNER JOIN AD_PInstance i ON (p.AD_PInstance_ID=i.AD_PInstance_ID) " + "WHERE p.AD_PInstance_ID=? " + "ORDER BY p.SeqNo";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql, trxName );

            pstmt.setInt( 1,pi.getAD_PInstance_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
            	//JOptionPane.showMessageDialog( null,"En setParameterFromDB"+"\n"+ "p.ParameterName="+rs.getString( 1 )+"\n"+ "AD_PInstance_para.P_String="+rs.getString( 2 )+"\n"+ "P_String_To"+rs.getString( 3 )+"\n"+ "P_Number="+rs.getString( 4 )+"\n"+ "P_Number_To="+rs.getString( 5 )+"\n"+ "p.P_Date="+rs.getString( 6 )+"\n"+ "P_Date_To="+rs.getString( 7 )+"\n"+ "p.Info="+rs.getString( 8 )+"\n"+ "p.Info_To="+rs.getString( 9 )+"\n"+ "i.AD_Client_ID="+rs.getString( 10 )+ "i.AD_Org_ID="+rs.getString( 11 )+ "i.AD_User_ID="+rs.getString( 12 ),"..", JOptionPane.INFORMATION_MESSAGE );
                String ParameterName = rs.getString( 1 );

                // String

                Object Parameter    = rs.getString( 2 );
                Object Parameter_To = rs.getString( 3 );

                // Big Decimal

                if( (Parameter == null) && (Parameter_To == null) ) {
                    Parameter    = rs.getBigDecimal( 4 );
                    Parameter_To = rs.getBigDecimal( 5 );
                }

                // Timestamp

                if( (Parameter == null) && (Parameter_To == null) ) {
                    Parameter    = rs.getTimestamp( 6 );
                    Parameter_To = rs.getTimestamp( 7 );
                }

                // Info

                String Info    = rs.getString( 8 );
                String Info_To = rs.getString( 9 );

                //

                list.add( new ProcessInfoParameter( ParameterName,Parameter,Parameter_To,Info,Info_To ));

                //

                if( pi.getAD_Client_ID() == null ) {
                    pi.setAD_Client_ID( rs.getInt( 10 ));
                }

                if( pi.getAD_User_ID() == null ) {
                    pi.setAD_User_ID( rs.getInt( 12 ));
                }
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            s_log.log( Level.SEVERE,"getParameter",e );
        }

        //

        ProcessInfoParameter[] pars = new ProcessInfoParameter[ list.size()];

        list.toArray( pars );
        pi.setParameter( pars );
    }    // setParameterFromDB
}    // ProcessInfoUtil



/*
 *  @(#)ProcessInfoUtil.java   25.03.06
 * 
 *  Fin del fichero ProcessInfoUtil.java
 *  
 *  Versión 2.2
 *
 */
