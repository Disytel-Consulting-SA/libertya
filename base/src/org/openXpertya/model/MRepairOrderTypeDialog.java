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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.swing.Box;
import javax.swing.JLabel;

import org.compiere.plaf.CompiereColor;
import org.compiere.swing.CDialog;
import org.compiere.swing.CPanel;
import org.openXpertya.model.MField;
import org.openXpertya.model.MFieldVO;
import org.openXpertya.model.MPInstancePara;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MRepairOrderTypeDialog extends CDialog implements ActionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param frame
     * @param WindowNo
     * @param pi
     */

    public MRepairOrderTypeDialog( Frame frame,int WindowNo ) {
        super( frame,"Tipo de pedido destino",true );
        m_frame = frame;

        try {
            jbInit();
        } catch( Exception ex ) {
            log.log( Level.SEVERE,ex.getMessage());
        }

        //

        m_WindowNo    = WindowNo;

        //

    }    // ProcessParameter

    /** Descripción de Campos */

    private Frame m_frame;

    /** Descripción de Campos */

    private int m_WindowNo;

    /** Descripción de Campos */

    private boolean m_isOK = false;
    
    private int m_C_DocType_ID;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( MRepairOrderTypeDialog.class );

    //

    /** Descripción de Campos */

    private GridBagConstraints gbc = new GridBagConstraints();

    /** Descripción de Campos */

    private Insets nullInset = new Insets( 0,0,0,0 );

    /** Descripción de Campos */

    private Insets labelInset = new Insets( 2,12,2,0 );    // top,left,bottom,right

    /** Descripción de Campos */

    private Insets fieldInset = new Insets( 2,5,2,0 );    // top,left,bottom,right

    /** Descripción de Campos */

    private Insets fieldInsetRight = new Insets( 2,5,2,12 );    // top,left,bottom,right

    /** Descripción de Campos */

    private int m_line = 0;

    //

    /** Descripción de Campos */

    private CPanel mainPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    private CPanel centerPanel = new CPanel();

    /** Descripción de Campos */

    private GridBagLayout centerLayout = new GridBagLayout();
    
    /**     */
    private org.compiere.swing.CButton m_bn_ok=new org.compiere.swing.CButton(Env.getImageIcon( "Ok24.gif" ));
    org.compiere.swing.CComboBox m_tipos=new org.compiere.swing.CComboBox();

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    void jbInit() throws Exception {
        CompiereColor.setBackground( this );
        mainPanel.setLayout( mainLayout );
        centerPanel.setLayout( centerLayout );
        this.getContentPane().add( mainPanel );
        mainPanel.add( centerPanel,BorderLayout.CENTER );
        
        //mainPanel.add( confirmPanel,BorderLayout.SOUTH );
        //confirmPanel.addActionListener( this );
        mainPanel.add(m_bn_ok, BorderLayout.SOUTH);
        m_bn_ok.addActionListener( this );
        
        int ancho=250;
        int alto=120;
        
        this.setSize(ancho, alto);
        this.setLocation(512-(ancho>>1), 384-(alto>>1));	// en el centro a 1024x768
        
    }    // jbInit

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        this.removeAll();
        super.dispose();
    }    // dispose

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean initDialog() {
        log.config( "" );

        // Prepare panel

        gbc.anchor    = GridBagConstraints.NORTHWEST;
        gbc.weightx   = 0;
        gbc.weighty   = 0;
        gbc.gridy     = m_line++;
        gbc.gridx     = 0;
        gbc.gridwidth = 1;
        gbc.insets    = nullInset;
        gbc.fill      = GridBagConstraints.HORIZONTAL;
        centerPanel.add( Box.createVerticalStrut( 10 ),gbc );    // top gap 10+2=12

        //
        
        

        // Create Fields

//      Label Preparation

        gbc.gridy     = m_line++;
        gbc.gridwidth = 1;
        gbc.fill      = GridBagConstraints.HORIZONTAL;    // required for right justified
        gbc.gridx   = 0;
        gbc.weightx = 0;

        JLabel label = new JLabel("Tipo de pedido");

        gbc.insets = labelInset;
        centerPanel.add( label,gbc );

        // Field Preparation

        gbc.insets    = fieldInset;
        gbc.fill      = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;
        gbc.gridx     = 1;
        gbc.weightx   = 1;

        // The Editor
        
        ArrayList lista_tipos=inicializar_lista();
        
        if(lista_tipos==null || lista_tipos.size()==0)
        	return false;

        int tlen=lista_tipos.size();
        for(int i=0;i<tlen;i++)
        	m_tipos.addItem((org.openXpertya.util.KeyNamePair)lista_tipos.get(i));

        //

        centerPanel.add(m_tipos,gbc );

        return true;
    }    // initDialog

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        m_isOK = false;

        if( e.getSource().equals( m_bn_ok )) {
        		org.openXpertya.util.KeyNamePair select=(org.openXpertya.util.KeyNamePair)m_tipos.getSelectedItem();
                m_isOK = true;
                m_C_DocType_ID=select.getKey();
                dispose();
        } 
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isOK() {
        return m_isOK;
    }    // isOK
    
    /**
     * Devuelve el resultado de la seleccion
     * 
     * @return	
     */
    public int getC_DocType_ID()
    {
    	if(m_isOK==true)
    		return m_C_DocType_ID;
    	
    	return -1;
    }
    
    protected ArrayList inicializar_lista()
    {
    	ArrayList dev=new ArrayList();
    	
    	StringBuffer sql=new StringBuffer(	"SELECT C_DocType_ID, Name ")
    								.append("FROM C_DocType ")
    								.append("WHERE DocBaseType='SOO' ")
    								.append("AND DocSubTypeSO NOT IN ('OB', 'ON') ");
    	String fsql=MRole.getDefault().addAccessSQL(sql.toString(), "C_DocType", MRole.SQL_NOTQUALIFIED, MRole.SQL_RO);
    	try
    	{
    		PreparedStatement pstmt=DB.prepareStatement(fsql);
    		ResultSet rs=pstmt.executeQuery();
    		
    		while(rs.next())
    		{
    			org.openXpertya.util.KeyNamePair knp=new org.openXpertya.util.KeyNamePair(rs.getInt("C_DocType_ID"), rs.getString("Name"));
    			dev.add(knp);
    		}
    		
    		rs.close();
    		rs=null;
    		pstmt.close();
    		pstmt=null;
    	}
    	catch(SQLException e)
    	{
    		//
    		log.severe("Obteniendo lista tipos de pedido: no se puede obtener la lista");
    		return null;
    	}
    	
    	return dev;
    }
    
}    // MRepairOrderTypeDialog



/*
 *  @(#)ProcessParameter.java   02.07.07
 * 
 *  Fin del fichero ProcessParameter.java
 *  
 *  Versión 2.2
 *
 */
