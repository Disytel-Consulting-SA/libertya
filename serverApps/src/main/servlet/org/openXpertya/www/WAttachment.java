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



package org.openXpertya.www;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ecs.AlignType;
import org.apache.ecs.xhtml.a;
import org.apache.ecs.xhtml.br;
import org.apache.ecs.xhtml.form;
import org.apache.ecs.xhtml.input;
import org.apache.ecs.xhtml.label;
import org.apache.ecs.xhtml.p;
import org.apache.ecs.xhtml.table;
import org.apache.ecs.xhtml.td;
import org.apache.ecs.xhtml.textarea;
import org.apache.ecs.xhtml.tr;
import org.openXpertya.model.MAttachment;
import org.openXpertya.model.MAttachmentEntry;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.FileUpload;
import org.openXpertya.util.Msg;
import org.openXpertya.util.WebDoc;
import org.openXpertya.util.WebEnv;
import org.openXpertya.util.WebUtil;

/**
 * Descripción de Clase
 *
 *
 * @versión    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class WAttachment extends HttpServlet {

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( WAttachment.class );

    /**
     * Descripción de Método
     *
     *
     * @param config
     *
     * @throws ServletException
     */

    public void init( ServletConfig config ) throws ServletException {
        super.init( config );

        if( !WebEnv.initWeb( config )) {
            throw new ServletException( "WAttachment.init" );
        }
    }    // init

    /** Descripción de Campos */

    public static final String P_Attachment_ID = "AD_Attachment_ID";

    /** Descripción de Campos */

    public static final String P_ATTACHMENT_INDEX = "AttachmentIndex";

    /** Descripción de Campos */

    public static final String P_TEXTMSG = "TextMsg";

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param response
     *
     * @throws IOException
     * @throws ServletException
     */

    public void doGet( HttpServletRequest request,HttpServletResponse response ) throws ServletException,IOException {
        log.fine( "doGet" );

        HttpSession   sess = request.getSession( false );
        WWindowStatus ws   = WWindowStatus.get( request );
        WebDoc        doc  = null;

        if( ws == null ) {
            doc = WebDoc.createPopup( "No Context" );
            doc.addPopupClose();
        } else {
            String error            = null;
            int    AD_Attachment_ID = WebUtil.getParameterAsInt( request,P_Attachment_ID );

            if( AD_Attachment_ID != 0 ) {
                int attachmentIndex = WebUtil.getParameterAsInt( request,P_ATTACHMENT_INDEX );

                if( attachmentIndex != 0 ) {
                    error = streamAttachment( AD_Attachment_ID,attachmentIndex,response,ws );

                    if( error == null ) {
                        return;
                    }
                }
            }

            doc = createPage( ws.ctx,ws.curTab.getAD_AttachmentID(),ws.curTab.getAD_Table_ID(),ws.curTab.getRecord_ID(),error );
        }

        //

        WebUtil.createResponse( request,response,this,null,doc,false );
    }    // doGet

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param response
     *
     * @throws IOException
     * @throws ServletException
     */

    public void doPost( HttpServletRequest request,HttpServletResponse response ) throws ServletException,IOException {
        log.fine( "doPost" );

        HttpSession   sess = request.getSession( false );
        WWindowStatus ws   = WWindowStatus.get( request );

        //

        WebDoc doc = null;

        if( ws == null ) {
            doc = WebDoc.create( "Help - No Context" );
        } else {
            String error = processPost( request,response,ws );

            doc = createPage( ws.ctx,ws.curTab.getAD_AttachmentID(),ws.curTab.getAD_Table_ID(),ws.curTab.getRecord_ID(),error );
        }

        //

        WebUtil.createResponse( request,response,this,null,doc,false );
    }    // doPost

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_Attachment_ID
     * @param AD_Table_ID
     * @param Record_ID
     * @param error
     *
     * @return
     */

    private WebDoc createPage( Properties ctx,int AD_Attachment_ID,int AD_Table_ID,int Record_ID,String error ) {
        WebDoc doc = WebDoc.createPopup( Msg.translate( ctx,"AD_Attachment_ID" ));
        table table = doc.getTable();

        //

        if( error != null ) {
            table.addElement( new tr().addElement( new td( "popupHeader",AlignType.RIGHT,AlignType.TOP,false,null )).addElement( new td( "popupHeader",AlignType.LEFT,AlignType.TOP,false,new p( error,AlignType.LEFT ).setClass( "Cerror" ))));    // window.css
        }

        MAttachment attachment = null;

        if( AD_Attachment_ID != 0 ) {
            attachment = new MAttachment( ctx,AD_Attachment_ID,null );
        } else {
            attachment = new MAttachment( ctx,AD_Table_ID,Record_ID,null );
        }

        //

        tr tr   = new tr();
        td left = new td( "popupCenter",AlignType.RIGHT,AlignType.TOP,false,new label( "TextMsg","T",Msg.translate( ctx,"TextMsg" )));

        //

        td right = new td( "popupCenter",AlignType.LEFT,AlignType.TOP,false );

        // Text Message Update

        form textMsg = new form( "WAttachment" );

        textMsg.addElement( new input( input.TYPE_HIDDEN,P_Attachment_ID,AD_Attachment_ID ));
        textMsg.addElement( new input( input.TYPE_HIDDEN,"AD_Table_ID",AD_Table_ID ));
        textMsg.addElement( new input( input.TYPE_HIDDEN,"Record_ID",Record_ID ));

        textarea msg = new textarea( P_TEXTMSG,5,40 );

        msg.addElement( attachment.getTextMsg());
        textMsg.addElement( msg );
        textMsg.addElement( new br());
        textMsg.addElement( new input( input.TYPE_SUBMIT,"submit","Submit" ));
        right.addElement( textMsg );

        // Existing Links

        p                  p       = new p();
        MAttachmentEntry[] entries = attachment.getEntries();

        for( int i = 0;i < entries.length;i++ ) {
            MAttachmentEntry entry = entries[ i ];

            if( i > 0 ) {
                p.addElement( " - " );
            }

            String url = "WAttachment?" + P_Attachment_ID + "=" + AD_Attachment_ID + "&" + P_ATTACHMENT_INDEX + "=" + entry.getIndex();

            p.addElement( new a( url,null,a.TARGET_BLANK,entry.getName()));
        }

        right.addElement( p );

        // Upload

        form upload = FileUpload.createForm( "WAttachment" );

        upload.addElement( new input( input.TYPE_HIDDEN,P_Attachment_ID,AD_Attachment_ID ));
        upload.addElement( new input( input.TYPE_HIDDEN,"AD_Table_ID",AD_Table_ID ));
        upload.addElement( new input( input.TYPE_HIDDEN,"Record_ID",Record_ID ));
        right.addElement( upload );

        //

        tr.addElement( left );
        tr.addElement( right );
        table.addElement( tr );

        // Footer

        doc.addPopupClose();

        //
        // System.out.println(doc);

        return doc;
    }    // createPage

    /**
     * Descripción de Método
     *
     *
     * @param AD_Attachment_ID
     * @param attachmentIndex
     * @param response
     * @param ws
     *
     * @return
     */

    private String streamAttachment( int AD_Attachment_ID,int attachmentIndex,HttpServletResponse response,WWindowStatus ws ) {
        log.info( "streamAttachment - AD_Attachment_ID=" + AD_Attachment_ID + ", AttachmentIndex=" + attachmentIndex );

        MAttachment attachment = new MAttachment( ws.ctx,AD_Attachment_ID,null );

        if( attachment.getID() == 0 ) {
            log.fine( "streamAttachment - No Attachment AD_Attachment_ID=" + AD_Attachment_ID );

            return "Attachment not found";
        }

        // Make sure it's the right attachment

        if( ws.curTab.getAD_AttachmentID() != AD_Attachment_ID ) {
            log.warning( "streamAttachment - Tab AD_Attachment_ID=" + ws.curTab.getAD_AttachmentID() + " <> " + AD_Attachment_ID );

            return "Your Attachment not found";
        }

        // Stream it

        return WebUtil.streamAttachment( response,attachment,attachmentIndex );
    }    // streamAttachment

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param response
     * @param ws
     *
     * @return
     */

    private String processPost( HttpServletRequest request,HttpServletResponse response,WWindowStatus ws ) {
        int        AD_Attachment_ID = 0;
        int        AD_Table_ID      = 0;
        int        Record_ID        = 0;
        String     textMsg          = null;
        FileUpload upload           = null;

        // URL Encrypted

        if( request.getContentType().equals( form.ENC_DEFAULT )) {
            AD_Attachment_ID = WebUtil.getParameterAsInt( request,P_Attachment_ID );
            AD_Table_ID = WebUtil.getParameterAsInt( request,"AD_Table_ID" );
            Record_ID   = WebUtil.getParameterAsInt( request,"Record_ID" );
            textMsg     = WebUtil.getParameter( request,P_TEXTMSG );
        } else {
            upload = new FileUpload( request );

            String error = upload.getError();

            if( error != null ) {
                log.warning( "pocessPost - " + error );

                return error;
            }

            AD_Attachment_ID = upload.getParameterAsInt( P_Attachment_ID );
            AD_Table_ID      = upload.getParameterAsInt( "AD_Table_ID" );
            Record_ID        = upload.getParameterAsInt( "Record_ID" );
        }

        // WebEnv.dump(request);

        log.info( "processPost - AD_Attachment_ID=" + AD_Attachment_ID + ", AD_Table_ID=" + AD_Table_ID + ", Record_ID=" + Record_ID + " - Upload=" + upload );

        // Check if you own the attachment

        if( ws.curTab.getAD_AttachmentID() != AD_Attachment_ID ) {
            return "Your Attachment not found";
        }

        // Check if we can save

        if( (AD_Attachment_ID != 0) && (Record_ID == 0) ) {
            return "Need to save record first";
        }

        MAttachment attachment = null;

        if( AD_Attachment_ID == 0 ) {
            attachment = new MAttachment( ws.ctx,AD_Table_ID,Record_ID,null );
        } else {
            attachment = new MAttachment( ws.ctx,AD_Attachment_ID,null );
        }

        // Update Attachment Text

        if( textMsg != null ) {
            attachment.setTextMsg( textMsg );
        }

        // Create Attachment

        if( upload != null ) {
            attachment.addEntry( upload.getFileName(),upload.getData());
        }

        // Save and update

        if( attachment.save()) {
            ws.curTab.loadAttachments();    // update Tab
        } else {
            return "Attachment not saved";
        }

        // OK

        return null;
    }    // processPost
}    // WAttachment



/*
 *  @(#)WAttachment.java   23.03.06
 * 
 *  Fin del fichero WAttachment.java
 *  
 *  Versión 2.2
 *
 */
