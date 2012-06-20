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
import java.util.logging.Level;

import org.openXpertya.model.MProject;
import org.openXpertya.model.MProjectType;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ProjectSetType extends SvrProcess {

    /** Descripción de Campos */

    private int m_C_Project_ID = 0;

    /** Descripción de Campos */

    private int m_C_ProjectType_ID = 0;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                continue;
            } else if( name.equals( "C_ProjectType_ID" )) {
                m_C_ProjectType_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }
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
        m_C_Project_ID = getRecord_ID();
        log.info( "doIt - C_Project_ID=" + m_C_Project_ID + ", C_ProjectType_ID=" + m_C_ProjectType_ID );

        //

        MProject project = new MProject( getCtx(),m_C_Project_ID,get_TrxName());

        if( (project.getC_Project_ID() == 0) || (project.getC_Project_ID() != m_C_Project_ID) ) {
            throw new IllegalArgumentException( "Project not found C_Project_ID=" + m_C_Project_ID );
        }

        if( project.getC_ProjectType_ID_Int() > 0 ) {
            throw new IllegalArgumentException( "Project already has Type (Cannot overwrite) " + project.getC_ProjectType_ID());
        }

        //

        MProjectType type = new MProjectType( getCtx(),m_C_ProjectType_ID,get_TrxName());

        if( (type.getC_ProjectType_ID() == 0) || (type.getC_ProjectType_ID() != m_C_ProjectType_ID) ) {
            throw new IllegalArgumentException( "Project Type not found C_ProjectType_ID=" + m_C_ProjectType_ID );
        }

        // Set & Copy if Service

        project.setProjectType( type );

        if( !project.save()) {
            throw new Exception( "@Error@" );
        }

        //

        return "@OK@";
    }    // doIt
}    // ProjectSetType



/*
 *  @(#)ProjectSetType.java   02.07.07
 * 
 *  Fin del fichero ProjectSetType.java
 *  
 *  Versión 2.2
 *
 */
