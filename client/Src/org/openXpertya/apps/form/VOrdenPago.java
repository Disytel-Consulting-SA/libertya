/*
 * VOrdenPago.java
 *
 * Created on 14 de septiembre de 2007, 09:07
 */

package org.openXpertya.apps.form;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.compiere.swing.CComboBox;
import org.compiere.swing.CPanel;
import org.openXpertya.OpenXpertya;
import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.AuthContainer;
import org.openXpertya.apps.form.VOrdenPagoModel.MedioPago;
import org.openXpertya.apps.form.VOrdenPagoModel.MedioPagoCheque;
import org.openXpertya.apps.form.VOrdenPagoModel.MedioPagoCredito;
import org.openXpertya.apps.form.VOrdenPagoModel.MedioPagoEfectivo;
import org.openXpertya.apps.form.VOrdenPagoModel.MedioPagoTransferencia;
import org.openXpertya.apps.form.VOrdenPagoModel.ResultItemFactura;
import org.openXpertya.grid.ed.VCheckBox;
import org.openXpertya.grid.ed.VComboBox;
import org.openXpertya.grid.ed.VDate;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.grid.ed.VNumber;
import org.openXpertya.images.ImageFactory;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MCurrency;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MPreference;
import org.openXpertya.model.MUser;
import org.openXpertya.model.Query;
import org.openXpertya.model.RetencionProcessor;
import org.openXpertya.model.X_AD_Role;
import org.openXpertya.model.X_C_BankAccountDoc;
import org.openXpertya.pos.model.AuthOperation;
import org.openXpertya.pos.model.User;
import org.openXpertya.pos.view.AuthorizationDialog;
import org.openXpertya.pos.view.KeyUtils;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.ASyncProcess;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.TimeUtil;
import org.openXpertya.util.UserAuthConstants;
import org.openXpertya.util.Util;
import org.openXpertya.util.ValueNamePair;

/**
 *
 * @author  usuario
 */
public class VOrdenPago extends CPanel implements FormPanel,ActionListener,TableModelListener,VetoableChangeListener,ChangeListener,TreeModelListener,MouseListener,CellEditorListener,ASyncProcess,AuthContainer {
    
	protected BigDecimal maxPaymentAllowed = null;
	
	public class DecimalEditor extends AbstractCellEditor implements TableCellEditor {

