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



package org.openXpertya.apps;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.compiere.plaf.CompiereColor;
import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CButton;
import org.compiere.swing.CComboBox;
import org.openXpertya.apps.RTFScaledEditorPane;
import org.openXpertya.attachment.AttachmentIntegrationInterface;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTextArea;
import org.openXpertya.model.MAttachment;
import org.openXpertya.model.MAttachmentEntry;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class Attachment extends JDialog implements ActionListener {

	/** Compatibilidad con constructor sin definicion de client/org */
	public Attachment( Frame frame,int WindowNo,int AD_Attachment_ID,int AD_Table_ID,int Record_ID,String trxName ) {
		this(frame, WindowNo, AD_Attachment_ID, AD_Table_ID, Record_ID, trxName, null, null);
	}
	
    /**
     * Constructor de la clase ...
     *
     *
     * @param frame
     * @param WindowNo
     * @param AD_Attachment_ID
     * @param AD_Table_ID
     * @param Record_ID
     * @param trxName
     */

    public Attachment( Frame frame,int WindowNo,int AD_Attachment_ID,int AD_Table_ID,int Record_ID,String trxName, Integer recordClientID, Integer recordOrgID ) {
        super( frame,Msg.getMsg( Env.getCtx(),"Attachment" ),true );

        // needs to be modal otherwise APanel does not recongize change.

        log.config( "Attachment - ID=" + AD_Attachment_ID + ", Table=" + AD_Table_ID + ", Record=" + Record_ID );

        //

        m_WindowNo = WindowNo;

        //

        try {
            jbInit();
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"Attachment",ex );
        }

        // Create Model

        if( AD_Attachment_ID == 0 ) {
            m_attachment = new MAttachment( Env.getCtx(),AD_Table_ID,Record_ID,trxName );
        } else {
            m_attachment = new MAttachment( Env.getCtx(),AD_Attachment_ID,trxName );
        }
        
        // Utilizar client/org del registro asociado, si es que estan definidos
        if (recordClientID != null && recordOrgID != null)
        	m_attachment.setClientOrg(recordClientID, recordOrgID);

        loadAttachments();

        //

        try {
            AEnv.showCenterWindow( frame,this );
        } catch( Exception e ) {
        }

        text.requestFocus();
    }    // Attachment

    /** Descripción de Campos */

    private int m_WindowNo;

    /** Descripción de Campos */

    private MAttachment m_attachment;

    /** Descripción de Campos */

    private boolean m_change = false;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( Attachment.class );

    //

    /** Descripción de Campos */

    private CPanel mainPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    private CTextArea text = new CTextArea();

    /** Descripción de Campos */

    private CButton bOpen = new CButton();

    /** Descripción de Campos */

    private CButton bSave = new CButton();

    /** Descripción de Campos */

    private CPanel northPanel = new CPanel();

    /** Descripción de Campos */

    private CButton bLoad = new CButton();

    /** Descripción de Campos */

    private BorderLayout northLayout = new BorderLayout();

    /** Descripción de Campos */

    private ConfirmPanel confirmPanel = new ConfirmPanel( true );

    /** Descripción de Campos */

    private CPanel toolBar = new CPanel( new FlowLayout( FlowLayout.LEADING,5,5 ));

    /** Descripción de Campos */

    private CButton bDelete = new CButton();

    /** Descripción de Campos */

    private CButton bDeleteAll = null;

    /** Descripción de Campos */

    private CComboBox cbContent = new CComboBox();

    /** Descripción de Campos */

    private JSplitPane centerPane = new JSplitPane();

    //

    /** Descripción de Campos */

    private CPanel graphPanel = new CPanel( new BorderLayout());

    /** Descripción de Campos */

    private GImage gifPanel = new GImage();

    /** Descripción de Campos */

    private JScrollPane gifScroll = new JScrollPane( gifPanel );

    /** Descripción de Campos */

    private PdfPanel pdfpanel = null;

    /** Descripción de Campos */

    private CTextArea info = new CTextArea();

    private org.openXpertya.pdf.viewer.PDFViewerBean pdfViewer = org.openXpertya.pdf.Document.getViewer();
    
    /** Guardar adjunto en externo */
    private CButton externalUpload = new CButton();
    
    /** Eliminar adjunto de externo */
    private CButton externalDelete = new CButton();
    
    
    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    void jbInit() throws Exception {
        CompiereColor.setBackground( this );
        mainPanel.setLayout( mainLayout );
        mainLayout.setHgap( 5 );
        mainLayout.setVgap( 5 );
        this.getContentPane().add( mainPanel );
        northPanel.setLayout( northLayout );
        northPanel.add( toolBar,BorderLayout.CENTER );
        if (MAttachment.isLocalAttachmentEnabled()) {
        	toolBar.add( bLoad );
        	toolBar.add( bDelete );
        }
        toolBar.add( bSave );
        toolBar.add( bOpen );
        toolBar.add( cbContent );
        if (MAttachment.isExternalAttachmentEnabled()) {
        	toolBar.add( externalUpload );
        	toolBar.add( externalDelete );
        }
        mainPanel.add( northPanel,BorderLayout.NORTH );

        //

        bOpen.setEnabled( false );
        bOpen.setIcon( Env.getImageIcon( "Editor24.gif" ));
        bOpen.setMargin( new Insets( 0,2,0,2 ));
        bOpen.setToolTipText( Msg.getMsg( Env.getCtx(),"Open" ));
        bOpen.addActionListener( this );

        //

        bSave.setEnabled( false );
        bSave.setIcon( Env.getImageIcon( "Export24.gif" ));
        bSave.setMargin( new Insets( 0,2,0,2 ));
        bSave.setToolTipText( Msg.getMsg( Env.getCtx(),"AttachmentSave" ));
        bSave.addActionListener( this );

        //

        bLoad.setIcon( Env.getImageIcon( "Import24.gif" ));
        bLoad.setMargin( new Insets( 0,2,0,2 ));
        bLoad.setToolTipText( Msg.getMsg( Env.getCtx(),"Load" ));
        bLoad.addActionListener( this );

        //

        bDelete.setIcon( Env.getImageIcon( "Delete24.gif" ));
        bDelete.setMargin( new Insets( 0,2,0,2 ));
        bDelete.setToolTipText( Msg.getMsg( Env.getCtx(),"Delete" ));
        bDelete.addActionListener( this );

        //

        externalUpload.setIcon( Env.getImageIcon( "ExternalUpload24.gif" ));
        externalUpload.setMargin( new Insets( 0,2,0,2 ));
        externalUpload.setToolTipText( "Cargar en repositorio externo" );
        externalUpload.addActionListener( this );

        //

        externalDelete.setIcon( Env.getImageIcon( "ExternalDelete24.gif" ));
        externalDelete.setMargin( new Insets( 0,2,0,2 ));
        externalDelete.setToolTipText( "Eliminar de repositorio externo" );
        externalDelete.addActionListener( this );
        
        //

        Dimension size = cbContent.getPreferredSize();

        size.width = 200;
        cbContent.setPreferredSize( size );

        // cbContent.setToolTipText(text);

        cbContent.addActionListener( this );
        cbContent.setLightWeightPopupEnabled( false );    // Acrobat Panel is heavy

        //

        text.setBackground( CompierePLAF.getInfoBackground());
        text.setPreferredSize( new Dimension( 200,200 ));

        //

        mainPanel.add( confirmPanel,BorderLayout.SOUTH );
        confirmPanel.addActionListener( this );
        bDeleteAll = ConfirmPanel.createDeleteButton( true );
        confirmPanel.addButton( bDeleteAll );
        bDeleteAll.addActionListener( this );

        //

        info.setText( "-" );
        info.setReadWrite( false );
        graphPanel.add( info,BorderLayout.CENTER );

        //

        mainPanel.add( centerPane,BorderLayout.CENTER );
        centerPane.add( graphPanel,JSplitPane.LEFT );
        centerPane.add( text,JSplitPane.RIGHT );
        centerPane.setResizeWeight( .75 );    // more to graph
    }                                         // jbInit

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
    	pdfViewer = null;    	
        super.dispose();
    }    // dispose

    /**
     * Descripción de Método
     *
     */

    private void loadAttachments() {
        log.config( "" );

        // Set Text/Description

        String sText = m_attachment.getTextMsg();

        if( sText == null ) {
            text.setText( "" );
        } else {
            text.setText( sText );
        }

        // Set Combo

        int size = m_attachment.getEntryCount();

        for( int i = 0;i < size;i++ ) {
            cbContent.addItem( m_attachment.getEntryName( i ));
        }

        if( size > 0 ) {
            cbContent.setSelectedIndex( 0 );
        } else {
            displayData( 0 );
        }
    }    // loadAttachment

    /**
     * Descripción de Método
     *
     *
     * @param index
     */

    private void displayData( int index ) {
        MAttachmentEntry entry = m_attachment.getEntry( index );

        log.config( "Index=" + index + " - " + entry );

        // Reset UI

        gifPanel.setImage( null );
        graphPanel.removeAll();

        //

        bDelete.setEnabled( false );
        bOpen.setEnabled( false );
        bSave.setEnabled( false );

        Dimension size = null;

        // no attachment

        if (entry == null) {
        	info.setText( "-" );
        } else if (entry.getData() == null)  {
            info.setText( (entry.getRetrieveError() != null) ? entry.getRetrieveError() : "-" );
        } else {
            bOpen.setEnabled( true );
            bSave.setEnabled( true );
            bDelete.setEnabled( entry.getM_UID() == null );
            externalDelete.setEnabled(entry.getM_UID() != null);
            log.config( entry.toStringX());

            //
            System.out.println("Entrada: " + entry.toStringX());
            
           	info.setText( entry.toStringX());

            if( entry.isPDF()) {
                try {
                    graphPanel.getInsets();

                    File f = entry.getFile();

                    PdfPanel pdfpanel = PdfPanel.loadPdf( f,graphPanel,false,false,true,true,true,true );

                    size = pdfpanel.getSize();
                    f.deleteOnExit();

// Temporalmente comentado por incompatibilidad en librerías. TODO: check manera de incorporarlo para evitar error al ejecutar desde Terminal                	
//					pdfViewer.loadPDF(entry.getInputStream());
//					pdfViewer.setScale(50);
//					size = pdfViewer.getPreferredSize();
//				//	size.width = Math.min(size.width, 400);
//				//	size.height = Math.min(size.height, 400);
//					//
//					graphPanel.add(pdfViewer, BorderLayout.CENTER);
//                    //

                } catch( Exception e ) {
                    log.log( Level.SEVERE,"(is pdf):"+e.getMessage(),e );
                }
            } else if( entry.isGraphic()) {

                // Can we display it

            	/*
            	 * ---------------------------------------------------------------------------------
            	 * Modified by Matías Cap - Disytel
            	 * ---------------------------------------------------------------------------------
            	 * 
            	 * Image no permite manipular imágenes en formato bmp. Se debió utilizar 
            	 * BufferedImage (extensión de Image) para usar ImageIO. ImageIo.read(f) lee el archivo
            	 * a adjuntar y devuelve un BufferedImage de ese archivo leído en su formato original. 
            	 * 
            	 * ---------------------------------------------------------------------------------
            	 * Código anterior
            	 * ---------------------------------------------------------------------------------
            	 * 
            	 * Image image = Toolkit.getDefaultToolkit().createImage( entry.getData());
            	 * 
            	 * 
            	 */
            	
                File f = entry.getFile();
                BufferedImage bufImg = null;
                try{
                	bufImg = ImageIO.read(f);
                	f.delete();		// <- Faltaba eliminar el archivo temporal luego de ser leido
                }catch(Exception e){
                	log.log(Level.SEVERE,"Invalid Image");
                }
                /*
                 * ---------------------------------------------------------------------------------
                 * Fin modificación Matías Cap - Disytel
                 * ---------------------------------------------------------------------------------
                 */
                	
                
                if( bufImg != null ) {
                    gifPanel.setImage( bufImg );
                    size = gifPanel.getPreferredSize();

                    if( (size.width == -1) && (size.height == -1) ) {
                        log.log( Level.SEVERE,"Invalid Image" );                    
                    } else {

                        // size.width += 40;
                        // size.height += 40;

                        graphPanel.add( gifScroll,BorderLayout.CENTER );
                    }
                } else {
                    log.log( Level.SEVERE,"Could not create image" );
                }
            } else if (entry.isHTML() || entry.isText() || entry.isRTF()) {  
            	try{
            		JEditorPane textPanel;
            		if (entry.isRTF())
            			textPanel = new RTFScaledEditorPane(entry.getInputStream(), graphPanel);
            		else
            		{
            			File f = entry.getFile();
            			textPanel = new JEditorPane( "file:///" + f.getAbsolutePath() );
            			JScrollPane jScrollPane = new JScrollPane();
            			textPanel.setEditable(false); 
            			jScrollPane.setViewportView(textPanel);
            			textPanel.setPreferredSize(new Dimension(800,600));
            			graphPanel.add(jScrollPane,BorderLayout.CENTER);
            			f.delete();
            		}
            	}
            	catch (Exception e){ log.severe("txt html rtf attachment Exception:" + e.getMessage()); }
            }
        }

        if( graphPanel.getComponentCount() == 0 ) {
            graphPanel.add( info,BorderLayout.CENTER );
        }

        log.config( "Size=" + size );

        // graphPanel.setPreferredSize(size);
        // centerPane.setDividerLocation(size.width+30);
        // size.width += 100;
        // size.height += 100;
        // centerPane.setPreferredSize(size);

        pack();
    }    // displayData

    /**
     * Descripción de Método
     *
     *
     * @param index
     *
     * @return
     */

    private String getFileName( int index ) {
        String fileName = null;

        if( cbContent.getItemCount() > index ) {
            fileName = ( String )cbContent.getItemAt( index );
        }

        return fileName;
    }    // getFileName

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {

        // log.config( "Attachment.actionPerformed - " + e.getActionCommand());
        // Save and Close

        if( e.getActionCommand().equals( ConfirmPanel.A_OK )) {
            String newText = text.getText();

            if( newText == null ) {
                newText = "";
            }

            String oldText = m_attachment.getTextMsg();

            if( oldText == null ) {
                oldText = "";
            }

            if( !m_change ) {
                m_change = !newText.equals( oldText );
            }

            boolean shouldDispose = true;
            if( (newText.length() > 0) || (m_attachment.getEntryCount() > 0) ) {
                if( m_change ) {
                    m_attachment.setTextMsg( text.getText());
                    if (!m_attachment.save()) {
                    	ADialog.error(m_WindowNo, this, "Error al guardar adjuntos. " + CLogger.retrieveErrorAsString() + ". Reintente o cancele.");
                    	shouldDispose = false;
                    } 
                }
            } else {
            	if (!m_attachment.delete( true )) {
            		ADialog.error(m_WindowNo, this, "Error al eliminar adjuntos. " + CLogger.retrieveErrorAsString() + ". Reintente o cancele.");
            		shouldDispose = false;
            	} 
            }

            if (shouldDispose)
            	dispose();
        }

        // Cancel

        else if( e.getActionCommand().equals( ConfirmPanel.A_CANCEL )) {
            dispose();
        }

        // Delete Attachment

        else if( e.getSource() == bDeleteAll ) {
            deleteAttachment();
            dispose();
        }

        // Delete individual entry and Return
        else if( e.getSource() == bDelete || e.getSource() == externalDelete) {
            deleteAttachmentEntry();

            // Show Data

        } else if( e.getSource() == cbContent ) {
            displayData( cbContent.getSelectedIndex());

            // Load Attachment

        } else if( e.getSource() == bLoad) {
            loadFile(null);

            // Open Attachment

        } else if( e.getSource() == externalUpload) {
            loadFile(MAttachment.getIntegrationImpl());

            // Open Attachment

        } else if( e.getSource() == bSave ) {
            saveAttachmentToFile();

            // Open Attachment

        } else if( e.getSource() == bOpen ) {
            if( !openAttachment()) {
                saveAttachmentToFile();
            }
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     */

    private void loadFile(AttachmentIntegrationInterface handler) {
        log.info( "" );

        JFileChooser chooser = new JFileChooser();

        chooser.setDialogType( JFileChooser.OPEN_DIALOG );
        chooser.setDialogTitle( Msg.getMsg( Env.getCtx(),"AttachmentNew" ));

        int returnVal = chooser.showOpenDialog( this );

        if( returnVal != JFileChooser.APPROVE_OPTION ) {
            return;
        }

        //

        String fileName = chooser.getSelectedFile().getName();

        log.config( fileName );

        File file = chooser.getSelectedFile();

        if( m_attachment.addEntry( file, handler )) {
            cbContent.addItem( fileName );
            cbContent.setSelectedIndex( cbContent.getItemCount() - 1 );
            m_change = true;
        }
    }    // getFileName

    /**
     * Descripción de Método
     *
     */

    private void deleteAttachment() {
        log.info( "" );

        if( ADialog.ask( m_WindowNo,this,"AttachmentDelete?" )) {
            if (!m_attachment.delete( true )) {
            	ADialog.error(m_WindowNo, this, "Error al eliminar todos los adjuntos. " + CLogger.retrieveErrorAsString());
            }
        }
    }    // deleteAttachment

    /**
     * Descripción de Método
     *
     */

    private void deleteAttachmentEntry() {
        log.info( "" );

        int    index    = cbContent.getSelectedIndex();
        String fileName = getFileName( index );

        if( fileName == null ) {
            return;
        }

        //

        if( ADialog.ask( m_WindowNo,this,"AttachmentDeleteEntry?",fileName )) {
            if( m_attachment.deleteEntry( index )) {
                cbContent.removeItemAt( index );
            }

            m_change = true;
        }
    }    // deleteAttachment

    /**
     * Descripción de Método
     *
     */

    private void saveAttachmentToFile() {
        int index = cbContent.getSelectedIndex();

        log.info( "index=" + index );

        if( m_attachment.getEntryCount() < index ) {
            return;
        }

        String fileName = getFileName( index );
        String ext      = fileName.lastIndexOf( "." ) > 0 ? fileName.substring( fileName.lastIndexOf( "." )) : "";
        
        log.config( "Ext=" + ext );

        JFileChooser chooser = new JFileChooser();

        chooser.setDialogType( JFileChooser.SAVE_DIALOG );
        chooser.setDialogTitle( Msg.getMsg( Env.getCtx(),"AttachmentSave" ));

        File f = new File( fileName );

        chooser.setSelectedFile( f );

        // Show dialog

        int returnVal = chooser.showSaveDialog( this );

        if( returnVal != JFileChooser.APPROVE_OPTION ) {
            return;
        }

        File saveFile = chooser.getSelectedFile();

        if( saveFile == null ) {
            return;
        }

        log.config( "Save to " + saveFile.getAbsolutePath());
        m_attachment.getEntryFile( index,saveFile );
    }    // saveAttachmentToFile

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean openAttachment() {
        int    index = cbContent.getSelectedIndex();
        byte[] data  = m_attachment.getEntryData( index );

        if( data == null ) {
            return false;
        }

        try {
            String fileName = System.getProperty( "java.io.tmpdir" ) + m_attachment.getEntryName( index );
            File tempFile = new File( fileName );

            m_attachment.getEntryFile( index,tempFile );

            if( isWindows()) {

                // Runtime.getRuntime().exec ("rundll32 url.dll,FileProtocolHandler " + url);

                Process p = Runtime.getRuntime().exec( "rundll32 SHELL32.DLL,ShellExec_RunDLL \"" + tempFile + "\"" );

                // p.waitFor();

                return true;
            } else if( isMac()) {
                String[] cmdArray = new String[]{ "open",tempFile.getAbsolutePath()};
                Process p = Runtime.getRuntime().exec( cmdArray );

                // p.waitFor();

                return true;
            } else    // other OS
            {}
        } catch( Exception e ) {
            log.log( Level.SEVERE,"openFile",e );
        }

        return false;
    }    // openFile

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean isMac() {
        String osName = System.getProperty( "os.name" );

        osName = osName.toLowerCase();

        return osName.indexOf( "mac" ) != -1;
    }    // isMac

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean isWindows() {
        String osName = System.getProperty( "os.name" );

        osName = osName.toLowerCase();

        return osName.indexOf( "windows" ) != -1;
    }    // isWindows

    /**
     * Descripción de Clase
     *
     *
     * @version    2.2, 12.10.07
     * @author     Equipo de Desarrollo de openXpertya    
     */

    class GImage extends JPanel {

        /**
         * Constructor de la clase ...
         *
         */

        public GImage() {
            super();
        }    // GImage

        /** Descripción de Campos */

        private Image m_image = null;

        /**
         * Descripción de Método
         *
         *
         * @param image
         */

        public void setImage( Image image ) {
            m_image = image;

            if( m_image == null ) {
                return;
            }

            MediaTracker mt = new MediaTracker( this );

            mt.addImage( m_image,0 );

            try {
                mt.waitForID( 0 );
            } catch( Exception e ) {
            }

            Dimension dim = new Dimension( m_image.getWidth( this ),m_image.getHeight( this ));

            this.setPreferredSize( dim );
        }    // setImage

        /**
         * Descripción de Método
         *
         *
         * @param g
         */

        public void paint( Graphics g ) {
            Insets in = getInsets();

            if( m_image != null ) {
                g.drawImage( m_image,in.left,in.top,this );
            }
        }    // paint

        /**
         * Descripción de Método
         *
         *
         * @param g
         */

        public void update( Graphics g ) {
            paint( g );
        }    // update
    }    // GImage


    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {}    // main
}    // Attachment



/*
 *  @(#)Attachment.java   02.07.07
 * 
 *  Fin del fichero Attachment.java
 *  
 *  Versión 2.2
 *
 */
