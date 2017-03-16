/*
 * NewJFrame.java
 *
 * Created on 1 de agosto de 2007, 09:38
 */

package org.openXpertya.apps.form;

import java.awt.Cursor;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.sql.Timestamp;
import java.util.logging.Level;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import org.compiere.plaf.CompiereColor;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.AWindow;
import org.openXpertya.apps.StatusBar;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.model.MBankStatement;
import org.openXpertya.model.MLookup;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.model.MQuery;
import org.openXpertya.model.MTab;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

/**
 * 
 * Formulario de conciliacion manual a partir de extractos bancarios importados (I_*)
 *
 * @author  usuario
 */
public class VConciliacionExtractoImportado extends FormFrame implements VetoableChangeListener {
	
	/* Constantes para distribución visual */
	private static final int WINDOW_PREF_SIZE_X = 1000;
	private static final int WINDOW_PREF_SIZE_Y = 600;
	private static final int GRID_COLUMN_SIZE = 110;
	
    /** Creates new form NewJFrame */
	public VConciliacionExtractoImportado( MTab mTab, int modoLineas, int modoPagos ) {
		m_mTab = mTab;
		m_WindowNo = mTab.getWindowNo();
		
		m_modoLineas = modoLineas;
		m_modoPagos = modoPagos;
		
		CompiereColor.setBackground( this );
		
        initComponents();
        customInitComponents();
    }
    
	public void vetoableChange(PropertyChangeEvent arg0) throws PropertyVetoException {
		
	}
	
	public static VLookup VLookupFactory(int AD_Column_ID, int WindowNo) {
        // = 4917;    // C_BankStatement.C_BankAccount_ID, 
        MLookup lookup       = MLookupFactory.get( Env.getCtx(),WindowNo,0,AD_Column_ID,DisplayType.TableDir );
        String columnKeyName = lookup.getColumnName();
        String columnName = columnKeyName.substring(columnKeyName.lastIndexOf(".") + 1);
        
        VLookup vl = new VLookup( columnName, true, false, true, lookup );
        
        return vl;
	}

	public static void ZoomFactory(int AD_Column_ID, Object value, int WindowNo) {
		
        MLookup lookup       = MLookupFactory.get( Env.getCtx(),WindowNo,0,AD_Column_ID,DisplayType.TableDir );
        String columnKeyName = lookup.getColumnName();
        String columnName = columnKeyName.substring(columnKeyName.lastIndexOf(".") + 1);

        MQuery zoomQuery = new MQuery();    // ColumnName might be changed in MTab.validateQuery
        zoomQuery.addRestriction( columnName, MQuery.EQUAL, value );
        int AD_Window_ID = lookup.getZoom( zoomQuery );
        
        AWindow frame = new AWindow();
        if( frame.initWindow( AD_Window_ID, zoomQuery )) {
        	AEnv.showCenterScreen( frame );
        }
	}
	
