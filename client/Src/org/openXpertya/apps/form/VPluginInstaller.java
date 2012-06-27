package org.openXpertya.apps.form;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;

import org.compiere.plaf.CompiereColor;
import org.compiere.swing.CButton;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTextPane;
import org.openXpertya.OpenXpertya;
import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.SwingWorker;
import org.openXpertya.grid.ed.VFile;
import org.openXpertya.plugin.common.PluginConstants;
import org.openXpertya.plugin.common.PluginUtils;
import org.openXpertya.plugin.common.PluginUtils.PluginStatusListener;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.replication.ReplicationCache;
import org.openXpertya.util.ASyncProcess;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Trx;
import org.openXpertya.utils.JarHelper;



public class VPluginInstaller extends CPanel implements FormPanel, ASyncProcess {
	
	/* Componentes de la clase */
	private Properties m_ctx;
	private Properties m_component_props;
	private String m_trx;
	private int m_WindowNo;
	private FormFrame m_frame;
	private VFile fileChooser;
	private CButton doInstallButton;
	private CTextPane detailsTextPane;
	private CPanel topPanel;
	private CPanel centerPanel;
	private CPanel mainPanel;
	private boolean fileOK = false;
	private boolean m_isLocked = false;

	private SwingWorker currentWorker = null;
	
	/**
	 * Inicializacion del formulario
	 */
	public void init(int WindowNo, FormFrame frame) {
        m_WindowNo = WindowNo;
        m_frame    = frame;

//      Comentado porque no funciona la interrupción del worker swing. Una vez que 
//      está ejecutando no es posible pararlo con el método interrupt(). Si se encuentra
//      una forma de hacerlo sería lo correcto, de modo de cancelar la instalación.
        
//        m_frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
//        m_frame.addWindowListener(new WindowAdapter() {
//
//			@Override
//			public void windowClosing(WindowEvent e) {
//				if (close()) {
//					System.out.println("Cerrar");
//					m_frame.setVisible(false);
//					m_frame.dispose();
//				}
//			}
//	
//        	
//        });
//        
        initComponents();
        
        m_frame.pack();
	}

	/**
	 * Liberacion de recursos
	 */
	public void dispose() {
		m_frame.dispose();
		m_frame = null;
		
	}
	
	/**
	 * Inicializacion de componentes visuales
	 */
	private void initComponents() {
		
		/* Contexto y transacción */
		m_ctx = Env.getCtx();
		m_trx = Trx.createTrxName();
		
		/* Listener especial para el File Chooser */
		ActionListener specialListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showPluginDetails();
			}			
		};
		
		/* Instanciar componentes */
		fileChooser = new VFile("fileName", true, false, true, true, specialListener);
		doInstallButton = new CButton("Instalar");
		detailsTextPane = new CTextPane();
		
		/* Dimensiones y propiedades de componentes */
		setSize(new Dimension(720,480));
		setPreferredSize(new Dimension(720,480));
		topPanel = new CPanel(new FlowLayout(FlowLayout.CENTER));
		topPanel.setBackgroundColor(CompiereColor.getBackground(topPanel));
		centerPanel = new CPanel(new BorderLayout());
		mainPanel = new CPanel();
		fileChooser.setPreferredSize(new Dimension(575,20));
		doInstallButton.setPreferredSize(new Dimension(100,20));
		detailsTextPane.setPreferredSize(new Dimension(680,430));
		detailsTextPane.setEditable(false);
		doInstallButton.setEnabled(false);
		
		/* Incorporar los componentes a los paneles */
		topPanel.add(fileChooser);
		topPanel.add(doInstallButton);
		centerPanel.add(detailsTextPane, BorderLayout.CENTER);
		
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		
		/* Listeners */
		doInstallButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!PluginUtils.isInstallingPlugin()) 
					performInstall();
			}
		});
		
		/* Incorporar al contendor correspondiente */ 
