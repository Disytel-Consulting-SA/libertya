CREATE OR REPLACE  VIEW RV_MPC_Order AS 
  SELECT o.AD_Client_ID, o.AD_Org_ID,o.IsActive,o.Created, o.CreatedBy, o.Updated, o.UpdatedBy,
o.MPC_Order_ID, o.DocumentNo,o.DocStatus,o.M_Warehouse_ID,o.M_Product_ID,o.QtyEntered,
o.QtyReject,o.QtyScrap,o.QtyBatchs, o.QtyBatchSize,o.DateOrdered, o.DatePromised,o.DateStart,
o.DateStartSchedule,o.DateFinish,o.DateFinishSchedule,o.DateConfirm,o.DateDelivered,o.Lot,
o.MPC_Product_BOM_ID,o.AD_Workflow_ID, (select p.Weight from M_Product p where p.M_Product_ID=o.M_Product_ID) as Weight
FROM MPC_Order o;

CREATE OR REPLACE  VIEW RV_MPC_Order_BOMLine AS 
SELECT
obl.AD_Client_ID ,
obl.AD_Org_ID ,
obl.CreatedBy ,
obl.UpdatedBY ,
obl.Updated ,
obl.Created ,
obl.IsActive ,
obl.MPC_Order_BOM_ID,
obl.MPC_Order_BOMLine_ID  ,
obl.MPC_Order_ID ,
obl.IsCritical ,
obl.ComponentType,
obl.M_Product_ID  ,
obl.C_UOM_ID  ,
ROUND(obl.QtyRequiered ,4) AS QtyRequiered ,
ROUND(BOMQtyReserved(obl.M_Product_ID,obl.M_Warehouse_ID,0), 4) AS QtyReserved ,
ROUND(BOMQtyAvailable(obl.M_Product_ID,obl.M_Warehouse_ID,0),4) AS QtyAvailable ,
ROUND(BOMQtyOnHand(obl.M_Product_ID,obl.M_Warehouse_ID,0),4) AS QtyOnHand  ,
obl.M_Warehouse_ID  ,
ROUND(obl.QtyBom,4) AS QtyBom,
obl.isQtyPercentage ,
ROUND(obl.QtyBatch,4) AS QtyBatch,
CASE WHEN  o.QtyBatchs = 0 THEN 1 ELSE  ROUND( obl.QtyRequiered / o.QtyBatchs, 4) END AS QtyBatchSize 
--DECODE(o.QtyBatchs , 0 , 0 ,  ROUND( obl.QtyRequiered / o.QtyBatchs, 4) ) AS QtyBatchSize
FROM MPC_Order_BOMLine obl INNER JOIN MPC_Order o ON (o.MPC_Order_ID = obl.MPC_Order_ID);


CREATE OR REPLACE  VIEW RV_MPC_Order_Storage AS 
SELECT
obl.AD_Client_ID ,
obl.AD_Org_ID ,
obl.CreatedBy ,
obl.UpdatedBY ,
obl.Updated ,
obl.Created ,
obl.IsActive ,
obl.MPC_Order_BOM_ID,
obl.MPC_Order_BOMLine_ID  ,
obl.MPC_Order_ID ,
obl.IsCritical ,
obl.M_Product_ID  ,
(Select p.Name FROM M_Product p Where p.M_Product_ID=o.M_Product_ID) as Name,
obl.C_UOM_ID  ,
s.QtyOnhand ,
ROUND(obl.QtyRequiered ,4) AS QtyRequiered ,
CASE  WHEN o.QtyBatchs = 0 THEN 1 ELSE ROUND( obl.QtyRequiered / o.QtyBatchs, 4) END AS QtyBatchSize,
--DECODE(o.QtyBatchs , 0 , 0 ,  ROUND( obl.QtyRequiered / o.QtyBatchs, 4)  ) AS QtyBatchSize,
ROUND(BOMQtyReserved(obl.M_Product_ID,obl.M_Warehouse_ID,0), 4) AS QtyReserved ,
ROUND(BOMQtyAvailable(obl.M_Product_ID,obl.M_Warehouse_ID,0),4) AS QtyAvailable ,
obl.M_Warehouse_ID  ,
obl.QtyBom ,
obl.isQtyPercentage ,
ROUND(obl.QtyBatch,4) AS QtyBatch ,
l.M_Locator_ID ,
l.x ,
l.y ,
l.z 
FROM MPC_Order_BOMLine obl INNER JOIN MPC_Order o ON (o.MPC_Order_ID = obl.MPC_Order_ID)  LEFT OUTER  JOIN M_Storage s ON ( s.M_Product_ID = obl.M_Product_ID AND s.QtyOnHand <> 0 AND  obl.M_Warehouse_ID = (SELECT ld.M_Warehouse_ID FROM M_Locator ld WHERE  s.M_Locator_ID=ld.M_Locator_ID))   LEFT OUTER JOIN M_Locator l ON (l.M_Locator_ID =  s.M_Locator_ID)  ORDER BY obl.M_Product_ID 
;

