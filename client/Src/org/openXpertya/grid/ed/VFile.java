
package org.openXpertya.grid.ed;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.util.logging.Level;

import javax.swing.*;

import org.compiere.plaf.*;
import org.compiere.swing.*;
import org.openXpertya.util.*;

/**
 *	File/Path Selection
 *
 *  @author 	Initial: Jirimuto
 *  @version 	$Id: VFile.java,v 1.4 2006/03/15 02:53:28 jjanke Exp $
 */
public class VFile extends JComponent implements VEditor, ActionListener
{
	public VFile(String columnName, boolean mandatory, 
			boolean isReadOnly, boolean isUpdateable, boolean files)
	{
		this(columnName, mandatory, isReadOnly, isUpdateable, files, null);
	}
	
	/**
	 *	Constructor
	 *
	 * 	@param columnName column name
	 * 	@param mandatory mandatory
	 * 	@param isReadOnly read only
	 * 	@param isUpdateable updateable
	 * 	@param files Files only if false Directory only
	 */
	public VFile(String columnName, boolean mandatory, 
		boolean isReadOnly, boolean isUpdateable, boolean files, ActionListener anotherListener)
	{
		super();
		super.setName(columnName);
		m_columnName = columnName;
		if (files)	//	default Directories
			m_selectionMode = JFileChooser.FILES_ONLY;
		String col = columnName.toLowerCase();
		if (col.indexOf("open") != -1 || col.indexOf("load") != -1)
			m_dialogType = JFileChooser.OPEN_DIALOG;
		else if (col.indexOf("save") != -1)
			m_dialogType = JFileChooser.SAVE_DIALOG;
		//
		LookAndFeel.installBorder(this, "TextField.border");
		this.setLayout(new BorderLayout());
		//  Size
		this.setPreferredSize(m_text.getPreferredSize());		//	causes r/o to be the same length
		int height = m_text.getPreferredSize().height;

		//  Button
		m_button.setIcon(Env.getImageIcon("Open16.gif"));
		m_button.setMargin(new Insets(0,0,0,0));
		m_button.setPreferredSize(new Dimension(height, height));
		m_button.addActionListener(this);
		this.add(m_button, BorderLayout.EAST);
		//	***	Button & Text	***
		m_text.setBorder(null);
		m_text.setEditable(false);
		m_text.setFocusable(true);
		m_text.setFont(CompierePLAF.getFont_Field());
		m_text.setForeground(CompierePLAF.getTextColor_Normal());
		m_text.addMouseListener(new VFile_mouseAdapter(this));
		this.add(m_text, BorderLayout.CENTER);

		//	Editable
		if (isReadOnly || !isUpdateable)
			setReadWrite (false);
		else
			setReadWrite (true);
		setMandatory (mandatory);
		
		m_anotherListener = anotherListener; 
	}	//	VFile

	/**
	 *  Dispose
	 */
	public void dispose()
	{
		m_text = null;
		m_button = null;
	}   //  dispose

	/** The Text Field                  */
	private JTextField			m_text = new JTextField(VLookup.DISPLAY_LENGTH);
	private byte[]				m_data = null;
	/** The Button                      */
	private CButton				m_button = new CButton();
	/** Column Name						*/
	private String				m_columnName;
	/** Selection Mode					*/
	private int					m_selectionMode = JFileChooser.DIRECTORIES_ONLY;
	/** Save/Open						*/
	private int					m_dialogType = JFileChooser.CUSTOM_DIALOG;
	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(VFile.class);
	/** Otro listener */
	private ActionListener		m_anotherListener;

	/**
	 *	Enable/disable
	 *  @param value true if ReadWrite
	 */
	public void setReadWrite (boolean value)
	{
		m_button.setReadWrite (value);
		if (m_button.isVisible() != value)
			m_button.setVisible (value);
		setBackground(false);
	}	//	setReadWrite

	/**
	 *	IsReadWrite
	 *  @return value true if ReadWrite
	 */
	public boolean isReadWrite()
	{
		return m_button.isReadWrite();
	}	//	isReadWrite

	/**
	 *	Set Mandatory (and back bolor)
	 *  @param mandatory true if mandatory
	 */
	public void setMandatory (boolean mandatory)
	{
		m_button.setMandatory(mandatory);
		setBackground(false);
	}	//	setMandatory

	/**
	 *	Is it mandatory
	 *  @return true if mandatory
	 */
	public boolean isMandatory()
	{
		return m_button.isMandatory();
	}	//	isMandatory

	/**
	 *	Set Background
	 *  @param color color
	 */
	public void setBackground (Color color)
	{
		if (!color.equals(m_text.getBackground()))
			m_text.setBackground(color);
	}	//	setBackground

