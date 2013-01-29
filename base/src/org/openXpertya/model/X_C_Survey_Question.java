/*
 * @(#)X_C_Survey_Question.java   12.oct 2007  Versión 2.2
 *
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son copyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  copyRight (c)  2003-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son copyRight (c) 2005-2006 de Dataware Sistemas S.L., otras son
 * copyright (c) 2005-2006 de Indeos Consultoría S.L., otras son copyright (c) 2005-2006 de Disytel
 * Servicios Digitales S.A., y otras  partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o 
 * mejoradas a partir de código original de  terceros, recogidos en el ADDENDUM  A, sección 3 (A.3)
 * de dicha licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará
 * sujeto a su respectiva licencia original.  
 *    Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.model;

import org.openXpertya.util.KeyNamePair;

//~--- Importaciones JDK ------------------------------------------------------

import java.math.BigDecimal;

import java.sql.ResultSet;

import java.util.Properties;

/** Generated Model for C_Survey_Question
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke (generated)
 *  @version FUNDESLE - 2006-06-29 16:03:39.031 */
public class X_C_Survey_Question extends PO {

/** AD_Table_ID=1000098 */
    public static final int	Table_ID	= 1000098;

/** TableName=C_Survey_Question */
    public static final String	Table_Name	= "C_Survey_Question";

/** Vacio = 3 */
    public static final String	STYLE_Vacio	= "3";

/** Estilo 2 = 2 */
    public static final String	STYLE_Estilo2	= "2";

/** Estilo 1 = 1 */
    public static final String	STYLE_Estilo1	= "1";

    /** Descripción de Campo */
    public static final int	STYLE_AD_Reference_ID	= 1000055;

    /** Descripción de Campo */
    protected static KeyNamePair	Model	= new KeyNamePair(1000098, "C_Survey_Question");

    /** Descripción de Campo */
    protected static BigDecimal	AccessLevel	= new BigDecimal(3);

/**
 * Standard Constructor
 *
 * @param ctx
 * @param C_Survey_Question_ID
 * @param trxName
 */
    public X_C_Survey_Question(Properties ctx, int C_Survey_Question_ID, String trxName) {

        super(ctx, C_Survey_Question_ID, trxName);

/** if (C_Survey_Question_ID == 0)
{
setC_Survey_ID (0);
setC_Survey_Question_ID (0);
setIsBloqued (false);
setIsBoolean (false);
setIsListRef (false);
setIsNumber (false);
setIsRadioButton (false);
setIsText (true);       // Y
setQuestion (null);
}
 */
    }

/**
 * Load Constructor
 *
 * @param ctx
 * @param rs
 * @param trxName
 */
    public X_C_Survey_Question(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }

/**
 * Load Meta Data
 *
 * @param ctx
 *
 * @return
 */
    protected POInfo initPO(Properties ctx) {

        POInfo	poi	= POInfo.getPOInfo(ctx, Table_ID);

        return poi;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("X_C_Survey_Question[").append(getID()).append("]");

        return sb.toString();
    }

