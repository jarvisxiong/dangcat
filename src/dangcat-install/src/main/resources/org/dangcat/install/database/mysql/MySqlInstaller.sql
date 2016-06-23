create database if not exists ${databaseName};
use ${databaseName};
<#if scripts??>
<#if characterSet??>
SET NAMES ${characterSet};
</#if>
<#list scripts as script>
source ${script};
</#list>
</#if>
DROP USER root@'127.0.0.1';
DROP USER ''@'localhost';
<#if user != "root">
RENAME USER root@'localhost' TO ${user}@'localhost';
</#if>
SET PASSWORD FOR ${user}@'localhost' = PASSWORD('${password}');
GRANT ALL PRIVILEGES ON *.* TO ${user}@'%' IDENTIFIED BY '${password}' WITH GRANT OPTION;
FLUSH PRIVILEGES;