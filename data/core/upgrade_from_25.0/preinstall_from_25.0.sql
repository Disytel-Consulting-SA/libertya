-- 20260123 Fix correspondiente al micro de facturacion (metadata faltante)
update libertya.ad_system set dummy = (SELECT libertya.addcolumnifnotexists('C_POSJournal','iscontingencia','character(1)'));

