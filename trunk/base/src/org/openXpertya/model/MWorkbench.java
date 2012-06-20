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

import java.awt.Dimension;
import java.awt.Image;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.Icon;

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

public class MWorkbench implements Serializable {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     */

    public MWorkbench( Properties ctx ) {
        m_ctx = ctx;
    }    // MWorkbench

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param AD_Window_ID
     */

    public MWorkbench( Properties ctx,int AD_Window_ID ) {
        m_ctx = ctx;
        m_windows.add( new WBWindow( TYPE_WINDOW,AD_Window_ID ));
    }    // MWorkbench

    /** Descripción de Campos */

    private Properties m_ctx;

    /** Descripción de Campos */

    private ArrayList m_windows = new ArrayList();

    /** Descripción de Campos */

    private int AD_Workbench_ID = 0;

    /** Descripción de Campos */

    private String Name = "";

    /** Descripción de Campos */

    private String Description = "";

    /** Descripción de Campos */

    private String Help = "";

    /** Descripción de Campos */

    private int AD_Column_ID = 0;

    /** Descripción de Campos */

    private int AD_Image_ID = 0;

    /** Descripción de Campos */

    private int AD_Color_ID = 0;

    /** Descripción de Campos */

    private int PA_Goal_ID = 0;

    /** Descripción de Campos */

    private String ColumnName = "";

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( MWorkbench.class );

    /**
     * Descripción de Método
     *
     *
     * @param ad_Workbench_ID
     *
     * @return
     */

