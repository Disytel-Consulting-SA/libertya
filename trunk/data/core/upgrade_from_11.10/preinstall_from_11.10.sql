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
ALTER TABLE T_BalanceReport ADD COLUMN truedatetrx timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) WITH time zone;

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
