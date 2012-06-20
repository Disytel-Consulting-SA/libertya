/*
 * Copyright (C) 2004 Nicky BRAMANTE
 * 
 * This file is part of FreeQueryBuilder
 * 
 * FreeQueryBuilder is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * Send questions or suggestions to nickyb@interfree.it
 */

package nickyb.fqb.runtime;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import javax.swing.border.EmptyBorder;

import nickyb.fqb.util.DefaultPanel;
import nickyb.fqb.util.DefaultScrollPane;
import nickyb.fqb.util.UIUtilities;

public class InfoDriverPane extends DefaultPanel implements ActionListener
{
	private InfoDriverPane.InfoElement info;
	private DialogAdministrator admin;
		
	private JComboBox cbxFile;
	private JTextField txtName;
	private JTextField txtDriver;
	private JTextField txtExample;
		
	private JButton btnApply;
		
	public InfoDriverPane(DialogAdministrator admin)
	{
		super(2,2);
		this.admin = admin;
			
		txtName = new JTextField();
		txtDriver = new JTextField();
		txtExample = new JTextField();
			
		cbxFile = new JComboBox(new String[]{"$CLASSPATH"});
		cbxFile.setPreferredSize(txtName.getPreferredSize());
		cbxFile.setFont(UIUtilities.fontPLAIN);
		cbxFile.setSize(txtName.getSize());
			
		JPanel detail = new JPanel();
		detail.setPreferredSize(new Dimension(199,199));
			
		GridBagLayout gbl = new GridBagLayout();
		detail.setLayout(gbl);
		detail.setBorder(new EmptyBorder(4,4,4,4));
			
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx	= 1.0;
			
		JLabel lbl;
		gbl.setConstraints(lbl = new JLabel("name:"), gbc);
		detail.add(lbl);
			
		gbc.insets = new Insets(0,0,30,0);
		gbl.setConstraints(txtName, gbc);
		detail.add(txtName);
			
		gbc.insets = new Insets(0,0,0,0);
		gbl.setConstraints(lbl = new JLabel("file:"), gbc);
		detail.add(lbl);
		gbl.setConstraints(cbxFile, gbc);
		detail.add(cbxFile);
		
		gbc.weightx	= 1.0;
		gbl.setConstraints(lbl = new JLabel("driver:"), gbc);
		detail.add(lbl);
		gbl.setConstraints(txtDriver, gbc);
		detail.add(txtDriver);

		gbl.setConstraints(lbl = new JLabel("example:"), gbc);
		detail.add(lbl);
			
		gbc.insets = new Insets(0,0,30,0);
		gbl.setConstraints(txtExample, gbc);
		detail.add(txtExample);
			
		Box bar = new Box(BoxLayout.X_AXIS);
		bar.add(btnApply = UIUtilities.createCustomButton("apply",this));
		bar.add(Box.createHorizontalGlue());
			
		setCenterComponent(new DefaultScrollPane("edit driver",detail,false));
		setSouthComponent(bar);
	}
	
	void addFile(String filename)
	{
		cbxFile.addItem(filename);
	}
		
	void removeFile(String filename)
	{
		cbxFile.removeItem(filename);
	}
		
	void setInfo(InfoDriverPane.InfoElement info)
	{
		this.info = info;
		
		cbxFile.setSelectedItem(info.file);
		txtName.setText(info.name);
		txtDriver.setText(info.driver);
		txtExample.setText(info.example);
	}
		
	public void actionPerformed(ActionEvent e)
	{
		String name = txtName.getText();
		
		if(name.indexOf(';')!=-1) return;
		if(!name.equals(info.name) && UserSession.jdbc.containsKey(name + ".driver")) return;
		
		if(e.getSource() == btnApply)
		{
			info.name	= name;
			info.file	= cbxFile.getSelectedItem().toString();
			info.driver = txtDriver.getText();
			info.example = txtExample.getText();
			
			admin.save();
		}
	}
	
	static class InfoElement
	{
		private static int counter = 0;
		
		String name		= new String();
		String file		= new String();
		String driver	= new String();
		String example	= new String();
		
		InfoElement()
		{
			this("new_driver_" + (++counter));
		}
		
		InfoElement(String name)
		{
			this.name = name;
			this.file = "$CLASSPATH";
		}
		
		public String toString()
		{
			return name;
		}
	}
}

