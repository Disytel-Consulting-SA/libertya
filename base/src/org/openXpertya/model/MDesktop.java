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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

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

public class MDesktop {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     */

    public MDesktop( Properties ctx ) {
        m_ctx = ctx;
    }    // MDesktop

    /** Descripción de Campos */

    private Properties m_ctx;

    /** Descripción de Campos */

    private ArrayList m_workbenches = new ArrayList();

    /** Descripción de Campos */

    private int AD_Desktop_ID;

    /** Descripción de Campos */

    private String Name;

    /** Descripción de Campos */

    private String Description;

    /** Descripción de Campos */

    private String Help;

    /** Descripción de Campos */

    private int AD_Column_ID;

    /** Descripción de Campos */

    private int AD_Image_ID;

    /** Descripción de Campos */

    private int AD_Color_ID;

    /** Descripción de Campos */

    private int PA_Goal_ID;

    /** Descripción de Campos */

    private CLogger log = CLogger.getCLogger( getClass());

    /**
     * Descripción de Método
     *
     *
     * @param ad_Desktop_ID
     *
     * @return
     */

    public boolean initDesktop( int ad_Desktop_ID ) {
        AD_Desktop_ID = ad_Desktop_ID;

        // Get WB info

        String sql = null;

        if( Env.isBaseLanguage( m_ctx,"AD_Desktop" )) {
            sql = "SELECT Name,Description,Help,"                          // 1..3
                  + " AD_Column_ID,AD_Image_ID,AD_Color_ID,PA_Goal_ID "    // 4..7
                  + "FROM AD_Desktop " + "WHERE AD_Desktop_ID=? AND IsActive='Y'";
        } else {
            sql = "SELECT t.Name,t.Description,t.Help," + " w.AD_Column_ID,w.AD_Image_ID,w.AD_Color_ID,w.PA_Goal_ID " + "FROM AD_Desktop w, AD_Desktop_Trl t " + "WHERE w.AD_Desktop_ID=? AND w.IsActive='Y'" + " AND w.AD_Desktop_ID=t.AD_Desktop_ID" + " AND t.AD_Language='" + Env.getAD_Language( m_ctx ) + "'";
        }

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,AD_Desktop_ID );

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
            } else {
                AD_Desktop_ID = 0;
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"initDesktop",e );
        }

        if( AD_Desktop_ID == 0 ) {
            return false;
        }

        return initDesktopWorkbenches();
    }    // initDesktop

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        return "MDesktop ID=" + AD_Desktop_ID + " " + Name;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_Desktop_ID() {
        return AD_Desktop_ID;
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

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean initDesktopWorkbenches() {
        String sql = "SELECT AD_Workbench_ID " + "FROM AD_DesktopWorkbench " + "WHERE AD_Desktop_ID=? AND IsActive='Y' " + "ORDER BY SeqNo";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,AD_Desktop_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                int AD_Workbench_ID = rs.getInt( 1 );

                m_workbenches.add( new Integer( AD_Workbench_ID ));
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"MWorkbench.initDesktopWorkbenches",e );

            return false;
        }

        return true;
    }    // initDesktopWorkbenches

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getWindowCount() {
        return m_workbenches.size();
    }    // getWindowCount

    /**
     * Descripción de Método
     *
     *
     * @param index
     *
     * @return
     */

    public int getAD_Workbench_ID( int index ) {
        if( (index < 0) || (index > m_workbenches.size())) {
            return -1;
        }

        Integer id = ( Integer )m_workbenches.get( index );

        return id.intValue();
    }    // getAD_Workbench_ID
}    // MDesktop



/*
 *  @(#)MDesktop.java   02.07.07
 * 
 *  Fin del fichero MDesktop.java
 *  
 *  Versión 2.2
 *
 */
