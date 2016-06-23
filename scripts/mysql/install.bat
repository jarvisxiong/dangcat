set MYSQL_HOME="D:\Program Files\mysql\mysql-5.1.62-win32"
set DIRNAME=%~dp0
%MYSQL_HOME%\bin\mysqld install MySql-dangcat --defaults-file="%DIRNAME%\my.ini"