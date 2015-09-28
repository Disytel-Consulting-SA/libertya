package org.openXpertya.apps.form;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigDecimal;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.Utilities;

import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTextField;
import org.openXpertya.OpenXpertya;
import org.openXpertya.apps.AEnv;
import org.openXpertya.model.MProduct;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class VProductPricePoll extends FormFrame  {

	// Labels
	CLabel valueLabel = new CLabel("CODIGO");
	CLabel upcLabel = new CLabel("UPC");
	CLabel nameLabel = new CLabel("ARTICULO");
	CLabel priceLabel = new CLabel("PRECIO");
	
	// Campos
	CTextField upcField = new CTextField();
	CTextField valueField = new CTextField(); 
	JTextPane nameField = new JTextPane();
	CTextField priceField = new CTextField();
	
	//dimensiones
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	protected static final int labelHeight = 30;
	protected static final int fieldHeight = 100;
	protected static final int fontSizeLabel = 14;
	protected static final int fontSizeFieldSmall = 24;
	protected static final int fontSizeFieldLarge = 48;
	protected static final int fontSizeFieldGiant = 96;
	
	// Flag para almacenar el estado de finalización de ingreso del codigo ingreado (en función del <enter> enviado por el lector)
	protected boolean entered = false;
	
	// Se esta mostrando información (precio, error, etc.?)
	protected boolean displaying = false;
	
	// Thread para el vaciado de datos del display
	DisplayTimer timer = null;
	
	/** Omision de argumento Version Lista de precio */
	public static String SKIP_PLV_ARGUMENT = "-1";
	
	protected static Color backColor = new Color(150, 0, 100);
	protected static Color nameColor = Color.WHITE;
	protected static Color priceColor = Color.YELLOW;
	
	public VProductPricePoll() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Consulta de precio");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
//		setUndecorated(true);
		setPreferredSize(screenSize);
		
		loadComponents();
		startTimer();
		
		setJMenuBar(null);
		AEnv.showCenterScreen(this);
	}

	
	protected void loadComponents() {
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		// UPC y Value LABELs
		CPanel upcValueLabelPanel = new CPanel();
		upcValueLabelPanel.setLayout(new BoxLayout(upcValueLabelPanel, BoxLayout.X_AXIS));
		upcValueLabelPanel.setPreferredSize(new Dimension(screenSize.width, labelHeight));
		upcValueLabelPanel.setMaximumSize(new Dimension(screenSize.width, labelHeight));
		upcLabel.setPreferredSize(new Dimension(screenSize.width / 2, labelHeight));
		valueLabel.setPreferredSize(new Dimension(screenSize.width / 2, labelHeight));
		upcLabel.setHorizontalAlignment(SwingConstants.LEFT);
		valueLabel.setHorizontalAlignment(SwingConstants.LEFT);
		upcValueLabelPanel.add(upcLabel);
		upcValueLabelPanel.add(valueLabel);
		getContentPane().add(upcValueLabelPanel);
		
		// UPC y Value FIELDs
		CPanel upcValuePanel = new CPanel();
		upcValuePanel.setLayout(new BoxLayout(upcValuePanel, BoxLayout.X_AXIS));
		upcValuePanel.setPreferredSize(new Dimension(screenSize.width, fieldHeight));
		upcValuePanel.setMaximumSize(new Dimension(screenSize.width, fieldHeight));
		upcField.setPreferredSize(new Dimension(screenSize.width / 2, labelHeight));
		valueField.setPreferredSize(new Dimension(screenSize.width / 2, labelHeight));
		upcField.setHorizontalAlignment(SwingConstants.LEFT);
		valueField.setHorizontalAlignment(SwingConstants.LEFT);
		upcValuePanel.add(upcField);
		upcValuePanel.add(valueField);
		getContentPane().add(upcValuePanel);
		
		// Name LABEL
		CPanel namePanel = new CPanel();
		namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
		namePanel.setPreferredSize(new Dimension(screenSize.width, labelHeight));
		namePanel.setMaximumSize(new Dimension(screenSize.width, labelHeight));
		nameLabel.setPreferredSize(new Dimension(screenSize.width, labelHeight));
		namePanel.add(nameLabel);
		getContentPane().add(namePanel);
		
		// Name FIELD
		nameField.setPreferredSize(new Dimension(screenSize.width, screenSize.height / 3));
		getContentPane().add(nameField);
		
		// Price LABEL
		CPanel pricePanel = new CPanel();
		pricePanel.setLayout(new BoxLayout(pricePanel, BoxLayout.X_AXIS));
		pricePanel.setPreferredSize(new Dimension(screenSize.width, labelHeight));
		pricePanel.setMaximumSize(new Dimension(screenSize.width, labelHeight));
		priceLabel.setPreferredSize(new Dimension(screenSize.width, labelHeight));
		pricePanel.add(priceLabel);
		getContentPane().add(pricePanel);
						
		// Price FIELD		
		priceField.setPreferredSize(new Dimension(screenSize.width, screenSize.height / 4));
		getContentPane().add(priceField);

		// Tamaño tipografías LABELs
		upcLabel.setFont(new Font(nameLabel.getName(), Font.BOLD, fontSizeLabel));
		valueLabel.setFont(new Font(nameLabel.getName(), Font.BOLD, fontSizeLabel));
		nameLabel.setFont(new Font(nameLabel.getName(), Font.BOLD, fontSizeLabel));
		priceLabel.setFont(new Font(nameLabel.getName(), Font.BOLD, fontSizeLabel));

		// Tamaño tipografías FIELDs
		upcField.setFont(new Font(nameField.getName(), Font.BOLD, fontSizeFieldSmall));
		valueField.setFont(new Font(nameField.getName(), Font.BOLD, fontSizeFieldSmall));
		nameField.setFont(new Font(nameField.getName(), Font.BOLD, fontSizeFieldLarge));
		priceField.setFont(new Font(nameField.getName(), Font.BOLD, fontSizeFieldGiant));

		// Colores componentes
		upcField.setBackground(backColor);
		valueField.setBackground(backColor);
		nameField.setBackground(backColor);
		priceField.setBackground(backColor);
		
		upcField.setForeground(nameColor);
		valueField.setForeground(nameColor);
		nameField.setForeground(nameColor);
		priceField.setForeground(priceColor);
		
		// Alineacion
		upcLabel.setHorizontalAlignment(JTextField.CENTER);
		valueLabel.setHorizontalAlignment(JTextField.CENTER);
		nameLabel.setHorizontalAlignment(JTextField.CENTER);
		priceLabel.setHorizontalAlignment(JTextField.CENTER);

		upcField.setHorizontalAlignment(JTextField.CENTER);
		valueField.setHorizontalAlignment(JTextField.CENTER);
		priceField.setHorizontalAlignment(JTextField.CENTER);
		
		StyledDocument doc = nameField.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		
		// Configuraciones adicionales 
		upcField.setCaretColor(backColor);
		upcField.setBorder(null);
		valueField.setEditable(false);
		valueField.setBorder(null);
		nameField.setEditable(false);
		priceField.setEditable(false);
		
		// Cargar los componentes
		resetDisplay(true);
		
		// 
		upcField.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// Se completó la lectura del código?
				if (KeyEvent.VK_ENTER == e.getKeyCode()) {
					getPrice();
					entered = true;
				} else {
					// Si se ingresó una parte del código, discriminar si es el primero o no a fin de vaciar el campo
					if (entered) {
						entered = false;
						upcField.setText("");
					}
				}
			}
		});
		
		upcField.requestFocus();
	}
	
	
	
	protected void getPrice() {
		
		// UPC correctamente indicado?
		if (upcField.getText() == null || upcField.getText().length() == 0) {
			return;
		}

		// Renovar el plazo de visualización X segundos
		displaying = true;
		timer.resetCounter();
	
		// Recuperar articulo y precio
		int productID = DB.getSQLValue(null, "SELECT m_product_id FROM m_productupc where upc = ?", upcField.getText());
		
		if (upcField == null || upcField.getText() == null || upcField.getText().length() == 0 || productID == -1) {
			valueField.setText("");
			setTextNameField("ARTICULO INEXISTENTE");
			priceField.setText("");
			return;
		}

		// Datos artículo
		MProduct aProduct = new MProduct(Env.getCtx(), productID, null);
		valueField.setText(aProduct.getValue());
		setTextNameField(aProduct.getName());
		
		// Precio
		BigDecimal price = 
				DB.getSQLValueBD(null, 	" SELECT pricestd " +
										" FROM m_productprice " +
										" WHERE m_product_id = " + productID + 
										" AND m_pricelist_version_id = ? ", Env.getContextAsInt(Env.getCtx(), "#M_PriceList_Version_ID"));

		if (price == null) {
			priceField.setText("CONSULTE PRECIO EN LINEA DE CAJA");
			return;
		}
		
		priceField.setText("$" + price.setScale(2, BigDecimal.ROUND_HALF_EVEN).toString());

	}
	
	// Inicia el contador 
	protected void startTimer() {
		timer = new DisplayTimer(this);
		Thread thread = new Thread(timer);
		thread.start();
	}
	
	// Vacia la informacion visualizada
	public void resetDisplay(boolean cleanUPC) {
		if (cleanUPC)
			upcField.setText("");
		valueField.setText(""); 
		setTextNameField("CONSULTA DE PRECIO\nPASE EL ARTICULO POR EL LECTOR");
		priceField.setText("");
		displaying = false;
	}
	
	// Seteo del campo name
	protected void setTextNameField(String text) {
		nameField.setText((getNameFieldRowCount() <= 2 ? "\n" : "") + text);
	}
	
	// Workaround basico para lograr al menos un centrado vertical rudimentario en el campo name, 
	// suponiendo resoluciones de visualización en pantallas tradicionales.
	protected int getNameFieldRowCount() {
		int totalCharacters = nameField.getText().length(); 
		int lineCount = (totalCharacters == 0) ? 1 : 0;

		try {
		   int offset = totalCharacters; 
		   while (offset > 0) {
		      offset = Utilities.getRowStart(nameField, offset) - 1;
		      lineCount++;
		   }
		   return lineCount;
		} catch (BadLocationException e) {
		    e.printStackTrace();
		    return lineCount;
		} 
	}
	
	
	public static class DisplayTimer implements Runnable {

		/** Cantidad de segundos a visualizar */
		public static int DISPLAY_TIME_SECONDS = 10;
		// Owner del timer
		VProductPricePoll handler;
		// Cantidad de segundos pasados
		int currentCount = 0;
		
		public DisplayTimer(VProductPricePoll handler) {
			this.handler = handler;
		}
		
		@Override
		public void run() {
			while (true) {
				if (currentCount >= DISPLAY_TIME_SECONDS) {
					currentCount = 0;
					if (handler.displaying)
						// Originalmente en false. De esta manera, no se vaciaba el UPC dado que justo podría estar cargandose los números del nuevo UPC a consultar
						// Sin embargo, como es con lector de codigo de barras, ésto no debería presentar retardo alguno.
						handler.resetDisplay(true);
				} else { 
					try {
						Thread.sleep(1000);
						currentCount++;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		}
		
		public void resetCounter() {
			currentCount = 0;
		}
		
	}
	

	public static void main(String[] args)
	{
		System.out.println(	"Iniciando...  Argumentos soportados: \n" +
							" 				1) M_PriceList_Version_ID. Opcional si no se especifican más argumentos.  Se puede omitir indicando " + SKIP_PLV_ARGUMENT + " si es necesario indicar demas argumentos. Si no se especifica o se indica " + SKIP_PLV_ARGUMENT + " determina la organización segun configuración de replicación. \n" +
							" 				2) Duración de display.  Por defecto " + DisplayTimer.DISPLAY_TIME_SECONDS + " segundos. Opcional si no se especifican más argumentos. \n " + 
							" 				3) Color de fondo en formato RRGGBB hexadecimal. Opcional si no se especifican más argumentos. \n " + 
							" 				4) Color de nombre de artículo en formato RRGGBB hexadecimal. Opcional si no se especifican más argumentos. \n " +
							" 				5) Color de precio en formato RRGGBB hexadecimal. Opcional si no se especifican más argumentos. \n  " +
							" 				Ejemplo para omitir PLV, indicar 5 segundos y configurar colores: VProductPricePoll -1 5 000000 15FF15 2050FF "); 
		
	  	String oxpHomeDir = System.getenv("OXP_HOME"); 
	  	if (oxpHomeDir == null) {
	  		System.err.println("ERROR: La variable de entorno OXP_HOME no está seteada ");
	  		System.exit(1);
	  	}
	  	// Cargar el entorno basico
	  	System.setProperty("OXP_HOME", oxpHomeDir);
	  	if (!OpenXpertya.startupEnvironment( false )){
	  		System.err.println("ERROR: Error al iniciar la configuracion (postgres esta corriendo?) ");
	  		System.exit(1);
	  	}
	  	
	  	// Configuracion de compañía y organización.  Por defecto se toma a partir de la configuración de replicacion.
	  	int clientId = DB.getSQLValue(null, " SELECT AD_Client_ID FROM AD_ReplicationHost WHERE thisHost = 'Y' ");
	  	int orgId = DB.getSQLValue(null, " SELECT AD_Org_ID FROM AD_ReplicationHost WHERE thisHost = 'Y' ");
	  	int priceListVersionID = -1;

	  	// Si se recibe la version de la lista de precio por argumento, se redefine la compañía y organización a partir de la version 
	  	if (args.length > 0 && args[0] != null && args[0].length() > 0 && !SKIP_PLV_ARGUMENT.equals(args[0])) {
	  		priceListVersionID = Integer.parseInt(args[0]);
	  		clientId = DB.getSQLValue(null, "SELECT AD_Client_ID FROM M_PriceList_Version WHERE M_PriceList_Version_ID = " + priceListVersionID);
	  		orgId = DB.getSQLValue(null, "SELECT AD_Org_ID FROM M_PriceList_Version WHERE M_PriceList_Version_ID = " + priceListVersionID);
	  	} else {
	  		System.out.println("Determinando la version de lista de precio a utilizar a partir de la organizacion (basado en configuración de replicación).");
	  		
		  	// Si no se recibe la versión de lista de precio, se toma la PLV de venta para la organización en cuestión   
	  		priceListVersionID = DB.getSQLValue(null, " SELECT m_pricelist_version_id " +
		  						 						" FROM m_pricelist_version plv " +
		  						 						" INNER JOIN m_pricelist pl ON plv.m_pricelist_id = pl.m_pricelist_id " +
		  						 						" WHERE pl.issopricelist = 'Y' " +
		  						 						" AND plv.ad_org_id = ?" +
		  						 						" ORDER BY plv.UPDATED desc " +
		  						 						" LIMIT 1", orgId);	
	  	}
	  	
	  	// Setear valores en contexto 
	  	Env.setContext(Env.getCtx(), "#AD_Client_ID", clientId);
	  	Env.setContext(Env.getCtx(), "#AD_Org_ID", orgId);
	  	Env.setContext(Env.getCtx(), "#AD_Language", "es_AR");
	  	Env.setContext(Env.getCtx(), "#M_PriceList_Version_ID", priceListVersionID);
	  	
	  	// Redefinicion de tiempo de visualizacion?
	  	if (args.length > 1 && args[1] != null && args[1].length() > 0) {
	  		DisplayTimer.DISPLAY_TIME_SECONDS = Integer.parseInt(args[1]);
	  	}

	  	// Redefinicion de color de fondo
	  	if (args.length > 2 && args[2] != null && args[2].length() > 0) {
	  		backColor = Color.decode("0x" + args[2]);
	  	}
	  	
	  	// Redefinicion de color de campo nombre
	  	if (args.length > 3 && args[3] != null && args[3].length() > 0) {
	  		nameColor = Color.decode("0x" + args[3]);
	  	}
	  	
	  	// Redefinicion de color de campo precio
	  	if (args.length > 4 && args[4] != null && args[4].length() > 0) {
	  		priceColor = Color.decode("0x" + args[4]);
	  	}
	  	
	  	System.out.println("ClientID:" + clientId + " OrgID:" + orgId + " PriceListVersionID:" + priceListVersionID + " DisplayTime:" + DisplayTimer.DISPLAY_TIME_SECONDS);
	  	
		new VProductPricePoll();
	}

}
