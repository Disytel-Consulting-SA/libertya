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
  
--20170814-2015 Fixes a la view de retenciones emitidas de iibb bs as
DROP VIEW rv_c_invoice_reten_iibb_bsas_emitidas ;

CREATE OR REPLACE VIEW rv_c_invoice_reten_iibb_bsas_emitidas AS 
 SELECT ri.ad_org_id, ri.ad_client_id, bp.taxid AS cuit, 0 AS importeretencion, ri.created AS date, ipagada.puntodeventa AS numerosucursal, ipagada.numerocomprobante AS numeroemision, 'A'::bpchar AS tipooperacion
   FROM m_retencion_invoice ri
   JOIN c_invoice i ON i.c_invoice_id = ri.c_invoice_id
   JOIN c_allocationline al ON al.c_allocationhdr_id = ri.c_allocationhdr_id
   JOIN c_invoice ipagada ON ipagada.c_invoice_id = al.c_invoice_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_retencionschema rs ON rs.c_retencionschema_id = ri.c_retencionschema_id
   JOIN c_retenciontype rt ON rt.c_retenciontype_id = rs.c_retenciontype_id
   JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
   JOIN ad_clientinfo ci ON ci.ad_client_id = ri.ad_client_id
  WHERE rt.retentiontype = 'B'::bpchar AND (dt.docbasetype = ANY (ARRAY['API'::bpchar, 'APC'::bpchar, 'APP'::bpchar])) AND rs.c_region_id = 1000083;

ALTER TABLE rv_c_invoice_reten_iibb_bsas_emitidas
  OWNER TO libertya;
  
--20170815-121049 Merge de r2139  
update ad_system set dummy = (SELECT addcolumnifnotexists('C_DocType','open_close_by_pos','character(1)'));

--20170816-1318 Filtrar estado de retenciones
drop view m_retencion_invoice_v;

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
  WHERE i.docstatus in ('CO','CL')
  ORDER BY ri.m_retencion_invoice_id, ri.ad_client_id, ri.ad_org_id, ri.c_retencionschema_id, ri.c_invoice_id, ri.c_allocationhdr_id, ri.c_invoiceline_id, ri.c_invoice_retenc_id, ri.amt_retenc, ri.c_currency_id, ri.pagos_ant_acumulados_amt, ri.retenciones_ant_acumuladas_amt, ri.pago_actual_amt, ri.retencion_percent, ri.importe_no_imponible_amt, ri.base_calculo_percent, ri.issotrx, ri.baseimponible_amt, ri.importe_determinado_amt, rs.c_retenciontype_id, i.c_bpartner_id, rs.retencionapplication, bp.taxid, i.dateinvoiced, iv.documentno, iv.dateinvoiced, iv.grandtotal, iv.c_project_id, iv.totallines;

ALTER TABLE m_retencion_invoice_v
  OWNER TO libertya;
  
--20170822-1240 Nueva columna para configurar el dato "A la orden" de los cheques a proveedores
update ad_system set dummy = (SELECT addcolumnifnotexists('c_bpartner','a_name_check','character varying(60)'));

--20170822-1350 Para cheques, la fecha de pago es la fecha de vencimiento del mismo
CREATE OR REPLACE VIEW rv_unreconciled_payment AS 
 SELECT p.ad_org_id, p.ad_client_id, (CASE WHEN p.tendertype = 'K' AND p.duedate IS NOT NULL THEN p.duedate ELSE p.datetrx END) as fechapago, p.dateemissioncheck AS fechaemision, p.documentno AS nrocheque, ( SELECT a.documentno
           FROM c_allocationline al
      JOIN c_allocationhdr a ON a.c_allocationhdr_id = al.c_allocationhdr_id
     WHERE al.c_payment_id = p.c_payment_id AND (a.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))
     ORDER BY a.datetrx
    LIMIT 1) AS nroop, bp.name AS entidadcomercial, p.payamt AS monto, ba.c_bankaccount_id
   FROM c_payment p
   JOIN c_bpartner bp ON bp.c_bpartner_id = p.c_bpartner_id
   JOIN c_bankaccount ba ON ba.c_bankaccount_id = p.c_bankaccount_id
  WHERE p.isreconciled = 'N'::bpchar AND (p.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) AND p.tendertype = 'K'::bpchar AND p.isreceipt = 'N'::bpchar;

ALTER TABLE rv_unreconciled_payment
  OWNER TO libertya;
  
--20170823-1615 Nueva configuración de tipo de documento para indicar el tipo de transacción frente al LIVA
update ad_system set dummy = (SELECT addcolumnifnotexists('c_doctype','transactiontypefrontliva','character(1)'));

--20170828-1427 Nuevo indice para reduccion de tiempos en la importacion de padrones.
--COMENTADO 20171006: 
--	Este indice ya existe.  Bajo 11.10 fue eliminado pero bajo 13.01 fue creado nuevamente. 
--	No hay necesidad de que exista en el presente preinstall.
--	En realidad la presencia de esta sentencia genera un error en instalación (no se puede usar IF NOT EXISTS por incompatibilidad con Postgres 8.4) 
--create index c_bpartner_padron_bsas_cuit on c_bpartner_padron_bsas(cuit);

--20170830-0044 En el caso de efectivo, si tenemos una condición parámetro, entonces siempre debe tener una factura/nc relacionada
CREATE OR REPLACE FUNCTION v_documents_org_filtered(
    bpartner integer,
    summaryonly boolean,
    condition character,
    dateto timestamp without time zone)
  RETURNS SETOF v_documents_org_type_condition AS
$BODY$
declare
    consulta varchar;
    orderby1 varchar;
    orderby2 varchar;
    orderby3 varchar;
    leftjoin1 varchar;
    leftjoin2 varchar;
    advancedcondition varchar;
    advancedconditioncashlineexception varchar;
    whereclauseConditionDebit varchar;
    whereclauseConditionCredit varchar;
    whereclauseConditionCreditCashException varchar;
    whereclauseDateTo varchar;
    selectallocationNull varchar;
    selectallocationPayment varchar;
    selectallocationCashline varchar;
    selectallocationCredit varchar;
    selectAllocationReferencePayment varchar;
    selectAllocationReferenceCashline varchar;
    selectAllocationReferenceCredit varchar;
    adocument v_documents_org_type_condition;
   
