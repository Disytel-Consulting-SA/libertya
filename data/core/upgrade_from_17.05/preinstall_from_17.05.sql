-- ========================================================================================
-- PREINSTALL FROM 17.05
-- ========================================================================================
-- Consideraciones importantes:
--	1) NO hacer cambios en el archivo, realizar siempre APPENDs al final del mismo 
-- 	2) Recordar realizar las adiciones con un comentario con formato YYYYMMDD-HHMM
-- ========================================================================================

--20170529-1030 Nueva columna que permite indicar si un pago es manual, en ese caso se genera un allocation unilateral al completar 
update ad_system set dummy = (SELECT addcolumnifnotexists('C_Payment','ismanual','character(1) NOT NULL DEFAULT ''N''::bpchar'));

--20170606-1445 El resultado del pendiente debe ser positivo ya que para payments con importe negativo no estaba retornando correctamente el valor
CREATE OR REPLACE FUNCTION paymentavailable(
  p_c_payment_id integer,
  dateto timestamp without time zone)
  RETURNS numeric AS
$BODY$
DECLARE
v_Currency_ID INTEGER;
v_AvailableAmt NUMERIC := 0;
  v_IsReceipt CHARACTER(1);
  v_Amt NUMERIC := 0;
  r RECORD;
v_Charge_ID INTEGER;
v_ConversionType_ID INTEGER;
v_allocatedAmt NUMERIC; -- candida alocada total convertida a la moneda de la linea
v_DateAcct timestamp without time zone;
BEGIN
BEGIN

SELECT C_Currency_ID, PayAmt, IsReceipt,
C_Charge_ID,C_ConversionType_ID, DateAcct
INTO STRICT
v_Currency_ID, v_AvailableAmt, v_IsReceipt,
v_Charge_ID,v_ConversionType_ID, v_DateAcct
FROM C_Payment
WHERE C_Payment_ID = p_C_Payment_ID;
EXCEPTION
WHEN OTHERS THEN
  RAISE NOTICE 'PaymentAvailable - %', SQLERRM;
RETURN NULL;
END;

IF (v_Charge_ID > 0 ) THEN
RETURN 0;
END IF;

v_allocatedAmt := 0;
FOR r IN
SELECT a.AD_Client_ID, a.AD_Org_ID, al.Amount, a.C_Currency_ID, a.DateTrx
FROM C_AllocationLine al
INNER JOIN C_AllocationHdr a ON (al.C_AllocationHdr_ID=a.C_AllocationHdr_ID)
WHERE al.C_Payment_ID = p_C_Payment_ID
  AND a.IsActive='Y'
  AND (dateTo IS NULL OR a.dateacct::date <= dateTo::date)
LOOP
v_Amt := currencyConvert(r.Amount, r.C_Currency_ID, v_Currency_ID,
v_DateAcct, v_ConversionType_ID, r.AD_Client_ID, r.AD_Org_ID);
v_allocatedAmt := v_allocatedAmt + v_Amt;
END LOOP;

-- esto supone que las alocaciones son siempre no negativas; si esto no pasa, se van a retornar valores que no van a tener sentido
v_AvailableAmt := ABS(v_AvailableAmt) - v_allocatedAmt;
-- v_AvailableAmt aca DEBE ser NO Negativo si admeas, las suma de las alocaciones nunca superan el monto del pago
-- de cualquiera manera, por "seguridad", si el valor es negativo, se corrige a cero
IF (v_AvailableAmt < 0) THEN
RAISE NOTICE 'Payment Available negative, correcting to zero - %',v_AvailableAmt ;
v_AvailableAmt := 0;
END IF;

v_AvailableAmt := currencyRound(v_AvailableAmt,v_Currency_ID,NULL);
RETURN v_AvailableAmt;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION paymentavailable(integer, timestamp without time zone)
  OWNER TO libertya;

