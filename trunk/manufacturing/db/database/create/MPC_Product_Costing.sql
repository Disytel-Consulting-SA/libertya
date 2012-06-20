/*************************************************************************
 * The contents of this file are subject to the Compiere License.  You may
 * obtain a copy of the License at    http://www.compiere.org/license.html 
 * Software is on an  "AS IS" basis,  WITHOUT WARRANTY OF ANY KIND, either 
 * express or implied. See the License for details. Code: Compiere ERP+CPM
 * Copyright (C) 2003-2003 Victor Pérez, e-Evolution S.C.,Inc. All Rights Reserved.
 *************************************************************************
 * $Id:  MP_Product_Costing.sql,v 1 2002/10/21 04:49:46 vpj-cd Exp $
 ***
 * Title:	 MP_Product_Costing
 * Description:
 ************************************************************************/
DROP TABLE MPC_Product_Costing  CASCADE CONSTRAINTS;
CREATE TABLE  MPC_Product_Costing
(
    C_AcctSchema_ID 		         NUMBER   (10)                   NOT NULL 
  , MPC_Product_Costing_ID               NUMBER   (10)                   NOT NULL
  , MPC_Cost_Element_ID                  NUMBER   (10)                   NOT NULL 
  , M_Product_ID                         NUMBER   (10)                   NOT NULL
  , MPC_Cost_Group_ID                    NUMBER   (10)                   NOT NULL
  , AD_Client_ID                         NUMBER   (10)                   NOT NULL
  , AD_Org_ID                            NUMBER   (10)                   NOT NULL
  , Created                              DATE                            DEFAULT SYSDATE NOT NULL
  , CreatedBy                            NUMBER   (10)                   NOT NULL
  , Updated                              DATE                            DEFAULT SYSDATE NOT NULL
  , UpdatedBy                            NUMBER   (10)                   NOT NULL
  , M_Warehouse_ID		         NUMBER   (10)                   DEFAULT 0
  , S_Resource_ID          	         NUMBER   (10)                   DEFAULT 0
  , CostTLAmt	   	                 NUMBER    	             	     DEFAULT 0
  , CostLLAmt	   	                 NUMBER    	                     DEFAULT 0
  , CostCumQty	   	                 NUMBER    	             	     DEFAULT 0
  , CostCumAmt	   	                 NUMBER    	                     DEFAULT 0
  , IsActive                             CHAR     (1)                    DEFAULT 'Y' NOT NULL
  , CHECK (IsActive in ('Y','N'))
  , CONSTRAINT MP_Product_Costing_Key PRIMARY KEY (M_Product_ID  , C_AcctSchema_ID ,MPC_Cost_Group_ID , MPC_Cost_Element_ID, S_Resource_ID ,M_Warehouse_ID)                           
);

-- 
-- TABLE:  MP_Product_Costing
--
ALTER TABLE  MPC_Product_Costing ADD CONSTRAINT ADClientMPCProdductCosting
    FOREIGN KEY (AD_Client_ID)
    REFERENCES AD_Client(AD_Client_ID)
;

ALTER TABLE  MPC_Product_Costing ADD CONSTRAINT ADOrgMPCProductCosting
    FOREIGN KEY (AD_Org_ID)
    REFERENCES AD_Org(AD_Org_ID)
;
ALTER TABLE  MPC_Product_Costing ADD CONSTRAINT MPCCostEMPCProductCosting
    FOREIGN KEY (MPC_Cost_Element_ID)
    REFERENCES MPC_Cost_Element(MPC_Cost_Element_ID)
;
ALTER TABLE  MPC_Product_Costing ADD CONSTRAINT MProductMPCProductCosting
    FOREIGN KEY (M_Product_ID)
    REFERENCES M_Product(M_Product_ID)
;
ALTER TABLE  MPC_Product_Costing ADD CONSTRAINT CAcctSchemaMPCProductCosting
    FOREIGN KEY (C_AcctSchema_ID)
    REFERENCES C_AcctSchema(C_AcctSchema_ID)
;
ALTER TABLE  MPC_Product_Costing ADD CONSTRAINT MWarehouseMPCProductCosting
    FOREIGN KEY (M_Warehouse_ID)
    REFERENCES M_Warehouse(M_Warehouse_ID)
;
ALTER TABLE  MPC_Product_Costing ADD CONSTRAINT MPCCostGroupMPCProductCosting
    FOREIGN KEY (MPC_Cost_Group_ID)
    REFERENCES MPC_Cost_Group(MPC_Cost_Group_ID)
;

