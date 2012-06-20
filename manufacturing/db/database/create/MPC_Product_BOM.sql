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
 * $Id: MPC_Product_BOM.sql,v 1.3 2004/02/11 16:54:13 vpj-cd Exp $
 ***
 * Title:	Bill of Material
 * Description:
 ************************************************************************/

DROP TABLE MPC_Product_BOM CASCADE CONSTRAINTS;
CREATE TABLE MPC_Product_BOM
(
    MPC_Product_BOM_ID                NUMBER   (10)                   NOT NULL
  , AD_Client_ID                     NUMBER   (10)                   NOT NULL
  , AD_Org_ID                        NUMBER   (10)                   NOT NULL
  , C_UOM_ID			     NUMBER   (10)		     NOT NULL
  , Created                          DATE                            DEFAULT SYSDATE NOT NULL
  , CreatedBy                        NUMBER   (10)                   NOT NULL
  , Description                      NVARCHAR2 (510)                  
  , DocumentNo                       NVARCHAR2 (30)                   NOT NULL                  
  , Name                             VARCHAR2 (120)                  NOT NULL 
  , M_Product_ID                     NUMBER   (10)                   NOT NULL
  , M_AttributeSetInstance_ID        NUMBER(10, 0)
  , Revision                         VARCHAR2 (10)                   
  , Value                            VARCHAR2 (80)                   NOT NULL                          
  , ValidFrom                        DATE                            NOT NULL
  , ValidTo                          DATE                            NULL 
  , BOMType                          CHAR(1)                         
  , Updated                          DATE                            DEFAULT SYSDATE NOT NULL
  , UpdatedBy                        NUMBER   (10)                   NOT NULL  
  , IsActive                         CHAR     (1)                    DEFAULT ('Y') NOT NULL
    CHECK (IsActive in ('Y','N')),
    CONSTRAINT MPC_Product_BOM_Key PRIMARY KEY (MPC_Product_BOM_ID)
);

-- 
-- TABLE: M_Product_BOM 
--

ALTER TABLE MPC_Product_BOM ADD CONSTRAINT ADClient_MPCProductBOM 
    FOREIGN KEY (AD_Client_ID)
    REFERENCES AD_Client(AD_Client_ID) ON DELETE CASCADE
;

ALTER TABLE MPC_Product_BOM ADD CONSTRAINT  ADOrg_MPCProductBOM 
    FOREIGN KEY ( AD_Org_ID)
    REFERENCES  AD_Org(AD_Org_ID) ON DELETE CASCADE
;

ALTER TABLE MPC_Product_BOM ADD CONSTRAINT  CUOM_MPCProductBOM 
    FOREIGN KEY ( C_UOM_ID)
    REFERENCES  C_UOM (C_UOM_ID) ON DELETE CASCADE
;

ALTER TABLE MPC_Product_BOM ADD CONSTRAINT  MProduct_MPCProductBOM 
    FOREIGN KEY (M_Product_ID)
    REFERENCES  M_Product(M_Product_ID) ON DELETE CASCADE
;

ALTER TABLE MPC_Product_BOM ADD CONSTRAINT MAttributeSetI_MPCProductBOM 
    FOREIGN KEY (M_AttributeSetInstance_ID)
    REFERENCES M_AttributeSetInstance(M_AttributeSetInstance_ID) ON DELETE CASCADE
;