		private VNumber vn;
		
	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	    	if(vn == null) {
	    		vn = new VNumber();
	    	}
	    	vn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					stopCellEditing();
				}
			});
			return vn;
        }
	    
	    // Override to invoke setValue on the formatted text field.
	    public Component getTableCellEditorComponent(JTable table,
	            Object value, boolean isSelected,
	            int row, int column) {
	    	if(vn == null) {
	    		vn = new VNumber();
	    	}
	        vn.setValue(value);
	        return vn;
	    	/*JFormattedTextField ftf =
	            (JFormattedTextField)super.getTableCellEditorComponent(
	                table, value, isSelected, row, column);
	        ftf.setValue(value);
	        return ftf;*/
	    }

	    public Object getCellEditorValue() {
	    	if(vn == null) {
	    		vn = new VNumber();
	    	}
	    	return vn.getValue();
	        /*Object o = ftf.getValue();
	        if (o instanceof BigDecimal) {
	            return o;
	        } else if (o instanceof Number) {
	            return new BigDecimal(o.toString());
	        } else {
	            try {
	                return decFormat.parseObject(o.toString());
	            } catch (ParseException exc) {
	                return null;
	            }
	        }*/
	    }
	}
	
	class MyRenderer extends DefaultTreeCellRenderer {

		protected HashMap customLeafs = new HashMap();
		
	    public MyRenderer() {
	    	customLeafs.put("E", new ImageIcon(OpenXpertya.getImage16()));
	    	customLeafs.put("T", new ImageIcon(OpenXpertya.getImage16()));
	    	customLeafs.put("C", new ImageIcon(OpenXpertya.getImage16()));
	    }

	    private Icon retencionIcon = ImageFactory.getImageIcon("Register16.gif");
	    private Icon paymentIcon = ImageFactory.getImageIcon("Caunt16.gif");
	    private Icon folderOpen =  ImageFactory.getImageIcon("FolderOpen.gif");
	    private Icon folderClosed =  ImageFactory.getImageIcon("FolderClose.gif");
	    
	    public Component getTreeCellRendererComponent(
	                        JTree tree,
	                        Object value,
	                        boolean sel,
	                        boolean expanded,
	                        boolean leaf,
	                        int row,
	                        boolean hasFocus) {
	    	
	    	MyTreeNode td = (MyTreeNode)value;
	    	
	    	Component ret = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
	    	
	    	if (td.isLeaf()) {
	    		try {
	    			if (td.isMedioPago()) {
	    				VOrdenPagoModel.MedioPago mp = (VOrdenPagoModel.MedioPago)td.getUserObject();
	    				
	    				if (customLeafs.containsKey(mp.getTipoMP()))
	    					setIcon((ImageIcon)customLeafs.get(mp.getTipoMP()));
	    			}
	    		} catch (Exception e) {
	    			
	    		}
	    		
	    		if (td.isRetencion())
	    			setIcon( retencionIcon );
	    		else
	    			setIcon( paymentIcon ); // leafIcon
	    	} else {
	    		if (expanded)
	    			setIcon( folderOpen );
	    		else
	    			setIcon( folderClosed );
	    	}
	    	return ret;
	    }
	}
	
	protected class MyNumberTableCellRenderer extends DefaultTableCellRenderer {

		private NumberFormat nf = null;
		
		public MyNumberTableCellRenderer(NumberFormat nf) {
			super();
			this.nf = nf;
		}
		
		@Override
		protected void setValue(Object arg0) {
			if (arg0 == null)
				arg0 = new BigDecimal(0.0);
			setHorizontalAlignment(JLabel.RIGHT);
			setText(nf.format(arg0));
		}
		
	}

	public void initFormattedTextField(JFormattedTextField ftf) {
		NumberFormatter numFormatter = new NumberFormatter(m_model.getNumberFormat());
		numFormatter.setMinimum(BigDecimal.ZERO);
		numFormatter.setMaximum(null);

        ftf.setFormatterFactory(new DefaultFormatterFactory(numFormatter));
        ftf.setValue(BigDecimal.ZERO);
        ftf.setHorizontalAlignment(JTextField.TRAILING);
        ftf.setFocusLostBehavior(JFormattedTextField.COMMIT);
        
        ftf.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "check");
		ftf.getActionMap().put("check", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				JFormattedTextField ftf = (JFormattedTextField)e.getSource();
				
				if (!ftf.isEditValid()) { // The text is invalid.
					ftf.setValue(BigDecimal.ZERO);
					ftf.postActionEvent();
				} else {
					try { // The text is valid,
						ftf.commitEdit(); // so use it.
						ftf.postActionEvent(); // stop editing
					} catch (java.text.ParseException exc) {
					}
				}
			}
		});
		
		/*ftf.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				ftf.getText().contains()
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// Modificar el agrupador por el decimal
				/*if(DecimalFormatSymbols.getInstance().getGroupingSeparator() == e.getKeyChar()) {
					e.setKeyChar(DecimalFormatSymbols.getInstance().getDecimalSeparator());
					e.setKeyCode((DecimalFormatSymbols.getInstance().getGroupingSeparator() == '.') ? KeyEvent.VK_COMMA
							: KeyEvent.VK_PERIOD);
				}
			}
		});*/
	}
	
    /** Creates new form VOrdenPago */
    public VOrdenPago() {
    	setModel(createModel());
    	m_trxName = getModel().getTrxName();
    }
    
    protected VOrdenPagoModel createModel() {
    	return new VOrdenPagoModel();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" C�digo Generado  ">//GEN-BEGIN:initComponents
    private void initComponents() {
        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        lblBPartner = new javax.swing.JLabel();
        lblClient = new javax.swing.JLabel();
        lblDocumentNo = new javax.swing.JLabel();
        lblDescription = new javax.swing.JLabel();
        cboClient = VComponentsFactory.VLookupFactory("AD_Client_ID", "C_Invoice", m_WindowNo, DisplayType.Table );
        BPartnerSel = VComponentsFactory.VLookupFactory("C_BPartner_ID", "C_BPartner", m_WindowNo, DisplayType.Search );
        fldDocumentNo = new org.compiere.swing.CTextField(); 
        jPanel10 = new javax.swing.JPanel();
        lblOrg = new javax.swing.JLabel();
        cboOrg = VComponentsFactory.VLookupFactory("AD_Org_ID", "C_BPartner", m_WindowNo, DisplayType.Table);
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        radPayTypeStd = new javax.swing.JRadioButton();
        radPayTypeAdv = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextField();
		txtDescription.setEditable(true);	
		lblDocumentType = new javax.swing.JLabel();
		cboDocumentType = VComponentsFactory.VLookupFactory("C_DOCTYPE_ID", "C_DOCTYPE", m_WindowNo, DisplayType.Table,m_model.getDocumentTypeSqlValidation(),true); //jv debe ser obligatorio
		lblDateTrx = new javax.swing.JLabel();
		createDate();
		
		lblPaymentRule = new javax.swing.JLabel();
		cboPaymentRule = createPaymentRuleCombo();
		cboPaymentRule.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				updatePaymentRule();
			}
		});
		
        tblFacturas = new javax.swing.JTable(getFacturasTableModel());
        txtTotalPagar1 = new VNumber();
        lblTotalPagar1 = new javax.swing.JLabel();
        rInvoiceAll = new javax.swing.JRadioButton();
        rInvoiceDate = new javax.swing.JRadioButton();
        invoiceDatePick = VComponentsFactory.VDateFactory();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree(getMediosPagoTreeModel());
        cmdEliminar = new javax.swing.JButton();
        cmdEditar = new javax.swing.JButton();
        txtSaldo = new VNumber();
        txtDifCambio = new javax.swing.JTextField();
        lblSaldo = new javax.swing.JLabel();
        txtMedioPago2 = new javax.swing.JTextField();
        txtRetenciones2 = new javax.swing.JTextField();
        txtTotalPagar2 = new javax.swing.JTextField();
        lblTotalPagar2 = new javax.swing.JLabel();
        lblRetenciones2 = new javax.swing.JLabel();
        lblDifCambio = new javax.swing.JLabel();
        lblMedioPago2 = new javax.swing.JLabel();
        cmdGrabar = new javax.swing.JButton();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        lblEfectivoLibroCaja = new javax.swing.JLabel();
        efectivoLibroCaja = VComponentsFactory.VLookupFactory("C_Cash_ID", "C_Cash", m_WindowNo, DisplayType.Search, m_model.getEfectivoLibroCajaSqlValidation() ); 
        lblEfectivoImporte = new javax.swing.JLabel();
        txtEfectivoImporte = new VNumber();
        jPanel6 = new javax.swing.JPanel();
        lblTransfCtaBancaria = new javax.swing.JLabel();
        lblTransfNroTransf = new javax.swing.JLabel();
        lblTransfImporte = new javax.swing.JLabel();
        lblTransfFecha = new javax.swing.JLabel();
        transfCtaBancaria = VComponentsFactory.VLookupFactory("C_BankAccount_ID", "C_BankAccount", m_WindowNo, DisplayType.Search, m_model.getTransfCtaBancariaSqlValidation());
        txtTransfNroTransf = new javax.swing.JTextField();
        txtTransfImporte = new VNumber();
        transFecha = VComponentsFactory.VDateFactory();
        jPanel7 = new javax.swing.JPanel();
        lblChequeChequera = new javax.swing.JLabel();
        lblChequeNroCheque = new javax.swing.JLabel();
        lblChequeImporte = new javax.swing.JLabel();
        lblChequeFechaEmision = new javax.swing.JLabel();
        lblChequeFechaPago = new javax.swing.JLabel();
        lblChequeALaOrden = new javax.swing.JLabel();
        lblChequeBanco = new javax.swing.JLabel();
        lblChequeCUITLibrador = new javax.swing.JLabel(); 
        lblChequeDescripcion = new javax.swing.JLabel();
        //chequeChequera = VComponentsFactory.VLookupFactory("C_BankAccountDoc_ID", "C_BankAccountDoc", m_WindowNo, DisplayType.TableDir , m_model.getChequeChequeraSqlValidation() );
        chequeChequera = createChequeChequeraLookup(); 
        txtChequeImporte = new VNumber();
        chequeFechaEmision = VComponentsFactory.VDateFactory();
        chequeFechaPago = VComponentsFactory.VDateFactory();
        txtChequeALaOrden = new javax.swing.JTextField();
        txtChequeNroCheque = new javax.swing.JTextField();
        txtChequeBanco = new javax.swing.JTextField();
        cboChequeBancoID = createChequeBancoIDLookup();
        txtChequeCUITLibrador = new javax.swing.JTextField();
        txtChequeDescripcion = new javax.swing.JTextField();
        panelCamProy = new javax.swing.JPanel();
        cboCampaign = VComponentsFactory.VLookupFactory("C_Campaign_ID", "C_Invoice", m_WindowNo, DisplayType.Table, null, false );
        cboProject = VComponentsFactory.VLookupFactory("C_Project_ID", "C_Invoice", m_WindowNo, DisplayType.Table, null, false );
        cboCurrency = VComponentsFactory.VLookupFactory("C_Currency_ID", "C_Currency", m_WindowNo, DisplayType.Table, m_model.getCurrencySqlValidation() );
        lblCampaign = new javax.swing.JLabel();
        lblProject = new javax.swing.JLabel();
        lblCurrency = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        cmdProcess = new javax.swing.JButton();
        cmdCancel = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        
        lblCreditInvoice = new javax.swing.JLabel();
		creditInvoice = VComponentsFactory.VLookupFactory("C_Invoice_ID",
				"C_Invoice", m_WindowNo, DisplayType.Search,
				m_model.getCreditSqlValidation(), true, 
				m_model.addSecurityValidationToNC(), true);
        lblCreditAvailable = new javax.swing.JLabel();
        txtCreditAvailable = new VNumber();
        txtCreditAvailable.setReadWrite(false);
        lblCreditImporte = new javax.swing.JLabel();
        txtCreditImporte = new VNumber();
        
        checkPayAll = new VCheckBox();
        checkPayAll.setText("Pagar Todo");
        checkPayAll.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				updatePayAllInvoices(false);
				
				if(checkPayAll.isSelected()) {
	
					/**
					 * Si no esta nulo la entidad comercial, validar si hay tasas de conversion para la fecha 
					 * del recibo/pago y para cada factura
					 * 
					 * dREHER
					 */
					
					if(!ValidateConvertionRate()) {
						checkPayAll.setSelected(false);
						return;
					}
	
				}
			}
		});
        
        checkPayAll.addAction("setSelected",
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
				new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						checkPayAll.setSelected(!((VCheckBox) arg0.getSource()).isSelected());
						fireActionPerformed(checkPayAll.getActionListeners(), null);
					}
				});
        
        m_frame.setMinimumSize(new java.awt.Dimension(800, 800)); // dREHER ajustar la ventana por defecto
        m_frame.setSize(m_frame.getMinimumSize());
        // m_frame.setOpaque(false);
        jPanel1.setOpaque(false);
        jPanel9.setOpaque(false);
        lblBPartner.setText("Entidad Comercial");

        chequeFechaEmision.setMandatory(true);
        chequeFechaPago.setMandatory(true);
        
        createLeftUpStaticPanel();
        
        createRightUpStaticPanel();
        
        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jPanel3.setOpaque(false);
        buttonGroup1.add(radPayTypeStd);
        radPayTypeStd.setSelected(true);
        radPayTypeStd.setText("PAGO NORMAL");
        radPayTypeStd.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        radPayTypeStd.setMargin(new java.awt.Insets(0, 0, 0, 0));
        radPayTypeStd.setOpaque(false);
        radPayTypeStd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onTipoPagoChange(false);
            }
        });
        radPayTypeStd.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				onTipoPagoChange(false);
				
			}
		});
        
        buttonGroup1.add(radPayTypeAdv);
        radPayTypeAdv.setText("PAGO ADELANTADO");
        radPayTypeAdv.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        radPayTypeAdv.setMargin(new java.awt.Insets(0, 0, 0, 0));
        radPayTypeAdv.setOpaque(false);
        radPayTypeAdv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onTipoPagoChange(false);
            }
        });
        
        radPayTypeAdv.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				onTipoPagoChange(false);
				
			}
		});

        // tblFacturas.setModel(new javax.swing.table.DefaultTableModel());
        jScrollPane1.setViewportView(tblFacturas);

        lblTotalPagar1.setText("TOTAL A PAGAR");

        buttonGroup2.add(rInvoiceAll);
        rInvoiceAll.setSelected(true);
        rInvoiceAll.setText("TODAS");
        rInvoiceAll.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rInvoiceAll.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rInvoiceAll.setOpaque(false);
        rInvoiceAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onFechaChange(false);
            }
        });

        buttonGroup2.add(rInvoiceDate);
        rInvoiceDate.setText("VENCIDAS A FECHA:");
        rInvoiceDate.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rInvoiceDate.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rInvoiceDate.setOpaque(false);
        rInvoiceDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onFechaChange(false);
            }
        });

        jTabbedPane1.addTab("TITULO TAB1", createInvoicesPanel());

        jPanel4.setOpaque(false);
        jScrollPane2.setViewportView(jTree1);

        cmdEliminar.setText("ELIMINAR");
        cmdEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdEliminarActionPerformed(evt);
            }
        });

        cmdEditar.setText("EDITAR");
        cmdEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdEditarActionPerformed(evt);
            }
        });

        txtSaldo.setReadWrite(false);
        
        txtDifCambio.setEditable(false);

        lblSaldo.setText("SALDO");

        txtMedioPago2.setEditable(false);

        txtRetenciones2.setEditable(false);

        txtTotalPagar2.setEditable(false);

        lblTotalPagar2.setText("TOTAL A PAGAR");

        lblRetenciones2.setText("RETENCIONES");
        
        lblDifCambio.setText("Diferecia de Cambio");

        lblMedioPago2.setText("MEDIO DE PAGO");

        cmdGrabar.setText("GRABAR");
        cmdGrabar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSavePMActionPerformed(evt);
            }
        });

        // Pestañas de pagos
        jTabbedPane2.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane2.addTab("EFECTIVO", createCashTab());
        jTabbedPane2.addTab("TRANSFERENCIA", createTransferTab());
        jTabbedPane2.addTab("CHEQUE", createCheckTab());
        jTabbedPane2.addTab("CREDITO", createCreditTab());
        
        // Se crea el panel que contiene Campaña y Proyecto
        createCamProyPanel();
        
        // Crear el panel de pagos
        createPaymentTab();
        jTabbedPane1.addTab("TITULO TAB2", jPanel4);

        jPanel2.setOpaque(false);
        cmdProcess.setText("SIGUIENTE/PROCESAR");
        cmdProcess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdProcessActionPerformed(evt);
            }
        });

        cmdCancel.setText("CANCELAR");
        cmdCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdCancelActionPerformed(evt);
            }
        });
        
        // Creo el panel de botones
        createButtonsPanel();

        // Agrego los paneles al frame principal
        addPanelsToFrame();
        
        // Pagos anticipados como medios de pago
		jTabbedPane2.addTab("PAGO ADELANTADO", createPagoAdelantadoTab());
		m_PagoAdelantadoTabIndex = jTabbedPane1.indexOfComponent(panelPagoAdelantado);
        
        //Agrego un listener para verificar si es null o no, y deshabilitar el boton Siguiente en caso de que sea null
        
        this.BPartnerSel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	cmdBPartnerSelActionPerformed(evt);
            }
        });
        
        setAuthDialog(new AuthorizationDialog(this));
        
        keyBindingsInit();
    }// </editor-fold>//GEN-END:initComponents

    
    protected void keyBindingsInit(){
    	// Deshabilito el F10 que algunos look and feel y 
		// técnicas de focos asignan al primer componente menú de la barra de menú 
		m_frame.getJMenuBar().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
              KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F10,0), "none");
		// Se asignan las teclas shorcut de las acciones.
		setActionKeys(new HashMap<String,KeyStroke>());
		getActionKeys().put(GOTO_BPARTNER, KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
		getActionKeys().put(MOVE_INVOICE_FORWARD, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
		getActionKeys().put(MOVE_INVOICE_BACKWARD, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));
		getActionKeys().put(GOTO_PROCESS, KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0));
		getActionKeys().put(GOTO_EXIT, KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
		getActionKeys().put(ADD_PAYMENT, KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0));
		getActionKeys().put(EDIT_PAYMENT, KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0));
		getActionKeys().put(REMOVE_PAYMENT, KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0));
		getActionKeys().put(GO_BACK, KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
		getActionKeys().put(MOVE_PAYMENT_FORWARD, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
		getActionKeys().put(MOVE_PAYMENT_BACKWARD, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));
		getActionKeys().put(GOTO_PAYALL, KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		getActionKeys().put(COMPLETE_INVOICE_AMT, KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));
		
		// Accion: Foco en Entidad Comercial
		m_frame.getRootPane().getActionMap().put(GOTO_BPARTNER,
        	new AbstractAction() {
        		public void actionPerformed(ActionEvent e) {
					BPartnerSel.requestFocus();
				}
        	}
        );
        
        // Accion: Moverse hacia adelante sobre las facturas
        m_frame.getRootPane().getActionMap().put(MOVE_INVOICE_FORWARD,
        	new AbstractAction() {
        		public void actionPerformed(ActionEvent e) {
        			moveTableSelection(tblFacturas, true);
				}
        	}
        );
        
        // Accion: Moverse hacia atras sobre las facturas
        m_frame.getRootPane().getActionMap().put(MOVE_INVOICE_BACKWARD,
        	new AbstractAction() {
        		public void actionPerformed(ActionEvent e) {
        			moveTableSelection(tblFacturas, false);
				}
        	}
        );

        // Accion: Seleccionar el botón Siguiente o Procesar
        m_frame.getRootPane().getActionMap().put(GOTO_PROCESS,
        	new AbstractAction() {
        		public void actionPerformed(ActionEvent e) {
					if(cmdProcess.isEnabled()){
						cmdProcessActionPerformed(null);
					}
				}
        	}
        );

        // Accion: Seleccionar el botón cancelar
        m_frame.getRootPane().getActionMap().put(GOTO_EXIT,
        	new AbstractAction() {
        		public void actionPerformed(ActionEvent e) {
        			cmdCancelActionPerformed(null);
				}
        	}
        );
        
        // Accion: Agregar un pago
        m_frame.getRootPane().getActionMap().put(ADD_PAYMENT,
        	new AbstractAction() {
        		public void actionPerformed(ActionEvent e) {
        			cmdSavePMActionPerformed(null);
				}
        	}
        );
        
     // Accion: Editar un pago
        m_frame.getRootPane().getActionMap().put(EDIT_PAYMENT,
        	new AbstractAction() {
        		public void actionPerformed(ActionEvent e) {
					cmdEditarActionPerformed(null);
				}
        	}
        );
        
        // Accion: Eliminar un pago
        m_frame.getRootPane().getActionMap().put(REMOVE_PAYMENT,
        	new AbstractAction() {
        		public void actionPerformed(ActionEvent e) {
					cmdEliminarActionPerformed(null);
				}
        	}
        );
        
        // Accion: Foco en Entidad Comercial
		m_frame.getRootPane().getActionMap().put(GO_BACK,
        	new AbstractAction() {
        		public void actionPerformed(ActionEvent e) {
					jTabbedPane1.setSelectedIndex(0);
					
				}
        	}
        );
		
		// Accion: Moverse hacia adelante sobre las facturas
        m_frame.getRootPane().getActionMap().put(MOVE_PAYMENT_FORWARD,
        	new AbstractAction() {
        		public void actionPerformed(ActionEvent e) {
        			if(!jTree1.isFocusOwner()){
        				jTree1.requestFocus();
        				jTree1.setSelectionRow(0);
        			}
				}
        	}
        );
        
        // Accion: Moverse hacia atras sobre las facturas
        m_frame.getRootPane().getActionMap().put(MOVE_PAYMENT_BACKWARD,
        	new AbstractAction() {
        		public void actionPerformed(ActionEvent e) {
        			moveTableSelection(tblFacturas, false);
				}
        	}
        );
        
        // Accion: Foco en el check para pagar todas las facturas
		m_frame.getRootPane().getActionMap().put(GOTO_PAYALL,
        	new AbstractAction() {
        		public void actionPerformed(ActionEvent e) {
        			checkPayAll.setSelected(!checkPayAll.isSelected());
        			updatePayAllInvoices(false);
				}
        	}
        );
		
        // Accion: Completar el total abierto de la factura seleccionada
		m_frame.getRootPane().getActionMap().put(COMPLETE_INVOICE_AMT,
        	new AbstractAction() {
        		public void actionPerformed(ActionEvent e) {
        			// Si estoy en la primer pestaña y hay una fila seleccionada...
        			if (jTabbedPane1.getSelectedIndex() == 0 && tblFacturas != null && tblFacturas.getSelectedRow() >= 0) {
        				int cellEdCol = tblFacturas.getColumnModel().getColumnCount() - 1;
        				tblFacturas.getColumnModel().getColumn(cellEdCol).getCellEditor().cancelCellEditing();
        				tblFacturas.setValueAt(new BigDecimal(Long.MAX_VALUE), tblFacturas.getSelectedRow(), cellEdCol); 
        		    	tblFacturas.getColumnModel().getColumn(cellEdCol).getCellEditor().cancelCellEditing();
        			}
				}
        	}
        );
        
        
        // Iniciales
        setActionEnabled(GOTO_BPARTNER, true);
		setActionEnabled(GOTO_PROCESS, true);
		setActionEnabled(GOTO_EXIT, true);
		setActionEnabled(ADD_PAYMENT, false);
		setActionEnabled(EDIT_PAYMENT, false);
		setActionEnabled(REMOVE_PAYMENT, false);
		setActionEnabled(GO_BACK, false);
		setActionEnabled(MOVE_PAYMENT_FORWARD, false);
		setActionEnabled(MOVE_PAYMENT_BACKWARD, false);
		setActionEnabled(MOVE_INVOICE_FORWARD, true);
		setActionEnabled(MOVE_INVOICE_BACKWARD, true);
		setActionEnabled(GOTO_PAYALL, true);
		setActionEnabled(COMPLETE_INVOICE_AMT, true);
		
        // Las subclases también deben definir las suyas
        customKeyBindingsInit();
    }
    
    
    protected void createDate() {
    	dateTrx = VComponentsFactory.VDateFactory();
		dateTrx.setValue(new Date());
		dateTrx.setMandatory(true);
		dateTrx.addVetoableChangeListener(new VetoableChangeListener() {
			
			@Override
			public void vetoableChange(PropertyChangeEvent evt)
					throws PropertyVetoException {
				
				m_model.setFechaOP(dateTrx.getTimestamp());
				m_model.actualizarFacturas();
				Env.setContext(m_ctx, m_WindowNo, "Date", dateTrx.getTimestamp());
				
				/**
				 * Si no esta nulo la entidad comercial, validar si hay tasas de conversion para la fecha 
				 * del recibo/pago y para cada factura
				 * 
				 * dREHER
				 */
				
				if(!ValidateConvertionRate()) {
					return;
				}
			}
		});
    }
    
    protected void setActionEnabled(String action, boolean enabled) {
		String kAction = (enabled?action:"none");
        KeyStroke keyStroke = getActionKeys().get(action);
        
        m_frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            	keyStroke, kAction);
	}
    
    /**
     * Crea el panel de ingreso de facturas a pagar
     */
    protected JComponent createInvoicesPanel(){
        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 647, Short.MAX_VALUE)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(radPayTypeStd)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(radPayTypeAdv)
                        .addContainerGap(50, Short.MAX_VALUE)
                        .add(checkPayAll, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                        .add(rInvoiceAll)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(rInvoiceDate)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(invoiceDatePick, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 179, Short.MAX_VALUE)
                        .add(lblTotalPagar1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtTotalPagar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 95, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(radPayTypeStd)
                    .add(radPayTypeAdv)
                    .add(checkPayAll))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblTotalPagar1)
                    .add(rInvoiceAll)
                    .add(rInvoiceDate)
                    .add(txtTotalPagar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(invoiceDatePick, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        return jPanel3;
    }
    
	/**
	 * Creo el panel estático de arriba a la derecha que contiene la
	 * organización
	 */
    protected void createRightUpStaticPanel(){
    	jPanel10.setOpaque(false);
    	org.jdesktop.layout.GroupLayout jPanel10Layout = new org.jdesktop.layout.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblOrg)
                    .add(lblDocumentNo)
                    .add(lblDocumentType)
                    .add(lblPaymentRule))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(cboOrg, 0, 234, Short.MAX_VALUE)
                    .add(fldDocumentNo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                    .add(cboDocumentType, 0, 234, Short.MAX_VALUE)
                    .add(cboPaymentRule, 0, 234, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblOrg)
                    .add(cboOrg, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblDocumentNo)
                    .add(fldDocumentNo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblDocumentType)
                    .add(cboDocumentType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(lblPaymentRule)
                        .add(cboPaymentRule, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    ))
        );
    }

	/**
	 * Creo el panel estático de arriba a la izquierda que contiene la
	 * compañía
	 */
    protected void createLeftUpStaticPanel()
    {
    	org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblBPartner)
                    .add(lblClient)  
                    .add(lblDateTrx)
                    .add(lblDescription)
                    )
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(cboClient, 0, 234, Short.MAX_VALUE)
                    .add(BPartnerSel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                    .add(dateTrx, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                    .add(txtDescription,org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,234,Short.MAX_VALUE)
                		)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblClient)
                    .add(cboClient, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblBPartner)
                    .add(BPartnerSel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            	.add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
            			.add(lblDateTrx)
            			.add(dateTrx,org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                  .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                  .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                		.add(lblDescription)
                		.add(txtDescription,org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                		)
                .addContainerGap())
        );
    }
    
    
    /**
     * Creo el panel que contiene Campaña y Proyecto
     */
    protected void createCamProyPanel(){
        panelCamProy.setOpaque(false);
    	org.jdesktop.layout.GroupLayout panelCamProyLayout = new org.jdesktop.layout.GroupLayout(panelCamProy);
        panelCamProy.setLayout(panelCamProyLayout);
        panelCamProyLayout.setHorizontalGroup(
            panelCamProyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelCamProyLayout.createSequentialGroup()
                .add(panelCamProyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblCampaign)
                    .add(lblProject)
                    .add(lblCurrency))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panelCamProyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cboCampaign, 0, 239, Short.MAX_VALUE)
                    .add(cboProject, 0, 239, Short.MAX_VALUE)
                    .add(cboCurrency, 0, 239, Short.MAX_VALUE)))
        );
        panelCamProyLayout.setVerticalGroup(
            panelCamProyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelCamProyLayout.createSequentialGroup()
                .addContainerGap()
                .add(panelCamProyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblCampaign)
                    .add(cboCampaign, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panelCamProyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblProject)
                    .add(cboProject, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panelCamProyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                	.add(lblCurrency)
                    .add(cboCurrency, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }
    
    /**
     * Crea la pestaña de efectivo 
     */
    protected JComponent createCashTab(){
    	jPanel5.setOpaque(false);
        lblEfectivoLibroCaja.setText("LIBRO DE CAJA");
        lblEfectivoImporte.setText("IMPORTE");
        txtEfectivoImporte.setValue(BigDecimal.ZERO);

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(lblEfectivoImporte)
                    .add(lblEfectivoLibroCaja))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(efectivoLibroCaja, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                    .add(txtEfectivoImporte, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblEfectivoLibroCaja)
                    .add(efectivoLibroCaja, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblEfectivoImporte)
                    .add(txtEfectivoImporte, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(118, Short.MAX_VALUE))
        );
        return jPanel5;
    }

	/**
	 * Crea la pestaña de transferencia bancaria
	 * 
	 * @return el panel para la pestaña
	 */
    protected JComponent createTransferTab(){
    	jPanel6.setOpaque(false);
        lblTransfCtaBancaria.setText("CUENTA BANCARIA");
        lblTransfNroTransf.setText("NRO TRANSFERENCIA");
        lblTransfImporte.setText("IMPORTE");
        lblTransfFecha.setText("FECHA");
        txtTransfImporte.setValue(BigDecimal.ZERO);

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(lblTransfFecha)
                    .add(lblTransfImporte)
                    .add(lblTransfNroTransf)
                    .add(lblTransfCtaBancaria))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(txtTransfImporte, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                    .add(txtTransfNroTransf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                    .add(transfCtaBancaria, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                    .add(transFecha, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblTransfCtaBancaria)
                    .add(transfCtaBancaria, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblTransfNroTransf)
                    .add(txtTransfNroTransf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblTransfImporte)
                    .add(txtTransfImporte, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblTransfFecha)
                    .add(transFecha, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(66, Short.MAX_VALUE))
        );
        return jPanel6;
    }
    
    /**
     * Crea la pestaña de cheques
     * @return el panel que se debe insertar como pestaña
     */
    protected JComponent createCheckTab(){
    	jPanel7.setOpaque(false);
        lblChequeChequera.setText("CHEQUERA");
        lblChequeNroCheque.setText("NUMERO DE CHEQUE");
        lblChequeImporte.setText("IMPORTE");
        lblChequeFechaEmision.setText("FECHA EMISION");
        lblChequeFechaPago.setText("FECHA PAGO");
        lblChequeALaOrden.setText(getModel().isSOTrx()?"LIBRADOR":"A LA ORDEN");
        lblChequeBanco.setText("BANCO");
        lblChequeCUITLibrador.setText("CUIT LIBRADOR");
        lblChequeDescripcion.setText("DESCRIPCION");
        
        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                	.add(lblChequeCUITLibrador)
                	.add(lblChequeBanco)
                    .add(lblChequeALaOrden)
                    .add(lblChequeChequera)
                    .add(lblChequeNroCheque)
                    .add(lblChequeImporte)
                    .add(lblChequeFechaEmision)
                    .add(lblChequeFechaPago)
                    .add(lblChequeDescripcion))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                	.add(txtChequeCUITLibrador, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                	.add(txtChequeBanco, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                    .add(txtChequeNroCheque, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                    .add(txtChequeALaOrden, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                    .add(txtChequeImporte, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                    .add(chequeChequera, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                    .add(chequeFechaEmision, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                    .add(chequeFechaPago, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                    .add(txtChequeDescripcion, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblChequeChequera)
                    .add(chequeChequera, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblChequeNroCheque)
                    .add(txtChequeNroCheque, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblChequeImporte)
                    .add(txtChequeImporte, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblChequeFechaEmision)
                    .add(chequeFechaEmision, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblChequeFechaPago)
                    .add(chequeFechaPago, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblChequeALaOrden)
                    .add(txtChequeALaOrden, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblChequeBanco)
                    .add(txtChequeBanco, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblChequeCUITLibrador)
                    .add(txtChequeCUITLibrador, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblChequeDescripcion)
                    .add(txtChequeDescripcion, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        return jPanel7;
    }

	/**
	 * Crea la pestaña de Crédito
	 * 
	 * @return panel que se debe insertar como pestaña
	 */
    protected JComponent createCreditTab(){
    	jPanel11.setOpaque(false);
    	lblCreditInvoice.setText("CREDITO");        
        lblCreditAvailable.setText("DISPONIBLE");        
        lblCreditImporte.setText("IMPORTE");        
        txtCreditAvailable.setValue(BigDecimal.ZERO); 
        txtCreditImporte.setValue(BigDecimal.ZERO);
        
        org.jdesktop.layout.GroupLayout jPanel11Layout = new org.jdesktop.layout.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(lblCreditImporte)
                    .add(lblCreditAvailable)
                    .add(lblCreditInvoice))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(txtCreditImporte, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                    .add(txtCreditAvailable, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                    .add(creditInvoice, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblCreditInvoice)
                    .add(creditInvoice, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblCreditAvailable)
                    .add(txtCreditAvailable, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblCreditImporte)
                    .add(txtCreditImporte, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(66, Short.MAX_VALUE))
        );
        return jPanel11;
    }
    
    /**
     * Crea el panel de pagos
     */
    protected void createPaymentTab(){
    	org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jTabbedPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                    .add(panelCamProy, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(cmdGrabar))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                       		.add(jPanel4Layout.createSequentialGroup()
                            .add(lblDifCambio)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(txtDifCambio)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 72, Short.MAX_VALUE)
                            .add(lblTotalPagar2))
                            .add(jPanel4Layout.createSequentialGroup()
                                .add(cmdEliminar)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(cmdEditar)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 72, Short.MAX_VALUE)
                                .add(lblSaldo))
                            .add(lblRetenciones2)
                            .add(lblMedioPago2))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, txtRetenciones2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 75, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, txtTotalPagar2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 75, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, txtMedioPago2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 75, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, txtDifCambio, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 75, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, txtSaldo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 75, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblSaldo)
                            .add(cmdEliminar)
                            .add(cmdEditar)
                            .add(txtSaldo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblTotalPagar2)
                            .add(lblDifCambio)
                            .add(txtDifCambio)
                            .add(txtTotalPagar2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(txtRetenciones2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lblRetenciones2)))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel4Layout.createSequentialGroup()
                        .add(panelCamProy, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jTabbedPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtMedioPago2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblMedioPago2)
                    .add(cmdGrabar))
                .addContainerGap())
        );
    }
    
    /**
     * Crea el panel de botones
     */
    protected void createButtonsPanel(){
        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(424, Short.MAX_VALUE)
                .add(cmdCancel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cmdProcess)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
               // .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cmdProcess)
                    .add(cmdCancel))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }
    
    /**
     * Agrego los paneles al frame principal
     */
    protected void addPanelsToFrame(){
    	org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(m_frame.getContentPane());
        m_frame.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 672, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
    }
    
	// Uso de un pago adelantado como parte de una orden de pago
	protected JComponent createPagoAdelantadoTab()
	{
		panelPagoAdelantado= new javax.swing.JPanel();
		panelPagoAdelantado.setOpaque(false);
		
		lblPagoAdelantado = new JLabel();
		lblPagoAdelantado.setText(getModel().isSOTrx()?"COBRO":"PAGO");
        lblPagoAdelantadoImporte = new JLabel();
        lblPagoAdelantadoImporte.setText("IMPORTE");
        txtPagoAdelantadoImporte = new VNumber();
        txtPagoAdelantadoImporte.setValue(BigDecimal.ZERO);
        //initFormattedTextField((JFormattedTextField)txtPagoAdelantadoImporte);
        pagoAdelantado = VComponentsFactory.VLookupFactory("C_Payment_ID",
				"C_Payment", m_WindowNo, DisplayType.Search, getModel()
						.getPagoAdelantadoSqlValidation());

		cashAdelantado = VComponentsFactory.VLookupFactory("C_CashLine_ID",
				"C_CashLine", m_WindowNo, DisplayType.Search, getModel()
						.getCashAnticipadoSqlValidation());
        lblPagoAdelantadoType = new JLabel();
        lblPagoAdelantadoType.setText("TIPO");
        cboPagoAdelantadoType = new VComboBox(new Object[] {
        	getMsg("Payment"),
        	getMsg("Cash")
        });
        // Por defecto pago.
        cboPagoAdelantadoType.setSelectedIndex(PAGO_ADELANTADO_TYPE_PAYMENT_INDEX);
        cboPagoAdelantadoType.setPreferredSize(new Dimension(200,20));
        cboPagoAdelantadoType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updatePagoAdelantadoTab();
			}
        });
        pagoAdelantadoTypePanel = new JPanel();
        pagoAdelantadoTypePanel.setLayout(new BorderLayout());
        txtPagoAdelantadoAvailable = new VNumber();
        txtPagoAdelantadoAvailable.setReadWrite(false);
        lblPagoAdelantadoAvailable = new JLabel();
        lblPagoAdelantadoAvailable.setText("PENDIENTE");
        
        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(panelPagoAdelantado);
        panelPagoAdelantado.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                	.add(lblPagoAdelantadoType)
                	.add(lblPagoAdelantado)
                    .add(lblPagoAdelantadoAvailable)
                    .add(lblPagoAdelantadoImporte))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                	.add(cboPagoAdelantadoType, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                	.add(pagoAdelantadoTypePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                    .add(txtPagoAdelantadoAvailable, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                    .add(txtPagoAdelantadoImporte, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblPagoAdelantadoType)
                    .add(cboPagoAdelantadoType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblPagoAdelantado)
                    .add(pagoAdelantadoTypePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblPagoAdelantadoAvailable)
                    .add(txtPagoAdelantadoAvailable, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblPagoAdelantadoImporte)
                    .add(txtPagoAdelantadoImporte, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                	))
        );
		
        updatePagoAdelantadoTab();
		return panelPagoAdelantado;	
	}    
    
    private void cmdSavePMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSavePMActionPerformed

    	try {
    		
	    	VOrdenPagoModel.MedioPago mp = null;
	    	
	    	switch (jTabbedPane2.getSelectedIndex())
	    	{
	    	case 0: // Efectivo
	    		mp = saveCashMedioPago();
	    		break;
	    		
	    	case 1: // Transferencia
	    		mp = saveTransferMedioPago();
	    		break;
	    	case 2: // Cheque
	    		mp = saveCheckMedioPago();
				break;
	    	case 3: // Crédito
	    		ArrayList<VOrdenPagoModel.MedioPagoCredito> mps = saveCreditMedioPago();
	    		for (VOrdenPagoModel.MedioPago unMP : mps)
	    			savePMFinalize(unMP);
	    		break;
	    	case 4:
				// Adelantado
	    		List<VOrdenPagoModel.MedioPago> mpas = savePagoAdelantadoMedioPago();
	    		for (VOrdenPagoModel.MedioPago unMP : mpas)
	    			savePMFinalize(unMP);
	    		break;
	    	default:
	    		cmdCustomSaveMedioPago(jTabbedPane2.getSelectedIndex());
	    		break;
	    	}
	    	
	    	savePMFinalize(mp);	// para Credito, mp sera null, con lo cual no hay necesidad de revalidar
	    	
    	} catch (InterruptedException e) {
    		String title = Msg.getMsg(m_ctx, "Error");
    		String msg = Msg.parseTranslation(m_ctx, e.getMessage());
    		JOptionPane.showMessageDialog(this, msg, title, JOptionPane.ERROR_MESSAGE);
    	} catch (Exception e) {
    		String title = Msg.getMsg(m_ctx, "Error");
    		String msg = Msg.parseTranslation(m_ctx, "@SaveErrorNotUnique@ \n\n" + e.getMessage() /*"@SaveError@"*/ );
    		
    		JOptionPane.showMessageDialog(this, msg, title, JOptionPane.ERROR_MESSAGE);
    	}
    }//GEN-LAST:event_cmdSavePMActionPerformed
    
    
    protected void savePMFinalize(VOrdenPagoModel.MedioPago mp) throws Exception {
    	if(mp != null){
    		if (!m_model.validateCurrentConversionRate((Integer) cboCurrency.getValue()))
    			throw new InterruptedException("@NoCurrencyConvertError@");
    		m_model.addMedioPago(mp);
    	}
    	updateTreeModel();
    	clearMediosPago();
		// Actualizar componentes de interfaz gráfica necesarios luego de
		// agregar el medio de pago 
    	updateCustomInfoAfterMedioPago(MEDIOPAGO_ACTION_INSERT);
    }
    
    protected MedioPagoEfectivo saveCashMedioPago() throws Exception{
    	MedioPagoEfectivo mpe = m_model.getNuevoMedioPagoEfectivo();
    	try {
			mpe.monedaOriginalID = (Integer) cboCurrency.getValue();
		} catch (Exception e) {
			throw new Exception(cboCurrency.getValue().toString());
		}
		try {
			mpe.importe = (BigDecimal)txtEfectivoImporte.getValue();
		} catch (Exception e) {
			throw new Exception(lblEfectivoImporte.getText());
		}		
		try {
			mpe.libroCaja_ID = (Integer)efectivoLibroCaja.getValue();
		} catch (NullPointerException ee) {
			throw new Exception(lblEfectivoLibroCaja.getText());
		}
		if (mpe.importe.compareTo(new BigDecimal(0.0)) <= 0)
			throw new Exception(lblEfectivoImporte.getText());

		// La fecha de la caja debe ser del mismo día de la operación
		if(!TimeUtil.isSameDay(mpe.getDateAcct(), getModel().getFechaOP())){
			throw new Exception(Msg.parseTranslation(m_ctx,
					"@NotAllowedCashWithDiferentDate@: \n - @DateTrx@ "
							+ getModel().getSimpleDateFormat().format(getModel().m_fechaTrx) + " \n - @Date@ @C_Cash_ID@ "
							+ getModel().getSimpleDateFormat().format(mpe.getDateAcct())));
		}
		
		mpe.setCampaign(getC_Campaign_ID() == null?0:getC_Campaign_ID());
		mpe.setProject(getC_Project_ID() == null?0:getC_Project_ID());
		
		return mpe;
    }
    
    
    protected MedioPagoTransferencia saveTransferMedioPago() throws Exception{
    	MedioPagoTransferencia mpt = m_model.getNuevoMedioPagoTransferencia();		
    	try {
			mpt.monedaOriginalID = (Integer) cboCurrency.getValue();
		} catch (Exception e) {
			throw new Exception(cboCurrency.getValue().toString());
		}
		try {
			mpt.C_BankAccount_ID = (Integer)transfCtaBancaria.getValue();
		} catch (NullPointerException ee) {
			throw new Exception(lblTransfCtaBancaria.getText());
		}		
		mpt.fechaTransf = transFecha.getTimestamp();		
		try {
			mpt.importe = (BigDecimal)txtTransfImporte.getValue();
		} catch (Exception e) {
			throw new Exception(lblTransfImporte.getText());
		}		
		mpt.nroTransf = txtTransfNroTransf.getText();
		
		if (mpt.fechaTransf == null)
			throw new Exception(lblTransfFecha.getText());
		
		if (mpt.importe.compareTo(new BigDecimal(0.0)) <= 0)
			throw new Exception(lblTransfImporte.getText());
		
		if (mpt.nroTransf.trim().equals(""))
			throw new Exception(lblTransfNroTransf.getText());
		
		mpt.setCampaign(getC_Campaign_ID() == null?0:getC_Campaign_ID());
		mpt.setProject(getC_Project_ID() == null?0:getC_Project_ID());
		
		return mpt;
    }
    
    
    protected MedioPagoCheque saveCheckMedioPago() throws Exception{
    	MedioPagoCheque mpc = m_model.getNuevoMedioPagoCheque();		
		mpc.aLaOrden = txtChequeALaOrden.getText();		
		try {
			mpc.monedaOriginalID = (Integer) cboCurrency.getValue();
		} catch (Exception e) {
			throw new Exception(cboCurrency.getValue().toString());
		}
		try {
			mpc.chequera_ID = (Integer)chequeChequera.getValue();
		} catch (Exception e) {
			throw new Exception(lblChequeChequera.getText());
		}		
		mpc.fechaEm = chequeFechaEmision.getTimestamp();
		mpc.fechaPago = chequeFechaPago.getTimestamp();
		try {	    		
			mpc.importe = (BigDecimal)txtChequeImporte.getValue();
		} catch (Exception e) {
			throw new Exception(lblChequeImporte.getText());
		}		
		mpc.nroCheque = txtChequeNroCheque.getText(); 
		mpc.banco = txtChequeBanco.getText().trim();
		mpc.cuitLibrador = txtChequeCUITLibrador.getText().trim();
		mpc.descripcion = txtChequeDescripcion.getText().trim();
		// A La Orden: Campo no obligatorio
		//
		// if (mpc.aLaOrden.trim().equals(""))
		// 	throw new Exception("");
		
		if (mpc.fechaEm == null)
			throw new Exception(lblChequeFechaEmision.getText());
		
		if (mpc.fechaPago == null)
			throw new Exception(lblChequeFechaPago.getText());
		
		if (mpc.fechaPago.compareTo(mpc.fechaEm) < 0) {
			throw new Exception(getMsg("InvalidCheckDueDate"));
		}
		
		// Realizar la comparación para que la diferencia de días sea mayor o
		// igual al mínimo permitido
		String diffDaysPreference = MPreference.searchCustomPreferenceValue(VOrdenPagoModel.MIN_CHECK_DIFF_DAYS_PREFERENCE_NAME,
				Env.getAD_Client_ID(m_ctx), Env.getAD_Org_ID(m_ctx), Env.getAD_User_ID(m_ctx), true);
		if(!Util.isEmpty(diffDaysPreference, true)){
			Integer diffDaysPreferenceInt = Integer.parseInt(diffDaysPreference);
			if (TimeUtil.getDiffDays(mpc.fechaEm, mpc.fechaPago) < diffDaysPreferenceInt.intValue()) {
				throw new Exception(
						getModel().getMsg("InvalidCheckDiffDays", new Object[] { diffDaysPreferenceInt }));
			}
		}
		
		if (mpc.importe.compareTo(new BigDecimal(0.0)) <= 0)
			throw new Exception(lblChequeImporte.getText());
		
		if (mpc.nroCheque.trim().equals(""))
			throw new Exception(lblChequeNroCheque.getText());
		
		mpc.setCampaign(getC_Campaign_ID() == null?0:getC_Campaign_ID());
		mpc.setProject(getC_Project_ID() == null?0:getC_Project_ID());
		
		return mpc;
    }
    
    
    protected ArrayList<MedioPagoCredito> saveCreditMedioPago() throws Exception{

    	int count = 0;
    	boolean isMultiSelect = false;
    	BigDecimal saldo = (BigDecimal)txtSaldo.getValue();
    	ArrayList<MedioPagoCredito> retValue = new ArrayList<MedioPagoCredito>();
    	try {
    		// Es multiSeleccion?
    		count = ((Object[])creditInvoice.getValue()).length;
    		isMultiSelect = true;
    	} catch (Exception e) { 
    		/* No es multiselect */
    		count = 1;
    	} 
    	
		// Multiseleccion: Si ya no queda saldo por cancelar, entonces no es correcto intentar asignar mas NCs
		if (isMultiSelect) {
			BigDecimal totalCreditsOpenAmt = (BigDecimal)txtCreditAvailable.getValue();
			if (totalCreditsOpenAmt.compareTo(saldo) > 0)
				throw new Exception("El monto de la multi selección ($" + totalCreditsOpenAmt + ") es mayor que el monto a cancelar ($" + saldo + ")" );
		}
    	
    	for (int i = 0; i < count; i++) {
	    	MedioPagoCredito mpcm = m_model.getNuevoMedioPagoCredito();
	    	try {
				mpcm.monedaOriginalID = (Integer) cboCurrency.getValue();
			} catch (Exception e) {
				throw new Exception(cboCurrency.getValue().toString());
			}
			try {
				if (isMultiSelect)
					mpcm.setC_invoice_ID((Integer)((Object[])creditInvoice.getValue())[i]);
				else
					mpcm.setC_invoice_ID((Integer)creditInvoice.getValue());
			} catch (Exception ee) {
				throw new Exception(lblCreditInvoice.getText());
			}
			
			try {
				mpcm.setAvailableAmt(getModel().getCreditAvailableAmt(mpcm.getC_invoice_ID() ) );
			} catch (Exception e) {
				throw new Exception(lblCreditAvailable.getText());
			}
			
			try {
				if (isMultiSelect) {
					// Setear el mínimo entre el monto pendiente de la NC y el total pendiente a pagar
					try {
						double min = Math.min(	getModel().getCreditAvailableAmt(mpcm.getC_invoice_ID()).doubleValue(),
												saldo.doubleValue());
						mpcm.setImporte(BigDecimal.valueOf(min));
						saldo= BigDecimal.valueOf(saldo.doubleValue()-min);
					} catch (Exception ex) { }
				}
				else {
					mpcm.setImporte((BigDecimal)txtCreditImporte.getValue());
				}
			} catch (Exception e) {
				throw new InterruptedException(lblCreditImporte.getText());
			}
			
			mpcm.validate();
	
			mpcm.setCampaign(getC_Campaign_ID() == null?0:getC_Campaign_ID());
			mpcm.setProject(getC_Project_ID() == null?0:getC_Project_ID());
			
			retValue.add(mpcm);
    	}
		
		return retValue;
    }
    
	protected List<MedioPago> savePagoAdelantadoMedioPago() throws Exception {
		boolean isCash = cboPagoAdelantadoType.getSelectedIndex() == PAGO_ADELANTADO_TYPE_CASH_INDEX;
		int count = 0;
    	boolean isMultiSelect = false;
    	BigDecimal saldo = (BigDecimal)txtSaldo.getValue();
    	ArrayList<MedioPago> retValue = new ArrayList<MedioPago>();
    	Object[] adelantados = null;
    	try {
    		// Es multiSeleccion?
    		adelantados = 
    				isCash ? ((Object[]) cashAdelantado.getValue())
    						: ((Object[]) pagoAdelantado.getValue()); 
			count = adelantados.length;
    		isMultiSelect = true;
    	} catch (Exception e) { 
    		/* No es multiselect */
    		count = 1;
    	} 
    	
		// Multiseleccion: Si ya no queda saldo por cancelar, entonces no es correcto
		// intentar asignar mas adelantos
		if (isMultiSelect) {
			BigDecimal totalAdelantosOpenAmt = (BigDecimal)txtPagoAdelantadoAvailable.getValue();
			if (totalAdelantosOpenAmt.compareTo(saldo) > 0)
				throw new Exception("El monto de la multi selección ($" + totalAdelantosOpenAmt
						+ ") es mayor que el monto a cancelar ($" + saldo + ")");
		}
    	
		Integer payID = null;
		BigDecimal amount = null;
		Integer monedaOriginalID;
		BigDecimal toAllocAmt; 
		BigDecimal auxAmount = BigDecimal.ZERO;
		try {
			auxAmount = (BigDecimal)txtPagoAdelantadoImporte.getValue();
		} catch (Exception e) {
			throw new Exception("@Invalid@ @Amount@");
		}
    	for (int i = 0; i < count && auxAmount.compareTo(BigDecimal.ZERO) > 0; i++) {
			// Obtengo los datos de la interfaz
			payID = isMultiSelect ? (Integer) adelantados[i]
					: (Integer) (isCash ? cashAdelantado.getValue() 
										: pagoAdelantado.getValue());
			
			try {
				monedaOriginalID = (Integer) cboCurrency.getValue();
			} catch (Exception e) {
				throw new Exception(cboCurrency.getValue().toString());
			}
			try {
				toAllocAmt = !isMultiSelect? auxAmount : 
						(isCash ? getModel().getCashAdelantadoAvailableAmt(payID)
								: getModel().getPagoAdelantadoAvailableAmt(payID));
				toAllocAmt = toAllocAmt.compareTo(auxAmount) > 0?auxAmount:toAllocAmt;
			} catch (Exception e) {
				throw new Exception("@Invalid@ @Amount@");
			}
			
			// Se agrega el cobro adelantado como medio de cobro
			retValue.add(getModel().addPagoAdelantado(payID, toAllocAmt, isCash, monedaOriginalID));
			auxAmount = auxAmount.subtract(toAllocAmt);
    	}
		return retValue;
	}
    
    
    private MyTreeNode darElementoArbolSeleccionado() {
    	if (jTree1.getSelectionCount() == 1) {
			TreePath path = jTree1.getSelectionPath();
			
			MyTreeNode tn = (MyTreeNode)path.getLastPathComponent();
			return tn;
    	}
    	
    	return null;
    }
    
    private void cmdEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdEditarActionPerformed
    	try {
			MyTreeNode tn = darElementoArbolSeleccionado();
			if (tn != null) {
				cmdEditMedioPago(tn);
				// Actualizar componentes de interfaz gráfica necesarios luego de
				// agregar el medio de pago 
		    	updateCustomInfoAfterMedioPago(MEDIOPAGO_ACTION_EDIT);
			}
			updateTreeModel();
    	} catch (Exception e) {
    		
    	}
    }//GEN-LAST:event_cmdEditarActionPerformed

    private void cmdEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdEliminarActionPerformed
    	try {
			MyTreeNode tn = darElementoArbolSeleccionado();

			if (tn != null) {
				cmdDeleteMedioPago(tn);
				// Actualizar componentes de interfaz gráfica necesarios luego de
				// agregar el medio de pago 
		    	updateCustomInfoAfterMedioPago(MEDIOPAGO_ACTION_DELETE);
			}
			updateTreeModel();
    	} catch (Exception e) {
    		
    	}
    }//GEN-LAST:event_cmdEliminarActionPerformed

    private void onFechaChange(boolean toPayMoment) {//GEN-FIRST:event_onFechaChange
    	if (rInvoiceAll.isSelected()) {
    		invoiceDatePick.setValue(new Date());
    		invoiceDatePick.setReadWrite(false);
    	} else {
    		invoiceDatePick.setReadWrite(true);
    	}
    	
    	m_model.setFechaTablaFacturas(invoiceDatePick.getTimestamp(),rInvoiceAll.isSelected());
    	updatePayAllInvoices(toPayMoment);
    }//GEN-LAST:event_onFechaChange

    protected void onTipoPagoChange(boolean toPayMoment) {//GEN-FIRST:event_onTipoPagoChange
    	if (radPayTypeStd.isSelected()) {
    		tblFacturas.setEnabled(true);
    		txtTotalPagar1.setReadWrite(false);
    		rInvoiceAll.setEnabled(true);
    		rInvoiceDate.setEnabled(true);
    	} else {
    		tblFacturas.setEnabled(false);
    		txtTotalPagar1.setReadWrite(true);
    		rInvoiceAll.setSelected(true);
    		rInvoiceAll.setEnabled(true);
    		rInvoiceDate.setEnabled(false);
    		//forzo la verificación de la entidad comercial
    		this.cmdBPartnerSelActionPerformed(null);
    		
    		//
    		
    	}

    	m_model.setPagoNormal(radPayTypeStd.isSelected(), null);
    	
    	onFechaChange(toPayMoment);
    	// Actualizar custom de las subclases
    	updateCustomTipoPagoChange();
    }//GEN-LAST:event_onTipoPagoChange

    
    protected void updatePayAllInvoices(boolean toPayMoment){
    	getModel().updatePayAllInvoices(checkPayAll.isSelected(), toPayMoment);
		// Actualizar el total a pagar
		updateTotalAPagar1();
		tblFacturas.repaint();
    }
    
    protected void updatePayInvoice(boolean pay, int row, boolean toPayMoment){
    	getModel().updatePayInvoice(pay, row, toPayMoment);
		// Actualizar el total a pagar
		updateTotalAPagar1();
		tblFacturas.repaint();
    }
    
    private void cmdCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdCancelActionPerformed

    	dispose();
    	
    }//GEN-LAST:event_cmdCancelActionPerformed

    protected void showError(String msg) {
		//String title = Msg.getMsg(m_ctx, "Error");
		String translatedMsg = Msg.parseTranslation(m_ctx, msg /*"@SaveError@"*/ );
		
		//JOptionPane.showMessageDialog(this, translatedMsg, title, JOptionPane.ERROR_MESSAGE);
		ADialog.error(m_WindowNo, this, translatedMsg);
		
    }
    
    protected boolean showAsk(String msg) {
    	String translatedMsg = Msg.parseTranslation(m_ctx, msg);
    	return ADialog.ask(m_WindowNo, this, translatedMsg);
    }

	/**
	 * Mostrar una ventana dialog con el mensaje parámetro
	 * 
	 * @param msg
	 *            clave ad_message de un mensaje o la descripción de un mensaje.
	 */
    protected void showInfo(String msg){
		ADialog.info(m_WindowNo, this, msg);
    }
    
    /* 
     * Este método se utiliza en el botón "Siquiente (F8)" y "Emitir Pago (F8)".
     * Dado que es el mismo botón pero con nombre cambiado según la pestaña.
     * Pestaña "Seleccion de Pago (F2)" >> jTabbedPane1.getSelectedIndex() = 0
     * Pestaña "Tipo de Pago" >> jTabbedPane1.getSelectedIndex() = 1
     */
    protected void cmdProcessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdProcessActionPerformed
    	
    	validatePaymentBlocked();
    	
    	final int idx = jTabbedPane1.getSelectedIndex();
    	/*
    	 * idx = 0, se encuentra en la pestaña Seleccion de Pago (F2). 
    	 * El botón accionado es "Siguiente (F8)"
    	 */
    	if (idx == 0) {

			// Aviso si la OP tiene pagos parciales
			if ((m_model.getPartialPayment()) && (!ADialog.ask(m_WindowNo, this,
					Msg.getMsg(Env.getCtx(), "PartialPayment"))))
				return;
			
    		clearMediosPago();
    		// Procesar
    		
    		BigDecimal monto = null;
    		
    		try {
    			monto = (BigDecimal)txtTotalPagar1.getValue();
    			setToPayAmtContext(monto);
    		} catch (Exception e) {
    			showError("@SaveErrorNotUnique@ \n\n" + lblTotalPagar1.getText());
    			
        		txtTotalPagar1.requestFocusInWindow();
        		//txtTotalPagar1.select(0, txtTotalPagar1.getText().length() - 1);
        		
        		return;
    		}
    		
    		m_model.setActualizarFacturasAuto(false);
    		// Fuerzo la actualizacion de los valores de la interfaz
    		onTipoPagoChange(true);
    		m_model.setPagoNormal(radPayTypeStd.isSelected(), monto);
    		m_model.setActualizarFacturasAuto(true);
    		m_model.setDocumentNo(fldDocumentNo.getText());
    		// Realizar acciones antes del PreProcesar
    		try {
        		makeOperationsBeforePreProcesar();
			} catch (Exception e) {
				showError("Se produjo un error al pre procesar. "+e.getMessage());
				return;
			}
    		
    		int status = m_model.doPreProcesar();
    		updateTreeModel();	
    		
    		switch ( status )
    		{
    		case VOrdenPagoModel.PROCERROR_OK:
    			break;
    			
    		case VOrdenPagoModel.PROCERROR_INSUFFICIENT_INVOICES:
    			showError("@InsufficientInvoicesToPayError@");
    			return;
    		
    		case VOrdenPagoModel.PROCERROR_NOT_SELECTED_BPARTNER:
    			showError("@NotSelectedBPartner@");
    			return;

    		case VOrdenPagoModel.PROCERROR_DOCUMENTNO_NOT_SET:
    			showError("Debe indicar el número de documento");
    			return;
    		
    		case VOrdenPagoModel.PROCERROR_DOCUMENTNO_ALREADY_EXISTS:
    			showError("Número de documento ya existente");
    			return;

    		case VOrdenPagoModel.PROCERROR_DOCUMENTNO_ALREADY_EXISTS_IN_OTHER_PERIOD:
    			showError("El Nro. de Documento ingresado pertenece a un Recibo anulado pero no es posible reutilizarlo porque está fuera del período actual.");
    			return;
    		
    		case VOrdenPagoModel.PROCERROR_DOCUMENTTYPE_NOT_SET:
    			showError("Debe indicar el tipo de documento");
    			return;
    			
    		case VOrdenPagoModel.PROCERROR_DOCUMENTNO_INVALID:
    			showError("El numero de documento no coincide con el esperado en la secuencia (prefijo - valor - sufijo)");
    			return;
    			
    		case VOrdenPagoModel.PROCERROR_BOTH_EXCHANGE_INVOICES:
    			showError("@BothExchangeInvoices@");
    			return;
    			
    		default:
    			showError("@ValidationError@");
    			return;
    		}
    		
			// Actualizo componentes gráficos antes de pasar a la siguiente
			// pestaña
    		updateComponentsPreProcesar();
    		dateTrx.setReadWrite(false);
    		fldDocumentNo.setValue(getModel().getDocumentNo());
    		// Avanza a la siguiente tab
    		m_cambioTab = true;
    		jTabbedPane1.setSelectedIndex(1);
    		m_cambioTab = false;
    		
    		updatePaymentsTabsState();
    		treeUpdated();
    		// Actualizar descuento de entidad comercial
    		customUpdateBPartnerRelatedComponents(false);
    		m_model.setFechaOP(dateTrx.getTimestamp());
    		
    	} 
    	/*
    	 * idx = 1, se encuentra en la pestaña "Tipo de Pago". 
    	 * El botón accionado es "Emitir Pago (F8)"
    	 */	
    	else if (idx == 1) {
    		
    		//Autorización para OP si existen pagos anticipados
    		CallResult res = validateDebitNote();

    		if ((res != null )&& (res.isError())){
				if(!Util.isEmpty(res.getMsg(), true)){
					showError(res.getMsg());
				}
				return;
			}
    		
    		getModel().setDescription(txtDescription.getText());
    		
    		m_model.setProjectID(getC_Project_ID() == null?0:getC_Project_ID());
    		m_model.setCampaignID(getC_Campaign_ID() == null?0:getC_Campaign_ID());
    		
    		// dREHER, si se trata de cobros, calcular diferencias de cambio
    		// Para pagos, no calcularlas 
    		if(m_model.getIsSOTrx().equals("Y")) {
    			BigDecimal exchangeDifference = getModel().calculateExchangeDifference();
    			m_model.setExchangeDifference( exchangeDifference == null?BigDecimal.ZERO:exchangeDifference);
    		}else {
    			m_model.setExchangeDifference(Env.ZERO);
    			debug("Se trata de pagos, NO calculo diferencia de cambio!");
    		}
    			
    		
    		/*
    		 * Esta validacion se realiza primero porque no necesariamente bloquea el proceso
    		 * si arroja error. 
    		 * El usuario decide si desea continuar o no.
    		 */
    		int status = m_model.nonBlockingValidations();
    		if(status == VOrdenPagoModel.PROCERROR_PARTNER_WITHOUT_BANKLIST) {
	    		if(showAsk("PaymentsPartnerCheckWithoutBankList")) {
	    			status = VOrdenPagoModel.PROCERROR_OK;
	    		} else {
	    			return;
	    		}
    		} 
    		
    		status = m_model.doPostProcesar(maxPaymentAllowed);
    		
    		switch (status) 
    		{
    		case VOrdenPagoModel.PROCERROR_OK:
    			break;
    		
    		case VOrdenPagoModel.PROCERROR_PAYMENTS_AMT_MAX_ALLOWED:
    			showError("@PaymentsAmtMaxAllowedExceeded@");
    			break;
    			
    		case VOrdenPagoModel.PROCERROR_PAYMENTS_AMT_MATCH:
    			showError("@PaymentsAmtMatchError@");
    			break;
    			
    		case VOrdenPagoModel.PROCERROR_PAYMENTS_GENERATION:
    			showError("@PaymentsGenerationError@ : "+ this.m_model.getMsgAMostrar());
    			break;
    			
    		default:
    			showError("@Error@"+ this.m_model.getMsgAMostrar());
    			break;
    		}
    		
    		if (status == VOrdenPagoModel.PROCERROR_OK)
    		{
	    		m_model.mostrarInforme(this, isPrintRetentions());
	    		
	    		// Reset	    		
	    		reset();
    		}
    		//else
    			//m_model.initTrx();

    	}    	
    }//GEN-LAST:event_cmdProcessActionPerformed

    // dREHER
	private void debug(String string) {
		System.out.println("==> VOrdenPago. " + string);
	}

	protected CallResult validateDebitNote() {
		CallResult result = null; 
		// Aviso si la OP tiene Nota de débitos a clientes
		// Si es no es un pago adelantado, existen pagos sin imputar y tiene el check de autorizaciones, pido autorización
		if (!radPayTypeAdv.isSelected()
				&& (getModel().getPaymentAmount() > getModel().getAmountAdvancedPayment())
				&& (getModel().isAuthorizations())) {
			AuthOperation authOperation = new AuthOperation(
					UserAuthConstants.ADVANCED_OP_RC_UID,
					Msg.getMsg(m_ctx,"AdvancedOPRCAuth"),
					UserAuthConstants.OPRC_FINISH_MOMENT);
			getAuthDialog().addAuthOperation(authOperation);
			getAuthDialog().authorizeOperation(
					UserAuthConstants.OPRC_FINISH_MOMENT);
			result = getAuthDialog().getAuthorizeResult(true);
		}
		return result;
	}

	protected void validatePaymentBlocked(){
		if(this.BPartnerSel.getValue() == null){
			return;
		}
		MBPartner partner = new MBPartner(m_ctx, (Integer)this.BPartnerSel.getValue(), m_trxName);
    	if(partner.ispaymentblocked()) { 
    		String error_msg = (Util.isEmpty(partner.getpaymentblockeddescr(), true)) ? Msg.getMsg(m_ctx, "PartnerPaymentAuthorizationFailed") : partner.getpaymentblockeddescr();
    		showError(error_msg);
    		this.cmdProcess.setEnabled(false);//Bloqueo la continuacion del pago
    		this.BPartnerSel.setValue(null);
    	} else {
    		this.cmdProcess.setEnabled(true);
    	}
	}
	
	protected void validateOnlyAllowProviders(){
		if(this.BPartnerSel.getValue() == null){
			return;
		}
		
		MBPartner partner = new MBPartner(m_ctx, (Integer)this.BPartnerSel.getValue(), m_trxName);
		/*
    	 * Chequea que si el campo "Admitir OP Solo EC Proveedores" (del Tipo de Documento) 
    	 * está activo, entonces la EC debe ser un "Proveedor".
    	 * Por cuestiones de performance, el chequeo de si la EC es Proveedor se hace primero
    	 */
    	if(!partner.isVendor()) {
    		List<Object> params = new ArrayList<Object>();
    		final StringBuffer whereClause = new StringBuffer();
    		whereClause.append("isPaymentOrderSeq=? AND AllowOnlyProviders=?");
    		params.add(new Boolean(true));
    		params.add(new Boolean(true));
    		Query q = new Query(m_ctx, MDocType.Table_Name, whereClause.toString(), m_trxName);
    		q.setParameters(params);
    		MDocType result = q.first();
    		if(result != null) {
    			String error_msg = Msg.translate(m_ctx, "OnlyAllowedProviders");
	    		showError(error_msg);
	    		this.cmdProcess.setEnabled(false);//Bloqueo la continuacion del pago
	    		this.BPartnerSel.setValue(null);
    		}
    	}
	}
	
	/**
	 * Validaciones de Entidad Comercial
	 */
	protected void doBPartnerValidations(){
		// Validar bloqueo de pagos
		validatePaymentBlocked();
		
		// Validar sólo proveedor
		validateOnlyAllowProviders();
		
	}
	
	protected boolean ValidateConvertionRate() {
		
		if(this.BPartnerSel.getValue() == null){
			return true;
		}
		
		/**
		 * Si no esta nulo la entidad comercial, validar si hay tasas de conversion para la fecha 
		 * del recibo/pago y para cada factura
		 * 
		 * dREHER
		 */
		
		// 1- valido tasa de conversion para la fecha del recibo/pago
		if(!getModel().validateConvertionRate()){
			
			showError("No se encontro tasa de conversion para la moneda y fecha " 
					+ "de alguno de los comprobantes a pagar");
			
			return false;
		}

		// 2- valido tasa de conversion para la fecha de cada factura
		if(!getModel().validateConvertionRate(getModel().m_fechaTrx)){
			
			showError("No se encontro tasa de conversion para la moneda y fecha " 
					+ "de la transaccion!");
			
			return false;
		}
		
		return true;
		
	}

	/**
     * Metodo que determina el valor que se encuentra dentro de la entidad comercial.
     * Si es null y está seteado el radio button de pago anticipado, no se puede pasar a Siguiente.
     * Para que el boton Siguiente se encuentre habilitado, debería ingresar una entidad comercial en el VLookUP
     * Además chequea si la entidad comercial está habilitada para recibir pagos.
     * @param evt
     */
    
    private void cmdBPartnerSelActionPerformed(java.awt.event.ActionEvent evt){
    	cmdProcess.setEnabled(BPartnerSel.getValue() != null);
    	// Validaciones de entidad comercial
		doBPartnerValidations();
    }
    
    
    // Declaración de varibales -no modificar//GEN-BEGIN:variables
    
    protected VLookup BPartnerSel;
    protected org.compiere.swing.CTextField fldDocumentNo;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    protected VLookup cboCampaign;
    protected VDate dateTrx;
    protected VLookup cboClient;
    protected VLookup cboOrg;
    protected VLookup cboProject;
    protected VLookup cboCurrency;
    protected VLookup chequeChequera;
    protected VDate chequeFechaEmision;
    protected VDate chequeFechaPago;
    protected javax.swing.JButton cmdCancel;
    protected javax.swing.JButton cmdEditar;
    protected javax.swing.JButton cmdEliminar;
    protected javax.swing.JButton cmdGrabar;
    protected javax.swing.JButton cmdProcess;
    protected VLookup efectivoLibroCaja;    
    protected VDate invoiceDatePick;
    protected javax.swing.JPanel jPanel1;
    protected javax.swing.JPanel jPanel10;
    protected javax.swing.JPanel jPanel11;
    protected javax.swing.JPanel jPanel2;
    protected javax.swing.JPanel jPanel3;
    protected javax.swing.JPanel jPanel4;
    protected javax.swing.JPanel jPanel5;
    protected javax.swing.JPanel jPanel6;
    protected javax.swing.JPanel jPanel7;
    protected javax.swing.JPanel jPanel9;
    protected javax.swing.JScrollPane jScrollPane1;
    protected javax.swing.JScrollPane jScrollPane2;
    protected javax.swing.JTabbedPane jTabbedPane1;
    protected javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTree jTree1;
    protected javax.swing.JLabel lblBPartner;
    protected javax.swing.JLabel lblCampaign;
    protected javax.swing.JLabel lblChequeALaOrden;
    protected javax.swing.JLabel lblChequeChequera;
    protected javax.swing.JLabel lblChequeFechaEmision;
    protected javax.swing.JLabel lblChequeFechaPago;
    protected javax.swing.JLabel lblChequeImporte;
    protected javax.swing.JLabel lblChequeNroCheque;
    protected javax.swing.JLabel lblChequeDescripcion;
    protected javax.swing.JLabel lblClient;
    protected javax.swing.JLabel lblDocumentNo;
    private javax.swing.JLabel lblDescription;
    
    protected javax.swing.JLabel lblDocumentType;
    protected VLookup cboDocumentType;
    
    protected javax.swing.JLabel lblDateTrx;
    
    protected javax.swing.JLabel lblPaymentRule;
    protected CComboBox cboPaymentRule;
    protected Map<String, ValueNamePair> paymentRules;
    
    protected javax.swing.JLabel lblEfectivoImporte;
    protected javax.swing.JLabel lblEfectivoLibroCaja;
    protected javax.swing.JLabel lblMedioPago2;
    protected javax.swing.JLabel lblOrg;
    protected javax.swing.JLabel lblProject;
    protected javax.swing.JLabel lblCurrency;
    protected javax.swing.JLabel lblRetenciones2;
    protected javax.swing.JLabel lblDifCambio;
    protected javax.swing.JLabel lblSaldo;
    protected javax.swing.JLabel lblTotalPagar1;
    protected javax.swing.JLabel lblTotalPagar2;
    protected javax.swing.JLabel lblTransfCtaBancaria;
    protected javax.swing.JLabel lblTransfFecha;
    protected javax.swing.JLabel lblTransfImporte;
    protected javax.swing.JLabel lblTransfNroTransf;
    protected javax.swing.JPanel panelCamProy;
    protected javax.swing.JRadioButton rInvoiceAll;
    protected javax.swing.JRadioButton rInvoiceDate;
    protected javax.swing.JRadioButton radPayTypeAdv;
    protected javax.swing.JRadioButton radPayTypeStd;
    protected VCheckBox checkPayAll;
    protected javax.swing.JTable tblFacturas;
    protected VDate transFecha;
    protected VLookup transfCtaBancaria;
    protected javax.swing.JTextField txtDescription;
    protected javax.swing.JTextField txtChequeALaOrden;
    protected VNumber txtChequeImporte;
    protected javax.swing.JTextField txtChequeNroCheque;
    protected javax.swing.JTextField txtChequeDescripcion;
    protected VNumber txtEfectivoImporte;
    protected javax.swing.JTextField txtMedioPago2;
    protected javax.swing.JTextField txtRetenciones2;
    protected VNumber txtSaldo;
    protected javax.swing.JTextField txtDifCambio;
    protected VNumber txtTotalPagar1;
    protected javax.swing.JTextField txtTotalPagar2;
    protected VNumber txtTransfImporte;
    protected javax.swing.JTextField txtTransfNroTransf;
    // Fin de declaraci�n de variables//GEN-END:variables
    protected JLabel lblCreditInvoice;
    protected VLookup creditInvoice;
    protected JLabel lblCreditImporte;
    protected VNumber txtCreditImporte;
    protected JLabel lblCreditAvailable;
    protected VNumber txtCreditAvailable;
	protected javax.swing.JLabel lblChequeBanco;
	protected javax.swing.JLabel lblChequeCUITLibrador;
	protected javax.swing.JTextField txtChequeBanco;
	protected VLookup cboChequeBancoID;
	protected javax.swing.JTextField txtChequeCUITLibrador;
	
    protected javax.swing.JPanel panelPagoAdelantado;
    protected javax.swing.JLabel lblPagoAdelantado;
    protected javax.swing.JLabel lblPagoAdelantadoImporte;
    protected VNumber txtPagoAdelantadoImporte;
    protected VLookup pagoAdelantado;
    protected VLookup cashAdelantado;
    protected VComboBox cboPagoAdelantadoType;
    protected JLabel lblPagoAdelantadoType;
    protected JLabel lblPagoAdelantadoAvailable;
    protected VNumber txtPagoAdelantadoAvailable;
    protected javax.swing.JPanel pagoAdelantadoTypePanel;
    
    protected static final int PAGO_ADELANTADO_TYPE_PAYMENT_INDEX = 0;
    protected static final int PAGO_ADELANTADO_TYPE_CASH_INDEX = 1;
    
    private int m_PagoAdelantadoTabIndex = -1;
    private int m_chequeTerceroTabIndex = -1;
    
    private JPanel panelChequeTercero;
    private javax.swing.JLabel lblChequeTerceroCuenta;
    private javax.swing.JLabel lblChequeTercero;
    private javax.swing.JLabel lblChequeTerceroImporte;
    private javax.swing.JLabel lblChequeTerceroDescripcion;
    private VLookup chequeTerceroCuenta;
    protected VLookup chequeTercero; //jv cambio a protected en referencia a protected WSearchEditor chequeTercero; en WOrdenPago
    private VNumber txtChequeTerceroImporte;
    private javax.swing.JTextField txtChequeTerceroDescripcion;
    
    // Variables //
    
    protected int m_WindowNo = 0;
    protected FormFrame m_frame;

    protected boolean m_cambioTab = false;
    
    protected int m_C_Currency_ID = Env.getContextAsInt( Env.getCtx(), "$C_Currency_ID" );

    protected static CLogger log = CLogger.getCLogger( VOrdenPago.class ); //jv cambio a protected en referencia a WOrdenPago
    protected VOrdenPagoModel m_model;
    protected Properties m_ctx = Env.getCtx();
    private String m_trxName;
    
    private Map<String, KeyStroke> actionKeys;
    
    protected static final String GOTO_BPARTNER = "GOTO_BPARTNER";
    protected static final String MOVE_INVOICE_FORWARD = "MOVE_INVOICE_FORWARD";
    protected static final String MOVE_INVOICE_BACKWARD = "MOVE_INVOICE_BACKWARD";
    protected static final String GOTO_PROCESS = "GOTO_PROCESS";
    protected static final String GOTO_EXIT = "GOTO_EXIT";
    protected static final String ADD_PAYMENT = "ADD_PAYMENT";
    protected static final String EDIT_PAYMENT = "EDIT_PAYMENT";
    protected static final String REMOVE_PAYMENT = "REMOVE_PAYMENT";
    protected static final String GO_BACK = "GO_BACK";
    protected static final String MOVE_PAYMENT_FORWARD = "MOVE_PAYMENT_FORWARD";
    protected static final String MOVE_PAYMENT_BACKWARD = "MOVE_PAYMENT_BACKWARD";
    protected static final String GOTO_PAYALL = "GOTO_PAYALL";
    protected static final String COMPLETE_INVOICE_AMT = "COMPLETE_INVOICE_AMT";
    
    protected static final Integer TAB_INDEX_EFECTIVO = 0;
    protected static final Integer TAB_INDEX_TRANSFERENCIA = 1;
    protected static final Integer TAB_INDEX_CHEQUE = 2;
    protected static final Integer TAB_INDEX_CREDITO = 3;
    protected static final Integer TAB_INDEX_PAGO_ADELANTADO = 4;
    
    protected static final Integer MEDIOPAGO_ACTION_INSERT = 0;
    protected static final Integer MEDIOPAGO_ACTION_EDIT = 1;
    protected static final Integer MEDIOPAGO_ACTION_DELETE = 2;
    
    private AuthorizationDialog authDialog = null;
    
    protected Map<String, String> preferenceDefaultValues = new HashMap<String, String>();
    
	public void init(int WindowNo, FormFrame frame) {
        m_WindowNo = WindowNo;
        m_frame    = frame;

        setIsSOTrxContext();
        
        if (Env.getAD_Org_ID(Env.getCtx()) == 0) {
        	showError("@InvalidPORCOrg@");
        	SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					m_frame.dispose();
				}
        	});
        	
        	return;
        }
        this.revalidate();
        this.repaint();
        initComponents();
        customInitComponents();
        initTranslations();
        initDefaultValues();
        m_model.m_facturasTableModel.addTableModelListener(this);
        m_frame.pack();
        
        onTipoPagoChange(false);
	}
	
	protected void customInitComponents() {
		//
		// Los setModel los invoco desde initComponents
		//
		// tblFacturas.setModel(m_model.getFacturasTableModel());
		// jTree1.setModel(m_model.getMediosPagoTreeModel());
		
		Date d = new Date();
		
		invoiceDatePick.setValue(d);
		
		clearMediosPago();
		
		//
		
		txtTotalPagar1.setValue(null);
		txtTotalPagar1.setValue(BigDecimal.ZERO);
		lblDateTrx.setText(getModel().isSOTrx()?"Fecha del recibo:":"Fecha de la O/P:");		
		//txtSaldo.setText("");
		txtDifCambio.setText("");
		txtTotalPagar2.setText("");
		txtRetenciones2.setText("");
		txtMedioPago2.setText("");
		txtDescription.setText("");

		//
		
		jTree1.setCellRenderer(new MyRenderer());
		jTree1.expandRow(0);
		
		// 
		
		//initFormattedTextField((JFormattedTextField)txtTotalPagar1);
		//initFormattedTextField((JFormattedTextField)txtChequeImporte);
		//initFormattedTextField((JFormattedTextField)txtEfectivoImporte);
		//initFormattedTextField((JFormattedTextField)txtTransfImporte);
		//initFormattedTextField((JFormattedTextField)txtCreditImporte);
		//initFormattedTextField((JFormattedTextField)txtCreditAvailable);
		//initFormattedTextField((JFormattedTextField)txtPagoAdelantadoAvailable);
		//txtCreditAvailable.setText("");
		// txtPagoAdelantadoAvailable.setText("");
		
		//
		cboCurrency.addVetoableChangeListener(this);
		efectivoLibroCaja.addVetoableChangeListener(this);
		transfCtaBancaria.addVetoableChangeListener(this);
		chequeChequera.addVetoableChangeListener(this);
		BPartnerSel.addVetoableChangeListener(this);
		
		chequeFechaEmision.addVetoableChangeListener(this);
		chequeFechaPago.addVetoableChangeListener(this);
		invoiceDatePick.addVetoableChangeListener(this);
		transFecha.addVetoableChangeListener(this);
		
		tblFacturas.addVetoableChangeListener(this);
		tblFacturas.getModel().addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				// Se verifica que no se esté intentando pagar una factura que no tiene una tasa de cambio para la fecha actual

				/**
				 *  dREHER esta validacion se corre al evento de seleccion de entidad comercial, 
				 *  si devuelve falso no CONTINUA
				 *  
				 *  2024-04-10
				 */
				// validateConversionRate();
				// tableUpdated();
			}
		});				
		
		// tblFacturas.getDefaultEditor(BigDecimal.class).addCellEditorListener(this);
		// TableCellEditor cellEd = tblFacturas.getDefaultEditor(BigDecimal.class);
		TableCellEditor cellEd = new DecimalEditor();
		TableCellEditor cellEd2 = new DecimalEditor();
		int cc = tblFacturas.getColumnModel().getColumnCount();
		TableColumn tc = tblFacturas.getColumnModel().getColumn(cc - 1);
		TableColumn tc2 = tblFacturas.getColumnModel().getColumn(cc - 2);
		tc.setCellEditor(cellEd);
		tc2.setCellEditor(cellEd2);
		cellEd.addCellEditorListener(this);
		cellEd2.addCellEditorListener(this);
		
		// TableCellRenderer cellRend = tblFacturas.getDefaultRenderer(Float.class);
		// tblFacturas.setDefaultRenderer(Number.class, cellRend);
		tblFacturas
				.getColumnModel()
				.getColumn(tblFacturas.getColumnModel().getColumnCount() - 1)
				.setCellRenderer(
						new MyNumberTableCellRenderer(m_model.getNumberFormat()));
		tblFacturas
				.getColumnModel()
				.getColumn(tblFacturas.getColumnModel().getColumnCount() - 2)
				.setCellRenderer(
						new MyNumberTableCellRenderer(m_model.getNumberFormat()));
		// Deshabilito los atajos F4 y F8 del jtable ya que sino me los toma ahí
		tblFacturas.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
	              KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4,0), "none");
		tblFacturas.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
	              KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F6,0), "none");
		tblFacturas.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
	              KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F8,0), "none");
		tblFacturas.setSurrendersFocusOnKeystroke(true);
		tblFacturas.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode() == arg0.VK_ENTER){
					// Si hay alguna celda en edición se corta la edición y luego
					if(tblFacturas.getEditingRow() == -1 && tblFacturas.getSelectedRow() >= 0){
						updatePayInvoice(true, tblFacturas.getSelectedRow(), false);
						// Seleccionar la próxima fila
						if(tblFacturas.getSelectedRow() < tblFacturas.getRowCount()-1){
							tblFacturas.setRowSelectionInterval(
									tblFacturas.getSelectedRow(),
									tblFacturas.getSelectedRow());
						}
					}
				}				
			}
		});
		
		jTree1.addVetoableChangeListener(this);
		jTree1.getModel().addTreeModelListener(this);
		jTree1.addMouseListener(this);
		
		jTabbedPane1.addChangeListener(this);
		jTabbedPane2.addChangeListener(this);
		
		//
		
		cmdCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/openXpertya/images/Cancel16.gif")));
		cmdCancel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

		cmdProcess.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/openXpertya/images/Process16.gif")));
		cmdProcess.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        cboClient.setReadWrite(false);
        cboClient.setValue(Env.getAD_Client_ID(m_ctx));
        cboOrg.setMandatory(true);
        cboOrg.addVetoableChangeListener(this);
        cboOrg.setValue(Env.getAD_Org_ID(m_ctx));
        
        cboDocumentType.setMandatory(true);
        cboDocumentType.addVetoableChangeListener(this);

        // campo para numero de documento
        fldDocumentNo.setMandatory(true);
        
        updateOrg((Integer)cboOrg.getValue());
		
        String Element_MC = Env.getContext(m_ctx,"$Element_MC");
        String Element_PJ = Env.getContext(m_ctx,"$Element_PJ");
        
        cboCampaign.setVisible("Y".equals(Element_MC));
        cboCampaign.setMandatory(false);
        cboCampaign.setValue(null);
        cboCampaign.refresh();
        
        cboProject.setVisible("Y".equals(Element_PJ));
        cboProject.setMandatory(false);
        cboProject.setValue(null);
        cboProject.refresh();
        
        cboCurrency.setMandatory(true);
        cboCurrency.setValue(m_C_Currency_ID);
        cboCurrency.refresh();
        setCurrencyContext();

        // Cuando cambia el documento de crédito, se carga el 
        // importe disponible en el text correspondiente
        creditInvoice.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// Para multiple seleccion de comprobantes, omitir logica de asignacion de pendiente
				try {
					if (creditInvoice.getValue() != null && ((Object[])creditInvoice.getValue()).length>1) {
						txtCreditImporte.setEnabled(false);
						txtCreditAvailable.setValue(BigDecimal.valueOf(getTotalCreditsOpenAmt()));
						return;
					}
				} catch (Exception ex) { }
				
				Integer invoiceID = (Integer)creditInvoice.getValue();
				if (invoiceID != null)
					txtCreditAvailable.setValue(getModel().getCreditAvailableAmt(invoiceID, (Integer)cboCurrency.getValue()));
					// Setear el mínimo entre el monto pendiente de la NC y el total pendiente a pagar 
					try {
						double min = Math.min(	((BigDecimal)txtCreditAvailable.getValue()).doubleValue(),
												((BigDecimal)txtSaldo.getValue()).doubleValue() );
						txtCreditImporte.setValue(BigDecimal.valueOf(min));
						txtCreditImporte.setEnabled(true);
					} catch (Exception ex) { }
			}
        	
        });
        
        
    	BPartnerSel.addActionListener( new ActionListener() {

    		public void actionPerformed(ActionEvent e) {
    			setBPartnerContext();
    		}
    		
    	});
    	
    	cboCurrency.addActionListener( new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			setCurrencyContext();
    			updatePayAmt(getModel().getSaldoMediosPago());
    		}
    		
    	});

        // Cuando cambia el documento de pago adelantado, se carga el 
        // importe disponible en el text correspondiente
        /*pagoAdelantado.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Integer paymentID = (Integer)pagoAdelantado.getValue();
				if (paymentID != null)
					txtPagoAdelantadoAvailable.setValue(getModel().getPagoAdelantadoAvailableAmt(paymentID));
			}
        	
        });

        // Cuando cambia el documento de efectivo adelantado, se carga el 
        // importe disponible en el text correspondiente
        cashAdelantado.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Integer cashLineID = (Integer)cashAdelantado.getValue();
				if (cashLineID != null)
					txtPagoAdelantadoAvailable.setValue(getModel().getCashAdelantadoAvailableAmt(cashLineID));
			}
        	
        });*/
        // Total a pagar 1
    	updateTotalAPagar1();
        // Agregado de pestañas con otras formas de pago. Método vacío que deben
        // implementar las subclases en caso de que quieran agregar otras pestañas.
        addCustomPaymentTabs(jTabbedPane2);
        // Agregado de operaciones luego de crear las pestañas custom
        addCustomOperationAfterTabsDefinition();
	}

	protected double getTotalCreditsOpenAmt() {
		double totalCreditsOpenAmt = 0;
		for (int i = 0; i < ((Object[])creditInvoice.getValue()).length; i++) { 
			totalCreditsOpenAmt = totalCreditsOpenAmt
					+ getModel().getCreditAvailableAmt((Integer) ((Object[]) creditInvoice.getValue())[i],
							(Integer) cboCurrency.getValue()).doubleValue();	
		}
		return totalCreditsOpenAmt;
	}
	
	protected void initTranslations() {
		String name;
		

		// Efectivo
		
		lblEfectivoImporte.setText(Msg.getElement(m_ctx, "Amount"));
		lblEfectivoLibroCaja.setText(Msg.getElement(m_ctx, "C_CashBook_ID"));
		
		// Transferencia 
		
		lblTransfCtaBancaria.setText(Msg.getElement(m_ctx, "C_BankAccount_ID"));
		lblTransfFecha.setText(Msg.getMsg(m_ctx, "Date"));
		lblTransfImporte.setText(Msg.getElement(m_ctx, "Amount"));
		lblTransfNroTransf.setText(Msg.getMsg(m_ctx, "TransferNumber"));
		
		// Cheques
		
		lblChequeALaOrden.setText(Msg.getMsg(m_ctx, "ALaOrden"));
		lblChequeChequera.setText(Msg.getElement(m_ctx, "CheckAccount"));
		lblChequeFechaEmision.setText(Msg.getMsg(m_ctx, "EmittingDate"));
		lblChequeFechaPago.setText(Msg.getElement(m_ctx, "PayDate"));
		lblChequeImporte.setText(Msg.getElement(m_ctx, "Amount"));
		lblChequeNroCheque.setText(Msg.getElement(m_ctx, "CheckNo"));
		lblChequeBanco.setText(Msg.translate(m_ctx, "C_Bank_ID"));
		lblChequeCUITLibrador.setText(Msg.translate(m_ctx, "CUITLibrador"));
		lblChequeDescripcion.setText(getMsg("Description"));
		
		// Credito
		lblCreditInvoice.setText(Msg.translate(m_ctx, "Credit"));
		lblCreditAvailable.setText(Msg.translate(m_ctx, "OpenAmt"));
		lblCreditImporte.setText(Msg.getElement(m_ctx, "Amount"));
		
		//
		
		lblBPartner.setText(Msg.getElement(m_ctx, "C_BPartner_ID")+" "+KeyUtils.getKeyStr(getActionKeys().get(GOTO_BPARTNER)));
		lblTotalPagar1.setText(Msg.getElement(m_ctx, "Amount"));
		lblDescription.setText(Msg.getElement(m_ctx, "Description"));
		//lblDateTrx.setText("Fecha");
		
		radPayTypeStd.setText(Msg.getMsg(m_ctx, "StandardPayment"));
		radPayTypeAdv.setText(Msg.getMsg(m_ctx, "AdvancedPayment"));
		
		lblTotalPagar2.setText(Msg.getElement(m_ctx, "Amount"));
		lblMedioPago2.setText(Msg.getElement(m_ctx, "TenderType"));
		lblRetenciones2.setText(Msg.getElement(m_ctx, "C_Withholding_ID"));
		lblDifCambio.setText(Msg.getMsg(m_ctx, "ExchangeDifference"));
		
		rInvoiceAll.setText(Msg.translate(m_ctx, "SearchAND"));
		//rInvoiceDate.setText(Msg.translate(m_ctx, "DueStart"));
		rInvoiceDate.setText(Msg.translate(m_ctx, "BeforeDueDate"));
		
		checkPayAll.setText(Msg.getMsg(m_ctx, "PayAll") + " "
				+ KeyUtils.getKeyStr(getActionKeys().get(GOTO_PAYALL)));
		
		// Saldo Total
		name = VModelHelper.GetReferenceValueTrlFromColumn("I_ReportLine", "AmountType", "BT", "name");
		lblSaldo.setText(name != null ? name : "");
		
		lblPaymentRule.setText(Msg.getElement(m_ctx, "PaymentRule"));
		//
		
		cmdCancel.setText(Msg.getMsg(m_ctx, "Close")+" "+KeyUtils.getKeyStr(getActionKeys().get(GOTO_EXIT)));
		cmdEditar.setText(Msg.getMsg(m_ctx, "Edit").replace("&", "")+" "+KeyUtils.getKeyStr(getActionKeys().get(EDIT_PAYMENT)));
		cmdEliminar.setText(Msg.getMsg(m_ctx, "Delete").replace("&", "")+" "+KeyUtils.getKeyStr(getActionKeys().get(REMOVE_PAYMENT)));
		cmdGrabar.setText(Msg.getMsg(m_ctx, "Save").replace("&", "")+" "+KeyUtils.getKeyStr(getActionKeys().get(ADD_PAYMENT)));
		cmdProcess.setText(Msg.getElement(m_ctx, "Processing")+" "+KeyUtils.getKeyStr(getActionKeys().get(GOTO_PROCESS)));
		
		//
		
		jTabbedPane1.setTitleAt(0, Msg.getMsg(m_ctx, "PaymentSelection"));
		jTabbedPane1.setTitleAt(1, Msg.getElement(m_ctx, "PaymentRule"));
		
		// Efectivo
		name = VModelHelper.GetReferenceValueTrlFromColumn("C_Order", "PaymentRule", "B", "name");
		jTabbedPane2.setTitleAt(0, name != null ? name : "");
		
		// Transferencia Bancaria
		name = VModelHelper.GetReferenceValueTrlFromColumn("C_Order", "PaymentRule", "Tr", "name");
		jTabbedPane2.setTitleAt(1, name != null ? name : "");
		
		// Cheque
		name = VModelHelper.GetReferenceValueTrlFromColumn("C_Order", "PaymentRule", "S", "name");
		jTabbedPane2.setTitleAt(2, name != null ? name : "");
		
		// Credito
		jTabbedPane2.setTitleAt(3, Msg.translate(m_ctx, "Credit"));
		
		// Pago Adelantado
		jTabbedPane2.setTitleAt(4, getMsg("AdvancedPayment"));
				
		lblClient.setText(Msg.translate(m_ctx,"AD_Client_ID"));
		lblDocumentNo.setText("Nro. Documento");
        lblOrg.setText(Msg.translate(m_ctx,"AD_Org_ID"));
        lblDocumentType.setText(Msg.translate(m_ctx,"C_DOCTYPE_ID"));
		lblProject.setText(Msg.translate(m_ctx,"C_Project_ID"));
		lblCurrency.setText(Msg.translate(m_ctx,"C_Currency_ID"));
		lblCampaign.setText(Msg.translate(m_ctx,"C_Campaign_ID"));
		lblPagoAdelantadoType.setText(getMsg("Type"));
		lblPagoAdelantadoAvailable.setText(Msg.translate(m_ctx, "OpenAmt"));
		
		// Pagos Adelantados
		lblPagoAdelantadoImporte.setText(Msg.getElement(m_ctx, "Amount"));
		lblPagoAdelantado.setText(getMsg("Payment"));
		
        updateCaptions();
	}
	
	protected void updateCaptions() {
		
		// Traduccion del boton PROCESAR
		// Actualizar las acciones habilitadas de los atajos para cada pestaña
		
		if (jTabbedPane1.getSelectedIndex() == 0){
			cmdProcess.setText(Msg.getMsg(m_ctx, "NextStep")+" "+KeyUtils.getKeyStr(getActionKeys().get(GOTO_PROCESS)));
			jTabbedPane1.setTitleAt(0, Msg.getMsg(m_ctx, "PaymentSelection"));
			lblBPartner.setText(Msg.getElement(m_ctx, "C_BPartner_ID") + " "
					+ KeyUtils.getKeyStr(getActionKeys().get(GOTO_BPARTNER)));
			setActionEnabled(GOTO_BPARTNER, true);
			setActionEnabled(GOTO_PROCESS, true);
			setActionEnabled(GOTO_EXIT, true);
			setActionEnabled(ADD_PAYMENT, false);
			setActionEnabled(EDIT_PAYMENT, false);
			setActionEnabled(REMOVE_PAYMENT, false);
			setActionEnabled(GO_BACK, false);
			setActionEnabled(MOVE_PAYMENT_FORWARD, false);
			setActionEnabled(MOVE_PAYMENT_BACKWARD, false);
			setActionEnabled(MOVE_INVOICE_FORWARD, true);
			setActionEnabled(MOVE_INVOICE_BACKWARD, true);
			setActionEnabled(GOTO_BPARTNER, true);
		}
		else if (jTabbedPane1.getSelectedIndex() == 1){
			cmdProcess.setText(getMsg("EmitPayment")+" "+KeyUtils.getKeyStr(getActionKeys().get(GOTO_PROCESS)));
			jTabbedPane1.setTitleAt(0, jTabbedPane1.getTitleAt(0) + " "
					+ KeyUtils.getKeyStr(getActionKeys().get(GO_BACK)));
			lblBPartner.setText(Msg.getElement(m_ctx, "C_BPartner_ID"));
			setActionEnabled(GOTO_BPARTNER, false);
			setActionEnabled(MOVE_INVOICE_FORWARD, false);
			setActionEnabled(MOVE_INVOICE_BACKWARD, false);
			setActionEnabled(GOTO_PROCESS, true);
			setActionEnabled(GOTO_EXIT, true);
			setActionEnabled(ADD_PAYMENT, true);
			setActionEnabled(EDIT_PAYMENT, true);
			setActionEnabled(REMOVE_PAYMENT, true);
			setActionEnabled(GO_BACK, true);
			setActionEnabled(MOVE_PAYMENT_FORWARD, true);
			setActionEnabled(MOVE_PAYMENT_BACKWARD, true);
			setActionEnabled(GOTO_BPARTNER, false);
		}
		
		// Las subclases también deben realizar las operaciones necesarias
		// correspondientes en esta instancia
		customUpdateCaptions();
	}
	
	protected void initDefaultValues() {
		// Organización
		String preferenceOrg = MPreference.searchCustomPreferenceValue(
				VOrdenPagoModel.ORG_DEFAULT_VALUE_PREFERENCE_NAME, Env.getAD_Client_ID(m_ctx), Env.getAD_Org_ID(m_ctx),
				Env.getAD_User_ID(m_ctx), true);
		if(!Util.isEmpty(preferenceOrg, true)){
			Integer preferenceOrgInt = Integer.parseInt(preferenceOrg);
			cboOrg.setValue(preferenceOrgInt);
			updateOrg(preferenceOrgInt);
		}
		
		// Tipo de Documento
		String preferenceDocTypeKey = MPreference.searchCustomPreferenceValue(
				VOrdenPagoModel.DOCTYPE_DEFAULT_VALUE_PREFERENCE_NAME, 
				Env.getAD_Client_ID(m_ctx), Env.getAD_Org_ID(m_ctx),
				Env.getAD_User_ID(m_ctx), true);
		if(!Util.isEmpty(preferenceDocTypeKey, true)){
			MDocType dt = MDocType.getDocType(m_ctx, preferenceDocTypeKey, m_trxName);
			if(dt != null && cboDocumentType.getM_lookup().containsKey(dt.getID())){
				
				cboDocumentType.setValue(dt.getID());
				try{
					vetoableChange(new PropertyChangeEvent(cboDocumentType, "C_DocType_ID", null, dt.getID()));
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}	
	
	protected void clearMediosPago() {
		
		// Efectivo
		
		efectivoLibroCaja.setValue(null);
		//txtEfectivoImporte.setText("");
		
		// Transferencia 
		
		transfCtaBancaria.setValue(null);
		//transFecha.setValue(d);
		transFecha.setValue(dateTrx.getValue());
		//txtTransfImporte.setText("");
		txtTransfNroTransf.setText("");
		
		// Cheque
		
		chequeChequera.setValue(null);
		//chequeFechaEmision.setValue(d);
		chequeFechaEmision.setValue(dateTrx.getValue());
		chequeFechaPago.setValue(null);
		if (getModel().getBPartner() == null) 
			txtChequeALaOrden.setText("");
		// Modified by Matías Cap -Disytel
		// El campo de cheque a la orden no se limpia cuando se pasan de
		// pestañas, es mas queda la misma descripción para diferentes OP 
		// para distintas entidades comerciales. La modificación involucra que
		// cuando tenemos una entidad comercial entonces coloque el nombre
		// sacando lo que había puesto antes.
		// -------------------------------------------------------------
		// Líneas anteriores comentadas 
		// -------------------------------------------------------------
//		else if (txtChequeALaOrden.getText().equals(""))
//			txtChequeALaOrden.setText(getModel().getBPartner().getName());
		// -------------------------------------------------------------
		else
			txtChequeALaOrden.setText(getModel().getChequeALaOrden());
		// -------------------------------------------------------------
		//txtChequeImporte.setText("");
		txtChequeNroCheque.setText("");
		txtChequeBanco.setText("");
		txtChequeCUITLibrador.setText("");
		txtChequeDescripcion.setText("");
		
		// Credito
		creditInvoice.setValue(null);
		//txtCreditAvailable.setText("");
		//txtCreditImporte.setText("");
		
		// Pago anticipado
		pagoAdelantado.setValue(null);
		txtPagoAdelantadoImporte.setValue(null);
		cashAdelantado.setValue(null);
		txtPagoAdelantadoAvailable.setValue(null);
		
		// Cheque de tercero
		if (m_chequeTerceroTabIndex >= 0) { // Está disponible la opcion de cheques de tercero
			//chequeTerceroCuenta.setValue(null); No se borra la cuenta para la comodidad del usuario, dado que es un campo de filtro.
			chequeTercero.setValue(null);
			//txtChequeTerceroImporte.setText("");
			txtChequeTerceroDescripcion.setText("");
		}
		cboChequeBancoID.setValue(null);
		updatePaymentsTabsState();
	}
	
	protected void loadMedioPago(VOrdenPagoModel.MedioPago mp) {
		
		clearMediosPago();
		
		if (mp.getTipoMP().equals(VOrdenPagoModel.MedioPago.TIPOMEDIOPAGO_EFECTIVO)) { // Efectivo - Cash 
		
			VOrdenPagoModel.MedioPagoEfectivo mpe = (VOrdenPagoModel.MedioPagoEfectivo)mp;
			
			efectivoLibroCaja.setValue(mpe.libroCaja_ID);
			txtEfectivoImporte.setValue(mpe.importe);
			
			jTabbedPane2.setSelectedIndex(TAB_INDEX_EFECTIVO);
			
		} else if (mp.getTipoMP().equals(VOrdenPagoModel.MedioPago.TIPOMEDIOPAGO_TRANSFERENCIA)) { // Transferencia - Transfer
			
			VOrdenPagoModel.MedioPagoTransferencia mpt = (VOrdenPagoModel.MedioPagoTransferencia)mp;
			
			transfCtaBancaria.setValue(mpt.C_BankAccount_ID);
			transFecha.setValue(mpt.fechaTransf);
			txtTransfImporte.setValue(mpt.importe);
			txtTransfNroTransf.setText(mpt.nroTransf);
			
			jTabbedPane2.setSelectedIndex(TAB_INDEX_TRANSFERENCIA);
			
		} else if (mp.getTipoMP().equals(VOrdenPagoModel.MedioPago.TIPOMEDIOPAGO_CHEQUE)) { // Cheque - Check
			
			VOrdenPagoModel.MedioPagoCheque mpc = (VOrdenPagoModel.MedioPagoCheque)mp;
			
			chequeChequera.setValue(mpc.chequera_ID);
			chequeFechaEmision.setValue(mpc.fechaEm);
			chequeFechaPago.setValue(mpc.fechaPago);
			txtChequeALaOrden.setText(mpc.aLaOrden);
			txtChequeImporte.setValue(mpc.importe);
			txtChequeNroCheque.setText(mpc.nroCheque);
			txtChequeBanco.setText(mpc.banco);
			cboChequeBancoID.setValue(mpc.bancoID);
			txtChequeCUITLibrador.setText(mpc.cuitLibrador);
			txtChequeDescripcion.setText(mpc.descripcion);
			jTabbedPane2.setSelectedIndex(TAB_INDEX_CHEQUE);
		} else if (mp.getTipoMP().equals(VOrdenPagoModel.MedioPago.TIPOMEDIOPAGO_CREDITO)) { // Credito
			VOrdenPagoModel.MedioPagoCredito mpcm = (VOrdenPagoModel.MedioPagoCredito)mp;
			creditInvoice.setValue(mpcm.getC_invoice_ID());
			txtCreditAvailable.setValue(mpcm.getAvailableAmt());
			txtCreditImporte.setValue(mpcm.getImporte());
			
			jTabbedPane2.setSelectedIndex(TAB_INDEX_CREDITO);
		} else if (mp.getTipoMP().equals(VOrdenPagoModel.MedioPago.TIPOMEDIOPAGO_PAGOANTICIPADO)) { // Pago adelantado
			VOrdenPagoModel.MedioPagoAdelantado mpa = (VOrdenPagoModel.MedioPagoAdelantado)mp;
			cboPagoAdelantadoType.setSelectedIndex(PAGO_ADELANTADO_TYPE_PAYMENT_INDEX);
			pagoAdelantado.setValue(mpa.getC_Payment_ID());
			txtPagoAdelantadoImporte.setValue(mpa.getImporte());
			txtPagoAdelantadoAvailable.setValue(getModel().getPagoAdelantadoAvailableAmt(mpa.getC_Payment_ID()));
			
			jTabbedPane2.setSelectedIndex(TAB_INDEX_PAGO_ADELANTADO);
		} else if (mp.getTipoMP().equals(VOrdenPagoModel.MedioPago.TIPOMEDIOPAGO_EFECTIVOADELANTADO)) { // Efectivo adelantado
			VOrdenPagoModel.MedioPagoEfectivoAdelantado mpa = (VOrdenPagoModel.MedioPagoEfectivoAdelantado)mp;
			cboPagoAdelantadoType.setSelectedIndex(PAGO_ADELANTADO_TYPE_CASH_INDEX);
			cashAdelantado.setValue(mpa.getCashLineID());
			txtPagoAdelantadoImporte.setValue(mpa.getImporte());
			txtPagoAdelantadoAvailable.setValue(getModel().getCashAdelantadoAvailableAmt(mpa.getCashLineID()));
			
			jTabbedPane2.setSelectedIndex(TAB_INDEX_PAGO_ADELANTADO);
		} else if (mp.getTipoMP().equals(VOrdenPagoModel.MedioPago.TIPOMEDIOPAGO_CHEQUETERCERO)) { // Cheque de tercero
			VOrdenPagoModel.MedioPagoChequeTercero mpct = (VOrdenPagoModel.MedioPagoChequeTercero)mp;
			chequeTercero.setValue(mpct.getC_Payment_ID());
			txtChequeTerceroImporte.setValue(mpct.getImporte());
			txtChequeTerceroDescripcion.setText(mpct.description);
			
			jTabbedPane2.setSelectedIndex(m_chequeTerceroTabIndex);
		}
			
	}
		
	
	public void dispose() {

		m_model.dispose();
		m_model = null;
		
		m_frame.dispose();
		m_frame = null;
		
	}
	
	protected boolean isPrintRetentions(){
		return m_model.getSumaRetenciones().compareTo(BigDecimal.ZERO) > 0
				&& ADialog.ask(m_WindowNo, this, "PrintOPRetentions");
	}

	public void actionPerformed(ActionEvent arg0) {}

	public void tableChanged(TableModelEvent arg0) {
		// System.out.println("tableChanged: " + arg0);
		if ( (arg0.getColumn() == m_model.m_facturasTableModel.getColumnCount() - 1) || (arg0.getColumn() == m_model.m_facturasTableModel.getColumnCount() - 2) ){
			// Se actualizó el monto manual
			for (int row = arg0.getFirstRow(); row <= arg0.getLastRow() && row < m_model.m_facturas.size(); row++) {
				ResultItemFactura rif = (ResultItemFactura)m_model.m_facturas.get(row);
				int currency_ID_To = (Integer) m_model.m_facturas.get(row).getItem(m_model.m_facturasTableModel.getCurrencyColIdx());
				if (arg0.getColumn() == m_model.m_facturasTableModel.getColumnCount() - 1){
					m_model.actualizarPagarConPagarCurrency(row,rif,currency_ID_To, true);
				}
				else{
					m_model.actualizarPagarCurrencyConPagar(row,rif,currency_ID_To, true);
				}
				m_model.m_facturasTableModel.fireTableDataChanged();	
			}
		}
	}

	public void vetoableChange(PropertyChangeEvent e) throws PropertyVetoException {
		// System.out.println("vetoableChange: " + arg0);
		if (e.getSource() == BPartnerSel) {
			
			// m_model.setBPartnerFacturas((Integer)BPartnerSel.getValue());
			
			SwingUtilities.invokeLater( new Runnable() {
	            public void run() {
	            	// Actualizo el modelo
	            	getModel().updateBPartner((Integer)BPartnerSel.getValue());
					// Actualizo los componentes custom de la interfaz gráfica
					// relacionados con el cambio de la entidad comercial
	            	customUpdateBPartnerRelatedComponents(true);
	            	buscarPagos();
	            	// Actualizar interfaz grafica para null value
					if (BPartnerSel.getValue() == null)
						cmdBPartnerSelActionPerformed(null);
					else {
						/**
						 * Si no esta nulo la entidad comercial, validar si hay tasas de conversion para la fecha 
						 * del recibo/pago y para cada factura
						 * 
						 * dREHER
						 */
						
						if(!ValidateConvertionRate()) {
							return;
						}
					}
	            	
					updatePayAllInvoices(false);
	            	
					// Activo/Desactivo pestaña de Pagos Adelantados dependiendo
					// que el proveedor permitao no, OP Anticipadas
					boolean allow = getModel().isAllowAdvancedPayment();
					radPayTypeAdv.setEnabled(allow);
					if (!allow) {
						radPayTypeStd.setSelected(true);
					}	
	            }
	        } );			
		} else if (e.getSource() == cboCurrency) {
			updateDependent();
		} else if (e.getSource() == efectivoLibroCaja) {
			
		} else if (e.getSource() == transfCtaBancaria) {
			
		} else if (e.getSource() == chequeChequera) {
			chequeraChange(e);
		} else if (e.getSource() == invoiceDatePick) {
			
			onFechaChange(false);
			
		} else if (e.getSource() == cboOrg) {
			int AD_Org_ID = (Integer)e.getNewValue();
			updateOrg(AD_Org_ID);
		
		} else if (e.getSource() == cboDocumentType) {
			if(e.getNewValue() != null){
				getModel().setDocumentType((Integer)e.getNewValue());
				String documentNo = null;
				try {
					documentNo = getModel().nextDocumentNo();
				} catch (Exception e2) {
					m_model.setDocumentType(null);
					fldDocumentNo.setValue(null);
					showInfo(e2.getMessage());
				}
				fldDocumentNo.setValue(documentNo);
			}
			else{
				fldDocumentNo.setValue(null);
				m_model.setDocumentType(null);
			}
		} 
	}

	protected void updateDependent() {
		efectivoLibroCaja.setValue(null);
	}

	public void stateChanged(ChangeEvent arg0) {
		if (arg0.getSource() == jTabbedPane1) {
			// TAB principal
			if (!m_cambioTab && jTabbedPane1.getSelectedIndex() == 1) {
				jTabbedPane1.setSelectedIndex(0);
			}
			
			updateCaptions();
				
			BPartnerSel.setReadWrite(jTabbedPane1.getSelectedIndex() == 0);
			cboOrg.setReadWrite(jTabbedPane1.getSelectedIndex() == 0);
			cboDocumentType.setReadWrite(jTabbedPane1.getSelectedIndex() == 0);
			fldDocumentNo.setReadWrite(jTabbedPane1.getSelectedIndex() == 0);
			dateTrx.setReadWrite(jTabbedPane1.getSelectedIndex() == 0);
			cboPaymentRule.setReadWrite(jTabbedPane1.getSelectedIndex() == 0);
			
		} else if (arg0.getSource() == jTabbedPane2) {
			// TAB de medios de pago
			updateContextValues();
		} else {
			// System.out.println("stateChanged: " + arg0);
		}
	}

	protected void treeUpdated() {
		
		// Expand all tree nodes 
		
		SwingUtilities.invokeLater( new Runnable() {
            public void run() {
            	for (int i = 0; i < jTree1.getRowCount(); i++)
            		jTree1.expandRow(i);
            }
        } );
		
		// Update stats text fields
		updateSummaryInfo();
	}

	/**
	 * Actualización de la organización
	 * @param AD_Org_ID id de la organización nueva
	 */
	protected void updateOrg(Integer AD_Org_ID){
		getModel().updateOrg(AD_Org_ID);
    	updatePayAllInvoices(false);
	}
	
	
	/**
	 * Actualización de la información de resumen de pagos, saldo, retenciones,
	 * etc.
	 */
	protected void updateSummaryInfo(){
		BigDecimal sumaMediosPago = m_model.getSumaMediosPago();
		
		txtSaldo.setValue(m_model.getSaldoMediosPago());
		txtDifCambio.setText(numberFormat(getModel().calculateExchangeDifference()));
		txtTotalPagar2.setText(numberFormat(m_model.getSumaTotalPagarFacturas()));
		txtRetenciones2.setText(numberFormat(m_model.getSumaRetenciones()));
		txtMedioPago2.setText(numberFormat(sumaMediosPago));
		
		cboProject.setReadWrite(sumaMediosPago.signum() == 0);
		cboCampaign.setReadWrite(sumaMediosPago.signum() == 0);
	}

	protected String numberFormat(BigDecimal nn) {
		return m_model.numberFormat(nn);
	}
	
	protected BigDecimal numberParse(String nn) throws ParseException {
		return m_model.numberParse(nn);
	}
	
	protected void tableUpdated() {
		updateTotalAPagar1();
	}
	
	public void treeNodesChanged(TreeModelEvent arg0) {
		treeUpdated();
	}

	public void treeNodesInserted(TreeModelEvent arg0) {
		treeUpdated();
	}

	public void treeNodesRemoved(TreeModelEvent arg0) {
		treeUpdated();
	}

	public void treeStructureChanged(TreeModelEvent arg0) {
		treeUpdated();
	}

	public void mouseClicked(MouseEvent arg0) {

		MyTreeNode tn = darElementoArbolSeleccionado();

		if (tn != null) {
			//boolean activo = tn.isMedioPago();
			boolean activo = canEditTreeNode(tn);
			
			cmdEditar.setEnabled(activo);
			cmdEliminar.setEnabled(activo);
		}
		
	}

	public void mousePressed(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}

	public void editingStopped(ChangeEvent arg0) {
		tableUpdated();
	}

	public void editingCanceled(ChangeEvent arg0) {
		tableUpdated();
	}

	public void lockUI(ProcessInfo pi) {
		this.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));
        this.setEnabled( false );
	}

	public void unlockUI(ProcessInfo pi) {
		this.setEnabled( true );
        this.setCursor( Cursor.getDefaultCursor());
	}

	public boolean isUILocked() {
		return this.isEnabled();
	}

	public void executeASync(ProcessInfo pi) { }
    
	protected void setModel(VOrdenPagoModel model) {
		m_model = model;
	}
	
	protected VOrdenPagoModel getModel() {
		return m_model;
	}
	
	protected VLookup createChequeChequeraLookup() {
		return VComponentsFactory.VLookupFactory("C_BankAccountDoc_ID", "C_BankAccountDoc", m_WindowNo, DisplayType.TableDir , getModel().getChequeChequeraSqlValidation() );
	}

	protected VLookup createChequeBancoIDLookup() {
		return VComponentsFactory.VLookupFactory("C_Bank_ID", "C_Bank", m_WindowNo, DisplayType.Search);
	}
	
	protected void chequeraChange(PropertyChangeEvent e) {
		Integer C_BankAccountDoc_ID = (Integer) e.getNewValue();
		if(C_BankAccountDoc_ID != null && C_BankAccountDoc_ID > 0) {
			X_C_BankAccountDoc bankAccountDoc = new X_C_BankAccountDoc(Env.getCtx(),C_BankAccountDoc_ID,null);
			int nextNroCheque = bankAccountDoc.getCurrentNext();
			txtChequeNroCheque.setText(String.valueOf(nextNroCheque));
			txtChequeNroCheque.setEditable(bankAccountDoc.isAllowManualCheckNo());
		} else{
			txtChequeNroCheque.setText("");
			txtChequeNroCheque.setEditable(true);
		}
	}
	
	/**
	 * Redefinir en caso de ser nesario ampliar el conjunto de pestañas con medios de
	 * pago. Aquí se deben agregar las pestañas necesarias por las especializaciones.
	 * @param tabbedPane Panel de pestaña que contiene los medios de pagos.
	 */
	protected void addCustomPaymentTabs(JTabbedPane tabbedPane) { 
		// Agregado de pestaña de medio de pago cheques de terceros.
		tabbedPane.addTab(getMsg("ThirdPartyCheck"), createChequeTerceroTab());
		m_chequeTerceroTabIndex = tabbedPane.indexOfComponent(panelChequeTercero);
	}

	/**
	 * Redefinir en caso que de ser necesario ampliar la funcionalidad y
	 * operaciones luego de crear pestañas custom si hubiere.
	 */
	protected void addCustomOperationAfterTabsDefinition(){
		// Incorporación de operaciones luego de la definición de las pestañas
		// custom
	}
	
	/**
	 * Guardado de medios de pago específicos de las subclases. En caso de que se intente
	 * guardar un medio de pago que no es de esta clase, entonces se invoca este método
	 * el cual debe implementar la lógica de guardado del medio de pago indicado por
	 * el tabIndex.
	 * @param tabIndex Indice de pestaña que contiene los datos del medio de pago.
	 */	
	protected void cmdCustomSaveMedioPago(int tabIndex) throws Exception { 
		if (tabIndex == m_chequeTerceroTabIndex)
			saveChequeTerceroMedioPago();
	}
	
	protected Integer getC_Campaign_ID() {
		return (Integer)cboCampaign.getValue();
	}
	
	protected Integer getC_Project_ID() {
		return (Integer)cboProject.getValue();
	}
	
	protected boolean canEditTreeNode(MyTreeNode treeNode) {
		return treeNode.isMedioPago();
	}
	
	/**
	 * Ingresa en modo de edición el medio de pago seleccionado en el árbol.
	 */
	protected void cmdEditMedioPago(MyTreeNode tn) {
		VOrdenPagoModel.MedioPago mp = (VOrdenPagoModel.MedioPago)tn.getUserObject();
		
		if (mp == null)
			return;
		txtCreditImporte.setEnabled(true);
		if (tn.isMedioPago()) {
			m_model.removeMedioPago(mp);
			loadMedioPago(mp);
		}
	}

	/**
	 * Borrado del nodo actual del arbol de medios de pago y retenciones.
	 */
	protected void cmdDeleteMedioPago(MyTreeNode tn) {
		VOrdenPagoModel.MedioPago mp = (VOrdenPagoModel.MedioPago)tn.getUserObject();
		
		if (mp == null)
			return;
		
		if (tn.isMedioPago()) {
			if (confirmDeleteMP(mp)){
				m_model.removeMedioPago(mp);
				updatePayAmt(getModel().getSaldoMediosPago());
			}
		}
	}
	
	protected boolean confirmDeleteMP(Object mp) {
		String msg = Msg.parseTranslation(m_ctx, "@DeleteRecord?@\r\n" + mp.toString());
		String title = Msg.getMsg(m_ctx, "Delete"); 
		
		return (JOptionPane.showConfirmDialog(this, msg, title, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION);

	}
	
	public String getMsg(String name) {
		return Msg.translate(m_ctx, name);
	}
	
	protected void updatePaymentsTabsState() {		
		updatePayAmt(getModel().getSaldoMediosPago()); //Refrescar el monto de la pestaña con el total a pagar
		/*
		 * Si el perfil tiene cargado un Unico medio de pago entonces se habilita solo ese.
		 */
		X_AD_Role role = new X_AD_Role(Env.getCtx(), Integer.parseInt((String)Env.getCtx().get("#AD_Role_ID")), null);
		if(role.getpaymentmedium() == null) {
			/*CODIGO QUE YA ESTABA*/
			jTabbedPane2.setEnabledAt(TAB_INDEX_CREDITO, m_model.isNormalPayment());
			jTabbedPane2.setEnabledAt(TAB_INDEX_PAGO_ADELANTADO, m_model.isNormalPayment());
			// Refrescar el monto de la pestaña con el total a pagar
			//updatePayAmt(getModel().getSaldoMediosPago());
//			jTabbedPane2.setSelectedIndex(TAB_INDEX_EFECTIVO);
		}
		else {//Si tiene un medio de pago cargado		
			String pm = role.getpaymentmedium();		 
			if(pm.equals(X_AD_Role.PAYMENTMEDIUM_Efectivo)) {
				jTabbedPane2.setEnabledAt(TAB_INDEX_EFECTIVO, true);
				jTabbedPane2.setSelectedIndex(TAB_INDEX_EFECTIVO);
				jTabbedPane2.setEnabledAt(TAB_INDEX_TRANSFERENCIA, false);
				jTabbedPane2.setEnabledAt(TAB_INDEX_CHEQUE, false);
				jTabbedPane2.setEnabledAt(TAB_INDEX_CREDITO, false);
				jTabbedPane2.setEnabledAt(TAB_INDEX_PAGO_ADELANTADO, false);
				if (m_chequeTerceroTabIndex != -1) jTabbedPane2.setEnabledAt(m_chequeTerceroTabIndex, false);// Tab de Cheque de terceros
				this.maxPaymentAllowed = role.getpaymentmediumlimit();
			} 
			else if(pm.equals(X_AD_Role.PAYMENTMEDIUM_Cheque)) {
				jTabbedPane2.setEnabledAt(TAB_INDEX_EFECTIVO, false);
				jTabbedPane2.setEnabledAt(TAB_INDEX_TRANSFERENCIA, false);
				jTabbedPane2.setEnabledAt(TAB_INDEX_CHEQUE, true);
				jTabbedPane2.setSelectedIndex(TAB_INDEX_CHEQUE);
				jTabbedPane2.setEnabledAt(TAB_INDEX_CREDITO, false);
				jTabbedPane2.setEnabledAt(TAB_INDEX_PAGO_ADELANTADO, false);
				if (m_chequeTerceroTabIndex != -1) jTabbedPane2.setEnabledAt(m_chequeTerceroTabIndex, false);// Tab de Cheque de terceros
				this.maxPaymentAllowed = role.getpaymentmediumlimit();
			}
			else if(pm.equals(X_AD_Role.PAYMENTMEDIUM_Credito)) {
				jTabbedPane2.setEnabledAt(TAB_INDEX_EFECTIVO, false);
				jTabbedPane2.setEnabledAt(TAB_INDEX_TRANSFERENCIA, false);
				jTabbedPane2.setEnabledAt(TAB_INDEX_CHEQUE, false);
				jTabbedPane2.setEnabledAt(TAB_INDEX_CREDITO, true);
				jTabbedPane2.setSelectedIndex(TAB_INDEX_CREDITO);
				jTabbedPane2.setEnabledAt(TAB_INDEX_PAGO_ADELANTADO, false);
				if (m_chequeTerceroTabIndex != -1) jTabbedPane2.setEnabledAt(m_chequeTerceroTabIndex, false);// Tab de Cheque de terceros
				this.maxPaymentAllowed = role.getpaymentmediumlimit();
			}
			else if(pm.equals(X_AD_Role.PAYMENTMEDIUM_ChequeDeTerceros)) {
				jTabbedPane2.setEnabledAt(TAB_INDEX_EFECTIVO, false);
				jTabbedPane2.setEnabledAt(TAB_INDEX_TRANSFERENCIA, false);
				jTabbedPane2.setEnabledAt(TAB_INDEX_CHEQUE, false);
				jTabbedPane2.setEnabledAt(TAB_INDEX_CREDITO, false);
				jTabbedPane2.setEnabledAt(TAB_INDEX_PAGO_ADELANTADO, false);
				if (m_chequeTerceroTabIndex != -1) {
					jTabbedPane2.setEnabledAt(m_chequeTerceroTabIndex, true);// Tab de Cheque de terceros
					jTabbedPane2.setSelectedIndex(m_chequeTerceroTabIndex);
				}
				this.maxPaymentAllowed = role.getpaymentmediumlimit();
			}
			else if(pm.equals(X_AD_Role.PAYMENTMEDIUM_PagoAdelantado)) {
				jTabbedPane2.setEnabledAt(TAB_INDEX_EFECTIVO, false);
				jTabbedPane2.setEnabledAt(TAB_INDEX_TRANSFERENCIA, false);
				jTabbedPane2.setEnabledAt(TAB_INDEX_CHEQUE, false);
				jTabbedPane2.setEnabledAt(TAB_INDEX_CREDITO, false);
				jTabbedPane2.setEnabledAt(TAB_INDEX_PAGO_ADELANTADO, true);
				jTabbedPane2.setSelectedIndex(TAB_INDEX_PAGO_ADELANTADO);
				if (m_chequeTerceroTabIndex != -1) jTabbedPane2.setEnabledAt(m_chequeTerceroTabIndex, false);// Tab de Cheque de terceros
				this.maxPaymentAllowed = role.getpaymentmediumlimit();
			}
			else if(pm.equals(X_AD_Role.PAYMENTMEDIUM_TransferenciaBancaria)) {
				jTabbedPane2.setEnabledAt(TAB_INDEX_EFECTIVO, false);
				jTabbedPane2.setEnabledAt(TAB_INDEX_TRANSFERENCIA, true);
				jTabbedPane2.setSelectedIndex(TAB_INDEX_TRANSFERENCIA);
				jTabbedPane2.setEnabledAt(TAB_INDEX_CHEQUE, false);
				jTabbedPane2.setEnabledAt(TAB_INDEX_CREDITO, false);
				jTabbedPane2.setEnabledAt(TAB_INDEX_PAGO_ADELANTADO, false);
				if (m_chequeTerceroTabIndex != -1) jTabbedPane2.setEnabledAt(m_chequeTerceroTabIndex, false);// Tab de Cheque de terceros
				this.maxPaymentAllowed = role.getpaymentmediumlimit();
			}		
		}
	}
	
	protected void updatePagoAdelantadoTab() {
		pagoAdelantadoTypePanel.removeAll();
		pagoAdelantado.setValue(null);
		cashAdelantado.setValue(null);
		//txtPagoAdelantadoAvailable.setText("");
		if (cboPagoAdelantadoType.getSelectedIndex() == PAGO_ADELANTADO_TYPE_PAYMENT_INDEX) {
			pagoAdelantadoTypePanel.add(pagoAdelantado);
			lblPagoAdelantado.setText(getMsg("Payment"));
		} else if (cboPagoAdelantadoType.getSelectedIndex() == PAGO_ADELANTADO_TYPE_CASH_INDEX) {
			pagoAdelantadoTypePanel.add(cashAdelantado);
			lblPagoAdelantado.setText(getMsg("Cash"));
		}
	}
	
	private JPanel createChequeTerceroTab() {
		panelChequeTercero= new javax.swing.JPanel();
		panelChequeTercero.setOpaque(false);
		
		// Cuenta
		lblChequeTerceroCuenta = new JLabel();
		lblChequeTerceroCuenta.setText(getMsg("Account"));
        chequeTerceroCuenta = VComponentsFactory.VLookupFactory("C_BankAccount_ID", "C_BankAccount", m_WindowNo, DisplayType.Search, getModel().getChequeTerceroCuentaSqlValidation(), false);
        chequeTerceroCuenta.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		Integer bankAccountID = (Integer)chequeTerceroCuenta.getValue();
        		if (bankAccountID == null)
        			bankAccountID = 0;
        		Env.setContext(m_ctx, m_WindowNo, "C_BankAccount_ID", bankAccountID);
			}
        });
        chequeTerceroCuenta.setValue(null);
		// Cheque
        lblChequeTercero = new JLabel();
        lblChequeTercero.setText(getMsg("Check"));
        chequeTercero = VComponentsFactory.VLookupFactory("C_Payment_ID", "C_Payment", m_WindowNo, DisplayType.Search, getModel().getChequeTerceroSqlValidation(),
        		true, true, true, true, "org.openXpertya.apps.search.InfoPaymentChequeTerceros"); //JACOFER
        chequeTercero.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chequeTercero.getValue() != null) {
					// Selección múltiple de cheques en cartera
					//Si selecciono uno solo, funciona como antes.
					if (chequeTercero.getValue().getClass().equals(Integer.class)) {
						Integer paymentID = (Integer)chequeTercero.getValue();
						BigDecimal importe = BigDecimal.ZERO;
						if (paymentID != null)
							importe = getModel().getPaymentAmt(paymentID);
						txtChequeTerceroImporte.setValue(importe);
					} else { //Si selecciono más de uno, viene como un array de enteros
						for (Object objID : ((Object[])chequeTercero.getValue())) {
							Integer paymentID = (Integer)objID;
							BigDecimal importe = BigDecimal.ZERO;
							if (paymentID != null)
								importe = getModel().getPaymentAmt(paymentID);
							txtChequeTerceroImporte.setValue(importe);
							try {
								VOrdenPagoModel.MedioPago mp = null;
								saveChequeTerceroMedioPago(paymentID);
								savePMFinalize(mp);	// para Credito, mp sera null, con lo cual no hay necesidad de revalidar
							} catch (InterruptedException ex) {
					    		String title = Msg.getMsg(m_ctx, "Error");
					    		String msg = Msg.parseTranslation(m_ctx, ex.getMessage());
					    		JOptionPane.showMessageDialog(chequeTercero, msg, title, JOptionPane.ERROR_MESSAGE);
					    	} catch (Exception ex) {
					    		String title = Msg.getMsg(m_ctx, "Error");
					    		String msg = Msg.parseTranslation(m_ctx, "@SaveErrorNotUnique@ \n\n" + ex.getMessage() /*"@SaveError@"*/ );
					    		
					    		JOptionPane.showMessageDialog(chequeTercero, msg, title, JOptionPane.ERROR_MESSAGE);
					    	}
						}
					}
				}
			}
        });
        // Importe
        lblChequeTerceroImporte = new JLabel();
        lblChequeTerceroImporte.setText(Msg.getElement(m_ctx, "Amount"));
        txtChequeTerceroImporte = new VNumber();
        txtChequeTerceroImporte.setValue(BigDecimal.ZERO);
        //initFormattedTextField((JFormattedTextField)txtChequeTerceroImporte);
        txtChequeTerceroImporte.setValue(null);
        txtChequeTerceroImporte.setReadWrite(false);
        // Descripcion
        lblChequeTerceroDescripcion = new JLabel();
        lblChequeTerceroDescripcion.setText(getMsg("Description"));
        txtChequeTerceroDescripcion = new javax.swing.JTextField();
      
        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(panelChequeTercero);
        panelChequeTercero.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                	.add(lblChequeTerceroCuenta)
                	.add(lblChequeTercero)
                    .add(lblChequeTerceroImporte)
                    .add(lblChequeTerceroDescripcion))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                	.add(chequeTerceroCuenta, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                	.add(chequeTercero, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                    .add(txtChequeTerceroImporte, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                    .add(txtChequeTerceroDescripcion, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblChequeTerceroCuenta)
                    .add(chequeTerceroCuenta, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblChequeTercero)
                    .add(chequeTercero, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblChequeTerceroImporte)
                    .add(txtChequeTerceroImporte, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblChequeTerceroDescripcion)
                    .add(txtChequeTerceroDescripcion, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)))
        );

		
		return panelChequeTercero;
	}
	
	private void saveChequeTerceroMedioPago() throws Exception {
		saveChequeTerceroMedioPago(null);
	}
	
	private void saveChequeTerceroMedioPago(Integer paymentID) throws Exception {
		//JACOFER Si no paso el ID del Pago, lo obtengo del VLookup (caso selección simple normal, compatibilidad)
		if (paymentID == null)
			paymentID = (Integer)chequeTercero.getValue();
		
		BigDecimal importe; 
		Integer monedaOriginalID;
		try {
			monedaOriginalID = (Integer) cboCurrency.getValue();
		} catch (Exception e) {
			throw new Exception(cboCurrency.getValue().toString());
		}
		try {
			importe = (BigDecimal)txtChequeTerceroImporte.getValue();
		} catch (Exception e) {
			throw new Exception(lblChequeTerceroImporte.getText());
		}
		String description = txtChequeTerceroDescripcion.getText().trim();
		getModel().addChequeTercero(paymentID, importe, description, monedaOriginalID);
	}
	
	private void updateContextValues() {
		if (jTabbedPane2.getSelectedIndex() == m_chequeTerceroTabIndex) {
			setIsSOTrxContext("Y");
			setBPartnerContext(null);
		} else {
			setIsSOTrxContext();
			setBPartnerContext();
		}
		updatePaymentsTabsState();
	}
	
	private void setIsSOTrxContext(String value) {
		Env.setContext(m_ctx,m_WindowNo,"IsSOTrx", value);
	}
	
	private void setIsSOTrxContext() {
		setIsSOTrxContext(getModel().getIsSOTrx());
	}
	
	private void setBPartnerContext(Integer bPartnerID) {
		if (bPartnerID != null)
			Env.setContext(m_ctx, m_WindowNo, "C_BPartner_ID", bPartnerID);
		else
			Env.setContext(m_ctx, m_WindowNo, "C_BPartner_ID", (String)null);
	}
	
	private void setCurrencyContext(Integer currencyID) {
		if (currencyID != null){
			Env.setContext(m_ctx, m_WindowNo, "C_Currency_ID", currencyID);
			// Es necesario setear el Currency_Id a 0 ya que al querer utilizarlo en alguna consulta de filtro para un VLookUp lo busca con m_WindowNo 0.
			Env.setContext(m_ctx, 0, "C_Currency_ID", currencyID);
		}
		else
			Env.setContext(m_ctx, m_WindowNo, "C_Currency_ID", (String)null);
	}
	
	private void setBPartnerContext() {
		Integer value = 0;
		if (BPartnerSel.getValue() != null)
			value = (Integer)BPartnerSel.getValue(); 
		setBPartnerContext(value);
	}
	
	private void setCurrencyContext() {
		Integer value = 0;
		if (cboCurrency.getValue() != null)
			value = (Integer) cboCurrency.getValue(); 
		setCurrencyContext(value);
	}
	
	// Seteo en el contexto el valor total a pagar de la OP para poder mostrarlo en la ventana info de Cheques
	protected void setToPayAmtContext(BigDecimal amount) {
		Env.setContext(m_ctx,m_WindowNo,"ToPayAmt", amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
	} 
	
	/**
	 * Busca las notas de créditos o pagos anticipados sin imputar para la
	 * entidad comercial seleccionada
	 */
	private void buscarPagos() {
		String title = Msg.getMsg(m_ctx, "InfoPayment");
		Integer value = 0;
		if (BPartnerSel.getValue() != null) {
			value = (Integer) BPartnerSel.getValue();
			if (getModel().buscarPagos(value) == true) {
				JOptionPane
						.showMessageDialog(
								this,
								"El Proveedor tiene notas de crédito o pagos anticipados sin imputar",
								title, 0);
			}
		}
	}

	private void viewDescription() {		
		txtDescription.setText("");
		Integer value = 0;
		if (BPartnerSel.getValue() != null) {
			value = (Integer) BPartnerSel.getValue();
			MBPartner bpartner = new MBPartner(m_ctx, value.intValue(), null);			
			if (bpartner.getDescription() != null) {
				if (bpartner.getDescription().compareTo("") != 0) {
					String description=bpartner.getDescription();					
					getModel().setDescription(description);
					txtDescription.setText(getModel().getDescription());
				}
			}
		}
	}

	/**
	 * Actualizar el total a pagar de la primer pestaña
	 */
	protected void updateTotalAPagar1(){
		BigDecimal total = m_model.getSumaTotalPagarFacturas();
		String n = numberFormat(total);
		txtTotalPagar1.setValue(total);
		txtTotalPagar1.setValue(total);
	}
	
	/**
	 * Actualiza componentes custom de la interfaz gráfica relacionadas con el
	 * cambio de entidad comercial
	 */
	protected void customUpdateBPartnerRelatedComponents(boolean loadingBP){
		if(loadingBP==true)
		{	
			viewDescription();	
		}
		// Por ahora aca no se hace nada, verificar subclases
	}
	
	protected void updatePayAmt(BigDecimal amt){
		int currencyID = ( (Integer) cboCurrency.getValue() == null) ? m_C_Currency_ID : (Integer) cboCurrency.getValue();

		amt = MCurrency.currencyConvert(amt, m_C_Currency_ID, currencyID, getModel().m_fechaTrx, getModel().AD_Org_ID, m_ctx);
		
		Integer tabIndexSelected = jTabbedPane2.getSelectedIndex();
		if(tabIndexSelected.equals(TAB_INDEX_CHEQUE)){
			txtChequeImporte.setValue(amt);
		}
		else if(tabIndexSelected.equals(TAB_INDEX_CREDITO)){
			txtCreditImporte.setValue(amt);
		}
		else if(tabIndexSelected.equals(TAB_INDEX_EFECTIVO)){
			txtEfectivoImporte.setValue(amt);
		}
		else if(tabIndexSelected.equals(TAB_INDEX_PAGO_ADELANTADO)){
			txtPagoAdelantadoImporte.setValue(amt);
		}
		else if(tabIndexSelected.equals(TAB_INDEX_TRANSFERENCIA)){
			txtTransfImporte.setValue(amt);
		}
		else {
			updateCustomPayAmt(amt);
		}
	}
	
	protected void updateCustomPayAmt(BigDecimal amt){
		// Por ahora aca no se hace nada, verificar subclases
	}
	
	
	/**
	 * Actualizo componentes luego del preprocesar y antes de procesar.
	 */
	protected void updateComponentsPreProcesar(){
		// Por ahora aca no se hace nada, verificar subclases		
	}

	/**
	 * Actualiza componentes de interfaz gráfica luego de agregar o eliminar un
	 * medio de pago
	 * 
	 * @param recibe
	 *            la acción que se realizó sobre el medio de pago, las acciones
	 *            pueden ser inserción, edición y eliminación, valores
	 *            referenciados en {@link VOrdenPago#MEDIOPAGO_ACTION_INSERT},
	 *            {@link VOrdenPago#MEDIOPAGO_ACTION_EDIT} y
	 *            {@link VOrdenPago#MEDIOPAGO_ACTION_DELETE}.
	 */
	protected void updateCustomInfoAfterMedioPago(Integer medioPagoAction){
		// Por ahora aca no se hace nada, verificar subclases
	}
	
	/**
	 * Realizar operaciones antes del pro-procesar
	 */
	protected void makeOperationsBeforePreProcesar() throws Exception{
		// Por ahora no hace nada aquí
	}

	/**
	 * Realizar operaciones luego de cambiar el tipo de pago, normal o
	 * adelantado
	 */
	protected void updateCustomTipoPagoChange(){
		// Por ahora no hace nada aquí
	}
	
	/**
	 * Realizar operaciones luego de iniciar los atajos y sus operaciones
	 */
	protected void customKeyBindingsInit(){
		// Por ahora no hace nada aquí		
	}
	
	/**
	 * Realizar operaciones luego de actualizar los captions y acciones
	 */
	protected void customUpdateCaptions(){
		// Por ahora no hace nada aquí
	}
	
	/**
	 * Resetear info.
	 */
	protected void reset(){
		BPartnerSel.setValue(null);
		m_model.setBPartnerFacturas(0);
		radPayTypeStd.setSelected(true);
		m_model.actualizarFacturas();
		cboCampaign.setValue(null);
		cboProject.setValue(null);
		cboCurrency.setValue(m_C_Currency_ID);
		txtTotalPagar1.setValue(null);
		m_cambioTab = true;
		jTabbedPane1.setSelectedIndex(0);
		m_cambioTab = false;
		txtDescription.setText("");

		m_model.setDescription("");
		checkPayAll.setSelected(false);
		cboChequeBancoID.setValue(null);
		
		cboDocumentType.setValue(null);
		m_model.setDocumentType(null);
		fldDocumentNo.setValue(null);
		m_model.setDocumentNo(null);
		
		/*String documentNo = null;
		Integer docTypeID = (Integer)cboDocumentType.getValue();
		try {
			documentNo = getModel().nextDocumentNo();
		} catch (Exception e) {
			documentNo = null;
			docTypeID = null;
		}
		
		m_model.setDocumentType(docTypeID);
		cboDocumentType.setValue(docTypeID);
		fldDocumentNo.setValue(documentNo);
		m_model.setDocumentNo(documentNo);*/
		
		getAuthDialog().markAuthorized(UserAuthConstants.OPRC_FINISH_MOMENT, true);
		
		getModel().reset();
	}

	protected void setActionKeys(Map<String, KeyStroke> actionKeys) {
		this.actionKeys = actionKeys;
	}

	protected Map<String, KeyStroke> getActionKeys() {
		return actionKeys;
	}
	
	/**
	 * Realiza el movimiento hacia adelante o atrás de la selección de una
	 * grilla.
	 * 
	 * @param forward
	 *            <code>true</code> para avanzar una fila, <code>false</code>
	 *            para retroceder.
	 */
	private void moveTableSelection(JTable table, boolean forward) {
		if (table.getRowCount() == 0) {
			return;
		}
		int srow = table.getSelectedRow();
		if (srow == -1) {
			srow = 0;
		} else {
			srow = forward ? srow+1 : srow-1;
			if (srow < 0) {
				srow = table.getRowCount() - 1;
			} else if (srow >= table.getRowCount()) {
				srow = 0;
			}
		}
		table.requestFocus();
		table.setRowSelectionInterval(srow, srow);
		table.setColumnSelectionInterval(table.getColumnCount()-1, table.getColumnCount()-1);
	}
	
	/**
	 * Dispara el evento action performed con el evento parámetro a todos los
	 * listeners parámetro
	 * 
	 * @param listeners
	 * @param event
	 */
	protected void fireActionPerformed(ActionListener[] listeners, ActionEvent event){
		for (ActionListener actionListener : listeners) {
			actionListener.actionPerformed(event);
		}
	}
	
	protected CComboBox createPaymentRuleCombo(){
		CComboBox paymentRuleCombo = new CComboBox();
		paymentRules = new HashMap<String, ValueNamePair>();
		List<ValueNamePair> list = getModel().getPaymentRulesList();
		for (ValueNamePair paymentRule : list) {
			paymentRuleCombo.addItem(paymentRule);
			paymentRules.put(paymentRule.getValue(), paymentRule);
		}
		paymentRuleCombo.setMandatory(true);
		paymentRuleCombo.setValue(paymentRules.get(getModel().getDefaultPaymentRule()));
		getModel().setPaymentRule(getModel().getDefaultPaymentRule());
		Env.setContext(m_ctx, m_WindowNo, "PaymentRule", getModel().getDefaultPaymentRule());
		return paymentRuleCombo;
	}
	
	protected void updatePaymentRule(){
		getModel().setPaymentRule(((ValueNamePair)cboPaymentRule.getValue()).getValue());
		Env.setContext(m_ctx, m_WindowNo, "PaymentRule", ((ValueNamePair) cboPaymentRule.getValue()).getValue());
		getModel().actualizarFacturas();
	}

	/* === Contenido migrado de VOrdenPagoModel === */
	
	public class MyTreeNode extends DefaultMutableTreeNode {
		
		protected String m_msg;
		protected boolean m_leaf;
		
		public MyTreeNode(String msg, Object obj, boolean leaf) {
			m_msg = msg;
			m_leaf = leaf;
			userObject = obj;
		}
		
		public void setMsg(String msg) {
			m_msg = msg;
		}
		
		public String toString() {
			return m_msg != null ? m_msg : userObject.toString();
		}
		
		public boolean isLeaf() {
			return m_leaf;
		}
		
		public boolean isMedioPago() {
			return userObject != null && userObject instanceof MedioPago;
		}
		
		public boolean isRetencion() {
			return userObject != null && !isMedioPago();
		}
	}
	
	// Tree de Medios de pago
	private DefaultTreeModel m_arbolModel = null;
	private MyTreeNode m_nodoRaiz = null;
	private MyTreeNode m_nodoRetenciones = null;
	private MyTreeNode m_nodoMediosPago = null;

	
	public TableModel getFacturasTableModel() {
		return VModelHelper.HideColumnsTableModelFactory( m_model.m_facturasTableModel );
	}
	
	
	protected void initTreeModel() {
		String nodoRaizMsg ;
		String nodoRetencionesMsg = Msg.getElement(m_ctx, "C_Withholding_ID");
		String nodoMediosPagoMsg = Msg.translate(m_ctx, m_model.getMsgMap().get("TenderType"));
		String bpName = m_model.BPartner != null ? m_model.BPartner.getName() + " - " : "";
		
		String monto = numberFormat(m_model.getSumaTotalPagarFacturas());
		
		nodoRaizMsg = bpName + Msg.getElement(m_ctx, "Amount") + ": " + monto;
		
		// Crear los nodos base o actualizar sus mensajes
		
		if (m_nodoRaiz == null) {
			m_nodoRaiz = new MyTreeNode(nodoRaizMsg, null, false);
		} else {
			m_nodoRaiz.setMsg(nodoRaizMsg);
			m_nodoRaiz.removeAllChildren();
		}
		
		if (m_nodoRetenciones == null)
			m_nodoRetenciones = new MyTreeNode(nodoRetencionesMsg, null, false);
		else
			m_nodoRetenciones.setMsg(nodoRetencionesMsg);
		
		if (m_nodoMediosPago == null)
			m_nodoMediosPago = new MyTreeNode(nodoMediosPagoMsg, null, false);
		else
			m_nodoMediosPago.setMsg(nodoMediosPagoMsg);
		
		// Agrego los hijos del nodo raiz
		
		if (m_model.m_retenciones.size() > 0) 
			m_nodoRaiz.add(m_nodoRetenciones);
		
		m_nodoRaiz.add(m_nodoMediosPago);
		
		// Actualizo el Modelo 
		
		if (m_arbolModel == null)
			m_arbolModel = new DefaultTreeModel(m_nodoRaiz);
		else
			m_arbolModel.setRoot(m_nodoRaiz);
	}
	
	public void updateTreeModel() {
		initTreeModel();
		
		// Agrego las retenciones
		
		if (m_nodoRetenciones != null) {
			m_nodoRetenciones.removeAllChildren();
			
			for (RetencionProcessor r : m_model.m_retenciones)
				m_nodoRetenciones.add(new MyTreeNode(r.getRetencionSchemaName() + ": " + numberFormat( r.getAmount() ), r, true));
		}
		
		// Agrego los medios de pago
		
		if (m_nodoMediosPago != null) {
			m_nodoMediosPago.removeAllChildren();
			
			for (MedioPago mp : m_model.m_mediosPago) 
				m_nodoMediosPago.add(new MyTreeNode(null, mp, true));
		}
		
		m_arbolModel.nodeStructureChanged(m_nodoRaiz);
	}
	
	public TreeModel getMediosPagoTreeModel() {
		updateTreeModel();
		return m_arbolModel;
	}

	
	public boolean isForPos() {
		return false;
	}


	public User getUser(int userID) {
		MUser mUser = MUser.get(Env.getCtx(), userID);
		// Validación de usuario.
		if (mUser == null)
			return null;
		User user = new User(mUser.getName(), mUser.getPassword());
		user.setOverwriteLimitPrice(false);
		user.setPoSSupervisor(false);
		return user;
	}

	public AuthorizationDialog getAuthDialog() {
		return authDialog;
	}

	public void setAuthDialog(AuthorizationDialog authDialog) {
		this.authDialog = authDialog;
	}
	
}
