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
 * $Id: MPC_Cost_Element.sql,v 1.1 2004/02/11 19:08:39 vpj-cd Exp $
 ***
 * Title:	 MCP_MRP
 * Description:
 ************************************************************************/
DROP TABLE MPC_MRP CASCADE CONSTRAINT;
CREATE TABLE  MPC_MRP
(
    MPC_MRP_ID                       NUMBER   (10)                   NOT NULL
  , Version                          NUMBER   
  , Type  			     CHAR     (1)            
  , TypeMRP                          CHAR     (3)
  , Priority                         VARCHAR2 (10)                  
  , M_Warehouse_ID 		     NUMBER   (10)                   NOT NULL
  , AD_Client_ID                     NUMBER   (10)                   NOT NULL
  , AD_Org_ID                        NUMBER   (10)                   NOT NULL
  , Created                          DATE                            DEFAULT SYSDATE NOT NULL
  , CreatedBy                        NUMBER   (10)                   NOT NULL
  , Updated                          DATE                            DEFAULT SYSDATE NOT NULL
  , UpdatedBy                        NUMBER   (10)                   NOT NULL
  , Description                      NVARCHAR2(1020)                 
  , Name                             VARCHAR2 (120)                  NOT NULL 
  , Value                            VARCHAR2 (80)                   NOT NULL
  , IsActive                         CHAR     (1)                    DEFAULT 'Y' NOT NULL
  , C_Order_ID 			     NUMBER   (10)                  
  , C_OrderLine_ID                   NUMBER   (10)                   
  , M_Requisition_ID		     NUMBER   (10)
  , M_RequisitionLine_ID  	     NUMBER   (10)
  , MPC_Order_ID                     NUMBER   (10)                 
  , MPC_Forcast_ID                   NUMBER   (10)                 
  , M_Product_ID                     NUMBER   (10)                 
  , DateOrdered                      DATE                            DEFAULT SYSDATE NOT NULL 
  , DatePromised                     DATE                            DEFAULT SYSDATE NOT NULL
  , DateSimulation                   DATE                            DEFAULT SYSDATE NOT NULL
  , Qty                              NUMBER                          DEFAULT 0 NOT NULL 
  , CHECK (IsActive in ('Y','N'))
  --, CHECK (Type ('D','S'))
  , CONSTRAINT MPC_MRP_Key PRIMARY KEY (MPC_MRP_ID)                           
);

-- 
-- TABLE:  MPC_MRP
--

ALTER TABLE MPC_MRP ADD CONSTRAINT AD_OrgMPCMRP
    FOREIGN KEY (AD_Org_ID)
    REFERENCES AD_Org(AD_Org_ID)
;

ALTER TABLE MPC_MRP ADD CONSTRAINT ADClientMPCMRP
    FOREIGN KEY (AD_Client_ID)
    REFERENCES AD_Client(AD_Client_ID)
;

CREATE INDEX MPC_MRP_Product ON MPC_MRP(AD_Client_ID, AD_Org_ID, M_Warehouse_ID , DatePromised , M_Product_ID )
;