    public boolean initWorkbench( int ad_Workbench_ID ) {
        AD_Workbench_ID = ad_Workbench_ID;

        // Get WB info

        String sql = null;

        if( Env.isBaseLanguage( m_ctx,"AD_Workbench" )) {
            sql = "SELECT w.Name,w.Description,w.Help,"                            // 1..3
                  + " w.AD_Column_ID,w.AD_Image_ID,w.AD_Color_ID,w.PA_Goal_ID,"    // 4..7
                  + " c.ColumnName "                                                     // 8
                  + "FROM AD_Workbench w, AD_Column c " + "WHERE w.AD_Workbench_ID=?"    // #1
                  + " AND w.IsActive='Y'" + " AND w.AD_Column_ID=c.AD_Column_ID";
        } else {
            sql = "SELECT t.Name,t.Description,t.Help," + " w.AD_Column_ID,w.AD_Image_ID,w.AD_Color_ID,w.PA_Goal_ID," + " c.ColumnName " + "FROM AD_Workbench w, AD_Workbench_Trl t, AD_Column c " + "WHERE w.AD_Workbench_ID=?"    // #1
                  + " AND w.IsActive='Y'" + " AND w.AD_Workbench_ID=t.AD_Workbench_ID" + " AND t.AD_Language='" + Env.getAD_Language( m_ctx ) + "'" + " AND w.AD_Column_ID=c.AD_Column_ID";
        }

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,AD_Workbench_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                Name        = rs.getString( 1 );
                Description = rs.getString( 2 );

                if( Description == null ) {
                    Description = "";
                }

                Help = rs.getString( 3 );

                if( Help == null ) {
                    Help = "";
                }

                //

                AD_Column_ID = rs.getInt( 4 );
                AD_Image_ID  = rs.getInt( 5 );
                AD_Color_ID  = rs.getInt( 6 );
                PA_Goal_ID   = rs.getInt( 7 );
                ColumnName   = rs.getString( 8 );
            } else {
                AD_Workbench_ID = 0;
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"MWorkbench.initWorkbench",e );
        }

        if( AD_Workbench_ID == 0 ) {
            return false;
        }

        return initWorkbenchWindows();
    }    // initWorkbench

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        return "MWorkbench ID=" + AD_Workbench_ID + " " + Name + ", windows=" + m_windows.size() + ", LinkColumn=" + ColumnName;
    }    // toString

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        for( int i = 0;i < m_windows.size();i++ ) {
            dispose( i );
        }

        m_windows.clear();
        m_windows = null;
    }    // dispose

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MQuery getQuery() {
        return MQuery.getEqualQuery( ColumnName,"@#" + ColumnName + "@" );
    }    // getQuery

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_Workbench_ID() {
        return AD_Workbench_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getName() {
        return Name;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDescription() {
        return Description;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getHelp() {
        return Help;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_Column_ID() {
        return AD_Column_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_Image_ID() {
        return AD_Image_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_Color_ID() {
        return AD_Color_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getPA_Goal_ID() {
        return PA_Goal_ID;
    }

    /** Descripción de Campos */

    public static final int TYPE_WINDOW = 1;

    /** Descripción de Campos */

    public static final int TYPE_FORM = 2;

    /** Descripción de Campos */

    public static final int TYPE_PROCESS = 3;

    /** Descripción de Campos */

    public static final int TYPE_TASK = 4;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean initWorkbenchWindows() {
        String sql = "SELECT AD_Window_ID, AD_Form_ID, AD_Process_ID, AD_Task_ID " + "FROM AD_WorkbenchWindow " + "WHERE AD_Workbench_ID=? AND IsActive='Y'" + "ORDER BY SeqNo";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,AD_Workbench_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                int AD_Window_ID  = rs.getInt( 1 );
                int AD_Form_ID    = rs.getInt( 2 );
                int AD_Process_ID = rs.getInt( 3 );
                int AD_Task_ID    = rs.getInt( 4 );

                //

                if( AD_Window_ID > 0 ) {
                    m_windows.add( new WBWindow( TYPE_WINDOW,AD_Window_ID ));
                } else if( AD_Form_ID > 0 ) {
                    m_windows.add( new WBWindow( TYPE_FORM,AD_Form_ID ));
                } else if( AD_Process_ID > 0 ) {
                    m_windows.add( new WBWindow( TYPE_PROCESS,AD_Process_ID ));
                } else if( AD_Task_ID > 0 ) {
                    m_windows.add( new WBWindow( TYPE_TASK,AD_Task_ID ));
                }
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"MWorkbench.initWorkbenchWindows",e );

            return false;
        }

        return true;
    }    // initWorkbenchWindows

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getWindowCount() {
        return m_windows.size();
    }    // getWindowCount

    /**
     * Descripción de Método
     *
     *
     * @param index
     *
     * @return
     */

    public int getWindowType( int index ) {
        if( (index < 0) || (index > m_windows.size())) {
            return -1;
        }

        WBWindow win = ( WBWindow )m_windows.get( index );

        return win.Type;
    }    // getWindowType

    /**
     * Descripción de Método
     *
     *
     * @param index
     *
     * @return
     */

    public int getWindowID( int index ) {
        if( (index < 0) || (index > m_windows.size())) {
            return -1;
        }

        WBWindow win = ( WBWindow )m_windows.get( index );

        return win.ID;
    }    // getWindowID

    /**
     * Descripción de Método
     *
     *
     * @param index
     * @param mw
     */

    public void setMWindow( int index,MWindow mw ) {
        if( (index < 0) || (index > m_windows.size())) {
            throw new IllegalArgumentException( "MWorkbench.setMWindow - Index invalid: " + index );
        }

        WBWindow win = ( WBWindow )m_windows.get( index );

        if( win.Type != TYPE_WINDOW ) {
            throw new IllegalArgumentException( "MWorkbench.setMWindow - Not a MWindow: " + index );
        }

        win.mWindow = mw;
    }    // setMWindow

    /**
     * Descripción de Método
     *
     *
     * @param index
     *
     * @return
     */

    public MWindow getMWindow( int index ) {
        if( (index < 0) || (index > m_windows.size())) {
            throw new IllegalArgumentException( "MWorkbench.getMWindow - Index invalid: " + index );
        }

        WBWindow win = ( WBWindow )m_windows.get( index );

        if( win.Type != TYPE_WINDOW ) {
            throw new IllegalArgumentException( "MWorkbench.getMWindow - Not a MWindow: " + index );
        }

        return win.mWindow;
    }    // getMWindow

    /**
     * Descripción de Método
     *
     *
     * @param index
     *
     * @return
     */

    public String getName( int index ) {
        if( (index < 0) || (index > m_windows.size())) {
            throw new IllegalArgumentException( "MWorkbench.getName - Index invalid: " + index );
        }

        WBWindow win = ( WBWindow )m_windows.get( index );

        if( (win.mWindow != null) && (win.Type == TYPE_WINDOW) ) {
            return win.mWindow.getName();
        }

        return null;
    }    // getName

    /**
     * Descripción de Método
     *
     *
     * @param index
     *
     * @return
     */

    public String getDescription( int index ) {
        if( (index < 0) || (index > m_windows.size())) {
            throw new IllegalArgumentException( "MWorkbench.getDescription - Index invalid: " + index );
        }

        WBWindow win = ( WBWindow )m_windows.get( index );

        if( (win.mWindow != null) && (win.Type == TYPE_WINDOW) ) {
            return win.mWindow.getDescription();
        }

        return null;
    }    // getDescription

    /**
     * Descripción de Método
     *
     *
     * @param index
     *
     * @return
     */

    public String getHelp( int index ) {
        if( (index < 0) || (index > m_windows.size())) {
            throw new IllegalArgumentException( "MWorkbench.getHelp - Index invalid: " + index );
        }

        WBWindow win = ( WBWindow )m_windows.get( index );

        if( (win.mWindow != null) && (win.Type == TYPE_WINDOW) ) {
            return win.mWindow.getHelp();
        }

        return null;
    }    // getHelp

    /**
     * Descripción de Método
     *
     *
     * @param index
     *
     * @return
     */

    public Icon getIcon( int index ) {
        if( (index < 0) || (index > m_windows.size())) {
            throw new IllegalArgumentException( "Index invalid: " + index );
        }

        WBWindow win = ( WBWindow )m_windows.get( index );

        if( (win.mWindow != null) && (win.Type == TYPE_WINDOW) ) {
            return win.mWindow.getIcon();
        }

        return null;
    }    // getIcon

    /**
     * Descripción de Método
     *
     *
     * @param index
     *
     * @return
     */

    public Image getImage( int index ) {
        if( (index < 0) || (index > m_windows.size())) {
            throw new IllegalArgumentException( "Index invalid: " + index );
        }

        WBWindow win = ( WBWindow )m_windows.get( index );

        if( (win.mWindow != null) && (win.Type == TYPE_WINDOW) ) {
            return win.mWindow.getImage();
        }

        return null;
    }    // getImage

    /**
     * Descripción de Método
     *
     *
     * @param index
     *
     * @return
     */

    public int getAD_Color_ID( int index ) {
        if( (index < 0) || (index > m_windows.size())) {
            throw new IllegalArgumentException( "Index invalid: " + index );
        }

        WBWindow win      = ( WBWindow )m_windows.get( index );
        int      retValue = -1;

        // if (win.mWindow != null && win.Type == TYPE_WINDOW)
        // return win.mWindow.getAD_Color_ID();

        if( retValue == -1 ) {
            return getAD_Color_ID();
        }

        return retValue;
    }    // getAD_Color_ID

    /**
     * Descripción de Método
     *
     *
     * @param index
     * @param windowNo
     */

    public void setWindowNo( int index,int windowNo ) {
        if( (index < 0) || (index > m_windows.size())) {
            throw new IllegalArgumentException( "MWorkbench.setWindowNo - Index invalid: " + index );
        }

        WBWindow win = ( WBWindow )m_windows.get( index );

        win.WindowNo = windowNo;
    }    // getWindowNo

    /**
     * Descripción de Método
     *
     *
     * @param index
     *
     * @return
     */

    public int getWindowNo( int index ) {
        if( (index < 0) || (index > m_windows.size())) {
            throw new IllegalArgumentException( "MWorkbench.getWindowNo - Index invalid: " + index );
        }

        WBWindow win = ( WBWindow )m_windows.get( index );

        return win.WindowNo;
    }    // getWindowNo

    /**
     * Descripción de Método
     *
     *
     * @param index
     */

    public void dispose( int index ) {
        if( (index < 0) || (index > m_windows.size())) {
            throw new IllegalArgumentException( "MWorkbench.getWindowNo - Index invalid: " + index );
        }

        WBWindow win = ( WBWindow )m_windows.get( index );

        if( win.mWindow != null ) {
            win.mWindow.dispose();
        }

        win.mWindow = null;
    }    // dispose

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Dimension getWindowSize() {
        return null;
    }    // getWindowSize

    /**
     * Descripción de Clase
     *
     *
     * @version    2.2, 12.10.07
     * @author     Equipo de Desarrollo de openXpertya    
     */

    class WBWindow {

        /**
         * Constructor de la clase ...
         *
         *
         * @param type
         * @param id
         */

        public WBWindow( int type,int id ) {
            Type = type;
            ID   = id;
        }

        /** Descripción de Campos */

        public int Type = 0;

        /** Descripción de Campos */

        public int ID = 0;

        /** Descripción de Campos */

        public int WindowNo = -1;

        //

        /** Descripción de Campos */

        public MWindow mWindow = null;

        // public MFrame   mFrame = null;
        // public MProcess mProcess = null;

    }    // WBWindow
}        // Workbench



/*
 *  @(#)MWorkbench.java   02.07.07
 * 
 *  Fin del fichero MWorkbench.java
 *  
 *  Versión 2.2
 *
 */