//		this.add(mainPanel);
//		m_frame.add(this);
		m_frame.setContentPane(mainPanel);
		m_frame.pack();
	}
	
	/**
	 * Entrada principal al proceso de instalación de un plugin
	 * Debe manejar las excepciones correspondientes
	 */
	private void performInstall() 
	{
		/* Indicar el error correspondiente si no se especificó un archivo */
		if (fileChooser.getDisplay() == null || fileChooser.getDisplay().length() == 0 || !fileOK)
		{
			ADialog.error(m_WindowNo, this, Msg.parseTranslation(m_ctx, "Jar incorrecto o sin especificar!" ));
        	return;
		}
		
		String pluginStatus = VPluginInstallerUtils.validatePlugin(m_component_props, m_ctx);
		if (pluginStatus.length() > 0)
		{
			ADialog.error(m_WindowNo, this, "ERROR: " + pluginStatus);
			doInstallButton.setEnabled(false);
			return;
		}
		

		/* Si el usario da el ok, realizar la instalación por partes */
		setComponentsEnabled(false);
		detailsTextPane.setText(" Iniciando instalacion.  Esto puede demorar varios minutos.  Por favor, espere... ");
		PluginUtils.setStatusListener(installListener);
		if (confirm("Esto instalará el plugin en el sistema, está seguro?"))
		{
			currentWorker = new SwingWorker() {
				
				@Override
				public Object construct() {
					try {
						/* Iniciar la transacción y setear componente global */
						m_trx = Trx.createTrxName();
						Trx.getTrx(m_trx).start();
						PluginUtils.startInstalation(m_trx);
										
						/* Instalacion por etapas: pre - install - post */
						PluginUtils.appendStatus(" === Instalando Componente y Version. Registrando Plugin === ");
						VPluginInstallerUtils.createComponentAndVersion(m_ctx, m_trx, m_component_props);

						/* Preinstalacion - Sentencias SQL */
						PluginUtils.appendStatus(" === Ejecutando sentencias de preinstalacion === ");
						VPluginInstallerUtils.doPreInstall(m_ctx, m_trx, fileChooser.getDisplay(), PluginConstants.URL_INSIDE_JAR + PluginConstants.FILENAME_PREINSTALL);
						
						/* Comprobar secuencia por modificaciones a nivel SQL */
						PluginUtils.appendStatus(" === Comprobando secuencias de nuevos componentes - preinstalacion === ");
						VPluginInstallerUtils.sequenceCheck(m_ctx, m_trx);
						
						/* Instalacion - Carga de metadatos */
						PluginUtils.appendStatus(" === Insertando metadatos de instalación === ");
						VPluginInstallerUtils.doInstall(m_ctx, m_trx, fileChooser.getDisplay(), PluginConstants.URL_INSIDE_JAR + PluginConstants.FILENAME_INSTALL);
						
						/* Comprobar secuencia por modificaciones relacionadas en metadatos */
						PluginUtils.appendStatus(" === Comprobando secuencias de nuevos componentes - metadatos === ");
						VPluginInstallerUtils.sequenceCheck(m_ctx, m_trx);

						/* Información adicional del plugin */
						PluginUtils.appendStatus(" === Registrando informacion adicional del componente === ");
						VPluginInstallerUtils.setAditionalValues(m_ctx, m_trx, m_component_props);
						
						/* PostInstalacion - Invocar proceso genérico o ad-hoc */
						PluginUtils.appendStatus(" === Disparando proceso de postinstalación === ");
						boolean postInstallInvoked = VPluginInstallerUtils.doPostInstall(m_ctx, m_trx, fileChooser.getDisplay(), PluginConstants.URL_INSIDE_JAR + PluginConstants.FILENAME_POSTINSTALL, m_component_props, VPluginInstaller.this);
						if (!postInstallInvoked)
							performInstallFinalize();
					} catch (Exception e) {
						return e;
					}
					return null;
				}

				@Override
				public void finished() {
					Exception result = (Exception)getValue();
					mNormal();
					if (result != null) {
						handleException("Error al realizar la instalación: ", result);
					}
				}
			};
			
			mWait();
			currentWorker.start();
		}
		else
		{
			detailsTextPane.setText(" Instalacion cancelada.");
			setComponentsEnabled(true);
		}
	}
	

	
	/**
	 * Finalización del proceso de instalación de un plugin (sin existencia de postInstall)
	 * Debe manejar las excepciones correspondientes
	 */
	private void performInstallFinalize()
	{
		performInstallFinalize(null);
	}
	
	/**
	 * Finalización del proceso de instalación de un plugin (con existencia de postInstall)
	 * Debe manejar las excepciones correspondientes
	 */
	private void performInstallFinalize(ProcessInfo pi)
	{
		try
		{
			/* Indicar que no hubo post instalacion */
			if (pi == null)
				PluginUtils.appendStatus("Sin post instalacion");
			
			/* Contemplar error al ejecutar el proceso de postInstall */
			if (pi != null && pi.isError())
				throw new Exception(" Excepcion al ejecutar postInstall - " + pi.getSummary());

			/* Si hubo errores, consultar al usuario si en realidad está de acuerdo con la actualización */
			if (installHadErrors() && !confirm("Hubo errores en la instalación. Continuar de todos modos?"))
			{
				handleException("Instalación cancelada por el usuario.", new Exception(" Instalación cancelada por el usuario debido a errores en la ejecución. Verifique archivo de log."));
				return;
			}
			
			/* Finalizar la transacción y resetear componente global */
			Trx.getTrx(m_trx).commit();
			Trx.getTrx(m_trx).close();
			PluginUtils.stopInstalation();
		
			/* Informar todo OK */
			PluginUtils.appendStatus(" === Instalación finalizada === ");
			// detailsTextPane.setText(PluginUtils.getInstallStatus()); <-- comentado, el componente visual se colgaba con el volumen de datos a mostrar
			writeInstallLog();										//	<-- directamente se genera un archivo donde se almacena el log
			ADialog.info(m_WindowNo, this, "Instalación finalizada " + (installHadErrors()?"con errores. Verifique archivo de log":"correctamente") );
			setComponentsEnabled(true);
		}
		catch (Exception e)
		{
			handleException("Error al realizar la post-instalación: ", e);	
		}
	}
	
	/**
	 * Devuelve true en caso de que existieron errores en la instalación
	 */
	protected boolean installHadErrors()
	{
		 return PluginUtils.getErrorStatus() != null && PluginUtils.getErrorStatus().length() > 0;
	}
	
	
	/**
	 * Almacena en archivo correspondiente el log de instalacion 
	 */
	private void writeInstallLog()
	{
		String prefix = (String)m_component_props.get(PluginConstants.PROP_PREFIX);
		String fileName = "Component_" + prefix + "_install_" + Env.getDateTime("yyyyMMdd_HHmmss") + ".log";
		PluginUtils.writeInstallLog(OpenXpertya.getOXPHome(), fileName);
	}
	
	
	/**
	 * Muestra el detalle del plugin en pantalla
	 */
	private void showPluginDetails()
	{
		try
		{
			/* Validar la existencia del archivo de propiedades y presentar la info correspondiente y setear el nuevo estado */
			m_component_props = JarHelper.readPropertiesFromJar(fileChooser.getDisplay(), PluginConstants.URL_INSIDE_JAR + PluginConstants.PLUGIN_MANIFEST);
			String content = JarHelper.readFromJar(fileChooser.getDisplay(), PluginConstants.URL_INSIDE_JAR + PluginConstants.PLUGIN_MANIFEST, "<br>", null);
			detailsTextPane.setText(content);
			doInstallButton.setEnabled(true);
			fileOK = true;
		}
		catch (Exception e)
		{
			/* En caso de error, setear el nuevo estado e informar en pantalla */
			fileOK = false;
			ADialog.error(m_WindowNo, this, "Error al abrir del JAR: " + e.getMessage());
			doInstallButton.setEnabled(false);
		}
	}
	
	/**
	 * En caso de error al realizar la instalación rollbackear la trx, detener la instalacion e informar 
	 */
	private void handleException(String msg, Exception e)
	{
		/* Error en algún punto, rollback e informar al usuario */
		Trx.getTrx(m_trx).rollback();
		Trx.getTrx(m_trx).close();
		PluginUtils.stopInstalation();
		PluginUtils.appendStatus(msg + e.getMessage());
		// detailsTextPane.setText(PluginUtils.getInstallStatus()); <-- comentado, el componente visual se colgaba con el volumen de datos a mostrar
		writeInstallLog();										//	<-- directamente se genera un archivo donde se almacena el log
		setComponentsEnabled(true);
		ADialog.error(m_WindowNo, this, msg + e.getMessage());
	}
	
	
	/**Enabled
	 * Pide al usuario que valide adecuadamente
	 * @return true si el usuario hace click en Yes o false en caso contrario
	 */
	private boolean confirm(String text)
	{
		return (JOptionPane.showConfirmDialog(this, text, "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION);
	}


	public Properties getM_component_props() {
		return m_component_props;
	}


	public int getM_WindowNo() {
		return m_WindowNo;
	}


	@Override
	public void executeASync(ProcessInfo pi) {

	}


	@Override
	public boolean isUILocked() {
		return m_isLocked;
	}


	@Override
	public void lockUI(ProcessInfo pi) {
		m_isLocked = true;
	}


	@Override
	public void unlockUI(ProcessInfo pi) {
		m_isLocked = false;
		performInstallFinalize(pi);
	}
	
	/**
	 * Actualiza el estado de visibilidad los componentes
	 * @param status
	 */
	private void setComponentsEnabled(boolean status)
	{
		doInstallButton.setEnabled(status);
		detailsTextPane.setEnabled(status);
		fileChooser.setEnabled(status);
		if (!status) {
			m_frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		} else {
			m_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			// Limpio el dato actual del componentversionID local
			Env.setContext(m_ctx, PluginConstants.INSTALLED_COMPONENTVERSION_ID, -1);
			Env.setContext(m_ctx, PluginConstants.PROP_PREFIX, "");
			Env.setContext(m_ctx, PluginConstants.PROP_COPY_TO_CHANGELOG, "N");
			Env.setContext(m_ctx, PluginConstants.MAP_TO_COMPONENT, "N");
			Env.setContext(m_ctx, PluginConstants.COMPONENT_SOURCE_PREFIX, "");
			ReplicationCache.mappedUIDs = new HashMap<String, String>();;
		}
		
	}
	
	private PluginStatusListener installListener = new PluginStatusListener() {
		private ArrayList<String> lines = new ArrayList<String>();
		private int lineQty = 0;
		private final int MAX_LINES = 100;
		
		@Override
		public void statusChanged(final String statusLine, final boolean isError, final boolean newLine) {
			if (statusLine != null) {
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						StringBuffer status = new StringBuffer();
						for (String line : lines)
							status.append(line);
						StringBuffer newStatus = new StringBuffer("<font color=").append(isError?"red>":"blue>").append(statusLine).append("</font>").append(newLine?"<br>":""); 
						StringBuffer newStatusBold = new StringBuffer("<b>").append(newStatus);
						detailsTextPane.setTextNoCaret(status.toString() + newStatusBold); 

						if (newLine)
							lines.add(newStatus.toString());
						
				    	// Si llegó a la cantidad máxima de líneas extrae la primer
				    	// línea del status para ir recortando el log y que no se cuelgue
				    	// el TexPane.
				    	if (newLine && lineQty == MAX_LINES) {
				    		lines.remove(0);
				    	} else {
				    		if (newLine && lineQty != MAX_LINES)
				    			lineQty++;
				    	}
						
						// status.append(statusLine).append("<br>");
				    	// Se hace un scroll automático hacia el final del panel.
				    	SwingUtilities.invokeLater(new Runnable() {
				    		public void run() {
				    	    	JScrollBar vScroll = detailsTextPane.getVerticalScrollBar();
				    	    	vScroll.setValue(vScroll.getMaximum());
				    		}
				    	});
					}
				});
			}
		}
	};
	
    private void mWait() {
    	m_frame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    }
    
    private void mNormal() {
    	m_frame.setCursor(Cursor.getDefaultCursor());
    }
    
//    protected boolean close() {
//    	boolean close = true;
//    	if (currentWorker != null) {
//    		if (ADialog.ask(m_WindowNo, m_frame, "¿Desea cancelar la instalación?")) {
//    			// Verificar! Con la simple invocación de este método el hilo no se
//    			// cancela. Hay que buscar alguna otra forma de hacerlo.
//    			currentWorker.interrupt();
//    			close = true;
//    			handleException("Instalación interrumpida por el usuario.", new Exception(" Instalación interrumpida por el usuario durante el transcurso de la misma"));
//    		} else {
//    			close = false;
//    		}
//    	}
//    	return close;
//    }
	
}
