-----------------------------------------------------------------------------------------------------------------------------------
----1. 增加额外参数配置表
DECLARE

I_EXISTS   NUMBER;
BEGIN
  SELECT COUNT(1) INTO I_EXISTS  FROM USER_TABLES A WHERE A.TABLE_NAME = 'RISK_EXTRA_PARAM_CONFIG';
  
  IF I_EXISTS = 0 THEN
    EXECUTE IMMEDIATE '
                        CREATE TABLE RISK_EXTRA_PARAM_CONFIG
                        (
                          VC_PARAM_NAME        VARCHAR2(20) NOT NULL,
                          VC_CONFIG_TYPE       VARCHAR2(2) DEFAULT ''1'' NOT NULL,
                          VC_CONFIG_INFO       VARCHAR2(1000) DEFAULT '' '' NOT NULL,
                          VC_CONFIG_NEED_PARAM VARCHAR2(1000) DEFAULT '' '' NOT NULL
                        )
                      
                      ';
    EXECUTE IMMEDIATE '
             ALTER TABLE RISK_EXTRA_PARAM_CONFIG ADD PRIMARY KEY (VC_PARAM_NAME, VC_CONFIG_TYPE) ';
  ELSE
     EXECUTE IMMEDIATE 'DROP TABLE RISK_EXTRA_PARAM_CONFIG';
     EXECUTE IMMEDIATE '
                          CREATE TABLE RISK_EXTRA_PARAM_CONFIG
                        (
                          VC_PARAM_NAME        VARCHAR2(20) NOT NULL,
                          VC_CONFIG_TYPE       VARCHAR2(2) DEFAULT ''1'' NOT NULL,
                          VC_CONFIG_INFO       VARCHAR2(1000) DEFAULT '' '' NOT NULL,
                          VC_CONFIG_NEED_PARAM VARCHAR2(1000) DEFAULT '' '' NOT NULL
                        )
                      
                      ';
     EXECUTE IMMEDIATE '
             ALTER TABLE RISK_EXTRA_PARAM_CONFIG ADD PRIMARY KEY (VC_PARAM_NAME, VC_CONFIG_TYPE) ';
                     
  END IF;


END;
---------------------------------------------------------------------------------------------------------------------------------