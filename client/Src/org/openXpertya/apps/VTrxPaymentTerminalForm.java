package org.openXpertya.apps;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.compiere.swing.CButton;
import org.compiere.swing.CDialog;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTextField;
import org.compiere.swing.CTextPane;
import org.openXpertya.clover.model.TrxClover;
import org.openXpertya.grid.ed.VNumber;
import org.openXpertya.pos.ctrl.PoSConfig;
import org.openXpertya.pos.view.PoSMainForm;
import org.openXpertya.swing.util.FocusUtils;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

import com.clover.sdk.v3.payments.Payment;
import com.clover.sdk.v3.payments.TransactionInfo;

public class VTrxPaymentTerminalForm extends CDialog implements ActionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	PoSMainForm   formTPV;
	private JLabel lblPairingCode;
	private JLabel lblForPairingCode;
	private CTextPane info = new CTextPane();
    private CButton okButton;
    private CButton closeButton;
    private CButton cancelButton;
    
    private CButton abortButton;

    private static String NL = "<br>";
    private static Icon i_inform = Env.getImageIcon( "Inform32.gif" );
    private static Icon i_error = Env.getImageIcon( "Error32.gif" );
    private static Icon i_warn = Env.getImageIcon( "Warn32.gif" );
    private static Icon i_question = Env.getImageIcon( "Question32.gif" );
    private CLabel iconLabel = new CLabel();
    
	// uilizado para la transaccion clover
	private TrxClover clover = null;
    private boolean isConnect = false;
    
    private boolean isError = false;
    
    private int numeroCuotas = 0;
    private String plan = "";
    private String documentNo = "";
    private BigDecimal payamt = null;
    private BigDecimal montoACobrar = Env.ZERO;
    private String marcaTarjeta = "";
	private String MedioPago = "";
	private String NumeroComercio = "";

	private BigDecimal cashRetirementAmt = null;
	private BigDecimal cashARetirementAmt = null; // v 3.0


	private boolean isRetiraEfectivo = false;
	private Payment payment = null; // utilizado para guardar la info del ultimo payment
    
	private final javax.swing.JPanel contentPanel = new JPanel();

	// dREHER en caso de que falle Clover, poder pedir desde aca la info manualmente
	private CLabel cCouponNumberLabel = null;
	private CLabel cBatchLabel = null;
	private CLabel cCuotasPosnetLabel = null;
	private CLabel cCreditCardLabel = null;
	private CLabel cRetiraEfectivoLabel = null;
	private CLabel cMontoLabel = null;
	private CTextField cCouponNumberText = null;
	private CTextField cBatchText = null;
	private VNumber cCuotasPosnetText = null;
	private VNumber cMontoPosnetText = null;
	private CTextField cCreditCardText = null;
	private VNumber cRetiraEfectivo = null;
	
	private String MSG_BATCH;
	private String MSG_COUPON_NUMBER;
	private String MSG_CUOTAS_POSNET;
	private String MSG_CREDITCARD_NUMBER;
	private String MSG_RETIRA_EFECTIVO;
	private String MSG_MONTO;
	
	// v 2.0
	private String MSG_QUESTION;
	private String MSG_DESC_QUESTION;
	
	private CButton QOkButton;
	private CButton QKoButton;
	
	private JLabel QQuestionLabel = null;
	private JLabel QDescQuestionLabel = null;
		
	private CPanel cQuestionDataPanel;
	private String QACTION = null;
	
	// 
	
	private CPanel cPosnetDataPanel;
	private Map<String, KeyStroke> actionKeys;
	
	private boolean isShowCancelButton = false;
	private boolean isTrxLog = false;
	
	protected static final int COUPONBATCH_ID_MAX_LENGTH = 4;
	protected static final int COUPON_ID_MAX_LENGTH = 10;

	protected static final int CREDITCARDNUMBER_MAX_LENGTH = 16;
	
	// v 2.0
	Timer timer = null;
	
	
	/**
	 * Create the dialog.
	 */
	public VTrxPaymentTerminalForm(PoSMainForm orderTPV, BigDecimal amount, int cuotas, String plan, 
			String documentNo, boolean isRetiraEfectivo, BigDecimal amountCashBack, 
			boolean isAnular, Payment lastPayment, boolean isOnlyTestConnect, String marcaTarjeta, String medioPago, String numeroComercioClover) {
		
		formTPV = orderTPV;
		
		// Clover formatea los numeros tomando los ultimos dos como decimales, por lo tanto hay q sumarle estos ceros
		// Ej: 1.13 en TPV
		// a Clover -> 113
		
		// guardo el monto original a cobrar
		setMontoACobrar(amount);
		debug("Recibo para cobrar, medio pago: " + medioPago + " Monto:" + amount);
		
		amount = amount.multiply(Env.ONEHUNDRED);
				
		setPayamt(amount);
		setNumeroCuotas(cuotas);
		setPlan(plan);
		setDocumentNo(documentNo);
		setRetiraEfectivo(isRetiraEfectivo);
		setMarcaTarjeta(marcaTarjeta);
		setMedioPago(medioPago);
		setNumeroComercio(numeroComercioClover);
		
		// v 3.0
		setCashARetirementAmt(amountCashBack);
		
		debug("Monto a cobrar: " + amount.divide(new BigDecimal(100), RoundingMode.HALF_DOWN));
		debug("Cuotas: " + cuotas);
		debug("Plan: " + plan);
		debug("DocumentNo: " + documentNo);
		debug("Retira efe: " + isRetiraEfectivo);
		debug("Monto a retirar en efectivo: " + amountCashBack);
		debug("Marca Tarjeta: " + marcaTarjeta);
		debug("Medio Pago: " + medioPago);
		debug("Numero Comercio: " + numeroComercioClover);
		
		debug("IsTrxCloverLog: " + orderTPV.isTrxCloverLog);
		setTrxLog(orderTPV.isTrxCloverLog);
		
		init();
		
		// Solo iniciar el timer de transaccion ok, si no esta testeando coneccion
		if(!isOnlyTestConnect)
			initTimer();
	
		// Si no debe anular, es porque va un pago
		if(!isAnular && !isOnlyTestConnect) {
			
			// Si no llega un pago, quiere decir que debe cobrar, en cambio si llega pago y NO es anular
			// quiere decir que ya fue cobrado, NO volver a cobrar...
			if(lastPayment==null)
				SaleRequest(this);
			else {
				setPayment(lastPayment);
				YaCobradoContinuar();
			}
			
		} else if(isAnular){ // Aca viene para anular un pago
			setPayment(lastPayment);
			refundSale(this, false);
			
		} else if(isOnlyTestConnect){ // Aca viene para solo testear conexion
			isConnect = TestConnect(this);
		}
		
	}

	public boolean isTrxLog() {
		return isTrxLog;
	}

	public void setTrxLog(boolean isTrxLog) {
		this.isTrxLog = isTrxLog;
	}

	/**
	 * Timer para mostrar boton de abortar transaccion
	 * dREHER
	 */
	private void initTimer() {
		final int SECONDS_TO_WAIT = 37;   // default 37 acordado con la gente de Viedma
		// v 2.0
		// final Timer timer = new Timer();
		
		timer = new Timer();
		debug("initTimer...");

        // Programar la tarea que se ejecutará cada segundo
        timer.scheduleAtFixedRate(new TimerTask() {
        	int elapsedSeconds = 0;
            public void run() {
                elapsedSeconds++; // Incrementar el contador de segundos
                // System.out.println("Han pasado " + elapsedSeconds + " segundos.");

                if (elapsedSeconds >= SECONDS_TO_WAIT) {
                    // Acción a realizar después de 10 segundos
                    // debug("Timer - Han pasado " + SECONDS_TO_WAIT + " segundos. Se puede mostrar boton de abortar...");
                    
                    // v 2.0
                    // isShowCancelButton = true;
                    // showAbortButton();
                    
                	if(!okButton.isVisible() && !cancelButton.isVisible()) {

                		timer.cancel(); // Detener el temporizador
                		QACTION = "TIMER";
                		showQPanel("No se obtuvo respuesta desde dispositivo Clover!", 
                				"Ya paso la tarjeta del cliente y/o continuar?");

                	}else
                		elapsedSeconds = 0;
                    
                }
            }
        }, 1000L, 1000L); // Comenzar después de 1 segundo, repetir cada segundo
		
	}

	private void YaCobradoContinuar() {
		
		debug("No debe cobrar, YA COBRADO ANTERIORMENTE!");
		
		formTPV.setContinue(true);
		formTPV.lastPayment = getPayment();
		
		updateButtons(false, false, true);
		setError(false);
		
		if(getTrxClover()==null && getPayment()!=null) {
			
			clover = new TrxClover();
			clover.setDefault();
			
			TransactionInfo transactionInfo = payment.getTransactionInfo();
			
			clover.setPayAmt(Long2BigDecimal(payment.getAmount()));
			if(transactionInfo.getInstallmentsQuantity()!=null)
				clover.setCuotas(transactionInfo.getInstallmentsQuantity());
			clover.setPlan(transactionInfo.getInstallmentsPlanCode());

			clover.setAuthorizationID(payment.getCardTransaction().getAuthCode());
			clover.setCard(transactionInfo.getCardSymbol());
			
			// Paso los numeros que puedo obtener para comparar
			clover.setCardNumber(payment.getCardTransaction().getFirst6() +
					"-" +
					payment.getCardTransaction().getLast4());
			
			clover.setCardType(transactionInfo.getCardTypeLabel());
			
			if(payment.getCashbackAmount()!=null)
				clover.setCashRetirementAmt(Long2BigDecimal(payment.getCashbackAmount()));
			else
				clover.setCashRetirementAmt(new BigDecimal(0));
			
			// *********************************************************************
			// HardCoded test only!!!
			// setCashRetirementAmt(new BigDecimal(100));
			// debug("ATENCION!!! Forzando por codigo retiro en efectivo de $ 100");
			// *********************************************************************
			
			clover.setCouponBatchNumber(transactionInfo.getBatchNumber());
			clover.setCouponNumber(transactionInfo.getReceiptNumber());
			clover.setReferenceID(payment.getId());
			clover.setTransactionNumber(payment.getExternalPaymentId());
			clover.setTitularName(payment.getCardTransaction().getCardholderName());
		}
			
		getTrxClover().setYaCobrado(true);
	
	}
	
	protected BigDecimal Long2BigDecimal(Long amount) {
		return new BigDecimal(amount).divide(Env.ONEHUNDRED, 2, RoundingMode.HALF_UP);
	}

	private void init() {
		
		keyBindingsInit();
		
		this.setTitle("Cobro con Clover Online");
		setSize(640, 570);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		setResizable(false);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		CLabel lblNewLabel = new CLabel();
		lblNewLabel.setVerticalAlignment(SwingConstants.TOP);
		lblNewLabel.setText("<html>\r\n<div style='text-align: center;'>\r\n\tPanel de interaccion con Clover Online...");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(10, 14, 400, 45);
		contentPanel.add(lblNewLabel);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Respuestas Clover...", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(10, 50, 620, 425); // <-- 10, 50, 565, 360
		contentPanel.add(panel);
		
		lblForPairingCode = new CLabel("Codigo de emparejamiento :");
		lblForPairingCode.setBounds(24, 24, 87, 24);
		panel.add(lblForPairingCode);
		
		lblPairingCode = new JLabel("Esperando codigo...");
		lblPairingCode.setFont(new Font("Tahoma", Font.BOLD, 18));
		panel.add(lblPairingCode);
		
		panel.add(iconLabel);
		
		/* v 2.0
		abortButton = new CButton("Abortar (CTRL+T)");
		abortButton.setIcon(new ImageIcon(VTrxPaymentTerminalForm.class.getResource("/org/openXpertya/images/Cancel24.gif")));
		abortButton.setActionCommand("ABORTAR");
		abortButton.addActionListener(this);
		panel.add(abortButton);
		*/
		
		// Detail text
        info.setPreferredSize(new Dimension(530,190));
        info.setRequestFocusEnabled( false );
        info.setEnabled(true);
        info.setOpaque( true );
        info.setReadWrite(false);
        info.setAutoscrolls(true);
        info.setVisible(true);
		panel.add(info);
		
		/*
		 * Panel para agregar campos faltantes en caso de que se produzca error
		 * Lote, cupon, cuotas y tarjetas
		 */
		MSG_COUPON_NUMBER = getMsg("CouponNumber");
		MSG_BATCH = getMsg("CouponBatchNumberShort");
		MSG_CUOTAS_POSNET = getMsg("CuotasPosnet");
		MSG_CREDITCARD_NUMBER = getMsg("CreditCardNumber");
		MSG_RETIRA_EFECTIVO = getMsg("Retira Efectivo");
		MSG_MONTO = getMsg("Monto Cobrado");
		
		MSG_QUESTION = getMsg("Pregunta");
		MSG_DESC_QUESTION = getMsg("Detalle de la pregunta");
		
		QOkButton = new CButton("Si");
		QOkButton.setIcon(new ImageIcon(VTrxPaymentTerminalForm.class.getResource("/org/openXpertya/images/Ok24.png")));
		QOkButton.setActionCommand("QOk");
		QOkButton.addActionListener(this);
		
		QKoButton = new CButton("No");
		QKoButton.setIcon(new ImageIcon(VTrxPaymentTerminalForm.class.getResource("/org/openXpertya/images/Cancel24.gif")));
		QKoButton.setActionCommand("QKo");
		QKoButton.addActionListener(this);
		
		getCQuestionDataPanel().setVisible(false);
		
		panel.add(getCPosnetDataPanel());
		panel.add(getCQuestionDataPanel());
		
		/********************/
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		okButton = new CButton("Confirmar (CTRL+K)");
		okButton.setIcon(new ImageIcon(VTrxPaymentTerminalForm.class.getResource("/org/openXpertya/images/Ok24.png")));
		okButton.setActionCommand("OK");
		okButton.addActionListener(this);
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		closeButton = new CButton("Cerrar (CTRL+R)");
		closeButton.setIcon(new ImageIcon(VTrxPaymentTerminalForm.class.getResource("/org/openXpertya/images/Cancel24.gif")));
		closeButton.setActionCommand("CERRAR");
		closeButton.addActionListener(this);
		buttonPane.add(closeButton);
		
		cancelButton = new CButton("Anular (CTRL+N)");
		cancelButton.setIcon(new ImageIcon(VTrxPaymentTerminalForm.class.getResource("/org/openXpertya/images/Cancel24.gif")));
		cancelButton.setActionCommand("ANULAR");
		cancelButton.addActionListener(this);
		buttonPane.add(cancelButton);
		
		updateButtons(false, false, false);
		
        getCPosnetDataPanel().setVisible(false);
	}

	private void keyBindingsInit() {
		
		// Se asignan las teclas shorcut de las acciones.
		setActionKeys(new HashMap<String, KeyStroke>());
		getActionKeys().put("OK", KeyStroke.getKeyStroke("ctrl K"));
		getActionKeys().put("KO", KeyStroke.getKeyStroke(0, 0));
		getActionKeys().put("CERRAR", KeyStroke.getKeyStroke("ctrl R"));
	
	// v2.0	
	//	getActionKeys().put("ANULAR", KeyStroke.getKeyStroke("ctrl N"));
	//	getActionKeys().put("ABORTAR", KeyStroke.getKeyStroke("ctrl T"));
		
		getRootPane().getActionMap().put("OK", new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				if(okButton.isVisible()) OK();
			}
        	
        });
		getRootPane().getActionMap().put("CERRAR", new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				if(closeButton.isVisible()) CLOSE();
			}
        	
        });
		getRootPane().getActionMap().put("KO", new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				KO();
			}
        	
        });
		getRootPane().getActionMap().put("ANULAR", new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				if(cancelButton.isVisible()) ANULA();
			}
        	
        });
		getRootPane().getActionMap().put("ABORTAR", new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				ABORTAR();
			}
        	
        });
        
        setActionEnabled("OK", true);
        setActionEnabled("KO", true);
        setActionEnabled("CERRAR", true);
     
     // v 2.0   
     //   setActionEnabled("ANULAR", true);
     //   setActionEnabled("ABORTAR", true);
        
        // Anular el comportamiento de la tecla ESC
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0), "none");
        
	}
	
	private void setActionEnabled(String action, boolean enabled) {
		String kAction = (enabled ? action : "none");
		KeyStroke keyStroke = getActionKeys().get(action);

		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, kAction);
	}
	
	/**
	 * @return Devuelve actionKeys.
	 */
	private Map<String, KeyStroke> getActionKeys() {
		return actionKeys;
	}

	/**
	 * @param actionKeys Fija o asigna actionKeys.
	 */
	private void setActionKeys(Map<String, KeyStroke> actionKeys) {
		this.actionKeys = actionKeys;
	}

	/*************************** Inicio controles para carga manual de informacion **********************/
	
	/**
	 * This method initializes cPosnetDataPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCPosnetDataPanel() {
		if (cPosnetDataPanel == null) {
			
			cPosnetDataPanel = new CPanel();
			cPosnetDataPanel.setLayout(new GridBagLayout());
			cPosnetDataPanel.setBorder(new TitledBorder(null, "Carga Manual", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			
			// Primera línea
	        addComponent(MSG_BATCH, 0, 0, GridBagConstraints.HORIZONTAL, 10);
	        addComponent(getCBatchText(), 1, 0, 1, 1, GridBagConstraints.HORIZONTAL, 10);
	        addComponent(MSG_COUPON_NUMBER, 2, 0, GridBagConstraints.HORIZONTAL, 10);
	        addComponent(getCCouponNumberText(), 3, 0, 1, 1, GridBagConstraints.HORIZONTAL, 10);
	        
	        // Segunda linea
	        addComponent(MSG_CUOTAS_POSNET, 0, 1, GridBagConstraints.HORIZONTAL, 10);
	        addComponent(getCCuotasPosnetText(), 1, 1, 1, 1, GridBagConstraints.HORIZONTAL, 10);
	        addComponent(MSG_CREDITCARD_NUMBER, 2, 1, GridBagConstraints.HORIZONTAL, 10);
	        addComponent(getCreditCardText(), 3, 1, 1, 1, GridBagConstraints.HORIZONTAL, 10);

	        // Tercera línea
	        addComponent(MSG_RETIRA_EFECTIVO, 0, 2, GridBagConstraints.HORIZONTAL, 10);
	        addComponent(getRetiraEfectivo(), 1, 2, 1, 1, GridBagConstraints.HORIZONTAL, 10);
	        addComponent(MSG_MONTO, 2, 2, GridBagConstraints.HORIZONTAL, 10);
	        addComponent(getCMontoPosnetText(), 3, 2, 1, 1, GridBagConstraints.HORIZONTAL, 10);
		}
		
		return cPosnetDataPanel;
	}
	
	protected void showQPanel(String msg, String question) {
		showQPanel(msg, null, question);
	}
	
	/**
	 * Acomoda mensaje y pregunta, activa botones correspondientes
	 * @param timer
	 * @param msg 
	 * @param question
	 * dREHER
	 */
	protected void showQPanel(String msg, String msg2, String question) {
		
		getCPosnetDataPanel().setVisible(false);
		
		if(msg2!=null)
			msg += msg2;

		QDescQuestionLabel.setText(msg);
		
		QQuestionLabel.setText(question);
		getCQuestionDataPanel().setVisible(true);
		getCQuestionDataPanel().repaint();
		
	}

	
	
	/**
	 * This method initializes cQuestionDataPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCQuestionDataPanel() {
		if (cQuestionDataPanel == null) {
			
			cQuestionDataPanel = new CPanel();
			cQuestionDataPanel.setBorder(new TitledBorder(null, "ATENCION!", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			cQuestionDataPanel.setPreferredSize(new Dimension(550,175));
			cQuestionDataPanel.setLayout(new BoxLayout(cQuestionDataPanel, BoxLayout.Y_AXIS));
			
			QQuestionLabel = new JLabel(MSG_QUESTION);
			QQuestionLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
			
			QDescQuestionLabel = new JLabel("");
			
	        // Botones de Confirmacion
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			buttonPane.add(QOkButton);
			buttonPane.add(QKoButton);
			
			// Agregar los componentes al panel principal
			cQuestionDataPanel.add(QDescQuestionLabel);
			 // Agregar un espacio vertical de 10 píxeles
			cQuestionDataPanel.add(Box.createRigidArea(new Dimension(0, 10)));
	        
			cQuestionDataPanel.add(QQuestionLabel);
			
			 // Agregar un espacio vertical de 30 píxeles
			cQuestionDataPanel.add(Box.createRigidArea(new Dimension(0, 30)));
			cQuestionDataPanel.add(buttonPane);
	       
		}
		
		return cQuestionDataPanel;
	}
	
	private void addComponent(Component component, int gridx, int gridy, int gridwidth, int gridheight, int fill, int padx) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.fill = fill;
        gbc.insets = new Insets(padx, padx, padx, padx);
        cPosnetDataPanel.add(component, gbc);
    }

    private void addComponent(String label, int gridx, int gridy, int fill, int padx) {
    	addComponent(new JLabel(label), gridx, gridy, 1, 1, fill, padx);
    }
	
	protected String getMsg(String name) {
		return formTPV.getMsg(name);
	}
	
	/**
	 * This method initializes Cupon	
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private CTextField getCCouponNumberText() {
		if (cCouponNumberText == null) {
			cCouponNumberText = new CTextField();
			cCouponNumberText.setText("");
			cCouponNumberText.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			cCouponNumberText.setMandatory(isMandatoryData());
			cCouponNumberText.setBounds(0, 0, 50, 20);
			cCouponNumberText.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					char keyChar = e.getKeyChar();
					String idText = cCouponNumberText.getText();
					
					if(idText != null && idText.length() >= COUPON_ID_MAX_LENGTH){
						e.consume();
					}
					
					if(!Character.isDigit(keyChar)) {
						e.consume();
					}
				}				
			});
			FocusUtils.addFocusHighlight(cCouponNumberText);
		}
		return cCouponNumberText;
	}

	private String Spaces(int i) {
		String espacios = "";
		for(int z=0; z<i; z++)
			espacios = espacios + " ";
		return espacios;
	}

	/**
	 * This method initializes Lote
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private CTextField getCBatchText() {
		if (cBatchText == null) {
			cBatchText = new CTextField();
			cBatchText.setText("");
			cBatchText.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			cBatchText.setMandatory(isMandatoryData());
			cBatchText.setBounds(0, 0, 50, 20);
			cBatchText.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					char keyChar = e.getKeyChar();
					String idText = cBatchText.getText();
					
					if(idText != null && idText.length() >= COUPONBATCH_ID_MAX_LENGTH){
						e.consume();
					}
					
					if(!Character.isDigit(keyChar)) {
						e.consume();
					}
				}				
			});
			FocusUtils.addFocusHighlight(cBatchText);
		}
		return cBatchText;
	}

	private boolean isMandatoryData() {
		return true;
	}

	/**
	 * This method initializes Cuotas	
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private VNumber getCCuotasPosnetText() {
		if (cCuotasPosnetText == null) {
			cCuotasPosnetText = new VNumber();
			cCuotasPosnetText.setDisplayType(DisplayType.Integer);
			cCuotasPosnetText.setValue(numeroCuotas);
			cCuotasPosnetText.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			cCuotasPosnetText.setMandatory(isMandatoryData());
			cCuotasPosnetText.setBounds(0, 0, 50, 20);
			FocusUtils.addFocusHighlight(cCuotasPosnetText);
		}
		return cCuotasPosnetText;
	}
	
	/**
	 * This method initializes Monto Cobrado	
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private VNumber getCMontoPosnetText() {
		if (cMontoPosnetText == null) {
			cMontoPosnetText = new VNumber();
			cMontoPosnetText.setDisplayType(DisplayType.Number);
			cMontoPosnetText.setValue(getMontoACobrar());
			cMontoPosnetText.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			cMontoPosnetText.setMandatory(isMandatoryData());
			cMontoPosnetText.setBounds(0, 0, 50, 20);
			FocusUtils.addFocusHighlight(cMontoPosnetText);
		}
		return cMontoPosnetText;
	}
	
	/**
	 * This method initializes cAddressText1	
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private CTextField getCreditCardText() {
		/*
		 * Puede tener ceros a la izquierda, se convierte a cuadro de texto
		 * dREHER
		if (cCreditCardText == null) {
			cCreditCardText = new VNumber();
			cCreditCardText.setDisplayType(DisplayType.Quantity);
			cCreditCardText.setValue(0);
			cCreditCardText.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			cCreditCardText.setMandatory(isMandatoryData());
			cCreditCardText.setBounds(0, 0, 50, 20);
			FocusUtils.addFocusHighlight(cCreditCardText);
		}
		return cCreditCardText;
		*/
		
		if (cCreditCardText == null) {
			cCreditCardText = new CTextField();
			cCreditCardText.setText("");
			cCreditCardText.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			cCreditCardText.setMandatory(isMandatoryData());
			cCreditCardText.setBounds(0, 0, 50, 20);
			cCreditCardText.setPreferredSize(new Dimension(145, 25));
			cCreditCardText.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					char keyChar = e.getKeyChar();
					String idText = cBatchText.getText();
					
					if(idText != null && idText.length() >= CREDITCARDNUMBER_MAX_LENGTH){
						e.consume();
					}
					
					if(!Character.isDigit(keyChar)) {
						e.consume();
					}
				}				
			});
			FocusUtils.addFocusHighlight(cCreditCardText);
		}
		return cCreditCardText;
		
		
	}

	/**
	 * This method initializes cNameText1	
	 * 	
	 * @return org.compiere.swing.VNumber	
	 */
	private VNumber getRetiraEfectivo() {
		if (cRetiraEfectivo == null) {
			cRetiraEfectivo = new VNumber();
			cRetiraEfectivo.setValue(0.00);
			cRetiraEfectivo.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			cRetiraEfectivo.setMandatory(isMandatoryData());
			cRetiraEfectivo.setBounds(0, 0, 100, 20);
			FocusUtils.addFocusHighlight(cRetiraEfectivo);
		}
		return cRetiraEfectivo;
	}
	
	/****************** FIN de controles para carga manual de informacion *****************/
	
    /**
     * Descripción de Método
     *
     *
     * @param messageType
     */

    private void setInfoIcon( int messageType ) {
        switch( messageType ) {
	        case JOptionPane.ERROR_MESSAGE:
	            iconLabel.setIcon( i_error );
	
	            break;
	        case JOptionPane.INFORMATION_MESSAGE:
	            iconLabel.setIcon( i_inform );
	        	
	
	            break;
	        case JOptionPane.QUESTION_MESSAGE:
	            iconLabel.setIcon( i_question );
	
	            break;
	        case JOptionPane.WARNING_MESSAGE:
	            iconLabel.setIcon( i_warn );
	
	            break;
	        case JOptionPane.PLAIN_MESSAGE:
	        default:
	            break;
        }    // switch
    }        // setInfo
    
    private String quitaHTML(String detailText) {
		String[] html = new String[] {"<html>","<head>", "</head>", "<body>", "</body>","</html>"};
		for(String s: html)
			detailText = detailText.replace(s, "");
		
		return detailText;
	}

	public void addInfoMessage(String  message, boolean error) {
    	String color = "black";
    	
    	String detailText = info.getText();
    	if(detailText!=null)
    		detailText = quitaHTML(detailText);
    	
    	// Se traduce el mensaje.
    	message = Msg.translate(Env.getCtx(), message);
        if(message.equals(""))
        	return;
    	
    	StringBuffer sb = new StringBuffer();
    	sb.append(detailText);

    	if(error) {
    		color = "red";
    		message = "<b>" + message + "</b>";
    		setInfoIcon(JOptionPane.ERROR_MESSAGE);
    		
    	} else {
    		color = "navy";
    		setInfoIcon(JOptionPane.INFORMATION_MESSAGE);
    	}
    	sb.append("<font face='Tahoma' size=3 color=" + color + ">");
    	sb.append(message);
    	sb.append("</font>");

    	if(!detailText.equals(""))
    		sb.append(NL);

    	info.setText(sb.toString());
    	
    	// Se hace un scroll automático hacia el final del panel.
    	SwingUtilities.invokeLater(new Runnable() {
    		public void run() {
    	    	JScrollBar vScroll = info.getVerticalScrollBar();
    	    	if(vScroll!=null)
    	    		vScroll.setValue(vScroll.getMaximum());
    		}
    	});
    } 
	
	public void setPairingCode(final String code) {
		
		Runnable doTrxCloverEnded = new Runnable() {
			public void run() {
				lblPairingCode.setText(code);
				if(code==null || code.isEmpty())
					lblForPairingCode.setText("");
				debug("Seteo codigo de emparejamiento... " + code);				
			}
		};
		invoke(doTrxCloverEnded, false);
	}
	
	public void setMsg(final String msg, final boolean error) {
		
		Runnable doTrxCloverEnded = new Runnable() {
			public void run() {
				addInfoMessage(msg, error);
				debug("Mensaje vtrxPaymentTerminalForm... " + msg);				
			}
		};
		invoke(doTrxCloverEnded, false);
	}
	
	/**
	 * Invoca una acción representada por un Runnable en el hilo de eventos
	 * de Swing.
	 * @param action Accion a invocar.
	 * @param sync Indica si la invoicación se debe hacer sincrónica o asincrónica.
	 * (<code>SwingUtilities.invoikeAndWait</code> o <code>SwingUtilities.invokeLater</code>).
	 */
	protected void invoke(Runnable action, boolean sync) {
		try {
			if(sync)
				SwingUtilities.invokeAndWait(action);
			else
				SwingUtilities.invokeLater(action);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
			debug("Thread Interrupted: " + e.getMessage());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			debug("Invocation error: " + e.getMessage());
		}
	}
	
	private boolean TestConnect(final VTrxPaymentTerminalForm form) {
		return TestConnect(form, false);
	}
	
	private boolean TestConnect(final VTrxPaymentTerminalForm form, final boolean clean) {
		
		final VTrxPaymentTerminalForm form2 = form==null?this:form;
		
    	SwingWorker worker = new SwingWorker() {
    		
    		private boolean isOk = false;
    		
    		@Override
			public Object construct() {
    			
    			updateButtons(false, false, false);
    			
    			clover = new TrxClover();
    			
    			isOk = clover.isOkConnection(form2, getUrl(), getPort(), clean); // Devuelve false por error
    			
    			debug("isConnect=" + isOk);
    			
    			return isOk;
    		}

			@Override
			public void finished() {
    			
    			debug("Resultado= " + getValue());
    			
    			boolean success = (Boolean) getValue();
    			if(success && getTrxClover().getErrorCode() <= 0) {
    				
    				debug("Termino ok! ");
    				formTPV.setContinue(true);
    				formTPV.lastPayment = null;
    				
    				updateButtons(false, false, false);
    				setError(false);
    				setConnect(true);
    				
    				cerrarVentana();
    				
    			}else {
    				addInfoMessage("ERROR: No se puede trabajar en modalidad Online Clover!", true);
    				setError(true);
    				updateButtons(false, false, true);
    				setConnect(false);
    			}
    			
    		}
		
    	};
    	
    	worker.start();
		
    	return isConnect();
	}
	
	public void SaleRequest(final VTrxPaymentTerminalForm form) {
    	
    	if(payamt == null) {
			addInfoMessage("Debe indicar MONTO a cobrar!", true);
    		return;
    	}
    	
    	SwingWorker worker = new SwingWorker() {
    		
    		private boolean isOk = false;
    		
    		@Override
			public Object construct() {
    			
    			updateButtons(false, false, false);
    			
    			clover = new TrxClover();
    			clover.setDefault();
    			
    			clover.setCuotas(getNumeroCuotas());
    			clover.setCashRetirementAmt(getCashRetirementAmt());
    			clover.setPayAmt(getPayamt());
    			clover.setDocumentNo(getDocumentNo());
    			clover.setRetiraEfectivo(isRetiraEfectivo());
    			clover.setMarcaTarjeta(getMarcaTarjeta());
    			clover.setMedioPago(getMedioPago());
    			clover.setNumeroComercio(getNumeroComercio());
    			clover.setPayAmtACobrar(getMontoACobrar());

    			// v 3.0 dREHER guardar log 
    			clover.setSaveLog(form.isTrxLog());
    			
    			isOk = clover.SaleRequest(form, getUrl(), getPort()); // Devuelve false por error
    			
    			debug("isOk=" + isOk);
    			
    			return isOk;
    		}

			@Override
			public void finished() {
    			
    			debug("Resultado de Sales= " + getValue());
    			
    			boolean success = (Boolean) getValue();
    			if(success) {
    				
    				if(getTrxClover().getErrorCode()>=0 || getTrxClover().getPayAmt().compareTo(Env.ZERO)==0) {
    					
    					debug("Termino con Error:" + getTrxClover().getErrorCode());
    					updateButtons(false, false, true);
    					setError(true);
    					addInfoMessage("ERROR: se produjo error al cobrar:", true);
        				addInfoMessage("Codigo:" + getTrxClover().getErrorCode(), true);
    					getTrxClover().setCargaManual(false);
    					formTPV.setContinue(false);
    					
    				}else {

    					debug("Volvio de Clover, verificar importes... ");
     					
    					if(getMontoACobrar().compareTo(getTrxClover().getPayAmt()) != 0 &&
    							getTrxClover().getPayAmt().compareTo(Env.ZERO) > 0) {
    						
    						// v 3.0
    						/*
    						debug("Los importes NO coinciden, se pide confirmacion manual de datos...");
    						
    						getCQuestionDataPanel().setVisible(false);
    						getCQuestionDataPanel().repaint();
    						
    						addInfoMessage("Los importes NO coinciden, se pide confirmacion manual de datos...", true);
    						
    						HabilitaCargaManual();
    						
    						updateButtons(true, false, false);
    						*/
    						
    						debug("A Cobrar:" + getMontoACobrar() + " Cobrado en Clover:" + getTrxClover().getPayAmt());
    						
    						addInfoMessage("Los datos enviados a Clover NO parecen coincidir con los recibidos desde Clover.", true);
    						addInfoMessage("Verifique la tarjeta e importe, de ser necesario ingrese informacion manualmente.", true);
    						
    						// v 3.0
    						TestConnect(null, true);
    						
    						QACTION="TRX_OK";
    						
    					}else {

    						formTPV.setContinue(true);
    						formTPV.lastPayment = getPayment();

    						// v 2.0
    						// updateButtons(true, true, false);
    						updateButtons(true, false, false);
    						setError(false);

    						getTrxClover().setCargaManual(false);

    						// Comparar que lo utilizado en Clover con lo indicado en TPV
    						// En caso de que no coincida enviamos anulacion...
    						
    						// v 3.0 verificar monto retiro efectivo
    						if(!formTPV.isSameDataTrxClover(form, getMontoACobrar(), 
    								getMarcaTarjeta(), getTrxClover().getCashRetirementAmt(), false)) {

    							// Permitir anular
    							updateButtons(false, true, false);

    							addInfoMessage("ERROR: No coincide informacion TPV vs Clover, DEBE ANULAR cobro!", true);
    						}

    					}

    				}
    				
    			}else {
    				
    				// Cuando vuelve con algun error, de todas maneras cargar los campos manuales con
    				// los valores de Clover si es que NO son nulos...
    				/* v 2.0
    				if(getTrxClover().getCardNumber()!=null && !getTrxClover().getCardNumber().isEmpty())
    					getCreditCardText().setValue(getTrxClover().getCardNumber());
    				if(getTrxClover().getCouponNumber()!=null && !getTrxClover().getCouponNumber().isEmpty())
    					getCCouponNumberText().setValue(getTrxClover().getCouponNumber());
    				if(getTrxClover().getCouponBatchNumber()!=null && !getTrxClover().getCouponBatchNumber().isEmpty())
    					getCBatchText().setValue(getTrxClover().getCouponBatchNumber());
    				if(getTrxClover().getCashRetirementAmt()!=null && getTrxClover().getCashRetirementAmt().compareTo(BigDecimal.ZERO) > 0)
    					getRetiraEfectivo().setValue(getTrxClover().getCashRetirementAmt());
    				;
    				addInfoMessage("ERROR: se produjo error al cobrar:", true);
    				addInfoMessage("Codigo:" + getTrxClover().getErrorCode(), true);
    				setError(true);
    				updateButtons(true, false, true);
    				getCPosnetDataPanel().setVisible(true);
    				*/
    				
    				QACTION="TRX_OK";
    				
    				
    			}
    			
    		}
		
    	};
    	
    	worker.start();
    }
	
	public void refundSale(final VTrxPaymentTerminalForm form, final boolean isFromThis) {
		
    	if(getPayment() == null) {
			addInfoMessage("Debe indicar el cobro para Anular!", true);
			closeButton.setVisible(true);
    		return;
    	}
    	
    	if(getTrxClover()==null) {
    		addInfoMessage("No se encontro cobro para Anular!", true);
    		closeButton.setVisible(true);
    		return;
    	}
    	
    	SwingWorker worker = new SwingWorker() {
    		
    		private boolean isOk = false;
    		
    		@Override
			public Object construct() {
    			
    			updateButtons(false, false, false);
    			
    			String card = getTrxClover().getCard();
    			String cardNumber = getTrxClover().getCardNumber();
    			String cardType = getTrxClover().getCardType();
    			String couponBatchNumber = getTrxClover().getCouponBatchNumber();
    			String couponNumber = getTrxClover().getCouponNumber();
    			String referenceID = getTrxClover().getReferenceID();
    			String transactionNumber = getTrxClover().getTransactionNumber();
    			String numeroComercio = getTrxClover().getNumeroComercio();
    			
    			clover = new TrxClover();
    			clover.setDefault();

    			// Si se anula desde el propio formulario, NO limpiar campos...
    			if(isFromThis) {
    				clover.setCuotas(getNumeroCuotas());
    				clover.setCashRetirementAmt(getCashRetirementAmt());
    				clover.setPayAmt(getPayamt());
    				clover.setDocumentNo(getDocumentNo());
    				clover.setRetiraEfectivo(isRetiraEfectivo());
    				clover.setMarcaTarjeta(getMarcaTarjeta());
    				clover.setMedioPago(getMedioPago());
    				clover.setNumeroComercio(getNumeroComercio());
    				
    				clover.setCard(card);
    				clover.setCardNumber(cardNumber);
    				clover.setCardType(cardType);
    				clover.setCouponBatchNumber(couponBatchNumber);
    				clover.setCouponNumber(couponNumber);
    				clover.setReferenceID(referenceID);
    				clover.setTransactionNumber(transactionNumber);
    				clover.setNumeroComercio(numeroComercio);
    				
        			// v 3.0 dREHER guardar log 
        			clover.setSaveLog(form.isTrxLog());
    				
    			}else {
    				clover.setMarcaTarjeta(getMarcaTarjeta());
    				clover.setMedioPago(getMedioPago());
    			}
    			
    			isOk = clover.RefundSaleRequest(form, getUrl(), getPort(), getPayment()); // Devuelve false por error
    			
    			debug("isOk=" + isOk);
    			
    			return isOk;
    		}
    		
    		@Override
			public void finished() {
    			
    			debug("Resultado= " + getValue());
    			
    			boolean success = (Boolean) getValue();
    			if(success) {
    				
    				debug("Termino anular cobro ok! ");
    				
    				formTPV.setContinue(false);
    				formTPV.lastPayment = null;
    				
    				updateButtons(false, false, true);
    				
    				setError(true);
    				
    				addInfoMessage("ERROR: anulo cobro!", true);
    				
    			}else {
    				addInfoMessage("ERROR: se produjo error al anular cobro:", true);
    				addInfoMessage("Codigo:" + getTrxClover().getErrorCode(), true);
    				setError(false);
    				formTPV.lastPayment = getPayment();
    				updateButtons(false, false, true);
    				formTPV.setContinue(true);
    			}
    			
    		}
		
    	};
    	
    	worker.start();
		
	}
	
	protected String getPort() {
		return getPosConfig().getPortClover();
	}

	protected String getUrl() {
		return getPosConfig().getIpClover();	}

	
	protected void debug(String string) {
		System.out.println("VTrxPaymentTerminalForm. " + string);
	}
	
	/**
	 * Setea la visualizacion de los botones segun corresponda
	 * @param ok
	 * @param cancel
	 * @param close
	 */
	private void updateButtons(boolean ok, boolean cancel, boolean close) {
		okButton.setVisible(ok);
		cancelButton.setVisible(cancel);
		closeButton.setVisible(close);
		
		// v 2.0
		// showAbortButton();
		
		// v 2.0
		if(okButton.isVisible() || cancelButton.isVisible() || closeButton.isVisible()) {
			getCQuestionDataPanel().setVisible(false);
			getCQuestionDataPanel().repaint();
		}
		
	}
	
	private void showAbortButton() {
		abortButton.setVisible(true);
		
		// Si se ve el boton confirmar o anular, NO deja abortar
		if(okButton.isVisible() || cancelButton.isVisible() || !isShowCancelButton)
			abortButton.setVisible(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if("OK".equals(e.getActionCommand())) {
			
			OK();
			
		} else if("KO".equals(e.getActionCommand())) { // por ahora esto no tiene funcionalidad
	
			KO();
			
		} else if("CERRAR".equals(e.getActionCommand())) {
	
			CLOSE();
			
		} else if("ANULAR".equals(e.getActionCommand())) {
	
			ANULA();
			
		} else if("ABORTAR".equals(e.getActionCommand())) {
			
			ABORTAR();
			
		} else if("QOk".equals(e.getActionCommand())) { // v 2.0 Respondio SI a la pregunta
			
			if(QACTION.equals("TIMER")) {
				
				debug("Se corta timer a pedido del usuario...");
				timer.cancel();
				
				QACTION = "TRX_OK";
				showQPanel("<html>No se recibio respuesta. INTENTE CONECTAR EL DISPOSITIVO CLOVER!<br><br><b>***De ser necesario CANCELE la transaccion actual en Clover</b></html>", 
						"La transaccion fue registrada en el Clover?");
				
			}else if(QACTION.equals("TRX_OK")) {
				
				debug("El usuario respondio que la transaccion se registro Ok en Clover, debe mostrar formulario manual!");
				getCQuestionDataPanel().setVisible(false);
				getCQuestionDataPanel().repaint();
				
				HabilitaCargaManual();
				
			}
			
		} else if("QKo".equals(e.getActionCommand())) { // v 2.0 Respondio NO a la pregunta
			
			if(QACTION.equals("TIMER")) {

				debug("Sigue esperando a pedido del usuario...");
				
				getCQuestionDataPanel().setVisible(false);
				getCQuestionDataPanel().repaint();
				
				// Volver a inicializar el Timer con la misma tarea
				initTimer();
				
			}else if(QACTION.equals("TRX_OK")) {
				
				debug("El usuario respondio que la transaccion NO se registro Ok en Clover, salir!");
				getCQuestionDataPanel().setVisible(false);
				getCQuestionDataPanel().repaint();
				
				setError(true);
				addInfoMessage("ERROR: se produjo error al cobrar:", true);
				addInfoMessage("Codigo:" + getTrxClover().getErrorCode(), true);
				getTrxClover().setCargaManual(false);
				formTPV.setContinue(false);
				
				// Salio de Clover y respondio que la transaccion NO se registro, limpiar Clover
				// TestConnect(this, true);
				
				cerrarVentana();
				
			}
		}
	}
	
	/**
	 * Se habilita la carga manual de datos
	 * dREHER v 2.0
	 */
	public void HabilitaCargaManual() {
		// Cuando vuelve con algun error, de todas maneras cargar los campos manuales con
		// los valores de Clover si es que NO son nulos...
		
		if(getTrxClover().getCardNumber()!=null && !getTrxClover().getCardNumber().isEmpty())
			getCreditCardText().setValue(getTrxClover().getCardNumber());
		if(getTrxClover().getCouponNumber()!=null && !getTrxClover().getCouponNumber().isEmpty())
			getCCouponNumberText().setValue(getTrxClover().getCouponNumber());
		if(getTrxClover().getCouponBatchNumber()!=null && !getTrxClover().getCouponBatchNumber().isEmpty())
			getCBatchText().setValue(getTrxClover().getCouponBatchNumber());
		if(getTrxClover().getCashRetirementAmt()!=null && getTrxClover().getCashRetirementAmt().compareTo(BigDecimal.ZERO) > 0)
			getRetiraEfectivo().setValue(getTrxClover().getCashRetirementAmt());
		;
		
		// v 2.0
		if(getTrxClover().getErrorCode() >= 0) {
			addInfoMessage("ERROR: se produjo error al cobrar:", true);
			addInfoMessage("Codigo:" + getTrxClover().getErrorCode(), true);
		}

		addInfoMessage("El sistema NO recibio la respuesta desde Clover.", false);
		addInfoMessage("Por favor ingrese manualmente los datos de la TRX registrados en Clover.", false);
		addInfoMessage("IMPORTANTE. Los datos ingresados debajo, deberan coincidir exactamente", false);
		addInfoMessage("con los registrados en TRX Clover.", false);
		
		setError(true);
		updateButtons(true, false, true);
		getCPosnetDataPanel().setVisible(true);
	}

	// Anula pago
	private void ANULA() {
		refundSale(this, true);
		
		// v 3.0 si anula no controlar el retiro de efectivo
		if(!formTPV.isSameDataTrxClover(this, getMontoACobrar(), getMarcaTarjeta(), 
				getCashRetirementAmt(), true)) 
			formTPV.setContinue(false);
		
	}

	// Cerrar dialogo
	private void CLOSE() {
		// formTPV.setContinue(false);
		// Se cierra la ventana
		cerrarVentana();
	}

	// Cancelar
	private void KO() {
		formTPV.setContinue(false);
		// Se cierra la ventana
		cerrarVentana();
	}

	private void ABORTAR() {
		
		// Cuando vuelve con algun error, de todas maneras cargar los campos manuales con
		// los valores de Clover si es que NO son nulos...
		
		if(getTrxClover().getCardNumber()!=null && !getTrxClover().getCardNumber().isEmpty())
			getCreditCardText().setValue(getTrxClover().getCardNumber());
		if(getTrxClover().getCouponNumber()!=null && !getTrxClover().getCouponNumber().isEmpty())
			getCCouponNumberText().setValue(getTrxClover().getCouponNumber());
		if(getTrxClover().getCouponBatchNumber()!=null && !getTrxClover().getCouponBatchNumber().isEmpty())
			getCBatchText().setValue(getTrxClover().getCouponBatchNumber());
		if(getTrxClover().getCashRetirementAmt()!=null && getTrxClover().getCashRetirementAmt().compareTo(BigDecimal.ZERO) > 0)
			getRetiraEfectivo().setValue(getTrxClover().getCashRetirementAmt());
		;
		addInfoMessage("Se aborto comunicacion...", true);
		addInfoMessage("Codigo:" + getTrxClover().getErrorCode(), true);
		setError(true);
		updateButtons(true, false, true);
		getCPosnetDataPanel().setVisible(true);
		abortButton.setVisible(false);
		
	}
	
	// Confirma Ok
	private void OK() {
		/**
		 * Si estan visibles los campos de carga manual, quiere decir que ocurrio un error en Clover
		 * por lo tanto se debe completar la carga manual y verificarse como siempre en TPV
		 * Solo deja continuar si se completan los datos correctamente
		 * 
		 * dREHER
		 */
		boolean isCerrarOk = true;
		if(getCPosnetDataPanel().isVisible()) {
			isCerrarOk = ValidaDataCompleta();
		}
		
		if(isCerrarOk) {

			// Setear la info que deberia haber venido desde Clover segun el
			// contenido que se registro manualmente en el formulario
			if(getCPosnetDataPanel().isVisible()) {
				getTrxClover().setCardNumber(getCreditCardText().getText());
				getTrxClover().setCouponNumber(getCCouponNumberText().getText());
				getTrxClover().setCouponBatchNumber(getCBatchText().getText());
				if(getRetiraEfectivo().getValue()!=null && ((BigDecimal)getRetiraEfectivo().getValue()).compareTo(BigDecimal.ZERO) > 0)
					getTrxClover().setCashRetirementAmt((BigDecimal)getRetiraEfectivo().getValue());
				getTrxClover().setCargaManual(true);
				
				// v 2.0
				getTrxClover().setPayAmt((BigDecimal)this.getCMontoPosnetText().getValue());
			}else
				getTrxClover().setCargaManual(false);

			/** En el caso de la carga manual tomamos como valido lo que hay en el TPV
			BigDecimal montoCobrado = trxClover.getPayAmt();
			String marcaTarjetaClover = trxClover.getCard(); // Card=V1 Cardtype=VISA
			*/
			
			formTPV.setContinue(true);
			setError(false);
			
			// Se cierra la ventana
			cerrarVentana();
			
		}else {
			debug("No se completaron los datos minimos para continuar con la venta!");
			addInfoMessage("ATENCION: Debe completar la informacion solicitada!", true);
		}
	}
	
	// Completa con ceros a la izquierda
	private String fillWithZeros(String valueOf, int width) {
		int l = valueOf.length();
		if(l >= width)
			return valueOf;
		
		int zerosToAdd = width - l;
        String wz = "";
        for (int i = 0; i < zerosToAdd; i++) {
            wz += "0";
        }
		
		return wz + valueOf;
	}

	/**
	 * Desactiva teclas y cierra formulario de TrxClover
	 * dREHER
	 */
	private void cerrarVentana() {
        setActionEnabled("OK", false);
        setActionEnabled("KO", false);
        setActionEnabled("CERRAR", false);
        setActionEnabled("ANULAR", false);
        
        this.setVisible(false);
		
        dispose();
	}

	/**
	 * Valida que los campos de 
	 * Lote, Cupon, Cuotas y Numero Tarjeta esten correctamente completados
	 * @return true-> completo toda la info OK, false-> falta completar info manual
	 */
	private boolean ValidaDataCompleta() {
		boolean isOk = true;
		
		if(getCreditCardText().getValue()==null || 
				getCreditCardText().getText().isEmpty() || 
				getCreditCardText().getText().length() < 4) {
			addInfoMessage("Debe completar el numero de tarjeta! (minimo ult. 4 digitos)", true);
			return false;
		}
		if(getCBatchText().getValue()==null || getCBatchText().getText().isEmpty()) {
			addInfoMessage("Debe completar el numero de lote!", true);
			return false;
		}
		if(getCCouponNumberText().getValue()==null || getCCouponNumberText().getText().isEmpty()) {
			addInfoMessage("Debe completar el numero de cupon!", true);
			return false;
		}
		if(getCCuotasPosnetText().getValue()==null || ((Integer)getCCuotasPosnetText().getValue()) < 1) {
			addInfoMessage("Debe completar la cantidad de cuotas!", true);
			return false;
		}
		// v2.0
		if( ((BigDecimal)getCMontoPosnetText().getValue()).compareTo(Env.ZERO) <= 0) {
			addInfoMessage("Debe completar el monto cobrado!", true);
			return false;
		}
		
		return isOk;
	}

	/**
	 * Recuperan y guardan info en MPOS asociado al TPV
	 * @param property
	 * @return
	 * 
	 * @author dREHER
	 */
	public String getProperty(String property) {
		return formTPV.getProperty(property);
	}

	public void setProperty(String property, String data) {
		formTPV.setProperty(property, data);
	}
	
	
	private void setInfoMsg( String message ) {
	        message = Msg.translate(Env.getCtx(), message);
	    	StringBuffer sb = new StringBuffer();
	    	
	    	/*
	    	if(message.indexOf("\n") == -1) {
	    		sb.append( NL );
	    	}
	    	*/
	    	
	    	String detailText = info.getText();
	    	if(detailText!=null)
	    		detailText = quitaHTML(detailText);
	    	
	    	sb.append(detailText);
	    	
	        sb.append( "<b>" );

	        char[]  chars = message.toCharArray();
	        boolean first = true;
	        int     paras = 0;

	        for( int i = 0;i < chars.length;i++ ) {
	            char c = chars[ i ];

	            if( c == '\n' ) {
	                if( first ) {
	                    sb.append( "</b>" );
	                    first = false;
	                }

	                if( paras > 1 ) {
	                    sb.append( NL );
	                } else {
	                    sb.append( NL );
	                }

	                paras++;
	            } else {
	                sb.append( c );
	            }
	        }

	        info.setText(sb.toString() + "<br>");
	        debug("Agrego " + sb.toString());

	        Dimension size = info.getPreferredSize();

	        size.width  = 550;
	        size.height = ( Math.max( paras,message.length() / 60 ) + 1 ) * 30;
	        size.height = Math.min( size.height + 40,600 );
	        size.height = 70;
	        
	        info.setPreferredSize( size );

	        // Log.print("Para=" + paras + " - " + info.getPreferredSize());

	        info.setRequestFocusEnabled( false );
	        info.setReadWrite( false );
	        info.setOpaque( false );
	        info.setBorder( null );

	        //

	        info.setCaretPosition( 0 );
	}
	
	/**
	 * @return Returns the Clover.
	 */
	public TrxClover getTrxClover() {
		return clover;
	}

	/**
	 * @param clover set.
	 */
	public void setTrxClover(TrxClover clover) {
		this.clover = clover;
	}
	
	public int getNumeroCuotas() {
		return numeroCuotas;
	}

	public void setNumeroCuotas(int numeroCuotas) {
		this.numeroCuotas = numeroCuotas;
	}

	public String getDocumentNo() {
		return documentNo;
	}

	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
	}

	public String getPlan() {
		return plan;
	}

	public void setPlan(String plan) {
		this.plan = plan;
	}

	public BigDecimal getCashRetirementAmt() {
		return cashRetirementAmt;
	}

	public void setCashRetirementAmt(BigDecimal cashRetirementAmt) {
		this.cashRetirementAmt = cashRetirementAmt;
	}
	
    public BigDecimal getPayamt() {
		return payamt;
	}

	public void setPayamt(BigDecimal payamt) {
		this.payamt = payamt;
	}

	public boolean isRetiraEfectivo() {
		return isRetiraEfectivo;
	}

	public void setRetiraEfectivo(boolean isRetiraEfectivo) {
		this.isRetiraEfectivo = isRetiraEfectivo;
	}

	public boolean isError() {
		return isError;
	}

	public void setError(boolean isError) {
		this.isError = isError;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}
	
	public PoSMainForm getPoSMainForm() {
		return formTPV;
	}
	
	public PoSConfig getPosConfig() {
		
		if(formTPV!=null)
			return formTPV.getModel().getConfig();
		
		return null;
	}

	public boolean isConnect() {
		return isConnect;
	}

	public void setConnect(boolean isConnect) {
		this.isConnect = isConnect;
	}

	public String getMarcaTarjeta() {
		return marcaTarjeta;
	}

	public void setMarcaTarjeta(String marcaTarjeta) {
		this.marcaTarjeta = marcaTarjeta;
	}

	public String getMedioPago() {
		return MedioPago;
	}

	public void setMedioPago(String medioPago) {
		MedioPago = medioPago;
	}

	public BigDecimal getMontoACobrar() {
		return montoACobrar;
	}

	public void setMontoACobrar(BigDecimal montoACobrar) {
		this.montoACobrar = montoACobrar;
	}

	public String getNumeroComercio() {
		return NumeroComercio;
	}

	public void setNumeroComercio(String numeroComercio) {
		NumeroComercio = numeroComercio;
	}
	
	public BigDecimal getCashARetirementAmt() {
		return cashARetirementAmt;
	}

	public void setCashARetirementAmt(BigDecimal cashARetirementAmt) {
		this.cashARetirementAmt = cashARetirementAmt;
	}

}
