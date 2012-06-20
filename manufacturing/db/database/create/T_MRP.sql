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
DROP TABLE T_MRP  CASCADE CONSTRAINTS;
-- 
-- TABLE: 
--
CREATE TABLE T_MRP(
    AD_PInstance_ID    NUMBER(10, 0)    NOT NULL,
    M_Warehouse_ID     NUMBER(10, 0)    NOT NULL,
    M_Product_ID       NUMBER(10, 0)    NOT NULL,
    AD_Client_ID       NUMBER(10, 0)    NOT NULL,
    AD_Org_ID          NUMBER(10, 0)    NOT NULL,
    QtyOnHand          NUMBER            DEFAULT 0,
    Col_1            NVARCHAR2(60),
    Col_2            NVARCHAR2(12),
    Col_3            NVARCHAR2(12),
    Col_4            NVARCHAR2(12),
    Col_5            NVARCHAR2(12),
    Col_6            NVARCHAR2(12),
    Col_7            NVARCHAR2(12),
    Col_8            NVARCHAR2(12),
    Col_9            NVARCHAR2(12),
    CONSTRAINT T_MRP_Key PRIMARY KEY (AD_PInstance_ID, M_Warehouse_ID, M_Product_ID)
)
;
