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
