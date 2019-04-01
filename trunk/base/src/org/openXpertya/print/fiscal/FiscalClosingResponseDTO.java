package org.openXpertya.print.fiscal;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.openXpertya.util.Env;

public class FiscalClosingResponseDTO {

	/** Tipo de cierre: X o Z */
	public String closingType;
	
	/** Fecha */
	public Timestamp closingDate = Env.getDate();
	
	/** Última NC B/C emitida */
	public Integer creditnote_bc_lastemitted = 0;
	
	/** Última NC A emitida */
	public Integer creditnote_a_lastemitted = 0;

	/** Importe en NC */
	public BigDecimal creditnoteamt = BigDecimal.ZERO;
	
	/** Importe Gravado en NC */
	public BigDecimal creditnotegravadoamt = BigDecimal.ZERO;

	/** Importe No Gravado en NC */
	public BigDecimal creditnotenogravadoamt = BigDecimal.ZERO;
	
	/** Importe Exento en NC */
	public BigDecimal creditnoteexemptamt = BigDecimal.ZERO;
	
	/** Importe en Impuestos Internos de NC */
	public BigDecimal creditnoteinternaltaxamt = BigDecimal.ZERO;
	
	/** Importe en Impuestos No Inscriptos de NC */
	public BigDecimal creditnotenotregisteredtaxamt = BigDecimal.ZERO;
	
	/** Importe de Percepciones de NC */
	public BigDecimal creditnoteperceptionamt = BigDecimal.ZERO;
	
	/** Importe de Impuestos Totales de NC */
	public BigDecimal creditnotetaxamt = BigDecimal.ZERO;
	
	/** Número de Cierre Fiscal */
	public Integer fiscalclosingno = 0;
	
	/** Última Factura A emitida */
	public Integer fiscaldocument_a_lastemitted = 0;
	
	/** Importe Total de Facturas */
	public BigDecimal fiscaldocumentamt = BigDecimal.ZERO;
	
	/** Importe Gravado en Facturas */
	public BigDecimal fiscaldocumentgravadoamt = BigDecimal.ZERO;

	/** Importe No Gravado en Facturas */
	public BigDecimal fiscaldocumentnogravadoamt = BigDecimal.ZERO;
	
	/** Importe Exento en Facturas */
	public BigDecimal fiscaldocumentexemptamt = BigDecimal.ZERO;
	
	/** Última Factura B/C emitida */
	public Integer fiscaldocument_bc_lastemitted = 0;
	
	/** Importe en Impuestos Internos de Facturas */
	public BigDecimal fiscaldocumentinternaltaxamt = BigDecimal.ZERO;
	
	/** Importe en Impuestos No Inscriptos de Facturas */
	public BigDecimal fiscaldocumentnotregisteredtaxamt = BigDecimal.ZERO;
	
	/** Importe de Percepciones de Facturas */
	public BigDecimal fiscaldocumentperceptionamt = BigDecimal.ZERO;
	
	/** Importe de Impuestos Totales */
	public BigDecimal fiscaldocumenttaxamt = BigDecimal.ZERO;

	/** Importe de Documentos no fiscales homologados */
	public BigDecimal nofiscalhomologatedamt = BigDecimal.ZERO;
	
	/** Cantidad de NC canceladas */
	public Integer qtycanceledcreditnote = 0;
	
	/** Cantidad de Facturas canceladas */
	public Integer qtycanceledfiscaldocument = 0;
	
	/** Cantidad de NC A emitidas */
	public Integer qtycreditnotea = 0;
	
	/** Cantidad de NC B/C emitidas */
	public Integer qtycreditnotebc = 0;
	
	/** Cantidad de NC emitidas */
	public Integer qtycreditnote = 0;
	
	/** Cantidad de Facturas emitidas */
	public Integer qtyfiscaldocument = 0;
	
	/** Cantidad de Facturas A emitidas */
	public Integer qtyfiscaldocumenta = 0;
	
	/** Cantidad de Facturas B/C emitidas */
	public Integer qtyfiscaldocumentbc = 0;
	
	/** Cantidad de Documentos no fiscales emitidos */
	public Integer qtynofiscaldocument = 0;
	
	/** Cantidad de Documentos no fiscales homologados emitidos */
	public Integer qtynofiscalhomologated = 0;

	public FiscalClosingResponseDTO() {
		// TODO Auto-generated constructor stub
	}
}
