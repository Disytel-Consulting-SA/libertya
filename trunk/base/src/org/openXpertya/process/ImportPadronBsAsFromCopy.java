package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.logging.Level;

import org.openXpertya.model.MPreference;
import org.openXpertya.model.X_C_BPartner_Padron_BsAs;
import org.openXpertya.util.CPreparedStatement;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Trx;
import org.openXpertya.util.Util;


public class ImportPadronBsAsFromCopy extends SvrProcess {

        /**
         * Preference para el mantenimiento de la tabla de Padrón, para que no
         * crezca demasiado, se define ésta que valoriza en meses los registros
         * permanentes. Esto significa que si posee valor 1, entonces se guardarán
         * los del mes anterior al actual, si posee valor 2, de dos meses hacia
         * atrás, y así sucesivamente. El resto de registros anteriores se
         * eliminarán.
         */
        private static final String MANTENIMIENTO_PADRON = "MantenimientoPadron";        
        
        private static final String TABLA_PADRON_ALTO_RIESGO = "i_padron_caba_alto_riesgo";
        private static final String TABLA_PADRON_REGIMEN_SIMPLIFICADO = "i_padron_caba_regimen_simplificado";
        private static final String TABLA_PADRON_BS_AS = "i_padron_bs_as";
        
        private int p_AD_Org_ID = 0;
        private int p_ChunkSize = 50000;        
        private String p_NameCsvFile = null;
        private String p_PadronType = null;
        private int ad_Client_ID = 0;
        private int ad_User_ID = 0;
        
        /**     Client to be imported to                */
        private String clientCheck = null;
        
        
        private int partnerFound = 0;
        private int regInserted = 0;
        private int regDeleted; 
        
