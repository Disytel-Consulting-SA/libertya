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
 * $Id: MPC_Order_Collector.sql,v 1.1 2004/02/11 19:02:52 vpj-cd Exp $
 ***
 * Title:	 MP_Order_Collector
 * Description:
 ************************************************************************/
DROP TABLE MP_Order_Cost;
CREATE TABLE  MP_Order_Cost
(
    MP_Order_ID			     NUMBER   (10)                   NOT NULL 
  , MP_Cost_Element_ID               NUMBER   (10)                   NOT NULL 
  , M_Product_ID                     NUMBER   (10)                   NOT NULL
  , AD_Client_ID                     NUMBER   (10)                   NOT NULL
  , AD_Org_ID                        NUMBER   (10)                   NOT NULL
  , IsActive                         CHAR     (1)                    DEFAULT 'Y' NOT NULL
  , Created                          DATE                            DEFAULT SYSDATE NOT NULL
  , CreatedBy                        NUMBER   (10)                   NOT NULL
  , Updated                          DATE                            DEFAULT SYSDATE NOT NULL
  , UpdatedBy                        NUMBER   (10)                   NOT NULL
  , M_Warehouse_ID		     NUMBER   (10)    
  , MP_Plant_ID          	     NUMBER   (10) 
  , PlanQty 			     NUMBER   	             	     DEFAULT 0
  , RealQty			     NUMBER   	             	     DEFAULT 0
  , PlanCost	 		     NUMBER   	             	     DEFAULT 0
  , RealCost			     NUMBER    	             	     DEFAULT 0
  , CHECK (IsActive in ('Y','N'))
  , CONSTRAINT MP_Order_CollectorCost_Key PRIMARY KEY (MP_Order_ID, M_Product_ID  ,MP_Cost_Element_ID)                           
);

-- 
-- TABLE:  MP_Order_Collector
--

ALTER TABLE  MP_Order_CollectorCost ADD CONSTRAINT AD_OrgMP_MP_Order_Col
    FOREIGN KEY (AD_Org_ID)
    REFERENCES AD_Org(AD_Org_ID)
;

ALTER TABLE  MP_Order_CollectorCost ADD CONSTRAINT AD_ClientMP_Order_Col
    FOREIGN KEY (AD_Client_ID)
    REFERENCES AD_Client(AD_Client_ID)
;

--ALTER TABLE MP_Cost_Product ADD CONSTRAINT AD_ClientMP_Order_Col
--    FOREIGN KEY ( MP_Cost_Category_ID)
--    REFERENCES MP_Cost_Category(MP_Cost_Category_ID)
--;