--20170612-1200 Nueva columna para configurar la cuenta bancaria utilizada en el payment generado por liquidaciones
update ad_system set dummy = (SELECT addcolumnifnotexists('m_entidadfinanciera','C_BankAccount_Settlement_ID','integer'));

--20170613-1519 Creacion de tabla de bajo nivel para seguimiento de eventuales queries/conexiones relacioandas con informes canditadas a ser canceladas en caso de quedar "huerfanas" del cliente o servidor LY 
create table ad_keepalive (ad_session_id int, pid int, created timestamp, updated timestamp);

--20170626-2030 Creación de tabla que permite configurar datos necesarios para procesos de Corrección de Cobranzas
CREATE TABLE c_payment_recovery_config
(
  c_payment_recovery_config_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  c_doctype_recovery_id integer NOT NULL,
  c_doctype_credit_recovery_id integer NOT NULL,
  m_product_recovery_id integer NOT NULL,
  c_doctype_rejected_id integer NOT NULL,
  c_doctype_credit_rejected_id integer NOT NULL,
  m_product_rejected_id integer NOT NULL,
  CONSTRAINT c_payment_recovery_config_key PRIMARY KEY (c_payment_recovery_config_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE c_payment_recovery_config OWNER TO libertya;

--20170628-1400 Merge de revisión 2047
ALTER TABLE c_perceptionssettlement ALTER COLUMN internalno DROP NOT NULL;

--20170630-1201 Los registros en cuestión no contenian UID (usar a partir de ahora los especificados en version std) 
update m_pricelist set ad_componentobjectuid = 'CORE-M_PriceList-1010595' where name = 'Ventas' and ad_client_id = 1010016 and ad_componentobjectuid is null;
update m_pricelist set ad_componentobjectuid = 'CORE-M_PriceList-1010596' where name = 'Standard' and ad_client_id = 1010016 and ad_componentobjectuid is null;
update m_pricelist set ad_componentobjectuid = 'CORE-M_PriceList-1010597' where name = 'Costo' and ad_client_id = 1010016 and ad_componentobjectuid is null;
update m_pricelist_version set ad_componentobjectuid = 'CORE-M_PriceList_Version-1010595' where name = 'Ventas Inicial' and ad_client_id = 1010016 and ad_componentobjectuid is null;
update m_pricelist_version set ad_componentobjectuid = 'CORE-M_PriceList_Version-1010596' where name = '01.08.2008' and ad_client_id = 1010016 and ad_componentobjectuid is null;
update m_pricelist_version set ad_componentobjectuid = 'CORE-M_PriceList_Version-1010597' where name = 'Costo Inicial' and ad_client_id = 1010016 and ad_componentobjectuid is null;

--20170703-1225 Los tipos definidos generaban numeric field overflow para casos con valores grandes.
alter table t_libroiva alter column neto type numeric(20,2);
alter table t_libroiva alter column totalfacturado type numeric(20,2);
alter table t_libroiva alter column importe type numeric(20,2);

--20170705-1200 NUeva columna botón para procesar artículos del proveedor
update ad_system set dummy = (SELECT addcolumnifnotexists('C_BPartner','processpo','character(1) NOT NULL DEFAULT ''Y''::bpchar'));

--20170710-1630 Actualización de registros existentes
update c_externalservice set ad_componentobjectuid = 'CORE-C_ExternalService-1000001' where name = 'Central Pos - Visa' and ad_client_id = 1010016 and ad_componentobjectuid is null;
update c_externalservice set ad_componentobjectuid = 'CORE-C_ExternalService-1000004' where name = 'Central Pos - Amex' and ad_client_id = 1010016 and ad_componentobjectuid is null;
update c_externalservice set ad_componentobjectuid = 'CORE-C_ExternalService-1000005' where name = 'Central Pos - Naranja' and ad_client_id = 1010016 and ad_componentobjectuid is null;
update c_externalservice set ad_componentobjectuid = 'CORE-C_ExternalService-1000006' where name = 'Central Pos - FirstData' and ad_client_id = 1010016 and ad_componentobjectuid is null;
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000005', name = '100', description = '' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'Elementos por Página' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - Amex');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000027', name = '', description = 'Comisión' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'Importe Descuento' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - Amex');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000026', name = '', description = 'Otros Conceptos' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'Imp total desc aceleracion' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - Amex');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000041', name = '', description = 'Categoría de Impuesto' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'IVA 21' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - Amex');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000030', name = '', description = 'Percepción' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'Percepcion IVA' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - Amex');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000029', name = '', description = 'Retención' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'Retencion Ganancias' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - Amex');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000028', name = '', description = 'Retención' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'Retencion IVA' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - Amex');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000012', name = 'https://liquidacion.centralpos.com/api/v1/amex/impuestos', description = '' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'URL Impuestos' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - Amex');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000009', name = 'https://liquidacion.centralpos.com/api/v1/auth/login', description = '' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'URL Login' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - Amex');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000039', name = '', description = 'Comisión' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'Arancel' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - FirstData');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000040', name = '', description = 'Otros Conceptos' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'Costo Financiero' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - FirstData');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000007', name = '100', description = '' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'Elementos por Página' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - FirstData');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000002', name = '', description = 'Otros Conceptos' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'Gastos por promocion' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - FirstData');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000042', name = '', description = 'Categoría de Impuesto' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'IVA 21' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - FirstData');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000038', name = '', description = 'Percepción' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'Percepcion IVA' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - FirstData');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000037', name = '', description = 'Retención' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'Ret Ganancias' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - FirstData');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000001', name = '', description = 'Retención' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'Ret IVA' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - FirstData');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000043', name = 'https://liquidacion.centralpos.com/api/v1/firstdata/liquidacion-participantes', description = '' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'URL detalle liq' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - FirstData');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000011', name = 'https://liquidacion.centralpos.com/api/v1/auth/login', description = '' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'URL Login' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - FirstData');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000034', name = '', description = 'Comisión' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'Comisiones - Conceptos fact a descontar mes pago' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - Naranja');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000006', name = '100', description = '' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'Elementos por Página' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - Naranja');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000035', name = '', description = 'Otros Conceptos' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'Gastos - Conceptos fact a descontar mes pago' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - Naranja');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000036', name = '', description = 'Categoría de Impuesto' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'IVA 21' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - Naranja');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000033', name = '', description = 'Retención' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'Retencion Ganancias' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - Naranja');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000031', name = '', description = 'Retención' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'Retencion IVA' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - Naranja');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000014', name = 'https://liquidacion.centralpos.com/api/v1/naranja/conceptos-facturados-meses', description = '' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'URL Conceptos' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - Naranja');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000013', name = 'https://liquidacion.centralpos.com/api/v1/naranja/headers', description = '' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'URL Headers' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - Naranja');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000010', name = 'https://liquidacion.centralpos.com/api/v1/auth/login', description = '' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'URL Login' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - Naranja');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000021', name = '', description = 'Otros Conceptos' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'Cargo adic por op internacionales' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - Visa');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000020', name = '', description = 'Otros Conceptos' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'Cargo adic por planes cuotas' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - Visa');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000019', name = '', description = 'Otros Conceptos' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'Costo plan acelerado cuotas' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - Visa');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000018', name = '', description = 'Otros Conceptos' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'Dto por ventas de campañas' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - Visa');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000004', name = '100', description = '' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'Elementos por Página' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - Visa');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000022', name = '', description = 'Comisión' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'Importe Arancel' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - Visa');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000025', name = '', description = 'Percepción' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'IVA' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - Visa');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000023', name = '', description = 'Categ. de Impuesto' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'IVA 10.5' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - Visa');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000024', name = '', description = 'Categ. de Impuesto' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'IVA 21' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - Visa');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000017', name = '', description = 'Retención' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'Ret Ganancias' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - Visa');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000016', name = '', description = 'Retención' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'Ret IVA' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - Visa');
UPDATE c_externalserviceattributes esa SET AD_ComponentObjectUID = 'CORE-C_ExternalServiceAttributes-1000008', name = 'https://liquidacion.centralpos.com/api/v1/auth/login', description = '' WHERE esa.ad_client_id = 1010016 and esa.ad_componentobjectuid is null and value = 'URL Login' AND EXISTS (SELECT es.c_externalservice_id FROM c_externalservice es WHERE es.c_externalservice_id = esa.c_externalservice_id and es.name = 'Central Pos - Visa');

