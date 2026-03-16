-- 20260123 Fix correspondiente al micro de facturacion (metadata faltante)
update libertya.ad_system set dummy = (SELECT libertya.addcolumnifnotexists('C_POSJournal','iscontingencia','character(1)'));

-- Fix A-400 Inventario fisico y anulación – Comportamiento incorrecto del inventario físico al utilizar el tipo “Sobrescribir”
ALTER TABLE IF EXISTS libertya.m_inventory ADD inventorytype bpchar(1) DEFAULT 'D'::bpchar NOT NULL;
ALTER TABLE IF EXISTS libertya.m_inventory ALTER COLUMN inventorytype SET STORAGE EXTENDED;


-- 2026-02-13
create or replace
function libertya.getfechaconversion(p_timestamp timestamp without time zone,
p_cancelamismamoneda character)
 returns date
 language plpgsql
as $function$ declare fecha_base DATE := (p_timestamp - interval '1 day')::DATE;
dia_semana INT;
begin if coalesce(p_CancelaMismaMoneda, 'N') = 'N' then return p_timestamp;
end if;
dia_semana := extract(DOW from fecha_base);
if dia_semana = 0 then return fecha_base - interval '2 day';
elsif dia_semana = 6 then return fecha_base - interval '1 day';
else return fecha_base;
end if;
end;
$function$
;

-- merge org.libertya.core.micro.r2993.dev.import_facturas_proveedor_afip v3.0 (upgrade_from_2.0)
update ad_system set dummy = (SELECT addcolumnifnotexists('i_vendor_invoice_import','tipoidentificacionrecep','varchar(2)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_vendor_invoice_import','numeroidentificacionrecep','varchar(20)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_vendor_invoice_import','netogravado_0','numeric(20, 2)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_vendor_invoice_import','netogravado_2_5','numeric(20, 2)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_vendor_invoice_import','iva_2_5','numeric(20, 2)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_vendor_invoice_import','netogravado_5','numeric(20, 2)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_vendor_invoice_import','iva_5','numeric(20, 2)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_vendor_invoice_import','netogravado_10_5','numeric(20, 2)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_vendor_invoice_import','iva_10_5','numeric(20, 2)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_vendor_invoice_import','netogravado_21','numeric(20, 2)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_vendor_invoice_import','iva_21','numeric(20, 2)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_vendor_invoice_import','netogravado_27','numeric(20, 2)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_vendor_invoice_import','iva_27','numeric(20, 2)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_vendor_invoice_import','netogravadototal','numeric(20, 2)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_vendor_invoice_import','otrostributos','numeric(20, 2)'));

INSERT INTO libertya.ad_preference (ad_preference_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, ad_window_id, ad_user_id, "attribute", value)
VALUES(nextval('seq_ad_preference'), 1010016, 0, 'Y', current_timestamp, 100, current_timestamp, 100, NULL, NULL, 'Default_OtrosTributosTax_Vendors', '01 Percepcion IVA');

UPDATE libertya.ad_preference SET value='IVA 0%' WHERE attribute='Default_NoGravadoTax_Vendors';

-- 2026-03-06
-- Fix CC: evita pendientes residuales de 0,01 en invoiceopen cuando el comprobante no es de monto minimo.
-- Se corrige una condicion imposible (AND/AND) por la condicion correcta (AND/(OR)).
CREATE OR REPLACE FUNCTION libertya.invoiceopen(
    p_c_invoice_id integer,
    p_c_invoicepayschedule_id integer,
    p_c_currency_id integer,
    p_c_conversiontype_id integer,
    p_dateto timestamp without time zone)
RETURNS numeric AS
$BODY$
DECLARE
    v_Currency_ID        INTEGER := p_c_currency_id;
    v_GrandTotal         NUMERIC := 0;
    v_TotalOpenAmt       NUMERIC := 0;
    v_PaidAmt            NUMERIC := 0;
    v_Remaining          NUMERIC := 0;
    v_Precision          NUMERIC := 0;
    v_Min                NUMERIC := 0.01;
    s                    RECORD;
    v_ConversionType_ID  INTEGER := p_c_conversiontype_id;
    v_Date               timestamp with time zone := ('now'::text)::timestamp(6);
BEGIN
    SELECT currencyConvert(GrandTotal, I.c_currency_id, v_Currency_ID, v_Date, v_ConversionType_ID, I.AD_Client_ID, I.AD_Org_ID),
           (SELECT StdPrecision FROM C_Currency C WHERE C.C_Currency_ID = I.C_Currency_ID)
      INTO v_TotalOpenAmt, v_Precision
      FROM C_Invoice I
     WHERE I.C_Invoice_ID = p_C_Invoice_ID;

    IF NOT FOUND THEN
        RAISE NOTICE 'Invoice no econtrada - %', p_C_Invoice_ID;
        RETURN NULL;
    END IF;

    v_GrandTotal := v_TotalOpenAmt;
    v_PaidAmt := getAllocatedAmt(p_C_Invoice_ID, v_Currency_ID, v_ConversionType_ID, 1, p_dateto);

    IF (p_C_InvoicePaySchedule_ID > 0) THEN
        v_Remaining := abs(v_PaidAmt);
        FOR s IN
            SELECT ips.C_InvoicePaySchedule_ID,
                   currencyConvert(ips.DueAmt, i.c_currency_id, v_Currency_ID, v_Date, v_ConversionType_ID, i.AD_Client_ID, i.AD_Org_ID) as DueAmt
              FROM C_InvoicePaySchedule ips
              INNER JOIN C_Invoice i ON (ips.C_Invoice_ID = i.C_Invoice_ID)
             WHERE ips.C_Invoice_ID = p_C_Invoice_ID
               AND ips.IsValid = 'Y'
             ORDER BY ips.DueDate
        LOOP
            IF (s.C_InvoicePaySchedule_ID = p_C_InvoicePaySchedule_ID) THEN
                v_TotalOpenAmt := abs(s.DueAmt) - abs(v_Remaining);
                IF (v_TotalOpenAmt < 0) THEN
                    v_TotalOpenAmt := 0;
                END IF;
                EXIT;
            ELSE
                v_Remaining := abs(v_Remaining) - abs(s.DueAmt);
                IF (v_Remaining < 0) THEN
                    v_Remaining := 0;
                END IF;
            END IF;
        END LOOP;
    ELSE
        v_TotalOpenAmt := abs(v_TotalOpenAmt) - abs(v_PaidAmt);
    END IF;

    -- Si el total abierto esta dentro del umbral, solo se fuerza a cero cuando
    -- el total del comprobante NO es de magnitud minima (abs(grandtotal) > v_Min).
    IF (v_TotalOpenAmt >= -v_Min AND v_TotalOpenAmt <= v_Min
        AND (v_GrandTotal < -v_Min OR v_GrandTotal > v_Min)) THEN
        v_TotalOpenAmt := 0;
    END IF;

    -- El resultado final debe mantener el signo del comprobante.
    IF (v_GrandTotal < 0) THEN
        v_TotalOpenAmt := v_TotalOpenAmt * -1::numeric;
    END IF;

    v_TotalOpenAmt := ROUND(COALESCE(v_TotalOpenAmt, 0), v_Precision);
    RETURN v_TotalOpenAmt;
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;

ALTER FUNCTION libertya.invoiceopen(integer, integer, integer, integer, timestamp without time zone)
    OWNER TO libertya;

-- 20260312 Metadata para "Import Facturas"
update ad_system set dummy = (SELECT addcolumnifnotexists('i_invoice','periodfrom','timestamp without time zone'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_invoice','periodto','timestamp without time zone'));
