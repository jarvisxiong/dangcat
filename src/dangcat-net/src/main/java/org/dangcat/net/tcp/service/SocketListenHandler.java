package org.dangcat.net.tcp.service;

import org.apache.log4j.Logger;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.net.event.DatagramReceiveListener;

import java.io.IOException;
import java.net.Socket;

public class SocketListenHandler extends Thread {
    private static final Logger logger = Logger.getLogger(SocketListenHandler.class);
    private SocketProcess socketProcess = null;

    public SocketListenHandler(Socket socket, DatagramReceiveListener datagramReceiveListener) {
        this.socketProcess = new SocketProcess(socket, datagramReceiveListener);
    }

    @Override
    public void run() {
        try {
            do {
                String commandLine = this.socketProcess.readCommand();
                if (!ValueUtils.isEmpty(commandLine)) {
                    if (logger.isDebugEnabled())
                        logger.debug(commandLine);
                    if (commandLine.startsWith(SocketProcess.SEND))
                        this.socketProcess.receiveData(commandLine);
                }
            } while (!this.socketProcess.isClosed());
        } catch (IOException e) {
            if (logger.isDebugEnabled())
                logger.error(this, e);
            else
                logger.error(e);
        } finally {
            this.socketProcess.close();
        }
    }
}
