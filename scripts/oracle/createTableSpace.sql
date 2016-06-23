<<<<<<< HEAD

SET  SERVEROUTPUT ON SIZE  100000;

BEGIN
DECLARE
cnt integer := 0;
BEGIN
    SELECT 1 INTO cnt FROM dual WHERE exists(SELECT * FROM ALL_USERS WHERE USERNAME = UPPER(TRIM('dangcat')));
    IF cnt != 0  THEN
        DECLARE
        s  VARCHAR2(500);
        BEGIN
            s := 'DROP USER dangcat CASCADE';
            DBMS_OUTPUT.PUT_LINE(s);
            EXECUTE IMMEDIATE  s;
        END;        
    END IF;
exception
    WHEN no_data_found  THEN
        DBMS_OUTPUT.PUT_LINE(cnt);
    END;
END;
/


BEGIN
DECLARE
cnt integer := 0;
BEGIN
    SELECT 1 INTO cnt FROM dual WHERE exists(SELECT * FROM user_tablespaces WHERE tablespace_name = UPPER(TRIM('dangcat')));
    IF cnt != 0  THEN
        DECLARE
        s  VARCHAR2(500);
        BEGIN
            s := 'DROP TABLESPACE dangcat  INCLUDING CONTENTS  CASCADE  CONSTRAINTS';
			--'DROP TABLESPACE dhcp  INCLUDING CONTENTS AND DATAFILES CASCADE  CONSTRAINTS';
            DBMS_OUTPUT.PUT_LINE(s);
            EXECUTE IMMEDIATE  s;
        END;        
    END IF;
exception
    WHEN no_data_found  THEN
        DBMS_OUTPUT.PUT_LINE(cnt);
    END;
END;
/

create tablespace dangcat
  datafile 'D:/Work/Database/oracle/oradata/orcl/dangcat.dbf' size 128M REUSE  
   AUTOEXTEND ON NEXT 20M 
   ONLINE
   PERMANENT;



CREATE USER dangcat  PROFILE "DEFAULT" 
    IDENTIFIED BY "dangcat2014" DEFAULT TABLESPACE dangcat
    ACCOUNT UNLOCK;

GRANT "CONNECT" TO dangcat;
GRANT "RESOURCE" TO dangcat;
GRANT UNLIMITED TABLESPACE TO dangcat;
GRANT "DBA" TO dangcat;
GRANT ALTER ANY INDEX TO dangcat;
GRANT ALTER ANY TABLE TO dangcat;
GRANT ALTER ANY PROCEDURE TO dangcat;
GRANT CREATE ANY INDEX TO dangcat;
GRANT CREATE ANY PROCEDURE TO dangcat;
GRANT CREATE ANY TABLE TO dangcat;
GRANT DROP ANY INDEX TO dangcat;
GRANT DROP ANY PROCEDURE TO dangcat;
GRANT DROP ANY TABLE TO dangcat;
GRANT EXECUTE ANY PROCEDURE TO dangcat;
GRANT SELECT ANY TABLE TO dangcat; 


BEGIN
DBMS_OUTPUT.PUT_LINE('-------');
DBMS_OUTPUT.PUT_LINE('END-ORA');
DBMS_OUTPUT.PUT_LINE('-------');
END;
/

SPOOL OFF
/

QUIT
/
=======

SET  SERVEROUTPUT ON SIZE  100000;

BEGIN
DECLARE
cnt integer := 0;
BEGIN
    SELECT 1 INTO cnt FROM dual WHERE exists(SELECT * FROM ALL_USERS WHERE USERNAME = UPPER(TRIM('dangcat')));
    IF cnt != 0  THEN
        DECLARE
        s  VARCHAR2(500);
        BEGIN
            s := 'DROP USER dangcat CASCADE';
            DBMS_OUTPUT.PUT_LINE(s);
            EXECUTE IMMEDIATE  s;
        END;        
    END IF;
exception
    WHEN no_data_found  THEN
        DBMS_OUTPUT.PUT_LINE(cnt);
    END;
END;
/


BEGIN
DECLARE
cnt integer := 0;
BEGIN
    SELECT 1 INTO cnt FROM dual WHERE exists(SELECT * FROM user_tablespaces WHERE tablespace_name = UPPER(TRIM('dangcat')));
    IF cnt != 0  THEN
        DECLARE
        s  VARCHAR2(500);
        BEGIN
            s := 'DROP TABLESPACE dangcat  INCLUDING CONTENTS  CASCADE  CONSTRAINTS';
			--'DROP TABLESPACE dhcp  INCLUDING CONTENTS AND DATAFILES CASCADE  CONSTRAINTS';
            DBMS_OUTPUT.PUT_LINE(s);
            EXECUTE IMMEDIATE  s;
        END;        
    END IF;
exception
    WHEN no_data_found  THEN
        DBMS_OUTPUT.PUT_LINE(cnt);
    END;
END;
/

create tablespace dangcat
  datafile 'D:/Work/Database/oracle/oradata/orcl/dangcat.dbf' size 128M REUSE  
   AUTOEXTEND ON NEXT 20M 
   ONLINE
   PERMANENT;



CREATE USER dangcat  PROFILE "DEFAULT" 
    IDENTIFIED BY "dangcat2014" DEFAULT TABLESPACE dangcat
    ACCOUNT UNLOCK;

GRANT "CONNECT" TO dangcat;
GRANT "RESOURCE" TO dangcat;
GRANT UNLIMITED TABLESPACE TO dangcat;
GRANT "DBA" TO dangcat;
GRANT ALTER ANY INDEX TO dangcat;
GRANT ALTER ANY TABLE TO dangcat;
GRANT ALTER ANY PROCEDURE TO dangcat;
GRANT CREATE ANY INDEX TO dangcat;
GRANT CREATE ANY PROCEDURE TO dangcat;
GRANT CREATE ANY TABLE TO dangcat;
GRANT DROP ANY INDEX TO dangcat;
GRANT DROP ANY PROCEDURE TO dangcat;
GRANT DROP ANY TABLE TO dangcat;
GRANT EXECUTE ANY PROCEDURE TO dangcat;
GRANT SELECT ANY TABLE TO dangcat; 


BEGIN
DBMS_OUTPUT.PUT_LINE('-------');
DBMS_OUTPUT.PUT_LINE('END-ORA');
DBMS_OUTPUT.PUT_LINE('-------');
END;
/

SPOOL OFF
/

QUIT
/
>>>>>>> 9f04788feed41f13fb96e84f7b3e100e1f2b154c