	public static int DarColID(String ColumnName, String TableName) {
		String sql = "select c.ad_column_id from ad_column c " +
					" inner join ad_table t on (c.ad_table_id=t.ad_table_id) " +
					" where c.columnname ilike '" + ColumnName + "' and t.tablename ilike '" + TableName + "' ";
		
		return DB.getSQLValue(null, sql);
	}
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" C�digo Generado  ">//GEN-BEGIN:initComponents
    private void initComponents() {

    	/** Definir ancho acorde al volumen de información a mostrar */
        setPreferredSize(new Dimension(WINDOW_PREF_SIZE_X, WINDOW_PREF_SIZE_Y));
    	
        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel8 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        // jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblConciliar = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        cmdConciliar = new javax.swing.JButton();
        cmdUnconciliar = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblLineas = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jRadioButton1 = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblPagos = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jLabel6 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        cmdProcesar = new javax.swing.JButton();
        cmdCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText(Msg.parseTranslation( Env.getCtx(),"@C_BankAccount_ID@" ));

        // jLabel2.setText("-cuenta-bancaria-str-");
        
        // ********* //
        bankAccountField = VLookupFactory(4917, /* , */ m_WindowNo);
        bankAccountField.addVetoableChangeListener(this);
        bankAccountField.setReadWrite(false);
        
        jLabel2 = bankAccountField;
        // ********* //
        
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText(Msg.parseTranslation( Env.getCtx(),"@C_BankStatement_ID@" ));

        // ********* //
        jLabel4.setText( (String)m_mTab.getValue("Name") );
        // ********* //
        
        org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 143, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 117, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(39, 39, 39)
                .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 126, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 143, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2)
                    .add(jLabel3)
                    .add(jLabel4))
                .addContainerGap())
        );

        jPanel8Layout.linkSize(new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel4}, org.jdesktop.layout.GroupLayout.VERTICAL);

        tblConciliar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        
        tblConciliar.setAutoCreateColumnsFromModel(true);
        
        tblConciliar.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblConciliar.setMinimumSize(new java.awt.Dimension(200, 100));
        jScrollPane1.setViewportView(tblConciliar);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 618, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
        );

        cmdConciliar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/openXpertya/images/Previous24.gif")));
        cmdConciliar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdConciliarActionPerformed(evt);
            }
        });

        jPanel2.add(cmdConciliar);

        cmdUnconciliar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/openXpertya/images/Next24.gif")));
        cmdUnconciliar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdUnconciliarActionPerformed(evt);
            }
        });

        jPanel2.add(cmdUnconciliar);

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.X_AXIS));

        tblLineas.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane2.setViewportView(tblLineas);

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/openXpertya/images/Info24.gif")));
        jButton5.setMargin(new java.awt.Insets(1, 1, 1, 1));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/openXpertya/images/Entradas24.gif")));
        jButton4.setMargin(new java.awt.Insets(1, 1, 1, 1));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/openXpertya/images/Find24.gif")));
        jButton3.setMargin(new java.awt.Insets(1, 1, 1, 1));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Multiselecci\u00f3n");
        jRadioButton1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButton1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .add(jRadioButton1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 93, Short.MAX_VALUE)
                .add(jButton3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton4)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton5)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jButton5)
                        .add(jButton4)
                        .add(jButton3))
                    .add(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jRadioButton1)))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jLabel5.setText("");

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel4Layout.createSequentialGroup()
                .add(jLabel5)
                .addContainerGap())
            .add(jPanel4Layout.createSequentialGroup()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                .add(jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3.add(jPanel4);

        tblPagos.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane3.setViewportView(tblPagos);

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/openXpertya/images/Info24.gif")));
        jButton6.setMargin(new java.awt.Insets(1, 1, 1, 1));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/openXpertya/images/Entradas24.gif")));
        jButton7.setMargin(new java.awt.Insets(1, 1, 1, 1));
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/openXpertya/images/Find24.gif")));
        jButton8.setMargin(new java.awt.Insets(1, 1, 1, 1));
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("Multiselecci\u00f3n");
        jRadioButton2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButton2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .add(jRadioButton2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 96, Short.MAX_VALUE)
                .add(jButton8)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton7)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton6)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jButton6)
                        .add(jButton7)
                        .add(jButton8))
                    .add(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jRadioButton2)))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jLabel6.setText("");

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel6)
                .addContainerGap(214, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel5Layout.createSequentialGroup()
                .add(jLabel6)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3.add(jPanel5);

        cmdProcesar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/openXpertya/images/Process16.gif")));
        cmdProcesar.setText(Msg.getElement( Env.getCtx(),"Processing" ));
        cmdProcesar.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        cmdProcesar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdProcesarActionPerformed(evt);
            }
        });

        cmdCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/openXpertya/images/Cancel16.gif")));
        cmdCancelar.setText(Msg.parseTranslation( Env.getCtx(),"@Close@" ));
        cmdCancelar.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        cmdCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdCancelarActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(408, Short.MAX_VALUE)
                .add(cmdCancelar)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cmdProcesar)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cmdProcesar)
                    .add(cmdCancelar))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 618, Short.MAX_VALUE)
                    .add(jPanel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 618, Short.MAX_VALUE)
                    .add(jPanel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(statusBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(statusBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))

        );
        
        pack();
        AEnv.showCenterScreen(this);
    }// </editor-fold>//GEN-END:initComponents

    protected void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
    	// Magia sobre pagos //
    	findMatches(1);
    }//GEN-LAST:event_jButton6ActionPerformed

    protected void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
    	// Reset sobre pagos //
    	m_conModel.resetPagos();
    }//GEN-LAST:event_jButton7ActionPerformed

    protected void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
    	//	Zoom sobre el pago
    	if (tblPagos.getSelectedRowCount() > 0) {
    		int x = tblPagos.getSelectedRow();
    		//Object obj = tblPagos.getModel().getValueAt(x, 0);
    		Object obj = m_conModel.getPayID(x);
    		int tipo = m_conModel.getPagoRealMode(x);
   		
    		mWait(); 
    		ZoomFactory(darPagosColID(m_modoPagos, x), obj, m_WindowNo);
    		mNormal();
    	}
    }//GEN-LAST:event_jButton8ActionPerformed

    protected void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
    	// Magia sobre lineas importadas //
    	findMatches(0);
    }//GEN-LAST:event_jButton5ActionPerformed

    protected void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
    	// Reset sobre lineas importadas //
    	m_conModel.resetLineas();
    }//GEN-LAST:event_jButton4ActionPerformed

    protected void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
    	// Zoom sobre la linea importada
    	if (tblLineas.getSelectedRowCount() > 0) {
    		int x = tblLineas.getSelectedRow();
    		//Object obj = tblLineas.getModel().getValueAt(x, 0);
    		Object obj = m_conModel.getLineaID(x);
    		mWait();
    		ZoomFactory( darLineasColID(m_modoLineas), obj, m_WindowNo);
    		mNormal();
    	}
    }//GEN-LAST:event_jButton3ActionPerformed

    protected void cmdProcesarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdProcesarActionPerformed
    	String msg = "@ProcessOK@";
    	int msgType = JOptionPane.INFORMATION_MESSAGE;
    	try {
    		m_conModel.procesarConciliacion();
    	} catch (Exception e) {
    		log.severe(e.toString());
			msg = "@ProcessRunError@. "
					+ (!Util.isEmpty(e.getMessage()) ? e.getMessage() : (e
							.getCause() != null ? e.getCause().getMessage()
							: "Error al conciliar"));
    		msgType = JOptionPane.ERROR_MESSAGE;
    	} finally{
    		JOptionPane.showMessageDialog(this,
    				Msg.parseTranslation( Env.getCtx(),msg ),
    				Msg.getMsg(Env.getCtx(), "BankStatementMatching"),
    				JOptionPane.OK_OPTION | msgType);
    	}
    	
    }//GEN-LAST:event_cmdProcesarActionPerformed

    protected void cmdCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdCancelarActionPerformed
    	setVisible(false);
    	dispose();
    }//GEN-LAST:event_cmdCancelarActionPerformed

    protected void cmdUnconciliarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdUnconciliarActionPerformed
    	try {
    		m_conModel.unconcilData(tblConciliar.getSelectedRows());
            setAllColumnsWidth(tblLineas, GRID_COLUMN_SIZE);
            setAllColumnsWidth(tblPagos, GRID_COLUMN_SIZE);
    	} catch (Exception e) {
    		
    	}
    }//GEN-LAST:event_cmdUnconciliarActionPerformed

    protected void cmdConciliarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdConciliarActionPerformed
    	try {
    		if (askIfConcilOk()) {
    		
    			m_conModel.concilData(tblLineas.getSelectedRows(), tblPagos.getSelectedRows());
    			setAllColumnsWidth(tblConciliar, GRID_COLUMN_SIZE);
    		}
    	} catch (Exception e) {
    		
    	}
    }//GEN-LAST:event_cmdConciliarActionPerformed

    protected void findMatches(int side) {
    	try {
    		javax.swing.JTable thisTbl = null, otherTbl = null;
    		
    		if (side == 0) {
    			thisTbl = tblLineas;
    			otherTbl = tblPagos;
    			
    			m_conModel.resetPagos();
    		} else if (side == 1) {
    			otherTbl = tblLineas;
    			thisTbl = tblPagos;
    			
    			m_conModel.resetLineas();
    		} else {
    			return;
    		}
    		
 	    	if (thisTbl.getSelectedRowCount() > 0) {
	    		int x = thisTbl.getSelectedRow();
	    		int [] ret = m_conModel.findMatches(side, x);
	    		
	    		otherTbl.clearSelection();
	    		for (int a : ret) 
	    			otherTbl.addRowSelectionInterval(a,a);
	    	}
    	} catch (Exception e) {
    		log.log(Level.SEVERE, "VConciliacionExtractoImportado.findMatches", e);
    	}
    }
    
    protected boolean askIfConcilOk() {
    	boolean ret = m_conModel.precheckConcilData(tblLineas.getSelectedRows(), tblPagos.getSelectedRows());
    	
    	if (ret) 
    		return true;
    	
    	int op = JOptionPane.showConfirmDialog(this, "El monto de los elementos a conciliar no coincide. ¿Desea continuar?", "", JOptionPane.YES_NO_OPTION);
    	
    	return op == JOptionPane.YES_OPTION;
    }
    
    protected void customInitComponents() {
        radioChanged(null);
        tblConciliar.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        
        //
        Env.setContext( Env.getCtx(),m_WindowNo,"WindowName",Msg.translate(Env.getCtx(), "BankStatementMatching"));
        setTitle(Env.getHeader( Env.getCtx(),m_WindowNo ));
        setIconImage(org.openXpertya.OpenXpertya.getImage16());
        
        //
        
        jPanel1.setOpaque(false);
        jPanel2.setOpaque(false);
        jPanel3.setOpaque(false);
        jPanel4.setOpaque(false);
        jPanel5.setOpaque(false);
        jPanel6.setOpaque(false);
        jPanel7.setOpaque(false);
        jPanel8.setOpaque(false);
        jPanel9.setOpaque(false);
        
        //

        initModels();

        //

        int C_BankStatement_ID = Env.getContextAsInt( Env.getCtx(), m_WindowNo, "C_BankStatement_ID" );
        MBankStatement bs = new MBankStatement(Env.getCtx(), C_BankStatement_ID, null);

        int C_BankAccount_ID = bs.getC_BankAccount_ID(); // Env.getContextAsInt( Env.getCtx(), "C_BankAccount_ID" );
        bankAccountField.setValue( new Integer( C_BankAccount_ID ));
        m_conModel.setBankAccountID(C_BankAccount_ID);
        
        //
        
        m_conModel.setBankStatement(bs);

        //
        
        int mainCurrencyID = Env.getContextAsInt( Env.getCtx(), m_WindowNo, "$C_Currency_ID" ); // bs.getC_Currency_ID();
        
        m_conModel.init( ( Timestamp )m_mTab.getValue( "StatementDate" ), mainCurrencyID );
        
        //
        
        jLabel4.setText( bs.getName() ); // (String)m_mTab.getValue("Name") );
        
        initLabels();
        
        //
        
        statusBar.setStatusDB("");
        statusBar.setStatusLine(Msg.translate(Env.getCtx(),"BankStatementMatchingStatus"));
        
        setAllColumnsWidth(tblLineas, GRID_COLUMN_SIZE);
        setAllColumnsWidth(tblPagos, GRID_COLUMN_SIZE);
        
    }
    
    protected void initLabels() {
    	if (m_modoLineas == VConciliacionTableModel.MODOLINEAS_IMPORTADAS)
    		jLabel5.setText(Msg.getMsg(Env.getCtx(), "ImportedStatementLines"));
    	else if (m_modoLineas == VConciliacionTableModel.MODOLINEAS_EXISTENTES)
    		jLabel5.setText(Msg.getMsg(Env.getCtx(), "CurrentStatementLines"));
    	
    	if (m_modoPagos == VConciliacionTableModel.MODOPAGOS_PAGOS)
    		jLabel6.setText(Msg.parseTranslation( Env.getCtx(),"@C_Payment_ID@" ));
    	else if (m_modoPagos == VConciliacionTableModel.MODOPAGOS_BOLETAS)
    		jLabel6.setText(Msg.parseTranslation( Env.getCtx(),"@M_BoletaDeposito_ID@" ));
    	else if (m_modoPagos == VConciliacionTableModel.MODOPAGOS_PAGOSUNIONBOLETAS)
    		jLabel6.setText(Msg.parseTranslation( Env.getCtx(),"@C_Payment_ID@" ));
    }
    
    protected int darLineasColID(int modo) {
		// 9306: El ID de la columna I_BankStatement_ID en los metadatos
		// 4926: El ID de la columna C_BankStatementLine_ID en los metadatos

    	// TODO: Refactoring to VConciliacionTableModel
    	
    	if (modo == VConciliacionTableModel.MODOLINEAS_IMPORTADAS)
    		return 9306;
    	else if (modo == VConciliacionTableModel.MODOLINEAS_EXISTENTES)
    		return 4926; // C_BankStatementLine_ID
    	
    	return -1;
    }
    
    protected int darPagosColID(int modo, int rowIdx) {
    	// 8402: El ID de la columna C_Payment_ID en los metadatos, de la tabla C_Payment_v
    	// ?: EL ID de la columna M_BoletaDeposito_ID
    
    	// TODO: Refactoring to VConciliacionTableModel
    	
    	int C_PaymentColumn_ID = 8402;
    	int M_BoletaDepositoColumn_ID = DarColID("M_BoletaDeposito_ID", "M_BoletaDeposito");
    	
    	if (modo == VConciliacionTableModel.MODOPAGOS_PAGOS)
    		return C_PaymentColumn_ID;
    	else if (modo == VConciliacionTableModel.MODOPAGOS_BOLETAS)
    		return M_BoletaDepositoColumn_ID;
    	else if (modo == VConciliacionTableModel.MODOPAGOS_PAGOSUNIONBOLETAS) {
    		return darPagosColID(m_conModel.getPagoRealMode(rowIdx), rowIdx);
    	}
    		
    	return -1;
    }
    
    protected void initModels() {
    	m_conModel = new VConciliacionTableModel(m_modoLineas, m_modoPagos);
    	
        tblConciliar.setModel(m_conModel.getToConciliar());
        tblLineas.setModel(m_conModel.getLineas());
        tblPagos.setModel(m_conModel.getPagos());
                
    }
    
    protected void radioChanged(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioChanged
        // Multiseleccion
        if (jRadioButton1.isSelected()) {
            tblLineas.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            tblPagos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        } else {
            tblLineas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tblPagos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        }
    }//GEN-LAST:event_radioChanged

    protected void mWait() {
    	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
    
    protected void mNormal() {
    	setCursor(Cursor.getDefaultCursor());
    }
    
    @Override
	public void dispose() {

	}
    
    private int getPagoIDColumnIndex() {
    	int index = 0;
    	if(m_modoPagos == VConciliacionTableModel.MODOPAGOS_PAGOSUNIONBOLETAS)
    		index = 1;
    	
    	return index;
    }

    private int getLineaIDColumnIndex() {
    	return 0;
    }

    /**
     *  Especifica el ancho minimo para cada una de las columnas de la tabla dada 
     */
    private void setAllColumnsWidth(JTable aTable, int columnSize)
    {
		/* Definir un ancho razonable para cada columna */
		for (int i = 0; i < aTable.getColumnModel().getColumnCount(); i++)
			aTable.getColumnModel().getColumn(i).setMinWidth(columnSize);
    }
    
    
    private static final CLogger log = CLogger.getCLogger(VConciliacionTableModel.class);
    
    protected VConciliacionTableModel m_conModel = null;
    protected VLookup bankAccountField = null;
    protected MTab m_mTab = null;
    protected int m_WindowNo ;
    
    protected int m_modoLineas;
    protected int m_modoPagos;
    
    
    // Declaración de varibales -no modificar//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton cmdCancelar;
    private javax.swing.JButton cmdConciliar;
    private javax.swing.JButton cmdProcesar;
    private javax.swing.JButton cmdUnconciliar;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    protected javax.swing.JLabel jLabel1;
    protected javax.swing.JComponent jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    protected javax.swing.JLabel jLabel5;
    protected javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    protected javax.swing.JTable tblConciliar;
    protected javax.swing.JTable tblLineas;
    protected javax.swing.JTable tblPagos;
    private StatusBar statusBar = new StatusBar();
    // Fin de declaraci�n de variables//GEN-END:variables
    
}