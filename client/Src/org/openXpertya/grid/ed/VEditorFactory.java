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



package org.openXpertya.grid.ed;

import java.util.logging.Level;

import org.compiere.swing.CLabel;
import org.openXpertya.model.MAccountLookup;
import org.openXpertya.model.MField;
import org.openXpertya.model.MLocationLookup;
import org.openXpertya.model.MLocatorLookup;
import org.openXpertya.model.MPAttributeLookup;
import org.openXpertya.model.MTab;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DisplayType;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VEditorFactory {

    /**
     * Descripción de Método
     *
     *
     * @param mField
     * @param tableEditor
     *
     * @return
     */

    public static VEditor getEditor( MField mField,boolean tableEditor ) {
        return getEditor( null,mField,tableEditor );
    }    // getEditor

    /**
     * Descripción de Método
     *
     *
     * @param mTab
     * @param mField
     * @param tableEditor
     *
     * @return
     */

    public static VEditor getEditor( MTab mTab,MField mField,boolean tableEditor ) {
        
    	if( mField == null ) {
            return null;
        }

        VEditor editor      = null;
        int     displayType = mField.getDisplayType();
        String  columnName  = mField.getColumnName();
        boolean mandatory   = mField.isMandatory( false );    // no context check
        boolean readOnly    = mField.isReadOnly();
        boolean updateable  = mField.isUpdateable();
        int     WindowNo    = mField.getWindowNo();

        // Not a Field
       
        if( mField.isHeading()) {
            return null;
        }

        // String (clear/password)

        if( (displayType == DisplayType.String) || ( tableEditor && ( (displayType == DisplayType.Text) || (displayType == DisplayType.TextLong) ) ) ) {
            if( mField.isEncryptedField()) {
                VPassword vs = new VPassword( columnName,mandatory,readOnly,updateable,mField.getDisplayLength(),mField.getFieldLength(),mField.getVFormat());

                vs.setName( columnName );
                vs.setField( mField );
                editor = vs;
            }else {
                VString vs = new VString( columnName,mandatory,readOnly,updateable,mField.getDisplayLength(),mField.getFieldLength(),mField.getVFormat(),mField.getObscureType());

                vs.setName( columnName );
                vs.setField( mField );
                editor = vs;
            }
        }

        // Lookup

        else if( DisplayType.isLookup( displayType ) || (displayType == DisplayType.ID) ) {
        	VLookup vl=null;
        	if(columnName.compareTo("M_Product_ID")==0){
        		vl = new VLookup( columnName,mandatory,readOnly,updateable,mField.getLookup(),mTab);
        	}
        	else{
        		vl = new VLookup( columnName,mandatory,readOnly,updateable,mField.getLookup());
        	}

            vl.setName( columnName );
            vl.setField( mField );
            editor = vl;
        }

        // Eloy Gomez:
        // El editor VBinary esta configurado para que guarde los datos segun lo hacen los adjuntos, esto es, en labla D_File_Archive y D_File.
        // Al usar este editor, lo que queremos es que guarde los dato binarios en la tabla actual.
        // Por eso, usaremos el editor VFile, que es mas correcto.
        
        //Binary
//        else if( displayType == DisplayType.Binary ) {
//           log.fine("Estoy en el binary, d_file_archive A="+Env.getContextAsInt(Env.getCtx(), WindowNo, "D_File_Archive_ID")+", B="+mTab.getRecord_ID()+", C="+mTab.get_ValueAsString("D_File_Archive_ID"));
//           VBinary vn = new VBinary( columnName,mandatory,readOnly,updateable,displayType,mField.getHeader(),Env.getContextAsInt(Env.getCtx(), WindowNo, "D_File_Archive_ID"),Env.getContextAsInt(Env.getCtx(), WindowNo, "D_File_ID"),mTab);	
//          // vn.setRange( mField.getValueMin(),mField.getValueMax());
//           vn.setName( columnName );
//           vn.setField( mField );
//           editor = vn;
//        }
   
//    	File Path / Name
        else if( displayType == DisplayType.Binary ) {
			VFile file = new VFile (columnName, mandatory, readOnly, updateable, 
				displayType == DisplayType.Binary);
			file.setName(columnName);
			file.setField(mField);
			editor = file;
		}        

        
        // Number

        else if( DisplayType.isNumeric( displayType )) {
            VNumber vn = new VNumber( columnName,mandatory,readOnly,updateable,displayType,mField.getHeader());

            vn.setRange( mField.getValueMin(),mField.getValueMax());
            vn.setName( columnName );
            vn.setField( mField );
            editor = vn;
        }

        // YesNo

        else if( displayType == DisplayType.YesNo ) {
            VCheckBox vc = new VCheckBox( columnName,mandatory,readOnly,updateable,mField.getHeader(),mField.getDescription(),tableEditor );

            vc.setName( columnName );
            vc.setField( mField );
            editor = vc;
        }

        // Text (single row)

        else if( displayType == DisplayType.Text ) {
            VText vt = new VText( columnName,mandatory,readOnly,updateable,mField.getDisplayLength(),mField.getFieldLength());

            vt.setName( columnName );
            vt.setField( mField );
            editor = vt;
        }

        // Memo (single row)

        else if( displayType == DisplayType.Memo ) {
            VMemo vt = new VMemo( columnName,mandatory,readOnly,updateable,mField.getDisplayLength(),mField.getFieldLength());

            vt.setName( columnName );
            vt.setField( mField );
            editor = vt;
        }

        // Date

        else if( DisplayType.isDate( displayType )) {
            if( displayType == DisplayType.DateTime ) {
                readOnly = true;
            }

            VDate vd = new VDate( columnName,mandatory,readOnly,updateable,displayType,mField.getHeader());

            vd.setName( columnName );
            vd.setField( mField );
            editor = vd;
        }

        // Location

        else if( displayType == DisplayType.Location ) {
            VLocation loc = new VLocation( columnName,mandatory,readOnly,updateable,( MLocationLookup )mField.getLookup());

            loc.setName( columnName );
            loc.setField( mField );
            editor = loc;
        }

        // Locator

        else if( displayType == DisplayType.Locator ) {
            VLocator loc = new VLocator( columnName,mandatory,readOnly,updateable,( MLocatorLookup )mField.getLookup(),WindowNo );

            loc.setName( columnName );
            loc.setField( mField );
            editor = loc;
        }

        // Account

        else if( displayType == DisplayType.Account ) {
            VAccount acct = new VAccount( columnName,mandatory,readOnly,updateable,( MAccountLookup )mField.getLookup(),mField.getHeader());

            acct.setName( columnName );
            acct.setField( mField );
            editor = acct;
        }

        // Button

        else if( displayType == DisplayType.Button ) {
            VButton button = new VButton( columnName,mandatory,readOnly,updateable,mField.getHeader(),mField.getDescription(),mField.getHelp(),mField.getAD_Process_ID(), mField.getVO().AD_Reference_Value_ID);

            button.setName( columnName );
            button.setField( mField );
            editor = button;
        }

        // Assignment

        else if( displayType == DisplayType.Assignment ) {
            VAssignment assign = new VAssignment( mandatory,readOnly,updateable );

            assign.setName( columnName );
            assign.setField( mField );
            editor = assign;
        }

        // Color

        else if( displayType == DisplayType.Color ) {
            VColor color = new VColor( mTab,mandatory,readOnly );

            color.setName( columnName );
            color.setField( mField );
            editor = color;
        }

        // Image

        else if( displayType == DisplayType.Image ) {
            VImage image = new VImage( WindowNo );

            image.setName( columnName );
            image.setField( mField );
            editor = image;
        }

        // PAttribute

        else if( displayType == DisplayType.PAttribute ) {
            VPAttribute attrib = new VPAttribute( mandatory,readOnly,updateable,WindowNo,( MPAttributeLookup )mField.getLookup(),mTab != null ? mTab.getTabNo() : 0,columnName);

            attrib.setName( columnName );
            attrib.setField( mField );
            editor = attrib;
        }

        // Long Text (CLob)

        else if( displayType == DisplayType.TextLong ) {
            VTextLong vt = new VTextLong( columnName,mandatory,readOnly,updateable,mField.getDisplayLength(),mField.getFieldLength());

            vt.setName( columnName );
            vt.setField( mField );
            editor = vt;
        } else {
            log.log( Level.SEVERE,"VEditorFactory.getEditor - " + columnName + " - Unknown Type: " + displayType );
        }

        return editor;
    }    // getEditor

    /**
     * Descripción de Método
     *
     *
     * @param mField
     *
     * @return
     */

    public static CLabel getLabel( MField mField ) {
        if( mField == null ) {
            return null;
        }

        int displayType = mField.getDisplayType();

        // No Label for FieldOnly, CheckBox, Button

        if( mField.isFieldOnly() || (displayType == DisplayType.YesNo) || (displayType == DisplayType.Button) ) {
            return null;
        }

        //

        CLabel label = new CLabel( mField.getHeader(),mField.getDescription());

        label.setName( mField.getColumnName());

        // label.setFont(CompierePLAF.getFont_Label());
        // label.setForeground(CompierePLAF.getTextColor_Label());

        return label;
    }    // getLabel

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VEditorFactory.class );
}    // VEditorFactory



/*
 *  @(#)VEditorFactory.java   02.07.07
 * 
 *  Fin del fichero VEditorFactory.java
 *  
 *  Versión 2.2
 *
 */
