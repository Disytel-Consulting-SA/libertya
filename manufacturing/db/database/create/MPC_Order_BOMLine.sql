/*************************************************************************
 * The contents of this file are subject to the Compiere License.  You may
 * obtain a copy of the License at    http://www.compiere.org/license.html 
 * Software is on an  "AS IS" basis,  WITHOUT WARRANTY OF ANY KIND, either 
 * express or implied. See the License for details. Code: Compiere ERP+CPM
 * Copyright (C) 1999-2004 Jorg Janke, ComPiere, Inc. 
 * Copyright (C) 2004 Victor Pérez, e-Evolution, S.C.
 * All Rights Reserved.
 * Contributor(s): Victor Pérez, e-Evolution, S.C.
 *************************************************************************
 * $Id: MPC_Order_BOMLine.sql,v 1.1 2004/02/11 19:00:56 vpj-cd Exp $
 ***
 * Title:	MPC_Order_BOMLine
 * Description:
 ************************************************************************/
DROP TABLE MPC_Order_BOMLine CASCADE CONSTRAINTS;
CREATE TABLE MPC_Order_BOMLine
(
    MPC_Order_BOMLine_ID             NUMBER   (10)                   NOT NULL
  , MPC_Order_ID                     NUMBER   (10)                   NOT NULL
  , MPC_Order_BOM_ID                 NUMBER   (10)                   NOT NULL
  , AD_Client_ID                     NUMBER   (10)                   NOT NULL
  , AD_Org_ID                        NUMBER   (10)                   NOT NULL  
  , Assay                            NUMBER
  , BackflushGroup		     NVARCHAR2 (30) 
  , QtyBOM                           NUMBER                          DEFAULT 0 NOT NULL 
  , QtyRequiered		     NUMBER 		             DEFAULT 0 NOT NULL
  , QtyDelivered		     NUMBER 		             DEFAULT 0 NOT NULL
  , QtyReserved                      NUMBER 		             DEFAULT 0 NOT NULL
  , QtyReject			     NUMBER 	                     DEFAULT 0 NOT NULL
  , QtyScrap                         NUMBER                          DEFAULT 0 NOT NULL
  , QtyPost			     NUMBER 		             DEFAULT 0 NOT NULL 
  , QtyBatch                         NUMBER                          DEFAULT 0 NOT NULL
  , DeliverTo			     NUMBER   (10)		    
  , DateDelivered		     DATE		               
  , M_Warehouse_ID                   NUMBER    (10)                  NOT NULL   
  , M_locator_ID                     NUMBER    (10)                      
  , IsQtyPercentage                  CHAR     (1)                    DEFAULT ('N') NOT NULL
  , IsByProduct                      CHAR      (1)                   DEFAULT 'Y' NOT NULL
  , C_UOM_ID                         NUMBER    (10)                  NOT NULL
  , Created                          DATE                            DEFAULT SYSDATE NOT NULL
  , CreatedBy                        NUMBER    (10)                  NOT NULL
  , IsCritical                       CHAR      (1)                   DEFAULT ('N') NOT NULL
  , Description                      NVARCHAR2 (510)                  
  , Forecast                         NUMBER                          
  , IssueMethod                      CHAR      (1)
  , IsPhantom                        CHAR      (1)                   DEFAULT ('N') NOT NULL
  , LTOffSet                         NUMBER                          
  , Line                             NUMBER    (10)                  NOT NULL 
  , M_Product_ID                     NUMBER    (10)                  NOT NULL
  , Scrap                            NUMBER                          
  , ValidFrom                        DATE                            NOT NULL
  , ValidTo                          DATE                            NULL                         
  , Updated                          DATE                            DEFAULT SYSDATE NOT NULL
  , updatedBy                        NUMBER    (10)                  NOT NULL  
  , M_AttributeSetInstance_ID        NUMBER(10, 0)
  , IsActive                         CHAR      (1)                   DEFAULT ('Y') NOT NULL
    CHECK (IsActive in ('Y','N')),
    CONSTRAINT MPC_Order_BOMLine_Key PRIMARY KEY (MPC_Order_BOMLine_ID)
);
ALTER TABLE MPC_Order_BOMLine ADD CONSTRAINT ADClientMPCOrderBOMLine 
    FOREIGN KEY (AD_Client_ID)
    REFERENCES AD_Client(AD_Client_ID) ON DELETE CASCADE
;

ALTER TABLE MPC_Order_BOMLine ADD CONSTRAINT ADOrgMPCOrderBOMLine 
    FOREIGN KEY (AD_Org_ID)
    REFERENCES AD_Org(AD_Org_ID) ON DELETE CASCADE
;
--ALTER TABLE MPC_Order_BOMLine ADD CONSTRAINT MPCScheduleMPCOrderBOMLine 
--    FOREIGN KEY (MPC_Schedule_ID)
--    REFERENCES MPC_Schedule(MPC_Schedule_ID) ON DELETE CASCADE
--;

ALTER TABLE MPC_Order_BOMLine ADD CONSTRAINT MPCOrderMPCOrderBOMLine 
    FOREIGN KEY (MPC_Order_ID)
    REFERENCES MPC_Order(MPC_Order_ID) ON DELETE CASCADE
;
ALTER TABLE MPC_Order_BOMLine ADD CONSTRAINT MWarehouseMPCOrderBOMLine 
    FOREIGN KEY (M_Warehouse_ID)
    REFERENCES M_Warehouse(M_Warehouse_ID) ON DELETE CASCADE
;
ALTER TABLE MPC_Order_BOMLine ADD CONSTRAINT MlocatorMPCOrderBOMLine 
    FOREIGN KEY (M_locator_ID)
    REFERENCES M_locator(M_locator_ID) ON DELETE CASCADE
;
ALTER TABLE MPC_Order_BOMLine ADD CONSTRAINT CUOMMPCOrderBOMLine 
    FOREIGN KEY (C_UOM_ID)
    REFERENCES C_UOM(C_UOM_ID) ON DELETE CASCADE
;
ALTER TABLE MPC_Order_BOMLine ADD CONSTRAINT MProductMPCOrderBOMLine 
    FOREIGN KEY (M_Product_ID)
    REFERENCES M_Product(M_Product_ID) ON DELETE CASCADE
;
ALTER TABLE MPC_Order_BOMLine ADD CONSTRAINT MAttrSetInstMPCOrderBOMLine 
    FOREIGN KEY (M_AttributeSetInstance_ID)
    REFERENCES M_AttributeSetInstance(M_AttributeSetInstance_ID) ON DELETE CASCADE
;
ALTER TABLE MPC_Order_BOMLine ADD CONSTRAINT MPCOrderBOMMPCOrderBOMLine 
    FOREIGN KEY (MPC_Order_BOM_ID)
    REFERENCES MPC_Order_BOM(MPC_Order_BOM_ID) ON DELETE CASCADE
;


