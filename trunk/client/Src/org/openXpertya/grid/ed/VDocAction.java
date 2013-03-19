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
import org.openXpertya.model.MBankStatement;
import org.openXpertya.model.MBankTransfer;
import org.openXpertya.model.MBoletaDeposito;
import org.openXpertya.model.MCashLine;
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
import org.openXpertya.model.MWarehouseClose;
import org.openXpertya.model.X_C_POSJournal;
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

        // Locked

        if( Processing != null ) {
            boolean locked = "Y".equals( Processing );

            if( !locked && (Processing instanceof Boolean) ) {
                locked = (( Boolean )Processing ).booleanValue();
            }

            if( locked ) {
                options[ index++ ] = DocumentEngine.ACTION_Unlock;
            }
        }

        // Approval required           ..  NA

        if( DocStatus.equals( DocumentEngine.STATUS_NotApproved )) {
            options[ index++ ] = DocumentEngine.ACTION_Prepare;
            options[ index++ ] = DocumentEngine.ACTION_Void;
        }

        // Draft/Invalid                           ..  DR/IN

        else if( DocStatus.equals( DocumentEngine.STATUS_Drafted ) || DocStatus.equals( DocumentEngine.STATUS_Invalid )) {
            options[ index++ ] = DocumentEngine.ACTION_Complete;

            // options[index++] = DocumentEngine.ACTION_Prepare;

            options[ index++ ] = DocumentEngine.ACTION_Void;
        }

        // In Process                  ..  IP

        else if( DocStatus.equals( DocumentEngine.STATUS_InProgress ) || DocStatus.equals( DocumentEngine.STATUS_Approved )) {
            options[ index++ ] = DocumentEngine.ACTION_Complete;
            options[ index++ ] = DocumentEngine.ACTION_Void;
        }

        // Complete                    ..  CO

        else if( DocStatus.equals( DocumentEngine.STATUS_Completed )) {
            options[ index++ ] = DocumentEngine.ACTION_Close;
        }

        // Waiting Payment

        else if( DocStatus.equals( DocumentEngine.STATUS_WaitingPayment ) || DocStatus.equals( DocumentEngine.STATUS_WaitingConfirmation )) {
            options[ index++ ] = DocumentEngine.ACTION_Void;
            options[ index++ ] = DocumentEngine.ACTION_Prepare;
        }

        // Closed, Voided, REversed    ..  CL/VO/RE

        else if( DocStatus.equals( DocumentEngine.STATUS_Closed ) || DocStatus.equals( DocumentEngine.STATUS_Voided ) || DocStatus.equals( DocumentEngine.STATUS_Reversed )) {
            return;
        }

        if( m_AD_Table_ID == MOrder.Table_ID ) {

            // Draft                       ..  DR/IP/IN

            if( DocStatus.equals( DocumentEngine.STATUS_Drafted ) || DocStatus.equals( DocumentEngine.STATUS_InProgress ) || DocStatus.equals( DocumentEngine.STATUS_Invalid )) {
                options[ index++ ] = DocumentEngine.ACTION_Prepare;

                // Draft Sales Order Quote/Proposal - Process

                if( "Y".equals( IsSOTrx ) && ( "OB".equals( OrderType ) || "ON".equals( OrderType ))) {
                    DocAction = DocumentEngine.ACTION_Prepare;
                }
            }

            // Complete                    ..  CO

            else if( DocStatus.equals( DocumentEngine.STATUS_Completed )) {
                options[ index++ ] = DocumentEngine.ACTION_ReActivate;
                options[ index++ ] = DocumentEngine.ACTION_Void;
            }
        } else if( m_AD_Table_ID == MInOut.Table_ID ) {

            // Complete                    ..  CO

            if( DocStatus.equals( DocumentEngine.STATUS_Completed )) {
                options[ index++ ] = DocumentEngine.ACTION_Reverse_Correct;
                options[ index++ ] = DocumentEngine.ACTION_Void;
            }
        } else if( m_AD_Table_ID == MInvoice.Table_ID ) {

            // Complete                    ..  CO

            if( DocStatus.equals( DocumentEngine.STATUS_Completed )) {
                options[ index++ ] = DocumentEngine.ACTION_Reverse_Correct;
                options[ index++ ] = DocumentEngine.ACTION_Void;
            }
        } else if( m_AD_Table_ID == MPayment.Table_ID ) {

            // Complete                    ..  CO

            if( DocStatus.equals( DocumentEngine.STATUS_Completed )) {
                options[ index++ ] = DocumentEngine.ACTION_Reverse_Correct;
                options[ index++ ] = DocumentEngine.ACTION_Void;
            }
        } else if( (m_AD_Table_ID == MJournal.Table_ID) || (m_AD_Table_ID == MJournalBatch.Table_ID) ) {

            // Complete                    ..  CO

            if( DocStatus.equals( DocumentEngine.STATUS_Completed )) {
                options[ index++ ] = DocumentEngine.ACTION_Reverse_Correct;
                options[ index++ ] = DocumentEngine.ACTION_Reverse_Accrual;
            }
        } else if( m_AD_Table_ID == MAllocationHdr.Table_ID ) {
        	// -------------------------------------------------------------------------------
        	// Franco Bonafine - Disytel - 2009/03
        	// Modificación de las opciones de acción para Allocations/OP/RC:
        	// Se quita la acción de Inversa/Corrección y se agregan las acciones
        	// específicas para Allocations.
        	// Estas acciones están definidas en una Referencia diferente a la que
        	// contiene todas las acciones de doumentos (referencia: Allocation_DocumentAction)
        	            
        	/* Se comenta el código antiguo -------------->
        	if( DocStatus.equals( DocumentEngine.STATUS_Completed )) {
                options[ index++ ] = DocumentEngine.ACTION_Reverse_Correct;
                options[ index++ ] = DocumentEngine.ACTION_Void;
            }
            <--------------- Fin de código antiguo */

        	// Complete                    ..  CO
        	if( DocStatus.equals( DocumentEngine.STATUS_Completed )) {
                options[ index++ ] = MAllocationHdr.ALLOCATIONACTION_RevertAllocation;
                options[ index++ ] = MAllocationHdr.ALLOCATIONACTION_VoidPayments;      
                options[ index++ ] = MAllocationHdr.ALLOCATIONACTION_VoidPaymentsRetentions;
            }
        	// Fin agregado FB 2009/03
        	// -------------------------------------------------------------------------------
        } else if( m_AD_Table_ID == MBankStatement.Table_ID ) {

            // Complete                    ..  CO

            if( DocStatus.equals( DocumentEngine.STATUS_Completed )) {
                options[ index++ ] = DocumentEngine.ACTION_Void;
            }
        } else if(m_AD_Table_ID == MBankTransfer.Table_ID){
        	
        	// Complete                    ..  CO

            if( DocStatus.equals( DocumentEngine.STATUS_Completed )) {
                options[ index++ ] = DocumentEngine.ACTION_Void;
            }
        } else if( m_AD_Table_ID == MBoletaDeposito.Table_ID ) {

            // Complete                    ..  CO
            if( DocStatus.equals( DocumentEngine.STATUS_Completed )) {
                options[ index++ ] = DocumentEngine.ACTION_Void;
            }
        } else if( m_AD_Table_ID == MSplitting.Table_ID ) {

            // Complete                    ..  CO
            if( DocStatus.equals( DocumentEngine.STATUS_Completed )) {
                options[ index++ ] = DocumentEngine.ACTION_Void;
            }
        } else if( m_AD_Table_ID == MProductChange.Table_ID ) {

            // Complete                    ..  CO
            if( DocStatus.equals( DocumentEngine.STATUS_Completed )) {
                options[ index++ ] = DocumentEngine.ACTION_Void;
            }
        } else if ( m_AD_Table_ID == MPOSJournal.Table_ID ) {
            
        	// Si está en borrador solo se puede abrir la caja diaria
        	if( DocStatus.equals( X_C_POSJournal.DOCSTATUS_Drafted )) {
                options[ 0 ] = X_C_POSJournal.DOCACTION_Open;
            // Si está abierta entonces se puede cerrar o completar la caja diaria
            } else if ( DocStatus.equals( X_C_POSJournal.DOCSTATUS_Opened )) {
            	options[ index++ ] = X_C_POSJournal.DOCACTION_Close;
            }
        	
        } else if( m_AD_Table_ID == MCashLine.Table_ID ) {

            // Complete                    ..  CO

            if( DocStatus.equals( DocumentEngine.STATUS_Completed )) {
                options[ index++ ] = DocumentEngine.ACTION_Void;
            }
        } else if( m_AD_Table_ID == MWarehouseClose.Table_ID ) {

            // Complete                    ..  CO

            if( DocStatus.equals( DocumentEngine.STATUS_Completed )) {
                options[ index++ ] = DocumentEngine.ACTION_ReActivate;
            }
        // Anulación de inventario
        } else if( m_AD_Table_ID == MInventory.Table_ID ) {
            if( DocStatus.equals( DocumentEngine.STATUS_Completed )) {
                options[ index++ ] = DocumentEngine.ACTION_Void;
            }
        	
        }

		/*
		 * @ modificacion: 13-ago-2007
		 * Quitar del array todas las acciones que figuren en las restriciones
		 * */
		String sql = "SELECT ALLDOCUMENTTYPES, C_DOCTYPE_ID, INV_DOCACTION, ALLROLES, AD_ROLE_ID, AD_ORG_ID" +
				" FROM AD_ROLE_INVALID_ACTION WHERE " +
				"AD_CLIENT_ID=? AND ISACTIVE='Y' " +
				"AND AD_TABLE_ID=? ";
				//AND AD_ORG_ID=?
				//+ "AND AD_ROLE_ID=?";
		boolean allDocs = true;
		String[] opValidas= new String[options.length];
		Vector opciones = new Vector();
		boolean allRoles = false;
		int role = 0;
		int org = 0;
		
		PreparedStatement pst = DB.prepareStatement(sql);
		try{
			pst.setInt(1,Env.getAD_Client_ID(Env.getCtx()));
			//pst.setInt(2,Env.getAD_Org_ID(Env.getCtx()));
			pst.setInt(2,m_AD_Table_ID);
			//pst.setInt(4,Env.getAD_Role_ID(Env.getCtx()));
			ResultSet rs = pst.executeQuery();
			while(rs.next()){
				allDocs = "Y".equals(rs.getString("ALLDOCUMENTTYPES"));
				allRoles = "Y".equals(rs.getString("ALLROLES"));
				role = rs.getInt("AD_ROLE_ID");
				org = rs.getInt("AD_ORG_ID");
				if(org == 0 || org == Env.getAD_Org_ID(Env.getCtx())){
					if(allDocs && (allRoles || Env.getAD_Role_ID(Env.getCtx()) == role) )
						opciones.addElement(rs.getString("INV_DOCACTION"));
					else if(allRoles || Env.getAD_Role_ID(Env.getCtx()) == role){
						//obtener tipo actual y si es el mismo agrego
						int doctype=0;
						doctype = Env.getContextAsInt(Env.getCtx(), m_WindowNo, "C_DocType_ID");
						// 
						int doctypeTarget = Env.getContextAsInt(Env.getCtx(), m_WindowNo, "C_DocTypeTarget_ID");
						log.finer("C_DocType_ID="+doctype);
						log.finer("C_DocTypeTarget_ID="+doctypeTarget);
						if(doctype == 0)
							doctype = doctypeTarget;
						
						if(doctype == rs.getInt("C_DOCTYPE_ID"))
							opciones.addElement(rs.getString("INV_DOCACTION"));
						
					}
				}
			}
			rs.close();
			pst.close();
			//sacar del array las acciones
			int j = 0;
			int i = 0;
			for(i = 0; i < index; i++){
				if(!estaEnarray(opciones,options[i])){
					opValidas[j] = options[i];
					j++;
				}
			}
			if(opciones.size() > 0){ //encontro restricciones
				//vacio options y lo lleno con las validas solamente
				options = options = new String[s_value.length];
				index = 0;//actualiza index
				for(j = 0; j < opValidas.length;j++){
					if(opValidas[j] != null)
						options[index++] = opValidas[j];
					else
						break;//termina si encuentra null
				}
			}
		}catch(SQLException e) {
			e.printStackTrace();
        }

		///////////////////////////FIN modif Disytel		

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
