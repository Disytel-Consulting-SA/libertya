package org.openXpertya.apps.form;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.math.BigDecimal;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.compiere.swing.CButton;
import org.compiere.swing.CCheckBox;
import org.compiere.swing.CDialog;
import org.compiere.swing.CLabel;

public class VExchangeDifferenceForm extends CDialog implements ActionListener {
    VOrdenCobro ordenCobro;
	private CLabel lblLimit;
    private CLabel lblExchangeDif;
    private CLabel lblLimitPercent;
    private CLabel lblExchangeDifPercnt;
    private CCheckBox chckbxEmit;
    private CCheckBox chckbxInclude;
    private CButton okButton;
    private CButton cancelButton;
    
	private final javax.swing.JPanel contentPanel = new JPanel();

	/**
	 * Create the dialog.
	 */
	public VExchangeDifferenceForm(VOrdenCobro ordenCobro, BigDecimal exchangeDif, BigDecimal exchangeDifPercent, BigDecimal limit, BigDecimal limitPercent, boolean shouldEmit) {
		this.ordenCobro = ordenCobro;
		this.setTitle("Emitir/Incluir Nota de Cred/Deb");
		setSize(420, 320);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		setResizable(false);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		CLabel lblNewLabel = new CLabel("<html>\r\n<div style='text-align: center;'>\r\n\tActiva o no la emisión y/o la inclusión en el cobro de las Notas de Crédito o Notas de Débito<br>que correspondan.\r\n</div>\r\n</html>");
		lblNewLabel.setVerticalAlignment(SwingConstants.TOP);
		lblNewLabel.setText("<html>\r\n<div style='text-align: center;'>\r\n\tEmitir y/o incluír en el cobro de las Notas de Crédito o Notas de Débito que correspondan." +
		"\r\n<b>Si una factura en moneda extranjera se cobrará en la misma moneda, por favor active ambas opciones: Emitir/Incluir</div>\r\n</b></html>");
		//lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(10, 14, 386, 65);
		contentPanel.add(lblNewLabel);
		{
			CLabel lblNewLabel_1 = new CLabel("");
			//lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblNewLabel_1.setBounds(162, 230, 100, 22);
			contentPanel.add(lblNewLabel_1);
		}
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Diferencia de Cambio", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(10, 76, 386, 154);
		contentPanel.add(panel);
		panel.setLayout(null);
		{
			CLabel lblNewLabel_1 = new CLabel("Dif de Cambio %:");
			lblNewLabel_1.setText("Diferencia %:");
			lblNewLabel_1.setBounds(24, 24, 87, 24);
			panel.add(lblNewLabel_1);
		}
		{
			lblExchangeDifPercnt = new CLabel(exchangeDifPercent.toString());
			lblExchangeDifPercnt.setBounds(121, 24, 45, 22);
			panel.add(lblExchangeDifPercnt);
			lblExchangeDifPercnt.setHorizontalAlignment(SwingConstants.LEFT);
		}
		{
			lblLimitPercent = new CLabel(limitPercent.toString());
			lblLimitPercent.setBounds(121, 53, 45, 22);
			panel.add(lblLimitPercent);
			lblLimitPercent.setHorizontalAlignment(SwingConstants.LEFT);
		}
		{
			CLabel lblNewLabel_1 = new CLabel("Dif de Cambio $:");
			lblNewLabel_1.setText("Diferencia $:");
			lblNewLabel_1.setBounds(176, 24, 97, 22);
			panel.add(lblNewLabel_1);
		}
		{
			CLabel lblNewLabel_1 = new CLabel("Límite $:");
			lblNewLabel_1.setBounds(186, 53, 87, 22);
			panel.add(lblNewLabel_1);
		}
		{
			lblLimit = new CLabel(limit.toString());
			lblLimit.setBounds(286, 53, 68, 22);
			panel.add(lblLimit);
			lblLimit.setHorizontalAlignment(SwingConstants.LEFT);
		}
		{
			lblExchangeDif = new CLabel(exchangeDif.toString());
			lblExchangeDif.setBounds(283, 24, 71, 22);
			panel.add(lblExchangeDif);
			lblExchangeDif.setHorizontalAlignment(SwingConstants.LEFT);
		}
		
		chckbxInclude = new CCheckBox("Incluir");
		chckbxInclude.setText("<html><b>Incluir</b></html>");
		chckbxInclude.setBounds(219, 101, 78, 22);
		panel.add(chckbxInclude);
		
		chckbxEmit = new CCheckBox("Emitir");
		chckbxEmit.setText("<html><b>Emitir<b></html>");
		chckbxEmit.setBounds(101, 101, 78, 22);
		chckbxEmit.setSelected(shouldEmit);
		chckbxEmit.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (chckbxEmit.isSelected()) {
                	chckbxInclude.setEnabled(true);
                } else {
                	chckbxInclude.setEnabled(false);
                	chckbxInclude.setSelected(false);
                }
            }
        });
		panel.add(chckbxEmit);
		{
			CLabel lblNewLabel_1 = new CLabel("Límite %:");
			lblNewLabel_1.setBounds(10, 53, 100, 22);
			panel.add(lblNewLabel_1);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new CButton("");
				okButton.setIcon(new ImageIcon(VExchangeDifferenceForm.class.getResource("/org/openXpertya/images/Ok24.png")));
				okButton.setActionCommand("OK");
				okButton.addActionListener(this);
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				cancelButton = new CButton("");
				cancelButton.setIcon(new ImageIcon(VExchangeDifferenceForm.class.getResource("/org/openXpertya/images/Cancel24.gif")));
				cancelButton.setActionCommand("KO");
				cancelButton.addActionListener(this);
				buttonPane.add(cancelButton);
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if("OK".equals(e.getActionCommand())) {
			// Se settean los valores de Emitir / Incluir en la Vista y el Modelo.
			this.ordenCobro.setCintoloEmit(chckbxEmit.isSelected());
			this.ordenCobro.setCintoloInclude(chckbxInclude.isSelected());
			this.ordenCobro.setContinue(true);
			
			// Se cierra la venana
			this.setVisible(false);
			
		} else if("KO".equals(e.getActionCommand())) {
			this.ordenCobro.setContinue(false);
			// Se cierra la venana
			this.setVisible(false);
		} 
	}
}
