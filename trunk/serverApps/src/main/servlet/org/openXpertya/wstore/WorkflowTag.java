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



package org.openXpertya.wstore;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.ecs.xhtml.b;
import org.apache.ecs.xhtml.br;
import org.apache.ecs.xhtml.option;
import org.apache.ecs.xhtml.select;
import org.apache.taglibs.standard.tag.el.core.ExpressionUtil;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.HtmlCode;
import org.openXpertya.util.Msg;
import org.openXpertya.wf.MWFActivity;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class WorkflowTag extends TagSupport {

    /** Descripción de Campos */

    private CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    private String m_activityID_el = null;

    /** Descripción de Campos */

    private static final String C_MANDATORY = "Cmandatory";

    /**
     * Descripción de Método
     *
     *
     * @param info_el
     */

    public void setActivityID( String info_el ) {
        m_activityID_el = info_el;
    }    // setActivityID

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws JspException
     */

    public int doStartTag() throws JspException {
        Properties ctx = JSPEnv.getCtx(( HttpServletRequest )pageContext.getRequest());

        // Activity

        int    AD_WF_Activity_ID = 0;
        String info              = null;

        try {
            info = ( String )ExpressionUtil.evalNotNull( "workflow","activityID",m_activityID_el,String.class,this,pageContext );

            if( (info != null) && (info.length() != 0) ) {
                AD_WF_Activity_ID = Integer.parseInt( info );
            }
        } catch( Exception e ) {
            log.severe( "doStartTag - Activity" + e );
        }

        MWFActivity act = new MWFActivity( ctx,AD_WF_Activity_ID,null );

        if( (AD_WF_Activity_ID == 0) || (act == null) || (act.getID() != AD_WF_Activity_ID) ) {
            log.severe( "doStartTag - Activity Not found - " + m_activityID_el + " (" + info + ")" );

            return( SKIP_BODY );
        }

        String name = null;

        if( act.isUserApproval()) {
            name = "IsApproved";
        } else if( act.isUserManual()) {
            name = "IsConfirmed";
        } else {
            return( SKIP_BODY );
        }

        // YesNo

        option[] yesNoOptions = new option[ 3 ];

        yesNoOptions[ 0 ] = new option( " " );
        yesNoOptions[ 0 ].addElement( " " );
        yesNoOptions[ 0 ].setSelected( true );
        yesNoOptions[ 1 ] = new option( "Y" );
        yesNoOptions[ 1 ].addElement( Msg.translate( ctx,"Yes" ));
        yesNoOptions[ 2 ] = new option( "N" );
        yesNoOptions[ 2 ].addElement( Msg.translate( ctx,"No" ));

        select yesNoSelect = new select( name,yesNoOptions );

        yesNoSelect.setID( "ID_" + name );
        yesNoSelect.setClass( C_MANDATORY );

        //

        String nameTrl = Msg.translate( ctx,name );

        // Assemble

        HtmlCode html = new HtmlCode();

        html.addElement( new b( nameTrl ));
        html.addElement( yesNoSelect );
        html.addElement( new br());

        JspWriter out = pageContext.getOut();

        html.output( out );

        //

        return( SKIP_BODY );
    }    // doStartTag

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws JspException
     */

    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }    // doEndTag
}    // WorkflowTag



/*
 *  @(#)WorkflowTag.java   12.10.07
 * 
 *  Fin del fichero WorkflowTag.java
 *  
 *  Versión 2.2
 *
 */
