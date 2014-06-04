CREATE TABLE FIREBUILDING 
(
  F_NAME VARCHAR2(20) NOT NULL 
);

CREATE TABLE BUILDING 
(
  bldg_id varchar2(20) NOT NULL PRIMARY KEY
, bldg_name VARCHAR2(30) 
, vertices_no NUMBER(20) 
, coords SDO_GEOMETRY
);

INSERT INTO USER_SDO_GEOM_METADATA VALUES('building','coords',
SDO_DIM_ARRAY(SDO_DIM_ELEMENT('X',0,100,1),SDO_DIM_ELEMENT('Y',0,100,1)),NULL);

CREATE index index1 on building(coords) indextype is MDSYS.SPATIAL_INDEX;

CREATE TABLE firehydrant 
(
  fh_id varchar2(20) NOT NULL PRIMARY KEY, 
  coords SDO_GEOMETRY
 
);

INSERT INTO USER_SDO_GEOM_METADATA VALUES('firehydrant','coords',
SDO_DIM_ARRAY(SDO_DIM_ELEMENT('X',0,100,1),SDO_DIM_ELEMENT('Y',0,100,1)),NULL);

CREATE index index2 on firehydrant(coords) indextype is MDSYS.SPATIAL_INDEX;