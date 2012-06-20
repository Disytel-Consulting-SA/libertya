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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

import org.compiere.swing.CButton;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTextField;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class PdfToolbar extends JToolBar {

    /** Descripción de Campos */

    public static final int HEIGTH = 25;

    /** Descripción de Campos */

    public static final int WIDTH = 459;

    /** Descripción de Campos */

    private PdfPanel pdfpanel = null;

    /** Descripción de Campos */

    private CButton abrir = null;

    /** Descripción de Campos */

    private CButton guardar = null;

    /** Descripción de Campos */

    private CButton imprimir = null;

    /** Descripción de Campos */

    private CButton primera = null;

    /** Descripción de Campos */

    private CButton anterior = null;

    /** Descripción de Campos */

    private CTextField pagina = null;

    /** Descripción de Campos */

    private CTextField paginas = null;

    /** Descripción de Campos */

    private CButton siguiente = null;

    /** Descripción de Campos */

    private CButton ultima = null;

    /** Descripción de Campos */

    private CButton zoonMenos = null;

    /** Descripción de Campos */

    private CComboBox zoom = null;

    /** Descripción de Campos */

    private CButton zoomMas = null;

    /** Descripción de Campos */

    private CButton giraC = null;

    /** Descripción de Campos */

    private CButton giraR = null;

    /** Descripción de Campos */

    private CPanel panel1 = null;

    /** Descripción de Campos */

    private CPanel panel2 = null;

    /** Descripción de Campos */

    private CPanel panel3 = null;

    /** Descripción de Campos */

    private CPanel panel4 = null;

    /** Descripción de Campos */

    private JSeparator separador1 = null;

    /** Descripción de Campos */

    private JSeparator separador2 = null;

    /** Descripción de Campos */

    private JSeparator separador3 = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( PdfToolbar.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param p
     * @param withOpen
     * @param withSave
     * @param withPrint
     * @param withPage
     * @param withZoom
     * @param withRotate
     */

    public PdfToolbar( PdfPanel p,boolean withOpen,boolean withSave,boolean withPrint,boolean withPage,boolean withZoom,boolean withRotate ) {
        pdfpanel = p;
        jbInit();
        fillPicks( withOpen,withSave,withPrint,withPage,withZoom,withRotate );
    }

    /**
     * Descripción de Método
     *
     *
     * @param withOpen
     * @param withSave
     * @param withPrint
     * @param withPage
     * @param withZoom
     * @param withRotate
     */

    private void fillPicks( boolean withOpen,boolean withSave,boolean withPrint,boolean withPage,boolean withZoom,boolean withRotate ) {

        // Abrir

        getAbrir().setActionCommand( "abrir" );
        getAbrir().addActionListener( pdfpanel );
        getAbrir().setEnabled( withOpen );

        // guardar

        getGuardar().setActionCommand( "guardar" );
        getGuardar().addActionListener( pdfpanel );
        getGuardar().setEnabled( withSave );

        // Imprimir

        getImprimir().setActionCommand( "imprimir" );
        getImprimir().addActionListener( pdfpanel );
        getImprimir().setEnabled( withPrint );

        // pagina

        getPaginas().setPreferredSize( new Dimension( 0,0 ));
        getPaginas().setSize( 0,0 );
        getPagina().setActionCommand( "pagina" );
        getPagina().addActionListener( pdfpanel );
        getAnterior().setActionCommand( "anterior" );
        getAnterior().addActionListener( pdfpanel );
        getSiguiente().setActionCommand( "siguiente" );
        getSiguiente().addActionListener( pdfpanel );
        getUltima().setActionCommand( "ultima" );
        getUltima().addActionListener( pdfpanel );
        getPrimera().setActionCommand( "primera" );
        getPrimera().addActionListener( pdfpanel );
        getPagina().setEnabled( withPage );
        getAnterior().setEnabled( withPage );
        getSiguiente().setEnabled( withPage );
        getUltima().setEnabled( withPage );
        getPrimera().setEnabled( withPage );

        // Zoom

        getZoom().addItem( "10" );
        getZoom().addItem( "15" );
        getZoom().addItem( "25" );
        getZoom().addItem( "50" );
        getZoom().addItem( "75" );
        getZoom().addItem( "100" );
        getZoom().addItem( "125" );
        getZoom().addItem( "150" );
        getZoom().addItem( "200" );
//        getZoom().addItem( "400" );  Evitar Excepciones del tipo OutOfMemory
//        getZoom().addItem( "800" );  
//        getZoom().addItem( "1600" );
        getZoom().setSelectedItem( "100" );
        getZoom().addItemListener( pdfpanel );
        getZoomMenos().setActionCommand( "zoomMenos" );
        getZoomMenos().addActionListener( pdfpanel );
        getZoomMas().setActionCommand( "zoomMas" );
        getZoomMas().addActionListener( pdfpanel );
        getZoom().setEnabled( withZoom );
        getZoomMenos().setEnabled( withZoom );
        getZoomMas().setEnabled( withZoom );

        // Girar

        getGiraC().setActionCommand( "giraC" );
        getGiraC().addActionListener( pdfpanel );
        getGiraR().setActionCommand( "giraR" );
        getGiraR().addActionListener( pdfpanel );
        getGiraC().setEnabled( withRotate );
        getGiraR().setEnabled( withRotate );
    }

    /**
     * Descripción de Método
     *
     */

    private void jbInit() {
        setName( "PDF" );
        setPreferredSize( new Dimension( WIDTH,HEIGTH ));
        setSize( WIDTH,HEIGTH );
        add( getAbrir());
        add( getGuardar());
        add( getImprimir());
        add( getPanel1());
        add( getSeparador1());
        add( getPrimera());
        add( getAnterior());
        add( getPagina());
        add( getPaginas());
        add( getSiguiente());
        add( getUltima());
        add( getPanel2());
        add( getSeparador2());
        add( getZoomMenos());
        add( getZoom());
        add( getZoomMas());
        add( getPanel3());
        add( getSeparador3());
        add( getGiraC());
        add( getGiraR());
        add( getPanel4());
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public CComboBox getZoom() {
        if( zoom == null ) {
            zoom = new CComboBox();
            zoom.setPreferredSize( new Dimension( 75,21 ));
            zoom.setEditable( true );
            zoom.setFont( new Font( "Lucida Sans",0,11 ));
            zoom.setMinimumSize( new Dimension( 75,21 ));
            zoom.setMaximumSize( new Dimension( 75,21 ));
        }

        return zoom;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public CButton getAbrir() {
        if( abrir == null ) {
            abrir = new CButton();
            abrir.setIcon( Env.getImageIcon( "Open16.gif" ));
            abrir.setPreferredSize( new Dimension( 24,21 ));
            abrir.setMaximumSize( new Dimension( 24,21 ));
            abrir.setMinimumSize( new Dimension( 24,21 ));
            abrir.setToolTipText( "Nuevo" );
            abrir.setFocusPainted( false );
        }

        return abrir;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public CButton getGuardar() {
        if( guardar == null ) {
            guardar = new CButton();
            guardar.setIcon( Env.getImageIcon( "Save16.gif" ));
            guardar.setPreferredSize( new Dimension( 24,21 ));
            guardar.setMaximumSize( new Dimension( 24,21 ));
            guardar.setMinimumSize( new Dimension( 24,21 ));
            guardar.setToolTipText( "Guardar" );
            guardar.setFocusPainted( false );
        }

        return guardar;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public CButton getImprimir() {
        if( imprimir == null ) {
            imprimir = new CButton();
            imprimir.setIcon( Env.getImageIcon( "Print16.gif" ));
            imprimir.setPreferredSize( new Dimension( 24,21 ));
            imprimir.setMaximumSize( new Dimension( 24,21 ));
            imprimir.setMinimumSize( new Dimension( 24,21 ));
            imprimir.setToolTipText( "Imprimir" );
            imprimir.setFocusPainted( false );
        }

        return imprimir;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public CButton getZoomMas() {
        if( zoomMas == null ) {
            zoomMas = new CButton();
            zoomMas.setText( "" );
            zoomMas.setPreferredSize( new Dimension( 23,21 ));
            zoomMas.setMargin( new Insets( 2,2,2,2 ));
            zoomMas.setMaximumSize( new Dimension( 23,21 ));
            zoomMas.setMinimumSize( new Dimension( 23,21 ));
            zoomMas.setIcon( Env.getImageIcon( "Plus16.gif" ));
            zoomMas.setFocusPainted( false );
        }

        return zoomMas;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public CButton getZoomMenos() {
        if( zoonMenos == null ) {
            zoonMenos = new CButton();
            zoonMenos.setText( "" );
            zoonMenos.setPreferredSize( new Dimension( 23,21 ));
            zoonMenos.setMargin( new Insets( 2,2,2,2 ));
            zoonMenos.setMaximumSize( new Dimension( 23,21 ));
            zoonMenos.setMinimumSize( new Dimension( 23,21 ));
            zoonMenos.setIcon( Env.getImageIcon( "Minus16.gif" ));
            zoonMenos.setFocusPainted( false );
        }

        return zoonMenos;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public CTextField getPagina() {
        if( pagina == null ) {
            pagina = new CTextField();
            pagina.setFont( new Font( "Lucida Sans",0,11 ));
            pagina.setPreferredSize( new Dimension( 45,21 ));
            pagina.setMinimumSize( new Dimension( 45,21 ));
            pagina.setMaximumSize( new Dimension( 45,21 ));
            pagina.setMargin( new Insets( 0,2,0,2 ));
        }

        return pagina;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public CButton getSiguiente() {
        if( siguiente == null ) {
            siguiente = new CButton();
            siguiente.setText( "" );
            siguiente.setFont( new Font( "Lucida Sans",0,11 ));
            siguiente.setIcon( Env.getImageIcon( "Detail16.gif" ));
            siguiente.setPreferredSize( new Dimension( 24,21 ));
            siguiente.setMaximumSize( new Dimension( 24,21 ));
            siguiente.setMinimumSize( new Dimension( 24,21 ));
            siguiente.setToolTipText( "Siguiente" );
            siguiente.setFocusPainted( false );
        }

        return siguiente;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public CButton getAnterior() {
        if( anterior == null ) {
            anterior = new CButton();
            anterior.setText( "" );
            anterior.setFont( new Font( "Lucida Sans",0,11 ));
            anterior.setIcon( Env.getImageIcon( "Parent16.gif" ));
            anterior.setPreferredSize( new Dimension( 24,21 ));
            anterior.setMaximumSize( new Dimension( 24,21 ));
            anterior.setMinimumSize( new Dimension( 24,21 ));
            anterior.setToolTipText( "Anterior" );
            anterior.setFocusPainted( false );
        }

        return anterior;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public CButton getUltima() {
        if( ultima == null ) {
            ultima = new CButton();
            ultima.setIcon( Env.getImageIcon( "Ultima16.gif" ));
            ultima.setPreferredSize( new Dimension( 24,21 ));
            ultima.setMaximumSize( new Dimension( 24,21 ));
            ultima.setMinimumSize( new Dimension( 24,21 ));
            ultima.setToolTipText( "Last Page" );
            ultima.setFocusPainted( false );
        }

        return ultima;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public CButton getPrimera() {
        if( primera == null ) {
            primera = new CButton();
            primera.setIcon( Env.getImageIcon( "Primera16.gif" ));
            primera.setPreferredSize( new Dimension( 24,21 ));
            primera.setMaximumSize( new Dimension( 24,21 ));
            primera.setMinimumSize( new Dimension( 24,21 ));
            primera.setToolTipText( "Primera P�gina" );
            primera.setFocusPainted( false );
        }

        return primera;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public CTextField getPaginas() {
        if( paginas == null ) {
            paginas = new CTextField();
            paginas.setBorder( BorderFactory.createEmptyBorder( 0,2,0,2 ));
            paginas.setOpaque( false );
            paginas.setText( "" );
            paginas.setEditable( false );
            paginas.setEnabled( false );
            paginas.setDisabledTextColor( Color.black );
            paginas.setPreferredSize( new Dimension( 30,21 ));
            paginas.setMinimumSize( new Dimension( 30,21 ));
            paginas.setMaximumSize( new Dimension( 30,21 ));
        }

        return paginas;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public CButton getGiraC() {
        if( giraC == null ) {
            giraC = new CButton();
            giraC.setMaximumSize( new Dimension( 23,21 ));
            giraC.setMinimumSize( new Dimension( 23,21 ));
            giraC.setPreferredSize( new Dimension( 23,21 ));
            giraC.setIcon( Env.getImageIcon( "GiraC16.gif" ));
            giraC.setToolTipText( "Girar en sentido contrario al reloj" );
        }

        return giraC;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public CButton getGiraR() {
        if( giraR == null ) {
            giraR = new CButton();
            giraR.setPreferredSize( new Dimension( 23,21 ));
            giraR.setMaximumSize( new Dimension( 23,21 ));
            giraR.setMinimumSize( new Dimension( 23,21 ));
            giraR.setIcon( Env.getImageIcon( "GiraR16.gif" ));
            giraR.setToolTipText( "Girar en el sentido del reloj" );
        }

        return giraR;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private JSeparator getSeparador1() {
        if( separador1 == null ) {
            separador1 = new JSeparator();
            separador1.setOrientation( 1 );
            separador1.setPreferredSize( new Dimension( 9,21 ));
            separador1.setMinimumSize( new Dimension( 9,21 ));
            separador1.setMaximumSize( new Dimension( 9,21 ));
        }

        return separador1;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private JSeparator getSeparador2() {
        if( separador2 == null ) {
            separador2 = new JSeparator();
            separador2.setMinimumSize( new Dimension( 9,21 ));
            separador2.setPreferredSize( new Dimension( 9,21 ));
            separador2.setMaximumSize( new Dimension( 9,21 ));
            separador2.setOrientation( 1 );
        }

        return separador2;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private JSeparator getSeparador3() {
        if( separador3 == null ) {
            separador3 = new JSeparator();
            separador3.setMinimumSize( new Dimension( 9,21 ));
            separador3.setMaximumSize( new Dimension( 9,21 ));
            separador3.setPreferredSize( new Dimension( 9,21 ));
            separador3.setOrientation( 1 );
        }

        return separador3;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private CPanel getPanel1() {
        if( panel1 == null ) {
            panel1 = new CPanel();
            panel1.setMinimumSize( new Dimension( 7,21 ));
            panel1.setPreferredSize( new Dimension( 7,21 ));
            panel1.setMaximumSize( new Dimension( 7,21 ));
        }

        return panel1;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private CPanel getPanel2() {
        if( panel2 == null ) {
            panel2 = new CPanel();
            panel2.setMinimumSize( new Dimension( 7,21 ));
            panel2.setPreferredSize( new Dimension( 7,21 ));
            panel2.setMaximumSize( new Dimension( 7,21 ));
        }

        return panel2;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private CPanel getPanel3() {
        if( panel3 == null ) {
            panel3 = new CPanel();
            panel3.setMinimumSize( new Dimension( 7,21 ));
            panel3.setMaximumSize( new Dimension( 7,21 ));
            panel3.setPreferredSize( new Dimension( 7,21 ));
        }

        return panel3;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private CPanel getPanel4() {
        if( panel4 == null ) {
            panel4 = new CPanel();
            panel4.setMinimumSize( new Dimension( 7,21 ));
            panel4.setMaximumSize( new Dimension( 7,21 ));
            panel4.setPreferredSize( new Dimension( 7,21 ));
        }

        return panel4;
    }
}



/*
 *  @(#)PdfToolbar.java   02.07.07
 * 
 *  Fin del fichero PdfToolbar.java
 *  
 *  Versión 2.2
 *
 */
