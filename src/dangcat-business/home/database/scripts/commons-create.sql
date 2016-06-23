/*==============================================================*/
/* Table: OperatorGroup                                         */
/*==============================================================*/
create table OperatorGroup
(
   Id                   int not null,
   Name                 varchar(20) not null,
   Description          varchar(200),
   ParentId             integer,
   primary key (Id)
);

/*==============================================================*/
/* Table: OperatorInfo                                          */
/*==============================================================*/
create table OperatorInfo
(
   Id                   int not null,
   No                   varchar(20) not null,
   Password             varchar(32) not null,
   Name                 varchar(20) not null,
   Description          varchar(200),
   RoleId               int not null,
   GroupId              int not null,
   Mobile               varchar(20),
   Tel                  varchar(20),
   Email                varchar(30),
   RegisterTime         datetime,
   ExpiryTime           date,
   UseAble              tinyint,
   primary key (Id)
);

/*==============================================================*/
/* Index: IX_OperatorInfo_Name                                  */
/*==============================================================*/
create index IX_OperatorInfo_Name on OperatorInfo
(
   Name
);

/*==============================================================*/
/* Index: IX_OperatorInfo_No                                    */
/*==============================================================*/
create index IX_OperatorInfo_No on OperatorInfo
(
   No
);

/*==============================================================*/
/* Index: IX_OperatorInfo_OperatorGroup                         */
/*==============================================================*/
create index IX_OperatorInfo_OperatorGroup on OperatorInfo
(
   GroupId,
   No
);

/*==============================================================*/
/* Table: RoleInfo                                              */
/*==============================================================*/
create table RoleInfo
(
   Id                   int not null,
   Name                 varchar(20) not null,
   Description          varchar(200),
   primary key (Id)
);

/*==============================================================*/
/* Table: RolePermission                                        */
/*==============================================================*/
create table RolePermission
(
   RoleId               int not null,
   PermissionId         int not null,
   primary key (RoleId, PermissionId)
);

alter table OperatorInfo add constraint FK_OperatorInfo_GroupId foreign key (GroupId)
      references OperatorGroup (Id) on delete restrict on update restrict;

alter table OperatorInfo add constraint FK_OperatorInfo_RoleId foreign key (RoleId)
      references RoleInfo (Id) on delete restrict on update restrict;

alter table RolePermission add constraint FK_RolePermission_RoleId foreign key (RoleId)
      references RoleInfo (Id) on delete restrict on update restrict;
