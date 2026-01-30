-- 20260123 Fix correspondiente al micro de facturacion (metadata faltante)
update libertya.ad_system set dummy = (SELECT libertya.addcolumnifnotexists('C_POSJournal','iscontingencia','character(1)'));

-- Fix A-400 Inventario fisico y anulación – Comportamiento incorrecto del inventario físico al utilizar el tipo “Sobrescribir”
ALTER TABLE IF EXISTS libertya.m_inventory ADD inventorytype bpchar(1) DEFAULT 'D'::bpchar NOT NULL;
ALTER TABLE IF EXISTS libertya.m_inventory ALTER COLUMN inventorytype SET STORAGE EXTENDED;


