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

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import javax.swing.border.EmptyBorder;

import nickyb.fqb.util.DefaultPanel;
import nickyb.fqb.util.DefaultScrollPane;
import nickyb.fqb.util.UIUtilities;

public class InfoProfilePane extends DefaultPanel implements ActionListener
{
	private InfoProfilePane.InfoElement info;
	private DialogAdministrator admin;
		
	private JTextField txtName;
	private JTextField txtUrl;
	private JTextField txtUid;
	private JPasswordField txtPwd;
		
	private JButton btnApply;
	private JButton btnConnect;
		
	public InfoProfilePane(DialogAdministrator admin)
	{
		super(2,2);
		this.admin = admin;
			
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
		gbl.setConstraints(txtName = new JTextField(), gbc);
		detail.add(txtName);

		gbc.insets = new Insets(0,0,0,0);
		gbl.setConstraints(lbl = new JLabel("url:"), gbc);
		detail.add(lbl);
		gbl.setConstraints(txtUrl = new JTextField(), gbc);
		detail.add(txtUrl);

		gbl.setConstraints(lbl = new JLabel("user:"), gbc);
		detail.add(lbl);
		gbl.setConstraints(txtUid = new JTextField(), gbc);
		detail.add(txtUid);

		gbl.setConstraints(lbl = new JLabel("password:"), gbc);
		detail.add(lbl);
			
		gbc.insets = new Insets(0,0,30,0);
		gbl.setConstraints(txtPwd = new JPasswordField(), gbc);
		detail.add(txtPwd);

		Box bar = new Box(BoxLayout.X_AXIS);
		bar.add(btnApply = UIUtilities.createCustomButton("apply",this));
		bar.add(btnConnect = UIUtilities.createCustomButton("connect",this));
		bar.add(Box.createHorizontalGlue());
			
		setCenterComponent(new DefaultScrollPane("edit profile",detail,false));
		setSouthComponent(bar);
	}
		
	void setInfo(InfoProfilePane.InfoElement info)
	{
		this.info = info;
			
		txtName.setText(info.name);
		txtUrl.setText(info.url);
		txtUid.setText(info.uid);
		txtPwd.setText(null);
	}
		
	public void actionPerformed(ActionEvent e)
	{
		String name = txtName.getText();
		if(name.indexOf(';')!=-1) return;
		if(!name.equals(info.name) && UserSession.jdbc.containsKey(name + ".drv")) return;
		
		info.name	= name;
		info.url	= txtUrl.getText();
		info.uid	= txtUid.getText();

		admin.save();
		if(e.getSource() == btnConnect)
		{
			this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			String pwd = new String(txtPwd.getPassword());
			if(pwd.equals("")) pwd = null;
				
			if(admin.syswin.openConnection(info,pwd)) admin.dispose();
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	static class InfoElement
	{
		private static int counter = 0;
		
		String name	= new String();
		String url	= new String();
		String uid	= new String();
		
		InfoDriverPane.InfoElement dinfo;
		
		InfoElement(InfoDriverPane.InfoElement dinfo)
		{
			this(dinfo, "new_profile_" + (++counter));
		}
		
		InfoElement(InfoDriverPane.InfoElement dinfo, String name)
		{
			this.dinfo = dinfo;
			this.name = name;

			url = dinfo.example;
		}
		
		public String toString()
		{
			return name;
		}
	}
}
