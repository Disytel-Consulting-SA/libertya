/*
 * @(#)MSequence.java   07.jul 2007  Versión 2.1
 *
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son copyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  copyRight (c)  2003-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son copyRight (c) 2005-2006 de Dataware Sistemas S.L., otras son
 * copyright (c) 2005-2006 de Indeos Consultoría S.L., otras son copyright (c) 2005-2006 de Disytel
 * Servicios Digitales S.A., y otras  partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o 
 * mejoradas a partir de código original de  terceros, recogidos en el ADDENDUM  A, sección 3 (A.3)
 * de dicha licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará
 * sujeto a su respectiva licencia original.  
 *    Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

import org.openXpertya.util.CLogMgt;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Ini;
import org.openXpertya.util.Trx;

/**
 *      Sequence Model.
 *      @see org.openXpertya.process.SequenceCheck
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MSequence.java,v 1.36 2005/04/18 04:59:54 jjanke Exp $
 */
public class MSequence extends X_AD_Sequence {

    // begin vpj-cd e-evolution 02/11/2005  PostgreSQL
    // private static final boolean USE_PROCEDURE = true;

    /** Descripción de Campo */
    private static boolean	USE_PROCEDURE	= true;

    // end vpj-cd e-evolution 02/11/2005

    /** Sequence for Table Document No's */
    private static final String	PREFIX_DOCSEQ	= "DocumentNo_";

    /** Log Level for Next ID Call */
    private static final Level	LOGLEVEL	= Level.ALL;

    /** Start System Number */
    public static final int	INIT_SYS_NO	= 100;

    /** Start Number */
    public static final int	INIT_NO	= 1000000;	// 1 Mio

    /** Static Logger */
    private static CLogger	s_log	= CLogger.getCLogger(MSequence.class);

    /** Descripción de Campo */
    private static Vector	s_list	= new Vector(1000);

    /** Cache de secuencias existentes */
    private static HashSet<String> sequences_cache = new HashSet<String>();
    
    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param AD_Sequence_ID id
     * @param trxName
     */
    public MSequence(Properties ctx, int AD_Sequence_ID, String trxName) {

        super(ctx, AD_Sequence_ID, trxName);

        if (AD_Sequence_ID == 0) {

            // setName (null);
            //
            setIsTableID(false);
            setStartNo(INIT_NO);
            setCurrentNext(BigDecimal.valueOf(INIT_NO));
            setCurrentNextSys(INIT_SYS_NO);
            setIncrementNo(1);
            setIsAutoSequence(true);
            setIsAudited(false);
            setStartNewYear(false);
        }

    }		// Msequence
    
    /**
     *      New Document Sequence Constructor
     *      @param ctx context
     *      @param AD_Client_ID owner
     * @param sequenceName
     * @param StartNo
     * @param trxName
     */
    public MSequence(Properties ctx, int AD_Client_ID, String sequenceName, BigDecimal currentNext, int StartNo, String trxName) {

        this(ctx, 0, trxName);
        setClientOrg(AD_Client_ID, 0);		// Client Ownership
        setName(sequenceName);
        setDescription(sequenceName);
        setStartNo(StartNo);
        setCurrentNext(currentNext);
        setCurrentNextSys(StartNo / 10);

    }	// Msequence;
    
