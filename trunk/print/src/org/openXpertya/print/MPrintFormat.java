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



package org.openXpertya.print;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import javax.sql.RowSet;

import org.openXpertya.model.MQuery;
import org.openXpertya.model.MRole;
import org.openXpertya.model.PO;
import org.openXpertya.model.X_AD_PrintFormat;
import org.openXpertya.model.X_AD_PrintFormatItem;
import org.openXpertya.model.X_AD_PrintFormatItem_Trl;
import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.CPreparedStatement;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Language;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @versión    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MPrintFormat extends X_AD_PrintFormat {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param AD_PrintFormat_ID
     * @param trxName
     */

    public MPrintFormat( Properties ctx,int AD_PrintFormat_ID,String trxName ) {
        super( ctx,AD_PrintFormat_ID,trxName );

        // Language=[Deutsch,Locale=de_DE,AD_Language=en_US,DatePattern=DD.MM.YYYY,DecimalPoint=false]

        m_language = Language.getLoginLanguage();

        if( AD_PrintFormat_ID == 0 ) {
            setStandardHeaderFooter( true );
            setIsTableBased( true );
            setIsForm( false );
            setIsDefault( false );
        }

        m_items = getItems();
    }    // MPrintFormat

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MPrintFormat( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
        m_language = Language.getLoginLanguage();
        m_items    = getItems();
    }    // MPrintFormat

    /** Descripción de Campos */

    private MPrintFormatItem[] m_items = null;

    /** Descripción de Campos */

    private String m_translationViewLanguage = null;

    /** Descripción de Campos */

    private Language m_language;

    /** Descripción de Campos */

    private MPrintTableFormat m_tFormat;

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MPrintFormat.class );

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Language getLanguage() {
        return m_language;
    }    // getLanguage

    /**
     * Descripción de Método
     *
     *
     * @param language
     */

    public void setLanguage( Language language ) {
        if( language != null ) {
            m_language = language;

            // log.fine("setLanguage - " + language);

        }

        m_translationViewLanguage = null;
    }    // getLanguage

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int[] getOrderAD_Column_IDs() {
        HashMap map = new HashMap();    // SortNo - AD_Column_ID

        for( int i = 0;i < m_items.length;i++ ) {

            // Sort Order and Column must be > 0

            if( (m_items[ i ].getSortNo() != 0) && (m_items[ i ].getAD_Column_ID() != 0) ) {
                map.put( new Integer( m_items[ i ].getSortNo()),new Integer( m_items[ i ].getAD_Column_ID()));
            }
        }

        // Get SortNo and Sort them

        Integer[] keys = new Integer[ map.keySet().size()];

        map.keySet().toArray( keys );
        Arrays.sort( keys );

        // Create AD_Column_ID array

        int[] retValue = new int[ keys.length ];

        for( int i = 0;i < keys.length;i++ ) {
            Integer value = ( Integer )map.get( keys[ i ] );

            retValue[ i ] = value.intValue();
        }

        return retValue;
    }    // getOrderAD_Column_IDs

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int[] getAD_Column_IDs() {
        ArrayList list = new ArrayList();

        for( int i = 0;i < m_items.length;i++ ) {
            if( (m_items[ i ].getAD_Column_ID() != 0) && m_items[ i ].isPrinted()) {
                list.add( new Integer( m_items[ i ].getAD_Column_ID()));
            }
        }

        // Convert

        int[] retValue = new int[ list.size()];

        for( int i = 0;i < list.size();i++ ) {
            retValue[ i ] = (( Integer )list.get( i )).intValue();
        }

        return retValue;
    }    // getAD_Column_IDs

    /**
     * Descripción de Método
     *
     *
     * @param items
     */

    private void setItems( MPrintFormatItem[] items ) {
        if( items != null ) {
            m_items = items;
        }
    }    // setItems

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private MPrintFormatItem[] getItems() {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM AD_PrintFormatItem pfi " + "WHERE pfi.AD_PrintFormat_ID=? AND pfi.IsActive='Y'"

        // Display restrictions - Passwords, etc.

        + " AND NOT EXISTS (SELECT * FROM AD_Field f " + "WHERE pfi.AD_Column_ID=f.AD_Column_ID" + " AND (f.IsEncrypted='Y' OR f.ObscureType IS NOT NULL))" + "ORDER BY SeqNo";
        MRole role = MRole.getDefault( getCtx(),false );

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,getID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MPrintFormatItem pfi = new MPrintFormatItem( p_ctx,rs,get_TrxName());

                if( role.isColumnAccess( getAD_Table_ID(),pfi.getAD_Column_ID(),true )) {
                    list.add( pfi );
                }
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"getItems",e );
        }

        //

        MPrintFormatItem[] retValue = new MPrintFormatItem[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getItems

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getItemCount() {
        if( m_items == null ) {
            return -1;
        }

        return m_items.length;
    }    // getItemCount

    /**
     * Descripción de Método
     *
     *
     * @param index
     *
     * @return
     */

    public MPrintFormatItem getItem( int index ) {
        if( (index < 0) || (index >= m_items.length) ) {
            throw new ArrayIndexOutOfBoundsException( "Index=" + index + " - Length=" + m_items.length );
        }

        return m_items[ index ];
    }    // getItem

    /**
     * Descripción de Método
     *
     */

    public void setTranslation() {
    	// Obtengo todos los elementos del formato de impresión actual
    	List<PO> elements = PO.find(getCtx(), "AD_PrintFormatItem", "AD_PrintFormat_ID = ?", new Object[]{getID()}, null, get_TrxName());
    	List<PO> trls = null; 
    	// Itero por ellos y actualizo sus trls
    	for (PO element : elements) {
			trls = PO.find(getCtx(), "AD_PrintFormatItem_Trl", "AD_PrintFormatItem_ID = ?", new Object[]{element.getID()}, null, get_TrxName());
			for (PO elemTrl : trls) {
				((X_AD_PrintFormatItem_Trl)elemTrl).setPrintName(((X_AD_PrintFormatItem)element).getPrintName());
				((X_AD_PrintFormatItem_Trl)elemTrl).setPrintNameSuffix(((X_AD_PrintFormatItem)element).getPrintNameSuffix());
				if(!elemTrl.save()){
					log.severe("Error al actualizar las traducciones de los elementos");
				}
			}
		}
    }    // setTranslation

    /**
     * Descripción de Método
     *
     *
     * @param standardHeaderFooter
     */

    public void setStandardHeaderFooter( boolean standardHeaderFooter ) {
        super.setIsStandardHeaderFooter( standardHeaderFooter );

        if( standardHeaderFooter ) {
            setFooterMargin( 0 );
            setHeaderMargin( 0 );
        }
    }    // setSatndardHeaderFooter

    /**
     * Descripción de Método
     *
     *
     * @param tableBased
     */

    public void setIsTableBased( boolean tableBased ) {
        super.setIsTableBased( tableBased );

        if( tableBased ) {
            super.setIsForm( false );
        }
    }    // setIsTableBased

    /**
     * Descripción de Método
     *
     *
     * @param language
     */

    public void setTranslationLanguage( Language language ) {
        if( (language == null) || language.isBaseLanguage()) {
            log.info( "Ignored - " + language );
            m_translationViewLanguage = null;
        } else {
            log.info( "Language=" + language.getAD_Language());
            m_translationViewLanguage = language.getAD_Language();
            m_language                = language;
        }
    }    // setTranslationLanguage

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isTranslationView() {
        return m_translationViewLanguage != null;
    }    // isTranslationView

    /**
     * Descripción de Método
     *
     *
     * @param query
     */

    public void setTranslationViewQuery( MQuery query ) {

        // Set Table Name and add add restriction, if a view and language set

        if( (m_translationViewLanguage != null) && (query != null) && query.getTableName().toUpperCase().endsWith( "_V" )) {
            query.setTableName( query.getTableName() + "t" );
           // query.addRestriction( "AD_Language",MQuery.EQUAL,m_translationViewLanguage );
        }
    }    // setTranslationViewQuery

    /**
     * Descripción de Método
     *
     *
     * @param AD_PrintTableFormat_ID
     */

    public void setAD_PrintTableFormat_ID( int AD_PrintTableFormat_ID ) {
        super.setAD_PrintTableFormat_ID( AD_PrintTableFormat_ID );
        m_tFormat = MPrintTableFormat.get( getCtx(),AD_PrintTableFormat_ID,getAD_PrintFont_ID());
    }    // getAD_PrintTableFormat_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MPrintTableFormat getTableFormat() {
        if( m_tFormat == null ) {
            m_tFormat = MPrintTableFormat.get( getCtx(),getAD_PrintTableFormat_ID(),getAD_PrintFont_ID());
        }

        return m_tFormat;
    }    // getTableFormat

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MPrintFormat[ID=" ).append( getID()).append( ",Name=" ).append( getName()).append( ",Language=" ).append( getLanguage()).append( ",Items=" ).append( getItemCount()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param rs
     * @param index
     *
     * @return
     *
     * @throws SQLException
     */

    protected Object loadSpecial( ResultSet rs,int index ) throws SQLException {

        // CreateCopy
        // log.config( "MPrintFormat.loadSpecial", p_info.getColumnName(index));

        return null;
    }    // loadSpecial

    /**
     * Descripción de Método
     *
     *
     * @param value
     * @param index
     *
     * @return
     */

    protected String saveNewSpecial( Object value,int index ) {

        // CreateCopy
        // String colName = p_info.getColumnName(index);
        // String colClass = p_info.getColumnClass(index).toString();
        // String colValue = value == null ? "null" : value.getClass().toString();
        // log.log(Level.SEVERE, "PO.saveNewSpecial - Unknown class for column " + colName + " (" + colClass + ") - Value=" + colValue);

        if( value == null ) {
            return "NULL";
        }

        return value.toString();
    }    // saveNewSpecial

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_Table_ID
     *
     * @return
     */

    static public MPrintFormat createFromTable( Properties ctx,int AD_Table_ID ) {
        return createFromTable( ctx,AD_Table_ID,0 );
    }    // createFromTable

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_Table_ID
     * @param AD_PrintFormat_ID
     *
     * @return
     */

    static public MPrintFormat createFromTable( Properties ctx,int AD_Table_ID,int AD_PrintFormat_ID ) {
        int AD_Client_ID = Env.getAD_Client_ID( ctx );

        s_log.info( "AD_Table_ID=" + AD_Table_ID + " - AD_Client_ID=" + AD_Client_ID );

        MPrintFormat pf = new MPrintFormat( ctx,AD_PrintFormat_ID,null );

        pf.setAD_Table_ID( AD_Table_ID );

        // Get Info

        String sql = "SELECT TableName,"                                                                                                                                                                                          // 1
                     + " (SELECT COUNT(*) FROM AD_PrintFormat x WHERE x.AD_Table_ID=t.AD_Table_ID AND x.AD_Client_ID=c.AD_Client_ID) AS Count," + " COALESCE (cpc.AD_PrintColor_ID, pc.AD_PrintColor_ID) AS AD_PrintColor_ID,"    // 3
                     + " COALESCE (cpf.AD_PrintFont_ID, pf.AD_PrintFont_ID) AS AD_PrintFont_ID," + " COALESCE (cpp.AD_PrintPaper_ID, pp.AD_PrintPaper_ID) AS AD_PrintPaper_ID " + "FROM AD_Table t, AD_Client c" + " LEFT OUTER JOIN AD_PrintColor cpc ON (cpc.AD_Client_ID=c.AD_Client_ID AND cpc.IsDefault='Y')" + " LEFT OUTER JOIN AD_PrintFont cpf ON (cpf.AD_Client_ID=c.AD_Client_ID AND cpf.IsDefault='Y')" + " LEFT OUTER JOIN AD_PrintPaper cpp ON (cpp.AD_Client_ID=c.AD_Client_ID AND cpp.IsDefault='Y')," + " AD_PrintColor pc, AD_PrintFont pf, AD_PrintPaper pp " + "WHERE t.AD_Table_ID=? AND c.AD_Client_ID=?"    // #1/2
                     + " AND pc.IsDefault='Y' AND pf.IsDefault='Y' AND pp.IsDefault='Y'";
        boolean error = true;

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,AD_Table_ID );
            pstmt.setInt( 2,AD_Client_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {

                // Name

                String TableName  = rs.getString( 1 ).toLowerCase();
                String ColumnName = TableName + "_ID";
                String s          = ColumnName;

                if( !ColumnName.equals( "T_Report_ID" )) {
                    s = Msg.translate( ctx,ColumnName );

                    if( ColumnName.equals( s )) {    // not found
                        s = Msg.translate( ctx,TableName );
                    }
                }

                int count = rs.getInt( 2 );

                if( count > 0 ) {
                    s += "_" + ( count + 1 );
                }

                pf.setName( s );

                //

                pf.setAD_PrintColor_ID( rs.getInt( 3 ));
                pf.setAD_PrintFont_ID( rs.getInt( 4 ));
                pf.setAD_PrintPaper_ID( rs.getInt( 5 ));

                //

                error = false;
            } else {
                s_log.log( Level.SEVERE,"No info found " + AD_Table_ID );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            s_log.log( Level.SEVERE,"createFromTable",e );
        }

        if( error ) {
            return null;
        }

        // Save & complete

        if( !pf.save()) {
            return null;
        }

        // pf.dump();

        pf.setItems( createItems( ctx,pf ));

        //

        return pf;
    }    // createFromTable

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_ReportView_ID
     * @param ReportName
     *
     * @return
     */

    static public MPrintFormat createFromReportView( Properties ctx,int AD_ReportView_ID,String ReportName ) {
        int AD_Client_ID = Env.getAD_Client_ID( ctx );

        s_log.info( "AD_ReportView_ID=" + AD_ReportView_ID + " - AD_Client_ID=" + AD_Client_ID + " - " + ReportName );

        MPrintFormat pf = new MPrintFormat( ctx,0,null );

        pf.setAD_ReportView_ID( AD_ReportView_ID );

        // Get Info

        String sql = "SELECT t.TableName," + " (SELECT COUNT(*) FROM AD_PrintFormat x WHERE x.AD_ReportView_ID=rv.AD_ReportView_ID AND x.AD_Client_ID=c.AD_Client_ID) AS Count," + " COALESCE (cpc.AD_PrintColor_ID, pc.AD_PrintColor_ID) AS AD_PrintColor_ID," + " COALESCE (cpf.AD_PrintFont_ID, pf.AD_PrintFont_ID) AS AD_PrintFont_ID," + " COALESCE (cpp.AD_PrintPaper_ID, pp.AD_PrintPaper_ID) AS AD_PrintPaper_ID," + " t.AD_Table_ID " + "FROM AD_ReportView rv" + " INNER JOIN AD_Table t ON (rv.AD_Table_ID=t.AD_Table_ID)," + " AD_Client c" + " LEFT OUTER JOIN AD_PrintColor cpc ON (cpc.AD_Client_ID=c.AD_Client_ID AND cpc.IsDefault='Y')" + " LEFT OUTER JOIN AD_PrintFont cpf ON (cpf.AD_Client_ID=c.AD_Client_ID AND cpf.IsDefault='Y')" + " LEFT OUTER JOIN AD_PrintPaper cpp ON (cpp.AD_Client_ID=c.AD_Client_ID AND cpp.IsDefault='Y')," + " AD_PrintColor pc, AD_PrintFont pf, AD_PrintPaper pp " + "WHERE rv.AD_ReportView_ID=? AND c.AD_Client_ID=?" + " AND pc.IsDefault='Y' AND pf.IsDefault='Y' AND pp.IsDefault='Y'";
        boolean error = true;

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,AD_ReportView_ID );
            pstmt.setInt( 2,AD_Client_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {

                // Name

                String name = ReportName;

                if( (name == null) || (name.length() == 0) ) {
                    name = rs.getString( 1 );    // TableName
                }

                int count = rs.getInt( 2 );

                if( count > 0 ) {
                    name += "_" + count;
                }

                pf.setName( name );

                //

                pf.setAD_PrintColor_ID( rs.getInt( 3 ));
                pf.setAD_PrintFont_ID( rs.getInt( 4 ));
                pf.setAD_PrintPaper_ID( rs.getInt( 5 ));

                //

                pf.setAD_Table_ID( rs.getInt( 6 ));
                error = false;
            } else {
                s_log.log( Level.SEVERE,"Not found: AD_ReportView_ID=" + AD_ReportView_ID );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            s_log.log( Level.SEVERE,"createFromReportView",e );
        }

        if( error ) {
            return null;
        }

        // Save & complete

        if( !pf.save()) {
            return null;
        }

        // pf.dump();

        pf.setItems( createItems( ctx,pf ));

        //

        return pf;
    }    // createFromReportView

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param format
     *
     * @return
     */

    static private MPrintFormatItem[] createItems( Properties ctx,MPrintFormat format ) {
        s_log.fine( "From window Tab ..." );

        ArrayList list = new ArrayList();

        // Get Column List from Tab

        String sql = "SELECT AD_Column_ID "    // , Name, IsDisplayed, SeqNo
                     + "FROM AD_Field " + "WHERE AD_Tab_ID=(SELECT min(AD_Tab_ID) FROM AD_Tab WHERE AD_Table_ID=?)" + " AND IsEncrypted='N' AND ObscureType IS NULL " + "ORDER BY COALESCE(IsDisplayed,'N') DESC, SortNo, SeqNo, Name";
        
        // Properties ctx = getCtx();
        
        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,format.getAD_Table_ID());

            ResultSet rs    = pstmt.executeQuery();
            int       seqNo = 1;

            while( rs.next()) {
                MPrintFormatItem pfi = MPrintFormatItem.createFromColumn( format,rs.getInt( 1 ),seqNo++ );

                if( pfi != null ) {
                    list.add( pfi );
                    s_log.finest( "Tab: " + pfi );
                }
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            s_log.log( Level.SEVERE,"createItems (tab)",e );
        }

        // No Tab found for Table

        if( list.size() == 0 ) {
            s_log.fine( "From Table ..." );
            sql = "SELECT AD_Column_ID " + "FROM AD_Column " + "WHERE AD_Table_ID=? " + "ORDER BY IsIdentifier DESC, SeqNo, Name";

            try {
                PreparedStatement pstmt = DB.prepareStatement( sql );

                pstmt.setInt( 1,format.getAD_Table_ID());

                ResultSet rs    = pstmt.executeQuery();
                int       seqNo = 1;

                while( rs.next()) {
                    MPrintFormatItem pfi = MPrintFormatItem.createFromColumn( format,rs.getInt( 1 ),seqNo++ );

                    if( pfi != null ) {
                        list.add( pfi );
                        s_log.finest( "Table: " + pfi );
                    }
                }

                rs.close();
                pstmt.close();
            } catch( SQLException e ) {
                s_log.log( Level.SEVERE,"createItems (table)",e );
            }
        }

        //

        MPrintFormatItem[] retValue = new MPrintFormatItem[ list.size()];

        list.toArray( retValue );
        s_log.info( format + " - #" + retValue.length );

        return retValue;
    }    // createItems

    /**
     * Descripción de Método
     *
     *
     * @param fromFormat
     * @param toFormat
     *
     * @return
     */

    static private MPrintFormatItem[] copyItems( MPrintFormat fromFormat,MPrintFormat toFormat ) {
        s_log.info( "From=" + fromFormat );

        ArrayList          list  = new ArrayList();
        MPrintFormatItem[] items = fromFormat.getItems();

        for( int i = 0;i < items.length;i++ ) {
            MPrintFormatItem pfi = items[ i ].copyToClient( toFormat.getAD_Client_ID(),toFormat.getID());

            if( pfi != null ) {
                list.add( pfi );
            }
        }

        //

        MPrintFormatItem[] retValue = new MPrintFormatItem[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // copyItems

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param from_AD_PrintFormat_ID
     * @param to_AD_PrintFormat_ID
     *
     * @return
     */

    public static MPrintFormat copy( Properties ctx,int from_AD_PrintFormat_ID,int to_AD_PrintFormat_ID ) {
        return copy( ctx,from_AD_PrintFormat_ID,to_AD_PrintFormat_ID,-1 );
    }    // copy

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_PrintFormat_ID
     * @param to_Client_ID
     *
     * @return
     */

    public static MPrintFormat copyToClient( Properties ctx,int AD_PrintFormat_ID,int to_Client_ID ) {
        return copy( ctx,AD_PrintFormat_ID,0,to_Client_ID );
    }    // copy

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param from_AD_PrintFormat_ID
     * @param to_AD_PrintFormat_ID
     * @param to_Client_ID
     *
     * @return
     */

    private static MPrintFormat copy( Properties ctx,int from_AD_PrintFormat_ID,int to_AD_PrintFormat_ID,int to_Client_ID ) {
        s_log.info( "From AD_PrintFormat_ID=" + from_AD_PrintFormat_ID + ", To AD_PrintFormat_ID=" + to_AD_PrintFormat_ID + ", To Client_ID=" + to_Client_ID );

        if( from_AD_PrintFormat_ID == 0 ) {
            throw new IllegalArgumentException( "From_AD_PrintFormat_ID is 0" );
        }

        //

        MPrintFormat from = new MPrintFormat( ctx,from_AD_PrintFormat_ID,null );
        MPrintFormat to = new MPrintFormat( ctx,to_AD_PrintFormat_ID,null );    // could be 0

        MPrintFormat.copyValues( from,to );

        // New

        if( to_AD_PrintFormat_ID == 0 ) {
            if( to_Client_ID < 0 ) {
                to_Client_ID = Env.getAD_Client_ID( ctx );
            }

            to.setClientOrg( to_Client_ID,0 );
        }

        // Set Name - Remove TEMPLATE - add copy

        to.setName( Util.replace( to.getName(),"TEMPLATE",String.valueOf( to_Client_ID )));
        to.setName( to.getName() + " " + Msg.getMsg( ctx,"Copy" ) + " " + to.hashCode());    // unique name

        //

        to.save();

        // Copy Items

        to.setItems( copyItems( from,to ));

        return to;
    }    // copyToClient

    /** Descripción de Campos */

    static private CCache s_formats = new CCache( "AD_PrintFormat",30 );

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_PrintFormat_ID
     * @param readFromDisk
     *
     * @return
     */

    static public MPrintFormat get( Properties ctx,int AD_PrintFormat_ID,boolean readFromDisk ) {
        Integer      key = new Integer( AD_PrintFormat_ID );
        MPrintFormat pf  = null;

        if( !readFromDisk ) {
            pf = ( MPrintFormat )s_formats.get( key );
        }

        if( pf == null ) {
            pf = new MPrintFormat( ctx,AD_PrintFormat_ID,null );
            s_formats.put( key,pf );
        }

        return pf;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_ReportView_ID
     * @param AD_Table_ID
     *
     * @return
     */

    static public MPrintFormat get( Properties ctx,int AD_ReportView_ID,int AD_Table_ID ) {
        MPrintFormat      retValue = null;
        PreparedStatement pstmt    = null;
        String            sql      = "SELECT * FROM AD_PrintFormat WHERE ";

        if( AD_ReportView_ID > 0 ) {
            sql += "AD_ReportView_ID=?";
        } else {
            sql += "AD_Table_ID=?";
        }

        sql += " ORDER BY IsDefault DESC";

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,(AD_ReportView_ID > 0)
                            ?AD_ReportView_ID
                            :AD_Table_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = new MPrintFormat( ctx,rs,null );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"get",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        return retValue;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param AD_PrintFormat_ID
     */

    static public void deleteFromCache( int AD_PrintFormat_ID ) {
        Integer key = new Integer( AD_PrintFormat_ID );

        s_formats.put( key,null );
    }    // deleteFromCache

    /**
     * Descripción de Método
     *
     */

    public void updateItems() {
        m_items = getItems();
    }

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    static public void main( String[] args ) {
        org.openXpertya.OpenXpertya.startup( true );
    }    // main
    
    /**
	 * @param AD_Table_ID
	 * @param AD_Client_ID use -1 to retrieve from all client 
	 * @param trxName
	 */
	public static RowSet getAccessiblePrintFormats (int AD_Table_ID, int AD_Client_ID, String trxName)
	{
		RowSet rowSet = null;
		String sql = "SELECT AD_PrintFormat_ID, Name, AD_Client_ID "
			+ "FROM AD_PrintFormat "
			+ "WHERE AD_Table_ID=? AND IsTableBased='Y' ";
		if (AD_Client_ID >= 0)
		{
			sql = sql + " AND AD_Client_ID = ? ";
		}
		sql = sql + "ORDER BY AD_Client_ID DESC, IsDefault DESC, Name"; //	Own First 
		//
		sql = MRole.getDefault().addAccessSQL (
			sql, "AD_PrintFormat", MRole.SQL_NOTQUALIFIED, MRole.SQL_RO);
		CPreparedStatement pstmt = null;
		try
		{
			pstmt = DB.prepareStatement(sql, trxName);
			pstmt.setInt(1, AD_Table_ID);
			if (AD_Client_ID >= 0)
				pstmt.setInt(2, AD_Client_ID);
			rowSet = pstmt.getRowSet();
		}
		catch (SQLException e)
		{
			s_log.log(Level.SEVERE, sql, e);
		}
		finally {
			DB.close(pstmt);
			pstmt = null;
		}
		
		return rowSet;
	}

}    // MPrintFormat



/*
 *  @(#)MPrintFormat.java   23.03.06
 * 
 *  Fin del fichero MPrintFormat.java
 *  
 *  Versión 2.0
 *
 */
