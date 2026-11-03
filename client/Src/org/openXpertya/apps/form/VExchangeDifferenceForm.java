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
import org.openXpertya.util.DisplayType;

public class VExchangeDifferenceForm extends CDialog implements ActionListener {
    VOrdenPago ordenCobro; // OP#8516: recibo (VOrdenCobro) u orden de pago
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
	public VExchangeDifferenceForm(VOrdenPago ordenCobro, BigDecimal exchangeDif, BigDecimal exchangeDifPercent, BigDecimal limit, BigDecimal limitPercent, boolean shouldEmit) {
		this.ordenCobro = ordenCobro;
		boolean isPurchase = !ordenCobro.getModel().isSOTrx(); // OP#8516
		this.setTitle(isPurchase ? "Emitir/Incluir Cr\u00e9dito/D\u00e9bito de Proveedor" : "Emitir/Incluir Nota de Cred/Deb");
		setSize(580, 320); // OP#8516: mas ancha para ver bien los importes
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
		if (isPurchase) { // OP#8516
			lblNewLabel.setText("<html>\r\n<div style='text-align: center;'>\r\n\tEmitir y/o incluir en el pago los Cr\u00e9ditos o D\u00e9bitos de Proveedor por diferencia de cambio que correspondan." +
			"\r\n<b>Para cancelar el saldo de una factura en moneda extranjera, active ambas opciones: Emitir/Incluir</div>\r\n</b></html>");
		}
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(10, 14, 546, 65);
		contentPanel.add(lblNewLabel);
		{
			CLabel lblNewLabel_1 = new CLabel("");
			//lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblNewLabel_1.setBounds(162, 230, 100, 22);
			contentPanel.add(lblNewLabel_1);
		}
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Diferencia de Cambio", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(10, 76, 546, 154);
		contentPanel.add(panel);
		panel.setLayout(null);
		{
			CLabel lblNewLabel_1 = new CLabel("Dif de Cambio %:");
			lblNewLabel_1.setText("Diferencia %:");
			lblNewLabel_1.setBounds(24, 24, 100, 24);
			panel.add(lblNewLabel_1);
		}
		{
			lblExchangeDifPercnt = new CLabel(formatAmount(exchangeDifPercent));
			lblExchangeDifPercnt.setBounds(130, 24, 120, 22);
			panel.add(lblExchangeDifPercnt);
			lblExchangeDifPercnt.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		{
			lblLimitPercent = new CLabel(formatAmount(limitPercent));
			lblLimitPercent.setBounds(130, 53, 120, 22);
			panel.add(lblLimitPercent);
			lblLimitPercent.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		{
			CLabel lblNewLabel_1 = new CLabel("Dif de Cambio $:");
			lblNewLabel_1.setText("Diferencia $:");
			lblNewLabel_1.setBounds(280, 24, 90, 22);
			panel.add(lblNewLabel_1);
		}
		{
			CLabel lblNewLabel_1 = new CLabel("Límite $:");
			lblNewLabel_1.setBounds(280, 53, 90, 22);
			panel.add(lblNewLabel_1);
		}
		{
			lblLimit = new CLabel(formatAmount(limit));
			lblLimit.setBounds(375, 53, 150, 22);
			panel.add(lblLimit);
			lblLimit.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		{
			lblExchangeDif = new CLabel(formatAmount(exchangeDif));
			lblExchangeDif.setBounds(375, 24, 150, 22);
			panel.add(lblExchangeDif);
			lblExchangeDif.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		
		chckbxInclude = new CCheckBox("Incluir");
		chckbxInclude.setText("<html><b>Incluir</b></html>");
		chckbxInclude.setBounds(310, 101, 78, 22);
		panel.add(chckbxInclude);
		
		chckbxEmit = new CCheckBox("Emitir");
		chckbxEmit.setText("<html><b>Emitir<b></html>");
		chckbxEmit.setBounds(170, 101, 78, 22);
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
			lblNewLabel_1.setBounds(24, 53, 100, 22);
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
	
	/**
	 * OP#8516 Formatea un importe con separador de miles y 2 decimales para facilitar la lectura.
	 */
	private static String formatAmount(BigDecimal amount) {
		if (amount == null) {
			return "";
		}
		return DisplayType.getNumberFormat(DisplayType.Amount).format(amount);
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
