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

import org.openXpertya.util.DB;
import org.openXpertya.util.DBException;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ImportReportLine extends SvrProcess {

    /** Descripción de Campos */

    private int m_AD_Client_ID = 0;

    /** Descripción de Campos */

    private int m_PA_ReportLineSet_ID = 0;

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

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "AD_Client_ID" )) {
                m_AD_Client_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "PA_ReportLineSet_ID" )) {
                m_PA_ReportLineSet_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "DeleteOldImported" )) {
                m_deleteOldImported = "Y".equals( para[ i ].getParameter());
            } else {
                log.log( Level.SEVERE,"ImportReportLine.prepare - Unknown Parameter: " + name );
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
        String       clientCheck = " AND AD_Client_ID=" + m_AD_Client_ID;

        // ****    Prepare ****

        // Delete Old Imported

        if( m_deleteOldImported ) {
            sql = new StringBuffer( "DELETE I_ReportLine " + "WHERE I_IsImported='Y'" ).append( clientCheck );
            no = DB.executeUpdate( sql.toString());
            log.fine( "Delete Old Impored =" + no );
        }

        // Set Client, Org, IsActive, Created/Updated

        sql = new StringBuffer( "UPDATE I_ReportLine " + "SET AD_Client_ID = COALESCE (AD_Client_ID, " ).append( m_AD_Client_ID ).append( ")," + " AD_Org_ID = COALESCE (AD_Org_ID, 0)," + " IsActive = COALESCE (IsActive, 'Y')," + " Created = COALESCE (Created, SysDate)," + " CreatedBy = COALESCE (CreatedBy, 0)," + " Updated = COALESCE (Updated, SysDate)," + " UpdatedBy = COALESCE (UpdatedBy, 0)," + " I_ErrorMsg = NULL," + " I_IsImported = 'N' " + "WHERE I_IsImported<>'Y' OR I_IsImported IS NULL" );
        no = DB.executeUpdate( sql.toString());
        log.fine( "Reset=" + no );

        // ReportLineSetName (Default)

        if( m_PA_ReportLineSet_ID != 0 ) {
            sql = new StringBuffer( "UPDATE I_ReportLine i " + "SET ReportLineSetName=(SELECT Name FROM PA_ReportLineSet r" + " WHERE PA_ReportLineSet_ID=" ).append( m_PA_ReportLineSet_ID ).append( " AND i.AD_Client_ID=r.AD_Client_ID) " + "WHERE ReportLineSetName IS NULL AND PA_ReportLineSet_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
            no = DB.executeUpdate( sql.toString());
            log.fine( "Set ReportLineSetName Default=" + no );
        }

        // Set PA_ReportLineSet_ID

        sql = new StringBuffer( "UPDATE I_ReportLine i " + "SET PA_ReportLineSet_ID=(SELECT PA_ReportLineSet_ID FROM PA_ReportLineSet r" + " WHERE i.ReportLineSetName=r.Name AND i.AD_Client_ID=r.AD_Client_ID) " + "WHERE PA_ReportLineSet_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.fine( "Set PA_ReportLineSet_ID=" + no );

        //

        sql = new StringBuffer( "UPDATE I_ReportLine " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid ReportLineSet, ' " + "WHERE PA_ReportLineSet_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.config( "Invalid ReportLineSet=" + no );

        // Ignore if there is no Report Line Name or ID

        sql = new StringBuffer( "UPDATE I_ReportLine " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'Ignored=NoLineName, ' " + "WHERE PA_ReportLine_ID IS NULL AND Name IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.config( "Invalid LineName=" + no );

        // Validate ElementValue

        sql = new StringBuffer( "UPDATE I_ReportLine i " + "SET C_ElementValue_ID=(SELECT C_ElementValue_ID FROM C_ElementValue e" + " WHERE i.ElementValue=e.Value AND i.AD_Client_ID=e.AD_Client_ID) " + "WHERE C_ElementValue_ID IS NULL AND ElementValue IS NOT NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.fine( "Set C_ElementValue_ID=" + no );

        // Validate C_ElementValue_ID

        sql = new StringBuffer( "UPDATE I_ReportLine " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid ElementValue, ' " + "WHERE C_ElementValue_ID IS NULL AND LineType<>'C'"    // MReportLine.LINETYPE_Calculation
                                + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.config( "Invalid AccountType=" + no );

        // Set SeqNo

        sql = new StringBuffer( "UPDATE I_ReportLine " + "SET SeqNo=I_ReportLine_ID " + "WHERE SeqNo IS NULL" + " AND I_IsImported='N'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.fine( "Set SeqNo Default=" + no );

        // Copy/Sync from first Row of Line

        sql = new StringBuffer( "UPDATE I_ReportLine i " + "SET (Description, SeqNo, IsSummary, IsPrinted, LineType, CalculationType, AmountType, PostingType)=" + " (SELECT Description, SeqNo, IsSummary, IsPrinted, LineType, CalculationType, AmountType, PostingType" + " FROM I_ReportLine ii WHERE i.Name=ii.Name AND i.PA_ReportLineSet_ID=ii.PA_ReportLineSet_ID" + " AND ii.I_ReportLine_ID=(SELECT MIN(I_ReportLine_ID) FROM I_ReportLine iii" + " WHERE i.Name=iii.Name AND i.PA_ReportLineSet_ID=iii.PA_ReportLineSet_ID)) " + "WHERE EXISTS (SELECT *" + " FROM I_ReportLine ii WHERE i.Name=ii.Name AND i.PA_ReportLineSet_ID=ii.PA_ReportLineSet_ID" + " AND ii.I_ReportLine_ID=(SELECT MIN(I_ReportLine_ID) FROM I_ReportLine iii" + " WHERE i.Name=iii.Name AND i.PA_ReportLineSet_ID=iii.PA_ReportLineSet_ID))" + " AND I_IsImported='N'" ).append( clientCheck );    // not if previous error
        no = DB.executeUpdate( sql.toString());
        log.fine( "Sync from first Row of Line=" + no );

        // Validate IsSummary - (N) Y

        sql = new StringBuffer( "UPDATE I_ReportLine " + "SET IsSummary='N' " + "WHERE IsSummary IS NULL OR IsSummary NOT IN ('Y','N')" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.fine( "Set IsSummary Default=" + no );

        // Validate IsPrinted - (Y) N

        sql = new StringBuffer( "UPDATE I_ReportLine " + "SET IsPrinted='Y' " + "WHERE IsPrinted IS NULL OR IsPrinted NOT IN ('Y','N')" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.fine( "Set IsPrinted Default=" + no );

        // Validate Line Type - (S) C

        sql = new StringBuffer( "UPDATE I_ReportLine " + "SET LineType='S' " + "WHERE LineType IS NULL OR LineType NOT IN ('S','C')" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.fine( "Set LineType Default=" + no );

        // Validate Optional Calculation Type - A P R S

        sql = new StringBuffer( "UPDATE I_ReportLine " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid CalculationType, ' " + "WHERE CalculationType IS NOT NULL AND CalculationType NOT IN ('A','P','R','S')" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.config( "Invalid CalculationType=" + no );

        // Validate Optional Amount Type -

        sql = new StringBuffer( "UPDATE I_ReportLine " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid CalculationType, ' " + "WHERE AmountType IS NOT NULL AND UPPER(AmountType) NOT IN ('BP','CP','DP','QP', 'BY','CY','DY','QY', 'BT','CT','DT','QT')" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.config( "Invalid AmountType=" + no );

        // Validate Optional Posting Type - A B E S

        sql = new StringBuffer( "UPDATE I_ReportLine " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid CalculationType, ' " + "WHERE PostingType IS NOT NULL AND PostingType NOT IN ('A','B','E','S')" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.config( "Invalid PostingType=" + no );

        // Set PA_ReportLine_ID

        sql = new StringBuffer( "UPDATE I_ReportLine i " + "SET PA_ReportLine_ID=(SELECT PA_ReportLine_ID FROM PA_ReportLine r" + " WHERE i.Name=r.Name AND i.PA_ReportLineSet_ID=r.PA_ReportLineSet_ID AND ROWNUM=1) " + "WHERE PA_ReportLine_ID IS NULL AND PA_ReportLineSet_ID IS NOT NULL" + " AND I_IsImported='N'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.fine( "Set PA_ReportLine_ID=" + no );

        // -------------------------------------------------------------------

        int        noInsertLine = 0;
        int        noUpdateLine = 0;
        Connection conn         = DB.createConnection( false,Connection.TRANSACTION_READ_COMMITTED );

        // ****    Create Missing ReportLines

        sql = new StringBuffer( "SELECT DISTINCT PA_ReportLineSet_ID, Name " + "FROM I_ReportLine " + "WHERE I_IsImported='N' AND PA_ReportLine_ID IS NULL" + " AND I_IsImported='N'" ).append( clientCheck );

        try {

            // Insert ReportLine

            PreparedStatement pstmt_insertLine = conn.prepareStatement( "INSERT INTO PA_ReportLine " + "(PA_ReportLine_ID,PA_ReportLineSet_ID," + "AD_Client_ID,AD_Org_ID,IsActive,Created,CreatedBy,Updated,UpdatedBy," + "Name,SeqNo,IsPrinted,IsSummary,LineType)" + "SELECT ?,PA_ReportLineSet_ID," + "AD_Client_ID,AD_Org_ID,'Y',SysDate,CreatedBy,SysDate,UpdatedBy," + "Name,SeqNo,IsPrinted,IsSummary,LineType " + "FROM I_ReportLine " + "WHERE PA_ReportLineSet_ID=? AND Name=? AND ROWNUM=1"    // #2..3
                + clientCheck );
            PreparedStatement pstmt = DB.prepareStatement( sql.toString());
            ResultSet         rs    = pstmt.executeQuery();

            while( rs.next()) {
                int    PA_ReportLineSet_ID = rs.getInt( 1 );
                String Name                = rs.getString( 2 );

                //

                try {
                    int PA_ReportLine_ID = DB.getNextID( m_AD_Client_ID,"PA_ReportLine",null );

                    if( PA_ReportLine_ID <= 0 ) {
                        throw new DBException( "No NextID (" + PA_ReportLine_ID + ")" );
                    }

                    pstmt_insertLine.setInt( 1,PA_ReportLine_ID );
                    pstmt_insertLine.setInt( 2,PA_ReportLineSet_ID );
                    pstmt_insertLine.setString( 3,Name );

                    //

                    no = pstmt_insertLine.executeUpdate();
                    log.finest( "Insert ReportLine = " + no + ", PA_ReportLine_ID=" + PA_ReportLine_ID );
                    noInsertLine++;
                } catch( Exception ex ) {
                    log.finest( ex.toString());

                    continue;
                }
            }

            rs.close();
            pstmt.close();

            //

            pstmt_insertLine.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"Create ReportLine",e );
        }

        // Set PA_ReportLine_ID (for newly created)

        sql = new StringBuffer( "UPDATE I_ReportLine i " + "SET PA_ReportLine_ID=(SELECT PA_ReportLine_ID FROM PA_ReportLine r" + " WHERE i.Name=r.Name AND i.PA_ReportLineSet_ID=r.PA_ReportLineSet_ID AND ROWNUM=1) " + "WHERE PA_ReportLine_ID IS NULL AND PA_ReportLineSet_ID IS NOT NULL" + " AND I_IsImported='N'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.fine( "Set PA_ReportLine_ID=" + no );

        // ****    Update ReportLine

        sql = new StringBuffer( "UPDATE PA_ReportLine r " + "SET (Description,SeqNo,IsSummary,IsPrinted,LineType,CalculationType,AmountType,PostingType,Updated,UpdatedBy)=" + " (SELECT Description,SeqNo,IsSummary,IsPrinted,LineType,CalculationType,AmountType,PostingType,SysDate,UpdatedBy" + " FROM I_ReportLine i WHERE r.Name=i.Name AND r.PA_ReportLineSet_ID=i.PA_ReportLineSet_ID" + " AND i.I_ReportLine_ID=(SELECT MIN(I_ReportLine_ID) FROM I_ReportLine iii" + " WHERE i.Name=iii.Name AND i.PA_ReportLineSet_ID=iii.PA_ReportLineSet_ID)) " + "WHERE EXISTS (SELECT *" + " FROM I_ReportLine i WHERE r.Name=i.Name AND r.PA_ReportLineSet_ID=i.PA_ReportLineSet_ID" + " AND i.I_ReportLine_ID=(SELECT MIN(I_ReportLine_ID) FROM I_ReportLine iii" + " WHERE i.Name=iii.Name AND i.PA_ReportLineSet_ID=iii.PA_ReportLineSet_ID AND i.I_IsImported='N'))" ).append( clientCheck );
        noUpdateLine = DB.executeUpdate( sql.toString());
        log.config( "Update PA_ReportLine=" + noUpdateLine );

        // -------------------------------------------------------------------

        int noInsertSource = 0;
        int noUpdateSource = 0;

        // ****    Create ReportSource

        sql = new StringBuffer( "SELECT I_ReportLine_ID, PA_ReportSource_ID " + "FROM I_ReportLine " + "WHERE PA_ReportLine_ID IS NOT NULL" + " AND I_IsImported='N'" ).append( clientCheck );

        try {

            // Insert ReportSource

            PreparedStatement pstmt_insertSource = conn.prepareStatement( "INSERT INTO PA_ReportSource " + "(PA_ReportSource_ID," + "AD_Client_ID,AD_Org_ID,IsActive,Created,CreatedBy,Updated,UpdatedBy," + "PA_ReportLine_ID,ElementType,C_ElementValue_ID) " + "SELECT ?," + "AD_Client_ID,AD_Org_ID,'Y',SysDate,CreatedBy,SysDate,UpdatedBy," + "PA_ReportLine_ID,'AC',C_ElementValue_ID " + "FROM I_ReportLine " + "WHERE I_ReportLine_ID=?" + " AND I_IsImported='N'" + clientCheck );

            // Update ReportSource

            PreparedStatement pstmt_updateSource = conn.prepareStatement( "UPDATE PA_ReportSource " + "SET (ElementType,C_ElementValue_ID,Updated,UpdatedBy)=" + " (SELECT 'AC',C_ElementValue_ID,SysDate,UpdatedBy" + " FROM I_ReportLine" + " WHERE I_ReportLine_ID=?) " + "WHERE PA_ReportSource_ID=?" + clientCheck );

            // Set Imported = Y

            PreparedStatement pstmt_setImported = conn.prepareStatement( "UPDATE I_ReportLine SET I_IsImported='Y'," + " PA_ReportSource_ID=?, " + " Updated=SysDate, Processed='Y' WHERE I_ReportLine_ID=?" );
            PreparedStatement pstmt = DB.prepareStatement( sql.toString());
            ResultSet         rs    = pstmt.executeQuery();

            while( rs.next()) {
                int I_ReportLine_ID    = rs.getInt( 1 );
                int PA_ReportSource_ID = rs.getInt( 2 );

                //

                if( PA_ReportSource_ID == 0 )    // New ReportSource
                {
                    try {
                        PA_ReportSource_ID = DB.getNextID( m_AD_Client_ID,"PA_ReportSource",null );

                        if( PA_ReportSource_ID <= 0 ) {
                            throw new DBException( "No NextID (" + PA_ReportSource_ID + ")" );
                        }

                        pstmt_insertSource.setInt( 1,PA_ReportSource_ID );
                        pstmt_insertSource.setInt( 2,I_ReportLine_ID );

                        //

                        no = pstmt_insertSource.executeUpdate();
                        log.finest( "Insert ReportSource = " + no + ", I_ReportLine_ID=" + I_ReportLine_ID + ", PA_ReportSource_ID=" + PA_ReportSource_ID );
                        noInsertSource++;
                    } catch( Exception ex ) {
                        log.finest( "Insert ReportSource - " + ex.toString());
                        sql = new StringBuffer( "UPDATE I_ReportLine i " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||" ).append( DB.TO_STRING( "Insert ElementSource: " + ex.toString())).append( "WHERE I_ReportLine_ID=" ).append( I_ReportLine_ID );
                        DB.executeUpdate( sql.toString());

                        continue;
                    }
                } else    // update Report Source
                {
                    pstmt_updateSource.setInt( 1,I_ReportLine_ID );
                    pstmt_updateSource.setInt( 2,PA_ReportSource_ID );

                    try {
                        no = pstmt_updateSource.executeUpdate();
                        log.finest( "Update ReportSource = " + no + ", I_ReportLine_ID=" + I_ReportLine_ID + ", PA_ReportSource_ID=" + PA_ReportSource_ID );
                        noUpdateSource++;
                    } catch( SQLException ex ) {
                        log.finest( "Update ReportSource - " + ex.toString());
                        sql = new StringBuffer( "UPDATE I_ReportLine i " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||" ).append( DB.TO_STRING( "Update ElementSource: " + ex.toString())).append( "WHERE I_ReportLine_ID=" ).append( I_ReportLine_ID );
                        DB.executeUpdate( sql.toString());

                        continue;
                    }
                }    // update source

                // Set Imported to Y

                pstmt_setImported.setInt( 1,PA_ReportSource_ID );
                pstmt_setImported.setInt( 2,I_ReportLine_ID );
                no = pstmt_setImported.executeUpdate();

                if( no != 1 ) {
                    log.log( Level.SEVERE,"ImportReportLine.doIt - Set Imported=" + no );
                }
            }

            rs.close();
            pstmt.close();

            //

            pstmt_insertSource.close();
            pstmt_updateSource.close();
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

            throw new Exception( "ImportReportLine.doIt",e );
        } finally {
            if( conn != null ) {
                conn.close();
            }

            conn = null;
        }

        // Set Error to indicator to not imported

        sql = new StringBuffer( "UPDATE I_ReportLine " + "SET I_IsImported='N', Updated=SysDate " + "WHERE I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        addLog( 0,null,new BigDecimal( no ),"@Errors@" );
        addLog( 0,null,new BigDecimal( noInsertLine ),"@PA_ReportLine_ID@: @Inserted@" );
        addLog( 0,null,new BigDecimal( noUpdateLine ),"@PA_ReportLine_ID@: @Updated@" );
        addLog( 0,null,new BigDecimal( noInsertSource ),"@PA_ReportSource_ID@: @Inserted@" );
        addLog( 0,null,new BigDecimal( noUpdateSource ),"@PA_ReportSource_ID@: @Updated@" );

        return "";
    }    // doIt
}    // ImportReportLine



/*
 *  @(#)ImportReportLine.java   02.07.07
 * 
 *  Fin del fichero ImportReportLine.java
 *  
 *  Versión 2.2
 *
 */
