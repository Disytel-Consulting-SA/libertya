-- 
-- TABLE: MPC_WF_Node 
--
DROP TABLE MPC_Order_Node CASCADE CONSTRAINTS;


CREATE TABLE MPC_Order_Node(
    MPC_Order_Node_ID           NUMBER(10, 0)      NOT NULL,
    AD_WF_Node_ID            NUMBER(10, 0)      NOT NULL,
    MPC_Order_ID             NUMBER(10, 0)      NOT NULL,
    AD_Client_ID             NUMBER(10, 0)      NOT NULL,
    AD_Org_ID                NUMBER(10, 0)      NOT NULL,
    AD_Column_ID             NUMBER(10, 0)      ,      
    AD_WF_NodeStandard_ID    NUMBER(10),
    C_BPartner_ID            NUMBER(10),
    IsMilestone              CHAR(1)      DEFAULT ('N'),
    IsSubcontracting         CHAR(1) DEFAULT ('N'),   
    S_Resource_ID            NUMBER(10),
    UnitsCycles              NUMBER DEFAULT 0,
    ValidFrom                DATE,
    ValidTo                  DATE,
    IsActive                 CHAR(1)             DEFAULT 'Y' NOT NULL,
    Created                  DATE                DEFAULT SYSDATE NOT NULL,
    CreatedBy                NUMBER(10, 0)      NOT NULL,
    Updated                  DATE                DEFAULT SYSDATE NOT NULL,
    UpdatedBy                NUMBER(10, 0)      NOT NULL,
    Name                     NVARCHAR2(60)      NOT NULL,
    Value                    NVARCHAR2(40)      NOT NULL,
    Description              NVARCHAR2(255),
    Help                     NVARCHAR2(2000),
    AD_Workflow_ID           NUMBER(10, 0)      NOT NULL,
    MPC_Order_Workflow_ID           NUMBER(10, 0)      NOT NULL,
    IsCentrallyMaintained    CHAR(1)             DEFAULT 'Y' NOT NULL,
    EntityType               CHAR(1)             DEFAULT 'D' NOT NULL,
    UserOverwrite            CHAR(1)             DEFAULT 'N' NOT NULL,
    AD_WF_Responsible_ID     NUMBER(10, 0),
    Xposition                NUMBER(10, 0)      NOT NULL,
    Yposition                NUMBER(10, 0)      NOT NULL,
    Action                   CHAR(1)            NOT NULL,
    AD_Window_ID             NUMBER(10, 0),
    AD_Form_ID               NUMBER(10, 0),
    AD_Task_ID               NUMBER(10, 0),
    AD_Process_ID            NUMBER(10, 0),
    AD_WF_Block_ID           NUMBER(10, 0),
    SubFlowExecution         CHAR(1)           NOT NULL,
    Workflow_ID              NUMBER(10, 0),
    AttributeName            NVARCHAR2(60),
    AttributeValue           NVARCHAR2(60),
    DocAction                CHAR(2)            NOT NULL,
    StartMode                CHAR(1)            NOT NULL,
    FinishMode               CHAR(1)            NOT NULL,
    JoinElement              CHAR(1)            NOT NULL,
    SplitElement             CHAR(1)            NOT NULL,
    Limit                    NUMBER(10, 0)      NOT NULL,
    Priority                 NUMBER(10, 0)      NOT NULL,
    Duration                 NUMBER(10, 0)      NOT NULL,
    Cost                     NUMBER              DEFAULT 0 NOT NULL,
    WorkingTime              NUMBER(10, 0)      NOT NULL,
    WaitingTime              NUMBER(10, 0)      NOT NULL,
    AD_Image_ID              NUMBER(10, 0),
    CHECK (IsActive in ('Y','N')),
    CHECK (IsCentrallyMaintained in ('Y','N')),
    CHECK (UserOverwrite in ('Y','N')),
    CONSTRAINT MPC_Order_Node_Key PRIMARY KEY (MPC_Order_Node_ID, MPC_Order_ID)
)
;

-- 
-- INDEX: AD_WF_Node_Workflow 
--

CREATE INDEX MPC_Order_Node_Workflow ON MPC_Order_Node(MPC_Order_ID)
;

-- 
-- TABLE: AD_WF_Node 
--

