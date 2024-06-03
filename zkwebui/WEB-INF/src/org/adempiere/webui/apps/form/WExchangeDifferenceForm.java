package org.adempiere.webui.apps.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.panel.ADForm;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zul.Space;

/**
 * Formulario que se utiliza para mostrar las diferencias de cambio al cobrar
 * Basado en VExchangeDifferenceForm
 * @author dREHER
 */

public class WExchangeDifferenceForm extends Window implements ActionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final boolean DEBUG = true;
	WOrdenCobro ordenCobro;
	private Label lblLimit;
    private Label lblExchangeDif;
    private Label lblLimitPercent;
    private Label lblExchangeDifPercnt;
    private Checkbox chckbxEmit;
    private Checkbox chckbxInclude;
    private Button okButton;
    private Button cancelButton;
    
	private final Panel contentPanel = new Panel();
	
	private BigDecimal exchangeDif = null;
	private BigDecimal exchangeDifPercent = null;
	private BigDecimal limit = null;
	private BigDecimal limitPercent = null;
	private boolean shouldEmit = false;

	/**
	 * Create the dialog.
	 * @throws InterruptedException 
	 */
	public WExchangeDifferenceForm(WOrdenCobro ordenCobro, BigDecimal exchangeDif, BigDecimal exchangeDifPercent, BigDecimal limit, BigDecimal limitPercent, boolean shouldEmit) throws InterruptedException {
		this.ordenCobro = ordenCobro;
		this.setTitle("Emitir/Incluir Nota de Cred/Deb");
		this.setWidth("500px");
		this.setHeight("290px");
		this.setStyle("border: solid; 2px; padding: 2px;");
		
		this.exchangeDif = exchangeDif;
		this.exchangeDifPercent = exchangeDifPercent;
		this.limit = limit;
		this.limitPercent = limitPercent;
		this.shouldEmit = shouldEmit;
		
		initForm();
		
	}
	
	protected void initForm() {
		
		Html customHtml = new Html();
		customHtml.setContent("<div>&nbsp;<br></div>");
		
		Label lblTitulo = new Label();
		lblTitulo.setText("Emitir y/o incluír en el cobro de las Notas de Crédito o Notas de Débito que correspondan.");
		lblTitulo.setWidth("100%");
		
		Label lblSubtitulo = new Label();
		lblSubtitulo.setText("Si una factura en moneda extranjera se cobrará en la misma moneda, por favor active ambas opciones: Emitir/Incluir");
		lblSubtitulo.setWidth("100%");
		lblSubtitulo.setStyle("font-weight: bold;");
		
		okButton = new Button("");
		okButton.setImage("images/Ok24.png");
		okButton.addEventListener("onClick", new EventListener() {
            public void onEvent(Event evt) throws Exception {
            	actionPerformedOK(evt);
            }
        });
		
		cancelButton = new Button("");
		cancelButton.setImage("images/Cancel24.png");
		cancelButton.addEventListener("onClick", new EventListener() {
            public void onEvent(Event evt) throws Exception {
            	actionPerformedKO(evt);
            }
        });
		
		// Agrego titulo
		this.appendChild(lblTitulo);
		this.appendChild(new Space());
		
		this.appendChild(lblSubtitulo);
		this.appendChild(customHtml);

		// Controles intermedios
		this.appendChild(createGrid());

		// Botones a la derecha
		this.appendChild(customHtml);
		
		Panel pnlButtonRight = new Panel();
        pnlButtonRight.appendChild(okButton);
        pnlButtonRight.appendChild(cancelButton);
        pnlButtonRight.setAlign("right");
        pnlButtonRight.setWidth("100%");
		
		this.appendChild(pnlButtonRight);
		
		debug("Creo los controles generales y la ventana...");
		
	}

	private Component createGrid() {
		Label lblNewLabel_1 = new Label("Diferencia %:");
		
		lblExchangeDifPercnt = new Label(exchangeDifPercent.toString());
		lblLimitPercent = new Label(limitPercent.toString());
		
		Label lblNewLabel_2 = new Label("Diferencia $:");
		
		lblLimit = new Label(limit.toString());
		
		lblExchangeDif = new Label(exchangeDif.toString());
		
		chckbxInclude = new Checkbox();
		chckbxInclude.setText("Incluir");
		
		chckbxEmit = new Checkbox();
		chckbxEmit.setText("Emitir");
		chckbxEmit.setSelected(shouldEmit);
		chckbxEmit.addActionListener(new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				if (chckbxEmit.isSelected()) {
                	chckbxInclude.setEnabled(true);
                } else {
                	chckbxInclude.setEnabled(false);
                	chckbxInclude.setSelected(false);
                }
				
			}
		});
		
		Label lblNewLabel_3 = new Label("Límite %:");
		Label lblNewLabel_4 = new Label("Límite $:");
		
		Grid grid = new Grid();

		Rows rows = new Rows();
		
		Row row = rows.newRow();
		row.setSpans("4");
		row.appendChild(new Space());
		
		row = rows.newRow();
		// Dif porcentaje
		row.appendChild(lblNewLabel_1);
		row.appendChild(lblExchangeDifPercnt.rightAlign());
		
		// Dif pesos
		row.appendChild(lblNewLabel_2);
		row.appendChild(lblExchangeDif.rightAlign());
		
		
		row = rows.newRow();
		// Limite porcentaje
		row.appendChild(lblNewLabel_3);
		row.appendChild(lblLimitPercent.rightAlign());
				
		// Limite pesos
		row.appendChild(lblNewLabel_4);
		row.appendChild(lblLimit.rightAlign());
		
		// Emitir e Incluir
		row = rows.newRow();
		row.appendChild(new Space());
		row.appendChild(chckbxEmit);
		row.appendChild(new Space());
		row.appendChild(chckbxInclude);
		
		row = rows.newRow();
		row.setSpans("4");
		row.appendChild(new Space());
		
		debug("Creo los controles intermedios...");
		
		grid.appendChild(rows);
		
		return grid;
	}

	private void debug(String string) {
		if(DEBUG)
			System.out.println("WExchangeDifferenceFrom. " + string);
		
	}

	public void actionPerformedOK(Event e) {
			// Se settean los valores de Emitir / Incluir en la Vista y el Modelo.
			this.ordenCobro.setCintoloEmit(chckbxEmit.isSelected());
			this.ordenCobro.setCintoloInclude(chckbxInclude.isSelected());
			this.ordenCobro.setContinue(true);
			
			// Se cierra la ventana
			// this.setVisible(false);
			this.dispose();
	}

	public void actionPerformedKO(Event e) {
		this.ordenCobro.setContinue(false);
		// Se cierra la ventana
		// this.setVisible(false);
		this.dispose();
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