--20170711-0910 Nueva columna con el número de documento de la retención
DROP VIEW m_retencion_invoice_v;

CREATE OR REPLACE VIEW m_retencion_invoice_v AS 
 SELECT DISTINCT ri.m_retencion_invoice_id, ri.ad_client_id, ri.ad_org_id, ri.c_retencionschema_id, 
			ri.c_invoice_id, ri.c_allocationhdr_id, ri.c_invoiceline_id, ri.c_invoice_retenc_id, 
			ri.amt_retenc, ri.c_currency_id, ri.pagos_ant_acumulados_amt, ri.retenciones_ant_acumuladas_amt, 
			ri.pago_actual_amt, ri.retencion_percent, ri.importe_no_imponible_amt, ri.base_calculo_percent, 
			ri.issotrx, ri.baseimponible_amt, ri.importe_determinado_amt, rs.c_retenciontype_id, 
			i.c_bpartner_id, rs.retencionapplication, bp.taxid, i.dateinvoiced, i.documentno as retencion_documentno, 
			iv.documentno, iv.dateinvoiced AS fecha, iv.grandtotal, iv.c_project_id, iv.totallines
   FROM c_retencionschema rs
   JOIN c_retenciontype rt ON rs.c_retenciontype_id = rt.c_retenciontype_id
   JOIN m_retencion_invoice ri ON rs.c_retencionschema_id = ri.c_retencionschema_id
   JOIN c_invoice i ON ri.c_invoice_id = i.c_invoice_id
   JOIN c_bpartner bp ON i.c_bpartner_id = bp.c_bpartner_id
   JOIN c_allocationhdr a ON ri.c_allocationhdr_id = a.c_allocationhdr_id
   JOIN c_allocationline al ON a.c_allocationhdr_id = al.c_allocationhdr_id
   LEFT JOIN c_invoice iv ON iv.c_invoice_id = (( SELECT allo.c_invoice_id
   FROM c_allocationline allo
  WHERE allo.c_allocationhdr_id = al.c_allocationhdr_id
  ORDER BY allo.c_invoice_id DESC
 LIMIT 1))
  ORDER BY ri.m_retencion_invoice_id, ri.ad_client_id, ri.ad_org_id, ri.c_retencionschema_id, ri.c_invoice_id, ri.c_allocationhdr_id, ri.c_invoiceline_id, ri.c_invoice_retenc_id, ri.amt_retenc, ri.c_currency_id, ri.pagos_ant_acumulados_amt, ri.retenciones_ant_acumuladas_amt, ri.pago_actual_amt, ri.retencion_percent, ri.importe_no_imponible_amt, ri.base_calculo_percent, ri.issotrx, ri.baseimponible_amt, ri.importe_determinado_amt, rs.c_retenciontype_id, i.c_bpartner_id, rs.retencionapplication, bp.taxid, i.dateinvoiced, iv.documentno, iv.dateinvoiced, iv.grandtotal, iv.c_project_id, iv.totallines;

