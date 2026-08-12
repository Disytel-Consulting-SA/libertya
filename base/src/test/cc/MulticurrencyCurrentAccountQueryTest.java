package test.cc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.openXpertya.JasperReport.DataSource.DocumentsDataSource;
import org.openXpertya.cc.CurrentAccountQuery;
import org.openXpertya.util.Env;

class MulticurrencyCurrentAccountQueryTest {

	private static final Timestamp REPORT_DATE = Timestamp.valueOf("2026-06-15 00:00:00");

	@Test
	void currentAccountUsesInvoiceRateAndNativePaymentAmounts() {
		Properties ctx = new Properties();
		Env.setContext(ctx, "$C_Currency_ID", 118);
		CurrentAccountQuery query = new CurrentAccountQuery(ctx, null, null, false, null,
				REPORT_DATE, "A", 100, "C");
		query.setCurrencyID(118);

		String sql = query.getAllDocumentsQuery();

		assertTrue(sql.contains("source_invoice.cintolo_exchange_rate"));
		assertTrue(sql.contains("source_invoice.dateinvoiced"));
		assertTrue(sql.contains("LEFT JOIN LATERAL"));
		assertTrue(sql.contains("source_payment_doctype.doctypekey = 'CRR'"));
		assertTrue(sql.contains("source_payment_doctype.doctypekey = 'VPR'"));
		assertTrue(sql.contains("COALESCE(source_payment.c_charge_id, 0) = 0"));
		assertTrue(sql.contains("COALESCE(source_cashline.c_charge_id, 0) = 0"));
		assertTrue(sql.contains("paymentavailable(d.document_id, '2026-06-15'::timestamp without time zone)"));
		assertTrue(sql.contains("cashlineavailable(d.document_id, '2026-06-15'::timestamp without time zone)"));
		assertTrue(sql.contains("LEFT JOIN c_cash source_cash ON source_cash.c_cash_id = source_cashline.c_cash_id"));
		assertTrue(sql.contains("LEFT JOIN c_allocationhdr source_allocationhdr ON source_allocationhdr.c_allocationhdr_id = d.document_id"));
		assertTrue(sql.contains("CASE WHEN (d.documenttable = 'C_AllocationHdr' AND source_allocationhdr.allocationtype IN ('RCA', 'OPA')) THEN CASE"));
		assertTrue(sql.contains("FROM c_allocationline allocation_line"));
		assertTrue(sql.contains("COALESCE(allocation_hdr.allocationtype, '') NOT IN ('RCA', 'OPA')"));
		assertTrue(sql.contains("source_allocationhdr.allocationtype IN ('RCA', 'OPA')"));
		assertTrue(sql.contains("d.documenttable = 'C_AllocationHdr' AND EXISTS"));
		assertTrue(sql.contains("FROM c_allocationline advance_allocation_line"));
		assertTrue(sql.contains("allocation_line.c_payment_id = advance_allocation_line.c_payment_id"));
		assertTrue(sql.contains("allocation_line.c_cashline_id = advance_allocation_line.c_cashline_id"));
		assertTrue(sql.contains("allocation_line.c_invoice_credit_id = advance_allocation_line.c_invoice_credit_id"));
		assertTrue(sql.contains("FROM c_allocationline advance_credit_allocation_line"));
		assertTrue(sql.contains("advance_credit_allocation_line.c_invoice_credit_id IS NOT NULL"));
		assertTrue(sql.contains("allocation_line.c_invoice_credit_id = advance_credit_allocation_line.c_invoice_credit_id"));
		assertTrue(sql.contains("AND advance_allocation_hdr.allocationtype IN ('RCA', 'OPA')"));
		assertTrue(sql.contains("allocation_line.c_allocationline_id <> advance_allocation_line.c_allocationline_id"));
		assertTrue(sql.contains("LEFT JOIN c_invoice allocated_invoice ON allocated_invoice.c_invoice_id = allocation_line.c_invoice_id"));
		assertTrue(sql.contains("LEFT JOIN c_invoice allocated_credit_invoice ON allocated_credit_invoice.c_invoice_id = allocation_line.c_invoice_credit_id"));
		assertTrue(sql.contains("OR (d.documenttable = 'C_AllocationHdr' AND allocation_line.c_allocationhdr_id = d.document_id)"));
		assertTrue(sql.contains("AND (allocation_line.c_invoice_id IS NOT NULL OR allocation_line.c_invoice_credit_id IS NOT NULL)"));
		assertTrue(sql.contains("AND allocation_hdr.isactive = 'Y'"));
		assertTrue(sql.contains("COUNT(*) AS linecount"));
		assertTrue(sql.contains("document_allocated.linecount = document_allocated.nativecount"));
		assertTrue(sql.contains("document_allocated.linecount = document_allocated.allocatedcount"));
		assertTrue(sql.contains("document_allocated.linecount = 0"));
		assertTrue(sql.contains("COALESCE(document_allocated.nativeamt, 0::numeric) <= ABS(d.amount) - CASE"));
		assertTrue(sql.contains("WHEN (d.documenttable = 'C_AllocationHdr' AND source_allocationhdr.allocationtype IN ('RCA', 'OPA')) THEN GREATEST(ABS(d.amount) - COALESCE(document_allocated.nativeamt, 0::numeric), 0::numeric)"));
		assertTrue(sql.contains("ELSE ABS(d.openamt) END + 0.01::numeric"));
		assertTrue(sql.contains("d.documenttable = 'C_AllocationHdr' AND d.issotrx = 'Y'"));
		assertTrue(sql.contains("d.documenttable = 'C_AllocationHdr' AND d.issotrx = 'N'"));
		assertTrue(sql.contains("currencyconvert(ABS(allocation_line.amount), allocation_hdr.c_currency_id, d.c_currency_id"));
		assertTrue(sql.contains("currencyround(ABS(allocation_line.amount)/allocated_invoice.cintolo_exchange_rate"));
		assertTrue(sql.contains("currencyround(ABS(allocation_line.amount)/allocated_credit_invoice.cintolo_exchange_rate"));
		assertTrue(sql.contains("allocated_invoice.dateinvoiced::timestamp with time zone"));
		assertTrue(sql.contains("allocated_credit_invoice.dateinvoiced::timestamp with time zone"));
		assertTrue(sql.contains("document_allocated.allocatedamt"));
	}

