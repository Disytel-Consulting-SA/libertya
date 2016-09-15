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
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import org.compiere.plaf.CompiereColor;
import org.compiere.swing.CDialog;
import org.compiere.swing.CPanel;
import org.openXpertya.apps.ConfirmPanel.DialogButton;
import org.openXpertya.grid.GridController;
import org.openXpertya.grid.ed.VCheckBox;
import org.openXpertya.grid.ed.VEditor;
import org.openXpertya.grid.ed.VEditorFactory;
import org.openXpertya.model.Callout;
import org.openXpertya.model.CalloutProcess;
import org.openXpertya.model.MField;
import org.openXpertya.model.MFieldVO;
import org.openXpertya.model.MPInstancePara;
import org.openXpertya.model.MProcess;
import org.openXpertya.model.MultiMap;
import org.openXpertya.plugin.CalloutPluginEngine;
import org.openXpertya.plugin.MPluginStatus;
import org.openXpertya.plugin.MPluginStatusCallout;
import org.openXpertya.plugin.common.PluginCalloutUtils;
import org.openXpertya.plugin.common.PluginUtils;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Evaluatee;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ProcessParameter extends CDialog implements ActionListener,VetoableChangeListener, Evaluatee {

    private static final String NO_HELP = "NoHelp";

	/**
     * Constructor de la clase ...
     *
     *
     * @param frame
     * @param WindowNo
     * @param pi
     */

    public ProcessParameter( Frame frame,int WindowNo,ProcessInfo pi ) {
        super( frame,pi.getTitle(),true );
        m_frame = frame;

        try {
            jbInit();
        } catch( Exception ex ) {
            log.log( Level.SEVERE,ex.getMessage());
        }

        //

        m_WindowNo    = WindowNo;
        m_processInfo = pi;

        //

    }    // ProcessParameter

    /** Descripción de Campos */

    private Frame m_frame;

    /** Descripción de Campos */

    private int m_WindowNo;

    /** Descripción de Campos */

    private ProcessInfo m_processInfo;

    /** Descripción de Campos */

    private boolean m_isOK = false;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( ProcessParameter.class );

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

    private ArrayList m_vEditors = new ArrayList();

    /** Descripción de Campos */

    private ArrayList m_vEditors2 = new ArrayList();    // for ranges

    /** Descripción de Campos */

    private ArrayList m_mFields = new ArrayList();

    /** Descripción de Campos */

    private ArrayList m_mFields2 = new ArrayList();

    //

    /** Descripción de Campos */

    private CPanel mainPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    private CPanel centerPanel = new CPanel();

    /** Descripción de Campos */

    private GridBagLayout centerLayout = new GridBagLayout();

    /** Descripción de Campos */

    private ConfirmPanel confirmPanel = new ConfirmPanel( true );

    /** Descripción */
    
    private JEditorPane message = new JEditorPane();

    /** Contenedor del descriptor */

    private JScrollPane messagePane = new JScrollPane( message );

    private String description;
    
    private String help;
    
    private Map<String, MField> fields = new HashMap<String, MField>();
    
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
        mainPanel.add(messagePane, BorderLayout.NORTH);
        mainPanel.add( centerPanel,BorderLayout.CENTER );
        mainPanel.add( confirmPanel,BorderLayout.SOUTH );
        DialogButton bHelp = ConfirmPanel.createHelpButton(false);
        confirmPanel.addComponent(bHelp);
        confirmPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        confirmPanel.addActionListener( this );
        bHelp.addActionListener(this);
    }    // jbInit

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        m_vEditors.clear();
        m_vEditors2.clear();
        m_mFields.clear();
        m_mFields2.clear();
        this.removeAll();
        super.dispose();
    }    // dispose

    static public PreparedStatement GetProcessParameters(int AD_Process_ID) throws SQLException 
    {
        String sql = null;

        if( Env.isBaseLanguage( Env.getCtx(),"AD_Process_Para" )) {
            sql = "SELECT p.Name, p.Description, p.Help, " + "p.AD_Reference_ID, p.AD_Process_Para_ID, " + "p.FieldLength, p.IsMandatory, p.IsRange, p.ColumnName, " + "p.DefaultValue, p.DefaultValue2, p.VFormat, p.ValueMin, p.ValueMax, " + "p.SeqNo, p.AD_Reference_Value_ID, vr.Code AS ValidationCode, p.sameline, p.displaylogic, p.isencrypted, p.isreadonly, p.ReadOnlyLogic, p.Callout, p.CalloutAlsoOnLoad " + "FROM AD_Process_Para p" + " LEFT OUTER JOIN AD_Val_Rule vr ON (p.AD_Val_Rule_ID=vr.AD_Val_Rule_ID) " + "WHERE p.AD_Process_ID=?"    // 1
                  + " AND p.IsActive='Y' " + "ORDER BY SeqNo";
        } else {
            sql = "SELECT t.Name, t.Description, t.Help, " + "p.AD_Reference_ID, p.AD_Process_Para_ID, " + "p.FieldLength, p.IsMandatory, p.IsRange, p.ColumnName, " + "p.DefaultValue, p.DefaultValue2, p.VFormat, p.ValueMin, p.ValueMax, " + "p.SeqNo, p.AD_Reference_Value_ID, vr.Code AS ValidationCode, p.sameline, p.displaylogic, p.isencrypted, p.isreadonly, p.ReadOnlyLogic, p.Callout, p.CalloutAlsoOnLoad " + "FROM AD_Process_Para p" + " INNER JOIN AD_Process_Para_Trl t ON (p.AD_Process_Para_ID=t.AD_Process_Para_ID)" + " LEFT OUTER JOIN AD_Val_Rule vr ON (p.AD_Val_Rule_ID=vr.AD_Val_Rule_ID) " + "WHERE p.AD_Process_ID=?"    // 1
                  + " AND t.AD_Language='" + Env.getAD_Language( Env.getCtx()) + "'" + " AND p.IsActive='Y' " + "ORDER BY SeqNo";
        }

        PreparedStatement pstmt = DB.prepareStatement( sql, PluginUtils.getPluginInstallerTrxName() );

        pstmt.setInt( 1, AD_Process_ID );

        return pstmt;
    }
    
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

        message.setContentType("text/html");
        MProcess process = new MProcess(Env.getCtx(), m_processInfo.getAD_Process_ID(), PluginUtils.getPluginInstallerTrxName());
        message.setText("<p style=\"text-align:center;margin-top:0\">" +  getDescription() + "</p>");
        message.setEditable(false);
        
        //


        // Create Fields

        boolean hasFields = false;

        try {
        	PreparedStatement pstmt = GetProcessParameters(m_processInfo.getAD_Process_ID());
            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                hasFields = true;
                createField( rs );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE, "ProcessParameter.GetProcessParameters", e );
        }

        // both vectors the same?

        if( (m_mFields.size() != m_mFields2.size()) || (m_mFields.size() != m_vEditors.size()) || (m_mFields2.size() != m_vEditors2.size())) {
            log.log( Level.SEVERE,"View & Model vector size is different" );
        }

        // clean up

        if( hasFields ) {
            gbc.gridy = m_line++;
            centerPanel.add( Box.createVerticalStrut( 10 ),gbc );      // bottom gap
            gbc.gridx = 3;
            centerPanel.add( Box.createHorizontalStrut( 12 ),gbc );    // right gap
            processCallouts();
            updateComponents(true);
            AEnv.positionCenterWindow( m_frame,this );
        } else {
            dispose();
        }

        return hasFields;
    }    // initDialog

    private void loadMessage() {
        boolean trl = !Env.isBaseLanguage( Env.getCtx(),"AD_Process" );
        //String result = new String();
        String  SQL = "SELECT Name, Description, Help, IsReport " + "FROM AD_Process " + "WHERE AD_Process_ID=?";
        String desc = new String();
        String help = new String();
        
        if( trl ) {
            SQL = "SELECT t.Name, t.Description, t.Help, p.IsReport " + "FROM AD_Process p, AD_Process_Trl t " + "WHERE p.AD_Process_ID=t.AD_Process_ID" + " AND p.AD_Process_ID=? AND t.AD_Language=?";
        }

        try {
            PreparedStatement pstmt = DB.prepareStatement( SQL, PluginUtils.getPluginInstallerTrxName() );

            pstmt.setInt(1, m_processInfo.getAD_Process_ID());

            if( trl ) {
                pstmt.setString( 2,Env.getAD_Language( Env.getCtx()));
            }

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                String m_Name     = rs.getString( 1 );
                boolean m_IsReport = rs.getString( 4 ).equals( "Y" );

                //

                //desc += ( "<p>" );

                String s = rs.getString( 2 );    // Description

                if( rs.wasNull()) {
                    desc += Msg.getMsg( Env.getCtx(),"StartProcess?");
                } else {
                    desc += s;
                }

                //desc += "</p>";
                
                setDescription(desc);
                
                s = rs.getString( 3 );    // Help

                if( !rs.wasNull()) {
                    help += s;
                }
                setHelp(help);
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,SQL,e );
        }
    }

	/**
     * Descripción de Método
     *
     *
     * @param rs
     */

    private void createField( ResultSet rs ) {

        // Create Field

        MFieldVO voF    = MFieldVO.createParameter( Env.getCtx(),m_WindowNo,rs );
        MField   mField = new MField( voF );

        m_mFields.add( mField );    // add to Fields
        fields.put(mField.getColumnName(), mField);
        
        // Label Preparation

        gbc.gridy     = voF.sameLine?m_line-1:m_line;
        m_line++;
        gbc.gridwidth = 1;
        gbc.fill      = GridBagConstraints.HORIZONTAL;    // required for right justified
        gbc.gridx   = voF.sameLine?2:0;
        gbc.weightx = 0;

        JLabel label = VEditorFactory.getLabel( mField );

        if( label == null ) {
            gbc.insets = nullInset;
            centerPanel.add( Box.createHorizontalStrut( 12 ),gbc );    // left gap
        } else {
            gbc.insets = labelInset;
            centerPanel.add( label,gbc );
        }

        // Field Preparation

        gbc.insets    = voF.sameLine?fieldInsetRight:fieldInset;
        gbc.fill      = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;
        gbc.gridx     = voF.sameLine?3:1;
        gbc.weightx   = 1;

		// The Editor

        VEditor vEditor = VEditorFactory.getEditor( mField,false );

        vEditor.addVetoableChangeListener( this );
        
        if (voF.sameLine && mField.getDisplayType() == DisplayType.YesNo) {
        	((VCheckBox) vEditor).setHorizontalAlignment(SwingConstants.RIGHT);
        }

        // MField => VEditor - New Field value to be updated to editor

        mField.addPropertyChangeListener( vEditor );

        // Set Default

        Object defaultObject = mField.getDefault();

        mField.setValue( defaultObject,true );

        //

        centerPanel.add(( Component )vEditor,gbc );
        m_vEditors.add( vEditor );    // add to Editors

        //

        if( voF.isRange ) {

            // To Label

            gbc.gridx = 2;
            label = VEditorFactory.getLabel( mField );
            label.setText(" - ");
            label.setHorizontalAlignment(JLabel.CENTER);
            gbc.insets = labelInset;
            centerPanel.add( label,gbc );

            // To Field

            gbc.gridx  = 3;
            gbc.insets = fieldInsetRight;

            //

            MFieldVO voF2    = MFieldVO.createParameter( voF );
            MField   mField2 = new MField( voF2 );

            m_mFields2.add( mField2 );
            fields.put(mField.getColumnName()+"_TO", mField2);
            
            // The Editor

            VEditor vEditor2 = VEditorFactory.getEditor( mField2,false );

            if (mField.getDisplayType() == DisplayType.YesNo) {
            	((VCheckBox) vEditor2).setHorizontalAlignment(SwingConstants.RIGHT);
            }
            
            // New Field value to be updated to editor

            mField2.addPropertyChangeListener( vEditor2 );

            // Set Default

            Object defaultObject2 = mField2.getDefault();

            mField2.setValue( defaultObject2,true );

            //

            centerPanel.add(( Component )vEditor2,gbc );
            m_vEditors2.add( vEditor2 );
        } else {
            m_mFields2.add( null );
            m_vEditors2.add( null );
        }
    }    // createField

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        m_isOK = false;

        if( e.getActionCommand().equals( ConfirmPanel.A_OK )) {

            // check if saving parameters is complete

            if( saveParameters()) {
                m_isOK = true;
                dispose();
            }
        } else if( e.getActionCommand().equals( ConfirmPanel.A_CANCEL )) {
            dispose();
        } else if (e.getActionCommand().equals(ConfirmPanel.A_HELP)) {
        	String message = getHelp();
        	if (getHelp().length() == 0) {
        		message = Msg.translate(Env.getCtx(), NO_HELP);
        	}
        	ADialog.info(m_WindowNo, this, message);
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param evt
     *
     * @throws PropertyVetoException
     */

    public void vetoableChange( PropertyChangeEvent evt ) throws PropertyVetoException {

		 String valueStr = (evt.getNewValue() == null)
	            ?""
	            :evt.getNewValue().toString();
	 
		 if (evt.getNewValue() instanceof Boolean) {
			 valueStr = ((Boolean) evt.getNewValue() == false)
		             ?"N"
		             :"Y";
		 }

		 Env.setContext( Env.getCtx(),m_WindowNo,evt.getPropertyName(),valueStr );
		 processCallout(fields.get(evt.getPropertyName()), evt.getNewValue());
		 updateComponents(false);
    }    // vetoableChange

    
    public void updateComponents(boolean init){
		GridController.updateComponents(centerPanel.getComponents(), fields,
				false, true, true);
		pack();
    }
    
    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean saveParameters() {
        log.config( "" );

        StringBuffer sb   = new StringBuffer();
        int          size = m_mFields.size();
        boolean isDisplayed;
        
        for( int i = 0;i < size;i++ ) {
            MField field = ( MField )m_mFields.get( i );
            isDisplayed = field.isDisplayed(true);
            
            if( field.isMandatory( true ) && isDisplayed)         // check context
            {
                VEditor vEditor = ( VEditor )m_vEditors.get( i );
                Object  data    = vEditor.getValue();

                if( (data == null) || (data.toString().length() == 0) ) {
                    field.setInserting( true );    // set editable (i.e. updateable) otherwise deadlock
                    field.setError( true );

                    if( sb.length() > 0 ) {
                        sb.append( ", " );
                    }

                    sb.append( field.getHeader());
                } else {
                    field.setError( false );
                }

                // Check for Range

                VEditor vEditor2 = ( VEditor )m_vEditors2.get( i );

                if( vEditor2 != null ) {
                    Object data2  = vEditor.getValue();
                    MField field2 = ( MField )m_mFields2.get( i );

                    if( (data2 == null) || (data2.toString().length() == 0) ) {
                        field.setInserting( true );    // set editable (i.e. updateable) otherwise deadlock
                        field2.setError( true );

                        if( sb.length() > 0 ) {
                            sb.append( ", " );
                        }

                        sb.append( field.getHeader());
                    } else {
                        field2.setError( false );
                    }
                }    // range field
            }        // mandatory
            else if(!isDisplayed){
            	field.setValue(null, true);
            	VEditor vEditor2 = ( VEditor )m_vEditors2.get( i );
                if( vEditor2 != null ) {
                    MField field2 = ( MField )m_mFields2.get( i );
                    field2.setValue(null,true);
                }
            }
        }            // field loop

        if( sb.length() != 0 ) {
            ADialog.error( m_WindowNo,this,"FillMandatory",sb.toString());

            return false;
        }

        for( int i = 0;i < m_mFields.size();i++ ) {

            // Get Values

            VEditor editor  = ( VEditor )m_vEditors.get( i );
            VEditor editor2 = ( VEditor )m_vEditors2.get( i );
            Object  result  = editor.getValue();
            Object  result2 = null;

            if( editor2 != null ) {
                result2 = editor2.getValue();
            }

            // Don't save NULL values

            if( (result == null) && (result2 == null) ) {
                continue;
            }

            // Create Parameter

            MPInstancePara para = new MPInstancePara( Env.getCtx(),m_processInfo.getAD_PInstance_ID(),i );
            MField mField = ( MField )m_mFields.get( i );

            para.setParameterName( mField.getColumnName());

            // Date

            if( (result instanceof Timestamp) || (result2 instanceof Timestamp) ) {
                para.setP_Date(( Timestamp )result );

                if( (editor2 != null) && (result2 != null) ) {
                    para.setP_Date_To(( Timestamp )result2 );
                }
            }

            // Integer

            else if( (result instanceof Integer) || (result2 instanceof Integer) ) {
                if( result != null ) {
                    Integer ii = ( Integer )result;

                    para.setP_Number( ii.intValue());
                }

                if( (editor2 != null) && (result2 != null) ) {
                    Integer ii = ( Integer )result2;

                    para.setP_Number_To( ii.intValue());
                }
            }

            // BigDecimal

            else if( (result instanceof BigDecimal) || (result2 instanceof BigDecimal) ) {
                para.setP_Number(( BigDecimal )result );

                if( (editor2 != null) && (result2 != null) ) {
                    para.setP_Number_To(( BigDecimal )result2 );
                }
            }

            // Boolean

            else if( result instanceof Boolean ) {
                Boolean bb    = ( Boolean )result;
                String  value = bb.booleanValue()
                                ?"Y"
                                :"N";

                para.setP_String( value );

                // to does not make sense

            }

            // String

            else {
                if( result != null ) {
                    para.setP_String( result.toString());
                }

                if( (editor2 != null) && (result2 != null) ) {
                    para.setP_String_To( result2.toString());
                }
            }

            // Info

            para.setInfo( editor.getDisplay());

            if( editor2 != null ) {
                para.setInfo_To( editor2.getDisplay());
            }

            //

            para.save();
            log.fine( para.toString());
        }    // for every parameter

        return true;
    }    // saveParameters

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isOK() {
        return m_isOK;
    }    // isOK

	public String getDescription() {
		if (description == null) {
			loadMessage();
		}
		return description;

	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHelp() {
		if (help == null) {
			loadMessage();
		}
		return help;
	}

	public void setHelp(String help) {
		this.help = help;
	}

	@Override
	public String get_ValueAsString(String variableName) {
		return Env.getContext( Env.getCtx(),m_WindowNo,variableName,true );
	}
	
	protected void processCallouts(){
		Set<String> keys = fields.keySet();
		for (String columnName : keys) {
			MField field = fields.get(columnName);
			if(field.isCalloutAlsoOnLoad()){
				processCallout(fields.get(columnName), true, null);
			}
		}
	}
	
	public String processCallout( MField field, Object newValue) {
		return processCallout( field, false, newValue);
	}
	
	public String processCallout( MField field, boolean onLoad, Object newValue) {
        String callout = field.getCallout();

        Object value    = onLoad?field.getValue():newValue;
        Object oldValue = field.getOldValue();

        StringTokenizer st = new StringTokenizer( callout,";",false );
        
        while( st.hasMoreTokens() )         // for each callout
        {
            String  cmd         = st.nextToken().trim();
            CalloutProcess call = null;
            String  method      = null;
            int     methodStart = cmd.lastIndexOf( "." );

            try {
                if( methodStart != -1 )    // no class
                {
                    Class cClass = Class.forName( cmd.substring( 0,methodStart ));

                    call   = ( CalloutProcess )cClass.newInstance();
                    method = cmd.substring( methodStart + 1 );
                }
            } catch( Exception e ) {
                log.log( Level.SEVERE,"class",e );

                return "Callout Invalid: " + cmd + " (" + e.toString() + ")";
            }

            if( (call == null) || (method == null) || (method.length() == 0) ) {
                return "Callout Invalid: " + method;
            }

            String retValue = "";

            try {
        			retValue = call.start( Env.getCtx(),m_WindowNo,method,field,value,oldValue,fields );
            } catch( Exception e ) {
                log.log( Level.SEVERE,"start",e );
                retValue = "Callout Invalid: " + e.toString();

                return retValue;
            }

            if( !retValue.equals( "" ))    // interrupt on first error
            {
                log.severe( retValue );

                return retValue;
            }
        }                                  // for each callout

        return "";
    }    // processCallout
}    // ProcessParameter



/*
 *  @(#)ProcessParameter.java   02.07.07
 * 
 *  Fin del fichero ProcessParameter.java
 *  
 *  Versión 2.2
 *
 */
