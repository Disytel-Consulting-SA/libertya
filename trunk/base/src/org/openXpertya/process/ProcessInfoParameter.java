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

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ProcessInfoParameter implements Serializable {

    /**
     * Constructor de la clase ...
     *
     *
     * @param parameterName
     * @param parameter
     * @param parameter_To
     * @param info
     * @param info_To
     */

    public ProcessInfoParameter( String parameterName,Object parameter,Object parameter_To,String info,String info_To ) {
        setParameterName( parameterName );
        setParameter( parameter );
        setParameter_To( parameter_To );
        setInfo( info );
        setInfo_To( info_To );
    }    // ProcessInfoParameter

    /** Descripción de Campos */

    private String m_ParameterName;

    /** Descripción de Campos */

    private Object m_Parameter;

    /** Descripción de Campos */

    private Object m_Parameter_To;

    /** Descripción de Campos */

    private String m_Info = "";

    /** Descripción de Campos */

    private String m_Info_To = "";

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {

        // From .. To

        if( (m_Parameter_To != null) || (m_Info_To.length() > 0) ) {
            return "ProcessInfoParameter[" + m_ParameterName + "=" + m_Parameter + ( (m_Parameter == null)
                    ?""
                    :"{" + m_Parameter.getClass().getName() + "}" ) + " (" + m_Info + ") - " + m_Parameter_To + ( (m_Parameter_To == null)
                    ?""
                    :"{" + m_Parameter_To.getClass().getName() + "}" ) + " (" + m_Info_To + ")";
        }

        // Value

        return "ProcessInfoParameter[" + m_ParameterName + "=" + m_Parameter + ( (m_Parameter == null)
                ?""
                :"{" + m_Parameter.getClass().getName() + "}" ) + " (" + m_Info + ")";
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getInfo() {
        return m_Info;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getInfo_To() {
        return m_Info_To;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Object getParameter() {
        return m_Parameter;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getParameterAsInt() {
        if( m_Parameter == null ) {
            return 0;
        }

        if( m_Parameter instanceof Number ) {
            return(( Number )m_Parameter ).intValue();
        }

        BigDecimal bd = new BigDecimal( m_Parameter.toString());

        return bd.intValue();
    }    // getParameterAsInt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Object getParameter_To() {
        return m_Parameter_To;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getParameter_ToAsInt() {
        if( m_Parameter_To == null ) {
            return 0;
        }

        if( m_Parameter_To instanceof Number ) {
            return(( Number )m_Parameter_To ).intValue();
        }

        BigDecimal bd = new BigDecimal( m_Parameter_To.toString());

        return bd.intValue();
    }    // getParameter_ToAsInt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getParameterName() {
        return m_ParameterName;
    }

    /**
     * Descripción de Método
     *
     *
     * @param Info
     */

    public void setInfo( String Info ) {
        if( Info == null ) {
            m_Info = "";
        } else {
            m_Info = Info;
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param Info_To
     */

    public void setInfo_To( String Info_To ) {
        if( Info_To == null ) {
            m_Info_To = "";
        } else {
            m_Info_To = Info_To;
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param Parameter
     */

    public void setParameter( Object Parameter ) {
        m_Parameter = Parameter;
    }

    /**
     * Descripción de Método
     *
     *
     * @param Parameter_To
     */

    public void setParameter_To( Object Parameter_To ) {
        m_Parameter_To = Parameter_To;
    }

    /**
     * Descripción de Método
     *
     *
     * @param ParameterName
     */

    public void setParameterName( String ParameterName ) {
        m_ParameterName = ParameterName;
    }
    
    
    public String valueToString()
    {
    	return m_Parameter.toString();
    }
    
}    // ProcessInfoParameter



/*
 *  @(#)ProcessInfoParameter.java   25.03.06
 * 
 *  Fin del fichero ProcessInfoParameter.java
 *  
 *  Versión 2.2
 *
 */
