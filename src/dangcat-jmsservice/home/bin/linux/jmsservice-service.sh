#!/bin/sh
#
# Comments to support chkconfig on RedHat Linux
# chkconfig: - 20 80
# description: Starts and stops DangCat JMS Service. \
#	       used to provide JMS services.

SERVICE_NAME=dangcat-jmsservice
SERVICE_DISPLAYNAME="DangCat JMS Service"
SERVICE_CONFIGNAME=jmsservice
COMMAND=$1
#RUN_AS_USER=dangcat
if [ "x$COMMAND" = "x" ]; then
    COMMAND=console
fi

#You must set the home path when run as service 
DANGCAT_HOME=$(cd "$(dirname "$0")/.."; pwd)

SERVICE_SHELL="$DANGCAT_HOME/bin/jsw/bin/service.sh"
if [ ! -f "$SERVICE_SHELL" ]; then
    echo "The shell file $DANGCAT_HOME/bin/service.sh is not exist."
else
    PATH=$PATH:$DANGCAT_HOME/bin
    if [ ! -x "$SERVICE_SHELL" ]; then
        chmod +x "$SERVICE_SHELL"
    fi
    source "$SERVICE_SHELL"
fi

exit 0
