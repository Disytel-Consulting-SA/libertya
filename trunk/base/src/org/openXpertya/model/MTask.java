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

import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Task;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MTask extends X_AD_Task {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param AD_Task_ID
     * @param trxName
     */

    public MTask( Properties ctx,int AD_Task_ID,String trxName ) {
        super( ctx,AD_Task_ID,trxName );
    }    // MTask

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MTask( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MTask

    /** Descripción de Campos */

    private Task m_task = null;

 
    /** Get Server Process.
    *
    * @return
   Run this Process on Server only */
       public boolean isServerProcess() {

           Object	oo	= get_Value("IsServerProcess");

           if (oo != null) {

               if (oo instanceof Boolean) {
                   return ((Boolean) oo).booleanValue();
               }

               return "Y".equals(oo);
           }

           return false;
       }
    /**
    * Descripción de Método
    *
    *
    * @return
    */

    public String execute() {
        String cmd = Msg.parseTranslation( Env.getCtx(),getOS_Command()).trim();

        if( (cmd == null) || cmd.equals( "" )) {
            return "Cannot execute '" + getOS_Command() + "'";
        }

        //

        if( isServerProcess()) {
            return executeRemote( cmd );
        }

        return executeLocal( cmd );
    }    // execute

    /**
     * Descripción de Método
     *
     *
     * @param cmd
     *
     * @return
     */

    public String executeLocal( String cmd ) {
        log.config( cmd );

        if( (m_task != null) && m_task.isAlive()) {
            m_task.interrupt();
        }

        m_task = new Task( cmd );
        m_task.start();

        StringBuffer sb = new StringBuffer();

        while( true ) {

            // Give it a bit of time

            try {
                Thread.sleep( 500 );
            } catch( InterruptedException ioe ) {
                log.log( Level.SEVERE,cmd,ioe );
            }

            // Info to user

            sb.append( m_task.getOut()).append( "\n-----------\n" ).append( m_task.getErr()).append( "\n-----------" );

            // Are we done?

            if( !m_task.isAlive()) {
                break;
            }
        }

        log.config( "done" );

        return sb.toString();
    }    // executeLocal

    /**
     * Descripción de Método
     *
     *
     * @param cmd
     *
     * @return
     */

    public String executeRemote( String cmd ) {
        log.config( cmd );

        return "Remote:\n";
    }    // executeRemote

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MTask[" );

        sb.append( getID()).append( "-" ).append( getName()).append( ";Server=" ).append( isServerProcess()).append( ";" ).append( getOS_Command()).append( "]" );

        return sb.toString();
    }    // toString
}    // MTask



/*
 *  @(#)MTask.java   02.07.07
 * 
 *  Fin del fichero MTask.java
 *  
 *  Versión 2.2
 *
 */
