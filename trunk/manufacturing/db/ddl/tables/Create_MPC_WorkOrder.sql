  /*************************************************************************
   * The contents of this file are subject to the Compiere License.  You may
   * obtain a copy of the License at    http://www.compiere.org/license.html 
   * Software is on an  "AS IS" basis,  WITHOUT WARRANTY OF ANY KIND, either 
   * express or implied. See the License for details. Code: Compiere ERP+CPM
   * Copyright (C) 1999-2004 Jorg Janke, ComPiere, Inc. 
   * Copyright (C) 2004 Victor Pérez, e-Evolution, S.C.
   * Copyright (C) 2004 Colorado Correctional Industries 
   * All Rights Reserved.
   *************************************************************************
   * Title:	 Create MPC Work Order Table and Elements
   * Description:
   *	Creates and updates all Work Order elements as desired in the Compiere 
   *  AD_ Tables, Column, and Elements
   *
   *	Steps:
   *
   *  1. Remove the existing table and all dependancies	
   *	2. Create the Table in the database
   *  3. Create the Elements - D_Element_Check.sql
   *  4. Create the Column entries - 0_Add_New_Column.sql
   *  5. Update the AD_Element entries
   *	6. Update the AD_Column entries
   *
   *****************************************************************************/

  -- -----------------------------------------------------------------------------
  -- Remove the table, column, and element data
  -- -----------------------------------------------------------------------------

  DELETE FROM COMPIERE.AD_TABLE   WHERE UPPER(TABLENAME)  = 'MPC_WorkOrder'
  DELETE FROM COMPIERE.AD_ELEMENT WHERE UPPER(COLUMNNAME) = 'MPC_WorkOrder_ID'

  DROP TABLE MPC_WorkOrder;

  -- -----------------------------------------------------------------------------
  -- Create the database table definition
  -- -----------------------------------------------------------------------------

CREATE TABLE MPC_WorkOrder(
    MPC_WorkOrder_ID      NUMBER(10, 0)     NOT NULL,
    AD_Client_ID          NUMBER(10, 0)     NOT NULL,
    AD_Org_ID             NUMBER(10, 0)     NOT NULL,
    IsActive              CHAR(1)            DEFAULT 'Y' NOT NULL
                          CHECK (IsActive in ('Y','N')),
    Created               DATE              DEFAULT SYSDATE NOT NULL,
    CreatedBy             NUMBER(10, 0)     NOT NULL,
    Updated               DATE              DEFAULT SYSDATE NOT NULL,
    UpdatedBy             NUMBER(10, 0)     NOT NULL,
    DocumentNo            NVARCHAR2(30)     NOT NULL,
    DocStatus             CHAR(2)           NOT NULL,
    DocAction             CHAR(2)           NOT NULL,
    Processing            CHAR(1),
    Processed             CHAR(1)            DEFAULT 'N' NOT NULL
                          CHECK (Processed in ('Y','N')),
    Description           NVARCHAR2(255),
    M_Product_ID          NUMBER(10, 0)     NOT NULL,
    C_UOM_ID              NUMBER(10, 0)     NOT NULL,
    C_OrderLine_ID        NUMBER(10, 0)     NOT NULL,
    ScheduledStartDate    DATE,
    ScheduledEndDate      DATE,
    ActualStartDate       DATE,
    ActualEndDate         DATE,
    DatePromised          DATE,
    DateConfirmed         DATE,
    isApproved            CHAR(1)            DEFAULT 'Y' NOT NULL
                          CHECK (isApproved in ('Y','N')),
    IsPrinted             CHAR(1)            DEFAULT 'Y' NOT NULL
                          CHECK (IsPrinted in ('Y','N')),
    CONSTRAINT MPC_WorkOrder_Key PRIMARY KEY (MPC_WorkOrder_ID)
    USING INDEX
        PCTFREE 10
        INITRANS 2
        MAXTRANS 255
        STORAGE(INITIAL 10K
                NEXT 10K
                MINEXTENTS 1
                MAXEXTENTS 121
                PCTINCREASE 50
                ), 
    CONSTRAINT RefM_Product2161 FOREIGN KEY (M_Product_ID)
    REFERENCES M_Product(M_Product_ID),
    CONSTRAINT RefC_UOM2163 FOREIGN KEY (C_UOM_ID)
    REFERENCES C_UOM(C_UOM_ID),
    CONSTRAINT RefC_OrderLine2165 FOREIGN KEY (C_OrderLine_ID)
    REFERENCES C_OrderLine(C_OrderLine_ID)
)
PCTFREE 10
PCTUSED 40
INITRANS 1
MAXTRANS 255
STORAGE(INITIAL 10K
        NEXT 10K
        MINEXTENTS 1
        MAXEXTENTS 121
        PCTINCREASE 50
        )
;

