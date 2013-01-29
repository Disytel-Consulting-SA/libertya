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



package org.openXpertya.print;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.PO;
import org.openXpertya.model.X_AD_PrintFormatItem;
import org.openXpertya.model.X_AD_PrintFormatItem_Trl;
import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Language;

/**
 * Descripción de Clase
 *
 *
 * @versión    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MPrintFormatItem extends X_AD_PrintFormatItem {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param AD_PrintFormatItem_ID
     * @param trxName
     */

    public MPrintFormatItem( Properties ctx,int AD_PrintFormatItem_ID,String trxName ) {
        super( ctx,AD_PrintFormatItem_ID,trxName );

        // Default Setting

        if( AD_PrintFormatItem_ID == 0 ) {
            setFieldAlignmentType( FIELDALIGNMENTTYPE_Default );
            setLineAlignmentType( LINEALIGNMENTTYPE_None );
            setPrintFormatType( PRINTFORMATTYPE_Text );
            setPrintAreaType( PRINTAREATYPE_Content );
            setShapeType( SHAPETYPE_NormalRectangle );

            //

            setIsCentrallyMaintained( true );
            setIsRelativePosition( true );
            setIsNextLine( false );
            setIsNextPage( false );
            setIsSetNLPosition( false );
            setIsFilledRectangle( false );
            setIsImageField( false );
            setXSpace( 0 );
            setYSpace( 0 );
            setXPosition( 0 );
            setYPosition( 0 );
            setMaxWidth( 0 );
            setIsFixedWidth( false );
            setIsHeightOneLine( false );
            setMaxHeight( 0 );
            setLineWidth( 1 );
            setArcDiameter( 0 );

            //

            setIsOrderBy( false );
            setSortNo( 0 );
            setIsGroupBy( false );
            setIsPageBreak( false );
            setIsSummarized( false );
            setIsAveraged( false );
            setIsCounted( false );
            setIsMinCalc( false );
            setIsMaxCalc( false );
            setIsVarianceCalc( false );
            setIsDeviationCalc( false );
            setIsRunningTotal( false );
            setImageIsAttached( false );
            setIsSuppressNull( false );
        }
    }    // MPrintFormatItem

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MPrintFormatItem( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MPrintFormatItem

    /** Descripción de Campos */

    private String m_columnName = null;

    /** Descripción de Campos */

    private HashMap m_translationLabel;

    /** Descripción de Campos */

    private HashMap m_translationSuffix;

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MPrintFormatItem.class );

    /**
     * Descripción de Método
     *
     *
     * @param language
     *
     * @return
     */

    public String getPrintName( Language language ) {
        if( (language == null) || Env.isBaseLanguage( language,"AD_PrintFormatItem" )) {
            return getPrintName();
        }

        loadTranslations();

        String retValue = ( String )m_translationLabel.get( language.getAD_Language());

        if( (retValue == null) || (retValue.length() == 0) ) {
            return getPrintName();
        }

        return retValue;
    }    // getPrintName

    /**
     * Descripción de Método
     *
     */

    private void loadTranslations() {
        if( m_translationLabel == null ) {
            m_translationLabel  = new HashMap();
            m_translationSuffix = new HashMap();

            String sql = "SELECT AD_Language, PrintName, PrintNameSuffix FROM AD_PrintFormatItem_Trl WHERE AD_PrintFormatItem_ID=?";

            try {
                PreparedStatement pstmt = DB.prepareStatement( sql );

                pstmt.setInt( 1,getID());

                ResultSet rs = pstmt.executeQuery();

                while( rs.next()) {
                    m_translationLabel.put( rs.getString( 1 ),rs.getString( 2 ));
                    m_translationSuffix.put( rs.getString( 1 ),rs.getString( 3 ));
                }

                rs.close();
                pstmt.close();
            } catch( SQLException e ) {
                log.log( Level.SEVERE,"loadTrl",e );
            }
        }
    }    // loadTranslations

    /**
     * Descripción de Método
     *
     *
     * @param language
     *
     * @return
     */

    public String getPrintNameSuffix( Language language ) {
        if( (language == null) || Env.isBaseLanguage( language,"AD_PrintFormatItem" )) {
            return getPrintNameSuffix();
        }

        loadTranslations();

        String retValue = ( String )m_translationSuffix.get( language.getAD_Language());

        if( (retValue == null) || (retValue.length() == 0) ) {
            return getPrintNameSuffix();
        }

        return retValue;
    }    // getPrintNameSuffix

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isTypeField() {
        return getPrintFormatType().equals( PRINTFORMATTYPE_Field );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isTypeText() {
        return getPrintFormatType().equals( PRINTFORMATTYPE_Text );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isTypePrintFormat() {
        return getPrintFormatType().equals( PRINTFORMATTYPE_PrintFormat );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isTypeImage() {
        return getPrintFormatType().equals( PRINTFORMATTYPE_Image );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isTypeBox() {
        return getPrintFormatType().equals( PRINTFORMATTYPE_Line ) || getPrintFormatType().equals( PRINTFORMATTYPE_Rectangle );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isFieldCenter() {
        return getFieldAlignmentType().equals( FIELDALIGNMENTTYPE_Center );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isFieldAlignLeading() {
        return getFieldAlignmentType().equals( FIELDALIGNMENTTYPE_LeadingLeft );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isFieldAlignTrailing() {
        return getFieldAlignmentType().equals( FIELDALIGNMENTTYPE_TrailingRight );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isFieldAlignBlock() {
        return getFieldAlignmentType().equals( FIELDALIGNMENTTYPE_Block );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isFieldAlignDefault() {
        return getFieldAlignmentType().equals( FIELDALIGNMENTTYPE_Default );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isLineAlignCenter() {
        return getLineAlignmentType().equals( LINEALIGNMENTTYPE_Center );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isLineAlignLeading() {
        return getLineAlignmentType().equals( LINEALIGNMENTTYPE_LeadingLeft );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isLineAlignTrailing() {
        return getLineAlignmentType().equals( LINEALIGNMENTTYPE_TrailingRight );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isHeader() {
        return getPrintAreaType().equals( PRINTAREATYPE_Header );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isContent() {
        return getPrintAreaType().equals( PRINTAREATYPE_Content );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isFooter() {
        return getPrintAreaType().equals( PRINTAREATYPE_Footer );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MPrintFormatItem[" );

        sb.append( "ID=" ).append( getID()).append( ",Name=" ).append( getName()).append( ",Print=" ).append( getPrintName()).append( ", Seq=" ).append( getSeqNo()).append( ",Sort=" ).append( getSortNo()).append( ", Area=" ).append( getPrintAreaType()).append( ", MaxWidth=" ).append( getMaxWidth()).append( ",MaxHeight=" ).append( getMaxHeight()).append( ",OneLine=" ).append( isHeightOneLine()).append( ", Relative=" ).append( isRelativePosition());

        if( isRelativePosition()) {
            sb.append( ",X=" ).append( getXSpace()).append( ",Y=" ).append( getYSpace()).append( ",LineAlign=" ).append( getLineAlignmentType()).append( ",NewLine=" ).append( isNextLine()).append( ",NewPage=" ).append( isPageBreak());
        } else {
            sb.append( ",X=" ).append( getXPosition()).append( ",Y=" ).append( getYPosition());
        }

        sb.append( ",FieldAlign=" ).append( getFieldAlignmentType());

        //

        sb.append( ", Type=" ).append( getPrintFormatType());

        if( isTypeText()) {
            ;
        } else if( isTypeField()) {
            sb.append( ",AD_Column_ID=" ).append( getAD_Column_ID());
        } else if( isTypePrintFormat()) {
            sb.append( ",AD_PrintFormatChild_ID=" ).append( getAD_PrintFormatChild_ID()).append( ",AD_Column_ID=" ).append( getAD_Column_ID());
        } else if( isTypeImage()) {
            sb.append( ",ImageIsAttached=" ).append( isImageIsAttached()).append( ",ImageURL=" ).append( getImageURL());
        }

        //

        sb.append( ", Printed=" ).append( isPrinted()).append( ",SeqNo=" ).append( getSeqNo()).append( ",OrderBy=" ).append( isOrderBy()).append( ",SortNo=" ).append( getSortNo()).append( ",Summarized=" ).append( isSummarized());
        sb.append( "]" );

        return sb.toString();
    }    // toString

    /** Descripción de Campos */

    private static CCache s_columns = new CCache( "AD_PrintFormatItem",200 );

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getColumnName() {
        if( m_columnName == null ) {    // Get Column Name from AD_Column not index
            m_columnName = getColumnName( new Integer( getAD_Column_ID()));
        }

        return m_columnName;
    }    // getColumnName

    /**
     * Descripción de Método
     *
     *
     * @param AD_Column_ID
     *
     * @return
     */

    private static String getColumnName( Integer AD_Column_ID ) {
        if( (AD_Column_ID == null) || (AD_Column_ID.intValue() == 0) ) {
            return null;
        }

        //

        String retValue = ( String )s_columns.get( AD_Column_ID );

        if( retValue == null ) {
            String sql = "SELECT ColumnName FROM AD_Column WHERE AD_Column_ID=?";

            try {
                PreparedStatement pstmt = DB.prepareStatement( sql );

                pstmt.setInt( 1,AD_Column_ID.intValue());

                ResultSet rs = pstmt.executeQuery();

                if( rs.next()) {
                    retValue = rs.getString( 1 );
                    s_columns.put( AD_Column_ID,retValue );
                } else {
                    s_log.log( Level.SEVERE,"getColumnName - Not found AD_Column_ID=" + AD_Column_ID );
                }

                rs.close();
                pstmt.close();
            } catch( SQLException e ) {
                s_log.log( Level.SEVERE,"AD_Column_ID=" + AD_Column_ID,e );
            }
        }

        return retValue;
    }    // getColumnName

    /**
     * Descripción de Método
     *
     *
     * @param format
     * @param AD_Column_ID
     * @param seqNo
     *
     * @return
     */

    public static MPrintFormatItem createFromColumn( MPrintFormat format,int AD_Column_ID,int seqNo ) {
        MPrintFormatItem pfi = new MPrintFormatItem( format.getCtx(),0,null );

        pfi.setAD_PrintFormat_ID( format.getAD_PrintFormat_ID());
        pfi.setClientOrg( format );
        pfi.setAD_Column_ID( AD_Column_ID );
        pfi.setPrintFormatType( PRINTFORMATTYPE_Field );

        // translation is dome by trigger

        String sql = "SELECT c.ColumnName,e.Name,e.PrintName, "    // 1..3
                     + "c.AD_Reference_ID,c.IsKey,c.SeqNo "        // 4..6
                     + "FROM AD_Column c, AD_Element e " + "WHERE c.AD_Column_ID=?" + " AND c.AD_Element_ID=e.AD_Element_ID";

        // translate base entry if single language - trigger copies to trl tables

        Language language = format.getLanguage();
        boolean  trl      = !Env.isMultiLingualDocument( format.getCtx()) &&!language.isBaseLanguage();

        if( trl ) {
            sql = "SELECT c.ColumnName,e.Name,e.PrintName, "    // 1..3
                  + "c.AD_Reference_ID,c.IsKey,c.SeqNo "        // 4..6
                  + "FROM AD_Column c, AD_Element_Trl e " + "WHERE c.AD_Column_ID=?" + " AND c.AD_Element_ID=e.AD_Element_ID" + " AND e.AD_Language=?";
        }

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,AD_Column_ID );

            if( trl ) {
                pstmt.setString( 2,language.getAD_Language());
            }

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                String ColumnName = rs.getString( 1 );

                pfi.setName( rs.getString( 2 ));
                pfi.setPrintName( rs.getString( 3 ));

                int displayType = rs.getInt( 4 );

                if( DisplayType.isNumeric( displayType )) {
                    pfi.setFieldAlignmentType( FIELDALIGNMENTTYPE_TrailingRight );
                } else if( (displayType == DisplayType.Text) || (displayType == DisplayType.Memo) ) {
                    pfi.setFieldAlignmentType( FIELDALIGNMENTTYPE_Block );
                } else {
                    pfi.setFieldAlignmentType( FIELDALIGNMENTTYPE_LeadingLeft );
                }

                boolean isKey = "Y".equals( rs.getString( 5 ));

                //

                if( isKey || ColumnName.startsWith( "Created" ) || ColumnName.startsWith( "Updated" ) || ColumnName.equals( "AD_Client_ID" ) || ColumnName.equals( "AD_Org_ID" ) || ColumnName.equals( "IsActive" ) || (displayType == DisplayType.Button) || (displayType == DisplayType.Binary) || (displayType == DisplayType.ID) || (displayType == DisplayType.Image) || (displayType == DisplayType.RowID) || (seqNo == 0) ) {
                    pfi.setIsPrinted( false );
                    pfi.setSeqNo( 0 );
                } else {
                    pfi.setIsPrinted( true );
                    pfi.setSeqNo( seqNo );
                }

                int idSeqNo = rs.getInt( 6 );    // IsIdentifier SortNo

                if( idSeqNo > 0 ) {
                    pfi.setIsOrderBy( true );
                    pfi.setSortNo( idSeqNo );
                }
            } else {
                s_log.log( Level.SEVERE,"createFromColumn - Not Found AD_Column_ID=" + AD_Column_ID + " Trl=" + trl + " " + language.getAD_Language());
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            s_log.log( Level.SEVERE,"createFromColumn",e );
        }

        if( !pfi.save()) {
            return null;
        }

        // pfi.dump();

        return pfi;
    }    // createFromColumn

    /**
     * Descripción de Método
     *
     *
     * @param To_Client_ID
     * @param AD_PrintFormat_ID
     *
     * @return
     */

    public MPrintFormatItem copyToClient( int To_Client_ID,int AD_PrintFormat_ID ) {
        MPrintFormatItem to = new MPrintFormatItem( p_ctx,0,null );

        MPrintFormatItem.copyValues( this,to );
        to.setClientOrg( To_Client_ID,0 );
        to.setAD_PrintFormat_ID( AD_PrintFormat_ID );
        to.save();

        return to;
    }    // copyToClient

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {

        // Order

        if( !isOrderBy()) {
            setSortNo( 0 );
            setIsGroupBy( false );
            setIsPageBreak( false );
        }

        // Rel Position

        if( isRelativePosition()) {
            setXPosition( 0 );
            setYPosition( 0 );
        } else {
            setXSpace( 0 );
            setYSpace( 0 );
        }

        // Image

        if( isImageField()) {
            setImageIsAttached( false );
            setImageURL( null );
        }

        return true;
    }    // beforeSave

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     * @param success
     *
     * @return
     */

    protected boolean afterSave( boolean newRecord,boolean success ) {

        // Set Translation from Element

        if( newRecord

        // && MClient.get(getCtx()).isMultiLingualDocument()

        && (getPrintName() != null) && (getPrintName().length() > 0) 
        && getAD_Column_ID() != 0) {
        	// Obtener el nombre de traducción del elemento de la columna
        	String sql = "SELECT printname " +
        				 "FROM ad_column as c " +
        				 "INNER JOIN ad_element_trl as e ON e.ad_element_id = c.ad_element_id " +
        				 "WHERE c.ad_column_id = ? AND ad_language = ?";
        	PreparedStatement ps = null;
        	ResultSet rs = null;
        	// Obtengo mis traducciones
        	List<PO> printFormatItemTrls = PO.find(getCtx(), get_TableName()+"_trl", "ad_printformatitem_id = ?", new Object[]{getID()}, null, get_TrxName());
        	// Itero por mis traduciones
        	for (PO trl : printFormatItemTrls) {
        		try{
        			ps = DB.prepareStatement(sql, get_TrxName());
	        		ps.setInt(1, getAD_Column_ID());
	        		ps.setString(2, ((X_AD_PrintFormatItem_Trl)trl).getAD_Language());
	        		rs = ps.executeQuery();
	        		if(rs.next()){
	        			((X_AD_PrintFormatItem_Trl)trl).setPrintName(rs.getString("printname"));
	        			if(!trl.save()){
							throw new Exception("Error al guardar la traduccion del item del formato de impresion");
						}
	        		}
        		} catch(Exception e){
        			log.severe("Error al guardar la traduccion del item del formato de impresion");
        		} finally{
        			try{
        				if(ps != null)ps.close();
        				if(rs != null)rs.close();
        			} catch(Exception e){
        				log.log(Level.SEVERE, "copy Translation - " + sql, e);
        			}
        		}
			}
//            String sql = "UPDATE AD_PrintFormatItem_Trl trl " + "SET PrintName = (SELECT e.PrintName " + "FROM AD_Element_Trl e, AD_Column c " + "WHERE e.AD_Language=trl.AD_Language" + " AND e.AD_Element_ID=c.AD_Element_ID" + " AND c.AD_Column_ID=" + getAD_Column_ID() + ") " + "WHERE AD_PrintFormatItem_ID = " + getID() + " AND EXISTS (SELECT * " + "FROM AD_Element_Trl e, AD_Column c " + "WHERE e.AD_Language=trl.AD_Language" + " AND e.AD_Element_ID=c.AD_Element_ID" + " AND c.AD_Column_ID=" + getAD_Column_ID() + " AND trl.AD_PrintFormatItem_ID = " + getID() + ")" + " AND EXISTS (SELECT * FROM AD_Client " + "WHERE AD_Client_ID=trl.AD_Client_ID AND IsMultiLingualDocument='Y')";
//            int no = DB.executeUpdate( sql,get_TrxName());
//
//            log.fine( "translations updated #" + no );
        }

        return success;
    }    // afterSave
}    // MPrintFormatItem



/*
 *  @(#)MPrintFormatItem.java   23.03.06
 * 
 *  Fin del fichero MPrintFormatItem.java
 *  
 *  Versión 2.2
 *
 */
