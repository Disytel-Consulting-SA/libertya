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
 * Title:	 MCP_Cost_Element
 * Description:
 ************************************************************************/
DROP TABLE MPC_Cost_Element;
CREATE TABLE  MPC_Cost_Element
(
    MPC_Cost_Element_ID               NUMBER   (10)                   NOT NULL  
  , AD_Client_ID                     NUMBER   (10)                   NOT NULL
  , AD_Org_ID                        NUMBER   (10)                   NOT NULL
  , C_CostType_ID		     NUMBER   (10)                   NOT NULL
  , MPC_ElementType		     CHAR     (1)
  , IsActive                         CHAR     (1)                    DEFAULT 'Y' NOT NULL
  , IsSimulation		     CHAR     (1)		     DEFAULT 'N' NOT NULL	
  , Created                          DATE                            DEFAULT SYSDATE NOT NULL
  , CreatedBy                        NUMBER   (10)                   NOT NULL
  , Updated                          DATE                            DEFAULT SYSDATE NOT NULL
  , UpdatedBy                        NUMBER   (10)                   NOT NULL
  , Description                      NVARCHAR2(1020)                 
  , Name                             VARCHAR2 (120)                  NOT NULL 
  , Value                            VARCHAR2 (80)                   NOT NULL 
  , M_Warehouse_ID		     NUMBER   (10)    
  , MPC_Resource_ID          	     NUMBER   (10)  
  , CHECK (IsActive in ('Y','N'))
  , CONSTRAINT MPC_Cost_Element_Key PRIMARY KEY (MPC_Cost_Element_ID)                           
);

-- 
-- TABLE:  MPC_Cost_Element
--

ALTER TABLE MPC_Cost_Element ADD CONSTRAINT AD_OrgMPC_Cost_Element
    FOREIGN KEY (AD_Org_ID)
    REFERENCES AD_Org(AD_Org_ID)
;

ALTER TABLE MPC_Cost_Element ADD CONSTRAINT AD_ClientMPC_Cost_Element
    FOREIGN KEY (AD_Client_ID)
    REFERENCES AD_Client(AD_Client_ID)
;

ALTER TABLE MPC_Cost_Element ADD CONSTRAINT AD_ClientC_CostType
    FOREIGN KEY (  C_CostType_ID)
    REFERENCES  C_CostType( C_CostType_ID)
;