    public MSequence(Properties ctx, int AD_Client_ID, String sequenceName, BigDecimal currentNext, int StartNo, int currentNextSys, String trxName) {

        this(ctx, 0, trxName);
        setClientOrg(AD_Client_ID, 0);		// Client Ownership
        setName(sequenceName);
        setDescription(sequenceName);
        setStartNo(StartNo);
        setCurrentNext(currentNext);
        setCurrentNextSys(currentNextSys);
    }

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MSequence(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MSequence

    /**
     *      New Document Sequence Constructor
     *      @param ctx context
     *      @param AD_Client_ID owner
     *      @param tableName name
     * @param trxName
     */
    public MSequence(Properties ctx, int AD_Client_ID, String tableName, String trxName) {

        this(ctx, 0, trxName);
        setClientOrg(AD_Client_ID, 0);		// Client Ownership
        setName(PREFIX_DOCSEQ + tableName);
        setDescription("DocumentNo/Value for Table " + tableName);

    }						// MSequence;

    /**
     *      New Document Sequence Constructor
     *      @param ctx context
     *      @param AD_Client_ID owner
     * @param sequenceName
     * @param StartNo
     * @param trxName
     */
    public MSequence(Properties ctx, int AD_Client_ID, String sequenceName, int StartNo, String trxName) {

        this(ctx, 0, trxName);
        setClientOrg(AD_Client_ID, 0);		// Client Ownership
        setName(sequenceName);
        setDescription(sequenceName);
        setStartNo(StartNo);
        setCurrentNext(BigDecimal.valueOf(StartNo));
        setCurrentNextSys(StartNo / 10);

    }						// Msequence;

    /**
     * @param ctx
     * @param ad_sequence_id
     * @param trxName
     * @return is table id
     */
    public static boolean isTableSequence(Properties ctx, Integer ad_sequence_id, String trxName){
    	MSequence seq = new MSequence(ctx,ad_sequence_id,trxName);
    	return seq.isTableID();
    }    
    
    /**
     *      Check/Initialize Client DocumentNo/Value Sequences
     *      @param ctx context
     *      @param AD_Client_ID client
     * @param trxName
     *      @return true if no error
     */
    public static boolean checkClientSequences(Properties ctx, int AD_Client_ID, String trxName) {

        String	sql	= "SELECT TableName " + "FROM AD_Table t " + "WHERE IsActive='Y' AND IsView='N'"

        // Get all Tables with DocumentNo or Value
        + " AND AD_Table_ID IN " + "(SELECT AD_Table_ID FROM AD_Column " + "WHERE ColumnName = 'DocumentNo' OR ColumnName = 'Value')"

        // Ability to run multiple times
        + " AND 'DocumentNo_' || TableName NOT IN " + "(SELECT Name FROM AD_Sequence s " + "WHERE s.AD_Client_ID=?)";
        int	counter	= 0;
        boolean	success	= true;

        //
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql,trxName);
            pstmt.setInt(1, AD_Client_ID);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {

                String	tableName	= rs.getString(1);

                s_log.fine("Add: " + tableName);

                MSequence	seq	= new MSequence(ctx, AD_Client_ID, tableName, trxName);

                if (seq.save()) {
                    counter++;
                } else {

                    s_log.severe("Not created - AD_Client_ID=" + AD_Client_ID + " - " + tableName);
                    success	= false;
                }
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            s_log.log(Level.SEVERE, sql, e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        s_log.info("AD_Client_ID=" + AD_Client_ID + " - created #" + counter + " - success=" + success);

        return success;

    }		// checkClientSequences

    /**
     *      Create Table ID Sequence
     *      @param ctx context
     *      @param TableName table name
     * @param trxName
     *      @return true if created
     */
    public static boolean createTableSequence(Properties ctx, String TableName, String trxName) {

        MSequence	seq	= new MSequence(ctx, 0, trxName);

        seq.setClientOrg(0, 0);
        seq.setName(TableName);
        seq.setDescription("Table " + TableName);
        seq.setIsTableID(true);

        return seq.save();

    }		// createTableSequence

	/**
	 * LOCALE AR. Setear el siguiente número de comprobante de la secuencia
	 * teniendo en cuenta las particularidades del prefijo y el currentnext,
	 * para dejar correctamente seteado el current next con el punto de venta
	 * adecuado.
	 * 
	 * @param sequenceID
	 * @param nextNroComprobante
	 * @param trxName
	 * @return
	 */
    public static boolean setFiscalDocTypeNextNroComprobante(int sequenceID, int nextNroComprobante, String trxName) {
		MSequence seq = new MSequence(Env.getCtx(), sequenceID, trxName);
		String currentNext = String.valueOf(seq.getCurrentNext());
		String prefix = seq.getPrefix();
		
		NumberFormat format = NumberFormat.getNumberInstance();
		format.setMinimumIntegerDigits(8);
		format.setMaximumIntegerDigits(8);
		format.setGroupingUsed(false);
		// Se debe determinar que parte del punto de venta está contenido en el
		// CurrentNext (debido al conocido problema de los puntos de venta
		// terminados en Cero)
		//
		// Long del Prefijo = 4 -> PV en CurrentNext = 1
		// Long del Prefijo = 3 -> PV en CurrentNext = 2
		// Long del Prefijo = 2 -> PV en CurrentNext = 3
		// Long del Prefijo = 1 -> PV en CurrentNext = 4
		//
		// Se obtiene el número siguiente de documento según el ultimo comprobante
		// emitido por la impresora fiscal.
		String newCurrentNext = currentNext.substring(0, (4 - prefix.length()) + 1) + format.format(nextNroComprobante);
		// Se actualiza la secuencia solo si el número de comprobante siguiente es distinto al
		// que ya tenía la secuencia.
		if(!currentNext.equals(newCurrentNext)) {
			seq.setCurrentNext(new BigDecimal(newCurrentNext));
			return seq.save();
		}

    	return true;
    }

    /**
     *      Test
     *      @param args ignored
     */
    static public void main(String[] args) {

        org.openXpertya.OpenXpertya.startup(true);
        CLogMgt.setLevel(Level.SEVERE);
        CLogMgt.setLoggerLevel(Level.SEVERE, null);

        /**
         *     Lock Test 
         * String trxName = "test";
         * System.out.println(DB.getDocumentNo(115, trxName));
         * System.out.println(DB.getDocumentNo(116, trxName));
         * System.out.println(DB.getDocumentNo(117, trxName));
         * System.out.println(DB.getDocumentNo(118, trxName));
         * System.out.println(DB.getDocumentNo(118, trxName));
         * System.out.println(DB.getDocumentNo(117, trxName));
         *
         * trxName = "test1";
         * System.out.println(DB.getDocumentNo(115, trxName));     //      hangs here as supposed
         * System.out.println(DB.getDocumentNo(116, trxName));
         * System.out.println(DB.getDocumentNo(117, trxName));
         * System.out.println(DB.getDocumentNo(118, trxName));
         *
         *
         *
         *
         *
         * /** *
         */

        /** Time Test */
        long		time	= System.currentTimeMillis();
        Thread[]	threads	= new Thread[10];

        for (int i = 0; i < 10; i++) {

            Runnable	r	= new GetIDs(i);

            threads[i]	= new Thread(r);
            threads[i].start();
        }

        for (int i = 0; i < 10; i++) {

            try {
                threads[i].join();
            } catch (InterruptedException e) {}
        }

        time	= System.currentTimeMillis() - time;
        System.out.println("-------------------------------------------");
        System.out.println("Size=" + s_list.size() + " (should be 1000)");

        Integer[]	ia	= new Integer[s_list.size()];

        s_list.toArray(ia);
        Arrays.sort(ia);

        Integer	last		= null;
        int	duplicates	= 0;

        for (int i = 0; i < ia.length; i++) {

            if (last != null) {

                if (last.compareTo(ia[i]) == 0) {

                    // System.out.println(i + ": " + ia[i]);
                    duplicates++;
                }
            }

            last	= ia[i];
        }

        System.out.println("-------------------------------------------");
        System.out.println("Size=" + s_list.size() + " (should be 1000)");
        System.out.println("Duplicates=" + duplicates);
        System.out.println("Time (ms)=" + time + " - " + ((float) time / s_list.size()) + " each");
        System.out.println("-------------------------------------------");

        /**
         * try
         * {
         *       int retValue = -1;
         *       Connection conn = DB.getConnectionRW ();
         * //      DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());
         * //      Connection conn = DriverManager.getConnection ("jdbc:oracle:thin:@//dev2:1521/dev2", "openxp", "openxp");
         *
         *       conn.setAutoCommit(false);
         *       String sql = "SELECT CurrentNext, CurrentNextSys, IncrementNo "
         *               + "FROM AD_Sequence "
         *               + "WHERE Name='AD_Sequence' ";
         *       sql += "FOR UPDATE";
         *       //      creates ORA-00907: missing right parenthesis
         * //      sql += "FOR UPDATE OF CurrentNext, CurrentNextSys";
         *
         *
         *       PreparedStatement pstmt = conn.prepareStatement(sql,
         *               ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
         *       ResultSet rs = pstmt.executeQuery();
         *       System.out.println("AC=" + conn.getAutoCommit() + ", RO=" + conn.isReadOnly()
         *               + " - Isolation=" + conn.getTransactionIsolation() + "(" + Connection.TRANSACTION_READ_COMMITTED
         *               + ") - RSType=" + pstmt.getResultSetType() + "(" + ResultSet.TYPE_SCROLL_SENSITIVE
         *               + "), RSConcur=" + pstmt.getResultSetConcurrency() + "(" + ResultSet.CONCUR_UPDATABLE
         *               + ")");
         *
         *       if (rs.next())
         *       {
         *               int IncrementNo = rs.getInt(3);
         *               retValue = rs.getInt(1);
         *               rs.updateInt(1, retValue + IncrementNo);
         *               rs.updateRow();
         *       }
         *       else
         *               s_log.severe ("no record found");
         *       rs.close();
         *       pstmt.close();
         *       conn.commit();
         *       conn.close();
         *       /
         *       System.out.println("Next=" + retValue);
         *
         * }
         * catch (Exception e)
         * {
         *       e.printStackTrace ();
         * }
         *
         * System.exit(0);
         *
         * /** 
         *
         * int AD_Client_ID = 0;
         * int C_DocType_ID = 115; //      GL
         * String TableName = "C_Invoice";
         * String trxName = "x";
         * Trx trx = Trx.get(trxName, true);
         *
         * System.out.println ("none " + getNextID (0, "Test"));
         * System.out.println ("----------------------------------------------");
         * System.out.println ("trx1 " + getNextID (0, "Test"));
         * System.out.println ("trx2 " + getNextID (0, "Test"));
         * //      trx.rollback();
         * System.out.println ("trx3 " + getNextID (0, "Test"));
         * //      trx.commit();
         * System.out.println ("trx4 " + getNextID (0, "Test"));
         * //      trx.rollback();
         * //      trx.close();
         * System.out.println ("----------------------------------------------");
         * System.out.println ("none " + getNextID (0, "Test"));
         * System.out.println ("==============================================");
         *
         *
         * trx = Trx.get(trxName, true);
         * System.out.println ("none " + getDocumentNo(AD_Client_ID, TableName, null));
         * System.out.println ("----------------------------------------------");
         * System.out.println ("trx1 " + getDocumentNo(AD_Client_ID, TableName, trxName));
         * System.out.println ("trx2 " + getDocumentNo(AD_Client_ID, TableName, trxName));
         * trx.rollback();
         * System.out.println ("trx3 " + getDocumentNo(AD_Client_ID, TableName, trxName));
         * trx.commit();
         * System.out.println ("trx4 " + getDocumentNo(AD_Client_ID, TableName, trxName));
         * trx.rollback();
         * trx.close();
         * System.out.println ("----------------------------------------------");
         * System.out.println ("none " + getDocumentNo(AD_Client_ID, TableName, null));
         * System.out.println ("==============================================");
         *
         *
         * trx = Trx.get(trxName, true);
         * System.out.println ("none " + getDocumentNo(C_DocType_ID, null));
         * System.out.println ("----------------------------------------------");
         * System.out.println ("trx1 " + getDocumentNo(C_DocType_ID, trxName));
         * System.out.println ("trx2 " + getDocumentNo(C_DocType_ID, trxName));
         * trx.rollback();
         * System.out.println ("trx3 " + getDocumentNo(C_DocType_ID, trxName));
         * trx.commit();
         * System.out.println ("trx4 " + getDocumentNo(C_DocType_ID, trxName));
         * trx.rollback();
         * trx.close();
         * System.out.println ("----------------------------------------------");
         * System.out.println ("none " + getDocumentNo(C_DocType_ID, null));
         * System.out.println ("==============================================");
         * /** *
         */

    }		// main

    /**
     *      Get Next ID
     *      @param conn connection
     *      @param AD_Sequence_ID sequence
     *      @param OXPSYS sys
     *      @return next id or -1 (error) or -3 (parameter)
     */
    private static int nextID(Connection conn, int AD_Sequence_ID, boolean OXPSYS) {

        if ((conn == null) || (AD_Sequence_ID == 0)) {
            return -3;
        }

        //
        int			retValue	= -1;
        String			sqlUpdate	= "{call nextID(?,?,?)}";
        CallableStatement	cstmt		= null;

        try {

            cstmt	= conn.prepareCall(sqlUpdate, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            cstmt.setInt(1, AD_Sequence_ID);
            cstmt.setString(2, OXPSYS
                               ? "Y"
                               : "N");
            cstmt.registerOutParameter(3, Types.INTEGER);
            cstmt.execute();
            retValue	= cstmt.getInt(3);
            cstmt.close();
            cstmt	= null;

        } catch (Exception e) {
            s_log.log(Level.SEVERE, e.toString());
        }

        // Finish
        try {

            if (cstmt != null) {
                cstmt.close();
            }

        } catch (Exception e) {}

        return retValue;

    }		// nextID

    public static String getPrefix(Integer sequenceID, String trxName){
		return DB.getSQLValueString(trxName, "SELECT prefix FROM ad_sequence WHERE ad_sequence_id = ?", sequenceID);
    }
    
    public static String getSuffix(Integer sequenceID, String trxName){
		return DB.getSQLValueString(trxName, "SELECT suffix FROM ad_sequence WHERE ad_sequence_id = ?", sequenceID);
    }
    
    /**
     *      Validate Table Sequence Values
     *      @return true if updated
     */
    public boolean validateTableIDValue() {

        if (!isTableID()) {
            return false;
        }

        String	tableName	= getName();
        int	AD_Column_ID	= DB.getSQLValue(null, "SELECT MAX(c.AD_Column_ID) " + "FROM AD_Table t" + " INNER JOIN AD_Column c ON (t.AD_Table_ID=c.AD_Table_ID) " + "WHERE t.TableName='" + tableName + "'" + " AND c.ColumnName='" + tableName + "_ID'");

        if (AD_Column_ID <= 0) {
            return false;
        }

        //
        MSystem	system		= MSystem.get(getCtx());
        int	IDRangeEnd	= 0;

        if (system.getIDRangeEnd() != null) {
            IDRangeEnd	= system.getIDRangeEnd().intValue();
        }

        boolean	change	= false;
        String	info	= null;

        // Current Next
        String	sql	= "SELECT MAX(" + tableName + "_ID) FROM " + tableName;

        if (IDRangeEnd > 0) {
            sql	+= " WHERE " + tableName + "_ID < " + IDRangeEnd;
        }

        int	maxTableID	= DB.getSQLValue(null, sql);

        if (maxTableID < INIT_NO) {
            maxTableID	= INIT_NO - 1;
        }

        maxTableID++;		// Next

        if (getCurrentNext().compareTo(new BigDecimal(maxTableID)) < 0) {

            setCurrentNext(new BigDecimal(maxTableID));
            info	= "CurrentNext=" + maxTableID;
            change	= true;
        }

        // Get Max System_ID used in Table
        sql	= "SELECT MAX(" + tableName + "_ID) FROM " + tableName + " WHERE " + tableName + "_ID < " + INIT_NO;

        int	maxTableSysID	= DB.getSQLValue(null, sql);

        if (maxTableSysID <= 0) {
            maxTableSysID	= INIT_SYS_NO - 1;
        }

        maxTableSysID++;	// Next

        if (getCurrentNextSys() < maxTableSysID) {

            setCurrentNextSys(maxTableSysID);

            if (info == null) {
                info	= "CurrentNextSys=" + maxTableSysID;
            } else {
                info	+= " - CurrentNextSys=" + maxTableSysID;
            }

            change	= true;
        }

        if (info != null) {
            log.fine(getName() + " - " + info);
        }

        return change;

    }		// validate

    
    
    
    /**
     *      Validate Table Sequence Values
     *      Modificado para la sequencia de la tabla la tome de las secuencias y no de AD_SEquence
     *      
     *      @return true if updated
     */
    public boolean validateSequence() {

        if (!isTableID()) {
            return false;
        }

        String	tableName	= getName();
        int	AD_Column_ID	= DB.getSQLValue(this.get_TrxName(), "SELECT MAX(c.AD_Column_ID) " + "FROM AD_Table t" + " INNER JOIN AD_Column c ON (t.AD_Table_ID=c.AD_Table_ID) " + "WHERE t.TableName='" + tableName + "'" + " AND c.ColumnName='" + tableName + "_ID'");

        if (AD_Column_ID <= 0) {
            return false;
        }

        //
        MSystem	system		= MSystem.get(getCtx());
        int	IDRangeEnd	= 0;

        if (system.getIDRangeEnd() != null) {
            IDRangeEnd	= system.getIDRangeEnd().intValue();
        }

        boolean	change	= false;
        String	info	= null;

        // Current Next
        String	sql	= "SELECT MAX(" + tableName + "_ID) FROM " + tableName;

        if (IDRangeEnd > 0) {
            sql	+= " WHERE " + tableName + "_ID < " + IDRangeEnd;
        }

        int	maxTableID	= DB.getSQLValue(this.get_TrxName(), sql);

        if (maxTableID < INIT_NO) {
            maxTableID	= INIT_NO - 1;
        }

        maxTableID++;		// Next
        int nextId  = getNextID(getAD_Client_ID(), tableName, this.get_TrxName());
        
        if (nextId < maxTableID) {
        	int result=setNextID(getAD_Client_ID(),tableName,this.get_TrxName(), maxTableID);
        	if (result<maxTableID)
        	{
        		log.warning(" ---  No se pudo actualizar la secuencia para " + tableName);
        	}
            //setCurrentNext(maxTableID);
            info	= "CurrentNext=" + maxTableID;
            change	= true;
        }

        // Get Max System_ID used in Table
        sql	= "SELECT MAX(" + tableName + "_ID) FROM " + tableName + " WHERE " + tableName + "_ID < " + INIT_NO;

        int	maxTableSysID	= DB.getSQLValue(this.get_TrxName(), sql);

        if (maxTableSysID <= 0) {
            maxTableSysID	= INIT_SYS_NO - 1;
        }

        maxTableSysID++;	// Next

        if (getCurrentNextSys() < maxTableSysID) {

            setCurrentNextSys(maxTableSysID);

            if (info == null) {
                info	= "CurrentNextSys=" + maxTableSysID;
            } else {
                info	+= " - CurrentNextSys=" + maxTableSysID;
            }

            change	= true;
        }

        if (info != null) {
            log.fine(getName() + " - " + info);
        }

        return change;

    }		// validate
    
    
    //Retorna el nombre de la secuencia para la Tabla
    public static String getSequenceName(String tableName)
    {
    	String secuencia= "seq_"+ tableName.toLowerCase();
    	return secuencia;
    }
    
    
    //Asigna nextID como siguiente secuencia a la Tabla
    public static int setNextID(int AD_Client_ID, String TableName, String trxName, int nextId)
    {
    	String secuencia= getSequenceName(TableName);
		String sql	= "alter sequence " + secuencia + " restart with " + nextId  ;
	//	Connection		conn	= null;
	    PreparedStatement	pstmt	= null;
	   
	    /*
		Trx	trx= Trx.get(trxName+TableName + "secuencia_select", true);
	    if (trx != null) {
            conn	= trx.getConnection();
        } else {
            conn	= DB.getConnectionID();
        }
	    if (conn == null) {
            return -1;
        }
        */
		try {
			pstmt=DB.prepareStatement(sql,trxName);
			pstmt.execute();
			pstmt.close();
			pstmt=null;
			//conn=null;
			}
		catch (Exception e) {
	            s_log.log(Level.SEVERE, "get", e);
	            return -1;
	       }
		return nextId;

    	
    }
    
    
    public static int checkSequencia(int AD_Client_ID, String TableName, String trxName, int nextId)
    {
    	String secuencia= getSequenceName(TableName);
		String sql	= "alter sequence " + secuencia + " restart with " + nextId  ;
		Connection		conn	= null;
	    PreparedStatement	pstmt	= null;
	   
	    
		Trx	trx= Trx.get(trxName+TableName + "secuencia_select", true);
	    if (trx != null) {
            conn	= trx.getConnection();
        } else {
            conn	= DB.getConnectionID();
        }
	    if (conn == null) {
            return -1;
        }


		try {
			pstmt=conn.prepareStatement(sql);
			pstmt.execute();
			pstmt.close();
			pstmt=null;
			conn=null;
			}
		catch (Exception e) {
	            s_log.log(Level.SEVERE, "get", e);
	            return -1;
	       }
		return nextId;

    	
    }    
    
  //~--- get methods --------------------------------------------------------
    
    /**
     *      Get Sequence
     *      @param ctx context
     *      @param tableName table name
     *
     * @return
     */
    public static MSequence get(Properties ctx, String tableName, boolean isTableID, Integer AD_Client_ID) {

        String	sql	= 	"SELECT * FROM AD_Sequence " + 
        				" WHERE UPPER(Name) = UPPER(?)" + 
        				" AND IsTableID = " + (isTableID?"'Y'":"'N'");
        
        if (AD_Client_ID != null)
        	sql = sql + " AND AD_Client_ID = " + AD_Client_ID;
        MSequence		retValue	= null;
        PreparedStatement	pstmt		= null;

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setString(1, tableName.toUpperCase());

            ResultSet	rs	= pstmt.executeQuery();

            if (rs.next()) {
                retValue	= new MSequence(ctx, rs, null);
            }

            if (rs.next()) {
                s_log.log(Level.SEVERE, "More then one sequence for " + tableName);
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            s_log.log(Level.SEVERE, "get", e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        return retValue;

    }		// get

    /**
     *      Get next DocumentNo
     *      @return document no
     */
    public String getDocumentNo() {

        // create DocumentNo
        StringBuffer	doc	= new StringBuffer();
        String		prefix	= getPrefix();

        if ((prefix != null) && (prefix.length() > 0)) {
            doc.append(prefix);
        }

        doc.append(getNextID());

        String	suffix	= getSuffix();

        if ((suffix != null) && (suffix.length() > 0)) {
            doc.append(suffix);
        }

        return doc.toString();
    }		// getDocumentNo

    /**
     *      Get Document No based on Document Type
     *      @param C_DocType_ID document type
     *      @param trxName optional Transaction Name
     *      @return document no or null
     */
    public static synchronized String getDocumentNo(int C_DocType_ID, String trxName) {

        if (C_DocType_ID == 0) {

            s_log.severe("C_DocType_ID=0");

            return null;
        }

        MDocType	dt	= MDocType.get(Env.getCtx(), C_DocType_ID);	// wrong for SERVER, but r/o

        if ((dt != null) &&!dt.isDocNoControlled()) {

            s_log.finer("DocType_ID=" + C_DocType_ID + " Not DocNo controlled");

            return null;
        }

        if ((dt == null) || (dt.getDocNoSequence_ID() == 0)) {

            s_log.warning("No Sequence for DocType - " + dt);

            return null;
        }

        // Check OXPSYS
        boolean	OXPSYS	= Ini.getPropertyBool(Ini.P_OXPSYS);

        if (CLogMgt.isLevel(LOGLEVEL)) {
            s_log.log(LOGLEVEL, "DocType_ID=" + C_DocType_ID + " [" + trxName + "]");
        }

        // begin vpj-cd e-evolution 09/02/2005 PostgreSQL
        String	selectSQL	= null;

        if (DB.isPostgreSQL()) {

            selectSQL	= "SELECT CurrentNext, CurrentNextSys, IncrementNo, Prefix, Suffix, AD_Client_ID, AD_Sequence_ID, OID " + "FROM AD_Sequence " + "WHERE AD_Sequence_ID=?" + " AND IsActive='Y' AND IsTableID='N' AND IsAutoSequence='Y' " + " FOR UPDATE OF AD_Sequence ";
            USE_PROCEDURE	= false;

        } else {

            // String selectSQL = "SELECT CurrentNext, CurrentNextSys, IncrementNo, Prefix, Suffix, AD_Client_ID, AD_Sequence_ID "
            selectSQL	= "SELECT CurrentNext, CurrentNextSys, IncrementNo, Prefix, Suffix, AD_Client_ID, AD_Sequence_ID "

            // end vpj-cd e-evolution 09/02/2005     PostgreSQL
            + "FROM AD_Sequence " + "WHERE AD_Sequence_ID=?" + " AND IsActive='Y' AND IsTableID='N' AND IsAutoSequence='Y' ";
        }

        // + " FOR UPDATE";
        Connection		conn	= null;
        PreparedStatement	pstmt	= null;
        Trx			trx	= (trxName == null)
                                          ? null
                                          : Trx.get(trxName, true);

        //
        int	AD_Sequence_ID	= 0;
        int	incrementNo	= 0;
        BigDecimal	next		= new BigDecimal(-1);
        String	prefix		= "";
        String	suffix		= "";

        try {

            if (trx != null) {
                conn	= trx.getConnection();
            } else {
                conn	= DB.getConnectionID();
            }

            // Error
            if (conn == null) {
                return null;
            }

            //
            pstmt	= conn.prepareStatement(selectSQL, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            pstmt.setInt(1, dt.getDocNoSequence_ID());

            //
            ResultSet	rs	= pstmt.executeQuery();

            // s_log.fine("AC=" + conn.getAutoCommit() + " -Iso=" + conn.getTransactionIsolation()
            // + " - Type=" + pstmt.getResultSetType() + " - Concur=" + pstmt.getResultSetConcurrency());
            if (rs.next()) {

                incrementNo	= rs.getInt(3);
                prefix		= rs.getString(4);
                suffix		= rs.getString(5);

                int	AD_Client_ID	= rs.getInt(6);

                if (OXPSYS && (AD_Client_ID > 11)) {
                    OXPSYS	= false;
                }

                AD_Sequence_ID	= rs.getInt(7);

                if (USE_PROCEDURE) {
                    next	= new BigDecimal(nextID(conn, AD_Sequence_ID, OXPSYS));
                } else {

                    if (OXPSYS) {

                        next	= rs.getBigDecimal(2);
                        rs.updateBigDecimal(2, next.add(new BigDecimal(incrementNo)));

                    } else {

                        next	= rs.getBigDecimal(1);
                        rs.updateBigDecimal(1, next.add(new BigDecimal(incrementNo)));
                    }

                    rs.updateRow();
                }

            } else {

                s_log.warning("(DocType)- no record found - " + dt);
                next	= new BigDecimal(-2);
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

            // Commit
            if (trx == null) {

                conn.commit();

                // conn.close();
            }

            conn	= null;

        } catch (Exception e) {

            s_log.log(Level.SEVERE, "(DocType) [" + trxName + "]", e);
            next	= new BigDecimal(-2);
        }

        // Finish
        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

            // if (conn != null && trx == null)
            // conn.close();
            conn	= null;

        } catch (Exception e) {

            s_log.log(Level.SEVERE, "(DocType) - finish", e);
            pstmt	= null;
        }

        // Error
        if (next.compareTo(BigDecimal.ZERO) < 0) {
            return null;
        }

        // create DocumentNo
        StringBuffer	doc	= new StringBuffer();

        if ((prefix != null) && (prefix.length() > 0)) {
            doc.append(prefix);
        }

        doc.append(next);

        if ((suffix != null) && (suffix.length() > 0)) {
            doc.append(suffix);
        }

        String	documentNo	= doc.toString();

        s_log.finer(documentNo + " (" + incrementNo + ")" + " - C_DocType_ID=" + C_DocType_ID + " [" + trx + "]");

        return documentNo;

    }		// getDocumentNo

    /**
     *      Get Document No from table
     *      @param AD_Client_ID client
     *      @param TableName table name
     *      @param trxName optional Transaction Name
     *      @return document no or null
     */
    public static synchronized String getDocumentNo(int AD_Client_ID, String TableName, String trxName) {

        if ((TableName == null) || (TableName.length() == 0)) {
            throw new IllegalArgumentException("TableName missing");
        }

        // Check OXPSYS
        boolean	OXPSYS	= Ini.getPropertyBool(Ini.P_OXPSYS);

        if (OXPSYS && (AD_Client_ID > 11)) {
            OXPSYS	= false;
        }

        //
        if (CLogMgt.isLevel(LOGLEVEL)) {
            s_log.log(LOGLEVEL, TableName + " - OXPSYS=" + OXPSYS + " [" + trxName + "]");
        }

        // begin vpj-cd e-evolution 09/02/2005 PostgreSQL
        String	selectSQL	= null;

        if (DB.isPostgreSQL()) {

            selectSQL	= "SELECT CurrentNext, CurrentNextSys, IncrementNo, Prefix, Suffix, AD_Sequence_ID , OID " + "FROM AD_Sequence " + "WHERE Name=?" + " AND AD_Client_ID IN (0,?)" + " AND IsActive='Y' AND IsTableID='N' AND IsAutoSequence='Y' " + "ORDER BY AD_Client_ID DESC " + " FOR UPDATE OF AD_Sequence ";
            USE_PROCEDURE	= false;

        } else {

            // String selectSQL = "SELECT CurrentNext, CurrentNextSys, IncrementNo, Prefix, Suffix, AD_Sequence_ID "
            selectSQL	= "SELECT CurrentNext, CurrentNextSys, IncrementNo, Prefix, Suffix, AD_Sequence_ID "

            // end vpj-cd e-evolution 09/02/2005     PostgreSQL
            + "FROM AD_Sequence " + "WHERE Name=?" + " AND AD_Client_ID IN (0,?)" + " AND IsActive='Y' AND IsTableID='N' AND IsAutoSequence='Y' " + "ORDER BY AD_Client_ID DESC ";
        }

        // + "FOR UPDATE";
        Connection		conn	= null;
        PreparedStatement	pstmt	= null;
        Trx			trx	= (trxName == null)
                                          ? null
                                          : Trx.get(trxName, true);

        //
        int	AD_Sequence_ID	= 0;
        int	incrementNo	= 0;
        int	next		= -1;
        String	prefix		= "";
        String	suffix		= "";

        try {

            if (trx != null) {
                conn	= trx.getConnection();
            } else {
                conn	= DB.getConnectionID();
            }

            // Error
            if (conn == null) {
                return null;
            }

            //
            pstmt	= conn.prepareStatement(selectSQL, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            pstmt.setString(1, PREFIX_DOCSEQ + TableName);
            pstmt.setInt(2, AD_Client_ID);

            //
            ResultSet	rs	= pstmt.executeQuery();

            // s_log.fine("AC=" + conn.getAutoCommit() + " -Iso=" + conn.getTransactionIsolation()
            // + " - Type=" + pstmt.getResultSetType() + " - Concur=" + pstmt.getResultSetConcurrency());
            if (rs.next()) {

                AD_Sequence_ID	= rs.getInt(6);
                prefix		= rs.getString(4);
                suffix		= rs.getString(5);
                incrementNo	= rs.getInt(3);

                if (USE_PROCEDURE) {
                    next	= nextID(conn, AD_Sequence_ID, OXPSYS);
                } else {

                    if (OXPSYS) {

                        next	= rs.getInt(2);
                        rs.updateInt(2, next + incrementNo);

                    } else {

                        next	= rs.getInt(1);
                        rs.updateInt(1, next + incrementNo);
                    }

                    rs.updateRow();
                }

            } else {

                s_log.warning("(Table) - no record found - " + TableName);

                MSequence	seq	= new MSequence(Env.getCtx(), AD_Client_ID, TableName, null);

                next	= seq.getNextID();
                seq.save();
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

            // Commit
            if (trx == null) {

                conn.commit();

                // conn.close();
            }

            conn	= null;

        } catch (Exception e) {

            s_log.log(Level.SEVERE, "(Table) [" + trxName + "]", e);
            next	= -2;
        }

        // Finish
        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

            // if (conn != null && trx == null)
            // conn.close();
            conn	= null;

        } catch (Exception e) {

            s_log.log(Level.SEVERE, "(Table) - finish", e);
            pstmt	= null;
        }

        // Error
        if (next < 0) {
            return null;
        }

        // create DocumentNo
        StringBuffer	doc	= new StringBuffer();

        if ((prefix != null) && (prefix.length() > 0)) {
            doc.append(prefix);
        }

        doc.append(next);

        if ((suffix != null) && (suffix.length() > 0)) {
            doc.append(suffix);
        }

        String	documentNo	= doc.toString();

        s_log.finer(documentNo + " (" + incrementNo + ")" + " - Table=" + TableName + " [" + trx + "]");

        return documentNo;

    }		// getDocumentNo

    /**
     *      Get Next No and increase current next
     *      @return next no to use
     */
    public int getNextID() {

       // int	retValue	= getCurrentNext();

      //  setCurrentNext(retValue + getIncrementNo());

        return getNextID(getAD_Client_ID(),get_TableName(), this.get_TrxName() );

    }		// getNextNo

    
   
    
    /**
     *      Get next number for Key column = 0 is Error.
     *  @param AD_Client_ID client
     *  @param TableName table name
     *      @param trxName optional Transaction Name
     *  @return next no or (-1=not found, -2=error)
     */
    public static int getNextIDOracle(int AD_Client_ID, String TableName, String trxName) {
    	int useSecuencia=0;
        if ((TableName == null) || (TableName.length() == 0)) {
            throw new IllegalArgumentException("TableName missing");
        }

        int	retValue	= -1;

        // Check OXPSYS
        boolean	OXPSYS	= Ini.getPropertyBool(Ini.P_OXPSYS);

        if (OXPSYS && (AD_Client_ID > 11)) {
            OXPSYS	= false;
        }

        //
        if (CLogMgt.isLevel(LOGLEVEL)) {
            s_log.log(LOGLEVEL, TableName + " - OXPSYS=" + OXPSYS + " [" + trxName + "]");
        }

        // begin vpj-cd e-evolution 09/02/2005 PostgreSQL
        String	selectSQL	= null;

        if (DB.isPostgreSQL()) {

        	//Si tiene secuencia, la usa. 
        	//Sino toma los datos de ad_sequence y luego crea la secuencia
        	// la secuencia creada sube en 10000 el numerador.
        		String secuencia= "seq_"+ TableName;
        	
        		Boolean usarSecuencia = Boolean.FALSE;
        
        		String sqlCheck  = "select 1 from pg_class where UPPER(relname) =UPPER(?)";
        		useSecuencia = DB.getSQLValue(trxName, sqlCheck,secuencia);
        		usarSecuencia =!(useSecuencia==-1) ;
        
        
        	if (usarSecuencia.booleanValue())
        		selectSQL	= "SELECT CurrentNext, CurrentNextSys, IncrementNo, AD_Sequence_ID , OID " + "FROM AD_Sequence " + "WHERE Name=?" + " AND IsActive='Y' AND IsTableID='Y' AND IsAutoSequence='Y' " + " FOR UPDATE OF AD_Sequence ";
        	else
        	{
        		selectSQL	= "SELECT nextval(?)";
        		retValue = DB.getSQLValue(trxName,selectSQL,secuencia);
        		return retValue;
        		
        	}
            
            USE_PROCEDURE	= false;

        } else {

            // String selectSQL = "SELECT CurrentNext, CurrentNextSys, IncrementNo, AD_Sequence_ID "
            selectSQL	= "SELECT CurrentNext, CurrentNextSys, IncrementNo, AD_Sequence_ID "

            // end vpj-cd e-evolution 09/02/2005 PostgreSQL
            + "FROM AD_Sequence " + "WHERE Name=?" + " AND IsActive='Y' AND IsTableID='Y' AND IsAutoSequence='Y' ";
        }

        // + "FOR UPDATE"; // OF CurrentNext, CurrentNextSys";
        Trx			trx	= (trxName == null)
                                          ? null
                                          : Trx.get(trxName, true);
        Connection		conn	= null;
        PreparedStatement	pstmt	= null;

        for (int i = 0; i < 3; i++) {

            try {

                if (trx != null) {
                    conn	= trx.getConnection();
                } else {
                    conn	= DB.getConnectionID();
                }

                // Error
                if (conn == null) {
                    return -1;
                }

                //
                pstmt	= conn.prepareStatement(selectSQL, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                pstmt.setString(1, TableName);

                // DEBUG Acá se cuelga la segunda vuelta
                ResultSet	rs	= pstmt.executeQuery();

                if (CLogMgt.isLevelFinest()) {
                    s_log.finest("AC=" + conn.getAutoCommit() + ", RO=" + conn.isReadOnly() + " - Isolation=" + conn.getTransactionIsolation() + "(" + Connection.TRANSACTION_READ_COMMITTED + ") - RSType=" + pstmt.getResultSetType() + "(" + ResultSet.TYPE_SCROLL_SENSITIVE + "), RSConcur=" + pstmt.getResultSetConcurrency() + "(" + ResultSet.CONCUR_UPDATABLE + ")");
                }

                if (rs.next()) {

                               	
                    int	AD_Sequence_ID	= rs.getInt(4);

                    //
                    if (USE_PROCEDURE) {
                        retValue	= nextID(conn, AD_Sequence_ID, OXPSYS);
                    } else {

                        int	incrementNo	= rs.getInt(3);

                        if (OXPSYS) {

                            retValue	= rs.getInt(2);
                            rs.updateInt(2, retValue + incrementNo);

                        } else {

                            retValue	= rs.getInt(1);
                            rs.updateInt(1, retValue + incrementNo);
                        }

                        rs.updateRow();
                                             
                    }
                    if (trx == null) {
                        conn.commit();
                    }
                	
                } else {
                    s_log.severe("No record found - " + TableName);
                }
                
                rs.close();
                pstmt.close();
                pstmt	= null;
                
                //
                // conn.close();
                conn	= null;

                //
                break;		// EXIT

            } catch (Exception e) {

                s_log.log(Level.SEVERE, TableName + " - " + e.getMessage(), e);

                try {

                    conn.rollback();

                    if (pstmt != null) {
                        pstmt.close();
                    }

                } catch (SQLException e1) {}
            }

            Thread.yield();	// give it time
        }

        // Finish
        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

            // if (conn != null)
            // conn.close();
            conn	= null;

        } catch (Exception e) {

            s_log.log(Level.SEVERE, "Finish", e);
            pstmt	= null;
        }

        s_log.finest(retValue + " - Table=" + TableName + " [" + trx + "]");

        return retValue;

    }		// getNextID

    
    
    public static int getNextID(int AD_Client_ID, String TableName, String trxName) {
    	
    	if (!DB.isPostgreSQL()) {
    		// Si no es postgresql usamos el original
    		return getNextIDOracle(AD_Client_ID, TableName,trxName);
    	}
        String	selectSQL	= null;    	
        int	retValue	= -1;
        // Check OXPSYS
        boolean	OXPSYS	= Ini.getPropertyBool(Ini.P_OXPSYS);

        
        if ((TableName == null) || (TableName.length() == 0)) {
            throw new IllegalArgumentException("TableName missing");
        }

        
        if (OXPSYS && (AD_Client_ID > 11)) {
            OXPSYS	= false;
        }

        //
        if (CLogMgt.isLevel(LOGLEVEL)) {
            s_log.log(LOGLEVEL, TableName + " - OXPSYS=" + OXPSYS + " [" + trxName + "]");
        }

      
    	//Si tiene secuencia, la usa. 
    	//Sino toma los datos de ad_sequence y luego crea la secuencia
    	// la secuencia creada sube en 10000 el numerador.
    	String secuencia= getSequenceName(TableName);

    	// Primero hay que verificar si la secuencia existe en la cache, en
    	// caso de no existir, buscarla en la base de datos y agregar a cache
    	boolean existeSeq = false;
    	if (sequences_cache.contains(secuencia))
    		existeSeq = true;
    	else if (1 == DB.getSQLValue(trxName, "select count(1) from pg_class where relkind = 'S' and relname = '" + secuencia + "'"))
    	{
    		existeSeq = true;
    		sequences_cache.add(secuencia);	
    	}
    	
		
	    PreparedStatement	pstmt	= null;
	   
		try {
			if (existeSeq)
			{
				selectSQL	= "SELECT nextval(?)";
				pstmt=DB.prepareStatement(selectSQL,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE, trxName);
				pstmt.setString(1,secuencia);
				ResultSet	rs	= pstmt.executeQuery();    
				if (rs.next())
					retValue = rs.getInt(1);
				rs.close();
				pstmt.close();
				rs=null;
				pstmt=null;
			}
		
		} catch (SQLException e) {
            s_log.log(Level.SEVERE, "MSequence - Error al obtener nextVal", e);
            pstmt	= null;
		}
		if (!existeSeq)
		{
		
			selectSQL	= "SELECT CurrentNext, CurrentNextSys, IncrementNo, AD_Sequence_ID , OID " + "FROM AD_Sequence " + "WHERE Name=?  AND IsActive='Y' AND IsTableID='Y' AND IsAutoSequence='Y' ";
   
            pstmt=null;  
              //
             try{
              pstmt	= DB.prepareStatement(selectSQL, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, trxName);
              pstmt.setString(1, TableName);
              int incrementNo=1;
              ResultSet	rs	= pstmt.executeQuery();

              if (CLogMgt.isLevelFinest()) {
					s_log.finest("Isolation Level=" +  Connection.TRANSACTION_READ_COMMITTED	+ ") - RSType=" + pstmt.getResultSetType() + "("+ ResultSet.TYPE_SCROLL_SENSITIVE + "), RSConcur="
							+ pstmt.getResultSetConcurrency() + "("	+ ResultSet.CONCUR_UPDATABLE + ")");
				}

				if (rs.next()) {

					if (OXPSYS) {
						retValue = rs.getInt(2);
					} else {
						incrementNo = rs.getInt(3);
						retValue = rs.getInt(1) + incrementNo;
					}
				}
				else {
					// Aquí ya estamos en una situacion donde no hay configuracion alguna sobre la secuencia de la tabla en cuestion 
					retValue = 1010001;
				}
				String createSeq = "create sequence " + secuencia	+ " INCREMENT BY " + incrementNo + " START WITH " + (retValue + 1);
				pstmt=null;
				pstmt=DB.prepareStatement(createSeq,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE, trxName);
				pstmt.execute();
				rs.close();
				pstmt.close();
				pstmt=null;
				rs=null;
				
				sequences_cache.add(secuencia);
              
             }catch (Exception ex)
             {
            	 ex.printStackTrace();
            	 
             }
             try {

                 if (pstmt != null) {
                     pstmt.close();
                 }
                 pstmt	= null;

        } catch (Exception ez) {

            s_log.log(Level.SEVERE, "MSequence - Error al crear nueva secuencia", ez);
            pstmt	= null;
        	}

		}
	
        return retValue;
    }		// getNextID

    
    public static int getNextSequenceID(int AD_Client_ID, String sequenceName, String trxName) {
    	
        String	selectSQL	= null;    	
        int	retValue	= -1;
        // Check OXPSYS
        boolean	OXPSYS	= Ini.getPropertyBool(Ini.P_OXPSYS);

        
        if ((sequenceName == null) || (sequenceName.length() == 0)) {
            throw new IllegalArgumentException("SequenceName missing");
        }

        
        if (OXPSYS && (AD_Client_ID > 11)) {
            OXPSYS	= false;
        }

        //
        if (CLogMgt.isLevel(LOGLEVEL)) {
            s_log.log(LOGLEVEL, sequenceName + " - OXPSYS=" + OXPSYS + " [" + trxName + "]");
        }

      
		selectSQL	= "SELECT nextval(?)";
	    PreparedStatement	pstmt	= null;
	   
		try {
			pstmt=DB.prepareStatement(selectSQL,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
			pstmt.setString(1,sequenceName);
			ResultSet	rs	= pstmt.executeQuery();    
			if (rs.next())
				retValue = rs.getInt(1);
			rs.close();
			pstmt.close();
			rs=null;
			pstmt=null;
		
		} catch (Exception ez) {

            s_log.log(Level.SEVERE, "Finish", ez);
            pstmt	= null;
        }
	
        return retValue;
    }		// getNextID
    
    
    /**
     * Descripción de Clase
     *
     *
     * @versión    2.1, 02.jul 2007
     * @autor     Fundesle    
     */
    public static class GetIDs implements Runnable {

        /** Descripción de Campo */
        private int	m_i;

        /**
         * Constructor ...
         *
         *
         * @param i
         */
        public GetIDs(int i) {
            m_i	= i;
        }

        /**
         * Descripción de Método
         *
         */
        public void run() {

            for (int i = 0; i < 100; i++) {

                try {

                    int	no	= DB.getNextID(0, "Test", null);

                    s_list.add(new Integer(no));

                    // System.out.println("#" + m_i + ": " + no);

                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }		// GetIDs
    
    public static void resetCache() {
    	sequences_cache = new HashSet<String>();
    }
    
    
}		// MSequence



/*
 * @(#)MSequence.java   02.jul 2007
 * 
 *  Fin del fichero MSequence.java
 *  
 *  Versión 2.1  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
