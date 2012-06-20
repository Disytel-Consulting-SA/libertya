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



package org.openXpertya.install;

import java.util.logging.Level;

import org.openXpertya.model.MLanguage;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 30.12.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class LanguageMaintenance extends SvrProcess {

    /** Descripción de Campos */

    private int p_AD_Language_ID = 0;

    /** Descripción de Campos */

    private String p_MaintenanceMode = null;

    /** Descripción de Campos */

    public static String MAINTENANCEMODE_Add = "A";

    /** Descripción de Campos */

    public static String MAINTENANCEMODE_Delete = "D";

    /** Descripción de Campos */

    public static String MAINTENANCEMODE_ReCreate = "R";

    /** Descripción de Campos */

    private MLanguage m_language = null;

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
            } else if( name.equals( "MaintenanceMode" )) {
                p_MaintenanceMode = ( String )para[ i ].getParameter();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }

        p_AD_Language_ID = getRecord_ID();
    }    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    protected String doIt() throws Exception {
        m_language = new MLanguage( getCtx(),p_AD_Language_ID,get_TrxName());
        log.info( "doIt - Mode=" + p_MaintenanceMode + ", ID=" + p_AD_Language_ID + " - " + m_language );

        if( m_language.isBaseLanguage()) {
            throw new Exception( "El lenguaje base no tiene traducciones" );
        }

        int deleteNo = 0;
        int insertNo = 0;

        // Delete

        if( MAINTENANCEMODE_Delete.equals( p_MaintenanceMode ) || MAINTENANCEMODE_ReCreate.equals( p_MaintenanceMode )) {
            deleteNo = m_language.maintain( false );
        }

        // Add

        if( MAINTENANCEMODE_Add.equals( p_MaintenanceMode ) || MAINTENANCEMODE_ReCreate.equals( p_MaintenanceMode )) {
            if( m_language.isActive() && m_language.isSystemLanguage()) {
                insertNo = m_language.maintain( true );
            } else {
                throw new Exception( "Lenguaje no activo en el sistema" );
            }
        }

        // Delete

        if( MAINTENANCEMODE_Delete.equals( p_MaintenanceMode )) {
            if( m_language.isSystemLanguage()) {
                m_language.setIsSystemLanguage( false );
                m_language.save();
            }
        }

        return "@Borrado@=" + deleteNo + " - @Insertado@=" + insertNo;
    }    // doIt
}    // LanguageMaintenance



/*
 *  @(#)LanguageMaintenance.java   30.12.06
 * 
 *  Fin del fichero LanguageMaintenance.java
 *  
 *  Versión 2.2
 *
 */
