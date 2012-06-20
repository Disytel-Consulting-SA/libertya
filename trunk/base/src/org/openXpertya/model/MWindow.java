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

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.Icon;

import org.apache.ecs.xhtml.a;
import org.apache.ecs.xhtml.h2;
import org.apache.ecs.xhtml.h3;
import org.apache.ecs.xhtml.i;
import org.apache.ecs.xhtml.p;
import org.apache.ecs.xhtml.table;
import org.apache.ecs.xhtml.td;
import org.apache.ecs.xhtml.th;
import org.apache.ecs.xhtml.tr;
import org.compiere.plaf.CompiereColor;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.WebDoc;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class MWindow implements Serializable {

    /**
     * Constructor de la clase ...
     *
     *
     * @param vo
     */

    public MWindow( MWindowVO vo ) {
        m_vo = vo;

        if( loadTabData()) {
            enableEvents();
        }
    }    // MWindow

    /** Descripción de Campos */

    private MWindowVO m_vo;

    /** Descripción de Campos */

    private ArrayList m_tabs = new ArrayList();

    /** Descripción de Campos */

    private Rectangle m_position = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( MWindow.class );

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        log.info( "AD_Window_ID=" + m_vo.AD_Window_ID );

        for( int i = 0;i < getTabCount();i++ ) {
            getTab( i ).dispose();
        }

        m_tabs.clear();
        m_tabs = null;
    }    // dispose

    /**
     * Descripción de Método
     *
     */

    public void loadCompete() {

        // for all tabs

        for( int i = 0;i < getTabCount();i++ ) {
            getTab( i ).getMTable().loadComplete();
        }
    }    // loadComplete

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean loadTabData() {
        log.config( "" );

        if( m_vo.Tabs == null ) {
            return false;
        }

        for( int t = 0;t < m_vo.Tabs.size();t++ ) {
            MTabVO mTabVO = ( MTabVO )m_vo.Tabs.get( t );

            if( mTabVO != null ) {
                MTab mTab = new MTab( mTabVO );

                // Set Link Column

                if( mTab.getLinkColumnName().length() == 0 ) {
                    ArrayList parents = mTab.getParentColumnNames();

                    // No Parent - no link

                    if( parents.size() == 0 ) {
                        ;

                        // Standard case

                    } else if( parents.size() == 1 ) {
                        mTab.setLinkColumnName(( String )parents.get( 0 ));
                    } else {

                        // More than one parent.
                        // Search prior tabs for the "right parent"
                        // for all previous tabs

                        for( int i = 0;i < m_tabs.size();i++ ) {

                            // we have a tab

                            MTab   tab    = ( MTab )m_tabs.get( i );
                            String tabKey = tab.getKeyColumnName();    // may be ""

                            // look, if one of our parents is the key of that tab

                            for( int j = 0;j < parents.size();j++ ) {
                                String parent = ( String )parents.get( j );

                                if( parent.equals( tabKey )) {
                                    mTab.setLinkColumnName( parent );

                                    break;
                                }

                                // The tab could have more than one key, look into their parents

                                if( tabKey.equals( "" )) {
                                    for( int k = 0;k < tab.getParentColumnNames().size();k++ ) {
                                        if( parent.equals( tab.getParentColumnNames().get( k ))) {
                                            mTab.setLinkColumnName( parent );

                                            break;
                                        }
                                    }
                                }
                            }                      // for all parents
                        }                          // for all previous tabs
                    }                              // parents.size > 1
                }                                  // set Link column

                mTab.setLinkColumnName( null );    // overwrites, if AD_Column_ID exists

                //

                m_tabs.add( mTab );
            }
        }    // for all tabs

        return true;
    }    // loadTabData

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Image getImage() {
        if( m_vo.AD_Image_ID == 0 ) {
            return null;
        }

        //

        MImage mImage = MImage.get( Env.getCtx(),m_vo.AD_Image_ID );

        return mImage.getImage();
    }    // getImage

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Icon getIcon() {
        if( m_vo.AD_Image_ID == 0 ) {
            return null;
        }

        //

        MImage mImage = MImage.get( Env.getCtx(),m_vo.AD_Image_ID );

        return mImage.getIcon();
    }    // getIcon

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public CompiereColor getColor() {
        if( m_vo.AD_Color_ID == 0 ) {
            return null;
        }

        MColor mc = new MColor( m_vo.ctx,m_vo.AD_Color_ID,null );

        return mc.getCompiereColor();
    }    // getColor

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isSOTrx() {
        return m_vo.IsSOTrx;
    }    // isSOTrx

    /**
     * Descripción de Método
     *
     */

    public void query() {
        log.info( "" );

        MTab tab = getTab( 0 );

        tab.query( false,0 );

        if( tab.getRowCount() > 0 ) {
            tab.navigate( 0 );
        }
    }    // open

    /**
     * Descripción de Método
     *
     */

    private void enableEvents() {
        for( int i = 0;i < getTabCount();i++ ) {
            getTab( i ).enableEvents();
        }
    }    // enableEvents

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getTabCount() {
        return m_tabs.size();
    }    // getTabCount

    /**
     * Descripción de Método
     *
     *
     * @param i
     *
     * @return
     */

    public MTab getTab( int i ) {
        if( (i < 0) || (i + 1 > m_tabs.size())) {
            return null;
        }

        return( MTab )m_tabs.get( i );
    }    // getTab

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_Window_ID() {
        return m_vo.AD_Window_ID;
    }    // getAD_Window_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getWindowNo() {
        return m_vo.WindowNo;
    }    // getWindowNo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getName() {
        return m_vo.Name;
    }    // getName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDescription() {
        return m_vo.Description;
    }    // getDescription

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getHelp() {
        return m_vo.Help;
    }    // getHelp

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getWindowType() {
        return m_vo.WindowType;
    }    // getWindowType

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isTransaction() {
        return m_vo.WindowType.equals( MWindowVO.WINDOWTYPE_TRX );
    }    // isTransaction

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Dimension getWindowSize() {
        if( (m_vo.WinWidth != 0) && (m_vo.WinHeight != 0) ) {
            return new Dimension( m_vo.WinWidth,m_vo.WinHeight );
        }

        return null;
    }    // getWindowSize

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        return "MWindow[" + m_vo.WindowNo + "," + m_vo.Name + " (" + m_vo.AD_Window_ID + ")]";
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param javaClient
     *
     * @return
     */

    public WebDoc getHelpDoc( boolean javaClient ) {
        String title = Msg.getMsg( Env.getCtx(),"Window" ) + ": " + getName();
        WebDoc doc   = null;

        if( javaClient ) {
            doc = WebDoc.create( false,title,javaClient );
        } else    // HTML
        {
            doc = WebDoc.createPopup( title );
            doc.addPopupClose();
        }

        
        td center = doc.addPopupCenter( false );

        // Window

        if( getDescription().length() != 0 ) {
            center.addElement( new p().addElement( new i( getDescription())));
        }

        if( getHelp().length() != 0 ) {
            center.addElement( new p().addElement( getHelp()));
        }

        // Links to Tabs

        int size = getTabCount();
        p   p    = new p();

        for( int i = 0;i < size;i++ ) {
            MTab tab = getTab( i );

            if( i > 0 ) {
                p.addElement( " - " );
            }

            p.addElement( new a( "#Tab" + i ).addElement( tab.getName()));
        }

        center.addElement( p ).addElement( new p().addElement( WebDoc.NBSP ));

        // For all Tabs

        for( int i = 0;i < size;i++ ) {
            table table = new table( "1","5","5","100%",null );
            MTab  tab   = getTab( i );
            tr    tr    = new tr().addElement( new th().addElement( new a().setName( "Tab" + i ).addElement( new h2( Msg.getMsg( Env.getCtx(),"Tab" ) + ": " + tab.getName()))));

            if( tab.getDescription().length() != 0 ) {
                tr.addElement( new th().addElement( new i( tab.getDescription())));
            } else {
                tr.addElement( new th().addElement( WebDoc.NBSP ));
            }

            table.addElement( tr );

            // Desciption

            td td = new td().setColSpan( 2 );

            if( tab.getHelp().length() != 0 ) {
                td.addElement( new p().addElement( tab.getHelp()));
            }

            // Links to Fields

            p = new p();

            for( int j = 0;j < tab.getFieldCount();j++ ) {
                MField field = tab.getField( j );
                /*
                 * Disytel - Matias Cap
                 * Si se muestra en la ventana lo muestro en la ayuda
                 * sino no, ¿para qué?
                 */
                if(field.isDisplayed()){
                	String hdr   = field.getHeader();

                	if( (hdr != null) && (hdr.length() > 0) ) {
                		if( j > 0 ) {
                			p.addElement( " - " );
                		}

                		p.addElement( new a( "#Field" + i + j,hdr ));
                	}
                }
            }

            td.addElement( p );
            table.addElement( new tr().addElement( td ));

            // For all Fields

            for( int j = 0;j < tab.getFieldCount();j++ ) {
                MField field = tab.getField( j );
                /*
                 * Disytel - Matias Cap
                 * Si se muestra en la ventana lo muestro en la ayuda
                 * sino no, ¿para qué?
                 */
                if(field.isDisplayed()){
                	String hdr   = field.getHeader();

                	if( (hdr != null) && (hdr.length() > 0) ) {
                		td = new td().setColSpan( 2 ).addElement( new a().setName( "Field" + i + j ).addElement( new h3( Msg.getMsg( Env.getCtx(),"Field" ) + ": " + hdr )));

                		if( field.getDescription().length() != 0 ) {
                			td.addElement( new i( field.getDescription()));
                		}

                		//

                		if( field.getHelp().length() != 0 ) {
                			td.addElement( new p().addElement( field.getHelp()));
                		}

                		table.addElement( new tr().addElement( td ));
                	}
                }
             }    // for all Fields

                center.addElement( table );
                center.addElement( new p().addElement( WebDoc.NBSP ));
        }        // for all Tabs

        if( !javaClient ) {
            doc.addPopupClose();
        }

       // System.out.println( doc.toString());

        return doc;
    }    // getHelpDoc
}    // MWindow



/*
 *  @(#)MWindow.java   02.07.07
 * 
 *  Fin del fichero MWindow.java
 *  
 *  Versión 2.2
 *
 */
