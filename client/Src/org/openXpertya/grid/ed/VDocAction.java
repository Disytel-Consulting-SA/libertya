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



package org.openXpertya.grid.ed;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.compiere.plaf.CompiereColor;
import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CPanel;
import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MBankList;
import org.openXpertya.model.MBankStatement;
import org.openXpertya.model.MBankTransfer;
import org.openXpertya.model.MBoletaDeposito;
import org.openXpertya.model.MBrochure;
import org.openXpertya.model.MCashLine;
import org.openXpertya.model.MCreditCardClose;
import org.openXpertya.model.MInOut;
import org.openXpertya.model.MInventory;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MJournal;
import org.openXpertya.model.MJournalBatch;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MPOSJournal;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.MProductChange;
import org.openXpertya.model.MSplitting;
import org.openXpertya.model.MTab;
import org.openXpertya.model.MTransfer;
import org.openXpertya.model.MWarehouseClose;
import org.openXpertya.model.M_Table;
import org.openXpertya.model.PO;
import org.openXpertya.model.X_C_POSJournal;
import org.openXpertya.plugin.MPluginPO;
import org.openXpertya.plugin.common.PluginPOUtils;
import org.openXpertya.process.DocOptions;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.wf.MWFActivity;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VDocAction extends JDialog implements ActionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param WindowNo
     * @param mTab
     * @param button
     * @param Record_ID
     */

    public VDocAction( int WindowNo,MTab mTab,VButton button,int Record_ID ) {
        super( Env.getWindow( WindowNo ),Msg.translate( Env.getCtx(),"DocAction" ),true );
        log.config( "VDocAction" );
        m_WindowNo = WindowNo;
        m_mTab     = mTab;

        //

        try {
            jbInit();
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"VDocAction",ex );
        }

        // dynamic init preparation

        // Corregido: Franco Bonafine - Disytel (2010-11-26)
        // Siempre devolvía la tabla base, o sea la tabla de la pestaña principal. Si la acción
        // provenía de otra pestaña entonces no se tenía el ID de la tabla correcta.
        // m_AD_Table_ID = Env.getContextAsInt( Env.getCtx(),WindowNo,"BaseTable_ID" );
        m_AD_Table_ID = Env.getContextAsInt( Env.getCtx(),WindowNo, mTab.getTabNo(),"AD_Table_ID" );
        //
        
        if( s_value == null ) {
            readReference(button.getReferenceID());
        }

        //

        dynInit( Record_ID );

        //

        AEnv.positionCenterWindow( Env.getWindow( WindowNo ),this );
    }    // VDocAction

    //

    /** Descripción de Campos */

    private int m_WindowNo = 0;

    /** Descripción de Campos */

    private int m_AD_Table_ID;

    /** Descripción de Campos */

    private boolean m_OKpressed = false;

    /** Descripción de Campos */

    private MTab m_mTab;

    //

    /** Descripción de Campos */

    private static String[] s_value = null;

    /** Descripción de Campos */

    private static String[] s_name;

    /** Descripción de Campos */

    private static String[] s_description;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VDocAction.class );

    //

    /** Descripción de Campos */

    private CPanel mainPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    private CPanel northPanel = new CPanel();

    /** Descripción de Campos */

    private CComboBox actionCombo = new CComboBox();

    /** Descripción de Campos */

    private JLabel actionLabel = new JLabel();

    /** Descripción de Campos */

    private JScrollPane centerPane = new JScrollPane();

    /** Descripción de Campos */

    private JTextArea message = new JTextArea();

    /** Descripción de Campos */

    private FlowLayout northLayout = new FlowLayout();

    /** Descripción de Campos */

    private ConfirmPanel confirmPanel = new ConfirmPanel( true );

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    void jbInit() throws Exception {
    	log.fine("estoy en jbinit");
        CompiereColor.setBackground( this );
        mainPanel.setLayout( mainLayout );
        actionLabel.setText( Msg.translate( Env.getCtx(),"DocAction" ));
        actionCombo.addActionListener( this );
        message.setLineWrap( true );
        message.setPreferredSize( new Dimension( 350,35 ));
        message.setWrapStyleWord( true );
        message.setBackground( CompierePLAF.getFieldBackground_Inactive());
        message.setEditable( false );
        northPanel.setLayout( northLayout );
        northLayout.setAlignment( FlowLayout.RIGHT );
        getContentPane().add( mainPanel );
        mainPanel.add( northPanel,BorderLayout.NORTH );
        northPanel.add( actionLabel,null );
        northPanel.add( actionCombo,null );
        mainPanel.add( centerPane,BorderLayout.CENTER );
        centerPane.getViewport().add( message,null );

        //

        mainPanel.add( confirmPanel,BorderLayout.SOUTH );
        confirmPanel.addActionListener( this );
    }    // jbInit

    /**
     * Descripción de Método
     *
     *
     * @param Record_ID
     */

    private void dynInit( int Record_ID ) {
    	log.fine("estoy en dyninit");
        String DocStatus = ( String )m_mTab.getValue( "DocStatus" );
        String DocAction = ( String )m_mTab.getValue( "DocAction" );

        //

        Object Processing = m_mTab.getValue( "Processing" );
        String OrderType  = Env.getContext( Env.getCtx(),m_WindowNo,"OrderType" );
        String IsSOTrx = Env.getContext( Env.getCtx(),m_WindowNo,"IsSOTrx" );

        if( DocStatus == null ) {
            message.setText( "*** ERROR ***" );

            return;
        }

        log.fine( "DocStatus=" + DocStatus + ", DocAction=" + DocAction + ", OrderType=" + OrderType + ", IsSOTrx=" + IsSOTrx + ", Processing=" + Processing + ", AD_Table_ID=" + m_AD_Table_ID + ", Record_ID=" + Record_ID );

        //

        String[] options  = new String[ s_value.length ];
        int      index    = 0;
        String   wfStatus = MWFActivity.getActiveInfo( Env.getCtx(),m_AD_Table_ID,Record_ID );

        if( wfStatus != null ) {
            ADialog.error( m_WindowNo,this,"WFActiveForRecord",wfStatus );

            return;
        }

		/*******************
		 *  General Actions
		 */
		String[] docActionHolder = new String[] {DocAction};
		index = DocumentEngine.getValidActions(DocStatus, Processing, OrderType, IsSOTrx, m_AD_Table_ID, 
				docActionHolder, options, Record_ID);
        
		M_Table table = M_Table.get(Env.getCtx(), m_AD_Table_ID);
		PO po = table.getPO(Record_ID, null);
		if (po instanceof DocOptions)
			index = ((DocOptions) po).customizeValidActions(DocStatus, Processing, OrderType, IsSOTrx,
					m_AD_Table_ID, docActionHolder, options, index);

		/** Logica de soporte de plugins sobre acciones del documento */
		Vector<MPluginPO> plugins = PluginPOUtils.getPluginList(po);
		for (MPluginPO aPlugin : plugins) {
			if (aPlugin instanceof DocOptions) {
				index = ((DocOptions) aPlugin).customizeValidActions(DocStatus, Processing, OrderType, IsSOTrx,
						m_AD_Table_ID, docActionHolder, options, index);	
			}
		}
		/** Fin Logica de soporte de plugins sobre acciones del documento */
		
		Integer doctypeId = (Integer)m_mTab.getValue("C_DocType_ID");
		if(doctypeId==null || doctypeId.intValue()==0){
			doctypeId = (Integer)m_mTab.getValue("C_DocTypeTarget_ID");
		}
		log.fine("get doctype: " + doctypeId);
		if (doctypeId != null) {
			index = DocumentEngine.checkActionAccess(Env.getAD_Client_ID(Env.getCtx()),
					Env.getAD_Role_ID(Env.getCtx()), 
					doctypeId, options, index);
		}

		DocAction = docActionHolder[0];		

        for( int i = 0;i < index;i++ ) {

            // Serach for option and add it

            boolean added = false;

            for( int j = 0;(j < s_value.length) &&!added;j++ ) {
                if( options[ i ].equals( s_value[ j ] )) {
                    actionCombo.addItem( s_name[ j ] );
                    added = true;
                }
            }
        }

        // setDefault

        if( DocAction.equals( "--" )) {    // If None, suggest closing
            DocAction = DocumentEngine.ACTION_Close;
        }

        String defaultV = "";

        for( int i = 0;(i < s_value.length) && defaultV.equals( "" );i++ ) {
            if( DocAction.equals( s_value[ i ] )) {
                defaultV = s_name[ i ];
            }
        }

        if( !defaultV.equals( "" )) {
            actionCombo.setSelectedItem( defaultV );
        }
    }    // dynInit

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getNumberOfOptions() {
    	log.fine("estoy en getNumberofOption");
        return actionCombo.getItemCount();
    }    // getNumberOfOptions

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean getStartProcess() {
        return m_OKpressed;
    }    // getStartProcess

    /**
     * Descripción de Método
     *
     */

    private void readReference(int referenceID) {
        List<String> v_value = new ArrayList<String>();
        List<String> v_name = new ArrayList<String>();
        List<String> v_description = new ArrayList<String>();
        
        if (referenceID == 0) {
        	referenceID = 135; // Harcode original.
        }
        
        // Se leen los datos de las acciones generales de documento.
        readReferenceData(referenceID, v_value, v_name, v_description); // ID=135 Originalmente Harcodeado.
    	// -------------------------------------------------------------------------------
    	// Franco Bonafine - Disytel - 2009/03
        // Se invoca el método que lee los valores de la lista que contiene las acciones
        // específicas para Allocations/OP/RC.
        // La carga de estas acciones es temporal. Esta lista debería desaparecer cuando
        // las acciones de Allocations se agreguen en la lista que contiene todas las
        // acciones de documento.
        readReferenceData(MAllocationHdr.ALLOCATIONACTION_AD_Reference_ID, v_value, v_name, v_description);
        // -------------------------------------------------------------------------------
        
        // convert to arrays

        int size = v_value.size();

        s_value       = new String[ size ];
        s_name        = new String[ size ];
        s_description = new String[ size ];

        for( int i = 0;i < size;i++ ) {
            s_value[ i ]       = ( String )v_value.get( i );
            s_name[ i ]        = ( String )v_name.get( i );
            s_description[ i ] = ( String )v_description.get( i );
        }
        
    }    // readReference

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        if( e.getActionCommand().equals( ConfirmPanel.A_OK )) {
            if( save()) {
                dispose();
                m_OKpressed = true;

                return;
            }
        } else if( e.getActionCommand().equals( ConfirmPanel.A_CANCEL )) {
            dispose();

            return;
        } else if( e.getSource() != actionCombo ) {
            return;
        }

        int index = getSelectedIndex();

        // Display descriprion

        if( index != -1 ) {
            message.setText( s_description[ index ] );

            // log.finer("DocAction=" + s_name[index] + " - " + s_value[index]);

        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private int getSelectedIndex() {
        int index = -1;

        // get Selection

        String sel = ( String )actionCombo.getSelectedItem();

        if( sel == null ) {
            return index;
        }

        // find it in vector

        for( int i = 0;(i < s_name.length) && (index == -1);i++ ) {
            if( sel.equals( s_name[ i ] )) {
                index = i;
            }
        }

        //

        return index;
    }    // getSelectedIndex

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean save() {
        int index = getSelectedIndex();

        if( index == -1 ) {
            return false;
        }

        // Save Selection

    	// -------------------------------------------------------------------------------
    	// Franco Bonafine - Disytel - 2009/03
        // Si se trata de un Allocation, se debe traducir la acción específica de Allocation
        // a una acción estándar definida en la interfaz DocAction.
        // La acción específica de Allocation se guardará en el campo AllocationAction de la
        // pestaña actual, para que luego en MAllocationHdr se pueda determinar cual es la
        // acción real a ejecutar.
        // El mapping de acciones es el siguiente:
        //  - MAllocationHdr.ALLOCATIONACTION_RevertAllocation -> DocumentEngine.ACTION_Reverse_Correct
        //  - MAllocationHdr.ALLOCATIONACTION_VoidPayments -> DocumentEngine.ACTION_Void      
        //  - MAllocationHdr.ALLOCATIONACTION_VoidPaymentsRetentions -> DocumentEngine.ACTION_Void
        // De esta forma, al seleccionar por ejemplo la acción específica _VoidPayments se ejecutará
        // el método voidIt() de MAllocationHdr, y dentro de dicho método se deberá consultar
        // el valor de la acción específica para determinar si la acción es VoidPayments o VoidPaymentsRetentions. 
        String value = null;
        if (m_AD_Table_ID == MAllocationHdr.Table_ID)
        	value = translateAllocationAction(index);
        else
        	value = s_value[ index ];
    	// -------------------------------------------------------------------------------
        log.config( "DocAction=" + s_value[ index ] );
        m_mTab.setValue( "DocAction", value);

        return true;
    }    // save

	private boolean estaEnarray(Vector array, String elem){
		for(int i = 0; i < array.size(); i++){
			if(((String)array.elementAt(i)).equals(elem))
				return true;
		}
		return false;
	}
	
	private void readReferenceData(int referenceID, List<String> values, List<String> names, List<String> descriptions) {
        String sql;

        if( Env.isBaseLanguage( Env.getCtx(),"AD_Ref_List" )) {
            sql = "SELECT Value, Name, Description FROM AD_Ref_List " + "WHERE AD_Reference_ID=? ORDER BY Name";
        } else {
            sql = "SELECT l.Value, t.Name, t.Description " + "FROM AD_Ref_List l, AD_Ref_List_Trl t " + "WHERE l.AD_Ref_List_ID=t.AD_Ref_List_ID" + " AND t.AD_Language='" + Env.getAD_Language( Env.getCtx()) + "'" + " AND l.AD_Reference_ID=? ORDER BY t.Name";
        }

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );
            pstmt.setInt(1, referenceID);
            
            ResultSet         rs    = pstmt.executeQuery();

            while( rs.next()) {
                String value       = rs.getString( 1 );
                String name        = rs.getString( 2 );
                String description = rs.getString( 3 );

                if( description == null ) {
                    description = "";
                }

                //

                values.add( value );
                names.add( name );
                descriptions.add( description );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );
        }
	}
	
		
	private String translateAllocationAction(int actionIndex) {
		String allocAction = s_value[actionIndex];
		m_mTab.setValue("AllocationAction", allocAction);
		return MAllocationHdr.getDocActionByAllocationAction(allocAction);
	}
}    // VDocAction



/*
 *  @(#)VDocAction.java   02.07.07
 * 
 *  Fin del fichero VDocAction.java
 *  
 *  Versión 2.2
 *
 */
