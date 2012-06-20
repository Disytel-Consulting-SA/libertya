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
 * Title:	 Alter the M_Product Table
 * Description:
 *	Adds the 'IsManufactured' attribute.
 *
 *	Steps:
 *
 *  1. Remove the existing column and all dependancies	
 *	2. Create the column in the database
 *  3. Create the Elements - D_Element_Check.sql
 *  4. Create the Column entries - 0_Add_New_Column.sql
 *  5. Update the AD_Element entries
 *	6. Update any AD_Column entries 
 *
 *****************************************************************************/

-- -----------------------------------------------------------------------------
-- Alter M_Product
-- -----------------------------------------------------------------------------

ALTER TABLE "COMPIERE"."M_PRODUCT" SET UNUSED ("ISMANUFACTURED") CASCADE CONSTRAINTS;
ALTER TABLE COMPIERE.M_PRODUCT ADD IsManufactured CHAR(1) DEFAULT 'N' NOT NULL;

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
             NAME = 'Manufactured',      
        PRINTNAME = 'Manufactured',   
       COLUMNNAME = 'IsManufactured',    
      DESCRIPTION = 'Determines if a product is manufactured', 
             HELP = 'Determines if a product is manufactured' 
 WHERE UPPER(COLUMNNAME)= 'ISMANUFACTURED';

COMMIT;