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
 * Title:	 Create MPC Facility Table and Elements
 * Description:
 *	Creates and updates all Facility elements as desired in the Compiere 
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

DELETE FROM COMPIERE.AD_TABLE   WHERE UPPER(TABLENAME)  = 'MPC_FACILITY'
DELETE FROM COMPIERE.AD_ELEMENT WHERE UPPER(COLUMNNAME) = 'MPC_FACILITY_ID'

DROP TABLE MPC_Facility;

-- -----------------------------------------------------------------------------
-- Create the database table definition
-- -----------------------------------------------------------------------------

CREATE TABLE MPC_Facility(
    MPC_Facility_ID    NUMBER(10, 0)     NOT NULL,
    AD_Client_ID       NUMBER(10, 0)     NOT NULL,
    AD_Org_ID          NUMBER(10, 0)     NOT NULL,
    IsActive           CHAR(1)            DEFAULT 'Y' NOT NULL
                       CHECK (IsActive in ('Y','N')),
    Created            DATE              DEFAULT SYSDATE NOT NULL,
    CreatedBy          NUMBER(10, 0)     NOT NULL,
    Updated            DATE              DEFAULT SYSDATE NOT NULL,
    UpdatedBy          NUMBER(10, 0)     NOT NULL,
    Value              NVARCHAR2(40)     NOT NULL,
    Name               NVARCHAR2(60)     NOT NULL,
    Description        NVARCHAR2(255),
    C_Location_ID      NUMBER(10, 0)     NOT NULL,
    S_Resource_ID      NUMBER(10, 0),
    CONSTRAINT MPC_Facility_Key PRIMARY KEY (MPC_Facility_ID)
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
    CONSTRAINT RefC_Location2175 FOREIGN KEY (C_Location_ID)
    REFERENCES C_Location(C_Location_ID),
    CONSTRAINT RefS_Resource2180 FOREIGN KEY (S_Resource_ID)
    REFERENCES S_Resource(S_Resource_ID)
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

COMMENT ON TABLE MPC_Facility IS 'Work Cells consist of one or more Work Centers used to produce ''like'' products with similar routings. '
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
             NAME = 'Facility',      
        PRINTNAME = 'Facility',   
       COLUMNNAME = 'MPC_Facility_ID',    
      DESCRIPTION = 'Production Facility', 
             HELP = 'Production Facility' 
 WHERE UPPER(COLUMNNAME)= 'MPC_FACILITY_ID';

-- -----------------------------------------------------------------------------
-- Update the table entry
-- -----------------------------------------------------------------------------

UPDATE COMPIERE.AD_TABLE   
   SET ENTITYTYPE = 'A', 
             NAME = 'MPC_Facility',    
        TABLENAME = 'MPC_Facility',
      ACCESSLEVEL = '1'                -- Organization
  WHERE UPPER(TABLENAME) = 'MPC_FACILITY';

-- -----------------------------------------------------------------------------
-- Update the column entries - Setup Unique Entries
-- -----------------------------------------------------------------------------

-- MPC_Facility_ID --------------------------------------------------------------

UPDATE COMPIERE.AD_COLUMN
   SET AD_REFERENCE_ID = 13,               -- ID
           ISMANDATORY = 'Y',
              ISPARENT = 'N',
          ISUPDATEABLE = 'N',
                 ISKEY = 'Y'
 WHERE UPPER(COLUMNNAME) = 'MPC_FACILITY_ID'
   AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                        FROM COMPIERE.AD_TABLE 
                       WHERE UPPER(TABLENAME) = 'MPC_FACILITY');

-- S_Location_ID ---------------------------------------------------------------

UPDATE COMPIERE.AD_COLUMN
   SET AD_REFERENCE_ID = 19,               -- Table Direct
           ISMANDATORY = 'Y'
 WHERE UPPER(COLUMNNAME) = 'C_LOCATION_ID'
   AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                        FROM COMPIERE.AD_TABLE 
                       WHERE UPPER(TABLENAME) = 'MPC_FACILITY');

-- S_Resouce_ID ----------------------------------------------------------------

UPDATE COMPIERE.AD_COLUMN
   SET AD_REFERENCE_ID = 21,               -- Location
           ISMANDATORY = 'Y'
 WHERE UPPER(COLUMNNAME) = 'C_LOCATION_ID'
   AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                        FROM COMPIERE.AD_TABLE 
                       WHERE UPPER(TABLENAME) = 'MPC_FACILITY');

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
                       WHERE UPPER(TABLENAME) = 'MPC_FACILITY');

-- AD_Org_ID -------------------------------------------------------------------

UPDATE COMPIERE.AD_COLUMN
   SET AD_REFERENCE_ID = 19,               -- TableDir
        AD_VAL_RULE_ID = 104,              -- AD_OrgSecurityValidation
          ISUPDATEABLE = 'N',
          DEFAULTVALUE = '@AD_Org_ID@'
 WHERE COLUMNNAME   = 'AD_Org_ID'
   AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                        FROM COMPIERE.AD_TABLE 
                       WHERE UPPER(TABLENAME) = 'MPC_FACILITY');

-- Created ---------------------------------------------------------------------

UPDATE COMPIERE.AD_COLUMN
   SET AD_REFERENCE_ID = 16,               -- DateTime
          ISUPDATEABLE = 'N'
 WHERE COLUMNNAME   = 'Created'
   AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                        FROM COMPIERE.AD_TABLE 
                       WHERE UPPER(TABLENAME) = 'MPC_FACILITY');

-- AD_CreatedBy ----------------------------------------------------------------

UPDATE COMPIERE.AD_COLUMN
   SET AD_REFERENCE_ID = 18,               -- Table
 AD_REFERENCE_VALUE_ID = 110,              -- AD_User
          ISUPDATEABLE = 'N'
 WHERE COLUMNNAME   = 'CreatedBy'
   AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                        FROM COMPIERE.AD_TABLE 
                       WHERE UPPER(TABLENAME) = 'MPC_FACILITY');

-- Updated ---------------------------------------------------------------------

UPDATE COMPIERE.AD_COLUMN
   SET AD_REFERENCE_ID = 16,               -- DateTime
          ISUPDATEABLE = 'N'
 WHERE COLUMNNAME   = 'Updated'
   AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                        FROM COMPIERE.AD_TABLE 
                       WHERE UPPER(TABLENAME) = 'MPC_FACILITY');

-- AD_UpdatedBy ----------------------------------------------------------------

UPDATE COMPIERE.AD_COLUMN
   SET AD_REFERENCE_ID = 18,               -- Table
 AD_REFERENCE_VALUE_ID = 110,              -- AD_User
          ISUPDATEABLE = 'N'
 WHERE COLUMNNAME   = 'UpdatedBy'
   AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                        FROM COMPIERE.AD_TABLE 
                       WHERE UPPER(TABLENAME) = 'MPC_FACILITY');

-- Name ------------------------------------------------------------------------

UPDATE COMPIERE.AD_COLUMN
   SET ISIDENTIFIER = 'Y',              
        ISMANDATORY = 'Y',
       ISUPDATEABLE = 'Y'
 WHERE COLUMNNAME  = 'Name'
   AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                        FROM COMPIERE.AD_TABLE 
                       WHERE UPPER(TABLENAME) = 'MPC_FACILITY');

COMMIT;