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
 * $Id: MPC_Product_Routing.sql,v 1.3 2004/02/11 17:03:07 vpj-cd Exp $
 ***
 * Title:	Routing & Process
 * Description:
 ************************************************************************/
DROP TABLE MPC_Order_Node_Asset CASCADE CONSTRAINTS;
CREATE TABLE MPC_Order_Node_Asset
(
    MPC_Order_Node_Asset_ID          NUMBER   (10)                   NOT NULL	    
  , AD_Client_ID                     NUMBER   (10)                   NOT NULL
  , AD_Org_ID                        NUMBER   (10)                   NOT NULL
  , A_Asset_ID                       NUMBER   (10)                   NOT NULL
  , MPC_Order_Workflow_ID                  NUMBER   (10)                  NOT NULL
  , MPC_Order_Node_ID		     NUMBER   (10)                   NOT NULL  
  , MPC_Order_ID                     NUMBER (10)                     NOT NULL
  , Updated                          DATE                            DEFAULT SYSDATE NOT NULL
  , UpdatedBy                        NUMBER   (10)                   NOT NULL             
  , Created                          DATE                            DEFAULT SYSDATE NOT NULL
  , CreatedBy                        NUMBER   (10)                   NOT NULL
  , IsActive                         CHAR     (1)                    DEFAULT ('Y') NOT NULL                
  , CHECK (IsActive in ('Y','N'))
  , CONSTRAINT MPC_Order_Node_Asset_Key PRIMARY KEY (MPC_Order_Node_Asset_ID)
);

-- 
-- TABLE: MPC_Product_Routing
--

ALTER TABLE MPC_Order_Node_Asset ADD CONSTRAINT ADOrgMPCOrderNodeAsset
    FOREIGN KEY (AD_Org_ID)
    REFERENCES AD_Org(AD_Org_ID)
;

ALTER TABLE MPC_Order_Node_Asset ADD CONSTRAINT ADClientMPCOrderNodeAsset
    FOREIGN KEY (AD_Client_ID)
    REFERENCES AD_Client(AD_Client_ID)
;

ALTER TABLE MPC_Order_Node_Asset ADD CONSTRAINT MPCOrderWFMPCOrderNodeAsset
    FOREIGN KEY (MPC_Order_Workflow_ID)
    REFERENCES MPC_Order_Workflow(MPC_Order_Workflow_ID)
;
ALTER TABLE MPC_Order_Node_Asset ADD CONSTRAINT AAssetMPCOrderNodeAsset
    FOREIGN KEY (A_Asset_ID)
    REFERENCES A_Asset(A_Asset_ID)
;
ALTER TABLE MPC_Order_Node_Asset ADD CONSTRAINT MPCOrderNodeMPCOrderNodeAsset
    FOREIGN KEY (MPC_Order_Node_ID)
    REFERENCES MPC_Order_Node(MPC_Order_Node_ID)
;
ALTER TABLE MPC_Order_Node_Asset ADD CONSTRAINT  MPCOrderMPCOrderNodeAsset
    FOREIGN KEY (MPC_Order_ID)
    REFERENCES  MPC_Order(MPC_Order_ID)
;


 