        private StringBuffer sql;
        private String table_aux = null;
        
         
        @Override
        protected void prepare() {
                ProcessInfoParameter[] para = getParameter();
                for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "AD_Org_ID" )) {
                p_AD_Org_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "NameCsvFile" )) {
                p_NameCsvFile = ( String )para[ i ].getParameter();
            } else if( name.equals( "PadronType" )) {
                p_PadronType = ( String )para[ i ].getParameter();
            } else if( name.equals( "ChunkSize" )) {
                p_ChunkSize = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
                }
                ad_Client_ID = Env.getAD_Client_ID(Env.getCtx());
                ad_User_ID = Env.getAD_User_ID(getCtx());
                setClientCheck(" AND AD_Client_ID=" + ad_Client_ID);
        }
        
        @Override
        protected String doIt() throws Exception {
        	
        	/* Seleccionar tabla auxiliar segun tipo de padron */
        	if( p_PadronType.compareTo(X_C_BPartner_Padron_BsAs.PADRONTYPE_PadrónBsAs) == 0 ) {
        		table_aux = ImportPadronBsAsFromCopy.TABLA_PADRON_BS_AS;
            } else if( p_PadronType.compareTo(X_C_BPartner_Padron_BsAs.PADRONTYPE_PadrónDeAltoRiesgoCABA) == 0 ) {
            	table_aux = ImportPadronBsAsFromCopy.TABLA_PADRON_ALTO_RIESGO;
            } else if( p_PadronType.compareTo(X_C_BPartner_Padron_BsAs.PADRONTYPE_RégimenSimplificadoCABA) == 0 ) {
            	table_aux = ImportPadronBsAsFromCopy.TABLA_PADRON_REGIMEN_SIMPLIFICADO;
            } else {
                log.log( Level.SEVERE,"Unknown Table for: " + p_PadronType );
            }
    	
            /** Se elimina el contenido de la tabla temporal */
            sql = new StringBuffer();
            sql.append("DELETE FROM " + table_aux );
            DB.executeUpdate(sql.toString());
                       
            /** Se copia el contenido del padrón a la tabla temporal */
            long time  = System.currentTimeMillis();
            sql = new StringBuffer();
            sql.append("COPY " + table_aux + " FROM '"+ getPath() + p_NameCsvFile + "' WITH DELIMITER '" + getSeparatorCharacterCSV() + "'");
            DB.executeUpdate(sql.toString());
            time = System.currentTimeMillis() - time;
            log.info("Se importó el archivo " + p_NameCsvFile + " en " + time + " ms");
                       
            /** Se ejecuta el proceso de mantenimiento de padrón eliminando aquellos padrones que ya no se usan */
            regDeleted = maintainPadronTable();
            
            /** Se insertan los registros a la tabla c_bpartner_padron_bsas */
            time  = System.currentTimeMillis();             
            actualizarPadron();
            time = System.currentTimeMillis() - time;
            log.info("Se actualizó el padrón en " + time + " ms");
            
            /** Se actualiza el campo c_bpartner_id de la tabla c_bpartner_padron_bsas*/
            updateCBPartner();

            log.log (Level.SEVERE,"doIt - Entidades Comerciales NO Encontradas =" + (regInserted - partnerFound));
            
            
            addLog (0, null, new BigDecimal (regDeleted), "Registros eliminados por Mantenimiento de Padron");
            addLog (0, null, new BigDecimal (partnerFound), "Entidades Comerciales Encontrados");
            addLog (0, null, new BigDecimal (regInserted - partnerFound), "Entidades Comerciales No Encontrados");
            addLog (0, null, new BigDecimal (regInserted), "Entidades Comerciales insertados");

            return "Proceso finalizado satisfactoriamente";
        }

        private void updateCBPartner() {
                sql = new StringBuffer();
                sql.append("UPDATE c_bpartner_padron_bsas i \n");
                sql.append("SET     c_bpartner_id = \n");
                sql.append("       (SELECT b.c_bpartner_id \n");
                sql.append("       FROM    c_bpartner b \n");
                sql.append("       WHERE   REPLACE(b.taxid,'-','') = i.cuit \n");
                sql.append("       LIMIT 1) \n");
                sql.append("WHERE  cuit IN \n");
                sql.append("                ( SELECT REPLACE(taxid,'-','') \n");
                sql.append("                FROM    c_bpartner \n");
                sql.append("                )");
                partnerFound = DB.executeUpdate(sql.toString());
                log.log (Level.SEVERE,"doIt - Entidades Comerciales Encontradas =" + partnerFound);
        }

        private String getPath(){
                /**
                 * El nombre de AD_Preference es PathPadronCSV
                 */
                String preference = MPreference.searchCustomPreferenceValue("PathPadronCSV", getAD_Client_ID(), Env.getAD_Org_ID(getCtx()),Env.getAD_User_ID(getCtx()), true);
                if(Util.isEmpty(preference, true)){
                        throw new IllegalArgumentException( "@PathPadronCSVNotFound@" ); 
                        //Error al cargar la preferencia "PathPadronCSV"
                }
                return preference;
        }
        
        private String getSeparatorCharacterCSV(){
                /**
                 * El nombre de AD_Preference es SeparadorDeCampoEnCSVPadron
                 */
                String preference = MPreference.searchCustomPreferenceValue("SeparadorDeCampoEnCSVPadron", getAD_Client_ID(), Env.getAD_Org_ID(getCtx()),Env.getAD_User_ID(getCtx()), true);
                if(Util.isEmpty(preference, true)){
                        throw new IllegalArgumentException( "@SeparadorDeCampoEnCSVPadronNotFound@" ); 
                        //Error al cargar la preferencia "SeparadorDeCampoEnCSVPadron"
                }
                return preference;
        }
        
        /**
         * Mantenimiento de la tabla de padron para que no crezca en cada
         * importación ya que no debe actualizar por importaciones que pisen por
         * cuit los datos actuales
         * 
         * @throws Exception
         */
        protected int maintainPadronTable() throws Exception{
                String updatePadron = MPreference.searchCustomPreferenceValue(
                                MANTENIMIENTO_PADRON, getAD_Client_ID(), Env.getAD_Org_ID(getCtx()),
                                Env.getAD_User_ID(getCtx()), true);
                Integer padron = 0;
                try {
                        padron = Integer.parseInt(updatePadron);
                } catch (Exception e) {
                        throw e;
                }
                padron = padron * -1;
                // Eliminación de registros anteriores a los meses de tolerancia hacia atrás
                String sql = "DELETE FROM " + X_C_BPartner_Padron_BsAs.Table_Name
                                + " WHERE date_trunc('month',?::date) >= date_trunc('month',fecha_desde) AND padrontype = '" + p_PadronType + "' "+getClientCheck();
                Calendar toleranceDate = Calendar.getInstance();
                toleranceDate.setTimeInMillis(Env.getDate().getTime());
                toleranceDate.add(Calendar.MONTH, padron);
                PreparedStatement ps = new CPreparedStatement(
                                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, sql,
                                null, true);
                ps.setTimestamp(1, new Timestamp(toleranceDate.getTimeInMillis()));
                return ps.executeUpdate();
        }
        
        protected String getClientCheck() {
                return clientCheck;
        }

        protected void setClientCheck(String clientCheck) {
                this.clientCheck = clientCheck;
        }
        
        /**
         * Copia registros desde la tabla temporal a la
         * tabla c_bpartner_padron_bsas
         * 
         * @throws Exception
         */
        protected void actualizarPadron(){
                long time;      
                String trxName = "";
                CallableStatement cs = null;

                int qty = contarRegistrosAux();
                
                String sql = "{ call update_padron_from_" + table_aux + "(?, ?, ?, ?, ?, ?) }";

        for(int offset = 0; offset < qty; offset += p_ChunkSize)
        {

                try {
                        time = System.currentTimeMillis();                              
                        trxName = Trx.createTrxName();
                        Trx.getTrx(trxName).start();
                        
                        cs = DB.prepareCall(sql, ResultSet.CONCUR_UPDATABLE, true, trxName);                            

                        int i = 0;
                        DB.setParameter(cs, ++i, p_AD_Org_ID);
                        DB.setParameter(cs, ++i, ad_Client_ID);
                        DB.setParameter(cs, ++i, ad_User_ID);
                        DB.setParameter(cs, ++i, p_PadronType);
                        DB.setParameter(cs, ++i, offset);
                        DB.setParameter(cs, ++i, p_ChunkSize);
                        
                        cs.executeUpdate();

                                Trx.getTrx(trxName).commit();
                                cs.close();                             
                                
                                time = System.currentTimeMillis() - time;
                                log.info("Se procesaron " + p_ChunkSize + " registros en " + time + " ms");
                        }
                        catch (Exception e) {
                                Trx.getTrx(trxName).rollback();
                                log.log( Level.SEVERE,"actualizarPadron: " + e );                               
                        }
                        finally {
                                Trx.getTrx(trxName).close();
                        }            
        }
                        
                log.log (Level.SEVERE,"doIt - Registros Pasados al sistema =" + qty);
                regInserted = qty;
        }
        
        protected int contarRegistrosAux(){
                int c = 0;
                String sql = "select coalesce(count(*),0) from " + table_aux;
                PreparedStatement ps = DB.prepareStatement(sql.toString());
                
                try {
                        ResultSet rs = ps.executeQuery();
                        if(rs.next())
                                c = rs.getInt(1);
                        rs.close();
                        ps.close();
                } catch (Exception e) {
                        log.log( Level.SEVERE,"contarRegistrosAux: " + e );                     
                }
                                        
                return c;
        }

}