COMMENT ON TABLE MPC_WorkOrder IS 'Production Work Order.'
;
  -- -----------------------------------------------------------------------------
  -- Create the column and element entries
  -- -----------------------------------------------------------------------------

  @../../maintain/0_Add_New_Column.sql;
  @../../maintain/AD_Element_Check.sql;

  -- -----------------------------------------------------------------------------
  -- Update the element entries
  -- -----------------------------------------------------------------------------

  UPDATE COMPIERE.AD_ELEMENT 
     SET ENTITYTYPE = 'A', 
               NAME = 'Work Order',      
          PRINTNAME = 'Work Order',   
         COLUMNNAME = 'MPC_WorkOrder_ID',    
        DESCRIPTION = 'Manufacturing Work Order', 
               HELP = 'Manufacturing Work Order' 
   WHERE UPPER(COLUMNNAME)= 'MPC_WORKORDER_ID';

  UPDATE COMPIERE.AD_ELEMENT 
     SET ENTITYTYPE = 'A', 
               NAME = 'Scheduled Start Date',      
          PRINTNAME = 'Scheduled Start Date',   
         COLUMNNAME = 'ScheduledStartDate',    
        DESCRIPTION = 'Scheduled Start Date', 
               HELP = 'Scheduled Start Date' 
   WHERE UPPER(COLUMNNAME)= 'SCHEDULEDSTARTDATE';

  UPDATE COMPIERE.AD_ELEMENT 
     SET ENTITYTYPE = 'A', 
               NAME = 'Scheduled End Date',      
          PRINTNAME = 'Scheduled End Date',   
         COLUMNNAME = 'ScheduledEndDate',    
        DESCRIPTION = 'Scheduled End Date', 
               HELP = 'Scheduled End Date' 
   WHERE UPPER(COLUMNNAME)= 'SCHEDULEDENDDATE';

  UPDATE COMPIERE.AD_ELEMENT 
     SET ENTITYTYPE = 'A', 
               NAME = 'Actual Start Date',      
          PRINTNAME = 'Actual Start Date',   
         COLUMNNAME = 'ActualStartDate',    
        DESCRIPTION = 'Actual Start Date', 
               HELP = 'Actual Start Date' 
   WHERE UPPER(COLUMNNAME)= 'ACTUALSTARTDATE';

  UPDATE COMPIERE.AD_ELEMENT 
     SET ENTITYTYPE = 'A', 
               NAME = 'Actual End Date',      
          PRINTNAME = 'Actual End Date',   
         COLUMNNAME = 'ActualEndDate',    
        DESCRIPTION = 'Actual End Date', 
               HELP = 'Actual End Date' 
   WHERE UPPER(COLUMNNAME)= 'ACTUALENDDATE';

  UPDATE COMPIERE.AD_ELEMENT 
     SET ENTITYTYPE = 'A', 
               NAME = 'Date Confirmed',      
          PRINTNAME = 'Date Confirmed',   
         COLUMNNAME = 'DateConfirmed',    
        DESCRIPTION = 'Date Confirmed', 
               HELP = 'Date Confirmed' 
   WHERE UPPER(COLUMNNAME)= 'DATECONFIRMED';

  -- -----------------------------------------------------------------------------
  -- Update the table entry
  -- -----------------------------------------------------------------------------

  UPDATE COMPIERE.AD_TABLE   
     SET ENTITYTYPE = 'A', 
       ISDELETEABLE = 'N',
               NAME = 'MPC_WorkOrder',    
          TABLENAME = 'MPC_WorkOrder',
        ACCESSLEVEL = '1'                -- Organization
    WHERE UPPER(TABLENAME) = 'MPC_WORKORDER';

  -- -----------------------------------------------------------------------------
  -- Update the column entries - Setup Unique Entries
  -- -----------------------------------------------------------------------------

  -- MPC_WorkOrder_ID ------------------------------------------------------------

  UPDATE COMPIERE.AD_COLUMN
     SET AD_REFERENCE_ID = 13,               -- ID
             ISMANDATORY = 'Y',
                ISPARENT = 'N',
            ISUPDATEABLE = 'N',
                   ISKEY = 'Y'
   WHERE UPPER(COLUMNNAME) = 'MPC_WORKORDER_ID'
     AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                          FROM COMPIERE.AD_TABLE 
                         WHERE UPPER(TABLENAME) = 'MPC_WORKORDER');

  -- Doc Action ------------------------------------------------------------------

  UPDATE COMPIERE.AD_COLUMN
     SET AD_REFERENCE_ID = 28,               -- Button
         AD_PROCESS_ID   = 104,              -- C_OrderPost **** To be changed ****
             ISMANDATORY = 'Y',
                ISPARENT = 'N',
            ISUPDATEABLE = 'Y',
                   ISKEY = 'N'
   WHERE UPPER(COLUMNNAME) = 'DOCACTION'
     AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                          FROM COMPIERE.AD_TABLE 
                         WHERE UPPER(TABLENAME) = 'MPC_WORKORDER');

  -- Doc Status ------------------------------------------------------------------

  UPDATE COMPIERE.AD_COLUMN
     SET AD_REFERENCE_ID = 17,               -- List
   AD_REFERENCE_VALUE_ID = 131,              -- All_Document Status
             ISMANDATORY = 'Y',
            ISUPDATEABLE = 'N',
            DEFAULTVALUE = 'DR'
   WHERE UPPER(COLUMNNAME) = 'DOCSTATUS'
     AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                          FROM COMPIERE.AD_TABLE 
                         WHERE UPPER(TABLENAME) = 'MPC_WORKORDER');

  -- Document No -----------------------------------------------------------------

  UPDATE COMPIERE.AD_COLUMN
         SET ISMANDATORY = 'Y',
                   SEQNO = 1,
            ISIDENTIFIER = 'Y'
   WHERE UPPER(COLUMNNAME) = 'DOCUMENTNO'
     AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                          FROM COMPIERE.AD_TABLE 
                         WHERE UPPER(TABLENAME) = 'MPC_WORKORDER');

  -- Approved --------------------------------------------------------------------

  UPDATE COMPIERE.AD_COLUMN
         SET DEFAULTVALUE = '@IsApproved@',
              ISMANDATORY = 'Y'
   WHERE UPPER(COLUMNNAME) = 'ISAPPROVED'
     AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                          FROM COMPIERE.AD_TABLE 
                         WHERE UPPER(TABLENAME) = 'MPC_WORKORDER');



