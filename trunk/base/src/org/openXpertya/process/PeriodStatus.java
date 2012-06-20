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

import java.util.logging.Level;

import org.openXpertya.model.MPeriod;
import org.openXpertya.model.MPeriodControl;
import org.openXpertya.util.CacheMgt;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class PeriodStatus extends SvrProcess {

    /** Descripción de Campos */

    private int p_C_Period_ID = 0;

    /** Descripción de Campos */

    private String p_PeriodAction = null;

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
            } else if( name.equals( "PeriodAction" )) {
                p_PeriodAction = ( String )para[ i ].getParameter();
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
        }

        p_C_Period_ID = getRecord_ID();
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
        log.info( "C_Period_ID=" + p_C_Period_ID + ", PeriodAction=" + p_PeriodAction );

        MPeriod period = new MPeriod( getCtx(),p_C_Period_ID,get_TrxName());

        if( period.getID() == 0 ) {
            throw new IllegalArgumentException( "@NotFound@  @C_Period_ID@=" + p_C_Period_ID );
        }
        // Disytel - Franco Bonafine
        // Se crean los controles de periodos para todos los tipos de documento base.
        // Este método se encarga de "rellenar" los controles de períodos de todos los tipos
        // de documento base. En caso de que un Tipo Doc. Base no tenga un control de período
        // entonces será creado por este método.
        period.createPeriodControls();
        // -

        StringBuffer sql = new StringBuffer( "UPDATE C_PeriodControl " );

        sql.append( "SET PeriodStatus='" );

        // Open

        if( MPeriodControl.PERIODACTION_OpenPeriod.equals( p_PeriodAction )) {
            sql.append( MPeriodControl.PERIODSTATUS_Open );

            // Close

        } else if( MPeriodControl.PERIODACTION_ClosePeriod.equals( p_PeriodAction )) {
            sql.append( MPeriodControl.PERIODSTATUS_Closed );

            // Close Permanently

        } else if( MPeriodControl.PERIODACTION_PermanentlyClosePeriod.equals( p_PeriodAction )) {
            sql.append( MPeriodControl.PERIODSTATUS_PermanentlyClosed );
        } else {
            return "-";
        }

        //

        sql.append( "', PeriodAction='N', Updated=SysDate,UpdatedBy=" ).append( getAD_User_ID());

        // WHERE

        sql.append( " WHERE C_Period_ID=" ).append( period.getC_Period_ID()).append( " AND PeriodStatus<>'P'" ).append( " AND PeriodStatus<>'" ).append( p_PeriodAction ).append( "'" );

        int no = DB.executeUpdate( sql.toString(),get_TrxName());

        CacheMgt.get().reset( "C_PeriodControl",0 );
        CacheMgt.get().reset( "C_Period",p_C_Period_ID );

        return "@Updated@ #" + no;
    }    // doIt
}    // PeriodStatus



/*
 *  @(#)PeriodStatus.java   02.07.07
 * 
 *  Fin del fichero PeriodStatus.java
 *  
 *  Versión 2.2
 *
 */
