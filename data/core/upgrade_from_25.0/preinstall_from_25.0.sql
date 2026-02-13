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