    //~--- get methods --------------------------------------------------------

/** Get Reference.
 *
 * @return
System Reference (Pick List) */
    public int getAD_Reference_ID() {

        Integer	ii	= (Integer) get_Value("AD_Reference_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/**
 * Get C_Survey_ID
 *
 * @return
 */
    public int getC_Survey_ID() {

        Integer	ii	= (Integer) get_Value("C_Survey_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/**
 * Get C_Survey_Question_ID
 *
 * @return
 */
    public int getC_Survey_Question_ID() {

        Integer	ii	= (Integer) get_Value("C_Survey_Question_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/**
 * Get Question
 *
 * @return
 */
    public String getQuestion() {
        return (String) get_Value("Question");
    }

/**
 * Get ReSequence
 *
 * @return
 */
    public String getReSeq() {
        return (String) get_Value("ReSeq");
    }

/** Get Sequence.
Method of ordering records;
 *
 * @return
 lowest NUMERIC comes first */
    public int getSeqNo() {

        Integer	ii	= (Integer) get_Value("SeqNo");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/**
 * Get Style
 *
 * @return
 */
    public String getStyle() {
        return (String) get_Value("Style");
    }

/**
 * Get IsBloqued
 *
 * @return
 */
    public boolean isBloqued() {

        Object	oo	= get_Value("IsBloqued");

        if (oo != null) {

            if (oo instanceof Boolean) {
                return ((Boolean) oo).booleanValue();
            }

            return "Y".equals(oo);
        }

        return false;
    }

/**
 * Get IsBoolean
 *
 * @return
 */
    public boolean isBoolean() {

        Object	oo	= get_Value("IsBoolean");

        if (oo != null) {

            if (oo instanceof Boolean) {
                return ((Boolean) oo).booleanValue();
            }

            return "Y".equals(oo);
        }

        return false;
    }

/**
 * Get IsListRef
 *
 * @return
 */
    public boolean isListRef() {

        Object	oo	= get_Value("IsListRef");

        if (oo != null) {

            if (oo instanceof Boolean) {
                return ((Boolean) oo).booleanValue();
            }

            return "Y".equals(oo);
        }

        return false;
    }

/**
 * Get IsNumber
 *
 * @return
 */
    public boolean isNumber() {

        Object	oo	= get_Value("IsNumber");

        if (oo != null) {

            if (oo instanceof Boolean) {
                return ((Boolean) oo).booleanValue();
            }

            return "Y".equals(oo);
        }

        return false;
    }

/**
 * Get IsRadioButton
 *
 * @return
 */
    public boolean isRadioButton() {

        Object	oo	= get_Value("IsRadioButton");

        if (oo != null) {

            if (oo instanceof Boolean) {
                return ((Boolean) oo).booleanValue();
            }

            return "Y".equals(oo);
        }

        return false;
    }

/**
 * Get IsText
 *
 * @return
 */
    public boolean isText() {

        Object	oo	= get_Value("IsText");

        if (oo != null) {

            if (oo instanceof Boolean) {
                return ((Boolean) oo).booleanValue();
            }

            return "Y".equals(oo);
        }

        return false;
    }

/**
 * Get IsTitle
 *
 * @return
 */
    public boolean isTitle() {

        Object	oo	= get_Value("IsTitle");

        if (oo != null) {

            if (oo instanceof Boolean) {
                return ((Boolean) oo).booleanValue();
            }

            return "Y".equals(oo);
        }

        return false;
    }

    //~--- set methods --------------------------------------------------------

/** Set Reference.
 *
 * @param AD_Reference_ID
System Reference (Pick List) */
    public void setAD_Reference_ID(int AD_Reference_ID) {

        if (AD_Reference_ID <= 0) {
            set_Value("AD_Reference_ID", null);
        } else {
            set_Value("AD_Reference_ID", new Integer(AD_Reference_ID));
        }
    }

/**
 * Set C_Survey_ID
 *
 * @param C_Survey_ID
 */
    public void setC_Survey_ID(int C_Survey_ID) {
        set_Value("C_Survey_ID", new Integer(C_Survey_ID));
    }

/**
 * Set C_Survey_Question_ID
 *
 * @param C_Survey_Question_ID
 */
    public void setC_Survey_Question_ID(int C_Survey_Question_ID) {
        set_ValueNoCheck("C_Survey_Question_ID", new Integer(C_Survey_Question_ID));
    }

/**
 * Set IsBloqued
 *
 * @param IsBloqued
 */
    public void setIsBloqued(boolean IsBloqued) {
        set_Value("IsBloqued", new Boolean(IsBloqued));
    }

/**
 * Set IsBoolean
 *
 * @param IsBoolean
 */
    public void setIsBoolean(boolean IsBoolean) {
        set_Value("IsBoolean", new Boolean(IsBoolean));
    }

/**
 * Set IsListRef
 *
 * @param IsListRef
 */
    public void setIsListRef(boolean IsListRef) {
        set_Value("IsListRef", new Boolean(IsListRef));
    }

/**
 * Set IsNumber
 *
 * @param IsNumber
 */
    public void setIsNumber(boolean IsNumber) {
        set_Value("IsNumber", new Boolean(IsNumber));
    }

/**
 * Set IsRadioButton
 *
 * @param IsRadioButton
 */
    public void setIsRadioButton(boolean IsRadioButton) {
        set_Value("IsRadioButton", new Boolean(IsRadioButton));
    }

/**
 * Set IsText
 *
 * @param IsText
 */
    public void setIsText(boolean IsText) {
        set_Value("IsText", new Boolean(IsText));
    }

/**
 * Set IsTitle
 *
 * @param IsTitle
 */
    public void setIsTitle(boolean IsTitle) {
        set_Value("IsTitle", new Boolean(IsTitle));
    }

/**
 * Set Question
 *
 * @param Question
 */
    public void setQuestion(String Question) {

        if (Question == null) {
            throw new IllegalArgumentException("Question is mandatory");
        }

        if (Question.length() > 100) {

            log.warning("Length > 100 - truncated");
            Question	= Question.substring(0, 99);
        }

        set_Value("Question", Question);
    }

/**
 * Set ReSequence
 *
 * @param ReSeq
 */
    public void setReSeq(String ReSeq) {

        if ((ReSeq != null) && (ReSeq.length() > 1)) {

            log.warning("Length > 1 - truncated");
            ReSeq	= ReSeq.substring(0, 0);
        }

        set_Value("ReSeq", ReSeq);
    }

/** Set Sequence.
Method of ordering records;
 *
 * @param SeqNo
 lowest NUMERIC comes first */
    public void setSeqNo(int SeqNo) {
        set_Value("SeqNo", new Integer(SeqNo));
    }

/**
 * Set Style
 *
 * @param Style
 */
    public void setStyle(String Style) {

        if ((Style == null) || Style.equals("1") || Style.equals("2") || Style.equals("3")) {
            ;
        } else {
            throw new IllegalArgumentException("Style Invalid value - Reference_ID=1000055 - 1 - 2 - 3");
        }

        if ((Style != null) && (Style.length() > 1)) {

            log.warning("Length > 1 - truncated");
            Style	= Style.substring(0, 0);
        }

        set_Value("Style", Style);
    }
}



/*
 * @(#)X_C_Survey_Question.java   02.jul 2007
 * 
 *  Fin del fichero X_C_Survey_Question.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
