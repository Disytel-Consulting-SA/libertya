package org.openXpertya.process;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.X_C_Survey_Question;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;


public class SurveyQuestionReorder extends SvrProcess {

	/** Descripcion de Campos */
    private int p_Record_ID = 0;
    private Properties m_ctx = Env.getCtx();
    
	protected void prepare() {
		p_Record_ID = getRecord_ID();
	}

	protected String doIt() throws Exception {
		
		//Obtenemos la encuesta a reordenar
		X_C_Survey_Question question = new X_C_Survey_Question(m_ctx,p_Record_ID,get_TrxName());
		int encuesta = question.getC_Survey_ID();
		
		// Recorremos los campos los normalizamos de 10 en 10
		String sql = "SELECT C_Survey_Question_ID FROM C_Survey_Question WHERE C_Survey_ID=? Order By SeqNo";
		
		int sequencia = 10;
        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,encuesta );

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
            	// reordenamos
            	String sqlUpdate = "UPDATE C_Survey_Question SET SeqNo = " + sequencia + " WHERE C_Survey_Question_ID=" + rs.getInt(1);
            	int updated = DB.executeUpdate( sqlUpdate );

                if( updated != 1 ) {
                    log.warning( "Pregunta no reordenada" );
                }
                sequencia = sequencia + 10;
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );
        }
        
        // Como no refresca los cambios de la secuencia y no he encontrado ningun metodo que refresque la ventana:
        // abrimos la ventana de nuevo
        /*
        MQuery m_query = new MQuery("C_Survey_Question");
		m_query.addRestriction("C_Survey_ID",MQuery.EQUAL,encuesta);
		AEnv.zoom(m_query);
        
        // Recojemos la ventana antigua
		ProcessInfo pi = getProcessInfo();
		int ventana =  pi.getWindowNo();
		
		// Se cierra la ventana antigua
		Window frame = Env.getWindow(ventana);
		frame.dispose();
		*/
		
		return null;
	}
}