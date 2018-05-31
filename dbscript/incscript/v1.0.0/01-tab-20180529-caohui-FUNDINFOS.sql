-----------------------------------------------------------------------------------------------------------------------------------
----1、基金表增加字段I_STATIC_NUMBER 用来记录静态风控每个基金产生的有效条目计算结果数量,当分发基金时，按照数量排序分发，以便分发均匀
DECLARE
I_EXISTS   NUMBER;
BEGIN
  SELECT COUNT(1) INTO I_EXISTS FROM USER_TAB_COLS A WHERE A.TABLE_NAME = 'FUNDINFOS' AND A.COLUMN_NAME = 'I_STATIC_NUMBER';
  IF I_EXISTS = 0 THEN
    EXECUTE  IMMEDIATE 'ALTER TABLE FUNDINFOS ADD  (I_STATIC_NUMBER NUMBER DEFAULT 0 NOT NULL)' ;
  END IF;
  
END;
---------------------------------------------------------------------------------------------------------------------------------