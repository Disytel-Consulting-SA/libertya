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
ALTER TABLE i_bpartner ADD COLUMN plaza character varying(100);
ALTER TABLE i_bpartner ADD COLUMN address3 character varying(60);
ALTER TABLE i_bpartner ADD COLUMN address4 character varying(60);
ALTER TABLE i_bpartner ADD COLUMN contactphone character varying(40);
ALTER TABLE i_bpartner ADD COLUMN contactphone2 character varying(40);
ALTER TABLE i_bpartner ADD COLUMN contactphone3 character varying(40);
ALTER TABLE i_bpartner ADD COLUMN contactfax character varying(40);
ALTER TABLE i_bpartner ADD COLUMN isdn character varying(40);

-- 20120807-1547 Incorporación de nuevas columnas a importar en la importación de Artículos 
ALTER TABLE i_product ADD COLUMN productfamily_value character varying(60);
ALTER TABLE i_product ADD COLUMN isbom character(1) NOT NULL DEFAULT 'N'::bpchar;

-- 20120808-1545 Inserción de la columna IsMandatoryBank a la tabla C_POSPaymentMedium 
ALTER TABLE C_POSPaymentMedium ADD COLUMN IsMandatoryBank character(1) NOT NULL DEFAULT 'N'::bpchar;