	/**
	 *  Set Background based on editable / mandatory / error
	 *  @param error if true, set background to error color, otherwise mandatory/editable
	 */
	public void setBackground (boolean error)
	{
		if (error)
			setBackground(CompierePLAF.getFieldBackground_Error());
		else if (!isReadWrite())
			setBackground(CompierePLAF.getFieldBackground_Inactive());
		else if (isMandatory())
			setBackground(CompierePLAF.getFieldBackground_Mandatory());
		else
			setBackground(CompierePLAF.getFieldBackground_Normal());
	}   //  setBackground

	/**
	 *  Set Foreground
	 *  @param fg color
	 */
	public void setForeground(Color fg)
	{
		m_text.setForeground(fg);
	}   //  setForeground

	/**
	 *	Set Editor to value
	 *  @param value value
	 */
	public void setValue(Object value)
	{		
		if (value == null)
		{
			m_text.setText(null);
			m_data = null;
		}
		else
		{
			m_data = (byte[]) value;
			m_text.setText("(fichero)-[" + m_data.length + " bytes]");
		}
	}	//	setValue

	/**
	 *  Property Change Listener
	 *  @param evt PropertyChangeEvent
	 */
	public void propertyChange (PropertyChangeEvent evt)
	{
		if (evt.getPropertyName().equals(org.openXpertya.model.MField.PROPERTY)) {
			byte[] data =  (byte[])evt.getNewValue();
			setValue(data);
		}
	}   //  propertyChange

	/**
	 *	Return Editor value
	 *  @return value
	 */
	public Object getValue()
	{
		return m_data;
	}	//	getValue

	/**
	 *  Return Display Value
	 *  @return display value
	 */
	public String getDisplay()
	{
		return m_text.getText();
	}   //  getDisplay

	/**
	 *	ActionListener - Button - Start Dialog
	 *  @param e ActionEvent
	 */
	public void actionPerformed(ActionEvent e)
	{
		String m_value = m_text.getText();
		//
		log.config(m_value);
		
		JFileChooser chooser = new JFileChooser(m_value);
		chooser.setMultiSelectionEnabled(false);
		chooser.setFileSelectionMode(m_selectionMode);
		chooser.setDialogTitle(Msg.getElement(Env.getCtx(), m_columnName));
		chooser.setDialogType(m_dialogType);
		//	
		int returnVal = -1;
		if (m_dialogType == JFileChooser.SAVE_DIALOG)
			returnVal = chooser.showSaveDialog(this);
		else if (m_dialogType == JFileChooser.OPEN_DIALOG)
			returnVal = chooser.showOpenDialog(this);
		else //	if (m_dialogType == JFileChooser.CUSTOM_DIALOG)
			returnVal= chooser.showDialog(this, Msg.getElement(Env.getCtx(), m_columnName));
		if (returnVal != JFileChooser.APPROVE_OPTION)
			return;
		
		File selectedFile = chooser.getSelectedFile();
		
		// Cargamos el fichero.
		m_data = loadFile(selectedFile);
		
		m_text.setText(selectedFile.getAbsolutePath() );
		
        try {
            fireVetoableChange( m_columnName,"",getValue());
        } catch( PropertyVetoException pve ) {
        }
        
        /* disparar otro listener si el mismo se encuentra cargado */
        if (m_anotherListener != null)
        	m_anotherListener.actionPerformed(e);
		
	}	//	actionPerformed
	
	private byte[] loadFile(File file) {
		byte[] data = null;
		try {
			FileInputStream fis = new FileInputStream (file);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024*8];   //  8kB
			int length = -1;
			while ((length = fis.read(buffer)) != -1)
				os.write(buffer, 0, length);
			fis.close();
			data = os.toByteArray();
			os.close();
		}
		catch (IOException ioe)
		{
			log.log(Level.SEVERE, "addEntry (file)", ioe);
		}		
		
		return data;
	}

	/**
	 *  Action Listener Interface
	 *  @param listener listener
	 */
	public void addActionListener(ActionListener listener)
	{
		m_text.addActionListener(listener);
	}   //  addActionListener

	/**
	 *  Set Field/WindowNo for ValuePreference (NOP)
	 *  @param mField Model Field
	 */
	public void setField (org.openXpertya.model.MField mField)
	{
	}   //  setField

}	//	VFile

/******************************************************************************
 *	Mouse Listener for Popup Menu
 */
final class VFile_mouseAdapter extends java.awt.event.MouseAdapter
{
	/**
	 *	Constructor
	 *  @param adaptee adaptee
	 */
	VFile_mouseAdapter(VFile adaptee)
	{
		m_adaptee = adaptee;
	}	//	VLookup_mouseAdapter

	private VFile m_adaptee;

	/**
	 *	Mouse Listener
	 *  @param e MouseEvent
	 */
	public void mouseClicked(MouseEvent e)
	{
		
	}	//	mouse Clicked

}	//	VFile_mouseAdapter