CREATE OR REPLACE  VIEW RV_MPC_Order_Transactions AS 
SELECT DISTINCT o.AD_Client_ID,o.AD_Org_ID, o.IsActive, o.Created, o.CreatedBy, o.UpdatedBy, o.Updated,o.DocumentNo,ol.M_Product_ID, mt.M_Locator_ID,mt.MovementDate,o.MPC_Order_ID,
o.QtyDelivered, o.QtyScrap ,ol.QtyDelivered AS QtyDeliveredLine , (o.QtyDelivered  * ol.QtyBatch)/100 AS QtyIssueShouldbe,
ol.QtyScrap AS QtyScrapLine , (o.QtyScrap  * ol.QtyBatch)/100 AS QtyIssueScrapShouldBe , mt.CreatedBy AS CreatedByIssue, mt.UpdatedBy AS UpdatedByIssue,
(SELECT SUM(t.MovementQty) FROM M_Transaction t WHERE t.MPC_Order_BOMLine_ID=ol.MPC_Order_BOMLine_ID) AS QtyToDeliver,
((((o.QtyDelivered + o.QtyScrap) * ol.QtyBatch)/100) + (SELECT SUM(t.MovementQty) FROM M_Transaction t WHERE t.MPC_Order_BOMLine_ID  = ol.MPC_Order_BOMLine_ID)) AS DifferenceQty
FROM MPC_Order o INNER JOIN MPC_Order_BOMLine ol ON (ol.MPC_Order_ID=o.MPC_Order_ID)  LEFT JOIN M_Transaction mt ON( mt.MPC_Order_BOMLine_ID  = ol.MPC_Order_BOMLine_ID)
;
 
--DROP  VIEW RV_MPC_PRODUCT_BOMLINE ;
DROP  VIEW RV_MPC_PRODUCT_BOMLINE ;
CREATE OR REPLACE VIEW RV_MPC_PRODUCT_BOMLINE  AS 
SELECT
t.SeqNo,
t.LevelNo,
t.Levels,
t.AD_Client_ID ,
t.AD_Org_ID ,
t.CreatedBy ,
t.UpdatedBy ,
t.Updated ,
t.Created ,
t.AD_PInstance_ID,
bl.IsActive ,
bl.MPC_Product_BOM_ID,
bl.MPC_Product_BOMLine_ID  ,
bl.Description,
bl.IsCritical ,
bl.ComponentType,
t.M_Product_ID  ,
bl.C_UOM_ID ,
bl.IssueMethod,
bl.Line,
bl.M_AttributeSetInstance_ID,
bl.Scrap,
bl.ValidFrom,
bl.ValidTo,
bl.QtyBom,
bl.QtyBatch,
bl.isQtyPercentage 
FROM  MPC_Product_BOMLine bl  RIGHT  JOIN  T_BOMLine t ON (t.MPC_Product_BOMLine_ID = bl.MPC_Product_BOMLine_ID) ORDER BY SeqNo ;