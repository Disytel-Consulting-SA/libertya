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

import java.sql.Timestamp;
import java.util.Properties;

/**
 * Descripción de Interface
 *
 *
 * @version    2.2, 12.10.07
 * @author         Equipo de Desarrollo de openXpertya    
 */

public interface ProcesadorOXP {

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_Client_ID();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getName();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDescription();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Properties getCtx();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getFrequencyType();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getFrequency();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getServerID();

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public Timestamp getDateNextRun( boolean requery );

    /**
     * Descripción de Método
     *
     *
     * @param dateNextWork
     */

    public void setDateNextRun( Timestamp dateNextWork );

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Timestamp getDateLastRun();

    /**
     * Descripción de Método
     *
     *
     * @param dateLastRun
     */

    public void setDateLastRun( Timestamp dateLastRun );

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean save();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ProcesadorLogOXP[] getLogs();
}    // ProcesadorOXP



/*
 *  @(#)ProcesadorOXP.java   02.07.07
 * 
 *  Fin del fichero ProcesadorOXP.java
 *  
 *  Versión 2.2
 *
 */
