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