ALTER TABLE m_retencion_invoice_v
  OWNER TO libertya;
  
--20170719-0955 Incremento de tamaño de columna cai
ALTER TABLE c_bpartner_cai ALTER COLUMN cai TYPE character varying(30);

--20170724-2012 Incorporación de nueva columna de estado de cadena de autorización a la importación de comprobantes
update ad_system set dummy = (SELECT addcolumnifnotexists('i_invoice','authorizationchainstatus','character(1)'));

--20170728-1312 Incorporación de nuevas columnas para registrar datos de CAI
update ad_system set dummy = (SELECT addcolumnifnotexists('i_invoice','cai','character varying(30)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_invoice','datecai','timestamp without time zone'));

--20170729-1225 Por defecto se setean a N las columnas Cliente y Cliente Potencial
ALTER TABLE C_BPARTNER ALTER COLUMN iscustomer SET DEFAULT 'N'::bpchar;
ALTER TABLE C_BPARTNER ALTER COLUMN isprospect SET DEFAULT 'N'::bpchar;

--20170809-1158 Se encontraron casos de allocations en negativo
CREATE OR REPLACE FUNCTION paymentavailable(
    p_c_payment_id integer,
    dateto timestamp without time zone)
  RETURNS numeric AS
$BODY$
DECLARE
v_Currency_ID INTEGER;
v_AvailableAmt NUMERIC := 0;
  v_IsReceipt CHARACTER(1);
  v_Amt NUMERIC := 0;
  r RECORD;