-- C_OrderLine_ID --------------------------------------------------------------

UPDATE COMPIERE.AD_COLUMN
   SET AD_REFERENCE_ID = 19,               -- TableDir
           ISMANDATORY = 'Y',
          ISUPDATEABLE = 'N',
              ISPARENT = 'Y'
 WHERE UPPER(COLUMNNAME) = 'C_ORDERLINE_ID'
   AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                        FROM COMPIERE.AD_TABLE 
                       WHERE UPPER(TABLENAME) = 'MPC_WORKORDER');

  -- -----------------------------------------------------------------------------
  -- Update the column entries - Setup Defaults
  -- -----------------------------------------------------------------------------

  -- AD_Client_ID ----------------------------------------------------------------

  UPDATE COMPIERE.AD_COLUMN
     SET AD_REFERENCE_ID = 19,               -- TableDir
         AD_VAL_RULE_ID  = 103,              -- AD_ClientSecurityValidation
            ISUPDATEABLE = 'N',
            DEFAULTVALUE = '@AD_Client_ID@'
   WHERE COLUMNNAME   = 'AD_Client_ID'
     AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                          FROM COMPIERE.AD_TABLE 
                         WHERE UPPER(TABLENAME) = 'MPC_WORKORDER');

  -- AD_Org_ID -------------------------------------------------------------------

  UPDATE COMPIERE.AD_COLUMN
     SET AD_REFERENCE_ID = 19,               -- TableDir
          AD_VAL_RULE_ID = 104,              -- AD_OrgSecurityValidation
            ISUPDATEABLE = 'N',
            DEFAULTVALUE = '@AD_Org_ID@'
   WHERE COLUMNNAME   = 'AD_Org_ID'
     AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                          FROM COMPIERE.AD_TABLE 
                         WHERE UPPER(TABLENAME) = 'MPC_WORKORDER');

  -- Created ---------------------------------------------------------------------

  UPDATE COMPIERE.AD_COLUMN
     SET AD_REFERENCE_ID = 16,               -- DateTime
            ISUPDATEABLE = 'N'
   WHERE COLUMNNAME   = 'Created'
     AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                          FROM COMPIERE.AD_TABLE 
                         WHERE UPPER(TABLENAME) = 'MPC_WORKORDER');

  -- AD_CreatedBy ----------------------------------------------------------------

  UPDATE COMPIERE.AD_COLUMN
     SET AD_REFERENCE_ID = 18,               -- Table
   AD_REFERENCE_VALUE_ID = 110,              -- AD_User
            ISUPDATEABLE = 'N'
   WHERE COLUMNNAME   = 'CreatedBy'
     AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                          FROM COMPIERE.AD_TABLE 
                         WHERE UPPER(TABLENAME) = 'MPC_WORKORDER');

  -- Updated ---------------------------------------------------------------------

  UPDATE COMPIERE.AD_COLUMN
     SET AD_REFERENCE_ID = 16,               -- DateTime
            ISUPDATEABLE = 'N'
   WHERE COLUMNNAME   = 'Updated'
     AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                          FROM COMPIERE.AD_TABLE 
                         WHERE UPPER(TABLENAME) = 'MPC_WORKORDER');

  -- AD_UpdatedBy ----------------------------------------------------------------

  UPDATE COMPIERE.AD_COLUMN
     SET AD_REFERENCE_ID = 18,               -- Table
   AD_REFERENCE_VALUE_ID = 110,              -- AD_User
            ISUPDATEABLE = 'N'
   WHERE COLUMNNAME   = 'UpdatedBy'
     AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                          FROM COMPIERE.AD_TABLE 
                         WHERE UPPER(TABLENAME) = 'MPC_WORKORDER');

