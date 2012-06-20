
-- 
-- TABLE: MPC_Workflow 
--
DROP TABLE MPC_Order_Workflow CASCADE CONSTRAINT;

CREATE TABLE MPC_Order_Workflow(
    AD_Workflow_ID             NUMBER(10, 0)      NOT NULL,
    MPC_Order_Workflow_ID      NUMBER(10, 0)      NOT NULL,
    MPC_Order_ID               NUMBER(10, 0)      NOT NULL,
    AD_Client_ID               NUMBER(10, 0)      NOT NULL,
    AD_Org_ID                  NUMBER(10, 0)      NOT NULL,
    AD_Table_ID                NUMBER(10, 0)      ,
    IsActive                   CHAR(1)            DEFAULT 'Y' NOT NULL,
    IsDefault                  CHAR(1)            DEFAULT 'N' NOT NULL,
    Created                    DATE               DEFAULT SYSDATE NOT NULL,
    CreatedBy                  NUMBER(10, 0)      NOT NULL,
    Updated                    DATE                DEFAULT SYSDATE NOT NULL,
    UpdatedBy                  NUMBER(10, 0)      NOT NULL,
    Name                       NVARCHAR2(60)      NOT NULL,
    DocumentNo                 NVARCHAR2(32),             
    ProcessType                CHAR(3),
    S_Resource_ID              NUMBER(10,0),
    IsRouting                  CHAR(1)            DEFAULT('Y'),
    Description                NVARCHAR2(255),
    Help                       NVARCHAR2(2000),
    AccessLevel                CHAR(1)            NOT NULL,
    EntityType                 CHAR(1)             DEFAULT 'D' NOT NULL,
    MPC_Order_Node_ID          NUMBER(10, 0),
    DurationUnit               CHAR(1)            NOT NULL,
    Author                     NVARCHAR2(20)      NOT NULL,
    Version                    NUMBER(10, 0)      NOT NULL,
    PublishStatus              CHAR(1)            NOT NULL,
    ValidFrom                  DATE,
    ValidTo                    DATE,
    Priority                   NUMBER(10, 0)      NOT NULL,
    Limit                      NUMBER(10, 0)      NOT NULL,
    Duration                   NUMBER(10, 0)      NOT NULL,
    Cost                       NUMBER              DEFAULT 0 NOT NULL,
    WorkingTime                NUMBER(10, 0)      NOT NULL,
    WaitingTime                NUMBER(10, 0)      NOT NULL,
    AD_WF_Responsible_ID       NUMBER(10, 0),
    AD_WorkflowProcessor_ID    NUMBER(10, 0),
    ValidateWorkflow           CHAR(1),
    Value                      NVARCHAR2(240),
    CHECK (IsActive in ('Y','N')),
    CONSTRAINT MPC_Order_Workflow_Key PRIMARY KEY (MPC_Order_Workflow_ID, MPC_Order_ID)
)
;
-- 
-- TABLE: AD_Workflow 
--

--ALTER TABLE MPC_Order_Workflow ADD CONSTRAINT ADWFResponsibleMPCOrderWF 
--    FOREIGN KEY (AD_WF_Responsible_ID)
--    REFERENCES AD_WF_Responsible(AD_WF_Responsible_ID)
--;

ALTER TABLE MPC_Order_Workflow ADD CONSTRAINT ADWorkflowProcessorMPCOrderWF 
    FOREIGN KEY (AD_WorkflowProcessor_ID)
    REFERENCES AD_WorkflowProcessor(AD_WorkflowProcessor_ID) ON DELETE SET NULL
;

ALTER TABLE MPC_Order_Workflow ADD CONSTRAINT ADClientMPCOrderWF
    FOREIGN KEY (AD_Client_ID)
    REFERENCES AD_Client(AD_Client_ID)
;

ALTER TABLE MPC_Order_Workflow ADD CONSTRAINT ADOrgMPCOrderWF 
    FOREIGN KEY (AD_Org_ID)
    REFERENCES AD_Org(AD_Org_ID)
;
ALTER TABLE MPC_Order_Workflow ADD CONSTRAINT ADWorkflowMPCOrderWF
    FOREIGN KEY (AD_Workflow_ID)
    REFERENCES AD_Workflow(AD_Workflow_ID)
;
ALTER TABLE MPC_Order_Workflow ADD CONSTRAINT MPCOrderMPCOrderWF
    FOREIGN KEY (MPC_Order_ID)
    REFERENCES MPC_Order(MPC_Order_ID)
;
ALTER TABLE MPC_Order_Workflow ADD CONSTRAINT ADWFNodeMPCOrderWF
    FOREIGN KEY (MPC_Order_Node_ID)
    REFERENCES MPC_Order_Node(MPC_Order_Node_ID)
;
ALTER TABLE MPC_Order_Workflow ADD CONSTRAINT ADTableMPCOrderWF 
    FOREIGN KEY (AD_Table_ID)
    REFERENCES AD_Table(AD_Table_ID)
;
