@ECHO off
IF "%OS%" == "Windows_NT" (
    SET DIRNAME=%~dp0%
)ELSE (
    SET DIRNAME=.
)
   
SET SERVICE_NAME=dangcat-jmsservice
SET SERVICE_DISPLAYNAME=DangCat JMS Service
SET SERVICE_DESCRIPTION=Starts and stops DangCat JMS Service. used to provide JMS services.
SET SERVICE_CONFIGNAME=jmsservice
SET COMMAND=%1
IF "%COMMAND%" == "" SET COMMAND=console
SET DANGCAT_HOME=%DIRNAME%..
CALL "%DIRNAME%/jsw/bin/service.bat"