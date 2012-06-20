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
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.MClient;
import org.openXpertya.model.MSequence;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class SequenceCheck extends SvrProcess {

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( SequenceCheck.class );

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {}    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws java.lang.Exception
     */

    protected String doIt() throws java.lang.Exception {
        log.info( "" );

        //

        checkTableSequences( Env.getCtx(),this );
        checkTableID( Env.getCtx(),this );
        checkClientSequences( Env.getCtx(),this );

        return "Sequence Check";
    }    // doIt

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     */

    public static void validate( Properties ctx ) {
        try {
            checkTableSequences( ctx,null );
            checkTableID( ctx,null );
            checkClientSequences( ctx,null );
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"validate",e );
        }
    }    // validate

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param sp
     */

    private static void checkTableSequences( Properties ctx,SvrProcess sp ) {
        String sql = "SELECT TableName " + "FROM AD_Table t " + "WHERE IsActive='Y' AND IsView='N'" + " AND NOT EXISTS (SELECT * FROM AD_Sequence s " + "WHERE UPPER(s.Name)=UPPER(t.TableName) AND s.IsTableID='Y')";
        PreparedStatement pstmt = null;

        String trxName = null;
        if (sp!=null)
        	trxName = sp.get_TrxName();
        
        try {
            pstmt = DB.prepareStatement( sql, trxName);

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                String tableName = rs.getString( 1 );

                if( MSequence.createTableSequence( ctx,tableName, trxName)) {
                    if( sp != null ) {
                        sp.addLog( 0,null,null,tableName );
                    } else {
                        s_log.fine( tableName );
                    }
                } else {
                    rs.close();

                    throw new Exception( "Error creating Table Sequence for " + tableName );
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        // Sync Table Name case

        sql = "UPDATE AD_Sequence s " + "SET Name = (SELECT TableName FROM AD_Table t " + "WHERE t.IsView='N' AND UPPER(s.Name)=UPPER(t.TableName)) " + "WHERE s.IsTableID='Y'" + " AND EXISTS (SELECT * FROM AD_Table t " + "WHERE t.IsActive='Y' AND t.IsView='N'" + " AND UPPER(s.Name)=UPPER(t.TableName) AND s.Name<>t.TableName)";

        int no = DB.executeUpdate( sql, trxName );

        if( no != 0 ) {
            if( sp != null ) {
                sp.addLog( 0,null,null,"SyncName #" + no );
            } else {
                s_log.fine( "Sync #" + no );
            }
        }
    }    // checkTableSequences

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param sp
     */

    private static void checkTableID( Properties ctx,SvrProcess sp ) {
    	
        String trxName = null;
        if (sp!=null)
        	trxName = sp.get_TrxName();
    	
    	
        int IDRangeEnd = DB.getSQLValue( trxName ,"SELECT IDRangeEnd FROM AD_System" );

        if( IDRangeEnd <= 0 ) {
            IDRangeEnd = DB.getSQLValue( trxName, "SELECT MIN(IDRangeStart)-1 FROM AD_Replication" );
        }

        s_log.info( "IDRangeEnd = " + IDRangeEnd );

        //

        String sql = "SELECT * FROM AD_Sequence " + "WHERE IsTableID='Y' " + "ORDER BY Name";
        int               counter = 0;
        PreparedStatement pstmt   = null;

        if( sp != null ) {
            trxName = sp.get_TrxName();
        }

        try {
            pstmt = DB.prepareStatement( sql, trxName );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MSequence seq    = new MSequence( ctx,rs,trxName );
                int       old    = seq.getCurrentNext();
                int       oldSys = seq.getCurrentNextSys();

                //Modificado para que al validar la secuencia use las secuencias de la BD.
                if( seq.validateSequence()) {
                    if( seq.getCurrentNext() != old ) {
                        String msg = seq.getName() + " ID  " + old + " -> " + seq.getCurrentNext();

                        if( sp != null ) {
                            sp.addLog( 0,null,null,msg );
                        } else {
                            s_log.fine( msg );
                        }
                    }

                    if( seq.getCurrentNextSys() != oldSys ) {
                        String msg = seq.getName() + " Sys " + oldSys + " -> " + seq.getCurrentNextSys();

                        if( sp != null ) {
                            sp.addLog( 0,null,null,msg );
                        } else {
                            s_log.fine( msg );
                        }
                    }

                    if( seq.save()) {
                        counter++;
                    } else {
                        s_log.severe( "Not updated: " + seq );
                    }
                }

            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        s_log.fine( "#" + counter );
    }    // checkTableID

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param sp
     */

    private static void checkClientSequences( Properties ctx,SvrProcess sp ) {
        String trxName = null;

        if( sp != null ) {
            trxName = sp.get_TrxName();
        }

        // Sequence for DocumentNo/Value

        MClient[] clients = getAllClients( ctx, trxName );

        for( int i = 0;i < clients.length;i++ ) {
            MClient client = clients[ i ];

            if( !client.isActive()) {
                continue;
            }

            MSequence.checkClientSequences( ctx,client.getAD_Client_ID(),trxName );
        }    // for all clients
    }        // checkClientSequences
    
    
    
    
    
    
    /**
     *      Get all clients
     *      @param ctx context
     *      @return clients
     */
    public static MClient[] getAllClients(Properties ctx, String trxName) {

        ArrayList		list	= new ArrayList();
        String			sql	= "SELECT * FROM AD_Client";
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql, trxName);
            ResultSet	rs	= pstmt.executeQuery();
            while (rs.next()) {
                MClient	client	= new MClient(ctx, rs, trxName);
                list.add(client);
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            s_log.log(Level.SEVERE, "getAll", e);
        }
        try {

            if (pstmt != null) {
                pstmt.close();
            }
            pstmt	= null;
        } catch (Exception e) {
            pstmt	= null;
        }
        MClient[]	retValue	= new MClient[list.size()];
        list.toArray(retValue);
        return retValue;

    }		// get
    
    
}    // SequenceCheck



/*
 *  @(#)SequenceCheck.java   25.03.06
 * 
 *  Fin del fichero SequenceCheck.java
 *  
 *  Versión 2.2
 *
 */
