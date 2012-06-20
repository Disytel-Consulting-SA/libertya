DROP TABLE T_BOMLine;
CREATE TABLE T_BOMLine
(
  AD_PINSTANCE_ID    NUMBER(10,0),
  AD_CLIENT_ID       NUMBER(10,0),
  AD_ORG_ID          NUMBER(10,0),
  CREATED            DATE                       DEFAULT SYSDATE,
  CREATEDBY          NUMBER(10,0),
  UPDATED            DATE                       DEFAULT SYSDATE,
  UPDATEDBY          NUMBER(10,0),
  IsActive               CHAR(1) DEFAULT 'Y',
  SEQNO                  NUMBER(10,0),
  MPC_PRODUCT_BOM_ID NUMBER(10,0),
  MPC_PRODUCT_BOMLine_ID NUMBER(10,0),
  LevelNo                NUMBER(10,0),
  Levels                   VARCHAR(250)
);

CREATE OR REPLACE FORCE VIEW  RV_MPC_Product_BOMLine
AS
SELECT  tboml.AD_Client_ID, 
             tboml.AD_Org_ID, 
             tboml.IsActive,
             tboml.Created, 
             tboml.CreatedBy,
             tboml.Updated,
             tboml.UpdatedBy,
             tboml.LevelNo, 
             tboml.Levels,
             tboml.SeqNo,              
             boml.Line,
             boml.M_Product_ID,
             p.Name,
             p.Value,
             boml.M_AttributeSetInstance_ID, 
             boml.IsQtyPercentage,
             boml.QtyBOM,
             boml.QtyBatch,
             boml.C_UOM_ID,
             boml.ComponentType,
             boml.Description,
             boml.IsCritical,
             boml.IssueMethod,
             boml.Scrap,
             boml.ValidTo,
             boml.ValidFrom,
             tboml.AD_PInstance_ID           
FROM T_BOMLine tboml 
INNER JOIN MPC_Product_BOMLine boml ON boml.MPC_Product_BOMLine_ID = tboml.MPC_Product_BOMLine_ID
INNER JOIN M_Product p ON boml.M_Product_ID=p.M_Product_ID;
/