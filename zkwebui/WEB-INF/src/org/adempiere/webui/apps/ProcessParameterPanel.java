/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 Adempiere, Inc. All Rights Reserved.               *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/

package org.adempiere.webui.apps;



import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.adempiere.webui.component.Column;
import org.adempiere.webui.component.Columns;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WEditorPopupMenu;
import org.adempiere.webui.editor.WebEditorFactory;
import org.adempiere.webui.event.ContextMenuListener;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.util.UserPreference;
import org.adempiere.webui.window.FDialog;
import org.openXpertya.model.CalloutProcess;
import org.openXpertya.model.IProcessParameter;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MField;
import org.openXpertya.model.MFieldVO;
import org.openXpertya.model.MLookup;
import org.openXpertya.model.MPInstancePara;
import org.openXpertya.model.MultiMap;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Space;

/**
 *	Process Parameter Panel, based on existing ProcessParameter dialog.
 *	- Embedded in ProcessDialog
 *	- checks, if parameters exist and inquires and saves them
 *
 * 	@author 	Low Heng Sin
 * 	@version 	2006-12-01
 */
@SuppressWarnings("serial")
public class ProcessParameterPanel extends Panel 
implements ValueChangeListener, IProcessParameter 
{
		private String width;

		/**
		 *	Dynamic generated Parameter panel.
		 *  @param WindowNo window
		 *  @param pi process info
		 */
		public ProcessParameterPanel(int WindowNo, ProcessInfo pi)
		{
			this(WindowNo, pi, "100%");
		}	//	ProcessParameterPanel
		
		/**
		 *	Dynamic generated Parameter panel.
		 *  @param WindowNo window
		 *  @param pi process info
		 */
		public ProcessParameterPanel(int WindowNo, ProcessInfo pi, String width)
		{
			//
			m_WindowNo = WindowNo;
			m_processInfo = pi;
			this.width = width;
			//
			initComponent();
		}	//	ProcessParameterPanel

		private void initComponent() {
			centerPanel = GridFactory.newGridLayout();
			centerPanel.setInnerWidth(width);
			this.appendChild(centerPanel);
			
			//setup columns
	    	Columns columns = new Columns();
	    	centerPanel.appendChild(columns);
	    	Column col = new Column();
	    	col.setWidth("30%");
	    	columns.appendChild(col);
	    	col = new Column();
	    	col.setWidth("65%");
	    	columns.appendChild(col);
	    	col = new Column();
	    	col.setWidth("5%");
	    	columns.appendChild(col);

		}

		private int			m_WindowNo;
		private ProcessInfo m_processInfo;
		/**	Logger			*/
		private static CLogger log = CLogger.getCLogger(ProcessParameterPanel.class);
	
		//
		private ArrayList<WEditor>	m_wEditors = new ArrayList<WEditor>();
		private ArrayList<WEditor>	m_wEditors2 = new ArrayList<WEditor>();		//	for ranges
		private ArrayList<MField>	m_mFields = new ArrayList<MField>();
		private ArrayList<MField>	m_mFields2 = new ArrayList<MField>();
		private ArrayList<Label> m_separators = new ArrayList<Label>();
		
		private Map<String, MField> fields = new HashMap<String, MField>();//
		
		private Grid centerPanel = null;

		protected MultiMap m_depOnField = new MultiMap();
		
		/**
		 *  Dispose
		 */
		public void dispose()
		{
			m_wEditors.clear();
			m_wEditors2.clear();
			m_mFields.clear();
			m_mFields2.clear();
			fields.clear();
	        m_depOnField.clear();
		}   //  dispose

		/**
		 *	Read Fields to display
		 *  @return true if loaded OK
		 */
		public boolean init()
		{
			log.config("");

			// ASP
			MClient client = MClient.get(Env.getCtx());
			String ASPFilter = "";
//			if (client.isUseASP())
//				ASPFilter =
//					  "   AND (   p.AD_Process_Para_ID IN ( "
//					// Just ASP subscribed process parameters for client "
//					+ "              SELECT pp.AD_Process_Para_ID "
//					+ "                FROM ASP_Process_Para pp, ASP_Process p, ASP_Level l, ASP_ClientLevel cl "
//					+ "               WHERE p.ASP_Level_ID = l.ASP_Level_ID "
//					+ "                 AND cl.AD_Client_ID = " + client.getAD_Client_ID()
//					+ "                 AND cl.ASP_Level_ID = l.ASP_Level_ID "
//					+ "                 AND pp.ASP_Process_ID = p.ASP_Process_ID "
//					+ "                 AND pp.IsActive = 'Y' "
//					+ "                 AND p.IsActive = 'Y' "
//					+ "                 AND l.IsActive = 'Y' "
//					+ "                 AND cl.IsActive = 'Y' "
//					+ "                 AND pp.ASP_Status = 'S') " // Show
//					+ "        OR p.AD_Process_Para_ID IN ( "
//					// + show ASP exceptions for client
//					+ "              SELECT AD_Process_Para_ID "
//					+ "                FROM ASP_ClientException ce "
//					+ "               WHERE ce.AD_Client_ID = " + client.getAD_Client_ID()
//					+ "                 AND ce.IsActive = 'Y' "
//					+ "                 AND ce.AD_Process_Para_ID IS NOT NULL "
//					+ "                 AND ce.AD_Tab_ID IS NULL "
//					+ "                 AND ce.AD_Field_ID IS NULL "
//					+ "                 AND ce.ASP_Status = 'S') " // Show
//					+ "       ) "
//					+ "   AND p.AD_Process_Para_ID NOT IN ( "
//					// minus hide ASP exceptions for client
//					+ "          SELECT AD_Process_Para_ID "
//					+ "            FROM ASP_ClientException ce "
//					+ "           WHERE ce.AD_Client_ID = " + client.getAD_Client_ID()
//					+ "             AND ce.IsActive = 'Y' "
//					+ "             AND ce.AD_Process_Para_ID IS NOT NULL "
//					+ "             AND ce.AD_Tab_ID IS NULL "
//					+ "             AND ce.AD_Field_ID IS NULL "
//					+ "             AND ce.ASP_Status = 'H')"; // Hide
//			//
			String sql = null;
			if (Env.isBaseLanguage(Env.getCtx(), "AD_Process_Para"))
				sql = "SELECT p.Name, p.Description, p.Help, "
					+ "p.AD_Reference_ID, p.AD_Process_Para_ID, "
					+ "p.FieldLength, p.IsMandatory, p.IsRange, p.ColumnName, "
					+ "p.DefaultValue, p.DefaultValue2, p.VFormat, p.ValueMin, p.ValueMax, "
					+ "p.SeqNo, p.AD_Reference_Value_ID, vr.Code AS ValidationCode, "
					+ "p.ReadOnlyLogic, p.sameline, p.DisplayLogic, p.isencrypted, p.isReadOnly, "
					+ "p.Callout, p.CalloutAlsoOnLoad "
					+ "FROM AD_Process_Para p"
					+ " LEFT OUTER JOIN AD_Val_Rule vr ON (p.AD_Val_Rule_ID=vr.AD_Val_Rule_ID) "
					+ "WHERE p.AD_Process_ID=?"		//	1
					+ " AND p.IsActive='Y' "
					+ ASPFilter + " ORDER BY SeqNo";
			else
				sql = "SELECT t.Name, t.Description, t.Help, "
					+ "p.AD_Reference_ID, p.AD_Process_Para_ID, "
					+ "p.FieldLength, p.IsMandatory, p.IsRange, p.ColumnName, "
					+ "p.DefaultValue, p.DefaultValue2, p.VFormat, p.ValueMin, p.ValueMax, "
					+ "p.SeqNo, p.AD_Reference_Value_ID, vr.Code AS ValidationCode, "
					+ "p.ReadOnlyLogic, p.sameline, p.DisplayLogic, p.isencrypted, p.isReadOnly, "
					+ "p.Callout, p.CalloutAlsoOnLoad "
					+ "FROM AD_Process_Para p"
					+ " INNER JOIN AD_Process_Para_Trl t ON (p.AD_Process_Para_ID=t.AD_Process_Para_ID)"
					+ " LEFT OUTER JOIN AD_Val_Rule vr ON (p.AD_Val_Rule_ID=vr.AD_Val_Rule_ID) "
					+ "WHERE p.AD_Process_ID=?"		//	1
					+ " AND t.AD_Language='" + Env.getAD_Language(Env.getCtx()) + "'"
					+ " AND p.IsActive='Y' "
					+ ASPFilter + " ORDER BY SeqNo";

			//	Create Fields
			boolean hasFields = false;
			Rows rows = new Rows();
			try
			{
				PreparedStatement pstmt = DB.prepareStatement(sql, null);
				pstmt.setInt(1, m_processInfo.getAD_Process_ID());
				ResultSet rs = pstmt.executeQuery();
				while (rs.next())
				{
					hasFields = true;
					createField (rs, rows);
				}
				rs.close();
				pstmt.close();
			}
			catch(SQLException e)
			{
				log.log(Level.SEVERE, sql, e);
			}

			//	both vectors the same?
			if (m_mFields.size() != m_mFields2.size()
					|| m_mFields.size() != m_wEditors.size()
					|| m_mFields2.size() != m_wEditors2.size())
				log.log(Level.SEVERE, "View & Model vector size is different");

			//	clean up
			if (hasFields)
			{
				centerPanel.appendChild(rows);
				setDefaultValues();
		    	processCallouts();
		    	processDependencies();
				dynamicDisplay();
			}
			else
				dispose();
			return hasFields;
		}	//	initDialog


		/**
		 *	Create Field.
		 *	- creates Fields and adds it to m_mFields list
		 *  - creates Editor and adds it to m_vEditors list
		 *  Handeles Ranges by adding additional mField/vEditor.
		 *  <p>
		 *  mFields are used for default value and mandatory checking;
		 *  vEditors are used to retrieve the value (no data binding)
		 *
		 * @param rs result set
		 */
		private void createField (ResultSet rs, Rows rows)
		{
			boolean compactMode = "Y".equals(SessionManager.getSessionApplication().getUserPreference().getProperty(UserPreference.P_COMPACT_MODE));
			
			//  Create Field
			MFieldVO voF = MFieldVO.createParameter(Env.getCtx(), m_WindowNo, rs);
			MField mField = new MField (voF);
			m_mFields.add(mField);                      //  add to Fields

			fields.put(mField.getColumnName(), mField);
			
			// Dependientes
	        initDependants(mField);
			
			Row row = new Row();
			
			//	The Editor
			WEditor editor = WebEditorFactory.getEditor(mField, false);
			editor.addValueChangeListener(this);
			editor.dynamicDisplay();

			// Parametro obligatorio?
			if (mField.isMandatory(true)) {
				editor.getLabel().setMandatory(true, true);
			}
			
			// ((Label)editor.getLabel().getDecorator()).getSclass()
			
			//  MField => VEditor - New Field value to be updated to editor
			mField.addPropertyChangeListener(editor);
			//streach component to fill grid cell
            editor.fillHorizontal();
            //setup editor context menu
            WEditorPopupMenu popupMenu = editor.getPopupMenu();                    
            if (popupMenu != null)
            {
            	popupMenu.addMenuListener((ContextMenuListener)editor);
                this.appendChild(popupMenu);
            }
			//
			m_wEditors.add (editor);                   //  add to Editors
			
			if (compactMode) {
				Row labelRow = new Row();
				labelRow.appendChild(new Space());
            	Div div = new Div();
                div.setAlign("left");
				Label label = editor.getLabel();
				div.appendChild(label);
				// Incluir decorator?
	            if (editor.getLabel().getDecorator() != null && editor.getLabel().getDecorator().isVisible()) {
	            	div.appendChild(editor.getLabel().getDecorator());
	            }
	            labelRow.appendChild(div);
                rows.appendChild(labelRow);
			} else {
				Div div = new Div();
				div.setAlign("right");
	            Label label = editor.getLabel();
	            div.appendChild(label);
	            // Incluir decorator?
	            if (editor.getLabel().getDecorator() != null && editor.getLabel().getDecorator().isVisible()) {
	            	div.appendChild(editor.getLabel().getDecorator());
	            }
	            row.appendChild(div);
			}
			//row.appendChild(editor.getLabel().rightAlign());

			//
			if (voF.isRange)
			{
				Hbox box = new Hbox();
				box.appendChild(editor.getComponent());
				//
				MFieldVO voF2 = MFieldVO.createParameter(voF);
				MField mField2 = new MField (voF2);
				m_mFields2.add (mField2);
				fields.put(mField2.getColumnName()+"_TO", mField2);
				//	The Editor
				WEditor editor2 = WebEditorFactory.getEditor(mField2, false);
				//  New Field value to be updated to editor
				mField2.addPropertyChangeListener(editor2);
				editor2.dynamicDisplay();
				editor2.fillHorizontal();
				//setup editor context menu
                popupMenu = editor2.getPopupMenu();                    
                if (popupMenu != null)
                {
                	popupMenu.addMenuListener((ContextMenuListener)editor2);
                    this.appendChild(popupMenu);
                }
				//
				m_wEditors2.add (editor2);
				Label separator = new Label(" - ");
				m_separators.add(separator);
				box.appendChild(separator);
				box.appendChild(editor2.getComponent());
				if (compactMode) {
					row.appendChild(new Space());
				}
				row.appendChild(box);
			}
			else
			{
				if (compactMode) {
					row.appendChild(new Space());
				}
				row.appendChild(editor.getComponent());
				m_mFields2.add (null);
				m_wEditors2.add (null);
				m_separators.add(null);
			}
			rows.appendChild(row);
		}	//	createField

		

		/**
		 *	Save Parameter values
		 *  @return true if parameters saved
		 */
		public boolean saveParameters()
		{
			log.config("");

			/**
			 *	Mandatory fields
			 *  see - MTable.getMandatory
			 */
			StringBuffer sb = new StringBuffer();
			int size = m_mFields.size();
			boolean isDisplayed;
			for (int i = 0; i < size; i++)
			{
				MField field = (MField)m_mFields.get(i);
				isDisplayed = field.isDisplayed(true);
				if (field.isMandatory(true) && isDisplayed)        //  check context
				{
					WEditor wEditor = (WEditor)m_wEditors.get(i);
					Object data = wEditor.getValue();
					if (data == null || data.toString().length() == 0)
					{
						field.setInserting (true);  //  set editable (i.e. updateable) otherwise deadlock
						field.setError(true);
						if (sb.length() > 0)
							sb.append(", ");
						sb.append(field.getHeader());
					}
					else
						field.setError(false);
					//  Check for Range
					WEditor wEditor2 = (WEditor)m_wEditors2.get(i);
					if (wEditor2 != null)
					{
						Object data2 = wEditor.getValue();
						MField field2 = (MField)m_mFields2.get(i);
						if (data2 == null || data2.toString().length() == 0)
						{
							field.setInserting (true);  //  set editable (i.e. updateable) otherwise deadlock
							field2.setError(true);
							if (sb.length() > 0)
								sb.append(", ");
							sb.append(field.getHeader());
						}
						else
							field2.setError(false);
					}   //  range field
				}   //  mandatory
				else if(!isDisplayed){
					field.setValue(null, true);
					WEditor vEditor2 = ( WEditor )m_wEditors2.get( i );
	                if( vEditor2 != null ) {
	                    MField field2 = ( MField )m_mFields2.get( i );
	                    field2.setValue(null,true);
	                }
				}
			}   //  field loop


			if (sb.length() != 0)
			{
				FDialog.error(m_WindowNo, this, "FillMandatory", sb.toString());
				return false;
			}

			/**********************************************************************
			 *	Save Now
			 */
			for (int i = 0; i < m_mFields.size(); i++)
			{
				//	Get Values
				WEditor editor = (WEditor)m_wEditors.get(i);
				WEditor editor2 = (WEditor)m_wEditors2.get(i);
				Object result = editor.getValue();
				Object result2 = null;
				if (editor2 != null)
					result2 = editor2.getValue();
				
	            // Don't save NULL values

	            if( (result == null) && (result2 == null) ) {
	                continue;
	            }

				
				//	Create Parameter
				MPInstancePara para = new MPInstancePara (Env.getCtx(), m_processInfo.getAD_PInstance_ID(), i);
				MField mField = (MField)m_mFields.get(i);
				para.setParameterName(mField.getColumnName());
				
				//	Date
				if (result instanceof Timestamp || result2 instanceof Timestamp)
				{
					para.setP_Date((Timestamp)result);
					if (editor2 != null && result2 != null)
						para.setP_Date_To((Timestamp)result2);
				}
				//	Integer
				else if (result instanceof Integer || result2 instanceof Integer)
				{
					if (result != null)
					{
						Integer ii = (Integer)result;
						para.setP_Number(ii.intValue());
					}
					if (editor2 != null && result2 != null)
					{
						Integer ii = (Integer)result2;
						para.setP_Number_To(ii.intValue());
					}
				}
				//	BigDecimal
				else if (result instanceof BigDecimal || result2 instanceof BigDecimal)
				{
					para.setP_Number ((BigDecimal)result);
					if (editor2 != null && result2 != null)
						para.setP_Number_To ((BigDecimal)result2);
				}
				//	Boolean
				else if (result instanceof Boolean)
				{
					Boolean bb = (Boolean)result;
					String value = bb.booleanValue() ? "Y" : "N";
					para.setP_String (value);
					//	to does not make sense
				}
				//	String
				else
				{
					if (result != null)
						para.setP_String (result.toString());
					if (editor2 != null && result2 != null)
						para.setP_String_To (result2.toString());
				}

				//  Info
				para.setInfo (editor.getDisplay());
				if (editor2 != null)
					para.setInfo_To (editor2.getDisplay());
				//
				para.save();
				log.fine(para.toString());
			}	//	for every parameter

			return true;
		}	//	saveParameters

		/**
		 *	Editor Listener
		 *	@param evt ValueChangeEvent
		 
		 */
		
		public void valueChange(ValueChangeEvent evt) 
		{
			processNewValue(evt.getNewValue(), evt.getPropertyName());
		}
		
		private void processNewValue(Object value, String name) {
			if (value == null)
				value = new String("");

			if (value instanceof String)
				Env.setContext(Env.getCtx(), m_WindowNo, name, (String) value);
			else if (value instanceof Integer)
				Env.setContext(Env.getCtx(), m_WindowNo, name, ((Integer) value)
						.intValue());
			else if (value instanceof Boolean)
				Env.setContext(Env.getCtx(), m_WindowNo, name, ((Boolean) value)
						.booleanValue());
			else if (value instanceof Timestamp)
				Env.setContext(Env.getCtx(), m_WindowNo, name, (Timestamp) value);
			else
				Env.setContext(Env.getCtx(), m_WindowNo, name, value.toString());

			fields.get(name).setValue(value, true, false);
			processCallout(fields.get(name), value);
			processDependencies(fields.get(name));
			dynamicDisplay();
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
		
		private void dynamicDisplay() {
			for(int i = 0; i < m_wEditors.size(); i++) {
				WEditor editor = m_wEditors.get(i);
				MField mField = editor.getGridField();
				if (mField.isDisplayed(true)) {
					if (!editor.isVisible()) {
						editor.setVisible(true);
						if (mField.getVO().isRange) {
							m_separators.get(i).setVisible(true);
							m_wEditors2.get(i).setVisible(true);
						}
					}
					boolean rw = mField.isEditable(false, true, true); // r/w - check if field is Editable
					editor.setReadWrite(rw);
					editor.dynamicDisplay();
					if (mField.getVO().isRange) {
						m_wEditors2.get(i).setReadWrite(rw);
						m_wEditors2.get(i).dynamicDisplay();
					}
				} else if (editor.isVisible()) {
					editor.setVisible(false);
					if (mField.getVO().isRange) {
						m_separators.get(i).setVisible(false);
						m_wEditors2.get(i).setVisible(false);
					}
				}
			}			
		}

		/**
		 * Restore window context.
		 * @author teo_sarca [ 1699826 ]
		 * @see org.compiere.MField.GridField#restoreValue()
		 */
		protected void restoreContext() {
			for (MField f : m_mFields) {
				if (f != null)
					f.restoreValue();
			}
			for (MField f : m_mFields2) {
				if (f != null)
					f.restoreValue();
			}
		}
		
		public ArrayList getDependantList( String columnName ) {
	        return m_depOnField.getValues( columnName );
	    }    // getDependentFieldList
	    
	    public boolean hasDependants( String columnName ) {
	        return m_depOnField.containsKey( columnName );
	    }    // isDependentOn
		
		protected void setDefaultValues() {
	    	setDefaultValues(m_mFields);
	    	setDefaultValues(m_mFields2);
	    }
	    
	    protected void setDefaultValues(List<MField> fields) {
	    	Object defaultObject;
	    	for (MField field : fields) {
	    		if(field != null) {
	    			defaultObject = field.getDefault();
	    			field.refreshLookup();
	        		field.setValue( defaultObject, true, true );
	    		}
			}
	    }
		
	    private void initDependants(MField field) {
	    	ArrayList list = field.getDependentOn();

	        for( int i = 0;i < list.size();i++ ) {
	            m_depOnField.put( list.get( i ),field );    // ColumnName, Field
	        }
	    }
	    
	    protected void processDependencies(){
			Set<String> keys = fields.keySet();
			for (String columnName : keys) {
				processDependencies(fields.get(columnName));
			}
		}
		
		public void processDependencies( MField changedField ) {
	        String columnName = changedField.getColumnName();

	        if( !hasDependants( columnName )) {
	            return;
	        }

	        ArrayList list = getDependantList( columnName );

	        for( int i = 0;i < list.size();i++ ) {
	            MField dependentField = ( MField )list.get( i );
	            if( (dependentField != null) && (dependentField.getLookup() instanceof MLookup) ) {
	                MLookup mLookup = ( MLookup )dependentField.getLookup();
	                if( mLookup.getValidation().indexOf( "@" + columnName + "@" ) != -1 ) {
	                    log.fine( columnName + " changed - " + dependentField.getColumnName() + " set to null" );

	                    dependentField.setValue(null, true);
	                    
	                    /*if(dependentField.getLookup() != null) {
	                    	dependentField.getLookup().removeAllElements();
	                    	dependentField.getLookup().fillComboBox(dependentField.getVO().IsMandatory, false, false, false);
	                    }*/
	                    mLookup.refresh();	                    
	                }
	            }
	        }    // for all dependent fields
	    }        // processDependencies
	    
	}	//	ProcessParameterPanel