	@Test
	void cutoffRateModeRebuildsAllocationsAndConvertsInvoiceAmountAtReportDate() {
		Properties ctx = new Properties();
		Env.setContext(ctx, "$C_Currency_ID", 118);
		CurrentAccountQuery query = new CurrentAccountQuery(ctx, null, null, false, null,
				REPORT_DATE, "A", 100, "C");
		query.setCurrencyID(118);
		query.setDocumentConvertRate(false);

		String sql = query.getAllDocumentsQuery();

		assertFalse(sql.contains("source_invoice.cintolo_exchange_rate"));
		assertFalse(sql.contains("source_invoice.dateinvoiced"));
		assertTrue(sql.contains("allocated_invoice.cintolo_exchange_rate"));
		assertTrue(sql.contains("FROM c_allocationline allocation_line"));
		assertTrue(sql.contains("source_allocationhdr.allocationtype IN ('RCA', 'OPA')"));
		assertTrue(sql.contains("document_allocated.linecount = 0"));
		assertTrue(sql.contains("currencyconvert(CASE WHEN allocation_hdr.c_currency_id = allocated_invoice.c_currency_id"));
		assertTrue(sql.contains(", allocated_invoice.c_currency_id, 118, '2026-06-15'::timestamp without time zone, COALESCE(allocated_invoice.c_conversiontype_id, 0)"));
		assertTrue(sql.contains(", allocated_credit_invoice.c_currency_id, 118, '2026-06-15'::timestamp without time zone, COALESCE(allocated_credit_invoice.c_conversiontype_id, 0)"));
		assertTrue(sql.contains(", '2026-06-15'::timestamp without time zone, CASE WHEN d.documenttable = 'C_Invoice'"));
		assertTrue(sql.contains("paymentavailable(d.document_id, '2026-06-15'::timestamp without time zone)"));
		assertTrue(sql.contains("cashlineavailable(d.document_id, '2026-06-15'::timestamp without time zone)"));
	}

	@Test
	void conversionRateModeAcceptsCompatibilityParameterValues() {
		assertTrue(CurrentAccountQuery.getDocumentConvertRate("Y", false));
		assertTrue(CurrentAccountQuery.getDocumentConvertRate(CurrentAccountQuery.CONVERSION_RATE_DATE_DOCUMENT, false));
		assertFalse(CurrentAccountQuery.getDocumentConvertRate("N", true));
		assertFalse(CurrentAccountQuery.getDocumentConvertRate(CurrentAccountQuery.CONVERSION_RATE_DATE_CUTOFF, true));
	}

	@Test
	void receiptUsesPersistedInvoiceRateBeforeConversionTableFallback() {
		String sql = new ExposedDocumentsDataSource().getExposedDataSQL();

		assertTrue(sql.contains("COALESCE(i.cintolo_exchange_rate, 0) > 0"));
		assertTrue(sql.contains("currencyRound((al.amount + al.discountamt + al.writeoffamt)/i.cintolo_exchange_rate"));
		assertFalse(sql.contains(" / "));
		assertTrue(sql.contains("i.dateinvoiced::timestamp with time zone"));
		assertTrue(sql.contains("COALESCE(i.c_conversiontype_id, 0)"));
		assertFalse(sql.contains("ah.datetrx::timestamp with time zone"));
	}

	private static class ExposedDocumentsDataSource extends DocumentsDataSource {
		String getExposedDataSQL() {
			return getDataSQL();
		}
	}
}