ALTER TABLE MPC_Order_Node ADD CONSTRAINT ADFormMPCOrderNode 
    FOREIGN KEY (AD_Form_ID)
    REFERENCES AD_Form(AD_Form_ID)  ON DELETE CASCADE
;

ALTER TABLE MPC_Order_Node ADD CONSTRAINT ADImageMPCOrderNode 
    FOREIGN KEY (AD_Image_ID)
    REFERENCES AD_Image(AD_Image_ID) ON DELETE CASCADE
;

ALTER TABLE MPC_Order_Node ADD CONSTRAINT ADProcessMPCOrderNode 
    FOREIGN KEY (AD_Process_ID)
    REFERENCES AD_Process(AD_Process_ID) ON DELETE CASCADE
;

ALTER TABLE MPC_Order_Node ADD CONSTRAINT ADTaskMPCOrderNode 
    FOREIGN KEY (AD_Task_ID)
    REFERENCES AD_Task(AD_Task_ID)  ON DELETE CASCADE
;

ALTER TABLE MPC_Order_Node ADD CONSTRAINT ADWFBlockMPCOrderNode 
    FOREIGN KEY (AD_WF_Block_ID)
    REFERENCES AD_WF_Block(AD_WF_Block_ID) ON DELETE SET NULL
;

--ALTER TABLE MPC_Order_Node ADD CONSTRAINT ADWFResponsibleMPCOrderNode 
--    FOREIGN KEY (AD_WF_Responsible_ID)
--    REFERENCES AD_WF_Responsible(AD_WF_Responsible_ID)  ON DELETE CASCADE
---;

ALTER TABLE MPC_Order_Node ADD CONSTRAINT ADWindowMPCOrderNode 
    FOREIGN KEY (AD_Window_ID)
    REFERENCES AD_Window(AD_Window_ID)  ON DELETE CASCADE
;

ALTER TABLE MPC_Order_Node ADD CONSTRAINT ADWorkflowMPCOrderNode
    FOREIGN KEY (AD_Workflow_ID)
    REFERENCES AD_Workflow(AD_Workflow_ID) ON DELETE CASCADE
;

ALTER TABLE MPC_Order_Node ADD CONSTRAINT MPCOrderWorkflow_MPCOrderNode 
    FOREIGN KEY (MPC_Order_Workflow_ID)
    REFERENCES MPC_Order_Workflow(MPC_Order_Workflow_ID) ON DELETE CASCADE
;

ALTER TABLE MPC_Order_Node ADD CONSTRAINT Workflow_MPCOrderNode 
    FOREIGN KEY (Workflow_ID)
    REFERENCES AD_Workflow(AD_Workflow_ID)  ON DELETE CASCADE
;

ALTER TABLE MPC_Order_Node ADD CONSTRAINT ADClientMPCOrderNode 
    FOREIGN KEY (AD_Client_ID)
    REFERENCES AD_Client(AD_Client_ID)  ON DELETE CASCADE
;

ALTER TABLE MPC_Order_Node ADD CONSTRAINT ADOrgMPCOrderNode
    FOREIGN KEY (AD_Org_ID)
    REFERENCES AD_Org(AD_Org_ID)  ON DELETE CASCADE
;
ALTER TABLE MPC_Order_Node ADD CONSTRAINT SResourceMPCOrderNode
    FOREIGN KEY (S_Resource_ID)
    REFERENCES S_Resource(S_Resource_ID)  ON DELETE CASCADE
;
ALTER TABLE MPC_Order_Node ADD CONSTRAINT MPCOrderMPCOrderNode 
    FOREIGN KEY (MPC_Order_ID)
    REFERENCES MPC_Order(MPC_Order_ID)  ON DELETE CASCADE
;

ALTER TABLE MPC_Order_Node ADD CONSTRAINT ADWFNodeMPCOrderNode
    FOREIGN KEY (AD_WF_Node_ID)
    REFERENCES AD_WF_Node(AD_WF_Node_ID)  ON DELETE CASCADE
;
ALTER TABLE MPC_Order_Node ADD CONSTRAINT CBPartnerMPCOrderNode 
    FOREIGN KEY (C_BPartner_ID)
    REFERENCES C_BPartner(C_BPartner_ID)  ON DELETE CASCADE
;


