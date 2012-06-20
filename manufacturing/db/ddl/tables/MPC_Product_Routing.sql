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
DROP TABLE MPC_Product_Routing;
CREATE TABLE MPC_Product_Routing
(
    MPC_Product_Routing_ID                   NUMBER   (10)                   NOT NULL	
  , AD_Client_ID                     NUMBER   (10)                   NOT NULL
  , AD_Org_ID                        NUMBER   (10)                   NOT NULL    
  , Updated                          DATE                            DEFAULT SYSDATE NOT NULL
  , UpdatedBy                        NUMBER   (10)                   NOT NULL             
  , Created                          DATE                            DEFAULT SYSDATE NOT NULL
  , CreatedBy                        NUMBER   (10)                   NOT NULL
  , Value                            VARCHAR2 (80)                   NOT NULL
  , Name                             VARCHAR2 (120)                  NOT NULL   
  , Description                      NVARCHAR2 (510)
  , IsActive                         CHAR     (1)                    DEFAULT ('Y') NOT NULL              
  , Type                             CHAR (1)                    
  , Document                         NVARCHAR2 (22)                   
  , Ecn                              NVARCHAR2 (20)  
  , Revision                         NVARCHAR2 (10)                                          
  , ValidFrom                        DATE                            NOT NULL
  , ValidTo                          DATE                            NULL
  , CHECK (IsActive in ('Y','N'))
  , CONSTRAINT MPC_Product_Routing_Key PRIMARY KEY (MPC_Product_Routing_ID)
);

-- 
-- TABLE: MPC_Product_Routing
--

ALTER TABLE MPC_Product_Routing ADD CONSTRAINT AD_OrgMPC_ProductR
    FOREIGN KEY (AD_Org_ID)
    REFERENCES AD_Org(AD_Org_ID)
;

ALTER TABLE MPC_Product_Routing ADD CONSTRAINT AD_ClientMPC_ProductR
    FOREIGN KEY (AD_Client_ID)
    REFERENCES AD_Client(AD_Client_ID)
;
 
