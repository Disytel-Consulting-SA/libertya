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

