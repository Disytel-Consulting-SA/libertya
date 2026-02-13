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
