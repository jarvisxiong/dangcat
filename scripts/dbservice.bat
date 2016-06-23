@echo off
if /i "%1"=="start" call :start %2
if /i "%1"=="stop" call :stop %2
goto end

:start
if /i "%1"=="Oracle" call :startOracle
if /i "%1"=="SqlServer" call :startSqlServer
if "%1"=="" (
	call :startOracle
	call :startSqlServer
)
goto :eof

:stop
if "%1"=="Oracle" call :stopOracle
if "%1"=="SqlServer" call :stopSqlServer
if "%1"=="" (
	call :stopOracle
	call :stopSqlServer
)
goto :eof

:startOracle
echo "The oracle database is starting..."
net start OracleDBConsoleorcl
net start OracleServiceORCL
net start OracleOraDb11g_home2TNSListener
goto :eof

:stopOracle
echo "The oracle database is stopped."
net stop OracleOraDb11g_home2TNSListener
net stop OracleServiceORCL
net stop OracleDBConsoleorcl
goto :eof

:startSqlServer
echo "The sqlserver database is starting..."
net start MSSQLSERVER
net start SQLBrowser
goto :eof

:stopSqlServer
echo "The sqlserver database is stopped."
net stop SQLBrowser
net stop MSSQLSERVER
goto :eof

:end