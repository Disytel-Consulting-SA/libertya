-- ========================================================================================
-- PREINSTALL FROM 11.10
-- ========================================================================================
-- 	Consideraciones importantes:
--			1) NO hacer cambios en el archivo, realizar siempre APPENDs al final del mismo 
-- 			2) Recordar realizar las adiciones con un comentario con formato YYYYMMDD-HHMM
-- ========================================================================================

-- 20111014-1400 Incorporación de nueva columna para registrar la guía de transporte
ALTER TABLE m_transfer ADD COLUMN transport_guide character varying(100);

-- 20111014-1600 Incorporación de nueva columna para registrar el formulario papel
ALTER TABLE m_inventory ADD COLUMN paper_form character varying(100);

-- 20111014-1754 Fix a la vista ya que la posición del - o + es la segunda del string
CREATE OR REPLACE VIEW rv_rotationreport_v2 AS 
 SELECT io.ad_org_id, pg.m_product_gamas_id, pl.m_product_lines_id, io.issotrx, io.ad_client_id, io.movementdate AS movementdatefrom, io.movementdate AS movementdateto, ol.m_product_id, p.m_product_category_id, io.m_inout_id, io.movementtype, ol.m_inoutline_id, 
        CASE
            WHEN charat(io.movementtype::character varying, 2)::text = '-'::text THEN sum(ol.movementqty)
            ELSE 0::numeric
        END AS movementout, 
        CASE
            WHEN charat(io.movementtype::character varying, 2)::text = '+'::text THEN sum(ol.movementqty)
            ELSE 0::numeric
        END AS movementin
   FROM m_inout io
   JOIN m_inoutline ol ON ol.m_inout_id = io.m_inout_id
   JOIN m_product p ON p.m_product_id = ol.m_product_id
   JOIN m_product_category pc ON pc.m_product_category_id = p.m_product_category_id
   JOIN c_bpartner b ON b.c_bpartner_id = io.c_bpartner_id
   LEFT JOIN m_product_gamas pg ON pg.m_product_gamas_id = pc.m_product_gamas_id
   LEFT JOIN m_product_lines pl ON pl.m_product_lines_id = pg.m_product_lines_id
  GROUP BY io.ad_client_id, io.ad_org_id, pg.m_product_gamas_id, pl.m_product_lines_id, io.issotrx, io.movementdate, ol.m_product_id, p.m_product_category_id, io.m_inout_id, io.movementtype, ol.m_inoutline_id;

ALTER TABLE rv_rotationreport_v2 OWNER TO libertya;

-- 20111014-1802 Incorporación de nueva columna para registrar el nro pre-impreso
ALTER TABLE m_inout ADD COLUMN preprinted_docNo character varying(100);

-- 20111019-0927 Nueva vista con los movimientos de mercadería existentes, utilizada por la impresión de Cierre de Almacén
CREATE OR REPLACE VIEW v_movements AS 
(((( SELECT 'M_InOut'::character varying AS tablename, io.ad_client_id, io.ad_org_id, io.m_inout_id AS movement_document_id, io.documentno, io.movementdate::date AS movement_date, io.m_warehouse_id, 0 AS m_warehouseto_id, io.c_bpartner_id, io.description, io.docstatus, io.c_charge_id, io.c_doctype_id, dc.name AS doctypename
   FROM m_inout io
   LEFT JOIN c_doctype dc ON dc.c_doctype_id = io.c_doctype_id
UNION ALL 
 SELECT 'M_Inventory'::character varying AS tablename, i.ad_client_id, i.ad_org_id, i.m_inventory_id AS movement_document_id, i.documentno, i.movementdate::date AS movement_date, i.m_warehouse_id, 0 AS m_warehouseto_id, 0 AS c_bpartner_id, i.description, i.docstatus, i.c_charge_id, i.c_doctype_id, dc.name AS doctypename
   FROM m_inventory i
   LEFT JOIN c_doctype dc ON dc.c_doctype_id = i.c_doctype_id)
UNION ALL 
 SELECT 'M_ProductChange'::character varying AS tablename, pc.ad_client_id, pc.ad_org_id, pc.m_productchange_id AS movement_document_id, pc.documentno, pc.datetrx::date AS movement_date, pc.m_warehouse_id, 0 AS m_warehouseto_id, 0 AS c_bpartner_id, pc.description, pc.docstatus, 0 AS c_charge_id, 0 AS c_doctype_id, 'M_ProductChange_ID' AS doctypename
   FROM m_productchange pc)
UNION ALL 
 SELECT 'M_Splitting'::character varying AS tablename, s.ad_client_id, s.ad_org_id, s.m_splitting_id AS movement_document_id, s.documentno, s.datetrx::date AS movement_date, s.m_warehouse_id, 0 AS m_warehouseto_id, 0 AS c_bpartner_id, s.comments AS description, s.docstatus, 0 AS c_charge_id, 0 AS c_doctype_id, 'M_Splitting_ID' AS doctypename
   FROM m_splitting s)
UNION ALL 
 SELECT 'M_Transfer'::character varying AS tablename, t.ad_client_id, t.ad_org_id, t.m_transfer_id AS movement_document_id, t.documentno, t.datetrx::date AS movement_date, t.m_warehouse_id, t.m_warehouseto_id, 0 AS c_bpartner_id, t.description, t.docstatus, t.c_charge_id, 0 AS c_doctype_id, t.transfertype AS doctypename
   FROM m_transfer t)
UNION ALL 
 SELECT 'M_Movement'::character varying AS tablename, m.ad_client_id, m.ad_org_id, m.m_movement_id AS movement_document_id, m.documentno, m.movementdate::date AS movement_date, ws.m_warehouse_id, ws.m_warehouseto_id, 0 AS c_bpartner_id, m.description, m.docstatus, 0 AS c_charge_id, m.c_doctype_id, dc.name AS doctypename
   FROM m_movement m
   JOIN ( SELECT DISTINCT l.m_warehouse_id, ld.m_warehouse_id AS m_warehouseto_id, ml.m_movement_id
           FROM m_movementline ml
      JOIN m_locator l ON l.m_locator_id = ml.m_locator_id
   JOIN m_locator ld ON ld.m_locator_id = ml.m_locatorto_id
  ORDER BY l.m_warehouse_id, ld.m_warehouse_id, ml.m_movement_id) ws ON ws.m_movement_id = m.m_movement_id
   LEFT JOIN c_doctype dc ON dc.c_doctype_id = m.c_doctype_id;

ALTER TABLE v_movements OWNER TO libertya;

-- 20111019-1540 Nuevas columnas para la funcionalidad de habilitación de tipos de documento para TPV junto con sus días de habilitación
ALTER TABLE c_doctype ADD COLUMN enabledinpos character(1) NOT NULL DEFAULT 'Y'::bpchar;
ALTER TABLE c_doctype ADD COLUMN posenabledue character(1) NOT NULL DEFAULT 'N'::bpchar;
ALTER TABLE c_doctype ADD COLUMN posenableduedays INTEGER NOT NULL DEFAULT 0;

-- 20111021-1522 Nuevas columnas para la funcionalidad de vencimientos de claves
ALTER TABLE ad_user ADD COLUMN lastpasswordchangedate date;
ALTER TABLE ad_clientinfo ADD COLUMN passwordexpirationactive character(1) NOT NULL DEFAULT 'N'::bpchar;
ALTER TABLE ad_clientinfo ADD COLUMN passwordexpirationdays INTEGER NOT NULL DEFAULT 0;

-- 20111031-1722 Nueva columna para la funcionalidad de claves únicas a partir de la clave propia del usuario
ALTER TABLE ad_clientinfo ADD COLUMN uniquekeyactive character(1) NOT NULL DEFAULT 'N'::bpchar;

-- 20111115-1400 Nuevas columnas de configuración del TPV para devoluciones en efectivo de notas de crédito
ALTER TABLE c_pos ADD COLUMN returnedcashincncontrol character(1) NOT NULL DEFAULT 'N'::bpchar;
ALTER TABLE c_pos ADD COLUMN maxreturnedcashincn numeric(22,2) NOT NULL DEFAULT 0;

-- 20111202-1635 Nuevas columnas para registrar la cantidad de vida útil y el porcentaje anual de amortización para las subfamilias y los artículos
ALTER TABLE m_product_category ADD COLUMN yearlife integer NOT NULL DEFAULT 0;
ALTER TABLE m_product_category ADD COLUMN amortizationperc numeric(9,2) NOT NULL DEFAULT 0;
ALTER TABLE m_product ADD COLUMN yearlife integer NOT NULL DEFAULT 0;
ALTER TABLE m_product ADD COLUMN amortizationperc numeric(9,2) NOT NULL DEFAULT 0;

-- 20111202-1636 Nueva columna para los atributos para determinar si es sólo lectura
ALTER TABLE m_attribute ADD COLUMN isreadonly character(1) NOT NULL DEFAULT 'N'::bpchar;

-- 20111202-1637 Valor de fecha para instancias de atributos
ALTER TABLE m_attributeinstance ADD COLUMN valuedate timestamp;

-- 20111202-1638 Columna que determina si el atributo de un conjunto de atributos se debe colocar como parte de la descripción de la instancia del conjunto de atributos
ALTER TABLE m_attributeuse ADD COLUMN isdescription character(1) NOT NULL DEFAULT 'Y'::bpchar;

-- 20120209-1153 - PATCH: Linea agregada fuera de sincronia, para solucionar problema con funcion addcolumnifnotexists
alter table ad_system add column dummy numeric null;

-- 20111212-1359 Columnas para fix de Cobro en USD de factura en ARS
update ad_system set dummy = (SELECT addcolumnifnotexists('c_cashline','cashamount', 'numeric(20,2)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_cashline','c_cashcurrency_id','integer'));
-- 20111212-1359 Borra objetos de un Patch que se distribuyó con el fix de Caja USD->ARS
DELETE FROM ad_field WHERE ad_componentobjectuid = 'PLCM-AD_Field-1017556';
DELETE FROM ad_field WHERE ad_componentobjectuid = 'PLCM-AD_Field-1017555';
DELETE FROM ad_column WHERE ad_componentobjectuid = 'PLCM-AD_Column-1015328';
DELETE FROM ad_column WHERE ad_componentobjectuid = 'PLCM-AD_Column-1015327';
DELETE FROM ad_reference WHERE ad_componentobjectuid = 'PLCM-AD_Reference-1010191';
DELETE FROM ad_element WHERE ad_componentobjectuid = 'PLCM-AD_Element-1011087';
DELETE FROM ad_element WHERE ad_componentobjectuid = 'PLCM-AD_Element-1011086';

-- 20111214-1124 Nueva columna para poder configurar que los nros de serie de instancias de conjuntos de atributos contengan como prefijo la clave de línea de artículo, familia y subfamilia
ALTER TABLE m_attributeset ADD COLUMN useproductrelationslikesernoprefix character(1) NOT NULL DEFAULT 'N'::bpchar;

-- 20111214-1125 Columna para registrar el costo de los bienes de uso cuando ingresan por entrada simple
ALTER TABLE m_inventoryline ADD COLUMN cost numeric(22,9) NOT NULL DEFAULT 0;

-- 20111214-1126 Columna para registrar la línea de factura en la línea del remito cuando se crea un remito a partir de una factura
ALTER TABLE m_inoutline ADD COLUMN c_invoiceline_id integer;

-- 20111214-1128 Eliminación de constraint porque no se puede eliminar una línea de remito donde una la línea de factura la referencie
ALTER TABLE c_invoiceline drop CONSTRAINT minoutline_cinvoiceline;

-- 20111214-1202 Modificación de stored procedures contemplando el stock para bienes de uso
CREATE OR REPLACE FUNCTION bomqtyonhand(p_product_id integer, p_warehouse_id integer, p_locator_id integer)
  RETURNS numeric AS
$BODY$
DECLARE
	v_Warehouse_ID		INTEGER;
	v_Quantity		NUMERIC := 99999;	--	unlimited
	v_IsBOM			CHAR(1);
	v_IsStocked		CHAR(1);
	v_ProductType		CHAR(1);
	v_ProductQty		NUMERIC;
	v_StdPrecision		NUMERIC;
	bom RECORD;

BEGIN
	
	--	Check Parameters
		v_Warehouse_ID := p_Warehouse_ID;
		IF (v_Warehouse_ID IS NULL) THEN
			IF (p_Locator_ID IS NULL) THEN
				RETURN 0;
			ELSE
				SELECT 	MAX(M_Warehouse_ID) INTO v_Warehouse_ID
				FROM	M_Locator
				WHERE	M_Locator_ID=p_Locator_ID;
			END IF;
		END IF;
		IF (v_Warehouse_ID IS NULL) THEN
			RETURN 0;
		END IF;
	--	DBMS_OUTPUT.PUT_LINE('Warehouse=' || v_Warehouse_ID);
	
		--	Check, if product exists and if it is stocked
		BEGIN
			SELECT	IsBOM, ProductType, IsStocked
			  INTO STRICT	v_IsBOM, v_ProductType, v_IsStocked
			FROM M_Product
			WHERE M_Product_ID=p_Product_ID;
			--
		
		EXCEPTION	--	not found
			WHEN OTHERS THEN
				RETURN 0;
		END;
	
		-- Unlimited capacity if no item : Nota; aca Libertya retonra 0; Adempiere 3.6.0 retorna ilimitado, lo cual tiene sentido
		IF (v_IsBOM='N' AND ((v_ProductType<>'I' AND v_ProductType<>'A') OR v_IsStocked='N')) THEN
			RETURN v_Quantity; -- unlimited; tomando la nueva forma
		--	Stocked item
		ELSIF (v_IsStocked='Y') THEN
			--	Get ProductQty
			-- NOTA: Adempiere solo toma la suma, si importar si M_Locator_ID es distinto de Cero; Libertya, no; agregmos
			-- la condicion siguiente para respetar la fomra de Liberyta
			IF (p_locator_id IS NOT NULL AND p_locator_id <>0) THEN
				SELECT 	COALESCE(SUM(QtyOnHand), 0)
					INTO	v_ProductQty
				FROM 	M_Storage s
				WHERE M_Product_ID=p_Product_ID
				AND s.M_Locator_ID=p_locator_id;
			-- suma en todas las locaciones del almacen	
			ELSE
				SELECT 	COALESCE(SUM(QtyOnHand), 0)
					INTO	v_ProductQty
				FROM 	M_Storage s
				WHERE M_Product_ID=p_Product_ID
				AND EXISTS (SELECT * FROM M_Locator l WHERE s.M_Locator_ID=l.M_Locator_ID
							AND l.M_Warehouse_ID=v_Warehouse_ID);
			END IF;	
			--
			RETURN v_ProductQty;
		END IF;
	
		--	Go though BOM

		FOR bom IN  
			SELECT b.M_ProductBOM_ID, b.BOMQty, p.IsBOM, p.IsStocked, p.ProductType
			FROM M_Product_BOM b, M_Product p
			WHERE b.M_ProductBOM_ID=p.M_Product_ID
		  	AND b.M_Product_ID=p_Product_ID
		LOOP
			--	Stocked Items "leaf node"
			IF (bom.ProductType = 'I' AND bom.IsStocked = 'Y') THEN
				--	Get ProductQty
				-- NOTA idem anterior (aunque en productos BOM tal vez no tenga mucho sentido...): Adempiere solo toma la suma, si
				--importar si M_Locator_ID es distinto de Cero; Libertya, no; agregmos
				-- la condicion siguiente para respetar la fomra de Liberyta
				IF (p_locator_id IS NOT NULL AND p_locator_id <>0) THEN
					SELECT 	COALESCE(SUM(QtyOnHand), 0)
						INTO	v_ProductQty
					FROM 	M_Storage s
					WHERE M_Product_ID=p_Product_ID
					AND s.M_Locator_ID=p_locator_id;
				-- suma en todas las locaciones del almacen	
				ELSE
					SELECT 	COALESCE(SUM(QtyOnHand), 0)
						INTO	v_ProductQty
					FROM 	M_Storage s
					WHERE 	M_Product_ID=bom.M_ProductBOM_ID
					AND EXISTS (SELECT * FROM M_Locator l WHERE s.M_Locator_ID=l.M_Locator_ID
						AND l.M_Warehouse_ID=v_Warehouse_ID);
				END IF;		
				--	Get Rounding Precision
				SELECT 	COALESCE(MAX(u.StdPrecision), 0)
				  INTO	v_StdPrecision
				FROM 	C_UOM u, M_Product p 
				WHERE 	u.C_UOM_ID=p.C_UOM_ID AND p.M_Product_ID=bom.M_ProductBOM_ID;
				--	How much can we make with this product
				v_ProductQty := ROUND (v_ProductQty/bom.BOMQty, v_StdPrecision);
				--	How much can we make overall
				IF (v_ProductQty < v_Quantity) THEN
					v_Quantity := v_ProductQty;
				END IF;
			--	Another BOM
			ELSIF (bom.IsBOM = 'Y') THEN
				v_ProductQty := bomQtyOnHand (bom.M_ProductBOM_ID, v_Warehouse_ID, p_Locator_ID);
				--	How much can we make overall
				IF (v_ProductQty < v_Quantity) THEN
					v_Quantity := v_ProductQty;
				END IF;
			END IF;
		END LOOP;	--	BOM
	
	
		IF (v_Quantity > 0) THEN
			--	Get Rounding Precision for Product
			SELECT 	COALESCE(MAX(u.StdPrecision), 0)
			  INTO	v_StdPrecision
			FROM 	C_UOM u, M_Product p 
			WHERE 	u.C_UOM_ID=p.C_UOM_ID AND p.M_Product_ID=p_Product_ID;
			--
			RETURN ROUND (v_Quantity, v_StdPrecision);
		END IF;
	RETURN 0;
	
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE;
ALTER FUNCTION bomqtyonhand(integer, integer, integer) OWNER TO libertya;

CREATE OR REPLACE FUNCTION bomqtyreserved(p_product_id integer, p_warehouse_id integer, p_locator_id integer)
  RETURNS numeric AS
$BODY$
DECLARE
	v_Warehouse_ID		INTEGER;
	v_Quantity		NUMERIC := 99999;	--	unlimited
	v_IsBOM			CHAR(1);
	v_IsStocked		CHAR(1);
	v_ProductType		CHAR(1);
	v_ProductQty		NUMERIC;
	v_StdPrecision		NUMERIC;
	bom RECORD;

BEGIN
	
	--	Check Parameters
		v_Warehouse_ID := p_Warehouse_ID;
		IF (v_Warehouse_ID IS NULL) THEN
			IF (p_Locator_ID IS NULL) THEN
				RETURN 0;
			ELSE
				SELECT 	MAX(M_Warehouse_ID) INTO v_Warehouse_ID
				FROM	M_Locator
				WHERE	M_Locator_ID=p_Locator_ID;
			END IF;
		END IF;
		IF (v_Warehouse_ID IS NULL) THEN
			RETURN 0;
		END IF;
	--	DBMS_OUTPUT.PUT_LINE('Warehouse=' || v_Warehouse_ID);
	
		--	Check, if product exists and if it is stocked
		BEGIN
			SELECT	IsBOM, ProductType, IsStocked
			  INTO STRICT	v_IsBOM, v_ProductType, v_IsStocked
			FROM M_Product
			WHERE M_Product_ID=p_Product_ID;
			--
		
		EXCEPTION	--	not found
			WHEN OTHERS THEN
				RETURN 0;
		END;
	
		-- --	No reservation for non-stocked
		IF (v_IsBOM='N' AND ((v_ProductType<>'I' AND v_ProductType<>'A') OR v_IsStocked='N')) THEN
			RETURN 0; -- a diferencia de OnHand, aca se retorna 0 (en OnHand unlimited)
		--	Stocked item
		ELSIF (v_IsStocked='Y') THEN
			--	Get ProductQty
			-- NOTA: Adempiere solo toma la suma, si importar si M_Locator_ID es distinto de Cero; Libertya, no; agregmos
			-- la condicion siguiente para respetar la fomra de Liberyta
			IF (p_locator_id IS NOT NULL AND p_locator_id <>0) THEN
				SELECT 	COALESCE(SUM(QtyReserved), 0)
					INTO	v_ProductQty
				FROM 	M_Storage s
				WHERE M_Product_ID=p_Product_ID
				AND s.M_Locator_ID=p_locator_id;
			-- suma en todas las locaciones del almacen	
			ELSE
				SELECT 	COALESCE(SUM(QtyReserved), 0)
					INTO	v_ProductQty
				FROM 	M_Storage s
				WHERE M_Product_ID=p_Product_ID
				AND EXISTS (SELECT * FROM M_Locator l WHERE s.M_Locator_ID=l.M_Locator_ID
							AND l.M_Warehouse_ID=v_Warehouse_ID);
			END IF;	
			--
			RETURN v_ProductQty;
		END IF;
	
		--	Go though BOM

		FOR bom IN  
			SELECT b.M_ProductBOM_ID, b.BOMQty, p.IsBOM, p.IsStocked, p.ProductType
			FROM M_Product_BOM b, M_Product p
			WHERE b.M_ProductBOM_ID=p.M_Product_ID
		  	AND b.M_Product_ID=p_Product_ID
		LOOP
			--	Stocked Items "leaf node"
			IF (bom.ProductType = 'I' AND bom.IsStocked = 'Y') THEN
				--	Get ProductQty
				-- NOTA idem anterior (aunque en productos BOM tal vez no tenga mucho sentido...): Adempiere solo toma la suma, si
				--importar si M_Locator_ID es distinto de Cero; Libertya, no; agregmos
				-- la condicion siguiente para respetar la fomra de Liberyta
				IF (p_locator_id IS NOT NULL AND p_locator_id <>0) THEN
					SELECT 	COALESCE(SUM(QtyReserved), 0)
						INTO	v_ProductQty
					FROM 	M_Storage s
					WHERE M_Product_ID=p_Product_ID
					AND s.M_Locator_ID=p_locator_id;
				-- suma en todas las locaciones del almacen	
				ELSE
					SELECT 	COALESCE(SUM(QtyReserved), 0)
						INTO	v_ProductQty
					FROM 	M_Storage s
					WHERE 	M_Product_ID=bom.M_ProductBOM_ID
					AND EXISTS (SELECT * FROM M_Locator l WHERE s.M_Locator_ID=l.M_Locator_ID
						AND l.M_Warehouse_ID=v_Warehouse_ID);
				END IF;		
				--	Get Rounding Precision
				SELECT 	COALESCE(MAX(u.StdPrecision), 0)
				  INTO	v_StdPrecision
				FROM 	C_UOM u, M_Product p 
				WHERE 	u.C_UOM_ID=p.C_UOM_ID AND p.M_Product_ID=bom.M_ProductBOM_ID;
				--	How much can we make with this product
				v_ProductQty := ROUND (v_ProductQty/bom.BOMQty, v_StdPrecision);
				--	How much can we make overall
				IF (v_ProductQty < v_Quantity) THEN
					v_Quantity := v_ProductQty;
				END IF;
			--	Another BOM
			ELSIF (bom.IsBOM = 'Y') THEN
				v_ProductQty := bomQtyReserved (bom.M_ProductBOM_ID, v_Warehouse_ID, p_Locator_ID);
				--	How much can we make overall
				IF (v_ProductQty < v_Quantity) THEN
					v_Quantity := v_ProductQty;
				END IF;
			END IF;
		END LOOP;	--	BOM
	
	
		IF (v_Quantity > 0) THEN
			--	Get Rounding Precision for Product
			SELECT 	COALESCE(MAX(u.StdPrecision), 0)
			  INTO	v_StdPrecision
			FROM 	C_UOM u, M_Product p 
			WHERE 	u.C_UOM_ID=p.C_UOM_ID AND p.M_Product_ID=p_Product_ID;
			--
			RETURN ROUND (v_Quantity, v_StdPrecision);
		END IF;
	RETURN 0;
	
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE;
ALTER FUNCTION bomqtyreserved(integer, integer, integer) OWNER TO libertya;

CREATE OR REPLACE FUNCTION bomqtyordered(p_product_id integer, p_warehouse_id integer, p_locator_id integer)
  RETURNS numeric AS
$BODY$
DECLARE
	v_Warehouse_ID		INTEGER;
	v_Quantity		NUMERIC := 99999;	--	unlimited
	v_IsBOM			CHAR(1);
	v_IsStocked		CHAR(1);
	v_ProductType		CHAR(1);
	v_ProductQty		NUMERIC;
	v_StdPrecision		NUMERIC;
	bom RECORD;

BEGIN
	
	--	Check Parameters
		v_Warehouse_ID := p_Warehouse_ID;
		IF (v_Warehouse_ID IS NULL) THEN
			IF (p_Locator_ID IS NULL) THEN
				RETURN 0;
			ELSE
				SELECT 	MAX(M_Warehouse_ID) INTO v_Warehouse_ID
				FROM	M_Locator
				WHERE	M_Locator_ID=p_Locator_ID;
			END IF;
		END IF;
		IF (v_Warehouse_ID IS NULL) THEN
			RETURN 0;
		END IF;
	--	DBMS_OUTPUT.PUT_LINE('Warehouse=' || v_Warehouse_ID);
	
		--	Check, if product exists and if it is stocked
		BEGIN
			SELECT	IsBOM, ProductType, IsStocked
			  INTO STRICT	v_IsBOM, v_ProductType, v_IsStocked
			FROM M_Product
			WHERE M_Product_ID=p_Product_ID;
			--
		
		EXCEPTION	--	not found
			WHEN OTHERS THEN
				RETURN 0;
		END;
	
		-- --	No reservation for non-stocked
		IF (v_IsBOM='N' AND ((v_ProductType<>'I' AND v_ProductType<>'A') OR v_IsStocked='N')) THEN
			RETURN 0; -- a diferencia de OnHand, aca se retorna 0 (en OnHand unlimited)
		--	Stocked item
		ELSIF (v_IsStocked='Y') THEN
			--	Get ProductQty
			-- NOTA: Adempiere solo toma la suma, si importar si M_Locator_ID es distinto de Cero; Libertya, no; agregmos
			-- la condicion siguiente para respetar la fomra de Liberyta
			IF (p_locator_id IS NOT NULL AND p_locator_id <>0) THEN
				SELECT 	COALESCE(SUM(QtyOrdered), 0)
					INTO	v_ProductQty
				FROM 	M_Storage s
				WHERE M_Product_ID=p_Product_ID
				AND s.M_Locator_ID=p_locator_id;
			-- suma en todas las locaciones del almacen	
			ELSE
				SELECT 	COALESCE(SUM(QtyOrdered), 0)
					INTO	v_ProductQty
				FROM 	M_Storage s
				WHERE M_Product_ID=p_Product_ID
				AND EXISTS (SELECT * FROM M_Locator l WHERE s.M_Locator_ID=l.M_Locator_ID
							AND l.M_Warehouse_ID=v_Warehouse_ID);
			END IF;	
			--
			RETURN v_ProductQty;
		END IF;
	
		--	Go though BOM

		FOR bom IN  
			SELECT b.M_ProductBOM_ID, b.BOMQty, p.IsBOM, p.IsStocked, p.ProductType
			FROM M_Product_BOM b, M_Product p
			WHERE b.M_ProductBOM_ID=p.M_Product_ID
		  	AND b.M_Product_ID=p_Product_ID
		LOOP
			--	Stocked Items "leaf node"
			IF (bom.ProductType = 'I' AND bom.IsStocked = 'Y') THEN
				--	Get ProductQty
				-- NOTA idem anterior (aunque en productos BOM tal vez no tenga mucho sentido...): Adempiere solo toma la suma, si
				--importar si M_Locator_ID es distinto de Cero; Libertya, no; agregmos
				-- la condicion siguiente para respetar la fomra de Liberyta
				IF (p_locator_id IS NOT NULL AND p_locator_id <>0) THEN
					SELECT 	COALESCE(SUM(QtyOrdered), 0)
						INTO	v_ProductQty
					FROM 	M_Storage s
					WHERE M_Product_ID=p_Product_ID
					AND s.M_Locator_ID=p_locator_id;
				-- suma en todas las locaciones del almacen	
				ELSE
					SELECT 	COALESCE(SUM(QtyOrdered), 0)
						INTO	v_ProductQty
					FROM 	M_Storage s
					WHERE 	M_Product_ID=bom.M_ProductBOM_ID
					AND EXISTS (SELECT * FROM M_Locator l WHERE s.M_Locator_ID=l.M_Locator_ID
						AND l.M_Warehouse_ID=v_Warehouse_ID);
				END IF;		
				--	Get Rounding Precision
				SELECT 	COALESCE(MAX(u.StdPrecision), 0)
				  INTO	v_StdPrecision
				FROM 	C_UOM u, M_Product p 
				WHERE 	u.C_UOM_ID=p.C_UOM_ID AND p.M_Product_ID=bom.M_ProductBOM_ID;
				--	How much can we make with this product
				v_ProductQty := ROUND (v_ProductQty/bom.BOMQty, v_StdPrecision);
				--	How much can we make overall
				IF (v_ProductQty < v_Quantity) THEN
					v_Quantity := v_ProductQty;
				END IF;
			--	Another BOM
			ELSIF (bom.IsBOM = 'Y') THEN
				v_ProductQty := bomQtyOrdered (bom.M_ProductBOM_ID, v_Warehouse_ID, p_Locator_ID);
				--	How much can we make overall
				IF (v_ProductQty < v_Quantity) THEN
					v_Quantity := v_ProductQty;
				END IF;
			END IF;
		END LOOP;	--	BOM
	
	
		IF (v_Quantity > 0) THEN
			--	Get Rounding Precision for Product
			SELECT 	COALESCE(MAX(u.StdPrecision), 0)
			  INTO	v_StdPrecision
			FROM 	C_UOM u, M_Product p 
			WHERE 	u.C_UOM_ID=p.C_UOM_ID AND p.M_Product_ID=p_Product_ID;
			--
			RETURN ROUND (v_Quantity, v_StdPrecision);
		END IF;
	RETURN 0;
	
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE;
ALTER FUNCTION bomqtyordered(integer, integer, integer) OWNER TO libertya;

-- 20111214-1640 Nueva tabla para registrar los procesadores de amortizaciones
CREATE TABLE m_amortization_processor
(
  m_amortization_processor_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) WITH time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) WITH time zone,
  updatedby integer NOT NULL,
  name character varying(60) NOT NULL,
  description character varying(60),
  classname character varying(100),
  ad_componentobjectuid character varying(100),
  CONSTRAINT m_amortization_processor_key PRIMARY KEY (m_amortization_processor_id),
  CONSTRAINT m_amortization_processor_client FOREIGN KEY (ad_client_id)
      REFERENCES ad_client (ad_client_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT m_amortization_processor_org FOREIGN KEY (ad_org_id)
      REFERENCES ad_org (ad_org_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
ALTER TABLE m_amortization_processor OWNER TO libertya;

-- 20111214-1641 Nueva tabla para registrar los métodos de amortizaciones
CREATE TABLE m_amortization_method
(
  m_amortization_method_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) WITH time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) WITH time zone,
  updatedby integer NOT NULL,
  name character varying(60) NOT NULL,
  description character varying(60),
  m_amortization_processor_id integer NOT NULL,
  amortizationappperiod character(1),
  ad_componentobjectuid character varying(100),
  CONSTRAINT m_amortization_method_key PRIMARY KEY (m_amortization_method_id),
  CONSTRAINT m_amortization_method_client FOREIGN KEY (ad_client_id)
      REFERENCES ad_client (ad_client_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT m_amortization_method_org FOREIGN KEY (ad_org_id)
      REFERENCES ad_org (ad_org_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT m_amortization_method_processor FOREIGN KEY (m_amortization_processor_id)
      REFERENCES m_amortization_processor (m_amortization_processor_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
ALTER TABLE m_amortization_method OWNER TO libertya;

-- 20111214-1711 Nueva tabla para registrar las amortizaciones
CREATE TABLE m_amortization
(
  m_amortization_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) WITH time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) WITH time zone,
  updatedby integer NOT NULL,
  name character varying(60),
  description character varying(60),
  amortizationdate date NOT NULL,
  c_year_id integer,
  c_period_id integer,
  runamortizationprocess character(1),
  processing character(1),
  processed character(1) NOT NULL DEFAULT 'N'::bpchar,
  posted character(1) NOT NULL DEFAULT 'N'::bpchar,
  deleteamortization character(1),
  CONSTRAINT m_amortization_key PRIMARY KEY (m_amortization_id),
  CONSTRAINT m_amortization_client FOREIGN KEY (ad_client_id)
      REFERENCES ad_client (ad_client_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT m_amortization_org FOREIGN KEY (ad_org_id)
      REFERENCES ad_org (ad_org_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT m_amortization_year FOREIGN KEY (c_year_id)
      REFERENCES c_year (c_year_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT m_amortization_period FOREIGN KEY (c_period_id)
      REFERENCES c_period (c_period_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
ALTER TABLE m_amortization OWNER TO libertya;

-- 20111214-1745 Nueva tabla para registrar el detalle de las amortizaciones
CREATE TABLE m_amortizationline
(
  m_amortizationline_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) WITH time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) WITH time zone,
  updatedby integer NOT NULL,
  m_amortization_id integer NOT NULL,
  description character varying(60),
  m_product_id integer NOT NULL,
  m_attributesetinstance_id integer,
  processed character(1) NOT NULL DEFAULT 'N'::bpchar,
  unitcost numeric(22,2) NOT NULL DEFAULT 0,
  qty numeric(9,2) NOT NULL DEFAULT 0,
  totalcost numeric(22,2) NOT NULL DEFAULT 0,
  iniperiodamortizationamt numeric(22,2) NOT NULL DEFAULT 0,
  endperiodamortizationamt numeric(22,2) NOT NULL DEFAULT 0,
  amortizationamt numeric(22,2) NOT NULL DEFAULT 0,
  iniperiodresidualamt numeric(22,2) NOT NULL DEFAULT 0,
  endperiodresidualamt numeric(22,2) NOT NULL DEFAULT 0,
  residualamt numeric(22,2) NOT NULL DEFAULT 0,
  CONSTRAINT m_amortizationline_key PRIMARY KEY (m_amortization_id),
  CONSTRAINT m_amortizationline_client FOREIGN KEY (ad_client_id)
      REFERENCES ad_client (ad_client_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT m_amortizationline_org FOREIGN KEY (ad_org_id)
      REFERENCES ad_org (ad_org_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT m_amortizationline_amortization FOREIGN KEY (m_amortization_id)
      REFERENCES m_amortization (m_amortization_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT m_amortizationline_product FOREIGN KEY (m_product_id)
      REFERENCES m_product (m_product_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT m_amortizationline_masi FOREIGN KEY (m_attributesetinstance_id)
      REFERENCES m_attributesetinstance (m_attributesetinstance_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
ALTER TABLE m_amortizationline OWNER TO libertya;

-- 20111214-1840 Nueva Columna para registrar el método de amortización de la compañía
ALTER TABLE c_acctschema ADD COLUMN m_amortization_method_id integer;

-- 20111220-1146 Eliminación de contraint de clave única y recreación nueva por error en referencia a columna 
ALTER TABLE m_amortizationline DROP CONSTRAINT m_amortizationline_key;
ALTER TABLE m_amortizationline ADD CONSTRAINT m_amortizationline_key PRIMARY KEY (m_amortizationline_id);

-- 20111230-1237 Funciones para replicacion
CREATE OR REPLACE FUNCTION replication_is_record_replicated(p_repArray varchar)
RETURNS integer AS 
$BODY$
DECLARE 
unChar char;
	
BEGIN 
	-- recorrer el repArray.  Si en alguna posicion hay un valor diferente a 0 o 1, entonces ya fue replicado
	FOR i IN 1..length(p_repArray) LOOP
		IF substring(p_repArray, i, 1) <> '0' AND substring(p_repArray, i, 1) <> '1' THEN
			return 1;
		END IF;
	END LOOP;
	RETURN 0;
END;
$BODY$ 
LANGUAGE 'plpgsql' VOLATILE; 



-- TG_ARGV[0] es el ID de tabla
-- TG_ARGV[1] es el nombre de la tabla
CREATE OR REPLACE FUNCTION replication_event()
RETURNS trigger AS 
$BODY$
DECLARE 
	found integer; 
	replicationPos integer;
	v_newRepArray varchar; 
	aKeyColumn varchar;
	repSeq bigint;
BEGIN 

	-- =============== ELIMINACION =============== 
	IF (TG_OP = 'DELETE') THEN
		-- Omitir modificaciones si se recibe SKIP en el repArray
		IF (OLD.repArray = 'SKIP') THEN
			RETURN OLD;
		END IF;

		-- Si ya fue replicado el registro, impedir su eliminación
		IF replication_is_record_replicated(OLD.repArray) = 1 THEN
			RAISE EXCEPTION 'Imposible eliminar.  El registro ya fue replicado';
		END IF;	

		-- Todo en orden
		RETURN OLD;
	END IF;

	-- =============== INSERCION O ACTUALIZACION =============== 
	IF (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') THEN

		-- Si se está forzando un cambio en el repArray, entonces omitir seteos
		IF (TG_OP = 'UPDATE') THEN
			IF (OLD.repArray <> NEW.repArray) THEN
				return NEW;
			END IF;
		END IF;


		 -- Omitir modificaciones si se recibe SKIP en el repArray
		 IF (NEW.repArray = 'SKIP') THEN
			NEW.repArray := null;
			return NEW;
		 END IF;

		 -- Setear el retrieveUID en caso de no tenerlo
		 IF NEW.retrieveUID IS NULL OR NEW.retrieveUID = '' THEN 
			SELECT INTO replicationPos replicationArrayPos FROM AD_ReplicationHost WHERE thisHost = 'Y'; 
			IF replicationPos IS NULL THEN RAISE EXCEPTION 'Configuracion de Hosts incompleta: Ninguna sucursal tiene marca de Este Host'; END IF; 

			-- Formato de UID: h1_tableID-repSeq
			SELECT INTO repseq nextVal('repseq_' || TG_ARGV[1]);
			IF repseq IS NULL THEN RAISE EXCEPTION 'No hay definida una secuencia de replicacion para la tabla %', TG_ARGV[1]; END IF;
			NEW.retrieveUID := 'h'::varchar || replicationPos::varchar || '_' || repseq;
		END IF;
		 
		-- Validar si es una tabla marcada para replicacion
		SELECT INTO found replication_is_valid_table(NEW.AD_Client_ID, TG_ARGV[0]::int, NEW.retrieveUID); 
		IF found = 0 THEN RETURN NULL; END IF; 

		-- Marcar el registro para replicacion si corresponde
		-- Si es insercion, simplemente debe setearse a 1 donde corresponda
		IF (TG_OP = 'INSERT') THEN
			-- determinar el replication array
			SELECT INTO v_newRepArray replicationArray 
			FROM ad_tablereplication 
			WHERE ad_table_ID = TG_ARGV[0]::int;

			-- si no hay que replicar, finalizar el procesamiento
			IF v_newRepArray IS NULL OR v_newRepArray = '' THEN
				RETURN NEW;
			END IF;

			-- setear el repArray correspondiente
			NEW.repArray := v_newRepArray;

			RETURN NEW;
		-- Si es update, habra que modificar según el estado actual del registro
		-- 2 => 3 (Replicado a Replicar), 4 => 5 (Espera de Ack a Espera de Ack con cambios)
		ELSEIF (TG_OP = 'UPDATE') THEN 
				v_newRepArray := replace(OLD.repArray, '2', '3');
				NEW.repArray := replace(v_newRepArray, '4', '5');
				RETURN NEW;
		END IF;
	END IF;
END; 
$BODY$ 
LANGUAGE 'plpgsql' VOLATILE;

-- 20120104-1805 Incorporación de columnas para registrar el monto de alta y baja de bienes
ALTER TABLE m_amortizationline ADD COLUMN altaamt numeric(22,2) NOT NULL DEFAULT 0;
ALTER TABLE m_amortizationline ADD COLUMN bajaamt numeric(22,2) NOT NULL DEFAULT 0;

-- 20120109-1025 Columnas para registrar las cuentas contables de amortizaciones en la contabilidad por defecto 
ALTER TABLE C_AcctSchema_Default ADD COLUMN p_amortization_acct integer;
ALTER TABLE C_AcctSchema_Default ADD COLUMN p_amortization_realized_acct integer;

-- 20120109-1025 Columnas para registrar las cuentas contables de amortizaciones en la contabilidad de subfamilias
ALTER TABLE M_Product_Category_Acct ADD COLUMN p_amortization_acct integer;
ALTER TABLE M_Product_Category_Acct ADD COLUMN p_amortization_realized_acct integer;

-- 20120109-1026 Columnas para registrar las cuentas contables de amortizaciones en la contabilidad de artículos
ALTER TABLE M_Product_Acct ADD COLUMN p_amortization_acct integer;
ALTER TABLE M_Product_Acct ADD COLUMN p_amortization_realized_acct integer;

-- 20120109-1026 Columnas necesarias para la contabilización de amortizaciones
ALTER TABLE m_amortization ADD COLUMN c_doctype_id integer NOT NULL;
ALTER TABLE m_amortization ADD COLUMN documentno character varying(30) NOT NULL;
ALTER TABLE m_amortization ADD COLUMN c_currency_id integer NOT NULL;

-- 2012013-1429 - CAMBIOS EN FUNCIONES DE REPLICACION
-- TG_ARGV[0] es el ID de tabla
-- TG_ARGV[1] es el nombre de la tabla
CREATE OR REPLACE FUNCTION replication_event()
RETURNS trigger AS 
$BODY$
DECLARE 
	found integer; 
	replicationPos integer;
	v_newRepArray varchar; 
	aKeyColumn varchar;
	repSeq bigint;
BEGIN 

	-- =============== ELIMINACION =============== 
	IF (TG_OP = 'DELETE') THEN
		-- Omitir modificaciones si se recibe SKIP en el repArray
		IF (OLD.repArray = 'SKIP') THEN
			RETURN OLD;
		END IF;

		-- Si ya fue replicado el registro, impedir su eliminación
		IF replication_is_record_replicated(OLD.repArray) = 1 THEN
			RAISE EXCEPTION 'Imposible eliminar.  El registro ya fue replicado';
		END IF;	

		-- Todo en orden
		RETURN OLD;
	END IF;

	-- =============== INSERCION O ACTUALIZACION =============== 
	IF (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') THEN

		-- Si se está forzando un cambio en el repArray, entonces omitir seteos
		IF (TG_OP = 'UPDATE') THEN
			IF (OLD.repArray <> NEW.repArray) THEN
				return NEW;
			END IF;
		END IF;

		 -- Omitir modificaciones si se recibe SKIP en el repArray
		 IF (NEW.repArray = 'SKIP') THEN
			NEW.repArray := null;
			return NEW;
		 END IF;

		 -- Setear el retrieveUID en caso de no tenerlo
		 IF NEW.retrieveUID IS NULL OR NEW.retrieveUID = '' THEN 
			SELECT INTO replicationPos replicationArrayPos FROM AD_ReplicationHost WHERE thisHost = 'Y'; 
			IF replicationPos IS NULL THEN RAISE EXCEPTION 'Configuracion de Hosts incompleta: Ninguna sucursal tiene marca de Este Host'; END IF; 

			-- Formato de UID: h1_tableID-repSeq
			SELECT INTO repseq nextVal('repseq_' || TG_ARGV[1]);
			IF repseq IS NULL THEN RAISE EXCEPTION 'No hay definida una secuencia de replicacion para la tabla %', TG_ARGV[1]; END IF;
			NEW.retrieveUID := 'h'::varchar || replicationPos::varchar || '_' || repseq;
		END IF;
		 
		-- Validar si es una tabla marcada para replicacion
		SELECT INTO found replication_is_valid_table(NEW.AD_Client_ID, TG_ARGV[0]::int, NEW.retrieveUID); 
		IF found = 0 THEN RETURN NEW; END IF; 

		-- Marcar el registro para replicacion si corresponde
		-- Si es insercion, simplemente debe setearse a 1 donde corresponda
		IF (TG_OP = 'INSERT') THEN
			-- determinar el replication array
			SELECT INTO v_newRepArray replicationArray 
			FROM ad_tablereplication 
			WHERE ad_table_ID = TG_ARGV[0]::int;

			-- si no hay que replicar, finalizar el procesamiento
			IF v_newRepArray IS NULL OR v_newRepArray = '' THEN
				RETURN NEW;
			END IF;

			-- setear el repArray correspondiente (si tiene un 3 (replicacion bidireccional), 
			-- entonces setear un 1 en el registro a fin de que replique hacia otros hosts
			NEW.repArray := replace(v_newRepArray, '3', '1');

			RETURN NEW;
		-- Si es update, habra que modificar según el estado actual del registro
		-- 2 => 3 (Replicado a Replicar), 4 => 5 (Espera de Ack a Espera de Ack con cambios)
		ELSEIF (TG_OP = 'UPDATE') THEN 
				v_newRepArray := replace(OLD.repArray, '2', '3');
				NEW.repArray := replace(v_newRepArray, '4', '5');
				RETURN NEW;
		END IF;
	END IF;
END; 
$BODY$ 
LANGUAGE 'plpgsql' VOLATILE; 

-- 2012013-1429 - CAMBIOS EN FUNCIONES DE REPLICACION
CREATE OR REPLACE FUNCTION replication_is_valid_table(p_clientid integer, p_tableid integer, p_retrieveuid character varying)
  RETURNS integer AS
$BODY$
DECLARE
result int;
BEGIN
	-- Si p_retrieveUID es nulo entonces no se puede bitacorear
	IF p_retrieveUID IS NULL OR p_retrieveUID = '' THEN
		RETURN 0;
	END IF;
	
	-- Verificar si la tabla esta en la nomina de replicacion
	-- y si además posee al menos un 1 (direccional) o un 3 (bidireccional) en alguna posicion)
	SELECT INTO result COUNT(1)
	FROM ad_tablereplication
	WHERE ad_client_id = p_clientID
	AND ad_table_id = p_tableID
	AND (position('1' in replicationarray) > 0 OR position('3' in replicationarray) > 0);

	RETURN result;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE;

-- 20120125-1455 Modificación de vista para que no realice absoluto los montos de las facturas
CREATE OR REPLACE VIEW v_documents AS 
( SELECT 'C_Invoice' AS documenttable, i.c_invoice_id AS document_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, i.documentno, i.issotrx, i.docstatus, 
        CASE
            WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
            ELSE i.dateinvoiced
        END AS datetrx, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal AS amount, i.c_invoicepayschedule_id, ips.duedate
   FROM c_invoice_v i
   JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   LEFT JOIN c_invoicepayschedule ips ON i.c_invoicepayschedule_id = ips.c_invoicepayschedule_id
UNION ALL 
 SELECT 'C_Payment' AS documenttable, p.c_payment_id AS document_id, p.ad_client_id, p.ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt AS amount, NULL::integer AS c_invoicepayschedule_id, NULL::timestamp without time zone AS duedate
   FROM c_payment p
   JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id)
UNION ALL 
 SELECT 'C_CashLine' AS documenttable, cl.c_cashline_id AS document_id, cl.ad_client_id, cl.ad_org_id, cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby, 
        CASE
            WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id
            ELSE i.c_bpartner_id
        END AS c_bpartner_id, dt.c_doctype_id, 
        CASE
            WHEN cl.amount < 0.0 THEN 1
            ELSE (-1)
        END AS signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, '@line@'::text || cl.line::character varying::text AS documentno, 
        CASE
            WHEN cl.amount < 0.0 THEN 'N'::bpchar
            ELSE 'Y'::bpchar
        END AS issotrx, cl.docstatus, c.statementdate AS datetrx, c.dateacct, cl.c_currency_id, NULL::integer AS c_conversiontype_id, abs(cl.amount) AS amount, NULL::integer AS c_invoicepayschedule_id, NULL::timestamp without time zone AS duedate
   FROM c_cashline cl
   JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
   JOIN ( SELECT d.ad_client_id, d.c_doctype_id, d.name, d.printname
      FROM c_doctype d
     WHERE d.doctypekey::text = 'CMC'::text) dt ON cl.ad_client_id = dt.ad_client_id
   LEFT JOIN c_invoice i ON cl.c_invoice_id = i.c_invoice_id;

ALTER TABLE v_documents OWNER TO libertya;

-- 20120201-0915 Modificación de stored procedures contemplando el id de instancia
CREATE OR REPLACE FUNCTION bompricestd(m_product_id integer, m_pricelist_version_id integer, m_attributesetinstance_id integer)
  RETURNS numeric AS
$BODY$
DECLARE
    i_price NUMERIC;
BEGIN
    -- se deshabilitan los precios en instancias simplemente no usando m_attributesetinstance_id
    IF (m_attributesetinstance_id = 0) THEN
	select bomPriceStd(M_Product_ID,M_PriceList_Version_ID) into i_price;
    else	
	SELECT pricestd into i_price FROM M_ProductPriceInstance pi WHERE pi.M_PriceList_Version_ID=M_PriceList_Version_ID AND pi.M_Product_ID=M_Product_ID AND pi.M_AttributeSetInstance_ID=M_AttributeSetInstance_ID;
    END IF;
    return i_price;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION bompricestd(integer, integer, integer) OWNER TO libertya;

CREATE OR REPLACE FUNCTION bompricelist(m_product_id integer, m_pricelist_version_id integer, m_attributesetinstance_id integer)
  RETURNS numeric AS
$BODY$
DECLARE
    i_price NUMERIC;
BEGIN
    IF (m_attributesetinstance_id = 0) THEN
	select bomPriceList(M_Product_ID,M_PriceList_Version_ID) into i_price;
    else	
	SELECT pricelist into i_price FROM M_ProductPriceInstance pi WHERE pi.M_PriceList_Version_ID=M_PriceList_Version_ID AND pi.M_Product_ID=M_Product_ID AND pi.M_AttributeSetInstance_ID=M_AttributeSetInstance_ID;
    END IF;
    return i_price;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION bompricelist(integer, integer, integer) OWNER TO libertya;

CREATE OR REPLACE FUNCTION bompricelimit(m_product_id integer, m_pricelist_version_id integer, m_attributesetinstance_id integer)
  RETURNS numeric AS
$BODY$
DECLARE
    i_price NUMERIC;
BEGIN
    IF (m_attributesetinstance_id = 0) THEN
	select bomPriceLimit(M_Product_ID,M_PriceList_Version_ID) into i_price;
    else	
	SELECT pricelimit into i_price FROM M_ProductPriceInstance pi WHERE pi.M_PriceList_Version_ID=M_PriceList_Version_ID AND pi.M_Product_ID=M_Product_ID AND pi.M_AttributeSetInstance_ID=M_AttributeSetInstance_ID;
    END IF;
    return i_price;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION bompricelimit(integer, integer, integer) OWNER TO libertya;

-- 20120202-1300 Columna para marcar si se quiere imprimir el documento al completarlo
ALTER TABLE C_DocType ADD COLUMN isPrintAtCompleting character(1) NOT NULL DEFAULT 'N'::bpchar;

-- 20120309-1124 Tabla que se utilizara solo para eliminaciones.  Se cambian los nombres para homogeneizar con las restantes tablas
alter table ad_changelog_replication rename column recorduid to retrieveuid;
alter table ad_changelog_replication rename column replicationarray to reparray;

-- 20120313-0956 Incorporar logica para bitacorear eliminaciones
CREATE OR REPLACE FUNCTION replication_event()
  RETURNS trigger AS
$BODY$
DECLARE 
	found integer; 
	replicationPos integer;
	v_newRepArray varchar; 
	aKeyColumn varchar;
	repSeq bigint;
BEGIN 
	
	IF (TG_OP = 'DELETE') THEN
		-- Omitir bitacoreo de eliminacion.  El Skip no puede setearse al eliminar, primero se hace un UPDATE a SKIP del repArray y luego debe eliminarse el registro
		IF (OLD.repArray IS NULL OR OLD.repArray = '') THEN
			RETURN OLD;
		END IF;

		-- Si ya fue replicado el registro, debera eliminarse en los destinos
		IF replication_is_record_replicated(OLD.repArray) = 1 THEN
			-- determinar el replication array
			SELECT INTO v_newRepArray replicationArray 
			FROM ad_tablereplication 
			WHERE ad_table_ID = TG_ARGV[0]::int;

			IF v_newRepArray IS NOT NULL AND v_newRepArray <> '' THEN
				INSERT INTO ad_changelog_replication (AD_Changelog_Replication_ID, AD_Client_ID, AD_Org_ID, isActive, Created, CreatedBy, Updated, UpdatedBy, AD_Table_ID, retrieveUID, operationtype, binaryvalue, reparray, columnvalues)
				SELECT nextval('seq_ad_changelog_replication'),OLD.AD_Client_ID,OLD.AD_Org_ID,'Y',OLD.Created,OLD.CreatedBy,OLD.Updated,OLD.UpdatedBy,TG_ARGV[0]::int,OLD.retrieveUID,'I',null,v_newRepArray,null;
			END IF;
		END IF;	
		
		RETURN OLD;
	END IF;
	
	IF (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') THEN
		
		IF (TG_OP = 'UPDATE') THEN
			IF (OLD.repArray <> NEW.repArray) THEN
				return NEW;
			END IF;
		END IF;
		 
		 IF (NEW.repArray = 'SKIP') THEN
			NEW.repArray := NULL;
			return NEW;
		 END IF;
		 
		 IF NEW.retrieveUID IS NULL OR NEW.retrieveUID = '' THEN 
			SELECT INTO replicationPos replicationArrayPos FROM AD_ReplicationHost WHERE thisHost = 'Y'; 
			IF replicationPos IS NULL THEN RAISE EXCEPTION 'Configuracion de Hosts incompleta: Ninguna sucursal tiene marca de Este Host'; END IF; 
			
			SELECT INTO repseq nextVal('repseq_' || TG_ARGV[1]);
			IF repseq IS NULL THEN RAISE EXCEPTION 'No hay definida una secuencia de replicacion para la tabla %', TG_ARGV[1]; END IF;
			NEW.retrieveUID := 'h'::varchar || replicationPos::varchar || '_' || repseq;
		END IF;
		 
		
		SELECT INTO found replication_is_valid_table(NEW.AD_Client_ID, TG_ARGV[0]::int, NEW.retrieveUID); 
		IF found = 0 THEN RETURN NEW; END IF; 
		
		
		IF (TG_OP = 'INSERT') THEN
			
			SELECT INTO v_newRepArray replicationArray 
			FROM ad_tablereplication 
			WHERE ad_table_ID = TG_ARGV[0]::int;
			
			IF v_newRepArray IS NULL OR v_newRepArray = '' THEN
				RETURN NEW;
			END IF;
			
			
			NEW.repArray := replace(v_newRepArray, '3', '1');
			RETURN NEW;
		
		
		ELSEIF (TG_OP = 'UPDATE') THEN 
				v_newRepArray := replace(OLD.repArray, '2', '3');
				NEW.repArray := replace(v_newRepArray, '4', '5');
				RETURN NEW;
		END IF;
	END IF;
END; 
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION replication_event() OWNER TO libertya;

-- 20120316-0830 Vista para el informe de Declaración de Valores
CREATE OR REPLACE VIEW c_pos_declaracionvalores_v AS 
        (        (         SELECT i.ad_client_id, i.ad_org_id, i.c_posjournal_id, i.ad_user_id, i.c_currency_id, i.dateinvoiced AS datetrx, i.docstatus, NULL::unknown AS category, dt.docbasetype AS tendertype, (i.documentno::text || ' '::text) || COALESCE(i.description, ''::character varying)::text AS description, i.c_charge_id, i.chargename, i.c_invoice_id AS doc_id, 
                                CASE dt.signo_issotrx
                                    WHEN 1 THEN i.total - i.open
                                    WHEN (-1) THEN 0::numeric
                                    ELSE NULL::numeric
                                END::numeric(22,2) AS ingreso, 
                                CASE dt.signo_issotrx
                                    WHEN 1 THEN 0::numeric
                                    WHEN (-1) THEN i.total - i.open
                                    ELSE NULL::numeric
                                END::numeric(22,2) AS egreso
                           FROM ( SELECT DISTINCT i.ad_client_id, i.ad_org_id, i.documentno, inv.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, i.dateinvoiced::date AS dateinvoiced, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name AS chargename, currencybase(i.grandtotal, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2) AS total, currencybase(invoiceopen(i.c_invoice_id, 0), i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2) AS open
                                   FROM c_posjournalinvoices_v i
                              JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
                         JOIN c_invoice inv ON i.c_invoice_id = inv.c_invoice_id
                    LEFT JOIN c_charge ch ON ch.c_charge_id = inv.c_charge_id) i
                      JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id
                UNION ALL 
                         SELECT p.ad_client_id, p.ad_org_id, p.c_posjournal_id, p.ad_user_id, p.c_currency_id, p.datetrx, p.docstatus, NULL::unknown AS category, p.tendertype, (p.documentno::text || ' '::text) || COALESCE(p.description, ''::character varying)::text AS description, p.c_charge_id, p.chargename, p.c_payment_id AS doc_id, 
                                CASE p.isreceipt
                                    WHEN 'Y'::bpchar THEN p.total
                                    ELSE 0::numeric
                                END::numeric(22,2) AS ingreso, 
                                CASE p.isreceipt
                                    WHEN 'N'::bpchar THEN p.total
                                    ELSE 0::numeric
                                END::numeric(22,2) AS egreso
                           FROM ( SELECT p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date AS datetrx, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, p.c_currency_id, p.datetrx::timestamp with time zone, p.ad_client_id, p.ad_org_id)::numeric(22,2))::numeric(22,2) AS total
                                   FROM c_payment p
                              JOIN c_posjournalpayments_v pjp ON pjp.c_payment_id = p.c_payment_id
                         JOIN c_posjournal pj ON pj.c_posjournal_id = p.c_posjournal_id
                    LEFT JOIN c_charge ch ON ch.c_charge_id = p.c_charge_id
                   GROUP BY p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name) p)
        UNION ALL 
                 SELECT c.ad_client_id, c.ad_org_id, c.c_posjournal_id, c.ad_user_id, c.c_currency_id, c.datetrx, c.docstatus, c.cashtype AS category, c.tendertype, 
                        CASE
                            WHEN length(c.description::text) > 0 THEN c.description
                            ELSE c.info
                        END AS description, c.c_charge_id, c.chargename, c.c_cashline_id AS doc_id, 
                        CASE sign(c.total)
                            WHEN (-1) THEN 0::numeric
                            ELSE c.total
                        END::numeric(22,2) AS ingreso, 
                        CASE sign(c.total)
                            WHEN (-1) THEN c.total
                            ELSE 0::numeric
                        END::numeric(22,2) AS egreso
                   FROM ( SELECT cl.ad_client_id, cl.ad_org_id, cl.c_cashline_id, c.c_posjournal_id, pj.ad_user_id, cl.c_currency_id, c.statementdate::date AS datetrx, cl.docstatus, cl.description, pjp.info, pjp.tendertype, cl.cashtype, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, cl.c_currency_id, c.statementdate::timestamp with time zone, cl.ad_client_id, cl.ad_org_id)::numeric(22,2))::numeric(22,2) AS total
                           FROM c_cashline cl
                      JOIN c_cash c ON c.c_cash_id = cl.c_cash_id
                 JOIN c_posjournalpayments_v pjp ON pjp.c_cashline_id = cl.c_cashline_id
            JOIN c_posjournal pj ON pj.c_posjournal_id = c.c_posjournal_id
       LEFT JOIN c_charge ch ON ch.c_charge_id = cl.c_charge_id
      GROUP BY cl.ad_client_id, cl.ad_org_id, cl.c_cashline_id, c.c_posjournal_id, pj.ad_user_id, cl.c_currency_id, c.statementdate::date, cl.docstatus, cl.description, pjp.info, pjp.tendertype, cl.cashtype, ch.c_charge_id, ch.name) c)
UNION ALL 
         SELECT i.ad_client_id, i.ad_org_id, i.c_posjournal_id, i.ad_user_id, i.c_currency_id, i.dateinvoiced AS datetrx, i.docstatus, NULL::unknown AS category, i.tendertype, (i.documentno::text || ' '::text) || COALESCE(i.description, ''::character varying)::text AS description, i.c_charge_id, i.chargename, i.c_invoice_id AS doc_id, i.total AS ingreso, 0 AS egreso
           FROM ( SELECT i.ad_client_id, i.ad_org_id, i.documentno, i.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, pjp.tendertype, i.dateinvoiced::date AS dateinvoiced, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2))::numeric(22,2) AS total
                   FROM c_invoice i
              JOIN c_posjournalpayments_v pjp ON pjp.c_invoice_credit_id = i.c_invoice_id
         JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
    LEFT JOIN c_charge ch ON ch.c_charge_id = i.c_charge_id
   GROUP BY i.ad_client_id, i.ad_org_id, i.documentno, i.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, pjp.tendertype, i.dateinvoiced::date, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name) i
      JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id;

ALTER TABLE c_pos_declaracionvalores_v OWNER TO libertya;

--20120326-1655 Nueva columna proyecto a la tabla de cajas diarias
ALTER TABLE c_posjournal ADD COLUMN c_project_id integer;

--20120329-1920 Preinstall R3 - Kunan - Importación de Instancias
CREATE SEQUENCE libertya.seq_i_productinstance
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1010023
  CACHE 1;

--20120329-1920 Preinstall R3 - Kunan - Importación de Instancias
ALTER TABLE libertya.seq_i_productinstance OWNER TO libertya;

--20120329-1920 Preinstall R3 - Kunan - Importación de Instancias
CREATE TABLE libertya.i_productinstance
(
  i_productinstance_id integer NOT NULL DEFAULT nextval('libertya.seq_i_productinstance'::regclass),
  ad_client_id integer,
  ad_org_id integer,
  isactive character(1) DEFAULT 'Y'::bpchar,
  created timestamp without time zone DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer,
  updated timestamp without time zone DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer,
  i_isimported character(1) NOT NULL DEFAULT 'N'::bpchar,
  i_errormsg character varying(2000),
  m_product_id integer,
  m_attributesetinstance_id integer,
  upc character varying(30),
  product_value character(40),
  instance_description character(60),
  processing character(1),
  processed character(1) DEFAULT 'N'::bpchar,
  CONSTRAINT i_productinstance_key PRIMARY KEY (i_productinstance_id),
  CONSTRAINT mattributesetinstance_iproduct FOREIGN KEY (m_attributesetinstance_id)
      REFERENCES libertya.m_attributesetinstance (m_attributesetinstance_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT mproduct_iproduct FOREIGN KEY (m_product_id)
      REFERENCES libertya.m_product (m_product_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (OIDS=TRUE);
ALTER TABLE libertya.i_productinstance OWNER TO libertya;

--20120329-1920 Preinstall R4 - Kunan - Importación inicial de Inventario
ALTER TABLE libertya.i_inventory ADD COLUMN m_attributesetinstance_id integer;
ALTER TABLE libertya.i_inventory ALTER COLUMN m_attributesetinstance_id SET STORAGE PLAIN;
ALTER TABLE libertya.i_inventory ADD COLUMN instance_description character varying(255);
ALTER TABLE libertya.i_inventory ALTER COLUMN instance_description SET STORAGE EXTENDED;

--20120329-1920 Preinstall R11 - Kunan - Informe Existencias Valorizadas
ALTER TABLE libertya.t_inventoryvalue ADD COLUMN m_attributesetinstance_id numeric(10);
ALTER TABLE libertya.t_inventoryvalue ALTER COLUMN m_attributesetinstance_id SET STORAGE MAIN;
ALTER TABLE libertya.t_inventoryvalue ALTER COLUMN m_attributesetinstance_id SET DEFAULT 0;
ALTER TABLE libertya.t_inventoryvalue ADD COLUMN upc character varying(30);
ALTER TABLE libertya.t_inventoryvalue ALTER COLUMN upc SET STORAGE EXTENDED;
ALTER TABLE libertya.t_inventoryvalue ADD COLUMN pricevariationpercent numeric;
ALTER TABLE libertya.t_inventoryvalue ALTER COLUMN pricevariationpercent SET STORAGE MAIN;

--20120329-1920 Preinstall R11 - Kunan - Informe Existencias Valorizadas
CREATE SEQUENCE libertya.seq_t_inventoryvalue
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1010017
  CACHE 1;
ALTER TABLE libertya.seq_t_inventoryvalue OWNER TO libertya;

-- 20120506-1920 R11 - Kunan - Se eliminan los datos de la tabla temporal t_inventoryvalue antes de modificarla
DELETE FROM libertya.t_inventoryvalue;

--20120329-1920 Preinstall R11 - Kunan - Informe Existencias Valorizadas
ALTER TABLE libertya.t_inventoryvalue ADD COLUMN t_inventoryvalue_id integer;
ALTER TABLE libertya.t_inventoryvalue ALTER COLUMN t_inventoryvalue_id SET STORAGE PLAIN;
ALTER TABLE libertya.t_inventoryvalue ALTER COLUMN t_inventoryvalue_id SET NOT NULL;
ALTER TABLE libertya.t_inventoryvalue ALTER COLUMN t_inventoryvalue_id SET DEFAULT nextval('libertya.seq_t_inventoryvalue'::regclass);
ALTER TABLE libertya.t_inventoryvalue DROP CONSTRAINT t_inventoryvalue_key;
ALTER TABLE libertya.t_inventoryvalue ADD CONSTRAINT t_inventoryvalue_key PRIMARY KEY(t_inventoryvalue_id);

--20120329-1920 Preinstall R14 - Kunan - Filtro en la busqueda de pedidos desde el TPV
ALTER TABLE libertya.c_pos ADD COLUMN issearchtoday character(1);
ALTER TABLE libertya.c_pos ALTER COLUMN issearchtoday SET STORAGE EXTENDED;
UPDATE libertya.c_pos SET issearchtoday = 'N';
ALTER TABLE libertya.c_pos ALTER COLUMN issearchtoday SET NOT NULL;
ALTER TABLE libertya.c_pos ALTER COLUMN issearchtoday SET DEFAULT 'N'::bpchar;

--20120329-1920 Preinstall R15 - Kunan - Asociación entre pedido inicial y generado por el TPV
ALTER TABLE libertya.c_pos ADD COLUMN iscopyentity character(1);
ALTER TABLE libertya.c_pos ALTER COLUMN iscopyentity SET STORAGE EXTENDED;
UPDATE libertya.c_pos SET iscopyentity = 'N';
ALTER TABLE libertya.c_pos ALTER COLUMN iscopyentity SET NOT NULL;
ALTER TABLE libertya.c_pos ALTER COLUMN iscopyentity SET DEFAULT 'N'::bpchar;

--20120329-1920 Preinstall R16 - Kunan - En el módulo de Ventas el Ctrl+I no debe mostrar el precio de costo
ALTER TABLE libertya.ad_role ADD COLUMN viewsalesprice character(1);
ALTER TABLE libertya.ad_role ALTER COLUMN viewsalesprice SET STORAGE EXTENDED;
UPDATE libertya.ad_role SET viewsalesprice = 'Y';
ALTER TABLE libertya.ad_role ALTER COLUMN viewsalesprice SET NOT NULL;
ALTER TABLE libertya.ad_role ALTER COLUMN viewsalesprice SET DEFAULT 'Y'::bpchar;
ALTER TABLE libertya.ad_role ADD COLUMN viewpurchaseprice character(1);
ALTER TABLE libertya.ad_role ALTER COLUMN viewpurchaseprice SET STORAGE EXTENDED;
UPDATE libertya.ad_role SET viewpurchaseprice = 'Y';
ALTER TABLE libertya.ad_role ALTER COLUMN viewpurchaseprice SET NOT NULL;
ALTER TABLE libertya.ad_role ALTER COLUMN viewpurchaseprice SET DEFAULT 'Y'::bpchar;

--20120329-1920 Preinstall R17 - Kunan - Cambio de artículos por InstanciaCambio de artículos por Instancia
ALTER TABLE libertya.m_productchange ADD COLUMN m_attributesetinstance_id numeric(10);
ALTER TABLE libertya.m_productchange ALTER COLUMN m_attributesetinstance_id SET STORAGE MAIN;
ALTER TABLE libertya.m_productchange ADD COLUMN m_attributesetinstanceto_id numeric(10);
ALTER TABLE libertya.m_productchange ALTER COLUMN m_attributesetinstanceto_id SET STORAGE MAIN;

--20120330-1030 Registrar el lugar de salida de la mercadería en el pedido (TPV o Alamcén)
ALTER TABLE c_orderline ADD COLUMN checkoutplace character(1);

-- 20120403-1845 Nueva tabla temporal para informe Movimientos por Proveedor
CREATE TABLE t_vendormovements
(
  ad_pinstance_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  issotrx character(1),
  documentno character varying(30),
  c_doctype_id integer,
  dateacct timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  duedate timestamp without time zone,
  dateordered timestamp without time zone,
  c_bpartner_id integer,
  c_invoice_id integer,
  "name" character varying(60) NOT NULL,
  dateinvoiced timestamp without time zone NOT NULL,
  ispaid character(1) NOT NULL DEFAULT 'N'::bpchar,
  docbasetype character(3) NOT NULL,
  isfiscaldocument character(1) NOT NULL DEFAULT 'N'::bpchar,  
  CONSTRAINT adpinstance_t_vendormovements FOREIGN KEY (ad_pinstance_id)
      REFERENCES ad_pinstance (ad_pinstance_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=TRUE
);
ALTER TABLE t_vendormovements OWNER TO libertya;

-- 20120403-1920 Eliminación de la vista rv_vendor_payments - Se creará nuevamente con nuevas columnas requeridas
DROP VIEW rv_vendor_payments;

-- 20120403-1920 Nueva Vista para la modificación del informe Listar Pagos Emitidos
CREATE OR REPLACE VIEW rv_vendor_payments AS 
 SELECT p.ad_client_id, p.ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, bp.duns, bp.description, p.datetrx, p.documentno, p.checkno, p.payamt, p.c_bpartner_id
   FROM c_payment p
   JOIN c_bpartner bp ON p.c_bpartner_id = bp.c_bpartner_id
   JOIN c_doctype d ON p.c_doctype_id = d.c_doctype_id
  WHERE d.issotrx = 'N'::bpchar
  ORDER BY bp.duns, p.datetrx;

ALTER TABLE rv_vendor_payments OWNER TO libertya;

-- 20120404-1340 Nueva Vista para el informe Invoice Line Info Compras
CREATE OR REPLACE VIEW c_invoiceline_infocompras AS 
 SELECT i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, pc.M_Product_Category_ID, pc.name as Subfamilia, p.M_Product_ID, p.name, i.C_Invoice_ID, i.C_bpartner_ID,b.Name as Proveedor, i.dateinvoiced, il.C_InvoiceLine_ID, il.qtyinvoiced, il.qtyentered, il.priceentered, il.linenetamt, il.linetotalamt, il.description  
  FROM C_InvoiceLine il
  INNER JOIN C_Invoice i ON (il.C_Invoice_ID = i.C_Invoice_ID)
  INNER JOIN M_Product p ON (il.M_Product_ID = p.M_Product_ID)
  INNER JOIN M_Product_Category pc ON (p.M_Product_Category_ID = pc.M_Product_Category_ID)
  INNER JOIN C_BPartner b ON (b.C_BPartner_ID = i.C_BPartner_ID)
  WHERE (i.issotrx = 'N');

ALTER TABLE c_invoiceline_infocompras OWNER TO libertya;

-- 20120409-1500 Eliminación de la vista v_documents - Se creará nuevamente con nuevas columnas requeridas
DROP VIEW v_documents;

-- 20120409-1500 Creación de la Vista v_documents con nuevas columnas
CREATE OR REPLACE VIEW v_documents AS 
( SELECT 'C_Invoice' AS documenttable, i.c_invoice_id AS document_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, i.documentno, i.issotrx, i.docstatus, 
        CASE
            WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
            ELSE i.dateinvoiced
        END AS datetrx, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal AS amount, i.c_invoicepayschedule_id, ips.duedate
   FROM c_invoice_v i
   JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   LEFT JOIN c_invoicepayschedule ips ON i.c_invoicepayschedule_id = ips.c_invoicepayschedule_id
UNION ALL 
 SELECT 'C_Payment' AS documenttable, p.c_payment_id AS document_id, p.ad_client_id, p.ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt AS amount, NULL::integer AS c_invoicepayschedule_id, p.duedate 
   FROM c_payment p
   JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id)
UNION ALL 
 SELECT 'C_CashLine' AS documenttable, cl.c_cashline_id AS document_id, cl.ad_client_id, cl.ad_org_id, cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby, 
        CASE
            WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id
            ELSE i.c_bpartner_id
        END AS c_bpartner_id, dt.c_doctype_id, 
        CASE
            WHEN cl.amount < 0.0 THEN 1
            ELSE (-1)
        END AS signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, '@line@'::text || cl.line::character varying::text AS documentno, 
        CASE
            WHEN cl.amount < 0.0 THEN 'N'::bpchar
            ELSE 'Y'::bpchar
        END AS issotrx, cl.docstatus, c.statementdate AS datetrx, c.dateacct, cl.c_currency_id, NULL::integer AS c_conversiontype_id, abs(cl.amount) AS amount, NULL::integer AS c_invoicepayschedule_id, NULL::timestamp without time zone AS duedate
   FROM c_cashline cl
   JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
   JOIN ( SELECT d.ad_client_id, d.c_doctype_id, d.name, d.printname
      FROM c_doctype d
     WHERE d.doctypekey::text = 'CMC'::text) dt ON cl.ad_client_id = dt.ad_client_id
   LEFT JOIN c_invoice i ON cl.c_invoice_id = i.c_invoice_id;

ALTER TABLE v_documents OWNER TO libertya;

-- 20120409-1500 Creación de la Vista v_projectedpayments
CREATE OR REPLACE VIEW v_projectedpayments AS
( SELECT v.documenttable, v.document_id, v.ad_client_id, v.ad_org_id, v.isactive, v.created, v.createdby, v.updated, v.updatedby, v.c_bpartner_id, v.c_doctype_id, v.signo_issotrx, v.doctypename, v.doctypeprintname, v.documentno, v.issotrx, v.docstatus, v.datetrx, v.dateacct, v.c_currency_id, v.c_conversiontype_id, v.amount, v.c_invoicepayschedule_id, v.duedate, i.isPaid, NULL::character varying(20) as CheckNo 
FROM v_documents v
INNER JOIN C_Invoice i ON (i.c_invoice_id = v.document_id)
WHERE (i.issotrx = 'N') AND (documenttable = 'C_Invoice') )
UNION ALL 
( SELECT v.documenttable, v.document_id, v.ad_client_id, v.ad_org_id, v.isactive, v.created, v.createdby, v.updated, v.updatedby, v.c_bpartner_id, v.c_doctype_id, v.signo_issotrx, 'Cheque' AS doctypename, v.doctypeprintname, v.documentno, v.issotrx, v.docstatus, v.datetrx, v.dateacct, v.c_currency_id, v.c_conversiontype_id, v.amount, v.c_invoicepayschedule_id, v.duedate, NULL::character(1) as isPaid, p.CheckNo
FROM v_documents v
INNER JOIN C_Payment p ON (p.C_Payment_id = v.document_id)
WHERE (p.issotrx = 'N') AND (p.tendertype = 'K') AND (p.datetrx < p.duedate) );

ALTER TABLE v_projectedpayments OWNER TO libertya;

-- 20120411-1040 Creación de la Vista rv_ordernotinvoice
CREATE OR REPLACE VIEW rv_ordernotinvoice AS 
 select o.ad_client_id, o.ad_org_id, o.isactive, o.created, o.createdby, o.updated, o.updatedby, o.c_order_id, o.documentno, o.docstatus, o.docaction, o.c_doctype_id, o.c_doctypetarget_id, dateordered, c_BPartner_ID, o.totallines, o.grandtotal
 from C_Order o
 WHERE o.isactive = 'Y' AND o.issotrx = 'Y'
 EXCEPT
 select co.ad_client_id, co.ad_org_id, co.isactive, co.created, co.createdby, co.updated, co.updatedby, co.c_order_id, co.documentno, co.docstatus, co.docaction, co.c_doctype_id, co.c_doctypetarget_id, co.dateordered, co.c_BPartner_ID, co.totallines, co.grandtotal
 from c_invoice i
 INNER JOIN C_Order co ON (i.c_order_id = co.c_order_id)
 where i.c_order_id IS NOT NULL AND co.isactive = 'Y' AND co.issotrx = 'Y';
 
ALTER TABLE rv_ordernotinvoice OWNER TO libertya;

-- 20120411-1615 Creación de la Vista rv_salesorderwithdiscount
CREATE OR REPLACE VIEW rv_salesorderwithdiscount AS 
 SELECT o.ad_client_id, o.ad_org_id, o.isactive, o.created, o.createdby, o.updated, o.updatedby, o.c_order_id, o.documentno, o.docstatus, o.docaction, o.c_doctype_id, o.c_doctypetarget_id, o.dateordered, o.c_BPartner_ID, o.totallines, o.grandtotal
 FROM C_Order o
 INNER JOIN C_OrderLine ol ON (o.c_order_id = ol.c_order_id)
 WHERE (o.isactive = 'Y') AND (o.issotrx = 'Y') AND (ol.pricelist <> ol.priceactual);
 
ALTER TABLE rv_salesorderwithdiscount OWNER TO libertya;

-- 20120411-1402 Incorporación de nuevas columnas para registrar validaciones de cheques con plazos anteriores al cheque actual, por ahora en TPV
ALTER TABLE c_pospaymentmedium ADD COLUMN validatebeforecheckdeadlines character(1) NOT NULL DEFAULT 'N'::bpchar;
ALTER TABLE c_pospaymentmedium ADD COLUMN beforecheckdeadlinefrom integer;
ALTER TABLE c_pospaymentmedium ADD COLUMN beforecheckdeadlineto integer;


-- 20120411-1204 MasterSwitch en replicacion
CREATE OR REPLACE FUNCTION replication_event()
  RETURNS trigger AS
$BODY$
DECLARE 
	found integer; 
	replicationPos integer;
	v_newRepArray varchar; 
	aKeyColumn varchar;
	repSeq bigint;
	shouldReplicate varchar;
BEGIN 
	
	IF (TG_OP = 'DELETE') THEN
		-- Master Switch.  Si se encuentra desactivado, entonces no gestionar replicacion
		SELECT INTO shouldReplicate VALUE FROM AD_PREFERENCE WHERE ATTRIBUTE = 'ReplicationEventsActive';
		IF (shouldReplicate <> 'Y') THEN
			RETURN OLD;
		END IF;
		
		-- Omitir bitacoreo de eliminacion.  El Skip no puede setearse al eliminar, primero se hace un UPDATE a SKIP del repArray y luego debe eliminarse el registro
		IF (OLD.repArray IS NULL OR OLD.repArray = '') THEN
			RETURN OLD;
		END IF;

		-- Si ya fue replicado el registro, debera eliminarse en los destinos
		IF replication_is_record_replicated(OLD.repArray) = 1 THEN
			-- determinar el replication array
			SELECT INTO v_newRepArray replicationArray 
			FROM ad_tablereplication 
			WHERE ad_table_ID = TG_ARGV[0]::int;

			IF v_newRepArray IS NOT NULL AND v_newRepArray <> '' THEN
				INSERT INTO ad_changelog_replication (AD_Changelog_Replication_ID, AD_Client_ID, AD_Org_ID, isActive, Created, CreatedBy, Updated, UpdatedBy, AD_Table_ID, retrieveUID, operationtype, binaryvalue, reparray, columnvalues)
				SELECT nextval('seq_ad_changelog_replication'),OLD.AD_Client_ID,OLD.AD_Org_ID,'Y',OLD.Created,OLD.CreatedBy,OLD.Updated,OLD.UpdatedBy,TG_ARGV[0]::int,OLD.retrieveUID,'I',null,v_newRepArray,null;
			END IF;
		END IF;	
		
		RETURN OLD;
	END IF;
	
	IF (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') THEN

		-- Master Switch.  Si se encuentra desactivado, entonces no gestionar replicacion
		SELECT INTO shouldReplicate VALUE FROM AD_PREFERENCE WHERE ATTRIBUTE = 'ReplicationEventsActive';
		IF (shouldReplicate <> 'Y') THEN
			RETURN NEW;
		END IF;
		
		IF (TG_OP = 'UPDATE') THEN
			IF (OLD.repArray <> NEW.repArray) THEN
				return NEW;
			END IF;
		END IF;
		 
		 IF (NEW.repArray = 'SKIP') THEN
			NEW.repArray := NULL;
			return NEW;
		 END IF;
		 
		 IF NEW.retrieveUID IS NULL OR NEW.retrieveUID = '' THEN 
			SELECT INTO replicationPos replicationArrayPos FROM AD_ReplicationHost WHERE thisHost = 'Y'; 
			IF replicationPos IS NULL THEN RAISE EXCEPTION 'Configuracion de Hosts incompleta: Ninguna sucursal tiene marca de Este Host'; END IF; 
			
			SELECT INTO repseq nextVal('repseq_' || TG_ARGV[1]);
			IF repseq IS NULL THEN RAISE EXCEPTION 'No hay definida una secuencia de replicacion para la tabla %', TG_ARGV[1]; END IF;
			NEW.retrieveUID := 'h'::varchar || replicationPos::varchar || '_' || repseq;
		END IF;
		 
		
		SELECT INTO found replication_is_valid_table(NEW.AD_Client_ID, TG_ARGV[0]::int, NEW.retrieveUID); 
		IF found = 0 THEN RETURN NEW; END IF; 
		
		
		IF (TG_OP = 'INSERT') THEN
			
			SELECT INTO v_newRepArray replicationArray 
			FROM ad_tablereplication 
			WHERE ad_table_ID = TG_ARGV[0]::int;
			
			IF v_newRepArray IS NULL OR v_newRepArray = '' THEN
				RETURN NEW;
			END IF;
			
			
			NEW.repArray := replace(v_newRepArray, '3', '1');
			RETURN NEW;
		
		
		ELSEIF (TG_OP = 'UPDATE') THEN 
				v_newRepArray := replace(OLD.repArray, '2', '3');
				NEW.repArray := replace(v_newRepArray, '4', '5');
				RETURN NEW;
		END IF;
	END IF;
END; 
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION replication_event() OWNER TO libertya;

-- 20120413-1433 Incorporación de columna para agregar la máscara inicial de la tarjeta de crédito
ALTER TABLE M_EntidadFinanciera ADD COLUMN cardmask character varying(100);

-- 20120416-1240 Nueva columna en el reporte Informe de Saldos para poder filtrar por fecha hasta
update ad_system set dummy = (SELECT addcolumnifnotexists('T_BalanceReport','truedatetrx','timestamp without time zone'));

-- 20120416-1245 Eliminación de la vista v_projectedpayments por ser dependiente de v_documents
DROP VIEW v_projectedpayments;

-- 20120416-1245 Eliminación de la vista v_documents - Se creará nuevamente con la columna truedatetrx
DROP VIEW v_documents;

-- 20120416-1245 Creación de la Vista v_documents con nuevas columnas
CREATE OR REPLACE VIEW v_documents AS 
( SELECT 'C_Invoice' AS documenttable, i.c_invoice_id AS document_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, i.documentno, i.issotrx, i.docstatus, 
        CASE
            WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
            ELSE i.dateinvoiced
        END AS datetrx, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal AS amount, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced AS truedatetrx 
   FROM c_invoice_v i
   JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   LEFT JOIN c_invoicepayschedule ips ON i.c_invoicepayschedule_id = ips.c_invoicepayschedule_id
UNION ALL 
 SELECT 'C_Payment' AS documenttable, p.c_payment_id AS document_id, p.ad_client_id, p.ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt AS amount, NULL::integer AS c_invoicepayschedule_id, p.duedate, p.datetrx AS truedatetrx
   FROM c_payment p
   JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id)
UNION ALL 
 SELECT 'C_CashLine' AS documenttable, cl.c_cashline_id AS document_id, cl.ad_client_id, cl.ad_org_id, cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby, 
        CASE
            WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id
            ELSE i.c_bpartner_id
        END AS c_bpartner_id, dt.c_doctype_id, 
        CASE
            WHEN cl.amount < 0.0 THEN 1
            ELSE (-1)
        END AS signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, '@line@'::text || cl.line::character varying::text AS documentno, 
        CASE
            WHEN cl.amount < 0.0 THEN 'N'::bpchar
            ELSE 'Y'::bpchar
        END AS issotrx, cl.docstatus, c.statementdate AS datetrx, c.dateacct, cl.c_currency_id, NULL::integer AS c_conversiontype_id, abs(cl.amount) AS amount, NULL::integer AS c_invoicepayschedule_id, NULL::timestamp without time zone AS duedate, c.statementdate AS truedatetrx
   FROM c_cashline cl
   JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
   JOIN ( SELECT d.ad_client_id, d.c_doctype_id, d.name, d.printname
      FROM c_doctype d
     WHERE d.doctypekey::text = 'CMC'::text) dt ON cl.ad_client_id = dt.ad_client_id
   LEFT JOIN c_invoice i ON cl.c_invoice_id = i.c_invoice_id;

ALTER TABLE v_documents OWNER TO libertya;

-- 20120416-1245 Creación de la Vista v_projectedpayments
CREATE OR REPLACE VIEW v_projectedpayments AS
( SELECT v.documenttable, v.document_id, v.ad_client_id, v.ad_org_id, v.isactive, v.created, v.createdby, v.updated, v.updatedby, v.c_bpartner_id, v.c_doctype_id, v.signo_issotrx, v.doctypename, v.doctypeprintname, v.documentno, v.issotrx, v.docstatus, v.datetrx, v.dateacct, v.c_currency_id, v.c_conversiontype_id, v.amount, v.c_invoicepayschedule_id, v.duedate, i.isPaid, NULL::character varying(20) as CheckNo 
FROM v_documents v
INNER JOIN C_Invoice i ON (i.c_invoice_id = v.document_id)
WHERE (i.issotrx = 'N') AND (documenttable = 'C_Invoice') )
UNION ALL 
( SELECT v.documenttable, v.document_id, v.ad_client_id, v.ad_org_id, v.isactive, v.created, v.createdby, v.updated, v.updatedby, v.c_bpartner_id, v.c_doctype_id, v.signo_issotrx, 'Cheque' AS doctypename, v.doctypeprintname, v.documentno, v.issotrx, v.docstatus, v.datetrx, v.dateacct, v.c_currency_id, v.c_conversiontype_id, v.amount, v.c_invoicepayschedule_id, v.duedate, NULL::character(1) as isPaid, p.CheckNo
FROM v_documents v
INNER JOIN C_Payment p ON (p.C_Payment_id = v.document_id)
WHERE (p.issotrx = 'N') AND (p.tendertype = 'K') AND (p.datetrx < p.duedate) );

ALTER TABLE v_projectedpayments OWNER TO libertya;

-- 20120417-1440 Replicacion columna que almacena la fecha/hora de envio del registro a JMS
alter table ad_changelog_replication add column datelastsent timestamp null;

-- 20120418-1220 Modificación de la versión de ad_system para release 12.04
UPDATE ad_system SET version = '18-04-2012' WHERE ad_system_id = 0;

-- 20120418-1416 Incorporación para registro de precios y máximo porcentaje de variación de precios para Cambio de Artículo 
ALTER TABLE m_productchange ADD COLUMN productprice numeric(11,2) NOT NULL DEFAULT 0;
ALTER TABLE m_productchange ADD COLUMN producttoprice numeric(11,2) NOT NULL DEFAULT 0;
ALTER TABLE m_productchange ADD COLUMN maxpricevariationperc numeric(11,2) NOT NULL DEFAULT 0;

-- 20120418-1613 Vista para cambio de producto con todos los cálculos de variaciones de precios
CREATE OR REPLACE VIEW v_productchange AS 
SELECT m_productchange_id, ad_client_id, ad_org_id, isactive, created, 
       createdby, updated, updatedby, documentno, m_warehouse_id, datetrx, 
       description, m_product_id, m_locator_id, m_product_to_id, m_locator_to_id, 
       productqty, m_inventory_id, docaction, docstatus, processed, 
       void_inventory_id, m_attributesetinstance_id, m_attributesetinstanceto_id, 
       productprice, producttoprice, maxpricevariationperc, 
       productprice - producttoprice as price_diff, 
       abs(productprice - producttoprice) as price_diff_abs,
       ((productprice * maxpricevariationperc) / 100)::numeric(11,2) as allowed_diff,
       (((CASE coalesce(productprice,0) WHEN 0 THEN 0 ELSE abs(productprice - producttoprice) / productprice END))::numeric(11,2) * 100)::numeric(11,2) as pricevariationperc
  FROM m_productchange;
ALTER TABLE v_productchange OWNER TO libertya;

-- 20120419-1434 Modificación de la columna ingresada para la fecha real en la tabla temporal para que permita null y sin default
ALTER TABLE T_BalanceReport ALTER COLUMN truedatetrx DROP NOT NULL;
ALTER TABLE T_BalanceReport ALTER COLUMN truedatetrx DROP DEFAULT;

-- 20120419-1554 Eliminación de registros existentes por instalación de patch de modificaciones al informe de saldos a Enrique Correa
DELETE FROM AD_Element WHERE ad_componentobjectUID = 'CORE-AD_Element-1011145';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectUID = 'CORE-AD_Element_Trl-1011145-es_ES';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectUID = 'CORE-AD_Element_Trl-1011145-es_AR';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectUID = 'CORE-AD_Element_Trl-1011145-es_MX';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectUID = 'CORE-AD_Element_Trl-1011145-es_PY';
DELETE FROM AD_Column WHERE ad_componentobjectUID = 'CORE-AD_Column-1015515';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectUID = 'CORE-AD_Column_Trl-1015515-es_ES';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectUID = 'CORE-AD_Column_Trl-1015515-es_AR';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectUID = 'CORE-AD_Column_Trl-1015515-es_MX';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectUID = 'CORE-AD_Column_Trl-1015515-es_PY';
DELETE FROM AD_Process_Para WHERE ad_componentobjectUID = 'CORE-AD_Process_Para-1010554';
DELETE FROM AD_Process_Para_Trl WHERE ad_componentobjectUID = 'CORE-AD_Process_Para_Trl-es_ES-1010554';
DELETE FROM AD_Process_Para_Trl WHERE ad_componentobjectUID = 'CORE-AD_Process_Para_Trl-es_AR-1010554';
DELETE FROM AD_Process_Para_Trl WHERE ad_componentobjectUID = 'CORE-AD_Process_Para_Trl-es_MX-1010554';
DELETE FROM AD_Process_Para_Trl WHERE ad_componentobjectUID = 'CORE-AD_Process_Para_Trl-es_PY-1010554';

-- 20120419-1722 Incorporación de nueva columna que determina el signo del cargo 
ALTER TABLE c_charge ADD COLUMN sign integer NOT NULL DEFAULT 1;
-- 20120419-1722 Incorporación de nueva columna a la línea de inventario para ingresar la cantidad de la línea sin signo
ALTER TABLE m_inventoryline ADD COLUMN qtycountwithoutchargesign numeric(22,2);
-- 20120419-1723 Actualización de la cantidad sin signo con la cantidad contada para las instancias ya instaladas
UPDATE m_inventoryline
SET qtycountwithoutchargesign = qtycount;
-- 20120419-1723 Actualización de la cantidad sin signo no nula y con default 0
ALTER TABLE m_inventoryline ALTER COLUMN qtycountwithoutchargesign SET NOT NULL;
ALTER TABLE m_inventoryline ALTER COLUMN qtycountwithoutchargesign SET DEFAULT 0;

-- 20120424-1756 Incorporación de nueva config al TPV para pedir autorización al iniciar la ventana
ALTER TABLE c_pos ADD COLUMN initialposauthorization character(1) NOT NULL DEFAULT 'N'::bpchar;

-- 20120426-1020 Incorporación de nueva config al TPV para bloquear el cierre de la ventana y de la aplicación si existen líneas cargadas
ALTER TABLE c_pos ADD COLUMN lockedclosed character(1) NOT NULL DEFAULT 'N'::bpchar;

-- 20120502-1800 Incorporación de nueva columna para el tipo de documento con la posibilidad de agregar un límite de cantidad de líneas para los documentos
ALTER TABLE c_doctype ADD COLUMN linescountmax integer NOT NULL DEFAULT 0;

-- Replicacion: Filtros por tabla
alter table AD_TableReplication add column filters varchar(1000) null;

-- Replication: referencia a fecha de ultimo envio
ALTER TABLE ad_changelog_replication ADD COLUMN dateLastSentJMS timestamp null;

-- 20120515-0930 Creación de la Vista rv_cash_balance_begin_end con las nuevas columnas
CREATE OR REPLACE VIEW rv_cash_balance_begin_end AS 
( SELECT c.c_cash_id, 0 AS c_cashline_id, c.ad_client_id, c.ad_org_id, c.isactive, c.created, c.createdby, c.updated, c.updatedby, c.c_cashbook_id, c.name, c.statementdate, c.dateacct, c.processed, 0 AS line, 'Saldo Inicial' AS description, NULL::unknown AS cashtype, NULL::unknown AS c_charge_id, c.beginningbalance as amount, cb.c_currency_id, c.c_project_id
   FROM c_cash c
   JOIN c_cashbook cb ON c.c_cashbook_id = cb.c_cashbook_id
UNION 
 SELECT cl.c_cash_id, cl.c_cashline_id, c.ad_client_id, c.ad_org_id, cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby, c.c_cashbook_id, c.name, c.statementdate, c.dateacct, c.processed, cl.line, cl.description, cl.cashtype, cl.c_charge_id, cl.amount, cl.c_currency_id, cl.c_project_id
   FROM c_cash c
   JOIN c_cashline cl ON c.c_cash_id = cl.c_cash_id);
ALTER TABLE rv_cash_balance_begin_end OWNER TO libertya;

-- 20120515-1000 Eliminación de la Vista 
DROP VIEW m_retencion_invoice_v;

-- 20120515-1000 Creación de la Vista m_retencion_invoice_v con las nuevas columnas
CREATE OR REPLACE VIEW m_retencion_invoice_v AS 
 SELECT DISTINCT ri.m_retencion_invoice_id, ri.ad_client_id, ri.ad_org_id, ri.c_retencionschema_id, ri.c_invoice_id, ri.c_allocationhdr_id, ri.c_invoiceline_id, ri.c_invoice_retenc_id, ri.amt_retenc, ri.c_currency_id, ri.pagos_ant_acumulados_amt, ri.retenciones_ant_acumuladas_amt, ri.pago_actual_amt, ri.retencion_percent, ri.importe_no_imponible_amt, ri.base_calculo_percent, ri.issotrx, ri.baseimponible_amt, ri.importe_determinado_amt, rs.c_retenciontype_id, i.c_bpartner_id, rs.retencionapplication, bp.taxid, i.dateinvoiced, iv.documentno, iv.dateinvoiced as fecha, iv.grandtotal, iv.c_project_id, iv.totallines 
   FROM c_retencionschema rs
   JOIN c_retenciontype rt ON rs.c_retenciontype_id = rt.c_retenciontype_id
   JOIN m_retencion_invoice ri ON rs.c_retencionschema_id = ri.c_retencionschema_id
   JOIN c_invoice i ON ri.c_invoice_id = i.c_invoice_id
   JOIN c_bpartner bp ON i.c_bpartner_id = bp.c_bpartner_id
   JOIN c_allocationhdr a ON ri.c_allocationhdr_id = a.c_allocationhdr_id
   JOIN c_allocation_detail_v ad ON a.c_allocationhdr_id = ad.c_allocationhdr_id
   LEFT JOIN c_invoice iv ON ad.factura = iv.documentno;
ALTER TABLE m_retencion_invoice_v OWNER TO libertya;

-- 20120517-1630 Incorporación de nueva columna al impuesto para indicar que es impuesto de percepción
ALTER TABLE c_tax ADD COLUMN ispercepcion character(1) NOT NULL DEFAULT 'N'::bpchar;

-- 20120517-1630 Incorporación de nueva columna a la categoria de iva indicando que es pasible de percepciones
ALTER TABLE c_categoria_iva ADD COLUMN ispercepcionliable character(1) NOT NULL DEFAULT 'N'::bpchar;

-- 20120517-1640 Percepciones que aplica la organización
CREATE TABLE ad_org_percepcion
(
  ad_org_percepcion_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  "name" character varying(60) NOT NULL,
  description character varying(255),
  c_tax_id integer NOT NULL,
  CONSTRAINT ad_org_percepcion_key PRIMARY KEY (ad_org_percepcion_id),
  CONSTRAINT ad_org_percepcion_client FOREIGN KEY (ad_client_id)
      REFERENCES ad_client (ad_client_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT ad_org_percepcion_org FOREIGN KEY (ad_org_id)
      REFERENCES ad_org (ad_org_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT ad_org_percepcion_tax FOREIGN KEY (c_tax_id)
      REFERENCES c_tax (c_tax_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
ALTER TABLE ad_org_percepcion OWNER TO libertya;

-- 20120517-1640 Configuración de Percepciones de la entidad comercial
CREATE TABLE c_bpartner_percepcion
(
  c_bpartner_percepcion_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  c_bpartner_id integer NOT NULL,
  registrationno character varying(60),
  c_region_id integer NOT NULL,
  CONSTRAINT c_bpartner_percepcion_key PRIMARY KEY (c_bpartner_percepcion_id),
  CONSTRAINT c_bpartner_percepcion_bpartner FOREIGN KEY (c_bpartner_id)
      REFERENCES c_bpartner (c_bpartner_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT c_bpartner_percepcion_client FOREIGN KEY (ad_client_id)
      REFERENCES ad_client (ad_client_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT c_bpartner_percepcion_org FOREIGN KEY (ad_org_id)
      REFERENCES ad_org (ad_org_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT c_bpartner_percepcion_region FOREIGN KEY (c_region_id)
      REFERENCES c_region (c_region_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
ALTER TABLE c_bpartner_percepcion OWNER TO libertya;

-- 20120517-1640 Excenciones de percepción de la entidad comercial
CREATE TABLE c_bpartner_percexenc
(
  c_bpartner_percexenc_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  c_bpartner_id integer NOT NULL,
  date_from timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  date_to timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  percent numeric(24,2),
  c_tax_id integer NOT NULL,
  CONSTRAINT c_bpartner_percexenc_key PRIMARY KEY (c_bpartner_percexenc_id),
  CONSTRAINT c_bpartner_percexenc_bpartner FOREIGN KEY (c_bpartner_id)
      REFERENCES c_bpartner (c_bpartner_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT c_bpartner_percexenc_client FOREIGN KEY (ad_client_id)
      REFERENCES ad_client (ad_client_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT c_bpartner_percexenc_org FOREIGN KEY (ad_org_id)
      REFERENCES ad_org (ad_org_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT c_bpartner_percexenc_tax FOREIGN KEY (c_tax_id)
      REFERENCES c_tax (c_tax_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
ALTER TABLE c_bpartner_percexenc OWNER TO libertya;

-- 20120517-1740 Cambio o vuelto de esta asignación (usado en TPV para la impresión fiscal)
ALTER TABLE c_allocationline ADD COLUMN changeamt numeric(9,2) DEFAULT 0;

-- 20120517-1741 Prioridad 4 de descuento de línea
ALTER TABLE m_discountconfig ADD COLUMN linediscount4 character varying(1);

-- 20120521-1654 Parámetro de configuración de impresión de documento de cuenta corriente en TPV
ALTER TABLE c_pos ADD COLUMN isprintcurrentaccountdocument character(1) NOT NULL DEFAULT 'N'::bpchar;

-- 20120523-1457 Modificación de vista c_allocation_detail_v con nuevos campos
DROP VIEW m_retencion_invoice_v;
DROP VIEW c_allocation_detail_v;

CREATE OR REPLACE VIEW c_allocation_detail_v AS 
 SELECT ah.c_allocationhdr_id AS c_allocation_detail_v_id, ah.c_allocationhdr_id, ah.ad_client_id, ah.ad_org_id, ah.isactive, ah.created, ah.createdby, ah.updated, ah.updatedby, ah.datetrx AS fecha, i.documentno AS factura, COALESCE(i.c_currency_id, p.c_currency_id, cl.c_currency_id, credit.c_currency_id) AS c_currency_id, i.grandtotal AS montofactura, 
        CASE
            WHEN p.documentno IS NOT NULL THEN p.documentno
            ELSE 
            CASE
                WHEN al.c_invoice_credit_id IS NOT NULL THEN ((dt.printname::text || ' :'::text) || credit.documentno::text)::character varying
                ELSE NULL::character varying
            END
        END AS pagonro, 
        CASE
            WHEN p.tendertype IS NOT NULL THEN p.tendertype
            WHEN p.tendertype IS NULL THEN 'CA'::bpchar
            ELSE NULL::bpchar
        END AS tipo, 
        CASE
            WHEN cl.c_cashline_id IS NOT NULL THEN 'Y'::text
            WHEN cl.c_cashline_id IS NULL THEN 'N'::text
            ELSE NULL::text
        END AS cash, COALESCE(currencyconvert(al.amount + al.discountamt + al.writeoffamt, ah.c_currency_id, i.c_currency_id, NULL::timestamp with time zone, NULL::integer, ah.ad_client_id, ah.ad_org_id), 0::numeric(20,2)) AS montosaldado, abs(COALESCE(p.payamt, cl.amount, credit.grandtotal, 0::numeric(20,2))) AS payamt, al.c_allocationline_id, i.c_invoice_id, CASE
            WHEN p.documentno IS NOT NULL THEN p.documentno
            ELSE 
            CASE
                WHEN al.c_invoice_credit_id IS NOT NULL THEN ((dt.printname::text || ' :'::text) || credit.documentno::text)::character varying
                WHEN al.c_cashline_id IS NOT NULL THEN cl.description::character varying
                ELSE NULL::character varying
            END
        END AS paydescription
   FROM c_allocationhdr ah
   JOIN c_allocationline al ON ah.c_allocationhdr_id = al.c_allocationhdr_id
   LEFT JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
   LEFT JOIN c_payment p ON al.c_payment_id = p.c_payment_id
   LEFT JOIN c_cashline cl ON al.c_cashline_id = cl.c_cashline_id
   LEFT JOIN c_invoice credit ON al.c_invoice_credit_id = credit.c_invoice_id
   LEFT JOIN c_doctype dt ON credit.c_doctype_id = dt.c_doctype_id
  ORDER BY ah.c_allocationhdr_id, al.c_allocationline_id;

ALTER TABLE c_allocation_detail_v OWNER TO libertya;

CREATE OR REPLACE VIEW m_retencion_invoice_v AS 
 SELECT DISTINCT ri.m_retencion_invoice_id, ri.ad_client_id, ri.ad_org_id, ri.c_retencionschema_id, ri.c_invoice_id, ri.c_allocationhdr_id, ri.c_invoiceline_id, ri.c_invoice_retenc_id, ri.amt_retenc, ri.c_currency_id, ri.pagos_ant_acumulados_amt, ri.retenciones_ant_acumuladas_amt, ri.pago_actual_amt, ri.retencion_percent, ri.importe_no_imponible_amt, ri.base_calculo_percent, ri.issotrx, ri.baseimponible_amt, ri.importe_determinado_amt, rs.c_retenciontype_id, i.c_bpartner_id, rs.retencionapplication, bp.taxid, i.dateinvoiced, iv.documentno, iv.dateinvoiced AS fecha, iv.grandtotal, iv.c_project_id, iv.totallines
   FROM c_retencionschema rs
   JOIN c_retenciontype rt ON rs.c_retenciontype_id = rt.c_retenciontype_id
   JOIN m_retencion_invoice ri ON rs.c_retencionschema_id = ri.c_retencionschema_id
   JOIN c_invoice i ON ri.c_invoice_id = i.c_invoice_id
   JOIN c_bpartner bp ON i.c_bpartner_id = bp.c_bpartner_id
   JOIN c_allocationhdr a ON ri.c_allocationhdr_id = a.c_allocationhdr_id
   JOIN c_allocation_detail_v ad ON a.c_allocationhdr_id = ad.c_allocationhdr_id
   LEFT JOIN c_invoice iv ON ad.factura::text = iv.documentno::text
  ORDER BY ri.m_retencion_invoice_id, ri.ad_client_id, ri.ad_org_id, ri.c_retencionschema_id, ri.c_invoice_id, ri.c_allocationhdr_id, ri.c_invoiceline_id, ri.c_invoice_retenc_id, ri.amt_retenc, ri.c_currency_id, ri.pagos_ant_acumulados_amt, ri.retenciones_ant_acumuladas_amt, ri.pago_actual_amt, ri.retencion_percent, ri.importe_no_imponible_amt, ri.base_calculo_percent, ri.issotrx, ri.baseimponible_amt, ri.importe_determinado_amt, rs.c_retenciontype_id, i.c_bpartner_id, rs.retencionapplication, bp.taxid, i.dateinvoiced, iv.documentno, iv.dateinvoiced, iv.grandtotal, iv.c_project_id, iv.totallines;

ALTER TABLE m_retencion_invoice_v OWNER TO libertya;

-- 20120523-1945 Modificación de la función bompricelimit para que en el caso que no tenga un precio de instancia tome el del producto
CREATE OR REPLACE FUNCTION bompricelimit(m_product_id integer, m_pricelist_version_id integer, m_attributesetinstance_id integer)
  RETURNS numeric AS
$BODY$
DECLARE
    i_price NUMERIC;
BEGIN
    IF (m_attributesetinstance_id = 0) THEN
	select bomPriceLimit(M_Product_ID,M_PriceList_Version_ID) into i_price;
    else	
	SELECT pricelimit into i_price FROM M_ProductPriceInstance pi WHERE pi.M_PriceList_Version_ID=M_PriceList_Version_ID AND pi.M_Product_ID=M_Product_ID AND pi.M_AttributeSetInstance_ID=M_AttributeSetInstance_ID;
	IF (i_price ISNULL) THEN
		select bomPriceLimit(M_Product_ID,M_PriceList_Version_ID) into i_price;
	END IF;	
    END IF;
    return i_price;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION bompricelimit(integer, integer, integer) OWNER TO libertya;

-- 20120523-1945 Modificación de la función bompricelist para que en el caso que no tenga un precio de instancia tome el del producto
CREATE OR REPLACE FUNCTION bompricelist(m_product_id integer, m_pricelist_version_id integer, m_attributesetinstance_id integer)
  RETURNS numeric AS
$BODY$
DECLARE
    i_price NUMERIC;
BEGIN
    IF (m_attributesetinstance_id = 0) THEN
	select bomPriceList(M_Product_ID,M_PriceList_Version_ID) into i_price;
    else	
	SELECT pricelist into i_price FROM M_ProductPriceInstance pi WHERE pi.M_PriceList_Version_ID=M_PriceList_Version_ID AND pi.M_Product_ID=M_Product_ID AND pi.M_AttributeSetInstance_ID=M_AttributeSetInstance_ID;
	IF (i_price ISNULL) THEN
		select bomPriceList(M_Product_ID,M_PriceList_Version_ID) into i_price;
	END IF;	
    END IF;
    return i_price;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION bompricelist(integer, integer, integer) OWNER TO libertya;

-- 20120523-1945 Modificación de la función bompricestd para que en el caso que no tenga un precio de instancia tome el del producto
CREATE OR REPLACE FUNCTION bompricestd(m_product_id integer, m_pricelist_version_id integer, m_attributesetinstance_id integer)
  RETURNS numeric AS
$BODY$
DECLARE
    i_price NUMERIC;
BEGIN
    
    IF (m_attributesetinstance_id = 0) THEN
	select bomPriceStd(M_Product_ID,M_PriceList_Version_ID) into i_price;
    else	
	SELECT pricestd into i_price FROM M_ProductPriceInstance pi WHERE pi.M_PriceList_Version_ID=M_PriceList_Version_ID AND pi.M_Product_ID=M_Product_ID AND pi.M_AttributeSetInstance_ID=M_AttributeSetInstance_ID;
	IF (i_price ISNULL) THEN
		select bomPriceStd(M_Product_ID,M_PriceList_Version_ID) into i_price;
	END IF;	
    END IF;
    return i_price;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION bompricestd(integer, integer, integer) OWNER TO libertya;

-- 20120524-1456 Incorporación de columnas para registrar el posnet en la config del TPV y en el cobro de tarjeta de crédito
ALTER TABLE c_pos ADD COLUMN posnet character varying(40);
ALTER TABLE c_payment ADD COLUMN posnet character varying(40);

-- 20120529-1610 Incorporación de nueva columna para no permitir la utilización de la Nota de Crédito
ALTER TABLE c_invoice ADD COLUMN notexchangeablecredit character(1) NOT NULL DEFAULT 'N'::bpchar;

-- 20120529-1610 Modificación de la view c_invoice_v agregando dicha columna, eliminación y creación de las vistas dependientes
DROP VIEW rv_bpartneropen;
DROP VIEW v_projectedpayments;
DROP VIEW v_documents;
DROP VIEW c_invoiceline_v;
DROP VIEW c_invoice_v;

CREATE OR REPLACE VIEW c_invoice_v AS 
 SELECT i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.issotrx, i.documentno, i.docstatus, i.docaction, i.processing, i.processed, i.c_doctype_id, i.c_doctypetarget_id, i.c_order_id, i.description, i.isapproved, i.istransferred, i.salesrep_id, i.dateinvoiced, i.dateprinted, i.dateacct, i.c_bpartner_id, i.c_bpartner_location_id, i.ad_user_id, i.poreference, i.dateordered, i.c_currency_id, i.c_conversiontype_id, i.paymentrule, i.c_paymentterm_id, i.c_charge_id, i.m_pricelist_id, i.c_campaign_id, i.c_project_id, i.c_activity_id, i.isprinted, i.isdiscountprinted, i.ispaid, i.isindispute, i.ispayschedulevalid, NULL::unknown AS c_invoicepayschedule_id, i.chargeamt * d.signo_issotrx::numeric AS chargeamt, i.totallines, i.grandtotal * d.signo_issotrx::numeric * d.signo_issotrx::numeric AS grandtotal, d.signo_issotrx::numeric AS multiplier, 
        CASE
            WHEN "substring"(d.docbasetype::text, 2, 2) = 'P'::text THEN - 1::numeric
            ELSE 1::numeric
        END AS multiplierap, d.docbasetype, notexchangeablecredit
   FROM c_invoice i
   JOIN c_doctype d ON i.c_doctypetarget_id = d.c_doctype_id
  WHERE i.ispayschedulevalid <> 'Y'::bpchar
UNION ALL 
 SELECT i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.issotrx, i.documentno, i.docstatus, i.docaction, i.processing, i.processed, i.c_doctype_id, i.c_doctypetarget_id, i.c_order_id, i.description, i.isapproved, i.istransferred, i.salesrep_id, i.dateinvoiced, i.dateprinted, i.dateacct, i.c_bpartner_id, i.c_bpartner_location_id, i.ad_user_id, i.poreference, i.dateordered, i.c_currency_id, i.c_conversiontype_id, i.paymentrule, i.c_paymentterm_id, i.c_charge_id, i.m_pricelist_id, i.c_campaign_id, i.c_project_id, i.c_activity_id, i.isprinted, i.isdiscountprinted, i.ispaid, i.isindispute, i.ispayschedulevalid, ips.c_invoicepayschedule_id, NULL::unknown AS chargeamt, NULL::unknown AS totallines, ips.dueamt AS grandtotal, d.signo_issotrx AS multiplier, 
        CASE
            WHEN "substring"(d.docbasetype::text, 2, 2) = 'P'::text THEN - 1::numeric
            ELSE 1::numeric
        END AS multiplierap, d.docbasetype, notexchangeablecredit
   FROM c_invoice i
   JOIN c_doctype d ON i.c_doctypetarget_id = d.c_doctype_id
   JOIN c_invoicepayschedule ips ON i.c_invoice_id = ips.c_invoice_id
  WHERE i.ispayschedulevalid = 'Y'::bpchar AND ips.isvalid = 'Y'::bpchar;

ALTER TABLE c_invoice_v OWNER TO libertya;

CREATE OR REPLACE VIEW c_invoiceline_v AS 
 SELECT il.ad_client_id, il.ad_org_id, il.c_invoiceline_id, i.c_invoice_id, i.salesrep_id, i.c_bpartner_id, il.m_product_id, i.documentno, i.dateinvoiced, i.dateacct, i.issotrx, i.docstatus, round(i.multiplier * il.linenetamt, 2) AS linenetamt, round(i.multiplier * il.pricelist * il.qtyinvoiced, 2) AS linelistamt, 
        CASE
            WHEN COALESCE(il.pricelimit, 0::numeric) = 0::numeric THEN round(i.multiplier * il.linenetamt, 2)
            ELSE round(i.multiplier * il.pricelimit * il.qtyinvoiced, 2)
        END AS linelimitamt, round(i.multiplier * il.pricelist * il.qtyinvoiced - il.linenetamt, 2) AS linediscountamt, 
        CASE
            WHEN COALESCE(il.pricelimit, 0::numeric) = 0::numeric THEN 0::numeric
            ELSE round(i.multiplier * il.linenetamt - il.pricelimit * il.qtyinvoiced, 2)
        END AS lineoverlimitamt, il.qtyinvoiced, il.qtyentered, il.line, il.c_orderline_id, il.c_uom_id
   FROM c_invoice_v i, c_invoiceline il
  WHERE i.c_invoice_id = il.c_invoice_id;

ALTER TABLE c_invoiceline_v OWNER TO libertya;

CREATE OR REPLACE VIEW v_documents AS 
( SELECT 'C_Invoice' AS documenttable, i.c_invoice_id AS document_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, i.documentno, i.issotrx, i.docstatus, 
        CASE
            WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
            ELSE i.dateinvoiced
        END AS datetrx, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal AS amount, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced AS truedatetrx
   FROM c_invoice_v i
   JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   LEFT JOIN c_invoicepayschedule ips ON i.c_invoicepayschedule_id = ips.c_invoicepayschedule_id
UNION ALL 
 SELECT 'C_Payment' AS documenttable, p.c_payment_id AS document_id, p.ad_client_id, p.ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt AS amount, NULL::integer AS c_invoicepayschedule_id, p.duedate, p.datetrx AS truedatetrx
   FROM c_payment p
   JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id)
UNION ALL 
 SELECT 'C_CashLine' AS documenttable, cl.c_cashline_id AS document_id, cl.ad_client_id, cl.ad_org_id, cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby, 
        CASE
            WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id
            ELSE i.c_bpartner_id
        END AS c_bpartner_id, dt.c_doctype_id, 
        CASE
            WHEN cl.amount < 0.0 THEN 1
            ELSE (-1)
        END AS signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, '@line@'::text || cl.line::character varying::text AS documentno, 
        CASE
            WHEN cl.amount < 0.0 THEN 'N'::bpchar
            ELSE 'Y'::bpchar
        END AS issotrx, cl.docstatus, c.statementdate AS datetrx, c.dateacct, cl.c_currency_id, NULL::integer AS c_conversiontype_id, abs(cl.amount) AS amount, NULL::integer AS c_invoicepayschedule_id, NULL::timestamp without time zone AS duedate, c.statementdate AS truedatetrx
   FROM c_cashline cl
   JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
   JOIN ( SELECT d.ad_client_id, d.c_doctype_id, d.name, d.printname
      FROM c_doctype d
     WHERE d.doctypekey::text = 'CMC'::text) dt ON cl.ad_client_id = dt.ad_client_id
   LEFT JOIN c_invoice i ON cl.c_invoice_id = i.c_invoice_id;

ALTER TABLE v_documents OWNER TO libertya;

CREATE OR REPLACE VIEW rv_bpartneropen AS 
 SELECT i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_currency_id, i.grandtotal * i.multiplierap AS amt, invoiceopen(i.c_invoice_id, i.c_invoicepayschedule_id) * i.multiplierap AS openamt, i.dateinvoiced AS datedoc, COALESCE(daysbetween(now(), ips.duedate::timestamp with time zone), paymenttermduedays(i.c_paymentterm_id, i.dateinvoiced::timestamp with time zone, now())) AS daysdue
   FROM c_invoice_v i
   LEFT JOIN c_invoicepayschedule ips ON i.c_invoicepayschedule_id = ips.c_invoicepayschedule_id
  WHERE i.ispaid = 'N'::bpchar AND (i.docstatus = 'CO'::bpchar OR i.docstatus = 'CL'::bpchar)
UNION 
 SELECT p.ad_client_id, p.ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_currency_id, p.payamt * p.multiplierap * (- 1::numeric) AS amt, paymentavailable(p.c_payment_id) * p.multiplierap * (- 1::numeric) AS openamt, p.datetrx AS datedoc, NULL::unknown AS daysdue
   FROM c_payment_v p
  WHERE p.isallocated = 'N'::bpchar AND p.c_bpartner_id IS NOT NULL AND (p.docstatus = 'CO'::bpchar OR p.docstatus = 'CL'::bpchar);

ALTER TABLE rv_bpartneropen OWNER TO libertya;

CREATE OR REPLACE VIEW v_projectedpayments AS 
 SELECT v.documenttable, v.document_id, v.ad_client_id, v.ad_org_id, v.isactive, v.created, v.createdby, v.updated, v.updatedby, v.c_bpartner_id, v.c_doctype_id, v.signo_issotrx, v.doctypename, v.doctypeprintname, v.documentno, v.issotrx, v.docstatus, v.datetrx, v.dateacct, v.c_currency_id, v.c_conversiontype_id, v.amount, v.c_invoicepayschedule_id, v.duedate, i.ispaid, NULL::character varying(20) AS checkno
   FROM v_documents v
   JOIN c_invoice i ON i.c_invoice_id = v.document_id
  WHERE i.issotrx = 'N'::bpchar AND v.documenttable = 'C_Invoice'::text
UNION ALL 
 SELECT v.documenttable, v.document_id, v.ad_client_id, v.ad_org_id, v.isactive, v.created, v.createdby, v.updated, v.updatedby, v.c_bpartner_id, v.c_doctype_id, v.signo_issotrx, 'Cheque' AS doctypename, v.doctypeprintname, v.documentno, v.issotrx, v.docstatus, v.datetrx, v.dateacct, v.c_currency_id, v.c_conversiontype_id, v.amount, v.c_invoicepayschedule_id, v.duedate, NULL::character(1) AS ispaid, p.checkno
   FROM v_documents v
   JOIN c_payment p ON p.c_payment_id = v.document_id
  WHERE p.issotrx = 'N'::bpchar AND p.tendertype = 'K'::bpchar AND p.datetrx < p.duedate;

ALTER TABLE v_projectedpayments OWNER TO libertya;

-- 20120606-1145 Modificación de vista m_retencion_invoice_v con nuevos campos
CREATE OR REPLACE VIEW m_retencion_invoice_v AS 
 SELECT DISTINCT ri.m_retencion_invoice_id, ri.ad_client_id, ri.ad_org_id, ri.c_retencionschema_id, ri.c_invoice_id, ri.c_allocationhdr_id, ri.c_invoiceline_id, ri.c_invoice_retenc_id, ri.amt_retenc, ri.c_currency_id, ri.pagos_ant_acumulados_amt, ri.retenciones_ant_acumuladas_amt, ri.pago_actual_amt, ri.retencion_percent, ri.importe_no_imponible_amt, ri.base_calculo_percent, ri.issotrx, ri.baseimponible_amt, ri.importe_determinado_amt, rs.c_retenciontype_id, i.c_bpartner_id, rs.retencionapplication, bp.taxid, i.dateinvoiced, iv.documentno, iv.dateinvoiced AS fecha, iv.grandtotal, iv.c_project_id, iv.totallines
   FROM c_retencionschema rs
   JOIN c_retenciontype rt ON rs.c_retenciontype_id = rt.c_retenciontype_id
   JOIN m_retencion_invoice ri ON rs.c_retencionschema_id = ri.c_retencionschema_id
   JOIN c_invoice i ON ri.c_invoice_id = i.c_invoice_id
   JOIN c_bpartner bp ON i.c_bpartner_id = bp.c_bpartner_id
   JOIN c_allocationhdr a ON ri.c_allocationhdr_id = a.c_allocationhdr_id
   JOIN c_allocationline al ON a.c_allocationhdr_id = al.c_allocationhdr_id
   LEFT JOIN c_invoice iv ON iv.c_invoice_id = (SELECT c_invoice_id FROM c_allocationline allo WHERE allo.c_allocationhdr_id = al.c_allocationhdr_id ORDER BY c_invoice_id DESC LIMIT 1)
  ORDER BY ri.m_retencion_invoice_id, ri.ad_client_id, ri.ad_org_id, ri.c_retencionschema_id, ri.c_invoice_id, ri.c_allocationhdr_id, ri.c_invoiceline_id, ri.c_invoice_retenc_id, ri.amt_retenc, ri.c_currency_id, ri.pagos_ant_acumulados_amt, ri.retenciones_ant_acumuladas_amt, ri.pago_actual_amt, ri.retencion_percent, ri.importe_no_imponible_amt, ri.base_calculo_percent, ri.issotrx, ri.baseimponible_amt, ri.importe_determinado_amt, rs.c_retenciontype_id, i.c_bpartner_id, rs.retencionapplication, bp.taxid, i.dateinvoiced, iv.documentno, iv.dateinvoiced, iv.grandtotal, iv.c_project_id, iv.totallines;

ALTER TABLE m_retencion_invoice_v OWNER TO libertya;

-- 20120606-1855 Adicionales para el reporte de Resumen de Ventas
ALTER TABLE c_invoice ADD COLUMN initialcurrentaccountamt numeric(20,2) NOT NULL DEFAULT 0;
ALTER TABLE c_invoice ADD COLUMN c_pospaymentmedium_id integer;
ALTER TABLE c_payment ADD COLUMN c_pospaymentmedium_id integer;
ALTER TABLE c_cashline ADD COLUMN c_pospaymentmedium_id integer;

DROP VIEW c_pos_declaracionvalores_v;
DROP VIEW c_posjournalpayments_v;

CREATE OR REPLACE VIEW c_posjournalpayments_v AS 
 SELECT al.c_allocationhdr_id, al.c_allocationline_id, al.ad_client_id, al.ad_org_id, al.isactive, al.created, al.createdby, al.updated, al.updatedby, al.c_invoice_id, al.c_payment_id, al.c_cashline_id, al.c_invoice_credit_id, 
        CASE
            WHEN al.c_payment_id IS NOT NULL THEN p.tendertype::character varying
            WHEN al.c_cashline_id IS NOT NULL THEN 'CA'::character varying
            WHEN al.c_invoice_credit_id IS NOT NULL THEN 'CR'::character varying
            ELSE NULL::character varying
        END::character varying(2) AS tendertype, 
        CASE
            WHEN al.c_payment_id IS NOT NULL THEN p.documentno
            WHEN al.c_invoice_credit_id IS NOT NULL THEN ic.documentno
            ELSE NULL::character varying
        END::character varying(30) AS documentno, 
        CASE
            WHEN al.c_payment_id IS NOT NULL THEN p.description
            WHEN al.c_cashline_id IS NOT NULL THEN cl.description
            WHEN al.c_invoice_credit_id IS NOT NULL THEN ic.description
            ELSE NULL::character varying
        END::character varying(255) AS description, 
        CASE
            WHEN al.c_payment_id IS NOT NULL THEN ((p.documentno::text || '_'::text) || to_char(p.datetrx, 'DD/MM/YYYY'::text))::character varying
            WHEN al.c_cashline_id IS NOT NULL THEN ((c.name::text || '_#'::text) || cl.line::text)::character varying
            WHEN al.c_invoice_credit_id IS NOT NULL THEN ic.documentno
            ELSE NULL::character varying
        END::character varying(255) AS info, COALESCE(currencyconvert(al.amount + al.discountamt + al.writeoffamt, ah.c_currency_id, i.c_currency_id, NULL::timestamp with time zone, NULL::integer, ah.ad_client_id, ah.ad_org_id), 0::numeric)::numeric(20,2) AS amount, cl.c_cash_id, cl.line, ic.c_doctype_id, p.checkno, p.a_bank, p.checkno AS transferno, p.creditcardtype, p.m_entidadfinancieraplan_id, ep.m_entidadfinanciera_id, p.couponnumber, date_trunc('day'::text, ah.datetrx) AS allocationdate,
        CASE
            WHEN al.c_payment_id IS NOT NULL THEN p.docstatus
            WHEN al.c_cashline_id IS NOT NULL THEN cl.docstatus
            WHEN al.c_invoice_credit_id IS NOT NULL THEN ic.docstatus
            ELSE NULL::character(2)
        END AS docstatus
   FROM c_allocationline al
   LEFT JOIN c_allocationhdr ah ON al.c_allocationhdr_id = ah.c_allocationhdr_id
   LEFT JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
   LEFT JOIN c_payment p ON al.c_payment_id = p.c_payment_id
   LEFT JOIN m_entidadfinancieraplan ep ON p.m_entidadfinancieraplan_id = ep.m_entidadfinancieraplan_id
   LEFT JOIN c_cashline cl ON al.c_cashline_id = cl.c_cashline_id
   LEFT JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
   LEFT JOIN c_invoice ic ON al.c_invoice_credit_id = ic.c_invoice_id
  ORDER BY 
CASE
    WHEN al.c_payment_id IS NOT NULL THEN p.tendertype::character varying
    WHEN al.c_cashline_id IS NOT NULL THEN 'CA'::character varying
    WHEN al.c_invoice_credit_id IS NOT NULL THEN 'CR'::character varying
    ELSE NULL::character varying
END::character varying(2), 
CASE
    WHEN al.c_payment_id IS NOT NULL THEN p.documentno
    WHEN al.c_invoice_credit_id IS NOT NULL THEN ic.documentno
    ELSE NULL::character varying
END::character varying(30);

ALTER TABLE c_posjournalpayments_v OWNER TO libertya;

CREATE OR REPLACE VIEW c_pos_declaracionvalores_v AS 
        (        (         SELECT i.ad_client_id, i.ad_org_id, i.c_posjournal_id, i.ad_user_id, i.c_currency_id, i.dateinvoiced AS datetrx, i.docstatus, NULL::unknown AS category, dt.docbasetype AS tendertype, (i.documentno::text || ' '::text) || COALESCE(i.description, ''::character varying)::text AS description, i.c_charge_id, i.chargename, i.c_invoice_id AS doc_id, 
                                CASE dt.signo_issotrx
                                    WHEN 1 THEN i.total - i.open
                                    WHEN (-1) THEN 0::numeric
                                    ELSE NULL::numeric
                                END::numeric(22,2) AS ingreso, 
                                CASE dt.signo_issotrx
                                    WHEN 1 THEN 0::numeric
                                    WHEN (-1) THEN i.total - i.open
                                    ELSE NULL::numeric
                                END::numeric(22,2) AS egreso
                           FROM ( SELECT DISTINCT i.ad_client_id, i.ad_org_id, i.documentno, inv.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, i.dateinvoiced::date AS dateinvoiced, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name AS chargename, currencybase(i.grandtotal, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2) AS total, currencybase(initialcurrentaccountamt, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2) AS open
                                   FROM c_posjournalinvoices_v i
                              JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
                         JOIN c_invoice inv ON i.c_invoice_id = inv.c_invoice_id
                    LEFT JOIN c_charge ch ON ch.c_charge_id = inv.c_charge_id
                   ORDER BY i.ad_client_id, i.ad_org_id, i.documentno, inv.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, i.dateinvoiced::date, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name, currencybase(i.grandtotal, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2), currencybase(initialcurrentaccountamt, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2)) i
                      JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id
                UNION ALL 
                         SELECT p.ad_client_id, p.ad_org_id, p.c_posjournal_id, p.ad_user_id, p.c_currency_id, p.datetrx, p.docstatus, NULL::unknown AS category, p.tendertype, (p.documentno::text || ' '::text) || COALESCE(p.description, ''::character varying)::text AS description, p.c_charge_id, p.chargename, p.c_payment_id AS doc_id, 
                                CASE p.isreceipt
                                    WHEN 'Y'::bpchar THEN p.total
                                    ELSE 0::numeric
                                END::numeric(22,2) AS ingreso, 
                                CASE p.isreceipt
                                    WHEN 'N'::bpchar THEN p.total
                                    ELSE 0::numeric
                                END::numeric(22,2) AS egreso
                           FROM ( SELECT p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date AS datetrx, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, p.c_currency_id, p.datetrx::timestamp with time zone, p.ad_client_id, p.ad_org_id)::numeric(22,2))::numeric(22,2) AS total
                                   FROM c_payment p
                              JOIN c_posjournalpayments_v pjp ON pjp.c_payment_id = p.c_payment_id
                         JOIN c_posjournal pj ON pj.c_posjournal_id = p.c_posjournal_id
                    LEFT JOIN c_charge ch ON ch.c_charge_id = p.c_charge_id
                   GROUP BY p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name) p)
        UNION ALL 
                 SELECT c.ad_client_id, c.ad_org_id, c.c_posjournal_id, c.ad_user_id, c.c_currency_id, c.datetrx, c.docstatus, c.cashtype AS category, c.tendertype, 
                        CASE
                            WHEN length(c.description::text) > 0 THEN c.description
                            ELSE c.info
                        END AS description, c.c_charge_id, c.chargename, c.c_cashline_id AS doc_id, 
                        CASE sign(c.total)
                            WHEN (-1) THEN 0::numeric
                            ELSE c.total
                        END::numeric(22,2) AS ingreso, 
                        CASE sign(c.total)
                            WHEN (-1) THEN c.total
                            ELSE 0::numeric
                        END::numeric(22,2) AS egreso
                   FROM ( SELECT cl.ad_client_id, cl.ad_org_id, cl.c_cashline_id, c.c_posjournal_id, pj.ad_user_id, cl.c_currency_id, c.statementdate::date AS datetrx, cl.docstatus, cl.description, pjp.info, pjp.tendertype, cl.cashtype, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, cl.c_currency_id, c.statementdate::timestamp with time zone, cl.ad_client_id, cl.ad_org_id)::numeric(22,2))::numeric(22,2) AS total
                           FROM c_cashline cl
                      JOIN c_cash c ON c.c_cash_id = cl.c_cash_id
                 JOIN c_posjournalpayments_v pjp ON pjp.c_cashline_id = cl.c_cashline_id
            JOIN c_posjournal pj ON pj.c_posjournal_id = c.c_posjournal_id
       LEFT JOIN c_charge ch ON ch.c_charge_id = cl.c_charge_id
      GROUP BY cl.ad_client_id, cl.ad_org_id, cl.c_cashline_id, c.c_posjournal_id, pj.ad_user_id, cl.c_currency_id, c.statementdate::date, cl.docstatus, cl.description, pjp.info, pjp.tendertype, cl.cashtype, ch.c_charge_id, ch.name) c)
UNION ALL 
         SELECT i.ad_client_id, i.ad_org_id, i.c_posjournal_id, i.ad_user_id, i.c_currency_id, i.dateinvoiced AS datetrx, i.docstatus, NULL::unknown AS category, i.tendertype, (i.documentno::text || ' '::text) || COALESCE(i.description, ''::character varying)::text AS description, i.c_charge_id, i.chargename, i.c_invoice_id AS doc_id, i.total AS ingreso, 0 AS egreso
           FROM ( SELECT i.ad_client_id, i.ad_org_id, i.documentno, i.description, ah.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, pjp.tendertype, i.dateinvoiced::date AS dateinvoiced, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2))::numeric(22,2) AS total
                   FROM c_invoice i
              JOIN c_posjournalpayments_v pjp ON pjp.c_invoice_credit_id = i.c_invoice_id
              INNER JOIN c_allocationhdr as ah ON ah.c_allocationhdr_id = pjp.c_allocationhdr_id
         JOIN c_posjournal pj ON pj.c_posjournal_id = ah.c_posjournal_id
    LEFT JOIN c_charge ch ON ch.c_charge_id = i.c_charge_id
   GROUP BY i.ad_client_id, i.ad_org_id, i.documentno, i.description, ah.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, pjp.tendertype, i.dateinvoiced::date, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name) i
      JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id;

ALTER TABLE c_pos_declaracionvalores_v OWNER TO libertya;

CREATE OR REPLACE VIEW v_dailysales AS 

select 'P' as trxtype, pjp.ad_client_id, pjp.ad_org_id, i.c_invoice_id, pjp.allocationdate as datetrx, pjp.c_payment_id, pjp.c_cashline_id, pjp.c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, pjp.amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name as groupname, bp.c_categoria_iva_id, ci.name as categorianame, (CASE WHEN pjp.c_invoice_credit_id IS NOT NULL THEN dt.c_doctype_id WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.c_pospaymentmedium_id ELSE p.c_pospaymentmedium_id END) as c_pospaymentmedium_id, (CASE WHEN pjp.c_invoice_credit_id IS NOT NULL THEN dt.docbasetype WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.name ELSE ppm.name END) as pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name as entidadfinancieraname, pjp.m_entidadfinancieraplan_id, efp.name as planname, pjp.docstatus, i.issotrx
from c_posjournalpayments_v as pjp 
inner join c_invoice as i on i.c_invoice_id = pjp.c_invoice_id
inner join c_bpartner as bp on bp.c_bpartner_id = i.c_bpartner_id
inner join c_bp_group as bpg on bpg.c_bp_group_id = bp.c_bp_group_id
left join c_categoria_iva as ci on ci.c_categoria_iva_id = bp.c_categoria_iva_id
left join c_payment as p on p.c_payment_id = pjp.c_payment_id
left join c_pospaymentmedium as ppm on ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
left join c_cashline as c on c.c_cashline_id = pjp.c_cashline_id
left join c_pospaymentmedium as ppmc on ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
left join m_entidadfinanciera as ef on ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
left join m_entidadfinancieraplan as efp on efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
left join c_invoice as cc on cc.c_invoice_id = pjp.c_invoice_credit_id
left join c_doctype as dt on cc.c_doctypetarget_id = dt.c_doctype_id
UNION ALL
select 'CAI' as trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day',i.dateinvoiced) as datetrx, null as c_payment_id, null as c_cashline_id, null as c_invoice_credit_id, 'CC' as tendertype, i.documentno, i.description, null as info, i.initialcurrentaccountamt as amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name as groupname, bp.c_categoria_iva_id, ci.name as categorianame, null as c_pospaymentmedium_id, null as pospaymentmediumname, null as m_entidadfinanciera_id, null as entidadfinancieraname, null as m_entidadfinancieraplan_id, null as planname, i.docstatus, i.issotrx
from c_invoice as i 
inner join c_bpartner as bp on bp.c_bpartner_id = i.c_bpartner_id
inner join c_bp_group as bpg on bpg.c_bp_group_id = bp.c_bp_group_id
left join c_categoria_iva as ci on ci.c_categoria_iva_id = bp.c_categoria_iva_id
where i.initialcurrentaccountamt > 0
UNION ALL
select 'I' as trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day',i.dateinvoiced) as datetrx, null as c_payment_id, null as c_cashline_id, null as c_invoice_credit_id, dt.docbasetype as tendertype, i.documentno, i.description, null as info, i.grandtotal as amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name as groupname, bp.c_categoria_iva_id, ci.name as categorianame, dt.c_doctype_id as c_pospaymentmedium_id, dt.name as pospaymentmediumname, null as m_entidadfinanciera_id, null as entidadfinancieraname, null as m_entidadfinancieraplan_id, null as planname, i.docstatus, i.issotrx
from c_invoice as i 
inner join c_doctype as dt on i.c_doctypetarget_id = dt.c_doctype_id
inner join c_bpartner as bp on bp.c_bpartner_id = i.c_bpartner_id
inner join c_bp_group as bpg on bpg.c_bp_group_id = bp.c_bp_group_id
left join c_categoria_iva as ci on ci.c_categoria_iva_id = bp.c_categoria_iva_id;

ALTER TABLE v_dailysales OWNER TO libertya;

-- 20120610-1510 Nuevas columnas en el tipo de documento para arrastrar los descuentos del pedido en el Crear Desde, además del precio. Nueva columna en la factura para marcar aquellos documentos que tienen arrastre de descuento del pedido y así poder gestionarlos
ALTER TABLE c_doctype ADD COLUMN dragorderprice character(1) NOT NULL DEFAULT 'Y'::bpchar;
ALTER TABLE c_doctype ADD COLUMN dragorderlinediscounts character(1) NOT NULL DEFAULT 'N'::bpchar;
ALTER TABLE c_doctype ADD COLUMN dragorderdocumentdiscounts character(1) NOT NULL DEFAULT 'N'::bpchar;
ALTER TABLE c_invoice ADD COLUMN managedragorderdiscounts character(1) NOT NULL DEFAULT 'N'::bpchar;


-- 20120611-11:26.  Replicacion: usar SET como prefijo en repArray para indicar forzado de dicho campo
CREATE OR REPLACE FUNCTION replication_event()
  RETURNS trigger AS
$BODY$
DECLARE 
	found integer; 
	replicationPos integer;
	v_newRepArray varchar; 
	aKeyColumn varchar;
	repSeq bigint;
	shouldReplicate varchar;
BEGIN 
	
	IF (TG_OP = 'DELETE') THEN
		
		SELECT INTO shouldReplicate VALUE FROM AD_PREFERENCE WHERE ATTRIBUTE = 'ReplicationEventsActive';
		IF (shouldReplicate <> 'Y') THEN
			RETURN OLD;
		END IF;
		
		
		IF (OLD.repArray IS NULL OR OLD.repArray = '') THEN
			RETURN OLD;
		END IF;
		
		IF replication_is_record_replicated(OLD.repArray) = 1 THEN
			
			SELECT INTO v_newRepArray replicationArray 
			FROM ad_tablereplication 
			WHERE ad_table_ID = TG_ARGV[0]::int;
			IF v_newRepArray IS NOT NULL AND v_newRepArray <> '' THEN
				INSERT INTO ad_changelog_replication (AD_Changelog_Replication_ID, AD_Client_ID, AD_Org_ID, isActive, Created, CreatedBy, Updated, UpdatedBy, AD_Table_ID, retrieveUID, operationtype, binaryvalue, reparray, columnvalues)
				SELECT nextval('seq_ad_changelog_replication'),OLD.AD_Client_ID,OLD.AD_Org_ID,'Y',OLD.Created,OLD.CreatedBy,OLD.Updated,OLD.UpdatedBy,TG_ARGV[0]::int,OLD.retrieveUID,'I',null,v_newRepArray,null;
			END IF;
		END IF;	
		
		RETURN OLD;
	END IF;
	
	IF (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') THEN
		
		SELECT INTO shouldReplicate VALUE FROM AD_PREFERENCE WHERE ATTRIBUTE = 'ReplicationEventsActive';
		IF (shouldReplicate <> 'Y') THEN
			RETURN NEW;
		END IF;
		

		 IF (NEW.repArray = 'SKIP') THEN
			NEW.repArray := NULL;
			return NEW;
		 END IF;
		 
		 
		 IF NEW.retrieveUID IS NULL OR NEW.retrieveUID = '' THEN 
			SELECT INTO replicationPos replicationArrayPos FROM AD_ReplicationHost WHERE thisHost = 'Y'; 
			IF replicationPos IS NULL THEN RAISE EXCEPTION 'Configuracion de Hosts incompleta: Ninguna sucursal tiene marca de Este Host'; END IF; 
			
			SELECT INTO repseq nextVal('repseq_' || TG_ARGV[1]);
			IF repseq IS NULL THEN RAISE EXCEPTION 'No hay definida una secuencia de replicacion para la tabla %', TG_ARGV[1]; END IF;
			NEW.retrieveUID := 'h'::varchar || replicationPos::varchar || '_' || repseq;
		END IF;
		 
		
		SELECT INTO found replication_is_valid_table(NEW.AD_Client_ID, TG_ARGV[0]::int, NEW.retrieveUID); 
		IF found = 0 THEN RETURN NEW; END IF; 
		
		
		IF (TG_OP = 'INSERT') THEN
			
			SELECT INTO v_newRepArray replicationArray 
			FROM ad_tablereplication 
			WHERE ad_table_ID = TG_ARGV[0]::int;
			
			IF v_newRepArray IS NULL OR v_newRepArray = '' THEN
				RETURN NEW;
			END IF;
			
			
			NEW.repArray := replace(v_newRepArray, '3', '1');
			RETURN NEW;
		
		
		ELSEIF (TG_OP = 'UPDATE') THEN 
		
				 IF (substr(NEW.repArray, 1, 3) = 'SET') THEN
					NEW.repArray := substr(NEW.repArray, 4, length(NEW.repArray)-3);
				ELSE
					v_newRepArray := replace(OLD.repArray, '2', '3');
					NEW.repArray := replace(v_newRepArray, '4', '5');
				END IF;
				RETURN NEW;
		END IF;
	END IF;
END; 
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION replication_event() OWNER TO libertya;


-- 20120611-11:26.  Funcion para simplificar el forzado del repArray
CREATE OR REPLACE FUNCTION replicationSetRepArray(tablename varchar, retrieveuid varchar , reparray varchar )
RETURNS NUMERIC AS
$BODY$
BEGIN
	EXECUTE 'UPDATE ' || tablename || ' SET repArray = ''SET' || reparray || ''' WHERE retrieveUID = ''' || retrieveuid || '''';
	RETURN 1;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE;
  
-- 20120611-11:57.  Replicacion Setear valor por defecto de la columna thishost 
ALTER TABLE ad_replicationhost ALTER COLUMN thishost SET DEFAULT 'N';

-- 20120611-1800 Eliminación de registros existentes por creación de informe de caja para FOCOS
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015556';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid like 'CORE-AD_Column_Trl-1015556-es_%';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015557';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid like 'CORE-AD_Column_Trl-1015557-es_%';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015558';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid like 'CORE-AD_Column_Trl-1015558-es_%';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015559';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid like 'CORE-AD_Column_Trl-1015559-es_%';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015560';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid like 'CORE-AD_Column_Trl-1015560-es_%';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015561';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid like 'CORE-AD_Column_Trl-1015561-es_%';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015562';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid like 'CORE-AD_Column_Trl-1015562-es_%';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015563';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid like 'CORE-AD_Column_Trl-1015563-es_%';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015564';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid like 'CORE-AD_Column_Trl-1015564-es_%';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015565';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid like 'CORE-AD_Column_Trl-1015565-es_%';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015566';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid like 'CORE-AD_Column_Trl-1015566-es_%';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015567';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid like 'CORE-AD_Column_Trl-1015567-es_%';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015568';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid like 'CORE-AD_Column_Trl-1015568-es_%';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015569';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid like 'CORE-AD_Column_Trl-1015569-es_%';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015570';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid like 'CORE-AD_Column_Trl-1015570-es_%';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015571';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid like 'CORE-AD_Column_Trl-1015571-es_%';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015572';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid like 'CORE-AD_Column_Trl-1015572-es_%';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015573';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid like 'CORE-AD_Column_Trl-1015573-es_%';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015574';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid like 'CORE-AD_Column_Trl-1015574-es_%';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015575';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid like 'CORE-AD_Column_Trl-1015575-es_%';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015576';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid like 'CORE-AD_Column_Trl-1015576-es_%';
DELETE FROM AD_PrintFormatItem WHERE ad_componentobjectuid = 'CORE-AD_PrintFormatItem-1038299';
DELETE FROM AD_PrintFormatItem_Trl WHERE ad_componentobjectuid like 'CORE-AD_PrintFormatItem_Trl-es_%-1038299';
DELETE FROM AD_PrintFormatItem WHERE ad_componentobjectuid = 'CORE-AD_PrintFormatItem-1038300';
DELETE FROM AD_PrintFormatItem_Trl WHERE ad_componentobjectuid like 'CORE-AD_PrintFormatItem_Trl-es_%-1038300';
DELETE FROM AD_PrintFormatItem WHERE ad_componentobjectuid = 'CORE-AD_PrintFormatItem-1038301';
DELETE FROM AD_PrintFormatItem_Trl WHERE ad_componentobjectuid like 'CORE-AD_PrintFormatItem_Trl-es_%-1038301';
DELETE FROM AD_PrintFormatItem WHERE ad_componentobjectuid = 'CORE-AD_PrintFormatItem-1038302';
DELETE FROM AD_PrintFormatItem_Trl WHERE ad_componentobjectuid like 'CORE-AD_PrintFormatItem_Trl-es_%-1038302';
DELETE FROM AD_PrintFormatItem WHERE ad_componentobjectuid = 'CORE-AD_PrintFormatItem-1038303';
DELETE FROM AD_PrintFormatItem_Trl WHERE ad_componentobjectuid like 'CORE-AD_PrintFormatItem_Trl-es_%-1038303';
DELETE FROM AD_PrintFormatItem WHERE ad_componentobjectuid = 'CORE-AD_PrintFormatItem-1038304';
DELETE FROM AD_PrintFormatItem_Trl WHERE ad_componentobjectuid like 'CORE-AD_PrintFormatItem_Trl-es_%-1038304';
DELETE FROM AD_PrintFormatItem WHERE ad_componentobjectuid = 'CORE-AD_PrintFormatItem-1038305';
DELETE FROM AD_PrintFormatItem_Trl WHERE ad_componentobjectuid like 'CORE-AD_PrintFormatItem_Trl-es_%-1038305';
DELETE FROM AD_PrintFormatItem WHERE ad_componentobjectuid = 'CORE-AD_PrintFormatItem-1038306';
DELETE FROM AD_PrintFormatItem_Trl WHERE ad_componentobjectuid like 'CORE-AD_PrintFormatItem_Trl-es_%-1038306';
DELETE FROM AD_PrintFormatItem WHERE ad_componentobjectuid = 'CORE-AD_PrintFormatItem-1038307';
DELETE FROM AD_PrintFormatItem_Trl WHERE ad_componentobjectuid like 'CORE-AD_PrintFormatItem_Trl-es_%-1038307';
DELETE FROM AD_PrintFormatItem WHERE ad_componentobjectuid = 'CORE-AD_PrintFormatItem-1038308';
DELETE FROM AD_PrintFormatItem_Trl WHERE ad_componentobjectuid like 'CORE-AD_PrintFormatItem_Trl-es_%-1038308';
DELETE FROM AD_PrintFormatItem WHERE ad_componentobjectuid = 'CORE-AD_PrintFormatItem-1038309';
DELETE FROM AD_PrintFormatItem_Trl WHERE ad_componentobjectuid like 'CORE-AD_PrintFormatItem_Trl-es_%-1038309';
DELETE FROM AD_PrintFormatItem WHERE ad_componentobjectuid = 'CORE-AD_PrintFormatItem-1038310';
DELETE FROM AD_PrintFormatItem_Trl WHERE ad_componentobjectuid like 'CORE-AD_PrintFormatItem_Trl-es_%-1038310';
DELETE FROM AD_PrintFormatItem WHERE ad_componentobjectuid = 'CORE-AD_PrintFormatItem-1038311';
DELETE FROM AD_PrintFormatItem_Trl WHERE ad_componentobjectuid like 'CORE-AD_PrintFormatItem_Trl-es_%-1038311';
DELETE FROM AD_PrintFormatItem WHERE ad_componentobjectuid = 'CORE-AD_PrintFormatItem-1038312';
DELETE FROM AD_PrintFormatItem_Trl WHERE ad_componentobjectuid like 'CORE-AD_PrintFormatItem_Trl-es_%-1038312';
DELETE FROM AD_PrintFormatItem WHERE ad_componentobjectuid = 'CORE-AD_PrintFormatItem-1038313';
DELETE FROM AD_PrintFormatItem_Trl WHERE ad_componentobjectuid like 'CORE-AD_PrintFormatItem_Trl-es_%-1038313';
DELETE FROM AD_PrintFormatItem WHERE ad_componentobjectuid = 'CORE-AD_PrintFormatItem-1038314';
DELETE FROM AD_PrintFormatItem_Trl WHERE ad_componentobjectuid like 'CORE-AD_PrintFormatItem_Trl-es_%-1038314';
DELETE FROM AD_PrintFormatItem WHERE ad_componentobjectuid = 'CORE-AD_PrintFormatItem-1038315';
DELETE FROM AD_PrintFormatItem_Trl WHERE ad_componentobjectuid like 'CORE-AD_PrintFormatItem_Trl-es_%-1038315';
DELETE FROM AD_PrintFormatItem WHERE ad_componentobjectuid = 'CORE-AD_PrintFormatItem-1038316';
DELETE FROM AD_PrintFormatItem_Trl WHERE ad_componentobjectuid like 'CORE-AD_PrintFormatItem_Trl-es_%-1038316';
DELETE FROM AD_PrintFormatItem WHERE ad_componentobjectuid = 'CORE-AD_PrintFormatItem-1038317';
DELETE FROM AD_PrintFormatItem_Trl WHERE ad_componentobjectuid like 'CORE-AD_PrintFormatItem_Trl-es_%-1038317';
DELETE FROM AD_PrintFormatItem WHERE ad_componentobjectuid = 'CORE-AD_PrintFormatItem-1038318';
DELETE FROM AD_PrintFormatItem_Trl WHERE ad_componentobjectuid like 'CORE-AD_PrintFormatItem_Trl-es_%-1038318';
DELETE FROM AD_PrintFormatItem WHERE ad_componentobjectuid = 'CORE-AD_PrintFormatItem-1038319';
DELETE FROM AD_PrintFormatItem_Trl WHERE ad_componentobjectuid like 'CORE-AD_PrintFormatItem_Trl-es_%-1038319';
DELETE FROM AD_Process_Trl WHERE ad_componentobjectuid like 'CORE-AD_Process_Trl-es_%-1010285';
DELETE FROM AD_Process_Access WHERE ad_componentobjectuid = 'CORE-AD_Process_Access-1010285-0';
DELETE FROM AD_Process_Para WHERE ad_componentobjectuid = 'CORE-AD_Process_Para-1010569';
DELETE FROM AD_Process_Para_Trl WHERE ad_componentobjectuid like 'CORE-AD_Process_Para_Trl-es_%-1010569';
DELETE FROM AD_Menu WHERE ad_componentobjectuid = 'CORE-AD_Menu-1010417';
DELETE FROM AD_Menu_Trl WHERE ad_componentobjectuid like 'CORE-AD_Menu_Trl-es_%-1010417';
DELETE FROM AD_TreeNodeMM WHERE ad_componentobjectuid = 'CORE-AD_TreeNodeMM-10-1010417';
DELETE FROM AD_Process_Access WHERE ad_componentobjectuid = 'CORE-AD_Process_Access-1010285-1010076';
DELETE FROM AD_TreeNodeMM WHERE ad_componentobjectuid = 'CORE-AD_TreeNodeMM-1010115-1010417';
DELETE FROM AD_Table_Trl WHERE ad_componentobjectuid like 'CORE-AD_Table_Trl-es%1010285';
DELETE FROM AD_Pinstance WHERE (Ad_Process_ID = 1010285);
DELETE FROM AD_Process WHERE ad_componentobjectuid = 'CORE-AD_Process-1010285';
DELETE FROM AD_PrintFormat WHERE ad_componentobjectuid = 'CORE-AD_PrintFormat-1010920';
DELETE FROM AD_Table WHERE ad_componentobjectuid = 'CORE-AD_Table-1010285';

-- 20120622 - Funcion que valida si un registro ya fue replicado
CREATE OR REPLACE FUNCTION replication_is_record_replicated(p_reparray character varying)
  RETURNS integer AS
$BODY$
DECLARE 
unChar char;
	
BEGIN 
	
	FOR i IN 1..length(p_repArray) LOOP
		IF substring(p_repArray, i, 1) <> '0' AND substring(p_repArray, i, 1) <> '1' THEN
			return 1;
		END IF;
	END LOOP;
	RETURN 0;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;

-- 20120625-10:42:59 Correccion en caso de eliminacion.  Ampliacion de retrieveUID
CREATE OR REPLACE FUNCTION replication_event()
  RETURNS trigger AS
$BODY$
DECLARE 
	found integer; 
	replicationPos integer;
	v_newRepArray varchar; 
	aKeyColumn varchar;
	repSeq bigint;
	shouldReplicate varchar;
BEGIN 
	
	IF (TG_OP = 'DELETE') THEN
		
		SELECT INTO shouldReplicate VALUE FROM AD_PREFERENCE WHERE ATTRIBUTE = 'ReplicationEventsActive';
		IF (shouldReplicate <> 'Y') THEN
			RETURN OLD;
		END IF;
		
		
		IF (OLD.repArray IS NULL OR OLD.repArray = '') THEN
			RETURN OLD;
		END IF;
		
		IF replication_is_record_replicated(OLD.repArray) = 1 THEN
			
			SELECT INTO v_newRepArray replicationArray 
			FROM ad_tablereplication 
			WHERE ad_table_ID = TG_ARGV[0]::int;

			v_newRepArray := replace(v_newRepArray, '3', '1');
			
			IF v_newRepArray IS NOT NULL AND v_newRepArray <> '' THEN
				INSERT INTO ad_changelog_replication (AD_Changelog_Replication_ID, AD_Client_ID, AD_Org_ID, isActive, Created, CreatedBy, Updated, UpdatedBy, AD_Table_ID, retrieveUID, operationtype, binaryvalue, reparray, columnvalues)
				SELECT nextval('seq_ad_changelog_replication'),OLD.AD_Client_ID,OLD.AD_Org_ID,'Y',now(),OLD.CreatedBy,now(),OLD.UpdatedBy,TG_ARGV[0]::int,OLD.retrieveUID,'I',null,v_newRepArray,null;
			END IF;
		END IF;	
		
		RETURN OLD;
	END IF;
	
	IF (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') THEN
		
		SELECT INTO shouldReplicate VALUE FROM AD_PREFERENCE WHERE ATTRIBUTE = 'ReplicationEventsActive';
		IF (shouldReplicate <> 'Y') THEN
			RETURN NEW;
		END IF;
		

		 IF (NEW.repArray = 'SKIP') THEN
			NEW.repArray := NULL;
			return NEW;
		 END IF;
		 
		 
		 IF NEW.retrieveUID IS NULL OR NEW.retrieveUID = '' THEN 
			SELECT INTO replicationPos replicationArrayPos FROM AD_ReplicationHost WHERE thisHost = 'Y'; 
			IF replicationPos IS NULL THEN RAISE EXCEPTION 'Configuracion de Hosts incompleta: Ninguna sucursal tiene marca de Este Host'; END IF; 
			
			SELECT INTO repseq nextVal('repseq_' || TG_ARGV[1]);
			IF repseq IS NULL THEN RAISE EXCEPTION 'No hay definida una secuencia de replicacion para la tabla %', TG_ARGV[1]; END IF;
			NEW.retrieveUID := 'h'::varchar || replicationPos::varchar || '_' || repseq || '_' || lower(TG_ARGV[1]);
		END IF;
		 
	
--		SELECT INTO found replication_is_valid_table(NEW.AD_Client_ID, TG_ARGV[0]::int, NEW.retrieveUID); 
--		IF found = 0 THEN RETURN NEW; END IF; 
		
		
		IF (TG_OP = 'INSERT') THEN
			

			IF (substr(NEW.repArray, 1, 3) = 'SET') THEN
				NEW.repArray := substr(NEW.repArray, 4, length(NEW.repArray)-3);
			ELSE

				SELECT INTO v_newRepArray replicationArray 
				FROM ad_tablereplication 
				WHERE ad_table_ID = TG_ARGV[0]::int;
			
				IF v_newRepArray IS NULL OR v_newRepArray = '' THEN
					RETURN NEW;
				END IF;

				NEW.repArray := replace(v_newRepArray, '3', '1');
			END IF;
			RETURN NEW;
		
		
		ELSEIF (TG_OP = 'UPDATE') THEN 
		
				 IF (substr(NEW.repArray, 1, 3) = 'SET') THEN
					NEW.repArray := substr(NEW.repArray, 4, length(NEW.repArray)-3);
				ELSE
					v_newRepArray := replace(OLD.repArray, '2', '3');
					NEW.repArray := replace(v_newRepArray, '4', '5');
				END IF;
				RETURN NEW;
		END IF;
	END IF;
END; 
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION replication_event() OWNER TO libertya;

-- 20120626-1530 Incorporación de campo value a plan de entidades financieras
ALTER TABLE m_entidadfinancieraplan ADD COLUMN value character varying(60);

UPDATE m_entidadfinancieraplan
SET value = name;

ALTER TABLE m_entidadfinancieraplan ALTER COLUMN value SET NOT NULL;

-- 20120628-1735 Se insertó la columna P_RevenueExchange_Acct en la tabla C_AcctSchema_Default
ALTER TABLE libertya.C_AcctSchema_Default ADD COLUMN P_RevenueExchange_Acct integer;

-- 20120628-1735 Se actualizó la columna P_RevenueExchange_Acct en la tabla C_AcctSchema_Default, poniendo el valor que tiene el campo P_Revenue_Acct
UPDATE libertya.C_AcctSchema_Default SET P_RevenueExchange_Acct = P_Revenue_Acct WHERE P_RevenueExchange_Acct IS NULL;

-- 20120628-1735 Se actualizó la columna P_RevenueExchange_Acct en la tabla C_AcctSchema_Default, indicando que su valor no puede ser null
ALTER TABLE libertya.C_AcctSchema_Default ALTER COLUMN P_RevenueExchange_Acct SET NOT NULL;

-- 20120628-1745 Se insertó la columna P_RevenueExchange_Acct en la tabla M_Product_Category_Acct
ALTER TABLE libertya.M_Product_Category_Acct ADD COLUMN P_RevenueExchange_Acct integer;

-- 20120628-1745 Se actualizó la columna P_RevenueExchange_Acct en la tabla M_Product_Category_Acct, poniendo el valor que tiene el campo P_Revenue_Acct
UPDATE libertya.M_Product_Category_Acct SET P_RevenueExchange_Acct = P_Revenue_Acct WHERE P_RevenueExchange_Acct IS NULL;

-- 20120628-1745 Se actualizó la columna P_RevenueExchange_Acct en la tabla M_Product_Category_Acct, indicando que su valor no puede ser null
ALTER TABLE libertya.M_Product_Category_Acct ALTER COLUMN P_RevenueExchange_Acct SET NOT NULL;

-- 20120628-1750 Se insertó la columna P_RevenueExchange_Acct en la tabla M_Product_Acct
ALTER TABLE libertya.M_Product_Acct ADD COLUMN P_RevenueExchange_Acct integer;

-- 20120628-1750 Se actualizó la columna P_RevenueExchange_Acct en la tabla M_Product_Acct, poniendo el valor que tiene el campo P_Revenue_Acct
UPDATE libertya.M_Product_Acct SET P_RevenueExchange_Acct = P_Revenue_Acct WHERE P_RevenueExchange_Acct IS NULL;

-- 20120628-1750 Se actualizó la columna P_RevenueExchange_Acct en la tabla M_Product_Acct, indicando que su valor no puede ser null
ALTER TABLE libertya.M_Product_Acct ALTER COLUMN P_RevenueExchange_Acct SET NOT NULL;

-- 20120628-1755 Se insertó la columna IsExchange en la tabla C_Order
ALTER TABLE libertya.C_Order ADD COLUMN IsExchange character(1) NOT NULL DEFAULT 'N'::bpchar;

-- 20120628-1800 Se insertó la columna IsExchange en la tabla C_Invoice
ALTER TABLE libertya.C_Invoice ADD COLUMN IsExchange character(1) NOT NULL DEFAULT 'N'::bpchar;

-- 20120529-1610 Modificación de la view c_invoice_v agregando la columna isexchange, eliminación y creación de las vistas dependientes
DROP VIEW rv_bpartneropen;
DROP VIEW v_projectedpayments;
DROP VIEW v_documents;
DROP VIEW c_invoiceline_v;
DROP VIEW c_invoice_v;

CREATE OR REPLACE VIEW c_invoice_v AS 
 SELECT i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.issotrx, i.documentno, i.docstatus, i.docaction, i.processing, i.processed, i.c_doctype_id, i.c_doctypetarget_id, i.c_order_id, i.description, i.isapproved, i.istransferred, i.salesrep_id, i.dateinvoiced, i.dateprinted, i.dateacct, i.c_bpartner_id, i.c_bpartner_location_id, i.ad_user_id, i.poreference, i.dateordered, i.c_currency_id, i.c_conversiontype_id, i.paymentrule, i.c_paymentterm_id, i.c_charge_id, i.m_pricelist_id, i.c_campaign_id, i.c_project_id, i.c_activity_id, i.isprinted, i.isdiscountprinted, i.ispaid, i.isindispute, i.ispayschedulevalid, NULL::unknown AS c_invoicepayschedule_id, i.chargeamt * d.signo_issotrx::numeric AS chargeamt, i.totallines, i.grandtotal * d.signo_issotrx::numeric * d.signo_issotrx::numeric AS grandtotal, d.signo_issotrx::numeric AS multiplier, 
        CASE
            WHEN "substring"(d.docbasetype::text, 2, 2) = 'P'::text THEN - 1::numeric
            ELSE 1::numeric
        END AS multiplierap, d.docbasetype, i.notexchangeablecredit, i.isexchange
   FROM c_invoice i
   JOIN c_doctype d ON i.c_doctypetarget_id = d.c_doctype_id
  WHERE i.ispayschedulevalid <> 'Y'::bpchar
UNION ALL 
 SELECT i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.issotrx, i.documentno, i.docstatus, i.docaction, i.processing, i.processed, i.c_doctype_id, i.c_doctypetarget_id, i.c_order_id, i.description, i.isapproved, i.istransferred, i.salesrep_id, i.dateinvoiced, i.dateprinted, i.dateacct, i.c_bpartner_id, i.c_bpartner_location_id, i.ad_user_id, i.poreference, i.dateordered, i.c_currency_id, i.c_conversiontype_id, i.paymentrule, i.c_paymentterm_id, i.c_charge_id, i.m_pricelist_id, i.c_campaign_id, i.c_project_id, i.c_activity_id, i.isprinted, i.isdiscountprinted, i.ispaid, i.isindispute, i.ispayschedulevalid, ips.c_invoicepayschedule_id, NULL::unknown AS chargeamt, NULL::unknown AS totallines, ips.dueamt AS grandtotal, d.signo_issotrx AS multiplier, 
        CASE
            WHEN "substring"(d.docbasetype::text, 2, 2) = 'P'::text THEN - 1::numeric
            ELSE 1::numeric
        END AS multiplierap, d.docbasetype, i.notexchangeablecredit, i.isexchange
   FROM c_invoice i
   JOIN c_doctype d ON i.c_doctypetarget_id = d.c_doctype_id
   JOIN c_invoicepayschedule ips ON i.c_invoice_id = ips.c_invoice_id
  WHERE i.ispayschedulevalid = 'Y'::bpchar AND ips.isvalid = 'Y'::bpchar;

ALTER TABLE c_invoice_v OWNER TO libertya;

CREATE OR REPLACE VIEW c_invoiceline_v AS 
 SELECT il.ad_client_id, il.ad_org_id, il.c_invoiceline_id, i.c_invoice_id, i.salesrep_id, i.c_bpartner_id, il.m_product_id, i.documentno, i.dateinvoiced, i.dateacct, i.issotrx, i.docstatus, round(i.multiplier * il.linenetamt, 2) AS linenetamt, round(i.multiplier * il.pricelist * il.qtyinvoiced, 2) AS linelistamt, 
        CASE
            WHEN COALESCE(il.pricelimit, 0::numeric) = 0::numeric THEN round(i.multiplier * il.linenetamt, 2)
            ELSE round(i.multiplier * il.pricelimit * il.qtyinvoiced, 2)
        END AS linelimitamt, round(i.multiplier * il.pricelist * il.qtyinvoiced - il.linenetamt, 2) AS linediscountamt, 
        CASE
            WHEN COALESCE(il.pricelimit, 0::numeric) = 0::numeric THEN 0::numeric
            ELSE round(i.multiplier * il.linenetamt - il.pricelimit * il.qtyinvoiced, 2)
        END AS lineoverlimitamt, il.qtyinvoiced, il.qtyentered, il.line, il.c_orderline_id, il.c_uom_id
   FROM c_invoice_v i, c_invoiceline il
  WHERE i.c_invoice_id = il.c_invoice_id;

ALTER TABLE c_invoiceline_v OWNER TO libertya;

CREATE OR REPLACE VIEW v_documents AS 
( SELECT 'C_Invoice' AS documenttable, i.c_invoice_id AS document_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, i.documentno, i.issotrx, i.docstatus, 
        CASE
            WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
            ELSE i.dateinvoiced
        END AS datetrx, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal AS amount, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced AS truedatetrx
   FROM c_invoice_v i
   JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   LEFT JOIN c_invoicepayschedule ips ON i.c_invoicepayschedule_id = ips.c_invoicepayschedule_id
UNION ALL 
 SELECT 'C_Payment' AS documenttable, p.c_payment_id AS document_id, p.ad_client_id, p.ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt AS amount, NULL::integer AS c_invoicepayschedule_id, p.duedate, p.datetrx AS truedatetrx
   FROM c_payment p
   JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id)
UNION ALL 
 SELECT 'C_CashLine' AS documenttable, cl.c_cashline_id AS document_id, cl.ad_client_id, cl.ad_org_id, cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby, 
        CASE
            WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id
            ELSE i.c_bpartner_id
        END AS c_bpartner_id, dt.c_doctype_id, 
        CASE
            WHEN cl.amount < 0.0 THEN 1
            ELSE (-1)
        END AS signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, '@line@'::text || cl.line::character varying::text AS documentno, 
        CASE
            WHEN cl.amount < 0.0 THEN 'N'::bpchar
            ELSE 'Y'::bpchar
        END AS issotrx, cl.docstatus, c.statementdate AS datetrx, c.dateacct, cl.c_currency_id, NULL::integer AS c_conversiontype_id, abs(cl.amount) AS amount, NULL::integer AS c_invoicepayschedule_id, NULL::timestamp without time zone AS duedate, c.statementdate AS truedatetrx
   FROM c_cashline cl
   JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
   JOIN ( SELECT d.ad_client_id, d.c_doctype_id, d.name, d.printname
      FROM c_doctype d
     WHERE d.doctypekey::text = 'CMC'::text) dt ON cl.ad_client_id = dt.ad_client_id
   LEFT JOIN c_invoice i ON cl.c_invoice_id = i.c_invoice_id;

ALTER TABLE v_documents OWNER TO libertya;

CREATE OR REPLACE VIEW rv_bpartneropen AS 
 SELECT i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_currency_id, i.grandtotal * i.multiplierap AS amt, invoiceopen(i.c_invoice_id, i.c_invoicepayschedule_id) * i.multiplierap AS openamt, i.dateinvoiced AS datedoc, COALESCE(daysbetween(now(), ips.duedate::timestamp with time zone), paymenttermduedays(i.c_paymentterm_id, i.dateinvoiced::timestamp with time zone, now())) AS daysdue
   FROM c_invoice_v i
   LEFT JOIN c_invoicepayschedule ips ON i.c_invoicepayschedule_id = ips.c_invoicepayschedule_id
  WHERE i.ispaid = 'N'::bpchar AND (i.docstatus = 'CO'::bpchar OR i.docstatus = 'CL'::bpchar)
UNION 
 SELECT p.ad_client_id, p.ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_currency_id, p.payamt * p.multiplierap * (- 1::numeric) AS amt, paymentavailable(p.c_payment_id) * p.multiplierap * (- 1::numeric) AS openamt, p.datetrx AS datedoc, NULL::unknown AS daysdue
   FROM c_payment_v p
  WHERE p.isallocated = 'N'::bpchar AND p.c_bpartner_id IS NOT NULL AND (p.docstatus = 'CO'::bpchar OR p.docstatus = 'CL'::bpchar);

ALTER TABLE rv_bpartneropen OWNER TO libertya;

CREATE OR REPLACE VIEW v_projectedpayments AS 
 SELECT v.documenttable, v.document_id, v.ad_client_id, v.ad_org_id, v.isactive, v.created, v.createdby, v.updated, v.updatedby, v.c_bpartner_id, v.c_doctype_id, v.signo_issotrx, v.doctypename, v.doctypeprintname, v.documentno, v.issotrx, v.docstatus, v.datetrx, v.dateacct, v.c_currency_id, v.c_conversiontype_id, v.amount, v.c_invoicepayschedule_id, v.duedate, i.ispaid, NULL::character varying(20) AS checkno
   FROM v_documents v
   JOIN c_invoice i ON i.c_invoice_id = v.document_id
  WHERE i.issotrx = 'N'::bpchar AND v.documenttable = 'C_Invoice'::text
UNION ALL 
 SELECT v.documenttable, v.document_id, v.ad_client_id, v.ad_org_id, v.isactive, v.created, v.createdby, v.updated, v.updatedby, v.c_bpartner_id, v.c_doctype_id, v.signo_issotrx, 'Cheque' AS doctypename, v.doctypeprintname, v.documentno, v.issotrx, v.docstatus, v.datetrx, v.dateacct, v.c_currency_id, v.c_conversiontype_id, v.amount, v.c_invoicepayschedule_id, v.duedate, NULL::character(1) AS ispaid, p.checkno
   FROM v_documents v
   JOIN c_payment p ON p.c_payment_id = v.document_id
  WHERE p.issotrx = 'N'::bpchar AND p.tendertype = 'K'::bpchar AND p.datetrx < p.duedate;

ALTER TABLE v_projectedpayments OWNER TO libertya;

-- 20120630-1540 Incorporación de columna value a la tabla C_Charge
ALTER TABLE c_charge ADD COLUMN value character varying(60);

UPDATE c_charge
SET value = name;

ALTER TABLE c_charge ALTER COLUMN value SET NOT NULL;

-- 20120630-1840 Incorporación de columna C_Tax_ID a la tabla C_Categoria_IVA
ALTER TABLE libertya.C_Categoria_IVA ADD COLUMN C_Tax_ID INTEGER;

-- 20120701-1200 Procedure base para validacion de estado de los registros en replicación
CREATE TYPE rep_status AS (tablename varchar, retrieveuid varchar, reparray varchar, created timestamp); 
CREATE OR REPLACE FUNCTION replication_record_status(p_clientid int, p_xtraclause varchar)
  RETURNS setof rep_status AS
$BODY$
DECLARE
	atable varchar;
	tablenames varchar;
	astatus rep_status;
	statuses record;
	xtraclause varchar;
BEGIN
	xtraclause := ' 1 = 1 ';
	IF p_xtraclause is not null THEN
		xtraclause := p_xtraclause;
	END IF;

	tablenames := '';
	FOR atable IN (select table_name from information_schema.columns where lower(column_name) = 'reparray' and table_schema = 'libertya' order by table_name)  LOOP
		FOR astatus IN EXECUTE 'SELECT ''' || atable || ''' as tablename, retrieveuid, reparray, created FROM ' || atable || ' WHERE ad_client_id = ' || p_clientid || '  AND ' || xtraclause LOOP
			return next astatus;
		END LOOP;
	END LOOP;
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE;

-- 20120705-0900 Vista para el informe de movimientos por artículo
CREATE OR REPLACE VIEW v_product_movements AS 
 SELECT m.tablename, m.ad_client_id, m.ad_org_id, o.value AS orgvalue, o.name AS orgname, m.documentno, m.docstatus, m.description, m.datetrx, m.m_product_id, m.qty, m.type, m.aditionaltype, m.c_charge_id, m.chargename, p.name AS productname, p.value AS productvalue
   FROM (        (        (        (        (        (        (         SELECT 'M_Transfer' AS tablename, t.ad_client_id, t.ad_org_id, t.documentno, t.docstatus, t.description, t.datetrx, tl.m_product_id, tl.qty, t.transfertype AS type, t.movementtype AS aditionaltype, c.c_charge_id, c.name AS chargename
                                                                   FROM m_transfer t
                                                              JOIN m_transferline tl ON tl.m_transfer_id = t.m_transfer_id
                                                         LEFT JOIN c_charge c ON c.c_charge_id = t.c_charge_id
                                                        UNION ALL 
                                                                 SELECT 'M_ProductChange' AS tablename, pc.ad_client_id, pc.ad_org_id, pc.documentno, pc.docstatus, pc.description, pc.datetrx, pc.m_product_id, pc.productqty AS qty, NULL::unknown AS type, NULL::unknown AS aditionaltype, NULL::unknown AS c_charge_id, NULL::unknown AS chargename
                                                                   FROM m_productchange pc)
                                                UNION ALL 
                                                         SELECT 'M_ProductChange' AS tablename, pc.ad_client_id, pc.ad_org_id, pc.documentno, pc.docstatus, pc.description, pc.datetrx, pc.m_product_to_id AS m_product_id, pc.productqty * (-1)::numeric AS qty, NULL::unknown AS type, NULL::unknown AS aditionaltype, NULL::unknown AS c_charge_id, NULL::unknown AS chargename
                                                           FROM m_productchange pc)
                                        UNION ALL 
                                                 SELECT 'M_InOut' AS tablename, io.ad_client_id, io.ad_org_id, io.documentno, io.docstatus, io.description, io.movementdate AS datetrx, iol.m_product_id, iol.movementqty AS qty, dt.name AS type, NULL::unknown AS aditionaltype, NULL::unknown AS c_charge_id, NULL::unknown AS chargename
                                                   FROM m_inout io
                                              JOIN m_inoutline iol ON iol.m_inout_id = io.m_inout_id
                                         JOIN c_doctype dt ON dt.c_doctype_id = io.c_doctype_id)
                                UNION ALL 
                                         SELECT 'M_Splitting' AS tablename, s.ad_client_id, s.ad_org_id, s.documentno, s.docstatus, s.comments AS description, s.datetrx, s.m_product_id, s.productqty * (-1)::numeric AS qty, NULL::unknown AS type, NULL::unknown AS aditionaltype, NULL::unknown AS c_charge_id, NULL::unknown AS chargename
                                           FROM m_splitting s)
                        UNION ALL 
                                 SELECT 'M_Splitting' AS tablename, s.ad_client_id, s.ad_org_id, s.documentno, s.docstatus, s.comments AS description, s.datetrx, sl.m_product_to_id AS m_product_id, sl.productqty AS qty, NULL::unknown AS type, NULL::unknown AS aditionaltype, NULL::unknown AS c_charge_id, NULL::unknown AS chargename
                                   FROM m_splitting s
                              JOIN m_splittingline sl ON sl.m_splitting_id = s.m_splitting_id)
                UNION ALL 
                         SELECT 'M_Inventory' AS tablename, i.ad_client_id, i.ad_org_id, i.documentno, i.docstatus, i.description, i.movementdate AS datetrx, il.m_product_id, il.qtycount AS qty, dt.name AS type, i.inventorykind AS aditionaltype, c.c_charge_id, c.name AS chargename
                           FROM m_inventory i
                      JOIN m_inventoryline il ON i.m_inventory_id = il.m_inventory_id
                 JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
            LEFT JOIN c_charge c ON c.c_charge_id = i.c_charge_id
           WHERE NOT (i.m_inventory_id IN ( SELECT DISTINCT m_transfer.m_inventory_id
                    FROM m_transfer)) AND NOT (i.m_inventory_id IN ( SELECT DISTINCT m_productchange.m_inventory_id
                    FROM m_productchange)) AND NOT (i.m_inventory_id IN ( SELECT DISTINCT m_productchange.void_inventory_id
                    FROM m_productchange)) AND NOT (i.m_inventory_id IN ( SELECT DISTINCT s.m_inventory_id
                    FROM m_splitting s)) AND NOT (i.m_inventory_id IN ( SELECT DISTINCT s.void_inventory_id
                    FROM m_splitting s)))
        UNION ALL 
                 SELECT 'M_Movement' AS tablename, m.ad_client_id, m.ad_org_id, m.documentno, m.docstatus, m.description, m.movementdate AS datetrx, ml.m_product_id, ml.movementqty AS qty, dt.name AS type, NULL::unknown AS aditionaltype, NULL::unknown AS c_charge_id, NULL::unknown AS chargename
                   FROM m_movement m
              JOIN m_movementline ml ON ml.m_movement_id = m.m_movement_id
         JOIN c_doctype dt ON dt.c_doctype_id = m.c_doctype_id) m
   JOIN m_product p ON p.m_product_id = m.m_product_id
   JOIN ad_org o ON o.ad_org_id = m.ad_org_id;

ALTER TABLE v_product_movements OWNER TO libertya;

-- 20120705-1719 Incorporación de columna Proyecto a Transferencias Bancarias para luego asignarselas a las transferencias
ALTER TABLE C_BankTransfer ADD COLUMN c_project_id integer;

-- 20120706-1900 Se inserto la columna IsReceiptSeq en la tabla C_DocType
ALTER TABLE libertya.C_DocType ADD COLUMN IsReceiptSeq character(1); 

-- 20120706-1900 Se inserto la columna IsPaymentOrderSeq en la tabla C_DocType
ALTER TABLE libertya.C_DocType ADD COLUMN IsPaymentOrderSeq character(1); 

-- 20120710-1130 Se asigno el valor N a la columna IsReceiptSeq para todos los registros previos de C_Doctype
UPDATE libertya.C_DocType SET IsReceiptSeq = 'N' WHERE IsReceiptSeq IS NULL;

-- 20120710-1130 Se asigno el valor N a la columna IsPaymentOrderSeq para todos los registros previos de C_Doctype
UPDATE libertya.C_DocType SET IsPaymentOrderSeq = 'N' WHERE IsPaymentOrderSeq IS NULL; 

-- 20120710-1130 Se asigno el valor N por defecto para la columna IsReceiptSeq de C_Doctype
ALTER TABLE libertya.C_DocType ALTER COLUMN IsReceiptSeq SET DEFAULT 'N'::bpchar;

-- 20120710-1130 Se asigno el valor N por defecto para la columna IsPaymentOrderSeq de C_Doctype
ALTER TABLE libertya.C_DocType ALTER COLUMN IsPaymentOrderSeq SET DEFAULT 'N'::bpchar;

-- 20120706-1930 Se inserto la columna C_DocType_ID en la tabla C_AllocationHdr
ALTER TABLE libertya.C_AllocationHdr ADD COLUMN C_DocType_ID integer; 

-- 20120709-1945 Inserción de la columna IsNormalizedBank a la tabla C_POSPaymentMedium 
ALTER TABLE libertya.C_POSPaymentMedium ADD COLUMN IsNormalizedBank character(1) NOT NULL DEFAULT 'N'::bpchar;

-- 20120709-1945 Inserción de la columna C_Bank_ID a la tabla C_POSPaymentMedium 
ALTER TABLE libertya.C_POSPaymentMedium ADD COLUMN C_Bank_ID integer;

-- 20120709-1955 Inserción de la columna C_Bank_ID a la tabla C_Payment
ALTER TABLE libertya.C_Payment ADD COLUMN C_Bank_ID integer;

-- 20120711-1830 Inserción de la columna c_doctype_id a la tabla M_Transfer. Se pone el valor 0 para todos los registros existentes. Se marca la columna como obligatoria.
ALTER TABLE libertya.M_Transfer ADD COLUMN c_doctype_id integer;
UPDATE libertya.M_Transfer SET C_Doctype_ID = 0 WHERE C_Doctype_ID IS NULL;
ALTER TABLE libertya.M_Transfer ALTER COLUMN c_doctype_id SET NOT NULL;

-- 20120712-1910 Modificación de la función currencyrate para que trunque las fechas quitando la hora
CREATE OR REPLACE FUNCTION currencyrate(p_curfrom_id integer, p_curto_id integer, p_convdate timestamp with time zone, p_conversiontype_id integer, p_client_id integer, p_org_id integer)
  RETURNS numeric AS
$BODY$
	
DECLARE
	--	Currency From variables
	cf_IsEuro		CHAR(1);
	cf_IsEMUMember		CHAR(1);
	cf_EMUEntryDate		timestamp with time zone;
	cf_EMURate		NUMERIC;
	--	Currency To variables
	ct_IsEuro		CHAR(1);
	ct_IsEMUMember		CHAR(1);
	ct_EMUEntryDate	DATE;
	ct_EMURate		NUMERIC;
	--	Triangle
	v_CurrencyFrom		INTEGER;
	v_CurrencyTo		INTEGER;
	v_CurrencyEuro		INTEGER;
	--
	v_ConvDate		timestamp with time zone := now();
	v_ConversionType_ID	INTEGER := 0;
	v_Rate			NUMERIC;
	c			RECORD;			

BEGIN
--	No Conversion
	IF (p_CurFrom_ID = p_CurTo_ID) THEN
		RETURN 1;
	END IF;
	--	Default Date Parameter
	IF (p_ConvDate IS NOT NULL) THEN
		v_ConvDate := p_ConvDate;   --  SysDate
	END IF;
    --  Default Conversion Type
	IF (p_ConversionType_ID IS NULL OR p_ConversionType_ID = 0) THEN
		BEGIN
		    SELECT C_ConversionType_ID 
		      INTO STRICT v_ConversionType_ID
		    FROM C_ConversionType 
		    WHERE IsDefault='Y'
		      AND AD_Client_ID IN (0,p_Client_ID)
		    ORDER BY AD_Client_ID DESC
		    LIMIT 1;
		EXCEPTION WHEN OTHERS THEN
		    RAISE NOTICE 'Conversion Type Not Found';
		END;
    ELSE
        	v_ConversionType_ID := p_ConversionType_ID;
	END IF;

	--	Get Currency Info
	SELECT	MAX(IsEuro), MAX(IsEMUMember), MAX(EMUEntryDate), MAX(EMURate)
	  INTO	cf_IsEuro, cf_IsEMUMember, cf_EMUEntryDate, cf_EMURate
	FROM		C_Currency
	  WHERE	C_Currency_ID = p_CurFrom_ID;
	-- Not Found
	IF (cf_IsEuro IS NULL) THEN
		RAISE NOTICE 'From Currency Not Found';
		RETURN NULL;
	END IF;
	SELECT	MAX(IsEuro), MAX(IsEMUMember), MAX(EMUEntryDate), MAX(EMURate)
	  INTO	ct_IsEuro, ct_IsEMUMember, ct_EMUEntryDate, ct_EMURate
	FROM		C_Currency
	  WHERE	C_Currency_ID = p_CurTo_ID;
	-- Not Found
	IF (ct_IsEuro IS NULL) THEN
		RAISE NOTICE 'To Currency Not Found';
		RETURN NULL;
	END IF;

	--	Fixed - From Euro to EMU
	IF (cf_IsEuro = 'Y' AND ct_IsEMUMember ='Y' AND v_ConvDate >= ct_EMUEntryDate) THEN
		RETURN ct_EMURate;
	END IF;

	--	Fixed - From EMU to Euro
	IF (ct_IsEuro = 'Y' AND cf_IsEMUMember ='Y' AND v_ConvDate >= cf_EMUEntryDate) THEN
		RETURN 1 / cf_EMURate;
	END IF;

	--	Fixed - From EMU to EMU
	IF (cf_IsEMUMember = 'Y' AND cf_IsEMUMember ='Y'
			AND v_ConvDate >= cf_EMUEntryDate AND v_ConvDate >= ct_EMUEntryDate) THEN
		RETURN ct_EMURate / cf_EMURate;
	END IF;

	--	Flexible Rates
	v_CurrencyFrom := p_CurFrom_ID;
	v_CurrencyTo := p_CurTo_ID;

	-- if EMU Member involved, replace From/To Currency
	IF ((cf_isEMUMember = 'Y' AND v_ConvDate >= cf_EMUEntryDate)
	  OR (ct_isEMUMember = 'Y' AND v_ConvDate >= ct_EMUEntryDate)) THEN
		SELECT	MAX(C_Currency_ID)
		  INTO	v_CurrencyEuro
		FROM		C_Currency
		WHERE	IsEuro = 'Y';
		-- Conversion Rate not Found
		IF (v_CurrencyEuro IS NULL) THEN
			RAISE NOTICE 'Euro Not Found';
			RETURN NULL;
		END IF;
		IF (cf_isEMUMember = 'Y' AND v_ConvDate >= cf_EMUEntryDate) THEN
			v_CurrencyFrom := v_CurrencyEuro;
		ELSE
			v_CurrencyTo := v_CurrencyEuro;
		END IF;
	END IF;

	--	Get Rate

	BEGIN
		FOR c IN SELECT	MultiplyRate
			FROM	C_Conversion_Rate
			WHERE	C_Currency_ID=v_CurrencyFrom AND C_Currency_ID_To=v_CurrencyTo
			  AND	C_ConversionType_ID=v_ConversionType_ID
			  AND	TRUNC(v_ConvDate) BETWEEN TRUNC(ValidFrom) AND TRUNC(ValidTo)
			  AND	AD_Client_ID IN (0,p_Client_ID) AND AD_Org_ID IN (0,p_Org_ID)
			ORDER BY AD_Client_ID DESC, AD_Org_ID DESC, ValidFrom DESC
			LIMIT 1  -- Ader: mejora de performance
		LOOP
			v_Rate := c.MultiplyRate;
			EXIT;	--	only first
		END LOOP;
		IF (v_Rate IS NOT NULL) THEN
		     EXIT; -- Sale del BEGIN END
		END IF;	 
		-- ELSE v_Rate IS NULL : se busca la conversion inversa invirtiendo to-from  y diviendo a 1 por el resultado
		FOR c IN SELECT	MultiplyRate
			FROM	C_Conversion_Rate
			WHERE	C_Currency_ID=v_CurrencyTo AND C_Currency_ID_To=v_CurrencyFrom
			  AND	C_ConversionType_ID=v_ConversionType_ID
			  AND	TRUNC(v_ConvDate) BETWEEN TRUNC(ValidFrom) AND TRUNC(ValidTo)
			  AND	AD_Client_ID IN (0,p_Client_ID) AND AD_Org_ID IN (0,p_Org_ID)
			ORDER BY AD_Client_ID DESC, AD_Org_ID DESC, ValidFrom DESC
			LIMIT 1  -- Ader: mejora de performance
		LOOP
			v_Rate := 1::numeric/c.MultiplyRate; -- Null en divisor genera Null
			EXIT;	--	only first
		END LOOP;
	END;
	--	Not found
	IF (v_Rate IS NULL) THEN
		RAISE NOTICE 'Conversion Rate Not Found';
		RETURN NULL;
	END IF;

	--	Currency From was EMU
	IF (cf_isEMUMember = 'Y' AND v_ConvDate >= cf_EMUEntryDate) THEN
		RETURN v_Rate / cf_EMURate;
	END IF;

	--	Currency To was EMU
	IF (ct_isEMUMember = 'Y' AND v_ConvDate >= ct_EMUEntryDate) THEN
		RETURN v_Rate * ct_EMURate;
	END IF;

	RETURN v_Rate;

EXCEPTION WHEN OTHERS THEN
	RAISE NOTICE '%', SQLERRM;
	RETURN NULL;

	
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION currencyrate(integer, integer, timestamp with time zone, integer, integer, integer) OWNER TO libertya;

-- 20120718-1718 Incorporación de configuración de TPV para autorizar los descuentos manuales generales realizados
ALTER TABLE c_pos ADD COLUMN authorizegeneralmanualdiscount character(1) NOT NULL DEFAULT 'N'::bpchar;

-- 20120807-1447 Incorporación de nuevas columnas a importar en la importación de Entidades Comerciales 
update ad_system set dummy = (SELECT addcolumnifnotexists('i_bpartner','plaza', 'character varying(100)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_bpartner','address3', 'character varying(60)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_bpartner','address4', 'character varying(60)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_bpartner','contactphone', 'character varying(40)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_bpartner','contactphone2', 'character varying(40)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_bpartner','contactphone3', 'character varying(40)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_bpartner','contactfax', 'character varying(40)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_bpartner','isdn', 'character varying(40)'));

-- 20120807-1547 Incorporación de nuevas columnas a importar en la importación de Artículos 
update ad_system set dummy = (SELECT addcolumnifnotexists('i_product','productfamily_value', 'character varying(60)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_product','isbom', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));

-- 20120808-1545 Inserción de la columna IsMandatoryBank a la tabla C_POSPaymentMedium 
ALTER TABLE C_POSPaymentMedium ADD COLUMN IsMandatoryBank character(1) NOT NULL DEFAULT 'N'::bpchar;

-- 20120809-1741 Incorporación de parámetros adicionales para importación de facturas
update ad_system set dummy = (SELECT addcolumnifnotexists('i_invoice','paymentrule', 'character varying(2)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_invoice','createcashline', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_invoice','orgvalue', 'character varying(40)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_invoice','salesrep_name', 'character varying(60)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_invoice','documentnobysequence', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_invoice','pricelist_name', 'character varying(60)'));
ALTER TABLE i_invoice ALTER COLUMN taxindicator TYPE character varying(10);

-- 20120813-2225 Incorporación de eliminaciones por patch de migraciones generado
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010450';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010449';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010448';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010447';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010446';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010445';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010444';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010443';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010442';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010441';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010440';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010439';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010438';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010437';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010436';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010435';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010434';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010433';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010432';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010431';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010430';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010429';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010428';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010427';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010426';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010425';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010424';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010423';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010422';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010421';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010420';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010419';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010418';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010417';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010416';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010415';
DELETE FROM AD_ImpFormat WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat-1010072';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010414';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010413';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010412';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010411';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010410';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010409';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010408';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010407';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010406';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010405';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010404';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010403';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010402';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010401';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010400';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010399';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010398';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010397';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010396';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010395';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010394';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010393';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010392';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010391';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010390';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010389';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010388';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010387';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010386';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010385';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010384';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010383';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010382';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010381';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010380';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010379';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010378';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010377';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010376';
DELETE FROM AD_ImpFormat WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat-1010071';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_PY-1010884';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_MX-1010884';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_AR-1010884';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_ES-1010884';
DELETE FROM AD_Message WHERE ad_componentobjectuid = 'CORE-AD_Message-1010884';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_PY-1010883';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_MX-1010883';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_AR-1010883';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_ES-1010883';
DELETE FROM AD_Message WHERE ad_componentobjectuid = 'CORE-AD_Message-1010883';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_PY-1010882';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_MX-1010882';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_AR-1010882';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_ES-1010882';
DELETE FROM AD_Message WHERE ad_componentobjectuid = 'CORE-AD_Message-1010882';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_PY-1010881';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_MX-1010881';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_AR-1010881';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_ES-1010881';
DELETE FROM AD_Message WHERE ad_componentobjectuid = 'CORE-AD_Message-1010881';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_PY-1010880';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_MX-1010880';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_AR-1010880';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_ES-1010880';
DELETE FROM AD_Message WHERE ad_componentobjectuid = 'CORE-AD_Message-1010880';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_PY-1010879';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_MX-1010879';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_AR-1010879';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_ES-1010879';
DELETE FROM AD_Message WHERE ad_componentobjectuid = 'CORE-AD_Message-1010879';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_PY-1010878';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_MX-1010878';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_AR-1010878';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_ES-1010878';
DELETE FROM AD_Message WHERE ad_componentobjectuid = 'CORE-AD_Message-1010878';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_PY-1010877';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_MX-1010877';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_AR-1010877';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_ES-1010877';
DELETE FROM AD_Message WHERE ad_componentobjectuid = 'CORE-AD_Message-1010877';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_PY-1010876';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_MX-1010876';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_AR-1010876';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_ES-1010876';
DELETE FROM AD_Message WHERE ad_componentobjectuid = 'CORE-AD_Message-1010876';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_PY-1010875';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_MX-1010875';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_AR-1010875';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_ES-1010875';
DELETE FROM AD_Message WHERE ad_componentobjectuid = 'CORE-AD_Message-1010875';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010375';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010374';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010373';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010372';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010371';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010370';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010369';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010368';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010367';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010366';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010365';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010364';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010363';
DELETE FROM AD_ImpFormat_Row WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat_Row-1010362';
DELETE FROM AD_ImpFormat WHERE ad_componentobjectuid = 'CORE-AD_ImpFormat-1010070';
DELETE FROM AD_Process_Para_Trl WHERE ad_componentobjectuid = 'CORE-AD_Process_Para_Trl-es_PY-1010606';
DELETE FROM AD_Process_Para_Trl WHERE ad_componentobjectuid = 'CORE-AD_Process_Para_Trl-es_MX-1010606';
DELETE FROM AD_Process_Para_Trl WHERE ad_componentobjectuid = 'CORE-AD_Process_Para_Trl-es_AR-1010606';
DELETE FROM AD_Process_Para_Trl WHERE ad_componentobjectuid = 'CORE-AD_Process_Para_Trl-es_ES-1010606';
DELETE FROM AD_Process_Para WHERE ad_componentobjectuid = 'CORE-AD_Process_Para-1010606';
DELETE FROM AD_Ref_List_Trl WHERE ad_componentobjectuid = 'CORE-AD_Ref_List_Trl-es_PY-1010536';
DELETE FROM AD_Ref_List_Trl WHERE ad_componentobjectuid = 'CORE-AD_Ref_List_Trl-es_MX-1010536';
DELETE FROM AD_Ref_List_Trl WHERE ad_componentobjectuid = 'CORE-AD_Ref_List_Trl-es_AR-1010536';
DELETE FROM AD_Ref_List_Trl WHERE ad_componentobjectuid = 'CORE-AD_Ref_List_Trl-es_ES-1010536';
DELETE FROM AD_Ref_List WHERE ad_componentobjectuid = 'CORE-AD_Ref_List-1010536';
DELETE FROM AD_Ref_List_Trl WHERE ad_componentobjectuid = 'CORE-AD_Ref_List_Trl-es_PY-1010535';
DELETE FROM AD_Ref_List_Trl WHERE ad_componentobjectuid = 'CORE-AD_Ref_List_Trl-es_MX-1010535';
DELETE FROM AD_Ref_List_Trl WHERE ad_componentobjectuid = 'CORE-AD_Ref_List_Trl-es_AR-1010535';
DELETE FROM AD_Ref_List_Trl WHERE ad_componentobjectuid = 'CORE-AD_Ref_List_Trl-es_ES-1010535';
DELETE FROM AD_Ref_List WHERE ad_componentobjectuid = 'CORE-AD_Ref_List-1010535';
DELETE FROM AD_Reference_Trl WHERE ad_componentobjectuid = 'CORE-AD_Reference_Trl-es_PY-1010196';
DELETE FROM AD_Reference_Trl WHERE ad_componentobjectuid = 'CORE-AD_Reference_Trl-es_MX-1010196';
DELETE FROM AD_Reference_Trl WHERE ad_componentobjectuid = 'CORE-AD_Reference_Trl-es_AR-1010196';
DELETE FROM AD_Reference_Trl WHERE ad_componentobjectuid = 'CORE-AD_Reference_Trl-es_ES-1010196';
DELETE FROM AD_Reference WHERE ad_componentobjectuid = 'CORE-AD_Reference-1010196';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016705-es_PY';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016705-es_MX';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016705-es_AR';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016705-es_ES';
DELETE FROM AD_Field WHERE ad_componentobjectuid = 'CORE-AD_Field-1016705';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016704-es_PY';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016704-es_MX';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016704-es_AR';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016704-es_ES';
DELETE FROM AD_Field WHERE ad_componentobjectuid = 'CORE-AD_Field-1016704';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016703-es_PY';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016703-es_MX';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016703-es_AR';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016703-es_ES';
DELETE FROM AD_Field WHERE ad_componentobjectuid = 'CORE-AD_Field-1016703';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016702-es_PY';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016702-es_MX';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016702-es_AR';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016702-es_ES';
DELETE FROM AD_Field WHERE ad_componentobjectuid = 'CORE-AD_Field-1016702';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016701-es_PY';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016701-es_MX';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016701-es_AR';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016701-es_ES';
DELETE FROM AD_Field WHERE ad_componentobjectuid = 'CORE-AD_Field-1016701';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016700-es_PY';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016700-es_MX';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016700-es_AR';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016700-es_ES';
DELETE FROM AD_Field WHERE ad_componentobjectuid = 'CORE-AD_Field-1016700';
DELETE FROM AD_Window_Access WHERE ad_componentobjectuid = 'CORE-AD_Window_Access-1010077-279';
DELETE FROM AD_TreeNodeMM WHERE ad_componentobjectuid = 'CORE-AD_TreeNodeMM-1010116-382';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015669-es_PY';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015669-es_MX';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015669-es_AR';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015669-es_ES';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015669';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015668-es_PY';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015668-es_MX';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015668-es_AR';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015668-es_ES';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015668';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectuid = 'CORE-AD_Element_Trl-1011187-es_PY';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectuid = 'CORE-AD_Element_Trl-1011187-es_MX';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectuid = 'CORE-AD_Element_Trl-1011187-es_AR';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectuid = 'CORE-AD_Element_Trl-1011187-es_ES';
DELETE FROM AD_Element WHERE ad_componentobjectuid = 'CORE-AD_Element-1011187';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015667-es_PY';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015667-es_MX';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015667-es_AR';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015667-es_ES';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015667';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015666-es_PY';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015666-es_MX';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015666-es_AR';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015666-es_ES';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015666';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015665-es_PY';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015665-es_MX';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015665-es_AR';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015665-es_ES';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015665';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015664-es_PY';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015664-es_MX';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015664-es_AR';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015664-es_ES';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015664';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_PY-1010871';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_MX-1010871';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_AR-1010871';
DELETE FROM AD_Message_Trl WHERE ad_componentobjectuid = 'CORE-AD_Message_Trl-es_ES-1010871';
DELETE FROM AD_Message WHERE ad_componentobjectuid = 'CORE-AD_Message-1010871';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016698-es_PY';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016698-es_MX';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016698-es_AR';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016698-es_ES';
DELETE FROM AD_Field WHERE ad_componentobjectuid = 'CORE-AD_Field-1016698';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016697-es_PY';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016697-es_MX';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016697-es_AR';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016697-es_ES';
DELETE FROM AD_Field WHERE ad_componentobjectuid = 'CORE-AD_Field-1016697';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015662-es_PY';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015662-es_MX';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015662-es_AR';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015662-es_ES';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015662';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015661-es_PY';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015661-es_MX';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015661-es_AR';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015661-es_ES';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015661';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectuid = 'CORE-AD_Element_Trl-1011185-es_PY';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectuid = 'CORE-AD_Element_Trl-1011185-es_MX';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectuid = 'CORE-AD_Element_Trl-1011185-es_AR';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectuid = 'CORE-AD_Element_Trl-1011185-es_ES';
DELETE FROM AD_Element WHERE ad_componentobjectuid = 'CORE-AD_Element-1011185';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016696-es_PY';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016696-es_MX';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016696-es_AR';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016696-es_ES';
DELETE FROM AD_Field WHERE ad_componentobjectuid = 'CORE-AD_Field-1016696';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016695-es_PY';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016695-es_MX';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016695-es_AR';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016695-es_ES';
DELETE FROM AD_Field WHERE ad_componentobjectuid = 'CORE-AD_Field-1016695';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016694-es_PY';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016694-es_MX';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016694-es_AR';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016694-es_ES';
DELETE FROM AD_Field WHERE ad_componentobjectuid = 'CORE-AD_Field-1016694';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016693-es_PY';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016693-es_MX';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016693-es_AR';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016693-es_ES';
DELETE FROM AD_Field WHERE ad_componentobjectuid = 'CORE-AD_Field-1016693';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016692-es_PY';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016692-es_MX';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016692-es_AR';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016692-es_ES';
DELETE FROM AD_Field WHERE ad_componentobjectuid = 'CORE-AD_Field-1016692';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016691-es_PY';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016691-es_MX';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016691-es_AR';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016691-es_ES';
DELETE FROM AD_Field WHERE ad_componentobjectuid = 'CORE-AD_Field-1016691';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016690-es_PY';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016690-es_MX';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016690-es_AR';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016690-es_ES';
DELETE FROM AD_Field WHERE ad_componentobjectuid = 'CORE-AD_Field-1016690';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016689-es_PY';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016689-es_MX';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016689-es_AR';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1016689-es_ES';
DELETE FROM AD_Field WHERE ad_componentobjectuid = 'CORE-AD_Field-1016689';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015660-es_PY';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015660-es_MX';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015660-es_AR';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015660-es_ES';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015660';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015659-es_PY';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015659-es_MX';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015659-es_AR';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015659-es_ES';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015659';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectuid = 'CORE-AD_Element_Trl-1011184-es_PY';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectuid = 'CORE-AD_Element_Trl-1011184-es_MX';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectuid = 'CORE-AD_Element_Trl-1011184-es_AR';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectuid = 'CORE-AD_Element_Trl-1011184-es_ES';
DELETE FROM AD_Element WHERE ad_componentobjectuid = 'CORE-AD_Element-1011184';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015658-es_PY';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015658-es_MX';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015658-es_AR';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015658-es_ES';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015658';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectuid = 'CORE-AD_Element_Trl-1011183-es_PY';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectuid = 'CORE-AD_Element_Trl-1011183-es_MX';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectuid = 'CORE-AD_Element_Trl-1011183-es_AR';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectuid = 'CORE-AD_Element_Trl-1011183-es_ES';
DELETE FROM AD_Element WHERE ad_componentobjectuid = 'CORE-AD_Element-1011183';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015657-es_PY';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015657-es_MX';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015657-es_AR';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015657-es_ES';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015657';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectuid = 'CORE-AD_Element_Trl-1011182-es_PY';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectuid = 'CORE-AD_Element_Trl-1011182-es_MX';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectuid = 'CORE-AD_Element_Trl-1011182-es_AR';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectuid = 'CORE-AD_Element_Trl-1011182-es_ES';
DELETE FROM AD_Element WHERE ad_componentobjectuid = 'CORE-AD_Element-1011182';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015656-es_PY';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015656-es_MX';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015656-es_AR';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015656-es_ES';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015656';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectuid = 'CORE-AD_Element_Trl-1011181-es_PY';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectuid = 'CORE-AD_Element_Trl-1011181-es_MX';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectuid = 'CORE-AD_Element_Trl-1011181-es_AR';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectuid = 'CORE-AD_Element_Trl-1011181-es_ES';
DELETE FROM AD_Element WHERE ad_componentobjectuid = 'CORE-AD_Element-1011181';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015655-es_PY';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015655-es_MX';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015655-es_AR';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015655-es_ES';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015655';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015654-es_PY';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015654-es_MX';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015654-es_AR';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015654-es_ES';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015654';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015653-es_PY';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015653-es_MX';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015653-es_AR';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1015653-es_ES';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1015653';

-- 20120824-1010 Modificación de la función getallocatedamt
CREATE OR REPLACE FUNCTION getallocatedamt(p_c_invoice_id integer, p_c_currency_id integer, p_c_conversiontype_id integer, p_multiplierap integer)
  RETURNS numeric AS
$BODY$
/*************************************************************************
 * PL/Java:getAllocatedAmt( int C_Invoice_ID,int C_Currency_ID,
 * int C_ConversionType_ID,BigDecimal MultiplierAP ):
 * Funcion "privada"; usar invovicePaid o invoiceOpen
 * Title:	Calculate Paid/Allocated amount in Currency
 * Desc: suma todas las lineas de alocacion asociadas a la factura
 * (pertenecientes a una cabecera de alocacion activa) de manera
 * directa (pago a la factura,C_AllocationLine.C_Invoice_ID) o 
 * indirecta (se uso la factura para pagar otra (C_AllocationLine.C_Invoice_Credit_ID).
 * Los montos de alocación son convertidos a p_c_currency_id, y la sumatoria
 * final es multiplicado por p_multiplierap (tipicamente, usar 1); no se hace un 
 * redondeo final por moneda.
 * En caso de alacacion directa el monto considerado es:
 * ar.Amount + ar.DisCountAmt + ar.WriteOffAmt
 * En caso de alocacion indirecta el monto considerado es:
 * ar.Amount
 * Siendo ar la linea de alocacion asociada 
 * Test:
    SELECT C_Invoice_ID, IsPaid, IsSOTrx, GrandTotal, 
    getAllocatedAmt (C_Invoice_ID, C_Currency_ID, C_conversionType_ID , 1)
    FROM C_Invoice;
 *	
 ************************************************************************/
DECLARE
	v_MultiplierAP		NUMERIC := 1;
	v_PaidAmt			NUMERIC := 0;
	v_ConversionType_ID INTEGER := p_c_conversionType_ID;
	v_Currency_ID       INTEGER := p_c_currency_id;
	v_Temp     NUMERIC;
	ar			RECORD;
	v_DateInvoiced timestamp without time zone;

BEGIN
	--	Default
	IF (p_MultiplierAP IS NOT NULL) THEN
		v_MultiplierAP := p_MultiplierAP::numeric;
	END IF;

	SELECT DateInvoiced
	       INTO v_DateInvoiced
	FROM C_Invoice 
	WHERE C_Invoice_ID = p_c_invoice_id;
	
	--	Calculate Allocated Amount
	-- INPUTS:
	-- p_C_Invoice_ID: el id de la factura, para econtrar la lineas de alocacion asociadas directamente o como pago (via C_Invoice_Credit_ID) ; 
	--v_Currency_ID :la moneda a convertir los montos de las alocaciones(que va  a ser el mismo que la factura)
	--v_ConversionType_ID: el tipo de conversión de moneda, puede ser null (tambien el mismo especificado en la factura)
 	--OUTPUTS:
	--v_PaidAmt : la cantidad alocada para la factura, convertida v_Currency_ID
	-- Basicamente : es la suma de los montos de las lineas asociadas a la factura  convertidos previamente
	-- a la moneda de la factura (hay un pequeño detalle para las entradas en las que la factura es usada como pago....)
	FOR ar IN 
		SELECT	a.AD_Client_ID, a.AD_Org_ID,
		al.Amount, al.DiscountAmt, al.WriteOffAmt,
		a.C_Currency_ID, a.DateTrx , al.C_Invoice_Credit_ID
		FROM	C_AllocationLine al
		INNER JOIN C_AllocationHdr a ON (al.C_AllocationHdr_ID=a.C_AllocationHdr_ID)
		WHERE	(al.C_Invoice_ID = p_C_Invoice_ID OR 
				al.C_Invoice_Credit_ID = p_C_Invoice_ID ) -- condicion no en Adempiere
          	AND   a.IsActive='Y'
	LOOP
	    -- Agregado, para facturas como pago
		IF (p_C_Invoice_ID = ar.C_Invoice_Credit_ID) THEN
		   v_Temp := ar.Amount;
		ELSE
		   v_Temp := ar.Amount + ar.DisCountAmt + ar.WriteOffAmt;
		END IF;
		-- Se asume que este v_Temp es no negativo
		v_PaidAmt := v_PaidAmt
        -- Allocation
			+ currencyConvert(v_Temp,
				ar.C_Currency_ID, v_Currency_ID, v_DateInvoiced, v_ConversionType_ID, 
				ar.AD_Client_ID, ar.AD_Org_ID);
      	--RAISE NOTICE ' C_Invoice_ID=% , PaidAmt=% , Allocation= % ',p_C_Invoice_ID, v_PaidAmt, v_Temp;
	END LOOP;
	RETURN	v_PaidAmt * v_MultiplierAP;
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION getallocatedamt(integer, integer, integer, integer) OWNER TO libertya;

-- 20120824-1010 Modificación de la función currencyrate
CREATE OR REPLACE FUNCTION currencyrate(p_curfrom_id integer, p_curto_id integer, p_convdate timestamp with time zone, p_conversiontype_id integer, p_client_id integer, p_org_id integer)
  RETURNS numeric AS
$BODY$
	
DECLARE
	--	Currency From variables
	cf_IsEuro		CHAR(1);
	cf_IsEMUMember		CHAR(1);
	cf_EMUEntryDate		timestamp with time zone;
	cf_EMURate		NUMERIC;
	--	Currency To variables
	ct_IsEuro		CHAR(1);
	ct_IsEMUMember		CHAR(1);
	ct_EMUEntryDate	DATE;
	ct_EMURate		NUMERIC;
	--	Triangle
	v_CurrencyFrom		INTEGER;
	v_CurrencyTo		INTEGER;
	v_CurrencyEuro		INTEGER;
	--
	v_ConvDate		timestamp with time zone := now();
	v_ConversionType_ID	INTEGER := 0;
	v_Rate			NUMERIC;
	c			RECORD;			

BEGIN
--	No Conversion
	IF (p_CurFrom_ID = p_CurTo_ID) THEN
		RETURN 1;
	END IF;
	--	Default Date Parameter
	IF (p_ConvDate IS NOT NULL) THEN
		v_ConvDate := p_ConvDate;   --  SysDate
	END IF;
    --  Default Conversion Type
	IF (p_ConversionType_ID IS NULL OR p_ConversionType_ID = 0) THEN
		BEGIN
		    SELECT C_ConversionType_ID 
		      INTO STRICT v_ConversionType_ID
		    FROM C_ConversionType 
		    WHERE IsDefault='Y'
		      AND AD_Client_ID IN (0,p_Client_ID)
		    ORDER BY AD_Client_ID DESC
		    LIMIT 1;
		EXCEPTION WHEN OTHERS THEN
		    RAISE NOTICE 'Conversion Type Not Found';
		END;
    ELSE
        	v_ConversionType_ID := p_ConversionType_ID;
	END IF;

	--	Get Currency Info
	SELECT	MAX(IsEuro), MAX(IsEMUMember), MAX(EMUEntryDate), MAX(EMURate)
	  INTO	cf_IsEuro, cf_IsEMUMember, cf_EMUEntryDate, cf_EMURate
	FROM		C_Currency
	  WHERE	C_Currency_ID = p_CurFrom_ID;
	-- Not Found
	IF (cf_IsEuro IS NULL) THEN
		RAISE NOTICE 'From Currency Not Found';
		RETURN NULL;
	END IF;
	SELECT	MAX(IsEuro), MAX(IsEMUMember), MAX(EMUEntryDate), MAX(EMURate)
	  INTO	ct_IsEuro, ct_IsEMUMember, ct_EMUEntryDate, ct_EMURate
	FROM		C_Currency
	  WHERE	C_Currency_ID = p_CurTo_ID;
	-- Not Found
	IF (ct_IsEuro IS NULL) THEN
		RAISE NOTICE 'To Currency Not Found';
		RETURN NULL;
	END IF;

	--	Fixed - From Euro to EMU
	IF (cf_IsEuro = 'Y' AND ct_IsEMUMember ='Y' AND v_ConvDate >= ct_EMUEntryDate) THEN
		RETURN ct_EMURate;
	END IF;

	--	Fixed - From EMU to Euro
	IF (ct_IsEuro = 'Y' AND cf_IsEMUMember ='Y' AND v_ConvDate >= cf_EMUEntryDate) THEN
		RETURN 1 / cf_EMURate;
	END IF;

	--	Fixed - From EMU to EMU
	IF (cf_IsEMUMember = 'Y' AND cf_IsEMUMember ='Y'
			AND v_ConvDate >= cf_EMUEntryDate AND v_ConvDate >= ct_EMUEntryDate) THEN
		RETURN ct_EMURate / cf_EMURate;
	END IF;

	--	Flexible Rates
	v_CurrencyFrom := p_CurFrom_ID;
	v_CurrencyTo := p_CurTo_ID;

	-- if EMU Member involved, replace From/To Currency
	IF ((cf_isEMUMember = 'Y' AND v_ConvDate >= cf_EMUEntryDate)
	  OR (ct_isEMUMember = 'Y' AND v_ConvDate >= ct_EMUEntryDate)) THEN
		SELECT	MAX(C_Currency_ID)
		  INTO	v_CurrencyEuro
		FROM		C_Currency
		WHERE	IsEuro = 'Y';
		-- Conversion Rate not Found
		IF (v_CurrencyEuro IS NULL) THEN
			RAISE NOTICE 'Euro Not Found';
			RETURN NULL;
		END IF;
		IF (cf_isEMUMember = 'Y' AND v_ConvDate >= cf_EMUEntryDate) THEN
			v_CurrencyFrom := v_CurrencyEuro;
		ELSE
			v_CurrencyTo := v_CurrencyEuro;
		END IF;
	END IF;

	--	Get Rate

	BEGIN		
		FOR c IN SELECT	MultiplyRate
			FROM	C_Conversion_Rate
			WHERE	C_Currency_ID=v_CurrencyFrom AND C_Currency_ID_To=v_CurrencyTo
			  AND	C_ConversionType_ID=v_ConversionType_ID
			  AND	TRUNC(v_ConvDate) BETWEEN TRUNC(ValidFrom) AND TRUNC(ValidTo)
			  AND	AD_Client_ID IN (0,p_Client_ID) AND AD_Org_ID IN (0,p_Org_ID)
			ORDER BY AD_Client_ID DESC, AD_Org_ID DESC, ValidFrom DESC
			LIMIT 1  -- Ader: mejora de performance
		LOOP
			v_Rate := c.MultiplyRate;
			EXIT;	--	only first
		END LOOP;
		-- v_Rate IS NULL : se busca la conversion inversa invirtiendo to-from  y diviendo a 1 por el resultado
		IF (v_Rate IS NULL) THEN
			FOR c IN SELECT	MultiplyRate
				FROM	C_Conversion_Rate
				WHERE	C_Currency_ID=v_CurrencyTo AND C_Currency_ID_To=v_CurrencyFrom
				  AND	C_ConversionType_ID=v_ConversionType_ID
				  AND	TRUNC(v_ConvDate) BETWEEN TRUNC(ValidFrom) AND TRUNC(ValidTo)
				  AND	AD_Client_ID IN (0,p_Client_ID) AND AD_Org_ID IN (0,p_Org_ID)
				ORDER BY AD_Client_ID DESC, AD_Org_ID DESC, ValidFrom DESC
				LIMIT 1  -- Ader: mejora de performance
			LOOP
				v_Rate := 1::numeric/c.MultiplyRate; -- Null en divisor genera Null
				EXIT;	--	only first
			END LOOP;
		END IF;			
	END;
	--	Not found
	IF (v_Rate IS NULL) THEN
		RAISE NOTICE 'Conversion Rate Not Found';
		RETURN NULL;
	END IF;

	--	Currency From was EMU
	IF (cf_isEMUMember = 'Y' AND v_ConvDate >= cf_EMUEntryDate) THEN
		RETURN v_Rate / cf_EMURate;
	END IF;

	--	Currency To was EMU
	IF (ct_isEMUMember = 'Y' AND v_ConvDate >= ct_EMUEntryDate) THEN
		RETURN v_Rate * ct_EMURate;
	END IF;

	RETURN v_Rate;

EXCEPTION WHEN OTHERS THEN
	RAISE NOTICE '%', SQLERRM;
	RETURN NULL;

	
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION currencyrate(integer, integer, timestamp with time zone, integer, integer, integer) OWNER TO libertya;

-- 20120824-1015 Modificación de la función paymentavailable
CREATE OR REPLACE FUNCTION paymentavailable(p_c_payment_id integer)
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
 * Title:	Calculate Available Payment Amount in Payment Currency
 * Description:
 *		similar to C_Invoice_Open
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
 *- TODO: posiblemente refactorizar el calculo de alocacion a otra funcion
 *- no se deja el ingoreRounding de Adempiere (tal vez deberia ir...) y
 * se llama a currencyRound como en libertya (no deberia ser necesario; ya que
 * todos los montos intermedios son redondeados a la misma moneda; todos menos
 * PayAmt que se podria suponer ya esta redondeado de antemano) 
 ************************************************************************/
DECLARE
	v_Currency_ID		INTEGER;
	v_AvailableAmt		NUMERIC := 0;
   	v_IsReceipt         CHARACTER(1);
   	v_Amt               NUMERIC := 0;
   	r   			RECORD;
	v_Charge_ID INTEGER; -- no en Adempiere
	v_ConversionType_ID INTEGER; -- no en Adempiere; puede ser null (igual es dudoso este tipo de conversion, por que lo 
	--que se conveirte al moneda del pago son las lineas de alocacion, las cuales en otros calculos usan posiblemente otro tipo de conversion)
	v_DateTrx timestamp without time zone;

BEGIN

	BEGIN
	--	Get Currency, PayAmt,IsReceipt (not used, only for debug) and C_Charge_ID
	SELECT	C_Currency_ID, PayAmt, IsReceipt, 
			C_Charge_ID,C_ConversionType_ID, DateTrx
	  INTO	STRICT 
			v_Currency_ID, v_AvailableAmt, v_IsReceipt,
			v_Charge_ID,v_ConversionType_ID, v_DateTrx
	FROM	C_Payment     --NO CORREGIDO por AP/AR
	WHERE	C_Payment_ID = p_C_Payment_ID;
		EXCEPTION	--	No encontrado; posiblememte mal llamado
		WHEN OTHERS THEN
            	RAISE NOTICE 'PaymentAvailable - %', SQLERRM;
			RETURN NULL;
	END;
	
	IF (v_Charge_ID > 0 ) THEN -- mayor que cero, por lo tanto no null 
	   RETURN 0;
	END IF;
--  DBMS_OUTPUT.PUT_LINE('== C_Payment_ID=' || p_C_Payment_ID || ', PayAmt=' || v_AvailableAmt || ', Receipt=' || v_IsReceipt);

	--	Calculate Allocated Amount
	FOR r IN
		SELECT	a.AD_Client_ID, a.AD_Org_ID, al.Amount, a.C_Currency_ID, a.DateTrx
		FROM	C_AllocationLine al
	        INNER JOIN C_AllocationHdr a ON (al.C_AllocationHdr_ID=a.C_AllocationHdr_ID)
		WHERE	al.C_Payment_ID = p_C_Payment_ID
          	AND   a.IsActive='Y'
	LOOP
        v_Amt := currencyConvert(r.Amount, r.C_Currency_ID, v_Currency_ID, 
				v_DateTrx, v_ConversionType_ID, r.AD_Client_ID, r.AD_Org_ID);
	    v_AvailableAmt := v_AvailableAmt - v_Amt;
--      DBMS_OUTPUT.PUT_LINE('  Allocation=' || a.Amount || ' - Available=' || v_AvailableAmt);
	END LOOP;
	
	IF (v_AvailableAmt < 0) THEN
		RAISE NOTICE 'Payment Available negative, correcting to zero - %',v_AvailableAmt ;
		v_AvailableAmt := 0;
	END IF;
	
	-- siguiente NO en Libertya
	--	Ignore Rounding; 
	--IF (v_AvailableAmt BETWEEN -0.00999 AND 0.00999) THEN
	--	v_AvailableAmt := 0;
	--END IF;
	--	Round to penny
	--v_AvailableAmt := ROUND(COALESCE(v_AvailableAmt,0), 2);
	
	-- redondeo de moneda
	v_AvailableAmt :=  currencyRound(v_AvailableAmt,v_Currency_ID,NULL);
	RETURN	v_AvailableAmt;
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION paymentavailable(integer) OWNER TO libertya;

-- 20120824-1015 Modificación de la función cashlineavailable
CREATE OR REPLACE FUNCTION cashlineavailable(p_c_cashline_id integer)
  RETURNS numeric AS
$BODY$
/*************************************************************************
-Retorna NULL si parametro es null o si la linea no existe
-Retorna la cantidad disponible de la linea para alocacion futuras usando el mismo signo 
 que la linea, esto es, si C_CashLine.Amt <0 , se retorna 0 o un numero
 negativo; si C_CashLine.amt >0 , se retrona cero o un numero positivo.
-la cantidad disponible inicial de una linea de caja es C_CashLine.Amt
 (esto es, no se tiene en cuenta ni C_CashLine.DiscountAmt ni 
 C_CashLine.WriteoffAmt) 
-asume que las alocaciones son no negativas y solo se consideran aquellas
 lineas de alocacion que pertenecen a una cabecera de alocacion (C_AllocationHdr)
 activa (esta es la unica condicion que se aplica)
- se considera como monto de alocacion con respecto a la linea de caja 
  a C_AllocationLine.Amount (esto es, no se tiene en cuenta C_AllocationLine.WriteOff ni
  C_AllocationLine.Discount)
  
TEST: 
-- montos de lienas, monto disponible, y alocaciones relacionadas cada una de las lineas de caja
-- Availabe DEBE ser cero o tener el mismo signo que Amount,
-- si se usa una sola moneda, entonces 
-- (suma de AmountAllocatedInAlocLine en AH activas) + ABS(Available) debe ser iugal a  ABS(Amoumt) 
select cl.c_cashLine_id,cl.amount, 
cashLineAvailable(cl.c_cashLine_id) as Available
,al.c_allocationLine_id ,
al.amount as AmountAllocatedInAlocLine,
cl.c_currency_id as currencyCashLine,
ah.c_currency_id as currencyAlloc,
ah.isActive as AHActive
from 
c_cashLine cl left join c_allocationLine al on
  (al.c_cashLine_id = cl.c_cashLine_id)
left join 
C_AllocationHDR ah on (ah.C_allocationHdr_id = al.C_allocationHdr_id)

order by cl.c_cashLine_id;
  
************************************************************************/
DECLARE
	v_Currency_ID		INTEGER;
	v_Amt               NUMERIC;
   	r   			RECORD;
	v_ConversionType_ID INTEGER := 0; -- actuamente, tal como en PL/java se usa siempre 0, no se toma desde cashLine
	v_allocation NUMERIC;
	v_allocatedAmt NUMERIC;	-- candida alocada total convertida a la moneda de la linea 
	v_AvailableAmt		NUMERIC := 0;
	v_StatementDate timestamp without time zone;
 
BEGIN
	IF (p_C_Cashline_id IS NULL OR p_C_Cashline_id = 0) THEN
		RETURN NULL;
	END IF;
	
	--	Get Currency and Amount
	SELECT	C_Currency_ID, Amount
		INTO v_Currency_ID, v_Amt
	FROM	C_CashLine    
	WHERE	C_CashLine_ID  = p_C_Cashline_id;

	SELECT StatementDate
	       INTO v_StatementDate
	FROM C_Cash c 
	INNER JOIN C_CashLine cl ON c.C_Cash_ID = cl.C_Cash_ID 
	WHERE C_CashLine_ID = p_C_Cashline_id;
	
	IF NOT FOUND THEN
	  RETURN NULL;
	END IF;
	
	-- Calculate Allocated Amount
	-- input: p_C_Cashline_id,v_Currency_ID,v_ConversionType_ID
	--output: v_allocatedAmt
	v_allocatedAmt := 0.00;
	FOR r IN
		SELECT	a.AD_Client_ID, a.AD_Org_ID, al.Amount, a.C_Currency_ID, a.DateTrx
		FROM	C_AllocationLine al
	        INNER JOIN C_AllocationHdr a ON (al.C_AllocationHdr_ID=a.C_AllocationHdr_ID)
		WHERE	al.C_CashLine_ID = p_C_Cashline_id
          	AND   a.IsActive='Y'
	LOOP
        v_allocation := currencyConvert(r.Amount, r.C_Currency_ID, v_Currency_ID, 
				v_StatementDate, v_ConversionType_ID, r.AD_Client_ID, r.AD_Org_ID);
	    v_allocatedAmt := v_allocatedAmt + v_allocation;
	END LOOP;

	-- esto supone que las alocaciones son siempre no negativas; si esto no pasa, se van a retornar valores que no van a tener sentido
	v_AvailableAmt := ABS(v_Amt) - v_allocatedAmt;
	-- v_AvailableAmt aca DEBE ser NO Negativo si admeas, las suma de las alocaciones nunca superan el monto de la linea
	-- de cualquiera manera, por "seguridad", si el valor es negativo, se corrige a cero
    IF (v_AvailableAmt < 0) THEN
		RAISE NOTICE 'CashLine Available negative, correcting to zero - %',v_AvailableAmt ;
		v_AvailableAmt := 0.00;
    END IF;	
	--  el resultado debe ser 0 o de lo contrario tener el mismo signo que la linea; 
	IF (v_Amt < 0) THEN
		v_AvailableAmt := v_AvailableAmt * -1::numeric;
	END IF; 
	-- redondeo de moneda
	v_AvailableAmt :=  currencyRound(v_AvailableAmt,v_Currency_ID,NULL);
	RETURN	v_AvailableAmt;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION cashlineavailable(integer) OWNER TO libertya;

-- 20120824-1940 Incorporación de registro de caja diaria en línea de caja
update ad_system set dummy = (SELECT addcolumnifnotexists('c_cashline','c_posjournal_id', 'integer'));

-- 20120824-1940 Actualización de todas las cajas diarias de las líneas con la caja diaria del libro de caja a la cual pertenece
UPDATE c_cashline cl SET c_posjournal_id = (SELECT c_posjournal_id FROM c_cash c WHERE c.c_cash_id = cl.c_cash_id);

-- 20120824-2029 Mejora a la vista para obtener los cashlines y payments sin allocation
CREATE OR REPLACE VIEW c_posjournalpayments_v AS 
( SELECT al.c_allocationhdr_id, al.c_allocationline_id, al.ad_client_id, al.ad_org_id, al.isactive, al.created, al.createdby, al.updated, al.updatedby, al.c_invoice_id, al.c_payment_id, al.c_cashline_id, al.c_invoice_credit_id, 
        CASE
            WHEN al.c_payment_id IS NOT NULL THEN p.tendertype::character varying
            WHEN al.c_cashline_id IS NOT NULL THEN 'CA'::character varying
            WHEN al.c_invoice_credit_id IS NOT NULL THEN 'CR'::character varying
            ELSE NULL::character varying
        END::character varying(2) AS tendertype, 
        CASE
            WHEN al.c_payment_id IS NOT NULL THEN p.documentno
            WHEN al.c_invoice_credit_id IS NOT NULL THEN ic.documentno
            ELSE NULL::character varying
        END::character varying(30) AS documentno, 
        CASE
            WHEN al.c_payment_id IS NOT NULL THEN p.description
            WHEN al.c_cashline_id IS NOT NULL THEN cl.description
            WHEN al.c_invoice_credit_id IS NOT NULL THEN ic.description
            ELSE NULL::character varying
        END::character varying(255) AS description, 
        CASE
            WHEN al.c_payment_id IS NOT NULL THEN ((p.documentno::text || '_'::text) || to_char(p.datetrx, 'DD/MM/YYYY'::text))::character varying
            WHEN al.c_cashline_id IS NOT NULL THEN ((c.name::text || '_#'::text) || cl.line::text)::character varying
            WHEN al.c_invoice_credit_id IS NOT NULL THEN ic.documentno
            ELSE NULL::character varying
        END::character varying(255) AS info, COALESCE(currencyconvert(al.amount + al.discountamt + al.writeoffamt, ah.c_currency_id, i.c_currency_id, NULL::timestamp with time zone, NULL::integer, ah.ad_client_id, ah.ad_org_id), 0::numeric)::numeric(20,2) AS amount, cl.c_cash_id, cl.line, ic.c_doctype_id, p.checkno, p.a_bank, p.checkno AS transferno, p.creditcardtype, p.m_entidadfinancieraplan_id, ep.m_entidadfinanciera_id, p.couponnumber, date_trunc('day'::text, ah.datetrx) AS allocationdate, 
        CASE
            WHEN al.c_payment_id IS NOT NULL THEN p.docstatus
            WHEN al.c_cashline_id IS NOT NULL THEN cl.docstatus
            WHEN al.c_invoice_credit_id IS NOT NULL THEN ic.docstatus
            ELSE NULL::character(2)
        END AS docstatus
   FROM c_allocationline al
   LEFT JOIN c_allocationhdr ah ON al.c_allocationhdr_id = ah.c_allocationhdr_id
   LEFT JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
   LEFT JOIN c_payment p ON al.c_payment_id = p.c_payment_id
   LEFT JOIN m_entidadfinancieraplan ep ON p.m_entidadfinancieraplan_id = ep.m_entidadfinancieraplan_id
   LEFT JOIN c_cashline cl ON al.c_cashline_id = cl.c_cashline_id
   LEFT JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
   LEFT JOIN c_invoice ic ON al.c_invoice_credit_id = ic.c_invoice_id
  ORDER BY 
CASE
    WHEN al.c_payment_id IS NOT NULL THEN p.tendertype::character varying
    WHEN al.c_cashline_id IS NOT NULL THEN 'CA'::character varying
    WHEN al.c_invoice_credit_id IS NOT NULL THEN 'CR'::character varying
    ELSE NULL::character varying
END::character varying(2), 
CASE
    WHEN al.c_payment_id IS NOT NULL THEN p.documentno
    WHEN al.c_invoice_credit_id IS NOT NULL THEN ic.documentno
    ELSE NULL::character varying
END::character varying(30))
UNION ALL
-- Cashlines sin allocation
(SELECT null as c_allocationhdr_id, null as c_allocationline_id, cl.ad_client_id, cl.ad_org_id, cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby, null as c_invoice_id, null as c_payment_id, cl.c_cashline_id, null as c_invoice_credit_id, 
        'CA'::character varying(2) as tendertype, 
        NULL::character varying(30) AS documentno, 
        cl.description, 
        ((c.name::text || '_#'::text) || cl.line::text)::character varying(255) AS info, cl.amount, cl.c_cash_id, cl.line, null as c_doctype_id, null::character varying(20) as checkno, null::character varying(255) as a_bank, null::character varying(20) AS transferno, null::character(1) as creditcardtype, null as m_entidadfinancieraplan_id, null as m_entidadfinanciera_id, null::character varying(30) as couponnumber, date_trunc('day'::text, c.statementdate) AS allocationdate, 
        cl.docstatus
FROM c_cashline as cl
INNER JOIN c_cash as c ON c.c_cash_id = cl.c_cash_id
WHERE NOT EXISTS (SELECT c_allocationline_id FROM c_allocationline as al WHERE al.c_cashline_id = cl.c_cashline_id))
UNION ALL
( SELECT null as c_allocationhdr_id, null as c_allocationline_id, p.ad_client_id, p.ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, null as c_invoice_id, p.c_payment_id, null as c_cashline_id, null as c_invoice_credit_id, 
        p.tendertype::character varying(2), 
        p.documentno, 
        p.description, 
        ((p.documentno::text || '_'::text) || to_char(p.datetrx, 'DD/MM/YYYY'::text))::character varying(255) AS info, p.payamt, null as c_cash_id, null::numeric(18,0) as line, null as c_doctype_id, p.checkno, p.a_bank, p.checkno AS transferno, p.creditcardtype, p.m_entidadfinancieraplan_id, ep.m_entidadfinanciera_id, p.couponnumber, date_trunc('day'::text, p.datetrx) AS allocationdate, 
        p.docstatus
   FROM c_payment p 
   LEFT JOIN m_entidadfinancieraplan ep ON p.m_entidadfinancieraplan_id = ep.m_entidadfinancieraplan_id
  ORDER BY p.tendertype::character varying(2), p.documentno::character varying(30));

ALTER TABLE c_posjournalpayments_v OWNER TO libertya;

-- 20120824-2029 Mejora a la vista colocando absoluto a los egresos y filtrando los créditos solamente en estado CL y CO, además tomando la caja diaria del comprobante en lugar del allocation
CREATE OR REPLACE VIEW c_pos_declaracionvalores_v AS 
        (        (         SELECT i.ad_client_id, i.ad_org_id, i.c_posjournal_id, i.ad_user_id, i.c_currency_id, i.dateinvoiced AS datetrx, i.docstatus, NULL::unknown AS category, dt.docbasetype AS tendertype, (i.documentno::text || ' '::text) || COALESCE(i.description, ''::character varying)::text AS description, i.c_charge_id, i.chargename, i.c_invoice_id AS doc_id, 
                                CASE dt.signo_issotrx
                                    WHEN 1 THEN i.total - i.open
                                    WHEN (-1) THEN 0::numeric
                                    ELSE NULL::numeric
                                END::numeric(22,2) AS ingreso, 
                                CASE dt.signo_issotrx
                                    WHEN 1 THEN 0::numeric
                                    WHEN (-1) THEN abs(i.total - i.open)
                                    ELSE NULL::numeric
                                END::numeric(22,2) AS egreso
                           FROM ( SELECT DISTINCT i.ad_client_id, i.ad_org_id, i.documentno, inv.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, i.dateinvoiced::date AS dateinvoiced, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name AS chargename, currencybase(i.grandtotal, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2) AS total, currencybase(inv.initialcurrentaccountamt, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2) AS open
                                   FROM c_posjournalinvoices_v i
                              JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
                         JOIN c_invoice inv ON i.c_invoice_id = inv.c_invoice_id
                    LEFT JOIN c_charge ch ON ch.c_charge_id = inv.c_charge_id
                   ORDER BY i.ad_client_id, i.ad_org_id, i.documentno, inv.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, i.dateinvoiced::date, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name, currencybase(i.grandtotal, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2), currencybase(inv.initialcurrentaccountamt, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2)) i
                      JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id
                UNION ALL 
                         SELECT p.ad_client_id, p.ad_org_id, p.c_posjournal_id, p.ad_user_id, p.c_currency_id, p.datetrx, p.docstatus, NULL::unknown AS category, p.tendertype, (p.documentno::text || ' '::text) || COALESCE(p.description, ''::character varying)::text AS description, p.c_charge_id, p.chargename, p.c_payment_id AS doc_id, 
                                CASE p.isreceipt
                                    WHEN 'Y'::bpchar THEN p.total
                                    ELSE 0::numeric
                                END::numeric(22,2) AS ingreso, 
                                CASE p.isreceipt
                                    WHEN 'N'::bpchar THEN abs(p.total)
                                    ELSE 0::numeric
                                END::numeric(22,2) AS egreso
                           FROM ( SELECT p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date AS datetrx, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, p.c_currency_id, p.datetrx::timestamp with time zone, p.ad_client_id, p.ad_org_id)::numeric(22,2))::numeric(22,2) AS total
                                   FROM c_payment p
                              JOIN c_posjournalpayments_v pjp ON pjp.c_payment_id = p.c_payment_id
                         JOIN c_posjournal pj ON pj.c_posjournal_id = p.c_posjournal_id
                    LEFT JOIN c_charge ch ON ch.c_charge_id = p.c_charge_id
                   GROUP BY p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name) p)
        UNION ALL 
                 SELECT c.ad_client_id, c.ad_org_id, c.c_posjournal_id, c.ad_user_id, c.c_currency_id, c.datetrx, c.docstatus, c.cashtype AS category, c.tendertype, 
                        CASE
                            WHEN length(c.description::text) > 0 THEN c.description
                            ELSE c.info
                        END AS description, c.c_charge_id, c.chargename, c.c_cashline_id AS doc_id, 
                        CASE sign(c.total)
                            WHEN (-1) THEN 0::numeric
                            ELSE c.total
                        END::numeric(22,2) AS ingreso, 
                        CASE sign(c.total)
                            WHEN (-1) THEN abs(c.total)
                            ELSE 0::numeric
                        END::numeric(22,2) AS egreso
                   FROM ( SELECT cl.ad_client_id, cl.ad_org_id, cl.c_cashline_id, c.c_posjournal_id, pj.ad_user_id, cl.c_currency_id, c.statementdate::date AS datetrx, cl.docstatus, cl.description, pjp.info, pjp.tendertype, cl.cashtype, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, cl.c_currency_id, c.statementdate::timestamp with time zone, cl.ad_client_id, cl.ad_org_id)::numeric(22,2))::numeric(22,2) AS total
                           FROM c_cashline cl
                      JOIN c_cash c ON c.c_cash_id = cl.c_cash_id
                 JOIN c_posjournalpayments_v pjp ON pjp.c_cashline_id = cl.c_cashline_id
            JOIN c_posjournal pj ON pj.c_posjournal_id = c.c_posjournal_id
       LEFT JOIN c_charge ch ON ch.c_charge_id = cl.c_charge_id
      GROUP BY cl.ad_client_id, cl.ad_org_id, cl.c_cashline_id, c.c_posjournal_id, pj.ad_user_id, cl.c_currency_id, c.statementdate::date, cl.docstatus, cl.description, pjp.info, pjp.tendertype, cl.cashtype, ch.c_charge_id, ch.name) c)
UNION ALL 
         SELECT i.ad_client_id, i.ad_org_id, i.c_posjournal_id, i.ad_user_id, i.c_currency_id, i.dateinvoiced AS datetrx, i.docstatus, NULL::unknown AS category, i.tendertype, (i.documentno::text || ' '::text) || COALESCE(i.description, ''::character varying)::text AS description, i.c_charge_id, i.chargename, i.c_invoice_id AS doc_id, i.total AS ingreso, 0 AS egreso
           FROM ( SELECT i.ad_client_id, i.ad_org_id, i.documentno, i.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, pjp.tendertype, i.dateinvoiced::date AS dateinvoiced, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2))::numeric(22,2) AS total
                   FROM c_invoice i
              JOIN c_posjournalpayments_v pjp ON pjp.c_invoice_credit_id = i.c_invoice_id
         JOIN c_allocationhdr ah ON ah.c_allocationhdr_id = pjp.c_allocationhdr_id
    JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   LEFT JOIN c_charge ch ON ch.c_charge_id = i.c_charge_id
  GROUP BY i.ad_client_id, i.ad_org_id, i.documentno, i.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, pjp.tendertype, i.dateinvoiced::date, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name) i
        JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id
        WHERE i.docstatus IN ('CL','CO');

ALTER TABLE c_pos_declaracionvalores_v OWNER TO libertya;

-- 20120828-1040 Incorporación de nuevas columnas en la tabla T_CUENTACORRIENTE
ALTER TABLE T_CUENTACORRIENTE ADD COLUMN iso_code character(3);
ALTER TABLE T_CUENTACORRIENTE ADD COLUMN amount numeric(10,2) DEFAULT 0;

-- 20120828-1240 Incorporación al perfil de check que permite ser supervisor de cajas diarias
update ad_system set dummy = (SELECT addcolumnifnotexists('ad_role','posjournalsupervisor', 'character(1) NOT NULL DEFAULT ''Y''::bpchar'));

-- 20120831-1710 Modificación de la función cashlineavailable
CREATE OR REPLACE FUNCTION cashlineavailable(p_c_cashline_id integer)
  RETURNS numeric AS
$BODY$
DECLARE
	v_Currency_ID		INTEGER;
	v_Amt               NUMERIC;
   	r   			RECORD;
	v_ConversionType_ID INTEGER := 0; -- actuamente, tal como en PL/java se usa siempre 0, no se toma desde cashLine
	v_allocation NUMERIC;
	v_allocatedAmt NUMERIC;	-- candida alocada total convertida a la moneda de la linea 
	v_AvailableAmt		NUMERIC := 0;
	v_DateAcct timestamp without time zone;
 
BEGIN
	IF (p_C_Cashline_id IS NULL OR p_C_Cashline_id = 0) THEN
		RETURN NULL;
	END IF;
	
	--	Get Currency and Amount
	SELECT	C_Currency_ID, Amount
		INTO v_Currency_ID, v_Amt
	FROM	C_CashLine    
	WHERE	C_CashLine_ID  = p_C_Cashline_id;

	SELECT DateAcct
	       INTO v_DateAcct
	FROM C_Cash c 
	INNER JOIN C_CashLine cl ON c.C_Cash_ID = cl.C_Cash_ID 
	WHERE C_CashLine_ID = p_C_Cashline_id;
	
	IF NOT FOUND THEN
	  RETURN NULL;
	END IF;
	
	-- Calculate Allocated Amount
	-- input: p_C_Cashline_id,v_Currency_ID,v_ConversionType_ID
	--output: v_allocatedAmt
	v_allocatedAmt := 0.00;
	FOR r IN
		SELECT	a.AD_Client_ID, a.AD_Org_ID, al.Amount, a.C_Currency_ID, a.DateTrx
		FROM	C_AllocationLine al
	        INNER JOIN C_AllocationHdr a ON (al.C_AllocationHdr_ID=a.C_AllocationHdr_ID)
		WHERE	al.C_CashLine_ID = p_C_Cashline_id
          	AND   a.IsActive='Y'
	LOOP
        v_allocation := currencyConvert(r.Amount, r.C_Currency_ID, v_Currency_ID, 
				v_DateAcct, v_ConversionType_ID, r.AD_Client_ID, r.AD_Org_ID);
	    v_allocatedAmt := v_allocatedAmt + v_allocation;
	END LOOP;

	-- esto supone que las alocaciones son siempre no negativas; si esto no pasa, se van a retornar valores que no van a tener sentido
	v_AvailableAmt := ABS(v_Amt) - v_allocatedAmt;
	-- v_AvailableAmt aca DEBE ser NO Negativo si admeas, las suma de las alocaciones nunca superan el monto de la linea
	-- de cualquiera manera, por "seguridad", si el valor es negativo, se corrige a cero
    IF (v_AvailableAmt < 0) THEN
		RAISE NOTICE 'CashLine Available negative, correcting to zero - %',v_AvailableAmt ;
		v_AvailableAmt := 0.00;
    END IF;	
	--  el resultado debe ser 0 o de lo contrario tener el mismo signo que la linea; 
	IF (v_Amt < 0) THEN
		v_AvailableAmt := v_AvailableAmt * -1::numeric;
	END IF; 
	-- redondeo de moneda
	v_AvailableAmt :=  currencyRound(v_AvailableAmt,v_Currency_ID,NULL);
	RETURN	v_AvailableAmt;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION cashlineavailable(integer) OWNER TO libertya;

-- 20120831-1710 Modificación de la función getallocatedamt
CREATE OR REPLACE FUNCTION getallocatedamt(p_c_invoice_id integer, p_c_currency_id integer, p_c_conversiontype_id integer, p_multiplierap integer)
  RETURNS numeric AS
$BODY$
DECLARE
	v_MultiplierAP		NUMERIC := 1;
	v_PaidAmt			NUMERIC := 0;
	v_ConversionType_ID INTEGER := p_c_conversionType_ID;
	v_Currency_ID       INTEGER := p_c_currency_id;
	v_Temp     NUMERIC;
	ar			RECORD;
	v_DateAcct timestamp without time zone;

BEGIN
	--	Default
	IF (p_MultiplierAP IS NOT NULL) THEN
		v_MultiplierAP := p_MultiplierAP::numeric;
	END IF;

	SELECT DateAcct
	       INTO v_DateAcct
	FROM C_Invoice 
	WHERE C_Invoice_ID = p_c_invoice_id;
	
	--	Calculate Allocated Amount
	-- INPUTS:
	-- p_C_Invoice_ID: el id de la factura, para econtrar la lineas de alocacion asociadas directamente o como pago (via C_Invoice_Credit_ID) ; 
	--v_Currency_ID :la moneda a convertir los montos de las alocaciones(que va  a ser el mismo que la factura)
	--v_ConversionType_ID: el tipo de conversión de moneda, puede ser null (tambien el mismo especificado en la factura)
 	--OUTPUTS:
	--v_PaidAmt : la cantidad alocada para la factura, convertida v_Currency_ID
	-- Basicamente : es la suma de los montos de las lineas asociadas a la factura  convertidos previamente
	-- a la moneda de la factura (hay un pequeño detalle para las entradas en las que la factura es usada como pago....)
	FOR ar IN 
		SELECT	a.AD_Client_ID, a.AD_Org_ID,
		al.Amount, al.DiscountAmt, al.WriteOffAmt,
		a.C_Currency_ID, a.DateTrx , al.C_Invoice_Credit_ID
		FROM	C_AllocationLine al
		INNER JOIN C_AllocationHdr a ON (al.C_AllocationHdr_ID=a.C_AllocationHdr_ID)
		WHERE	(al.C_Invoice_ID = p_C_Invoice_ID OR 
				al.C_Invoice_Credit_ID = p_C_Invoice_ID ) -- condicion no en Adempiere
          	AND   a.IsActive='Y'
	LOOP
	    -- Agregado, para facturas como pago
		IF (p_C_Invoice_ID = ar.C_Invoice_Credit_ID) THEN
		   v_Temp := ar.Amount;
		ELSE
		   v_Temp := ar.Amount + ar.DisCountAmt + ar.WriteOffAmt;
		END IF;
		-- Se asume que este v_Temp es no negativo
		v_PaidAmt := v_PaidAmt
        -- Allocation
			+ currencyConvert(v_Temp,
				ar.C_Currency_ID, v_Currency_ID, v_DateAcct, v_ConversionType_ID, 
				ar.AD_Client_ID, ar.AD_Org_ID);
      	--RAISE NOTICE ' C_Invoice_ID=% , PaidAmt=% , Allocation= % ',p_C_Invoice_ID, v_PaidAmt, v_Temp;
	END LOOP;
	RETURN	v_PaidAmt * v_MultiplierAP;
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION getallocatedamt(integer, integer, integer, integer) OWNER TO libertya;

-- 20120831-1710 Modificación de la función paymentavailable
CREATE OR REPLACE FUNCTION paymentavailable(p_c_payment_id integer)
  RETURNS numeric AS
$BODY$
DECLARE
	v_Currency_ID		INTEGER;
	v_AvailableAmt		NUMERIC := 0;
   	v_IsReceipt         CHARACTER(1);
   	v_Amt               NUMERIC := 0;
   	r   			RECORD;
	v_Charge_ID INTEGER; -- no en Adempiere
	v_ConversionType_ID INTEGER; -- no en Adempiere; puede ser null (igual es dudoso este tipo de conversion, por que lo 
	--que se conveirte al moneda del pago son las lineas de alocacion, las cuales en otros calculos usan posiblemente otro tipo de conversion)
	v_DateAcct timestamp without time zone;

BEGIN

	BEGIN
	--	Get Currency, PayAmt,IsReceipt (not used, only for debug) and C_Charge_ID
	SELECT	C_Currency_ID, PayAmt, IsReceipt, 
			C_Charge_ID,C_ConversionType_ID, DateAcct
	  INTO	STRICT 
			v_Currency_ID, v_AvailableAmt, v_IsReceipt,
			v_Charge_ID,v_ConversionType_ID, v_DateAcct
	FROM	C_Payment     --NO CORREGIDO por AP/AR
	WHERE	C_Payment_ID = p_C_Payment_ID;
		EXCEPTION	--	No encontrado; posiblememte mal llamado
		WHEN OTHERS THEN
            	RAISE NOTICE 'PaymentAvailable - %', SQLERRM;
			RETURN NULL;
	END;
	
	IF (v_Charge_ID > 0 ) THEN -- mayor que cero, por lo tanto no null 
	   RETURN 0;
	END IF;
--  DBMS_OUTPUT.PUT_LINE('== C_Payment_ID=' || p_C_Payment_ID || ', PayAmt=' || v_AvailableAmt || ', Receipt=' || v_IsReceipt);

	--	Calculate Allocated Amount
	FOR r IN
		SELECT	a.AD_Client_ID, a.AD_Org_ID, al.Amount, a.C_Currency_ID, a.DateTrx
		FROM	C_AllocationLine al
	        INNER JOIN C_AllocationHdr a ON (al.C_AllocationHdr_ID=a.C_AllocationHdr_ID)
		WHERE	al.C_Payment_ID = p_C_Payment_ID
          	AND   a.IsActive='Y'
	LOOP
        v_Amt := currencyConvert(r.Amount, r.C_Currency_ID, v_Currency_ID, 
				v_DateAcct, v_ConversionType_ID, r.AD_Client_ID, r.AD_Org_ID);
	    v_AvailableAmt := v_AvailableAmt - v_Amt;
--      DBMS_OUTPUT.PUT_LINE('  Allocation=' || a.Amount || ' - Available=' || v_AvailableAmt);
	END LOOP;
	
	IF (v_AvailableAmt < 0) THEN
		RAISE NOTICE 'Payment Available negative, correcting to zero - %',v_AvailableAmt ;
		v_AvailableAmt := 0;
	END IF;
	
	-- siguiente NO en Libertya
	--	Ignore Rounding; 
	--IF (v_AvailableAmt BETWEEN -0.00999 AND 0.00999) THEN
	--	v_AvailableAmt := 0;
	--END IF;
	--	Round to penny
	--v_AvailableAmt := ROUND(COALESCE(v_AvailableAmt,0), 2);
	
	-- redondeo de moneda
	v_AvailableAmt :=  currencyRound(v_AvailableAmt,v_Currency_ID,NULL);
	RETURN	v_AvailableAmt;
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION paymentavailable(integer) OWNER TO libertya;

-- 20120903-1743 Nueva columna en la factura para setear si el nro de doc es manual
ALTER TABLE c_invoice ADD COLUMN ManualDocumentNo character(1) NOT NULL DEFAULT 'N'::bpchar;

-- 20120903-1744 Incorporación de soporte para registrar el valor por defecto a nivel de campo
ALTER TABLE ad_field ADD COLUMN defaultvalue character varying(2000);

CREATE OR REPLACE VIEW ad_field_v AS 
 SELECT t.ad_window_id, f.ad_tab_id, f.ad_field_id, tbl.ad_table_id, f.ad_column_id, f.name, f.description, f.help, f.isdisplayed, f.displaylogic, f.displaylength, f.seqno, f.sortno, f.issameline, f.isheading, f.isfieldonly, f.isreadonly, f.isencrypted AS isencryptedfield, f.obscuretype, c.columnname, c.columnsql, c.fieldlength, c.vformat, (CASE WHEN f.defaultvalue IS NULL THEN c.defaultvalue ELSE f.defaultvalue END) as defaultvalue, c.iskey, c.isparent, c.ismandatory, c.isidentifier, c.istranslated, c.ad_reference_value_id, c.callout, c.ad_reference_id, c.ad_val_rule_id, c.ad_process_id, c.isalwaysupdateable, c.readonlylogic, c.isupdateable, c.isencrypted AS isencryptedcolumn, c.isselectioncolumn, tbl.tablename, c.valuemin, c.valuemax, fg.name AS fieldgroup, vr.code AS validationcode, f.isdisplayedingrid, c.calloutalsoonload
   FROM ad_field f
   JOIN ad_tab t ON f.ad_tab_id = t.ad_tab_id
   LEFT JOIN ad_fieldgroup fg ON f.ad_fieldgroup_id = fg.ad_fieldgroup_id
   LEFT JOIN ad_column c ON f.ad_column_id = c.ad_column_id
   JOIN ad_table tbl ON c.ad_table_id = tbl.ad_table_id
   JOIN ad_reference r ON c.ad_reference_id = r.ad_reference_id
   LEFT JOIN ad_val_rule vr ON c.ad_val_rule_id = vr.ad_val_rule_id
  WHERE f.isactive = 'Y'::bpchar AND c.isactive = 'Y'::bpchar;

ALTER TABLE ad_field_v OWNER TO libertya;

CREATE OR REPLACE VIEW ad_field_vt AS 
 SELECT trl.ad_language, t.ad_window_id, f.ad_tab_id, f.ad_field_id, tbl.ad_table_id, f.ad_column_id, trl.name, trl.description, trl.help, f.isdisplayed, f.displaylogic, f.displaylength, f.seqno, f.sortno, f.issameline, f.isheading, f.isfieldonly, f.isreadonly, f.isencrypted AS isencryptedfield, f.obscuretype, c.columnname, c.columnsql, c.fieldlength, c.vformat, (CASE WHEN f.defaultvalue IS NULL THEN c.defaultvalue ELSE f.defaultvalue END) as defaultvalue, c.iskey, c.isparent, c.ismandatory, c.isidentifier, c.istranslated, c.ad_reference_value_id, c.callout, c.ad_reference_id, c.ad_val_rule_id, c.ad_process_id, c.isalwaysupdateable, c.readonlylogic, c.isupdateable, c.isencrypted AS isencryptedcolumn, c.isselectioncolumn, tbl.tablename, c.valuemin, c.valuemax, fgt.name AS fieldgroup, vr.code AS validationcode, f.isdisplayedingrid, c.calloutalsoonload
   FROM ad_field f
   JOIN ad_field_trl trl ON f.ad_field_id = trl.ad_field_id
   JOIN ad_tab t ON f.ad_tab_id = t.ad_tab_id
   LEFT JOIN ad_fieldgroup_trl fgt ON f.ad_fieldgroup_id = fgt.ad_fieldgroup_id AND trl.ad_language::text = fgt.ad_language::text
   LEFT JOIN ad_column c ON f.ad_column_id = c.ad_column_id
   JOIN ad_table tbl ON c.ad_table_id = tbl.ad_table_id
   JOIN ad_reference r ON c.ad_reference_id = r.ad_reference_id
   LEFT JOIN ad_val_rule vr ON c.ad_val_rule_id = vr.ad_val_rule_id
  WHERE f.isactive = 'Y'::bpchar AND c.isactive = 'Y'::bpchar;

ALTER TABLE ad_field_vt OWNER TO libertya;

-- 20120909-1550 Incorporación de configuración de tpv para anular comprobantes bajo clave
update ad_system set dummy = (SELECT addcolumnifnotexists('c_pos','voiddocuments', 'character(1) NOT NULL DEFAULT ''Y''::bpchar'));

-- 20120911-1700 Incorporación de parámetros adicionales para importación de pedidos
update ad_system set dummy = (SELECT addcolumnifnotexists('i_order','paymentrule', 'character varying(2)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_order','orgvalue', 'character varying(40)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_order','salesrep_name', 'character varying(60)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_order','documentnobysequence', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_order','pricelist_name', 'character varying(60)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_order','iso_code', 'character(3)'));
ALTER TABLE i_order ALTER COLUMN taxindicator TYPE character varying(10);

-- 20120912-0125 Incorporación de parámetro moneda para importación de facturas
update ad_system set dummy = (SELECT addcolumnifnotexists('i_invoice','iso_code', 'character(3)'));

-- 20120913-2100 Incorporación de parámetro orgvalue para importación de inventario
update ad_system set dummy = (SELECT addcolumnifnotexists('i_inventory','orgvalue', 'character varying(40)'));

-- 20120914-0010 Incorporación de nueva configuración para control de cuits por cheques
ALTER TABLE ad_orginfo ADD COLUMN checkcuitcontrol character(1) NOT NULL DEFAULT 'N'::bpchar;
ALTER TABLE ad_orginfo ADD COLUMN initialchecklimit numeric(20,2) NOT NULL DEFAULT 0;

CREATE TABLE c_checkcuitcontrol
(
  c_checkcuitcontrol_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  cuit character varying(20) NOT NULL,
  nombre character varying(60),
  checklimit numeric(20,2) NOT NULL DEFAULT 0,
  CONSTRAINT c_checkcuitcontrol_key PRIMARY KEY (c_checkcuitcontrol_id),
  CONSTRAINT c_checkcuitcontrol_org FOREIGN KEY (ad_org_id)
      REFERENCES ad_org (ad_org_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT c_checkcuitcontrol_client FOREIGN KEY (ad_client_id)
      REFERENCES ad_client (ad_client_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
ALTER TABLE c_checkcuitcontrol OWNER TO libertya; 

-- 20120927-1430 Creación de la tabla I_InOut para Importación de Remitos
CREATE TABLE i_inout
(
  i_inout_id integer NOT NULL,
  ad_client_id integer,
  ad_org_id integer,
  ad_orgtrx_id integer,
  isactive character(1) DEFAULT 'Y'::bpchar,
  created timestamp without time zone DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer,
  updated timestamp without time zone DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer,
  i_isimported character(1) NOT NULL DEFAULT 'N'::bpchar,
  i_errormsg character varying(2000),
  processing character(1),
  processed character(1) DEFAULT 'N'::bpchar,
  salesrep_id integer,
  m_warehouse_id integer,
  warehousevalue character varying(40),
  issotrx character(1) DEFAULT 'Y'::bpchar,
  c_bpartner_id integer,
  c_bpartner_location_id integer,
  bpartnervalue character varying(40),
  "name" character varying(60),
  c_location_id integer,
  address1 character varying(60),
  address2 character varying(60),
  postal character varying(10),
  city character varying(60),
  c_region_id integer,
  regionname character varying(60),
  ad_user_id integer,
  email character varying(60),
  contactname character varying(60),
  phone character varying(40),
  c_country_id integer,
  countrycode character(2),
  c_doctype_id integer,
  doctypename character varying(60),
  c_doctypeorder_id integer,
  doctypenameorder character varying(60),
  c_project_id integer,
  c_campaign_id integer,
  c_activity_id integer,
  m_inout_id integer,
  documentno character varying(30),
  c_order_id integer,
  c_orderline_id integer,
  documentnoorder character varying(30),
  movementdate timestamp without time zone,
  dateacct timestamp without time zone,
  description character varying(255),
  line numeric(18,0),
  m_product_id integer,
  productvalue character varying(40),
  upc character varying(30),
  m_inoutline_id integer,
  linedescription character varying(255),
  movementqty numeric(22,4) DEFAULT 0,
  m_locator_id integer,
  locatorvalue character varying(40),
  orgvalue character varying(40),
  salesrep_name character varying(60),
  documentnobysequence character(1) NOT NULL DEFAULT 'N'::bpchar,
  CONSTRAINT i_inout_key PRIMARY KEY (i_inout_id),
  CONSTRAINT adorg_iinout FOREIGN KEY (ad_org_id)
      REFERENCES ad_org (ad_org_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT adorgtrx_iinout FOREIGN KEY (ad_orgtrx_id)
      REFERENCES ad_org (ad_org_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT aduser_iinout FOREIGN KEY (ad_user_id)
      REFERENCES ad_user (ad_user_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT adusersalesrep_iinout FOREIGN KEY (salesrep_id)
      REFERENCES ad_user (ad_user_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cactivity_iinout FOREIGN KEY (c_activity_id)
      REFERENCES c_activity (c_activity_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cbpartner_iinout FOREIGN KEY (c_bpartner_id)
      REFERENCES c_bpartner (c_bpartner_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cbplocation_iinout FOREIGN KEY (c_bpartner_location_id)
      REFERENCES c_bpartner_location (c_bpartner_location_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT ccampaign_iinout FOREIGN KEY (c_campaign_id)
      REFERENCES c_campaign (c_campaign_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT ccountry_iinout FOREIGN KEY (c_country_id)
      REFERENCES c_country (c_country_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cdoctype_iinout FOREIGN KEY (c_doctype_id)
      REFERENCES c_doctype (c_doctype_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cdoctypeorder_iinout FOREIGN KEY (c_doctypeorder_id)
      REFERENCES c_doctype (c_doctype_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT clocation_iinout FOREIGN KEY (c_location_id)
      REFERENCES c_location (c_location_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT corder_iinout FOREIGN KEY (c_order_id)
      REFERENCES c_order (c_order_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT corderline_iinout FOREIGN KEY (c_orderline_id)
      REFERENCES c_orderline (c_orderline_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cproject_iinout FOREIGN KEY (c_project_id)
      REFERENCES c_project (c_project_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cregion_iinout FOREIGN KEY (c_region_id)
      REFERENCES c_region (c_region_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT minout_iinout FOREIGN KEY (m_inout_id)
      REFERENCES m_inout (m_inout_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT minoutline_iinout FOREIGN KEY (m_inoutline_id)
      REFERENCES m_inoutline (m_inoutline_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT mlocator_iinout FOREIGN KEY (m_locator_id)
      REFERENCES m_locator (m_locator_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT mwarehouse_iinout FOREIGN KEY (m_warehouse_id)
      REFERENCES m_warehouse (m_warehouse_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT mproduct_iinout FOREIGN KEY (m_product_id)
      REFERENCES m_product (m_product_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=TRUE
);
ALTER TABLE i_inout OWNER TO libertya;

-- 20121002-1928 Incorporación de reglas de validación a nivel de campo de ventana
update ad_system set dummy = (SELECT addcolumnifnotexists('ad_field','ad_val_rule_id', 'integer'));

CREATE OR REPLACE VIEW ad_field_v AS 
 SELECT t.ad_window_id, f.ad_tab_id, f.ad_field_id, tbl.ad_table_id, f.ad_column_id, f.name, f.description, f.help, f.isdisplayed, f.displaylogic, f.displaylength, f.seqno, f.sortno, f.issameline, f.isheading, f.isfieldonly, f.isreadonly, f.isencrypted AS isencryptedfield, f.obscuretype, c.columnname, c.columnsql, c.fieldlength, c.vformat, (CASE WHEN f.defaultvalue IS NULL THEN c.defaultvalue ELSE f.defaultvalue END) as defaultvalue, c.iskey, c.isparent, c.ismandatory, c.isidentifier, c.istranslated, c.ad_reference_value_id, c.callout, c.ad_reference_id, (CASE WHEN f.ad_val_rule_id IS NULL THEN c.ad_val_rule_id ELSE f.ad_val_rule_id END) as ad_val_rule_id, c.ad_process_id, c.isalwaysupdateable, c.readonlylogic, c.isupdateable, c.isencrypted AS isencryptedcolumn, c.isselectioncolumn, tbl.tablename, c.valuemin, c.valuemax, fg.name AS fieldgroup, vr.code AS validationcode, f.isdisplayedingrid, c.calloutalsoonload
   FROM ad_field f
   JOIN ad_tab t ON f.ad_tab_id = t.ad_tab_id
   LEFT JOIN ad_fieldgroup fg ON f.ad_fieldgroup_id = fg.ad_fieldgroup_id
   LEFT JOIN ad_column c ON f.ad_column_id = c.ad_column_id
   JOIN ad_table tbl ON c.ad_table_id = tbl.ad_table_id
   JOIN ad_reference r ON c.ad_reference_id = r.ad_reference_id
   LEFT JOIN ad_val_rule vr ON COALESCE(f.ad_val_rule_id, c.ad_val_rule_id) = vr.ad_val_rule_id
  WHERE f.isactive = 'Y'::bpchar AND c.isactive = 'Y'::bpchar;

ALTER TABLE ad_field_v OWNER TO libertya;

CREATE OR REPLACE VIEW ad_field_vt AS 
 SELECT trl.ad_language, t.ad_window_id, f.ad_tab_id, f.ad_field_id, tbl.ad_table_id, f.ad_column_id, trl.name, trl.description, trl.help, f.isdisplayed, f.displaylogic, f.displaylength, f.seqno, f.sortno, f.issameline, f.isheading, f.isfieldonly, f.isreadonly, f.isencrypted AS isencryptedfield, f.obscuretype, c.columnname, c.columnsql, c.fieldlength, c.vformat, (CASE WHEN f.defaultvalue IS NULL THEN c.defaultvalue ELSE f.defaultvalue END) as defaultvalue, c.iskey, c.isparent, c.ismandatory, c.isidentifier, c.istranslated, c.ad_reference_value_id, c.callout, c.ad_reference_id, (CASE WHEN f.ad_val_rule_id IS NULL THEN c.ad_val_rule_id ELSE f.ad_val_rule_id END) as ad_val_rule_id, c.ad_process_id, c.isalwaysupdateable, c.readonlylogic, c.isupdateable, c.isencrypted AS isencryptedcolumn, c.isselectioncolumn, tbl.tablename, c.valuemin, c.valuemax, fgt.name AS fieldgroup, vr.code AS validationcode, f.isdisplayedingrid, c.calloutalsoonload
   FROM ad_field f
   JOIN ad_field_trl trl ON f.ad_field_id = trl.ad_field_id
   JOIN ad_tab t ON f.ad_tab_id = t.ad_tab_id
   LEFT JOIN ad_fieldgroup_trl fgt ON f.ad_fieldgroup_id = fgt.ad_fieldgroup_id AND trl.ad_language::text = fgt.ad_language::text
   LEFT JOIN ad_column c ON f.ad_column_id = c.ad_column_id
   JOIN ad_table tbl ON c.ad_table_id = tbl.ad_table_id
   JOIN ad_reference r ON c.ad_reference_id = r.ad_reference_id
   LEFT JOIN ad_val_rule vr ON COALESCE(f.ad_val_rule_id, c.ad_val_rule_id) = vr.ad_val_rule_id
  WHERE f.isactive = 'Y'::bpchar AND c.isactive = 'Y'::bpchar;

ALTER TABLE ad_field_vt OWNER TO libertya;

-- 20121007-2214 Incorporación de nuevas columnas para soporte de transferencia de pedidos
update ad_system set dummy = (SELECT addcolumnifnotexists('c_order','nombrecli', 'character varying(40)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_order','nroidentificcliente', 'character varying(120)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_order','invoice_adress', 'character varying(120)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_order','cuit', 'character varying(20)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_order','ad_org_transfer_id', 'integer'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_order','m_warehouse_transfer_id', 'integer'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_orderline','qtytransferred', 'numeric(22,4) NOT NULL DEFAULT 0'));

-- 20121015-2340 Incorporación de nueva parametrización para imputación de notas de crédito automáticas
update ad_system set dummy = (SELECT addcolumnifnotexists('c_bpartner','automaticcreditnotes', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));

-- 20121018-1335 Incorporación de nueva columna a la tabla C_Order
update ad_system set dummy = (SELECT addcolumnifnotexists('c_order','istpvused', 'character(1) DEFAULT ''N''::bpchar'));

-- 20121018-1345 Modificación de la vista rv_ordernotinvoice
CREATE OR REPLACE VIEW rv_ordernotinvoice AS 
 SELECT o.ad_client_id, o.ad_org_id, o.isactive, o.created, o.createdby, o.updated, o.updatedby, o.c_order_id, o.documentno, o.docstatus, o.docaction, o.c_doctype_id, o.c_doctypetarget_id, o.dateordered, o.c_bpartner_id, o.totallines, o.grandtotal
   FROM c_order o
  WHERE o.isactive = 'Y'::bpchar AND o.issotrx = 'Y'::bpchar AND o.istpvused = 'N'::bpchar;
ALTER TABLE rv_ordernotinvoice OWNER TO libertya;

-- 20121018-1622 Modificación de la vista de saldos bancarios para que muestre el nro de documento correctamente
CREATE OR REPLACE VIEW v_bankbalances AS 
         SELECT bsl.ad_client_id, bsl.ad_org_id, bsl.isactive, bs.c_bankaccount_id, '@StatementLine@'::character varying AS documenttype, bsl.description::text::character varying AS documentno, bs.statementdate AS datetrx, bsl.dateacct AS duedate, bs.docstatus, '' AS ischequesencartera, 
                CASE
                    WHEN bsl.stmtamt < 0.0 THEN abs(bsl.stmtamt)
                    ELSE 0.0
                END AS debit, 
                CASE
                    WHEN bsl.stmtamt >= 0.0 THEN abs(bsl.stmtamt)
                    ELSE 0.0
                END AS credit, bsl.isreconciled
           FROM c_bankstatementline bsl
      JOIN c_bankstatement bs ON bsl.c_bankstatement_id = bs.c_bankstatement_id
   LEFT JOIN c_bpartner bp ON bsl.c_bpartner_id = bp.c_bpartner_id
UNION 
         SELECT p.ad_client_id, p.ad_org_id, p.isactive, p.c_bankaccount_id, dt.name AS documenttype, COALESCE((CASE WHEN p.checkno IS NULL OR trim(p.checkno) = '' THEN p.documentno ELSE p.checkno END),bp.name) AS documentno, p.datetrx, COALESCE(p.duedate, p.dateacct) AS duedate, p.docstatus, ba.ischequesencartera, 
                CASE
                    WHEN dt.signo_issotrx = 1 THEN abs(p.payamt)
                    ELSE 0.0
                END AS debit, 
                CASE
                    WHEN dt.signo_issotrx = (-1) THEN 
                    CASE
                        WHEN ba.ischequesencartera = 'Y'::bpchar THEN p.payamt
                        ELSE abs(p.payamt)
                    END
                    ELSE 0.0
                END AS credit, p.isreconciled
           FROM c_payment p
      JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id
   JOIN c_bpartner bp ON p.c_bpartner_id = bp.c_bpartner_id
   JOIN c_bankaccount ba ON p.c_bankaccount_id = ba.c_bankaccount_id
  WHERE NOT (EXISTS ( SELECT bsl.c_bankstatementline_id, bsl.ad_client_id, bsl.ad_org_id, bsl.isactive, bsl.created, bsl.createdby, bsl.updated, bsl.updatedby, bsl.c_bankstatement_id, bsl.line, bsl.description, bsl.isreversal, bsl.c_payment_id, bsl.valutadate, bsl.dateacct, bsl.c_currency_id, bsl.trxamt, bsl.stmtamt, bsl.c_charge_id, bsl.chargeamt, bsl.interestamt, bsl.memo, bsl.referenceno, bsl.ismanual, bsl.efttrxid, bsl.efttrxtype, bsl.eftmemo, bsl.eftpayee, bsl.eftpayeeaccount, bsl.createpayment, bsl.statementlinedate, bsl.eftstatementlinedate, bsl.eftvalutadate, bsl.eftreference, bsl.eftcurrency, bsl.eftamt, bsl.eftcheckno, bsl.matchstatement, bsl.c_bpartner_id, bsl.c_invoice_id, bsl.processed, bsl.m_boletadeposito_id, bsl.isreconciled, bs.c_bankstatement_id, bs.ad_client_id, bs.ad_org_id, bs.isactive, bs.created, bs.createdby, bs.updated, bs.updatedby, bs.c_bankaccount_id, bs.name, bs.description, bs.ismanual, bs.statementdate, bs.beginningbalance, bs.endingbalance, bs.statementdifference, bs.createfrom, bs.processing, bs.processed, bs.posted, bs.eftstatementreference, bs.eftstatementdate, bs.matchstatement, bs.isapproved, bs.docstatus, bs.docaction, bsr.c_bankstatline_reconcil_id, bsr.ad_client_id, bsr.ad_org_id, bsr.isactive, bsr.created, bsr.createdby, bsr.updated, bsr.updatedby, bsr.c_bankstatementline_id, bsr.c_payment_id, bsr.m_boletadeposito_id, bsr.c_currency_id, bsr.trxamt, bsr.referenceno, bsr.ismanual, bsr.processed, bsr.isreconciled, bsr.processing, bsr.docstatus, bsr.docaction
    FROM c_bankstatementline bsl
   JOIN c_bankstatement bs ON bsl.c_bankstatement_id = bs.c_bankstatement_id
   LEFT JOIN c_bankstatline_reconcil bsr ON bsl.c_bankstatementline_id::numeric = bsr.c_bankstatementline_id
  WHERE (bs.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) AND (bsl.c_payment_id = p.c_payment_id OR bsr.c_payment_id = p.c_payment_id::numeric)));

ALTER TABLE v_bankbalances OWNER TO libertya;

-- 20121018-2020 Modificación de la vista rv_projectedpayments
DROP VIEW v_projectedpayments;

CREATE OR REPLACE VIEW v_projectedpayments AS 
         SELECT v.documenttable, v.document_id, v.ad_client_id, v.ad_org_id, v.isactive, v.created, v.createdby, v.updated, v.updatedby, v.c_bpartner_id, v.c_doctype_id, v.signo_issotrx, v.doctypename, v.doctypeprintname, v.documentno, v.issotrx, v.docstatus, v.datetrx, v.dateacct, v.c_currency_id, v.c_conversiontype_id, v.amount * v.signo_issotrx::numeric * (-1)::numeric AS amount, v.c_invoicepayschedule_id, v.duedate, i.ispaid, NULL::character varying(20) AS checkno, invoiceopen(i.c_invoice_id, 0) * v.signo_issotrx::numeric * (-1)::numeric AS openamount, i.c_invoice_id, NULL::integer AS c_payment_id, ip.duedate AS filterdate
           FROM v_documents v
      JOIN c_invoice i ON i.c_invoice_id = v.document_id
   JOIN c_invoicepayschedule ip ON ip.c_invoice_id = v.document_id AND ip.c_invoicepayschedule_id = v.c_invoicepayschedule_id
  WHERE i.issotrx = 'N'::bpchar AND v.documenttable = 'C_Invoice'::text AND (v.docstatus = 'CO'::bpchar OR v.docstatus = 'CL'::bpchar)
UNION ALL 
         SELECT v.documenttable, v.document_id, v.ad_client_id, v.ad_org_id, v.isactive, v.created, v.createdby, v.updated, v.updatedby, v.c_bpartner_id, v.c_doctype_id, v.signo_issotrx, 'Cheque' AS doctypename, v.doctypeprintname, v.documentno, v.issotrx, v.docstatus, v.datetrx, v.dateacct, v.c_currency_id, v.c_conversiontype_id, v.amount * v.signo_issotrx::numeric * (-1)::numeric AS amount, v.c_invoicepayschedule_id, v.duedate, NULL::character(1) AS ispaid, p.checkno, paymentavailable(p.c_payment_id) * v.signo_issotrx::numeric * (-1)::numeric AS openamount, NULL::integer AS c_invoice_id, p.c_payment_id, p.duedate AS filterdate
           FROM v_documents v
      JOIN c_payment p ON p.c_payment_id = v.document_id
     WHERE p.issotrx = 'N'::bpchar AND p.tendertype = 'K'::bpchar AND p.datetrx < p.duedate AND (v.docstatus = 'CO'::bpchar OR v.docstatus = 'CL'::bpchar);

ALTER TABLE v_projectedpayments OWNER TO libertya;

-- 20121020-0125 Incorporación de columna clave a la tabla M_InOutLineMA, eliminando la clave múltiple
ALTER TABLE m_inoutlinema DROP CONSTRAINT m_inoutlinema_key;

ALTER TABLE m_inoutlinema ADD COLUMN m_inoutlinema_id integer;

CREATE SEQUENCE seq_m_inoutlinema
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1000000
  CACHE 1;
ALTER TABLE seq_m_inoutlinema OWNER TO libertya;

UPDATE m_inoutlinema
SET m_inoutlinema_id = nextval('seq_m_inoutlinema');

ALTER TABLE m_inoutlinema ADD CONSTRAINT m_inoutlinema_key PRIMARY KEY (m_inoutlinema_id);

ALTER TABLE m_inoutlinema ALTER COLUMN m_inoutlinema_id SET NOT NULL;

-- 20121027-0030 Mejora al cálculo de percepciones incorporando algoritmos y soporte para padrón de percepciones/retenciones
ALTER TABLE ad_org_percepcion ADD COLUMN c_retencionprocessor_id integer;
ALTER TABLE c_bpartner_padron_bsas ADD COLUMN padrontype character(1);

ALTER TABLE c_bpartner_percexenc ALTER COLUMN c_tax_id DROP NOT NULL;
ALTER TABLE c_bpartner_percexenc ADD COLUMN ad_org_percepcion_id integer;

-- 20121031-1907 Flag a la factura que permite o no aplicar percepciones
ALTER TABLE c_invoice ADD COLUMN applypercepcion character(1) NOT NULL DEFAULT 'Y'::bpchar;

-- 20121031-2015 Eliminación de registros existentes por creación de informe de libro de IVA - Kunan
DELETE FROM AD_Field WHERE ad_componentobjectuid = 'LIVA-AD_Field-1016463';
DELETE FROM AD_Field WHERE ad_componentobjectuid = 'LIVA-AD_Field-1016464';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Column_Trl-1015146-es_ES';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Column_Trl-1015146-es_AR';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Column_Trl-1015146-es_MX';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'LIVA-AD_Column-1015146';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'LIVA-AD_Column-1015147';
DELETE FROM AD_Menu WHERE ad_componentobjectuid = 'LIVA-AD_Menu-1010385';
DELETE FROM AD_Menu_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Menu_Trl-es_ES-1010385';
DELETE FROM AD_Menu_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Menu_Trl-es_AR-1010385';
DELETE FROM AD_Menu_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Menu_Trl-es_MX-1010385';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Field_Trl-1016463-es_ES';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Field_Trl-1016463-es_AR';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Field_Trl-1016463-es_MX';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Field_Trl-1016464-es_ES';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Field_Trl-1016464-es_AR';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Field_Trl-1016464-es_MX';
DELETE FROM AD_Element WHERE ad_componentobjectuid = 'LIVA-AD_Element-1011058';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Element_Trl-1011058-es_ES';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Element_Trl-1011058-es_AR';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Element_Trl-1011058-es_MX';
DELETE FROM AD_Process WHERE ad_componentobjectuid = 'LIVA-AD_Process-1010243';
DELETE FROM AD_Process_Para WHERE ad_componentobjectuid = 'LIVA-AD_Process_Para-1010492';
DELETE FROM AD_Process_Para WHERE ad_componentobjectuid = 'LIVA-AD_Process_Para-1010493';
DELETE FROM AD_Process_Para WHERE ad_componentobjectuid = 'LIVA-AD_Process_Para-1010494';
DELETE FROM AD_Process_Para WHERE ad_componentobjectuid = 'LIVA-AD_Process_Para-1010495';
DELETE FROM AD_Process_Para_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Process_Para_Trl-es_ES-1010492';
DELETE FROM AD_Process_Para_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Process_Para_Trl-es_AR-1010492';
DELETE FROM AD_Process_Para_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Process_Para_Trl-es_MX-1010492';
DELETE FROM AD_Process_Para_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Process_Para_Trl-es_ES-1010493';
DELETE FROM AD_Process_Para_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Process_Para_Trl-es_AR-1010493';
DELETE FROM AD_Process_Para_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Process_Para_Trl-es_MX-1010493';
DELETE FROM AD_Process_Para_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Process_Para_Trl-es_ES-1010494';
DELETE FROM AD_Process_Para_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Process_Para_Trl-es_AR-1010494';
DELETE FROM AD_Process_Para_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Process_Para_Trl-es_MX-1010494';
DELETE FROM AD_Process_Para_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Process_Para_Trl-es_ES-1010495';
DELETE FROM AD_Process_Para_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Process_Para_Trl-es_AR-1010495';
DELETE FROM AD_Process_Para_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Process_Para_Trl-es_MX-1010495';
DELETE FROM AD_Process_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Process_Trl-es_ES-1010243';
DELETE FROM AD_Process_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Process_Trl-es_AR-1010243';
DELETE FROM AD_Process_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Process_Trl-es_MX-1010243';
DELETE FROM AD_Process_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Process_Trl-es_ES-1010250';
DELETE FROM AD_Process_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Process_Trl-es_AR-1010250';
DELETE FROM AD_Process_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Process_Trl-es_MX-1010250';
DELETE FROM AD_Process_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Process_Trl-es_PY-1010250';
DELETE FROM AD_TreeNodeMM WHERE ad_componentobjectuid = 'LIVA-AD_TreeNodeMM-10-1010385';
DELETE FROM AD_TreeNodeMM WHERE ad_componentobjectuid = 'LIVA-AD_TreeNodeMM-1010115-1010385';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Column_Trl-1015147-es_ES';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Column_Trl-1015147-es_AR';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'LIVA-AD_Column_Trl-1015147-es_MX';
DELETE FROM AD_JasperReport WHERE ad_componentobjectuid = 'LIVA-AD_JasperReport-1010047';
DELETE FROM AD_Process_Access WHERE ad_componentobjectuid = 'LIVA-AD_Process_Access-1010243-0';
DELETE FROM AD_Process_Access WHERE ad_componentobjectuid = 'LIVA-AD_Process_Access-1010243-1010076';
DELETE FROM AD_Process_Access WHERE ad_componentobjectuid = 'LIVA-AD_Process_Access-1010250-0';
DELETE FROM AD_JasperReport WHERE ad_componentobjectuid = 'LIVA-AD_JasperReport-1010053';

-- 20121031-2020 Columnas insertadas para el Informe Libro IVA - Kunan
update ad_system set dummy = (SELECT addcolumnifnotexists('AD_ClientInfo','C_Location_ID', 'numeric(10)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('C_Categoria_Iva','I_Tipo_IVA', 'character varying(10)'));
ALTER TABLE C_Categoria_Iva ALTER COLUMN I_Tipo_IVA TYPE character varying(10);

-- 20121108-1130 Incorporación de columna TaxIDType a la tabla c_bpartner
ALTER TABLE libertya.c_bpartner ADD COLUMN TaxIDType character(2);

-- Actualización del campo TaxIdType para todas las EC existentes. Este campo es activado por locale ar.
UPDATE C_BPartner SET TaxIdType='80' WHERE TaxIdType IS NULL AND TaxId IS NOT NULL AND TaxId <> '' AND C_Categoria_Iva_ID IN (SELECT C_Categoria_Iva_ID FROM C_Categoria_Iva WHERE requierecuit = 'Y');
UPDATE C_BPartner SET TaxIdType='96' WHERE TaxIdType IS NULL AND TaxId IS NOT NULL AND TaxId <> '' AND C_Categoria_Iva_ID IN (SELECT C_Categoria_Iva_ID FROM C_Categoria_Iva WHERE requierecuit = 'N');

-- 20121109-2000 Incorporación de columna TipoComprobante a la tabla C_Invoice
ALTER TABLE libertya.C_Invoice ADD COLUMN TipoComprobante character(2);

-- 20121113-2351 Funciones para obtener precios std de costo y venta  
CREATE OR REPLACE FUNCTION getProductPriceStd(productID integer, orgID integer, priceListID integer, isSOPriceList character(1))
  RETURNS numeric AS
$BODY$
/*************************************************************************
 * Obtiene el precio del artículo parámetro en la lista parámetro. 
 * Si la lista parámetro es null, entonces se determina por los restantes 
 * parámetros:
 * - Lista con la organización parámetro.
 * - Lista de venta o compra dependiendo el parámetro isSOPriceList.
 * La consulta del precio de lista requerido se ordena por 
 * > Lista de precio por defecto 
 * > Versión de lista de precio válido desde decreciente 
 * > Fecha de creación de versión de lista de precios decreciente
 ************************************************************************/
DECLARE
	sql character varying;
	priceListVersionID integer;
	pricestd numeric := 0;
BEGIN
	-- Consulta para determinar la lista de precios a la cual se debe consultar por el precio
	SELECT INTO pricestd pp.pricestd
		from m_pricelist_version as plv 
		inner join m_pricelist as pl on pl.m_pricelist_id = plv.m_pricelist_id 
		inner join m_productprice as pp on pp.m_pricelist_version_id = plv.m_pricelist_version_id 
		where m_product_id = productID 
			AND (priceListID is null OR priceListID = 0 OR pl.m_pricelist_id = priceListID)
			AND (isSOPriceList is null OR pl.issopricelist = isSOPriceList)
			AND (orgID is null OR orgID = 0 OR pl.ad_org_id = orgID)
		order by pl.isdefault desc, plv.validfrom desc, plv.created desc 
		LIMIT 1;

	if(pricestd is null) then pricestd := 0; end if;
	return pricestd;
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE;
ALTER FUNCTION getProductPriceStd(integer, integer, integer, character) OWNER TO libertya;


CREATE OR REPLACE FUNCTION determineProductPriceStd(productID integer, orgID integer, isSOPriceList character(1))
  RETURNS numeric AS
$BODY$
/*************************************************************************
 * Obtiene el precio std del artículo parámetro determinado por los siguientes pasos en orden:
 * 1- Si se debe determinar el precio de costo, primero verifica la tarifa de costo del proveedor asociado al artículo. 
 * 2- Tarifa de costo última (válida más última) de la tarifa de la organización parámetro, sino de cualquier organización.
 * 3- Si se debe buscar el precio de costo y existe un proveedor asociado al artículo, entonces como tercera opción busca el precio std configurado en la relación entre el proveedor y el artículo.
 ************************************************************************/
DECLARE
	sql character varying;
	priceListVersionID integer;
	pricestd numeric;
	po record;
	popricestd numeric;
	popricelist integer;
BEGIN
	IF(isSOPriceList = 'N')
	THEN
		-- Para precio de costo, verificar si el artículo posee un proveedor configurado y obtener la relación entre ellos
		SELECT INTO po * 
			FROM M_Product_PO 
			WHERE M_Product_ID=productID AND IsActive='Y'
			ORDER BY IsCurrentVendor DESC, Updated DESC
			LIMIT 1;
		IF(po.c_bpartner_id is not null)
		THEN
			-- Me guardo este precio por si al final no encontramos ninguno para asociar, correspondería al punto 3 descrito
			popricestd = po.pricelist;
			-- Obtener el precio de costo de la tarifa de costo asociada al proveedor
			SELECT INTO popricelist po_pricelist_id
				FROM c_bpartner
				WHERE c_bpartner_id = po.c_bpartner_id;
			IF(popricelist is not null)
			THEN
				pricestd = getProductPriceStd(productID, null, popricelist, null);
			END IF;
		END IF;
		-- Si no tengo precio de costo todavía, entonces vamos por el punto 2
		IF(pricestd is null OR pricestd = 0)
		THEN
			pricestd := getProductPriceStd(productID, orgID, null, isSOPriceList);
			-- Si no existe el precio de costo para esa organización, busco el último entre todas las organizaciones
			IF(pricestd = 0)
			THEN
				pricestd := getProductPriceStd(productID, null, null, isSOPriceList);
			END IF;
		END IF;
		-- Si todavía no tengo precio de costo, entonces punto 3
		IF((pricestd is null OR pricestd = 0) AND (popricestd is not null AND popricestd > 0))
		THEN
			pricestd := popricestd;
		END IF;
	ELSE
		-- Para precio de venta se debe buscar el precio de venta de la organización parámetro
		pricestd := getProductPriceStd(productID, orgID, null, isSOPriceList);
		-- Si no existe el precio de venta para esa organización, busco el último entre todas las organizaciones
		IF(pricestd = 0)
		THEN
			pricestd := getProductPriceStd(productID, null, null, isSOPriceList);
		END IF;
	END IF;
	return pricestd;
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE;
ALTER FUNCTION determineProductPriceStd(integer, integer, character) OWNER TO libertya;

-- 20121115-2255 Modificaciones a vistas c_posjournalpayments_v y v_dailysales por mejoras y fixes al reporte Resumen de Ventas 
DROP VIEW c_pos_declaracionvalores_v;
DROP VIEW v_dailysales;
DROP VIEW c_posjournalpayments_v;

CREATE OR REPLACE VIEW c_posjournalpayments_v AS 
        (        ( SELECT al.c_allocationhdr_id, al.c_allocationline_id, al.ad_client_id, al.ad_org_id, al.isactive, al.created, al.createdby, al.updated, al.updatedby, al.c_invoice_id, al.c_payment_id, al.c_cashline_id, al.c_invoice_credit_id, 
                        CASE
                            WHEN al.c_payment_id IS NOT NULL THEN p.tendertype::character varying
                            WHEN al.c_cashline_id IS NOT NULL THEN 'CA'::character varying
                            WHEN al.c_invoice_credit_id IS NOT NULL THEN 'CR'::character varying
                            ELSE NULL::character varying
                        END::character varying(2) AS tendertype, 
                        CASE
                            WHEN al.c_payment_id IS NOT NULL THEN p.documentno
                            WHEN al.c_invoice_credit_id IS NOT NULL THEN ic.documentno
                            ELSE NULL::character varying
                        END::character varying(30) AS documentno, 
                        CASE
                            WHEN al.c_payment_id IS NOT NULL THEN p.description
                            WHEN al.c_cashline_id IS NOT NULL THEN cl.description
                            WHEN al.c_invoice_credit_id IS NOT NULL THEN ic.description
                            ELSE NULL::character varying
                        END::character varying(255) AS description, 
                        CASE
                            WHEN al.c_payment_id IS NOT NULL THEN ((p.documentno::text || '_'::text) || to_char(p.datetrx, 'DD/MM/YYYY'::text))::character varying
                            WHEN al.c_cashline_id IS NOT NULL THEN ((c.name::text || '_#'::text) || cl.line::text)::character varying
                            WHEN al.c_invoice_credit_id IS NOT NULL THEN ic.documentno
                            ELSE NULL::character varying
                        END::character varying(255) AS info, COALESCE(currencyconvert(al.amount + al.discountamt + al.writeoffamt, ah.c_currency_id, i.c_currency_id, NULL::timestamp with time zone, NULL::integer, ah.ad_client_id, ah.ad_org_id), 0::numeric)::numeric(20,2) AS amount, cl.c_cash_id, cl.line, ic.c_doctype_id, p.checkno, p.a_bank, p.checkno AS transferno, p.creditcardtype, p.m_entidadfinancieraplan_id, ep.m_entidadfinanciera_id, p.couponnumber, date_trunc('day'::text, ah.datetrx) AS allocationdate, 
                        CASE
                            WHEN al.c_payment_id IS NOT NULL THEN p.docstatus
                            WHEN al.c_cashline_id IS NOT NULL THEN cl.docstatus
                            WHEN al.c_invoice_credit_id IS NOT NULL THEN ic.docstatus
                            ELSE NULL::character(2)
                        END AS docstatus, 
                        CASE
                            WHEN al.c_payment_id IS NOT NULL THEN p.dateacct::date
                            WHEN al.c_cashline_id IS NOT NULL THEN c.dateacct::date
                            WHEN al.c_invoice_credit_id IS NOT NULL THEN ic.dateacct::date
                            ELSE NULL::date
                        END AS dateacct
                   FROM c_allocationline al
              LEFT JOIN c_allocationhdr ah ON al.c_allocationhdr_id = ah.c_allocationhdr_id
         LEFT JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
    LEFT JOIN c_payment p ON al.c_payment_id = p.c_payment_id
   LEFT JOIN m_entidadfinancieraplan ep ON p.m_entidadfinancieraplan_id = ep.m_entidadfinancieraplan_id
   LEFT JOIN c_cashline cl ON al.c_cashline_id = cl.c_cashline_id
   LEFT JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
   LEFT JOIN c_invoice ic ON al.c_invoice_credit_id = ic.c_invoice_id
  ORDER BY 
CASE
    WHEN al.c_payment_id IS NOT NULL THEN p.tendertype::character varying
    WHEN al.c_cashline_id IS NOT NULL THEN 'CA'::character varying
    WHEN al.c_invoice_credit_id IS NOT NULL THEN 'CR'::character varying
    ELSE NULL::character varying
END::character varying(2), 
CASE
    WHEN al.c_payment_id IS NOT NULL THEN p.documentno
    WHEN al.c_invoice_credit_id IS NOT NULL THEN ic.documentno
    ELSE NULL::character varying
END::character varying(30))
        UNION ALL 
                 SELECT NULL::unknown AS c_allocationhdr_id, NULL::unknown AS c_allocationline_id, cl.ad_client_id, cl.ad_org_id, cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby, NULL::unknown AS c_invoice_id, NULL::unknown AS c_payment_id, cl.c_cashline_id, NULL::unknown AS c_invoice_credit_id, 'CA'::character varying(2) AS tendertype, NULL::character varying(30) AS documentno, cl.description, (((c.name::text || '_#'::text) || cl.line::text))::character varying(255) AS info, cl.amount, cl.c_cash_id, cl.line, NULL::unknown AS c_doctype_id, NULL::character varying(20) AS checkno, NULL::character varying(255) AS a_bank, NULL::character varying(20) AS transferno, NULL::character(1) AS creditcardtype, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS m_entidadfinanciera_id, NULL::character varying(30) AS couponnumber, date_trunc('day'::text, c.statementdate) AS allocationdate, cl.docstatus, c.dateacct::date AS dateacct
                   FROM c_cashline cl
              JOIN c_cash c ON c.c_cash_id = cl.c_cash_id
             WHERE NOT (EXISTS ( SELECT al.c_allocationline_id
                      FROM c_allocationline al
                     WHERE al.c_cashline_id = cl.c_cashline_id)))
UNION ALL 
        ( SELECT NULL::unknown AS c_allocationhdr_id, NULL::unknown AS c_allocationline_id, p.ad_client_id, p.ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, NULL::unknown AS c_invoice_id, p.c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, p.tendertype::character varying(2) AS tendertype, p.documentno, p.description, (((p.documentno::text || '_'::text) || to_char(p.datetrx, 'DD/MM/YYYY'::text)))::character varying(255) AS info, p.payamt AS amount, NULL::unknown AS c_cash_id, NULL::numeric(18,0) AS line, NULL::unknown AS c_doctype_id, p.checkno, p.a_bank, p.checkno AS transferno, p.creditcardtype, p.m_entidadfinancieraplan_id, ep.m_entidadfinanciera_id, p.couponnumber, date_trunc('day'::text, p.datetrx) AS allocationdate, p.docstatus, p.dateacct::date AS dateacct
           FROM c_payment p
      LEFT JOIN m_entidadfinancieraplan ep ON p.m_entidadfinancieraplan_id = ep.m_entidadfinancieraplan_id
     ORDER BY p.tendertype::character varying(2), p.documentno);

ALTER TABLE c_posjournalpayments_v OWNER TO libertya;

CREATE OR REPLACE VIEW c_pos_declaracionvalores_v AS 
(( SELECT i.ad_client_id, i.ad_org_id, i.c_posjournal_id, i.ad_user_id, i.c_currency_id, i.dateinvoiced AS datetrx, i.docstatus, NULL::unknown AS category, dt.docbasetype AS tendertype, (i.documentno::text || ' '::text) || COALESCE(i.description, ''::character varying)::text AS description, i.c_charge_id, i.chargename, i.c_invoice_id AS doc_id, 
        CASE dt.signo_issotrx
            WHEN 1 THEN i.total - i.open
            WHEN (-1) THEN 0::numeric
            ELSE NULL::numeric
        END::numeric(22,2) AS ingreso, 
        CASE dt.signo_issotrx
            WHEN 1 THEN 0::numeric
            WHEN (-1) THEN abs(i.total - i.open)
            ELSE NULL::numeric
        END::numeric(22,2) AS egreso
   FROM ( SELECT DISTINCT i.ad_client_id, i.ad_org_id, i.documentno, inv.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, i.dateinvoiced::date AS dateinvoiced, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name AS chargename, currencybase(i.grandtotal, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2) AS total, currencybase(inv.initialcurrentaccountamt, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2) AS open
           FROM c_posjournalinvoices_v i
      JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_invoice inv ON i.c_invoice_id = inv.c_invoice_id
   LEFT JOIN c_charge ch ON ch.c_charge_id = inv.c_charge_id
  ORDER BY i.ad_client_id, i.ad_org_id, i.documentno, inv.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, i.dateinvoiced::date, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name, currencybase(i.grandtotal, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2), currencybase(inv.initialcurrentaccountamt, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2)) i
   JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id
UNION ALL 
 SELECT p.ad_client_id, p.ad_org_id, p.c_posjournal_id, p.ad_user_id, p.c_currency_id, p.datetrx, p.docstatus, NULL::unknown AS category, p.tendertype, (p.documentno::text || ' '::text) || COALESCE(p.description, ''::character varying)::text AS description, p.c_charge_id, p.chargename, p.c_payment_id AS doc_id, 
        CASE p.isreceipt
            WHEN 'Y'::bpchar THEN p.total
            ELSE 0::numeric
        END::numeric(22,2) AS ingreso, 
        CASE p.isreceipt
            WHEN 'N'::bpchar THEN abs(p.total)
            ELSE 0::numeric
        END::numeric(22,2) AS egreso
   FROM ( SELECT p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date AS datetrx, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, p.c_currency_id, p.datetrx::timestamp with time zone, p.ad_client_id, p.ad_org_id)::numeric(22,2))::numeric(22,2) AS total
           FROM c_payment p
      JOIN c_posjournalpayments_v pjp ON pjp.c_payment_id = p.c_payment_id
   JOIN c_posjournal pj ON pj.c_posjournal_id = p.c_posjournal_id
   LEFT JOIN c_charge ch ON ch.c_charge_id = p.c_charge_id
  GROUP BY p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name) p)
UNION ALL 
 SELECT c.ad_client_id, c.ad_org_id, c.c_posjournal_id, c.ad_user_id, c.c_currency_id, c.datetrx, c.docstatus, c.cashtype AS category, c.tendertype, 
        CASE
            WHEN length(c.description::text) > 0 THEN c.description
            ELSE c.info
        END AS description, c.c_charge_id, c.chargename, c.c_cashline_id AS doc_id, 
        CASE sign(c.total)
            WHEN (-1) THEN 0::numeric
            ELSE c.total
        END::numeric(22,2) AS ingreso, 
        CASE sign(c.total)
            WHEN (-1) THEN abs(c.total)
            ELSE 0::numeric
        END::numeric(22,2) AS egreso
   FROM ( SELECT cl.ad_client_id, cl.ad_org_id, cl.c_cashline_id, c.c_posjournal_id, pj.ad_user_id, cl.c_currency_id, c.statementdate::date AS datetrx, cl.docstatus, cl.description, pjp.info, pjp.tendertype, cl.cashtype, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, cl.c_currency_id, c.statementdate::timestamp with time zone, cl.ad_client_id, cl.ad_org_id)::numeric(22,2))::numeric(22,2) AS total
           FROM c_cashline cl
      JOIN c_cash c ON c.c_cash_id = cl.c_cash_id
   JOIN c_posjournalpayments_v pjp ON pjp.c_cashline_id = cl.c_cashline_id
   JOIN c_posjournal pj ON pj.c_posjournal_id = c.c_posjournal_id
   LEFT JOIN c_charge ch ON ch.c_charge_id = cl.c_charge_id
  GROUP BY cl.ad_client_id, cl.ad_org_id, cl.c_cashline_id, c.c_posjournal_id, pj.ad_user_id, cl.c_currency_id, c.statementdate::date, cl.docstatus, cl.description, pjp.info, pjp.tendertype, cl.cashtype, ch.c_charge_id, ch.name) c)
UNION ALL 
 SELECT i.ad_client_id, i.ad_org_id, i.c_posjournal_id, i.ad_user_id, i.c_currency_id, i.dateinvoiced AS datetrx, i.docstatus, NULL::unknown AS category, i.tendertype, (i.documentno::text || ' '::text) || COALESCE(i.description, ''::character varying)::text AS description, i.c_charge_id, i.chargename, i.c_invoice_id AS doc_id, i.total AS ingreso, 0 AS egreso
   FROM ( SELECT i.ad_client_id, i.ad_org_id, i.documentno, i.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, pjp.tendertype, i.dateinvoiced::date AS dateinvoiced, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2))::numeric(22,2) AS total
           FROM c_invoice i
      JOIN c_posjournalpayments_v pjp ON pjp.c_invoice_credit_id = i.c_invoice_id
   JOIN c_allocationhdr ah ON ah.c_allocationhdr_id = pjp.c_allocationhdr_id
   JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   LEFT JOIN c_charge ch ON ch.c_charge_id = i.c_charge_id
  GROUP BY i.ad_client_id, i.ad_org_id, i.documentno, i.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, pjp.tendertype, i.dateinvoiced::date, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name) i
   JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id
  WHERE i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar]);

ALTER TABLE c_pos_declaracionvalores_v OWNER TO libertya;

CREATE OR REPLACE VIEW v_dailysales AS 
        (        (         SELECT 'P' AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                                    ELSE i.c_invoice_id
                                END AS c_invoice_id, pjp.allocationdate AS datetrx, pjp.c_payment_id, pjp.c_cashline_id, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN i.c_invoice_id
                                    ELSE pjp.c_invoice_credit_id
                                END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, pjp.amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.c_doctype_id
                                    WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.c_doctype_id
                                    WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.c_pospaymentmedium_id
                                    ELSE p.c_pospaymentmedium_id
                                END AS c_pospaymentmedium_id, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.docbasetype::character varying
                                    WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.docbasetype::character varying
                                    WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.name
                                    ELSE ppm.name
                                END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct
                           FROM c_posjournalpayments_v pjp
                      JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
                 JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
            JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
       JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
   LEFT JOIN c_doctype dt ON cc.c_doctypetarget_id = dt.c_doctype_id
  WHERE date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) AND hdr.isactive = 'Y'::bpchar AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL)
                UNION ALL 
                         SELECT 'CAI' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 'CC' AS tendertype, i.documentno, i.description, NULL::unknown AS info, (i.grandtotal - COALESCE(cobros.amount, 0::numeric))::numeric(20,2) AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, NULL::unknown AS c_pospaymentmedium_id, NULL::unknown AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct
                           FROM c_invoice i
                      JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
                 LEFT JOIN ( SELECT c.c_invoice_id, sum(c.amount) AS amount
                              FROM ( SELECT 
                                           CASE
                                               WHEN (dt.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                                               ELSE i.c_invoice_id
                                           END AS c_invoice_id, pjp.amount
                                      FROM c_posjournalpayments_v pjp
                                 JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
                            JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
                       JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
                  JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
             JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
        LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
  WHERE date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) AND hdr.isactive = 'Y'::bpchar) c
                             GROUP BY c.c_invoice_id) cobros ON cobros.c_invoice_id = i.c_invoice_id
            JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
       JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE (cobros.amount IS NULL OR i.grandtotal <> cobros.amount) AND (dt.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])))
        UNION ALL 
                 SELECT 'I' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, dt.docbasetype AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, dt.c_doctype_id AS c_pospaymentmedium_id, dt.name AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct
                   FROM c_invoice i
              JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
         JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
    JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id)
UNION ALL 
         SELECT 'PCA' AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
                CASE
                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                    ELSE i.c_invoice_id
                END AS c_invoice_id, pjp.allocationdate AS datetrx, pjp.c_payment_id, pjp.c_cashline_id, 
                CASE
                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN i.c_invoice_id
                    ELSE pjp.c_invoice_credit_id
                END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, pjp.amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
                CASE
                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.c_doctype_id
                    WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.c_doctype_id
                    WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.c_pospaymentmedium_id
                    ELSE p.c_pospaymentmedium_id
                END AS c_pospaymentmedium_id, 
                CASE
                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.docbasetype::character varying
                    WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.docbasetype::character varying
                    WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.name
                    ELSE ppm.name
                END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct
           FROM c_posjournalpayments_v pjp
      JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
   JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
   LEFT JOIN c_doctype dt ON cc.c_doctypetarget_id = dt.c_doctype_id
  WHERE date_trunc('day'::text, i.dateacct) <> date_trunc('day'::text, pjp.dateacct::timestamp with time zone) AND hdr.isactive = 'Y'::bpchar AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL);

ALTER TABLE v_dailysales OWNER TO libertya;

-- 20121119-0130 Incorporación de nuevas columnas a importar en la importación de Entidades Comerciales
update ad_system set dummy = (SELECT addcolumnifnotexists('I_BPartner','TaxIdType', 'character varying(2)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('I_BPartner','ContactOrg', 'character varying(60)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('I_BPartner','AD_Org_Contact_ID', 'integer'));

-- 20121119-1605 Incorporación de nuevas columnas en la importación de Artículos
update ad_system set dummy = (SELECT addcolumnifnotexists('I_Product','ContactProduct', 'character varying(60)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('I_Product','AD_Org_Product_ID', 'integer'));

-- 20121122-1234 Incorporación de nueva columna a la tabla de Usuarios para identificar aquellos que loguean al sistema. Actualización de flag usuario de sistema para aquellos usuarios que poseen al menos un perfil asociado, si tienen un perfil configurado, entonces ingresan al sistema
update ad_system set dummy = (SELECT addcolumnifnotexists('AD_User','isSystemAccess', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));

UPDATE ad_user u
SET issystemaccess = 'Y'
WHERE EXISTS (SELECT ur.ad_user_id
		FROM ad_user_roles as ur
		WHERE u.ad_user_id = ur.ad_user_id);
		
-- 20121122-1847 Nueva vista para reporte de Existencias por Artículos	
CREATE OR REPLACE VIEW rv_storage_product AS 
 SELECT ad_client_id, 
	ad_org_id, 
	m_product_id, 
	value, 
	name, 
	m_product_category_id, 
	m_warehouse_id,
	sum(qtyonhand) as qtyonhand, 
	sum(qtyreserved) as qtyreserved, 
	sum(qtyavailable) as qtyavailable, 
	sum(qtyordered) as qtyordered
 FROM (SELECT s.ad_client_id, 
		s.ad_org_id, 
		s.m_product_id, 
		p.value, 
		p.name, 
		p.m_product_category_id, 
		l.m_warehouse_id, 
		s.qtyonhand, 
		s.qtyreserved, 
		s.qtyonhand - s.qtyreserved AS qtyavailable, 
		s.qtyordered
   FROM m_storage s
   JOIN m_locator l ON s.m_locator_id = l.m_locator_id
   JOIN m_product p ON s.m_product_id = p.m_product_id) as s
   GROUP BY ad_client_id, 
	ad_org_id, 
	m_product_id, 
	value, 
	name, 
	m_product_category_id, 
	m_warehouse_id;
ALTER TABLE rv_storage_product OWNER TO libertya;

--20121203-2107 Fix a función to_days por actualización de librerías de cliente postgresql
CREATE OR REPLACE FUNCTION to_days(timestamp without time zone)
  RETURNS integer AS
$BODY$SELECT DATE_PART('DAY', $1 - '0001-01-01bc')::integer AS result$BODY$
  LANGUAGE 'sql' VOLATILE;
ALTER FUNCTION to_days(timestamp without time zone) OWNER TO libertya;

--20121204-2306 Nueva columna que permite modificar las tarifas en los documentos de pedidos
update ad_system set dummy = (SELECT addcolumnifnotexists('c_doctype','allowchangepricelist', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));

--20121204-2315 Nueva columna que permite filtrar tipos de documento de pedidos en el Crear Desde de Remitos
update ad_system set dummy = (SELECT addcolumnifnotexists('c_doctype','enableincreatefromshipment', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));

UPDATE c_doctype
SET enableincreatefromshipment = 'Y'
WHERE doctypekey IN ('POO','SOSO', 'SOSOTD');

--20121209-2100 Nueva columna que permite marcar la Tarifa con percepciones incluidas en el precio
ALTER TABLE M_PriceList ADD COLUMN isperceptionsincluded character(1) NOT NULL DEFAULT 'N'::bpchar;

--20121209-2100 Incorporación de columna isperceptionsincluded a la tabla C_InvoiceTax
ALTER TABLE C_InvoiceTax ADD COLUMN isperceptionsincluded character(1) NOT NULL DEFAULT 'N'::bpchar;

--20121209-2100 Incorporación de columna linenetamount a la tabla C_InvoiceLine
update ad_system set dummy = (SELECT addcolumnifnotexists('c_invoiceline','linenetamount', 'numeric(22,2) NOT NULL DEFAULT 0'));

-- 20121209-2215 Incorporación de columna linenetamount a la tabla C_InvoiceLine
update ad_system set dummy = (SELECT addcolumnifnotexists('I_Padron_Sujeto','padrontype', 'character(1)'));

--20121211-1702 Nueva columna que permite configurar a las organizaciones para incluirlas en el proceso de generación de imputaciones automáticas 
update ad_system set dummy = (SELECT addcolumnifnotexists('ad_orginfo','allowautomaticallocation', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));

--20121226-2020 Se elimina el índice de cuit único en la tabla de padrón ya que pueden existir cuits repetidos
DROP INDEX c_bpartner_padron_bsas_cuit;

--20121227-1740 Correcciones y mejoras en replicacion
ALTER TABLE AD_Changelog_Replication ADD COLUMN includeInReplication character(1) not null DEFAULT 'N';
CREATE INDEX AD_Changelog_Replication_includeInReplication ON AD_Changelog_Replication (includeInReplication) WHERE includeInReplication = 'Y';

CREATE OR REPLACE FUNCTION replication_event()
  RETURNS trigger AS
$BODY$
DECLARE 
	found integer; 
	replicationPos integer;
	v_newRepArray varchar; 
	aKeyColumn varchar;
	repSeq bigint;
	shouldReplicate varchar;
BEGIN 
	
	IF (TG_OP = 'DELETE') THEN
		
		SELECT INTO shouldReplicate VALUE FROM AD_PREFERENCE WHERE ATTRIBUTE = 'ReplicationEventsActive';
		IF (shouldReplicate <> 'Y') THEN
			RETURN OLD;
		END IF;
		
		IF (OLD.repArray IS NULL OR OLD.repArray = '') THEN
			RETURN OLD;
		END IF;
		
		IF replication_is_record_replicated(OLD.repArray) = 1 THEN
			
			SELECT INTO v_newRepArray replicationArray 
			FROM ad_tablereplication 
			WHERE ad_table_ID = TG_ARGV[0]::int;

			v_newRepArray := replace(v_newRepArray, '3', '1');
			
			IF v_newRepArray IS NOT NULL AND v_newRepArray <> '' THEN
				INSERT INTO ad_changelog_replication (AD_Changelog_Replication_ID, AD_Client_ID, AD_Org_ID, isActive, Created, CreatedBy, Updated, UpdatedBy, AD_Table_ID, retrieveUID, operationtype, binaryvalue, reparray, columnvalues, includeInReplication)
				SELECT nextval('seq_ad_changelog_replication'),OLD.AD_Client_ID,OLD.AD_Org_ID,'Y',now(),OLD.CreatedBy,now(),OLD.UpdatedBy,TG_ARGV[0]::int,OLD.retrieveUID,'I',null,v_newRepArray,null,'Y';
			END IF;
		END IF;	
		
		RETURN OLD;
	END IF;
	
	IF (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') THEN
		
		SELECT INTO shouldReplicate VALUE FROM AD_PREFERENCE WHERE ATTRIBUTE = 'ReplicationEventsActive';
		IF (shouldReplicate <> 'Y') THEN
			RETURN NEW;
		END IF;

		 IF (NEW.repArray = 'SKIP') THEN
			NEW.repArray := NULL;
			return NEW;
		 END IF;
		 
		 IF NEW.retrieveUID IS NULL OR NEW.retrieveUID = '' THEN 
			SELECT INTO replicationPos replicationArrayPos FROM AD_ReplicationHost WHERE thisHost = 'Y'; 
			IF replicationPos IS NULL THEN RAISE EXCEPTION 'Configuracion de Hosts incompleta: Ninguna sucursal tiene marca de Este Host'; END IF; 
			
			SELECT INTO repseq nextVal('repseq_' || TG_ARGV[1]);
			IF repseq IS NULL THEN RAISE EXCEPTION 'No hay definida una secuencia de replicacion para la tabla %', TG_ARGV[1]; END IF;
			NEW.retrieveUID := 'h'::varchar || replicationPos::varchar || '_' || repseq || '_' || lower(TG_ARGV[1]);
		END IF;		
		
		IF (TG_OP = 'INSERT') THEN
			
			IF (substr(NEW.repArray, 1, 3) = 'SET') THEN
				NEW.repArray := substr(NEW.repArray, 4, length(NEW.repArray)-3);
			ELSE

				SELECT INTO v_newRepArray replicationArray 
				FROM ad_tablereplication 
				WHERE ad_table_ID = TG_ARGV[0]::int;
			
				IF v_newRepArray IS NULL OR v_newRepArray = '' THEN
					RETURN NEW;
				END IF;

				NEW.repArray := replace(v_newRepArray, '3', '1');
				NEW.repArray := replace(NEW.repArray, '2', '0');
				IF (position('1' in NEW.repArray) > 0) THEN
					NEW.includeInReplication = 'Y';
				ELSE
					NEW.repArray := NULL;
				END IF;
			END IF;
			RETURN NEW;
		
		ELSEIF (TG_OP = 'UPDATE') THEN 
		
				 IF (substr(NEW.repArray, 1, 3) = 'SET') THEN
					NEW.repArray := substr(NEW.repArray, 4, length(NEW.repArray)-3);
				ELSE
					v_newRepArray := replace(OLD.repArray, '2', '3');
					NEW.repArray := replace(v_newRepArray, '4', '5');
					IF (position('3' in NEW.repArray) > 0) THEN
						NEW.includeInReplication = 'Y';
					END IF;
				END IF;
				RETURN NEW;
		END IF;
	END IF;
END; 
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION replication_event() OWNER TO libertya;

--20130104-1247 Nueva columna que permite referenciar a los pagos originales. Muchas veces los pagos son copia de otros, por ejemplo en cheques de terceros. 
update ad_system set dummy = (SELECT addcolumnifnotexists('c_payment','original_ref_payment_id', 'integer'));

--20130115-0922 Nuevos cambios en logica de replicacion
CREATE OR REPLACE FUNCTION replication_is_valid_reference(p_columnid integer, column_data character varying)
  RETURNS integer AS
$BODY$
DECLARE
targetTableName varchar;
targetTableID int;
sourceTableID int;
isValid int;
colName varchar;
hostID int;
sourceRepArray varchar;
targetRepArray varchar;
viewTable varchar;
BEGIN

	-- En caso de que no presente un dato, entonces omitir cualquier validación
	IF column_data IS NULL THEN
		RETURN 1;
	END IF;

	-- si la columna es AD_Language, omitir cualquier tipo de validacion
	SELECT INTO colName columnname FROM AD_Column WHERE AD_Column_ID = p_columnID;
	IF colName = 'AD_Language' THEN
		RETURN 1;   
	END IF;
   
	-- ver si el campo es una referencia
	select into targetTableName replication_get_referenced_table(p_columnID);

	-- si es una referencia, verificar el bitacoreo en la tabla referenciada
	IF targetTableName != '' THEN
       
	-- verificar si la tabla destino es simplemente una vista
	select INTO viewTable isview from ad_table where tablename = targetTableName;
	IF viewTable = 'Y' THEN
		return 1;
	end if;
		
	-- si el valor es 0, entonces es una referencia valida (seria como null para LY)
	IF column_data = '0' THEN
		return 1;
	END IF;

        -- recuperar el identificador de la tabla destino
        SELECT into targetTableID AD_Table_ID FROM AD_Table WHERE upper(tablename) = upper(targetTableName);
        -- recuperar el identificador de la tabla origen
        SELECT into sourceTableID AD_Table_ID FROM AD_Column WHERE AD_Column_ID = p_columnid;
   
        -- ver si la tabla destino es bitacoreada
        SELECT INTO hostID replicationarraypos FROM AD_ReplicationHost WHERE thisHost = 'Y';
        IF hostID IS NULL THEN RAISE EXCEPTION 'Configuracion de Hosts incompleta: Ninguna sucursal tiene marca de Este Host'; END IF;

        -- si la tabla destino directamente no tiene la columna retrieveuid, entonces devolver que es invalido
        SELECT INTO isValid count(1) FROM information_schema.columns
            WHERE lower(column_name) = 'retrieveuid' AND lower(table_name) = lower(targetTableName);
        IF isValid = 0 THEN
            return 0;
        END IF;

	-- comparar el replicationArray de la tabla origen y de la tabla destino:
	-- unicamente si son iguales se podrá replicar, en caso contrario devolver que no
	-- contemplar caso de tablas bidireccionales, aqui solo importa que exista envio hacia el otro host
	-- (reemplazar 3 (bidireccional) por 1 (enviar)
	SELECT INTO sourceRepArray replace(replicationArray, '3', '1') FROM AD_TableReplication where ad_table_id = sourceTableID;
	SELECT INTO targetRepArray replace(replicationArray, '3', '1') FROM AD_TableReplication where ad_table_id = targetTableID;

	-- si los repArray de las tablas origen y destino son iguales, entonces todo bien
	IF sourceRepArray = targetRepArray THEN
		return 1;
	END IF;

	-- si los repArray de las tablas origen y destino son diferentes (y la destino esta marcada para replicar),
	-- entonces no puede referenciarse el registro, ya que existirá en algunos hosts y en otros no.
	IF sourceRepArray <> targetRepArray AND (position('1' in targetRepArray) > 0) THEN
		return 0;
	END IF;

	-- si son diferentes porque se debe a que la tabla destino tiene 
	-- repArray con posiciones de replicación (1),habra que analizar el registro en cuestión:

        -- hay que ver si se esta referenciando a 
		-- 1) un registro ya existente en el core (retrieveuid debe iniciar con o),
		-- 2) o bien a un registro proveniente de otra sucursal (no generado localmente)
        EXECUTE 'select count(1) FROM ' || targetTableName ||
            ' WHERE ' || targetTableName || '_ID = ' || column_data::int ||
            ' AND ( retrieveuid NOT ilike ''h' || hostID::varchar || '_%'' )' INTO isValid;       
       
        return isValid;
    END IF;
   
    return 1;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION replication_is_valid_reference(integer, character varying) OWNER TO libertya;


CREATE OR REPLACE FUNCTION replication_event()
  RETURNS trigger AS
$BODY$
DECLARE 
	found integer; 
	replicationPos integer;
	v_newRepArray varchar; 
	aKeyColumn varchar;
	repSeq bigint;
	shouldReplicate varchar;
	recordColumns RECORD;
	columnValue varchar;
	isValid integer;
	v_valueDetail varchar;
	v_nameDetail varchar;
	v_columnname varchar;
        v_tableid int; 
        v_tablename varchar;
        v_referenceStr varchar;
	v_referencedTableStr varchar;
	v_referencedTableID int;
	v_existsValueField int;
	v_existsNameField int;
	shouldcheckreferences boolean;
BEGIN 
	-- se deberan verificar referencias a registros fuera del esquema de replicacion? (inicialmente no)
	shouldcheckreferences := false;

	-- estamos en una accion de eliminacion?
	IF (TG_OP = 'DELETE') THEN

		-- Checkear switch maestro de replicacion
		SELECT INTO shouldReplicate VALUE FROM AD_PREFERENCE WHERE ATTRIBUTE = 'ReplicationEventsActive';
		IF (shouldReplicate <> 'Y') THEN
			RETURN OLD;
		END IF;

		-- Se repArray es nulo o vacio, no hay mas que hacer dado que es un registro fuera de replicacion
		IF (OLD.repArray IS NULL OR OLD.repArray = '') THEN
			RETURN OLD;
		END IF;

		-- El registro fue replicado? Si no lo fue puede ser eliminado, pero en caso contrario hay que registrar su eliminacion
		IF replication_is_record_replicated(OLD.repArray) = 1 THEN

			-- Recuperar el repArray de la tabla en cuestion
			SELECT INTO v_newRepArray replicationArray 
			FROM ad_tablereplication 
			WHERE ad_table_ID = TG_ARGV[0]::int;

			-- Cambiar 3 (replicacion bidireccional) por 1 (enviar); y 2 (recibir) por 0 (sin accion)
			v_newRepArray := replace(v_newRepArray, '3', '1');
			v_newRepArray := replace(v_newRepArray, '2', '0');

			-- Insertar en la tabla de eliminaciones
			IF v_newRepArray IS NOT NULL AND v_newRepArray <> '' THEN
				INSERT INTO ad_changelog_replication (AD_Changelog_Replication_ID, AD_Client_ID, AD_Org_ID, isActive, Created, CreatedBy, Updated, UpdatedBy, AD_Table_ID, retrieveUID, operationtype, binaryvalue, reparray, columnvalues, includeInReplication)
				SELECT nextval('seq_ad_changelog_replication'),OLD.AD_Client_ID,OLD.AD_Org_ID,'Y',now(),OLD.CreatedBy,now(),OLD.UpdatedBy,TG_ARGV[0]::int,OLD.retrieveUID,'I',null,v_newRepArray,null,'Y';
			END IF;
		END IF;	
		
		RETURN OLD;
	END IF;

	-- estamos en una accion de insercion o actualizacion?
	IF (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') THEN

		-- Checkear switch maestro de replicacion		
		SELECT INTO shouldReplicate VALUE FROM AD_PREFERENCE WHERE ATTRIBUTE = 'ReplicationEventsActive';
		IF (shouldReplicate <> 'Y') THEN
			RETURN NEW;
		END IF;

		-- El uso de SKIP se supone para omitir acciones posteriores en eliminacion (dado que setea repArray en NULL)
		IF (NEW.repArray = 'SKIP') THEN
			NEW.repArray := NULL;
			return NEW;
		END IF;

		-- Verificar si hay que generar el retrieveUID. Ejemplo: h1_291
		IF NEW.retrieveUID IS NULL OR NEW.retrieveUID = '' THEN 
			-- Obtener posicion de host
			SELECT INTO replicationPos replicationArrayPos FROM AD_ReplicationHost WHERE thisHost = 'Y'; 
			IF replicationPos IS NULL THEN RAISE EXCEPTION 'Configuracion de Hosts incompleta: Ninguna sucursal tiene marca de Este Host'; END IF; 
			-- Obtener siguiente valor para la tabla dada
			SELECT INTO repseq nextVal('repseq_' || TG_ARGV[1]);
			IF repseq IS NULL THEN RAISE EXCEPTION 'No hay definida una secuencia de replicacion para la tabla %', TG_ARGV[1]; END IF;
			NEW.retrieveUID := 'h'::varchar || replicationPos::varchar || '_' || repseq || '_' || lower(TG_ARGV[1]);
		END IF;		

		-- Si estamos insertando...
		IF (TG_OP = 'INSERT') THEN

			-- Si se indico el repArray con SET, entonces se esta configurando el registro manualmente.  No hacer nada mas.
			IF (substr(NEW.repArray, 1, 3) = 'SET') THEN
				NEW.repArray := substr(NEW.repArray, 4, length(NEW.repArray)-3);
			ELSE
				-- Recuperar el repArray
				SELECT INTO v_newRepArray replicationArray 
				FROM ad_tablereplication 
				WHERE ad_table_ID = TG_ARGV[0]::int;

				-- Si es nulo o vacio no hacer nada mas
				IF v_newRepArray IS NULL OR v_newRepArray = '' THEN
					RETURN NEW;
				END IF;

				-- Cambiar 3 (replicacion bidireccional) por 1 (enviar); y 2 (recibir) por 0 (sin accion)
				NEW.repArray := replace(v_newRepArray, '3', '1');
				NEW.repArray := replace(NEW.repArray, '2', '0');
				-- Si el registro deberá replicar hacia otros hosts (hay al menos un 1)
				-- entonces debe incluirse en replicacion y hay que check referencias
				IF (position('1' in NEW.repArray) > 0) THEN
					NEW.includeInReplication = 'Y';
					shouldcheckreferences := true;
				ELSE
					NEW.repArray := NULL;
				END IF;
			END IF;
			
		-- Si estamos actualizando...
		ELSEIF (TG_OP = 'UPDATE') THEN 

			-- Si se indico el repArray con SET, entonces se esta configurando el registro manualmente.  No hacer nada mas.		
			IF (substr(NEW.repArray, 1, 3) = 'SET') THEN
				NEW.repArray := substr(NEW.repArray, 4, length(NEW.repArray)-3);
			ELSE
				-- Cambiar los 2 (replicado) por 3 (modificado).
				-- Adicionalmente para JMS: 4 (espera ack) por 5 (cambios luego de ack)
				v_newRepArray := replace(OLD.repArray, '2', '3');
				NEW.repArray := replace(v_newRepArray, '4', '5');
				-- Si el registro deberá replicar hacia otros hosts (hay al menos un 3)
				-- entonces debe incluirse en replicacion y hay que check referencias
				IF (position('3' in NEW.repArray) > 0) THEN
					NEW.includeInReplication = 'Y';
					shouldcheckreferences := true;
				END IF;
			END IF;
		END IF;
	END IF;

	IF (shouldcheckreferences = true) THEN

		-- Validar referencias iterando todas las columnas de la tabla
		FOR recordColumns IN
			SELECT isc.column_name, isc.data_type, c.ad_column_id, t.tablename, t.ad_table_id
			FROM information_schema.columns isc
			INNER JOIN ad_table t ON lower(isc.table_name) = lower(t.tablename)
			INNER JOIN ad_column c ON lower(isc.column_name) = lower(c.columnname) AND t.ad_table_id = c.ad_table_id
			WHERE table_name = quote_ident(TG_TABLE_NAME)
			AND isc.data_type = 'integer'
			AND isc.column_name not in ('retrieveuid', 'reparray', 'datelastsentjms', 'includeinreplication')
		LOOP
			-- Obtener el value de la columna y verificar si es referencia valida.  En caso de no serlo, presentar error correspondiente
			EXECUTE 'SELECT (' || quote_literal(NEW) || '::' || TG_RELID::regclass || ').' || quote_ident(recordColumns.column_name) INTO columnValue;
			SELECT INTO isValid replication_is_valid_reference(recordColumns.ad_column_id, columnvalue);
			IF isValid = 0 THEN

				-- valores por defecto
				v_valueDetail := '';
				v_nameDetail := '';
				v_referenceStr := '';
				
				-- recuperar el nombre de la columna y tabla para brindar un mensaje mas intuitivo al usuario
				v_columnname := recordColumns.column_name;
				v_tablename := recordColumns.tablename;

				BEGIN 
					-- recuperar el nombre o value del registro referenciado a fin de mejorar la legibilidad del mensaje de error
					SELECT INTO v_referencedTableStr replication_get_referenced_table(recordColumns.ad_column_id);
					SELECT INTO v_referencedTableID AD_Table_ID FROM AD_Table WHERE tablename ilike v_referencedTableStr;

					-- ver si existe las columnas value y name
					SELECT INTO v_existsValueField Count(1) FROM AD_Column WHERE columnname ilike 'Value' AND AD_Table_ID = v_referencedTableID;
					SELECT INTO v_existsNameField Count(1) FROM AD_Column WHERE columnname ilike 'Name' AND AD_Table_ID = v_referencedTableID;

					-- cargar value y name
					IF v_existsValueField = 1 THEN
						EXECUTE 'SELECT value FROM ' || v_referencedTableStr || ' WHERE ' || v_referencedTableStr || '_ID = ' || columnvalue || '::int' INTO v_valueDetail;
						v_referenceStr := v_valueDetail;
					END IF;
					IF v_existsNameField = 1 THEN
						EXECUTE 'SELECT name FROM ' || v_referencedTableStr || ' WHERE ' || v_referencedTableStr || '_ID = ' || columnvalue || '::int' INTO v_nameDetail;
						v_referenceStr := v_referenceStr || ' ' || v_nameDetail;
					END IF;
				EXCEPTION
					WHEN OTHERS THEN
						-- do nothing
				END;
				
				-- concatenar acordemente para mensaje de retorno
				RAISE EXCEPTION 'Validacion de replicación - La columna: % (%) de la tabla: % (%) referencia al registro: % (%), fuera del sistema de replicacion.', v_columnname, recordColumns.ad_column_id, v_tablename, recordColumns.ad_table_id, v_referenceStr, columnvalue;
			END IF;
		END LOOP;

	END IF;
	RETURN NEW;
END; 
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION replication_event() OWNER TO libertya;

-- Nuevas columnas en AD_Role: Permiso para utilizar ventanas INFO
ALTER TABLE ad_role ADD COLUMN isinfobpartneraccess character(1) NOT NULL DEFAULT 'Y'::bpchar;
ALTER TABLE ad_role ADD COLUMN isinfoscheduleaccess character(1) NOT NULL DEFAULT 'Y'::bpchar;
ALTER TABLE ad_role ADD COLUMN isinfoorderaccess character(1) NOT NULL DEFAULT 'Y'::bpchar;
ALTER TABLE ad_role ADD COLUMN isinfoinvoiceaccess character(1) NOT NULL DEFAULT 'Y'::bpchar;
ALTER TABLE ad_role ADD COLUMN isinfoinoutaccess character(1) NOT NULL DEFAULT 'Y'::bpchar;
ALTER TABLE ad_role ADD COLUMN isinfopaymentaccess character(1) NOT NULL DEFAULT 'Y'::bpchar;
ALTER TABLE ad_role ADD COLUMN isinfocashlineaccess character(1) NOT NULL DEFAULT 'Y'::bpchar;
ALTER TABLE ad_role ADD COLUMN isinfoassignmentaccess character(1) NOT NULL DEFAULT 'Y'::bpchar;
ALTER TABLE ad_role ADD COLUMN isinfoassetaccess character(1) NOT NULL DEFAULT 'Y'::bpchar;

-- Versionado de BBDD
UPDATE ad_system SET version = '21-01-2013' WHERE ad_system_id = 0;

-- Compatibilidad de UID para este registro entre version 11.10 y 13.01
UPDATE AD_TREENODEMM set ad_componentobjectuid = 'CORE-AD_TreeNodeMM-1010114-203' where ad_componentobjectuid = 'CORE-AD_TreeNodeMM-203-1010114';