BEGIN
    whereclauseDateTo = ' ( 1 = 1 ) ';
    -- Armar la condición para fecha de corte
    if dateTo is not null then 
		whereclauseDateTo = ' dateacct::date <= ''' || dateTo || '''::date ';
    end if;
    
    --Si no se deben mostrar todos, entonces agregar la condicion por la forma de pago
    if condition <> 'A' then
		--Si se debe mostrar sólo efectivo, entonces no se debe mostrar los anticipos, si o si debe tener una factura asociada
		advancedcondition = 'il.paymentrule is null OR ';
		advancedconditioncashlineexception = ' (1=1) ';
		if condition = 'B' then
			advancedcondition = '';
			advancedconditioncashlineexception = ' (1=2) ';
		end if;
		whereclauseConditionDebit = ' (i.paymentrule = ''' || condition || ''') ';
		whereclauseConditionCredit = ' (' || advancedcondition || ' il.paymentrule = ''' || condition || ''') ';
		whereclauseConditionCreditCashException = '( CASE WHEN il.paymentrule is not null THEN il.paymentrule = ''' || condition || ''' WHEN ic.paymentrule is not null THEN ic.paymentrule = ''' || condition || ''' ELSE '|| advancedconditioncashlineexception ||' END ) ';
	else
		whereclauseConditionDebit = ' (1 = 1) ';
		whereclauseConditionCredit = ' (1 = 1) ';
		whereclauseConditionCreditCashException = ' (1 = 1) '; 
    end if;    

    -- recuperar informacion minima indispensable si summaryonly es true.  en caso de ser false, debe joinearse/ordenarse, etc.
    if summaryonly = false then

        orderby1 = ' ORDER BY ''C_Invoice''::text, i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name, dt.printname, i.documentno, i.issotrx, i.docstatus,
                 CASE
                     WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
                     ELSE i.dateinvoiced
                 END, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced, bp.socreditstatus ';

        orderby2 = ' ORDER BY ''C_Payment''::text, p.c_payment_id, p.ad_client_id, COALESCE(il.ad_org_id, p.ad_org_id), p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name, dt.printname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt, NULL::integer, p.duedate, bp.socreditstatus ';

        orderby3 = ' ORDER BY ''C_CashLine''::text, cl.c_cashline_id, cl.ad_client_id, COALESCE(il.ad_org_id, ic.ad_org_id, cl.ad_org_id), cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby,
                CASE
                    WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id
		    WHEN il.c_bpartner_id IS NOT NULL THEN il.c_bpartner_id
                    ELSE ic.c_bpartner_id
                END, dt.c_doctype_id,
                CASE
                    WHEN cl.amount < 0.0 THEN 1
                    ELSE (-1)
                END, dt.name, dt.printname, ''@line@''::text || cl.line::character varying::text,
                CASE
                    WHEN cl.amount < 0.0 THEN ''N''::bpchar
                    ELSE ''Y''::bpchar
                END, cl.docstatus, c.statementdate, c.dateacct, cl.c_currency_id, NULL::integer, abs(cl.amount), NULL::timestamp without time zone, COALESCE(bp.socreditstatus, bp2.socreditstatus, bp3.socreditstatus) ';
	
		selectallocationNull = ' NULL::integer ';
		selectallocationPayment = selectallocationNull;
		selectallocationCashline = selectallocationNull;
		selectallocationCredit = selectallocationNull;
	
    else
        orderby1 = '';
        orderby2 = '';
        orderby3 = '';

		selectAllocationReferencePayment = ' al.c_payment_id = p.c_payment_id ';
		selectAllocationReferenceCashline = ' al.c_cashline_id = cl.c_cashline_id ';
		selectAllocationReferenceCredit = ' al.c_invoice_credit_id = i.c_invoice_id ';
	
		selectallocationPayment = ' (SELECT ah.c_allocationhdr_id FROM c_allocationline al INNER JOIN c_allocationhdr ah on ah.c_allocationhdr_id = al.c_allocationhdr_id WHERE allocationtype <> ''MAN'' AND ah.dateacct::date = p.dateacct::date AND ' || selectAllocationReferencePayment || ' AND ' || whereclauseDateTo || ' ORDER BY ah.created LIMIT 1) as c_allocationhdr_id ';
		selectallocationCashline = ' (SELECT ah.c_allocationhdr_id FROM c_allocationline al INNER JOIN c_allocationhdr ah on ah.c_allocationhdr_id = al.c_allocationhdr_id WHERE allocationtype <> ''MAN'' AND ah.dateacct::date = c.dateacct::date AND ' || selectAllocationReferenceCashline || ' AND ' || whereclauseDateTo || ' ORDER BY ah.created LIMIT 1) as c_allocationhdr_id ';
		selectallocationCredit = ' (SELECT ah.c_allocationhdr_id FROM c_allocationline al INNER JOIN c_allocationhdr ah on ah.c_allocationhdr_id = al.c_allocationhdr_id WHERE allocationtype <> ''MAN'' AND ah.dateacct::date = i.dateacct::date AND ' || selectAllocationReferenceCredit || ' AND ' || whereclauseDateTo || ' ORDER BY ah.created LIMIT 1) as c_allocationhdr_id ';
	
    end if;    

    consulta = ' SELECT * FROM 

        (        ( SELECT DISTINCT ''C_Invoice''::text AS documenttable, i.c_invoice_id AS document_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, i.documentno, i.issotrx, i.docstatus,
                        CASE
                            WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
                            ELSE i.dateinvoiced
                        END AS datetrx, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal AS amount, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced AS truedatetrx, bp.socreditstatus, i.c_order_id, '
				|| selectallocationCredit || 
               ' FROM c_invoice_v i
              JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
         JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id and (' || $1 || ' = -1  or bp.c_bpartner_id = ' || $1 || ')
    LEFT JOIN c_invoicepayschedule ips ON i.c_invoicepayschedule_id = ips.c_invoicepayschedule_id
    WHERE 

' || whereclauseConditionDebit || '
' || orderby1 || '

    )
        UNION ALL
                ( SELECT DISTINCT ''C_Payment''::text AS documenttable, p.c_payment_id AS document_id, p.ad_client_id, COALESCE(il.ad_org_id, p.ad_org_id) AS ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt AS amount, NULL::integer AS c_invoicepayschedule_id, p.duedate, p.datetrx AS truedatetrx, bp.socreditstatus, 0 as c_order_id, '
		|| selectallocationPayment || 
                  ' FROM c_payment p
              JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id
         JOIN c_bpartner bp ON p.c_bpartner_id = bp.c_bpartner_id AND (' || $1 || ' = -1 or p.c_bpartner_id = ' || $1 || ')
	LEFT JOIN c_allocationline al ON al.c_payment_id = p.c_payment_id 
	LEFT JOIN c_invoice il ON il.c_invoice_id = al.c_invoice_id
	LEFT JOIN M_BoletaDepositoLine bdlr on bdlr.c_reverse_payment_id = p.c_payment_id
	LEFT JOIN M_BoletaDeposito bdr on bdr.M_BoletaDeposito_ID = bdlr.M_BoletaDeposito_ID
	LEFT JOIN M_BoletaDepositoLine bdle on bdle.c_depo_payment_id = p.c_payment_id
	LEFT JOIN M_BoletaDeposito bde on bde.M_BoletaDeposito_ID = bdle.M_BoletaDeposito_ID
	LEFT JOIN M_BoletaDeposito bddb on bddb.c_boleta_payment_id = p.c_payment_id
  WHERE 
CASE
    WHEN il.ad_org_id IS NOT NULL AND il.ad_org_id <> p.ad_org_id THEN p.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar])
    ELSE 1 = 1
END 

AND (CASE WHEN bdr.M_BoletaDeposito_ID IS NOT NULL 
		OR bde.M_BoletaDeposito_ID IS NOT NULL 
		OR bddb.M_BoletaDeposito_ID IS NOT NULL THEN p.docstatus NOT IN (''CO'',''CL'') 
	ELSE 1 = 1
	END) 

AND ' || whereclauseConditionCredit || '

' || orderby2 || '


)

UNION ALL

        ( SELECT DISTINCT ''C_CashLine''::text AS documenttable, cl.c_cashline_id AS document_id, cl.ad_client_id, COALESCE(il.ad_org_id, ic.ad_org_id, cl.ad_org_id) AS ad_org_id, cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby,
                CASE
                    WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id
		    WHEN il.c_bpartner_id IS NOT NULL THEN il.c_bpartner_id
                    ELSE ic.c_bpartner_id
                END AS c_bpartner_id, dt.c_doctype_id,
                CASE
                    WHEN cl.amount < 0.0 THEN 1
                    ELSE (-1)
                END AS signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, ''@line@''::text || cl.line::character varying::text AS documentno,
                CASE
                    WHEN cl.amount < 0.0 THEN ''N''::bpchar
                    ELSE ''Y''::bpchar
                END AS issotrx, cl.docstatus, c.statementdate AS datetrx, c.dateacct, cl.c_currency_id, NULL::integer AS c_conversiontype_id, abs(cl.amount) AS amount, NULL::integer AS c_invoicepayschedule_id, NULL::timestamp without time zone AS duedate, c.statementdate AS truedatetrx, COALESCE(bp.socreditstatus, bp2.socreditstatus, bp3.socreditstatus) AS socreditstatus, 0 as c_order_id, '
                || selectallocationCashline || 
       ' FROM c_cashline cl
      JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
   LEFT JOIN c_bpartner bp ON cl.c_bpartner_id = bp.c_bpartner_id AND (' || $1 || ' = -1 or cl.c_bpartner_id = ' || $1 || ')
   JOIN ( SELECT d.ad_client_id, d.c_doctype_id, d.name, d.printname
         FROM c_doctype d
        WHERE d.doctypekey::text = ''CMC''::text) dt ON cl.ad_client_id = dt.ad_client_id
   LEFT JOIN c_allocationline al ON al.c_cashline_id = cl.c_cashline_id
   LEFT JOIN c_invoice il ON il.c_invoice_id = al.c_invoice_id AND (' || $1 || ' = -1 or il.c_bpartner_id = ' || $1 || ')
   LEFT JOIN c_bpartner bp2 ON il.c_bpartner_id = bp2.c_bpartner_id
   LEFT JOIN c_invoice ic on ic.c_invoice_id = cl.c_invoice_id AND (' || $1 || ' = -1 or ic.c_bpartner_id = ' || $1 || ')
   LEFT JOIN c_bpartner bp3 ON ic.c_bpartner_id = bp3.c_bpartner_id
  WHERE (CASE WHEN cl.c_bpartner_id IS NOT NULL THEN (' || $1 || ' = -1 or cl.c_bpartner_id = ' || $1 || ')
        WHEN il.c_bpartner_id IS NOT NULL THEN (' || $1 || ' = -1 or il.c_bpartner_id = ' || $1 || ')
        WHEN ic.c_bpartner_id IS NOT NULL THEN (' || $1 || ' = -1 or ic.c_bpartner_id = ' || $1 || ')
        ELSE 1 = 2 END)
    AND (CASE WHEN (il.ad_org_id IS NOT NULL AND il.ad_org_id <> cl.ad_org_id) OR (ic.ad_org_id IS NOT NULL AND ic.ad_org_id <> cl.ad_org_id)
        THEN cl.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar])
        ELSE 1 = 1 END)

    AND (' || whereclauseConditionCreditCashException || ' )

' || orderby3 || '

)) AS d  
WHERE ' || whereclauseDateTo || ' ; ';

-- raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION v_documents_org_filtered(integer, boolean, character, timestamp without time zone)
  OWNER TO libertya;
  
--20170904-0850 Bug fix: Incorporación de columna "aplicacion" faltante en view
DROP VIEW c_invoice_percepciones_v;

CREATE OR REPLACE VIEW c_invoice_percepciones_v AS 
 SELECT i.ad_client_id, i.ad_org_id, dt.c_doctype_id, dt.name AS doctypename, 
        CASE
            WHEN dt.signo_issotrx = 1 THEN 'F'::text
            ELSE 'C'::text
        END AS doctypechar, 
        CASE
            WHEN "substring"(dt.doctypekey::text, 1, 2) = 'CI'::text THEN 'F'::text
            WHEN "substring"(dt.doctypekey::text, 1, 2) = 'CC'::text THEN 'NC'::text
            ELSE 'ND'::text
        END AS doctypenameshort, 
        CASE
            WHEN "substring"(dt.doctypekey::text, 1, 2) = 'CI'::text THEN 'T'::character(1)
            WHEN "substring"(dt.doctypekey::text, 1, 2) = 'CC'::text THEN 'R'::character(1)
            ELSE 'D'::character(1)
        END AS doctypenameshort_aditional, 
        CASE
            WHEN "substring"(dt.doctypekey::text, 1, 2) = 'CI'::text THEN 1
            WHEN "substring"(dt.doctypekey::text, 1, 2) = 'CC'::text THEN 102
            ELSE 2
        END AS tipo_de_documento_reg_neuquen, dt.docbasetype, i.c_invoice_id, i.documentno, date_trunc('day'::text, i.dateinvoiced) AS dateinvoiced, date_trunc('day'::text, i.dateacct) AS dateacct, date_trunc('day'::text, i.dateinvoiced) AS date, lc.letra, i.puntodeventa, i.numerocomprobante, i.grandtotal, bp.c_bpartner_id, bp.value AS bpartner_value, bp.name AS bpartner_name, replace(bp.taxid::text, '-'::text, ''::text) AS taxid, bp.iibb, 
        CASE
            WHEN length(bp.iibb::text) > 7 THEN 1
            ELSE 0
        END AS tipo_contribuyente, ((("substring"(replace(bp.taxid::text, '-'::text, ''::text), 1, 2) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 3, 8)) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 11, 1) AS taxid_with_script, COALESCE(i.nombrecli, bp.name) AS nombrecli, COALESCE(i.nroidentificcliente, bp.taxid) AS nroidentificcliente, ((("substring"(replace(bp.taxid::text, '-'::text, ''::text), 1, 2) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 3, 8)) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 11, 1) AS nroidentificcliente_with_script, ( SELECT l.address1
           FROM c_bpartner_location bpl
      JOIN c_location l ON l.c_location_id = bpl.c_location_id
     WHERE bpl.c_bpartner_id = bp.c_bpartner_id
     ORDER BY bpl.updated DESC
    LIMIT 1) AS address1, t.c_tax_id, t.name AS percepcionname, it.taxbaseamt, it.taxamt, (it.taxbaseamt * dt.signo_issotrx::numeric)::numeric(20,2) AS taxbaseamt_with_sign, (it.taxamt * dt.signo_issotrx::numeric)::numeric(20,2) AS taxamt_with_sign, 
        CASE
            WHEN it.taxbaseamt <> 0::numeric THEN it.taxamt * 100::numeric / it.taxbaseamt
            ELSE 0::numeric
        END::numeric(20,2) AS alicuota, lo.city AS org_city, lo.postal AS org_postal_code, r.jurisdictioncode, translate(i.documentno::text, lc.letra::text, ''::text)::character varying(30) AS documentno_without_letter, (case when i.issotrx = 'Y' then 'E' else 'S' end) as aplicacion
   FROM c_invoicetax it
   JOIN c_invoice i ON i.c_invoice_id = it.c_invoice_id
   JOIN c_letra_comprobante lc ON lc.c_letra_comprobante_id = i.c_letra_comprobante_id
   JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctypetarget_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_tax t ON t.c_tax_id = it.c_tax_id
   JOIN ad_orginfo oi ON oi.ad_org_id = i.ad_org_id
   LEFT JOIN c_location lo ON lo.c_location_id = oi.c_location_id
   LEFT JOIN c_region r ON r.c_region_id = lo.c_region_id
  WHERE t.ispercepcion = 'Y'::bpchar AND i.issotrx = 'Y'::bpchar AND ((i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar])) OR (i.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar);

ALTER TABLE c_invoice_percepciones_v
  OWNER TO libertya; 
  
  --20170922-1124 Tasas de conversion invalidas dado que son de cia System (se eliminan solo si las mismas no fueron modificadas)
  delete from c_conversion_rate where c_conversion_rate_id = 117 and ad_client_id = 0 and updated = '2000-01-02 00:00:00';
  delete from c_conversion_rate where c_conversion_rate_id = 119 and ad_client_id = 0 and updated = '2000-01-02 00:00:00';
  delete from c_conversion_rate where c_conversion_rate_id = 120 and ad_client_id = 0 and updated = '2000-01-02 00:00:00';
  
--20170926-0900 Nueva columna que permite mantener abierto un control de período aún si se encuentra cerrado
update ad_system set dummy = (SELECT addcolumnifnotexists('C_PeriodControl','permanentlyopen','character(1) NOT NULL DEFAULT ''N''::bpchar'));

--20171018-1353 Se tiene en cuenta la cantidad reservada (qtyreserved) para actualizar el reservado del storage
CREATE OR REPLACE FUNCTION update_reserved(
    clientid integer,
    orgid integer,
    productid integer)
  RETURNS void AS
$BODY$
/***********
Actualiza la cantidad reservada de los depósitos de la compañía, organización y artículo parametro, 
siempre y cuando existan los regitros en m_storage 
y sólo sobre locators marcados como default ya que asi se realiza al procesar pedidos.
Las cantidades reservadas se obtienen de pedidos procesados. 
IMPORTANTE: No funciona para artículos que no son ITEMS (Stockeables)
*/
BEGIN
	update m_storage s
	set qtyreserved = coalesce((select sum(ol.qtyreserved) as qtypending
					from c_orderline ol
					inner join c_order o on o.c_order_id = ol.c_order_id
					inner join m_warehouse w on w.m_warehouse_id = o.m_warehouse_id
					inner join m_locator l on l.m_warehouse_id = w.m_warehouse_id
					where ol.qtyreserved <> 0
						and o.processed = 'Y' 
						and s.m_product_id = ol.m_product_id
						and s.m_locator_id = l.m_locator_id
						and o.issotrx = 'Y'),0)
	where ad_client_id = clientid
		and (orgid = 0 or ad_org_id = orgid)
		and (productid = 0 or m_product_id = productid)
		and s.m_locator_id IN (select defaultLocator 
					from (select m_warehouse_id, max(m_locator_id) as defaultLocator
						from m_locator l
						where l.isdefault = 'Y' and l.isactive = 'Y'
						GROUP by m_warehouse_id) as dl);
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION update_reserved(integer, integer, integer)
  OWNER TO libertya;

--20171023-1530 Función que permite obtener la cantidad pendiente a fecha de corte
CREATE OR REPLACE FUNCTION getqtyreserved(
    clientid integer,
    orgid integer,
    locatorid integer,
    productid integer,
    dateto date)
  RETURNS numeric AS
$BODY$
/***********
Obtiene la cantidad reservada a fecha de corte. Si no hay fecha de corte, entonces se devuelven los pendientes actuales.
La cantidad pendiente a una fecha de corte equivale a:
1) Sumar la cantidad pedida a esa fecha
2) Sumar las NC con la marca Actualizar Pedido luego de esa fecha.
3) Restar los transaction de inouts con tipo de documento que gestionen cantidades reservadas de stock y pendientes a fecha de corte. Salidas son negativas y entradas positivas.
4) Restar las cantidades transferidas a fecha de corte
*/
DECLARE
	reserved numeric;
BEGIN
	reserved := 0;
	--Si no hay fecha de corte o es mayor o igual a la fecha actual, entonces se suman las cantidades reservadas de los pedidos
	if ( dateTo is null OR dateTo >= current_date ) THEN
		SELECT INTO reserved coalesce(sum(ol.qtyreserved),0)
		from c_orderline ol
		inner join c_order o on o.c_order_id = ol.c_order_id
		inner join m_warehouse w on w.m_warehouse_id = o.m_warehouse_id
		inner join m_locator l on l.m_warehouse_id = w.m_warehouse_id
		where ol.qtyreserved <> 0
			and o.processed = 'Y' 
			and ol.m_product_id = productid
			and l.m_locator_id = locatorid
			and o.issotrx = 'Y';
	ELSE
		SELECT INTO reserved coalesce(sum(qty),0)
		from (
		-- Cantidad pedida a fecha de corte
		select coalesce(sum(ol.qtyordered),0) as qty
			from c_orderline ol
			inner join c_order o on o.c_order_id = ol.c_order_id
			inner join c_doctype dt on dt.c_doctype_id = o.c_doctype_id
			inner join m_warehouse w on w.m_warehouse_id = o.m_warehouse_id
			inner join m_locator l on l.m_warehouse_id = w.m_warehouse_id
			where o.ad_client_id = clientid
				and o.ad_org_id = orgid
				and o.processed = 'Y' 
				and ol.m_product_id = productid
				and l.m_locator_id = locatorid
				and o.issotrx = 'Y'
				and dt.doctypekey NOT IN ('SOSOTH','SOSOT')
				and o.dateordered::date <= dateTo::date
				and o.dateordered::date <= current_date
		union all
		-- Notas de crédito con el check Actualizar Cantidades de Pedido
		select coalesce(sum(il.qtyinvoiced),0) as qty
		from c_invoiceline il
		inner join c_invoice i on i.c_invoice_id = il.c_invoice_id
		inner join c_orderline ol on ol.c_orderline_id = il.c_orderline_id
		inner join c_order o on o.c_order_id = ol.c_order_id
		inner join m_warehouse w on w.m_warehouse_id = o.m_warehouse_id
		inner join m_locator l on l.m_warehouse_id = w.m_warehouse_id
		where o.ad_client_id = clientid
			and o.ad_org_id = orgid
			and o.processed = 'Y' 
			and ol.m_product_id = productid
			and l.m_locator_id = locatorid
			and i.issotrx = 'Y'
			and i.updateorderqty = 'Y'
			and o.dateordered::date <= dateTo::date
			and i.dateinvoiced::date > dateTo::date
			and i.dateinvoiced::date <= current_date
		union all
		--En transaction las salidas son negativas y las entradas positivas
		select coalesce(sum(t.movementqty),0) as qty
		from m_transaction t
		inner join m_inoutline iol on iol.m_inoutline_id = t.m_inoutline_id
		inner join m_inout io on io.m_inout_id = iol.m_inout_id
		inner join c_doctype dt on dt.c_doctype_id = io.c_doctype_id
		inner join c_orderline ol on ol.c_orderline_id = iol.c_orderline_id
		inner join c_order o on o.c_order_id = ol.c_order_id
		where t.ad_client_id = clientid
			and t.ad_org_id = orgid
			and t.m_product_id = productid
			and t.m_locator_id = locatorid
			and dt.reservestockmanagment = 'Y'
			and o.dateordered::date <= dateTo::date
			and t.movementdate::date <= dateTo::date
			and t.movementdate::date <= current_date
		union all
		--Cantidades transferidas
		select coalesce(sum(ol.qtyordered * -1),0) as qty
		from c_orderline ol
		inner join c_order o on o.c_order_id = ol.c_order_id
		inner join c_orderline rl on rl.c_orderline_id = ol.ref_orderline_id
		inner join c_order r on r.c_order_id = rl.c_order_id
		inner join c_doctype dt on dt.c_doctype_id = o.c_doctype_id
		inner join m_warehouse w on w.m_warehouse_id = o.m_warehouse_id
		inner join m_locator l on l.m_warehouse_id = w.m_warehouse_id
		where o.ad_client_id = clientid
			and o.ad_org_id = orgid
			and o.processed = 'Y' 
			and ol.m_product_id = productid
			and l.m_locator_id = locatorid
			and o.issotrx = 'Y'
			and dt.doctypekey IN ('SOSOT')
			and r.dateordered::date <= dateTo::date
			and o.dateordered::date <= dateTo::date
			and o.dateordered::date <= current_date
		) todo;
	END IF;
	
	return reserved;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION getqtyreserved(integer, integer, integer, integer, date)
  OWNER TO libertya;
  
--20171024-0945 Incorporación de condición de configuración de tipo de documento frente a iva
CREATE OR REPLACE FUNCTION v_dailysales_current_account_filtered(
    orgid integer,
    posid integer,
    userid integer,
    datefrom date,
    dateto date,
    invoicedatefrom date,
    invoicedateto date,
    addinvoicedate boolean)
  RETURNS SETOF v_dailysales_type AS
$BODY$
declare
	consulta varchar;
	whereDateInvoices varchar;
	whereInvoiceDate varchar;
	wherePOSInvoices varchar;
	whereUserInvoices varchar;
	whereOrg varchar;
	whereClauseStd varchar;
	posJournalPaymentsFrom varchar;
	dateFromPOSJournalPayments varchar;
	dateToPOSJournalPayments varchar;
	orgIDPOSJournalPayments integer;
	adocument v_dailysales_type;
BEGIN
	-- Armado de las condiciones en base a los parámetros
	-- Organización
	whereOrg = '';
	if orgID is not null AND orgID > 0 THEN
		whereOrg = ' AND i.ad_org_id = ' || orgID;
	END IF;
	
	-- Fecha de factura
	whereInvoiceDate = '';
	if addInvoiceDate then
		if invoiceDateFrom is not null then
			whereInvoiceDate = ' AND date_trunc(''day'', i.dateacct) >= date_trunc(''day'', '''|| invoiceDateFrom || '''::date)';
		end if;
		if invoiceDateTo is not null then
			whereInvoiceDate = whereInvoiceDate || ' AND date_trunc(''day'', i.dateacct) <= date_trunc(''day'', ''' || invoiceDateTo || '''::date) ';
		end if;
	end if;

	-- Fechas para allocations y facturas
	whereDateInvoices = '';
	if dateFrom is not null then
		whereDateInvoices = ' AND date_trunc(''day''::text, i.dateinvoiced) >= date_trunc(''day'', ''' || dateFrom || '''::date)';
	end if;

	if dateTo is not null then
		whereDateInvoices = whereDateInvoices || ' AND date_trunc(''day''::text, i.dateinvoiced) <= date_trunc(''day'', ''' || dateTo || '''::date) ';
	end if;
	
	-- TPV
	wherePOSInvoices = ' AND (' || posID || ' = -1 OR pj.c_pos_id = ' || posID || ')';

	-- Usuario
	whereUserInvoices = ' AND (' || userID || ' = -1 OR pj.ad_user_id = ' || userID || ')';

	-- Condiciones básicas del reporte
	whereClauseStd = ' ( i.issotrx = ''Y'' ' ||
			 whereOrg || 
			 ' AND (i.docstatus = ''CO'' or i.docstatus = ''CL'' or i.docstatus = ''RE'' or i.docstatus = ''VO'' OR i.docstatus = ''??'') ' ||
			 ' AND dt.isfiscaldocument = ''Y'' ' || 
			 ' AND (dt.isfiscal is null OR dt.isfiscal = ''N'' OR (dt.isfiscal = ''Y'' AND i.fiscalalreadyprinted = ''Y'')) ' ||
			 ' AND dt.doctypekey not in (''RTR'', ''RTI'', ''RCR'', ''RCI'') ' || 
			 ' AND (dt.transactiontypefrontliva is null OR dt.transactiontypefrontliva = ''S'') ' || 
			 ' ) ';

	-- Agregar las condiciones anteriores
	whereClauseStd = whereClauseStd || whereInvoiceDate;

	-- Armado del llamado a la función que ejecuta la vista filtrada c_posjournalpayments_v
	dateFromPOSJournalPayments = (CASE WHEN dateFrom is null THEN 'null::date' ELSE '''' || dateFrom || '''::date' END);
	dateToPOSJournalPayments = (CASE WHEN dateTo is null THEN 'null::date' ELSE '''' || dateTo || '''::date' END);
	orgIDPOSJournalPayments = (CASE WHEN orgID is null THEN -1 ELSE orgID END);
	posJournalPaymentsFrom = 'c_posjournalpayments_v_filtered(' || orgIDPOSJournalPayments || ', ' || dateFromPOSJournalPayments || ', ' || dateToPOSJournalPayments || ')';

	-- Armar la consulta
	consulta = 'SELECT ''CAI''::character varying AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc(''day''::text, i.dateinvoiced) AS datetrx, NULL::integer AS c_payment_id, NULL::integer AS c_cashline_id, NULL::integer AS c_invoice_credit_id, ''CC'' AS tendertype, i.documentno, i.description, NULL::unknown AS info, (i.grandtotal - COALESCE(cobros.amount, 0::numeric))::numeric(20,2) AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, NULL::unknown AS c_pospaymentmedium_id, NULL::unknown AS pospaymentmediumname, NULL::integer AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::integer AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
           FROM c_invoice i
      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   LEFT JOIN ( SELECT c.c_invoice_id, sum(c.amount) AS amount
         FROM ( SELECT 
                      CASE
                          WHEN (dt.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                          ELSE i.c_invoice_id
                      END AS c_invoice_id, pjp.amount
                 FROM ' || posJournalPaymentsFrom || ' pjp
            JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
       JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_posjournal as pj on pj.c_posjournal_id = i.c_posjournal_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
  WHERE ' || whereClauseStd || whereDateInvoices || whereUserInvoices || wherePOSInvoices ||
  ' AND date_trunc(''day''::text, i.dateacct) = date_trunc(''day''::text, pjp.dateacct::timestamp with time zone) AND ((i.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar])) AND hdr.isactive = ''Y''::bpchar OR (i.docstatus = ANY (ARRAY[''VO''::bpchar, ''RE''::bpchar])) AND hdr.isactive = ''N''::bpchar)) c
        GROUP BY c.c_invoice_id) cobros ON cobros.c_invoice_id = i.c_invoice_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE ' || whereClauseStd || whereDateInvoices || whereUserInvoices || wherePOSInvoices ||
  ' AND (cobros.amount IS NULL OR i.grandtotal <> cobros.amount) AND i.initialcurrentaccountamt > 0::numeric AND (dt.docbasetype <> ALL (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND "position"(dt.doctypekey::text, ''CDN''::text) < 1
UNION ALL 
         SELECT ''CAIA''::character varying AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc(''day''::text, i.dateinvoiced) AS datetrx, NULL::integer AS c_payment_id, NULL::integer AS c_cashline_id, NULL::integer AS c_invoice_credit_id, ''CC'' AS tendertype, i.documentno, i.description, NULL::unknown AS info, (i.grandtotal - COALESCE(cobros.amount, 0::numeric))::numeric(20,2) * (-1)::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, NULL::integer AS c_pospaymentmedium_id, NULL::unknown AS pospaymentmediumname, NULL::integer AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::integer AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
           FROM c_invoice i
      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   LEFT JOIN ( SELECT c.c_invoice_id, sum(c.amount) AS amount
         FROM ( SELECT 
                      CASE
                          WHEN (dt.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                          ELSE i.c_invoice_id
                      END AS c_invoice_id, pjp.amount
                 FROM ' || posJournalPaymentsFrom || ' pjp
            JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
       JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_posjournal as pj on pj.c_posjournal_id = i.c_posjournal_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
  WHERE ' || whereClauseStd || whereDateInvoices || whereUserInvoices || wherePOSInvoices ||
  ' AND date_trunc(''day''::text, i.dateacct) = date_trunc(''day''::text, pjp.dateacct::timestamp with time zone) AND ((i.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar])) AND hdr.isactive = ''Y''::bpchar OR (i.docstatus = ANY (ARRAY[''VO''::bpchar, ''RE''::bpchar])) AND hdr.isactive = ''N''::bpchar)) c
        GROUP BY c.c_invoice_id) cobros ON cobros.c_invoice_id = i.c_invoice_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE ' || whereClauseStd || whereDateInvoices || whereUserInvoices || wherePOSInvoices ||
  ' AND (cobros.amount IS NULL OR i.grandtotal <> cobros.amount) AND i.initialcurrentaccountamt > 0::numeric AND (dt.docbasetype <> ALL (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND "position"(dt.doctypekey::text, ''CDN''::text) < 1 AND (i.docstatus = ANY (ARRAY[''VO''::bpchar, ''RE''::bpchar]));';

-- raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION v_dailysales_current_account_filtered(integer, integer, integer, date, date, date, date, boolean)
  OWNER TO libertya;

--v_dailysales_current_account_payments_filtered
  
CREATE OR REPLACE FUNCTION v_dailysales_current_account_payments_filtered(
    orgid integer,
    posid integer,
    userid integer,
    datefrom date,
    dateto date)
  RETURNS SETOF v_dailysales_type AS
$BODY$
declare
	consulta varchar;
	whereDatePayments varchar;
	wherePOSPayments varchar;
	whereUserPayments varchar;
	whereOrg varchar;
	whereClauseStd varchar;
	posJournalPaymentsFrom varchar;
	dateFromPOSJournalPayments varchar;
	dateToPOSJournalPayments varchar;
	orgIDPOSJournalPayments integer;
	adocument v_dailysales_type;
BEGIN
	-- Armado de las condiciones en base a los parámetros
	-- Organización
	whereOrg = '';
	if orgID is not null AND orgID > 0 THEN
		whereOrg = ' AND i.ad_org_id = ' || orgID;
	END IF;

	-- Fechas para allocations y facturas
	whereDatePayments = '';
	if dateFrom is not null then
		whereDatePayments = ' AND date_trunc(''day'', pjp.allocationdate) >= date_trunc(''day'', ''' || dateFrom || '''::date)';
	end if;

	if dateTo is not null then
		whereDatePayments = whereDatePayments || ' AND date_trunc(''day'', pjp.allocationdate) <= date_trunc(''day'', ''' || dateTo || '''::date) ';
	end if;
	
	-- TPV
	wherePOSPayments = ' AND (' || posID || ' = -1 OR COALESCE(pjh.c_pos_id, pj.c_pos_id) = ' || posID || ')';

	-- Usuario
	whereUserPayments = ' AND (' || userID || ' = -1 OR COALESCE(pjh.ad_user_id, pj.ad_user_id) = ' || userID || ')';

	-- Condiciones básicas del reporte
	whereClauseStd = ' ( i.issotrx = ''Y'' ' ||
			 whereOrg || 
			 ' AND (i.docstatus = ''CO'' or i.docstatus = ''CL'' or i.docstatus = ''RE'' or i.docstatus = ''VO'' OR i.docstatus = ''??'') ' ||
			 ' AND dtc.isfiscaldocument = ''Y'' ' || 
			 ' AND (dtc.isfiscal is null OR dtc.isfiscal = ''N'' OR (dtc.isfiscal = ''Y'' AND i.fiscalalreadyprinted = ''Y'')) ' ||
			 ' AND dtc.doctypekey not in (''RTR'', ''RTI'', ''RCR'', ''RCI'') ' || 
			 ' AND (dtc.transactiontypefrontliva is null OR dtc.transactiontypefrontliva = ''S'') ' || 
			 ' ) ';

	-- Armado del llamado a la función que ejecuta la vista filtrada c_posjournalpayments_v
	dateFromPOSJournalPayments = (CASE WHEN dateFrom is null THEN 'null::date' ELSE '''' || dateFrom || '''::date' END);
	dateToPOSJournalPayments = (CASE WHEN dateTo is null THEN 'null::date' ELSE '''' || dateTo || '''::date' END);
	orgIDPOSJournalPayments = (CASE WHEN orgID is null THEN -1 ELSE orgID END);
	posJournalPaymentsFrom = 'c_posjournalpayments_v_filtered(' || orgIDPOSJournalPayments || ', ' || dateFromPOSJournalPayments || ', ' || dateToPOSJournalPayments || ')';

	-- Armar la consulta
	consulta = 'SELECT ''PCA''::character varying AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
        CASE
            WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
            ELSE i.c_invoice_id
        END AS c_invoice_id, pjp.allocationdate AS datetrx, pjp.c_payment_id, pjp.c_cashline_id, 
        CASE
            WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN i.c_invoice_id
            ELSE pjp.c_invoice_credit_id
        END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, pjp.amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
        CASE
            WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.c_doctype_id
            WHEN (dtc.docbasetype <> ALL (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.c_doctype_id
            WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.c_pospaymentmedium_id
            ELSE p.c_pospaymentmedium_id
        END AS c_pospaymentmedium_id, 
        CASE
            WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.docbasetype::character varying
            WHEN (dtc.docbasetype <> ALL (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.docbasetype::character varying
            WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.name
            ELSE ppm.name
        END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, ef.value AS entidadfinancieravalue, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
   FROM ' || posJournalPaymentsFrom || ' pjp
   JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
   LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_posjournal pjh ON pjh.c_posjournal_id = hdr.c_posjournal_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
   LEFT JOIN c_doctype dt ON cc.c_doctypetarget_id = dt.c_doctype_id
  WHERE ' || whereClauseStd || whereDatePayments || whereUserPayments || wherePOSPayments ||
  ' AND date_trunc(''day''::text, i.dateacct) <> date_trunc(''day''::text, pjp.allocationdateacct::timestamp with time zone) AND i.initialcurrentaccountamt > 0::numeric AND hdr.isactive = ''Y''::bpchar AND ((dtc.docbasetype <> ALL (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) OR (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL);';

-- raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION v_dailysales_current_account_payments_filtered(integer, integer, integer, date, date)
  OWNER TO libertya;

--v_dailysales_invoices_filtered

CREATE OR REPLACE FUNCTION v_dailysales_invoices_filtered(
    orgid integer,
    posid integer,
    userid integer,
    datefrom date,
    dateto date,
    invoicedatefrom date,
    invoicedateto date,
    addinvoicedate boolean)
  RETURNS SETOF v_dailysales_type AS
$BODY$
declare
	consulta varchar;
	whereDateInvoices varchar;
	whereInvoiceDate varchar;
	wherePOSInvoices varchar;
	whereUserInvoices varchar;
	whereOrg varchar;
	whereClauseStd varchar;
	adocument v_dailysales_type;
BEGIN
	-- Armado de las condiciones en base a los parámetros
	-- Organización
	whereOrg = '';
	if orgID is not null AND orgID > 0 THEN
		whereOrg = ' AND i.ad_org_id = ' || orgID;
	END IF;
	
	-- Fecha de factura
	whereInvoiceDate = '';
	if addInvoiceDate then
		if invoiceDateFrom is not null then
			whereInvoiceDate = ' AND date_trunc(''day'', i.dateacct) >= date_trunc(''day'', '''|| invoiceDateFrom || '''::date)';
		end if;
		if invoiceDateTo is not null then
			whereInvoiceDate = whereInvoiceDate || ' AND date_trunc(''day'', i.dateacct) <= date_trunc(''day'', ''' || invoiceDateTo || '''::date) ';
		end if;
	end if;

	-- Fechas para allocations y facturas
	whereDateInvoices = '';
	if dateFrom is not null then
		whereDateInvoices = ' AND date_trunc(''day''::text, i.dateinvoiced) >= date_trunc(''day'', ''' || dateFrom || '''::date)';
	end if;

	if dateTo is not null then
		whereDateInvoices = whereDateInvoices || ' AND date_trunc(''day''::text, i.dateinvoiced) <= date_trunc(''day'', ''' || dateTo || '''::date) ';
	end if;
	
	-- TPV
	wherePOSInvoices = ' AND (' || posID || ' = -1 OR pj.c_pos_id = ' || posID || ')';

	-- Usuario
	whereUserInvoices = ' AND (' || userID || ' = -1 OR pj.ad_user_id = ' || userID || ')';

	-- Condiciones básicas del reporte
	whereClauseStd = ' ( i.issotrx = ''Y'' ' ||
			 whereOrg || 
			 ' AND (i.docstatus = ''CO'' or i.docstatus = ''CL'' or i.docstatus = ''RE'' or i.docstatus = ''VO'' OR i.docstatus = ''??'') ' ||
			 ' AND dtc.isfiscaldocument = ''Y'' ' || 
			 ' AND (dtc.isfiscal is null OR dtc.isfiscal = ''N'' OR (dtc.isfiscal = ''Y'' AND i.fiscalalreadyprinted = ''Y'')) ' ||
			 ' AND dtc.doctypekey not in (''RTR'', ''RTI'', ''RCR'', ''RCI'') ' || 
			 ' AND (dtc.transactiontypefrontliva is null OR dtc.transactiontypefrontliva = ''S'') ' || 
			 ' ) ';

	-- Agregar las condiciones anteriores
	whereClauseStd = whereClauseStd || whereInvoiceDate || whereDateInvoices || wherePOSInvoices || whereUserInvoices;

	-- Armar la consulta
	consulta = 'SELECT ''I''::character varying AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc(''day''::text, i.dateinvoiced) AS datetrx, NULL::integer AS c_payment_id, NULL::integer AS c_cashline_id, NULL::integer AS c_invoice_credit_id, dtc.docbasetype AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal * dtc.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, dtc.c_doctype_id AS c_pospaymentmedium_id, dtc.name AS pospaymentmediumname, NULL::integer AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::integer AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
   FROM c_invoice i
   LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE ' || whereClauseStd ||
  ' AND NOT (
  		EXISTS (
			SELECT * FROM (
				SELECT *
				FROM c_allocationline al
				WHERE i.c_invoice_id = al.c_invoice_id AND i.isvoidable = ''Y''::bpchar 
			) as FOO
			JOIN c_payment p ON p.c_payment_id = foo.c_payment_id
			JOIN c_cashline cl ON cl.c_payment_id = p.c_payment_id
		)
	);';

-- raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION v_dailysales_invoices_filtered(integer, integer, integer, date, date, date, date, boolean)
  OWNER TO libertya;

--v_dailysales_v2_filtered

CREATE OR REPLACE FUNCTION v_dailysales_v2_filtered(
    orgid integer,
    posid integer,
    userid integer,
    datefrom date,
    dateto date,
    invoicedatefrom date,
    invoicedateto date,
    addinvoicedate boolean)
  RETURNS SETOF v_dailysales_type AS
$BODY$
declare
	consulta varchar;
	whereDateInvoices varchar;
	whereDatePayments varchar;
	whereInvoiceDate varchar;
	wherePOSInvoices varchar;
	wherePOSPayments varchar;
	whereUserInvoices varchar;
	whereUserPayments varchar;
	whereOrg varchar;
	whereClauseStd varchar;
	posJournalPaymentsFrom varchar;
	dateFromPOSJournalPayments varchar;
	dateToPOSJournalPayments varchar;
	orgIDPOSJournalPayments integer;
	adocument v_dailysales_type;
BEGIN
	-- Armado de las condiciones en base a los parámetros
	-- Organización
	whereOrg = '';
	if orgID is not null AND orgID > 0 THEN
		whereOrg = ' AND i.ad_org_id = ' || orgID;
	END IF;
	
	-- Fecha de factura
	whereInvoiceDate = '';
	if addInvoiceDate then
		if invoiceDateFrom is not null then
			whereInvoiceDate = ' AND date_trunc(''day'', i.dateacct) >= date_trunc(''day'', '''|| invoiceDateFrom || '''::date)';
		end if;
		if invoiceDateTo is not null then
			whereInvoiceDate = whereInvoiceDate || ' AND date_trunc(''day'', i.dateacct) <= date_trunc(''day'', ''' || invoiceDateTo || '''::date) ';
		end if;
	end if;

	-- Fechas para allocations y facturas
	whereDatePayments = '';
	whereDateInvoices = '';
	if dateFrom is not null then
		whereDatePayments = ' AND date_trunc(''day'', pjp.allocationdate) >= date_trunc(''day'', ''' || dateFrom || '''::date)';
		whereDateInvoices = ' AND date_trunc(''day''::text, i.dateinvoiced) >= date_trunc(''day'', ''' || dateFrom || '''::date)';
	end if;

	if dateTo is not null then
		whereDatePayments = whereDatePayments || ' AND date_trunc(''day'', pjp.allocationdate) <= date_trunc(''day'', ''' || dateTo || '''::date) ';
		whereDateInvoices = whereDateInvoices || ' AND date_trunc(''day''::text, i.dateinvoiced) <= date_trunc(''day'', ''' || dateTo || '''::date) ';
	end if;
	
	-- TPV
	wherePOSPayments = ' AND (' || posID || ' = -1 OR COALESCE(pjh.c_pos_id, pj.c_pos_id) = ' || posID || ')';
	wherePOSInvoices = ' AND (' || posID || ' = -1 OR pj.c_pos_id = ' || posID || ')';

	-- Usuario
	whereUserPayments = ' AND (' || userID || ' = -1 OR COALESCE(pjh.ad_user_id, pj.ad_user_id) = ' || userID || ')';
	whereUserInvoices = ' AND (' || userID || ' = -1 OR pj.ad_user_id = ' || userID || ')';

	-- Condiciones básicas del reporte
	whereClauseStd = ' ( i.issotrx = ''Y'' ' ||
			 whereOrg || 
			 ' AND (i.docstatus = ''CO'' or i.docstatus = ''CL'' or i.docstatus = ''RE'' or i.docstatus = ''VO'' OR i.docstatus = ''??'') ' ||
			 ' AND dtc.isfiscaldocument = ''Y'' ' || 
			 ' AND (dtc.isfiscal is null OR dtc.isfiscal = ''N'' OR (dtc.isfiscal = ''Y'' AND i.fiscalalreadyprinted = ''Y'')) ' ||
			 ' AND dtc.doctypekey not in (''RTR'', ''RTI'', ''RCR'', ''RCI'') ' || 
			 ' AND (dtc.transactiontypefrontliva is null OR dtc.transactiontypefrontliva = ''S'') ' || 
			 ' ) ';

	-- Agregar las condiciones anteriores
	whereClauseStd = whereClauseStd || whereInvoiceDate;

	-- Armado del llamado a la función que ejecuta la vista filtrada c_posjournalpayments_v
	dateFromPOSJournalPayments = (CASE WHEN dateFrom is null THEN 'null::date' ELSE '''' || dateFrom || '''::date' END);
	dateToPOSJournalPayments = (CASE WHEN dateTo is null THEN 'null::date' ELSE '''' || dateTo || '''::date' END);
	orgIDPOSJournalPayments = (CASE WHEN orgID is null THEN -1 ELSE orgID END);
	posJournalPaymentsFrom = 'c_posjournalpayments_v_filtered(' || orgIDPOSJournalPayments || ', ' || dateFromPOSJournalPayments || ', ' || dateToPOSJournalPayments || ')';

	-- Armar la consulta
	consulta = '(        (         SELECT ''P''::character varying AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                                    ELSE i.c_invoice_id
                                END AS c_invoice_id, pjp.allocationdate AS datetrx, pjp.c_payment_id, pjp.c_cashline_id, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN i.c_invoice_id
                                    ELSE pjp.c_invoice_credit_id
                                END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, pjp.amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.c_doctype_id
                                    WHEN (dtc.docbasetype <> ALL (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.c_doctype_id
                                    WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.c_pospaymentmedium_id
                                    ELSE p.c_pospaymentmedium_id
                                END AS c_pospaymentmedium_id, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.docbasetype::character varying
                                    WHEN (dtc.docbasetype <> ALL (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.docbasetype::character varying
                                    WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.name
                                    ELSE ppm.name
                                END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, ef.value AS entidadfinancieravalue, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
                           FROM ' || posJournalPaymentsFrom || ' pjp
                      JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
                 LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
            JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
       JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_posjournal pjh ON pjh.c_posjournal_id = hdr.c_posjournal_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
   LEFT JOIN c_doctype dt ON cc.c_doctypetarget_id = dt.c_doctype_id
  WHERE ' || whereClauseStd || whereDatePayments || whereUserPayments || wherePOSPayments ||
  ' AND (date_trunc(''day''::text, i.dateacct) = date_trunc(''day''::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0::numeric) AND ((dtc.docbasetype <> ALL (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) OR (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL) AND (pjp.c_invoice_credit_id IS NULL OR pjp.c_invoice_credit_id IS NOT NULL AND (cc.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar]))) AND NOT (EXISTS ( SELECT c2.c_payment_id
   FROM c_cashline c2
  WHERE c2.c_payment_id = pjp.c_payment_id AND i.isvoidable = ''Y''::bpchar))
                UNION ALL 
                         SELECT ''NCC''::character varying AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc(''day''::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 
                                CASE
                                    WHEN i.paymentrule::text = ''T''::text OR i.paymentrule::text = ''Tr''::text THEN ''A''::character varying
                                    WHEN i.paymentrule::text = ''B''::text THEN ''CA''::character varying
                                    WHEN i.paymentrule::text = ''K''::text THEN ''C''::character varying
                                    WHEN i.paymentrule::text = ''P''::text THEN ''CC''::character varying
                                    WHEN i.paymentrule::text = ''S''::text THEN ''K''::character varying
                                    ELSE i.paymentrule
                                END AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal * dtc.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
                                CASE
                                    WHEN i.paymentrule::text = ''P''::text THEN NULL::integer
                                    ELSE ( SELECT ad_ref_list.ad_ref_list_id
                                       FROM ad_ref_list
                                      WHERE ad_ref_list.ad_reference_id = 195 AND ad_ref_list.value::text = i.paymentrule::text
                                     LIMIT 1)
                                END AS c_pospaymentmedium_id, 
                                CASE
                                    WHEN i.paymentrule::text = ''T''::text OR i.paymentrule::text = ''Tr''::text THEN ''A''::character varying
                                    WHEN i.paymentrule::text = ''B''::text THEN ''CA''::character varying
                                    WHEN i.paymentrule::text = ''K''::text THEN ''C''::character varying
                                    WHEN i.paymentrule::text = ''P''::text THEN NULL::character varying
                                    WHEN i.paymentrule::text = ''S''::text THEN ''K''::character varying
                                    ELSE i.paymentrule
                                END AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
                           FROM c_invoice i
                      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
                 JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
            JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
       JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE ' || whereClauseStd || whereDateInvoices || whereUserInvoices || wherePOSInvoices ||
  ' AND dtc.docbasetype = ''ARC''::bpchar AND ((i.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar])) OR (i.docstatus = ANY (ARRAY[''VO''::bpchar, ''RE''::bpchar])) AND (EXISTS ( SELECT al.c_allocationline_id
          FROM c_allocationline al
         WHERE al.c_invoice_id = i.c_invoice_id))) AND NOT (EXISTS ( SELECT al.c_allocationline_id
          FROM c_allocationline al
         WHERE al.c_invoice_credit_id = i.c_invoice_id)))
        UNION ALL 
                 SELECT ''PA''::character varying AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
                        CASE
                            WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                            ELSE i.c_invoice_id
                        END AS c_invoice_id, pjp.allocationdate AS datetrx, pjp.c_payment_id, pjp.c_cashline_id, 
                        CASE
                            WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN i.c_invoice_id
                            ELSE pjp.c_invoice_credit_id
                        END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, pjp.amount * (-1)::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
                        CASE
                            WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.c_doctype_id
                            WHEN (dtc.docbasetype <> ALL (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.c_doctype_id
                            WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.c_pospaymentmedium_id
                            ELSE p.c_pospaymentmedium_id
                        END AS c_pospaymentmedium_id, 
                        CASE
                            WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.docbasetype::character varying
                            WHEN (dtc.docbasetype <> ALL (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.docbasetype::character varying
                            WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.name
                            ELSE ppm.name
                        END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, ef.value AS entidadfinancieravalue, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
                   FROM ' || posJournalPaymentsFrom || ' pjp
              JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
         LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
    JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_posjournal pjh ON pjh.c_posjournal_id = hdr.c_posjournal_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
   LEFT JOIN c_doctype dt ON cc.c_doctypetarget_id = dt.c_doctype_id
  WHERE ' || whereClauseStd || whereDatePayments || whereUserPayments || wherePOSPayments ||
  ' AND (date_trunc(''day''::text, i.dateacct) = date_trunc(''day''::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0::numeric) AND hdr.isactive = ''N''::bpchar AND NOT (EXISTS ( SELECT c2.c_payment_id
   FROM c_cashline c2
  WHERE c2.c_payment_id = pjp.c_payment_id AND i.isvoidable = ''Y''::bpchar)))
UNION ALL 
         SELECT ''ND''::character varying AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc(''day''::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 
                CASE
                    WHEN i.paymentrule::text = ''T''::text OR i.paymentrule::text = ''Tr''::text THEN ''A''::character varying
                    WHEN i.paymentrule::text = ''B''::text THEN ''CA''::character varying
                    WHEN i.paymentrule::text = ''K''::text THEN ''C''::character varying
                    WHEN i.paymentrule::text = ''P''::text THEN ''CC''::character varying
                    WHEN i.paymentrule::text = ''S''::text THEN ''K''::character varying
                    ELSE i.paymentrule
                END AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal * dtc.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, ( SELECT ad_ref_list.ad_ref_list_id
                   FROM ad_ref_list
                  WHERE ad_ref_list.ad_reference_id = 195 AND ad_ref_list.value::text = i.paymentrule::text
                 LIMIT 1) AS c_pospaymentmedium_id, 
                CASE
                    WHEN i.paymentrule::text = ''T''::text OR i.paymentrule::text = ''Tr''::text THEN ''A''::character varying
                    WHEN i.paymentrule::text = ''B''::text THEN ''CA''::character varying
                    WHEN i.paymentrule::text = ''K''::text THEN ''C''::character varying
                    WHEN i.paymentrule::text = ''P''::text THEN ''CC''::character varying
                    WHEN i.paymentrule::text = ''S''::text THEN ''K''::character varying
                    ELSE i.paymentrule
                END AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
           FROM c_invoice i
      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE ' || whereClauseStd || whereDateInvoices || whereUserInvoices || wherePOSInvoices ||
  ' AND "position"(dtc.doctypekey::text, ''CDN''::text) = 1 AND ((i.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar])) OR (i.docstatus = ANY (ARRAY[''VO''::bpchar, ''RE''::bpchar])) AND (EXISTS ( SELECT al.c_allocationline_id
   FROM c_allocationline al
  WHERE al.c_invoice_credit_id = i.c_invoice_id))) AND NOT (EXISTS ( SELECT al.c_allocationline_id
   FROM c_allocationline al
  WHERE al.c_invoice_id = i.c_invoice_id));';

--raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION v_dailysales_v2_filtered(integer, integer, integer, date, date, date, date, boolean)
  OWNER TO libertya;

--20171027-1104 Omisión de Presupuestos y sólo remitos de salida
CREATE OR REPLACE FUNCTION getqtyreserved(
    clientid integer,
    orgid integer,
    locatorid integer,
    productid integer,
    dateto date)
  RETURNS numeric AS
$BODY$
/***********
Obtiene la cantidad reservada a fecha de corte. Si no hay fecha de corte, entonces se devuelven los pendientes actuales.
La cantidad pendiente a una fecha de corte equivale a:
1) Sumar la cantidad pedida a esa fecha
2) Sumar las NC con la marca Actualizar Pedido luego de esa fecha.
3) Restar los transaction de inouts con tipo de documento que gestionen cantidades reservadas de stock y pendientes a fecha de corte. Salidas son negativas y entradas positivas.
4) Restar las cantidades transferidas a fecha de corte
*/
DECLARE
	reserved numeric;
BEGIN
	reserved := 0;
	--Si no hay fecha de corte o es mayor o igual a la fecha actual, entonces se suman las cantidades reservadas de los pedidos
	if ( dateTo is null OR dateTo >= current_date ) THEN
		SELECT INTO reserved coalesce(sum(ol.qtyreserved),0)
		from c_orderline ol
		inner join c_order o on o.c_order_id = ol.c_order_id
		inner join m_warehouse w on w.m_warehouse_id = o.m_warehouse_id
		inner join m_locator l on l.m_warehouse_id = w.m_warehouse_id
		where ol.qtyreserved <> 0
			and o.processed = 'Y' 
			and ol.m_product_id = productid
			and l.m_locator_id = locatorid
			and o.issotrx = 'Y';
	ELSE
		SELECT INTO reserved coalesce(sum(qty),0)
		from (
		-- Cantidad pedida a fecha de corte
		select coalesce(sum(ol.qtyordered),0) as qty
			from c_orderline ol
			inner join c_order o on o.c_order_id = ol.c_order_id
			inner join c_doctype dt on dt.c_doctype_id = o.c_doctype_id
			inner join m_warehouse w on w.m_warehouse_id = o.m_warehouse_id
			inner join m_locator l on l.m_warehouse_id = w.m_warehouse_id
			where o.ad_client_id = clientid
				and o.ad_org_id = orgid
				and o.processed = 'Y' 
				and ol.m_product_id = productid
				and l.m_locator_id = locatorid
				and o.issotrx = 'Y'
				and dt.doctypekey NOT IN ('SOSOTH','SOSOT','SOON')
				and o.dateordered::date <= dateTo::date
				and o.dateordered::date <= current_date
		union all
		-- Notas de crédito con el check Actualizar Cantidades de Pedido
		select coalesce(sum(il.qtyinvoiced),0) as qty
		from c_invoiceline il
		inner join c_invoice i on i.c_invoice_id = il.c_invoice_id
		inner join c_orderline ol on ol.c_orderline_id = il.c_orderline_id
		inner join c_order o on o.c_order_id = ol.c_order_id
		inner join m_warehouse w on w.m_warehouse_id = o.m_warehouse_id
		inner join m_locator l on l.m_warehouse_id = w.m_warehouse_id
		where o.ad_client_id = clientid
			and o.ad_org_id = orgid
			and o.processed = 'Y' 
			and ol.m_product_id = productid
			and l.m_locator_id = locatorid
			and i.issotrx = 'Y'
			and i.updateorderqty = 'Y'
			and o.dateordered::date <= dateTo::date
			and i.dateinvoiced::date > dateTo::date
			and i.dateinvoiced::date <= current_date
		union all
		--En transaction las salidas son negativas y las entradas positivas
		select coalesce(sum(t.movementqty),0) as qty
		from m_transaction t
		inner join m_inoutline iol on iol.m_inoutline_id = t.m_inoutline_id
		inner join m_inout io on io.m_inout_id = iol.m_inout_id
		inner join c_doctype dt on dt.c_doctype_id = io.c_doctype_id
		inner join c_orderline ol on ol.c_orderline_id = iol.c_orderline_id
		inner join c_order o on o.c_order_id = ol.c_order_id
		inner join c_doctype as dto on dto.c_doctype_id = o.c_doctype_id
		where t.ad_client_id = clientid
			and t.ad_org_id = orgid
			and t.m_product_id = productid
			and t.m_locator_id = locatorid
			and dt.reservestockmanagment = 'Y'
			and dto.doctypekey NOT IN ('SOSOTH','SOSOT','SOON')
			and o.issotrx = 'Y'
			and o.dateordered::date <= dateTo::date
			and t.movementdate::date <= dateTo::date
			and t.movementdate::date <= current_date
		union all
		--Cantidades transferidas
		select coalesce(sum(ol.qtyordered * -1),0) as qty
		from c_orderline ol
		inner join c_order o on o.c_order_id = ol.c_order_id
		inner join c_orderline rl on rl.c_orderline_id = ol.ref_orderline_id
		inner join c_order r on r.c_order_id = rl.c_order_id
		inner join c_doctype dt on dt.c_doctype_id = o.c_doctype_id
		inner join m_warehouse w on w.m_warehouse_id = o.m_warehouse_id
		inner join m_locator l on l.m_warehouse_id = w.m_warehouse_id
		where o.ad_client_id = clientid
			and o.ad_org_id = orgid
			and o.processed = 'Y' 
			and ol.m_product_id = productid
			and l.m_locator_id = locatorid
			and o.issotrx = 'Y'
			and dt.doctypekey IN ('SOSOT')
			and r.dateordered::date <= dateTo::date
			and o.dateordered::date <= dateTo::date
			and o.dateordered::date <= current_date
		) todo;
	END IF;
	
	return reserved;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION getqtyreserved(integer, integer, integer, integer, date)
  OWNER TO libertya;
  
--20171030-1300 Nueva columna de configuración por tipo de documento para aplicar percepciones automáticamente
update ad_system set dummy = (SELECT addcolumnifnotexists('C_DocType','applyperception','character(1) NOT NULL DEFAULT ''Y''::bpchar'));

--20171108-1351 Soporte postgres 8.4 creacion de indices validando que no existan previamente
CREATE OR REPLACE FUNCTION addindexifnotexists(
    indexname character varying,
    tablename character varying,
    columnname character varying)
  RETURNS numeric AS
$BODY$
DECLARE
	existe integer;
BEGIN
	
	  select into existe count(1)
	from
	    pg_class t,
	    pg_class i,
	    pg_index ix,
	    pg_attribute a
	where
	    t.oid = ix.indrelid
	    and i.oid = ix.indexrelid
	    and a.attrelid = t.oid
	    and a.attnum = ANY(ix.indkey)
	    and t.relkind = 'r'
	    and lower(i.relname) = lower(indexname)
	    and lower(t.relname) = lower(tablename)
	    and lower(a.attname) = lower(columnname);


	IF (existe = 0) THEN
		EXECUTE 'CREATE INDEX ' || indexname || 
			' ON ' || tablename || 
			' (' || columnname || ')';
		RETURN 1;
	END IF;

	RETURN 0;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION addindexifnotexists(character varying, character varying, character varying)
  OWNER TO libertya;

--20171108-1355 Indicie para referencia a inoutline desde invoiceline
update ad_system set dummy = (SELECT addindexifnotexists('invoiceline_inoutline','c_invoiceline','m_inoutline_id')); 

--20171108-1405 No se debe tener en cuenta el tipo de documento 'Pedido Transferible'
CREATE OR REPLACE FUNCTION update_reserved(
    clientid integer,
    orgid integer,
    productid integer)
  RETURNS void AS
$BODY$
/***********
Actualiza la cantidad reservada de los depósitos de la compañía, organización y artículo parametro, 
siempre y cuando existan los regitros en m_storage 
y sólo sobre locators marcados como default ya que asi se realiza al procesar pedidos.
Las cantidades reservadas se obtienen de pedidos procesados. 
IMPORTANTE: No funciona para artículos que no son ITEMS (Stockeables)
*/
BEGIN
	update m_storage s
	set qtyreserved = coalesce((select sum(ol.qtyreserved) as qtypending
					from c_orderline ol
					inner join c_order o on o.c_order_id = ol.c_order_id
					inner join m_warehouse w on w.m_warehouse_id = o.m_warehouse_id
					inner join m_locator l on l.m_warehouse_id = w.m_warehouse_id
					inner join c_doctype dt on dt.c_doctype_id = o.c_doctypetarget_id
					where ol.qtyreserved <> 0
						and o.processed = 'Y' 
						and s.m_product_id = ol.m_product_id
						and s.m_locator_id = l.m_locator_id
						and o.issotrx = 'Y'
						and dt.doctypekey <> 'SOSOT'),0)
	where ad_client_id = clientid
		and (orgid = 0 or ad_org_id = orgid)
		and (productid = 0 or m_product_id = productid)
		and s.m_locator_id IN (select defaultLocator 
					from (select m_warehouse_id, max(m_locator_id) as defaultLocator
						from m_locator l
						where l.isdefault = 'Y' and l.isactive = 'Y'
						GROUP by m_warehouse_id) as dl);
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION update_reserved(integer, integer, integer)
  OWNER TO libertya;
  
--20171109-1344 Sobrecarga de funcion para gestion de indices que soporta whereclause
CREATE OR REPLACE FUNCTION addindexifnotexists(
    indexname character varying,
    tablename character varying,
    columnname character varying,
    whereclause character varying)
  RETURNS numeric AS
$BODY$
DECLARE
	existe integer;
BEGIN
	
	  select into existe count(1)
	from
	    pg_class t,
	    pg_class i,
	    pg_index ix,
	    pg_attribute a
	where
	    t.oid = ix.indrelid
	    and i.oid = ix.indexrelid
	    and a.attrelid = t.oid
	    and a.attnum = ANY(ix.indkey)
	    and t.relkind = 'r'
	    and lower(i.relname) = lower(indexname)
	    and lower(t.relname) = lower(tablename)
	    and lower(a.attname) = lower(columnname);


	IF (existe = 0) THEN
		EXECUTE 'CREATE INDEX ' || indexname || 
			' ON ' || tablename || 
			' (' || columnname || ') ' || 
			whereclause;
		RETURN 1;
	END IF;

	RETURN 0;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION addindexifnotexists(character varying, character varying, character varying, character varying)
  OWNER TO libertya;

--20171109-1344 La funcion con 3 argumentos llama a la completa
CREATE OR REPLACE FUNCTION addindexifnotexists(
    indexname character varying,
    tablename character varying,
    columnname character varying)
  RETURNS numeric AS
$BODY$
BEGIN
	RETURN addindexifnotexists(indexname, tablename, columnname, '');
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION addindexifnotexists(character varying, character varying, character varying)
  OWNER TO libertya;

--20171109-1344 Indice especial para gestion tarjetas
update ad_system set dummy = (SELECT addindexifnotexists('payment_auditstatus','c_payment','auditstatus','WHERE auditstatus=''TV'''));

--20171125-1800 Habilitar número de cheque manual
update ad_system set dummy = (SELECT addcolumnifnotexists('C_BankAccountDoc','allowmanualcheckno','character(1) NOT NULL DEFAULT ''N''::bpchar'));

update C_BankAccountDoc
set allowmanualcheckno = 'Y';

--20171129-0900 Habilitar cache en periodos y derivados
update ad_system set dummy = (SELECT addcolumnifnotexists('C_Calendar','cacheenabled','character(1) NOT NULL DEFAULT ''Y''::bpchar'));

--20171129-1245 Cláusula where en el procesador contable
update ad_system set dummy = (SELECT addcolumnifnotexists('C_AcctProcessor','whereclause','character varying(2000)'));

--20171201-1105 Performance: reducir el numero de registros a recuperar en cashlines en el ultimo union. Resultados en pruebas: 50s -> 1s
CREATE OR REPLACE FUNCTION v_documents_org_filtered(
    bpartner integer,
    summaryonly boolean,
    condition character,
    dateto timestamp without time zone)
  RETURNS SETOF v_documents_org_type_condition AS
$BODY$
declare
    consulta varchar;
    orderby1 varchar;
    orderby2 varchar;
    orderby3 varchar;
    leftjoin1 varchar;
    leftjoin2 varchar;
    advancedcondition varchar;
    advancedconditioncashlineexception varchar;
    whereclauseConditionDebit varchar;
    whereclauseConditionCredit varchar;
    whereclauseConditionCreditCashException varchar;
    whereclauseDateTo varchar;
    selectallocationNull varchar;
    selectallocationPayment varchar;
    selectallocationCashline varchar;
    selectallocationCredit varchar;
    selectAllocationReferencePayment varchar;
    selectAllocationReferenceCashline varchar;
    selectAllocationReferenceCredit varchar;
    adocument v_documents_org_type_condition;
   
BEGIN
    whereclauseDateTo = ' ( 1 = 1 ) ';
    -- Armar la condición para fecha de corte
    if dateTo is not null then 
		whereclauseDateTo = ' dateacct::date <= ''' || dateTo || '''::date ';
    end if;
    
    --Si no se deben mostrar todos, entonces agregar la condicion por la forma de pago
    if condition <> 'A' then
		--Si se debe mostrar sólo efectivo, entonces no se debe mostrar los anticipos, si o si debe tener una factura asociada
		advancedcondition = 'il.paymentrule is null OR ';
		advancedconditioncashlineexception = ' (1=1) ';
		if condition = 'B' then
			advancedcondition = '';
			advancedconditioncashlineexception = ' (1=2) ';
		end if;
		whereclauseConditionDebit = ' (i.paymentrule = ''' || condition || ''') ';
		whereclauseConditionCredit = ' (' || advancedcondition || ' il.paymentrule = ''' || condition || ''') ';
		whereclauseConditionCreditCashException = '( CASE WHEN il.paymentrule is not null THEN il.paymentrule = ''' || condition || ''' WHEN ic.paymentrule is not null THEN ic.paymentrule = ''' || condition || ''' ELSE '|| advancedconditioncashlineexception ||' END ) ';
	else
		whereclauseConditionDebit = ' (1 = 1) ';
		whereclauseConditionCredit = ' (1 = 1) ';
		whereclauseConditionCreditCashException = ' (1 = 1) '; 
    end if;    

    -- recuperar informacion minima indispensable si summaryonly es true.  en caso de ser false, debe joinearse/ordenarse, etc.
    if summaryonly = false then

        orderby1 = ' ORDER BY ''C_Invoice''::text, i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name, dt.printname, i.documentno, i.issotrx, i.docstatus,
                 CASE
                     WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
                     ELSE i.dateinvoiced
                 END, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced, bp.socreditstatus ';

        orderby2 = ' ORDER BY ''C_Payment''::text, p.c_payment_id, p.ad_client_id, COALESCE(il.ad_org_id, p.ad_org_id), p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name, dt.printname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt, NULL::integer, p.duedate, bp.socreditstatus ';

        orderby3 = ' ORDER BY ''C_CashLine''::text, cl.c_cashline_id, cl.ad_client_id, COALESCE(il.ad_org_id, ic.ad_org_id, cl.ad_org_id), cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby,
                CASE
                    WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id
		    WHEN il.c_bpartner_id IS NOT NULL THEN il.c_bpartner_id
                    ELSE ic.c_bpartner_id
                END, dt.c_doctype_id,
                CASE
                    WHEN cl.amount < 0.0 THEN 1
                    ELSE (-1)
                END, dt.name, dt.printname, ''@line@''::text || cl.line::character varying::text,
                CASE
                    WHEN cl.amount < 0.0 THEN ''N''::bpchar
                    ELSE ''Y''::bpchar
                END, cl.docstatus, c.statementdate, c.dateacct, cl.c_currency_id, NULL::integer, abs(cl.amount), NULL::timestamp without time zone, COALESCE(bp.socreditstatus, bp2.socreditstatus, bp3.socreditstatus) ';
	
		selectallocationNull = ' NULL::integer ';
		selectallocationPayment = selectallocationNull;
		selectallocationCashline = selectallocationNull;
		selectallocationCredit = selectallocationNull;
	
    else
        orderby1 = '';
        orderby2 = '';
        orderby3 = '';

		selectAllocationReferencePayment = ' al.c_payment_id = p.c_payment_id ';
		selectAllocationReferenceCashline = ' al.c_cashline_id = cl.c_cashline_id ';
		selectAllocationReferenceCredit = ' al.c_invoice_credit_id = i.c_invoice_id ';
	
		selectallocationPayment = ' (SELECT ah.c_allocationhdr_id FROM c_allocationline al INNER JOIN c_allocationhdr ah on ah.c_allocationhdr_id = al.c_allocationhdr_id WHERE allocationtype <> ''MAN'' AND ah.dateacct::date = p.dateacct::date AND ' || selectAllocationReferencePayment || ' AND ' || whereclauseDateTo || ' ORDER BY ah.created LIMIT 1) as c_allocationhdr_id ';
		selectallocationCashline = ' (SELECT ah.c_allocationhdr_id FROM c_allocationline al INNER JOIN c_allocationhdr ah on ah.c_allocationhdr_id = al.c_allocationhdr_id WHERE allocationtype <> ''MAN'' AND ah.dateacct::date = c.dateacct::date AND ' || selectAllocationReferenceCashline || ' AND ' || whereclauseDateTo || ' ORDER BY ah.created LIMIT 1) as c_allocationhdr_id ';
		selectallocationCredit = ' (SELECT ah.c_allocationhdr_id FROM c_allocationline al INNER JOIN c_allocationhdr ah on ah.c_allocationhdr_id = al.c_allocationhdr_id WHERE allocationtype <> ''MAN'' AND ah.dateacct::date = i.dateacct::date AND ' || selectAllocationReferenceCredit || ' AND ' || whereclauseDateTo || ' ORDER BY ah.created LIMIT 1) as c_allocationhdr_id ';
	
    end if;    

    consulta = ' SELECT * FROM 

        (        ( SELECT DISTINCT ''C_Invoice''::text AS documenttable, i.c_invoice_id AS document_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, i.documentno, i.issotrx, i.docstatus,
                        CASE
                            WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
                            ELSE i.dateinvoiced
                        END AS datetrx, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal AS amount, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced AS truedatetrx, bp.socreditstatus, i.c_order_id, '
				|| selectallocationCredit || 
               ' FROM c_invoice_v i
              JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
         JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id and (' || $1 || ' = -1  or bp.c_bpartner_id = ' || $1 || ')
    LEFT JOIN c_invoicepayschedule ips ON i.c_invoicepayschedule_id = ips.c_invoicepayschedule_id
    WHERE 

' || whereclauseConditionDebit || '
' || orderby1 || '

    )
        UNION ALL
                ( SELECT DISTINCT ''C_Payment''::text AS documenttable, p.c_payment_id AS document_id, p.ad_client_id, COALESCE(il.ad_org_id, p.ad_org_id) AS ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt AS amount, NULL::integer AS c_invoicepayschedule_id, p.duedate, p.datetrx AS truedatetrx, bp.socreditstatus, 0 as c_order_id, '
		|| selectallocationPayment || 
                  ' FROM c_payment p
              JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id
         JOIN c_bpartner bp ON p.c_bpartner_id = bp.c_bpartner_id AND (' || $1 || ' = -1 or p.c_bpartner_id = ' || $1 || ')
	LEFT JOIN c_allocationline al ON al.c_payment_id = p.c_payment_id 
	LEFT JOIN c_invoice il ON il.c_invoice_id = al.c_invoice_id
	LEFT JOIN M_BoletaDepositoLine bdlr on bdlr.c_reverse_payment_id = p.c_payment_id
	LEFT JOIN M_BoletaDeposito bdr on bdr.M_BoletaDeposito_ID = bdlr.M_BoletaDeposito_ID
	LEFT JOIN M_BoletaDepositoLine bdle on bdle.c_depo_payment_id = p.c_payment_id
	LEFT JOIN M_BoletaDeposito bde on bde.M_BoletaDeposito_ID = bdle.M_BoletaDeposito_ID
	LEFT JOIN M_BoletaDeposito bddb on bddb.c_boleta_payment_id = p.c_payment_id
  WHERE 
CASE
    WHEN il.ad_org_id IS NOT NULL AND il.ad_org_id <> p.ad_org_id THEN p.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar])
    ELSE 1 = 1
END 

AND (CASE WHEN bdr.M_BoletaDeposito_ID IS NOT NULL 
		OR bde.M_BoletaDeposito_ID IS NOT NULL 
		OR bddb.M_BoletaDeposito_ID IS NOT NULL THEN p.docstatus NOT IN (''CO'',''CL'') 
	ELSE 1 = 1
	END) 

AND ' || whereclauseConditionCredit || '

' || orderby2 || '


)

UNION ALL

        ( SELECT DISTINCT ''C_CashLine''::text AS documenttable, cl.c_cashline_id AS document_id, cl.ad_client_id, COALESCE(il.ad_org_id, ic.ad_org_id, cl.ad_org_id) AS ad_org_id, cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby,
                CASE
                    WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id
		    WHEN il.c_bpartner_id IS NOT NULL THEN il.c_bpartner_id
                    ELSE ic.c_bpartner_id
                END AS c_bpartner_id, dt.c_doctype_id,
                CASE
                    WHEN cl.amount < 0.0 THEN 1
                    ELSE (-1)
                END AS signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, ''@line@''::text || cl.line::character varying::text AS documentno,
                CASE
                    WHEN cl.amount < 0.0 THEN ''N''::bpchar
                    ELSE ''Y''::bpchar
                END AS issotrx, cl.docstatus, c.statementdate AS datetrx, c.dateacct, cl.c_currency_id, NULL::integer AS c_conversiontype_id, abs(cl.amount) AS amount, NULL::integer AS c_invoicepayschedule_id, NULL::timestamp without time zone AS duedate, c.statementdate AS truedatetrx, COALESCE(bp.socreditstatus, bp2.socreditstatus, bp3.socreditstatus) AS socreditstatus, 0 as c_order_id, '
                || selectallocationCashline || 
       ' FROM (
		select cl.* FROM c_cashline cl 
		where (' || $1 || ' = -1 or cl.c_bpartner_id = ' || $1 || ')
		union 
		select cl.* FROM c_cashline cl 
		LEFT JOIN c_invoice ic on ic.c_invoice_id = cl.c_invoice_id AND (' || $1 || ' = -1 or ic.c_bpartner_id = ' || $1 || ')
		where (' || $1 || ' = -1 or cl.c_bpartner_id = ' || $1 || ')
		union 
		select cl.* FROM c_cashline cl 
		LEFT JOIN c_allocationline al ON al.c_cashline_id = cl.c_cashline_id
		LEFT JOIN c_invoice il ON il.c_invoice_id = al.c_invoice_id AND (' || $1 || ' = -1 or il.c_bpartner_id = ' || $1 || ')
		where (' || $1 || ' = -1 or cl.c_bpartner_id = ' || $1 || ')
       ) as cl
      JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
   LEFT JOIN c_bpartner bp ON cl.c_bpartner_id = bp.c_bpartner_id AND (' || $1 || ' = -1 or cl.c_bpartner_id = ' || $1 || ')
   JOIN ( SELECT d.ad_client_id, d.c_doctype_id, d.name, d.printname
         FROM c_doctype d
        WHERE d.doctypekey::text = ''CMC''::text) dt ON cl.ad_client_id = dt.ad_client_id
   LEFT JOIN c_allocationline al ON al.c_cashline_id = cl.c_cashline_id
   LEFT JOIN c_invoice il ON il.c_invoice_id = al.c_invoice_id AND (' || $1 || ' = -1 or il.c_bpartner_id = ' || $1 || ')
   LEFT JOIN c_bpartner bp2 ON il.c_bpartner_id = bp2.c_bpartner_id
   LEFT JOIN c_invoice ic on ic.c_invoice_id = cl.c_invoice_id AND (' || $1 || ' = -1 or ic.c_bpartner_id = ' || $1 || ')
   LEFT JOIN c_bpartner bp3 ON ic.c_bpartner_id = bp3.c_bpartner_id
  WHERE (CASE WHEN cl.c_bpartner_id IS NOT NULL THEN (' || $1 || ' = -1 or cl.c_bpartner_id = ' || $1 || ')
        WHEN il.c_bpartner_id IS NOT NULL THEN (' || $1 || ' = -1 or il.c_bpartner_id = ' || $1 || ')
        WHEN ic.c_bpartner_id IS NOT NULL THEN (' || $1 || ' = -1 or ic.c_bpartner_id = ' || $1 || ')
        ELSE 1 = 2 END)
    AND (CASE WHEN (il.ad_org_id IS NOT NULL AND il.ad_org_id <> cl.ad_org_id) OR (ic.ad_org_id IS NOT NULL AND ic.ad_org_id <> cl.ad_org_id)
        THEN cl.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar])
        ELSE 1 = 1 END)

    AND (' || whereclauseConditionCreditCashException || ' )

' || orderby3 || '

)) AS d  
WHERE ' || whereclauseDateTo || ' ; ';

-- raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION v_documents_org_filtered(integer, boolean, character, timestamp without time zone)
  OWNER TO libertya;
  
--20171213-1535 Carga de diarios manuales
update ad_system set dummy = (SELECT addcolumnifnotexists('GL_Journal','CopyFrom','character(1)'));


-- 20171215-1003 Nuevo parametro UpdateBalances. Logica por defecto N
insert into ad_process_para values ((select nextval('seq_ad_process_para')),0,0,'Y',now(),100,now(),100,'UpdateBalances',null,null,(select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1000174'),100,20,null,null,'UpdateBalances','Y',0,'Y','N','N',null,null,null,null,null,'U','CORE-AD_Process_Para-20171214083805',null,'N',null,null,'N','N',null,'N');

-- Traducciones al parámetro
insert into ad_process_para_trl values ((select ad_process_para_id from ad_process_para where ad_componentobjectuid = 'CORE-AD_Process_Para-20171214083805'), 'es_AR', 0, 0, 'Y', now(), 100, now(), 100, 'Act.Balance', null, null, 'Y', 'CORE_AD_Process_Para_Trl_20171214083805-es_AR', null);
insert into ad_process_para_trl values ((select ad_process_para_id from ad_process_para where ad_componentobjectuid = 'CORE-AD_Process_Para-20171214083805'), 'es_ES', 0, 0, 'Y', now(), 100, now(), 100, 'Act.Balance', null, null, 'Y', 'CORE_AD_Process_Para_Trl_20171214083805-es_ES', null);
insert into ad_process_para_trl values ((select ad_process_para_id from ad_process_para where ad_componentobjectuid = 'CORE-AD_Process_Para-20171214083805'), 'es_MX', 0, 0, 'Y', now(), 100, now(), 100, 'Act.Balance', null, null, 'Y', 'CORE_AD_Process_Para_Trl_20171214083805-es_MX', null);
insert into ad_process_para_trl values ((select ad_process_para_id from ad_process_para where ad_componentobjectuid = 'CORE-AD_Process_Para-20171214083805'), 'es_PY', 0, 0, 'Y', now(), 100, now(), 100, 'Act.Balance', null, null, 'Y', 'CORE_AD_Process_Para_Trl_20171214083805-es_PY', null);

-- Nueva columna updatebalances en tabla t (si no la incorporaba, no se visualizaba nada en el informe
update ad_system set dummy = (SELECT addcolumnifnotexists('t_acct_detail','updatebalances','varchar(1) not null default ''N'''));

--20171220-2222 Se toma el importe como ingreso o egreso dependiendo el signo del tipo de documento
CREATE OR REPLACE FUNCTION c_pos_declaracionvalores_payments_filtered(posjournalids anyarray)
  RETURNS SETOF c_pos_declaracionvalores_type AS
$BODY$
declare
	consulta varchar;
	wherePosJournalPaymentsIDs varchar;
	whereClauseStd varchar;
	existsPosJournalIDs boolean;
	parameterFunctionCall varchar;
	posJournalPaymentsFunctionCall varchar;
	adocument c_pos_declaracionvalores_type;
BEGIN
	-- Armado de condiciones de filtro parámetro
	existsPosJournalIDs = posjournalIDs is not null and array_length(posjournalIDs,1) > 0 and posjournalIDs[1] <> -1;
	
	-- Filtro de cajas diarias
	wherePosJournalPaymentsIDs = '';
	if existsPosJournalIDs then
		wherePosJournalPaymentsIDs = ' AND pj.c_posjournal_id IN ('|| array_to_string(posjournalIDs, ',') || ')';
	end if;
	
	-- Condición std
	whereClauseStd = 'p.docstatus in (''CO'',''CL'')';

	-- Armado del parámetro a la función c_posjournalinvoices_v_filtered
	parameterFunctionCall = 'array[-1]';
	if existsPosJournalIDs then
		parameterFunctionCall = 'ARRAY[' || array_to_string(posjournalIDs, ',') || ']';
	end if;
	
	-- Armado de llamado a función c_posjournalinvoices_v_filtered
	posJournalPaymentsFunctionCall = 'c_posjournal_c_payment_v_filtered(' || parameterFunctionCall || ')';
	
	-- Consulta
	consulta = 
	'SELECT p.ad_client_id, p.ad_org_id, p.c_posjournal_id, p.ad_user_id, p.c_currency_id, p.datetrx, p.docstatus, NULL::text AS category, p.tendertype, (p.documentno::text || '' ''::text) || COALESCE(p.description, ''''::character varying)::text AS description, p.c_charge_id, p.chargename, p.c_payment_id AS doc_id, 
        CASE WHEN (p.total * p.signo_issotrx) < 0 
            THEN abs(p.total)
            ELSE 0::numeric
        END::numeric(22,2) AS ingreso, 
        CASE WHEN (p.total * p.signo_issotrx) >= 0 
	    THEN abs(p.total)
            ELSE 0::numeric
        END::numeric(22,2) AS egreso, p.c_invoice_id, p.invoice_documentno, p.invoice_grandtotal, p.entidadfinanciera_value, p.entidadfinanciera_name, p.bp_entidadfinanciera_value, p.bp_entidadfinanciera_name, p.cupon, p.creditcard, NULL::unknown AS generated_invoice_documentno, p.allocation_active, p.c_pos_id, p.posname, p.m_entidadfinanciera_id
   FROM ( SELECT p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date AS datetrx, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name AS chargename, sum(pjp.amount + 
                CASE
                    WHEN p.tendertype = ''C''::bpchar THEN pjp.changeamt
                    ELSE 0::numeric
                END)::numeric(22,2) AS total, pjp.invoice_documentno, (pjp.invoice_grandtotal + 
                CASE
                    WHEN p.tendertype = ''C''::bpchar THEN pjp.changeamt
                    ELSE 0::numeric
                END)::numeric(20,2) AS invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive AS allocation_active, pjp.c_invoice_id, pos.c_pos_id, pos.name AS posname, pjp.m_entidadfinanciera_id, dt.signo_issotrx
           FROM c_payment p
           INNER JOIN c_doctype dt on dt.c_doctype_id = p.c_doctype_id
      JOIN ' || posJournalPaymentsFunctionCall || ' pjp ON pjp.c_payment_id = p.c_payment_id
   JOIN c_posjournal pj ON pj.c_posjournal_id = p.c_posjournal_id
   JOIN c_pos pos ON pos.c_pos_id = pj.c_pos_id
   LEFT JOIN c_charge ch ON ch.c_charge_id = p.c_charge_id
   LEFT JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
  WHERE ' || whereClauseStd || wherePosJournalPaymentsIDs ||
  ' AND (i.c_invoice_id IS NULL OR NOT (EXISTS ( SELECT cl.c_cashline_id
   FROM c_cashline cl
  WHERE cl.c_payment_id = p.c_payment_id AND i.isvoidable = ''Y''::bpchar)))
  GROUP BY p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.changeamt, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive, pjp.c_invoice_id, pos.c_pos_id, pos.name, pjp.m_entidadfinanciera_id, dt.signo_issotrx) p;';

--raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION c_pos_declaracionvalores_payments_filtered(anyarray)
  OWNER TO libertya; 
  
--20180105-1030 Incorporación de currencyconverts en el informe Resumen de Ventas
CREATE OR REPLACE FUNCTION v_dailysales_current_account_filtered(
    orgid integer,
    posid integer,
    userid integer,
    datefrom date,
    dateto date,
    invoicedatefrom date,
    invoicedateto date,
    addinvoicedate boolean)
  RETURNS SETOF v_dailysales_type AS
$BODY$
declare
	consulta varchar;
	whereDateInvoices varchar;
	whereInvoiceDate varchar;
	wherePOSInvoices varchar;
	whereUserInvoices varchar;
	whereOrg varchar;
	whereClauseStd varchar;
	posJournalPaymentsFrom varchar;
	dateFromPOSJournalPayments varchar;
	dateToPOSJournalPayments varchar;
	orgIDPOSJournalPayments integer;
	adocument v_dailysales_type;
BEGIN
	-- Armado de las condiciones en base a los parámetros
	-- Organización
	whereOrg = '';
	if orgID is not null AND orgID > 0 THEN
		whereOrg = ' AND i.ad_org_id = ' || orgID;
	END IF;
	
	-- Fecha de factura
	whereInvoiceDate = '';
	if addInvoiceDate then
		if invoiceDateFrom is not null then
			whereInvoiceDate = ' AND date_trunc(''day'', i.dateacct) >= date_trunc(''day'', '''|| invoiceDateFrom || '''::date)';
		end if;
		if invoiceDateTo is not null then
			whereInvoiceDate = whereInvoiceDate || ' AND date_trunc(''day'', i.dateacct) <= date_trunc(''day'', ''' || invoiceDateTo || '''::date) ';
		end if;
	end if;

	-- Fechas para allocations y facturas
	whereDateInvoices = '';
	if dateFrom is not null then
		whereDateInvoices = ' AND date_trunc(''day''::text, i.dateinvoiced) >= date_trunc(''day'', ''' || dateFrom || '''::date)';
	end if;

	if dateTo is not null then
		whereDateInvoices = whereDateInvoices || ' AND date_trunc(''day''::text, i.dateinvoiced) <= date_trunc(''day'', ''' || dateTo || '''::date) ';
	end if;
	
	-- TPV
	wherePOSInvoices = ' AND (' || posID || ' = -1 OR pj.c_pos_id = ' || posID || ')';

	-- Usuario
	whereUserInvoices = ' AND (' || userID || ' = -1 OR pj.ad_user_id = ' || userID || ')';

	-- Condiciones básicas del reporte
	whereClauseStd = ' ( i.issotrx = ''Y'' ' ||
			 whereOrg || 
			 ' AND (i.docstatus = ''CO'' or i.docstatus = ''CL'' or i.docstatus = ''RE'' or i.docstatus = ''VO'' OR i.docstatus = ''??'') ' ||
			 ' AND dt.isfiscaldocument = ''Y'' ' || 
			 ' AND (dt.isfiscal is null OR dt.isfiscal = ''N'' OR (dt.isfiscal = ''Y'' AND i.fiscalalreadyprinted = ''Y'')) ' ||
			 ' AND dt.doctypekey not in (''RTR'', ''RTI'', ''RCR'', ''RCI'') ' || 
			 ' AND (dt.transactiontypefrontliva is null OR dt.transactiontypefrontliva = ''S'') ' || 
			 ' ) ';

	-- Agregar las condiciones anteriores
	whereClauseStd = whereClauseStd || whereInvoiceDate;

	-- Armado del llamado a la función que ejecuta la vista filtrada c_posjournalpayments_v
	dateFromPOSJournalPayments = (CASE WHEN dateFrom is null THEN 'null::date' ELSE '''' || dateFrom || '''::date' END);
	dateToPOSJournalPayments = (CASE WHEN dateTo is null THEN 'null::date' ELSE '''' || dateTo || '''::date' END);
	orgIDPOSJournalPayments = (CASE WHEN orgID is null THEN -1 ELSE orgID END);
	posJournalPaymentsFrom = 'c_posjournalpayments_v_filtered(' || orgIDPOSJournalPayments || ', ' || dateFromPOSJournalPayments || ', ' || dateToPOSJournalPayments || ')';

	-- Armar la consulta
	consulta = 'SELECT ''CAI''::character varying AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc(''day''::text, i.dateinvoiced) AS datetrx, NULL::integer AS c_payment_id, NULL::integer AS c_cashline_id, NULL::integer AS c_invoice_credit_id, ''CC'' AS tendertype, i.documentno, i.description, NULL::unknown AS info, (currencybase(i.grandtotal, i.c_currency_id, i.dateacct, i.ad_client_id, i.ad_org_id)::numeric(20,2) - COALESCE(cobros.amount, 0::numeric))::numeric(20,2) AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, NULL::unknown AS c_pospaymentmedium_id, NULL::unknown AS pospaymentmediumname, NULL::integer AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::integer AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
           FROM c_invoice i
      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   LEFT JOIN ( SELECT c.c_invoice_id, sum(c.amount) AS amount
         FROM ( SELECT 
                      CASE
                          WHEN (dt.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                          ELSE i.c_invoice_id
                      END AS c_invoice_id, currencybase(pjp.amount, i.c_currency_id, pjp.dateacct, pjp.ad_client_id, pjp.ad_org_id)::numeric(20,2) as amount
                 FROM ' || posJournalPaymentsFrom || ' pjp
            JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
       JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_posjournal as pj on pj.c_posjournal_id = i.c_posjournal_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
  WHERE ' || whereClauseStd || whereDateInvoices || whereUserInvoices || wherePOSInvoices ||
  ' AND date_trunc(''day''::text, i.dateacct) = date_trunc(''day''::text, pjp.dateacct::timestamp with time zone) AND ((i.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar])) AND hdr.isactive = ''Y''::bpchar OR (i.docstatus = ANY (ARRAY[''VO''::bpchar, ''RE''::bpchar])) AND hdr.isactive = ''N''::bpchar)) c
        GROUP BY c.c_invoice_id) cobros ON cobros.c_invoice_id = i.c_invoice_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE ' || whereClauseStd || whereDateInvoices || whereUserInvoices || wherePOSInvoices ||
  ' AND (cobros.amount IS NULL OR i.grandtotal <> cobros.amount) AND i.initialcurrentaccountamt > 0::numeric AND (dt.docbasetype <> ALL (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND "position"(dt.doctypekey::text, ''CDN''::text) < 1
UNION ALL 
         SELECT ''CAIA''::character varying AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc(''day''::text, i.dateinvoiced) AS datetrx, NULL::integer AS c_payment_id, NULL::integer AS c_cashline_id, NULL::integer AS c_invoice_credit_id, ''CC'' AS tendertype, i.documentno, i.description, NULL::unknown AS info, (currencybase(i.grandtotal, i.c_currency_id, i.dateacct, i.ad_client_id, i.ad_org_id)::numeric(20,2) - COALESCE(cobros.amount, 0::numeric))::numeric(20,2) * (-1)::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, NULL::integer AS c_pospaymentmedium_id, NULL::unknown AS pospaymentmediumname, NULL::integer AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::integer AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
           FROM c_invoice i
      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   LEFT JOIN ( SELECT c.c_invoice_id, sum(c.amount) AS amount
         FROM ( SELECT 
                      CASE
                          WHEN (dt.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                          ELSE i.c_invoice_id
                      END AS c_invoice_id, currencybase(pjp.amount, i.c_currency_id, pjp.dateacct, pjp.ad_client_id, pjp.ad_org_id)::numeric(20,2) as amount
                 FROM ' || posJournalPaymentsFrom || ' pjp
            JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
       JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_posjournal as pj on pj.c_posjournal_id = i.c_posjournal_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
  WHERE ' || whereClauseStd || whereDateInvoices || whereUserInvoices || wherePOSInvoices ||
  ' AND date_trunc(''day''::text, i.dateacct) = date_trunc(''day''::text, pjp.dateacct::timestamp with time zone) AND ((i.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar])) AND hdr.isactive = ''Y''::bpchar OR (i.docstatus = ANY (ARRAY[''VO''::bpchar, ''RE''::bpchar])) AND hdr.isactive = ''N''::bpchar)) c
        GROUP BY c.c_invoice_id) cobros ON cobros.c_invoice_id = i.c_invoice_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE ' || whereClauseStd || whereDateInvoices || whereUserInvoices || wherePOSInvoices ||
  ' AND (cobros.amount IS NULL OR i.grandtotal <> cobros.amount) AND i.initialcurrentaccountamt > 0::numeric AND (dt.docbasetype <> ALL (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND "position"(dt.doctypekey::text, ''CDN''::text) < 1 AND (i.docstatus = ANY (ARRAY[''VO''::bpchar, ''RE''::bpchar]));';

-- raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION v_dailysales_current_account_filtered(integer, integer, integer, date, date, date, date, boolean)
  OWNER TO libertya;

--funview v_dailysales_current_account_payments_filtered
CREATE OR REPLACE FUNCTION v_dailysales_current_account_payments_filtered(
    orgid integer,
    posid integer,
    userid integer,
    datefrom date,
    dateto date)
  RETURNS SETOF v_dailysales_type AS
$BODY$
declare
	consulta varchar;
	whereDatePayments varchar;
	wherePOSPayments varchar;
	whereUserPayments varchar;
	whereOrg varchar;
	whereClauseStd varchar;
	posJournalPaymentsFrom varchar;
	dateFromPOSJournalPayments varchar;
	dateToPOSJournalPayments varchar;
	orgIDPOSJournalPayments integer;
	adocument v_dailysales_type;
BEGIN
	-- Armado de las condiciones en base a los parámetros
	-- Organización
	whereOrg = '';
	if orgID is not null AND orgID > 0 THEN
		whereOrg = ' AND i.ad_org_id = ' || orgID;
	END IF;

	-- Fechas para allocations y facturas
	whereDatePayments = '';
	if dateFrom is not null then
		whereDatePayments = ' AND date_trunc(''day'', pjp.allocationdate) >= date_trunc(''day'', ''' || dateFrom || '''::date)';
	end if;

	if dateTo is not null then
		whereDatePayments = whereDatePayments || ' AND date_trunc(''day'', pjp.allocationdate) <= date_trunc(''day'', ''' || dateTo || '''::date) ';
	end if;
	
	-- TPV
	wherePOSPayments = ' AND (' || posID || ' = -1 OR COALESCE(pjh.c_pos_id, pj.c_pos_id) = ' || posID || ')';

	-- Usuario
	whereUserPayments = ' AND (' || userID || ' = -1 OR COALESCE(pjh.ad_user_id, pj.ad_user_id) = ' || userID || ')';

	-- Condiciones básicas del reporte
	whereClauseStd = ' ( i.issotrx = ''Y'' ' ||
			 whereOrg || 
			 ' AND (i.docstatus = ''CO'' or i.docstatus = ''CL'' or i.docstatus = ''RE'' or i.docstatus = ''VO'' OR i.docstatus = ''??'') ' ||
			 ' AND dtc.isfiscaldocument = ''Y'' ' || 
			 ' AND (dtc.isfiscal is null OR dtc.isfiscal = ''N'' OR (dtc.isfiscal = ''Y'' AND i.fiscalalreadyprinted = ''Y'')) ' ||
			 ' AND dtc.doctypekey not in (''RTR'', ''RTI'', ''RCR'', ''RCI'') ' || 
			 ' AND (dtc.transactiontypefrontliva is null OR dtc.transactiontypefrontliva = ''S'') ' || 
			 ' ) ';

	-- Armado del llamado a la función que ejecuta la vista filtrada c_posjournalpayments_v
	dateFromPOSJournalPayments = (CASE WHEN dateFrom is null THEN 'null::date' ELSE '''' || dateFrom || '''::date' END);
	dateToPOSJournalPayments = (CASE WHEN dateTo is null THEN 'null::date' ELSE '''' || dateTo || '''::date' END);
	orgIDPOSJournalPayments = (CASE WHEN orgID is null THEN -1 ELSE orgID END);
	posJournalPaymentsFrom = 'c_posjournalpayments_v_filtered(' || orgIDPOSJournalPayments || ', ' || dateFromPOSJournalPayments || ', ' || dateToPOSJournalPayments || ')';

	-- Armar la consulta
	consulta = 'SELECT ''PCA''::character varying AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
        CASE
            WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
            ELSE i.c_invoice_id
        END AS c_invoice_id, pjp.allocationdate AS datetrx, pjp.c_payment_id, pjp.c_cashline_id, 
        CASE
            WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN i.c_invoice_id
            ELSE pjp.c_invoice_credit_id
        END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, currencybase(pjp.amount, i.c_currency_id, pjp.dateacct, pjp.ad_client_id, pjp.ad_org_id)::numeric(20,2) as amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
        CASE
            WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.c_doctype_id
            WHEN (dtc.docbasetype <> ALL (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.c_doctype_id
            WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.c_pospaymentmedium_id
            ELSE p.c_pospaymentmedium_id
        END AS c_pospaymentmedium_id, 
        CASE
            WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.docbasetype::character varying
            WHEN (dtc.docbasetype <> ALL (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.docbasetype::character varying
            WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.name
            ELSE ppm.name
        END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, ef.value AS entidadfinancieravalue, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
   FROM ' || posJournalPaymentsFrom || ' pjp
   JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
   LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_posjournal pjh ON pjh.c_posjournal_id = hdr.c_posjournal_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
   LEFT JOIN c_doctype dt ON cc.c_doctypetarget_id = dt.c_doctype_id
  WHERE ' || whereClauseStd || whereDatePayments || whereUserPayments || wherePOSPayments ||
  ' AND date_trunc(''day''::text, i.dateacct) <> date_trunc(''day''::text, pjp.allocationdateacct::timestamp with time zone) AND i.initialcurrentaccountamt > 0::numeric AND hdr.isactive = ''Y''::bpchar AND ((dtc.docbasetype <> ALL (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) OR (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL);';

-- raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION v_dailysales_current_account_payments_filtered(integer, integer, integer, date, date)
  OWNER TO libertya;

--funview v_dailysales_invoices_filtered
CREATE OR REPLACE FUNCTION v_dailysales_invoices_filtered(
    orgid integer,
    posid integer,
    userid integer,
    datefrom date,
    dateto date,
    invoicedatefrom date,
    invoicedateto date,
    addinvoicedate boolean)
  RETURNS SETOF v_dailysales_type AS
$BODY$
declare
	consulta varchar;
	whereDateInvoices varchar;
	whereInvoiceDate varchar;
	wherePOSInvoices varchar;
	whereUserInvoices varchar;
	whereOrg varchar;
	whereClauseStd varchar;
	adocument v_dailysales_type;
BEGIN
	-- Armado de las condiciones en base a los parámetros
	-- Organización
	whereOrg = '';
	if orgID is not null AND orgID > 0 THEN
		whereOrg = ' AND i.ad_org_id = ' || orgID;
	END IF;
	
	-- Fecha de factura
	whereInvoiceDate = '';
	if addInvoiceDate then
		if invoiceDateFrom is not null then
			whereInvoiceDate = ' AND date_trunc(''day'', i.dateacct) >= date_trunc(''day'', '''|| invoiceDateFrom || '''::date)';
		end if;
		if invoiceDateTo is not null then
			whereInvoiceDate = whereInvoiceDate || ' AND date_trunc(''day'', i.dateacct) <= date_trunc(''day'', ''' || invoiceDateTo || '''::date) ';
		end if;
	end if;

	-- Fechas para allocations y facturas
	whereDateInvoices = '';
	if dateFrom is not null then
		whereDateInvoices = ' AND date_trunc(''day''::text, i.dateinvoiced) >= date_trunc(''day'', ''' || dateFrom || '''::date)';
	end if;

	if dateTo is not null then
		whereDateInvoices = whereDateInvoices || ' AND date_trunc(''day''::text, i.dateinvoiced) <= date_trunc(''day'', ''' || dateTo || '''::date) ';
	end if;
	
	-- TPV
	wherePOSInvoices = ' AND (' || posID || ' = -1 OR pj.c_pos_id = ' || posID || ')';

	-- Usuario
	whereUserInvoices = ' AND (' || userID || ' = -1 OR pj.ad_user_id = ' || userID || ')';

	-- Condiciones básicas del reporte
	whereClauseStd = ' ( i.issotrx = ''Y'' ' ||
			 whereOrg || 
			 ' AND (i.docstatus = ''CO'' or i.docstatus = ''CL'' or i.docstatus = ''RE'' or i.docstatus = ''VO'' OR i.docstatus = ''??'') ' ||
			 ' AND dtc.isfiscaldocument = ''Y'' ' || 
			 ' AND (dtc.isfiscal is null OR dtc.isfiscal = ''N'' OR (dtc.isfiscal = ''Y'' AND i.fiscalalreadyprinted = ''Y'')) ' ||
			 ' AND dtc.doctypekey not in (''RTR'', ''RTI'', ''RCR'', ''RCI'') ' || 
			 ' AND (dtc.transactiontypefrontliva is null OR dtc.transactiontypefrontliva = ''S'') ' || 
			 ' ) ';

	-- Agregar las condiciones anteriores
	whereClauseStd = whereClauseStd || whereInvoiceDate || whereDateInvoices || wherePOSInvoices || whereUserInvoices;

	-- Armar la consulta
	consulta = 'SELECT ''I''::character varying AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc(''day''::text, i.dateinvoiced) AS datetrx, NULL::integer AS c_payment_id, NULL::integer AS c_cashline_id, NULL::integer AS c_invoice_credit_id, dtc.docbasetype AS tendertype, i.documentno, i.description, NULL::unknown AS info, currencybase(i.grandtotal, i.c_currency_id, i.dateacct, i.ad_client_id, i.ad_org_id)::numeric(20,2) * dtc.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, dtc.c_doctype_id AS c_pospaymentmedium_id, dtc.name AS pospaymentmediumname, NULL::integer AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::integer AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
   FROM c_invoice i
   LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE ' || whereClauseStd ||
  ' AND NOT (
  		EXISTS (
			SELECT * FROM (
				SELECT *
				FROM c_allocationline al
				WHERE i.c_invoice_id = al.c_invoice_id AND i.isvoidable = ''Y''::bpchar 
			) as FOO
			JOIN c_payment p ON p.c_payment_id = foo.c_payment_id
			JOIN c_cashline cl ON cl.c_payment_id = p.c_payment_id
		)
	);';

-- raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION v_dailysales_invoices_filtered(integer, integer, integer, date, date, date, date, boolean)
  OWNER TO libertya;

--funview v_dailysales_v2_filtered
CREATE OR REPLACE FUNCTION v_dailysales_v2_filtered(
    orgid integer,
    posid integer,
    userid integer,
    datefrom date,
    dateto date,
    invoicedatefrom date,
    invoicedateto date,
    addinvoicedate boolean)
  RETURNS SETOF v_dailysales_type AS
$BODY$
declare
	consulta varchar;
	whereDateInvoices varchar;
	whereDatePayments varchar;
	whereInvoiceDate varchar;
	wherePOSInvoices varchar;
	wherePOSPayments varchar;
	whereUserInvoices varchar;
	whereUserPayments varchar;
	whereOrg varchar;
	whereClauseStd varchar;
	posJournalPaymentsFrom varchar;
	dateFromPOSJournalPayments varchar;
	dateToPOSJournalPayments varchar;
	orgIDPOSJournalPayments integer;
	adocument v_dailysales_type;
BEGIN
	-- Armado de las condiciones en base a los parámetros
	-- Organización
	whereOrg = '';
	if orgID is not null AND orgID > 0 THEN
		whereOrg = ' AND i.ad_org_id = ' || orgID;
	END IF;
	
	-- Fecha de factura
	whereInvoiceDate = '';
	if addInvoiceDate then
		if invoiceDateFrom is not null then
			whereInvoiceDate = ' AND date_trunc(''day'', i.dateacct) >= date_trunc(''day'', '''|| invoiceDateFrom || '''::date)';
		end if;
		if invoiceDateTo is not null then
			whereInvoiceDate = whereInvoiceDate || ' AND date_trunc(''day'', i.dateacct) <= date_trunc(''day'', ''' || invoiceDateTo || '''::date) ';
		end if;
	end if;

	-- Fechas para allocations y facturas
	whereDatePayments = '';
	whereDateInvoices = '';
	if dateFrom is not null then
		whereDatePayments = ' AND date_trunc(''day'', pjp.allocationdate) >= date_trunc(''day'', ''' || dateFrom || '''::date)';
		whereDateInvoices = ' AND date_trunc(''day''::text, i.dateinvoiced) >= date_trunc(''day'', ''' || dateFrom || '''::date)';
	end if;

	if dateTo is not null then
		whereDatePayments = whereDatePayments || ' AND date_trunc(''day'', pjp.allocationdate) <= date_trunc(''day'', ''' || dateTo || '''::date) ';
		whereDateInvoices = whereDateInvoices || ' AND date_trunc(''day''::text, i.dateinvoiced) <= date_trunc(''day'', ''' || dateTo || '''::date) ';
	end if;
	
	-- TPV
	wherePOSPayments = ' AND (' || posID || ' = -1 OR COALESCE(pjh.c_pos_id, pj.c_pos_id) = ' || posID || ')';
	wherePOSInvoices = ' AND (' || posID || ' = -1 OR pj.c_pos_id = ' || posID || ')';

	-- Usuario
	whereUserPayments = ' AND (' || userID || ' = -1 OR COALESCE(pjh.ad_user_id, pj.ad_user_id) = ' || userID || ')';
	whereUserInvoices = ' AND (' || userID || ' = -1 OR pj.ad_user_id = ' || userID || ')';

	-- Condiciones básicas del reporte
	whereClauseStd = ' ( i.issotrx = ''Y'' ' ||
			 whereOrg || 
			 ' AND (i.docstatus = ''CO'' or i.docstatus = ''CL'' or i.docstatus = ''RE'' or i.docstatus = ''VO'' OR i.docstatus = ''??'') ' ||
			 ' AND dtc.isfiscaldocument = ''Y'' ' || 
			 ' AND (dtc.isfiscal is null OR dtc.isfiscal = ''N'' OR (dtc.isfiscal = ''Y'' AND i.fiscalalreadyprinted = ''Y'')) ' ||
			 ' AND dtc.doctypekey not in (''RTR'', ''RTI'', ''RCR'', ''RCI'') ' || 
			 ' AND (dtc.transactiontypefrontliva is null OR dtc.transactiontypefrontliva = ''S'') ' || 
			 ' ) ';

	-- Agregar las condiciones anteriores
	whereClauseStd = whereClauseStd || whereInvoiceDate;

	-- Armado del llamado a la función que ejecuta la vista filtrada c_posjournalpayments_v
	dateFromPOSJournalPayments = (CASE WHEN dateFrom is null THEN 'null::date' ELSE '''' || dateFrom || '''::date' END);
	dateToPOSJournalPayments = (CASE WHEN dateTo is null THEN 'null::date' ELSE '''' || dateTo || '''::date' END);
	orgIDPOSJournalPayments = (CASE WHEN orgID is null THEN -1 ELSE orgID END);
	posJournalPaymentsFrom = 'c_posjournalpayments_v_filtered(' || orgIDPOSJournalPayments || ', ' || dateFromPOSJournalPayments || ', ' || dateToPOSJournalPayments || ')';

	-- Armar la consulta
	consulta = '(        (         SELECT ''P''::character varying AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                                    ELSE i.c_invoice_id
                                END AS c_invoice_id, pjp.allocationdate AS datetrx, pjp.c_payment_id, pjp.c_cashline_id, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN i.c_invoice_id
                                    ELSE pjp.c_invoice_credit_id
                                END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, currencybase(pjp.amount, i.c_currency_id, pjp.dateacct, pjp.ad_client_id, pjp.ad_org_id)::numeric(20,2) as amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.c_doctype_id
                                    WHEN (dtc.docbasetype <> ALL (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.c_doctype_id
                                    WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.c_pospaymentmedium_id
                                    ELSE p.c_pospaymentmedium_id
                                END AS c_pospaymentmedium_id, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.docbasetype::character varying
                                    WHEN (dtc.docbasetype <> ALL (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.docbasetype::character varying
                                    WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.name
                                    ELSE ppm.name
                                END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, ef.value AS entidadfinancieravalue, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
                           FROM ' || posJournalPaymentsFrom || ' pjp
                      JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
                 LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
            JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
       JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_posjournal pjh ON pjh.c_posjournal_id = hdr.c_posjournal_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
   LEFT JOIN c_doctype dt ON cc.c_doctypetarget_id = dt.c_doctype_id
  WHERE ' || whereClauseStd || whereDatePayments || whereUserPayments || wherePOSPayments ||
  ' AND (date_trunc(''day''::text, i.dateacct) = date_trunc(''day''::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0::numeric) AND ((dtc.docbasetype <> ALL (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) OR (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL) AND (pjp.c_invoice_credit_id IS NULL OR pjp.c_invoice_credit_id IS NOT NULL AND (cc.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar]))) AND NOT (EXISTS ( SELECT c2.c_payment_id
   FROM c_cashline c2
  WHERE c2.c_payment_id = pjp.c_payment_id AND i.isvoidable = ''Y''::bpchar))
                UNION ALL 
                         SELECT ''NCC''::character varying AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc(''day''::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 
                                CASE
                                    WHEN i.paymentrule::text = ''T''::text OR i.paymentrule::text = ''Tr''::text THEN ''A''::character varying
                                    WHEN i.paymentrule::text = ''B''::text THEN ''CA''::character varying
                                    WHEN i.paymentrule::text = ''K''::text THEN ''C''::character varying
                                    WHEN i.paymentrule::text = ''P''::text THEN ''CC''::character varying
                                    WHEN i.paymentrule::text = ''S''::text THEN ''K''::character varying
                                    ELSE i.paymentrule
                                END AS tendertype, i.documentno, i.description, NULL::unknown AS info, currencybase(i.grandtotal, i.c_currency_id, i.dateacct, i.ad_client_id, i.ad_org_id)::numeric(20,2) * dtc.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
                                CASE
                                    WHEN i.paymentrule::text = ''P''::text THEN NULL::integer
                                    ELSE ( SELECT ad_ref_list.ad_ref_list_id
                                       FROM ad_ref_list
                                      WHERE ad_ref_list.ad_reference_id = 195 AND ad_ref_list.value::text = i.paymentrule::text
                                     LIMIT 1)
                                END AS c_pospaymentmedium_id, 
                                CASE
                                    WHEN i.paymentrule::text = ''T''::text OR i.paymentrule::text = ''Tr''::text THEN ''A''::character varying
                                    WHEN i.paymentrule::text = ''B''::text THEN ''CA''::character varying
                                    WHEN i.paymentrule::text = ''K''::text THEN ''C''::character varying
                                    WHEN i.paymentrule::text = ''P''::text THEN NULL::character varying
                                    WHEN i.paymentrule::text = ''S''::text THEN ''K''::character varying
                                    ELSE i.paymentrule
                                END AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
                           FROM c_invoice i
                      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
                 JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
            JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
       JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE ' || whereClauseStd || whereDateInvoices || whereUserInvoices || wherePOSInvoices ||
  ' AND dtc.docbasetype = ''ARC''::bpchar AND ((i.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar])) OR (i.docstatus = ANY (ARRAY[''VO''::bpchar, ''RE''::bpchar])) AND (EXISTS ( SELECT al.c_allocationline_id
          FROM c_allocationline al
         WHERE al.c_invoice_id = i.c_invoice_id))) AND NOT (EXISTS ( SELECT al.c_allocationline_id
          FROM c_allocationline al
         WHERE al.c_invoice_credit_id = i.c_invoice_id)))
        UNION ALL 
                 SELECT ''PA''::character varying AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
                        CASE
                            WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                            ELSE i.c_invoice_id
                        END AS c_invoice_id, pjp.allocationdate AS datetrx, pjp.c_payment_id, pjp.c_cashline_id, 
                        CASE
                            WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN i.c_invoice_id
                            ELSE pjp.c_invoice_credit_id
                        END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, currencybase(pjp.amount, i.c_currency_id, pjp.dateacct, pjp.ad_client_id, pjp.ad_org_id)::numeric(20,2) * (-1)::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
                        CASE
                            WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.c_doctype_id
                            WHEN (dtc.docbasetype <> ALL (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.c_doctype_id
                            WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.c_pospaymentmedium_id
                            ELSE p.c_pospaymentmedium_id
                        END AS c_pospaymentmedium_id, 
                        CASE
                            WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.docbasetype::character varying
                            WHEN (dtc.docbasetype <> ALL (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.docbasetype::character varying
                            WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.name
                            ELSE ppm.name
                        END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, ef.value AS entidadfinancieravalue, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
                   FROM ' || posJournalPaymentsFrom || ' pjp
              JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
         LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
    JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_posjournal pjh ON pjh.c_posjournal_id = hdr.c_posjournal_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
   LEFT JOIN c_doctype dt ON cc.c_doctypetarget_id = dt.c_doctype_id
  WHERE ' || whereClauseStd || whereDatePayments || whereUserPayments || wherePOSPayments ||
  ' AND (date_trunc(''day''::text, i.dateacct) = date_trunc(''day''::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0::numeric) AND hdr.isactive = ''N''::bpchar AND NOT (EXISTS ( SELECT c2.c_payment_id
   FROM c_cashline c2
  WHERE c2.c_payment_id = pjp.c_payment_id AND i.isvoidable = ''Y''::bpchar)))
UNION ALL 
         SELECT ''ND''::character varying AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc(''day''::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 
                CASE
                    WHEN i.paymentrule::text = ''T''::text OR i.paymentrule::text = ''Tr''::text THEN ''A''::character varying
                    WHEN i.paymentrule::text = ''B''::text THEN ''CA''::character varying
                    WHEN i.paymentrule::text = ''K''::text THEN ''C''::character varying
                    WHEN i.paymentrule::text = ''P''::text THEN ''CC''::character varying
                    WHEN i.paymentrule::text = ''S''::text THEN ''K''::character varying
                    ELSE i.paymentrule
                END AS tendertype, i.documentno, i.description, NULL::unknown AS info, currencybase(i.grandtotal, i.c_currency_id, i.dateacct, i.ad_client_id, i.ad_org_id)::numeric(20,2) * dtc.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, ( SELECT ad_ref_list.ad_ref_list_id
                   FROM ad_ref_list
                  WHERE ad_ref_list.ad_reference_id = 195 AND ad_ref_list.value::text = i.paymentrule::text
                 LIMIT 1) AS c_pospaymentmedium_id, 
                CASE
                    WHEN i.paymentrule::text = ''T''::text OR i.paymentrule::text = ''Tr''::text THEN ''A''::character varying
                    WHEN i.paymentrule::text = ''B''::text THEN ''CA''::character varying
                    WHEN i.paymentrule::text = ''K''::text THEN ''C''::character varying
                    WHEN i.paymentrule::text = ''P''::text THEN ''CC''::character varying
                    WHEN i.paymentrule::text = ''S''::text THEN ''K''::character varying
                    ELSE i.paymentrule
                END AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
           FROM c_invoice i
      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE ' || whereClauseStd || whereDateInvoices || whereUserInvoices || wherePOSInvoices ||
  ' AND "position"(dtc.doctypekey::text, ''CDN''::text) = 1 AND ((i.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar])) OR (i.docstatus = ANY (ARRAY[''VO''::bpchar, ''RE''::bpchar])) AND (EXISTS ( SELECT al.c_allocationline_id
   FROM c_allocationline al
  WHERE al.c_invoice_credit_id = i.c_invoice_id))) AND NOT (EXISTS ( SELECT al.c_allocationline_id
   FROM c_allocationline al
  WHERE al.c_invoice_id = i.c_invoice_id));';

--raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION v_dailysales_v2_filtered(integer, integer, integer, date, date, date, date, boolean)
  OWNER TO libertya;
  
--20180112-1428 Mejoras al informe de Declaración de Valores incorporando currencyconvert
--Eliminación de dependencias
DROP VIEW c_pos_declaracionvalores_cash;
DROP VIEW c_pos_declaracionvalores_credit;
DROP VIEW c_pos_declaracionvalores_payments;
DROP VIEW c_pos_declaracionvalores_ventas;
DROP VIEW c_pos_declaracionvalores_voided;

DROP FUNCTION c_pos_declaracionvalores_cash_filtered(anyarray);
DROP FUNCTION c_pos_declaracionvalores_credit_filtered(anyarray);
DROP FUNCTION c_pos_declaracionvalores_payments_filtered(anyarray);
DROP FUNCTION c_pos_declaracionvalores_ventas_filtered(anyarray);
DROP FUNCTION c_pos_declaracionvalores_voided_filtered(anyarray);

DROP VIEW c_posjournal_c_cash_v;
DROP VIEW c_posjournal_c_invoice_credit_v;
DROP VIEW c_posjournal_c_payment_v;

drop function c_posjournal_c_cash_v_filtered(anyarray);
drop function c_posjournal_c_invoice_credit_v_filtered(anyarray);
drop function c_posjournal_c_payment_v_filtered(anyarray);

DROP TYPE c_posjournal_v_type;
DROP TYPE c_posjournal_c_payment_v_type;

--Mejora tipos de datos c_posjournal_c_payment_v_type y c_posjournal_v_type 
CREATE TYPE c_posjournal_c_payment_v_type AS (c_allocationhdr_id integer, c_allocationline_id integer, ad_client_id integer, ad_org_id integer, 
					isactive character(1), created timestamp without time zone, createdby integer, updated timestamp without time zone, 
					updatedby integer, c_invoice_id integer, c_payment_id integer, c_cashline_id integer, 
					c_invoice_credit_id integer, tendertype character varying, documentno character varying, 
					description character varying(255), info character varying, amount numeric(20,2), c_cash_id integer, 
					line numeric(18,0), c_doctype_id integer, checkno character varying, a_bank character varying, 
					transferno character varying, creditcardtype character(1), m_entidadfinancieraplan_id integer, 
					m_entidadfinanciera_id integer,  couponnumber character varying, allocationdate timestamp without time zone, 
					docstatus character(2), dateacct date, invoice_documentno character varying(30), invoice_grandtotal numeric(20,2), 
					entidadfinanciera_value character varying, entidadfinanciera_name character varying, 
					bp_entidadfinanciera_value character varying, bp_entidadfinanciera_name character varying, 
					cupon character varying, creditcard character varying, isfiscaldocument character(1), 
					isfiscal character(1), fiscalalreadyprinted character(1), changeamt numeric, c_currency_id integer);

CREATE TYPE c_posjournal_v_type AS (c_allocationhdr_id integer, c_allocationline_id integer, ad_client_id integer, ad_org_id integer, 
					isactive character(1), created timestamp without time zone, createdby integer, updated timestamp without time zone, 
					updatedby integer, c_invoice_id integer, c_payment_id integer, c_cashline_id integer, 
					c_invoice_credit_id integer, tendertype character varying, documentno character varying, 
					description character varying(255), info character varying, amount numeric(20,2), c_cash_id integer, 
					line numeric(18,0), c_doctype_id integer, checkno character varying, a_bank character varying, 
					transferno character varying, creditcardtype character(1), m_entidadfinancieraplan_id integer, 
					m_entidadfinanciera_id integer,  couponnumber character varying, allocationdate timestamp without time zone, 
					docstatus character(2), dateacct date, invoice_documentno character varying(30), invoice_grandtotal numeric(20,2), 
					entidadfinanciera_value character varying, entidadfinanciera_name character varying, 
					bp_entidadfinanciera_value character varying, bp_entidadfinanciera_name character varying, 
					cupon character varying, creditcard character varying, isfiscaldocument character(1), 
					isfiscal character(1), fiscalalreadyprinted character(1), c_currency_id integer);

--funview c_posjournal_c_cash_v_filtered
CREATE OR REPLACE FUNCTION c_posjournal_c_cash_v_filtered(posjournalids anyarray)
  RETURNS SETOF c_posjournal_v_type AS
$BODY$
declare
	consulta varchar;
	wherePosJournalIDs varchar;
	existsPosJournalIDs boolean;
	adocument c_posjournal_v_type;
BEGIN
	-- Armado de condiciones de filtro parámetro
	existsPosJournalIDs = posjournalIDs is not null and array_length(posjournalIDs,1) > 0 and posjournalIDs[1] <> -1;

	-- Filtro de cajas diarias
	wherePosJournalIDs = '';
	if existsPosJournalIDs then
		wherePosJournalIDs = ' AND c.c_posjournal_id IN ('|| array_to_string(posjournalIDs, ',') || ')';
	end if;
	
	consulta =  
	'SELECT al.c_allocationhdr_id, al.c_allocationline_id, al.ad_client_id, al.ad_org_id, al.isactive, al.created, al.createdby, al.updated, al.updatedby, al.c_invoice_id, al.c_payment_id, al.c_cashline_id, al.c_invoice_credit_id, ''CA''::character varying AS tendertype, NULL::character varying AS documentno, cl.description, ((c.name::text || ''_''::text) || cl.line::text)::character varying AS info, COALESCE(currencyconvert((case when ah.allocationtype in (''OPA'',''RCA'') then cl.amount else al.amount + al.discountamt + al.writeoffamt end), (case when ah.allocationtype in (''OPA'',''RCA'') then cl.c_currency_id else ah.c_currency_id end), ah.c_currency_id, ah.dateacct, null::integer, ah.ad_client_id, ah.ad_org_id), 0::numeric)::numeric(20,2) AS amount, cl.c_cash_id, cl.line, NULL::integer AS c_doctype_id, NULL::character varying AS checkno, NULL::character varying AS a_bank, NULL::character varying AS transferno, NULL::character(1) AS creditcardtype, NULL::integer AS m_entidadfinancieraplan_id, NULL::integer AS m_entidadfinanciera_id, NULL::character varying AS couponnumber, date_trunc(''day''::text, ah.datetrx) AS allocationdate, cl.docstatus, c.dateacct::date AS dateacct, (case when ah.allocationtype in (''OPA'',''RCA'') then ''Anticipo'' else i.documentno end) AS invoice_documentno, i.grandtotal AS invoice_grandtotal, NULL::character varying AS entidadfinanciera_value, NULL::character varying AS entidadfinanciera_name, NULL::character varying AS bp_entidadfinanciera_value, NULL::character varying AS bp_entidadfinanciera_name, NULL::character varying AS cupon, NULL::character varying AS creditcard, NULL::bpchar AS isfiscaldocument, NULL::bpchar AS isfiscal, NULL::bpchar AS fiscalalreadyprinted, ah.c_currency_id
           FROM c_allocationline al
      JOIN c_allocationhdr ah ON al.c_allocationhdr_id = ah.c_allocationhdr_id
   JOIN c_cashline cl ON al.c_cashline_id = cl.c_cashline_id
   JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
   LEFT JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
   WHERE 1=1 ' || wherePosJournalIDs || ' AND NOT EXISTS (SELECT ala.c_cashline_id FROM c_allocationline as ala INNER JOIN c_allocationhdr as aha on aha.c_allocationhdr_id = ala.c_allocationhdr_id WHERE ala.c_cashline_id = cl.c_cashline_id AND aha.allocationtype in (''OPA'',''RCA'') AND aha.c_allocationhdr_id <> ah.c_allocationhdr_id) ' ||
   ' UNION ALL 
         SELECT NULL::integer AS c_allocationhdr_id, NULL::integer AS c_allocationline_id, cl.ad_client_id, cl.ad_org_id, cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby, NULL::integer AS c_invoice_id, NULL::integer AS c_payment_id, cl.c_cashline_id, NULL::integer AS c_invoice_credit_id, ''CA''::character varying(2) AS tendertype, NULL::character varying(30) AS documentno, cl.description, (((c.name::text || ''_''::text) || cl.line::text))::character varying(255) AS info, cl.amount, cl.c_cash_id, cl.line, NULL::integer AS c_doctype_id, NULL::character varying(20) AS checkno, NULL::character varying(255) AS a_bank, NULL::character varying(20) AS transferno, NULL::character(1) AS creditcardtype, NULL::integer AS m_entidadfinancieraplan_id, NULL::integer AS m_entidadfinanciera_id, NULL::character varying(30) AS couponnumber, date_trunc(''day''::text, c.statementdate) AS allocationdate, cl.docstatus, c.dateacct::date AS dateacct, NULL::character varying(30) AS invoice_documentno, NULL::numeric(20,2) AS invoice_grandtotal, NULL::character varying AS entidadfinanciera_value, NULL::character varying AS entidadfinanciera_name, NULL::character varying AS bp_entidadfinanciera_value, NULL::character varying AS bp_entidadfinanciera_name, NULL::character varying AS cupon, NULL::character varying AS creditcard, NULL::bpchar AS isfiscaldocument, NULL::bpchar AS isfiscal, NULL::bpchar AS fiscalalreadyprinted, cl.c_currency_id
           FROM c_cashline cl
      JOIN c_cash c ON c.c_cash_id = cl.c_cash_id
     WHERE 1=1 '  || wherePosJournalIDs || 
     ' AND NOT (EXISTS ( SELECT al.c_allocationline_id
              FROM c_allocationline al
             WHERE al.c_cashline_id = cl.c_cashline_id));';

	--raise notice '%', consulta;
	FOR adocument IN EXECUTE consulta LOOP
		return next adocument;
	END LOOP;
END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION c_posjournal_c_cash_v_filtered(anyarray)
  OWNER TO libertya;

--funview c_posjournal_c_payment_v_filtered
CREATE OR REPLACE FUNCTION c_posjournal_c_payment_v_filtered(posjournalids anyarray)
  RETURNS SETOF c_posjournal_c_payment_v_type AS
$BODY$
declare
	consulta varchar;
	wherePosJournalIDs varchar;
	existsPosJournalIDs boolean;
	adocument c_posjournal_c_payment_v_type;
BEGIN
	-- Armado de condiciones de filtro parámetro
	existsPosJournalIDs = posjournalIDs is not null and array_length(posjournalIDs,1) > 0 and posjournalIDs[1] <> -1;

	-- Filtro de cajas diarias
	wherePosJournalIDs = '';
	if existsPosJournalIDs then
		wherePosJournalIDs = ' AND p.c_posjournal_id IN ('|| array_to_string(posjournalIDs, ',') || ')';
	end if;
	
	consulta =  
	'SELECT al.c_allocationhdr_id, al.c_allocationline_id, al.ad_client_id, al.ad_org_id, al.isactive, al.created, al.createdby, al.updated, al.updatedby, al.c_invoice_id, al.c_payment_id, al.c_cashline_id, al.c_invoice_credit_id, p.tendertype::character varying AS tendertype, p.documentno, p.description, ((p.documentno::text || ''_''::text) || to_char(p.datetrx, ''DD/MM/YYYY''::text))::character varying AS info, COALESCE(currencyconvert((case when ah.allocationtype in (''OPA'',''RCA'') then p.payamt else al.amount + al.discountamt + al.writeoffamt end), (case when ah.allocationtype in (''OPA'',''RCA'') then p.c_currency_id else ah.c_currency_id end), ah.c_currency_id, ah.dateacct, null::integer, ah.ad_client_id, ah.ad_org_id), 0::numeric)::numeric(20,2) AS amount, NULL::integer AS c_cash_id, NULL::numeric(18,0) AS line, NULL::integer AS c_doctype_id, p.checkno, p.a_bank, p.checkno AS transferno, p.creditcardtype, p.m_entidadfinancieraplan_id, ep.m_entidadfinanciera_id, p.couponnumber, date_trunc(''day''::text, ah.datetrx) AS allocationdate, p.docstatus, p.dateacct::date AS dateacct, (case when ah.allocationtype in (''OPA'',''RCA'') then ''Anticipo'' else i.documentno end) AS invoice_documentno, i.grandtotal AS invoice_grandtotal, ef.value AS entidadfinanciera_value, ef.name AS entidadfinanciera_name, bp.value AS bp_entidadfinanciera_value, bp.name AS bp_entidadfinanciera_name, p.couponnumber AS cupon, p.creditcardnumber AS creditcard, NULL::bpchar AS isfiscaldocument, NULL::bpchar AS isfiscal, NULL::bpchar AS fiscalalreadyprinted, al.changeamt, ah.c_currency_id
           FROM c_allocationline al
      JOIN c_allocationhdr ah ON al.c_allocationhdr_id = ah.c_allocationhdr_id
   LEFT JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
   JOIN c_payment p ON al.c_payment_id = p.c_payment_id
   LEFT JOIN m_entidadfinancieraplan ep ON p.m_entidadfinancieraplan_id = ep.m_entidadfinancieraplan_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = ep.m_entidadfinanciera_id
   LEFT JOIN c_bpartner bp ON bp.c_bpartner_id = ef.c_bpartner_id
   WHERE 1=1 ' || wherePosJournalIDs || ' AND NOT EXISTS (SELECT ala.c_payment_id FROM c_allocationline as ala INNER JOIN c_allocationhdr as aha on aha.c_allocationhdr_id = ala.c_allocationhdr_id WHERE ala.c_payment_id = p.c_payment_id AND aha.allocationtype in (''OPA'',''RCA'') AND aha.c_allocationhdr_id <> ah.c_allocationhdr_id) ' || 
   ' UNION ALL 
        ( SELECT NULL::integer AS c_allocationhdr_id, NULL::integer AS c_allocationline_id, p.ad_client_id, p.ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, NULL::integer AS c_invoice_id, p.c_payment_id, NULL::integer AS c_cashline_id, NULL::integer AS c_invoice_credit_id, p.tendertype::character varying(2) AS tendertype, p.documentno, p.description, (((p.documentno::text || ''_''::text) || to_char(p.datetrx, ''DD/MM/YYYY''::text)))::character varying(255) AS info, p.payamt AS amount, NULL::integer AS c_cash_id, NULL::numeric(18,0) AS line, NULL::integer AS c_doctype_id, p.checkno, p.a_bank, p.checkno AS transferno, p.creditcardtype, p.m_entidadfinancieraplan_id, ep.m_entidadfinanciera_id, p.couponnumber, date_trunc(''day''::text, p.datetrx) AS allocationdate, p.docstatus, p.dateacct::date AS dateacct, NULL::character varying(30) AS invoice_documentno, NULL::numeric(20,2) AS invoice_grandtotal, ef.value AS entidadfinanciera_value, ef.name AS entidadfinanciera_name, bp.value AS bp_entidadfinanciera_value, bp.name AS bp_entidadfinanciera_name, p.couponnumber AS cupon, p.creditcardnumber AS creditcard, NULL::bpchar AS isfiscaldocument, NULL::bpchar AS isfiscal, NULL::bpchar AS fiscalalreadyprinted, 0 AS changeamt, p.c_currency_id
           FROM c_payment p
      LEFT JOIN m_entidadfinancieraplan ep ON p.m_entidadfinancieraplan_id = ep.m_entidadfinancieraplan_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = ep.m_entidadfinanciera_id
   LEFT JOIN c_bpartner bp ON bp.c_bpartner_id = ef.c_bpartner_id
  WHERE 1=1 ' || wherePosJournalIDs || 
  ' AND NOT (EXISTS ( SELECT al.c_allocationline_id
    FROM c_allocationline al
   WHERE al.c_payment_id = p.c_payment_id))
  ORDER BY p.tendertype::character varying(2), p.documentno);';

	--raise notice '%', consulta;
	FOR adocument IN EXECUTE consulta LOOP
		return next adocument;
	END LOOP;
END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION c_posjournal_c_payment_v_filtered(anyarray)
  OWNER TO libertya;

--funview c_posjournal_c_invoice_credit_v_filtered
CREATE OR REPLACE FUNCTION c_posjournal_c_invoice_credit_v_filtered(posjournalids anyarray)
  RETURNS SETOF c_posjournal_v_type AS
$BODY$
declare
	consulta varchar;
	wherePosJournalIDs varchar;
	existsPosJournalIDs boolean;
	adocument c_posjournal_v_type;
BEGIN
	-- Armado de condiciones de filtro parámetro
	existsPosJournalIDs = posjournalIDs is not null and array_length(posjournalIDs,1) > 0 and posjournalIDs[1] <> -1;

	-- Filtro de cajas diarias
	wherePosJournalIDs = '';
	if existsPosJournalIDs then
		wherePosJournalIDs = ' AND ah.c_posjournal_id IN ('|| array_to_string(posjournalIDs, ',') || ')';
	end if;
	
	consulta =  
	'SELECT al.c_allocationhdr_id, al.c_allocationline_id, al.ad_client_id, al.ad_org_id, al.isactive, al.created, al.createdby, al.updated, al.updatedby, al.c_invoice_id, al.c_payment_id, al.c_cashline_id, al.c_invoice_credit_id, ''CR''::character varying AS tendertype, ic.documentno, ic.description, ic.documentno AS info, (al.amount + al.discountamt + al.writeoffamt)::numeric(20,2) AS amount, NULL::integer AS c_cash_id, NULL::integer AS line, ic.c_doctype_id, NULL::character varying AS checkno, NULL::character varying AS a_bank, NULL::character varying AS transferno, NULL::character(1) AS creditcardtype, NULL::integer AS m_entidadfinancieraplan_id, NULL::integer AS m_entidadfinanciera_id, NULL::character varying AS couponnumber, date_trunc(''day''::text, ah.datetrx) AS allocationdate, ic.docstatus, ic.dateacct::date AS dateacct, i.documentno AS invoice_documentno, i.grandtotal AS invoice_grandtotal, NULL::character varying AS entidadfinanciera_value, NULL::character varying AS entidadfinanciera_name, NULL::character varying AS bp_entidadfinanciera_value, NULL::character varying AS bp_entidadfinanciera_name, NULL::character varying AS cupon, NULL::character varying AS creditcard, dt.isfiscaldocument, dt.isfiscal, ic.fiscalalreadyprinted, ah.c_currency_id
   FROM c_allocationline al
   JOIN c_allocationhdr ah ON al.c_allocationhdr_id = ah.c_allocationhdr_id
   JOIN c_invoice ic ON al.c_invoice_credit_id = ic.c_invoice_id
   JOIN c_doctype dt ON dt.c_doctype_id = ic.c_doctypetarget_id
   LEFT JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
   WHERE 1=1 ' || wherePosJournalIDs;

	--raise notice '%', consulta;
	FOR adocument IN EXECUTE consulta LOOP
		return next adocument;
	END LOOP;
END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION c_posjournal_c_invoice_credit_v_filtered(anyarray)
  OWNER TO libertya;

--funview c_pos_declaracionvalores_voided_filtered
CREATE OR REPLACE FUNCTION c_pos_declaracionvalores_voided_filtered(posjournalids anyarray)
  RETURNS SETOF c_pos_declaracionvalores_type AS
$BODY$
declare
	consulta varchar;
	wherePosJournalAllocationsIDs varchar;
	existsPosJournalIDs boolean;
	parameterFunctionCall varchar;
	posJournalInvoicesFunctionCall varchar;
	adocument c_pos_declaracionvalores_type;
BEGIN
	-- Armado de condiciones de filtro parámetro
	existsPosJournalIDs = posjournalIDs is not null and array_length(posjournalIDs,1) > 0 and posjournalIDs[1] <> -1;
	
	-- Filtro de cajas diarias
	wherePosJournalAllocationsIDs = '';
	if existsPosJournalIDs then
		wherePosJournalAllocationsIDs = ' AND ji.c_posjournal_id IN ('|| array_to_string(posjournalIDs, ',') || ')';
	end if;

	-- Armado del parámetro a la función c_posjournalinvoices_v_filtered
	parameterFunctionCall = 'array[-1]';
	if existsPosJournalIDs then
		parameterFunctionCall = 'ARRAY[' || array_to_string(posjournalIDs, ',') || ']';
	end if;
	
	-- Armado de llamado a función c_posjournalinvoices_v_filtered
	posJournalInvoicesFunctionCall = 'c_posjournalinvoices_v_filtered(' || parameterFunctionCall || ', false)';
	
	-- Consulta
	consulta = 
	'SELECT ji.ad_client_id, ji.ad_org_id, ji.c_posjournal_id, pj.ad_user_id, ji.c_currency_id, ji.dateinvoiced AS datetrx, ji.docstatus, NULL::unknown AS category, ji.docbasetype AS tendertype, (ji.documentno::text || '' ''::text) || COALESCE(ji.description, ''''::character varying)::text AS description, NULL::unknown AS c_charge_id, NULL::unknown AS chargename, ji.c_invoice_id AS doc_id, ji.signo_issotrx::numeric * currencybase(ji.grandtotal, i.c_currency_id, i.dateacct, i.ad_client_id, i.ad_org_id)::numeric + COALESCE(( SELECT sum(al2.changeamt) AS sum
           FROM c_allocationline al2
      JOIN c_payment p2 ON p2.c_payment_id = al2.c_payment_id
   JOIN c_cashline cl ON cl.c_payment_id = p2.c_payment_id
  WHERE al2.c_invoice_id = ji.c_invoice_id AND p2.tendertype = ''C''::bpchar AND al2.isactive = ''N''::bpchar), 0::numeric) AS ingreso, 0::numeric(22,2) AS egreso, ji.c_invoice_id, ji.documentno AS invoice_documentno, ji.grandtotal AS invoice_grandtotal, NULL::unknown AS entidadfinanciera_value, NULL::unknown AS entidadfinanciera_name, NULL::unknown AS bp_entidadfinanciera_value, NULL::unknown AS bp_entidadfinanciera_name, NULL::unknown AS cupon, NULL::unknown AS creditcard, ic.documentno AS generated_invoice_documentno, ji.allocation_active, pos.c_pos_id, pos.name AS posname
   FROM ' || posJournalInvoicesFunctionCall || ' ji
   JOIN c_posjournal pj ON ji.c_posjournal_id = pj.c_posjournal_id
   JOIN c_pos pos ON pos.c_pos_id = pj.c_pos_id
   JOIN c_allocationline al ON al.c_allocationhdr_id = ji.c_allocationhdr_id
   JOIN c_invoice i ON i.c_invoice_id = al.c_invoice_id
   JOIN c_invoice ic ON al.c_invoice_credit_id = ic.c_invoice_id
  WHERE (ji.docstatus = ANY (ARRAY[''VO''::bpchar, ''RE''::bpchar])) ' || wherePosJournalAllocationsIDs || 
  ' AND (ji.isfiscal IS NULL OR ji.isfiscal = ''N''::bpchar OR ji.isfiscal = ''Y''::bpchar AND ji.fiscalalreadyprinted = ''Y''::bpchar) AND i.isvoidable = ''N''::bpchar;';

--raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION c_pos_declaracionvalores_voided_filtered(anyarray)
  OWNER TO libertya;

--funview c_pos_declaracionvalores_ventas_filtered
CREATE OR REPLACE FUNCTION c_pos_declaracionvalores_ventas_filtered(posjournalids anyarray)
  RETURNS SETOF c_pos_declaracionvalores_type AS
$BODY$
declare
	consulta varchar;
	wherePosJournalInvoicesIDs varchar;
	wherePosJournalAllocationsIDs varchar;
	whereClauseStd varchar;
	existsPosJournalIDs boolean;
	parameterFunctionCall varchar;
	posJournalInvoicesFunctionCall varchar;
	adocument c_pos_declaracionvalores_type;
BEGIN
	-- Armado de condiciones de filtro parámetro
	existsPosJournalIDs = posjournalIDs is not null and array_length(posjournalIDs,1) > 0 and posjournalIDs[1] <> -1;
	
	-- Filtro de cajas diarias
	wherePosJournalInvoicesIDs = '';
	wherePosJournalAllocationsIDs = '';
	if existsPosJournalIDs then
		wherePosJournalInvoicesIDs = ' AND i.c_posjournal_id IN ('|| array_to_string(posjournalIDs, ',') || ')';
		wherePosJournalAllocationsIDs = ' AND ah.c_posjournal_id IN ('|| array_to_string(posjournalIDs, ',') || ')';
	end if;
	
	-- Condición std
	whereClauseStd = 'i.docstatus in (''CO'',''CL'')';

	-- Armado del parámetro a la función c_posjournalinvoices_v_filtered
	parameterFunctionCall = 'array[-1]';
	if existsPosJournalIDs then
		parameterFunctionCall = 'ARRAY[' || array_to_string(posjournalIDs, ',') || ']';
	end if;
	
	-- Armado de llamado a función c_posjournalinvoices_v_filtered
	posJournalInvoicesFunctionCall = 'c_posjournalinvoices_v_filtered(' || parameterFunctionCall || ')';
	
	-- Consulta
	consulta = 
	'SELECT i.ad_client_id, i.ad_org_id, i.c_posjournal_id, i.ad_user_id, i.c_currency_id, i.dateinvoiced AS datetrx, i.docstatus, NULL::unknown AS category, dt.docbasetype AS tendertype, (i.documentno::text || '' ''::text) || COALESCE(i.description, ''''::character varying)::text AS description, i.c_charge_id, i.chargename, i.c_invoice_id AS doc_id, 
        CASE dt.signo_issotrx
            WHEN 1 THEN i.ca_amount
            WHEN (-1) THEN 0::numeric
            ELSE NULL::numeric
        END::numeric(22,2) AS ingreso, 
        CASE dt.signo_issotrx
            WHEN 1 THEN 0::numeric
            WHEN (-1) THEN abs(i.ca_amount)
            ELSE NULL::numeric
        END::numeric(22,2) AS egreso, i.c_invoice_id, i.documentno AS invoice_documentno, i.total AS invoice_grandtotal, NULL::unknown AS entidadfinanciera_value, NULL::unknown AS entidadfinanciera_name, NULL::unknown AS bp_entidadfinanciera_value, NULL::unknown AS bp_entidadfinanciera_name, NULL::unknown AS cupon, NULL::unknown AS creditcard, NULL::unknown AS generated_invoice_documentno, i.allocation_active, i.c_pos_id, i.posname
   FROM ( SELECT DISTINCT i.ad_client_id, i.ad_org_id, i.documentno, inv.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, i.dateinvoiced::date AS dateinvoiced, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name AS chargename, currencybase(i.grandtotal, i.c_currency_id, i.dateacct::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2) AS total, COALESCE(ds.amount, 0::numeric)::numeric(22,2) AS ca_amount, i.allocation_active, pos.c_pos_id, pos.name AS posname
           FROM ' || posJournalInvoicesFunctionCall || ' i
      JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_pos pos ON pos.c_pos_id = pj.c_pos_id
   JOIN c_invoice inv ON i.c_invoice_id = inv.c_invoice_id
   LEFT JOIN ( SELECT al.c_invoice_id, ah.c_posjournal_id, sum(al.amount) AS amount
    FROM c_allocationhdr ah
   JOIN c_allocationline al ON al.c_allocationhdr_id = ah.c_allocationhdr_id
   JOIN c_invoice i ON i.c_invoice_id = al.c_invoice_id
  WHERE ' || whereClauseStd || wherePosJournalAllocationsIDs ||
  ' AND ah.isactive = ''Y''::bpchar AND i.isvoidable = ''N''::bpchar
  GROUP BY al.c_invoice_id, ah.c_posjournal_id) ds ON ds.c_invoice_id = inv.c_invoice_id AND pj.c_posjournal_id = ds.c_posjournal_id
   LEFT JOIN c_charge ch ON ch.c_charge_id = inv.c_charge_id
   WHERE '  || whereClauseStd || wherePosJournalInvoicesIDs ||
  ' ORDER BY i.ad_client_id, i.ad_org_id, i.documentno, inv.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, i.dateinvoiced::date, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name, currencybase(i.grandtotal, i.c_currency_id, i.dateacct::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2), COALESCE(ds.amount, 0::numeric)::numeric(22,2), i.allocation_active, pos.c_pos_id, pos.name) i
   JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id;';

--raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION c_pos_declaracionvalores_ventas_filtered(anyarray)
  OWNER TO libertya;

--funview c_pos_declaracionvalores_payments_filtered
CREATE OR REPLACE FUNCTION c_pos_declaracionvalores_payments_filtered(posjournalids anyarray)
  RETURNS SETOF c_pos_declaracionvalores_type AS
$BODY$
declare
	consulta varchar;
	wherePosJournalPaymentsIDs varchar;
	whereClauseStd varchar;
	existsPosJournalIDs boolean;
	parameterFunctionCall varchar;
	posJournalPaymentsFunctionCall varchar;
	adocument c_pos_declaracionvalores_type;
BEGIN
	-- Armado de condiciones de filtro parámetro
	existsPosJournalIDs = posjournalIDs is not null and array_length(posjournalIDs,1) > 0 and posjournalIDs[1] <> -1;
	
	-- Filtro de cajas diarias
	wherePosJournalPaymentsIDs = '';
	if existsPosJournalIDs then
		wherePosJournalPaymentsIDs = ' AND pj.c_posjournal_id IN ('|| array_to_string(posjournalIDs, ',') || ')';
	end if;
	
	-- Condición std
	whereClauseStd = 'p.docstatus in (''CO'',''CL'')';

	-- Armado del parámetro a la función c_posjournalinvoices_v_filtered
	parameterFunctionCall = 'array[-1]';
	if existsPosJournalIDs then
		parameterFunctionCall = 'ARRAY[' || array_to_string(posjournalIDs, ',') || ']';
	end if;
	
	-- Armado de llamado a función c_posjournalinvoices_v_filtered
	posJournalPaymentsFunctionCall = 'c_posjournal_c_payment_v_filtered(' || parameterFunctionCall || ')';
	
	-- Consulta
	consulta = 
	'SELECT p.ad_client_id, p.ad_org_id, p.c_posjournal_id, p.ad_user_id, p.c_currency_id, p.datetrx, p.docstatus, NULL::text AS category, p.tendertype, (p.documentno::text || '' ''::text) || COALESCE(p.description, ''''::character varying)::text AS description, p.c_charge_id, p.chargename, p.c_payment_id AS doc_id, 
        CASE WHEN (p.total * p.signo_issotrx) < 0 
            THEN abs(p.total)
            ELSE 0::numeric
        END::numeric(22,2) AS ingreso, 
        CASE WHEN (p.total * p.signo_issotrx) >= 0 
	    THEN abs(p.total)
            ELSE 0::numeric
        END::numeric(22,2) AS egreso, p.c_invoice_id, p.invoice_documentno, p.invoice_grandtotal, p.entidadfinanciera_value, p.entidadfinanciera_name, p.bp_entidadfinanciera_value, p.bp_entidadfinanciera_name, p.cupon, p.creditcard, NULL::unknown AS generated_invoice_documentno, p.allocation_active, p.c_pos_id, p.posname, p.m_entidadfinanciera_id
   FROM ( SELECT p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date AS datetrx, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name AS chargename, 
		sum(currencybase(pjp.amount, pjp.c_currency_id, pjp.dateacct, pjp.ad_client_id, pjp.ad_org_id) + 
                CASE
                    WHEN p.tendertype = ''C''::bpchar THEN pjp.changeamt
                    ELSE 0::numeric
                END)::numeric(22,2) AS total, pjp.invoice_documentno, 
                (currencybase(pjp.invoice_grandtotal, i.c_currency_id, i.dateacct, i.ad_client_id, i.ad_org_id) + 
                CASE
                    WHEN p.tendertype = ''C''::bpchar THEN pjp.changeamt
                    ELSE 0::numeric
                END)::numeric(20,2) AS invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive AS allocation_active, pjp.c_invoice_id, pos.c_pos_id, pos.name AS posname, pjp.m_entidadfinanciera_id, dt.signo_issotrx
           FROM c_payment p
           INNER JOIN c_doctype dt on dt.c_doctype_id = p.c_doctype_id
      JOIN ' || posJournalPaymentsFunctionCall || ' pjp ON pjp.c_payment_id = p.c_payment_id
   JOIN c_posjournal pj ON pj.c_posjournal_id = p.c_posjournal_id
   JOIN c_pos pos ON pos.c_pos_id = pj.c_pos_id
   LEFT JOIN c_charge ch ON ch.c_charge_id = p.c_charge_id
   LEFT JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
  WHERE ' || whereClauseStd || wherePosJournalPaymentsIDs ||
  ' AND (i.c_invoice_id IS NULL OR NOT (EXISTS ( SELECT cl.c_cashline_id
   FROM c_cashline cl
  WHERE cl.c_payment_id = p.c_payment_id AND i.isvoidable = ''Y''::bpchar)))
GROUP BY p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, i.c_currency_id, i.dateacct, i.ad_client_id, i.ad_org_id, pjp.changeamt, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive, pjp.c_invoice_id, pos.c_pos_id, pos.name, pjp.m_entidadfinanciera_id, dt.signo_issotrx) p;';

--raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION c_pos_declaracionvalores_payments_filtered(anyarray)
  OWNER TO libertya;

--funview c_pos_declaracionvalores_credit_filtered
CREATE OR REPLACE FUNCTION c_pos_declaracionvalores_credit_filtered(posjournalids anyarray)
  RETURNS SETOF c_pos_declaracionvalores_type AS
$BODY$
declare
	consulta varchar;
	wherePosJournalCreditIDs varchar;
	whereClauseStd varchar;
	existsPosJournalIDs boolean;
	parameterFunctionCall varchar;
	posJournalCreditFunctionCall varchar;
	adocument c_pos_declaracionvalores_type;
BEGIN
	-- Armado de condiciones de filtro parámetro
	existsPosJournalIDs = posjournalIDs is not null and array_length(posjournalIDs,1) > 0 and posjournalIDs[1] <> -1;
	
	-- Filtro de cajas diarias
	wherePosJournalCreditIDs = '';
	if existsPosJournalIDs then
		wherePosJournalCreditIDs = ' AND pj.c_posjournal_id IN ('|| array_to_string(posjournalIDs, ',') || ')';
	end if;
	
	-- Condición std
	whereClauseStd = 'i.docstatus in (''CO'',''CL'')';

	-- Armado del parámetro a la función c_posjournalinvoices_v_filtered
	parameterFunctionCall = 'array[-1]';
	if existsPosJournalIDs then
		parameterFunctionCall = 'ARRAY[' || array_to_string(posjournalIDs, ',') || ']';
	end if;
	
	-- Armado de llamado a función c_posjournalinvoices_v_filtered
	posJournalCreditFunctionCall = 'c_posjournal_c_invoice_credit_v_filtered(' || parameterFunctionCall || ')';
	
	-- Consulta
	consulta = 
	'SELECT i.ad_client_id, i.ad_org_id, i.c_posjournal_id, i.ad_user_id, i.c_currency_id, i.dateinvoiced AS datetrx, i.docstatus, NULL::unknown AS category, i.tendertype, (i.documentno::text || '' ''::text) || COALESCE(i.description, ''''::character varying)::text AS description, i.c_charge_id, i.chargename, i.c_invoice_id AS doc_id, i.total AS ingreso, 0 AS egreso, i.invoice_id AS c_invoice_id, i.invoice_documentno, i.invoice_grandtotal, i.entidadfinanciera_value, i.entidadfinanciera_name, i.bp_entidadfinanciera_value, i.bp_entidadfinanciera_name, i.cupon, i.creditcard, NULL::unknown AS generated_invoice_documentno, i.allocation_active, i.c_pos_id, i.posname
   FROM ( SELECT i.ad_client_id, i.ad_org_id, i.documentno, i.description, ah.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, pjp.tendertype, i.dateinvoiced::date AS dateinvoiced, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, pjp.c_currency_id, pjp.dateacct, pjp.ad_client_id, pjp.ad_org_id)::numeric(22,2))::numeric(22,2) AS total, pjp.invoice_documentno, currencybase(pjp.invoice_grandtotal, si.c_currency_id, si.dateacct, si.ad_client_id, si.ad_org_id)::numeric(22,2) as invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive AS allocation_active, pjp.c_invoice_id AS invoice_id, pos.c_pos_id, pos.name AS posname
           FROM c_invoice i
      JOIN ' || posJournalCreditFunctionCall || ' pjp ON pjp.c_invoice_credit_id = i.c_invoice_id
   JOIN c_allocationhdr ah ON ah.c_allocationhdr_id = pjp.c_allocationhdr_id
   JOIN c_posjournal pj ON pj.c_posjournal_id = ah.c_posjournal_id
   JOIN c_pos pos ON pos.c_pos_id = pj.c_pos_id
   LEFT JOIN c_invoice si ON si.c_invoice_id = pjp.c_invoice_id
   LEFT JOIN c_charge ch ON ch.c_charge_id = i.c_charge_id
   WHERE ' || whereClauseStd || wherePosJournalCreditIDs ||
  ' GROUP BY i.ad_client_id, i.ad_org_id, i.documentno, i.description, ah.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, pjp.tendertype, i.dateinvoiced::date, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, si.c_currency_id, si.dateacct, si.ad_client_id, si.ad_org_id, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive, pjp.c_invoice_id, pos.c_pos_id, pos.name) i
   JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id';

--raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION c_pos_declaracionvalores_credit_filtered(anyarray)
  OWNER TO libertya;

CREATE OR REPLACE FUNCTION c_pos_declaracionvalores_cash_filtered(posjournalids anyarray)
  RETURNS SETOF c_pos_declaracionvalores_type AS
$BODY$
declare
	consulta varchar;
	wherePosJournalCashIDs varchar;
	whereClauseStd varchar;
	existsPosJournalIDs boolean;
	parameterFunctionCall varchar;
	posJournalCashFunctionCall varchar;
	adocument c_pos_declaracionvalores_type;
BEGIN
	-- Armado de condiciones de filtro parámetro
	existsPosJournalIDs = posjournalIDs is not null and array_length(posjournalIDs,1) > 0 and posjournalIDs[1] <> -1;
	
	-- Filtro de cajas diarias
	wherePosJournalCashIDs = '';
	if existsPosJournalIDs then
		wherePosJournalCashIDs = ' AND pj.c_posjournal_id IN ('|| array_to_string(posjournalIDs, ',') || ')';
	end if;
	
	-- Condición std
	whereClauseStd = 'cl.docstatus in (''CO'',''CL'')';

	-- Armado del parámetro a la función c_posjournalinvoices_v_filtered
	parameterFunctionCall = 'array[-1]';
	if existsPosJournalIDs then
		parameterFunctionCall = 'ARRAY[' || array_to_string(posjournalIDs, ',') || ']';
	end if;
	
	-- Armado de llamado a función c_posjournalinvoices_v_filtered
	posJournalCashFunctionCall = 'c_posjournal_c_cash_v_filtered(' || parameterFunctionCall || ')';
	
	-- Consulta
	consulta = 
	'SELECT c.ad_client_id, c.ad_org_id, c.c_posjournal_id, c.ad_user_id, c.c_currency_id, c.datetrx, c.docstatus, c.cashtype AS category, c.tendertype, 
        CASE
            WHEN length(c.description::text) > 0 THEN c.description
            ELSE c.info
        END AS description, c.c_charge_id, c.chargename, c.c_cashline_id AS doc_id, 
        CASE sign(c.amount)
            WHEN (-1) THEN 0::numeric
            ELSE c.total
        END::numeric(22,2) AS ingreso, 
        CASE sign(c.amount)
            WHEN (-1) THEN abs(c.total)
            ELSE 0::numeric
        END::numeric(22,2) AS egreso, c.c_invoice_id, c.invoice_documentno, c.invoice_grandtotal, c.entidadfinanciera_value, c.entidadfinanciera_name, c.bp_entidadfinanciera_value, c.bp_entidadfinanciera_name, c.cupon, c.creditcard, NULL::unknown AS generated_invoice_documentno, c.allocation_active, c.c_pos_id, c.posname
   FROM ( SELECT cl.ad_client_id, cl.ad_org_id, cl.c_cashline_id, c.c_posjournal_id, pj.ad_user_id, cl.c_currency_id, c.statementdate::date AS datetrx, cl.docstatus, cl.description, pjp.info, pjp.tendertype, cl.cashtype, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, pjp.c_currency_id, pjp.dateacct, pjp.ad_client_id, pjp.ad_org_id))::numeric(22,2) AS total, pjp.invoice_documentno, currencybase(pjp.invoice_grandtotal, i.c_currency_id, i.dateacct, i.ad_client_id, i.ad_org_id)::numeric(22,2) as invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, cl.amount, pjp.isactive AS allocation_active, pjp.c_invoice_id, pos.c_pos_id, pos.name AS posname
           FROM c_cashline cl
      JOIN c_cash c ON c.c_cash_id = cl.c_cash_id
   JOIN ' || posJournalCashFunctionCall || ' pjp ON pjp.c_cashline_id = cl.c_cashline_id
   JOIN c_posjournal pj ON pj.c_posjournal_id = c.c_posjournal_id
   JOIN c_pos pos ON pos.c_pos_id = pj.c_pos_id
   LEFT JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
   LEFT JOIN c_charge ch ON ch.c_charge_id = cl.c_charge_id
   WHERE ' || whereClauseStd || wherePosJournalCashIDs ||
   ' GROUP BY cl.ad_client_id, cl.ad_org_id, cl.c_cashline_id, c.c_posjournal_id, pj.ad_user_id, cl.c_currency_id, c.statementdate::date, cl.docstatus, cl.description, pjp.info, pjp.tendertype, cl.cashtype, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, i.c_currency_id, i.dateacct, i.ad_client_id, i.ad_org_id, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, cl.amount, pjp.isactive, pjp.c_invoice_id, pos.c_pos_id, pos.name) c;';

--raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION c_pos_declaracionvalores_cash_filtered(anyarray)
  OWNER TO libertya;

--Creación de views
CREATE OR REPLACE VIEW c_posjournal_c_cash_v AS 
 SELECT c_posjournal_c_cash_v_filtered.c_allocationhdr_id, c_posjournal_c_cash_v_filtered.c_allocationline_id, c_posjournal_c_cash_v_filtered.ad_client_id, c_posjournal_c_cash_v_filtered.ad_org_id, c_posjournal_c_cash_v_filtered.isactive, c_posjournal_c_cash_v_filtered.created, c_posjournal_c_cash_v_filtered.createdby, c_posjournal_c_cash_v_filtered.updated, c_posjournal_c_cash_v_filtered.updatedby, c_posjournal_c_cash_v_filtered.c_invoice_id, c_posjournal_c_cash_v_filtered.c_payment_id, c_posjournal_c_cash_v_filtered.c_cashline_id, c_posjournal_c_cash_v_filtered.c_invoice_credit_id, c_posjournal_c_cash_v_filtered.tendertype, c_posjournal_c_cash_v_filtered.documentno, c_posjournal_c_cash_v_filtered.description, c_posjournal_c_cash_v_filtered.info, c_posjournal_c_cash_v_filtered.amount, c_posjournal_c_cash_v_filtered.c_cash_id, c_posjournal_c_cash_v_filtered.line, c_posjournal_c_cash_v_filtered.c_doctype_id, c_posjournal_c_cash_v_filtered.checkno, c_posjournal_c_cash_v_filtered.a_bank, c_posjournal_c_cash_v_filtered.transferno, c_posjournal_c_cash_v_filtered.creditcardtype, c_posjournal_c_cash_v_filtered.m_entidadfinancieraplan_id, c_posjournal_c_cash_v_filtered.m_entidadfinanciera_id, c_posjournal_c_cash_v_filtered.couponnumber, c_posjournal_c_cash_v_filtered.allocationdate, c_posjournal_c_cash_v_filtered.docstatus, c_posjournal_c_cash_v_filtered.dateacct, c_posjournal_c_cash_v_filtered.invoice_documentno, c_posjournal_c_cash_v_filtered.invoice_grandtotal, c_posjournal_c_cash_v_filtered.entidadfinanciera_value, c_posjournal_c_cash_v_filtered.entidadfinanciera_name, c_posjournal_c_cash_v_filtered.bp_entidadfinanciera_value, c_posjournal_c_cash_v_filtered.bp_entidadfinanciera_name, c_posjournal_c_cash_v_filtered.cupon, c_posjournal_c_cash_v_filtered.creditcard, c_posjournal_c_cash_v_filtered.isfiscaldocument, c_posjournal_c_cash_v_filtered.isfiscal, c_posjournal_c_cash_v_filtered.fiscalalreadyprinted
   FROM c_posjournal_c_cash_v_filtered(ARRAY[(-1)]) c_posjournal_c_cash_v_filtered(c_allocationhdr_id, c_allocationline_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_invoice_id, c_payment_id, c_cashline_id, c_invoice_credit_id, tendertype, documentno, description, info, amount, c_cash_id, line, c_doctype_id, checkno, a_bank, transferno, creditcardtype, m_entidadfinancieraplan_id, m_entidadfinanciera_id, couponnumber, allocationdate, docstatus, dateacct, invoice_documentno, invoice_grandtotal, entidadfinanciera_value, entidadfinanciera_name, bp_entidadfinanciera_value, bp_entidadfinanciera_name, cupon, creditcard, isfiscaldocument, isfiscal, fiscalalreadyprinted);

ALTER TABLE c_posjournal_c_cash_v
  OWNER TO libertya;

CREATE OR REPLACE VIEW c_posjournal_c_invoice_credit_v AS 
 SELECT c_posjournal_c_invoice_credit_v_filtered.c_allocationhdr_id, c_posjournal_c_invoice_credit_v_filtered.c_allocationline_id, c_posjournal_c_invoice_credit_v_filtered.ad_client_id, c_posjournal_c_invoice_credit_v_filtered.ad_org_id, c_posjournal_c_invoice_credit_v_filtered.isactive, c_posjournal_c_invoice_credit_v_filtered.created, c_posjournal_c_invoice_credit_v_filtered.createdby, c_posjournal_c_invoice_credit_v_filtered.updated, c_posjournal_c_invoice_credit_v_filtered.updatedby, c_posjournal_c_invoice_credit_v_filtered.c_invoice_id, c_posjournal_c_invoice_credit_v_filtered.c_payment_id, c_posjournal_c_invoice_credit_v_filtered.c_cashline_id, c_posjournal_c_invoice_credit_v_filtered.c_invoice_credit_id, c_posjournal_c_invoice_credit_v_filtered.tendertype, c_posjournal_c_invoice_credit_v_filtered.documentno, c_posjournal_c_invoice_credit_v_filtered.description, c_posjournal_c_invoice_credit_v_filtered.info, c_posjournal_c_invoice_credit_v_filtered.amount, c_posjournal_c_invoice_credit_v_filtered.c_cash_id, c_posjournal_c_invoice_credit_v_filtered.line, c_posjournal_c_invoice_credit_v_filtered.c_doctype_id, c_posjournal_c_invoice_credit_v_filtered.checkno, c_posjournal_c_invoice_credit_v_filtered.a_bank, c_posjournal_c_invoice_credit_v_filtered.transferno, c_posjournal_c_invoice_credit_v_filtered.creditcardtype, c_posjournal_c_invoice_credit_v_filtered.m_entidadfinancieraplan_id, c_posjournal_c_invoice_credit_v_filtered.m_entidadfinanciera_id, c_posjournal_c_invoice_credit_v_filtered.couponnumber, c_posjournal_c_invoice_credit_v_filtered.allocationdate, c_posjournal_c_invoice_credit_v_filtered.docstatus, c_posjournal_c_invoice_credit_v_filtered.dateacct, c_posjournal_c_invoice_credit_v_filtered.invoice_documentno, c_posjournal_c_invoice_credit_v_filtered.invoice_grandtotal, c_posjournal_c_invoice_credit_v_filtered.entidadfinanciera_value, c_posjournal_c_invoice_credit_v_filtered.entidadfinanciera_name, c_posjournal_c_invoice_credit_v_filtered.bp_entidadfinanciera_value, c_posjournal_c_invoice_credit_v_filtered.bp_entidadfinanciera_name, c_posjournal_c_invoice_credit_v_filtered.cupon, c_posjournal_c_invoice_credit_v_filtered.creditcard, c_posjournal_c_invoice_credit_v_filtered.isfiscaldocument, c_posjournal_c_invoice_credit_v_filtered.isfiscal, c_posjournal_c_invoice_credit_v_filtered.fiscalalreadyprinted
   FROM c_posjournal_c_invoice_credit_v_filtered(ARRAY[(-1)]) c_posjournal_c_invoice_credit_v_filtered(c_allocationhdr_id, c_allocationline_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_invoice_id, c_payment_id, c_cashline_id, c_invoice_credit_id, tendertype, documentno, description, info, amount, c_cash_id, line, c_doctype_id, checkno, a_bank, transferno, creditcardtype, m_entidadfinancieraplan_id, m_entidadfinanciera_id, couponnumber, allocationdate, docstatus, dateacct, invoice_documentno, invoice_grandtotal, entidadfinanciera_value, entidadfinanciera_name, bp_entidadfinanciera_value, bp_entidadfinanciera_name, cupon, creditcard, isfiscaldocument, isfiscal, fiscalalreadyprinted);

ALTER TABLE c_posjournal_c_invoice_credit_v
  OWNER TO libertya;

CREATE OR REPLACE VIEW c_posjournal_c_payment_v AS 
 SELECT c_posjournal_c_payment_v_filtered.c_allocationhdr_id, c_posjournal_c_payment_v_filtered.c_allocationline_id, c_posjournal_c_payment_v_filtered.ad_client_id, c_posjournal_c_payment_v_filtered.ad_org_id, c_posjournal_c_payment_v_filtered.isactive, c_posjournal_c_payment_v_filtered.created, c_posjournal_c_payment_v_filtered.createdby, c_posjournal_c_payment_v_filtered.updated, c_posjournal_c_payment_v_filtered.updatedby, c_posjournal_c_payment_v_filtered.c_invoice_id, c_posjournal_c_payment_v_filtered.c_payment_id, c_posjournal_c_payment_v_filtered.c_cashline_id, c_posjournal_c_payment_v_filtered.c_invoice_credit_id, c_posjournal_c_payment_v_filtered.tendertype, c_posjournal_c_payment_v_filtered.documentno, c_posjournal_c_payment_v_filtered.description, c_posjournal_c_payment_v_filtered.info, c_posjournal_c_payment_v_filtered.amount, c_posjournal_c_payment_v_filtered.c_cash_id, c_posjournal_c_payment_v_filtered.line, c_posjournal_c_payment_v_filtered.c_doctype_id, c_posjournal_c_payment_v_filtered.checkno, c_posjournal_c_payment_v_filtered.a_bank, c_posjournal_c_payment_v_filtered.transferno, c_posjournal_c_payment_v_filtered.creditcardtype, c_posjournal_c_payment_v_filtered.m_entidadfinancieraplan_id, c_posjournal_c_payment_v_filtered.m_entidadfinanciera_id, c_posjournal_c_payment_v_filtered.couponnumber, c_posjournal_c_payment_v_filtered.allocationdate, c_posjournal_c_payment_v_filtered.docstatus, c_posjournal_c_payment_v_filtered.dateacct, c_posjournal_c_payment_v_filtered.invoice_documentno, c_posjournal_c_payment_v_filtered.invoice_grandtotal, c_posjournal_c_payment_v_filtered.entidadfinanciera_value, c_posjournal_c_payment_v_filtered.entidadfinanciera_name, c_posjournal_c_payment_v_filtered.bp_entidadfinanciera_value, c_posjournal_c_payment_v_filtered.bp_entidadfinanciera_name, c_posjournal_c_payment_v_filtered.cupon, c_posjournal_c_payment_v_filtered.creditcard, c_posjournal_c_payment_v_filtered.isfiscaldocument, c_posjournal_c_payment_v_filtered.isfiscal, c_posjournal_c_payment_v_filtered.fiscalalreadyprinted, c_posjournal_c_payment_v_filtered.changeamt
   FROM c_posjournal_c_payment_v_filtered(ARRAY[(-1)]) c_posjournal_c_payment_v_filtered(c_allocationhdr_id, c_allocationline_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_invoice_id, c_payment_id, c_cashline_id, c_invoice_credit_id, tendertype, documentno, description, info, amount, c_cash_id, line, c_doctype_id, checkno, a_bank, transferno, creditcardtype, m_entidadfinancieraplan_id, m_entidadfinanciera_id, couponnumber, allocationdate, docstatus, dateacct, invoice_documentno, invoice_grandtotal, entidadfinanciera_value, entidadfinanciera_name, bp_entidadfinanciera_value, bp_entidadfinanciera_name, cupon, creditcard, isfiscaldocument, isfiscal, fiscalalreadyprinted, changeamt);

ALTER TABLE c_posjournal_c_payment_v
  OWNER TO libertya;

CREATE OR REPLACE VIEW c_pos_declaracionvalores_cash AS 
 SELECT c_pos_declaracionvalores_cash_filtered.ad_client_id, c_pos_declaracionvalores_cash_filtered.ad_org_id, c_pos_declaracionvalores_cash_filtered.c_posjournal_id, c_pos_declaracionvalores_cash_filtered.ad_user_id, c_pos_declaracionvalores_cash_filtered.c_currency_id, c_pos_declaracionvalores_cash_filtered.datetrx, c_pos_declaracionvalores_cash_filtered.docstatus, c_pos_declaracionvalores_cash_filtered.category, c_pos_declaracionvalores_cash_filtered.tendertype, c_pos_declaracionvalores_cash_filtered.description, c_pos_declaracionvalores_cash_filtered.c_charge_id, c_pos_declaracionvalores_cash_filtered.chargename, c_pos_declaracionvalores_cash_filtered.doc_id, c_pos_declaracionvalores_cash_filtered.ingreso, c_pos_declaracionvalores_cash_filtered.egreso, c_pos_declaracionvalores_cash_filtered.c_invoice_id, c_pos_declaracionvalores_cash_filtered.invoice_documentno, c_pos_declaracionvalores_cash_filtered.invoice_grandtotal, c_pos_declaracionvalores_cash_filtered.entidadfinanciera_value, c_pos_declaracionvalores_cash_filtered.entidadfinanciera_name, c_pos_declaracionvalores_cash_filtered.bp_entidadfinanciera_value, c_pos_declaracionvalores_cash_filtered.bp_entidadfinanciera_name, c_pos_declaracionvalores_cash_filtered.cupon, c_pos_declaracionvalores_cash_filtered.creditcard, c_pos_declaracionvalores_cash_filtered.generated_invoice_documentno, c_pos_declaracionvalores_cash_filtered.allocation_active, c_pos_declaracionvalores_cash_filtered.c_pos_id, c_pos_declaracionvalores_cash_filtered.posname
   FROM c_pos_declaracionvalores_cash_filtered(ARRAY[(-1)]) c_pos_declaracionvalores_cash_filtered(ad_client_id, ad_org_id, c_posjournal_id, ad_user_id, c_currency_id, datetrx, docstatus, category, tendertype, description, c_charge_id, chargename, doc_id, ingreso, egreso, c_invoice_id, invoice_documentno, invoice_grandtotal, entidadfinanciera_value, entidadfinanciera_name, bp_entidadfinanciera_value, bp_entidadfinanciera_name, cupon, creditcard, generated_invoice_documentno, allocation_active, c_pos_id, posname);

ALTER TABLE c_pos_declaracionvalores_cash
  OWNER TO libertya;

CREATE OR REPLACE VIEW c_pos_declaracionvalores_credit AS 
 SELECT c_pos_declaracionvalores_credit_filtered.ad_client_id, c_pos_declaracionvalores_credit_filtered.ad_org_id, c_pos_declaracionvalores_credit_filtered.c_posjournal_id, c_pos_declaracionvalores_credit_filtered.ad_user_id, c_pos_declaracionvalores_credit_filtered.c_currency_id, c_pos_declaracionvalores_credit_filtered.datetrx, c_pos_declaracionvalores_credit_filtered.docstatus, c_pos_declaracionvalores_credit_filtered.category, c_pos_declaracionvalores_credit_filtered.tendertype, c_pos_declaracionvalores_credit_filtered.description, c_pos_declaracionvalores_credit_filtered.c_charge_id, c_pos_declaracionvalores_credit_filtered.chargename, c_pos_declaracionvalores_credit_filtered.doc_id, c_pos_declaracionvalores_credit_filtered.ingreso, c_pos_declaracionvalores_credit_filtered.egreso, c_pos_declaracionvalores_credit_filtered.c_invoice_id, c_pos_declaracionvalores_credit_filtered.invoice_documentno, c_pos_declaracionvalores_credit_filtered.invoice_grandtotal, c_pos_declaracionvalores_credit_filtered.entidadfinanciera_value, c_pos_declaracionvalores_credit_filtered.entidadfinanciera_name, c_pos_declaracionvalores_credit_filtered.bp_entidadfinanciera_value, c_pos_declaracionvalores_credit_filtered.bp_entidadfinanciera_name, c_pos_declaracionvalores_credit_filtered.cupon, c_pos_declaracionvalores_credit_filtered.creditcard, c_pos_declaracionvalores_credit_filtered.generated_invoice_documentno, c_pos_declaracionvalores_credit_filtered.allocation_active, c_pos_declaracionvalores_credit_filtered.c_pos_id, c_pos_declaracionvalores_credit_filtered.posname
   FROM c_pos_declaracionvalores_credit_filtered(ARRAY[(-1)]) c_pos_declaracionvalores_credit_filtered(ad_client_id, ad_org_id, c_posjournal_id, ad_user_id, c_currency_id, datetrx, docstatus, category, tendertype, description, c_charge_id, chargename, doc_id, ingreso, egreso, c_invoice_id, invoice_documentno, invoice_grandtotal, entidadfinanciera_value, entidadfinanciera_name, bp_entidadfinanciera_value, bp_entidadfinanciera_name, cupon, creditcard, generated_invoice_documentno, allocation_active, c_pos_id, posname);

ALTER TABLE c_pos_declaracionvalores_credit
  OWNER TO libertya;

CREATE OR REPLACE VIEW c_pos_declaracionvalores_payments AS 
 SELECT c_pos_declaracionvalores_payments_filtered.ad_client_id, c_pos_declaracionvalores_payments_filtered.ad_org_id, c_pos_declaracionvalores_payments_filtered.c_posjournal_id, c_pos_declaracionvalores_payments_filtered.ad_user_id, c_pos_declaracionvalores_payments_filtered.c_currency_id, c_pos_declaracionvalores_payments_filtered.datetrx, c_pos_declaracionvalores_payments_filtered.docstatus, c_pos_declaracionvalores_payments_filtered.category, c_pos_declaracionvalores_payments_filtered.tendertype, c_pos_declaracionvalores_payments_filtered.description, c_pos_declaracionvalores_payments_filtered.c_charge_id, c_pos_declaracionvalores_payments_filtered.chargename, c_pos_declaracionvalores_payments_filtered.doc_id, c_pos_declaracionvalores_payments_filtered.ingreso, c_pos_declaracionvalores_payments_filtered.egreso, c_pos_declaracionvalores_payments_filtered.c_invoice_id, c_pos_declaracionvalores_payments_filtered.invoice_documentno, c_pos_declaracionvalores_payments_filtered.invoice_grandtotal, c_pos_declaracionvalores_payments_filtered.entidadfinanciera_value, c_pos_declaracionvalores_payments_filtered.entidadfinanciera_name, c_pos_declaracionvalores_payments_filtered.bp_entidadfinanciera_value, c_pos_declaracionvalores_payments_filtered.bp_entidadfinanciera_name, c_pos_declaracionvalores_payments_filtered.cupon, c_pos_declaracionvalores_payments_filtered.creditcard, c_pos_declaracionvalores_payments_filtered.generated_invoice_documentno, c_pos_declaracionvalores_payments_filtered.allocation_active, c_pos_declaracionvalores_payments_filtered.c_pos_id, c_pos_declaracionvalores_payments_filtered.posname
   FROM c_pos_declaracionvalores_payments_filtered(ARRAY[(-1)]) c_pos_declaracionvalores_payments_filtered(ad_client_id, ad_org_id, c_posjournal_id, ad_user_id, c_currency_id, datetrx, docstatus, category, tendertype, description, c_charge_id, chargename, doc_id, ingreso, egreso, c_invoice_id, invoice_documentno, invoice_grandtotal, entidadfinanciera_value, entidadfinanciera_name, bp_entidadfinanciera_value, bp_entidadfinanciera_name, cupon, creditcard, generated_invoice_documentno, allocation_active, c_pos_id, posname);

ALTER TABLE c_pos_declaracionvalores_payments
  OWNER TO libertya;

CREATE OR REPLACE VIEW c_pos_declaracionvalores_ventas AS 
 SELECT c_pos_declaracionvalores_ventas_filtered.ad_client_id, c_pos_declaracionvalores_ventas_filtered.ad_org_id, c_pos_declaracionvalores_ventas_filtered.c_posjournal_id, c_pos_declaracionvalores_ventas_filtered.ad_user_id, c_pos_declaracionvalores_ventas_filtered.c_currency_id, c_pos_declaracionvalores_ventas_filtered.datetrx, c_pos_declaracionvalores_ventas_filtered.docstatus, c_pos_declaracionvalores_ventas_filtered.category, c_pos_declaracionvalores_ventas_filtered.tendertype, c_pos_declaracionvalores_ventas_filtered.description, c_pos_declaracionvalores_ventas_filtered.c_charge_id, c_pos_declaracionvalores_ventas_filtered.chargename, c_pos_declaracionvalores_ventas_filtered.doc_id, c_pos_declaracionvalores_ventas_filtered.ingreso, c_pos_declaracionvalores_ventas_filtered.egreso, c_pos_declaracionvalores_ventas_filtered.c_invoice_id, c_pos_declaracionvalores_ventas_filtered.invoice_documentno, c_pos_declaracionvalores_ventas_filtered.invoice_grandtotal, c_pos_declaracionvalores_ventas_filtered.entidadfinanciera_value, c_pos_declaracionvalores_ventas_filtered.entidadfinanciera_name, c_pos_declaracionvalores_ventas_filtered.bp_entidadfinanciera_value, c_pos_declaracionvalores_ventas_filtered.bp_entidadfinanciera_name, c_pos_declaracionvalores_ventas_filtered.cupon, c_pos_declaracionvalores_ventas_filtered.creditcard, c_pos_declaracionvalores_ventas_filtered.generated_invoice_documentno, c_pos_declaracionvalores_ventas_filtered.allocation_active, c_pos_declaracionvalores_ventas_filtered.c_pos_id, c_pos_declaracionvalores_ventas_filtered.posname
   FROM c_pos_declaracionvalores_ventas_filtered(ARRAY[(-1)]) c_pos_declaracionvalores_ventas_filtered(ad_client_id, ad_org_id, c_posjournal_id, ad_user_id, c_currency_id, datetrx, docstatus, category, tendertype, description, c_charge_id, chargename, doc_id, ingreso, egreso, c_invoice_id, invoice_documentno, invoice_grandtotal, entidadfinanciera_value, entidadfinanciera_name, bp_entidadfinanciera_value, bp_entidadfinanciera_name, cupon, creditcard, generated_invoice_documentno, allocation_active, c_pos_id, posname);

ALTER TABLE c_pos_declaracionvalores_ventas
  OWNER TO libertya;

CREATE OR REPLACE VIEW c_pos_declaracionvalores_voided AS 
 SELECT c_pos_declaracionvalores_voided_filtered.ad_client_id, c_pos_declaracionvalores_voided_filtered.ad_org_id, c_pos_declaracionvalores_voided_filtered.c_posjournal_id, c_pos_declaracionvalores_voided_filtered.ad_user_id, c_pos_declaracionvalores_voided_filtered.c_currency_id, c_pos_declaracionvalores_voided_filtered.datetrx, c_pos_declaracionvalores_voided_filtered.docstatus, c_pos_declaracionvalores_voided_filtered.category, c_pos_declaracionvalores_voided_filtered.tendertype, c_pos_declaracionvalores_voided_filtered.description, c_pos_declaracionvalores_voided_filtered.c_charge_id, c_pos_declaracionvalores_voided_filtered.chargename, c_pos_declaracionvalores_voided_filtered.doc_id, c_pos_declaracionvalores_voided_filtered.ingreso, c_pos_declaracionvalores_voided_filtered.egreso, c_pos_declaracionvalores_voided_filtered.c_invoice_id, c_pos_declaracionvalores_voided_filtered.invoice_documentno, c_pos_declaracionvalores_voided_filtered.invoice_grandtotal, c_pos_declaracionvalores_voided_filtered.entidadfinanciera_value, c_pos_declaracionvalores_voided_filtered.entidadfinanciera_name, c_pos_declaracionvalores_voided_filtered.bp_entidadfinanciera_value, c_pos_declaracionvalores_voided_filtered.bp_entidadfinanciera_name, c_pos_declaracionvalores_voided_filtered.cupon, c_pos_declaracionvalores_voided_filtered.creditcard, c_pos_declaracionvalores_voided_filtered.generated_invoice_documentno, c_pos_declaracionvalores_voided_filtered.allocation_active, c_pos_declaracionvalores_voided_filtered.c_pos_id, c_pos_declaracionvalores_voided_filtered.posname
   FROM c_pos_declaracionvalores_voided_filtered(ARRAY[(-1)]) c_pos_declaracionvalores_voided_filtered(ad_client_id, ad_org_id, c_posjournal_id, ad_user_id, c_currency_id, datetrx, docstatus, category, tendertype, description, c_charge_id, chargename, doc_id, ingreso, egreso, c_invoice_id, invoice_documentno, invoice_grandtotal, entidadfinanciera_value, entidadfinanciera_name, bp_entidadfinanciera_value, bp_entidadfinanciera_name, cupon, creditcard, generated_invoice_documentno, allocation_active, c_pos_id, posname);

ALTER TABLE c_pos_declaracionvalores_voided
  OWNER TO libertya;
  
  
--20180115 Desactivacion de registros a replicar hacia un eventual host con problemas. Por ejemplo sin conectividad
CREATE OR REPLACE FUNCTION delay_records_for_host(p_hostpos int , p_delayvalue varchar)
  RETURNS INTEGER AS
$BODY$
DECLARE
atable VARCHAR;
query VARCHAR;
BEGIN
    FOR atable IN (    SELECT lower(t.tablename)
            FROM ad_tablereplication tr
            INNER JOIN ad_table t ON tr.ad_table_id = t.ad_table_id
            UNION
            SELECT 'ad_changelog_replication' )
    LOOP
        raise notice 'Delay para tabla %', atable;
        query =    ' UPDATE ' || atable ||
            ' SET reparray = ';
        if atable <> 'ad_changelog_replication' then
            query = query || '''SET''||';
        end if;
       
        query = query ||'reparray, includeinreplication = ''' || p_delayvalue || ''' ' ||
                ' WHERE includeinreplication = ''Y'' ' ||
                ' AND (    ( strpos(reparray, ''1'') = ' || p_hostpos || ' AND (CHAR_LENGTH(reparray) - CHAR_LENGTH(REPLACE(reparray, ''1'', ''''))) = 1 )' ||
                '     OR ( strpos(reparray, ''3'') = ' || p_hostpos || ' AND (CHAR_LENGTH(reparray) - CHAR_LENGTH(REPLACE(reparray, ''3'', ''''))) = 1 )' ||
                '     OR ( strpos(reparray, ''A'') = ' || p_hostpos || ' AND (CHAR_LENGTH(reparray) - CHAR_LENGTH(REPLACE(reparray, ''A'', ''''))) = 1 )' ||
                '     OR ( strpos(reparray, ''a'') = ' || p_hostpos || ' AND (CHAR_LENGTH(reparray) - CHAR_LENGTH(REPLACE(reparray, ''a'', ''''))) = 1 ) ) ';
        --raise notice '%', query;
        EXECUTE query;
    END LOOP;

    return 0;

END
$BODY$
  LANGUAGE 'plpgsql' VOLATILE;
  
-- Reactivacion de registros desactivados
CREATE OR REPLACE FUNCTION undelay_records_for_host(p_delayvalue varchar)
  RETURNS INTEGER AS
$BODY$
DECLARE
atable VARCHAR;
query VARCHAR;
BEGIN
    FOR atable IN (    SELECT lower(t.tablename)
            FROM ad_tablereplication tr
            INNER JOIN ad_table t ON tr.ad_table_id = t.ad_table_id
            UNION
            SELECT 'ad_changelog_replication' )
    LOOP
        raise notice 'Undelay para tabla %', atable;
        query =    ' UPDATE ' || atable ||
            ' SET reparray = ';
        if atable <> 'ad_changelog_replication' then
            query = query || '''SET''||';
        end if;
       
        query = query ||'reparray, includeinreplication = ''Y'' ' ||
                ' WHERE includeinreplication = ''' || p_delayvalue || ''' ';
        --raise notice '%', query;
        EXECUTE query;
    END LOOP;

    return 0;

END
$BODY$
  LANGUAGE 'plpgsql' VOLATILE;
  
--20180119 Nuevo indice remito -> factura, el cual es necesario para agilizar los tiempos en la impresion de facturas
update ad_system set dummy = (SELECT addindexifnotexists('m_inout_invoice','m_inout','c_invoice_id'));

--20180124-0900 Constraints para la tabla de configuración de corrección de cobranza
ALTER TABLE c_payment_recovery_config ADD CONSTRAINT payment_recovery_config_client FOREIGN KEY (ad_client_id)
      REFERENCES ad_client (ad_client_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE c_payment_recovery_config ADD CONSTRAINT payment_recovery_config_org FOREIGN KEY (ad_org_id)
      REFERENCES ad_org (ad_org_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE c_payment_recovery_config ADD CONSTRAINT payment_recovery_config_doctype_recovery FOREIGN KEY (c_doctype_recovery_id)
      REFERENCES c_doctype (c_doctype_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE c_payment_recovery_config ADD CONSTRAINT payment_recovery_config_doctype_credit_recovery FOREIGN KEY (c_doctype_credit_recovery_id)
      REFERENCES c_doctype (c_doctype_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE c_payment_recovery_config ADD CONSTRAINT payment_recovery_config_doctype_rejected FOREIGN KEY (c_doctype_rejected_id)
      REFERENCES c_doctype (c_doctype_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE c_payment_recovery_config ADD CONSTRAINT payment_recovery_config_doctype_credit_rejected FOREIGN KEY (c_doctype_credit_rejected_id)
      REFERENCES c_doctype (c_doctype_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE c_payment_recovery_config ADD CONSTRAINT payment_recovery_config_product_recovery FOREIGN KEY (m_product_recovery_id)
      REFERENCES m_product (m_product_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE c_payment_recovery_config ADD CONSTRAINT payment_recovery_config_product_rejected FOREIGN KEY (m_product_rejected_id)
      REFERENCES m_product (m_product_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
      
--20180124-1056 Las preferencias DIF_CAMBIO_ARTICULO y DIF_CAMBIO_PTO_VENTA estaban con compañía System en lugar de Libertya
update ad_preference set ad_client_id = 1010016 where ad_componentobjectuid in ('CORE-AD_Preference-1011156', 'CORE-AD_Preference-1011157');

--20180130-1542 Factura electronica. Codigos de paises faltantes.  Segun definicion AFIP.
update c_country set countrycodefe = '301' where ad_componentobjectuid = 'CORE-C_Country-110';
update c_country set countrycodefe = '401' where ad_componentobjectuid = 'CORE-C_Country-114';
update c_country set countrycodefe = '149' where ad_componentobjectuid = 'CORE-C_Country-115';
update c_country set countrycodefe = '265' where ad_componentobjectuid = 'CORE-C_Country-117';
update c_country set countrycodefe = '237' where ad_componentobjectuid = 'CORE-C_Country-118';
update c_country set countrycodefe = '349' where ad_componentobjectuid = 'CORE-C_Country-120';
update c_country set countrycodefe = '501' where ad_componentobjectuid = 'CORE-C_Country-122';
update c_country set countrycodefe = '405' where ad_componentobjectuid = 'CORE-C_Country-108';
update c_country set countrycodefe = '350' where ad_componentobjectuid = 'CORE-C_Country-124';
update c_country set countrycodefe = '239' where ad_componentobjectuid = 'CORE-C_Country-125';
update c_country set countrycodefe = '303' where ad_componentobjectuid = 'CORE-C_Country-126';
update c_country set countrycodefe = '345' where ad_componentobjectuid = 'CORE-C_Country-127';
update c_country set countrycodefe = '201' where ad_componentobjectuid = 'CORE-C_Country-128';
update c_country set countrycodefe = '439' where ad_componentobjectuid = 'CORE-C_Country-129';
update c_country set countrycodefe = '236' where ad_componentobjectuid = 'CORE-C_Country-131';
update c_country set countrycodefe = '112' where ad_componentobjectuid = 'CORE-C_Country-132';
update c_country set countrycodefe = '305' where ad_componentobjectuid = 'CORE-C_Country-134';
update c_country set countrycodefe = '446' where ad_componentobjectuid = 'CORE-C_Country-136';
update c_country set countrycodefe = '103' where ad_componentobjectuid = 'CORE-C_Country-137';
update c_country set countrycodefe = '346' where ad_componentobjectuid = 'CORE-C_Country-141';
update c_country set countrycodefe = '101' where ad_componentobjectuid = 'CORE-C_Country-143';
update c_country set countrycodefe = '104' where ad_componentobjectuid = 'CORE-C_Country-144';
update c_country set countrycodefe = '306' where ad_componentobjectuid = 'CORE-C_Country-145';
update c_country set countrycodefe = '105' where ad_componentobjectuid = 'CORE-C_Country-146';
update c_country set countrycodefe = '150' where ad_componentobjectuid = 'CORE-C_Country-148';
update c_country set countrycodefe = '107' where ad_componentobjectuid = 'CORE-C_Country-150';
update c_country set countrycodefe = '111' where ad_componentobjectuid = 'CORE-C_Country-151';
update c_country set countrycodefe = '155' where ad_componentobjectuid = 'CORE-C_Country-157';
update c_country set countrycodefe = '108' where ad_componentobjectuid = 'CORE-C_Country-158';
update c_country set countrycodefe = '109' where ad_componentobjectuid = 'CORE-C_Country-159';
update c_country set countrycodefe = '110' where ad_componentobjectuid = 'CORE-C_Country-162';
update c_country set countrycodefe = '447' where ad_componentobjectuid = 'CORE-C_Country-163';
update c_country set countrycodefe = '435' where ad_componentobjectuid = 'CORE-C_Country-165';
update c_country set countrycodefe = '451' where ad_componentobjectuid = 'CORE-C_Country-166';
update c_country set countrycodefe = '409' where ad_componentobjectuid = 'CORE-C_Country-167';
update c_country set countrycodefe = '153' where ad_componentobjectuid = 'CORE-C_Country-168';
update c_country set countrycodefe = '233' where ad_componentobjectuid = 'CORE-C_Country-169';
update c_country set countrycodefe = '209' where ad_componentobjectuid = 'CORE-C_Country-170';
update c_country set countrycodefe = '113' where ad_componentobjectuid = 'CORE-C_Country-172';
update c_country set countrycodefe = '119' where ad_componentobjectuid = 'CORE-C_Country-174';
update c_country set countrycodefe = '160' where ad_componentobjectuid = 'CORE-C_Country-175';
update c_country set countrycodefe = '440' where ad_componentobjectuid = 'CORE-C_Country-176';
update c_country set countrycodefe = '161' where ad_componentobjectuid = 'CORE-C_Country-177';
update c_country set countrycodefe = '254' where ad_componentobjectuid = 'CORE-C_Country-178';
update c_country set countrycodefe = '512' where ad_componentobjectuid = 'CORE-C_Country-180';
update c_country set countrycodefe = '411' where ad_componentobjectuid = 'CORE-C_Country-181';
update c_country set countrycodefe = '115' where ad_componentobjectuid = 'CORE-C_Country-186';
update c_country set countrycodefe = '116' where ad_componentobjectuid = 'CORE-C_Country-187';
update c_country set countrycodefe = '438' where ad_componentobjectuid = 'CORE-C_Country-101';
update c_country set countrycodefe = '117' where ad_componentobjectuid = 'CORE-C_Country-190';
update c_country set countrycodefe = '240' where ad_componentobjectuid = 'CORE-C_Country-194';
update c_country set countrycodefe = '118' where ad_componentobjectuid = 'CORE-C_Country-198';
update c_country set countrycodefe = '156' where ad_componentobjectuid = 'CORE-C_Country-199';
update c_country set countrycodefe = '431' where ad_componentobjectuid = 'CORE-C_Country-203';
update c_country set countrycodefe = '341' where ad_componentobjectuid = 'CORE-C_Country-205';
update c_country set countrycodefe = '414' where ad_componentobjectuid = 'CORE-C_Country-206';
update c_country set countrycodefe = '416' where ad_componentobjectuid = 'CORE-C_Country-207';
update c_country set countrycodefe = '316' where ad_componentobjectuid = 'CORE-C_Country-209';
update c_country set countrycodefe = '318' where ad_componentobjectuid = 'CORE-C_Country-210';
update c_country set countrycodefe = '317' where ad_componentobjectuid = 'CORE-C_Country-211';
update c_country set countrycodefe = '319' where ad_componentobjectuid = 'CORE-C_Country-213';
update c_country set countrycodefe = '321' where ad_componentobjectuid = 'CORE-C_Country-217';
update c_country set countrycodefe = '352' where ad_componentobjectuid = 'CORE-C_Country-218';
update c_country set countrycodefe = '120' where ad_componentobjectuid = 'CORE-C_Country-219';
update c_country set countrycodefe = '514' where ad_componentobjectuid = 'CORE-C_Country-220';
update c_country set countrycodefe = '308' where ad_componentobjectuid = 'CORE-C_Country-221';
update c_country set countrycodefe = '309' where ad_componentobjectuid = 'CORE-C_Country-222';
update c_country set countrycodefe = '323' where ad_componentobjectuid = 'CORE-C_Country-223';
update c_country set countrycodefe = '353' where ad_componentobjectuid = 'CORE-C_Country-224';
update c_country set countrycodefe = '324' where ad_componentobjectuid = 'CORE-C_Country-225';
update c_country set countrycodefe = '441' where ad_componentobjectuid = 'CORE-C_Country-226';
update c_country set countrycodefe = '325' where ad_componentobjectuid = 'CORE-C_Country-227';
update c_country set countrycodefe = '121' where ad_componentobjectuid = 'CORE-C_Country-228';
update c_country set countrycodefe = '122' where ad_componentobjectuid = 'CORE-C_Country-229';
update c_country set countrycodefe = '123' where ad_componentobjectuid = 'CORE-C_Country-230';
update c_country set countrycodefe = '418' where ad_componentobjectuid = 'CORE-C_Country-231';
update c_country set countrycodefe = '442' where ad_componentobjectuid = 'CORE-C_Country-232';
update c_country set countrycodefe = '419' where ad_componentobjectuid = 'CORE-C_Country-233';
update c_country set countrycodefe = '344' where ad_componentobjectuid = 'CORE-C_Country-234';
update c_country set countrycodefe = '450' where ad_componentobjectuid = 'CORE-C_Country-235';
update c_country set countrycodefe = '124' where ad_componentobjectuid = 'CORE-C_Country-236';
update c_country set countrycodefe = '125' where ad_componentobjectuid = 'CORE-C_Country-237';
update c_country set countrycodefe = '326' where ad_componentobjectuid = 'CORE-C_Country-238';
update c_country set countrycodefe = '327' where ad_componentobjectuid = 'CORE-C_Country-239';
update c_country set countrycodefe = '126' where ad_componentobjectuid = 'CORE-C_Country-240';
update c_country set countrycodefe = '420' where ad_componentobjectuid = 'CORE-C_Country-241';
update c_country set countrycodefe = '520' where ad_componentobjectuid = 'CORE-C_Country-242';
update c_country set countrycodefe = '129' where ad_componentobjectuid = 'CORE-C_Country-244';
update c_country set countrycodefe = '515' where ad_componentobjectuid = 'CORE-C_Country-248';
update c_country set countrycodefe = '443' where ad_componentobjectuid = 'CORE-C_Country-249';
update c_country set countrycodefe = '421' where ad_componentobjectuid = 'CORE-C_Country-250';
update c_country set countrycodefe = '329' where ad_componentobjectuid = 'CORE-C_Country-251';
update c_country set countrycodefe = '127' where ad_componentobjectuid = 'CORE-C_Country-253';
update c_country set countrycodefe = '151' where ad_componentobjectuid = 'CORE-C_Country-254';
update c_country set countrycodefe = '304' where ad_componentobjectuid = 'CORE-C_Country-255';
update c_country set countrycodefe = '158' where ad_componentobjectuid = 'CORE-C_Country-256';
update c_country set countrycodefe = '503' where ad_componentobjectuid = 'CORE-C_Country-257';
update c_country set countrycodefe = '423' where ad_componentobjectuid = 'CORE-C_Country-105';
update c_country set countrycodefe = '330' where ad_componentobjectuid = 'CORE-C_Country-258';
update c_country set countrycodefe = '241' where ad_componentobjectuid = 'CORE-C_Country-260';
update c_country set countrycodefe = '504' where ad_componentobjectuid = 'CORE-C_Country-262';
update c_country set countrycodefe = '219' where ad_componentobjectuid = 'CORE-C_Country-263';
update c_country set countrycodefe = '130' where ad_componentobjectuid = 'CORE-C_Country-264';
update c_country set countrycodefe = '131' where ad_componentobjectuid = 'CORE-C_Country-265';
update c_country set countrycodefe = '521' where ad_componentobjectuid = 'CORE-C_Country-268';
update c_country set countrycodefe = '422' where ad_componentobjectuid = 'CORE-C_Country-269';
update c_country set countrycodefe = '328' where ad_componentobjectuid = 'CORE-C_Country-270';
update c_country set countrycodefe = '332' where ad_componentobjectuid = 'CORE-C_Country-271';
update c_country set countrycodefe = '516' where ad_componentobjectuid = 'CORE-C_Country-272';
update c_country set countrycodefe = '357' where ad_componentobjectuid = 'CORE-C_Country-273';
update c_country set countrycodefe = '513' where ad_componentobjectuid = 'CORE-C_Country-275';
update c_country set countrycodefe = '312' where ad_componentobjectuid = 'CORE-C_Country-278';
update c_country set countrycodefe = '322' where ad_componentobjectuid = 'CORE-C_Country-283';
update c_country set countrycodefe = '427' where ad_componentobjectuid = 'CORE-C_Country-285';
update c_country set countrycodefe = '133' where ad_componentobjectuid = 'CORE-C_Country-287';
update c_country set countrycodefe = '234' where ad_componentobjectuid = 'CORE-C_Country-290';
update c_country set countrycodefe = '235' where ad_componentobjectuid = 'CORE-C_Country-292';
update c_country set countrycodefe = '506' where ad_componentobjectuid = 'CORE-C_Country-293';
update c_country set countrycodefe = '428' where ad_componentobjectuid = 'CORE-C_Country-294';
update c_country set countrycodefe = '157' where ad_componentobjectuid = 'CORE-C_Country-295';
update c_country set countrycodefe = '302' where ad_componentobjectuid = 'CORE-C_Country-296';
update c_country set countrycodefe = '134' where ad_componentobjectuid = 'CORE-C_Country-297';
update c_country set countrycodefe = '152' where ad_componentobjectuid = 'CORE-C_Country-298';
update c_country set countrycodefe = '135' where ad_componentobjectuid = 'CORE-C_Country-299';
update c_country set countrycodefe = '333' where ad_componentobjectuid = 'CORE-C_Country-300';
update c_country set countrycodefe = '448' where ad_componentobjectuid = 'CORE-C_Country-301';
update c_country set countrycodefe = '449' where ad_componentobjectuid = 'CORE-C_Country-302';
update c_country set countrycodefe = '136' where ad_componentobjectuid = 'CORE-C_Country-304';
update c_country set countrycodefe = '159' where ad_componentobjectuid = 'CORE-C_Country-305';
update c_country set countrycodefe = '307' where ad_componentobjectuid = 'CORE-C_Country-308';
update c_country set countrycodefe = '138' where ad_componentobjectuid = 'CORE-C_Country-309';
update c_country set countrycodefe = '232' where ad_componentobjectuid = 'CORE-C_Country-310';
update c_country set countrycodefe = '137' where ad_componentobjectuid = 'CORE-C_Country-312';
update c_country set countrycodefe = '430' where ad_componentobjectuid = 'CORE-C_Country-107';
update c_country set countrycodefe = '334' where ad_componentobjectuid = 'CORE-C_Country-315';
update c_country set countrycodefe = '354' where ad_componentobjectuid = 'CORE-C_Country-317';
update c_country set countrycodefe = '139' where ad_componentobjectuid = 'CORE-C_Country-318';
update c_country set countrycodefe = '335' where ad_componentobjectuid = 'CORE-C_Country-319';
update c_country set countrycodefe = '140' where ad_componentobjectuid = 'CORE-C_Country-321';
update c_country set countrycodefe = '519' where ad_componentobjectuid = 'CORE-C_Country-323';
update c_country set countrycodefe = '141' where ad_componentobjectuid = 'CORE-C_Country-325';
update c_country set countrycodefe = '355' where ad_componentobjectuid = 'CORE-C_Country-327';
update c_country set countrycodefe = '517' where ad_componentobjectuid = 'CORE-C_Country-329';
update c_country set countrycodefe = '142' where ad_componentobjectuid = 'CORE-C_Country-330';
update c_country set countrycodefe = '331' where ad_componentobjectuid = 'CORE-C_Country-332';
update c_country set countrycodefe = '426' where ad_componentobjectuid = 'CORE-C_Country-333';
update c_country set countrycodefe = '356' where ad_componentobjectuid = 'CORE-C_Country-337';
update c_country set countrycodefe = '505' where ad_componentobjectuid = 'CORE-C_Country-338';
update c_country set countrycodefe = '337' where ad_componentobjectuid = 'CORE-C_Country-340';
update c_country set countrycodefe = '348' where ad_componentobjectuid = 'CORE-C_Country-345';
update c_country set countrycodefe = '144' where ad_componentobjectuid = 'CORE-C_Country-347';
update c_country set countrycodefe = '132' where ad_componentobjectuid = 'CORE-C_Country-348';

--20180131-1000 Codigos de monedas adicionales
update c_currency set wsfecode = '014' where ad_componentobjectuid = 'CORE-C_Currency-241';
update c_currency set wsfecode = '059' where ad_componentobjectuid = 'CORE-C_Currency-282';
update c_currency set wsfecode = '023' where ad_componentobjectuid = 'CORE-C_Currency-205';
update c_currency set wsfecode = '026' where ad_componentobjectuid = 'CORE-C_Currency-120';
update c_currency set wsfecode = '031' where ad_componentobjectuid = 'CORE-C_Currency-146';
update c_currency set wsfecode = '035' where ad_componentobjectuid = 'CORE-C_Currency-308';
update c_currency set wsfecode = '042' where ad_componentobjectuid = 'CORE-C_Currency-298';
update c_currency set wsfecode = '009' where ad_componentobjectuid = 'CORE-C_Currency-318';

--20180202-1207 Relacionados con merge r2303 y 2304

-- Columna agregada para el Detalle de Cuentas (CORE)
alter table T_Acct_Detail add column origin_tableName varchar(150);
alter table T_Acct_Detail add column procedence_id integer;

-- Agregado de columna para el campo cuenta contable (CORE)
alter table C_Payment add column accounting_c_charge_id integer;
alter table C_BankTransfer add column accounting_c_charge_id integer;
alter table C_CreditCardSettlement add column accounting_c_charge_id integer;
alter table C_CashLine add column accounting_c_charge_id integer;

-- Actualizacion de las descripciones de registros contables
-- Se modificarán para liquidaciones de tarj de crédito, facturas, OP/RC y extractos

-- update description on c_creditCardSettlement
update fact_acct fa set description = (
    select settlementNo || ' ' || description from C_CreditCardSettlement ccs where ccs.C_CreditCardSettlement_ID = fa.record_id) 
where ad_table_id = (select ad_table_id from ad_table where tablename = 'C_CreditCardSettlement') and ad_client_id = 1010016;

-- update description on c_invoice
update fact_acct fa set description = (
    select i.documentNo || coalesce(' #' || to_char(l.line,'99999'), '') || coalesce(' (' || l.description || ')' ,'')
    from C_Invoice i  
    left join C_invoiceLine l on (i.c_invoice_id = l.c_invoice_id) 
    where i.C_Invoice_ID = fa.record_id and l.c_invoiceLine_id = fa.line_id) 
where ad_table_id = (select ad_table_id from ad_table where tablename = 'C_Invoice') and ad_client_id = 1010016;

-- update description on c_bankStatement
update fact_acct fa set description = (
    select bs.name || coalesce(' #' || to_char(bsl.line, '99999'), '') || coalesce(' (' || bsl.description || ')' ,'')
    from c_bankStatement bs  
    left join C_bankStatementLine bsl on (bs.c_bankStatement_id = bsl.c_bankStatement_id) 
    where bs.c_bankStatement_ID = fa.record_id and bsl.c_bankStatement_id = fa.line_id)
where ad_table_id = (select ad_table_id from ad_table where tablename = 'C_BankStatement') and ad_client_id = 1010016;

-- update description on allocationHdr
update fact_acct fa set description = (
    select a.documentNo || coalesce(' #' || to_char(al.allocationNo, '99999'), '') || coalesce(' (' || al.line_description || ')','')
    from c_allocationHdr a  
    left join C_allocationLine al on (a.c_allocationHdr_id = al.c_allocationHdr_id)
    where a.c_allocationHdr_ID = fa.record_id and al.c_allocationLine_id = fa.line_id)
where ad_table_id = (select ad_table_id from ad_table where tablename = 'C_AllocationHdr') and ad_client_id = 1010016;

-- El campo Record_ID de la tabla AD_Attachment debe ser de tipo referencia (18), no un boton (28) 
update ad_column set ad_reference_id = 18 where ad_componentobjectuid = 'CORE-AD_Column-2097';

--20180223-1830 Incorporación de líneas de factura y de pedido a la estructura de descuentos de comprobantes
update ad_system set dummy = (SELECT addcolumnifnotexists('C_DocumentDiscount','c_orderline_id','integer'));
update ad_system set dummy = (SELECT addcolumnifnotexists('C_DocumentDiscount','c_invoiceline_id','integer'));

ALTER TABLE C_DocumentDiscount ADD CONSTRAINT cinvoiceline_cdocumentdiscount FOREIGN KEY (c_invoiceline_id)
  REFERENCES c_invoiceline (c_invoiceline_id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE CASCADE;
ALTER TABLE C_DocumentDiscount ADD CONSTRAINT corderline_cdocumentdiscount FOREIGN KEY (c_orderline_id)
  REFERENCES c_orderline (c_orderline_id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE CASCADE;

--20180310-2045 Nueva columna de configuración de descuento de corte por Línea de Artículo
update ad_system set dummy = (SELECT addcolumnifnotexists('M_DiscountSchemaBreak','m_product_lines_id','integer'));

--20180315-1403 Merge DT 728469
DROP VIEW rv_orderline_pending;
CREATE OR REPLACE VIEW rv_orderline_pending AS 
 SELECT o.ad_client_id, o.ad_org_id, o.isactive, o.created, o.createdby, o.updated, o.updatedby, o.c_order_id, o.documentno, o.dateordered::date AS dateordered, o.datepromised::date AS datepromised, o.c_bpartner_id, o.issotrx, ol.c_orderline_id, ol.m_product_id, ol.qtyordered, ol.qtyinvoiced, ol.qtydelivered, ol.qtyordered - ol.qtyinvoiced AS pendinginvoice, ol.qtyordered - ol.qtydelivered AS pendingdeliver, 
        CASE
            WHEN ol.qtyordered <> ol.qtyinvoiced AND ol.qtyordered <> ol.qtydelivered THEN 'ID'::text
            WHEN ol.qtyordered <> ol.qtyinvoiced AND ol.qtyordered = ol.qtydelivered THEN 'I'::text
            WHEN ol.qtyordered <> ol.qtydelivered THEN 'D'::text
            ELSE 'N'::text
        END AS status
   FROM c_order o
   JOIN c_orderline ol ON o.c_order_id = ol.c_order_id AND (ol.qtyordered <> ol.qtydelivered OR ol.qtyordered <> ol.qtyinvoiced) AND (o.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) AND ol.m_product_id IS NOT NULL
  ORDER BY o.c_order_id;

ALTER TABLE rv_orderline_pending
  OWNER TO libertya;

--20180316-1200 Merge DT 728469. Adecuaciones necesarias a fin de corizar desarrollo y evitar pisado de clases OP
update ad_system set dummy = (SELECT addcolumnifnotexists('AD_Role','paymentmedium','character(1)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('AD_Role','paymentmediumlimit','numeric(20)'));

--20180328-1156 Los procesadores contables de Wide Systems y WideFast-Track por defecto deben estar deshabilitados
update c_acctprocessor set isactive = 'N' where ad_componentobjectuid in ('CORE-C_AcctProcessor-1000008', 'CORE-C_AcctProcessor-1000005');

--20180405-1040 Merge r2345 y r2348
CREATE VIEW saldosOPA_v AS
SELECT sb.ad_client_id, sb.ad_org_id, sb.documentno, sb.c_bpartner_id, sb.datetrx, sb.grandtotal, (sb.grandtotal - ((sb.cashlineavailable + sb.paymentavailable) + sb.invoiceopen)) AS imputado, ((sb.cashlineavailable + sb.paymentavailable) + sb.invoiceopen) AS saldo FROM (SELECT a.ad_client_id, a.ad_org_id, a.documentno, a.c_bpartner_id, (a.datetrx)::date AS datetrx, a.grandtotal, sum(CASE WHEN (al.c_cashline_id IS NOT NULL) THEN abs(cashlineavailable(al.c_cashline_id)) ELSE (0)::numeric END) AS cashlineavailable, sum(CASE WHEN (al.c_payment_id IS NOT NULL) THEN paymentavailable(al.c_payment_id) ELSE (0)::numeric END) AS paymentavailable, sum(CASE WHEN (al.c_invoice_credit_id IS NOT NULL) THEN invoiceopen(al.c_invoice_credit_id, 0) ELSE (0)::numeric END) AS invoiceopen FROM (c_allocationhdr a JOIN c_allocationline al ON ((al.c_allocationhdr_id = a.c_allocationhdr_id))) WHERE ((((a.allocationtype)::text = 'OPA'::text) AND (((cashlineavailable(al.c_cashline_id) < (0)::numeric) OR (paymentavailable(al.c_payment_id) > (0)::numeric)) OR (invoiceopen(al.c_invoice_credit_id, 0) > (0)::numeric))) AND (a.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar]))) GROUP BY a.ad_client_id, a.ad_org_id, a.documentno, a.c_bpartner_id, a.datetrx, a.grandtotal) sb ORDER BY sb.documentno;

ALTER TABLE C_BPartner ADD COLUMN paymentblocked character(1);
ALTER TABLE C_BPartner ADD COLUMN paymentblockeddescr character varying(255);
ALTER TABLE M_Product ADD COLUMN marketingblocked character(1);
ALTER TABLE M_Product ADD COLUMN marketingblockeddescr character varying(255);

--20180411-1408 Merge relacionado con r2354
update ad_field set isreadonly = 'N' where ad_componentobjectuid = 'HTCA2CORE-AD_Field-1019910-20180405102350';
update ad_field set isreadonly = 'N' where ad_componentobjectuid = 'HTCA2CORE-AD_Field-1019911-20180405102403';

ALTER TABLE C_BPartner ALTER COLUMN paymentBlocked SET DEFAULT 'N';
UPDATE C_BPartner SET paymentBlocked='N' WHERE paymentBlocked IS NULL;
ALTER TABLE C_BPartner ALTER COLUMN paymentBlocked SET NOT NULL;

ALTER TABLE M_Product ALTER COLUMN marketingBlocked SET DEFAULT 'N';
UPDATE M_Product SET marketingBlocked='N' WHERE marketingBlocked IS NULL;
ALTER TABLE M_Product ALTER COLUMN marketingBlocked SET NOT NULL;

--20180411-1910 Parametrización que permite discriminar el arrastre de descuentos y recargos
update ad_system set dummy = (SELECT addcolumnifnotexists('C_DocType','dragorderlinesurcharges','character(1) NOT NULL DEFAULT ''N''::bpchar'));
update ad_system set dummy = (SELECT addcolumnifnotexists('C_DocType','dragorderdocumentsurcharges','character(1) NOT NULL DEFAULT ''N''::bpchar'));
update ad_system set dummy = (SELECT addcolumnifnotexists('C_Invoice','managedragordersurcharges','character(1) NOT NULL DEFAULT ''N''::bpchar'));

UPDATE C_DocType
SET dragorderlinesurcharges = 'Y'
WHERE dragorderlinediscounts = 'Y';

UPDATE C_DocType
SET dragorderdocumentsurcharges = 'Y'
WHERE dragorderdocumentdiscounts = 'Y';

UPDATE C_Invoice
SET managedragordersurcharges = 'Y'
WHERE managedragorderdiscounts = 'Y';

--20180418-1420 Acceso a acciones de documento por perfil
CREATE TABLE AD_Document_Action_Access
(
ad_document_action_access_id integer NOT NULL,
ad_role_id integer NOT NULL,
c_doctype_id integer NOT NULL,
ad_ref_list_id integer NOT NULL,
ad_client_id integer NOT NULL,
ad_org_id integer NOT NULL,
isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
createdby integer NOT NULL,
updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
updatedby integer NOT NULL,
ad_componentobjectuid character varying(100),
ad_componentversion_id integer,
CONSTRAINT ad_document_action_access_key PRIMARY KEY (ad_document_action_access_id),
CONSTRAINT ad_document_action_role_access_fk FOREIGN KEY (ad_role_id)
REFERENCES ad_role (ad_role_id) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE CASCADE,
CONSTRAINT ad_document_action_doctype_access_fk FOREIGN KEY (c_doctype_id)
REFERENCES c_doctype (c_doctype_id) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE CASCADE,
CONSTRAINT ad_document_action_reflist_access_fk FOREIGN KEY (ad_ref_list_id)
REFERENCES ad_ref_list (ad_ref_list_id) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS=TRUE
);
ALTER TABLE AD_Document_Action_Access
OWNER TO libertya;

--Estado de documento Revertido para comprobantes de compras en estado anulado
update c_invoice
set docstatus = 'RE'
where ad_client_id = 1010016 and issotrx = 'N' and docstatus = 'VO';

--20180425-1135 Merge relacionado con r2361 
update ad_system set dummy = (SELECT addcolumnifnotexists('GL_JournalBatch', 'IsReActivated', 'character(1)'));

--20180427-1030 Fix de cashlines en la funview v_documents_org_filtered
CREATE OR REPLACE FUNCTION v_documents_org_filtered(
    bpartner integer,
    summaryonly boolean,
    condition character,
    dateto timestamp without time zone)
  RETURNS SETOF v_documents_org_type_condition AS
$BODY$
declare
    consulta varchar;
    orderby1 varchar;
    orderby2 varchar;
    orderby3 varchar;
    leftjoin1 varchar;
    leftjoin2 varchar;
    advancedcondition varchar;
    advancedconditioncashlineexception varchar;
    whereclauseConditionDebit varchar;
    whereclauseConditionCredit varchar;
    whereclauseConditionCreditCashException varchar;
    whereclauseDateTo varchar;
    selectallocationNull varchar;
    selectallocationPayment varchar;
    selectallocationCashline varchar;
    selectallocationCredit varchar;
    selectAllocationReferencePayment varchar;
    selectAllocationReferenceCashline varchar;
    selectAllocationReferenceCredit varchar;
    adocument v_documents_org_type_condition;
   
BEGIN
    whereclauseDateTo = ' ( 1 = 1 ) ';
    -- Armar la condición para fecha de corte
    if dateTo is not null then 
		whereclauseDateTo = ' dateacct::date <= ''' || dateTo || '''::date ';
    end if;
    
    --Si no se deben mostrar todos, entonces agregar la condicion por la forma de pago
    if condition <> 'A' then
		--Si se debe mostrar sólo efectivo, entonces no se debe mostrar los anticipos, si o si debe tener una factura asociada
		advancedcondition = 'il.paymentrule is null OR ';
		advancedconditioncashlineexception = ' (1=1) ';
		if condition = 'B' then
			advancedcondition = '';
			advancedconditioncashlineexception = ' (1=2) ';
		end if;
		whereclauseConditionDebit = ' (i.paymentrule = ''' || condition || ''') ';
		whereclauseConditionCredit = ' (' || advancedcondition || ' il.paymentrule = ''' || condition || ''') ';
		whereclauseConditionCreditCashException = '( CASE WHEN il.paymentrule is not null THEN il.paymentrule = ''' || condition || ''' WHEN ic.paymentrule is not null THEN ic.paymentrule = ''' || condition || ''' ELSE '|| advancedconditioncashlineexception ||' END ) ';
	else
		whereclauseConditionDebit = ' (1 = 1) ';
		whereclauseConditionCredit = ' (1 = 1) ';
		whereclauseConditionCreditCashException = ' (1 = 1) '; 
    end if;    

    -- recuperar informacion minima indispensable si summaryonly es true.  en caso de ser false, debe joinearse/ordenarse, etc.
    if summaryonly = false then

        orderby1 = ' ORDER BY ''C_Invoice''::text, i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name, dt.printname, i.documentno, i.issotrx, i.docstatus,
                 CASE
                     WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
                     ELSE i.dateinvoiced
                 END, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced, bp.socreditstatus ';

        orderby2 = ' ORDER BY ''C_Payment''::text, p.c_payment_id, p.ad_client_id, COALESCE(il.ad_org_id, p.ad_org_id), p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name, dt.printname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt, NULL::integer, p.duedate, bp.socreditstatus ';

        orderby3 = ' ORDER BY ''C_CashLine''::text, cl.c_cashline_id, cl.ad_client_id, COALESCE(il.ad_org_id, ic.ad_org_id, cl.ad_org_id), cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby,
                CASE
                    WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id
		    WHEN il.c_bpartner_id IS NOT NULL THEN il.c_bpartner_id
                    ELSE ic.c_bpartner_id
                END, dt.c_doctype_id,
                CASE
                    WHEN cl.amount < 0.0 THEN 1
                    ELSE (-1)
                END, dt.name, dt.printname, ''@line@''::text || cl.line::character varying::text,
                CASE
                    WHEN cl.amount < 0.0 THEN ''N''::bpchar
                    ELSE ''Y''::bpchar
                END, cl.docstatus, c.statementdate, c.dateacct, cl.c_currency_id, NULL::integer, abs(cl.amount), NULL::timestamp without time zone, COALESCE(bp.socreditstatus, bp2.socreditstatus, bp3.socreditstatus) ';
	
		selectallocationNull = ' NULL::integer ';
		selectallocationPayment = selectallocationNull;
		selectallocationCashline = selectallocationNull;
		selectallocationCredit = selectallocationNull;
	
    else
        orderby1 = '';
        orderby2 = '';
        orderby3 = '';

		selectAllocationReferencePayment = ' al.c_payment_id = p.c_payment_id ';
		selectAllocationReferenceCashline = ' al.c_cashline_id = cl.c_cashline_id ';
		selectAllocationReferenceCredit = ' al.c_invoice_credit_id = i.c_invoice_id ';
	
		selectallocationPayment = ' (SELECT ah.c_allocationhdr_id FROM c_allocationline al INNER JOIN c_allocationhdr ah on ah.c_allocationhdr_id = al.c_allocationhdr_id WHERE allocationtype <> ''MAN'' AND ah.dateacct::date = p.dateacct::date AND ' || selectAllocationReferencePayment || ' AND ' || whereclauseDateTo || ' ORDER BY ah.created LIMIT 1) as c_allocationhdr_id ';
		selectallocationCashline = ' (SELECT ah.c_allocationhdr_id FROM c_allocationline al INNER JOIN c_allocationhdr ah on ah.c_allocationhdr_id = al.c_allocationhdr_id WHERE allocationtype <> ''MAN'' AND ah.dateacct::date = c.dateacct::date AND ' || selectAllocationReferenceCashline || ' AND ' || whereclauseDateTo || ' ORDER BY ah.created LIMIT 1) as c_allocationhdr_id ';
		selectallocationCredit = ' (SELECT ah.c_allocationhdr_id FROM c_allocationline al INNER JOIN c_allocationhdr ah on ah.c_allocationhdr_id = al.c_allocationhdr_id WHERE allocationtype <> ''MAN'' AND ah.dateacct::date = i.dateacct::date AND ' || selectAllocationReferenceCredit || ' AND ' || whereclauseDateTo || ' ORDER BY ah.created LIMIT 1) as c_allocationhdr_id ';
	
    end if;    

    consulta = ' SELECT * FROM 

        (        ( SELECT DISTINCT ''C_Invoice''::text AS documenttable, i.c_invoice_id AS document_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, i.documentno, i.issotrx, i.docstatus,
                        CASE
                            WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
                            ELSE i.dateinvoiced
                        END AS datetrx, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal AS amount, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced AS truedatetrx, bp.socreditstatus, i.c_order_id, '
				|| selectallocationCredit || 
               ' FROM c_invoice_v i
              JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
         JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id and (' || $1 || ' = -1  or bp.c_bpartner_id = ' || $1 || ')
    LEFT JOIN c_invoicepayschedule ips ON i.c_invoicepayschedule_id = ips.c_invoicepayschedule_id
    WHERE 

' || whereclauseConditionDebit || '
' || orderby1 || '

    )
        UNION ALL
                ( SELECT DISTINCT ''C_Payment''::text AS documenttable, p.c_payment_id AS document_id, p.ad_client_id, COALESCE(il.ad_org_id, p.ad_org_id) AS ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt AS amount, NULL::integer AS c_invoicepayschedule_id, p.duedate, p.datetrx AS truedatetrx, bp.socreditstatus, 0 as c_order_id, '
		|| selectallocationPayment || 
                  ' FROM c_payment p
              JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id
         JOIN c_bpartner bp ON p.c_bpartner_id = bp.c_bpartner_id AND (' || $1 || ' = -1 or p.c_bpartner_id = ' || $1 || ')
	LEFT JOIN c_allocationline al ON al.c_payment_id = p.c_payment_id 
	LEFT JOIN c_invoice il ON il.c_invoice_id = al.c_invoice_id
	LEFT JOIN M_BoletaDepositoLine bdlr on bdlr.c_reverse_payment_id = p.c_payment_id
	LEFT JOIN M_BoletaDeposito bdr on bdr.M_BoletaDeposito_ID = bdlr.M_BoletaDeposito_ID
	LEFT JOIN M_BoletaDepositoLine bdle on bdle.c_depo_payment_id = p.c_payment_id
	LEFT JOIN M_BoletaDeposito bde on bde.M_BoletaDeposito_ID = bdle.M_BoletaDeposito_ID
	LEFT JOIN M_BoletaDeposito bddb on bddb.c_boleta_payment_id = p.c_payment_id
  WHERE 
CASE
    WHEN il.ad_org_id IS NOT NULL AND il.ad_org_id <> p.ad_org_id THEN p.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar])
    ELSE 1 = 1
END 

AND (CASE WHEN bdr.M_BoletaDeposito_ID IS NOT NULL 
		OR bde.M_BoletaDeposito_ID IS NOT NULL 
		OR bddb.M_BoletaDeposito_ID IS NOT NULL THEN p.docstatus NOT IN (''CO'',''CL'') 
	ELSE 1 = 1
	END) 

AND ' || whereclauseConditionCredit || '

' || orderby2 || '


)

UNION ALL

        ( SELECT DISTINCT ''C_CashLine''::text AS documenttable, cl.c_cashline_id AS document_id, cl.ad_client_id, COALESCE(il.ad_org_id, ic.ad_org_id, cl.ad_org_id) AS ad_org_id, cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby,
                CASE
                    WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id
		    WHEN il.c_bpartner_id IS NOT NULL THEN il.c_bpartner_id
                    ELSE ic.c_bpartner_id
                END AS c_bpartner_id, dt.c_doctype_id,
                CASE
                    WHEN cl.amount < 0.0 THEN 1
                    ELSE (-1)
                END AS signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, ''@line@''::text || cl.line::character varying::text AS documentno,
                CASE
                    WHEN cl.amount < 0.0 THEN ''N''::bpchar
                    ELSE ''Y''::bpchar
                END AS issotrx, cl.docstatus, c.statementdate AS datetrx, c.dateacct, cl.c_currency_id, NULL::integer AS c_conversiontype_id, abs(cl.amount) AS amount, NULL::integer AS c_invoicepayschedule_id, NULL::timestamp without time zone AS duedate, c.statementdate AS truedatetrx, COALESCE(bp.socreditstatus, bp2.socreditstatus, bp3.socreditstatus) AS socreditstatus, 0 as c_order_id, '
                || selectallocationCashline || 
       ' FROM (
		select cl.* FROM c_cashline cl 
		where (' || $1 || ' = -1 or cl.c_bpartner_id = ' || $1 || ')
		union 
		select cl.* FROM c_cashline cl 
		JOIN c_invoice ic on ic.c_invoice_id = cl.c_invoice_id AND (' || $1 || ' = -1 or ic.c_bpartner_id = ' || $1 || ')
		union 
		select cl.* FROM c_cashline cl 
		JOIN c_allocationline al ON al.c_cashline_id = cl.c_cashline_id
		JOIN c_invoice il ON il.c_invoice_id = al.c_invoice_id AND (' || $1 || ' = -1 or il.c_bpartner_id = ' || $1 || ')
       ) as cl
      JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
   LEFT JOIN c_bpartner bp ON cl.c_bpartner_id = bp.c_bpartner_id AND (' || $1 || ' = -1 or cl.c_bpartner_id = ' || $1 || ')
   JOIN ( SELECT d.ad_client_id, d.c_doctype_id, d.name, d.printname
         FROM c_doctype d
        WHERE d.doctypekey::text = ''CMC''::text) dt ON cl.ad_client_id = dt.ad_client_id
   LEFT JOIN c_allocationline al ON al.c_cashline_id = cl.c_cashline_id
   LEFT JOIN c_invoice il ON il.c_invoice_id = al.c_invoice_id AND (' || $1 || ' = -1 or il.c_bpartner_id = ' || $1 || ')
   LEFT JOIN c_bpartner bp2 ON il.c_bpartner_id = bp2.c_bpartner_id
   LEFT JOIN c_invoice ic on ic.c_invoice_id = cl.c_invoice_id AND (' || $1 || ' = -1 or ic.c_bpartner_id = ' || $1 || ')
   LEFT JOIN c_bpartner bp3 ON ic.c_bpartner_id = bp3.c_bpartner_id
  WHERE (CASE WHEN cl.c_bpartner_id IS NOT NULL THEN (' || $1 || ' = -1 or cl.c_bpartner_id = ' || $1 || ')
        WHEN il.c_bpartner_id IS NOT NULL THEN (' || $1 || ' = -1 or il.c_bpartner_id = ' || $1 || ')
        WHEN ic.c_bpartner_id IS NOT NULL THEN (' || $1 || ' = -1 or ic.c_bpartner_id = ' || $1 || ')
        ELSE 1 = 2 END)
    AND (CASE WHEN (il.ad_org_id IS NOT NULL AND il.ad_org_id <> cl.ad_org_id) OR (ic.ad_org_id IS NOT NULL AND ic.ad_org_id <> cl.ad_org_id)
        THEN cl.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar])
        ELSE 1 = 1 END)

    AND (' || whereclauseConditionCreditCashException || ' )

' || orderby3 || '

)) AS d  
WHERE ' || whereclauseDateTo || ' ; ';

-- raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION v_documents_org_filtered(integer, boolean, character, timestamp without time zone)
  OWNER TO libertya;
  
--20180507-1330 Nueva tabla que permite configurar cláusulas where por organización en un único procesador contable
CREATE TABLE c_acctprocessortable
(
  c_acctprocessortable_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  c_acctprocessor_id integer NOT NULL,
  ad_table_id integer NOT NULL,
  whereclause character varying(2000),
  CONSTRAINT c_acctprocessortable_key PRIMARY KEY (c_acctprocessortable_id),
  CONSTRAINT c_acctprocessortable_acctprocessor FOREIGN KEY (c_acctprocessor_id)
      REFERENCES c_acctprocessor (c_acctprocessor_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT c_acctprocessortable_table FOREIGN KEY (ad_table_id)
      REFERENCES ad_table (ad_table_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=TRUE
);
ALTER TABLE c_acctprocessortable
  OWNER TO libertya;
  
--20180510-2335 Nuevo parámetro de grupo de ec en el informe de estado de cuenta de ec
update ad_system set dummy = (SELECT addcolumnifnotexists('t_estadodecuenta', 'c_bp_group_id', 'integer'));

--20180516-1500 Nueva columna para indicar que el asiento fue reactivado
update ad_system set dummy = (SELECT addcolumnifnotexists('GL_Journal', 'IsReActivated', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));

--20180524-1040 Mejoras a la función para determinar el pendiente y actualizarlo
CREATE OR REPLACE FUNCTION getqtyreserved(
    clientid integer,
    orgid integer,
    locatorid integer,
    productid integer,
    dateto date)
  RETURNS numeric AS
$BODY$
/***********
Obtiene la cantidad reservada a fecha de corte. Si no hay fecha de corte, entonces se devuelven los pendientes actuales.
Por lo pronto no se utiliza el pendiente a fecha de corte ya que primero deberíamos analizar e implementar 
una forma en la que se determine cuando un pedido fue completo, anulado, etc.
*/
DECLARE
	reserved numeric;
BEGIN
	reserved := 0;
	--Si no hay fecha de corte o es mayor o igual a la fecha actual, entonces se suman las cantidades reservadas de los pedidos
	--if ( dateTo is null OR dateTo >= current_date ) THEN
		SELECT INTO reserved coalesce(sum(ol.qtyreserved - coalesce(movementqty,0)),0)
		from c_orderline ol
		inner join c_order o on o.c_order_id = ol.c_order_id
		inner join c_doctype dto on dto.c_doctype_id = o.c_doctypetarget_id
		inner join m_warehouse w on w.m_warehouse_id = o.m_warehouse_id
		inner join m_locator l on l.m_warehouse_id = w.m_warehouse_id
		left join (select iol.c_orderline_id, sum(iol.movementqty) as movementqty  
				from m_inoutline as iol  
				inner join m_inout as io on io.m_inout_id = iol.m_inout_id  
				inner join c_doctype as dt on dt.c_doctype_id = io.c_doctype_id  
				join c_orderline ol on ol.c_orderline_id = iol.c_orderline_id
				where ol.ad_client_id = clientid
					and ol.ad_org_id = orgid 
					and dt.doctypekey = 'DC' 
					and dt.reservestockmanagment = 'N'
					and io.docstatus in ('CO','CL')
					and iol.m_product_id = productid
					and ol.qtyreserved <> 0
				group by iol.c_orderline_id) as dc on dc.c_orderline_id = ol.c_orderline_id
		where o.ad_client_id = clientid
			and o.ad_org_id = orgid 
			and ol.qtyreserved <> 0
			and o.processed = 'Y' 
			and ol.m_product_id = productid
			and l.m_locator_id = locatorid
			and o.issotrx = 'Y'
			and dto.doctypekey <> 'SOSOT';
	/*ELSE
		SELECT INTO reserved coalesce(sum(qty),0)
		from (
		-- Cantidad pedida a fecha de corte
		select coalesce(sum(ol.qtyordered),0) as qty
			from c_orderline ol
			inner join c_order o on o.c_order_id = ol.c_order_id
			inner join c_doctype dt on dt.c_doctype_id = o.c_doctypetarget_id
			inner join m_warehouse w on w.m_warehouse_id = o.m_warehouse_id
			inner join m_locator l on l.m_warehouse_id = w.m_warehouse_id
			where o.ad_client_id = clientid
				and o.ad_org_id = orgid
				and o.processed = 'Y' 
				and ol.m_product_id = productid
				and l.m_locator_id = locatorid
				and o.issotrx = 'Y'
				and dt.doctypekey NOT IN ('SOSOT')
				and o.dateordered::date <= dateTo::date
				and o.dateordered::date <= current_date
		union all
		-- Notas de crédito con (o sin) el check Actualizar Cantidades de Pedido
		select coalesce(sum(il.qtyinvoiced),0) as qty
		from c_invoiceline il
		inner join c_invoice i on i.c_invoice_id = il.c_invoice_id
		inner join c_doctype dt on dt.c_doctype_id = i.c_doctypetarget_id
		inner join c_orderline ol on ol.c_orderline_id = il.c_orderline_id
		inner join c_order o on o.c_order_id = ol.c_order_id
		inner join m_warehouse w on w.m_warehouse_id = o.m_warehouse_id
		inner join m_locator l on l.m_warehouse_id = w.m_warehouse_id
		where o.ad_client_id = clientid
			and o.ad_org_id = orgid
			and o.processed = 'Y' 
			and ol.m_product_id = productid
			and l.m_locator_id = locatorid
			and i.issotrx = 'Y'
			and il.m_inoutline_id is null
			and dt.signo_issotrx = '-1'
			and o.dateordered::date <= dateTo::date
			and i.dateinvoiced::date > dateTo::date
			and i.dateinvoiced::date <= current_date
		union all
		--En transaction las salidas son negativas y las entradas positivas
		select coalesce(sum(t.movementqty),0) as qty
		from m_transaction t
		inner join m_inoutline iol on iol.m_inoutline_id = t.m_inoutline_id
		inner join m_inout io on io.m_inout_id = iol.m_inout_id
		inner join c_doctype dt on dt.c_doctype_id = io.c_doctype_id
		inner join c_orderline ol on ol.c_orderline_id = iol.c_orderline_id
		inner join c_order o on o.c_order_id = ol.c_order_id
		where t.ad_client_id = clientid
			and t.ad_org_id = orgid
			and t.m_product_id = productid
			and t.m_locator_id = locatorid
			and dt.reservestockmanagment = 'Y'
			and o.dateordered::date <= dateTo::date
			and t.movementdate::date <= dateTo::date
			and t.movementdate::date <= current_date
		union all
		--Cantidades transferidas
		select coalesce(sum(ol.qtyordered * -1),0) as qty
		from c_orderline ol
		inner join c_order o on o.c_order_id = ol.c_order_id
		inner join c_orderline rl on rl.c_orderline_id = ol.ref_orderline_id
		inner join c_order r on r.c_order_id = rl.c_order_id
		inner join c_doctype dt on dt.c_doctype_id = o.c_doctype_id
		inner join m_warehouse w on w.m_warehouse_id = o.m_warehouse_id
		inner join m_locator l on l.m_warehouse_id = w.m_warehouse_id
		where o.ad_client_id = clientid
			and o.ad_org_id = orgid
			and o.processed = 'Y' 
			and ol.m_product_id = productid
			and l.m_locator_id = locatorid
			and o.issotrx = 'Y'
			and dt.doctypekey IN ('SOSOT')
			and r.dateordered::date <= dateTo::date
			and o.dateordered::date <= dateTo::date
			and o.dateordered::date <= current_date
		) todo;
	END IF;*/
	
	return reserved;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION getqtyreserved(integer, integer, integer, integer, date)
  OWNER TO libertya;

CREATE OR REPLACE FUNCTION update_reserved(
    clientid integer,
    orgid integer,
    productid integer)
  RETURNS void AS
$BODY$
/***********
Actualiza la cantidad reservada de los depósitos de la compañía, organización y artículo parametro, 
siempre y cuando existan los regitros en m_storage 
y sólo sobre locators marcados como default ya que asi se realiza al procesar pedidos.
Las cantidades reservadas se obtienen de pedidos procesados. 
IMPORTANTE: No funciona para artículos que no son ITEMS (Stockeables)
*/
BEGIN
	update m_storage s
	set qtyreserved = getqtyreserved(clientid, orgid, s.m_locator_id, productid, null::date)
	where ad_client_id = clientid
		and (orgid = 0 or ad_org_id = orgid)
		and (productid = 0 or m_product_id = productid)
		and s.m_locator_id IN (select defaultLocator 
					from (select m_warehouse_id, max(m_locator_id) as defaultLocator
						from m_locator l
						where l.isdefault = 'Y' and l.isactive = 'Y'
						GROUP by m_warehouse_id) as dl);
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION update_reserved(integer, integer, integer)
  OWNER TO libertya;

--20180524-1125 Fixes a la actualización de pendientes cuando los parámetros son 0
CREATE OR REPLACE FUNCTION update_reserved(
    clientid integer,
    orgid integer,
    productid integer)
  RETURNS void AS
$BODY$
/***********
Actualiza la cantidad reservada de los depósitos de la compañía, organización y artículo parametro, 
siempre y cuando existan los regitros en m_storage 
y sólo sobre locators marcados como default ya que asi se realiza al procesar pedidos.
Las cantidades reservadas se obtienen de pedidos procesados. 
IMPORTANTE: No funciona para artículos que no son ITEMS (Stockeables)
*/
BEGIN
	update m_storage s
	set qtyreserved = getqtyreserved(clientid, s.ad_org_id, s.m_locator_id, s.m_product_id, null::date)
	where ad_client_id = clientid
		and (orgid = 0 or ad_org_id = orgid)
		and (productid = 0 or m_product_id = productid)
		and s.m_locator_id IN (select defaultLocator 
					from (select m_warehouse_id, max(m_locator_id) as defaultLocator
						from m_locator l
						where l.isdefault = 'Y' and l.isactive = 'Y'
						GROUP by m_warehouse_id) as dl);
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION update_reserved(integer, integer, integer)
  OWNER TO libertya;
  
--20180611-1600 Nueva view para detalle de transferencias entre almacenes
CREATE OR REPLACE VIEW m_transfer_detailed_v AS 
 SELECT t.ad_client_id, t.ad_org_id, t.m_transfer_id, t.documentno, t.datetrx::date as datetrx, t.duedate::date as duedate, 
	t.description, t.docstatus, t.createdby, t.updatedby, t.movementtype, t.c_charge_id, t.c_doctype_id,
	t.m_warehouse_id, t.m_warehouseto_id, t.c_bpartner_id, t.c_order_id, tl.m_product_id, tl.line, tl.qty
FROM m_transfer t
JOIN m_transferline tl ON tl.m_transfer_id = t.m_transfer_id
WHERE t.transfertype = 'T' and t.docstatus IN ('CO','CL','VO','RE')
ORDER BY t.datetrx, t.documentno, tl.line;

ALTER TABLE m_transfer_detailed_v
  OWNER TO libertya;
  
--20180627-1130 Mejoras a la funview v_documents por nuevos parámetros
--Funview v_documents_org_filtered(integer, boolean, character, timestamp without time zone, integer, character, boolean)
CREATE OR REPLACE FUNCTION v_documents_org_filtered(
    bpartner integer,
    summaryonly boolean,
    condition character,
    dateto timestamp without time zone,
    orgid integer,
    accountType character,
    addstatusclause boolean)
  RETURNS SETOF v_documents_org_type_condition AS
$BODY$
declare
    consulta varchar;
    orderby1 varchar;
    orderby2 varchar;
    orderby3 varchar;
    leftjoin1 varchar;
    leftjoin2 varchar;
    advancedcondition varchar;
    advancedconditioncashlineexception varchar;
    whereclauseConditionDebit varchar;
    whereclauseConditionCredit varchar;
    whereclauseConditionCreditCashException varchar;
    whereclauseDateTo varchar;
    selectallocationNull varchar;
    selectallocationPayment varchar;
    selectallocationCashline varchar;
    selectallocationCredit varchar;
    selectAllocationReferencePayment varchar;
    selectAllocationReferenceCashline varchar;
    selectAllocationReferenceCredit varchar;
    adocument v_documents_org_type_condition;
    whereclauseOrgPayment varchar;
    whereclauseOrgCashline varchar;
    selectOrgPayment varchar;
    selectOrgCashline varchar;
    whereclauseAccountTypeStd varchar;
    whereclauseAccountTypeCashLine varchar;
    whereclauseDocStatusPayment varchar;
    whereclauseDocStatusCashLine varchar;
    whereclauseDocStatusInvoice varchar;
BEGIN
    whereclauseDateTo = ' ( 1 = 1 ) ';
    -- Armar la condición para fecha de corte
    if dateTo is not null then 
		whereclauseDateTo = ' dateacct::date <= ''' || dateTo || '''::date ';
    end if;
    
    --Si no se deben mostrar todos, entonces agregar la condicion por la forma de pago
    if condition <> 'A' then
		--Si se debe mostrar sólo efectivo, entonces no se debe mostrar los anticipos, si o si debe tener una factura asociada
		advancedcondition = 'il.paymentrule is null OR ';
		advancedconditioncashlineexception = ' (1=1) ';
		if condition = 'B' then
			advancedcondition = '';
			advancedconditioncashlineexception = ' (1=2) ';
		end if;
		whereclauseConditionDebit = ' (i.paymentrule = ''' || condition || ''') ';
		whereclauseConditionCredit = ' (' || advancedcondition || ' il.paymentrule = ''' || condition || ''') ';
		whereclauseConditionCreditCashException = '( CASE WHEN il.paymentrule is not null THEN il.paymentrule = ''' || condition || ''' WHEN ic.paymentrule is not null THEN ic.paymentrule = ''' || condition || ''' ELSE '|| advancedconditioncashlineexception ||' END ) ';
	else
		whereclauseConditionDebit = ' (1 = 1) ';
		whereclauseConditionCredit = ' (1 = 1) ';
		whereclauseConditionCreditCashException = ' (1 = 1) '; 
    end if;    

    whereclauseOrgPayment = ' (1 = 1) ';
    whereclauseOrgCashline = ' (1 = 1) ';

    selectOrgPayment = ' p.ad_org_id ';
    selectOrgCashline = ' cl.ad_org_id ';
    
    if (orgid is not null and orgid > 0) then
	whereclauseOrgPayment = ' CASE WHEN il.ad_org_id IS NOT NULL AND il.ad_org_id <> p.ad_org_id THEN p.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar]) ELSE 1 = 1 END ';
	whereclauseOrgCashline = ' (CASE WHEN (il.ad_org_id IS NOT NULL AND il.ad_org_id <> cl.ad_org_id) OR (ic.ad_org_id IS NOT NULL AND ic.ad_org_id <> cl.ad_org_id) THEN cl.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar]) ELSE 1 = 1 END) ';

	selectOrgPayment = ' COALESCE(il.ad_org_id, p.ad_org_id) AS ad_org_id ';
        selectOrgCashline = ' COALESCE(il.ad_org_id, ic.ad_org_id, cl.ad_org_id) AS ad_org_id ';
    end if;

    -- Tipo de Cuenta
    whereclauseAccountTypeStd = ' (1=1) ';
    whereclauseAccountTypeCashLine = ' (1=1) ';
    
    if (accountType is not null and accountType <> 'B') then
	if (accountType = 'V') then 
		whereclauseAccountTypeStd = ' bp.isvendor = ''Y'' ';
		whereclauseAccountTypeCashLine = ' (CASE WHEN cl.c_bpartner_id IS NOT NULL THEN bp.isvendor = ''Y'' 
							WHEN il.c_bpartner_id IS NOT NULL THEN bp2.isvendor = ''Y'' 
							WHEN ic.c_bpartner_id IS NOT NULL THEN bp3.isvendor = ''Y'' 
							ELSE 1 = 2 END) ';
	else 
		whereclauseAccountTypeStd = ' bp.iscustomer = ''Y'' '; 
		whereclauseAccountTypeCashLine = ' (CASE WHEN cl.c_bpartner_id IS NOT NULL THEN bp.iscustomer = ''Y'' 
							WHEN il.c_bpartner_id IS NOT NULL THEN bp2.iscustomer = ''Y'' 
							WHEN ic.c_bpartner_id IS NOT NULL THEN bp3.iscustomer = ''Y'' 
							ELSE 1 = 2 END) ';
	end if;
    end if;

    whereclauseDocStatusPayment = ' (1=1) ';
    whereclauseDocStatusCashLine = ' (1=1) ';
    whereclauseDocStatusInvoice = ' (1=1) ';
    if addstatusclause then
	whereclauseDocStatusPayment = ' p.docstatus IN (''CO'',''CL'') ';
	whereclauseDocStatusCashLine = ' cl.docstatus IN (''CO'',''CL'') ';
	whereclauseDocStatusInvoice = ' i.docstatus IN (''CO'',''CL'') ';
    end if;
    
    -- recuperar informacion minima indispensable si summaryonly es true.  en caso de ser false, debe joinearse/ordenarse, etc.
    if summaryonly = false then

        orderby1 = ' ORDER BY ''C_Invoice''::text, i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name, dt.printname, i.documentno, i.issotrx, i.docstatus,
                 CASE
                     WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
                     ELSE i.dateinvoiced
                 END, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced, bp.socreditstatus ';

        orderby2 = ' ORDER BY ''C_Payment''::text, p.c_payment_id, p.ad_client_id, ' || selectOrgPayment || ', p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name, dt.printname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt, NULL::integer, p.duedate, bp.socreditstatus ';

        orderby3 = ' ORDER BY ''C_CashLine''::text, cl.c_cashline_id, cl.ad_client_id, ' || selectOrgCashline || ', cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby,
                CASE
                    WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id
		    WHEN il.c_bpartner_id IS NOT NULL THEN il.c_bpartner_id
                    ELSE ic.c_bpartner_id
                END, dt.c_doctype_id,
                CASE
                    WHEN cl.amount < 0.0 THEN 1
                    ELSE (-1)
                END, dt.name, dt.printname, ''@line@''::text || cl.line::character varying::text,
                CASE
                    WHEN cl.amount < 0.0 THEN ''N''::bpchar
                    ELSE ''Y''::bpchar
                END, cl.docstatus, c.statementdate, c.dateacct, cl.c_currency_id, NULL::integer, abs(cl.amount), NULL::timestamp without time zone, COALESCE(bp.socreditstatus, bp2.socreditstatus, bp3.socreditstatus) ';
	
	selectallocationNull = ' NULL::integer ';
	selectallocationPayment = selectallocationNull;
	selectallocationCashline = selectallocationNull;
	selectallocationCredit = selectallocationNull;
	
    else
        orderby1 = '';
        orderby2 = '';
        orderby3 = '';

	selectAllocationReferencePayment = ' al.c_payment_id = p.c_payment_id ';
	selectAllocationReferenceCashline = ' al.c_cashline_id = cl.c_cashline_id ';
	selectAllocationReferenceCredit = ' al.c_invoice_credit_id = i.c_invoice_id ';

	selectallocationPayment = ' (SELECT ah.c_allocationhdr_id FROM c_allocationline al INNER JOIN c_allocationhdr ah on ah.c_allocationhdr_id = al.c_allocationhdr_id WHERE allocationtype <> ''MAN'' AND ah.dateacct::date = p.dateacct::date AND ' || selectAllocationReferencePayment || ' AND ' || whereclauseDateTo || ' ORDER BY ah.created LIMIT 1) as c_allocationhdr_id ';
	selectallocationCashline = ' (SELECT ah.c_allocationhdr_id FROM c_allocationline al INNER JOIN c_allocationhdr ah on ah.c_allocationhdr_id = al.c_allocationhdr_id WHERE allocationtype <> ''MAN'' AND ah.dateacct::date = c.dateacct::date AND ' || selectAllocationReferenceCashline || ' AND ' || whereclauseDateTo || ' ORDER BY ah.created LIMIT 1) as c_allocationhdr_id ';
	selectallocationCredit = ' (SELECT ah.c_allocationhdr_id FROM c_allocationline al INNER JOIN c_allocationhdr ah on ah.c_allocationhdr_id = al.c_allocationhdr_id WHERE allocationtype <> ''MAN'' AND ah.dateacct::date = i.dateacct::date AND ' || selectAllocationReferenceCredit || ' AND ' || whereclauseDateTo || ' ORDER BY ah.created LIMIT 1) as c_allocationhdr_id ';
	
    end if;    

    consulta = ' SELECT * FROM 

        (        ( SELECT DISTINCT ''C_Invoice''::text AS documenttable, i.c_invoice_id AS document_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, i.documentno, i.issotrx, i.docstatus,
                        CASE
                            WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
                            ELSE i.dateinvoiced
                        END AS datetrx, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal AS amount, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced AS truedatetrx, bp.socreditstatus, i.c_order_id, '
				|| selectallocationCredit || 
               ' FROM c_invoice_v i
              JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
         JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id and (' || $1 || ' = -1  or bp.c_bpartner_id = ' || $1 || ')
    LEFT JOIN c_invoicepayschedule ips ON i.c_invoicepayschedule_id = ips.c_invoicepayschedule_id
    WHERE 

' || whereclauseDocStatusInvoice || '
AND ' || whereclauseConditionDebit || ' 
AND ' || whereclauseAccountTypeStd || '
' || orderby1 || '

    )
        UNION ALL
                ( SELECT DISTINCT ''C_Payment''::text AS documenttable, p.c_payment_id AS document_id, p.ad_client_id, ' || selectOrgPayment || ', p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt AS amount, NULL::integer AS c_invoicepayschedule_id, p.duedate, p.datetrx AS truedatetrx, bp.socreditstatus, 0 as c_order_id, '
		|| selectallocationPayment || 
                  ' FROM c_payment p
              JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id
         JOIN c_bpartner bp ON p.c_bpartner_id = bp.c_bpartner_id AND (' || $1 || ' = -1 or p.c_bpartner_id = ' || $1 || ')
	LEFT JOIN c_allocationline al ON al.c_payment_id = p.c_payment_id 
	LEFT JOIN c_invoice il ON il.c_invoice_id = al.c_invoice_id
	LEFT JOIN M_BoletaDepositoLine bdlr on bdlr.c_reverse_payment_id = p.c_payment_id
	LEFT JOIN M_BoletaDeposito bdr on bdr.M_BoletaDeposito_ID = bdlr.M_BoletaDeposito_ID
	LEFT JOIN M_BoletaDepositoLine bdle on bdle.c_depo_payment_id = p.c_payment_id
	LEFT JOIN M_BoletaDeposito bde on bde.M_BoletaDeposito_ID = bdle.M_BoletaDeposito_ID
	LEFT JOIN M_BoletaDeposito bddb on bddb.c_boleta_payment_id = p.c_payment_id
  WHERE 

' || whereclauseDocStatusPayment || '
AND ' || whereclauseOrgPayment || '
AND (CASE WHEN bdr.M_BoletaDeposito_ID IS NOT NULL 
		OR bde.M_BoletaDeposito_ID IS NOT NULL 
		OR bddb.M_BoletaDeposito_ID IS NOT NULL THEN p.docstatus NOT IN (''CO'',''CL'') 
	ELSE 1 = 1
	END) 

AND ' || whereclauseConditionCredit || '
AND ' || whereclauseAccountTypeStd || '
' || orderby2 || '


)

UNION ALL

        ( SELECT DISTINCT ''C_CashLine''::text AS documenttable, cl.c_cashline_id AS document_id, cl.ad_client_id, ' || selectOrgCashline || ', cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby,
                CASE
                    WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id
		    WHEN il.c_bpartner_id IS NOT NULL THEN il.c_bpartner_id
                    ELSE ic.c_bpartner_id
                END AS c_bpartner_id, dt.c_doctype_id,
                CASE
                    WHEN cl.amount < 0.0 THEN 1
                    ELSE (-1)
                END AS signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, ''@line@''::text || cl.line::character varying::text AS documentno,
                CASE
                    WHEN cl.amount < 0.0 THEN ''N''::bpchar
                    ELSE ''Y''::bpchar
                END AS issotrx, cl.docstatus, c.statementdate AS datetrx, c.dateacct, cl.c_currency_id, NULL::integer AS c_conversiontype_id, abs(cl.amount) AS amount, NULL::integer AS c_invoicepayschedule_id, NULL::timestamp without time zone AS duedate, c.statementdate AS truedatetrx, COALESCE(bp.socreditstatus, bp2.socreditstatus, bp3.socreditstatus) AS socreditstatus, 0 as c_order_id, '
                || selectallocationCashline || 
       ' FROM (
		select cl.* FROM c_cashline cl 
		where (' || $1 || ' = -1 or cl.c_bpartner_id = ' || $1 || ') AND ' || whereclauseDocStatusCashLine || '
		union 
		select cl.* FROM c_cashline cl 
		JOIN c_invoice ic on ic.c_invoice_id = cl.c_invoice_id AND (' || $1 || ' = -1 or ic.c_bpartner_id = ' || $1 || ') AND ' || whereclauseDocStatusCashLine || '
		union 
		select cl.* FROM c_cashline cl 
		JOIN c_allocationline al ON al.c_cashline_id = cl.c_cashline_id
		JOIN c_invoice il ON il.c_invoice_id = al.c_invoice_id AND (' || $1 || ' = -1 or il.c_bpartner_id = ' || $1 || ') AND ' || whereclauseDocStatusCashLine || '
       ) as cl
      JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
   LEFT JOIN c_bpartner bp ON cl.c_bpartner_id = bp.c_bpartner_id AND (' || $1 || ' = -1 or cl.c_bpartner_id = ' || $1 || ')
   JOIN ( SELECT d.ad_client_id, d.c_doctype_id, d.name, d.printname
         FROM c_doctype d
        WHERE d.doctypekey::text = ''CMC''::text) dt ON cl.ad_client_id = dt.ad_client_id
   LEFT JOIN c_allocationline al ON al.c_cashline_id = cl.c_cashline_id
   LEFT JOIN c_invoice il ON il.c_invoice_id = al.c_invoice_id AND (' || $1 || ' = -1 or il.c_bpartner_id = ' || $1 || ')
   LEFT JOIN c_bpartner bp2 ON il.c_bpartner_id = bp2.c_bpartner_id
   LEFT JOIN c_invoice ic on ic.c_invoice_id = cl.c_invoice_id AND (' || $1 || ' = -1 or ic.c_bpartner_id = ' || $1 || ')
   LEFT JOIN c_bpartner bp3 ON ic.c_bpartner_id = bp3.c_bpartner_id
  WHERE (CASE WHEN cl.c_bpartner_id IS NOT NULL THEN (' || $1 || ' = -1 or cl.c_bpartner_id = ' || $1 || ')
        WHEN il.c_bpartner_id IS NOT NULL THEN (' || $1 || ' = -1 or il.c_bpartner_id = ' || $1 || ')
        WHEN ic.c_bpartner_id IS NOT NULL THEN (' || $1 || ' = -1 or ic.c_bpartner_id = ' || $1 || ')
        ELSE 1 = 2 END)

    AND ' || whereclauseOrgCashline || '
    AND (' || whereclauseConditionCreditCashException || ' )
    AND ' || whereclauseAccountTypeCashLine || '

' || orderby3 || '

)) AS d  
WHERE ' || whereclauseDateTo || ' ; ';

-- raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION v_documents_org_filtered(integer, boolean, character, timestamp without time zone, integer, character, boolean)
  OWNER TO libertya;
  
--Funview v_documents_org_filtered(integer, boolean, character, timestamp without time zone)
CREATE OR REPLACE FUNCTION v_documents_org_filtered(
    bpartner integer,
    summaryonly boolean,
    condition character,
    dateto timestamp without time zone)
  RETURNS SETOF v_documents_org_type_condition AS
$BODY$
BEGIN
   return query select * from v_documents_org_filtered(bpartner, summaryonly, condition, dateto, 0, 'B', false);
END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION v_documents_org_filtered(integer, boolean, character, timestamp without time zone)
  OWNER TO libertya;
  
--20180629-1224 Incremento de tamaño de la columna para registrar el último changelog id
DROP VIEW ad_plugin_v;

alter table ad_plugin alter column component_last_changelog type character varying(60);

CREATE OR REPLACE VIEW ad_plugin_v AS 
 SELECT cv.name, cv.ad_componentobjectuid, cv.version, p.created, p.updated, p.createdby, p.updatedby, p.component_export_date, p.component_last_changelog
   FROM ad_componentversion cv
   JOIN ad_plugin p ON p.ad_componentversion_id = cv.ad_componentversion_id
  ORDER BY p.created;

ALTER TABLE ad_plugin_v
  OWNER TO libertya;
  
--20180629-1620 Incorporación del estado WC para inclusión de comprobantes
CREATE OR REPLACE FUNCTION v_documents_org_filtered(
    bpartner integer,
    summaryonly boolean,
    condition character,
    dateto timestamp without time zone,
    orgid integer,
    accountType character,
    addstatusclause boolean)
  RETURNS SETOF v_documents_org_type_condition AS
$BODY$
declare
    consulta varchar;
    orderby1 varchar;
    orderby2 varchar;
    orderby3 varchar;
    leftjoin1 varchar;
    leftjoin2 varchar;
    advancedcondition varchar;
    advancedconditioncashlineexception varchar;
    whereclauseConditionDebit varchar;
    whereclauseConditionCredit varchar;
    whereclauseConditionCreditCashException varchar;
    whereclauseDateTo varchar;
    selectallocationNull varchar;
    selectallocationPayment varchar;
    selectallocationCashline varchar;
    selectallocationCredit varchar;
    selectAllocationReferencePayment varchar;
    selectAllocationReferenceCashline varchar;
    selectAllocationReferenceCredit varchar;
    adocument v_documents_org_type_condition;
    whereclauseOrgPayment varchar;
    whereclauseOrgCashline varchar;
    selectOrgPayment varchar;
    selectOrgCashline varchar;
    whereclauseAccountTypeStd varchar;
    whereclauseAccountTypeCashLine varchar;
    whereclauseDocStatusPayment varchar;
    whereclauseDocStatusCashLine varchar;
    whereclauseDocStatusInvoice varchar;
BEGIN
    whereclauseDateTo = ' ( 1 = 1 ) ';
    -- Armar la condición para fecha de corte
    if dateTo is not null then 
		whereclauseDateTo = ' dateacct::date <= ''' || dateTo || '''::date ';
    end if;
    
    --Si no se deben mostrar todos, entonces agregar la condicion por la forma de pago
    if condition <> 'A' then
		--Si se debe mostrar sólo efectivo, entonces no se debe mostrar los anticipos, si o si debe tener una factura asociada
		advancedcondition = 'il.paymentrule is null OR ';
		advancedconditioncashlineexception = ' (1=1) ';
		if condition = 'B' then
			advancedcondition = '';
			advancedconditioncashlineexception = ' (1=2) ';
		end if;
		whereclauseConditionDebit = ' (i.paymentrule = ''' || condition || ''') ';
		whereclauseConditionCredit = ' (' || advancedcondition || ' il.paymentrule = ''' || condition || ''') ';
		whereclauseConditionCreditCashException = '( CASE WHEN il.paymentrule is not null THEN il.paymentrule = ''' || condition || ''' WHEN ic.paymentrule is not null THEN ic.paymentrule = ''' || condition || ''' ELSE '|| advancedconditioncashlineexception ||' END ) ';
	else
		whereclauseConditionDebit = ' (1 = 1) ';
		whereclauseConditionCredit = ' (1 = 1) ';
		whereclauseConditionCreditCashException = ' (1 = 1) '; 
    end if;    

    whereclauseOrgPayment = ' (1 = 1) ';
    whereclauseOrgCashline = ' (1 = 1) ';

    selectOrgPayment = ' p.ad_org_id ';
    selectOrgCashline = ' cl.ad_org_id ';
    
    if (orgid is not null and orgid > 0) then
	whereclauseOrgPayment = ' CASE WHEN il.ad_org_id IS NOT NULL AND il.ad_org_id <> p.ad_org_id THEN p.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar]) ELSE 1 = 1 END ';
	whereclauseOrgCashline = ' (CASE WHEN (il.ad_org_id IS NOT NULL AND il.ad_org_id <> cl.ad_org_id) OR (ic.ad_org_id IS NOT NULL AND ic.ad_org_id <> cl.ad_org_id) THEN cl.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar]) ELSE 1 = 1 END) ';

	selectOrgPayment = ' COALESCE(il.ad_org_id, p.ad_org_id) AS ad_org_id ';
        selectOrgCashline = ' COALESCE(il.ad_org_id, ic.ad_org_id, cl.ad_org_id) AS ad_org_id ';
    end if;

    -- Tipo de Cuenta
    whereclauseAccountTypeStd = ' (1=1) ';
    whereclauseAccountTypeCashLine = ' (1=1) ';
    
    if (accountType is not null and accountType <> 'B') then
	if (accountType = 'V') then 
		whereclauseAccountTypeStd = ' bp.isvendor = ''Y'' ';
		whereclauseAccountTypeCashLine = ' (CASE WHEN cl.c_bpartner_id IS NOT NULL THEN bp.isvendor = ''Y'' 
							WHEN il.c_bpartner_id IS NOT NULL THEN bp2.isvendor = ''Y'' 
							WHEN ic.c_bpartner_id IS NOT NULL THEN bp3.isvendor = ''Y'' 
							ELSE 1 = 2 END) ';
	else 
		whereclauseAccountTypeStd = ' bp.iscustomer = ''Y'' '; 
		whereclauseAccountTypeCashLine = ' (CASE WHEN cl.c_bpartner_id IS NOT NULL THEN bp.iscustomer = ''Y'' 
							WHEN il.c_bpartner_id IS NOT NULL THEN bp2.iscustomer = ''Y'' 
							WHEN ic.c_bpartner_id IS NOT NULL THEN bp3.iscustomer = ''Y'' 
							ELSE 1 = 2 END) ';
	end if;
    end if;

    whereclauseDocStatusPayment = ' (1=1) ';
    whereclauseDocStatusCashLine = ' (1=1) ';
    whereclauseDocStatusInvoice = ' (1=1) ';
    if addstatusclause then
	whereclauseDocStatusPayment = ' p.docstatus IN (''CO'',''CL'',''WC'') ';
	whereclauseDocStatusCashLine = ' cl.docstatus IN (''CO'',''CL'',''WC'') ';
	whereclauseDocStatusInvoice = ' i.docstatus IN (''CO'',''CL'',''WC'') ';
    end if;
    
    -- recuperar informacion minima indispensable si summaryonly es true.  en caso de ser false, debe joinearse/ordenarse, etc.
    if summaryonly = false then

        orderby1 = ' ORDER BY ''C_Invoice''::text, i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name, dt.printname, i.documentno, i.issotrx, i.docstatus,
                 CASE
                     WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
                     ELSE i.dateinvoiced
                 END, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced, bp.socreditstatus ';

        orderby2 = ' ORDER BY ''C_Payment''::text, p.c_payment_id, p.ad_client_id, ' || selectOrgPayment || ', p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name, dt.printname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt, NULL::integer, p.duedate, bp.socreditstatus ';

        orderby3 = ' ORDER BY ''C_CashLine''::text, cl.c_cashline_id, cl.ad_client_id, ' || selectOrgCashline || ', cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby,
                CASE
                    WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id
		    WHEN il.c_bpartner_id IS NOT NULL THEN il.c_bpartner_id
                    ELSE ic.c_bpartner_id
                END, dt.c_doctype_id,
                CASE
                    WHEN cl.amount < 0.0 THEN 1
                    ELSE (-1)
                END, dt.name, dt.printname, ''@line@''::text || cl.line::character varying::text,
                CASE
                    WHEN cl.amount < 0.0 THEN ''N''::bpchar
                    ELSE ''Y''::bpchar
                END, cl.docstatus, c.statementdate, c.dateacct, cl.c_currency_id, NULL::integer, abs(cl.amount), NULL::timestamp without time zone, COALESCE(bp.socreditstatus, bp2.socreditstatus, bp3.socreditstatus) ';
	
	selectallocationNull = ' NULL::integer ';
	selectallocationPayment = selectallocationNull;
	selectallocationCashline = selectallocationNull;
	selectallocationCredit = selectallocationNull;
	
    else
        orderby1 = '';
        orderby2 = '';
        orderby3 = '';

	selectAllocationReferencePayment = ' al.c_payment_id = p.c_payment_id ';
	selectAllocationReferenceCashline = ' al.c_cashline_id = cl.c_cashline_id ';
	selectAllocationReferenceCredit = ' al.c_invoice_credit_id = i.c_invoice_id ';

	selectallocationPayment = ' (SELECT ah.c_allocationhdr_id FROM c_allocationline al INNER JOIN c_allocationhdr ah on ah.c_allocationhdr_id = al.c_allocationhdr_id WHERE allocationtype <> ''MAN'' AND ah.dateacct::date = p.dateacct::date AND ' || selectAllocationReferencePayment || ' AND ' || whereclauseDateTo || ' ORDER BY ah.created LIMIT 1) as c_allocationhdr_id ';
	selectallocationCashline = ' (SELECT ah.c_allocationhdr_id FROM c_allocationline al INNER JOIN c_allocationhdr ah on ah.c_allocationhdr_id = al.c_allocationhdr_id WHERE allocationtype <> ''MAN'' AND ah.dateacct::date = c.dateacct::date AND ' || selectAllocationReferenceCashline || ' AND ' || whereclauseDateTo || ' ORDER BY ah.created LIMIT 1) as c_allocationhdr_id ';
	selectallocationCredit = ' (SELECT ah.c_allocationhdr_id FROM c_allocationline al INNER JOIN c_allocationhdr ah on ah.c_allocationhdr_id = al.c_allocationhdr_id WHERE allocationtype <> ''MAN'' AND ah.dateacct::date = i.dateacct::date AND ' || selectAllocationReferenceCredit || ' AND ' || whereclauseDateTo || ' ORDER BY ah.created LIMIT 1) as c_allocationhdr_id ';
	
    end if;    

    consulta = ' SELECT * FROM 

        (        ( SELECT DISTINCT ''C_Invoice''::text AS documenttable, i.c_invoice_id AS document_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, i.documentno, i.issotrx, i.docstatus,
                        CASE
                            WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
                            ELSE i.dateinvoiced
                        END AS datetrx, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal AS amount, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced AS truedatetrx, bp.socreditstatus, i.c_order_id, '
				|| selectallocationCredit || 
               ' FROM c_invoice_v i
              JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
         JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id and (' || $1 || ' = -1  or bp.c_bpartner_id = ' || $1 || ')
    LEFT JOIN c_invoicepayschedule ips ON i.c_invoicepayschedule_id = ips.c_invoicepayschedule_id
    WHERE 

' || whereclauseDocStatusInvoice || '
AND ' || whereclauseConditionDebit || ' 
AND ' || whereclauseAccountTypeStd || '
' || orderby1 || '

    )
        UNION ALL
                ( SELECT DISTINCT ''C_Payment''::text AS documenttable, p.c_payment_id AS document_id, p.ad_client_id, ' || selectOrgPayment || ', p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt AS amount, NULL::integer AS c_invoicepayschedule_id, p.duedate, p.datetrx AS truedatetrx, bp.socreditstatus, 0 as c_order_id, '
		|| selectallocationPayment || 
                  ' FROM c_payment p
              JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id
         JOIN c_bpartner bp ON p.c_bpartner_id = bp.c_bpartner_id AND (' || $1 || ' = -1 or p.c_bpartner_id = ' || $1 || ')
	LEFT JOIN c_allocationline al ON al.c_payment_id = p.c_payment_id 
	LEFT JOIN c_invoice il ON il.c_invoice_id = al.c_invoice_id
	LEFT JOIN M_BoletaDepositoLine bdlr on bdlr.c_reverse_payment_id = p.c_payment_id
	LEFT JOIN M_BoletaDeposito bdr on bdr.M_BoletaDeposito_ID = bdlr.M_BoletaDeposito_ID
	LEFT JOIN M_BoletaDepositoLine bdle on bdle.c_depo_payment_id = p.c_payment_id
	LEFT JOIN M_BoletaDeposito bde on bde.M_BoletaDeposito_ID = bdle.M_BoletaDeposito_ID
	LEFT JOIN M_BoletaDeposito bddb on bddb.c_boleta_payment_id = p.c_payment_id
  WHERE 

' || whereclauseDocStatusPayment || '
AND ' || whereclauseOrgPayment || '
AND (CASE WHEN bdr.M_BoletaDeposito_ID IS NOT NULL 
		OR bde.M_BoletaDeposito_ID IS NOT NULL 
		OR bddb.M_BoletaDeposito_ID IS NOT NULL THEN p.docstatus NOT IN (''CO'',''CL'') 
	ELSE 1 = 1
	END) 

AND ' || whereclauseConditionCredit || '
AND ' || whereclauseAccountTypeStd || '
' || orderby2 || '


)

UNION ALL

        ( SELECT DISTINCT ''C_CashLine''::text AS documenttable, cl.c_cashline_id AS document_id, cl.ad_client_id, ' || selectOrgCashline || ', cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby,
                CASE
                    WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id
		    WHEN il.c_bpartner_id IS NOT NULL THEN il.c_bpartner_id
                    ELSE ic.c_bpartner_id
                END AS c_bpartner_id, dt.c_doctype_id,
                CASE
                    WHEN cl.amount < 0.0 THEN 1
                    ELSE (-1)
                END AS signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, ''@line@''::text || cl.line::character varying::text AS documentno,
                CASE
                    WHEN cl.amount < 0.0 THEN ''N''::bpchar
                    ELSE ''Y''::bpchar
                END AS issotrx, cl.docstatus, c.statementdate AS datetrx, c.dateacct, cl.c_currency_id, NULL::integer AS c_conversiontype_id, abs(cl.amount) AS amount, NULL::integer AS c_invoicepayschedule_id, NULL::timestamp without time zone AS duedate, c.statementdate AS truedatetrx, COALESCE(bp.socreditstatus, bp2.socreditstatus, bp3.socreditstatus) AS socreditstatus, 0 as c_order_id, '
                || selectallocationCashline || 
       ' FROM (
		select cl.* FROM c_cashline cl 
		where (' || $1 || ' = -1 or cl.c_bpartner_id = ' || $1 || ') AND ' || whereclauseDocStatusCashLine || '
		union 
		select cl.* FROM c_cashline cl 
		JOIN c_invoice ic on ic.c_invoice_id = cl.c_invoice_id AND (' || $1 || ' = -1 or ic.c_bpartner_id = ' || $1 || ') AND ' || whereclauseDocStatusCashLine || '
		union 
		select cl.* FROM c_cashline cl 
		JOIN c_allocationline al ON al.c_cashline_id = cl.c_cashline_id
		JOIN c_invoice il ON il.c_invoice_id = al.c_invoice_id AND (' || $1 || ' = -1 or il.c_bpartner_id = ' || $1 || ') AND ' || whereclauseDocStatusCashLine || '
       ) as cl
      JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
   LEFT JOIN c_bpartner bp ON cl.c_bpartner_id = bp.c_bpartner_id AND (' || $1 || ' = -1 or cl.c_bpartner_id = ' || $1 || ')
   JOIN ( SELECT d.ad_client_id, d.c_doctype_id, d.name, d.printname
         FROM c_doctype d
        WHERE d.doctypekey::text = ''CMC''::text) dt ON cl.ad_client_id = dt.ad_client_id
   LEFT JOIN c_allocationline al ON al.c_cashline_id = cl.c_cashline_id
   LEFT JOIN c_invoice il ON il.c_invoice_id = al.c_invoice_id AND (' || $1 || ' = -1 or il.c_bpartner_id = ' || $1 || ')
   LEFT JOIN c_bpartner bp2 ON il.c_bpartner_id = bp2.c_bpartner_id
   LEFT JOIN c_invoice ic on ic.c_invoice_id = cl.c_invoice_id AND (' || $1 || ' = -1 or ic.c_bpartner_id = ' || $1 || ')
   LEFT JOIN c_bpartner bp3 ON ic.c_bpartner_id = bp3.c_bpartner_id
  WHERE (CASE WHEN cl.c_bpartner_id IS NOT NULL THEN (' || $1 || ' = -1 or cl.c_bpartner_id = ' || $1 || ')
        WHEN il.c_bpartner_id IS NOT NULL THEN (' || $1 || ' = -1 or il.c_bpartner_id = ' || $1 || ')
        WHEN ic.c_bpartner_id IS NOT NULL THEN (' || $1 || ' = -1 or ic.c_bpartner_id = ' || $1 || ')
        ELSE 1 = 2 END)

    AND ' || whereclauseOrgCashline || '
    AND (' || whereclauseConditionCreditCashException || ' )
    AND ' || whereclauseAccountTypeCashLine || '

' || orderby3 || '

)) AS d  
WHERE ' || whereclauseDateTo || ' ; ';

-- raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION v_documents_org_filtered(integer, boolean, character, timestamp without time zone, integer, character, boolean)
  OWNER TO libertya;
  
--20180702-1112 Merge r2404
update ad_system set dummy = (SELECT addcolumnifnotexists('C_DocType','AllowOnlyProviders','character(1) NOT NULL DEFAULT ''N'''));

--20180704-1608 Importe imputado a un payment con fecha de corte
CREATE OR REPLACE FUNCTION paymentallocated(
    p_c_payment_id integer,
    p_c_currency_id integer,
    p_dateto timestamp without time zone)
  RETURNS numeric AS
$BODY$
/*************************************************************************
 * The contents of this file are subject to the Compiere License.  You may
 * obtain a copy of the License at    http://www.compiere.org/license.html
 * Software is on an  "AS IS" basis,  WITHOUT WARRANTY OF ANY KIND, either
 * express or implied. See the License for details. Code: Compiere ERP+CRM
 * Copyright (C) 1999-2001 Jorg Janke, ComPiere, Inc. All Rights Reserved.
 *
 * converted to postgreSQL by Karsten Thiemann (Schaeffer AG), 
 * kthiemann@adempiere.org
 *************************************************************************
 * Title:	Calculate Allocated Payment Amount in Payment Currency
 * Description:
    --
    SELECT paymentAllocated(C_Payment_ID,C_Currency_ID), PayAmt, IsAllocated
    FROM C_Payment_v 
    WHERE C_Payment_ID<1000000;
    --
    UPDATE C_Payment_v 
    SET IsAllocated=CASE WHEN paymentAllocated(C_Payment_ID, C_Currency_ID)=PayAmt THEN 'Y' ELSE 'N' END
    WHERE C_Payment_ID>=1000000;
 ****
 *-Pasado a Liberya a partir en Adempiere 360LTS
 *-ids son enteros
 *-se asume que todos los montos son no negativos
 *-no se utilza la vista C_Payment_V y no se corrige por AP/CM (de todas maneras
 * Libertya actual no estaba usando esto y la vista siempre retorna 1 y multiplicaba
 * por este monto)
 *-no se utiliza multiplicadores: retorna siempre algo en el entorno [0..PayAmt]
 *-la obtencion del si esta asociado a un cargo y la monto del pago es obtenido
 *  en una sola setencia sql
 *-se utiliza el redondeo por moneda en vez del redondeo a 2 (aunque esto
 * ultimo tiene su sentido teniendo en cuenta que liberya maneja solo 2 decimales)
 ************************************************************************/
DECLARE
	v_AllocatedAmt		NUMERIC := 0;
    	v_PayAmt        	NUMERIC;
    	r   			RECORD;
BEGIN
    --  Charge - nothing available
    SELECT 
      INTO v_PayAmt MAX(PayAmt) 
    FROM C_Payment 
    WHERE C_Payment_ID=p_C_Payment_ID AND C_Charge_ID > 0;
    
    IF (v_PayAmt IS NOT NULL) THEN
        RETURN v_PayAmt;
    END IF;
    
	--	Calculate Allocated Amount
	FOR r IN
		SELECT	a.AD_Client_ID, a.AD_Org_ID, al.Amount, a.C_Currency_ID, a.DateTrx
			FROM	C_AllocationLine al
	          INNER JOIN C_AllocationHdr a ON (al.C_AllocationHdr_ID=a.C_AllocationHdr_ID)
			WHERE	al.C_Payment_ID = p_C_Payment_ID
          	AND   a.IsActive='Y'
          	AND (p_dateto IS NULL OR a.dateacct::date <= p_dateto::date)
	LOOP
		v_AllocatedAmt := v_AllocatedAmt
			+ currencyConvert(r.Amount, r.C_Currency_ID, 
			p_C_Currency_ID, r.DateTrx, null, r.AD_Client_ID, r.AD_Org_ID);
	END LOOP;
	--  NO en libertya:	Round to penny
	-- en vez se redondea usando la moneda
	v_AllocatedAmt := currencyRound(COALESCE(v_AllocatedAmt,0),p_c_currency_id,NULL); 
	RETURN	v_AllocatedAmt;
END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION paymentallocated(integer, integer, timestamp without time zone)
  OWNER TO libertya;

CREATE OR REPLACE FUNCTION paymentallocated(
    p_c_payment_id integer,
    p_c_currency_id integer)
  RETURNS numeric AS
$BODY$
BEGIN
    RETURN paymentallocated(p_c_payment_id, p_c_currency_id, null::timestamp);
END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION paymentallocated(integer, integer)
  OWNER TO libertya; 

--20180706-1830 Flag que marca un pedido cuando fue reactivado
update ad_system set dummy = (SELECT addcolumnifnotexists('C_Order','isreactivated','character(1) NOT NULL DEFAULT ''N''::bpchar'));

--20180710-0926 View especifica para el reporte de movimientos bancarios
create or replace view c_payment_movements_v as
select 	p.*, (p.payamt * dt.signo_issotrx * -1) as payamtsign
from c_payment p
inner join c_doctype dt on p.c_doctype_id = dt.c_doctype_id;

--20180712-2125 Nueva columna para registrar la cantidad devuelta de un pedido
update ad_system set dummy = (SELECT addcolumnifnotexists('C_OrderLine','qtyreturned','numeric(22,4) NOT NULL DEFAULT 0'));

--Nueva columna para registrar la configuración para permitir entregar devoluciones a la información de la compañía
update ad_system set dummy = (SELECT addcolumnifnotexists('ad_clientinfo','allowdeliveryreturned','character(1) NOT NULL DEFAULT ''Y''::bpchar'));

--La cantidad reservada de un artículo es la suma del qtyreserved de los pedidos de cliente
CREATE OR REPLACE FUNCTION getqtyreserved(
clientid integer,
orgid integer,
locatorid integer,
productid integer,
dateto date)
RETURNS numeric AS
$BODY$
/***********
Obtiene la cantidad reservada a fecha de corte. Si no hay fecha de corte, entonces se devuelven los pendientes actuales.
Por lo pronto no se utiliza el pendiente a fecha de corte ya que primero deberíamos analizar e implementar 
una forma en la que se determine cuando un pedido fue completo, anulado, etc.
*/
DECLARE
reserved numeric;
BEGIN
reserved := 0;
--Si no hay fecha de corte o es mayor o igual a la fecha actual, entonces se suman las cantidades reservadas de los pedidos
--if ( dateTo is null OR dateTo >= current_date ) THEN
SELECT INTO reserved coalesce(sum(ol.qtyreserved),0)
from c_orderline ol
inner join c_order o on o.c_order_id = ol.c_order_id
inner join c_doctype dto on dto.c_doctype_id = o.c_doctypetarget_id
inner join m_warehouse w on w.m_warehouse_id = o.m_warehouse_id
inner join m_locator l on l.m_warehouse_id = w.m_warehouse_id
where o.ad_client_id = clientid
and o.ad_org_id = orgid 
and ol.qtyreserved <> 0
and o.processed = 'Y' 
and ol.m_product_id = productid
and l.m_locator_id = locatorid
and o.issotrx = 'Y'
and dto.doctypekey <> 'SOSOT';
/*ELSE
SELECT INTO reserved coalesce(sum(qty),0)
from (
-- Cantidad pedida a fecha de corte
select coalesce(sum(ol.qtyordered),0) as qty
from c_orderline ol
inner join c_order o on o.c_order_id = ol.c_order_id
inner join c_doctype dt on dt.c_doctype_id = o.c_doctypetarget_id
inner join m_warehouse w on w.m_warehouse_id = o.m_warehouse_id
inner join m_locator l on l.m_warehouse_id = w.m_warehouse_id
where o.ad_client_id = clientid
and o.ad_org_id = orgid
and o.processed = 'Y' 
and ol.m_product_id = productid
and l.m_locator_id = locatorid
and o.issotrx = 'Y'
and dt.doctypekey NOT IN ('SOSOT')
and o.dateordered::date <= dateTo::date
and o.dateordered::date <= current_date
union all
-- Notas de crédito con (o sin) el check Actualizar Cantidades de Pedido
select coalesce(sum(il.qtyinvoiced),0) as qty
from c_invoiceline il
inner join c_invoice i on i.c_invoice_id = il.c_invoice_id
inner join c_doctype dt on dt.c_doctype_id = i.c_doctypetarget_id
inner join c_orderline ol on ol.c_orderline_id = il.c_orderline_id
inner join c_order o on o.c_order_id = ol.c_order_id
inner join m_warehouse w on w.m_warehouse_id = o.m_warehouse_id
inner join m_locator l on l.m_warehouse_id = w.m_warehouse_id
where o.ad_client_id = clientid
and o.ad_org_id = orgid
and o.processed = 'Y' 
and ol.m_product_id = productid
and l.m_locator_id = locatorid
and i.issotrx = 'Y'
and il.m_inoutline_id is null
and dt.signo_issotrx = '-1'
and o.dateordered::date <= dateTo::date
and i.dateinvoiced::date > dateTo::date
and i.dateinvoiced::date <= current_date
union all
--En transaction las salidas son negativas y las entradas positivas
select coalesce(sum(t.movementqty),0) as qty
from m_transaction t
inner join m_inoutline iol on iol.m_inoutline_id = t.m_inoutline_id
inner join m_inout io on io.m_inout_id = iol.m_inout_id
inner join c_doctype dt on dt.c_doctype_id = io.c_doctype_id
inner join c_orderline ol on ol.c_orderline_id = iol.c_orderline_id
inner join c_order o on o.c_order_id = ol.c_order_id
where t.ad_client_id = clientid
and t.ad_org_id = orgid
and t.m_product_id = productid
and t.m_locator_id = locatorid
and dt.reservestockmanagment = 'Y'
and o.dateordered::date <= dateTo::date
and t.movementdate::date <= dateTo::date
and t.movementdate::date <= current_date
union all
--Cantidades transferidas
select coalesce(sum(ol.qtyordered * -1),0) as qty
from c_orderline ol
inner join c_order o on o.c_order_id = ol.c_order_id
inner join c_orderline rl on rl.c_orderline_id = ol.ref_orderline_id
inner join c_order r on r.c_order_id = rl.c_order_id
inner join c_doctype dt on dt.c_doctype_id = o.c_doctype_id
inner join m_warehouse w on w.m_warehouse_id = o.m_warehouse_id
inner join m_locator l on l.m_warehouse_id = w.m_warehouse_id
where o.ad_client_id = clientid
and o.ad_org_id = orgid
and o.processed = 'Y' 
and ol.m_product_id = productid
and l.m_locator_id = locatorid
and o.issotrx = 'Y'
and dt.doctypekey IN ('SOSOT')
and r.dateordered::date <= dateTo::date
and o.dateordered::date <= dateTo::date
and o.dateordered::date <= current_date
) todo;
END IF;*/

return reserved;
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;
ALTER FUNCTION getqtyreserved(integer, integer, integer, integer, date)
OWNER TO libertya;

--Nueva función para calcular la cantidad reservada de un pedido
CREATE OR REPLACE FUNCTION calculateqtyreserved(orderlineid integer)
RETURNS numeric(22,4) AS
$BODY$
/***********
* Obtener la cantidad reservada de la línea de pedido parámetro. 
**/
DECLARE
reserved numeric(22,4);
r record;
BEGIN
reserved := 0;

--Verificar si la compañía permite entregar devoluciones
select into r ol.qtyordered, ol.qtydelivered, ol.qtytransferred, ol.qtyreturned, ci.allowdeliveryreturned 
from ad_clientinfo ci 
join c_orderline ol on ol.ad_client_id = ci.ad_client_id
where ol.c_orderline_id = orderlineid;

-- Cálculo del reservado:
-- Cantidad pedida - 
-- Cantidad entregada - 
-- Cantidad transferida -
-- Cantidad devuelta, sii no se permite entregar devoluciones
reserved = r.qtydelivered + r.qtytransferred;

IF r.allowdeliveryreturned = 'N' THEN
reserved = reserved + r.qtyreturned;
END IF;

reserved = r.qtyordered - reserved;

return reserved;
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;
ALTER FUNCTION calculateqtyreserved(integer)
OWNER TO libertya;

--Mejora a la vista del informe de seguimiento de pedidos/notas de credito
DROP VIEW rv_orderline_pending;

CREATE OR REPLACE VIEW rv_orderline_pending AS 
SELECT o.ad_client_id, o.ad_org_id, o.isactive, o.created, o.createdby, o.updated, o.updatedby, o.c_order_id, o.documentno, o.dateordered::date AS dateordered, o.datepromised::date AS datepromised, o.c_bpartner_id, o.issotrx, ol.c_orderline_id, ol.m_product_id, ol.qtyordered, ol.qtyinvoiced, ol.qtydelivered, ol.qtyordered - ol.qtyinvoiced AS pendinginvoice, ol.qtyreserved AS pendingdeliver, 
CASE
WHEN ol.qtyordered <> ol.qtyinvoiced AND ol.qtyreserved <> 0 THEN NULL::text
WHEN ol.qtyordered <> ol.qtyinvoiced AND ol.qtyreserved = 0 THEN 'I'::text
WHEN ol.qtyreserved <> 0 THEN 'D'::text
ELSE 'N'::text
END AS status
FROM c_order o
JOIN c_orderline ol ON o.c_order_id = ol.c_order_id AND (ol.qtyreserved <> 0 OR ol.qtyordered <> ol.qtyinvoiced) AND (o.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) AND ol.m_product_id IS NOT NULL
ORDER BY o.c_order_id;