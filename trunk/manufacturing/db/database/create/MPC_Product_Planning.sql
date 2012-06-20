/*************************************************************************
 * The contents of this file are subject to the Compiere License.  You may
 * obtain a copy of the License at    http://www.compiere.org/license.html 
 * Software is on an  "AS IS" basis,  WITHOUT WARRANTY OF ANY KIND, either 
 * express or implied. See the License for details. Code: Compiere ERP+CPM
 * Copyright (C) 2003-2003 Victor Pérez, e-Evolution S.C.,Inc. All Rights Reserved.
 *************************************************************************
 * $Id:  MPC_Product_Planig.sql,v 1 2002/10/21 04:49:46 vpj-cd Exp $
 ***
 * Title:	 MP_Product_Costing
 * Description:
 ************************************************************************/
DROP TABLE MPC_Product_Planning  CASCADE CONSTRAINTS;
CREATE TABLE  MPC_Product_Planning
(
    MPC_Product_Planning_ID              NUMBER   (10)                   NOT NULL
  , M_Product_ID                         NUMBER   (10)                   NOT NULL
  , AD_Client_ID                         NUMBER   (10)                   NOT NULL
  , AD_Org_ID                            NUMBER   (10)                   NOT NULL
  , Created                              DATE                            DEFAULT SYSDATE NOT NULL
  , CreatedBy                            NUMBER   (10)                   NOT NULL
  , Updated                              DATE                            DEFAULT SYSDATE NOT NULL
  , UpdatedBy                            NUMBER   (10)                   NOT NULL
  , M_Warehouse_ID		         NUMBER   (10)                   DEFAULT 0
  , S_Resource_ID          	         NUMBER   (10)                   DEFAULT 0
  , IsMPS                                CHAR     (1)                    DEFAULT 'Y' NOT NULL
  , IsCreatePlan				 CHAR     (1)                    DEFAULT 'Y' NOT NULL
  , TimeFence                            NUMBER   (10)                   DEFAULT 0
  , IsRequiredMRP			 CHAR     (1)                    DEFAULT 'Y' NOT NULL
  , OrderPolicy 			 CHAR     (3)                   
  , OrderQty				 NUMBER   (10,0)                 NOT NULL
  , OrderPeriod				 NUMBER   (10)                   DEFAULT 0
  , Planner_ID				 NUMBER   (10)                  
  , MfgLeadTime				 NUMBER   (10)
  , MPC_Product_BOM_ID			 NUMBER   (10)
  , AD_Workflow_ID			 NUMBER   (10)
  , IsIssue				 CHAR     (1)                    DEFAULT 'Y' NOT NULL
  , IsPhantom				 CHAR     (1)                    DEFAULT 'Y' NOT NULL
  , Yield				 NUMBER   (10,0)
  , WorkingTime				 NUMBER   (10,0)
  , IsActive                             CHAR     (1)                    DEFAULT 'Y' NOT NULL
  , CHECK (IsActive in ('Y','N'))
  , CONSTRAINT MPC_Product_Planning_Key PRIMARY KEY (M_Product_ID , S_Resource_ID ,M_Warehouse_ID)                           
);

-- 
-- TABLE:  MPC_Product_Planning
--
ALTER TABLE  MPC_Product_Planning ADD CONSTRAINT ADClientMPCProdductPlanning
    FOREIGN KEY (AD_Client_ID)
    REFERENCES AD_Client(AD_Client_ID)
;

ALTER TABLE  MPC_Product_Planning ADD CONSTRAINT ADOrgMPCProductPlanning
    FOREIGN KEY (AD_Org_ID)
    REFERENCES AD_Org(AD_Org_ID)
;

ALTER TABLE  MPC_Product_Planning ADD CONSTRAINT MProductMPCProductPlanning
    FOREIGN KEY (M_Product_ID)
    REFERENCES M_Product(M_Product_ID)
;

ALTER TABLE  MPC_Product_Planning  ADD CONSTRAINT MWarehouseMPCProductPlanning
    FOREIGN KEY (M_Warehouse_ID)
    REFERENCES M_Warehouse(M_Warehouse_ID)
;
ALTER TABLE  MPC_Product_Planning  ADD CONSTRAINT SResourceMPCProductPlanning
    FOREIGN KEY (S_Resource_ID)
    REFERENCES S_Resource(S_Resource_ID)
;