v_Charge_ID INTEGER;
v_ConversionType_ID INTEGER;
v_allocatedAmt NUMERIC; -- candida alocada total convertida a la moneda de la linea
v_DateAcct timestamp without time zone;
BEGIN
BEGIN

SELECT C_Currency_ID, PayAmt, IsReceipt,
C_Charge_ID,C_ConversionType_ID, DateAcct
INTO STRICT
v_Currency_ID, v_AvailableAmt, v_IsReceipt,
v_Charge_ID,v_ConversionType_ID, v_DateAcct
FROM C_Payment
WHERE C_Payment_ID = p_C_Payment_ID;
EXCEPTION
WHEN OTHERS THEN
  RAISE NOTICE 'PaymentAvailable - %', SQLERRM;
RETURN NULL;
END;

IF (v_Charge_ID > 0 ) THEN
RETURN 0;
END IF;

v_allocatedAmt := 0;
FOR r IN
SELECT a.AD_Client_ID, a.AD_Org_ID, al.Amount, a.C_Currency_ID, a.DateTrx
FROM C_AllocationLine al
INNER JOIN C_AllocationHdr a ON (al.C_AllocationHdr_ID=a.C_AllocationHdr_ID)
WHERE al.C_Payment_ID = p_C_Payment_ID
  AND a.IsActive='Y'
  AND (dateTo IS NULL OR a.dateacct::date <= dateTo::date)
LOOP
v_Amt := currencyConvert(r.Amount, r.C_Currency_ID, v_Currency_ID,
v_DateAcct, v_ConversionType_ID, r.AD_Client_ID, r.AD_Org_ID);
v_allocatedAmt := v_allocatedAmt + v_Amt;
END LOOP;

-- esto supone que las alocaciones son siempre no negativas; si esto no pasa, se van a retornar valores que no van a tener sentido
v_AvailableAmt := ABS(v_AvailableAmt) - abs(v_allocatedAmt);
-- v_AvailableAmt aca DEBE ser NO Negativo si admeas, las suma de las alocaciones nunca superan el monto del pago
-- de cualquiera manera, por "seguridad", si el valor es negativo, se corrige a cero
IF (v_AvailableAmt < 0) THEN
RAISE NOTICE 'Payment Available negative, correcting to zero - %',v_AvailableAmt ;
v_AvailableAmt := 0;
END IF;

v_AvailableAmt := currencyRound(v_AvailableAmt,v_Currency_ID,NULL);
RETURN v_AvailableAmt;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION paymentavailable(integer, timestamp without time zone)
  OWNER TO libertya;