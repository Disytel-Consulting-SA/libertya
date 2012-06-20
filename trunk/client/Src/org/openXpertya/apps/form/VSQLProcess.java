/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.apps.form;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;
import java.util.logging.Level;

import javax.swing.JPanel;

import org.compiere.swing.CButton;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTextArea;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.grid.ed.VText;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VSQLProcess extends JPanel implements FormPanel,ActionListener {

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     * @param frame
     */

    public void init( int WindowNo,FormFrame frame ) {
        log.info( "VSQLProcess.init" );
        m_WindowNo = WindowNo;
        m_frame    = frame;

        try {
            jbInit();
            frame.getContentPane().add( this,BorderLayout.CENTER );

            // frame.getContentPane().add(confirmPanel, BorderLayout.SOUTH);

        } catch( Exception e ) {
            log.log( Level.SEVERE,"VSQLProcess.init",e );
        }
    }    // init

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        if( m_frame != null ) {
            m_frame.dispose();
        }

        m_frame = null;
    }    // dispose

    /** Descripción de Campos */

    private int m_WindowNo = 0;

    /** Descripción de Campos */

    private FormFrame m_frame;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VSQLProcess.class );

    /** Descripción de Campos */

    private static final String[] DML_KEYWORDS = new String[]{ "SELECT","UPDATE","DELETE","TRUNCATE" };

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    private CPanel northPanel = new CPanel();

    /** Descripción de Campos */

    private CLabel sqlLabel = new CLabel( "SQL" );

    /** Descripción de Campos */

    private VText sqlField = new VText( "SQL",false,false,true,3000,9000 );

    /** Descripción de Campos */

    private JPanel centerPanel = new JPanel();

    /** Descripción de Campos */

    private BorderLayout centerLayout = new BorderLayout();

    /** Descripción de Campos */

    private BorderLayout northLayout = new BorderLayout();

    /** Descripción de Campos */

    private CTextArea resultField = new CTextArea( 20,60 );

    /** Descripción de Campos */

    private CButton sqlButton = ConfirmPanel.createProcessButton( true );

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    void jbInit() throws Exception {
        this.setLayout( mainLayout );
        mainLayout.setHgap( 5 );
        mainLayout.setVgap( 5 );

        //

        this.add( northPanel,BorderLayout.NORTH );
        northLayout.setHgap( 5 );
        northLayout.setVgap( 5 );
        northPanel.setLayout( northLayout );
        sqlLabel.setText( "SQL" );
        northPanel.add( sqlLabel,BorderLayout.WEST );

        //

        northPanel.add( sqlField,BorderLayout.CENTER );
        sqlButton.addActionListener( this );
        northPanel.add( sqlButton,BorderLayout.EAST );

        //

        this.add( centerPanel,BorderLayout.CENTER );
        centerPanel.setLayout( centerLayout );
        centerLayout.setHgap( 0 );
        resultField.setReadWrite( false );
        centerPanel.add( resultField,BorderLayout.CENTER );
    }    // jbInit

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        resultField.setText( processStatements( sqlField.getText(),false ));
    }    // actionedPerformed

    /**
     * Descripción de Método
     *
     *
     * @param sqlStatements
     * @param allowDML
     *
     * @return
     */

    public static String processStatements( String sqlStatements,boolean allowDML ) {
        if( (sqlStatements == null) || (sqlStatements.length() == 0) ) {
            return "";
        }

        StringBuffer result = new StringBuffer();

        //

        StringTokenizer st = new StringTokenizer( sqlStatements,";",false );

        while( st.hasMoreTokens()) {
            result.append( processStatement( st.nextToken(),allowDML ));
            result.append( Env.NL );
        }

        //

        return result.toString();
    }    // processStatements

    /**
     * Descripción de Método
     *
     *
     * @param sqlStatement
     * @param allowDML
     *
     * @return
     */

    public static String processStatement( String sqlStatement,boolean allowDML ) {
        if( sqlStatement == null ) {
            return "";
        }

        StringBuffer sb    = new StringBuffer();
        char[]       chars = sqlStatement.toCharArray();

        for( int i = 0;i < chars.length;i++ ) {
            char c = chars[ i ];

            if( Character.isWhitespace( c )) {
                sb.append( ' ' );
            } else {
                sb.append( c );
            }
        }

        String sql = sb.toString().trim();

        if( sql.length() == 0 ) {
            return "";
        }

        //

        StringBuffer result = new StringBuffer( "SQL> " ).append( sql ).append( Env.NL );

        if( !allowDML ) {
            boolean error = false;
            String  SQL   = sql.toUpperCase();

            for( int i = 0;i < DML_KEYWORDS.length;i++ ) {
                if( SQL.startsWith( DML_KEYWORDS[ i ] + " " ) || (SQL.indexOf( " " + DML_KEYWORDS[ i ] + " " ) != -1) || (SQL.indexOf( "(" + DML_KEYWORDS[ i ] + " " ) != -1) ) {
                    result.append( "===> ERROR: Not Allowed Keyword " ).append( DML_KEYWORDS[ i ] ).append( Env.NL );
                    error = true;
                }
            }

            if( error ) {
                return result.toString();
            }
        }    // !allowDML

        // Process

        Connection conn = DB.createConnection( true,Connection.TRANSACTION_READ_COMMITTED );
        Statement stmt = null;

        try {
            stmt = conn.createStatement();

            boolean OK    = stmt.execute( sql );
            int     count = stmt.getUpdateCount();

            if( count == -1 ) {
                result.append( "---> ResultSet" );
            } else {
                result.append( "---> Result=" ).append( count );
            }
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"process statement: " + sql + " - " + e.toString());
            result.append( "===> " ).append( e.toString());
        }

        // Clean up

        try {
            stmt.close();
        } catch( SQLException e1 ) {
            log.log( Level.SEVERE,"processStatement - close statement",e1 );
        }

        stmt = null;

        try {
            conn.close();
        } catch( SQLException e2 ) {
            log.log( Level.SEVERE,"processStatement - close connection",e2 );
        }

        conn = null;

        //

        result.append( Env.NL );

        return result.toString();
    }    // processStatement
}    // VSQLProcess



/*
 *  @(#)VSQLProcess.java   02.07.07
 * 
 *  Fin del fichero VSQLProcess.java
 *  
 *  Versión 2.2
 *
 */
