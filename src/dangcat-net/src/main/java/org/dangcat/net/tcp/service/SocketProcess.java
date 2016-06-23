package org.dangcat.net.tcp.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.net.event.DatagramEvent;
import org.dangcat.net.event.DatagramReceiveListener;

class SocketProcess
{
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    private static final Logger logger = Logger.getLogger(SocketProcess.class);
    public static final String SEND = "SEND";
    private byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
    private boolean closed = false;
    private DatagramReceiveListener datagramReceiveListener = null;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;
    private Socket socket = null;

    protected SocketProcess(Socket socket)
    {
        this(socket, null);
    }

    protected SocketProcess(Socket socket, DatagramReceiveListener datagramReceiveListener)
    {
        this.socket = socket;
        this.datagramReceiveListener = datagramReceiveListener;
    }

    protected void close()
    {
        try
        {
            if (this.socket != null)
                this.socket.close();
        }
        catch (IOException e)
        {
        }
        this.socket = null;
    }

    private InputStream getInputStream() throws IOException
    {
        if (this.inputStream == null)
            this.inputStream = new BufferedInputStream(this.socket.getInputStream());
        return this.inputStream;
    }

    private OutputStream getOutputStream() throws IOException
    {
        if (this.outputStream == null)
            this.outputStream = new BufferedOutputStream(this.socket.getOutputStream());
        return this.outputStream;
    }

    protected boolean isClosed()
    {
        return this.closed || this.socket.isClosed();
    }

    protected String readCommand() throws IOException
    {
        InputStream inputStream = this.getInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int data;
        while ((data = inputStream.read()) != '\n')
            outputStream.write(data);
        return new String(outputStream.toByteArray());
    }

    protected void receiveData(String commandLine) throws IOException
    {
        if (this.datagramReceiveListener == null)
            return;

        int sendLength = ValueUtils.parseInt(commandLine.substring(SEND.length() + 1));
        if (sendLength == 0)
            return;

        InputStream inputStream = this.getInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        long count = 0;
        int length = 0;
        do
        {
            int bufferLength = (int) ((sendLength - count > this.buffer.length) ? this.buffer.length : (sendLength - count));
            length = inputStream.read(this.buffer, 0, bufferLength);
            if (length > 0)
            {
                outputStream.write(this.buffer, 0, length);
                count += length;
            }
        } while (length > 0 && count < sendLength);

        byte[] receivedBytes = outputStream.toByteArray();
        if (receivedBytes != null)
        {
            if (receivedBytes.length == sendLength)
            {
                DatagramEvent datagramEvent = new DatagramEvent(this.socket.getInetAddress(), this.socket.getPort(), receivedBytes);
                datagramEvent.setLocalAddress(this.socket.getLocalAddress());
                datagramEvent.setLocalPort(this.socket.getLocalPort());
                this.datagramReceiveListener.onReceive(datagramEvent);
            }
            else
            {
                if (logger.isDebugEnabled())
                    logger.error("receive Data length " + receivedBytes.length + ", but expect " + sendLength);
            }
        }
    }

    protected void sendCommand(String command) throws IOException
    {
        if (logger.isDebugEnabled())
            logger.debug(command);
        OutputStream outputStream = this.getOutputStream();
        outputStream.write(command.getBytes());
        outputStream.write('\n');
        outputStream.flush();
    }

    protected void sendData(byte[] data) throws IOException
    {
        this.sendData(new ByteArrayInputStream(data));
    }

    protected void sendData(InputStream inputStream) throws IOException
    {
        this.sendCommand(SEND + " " + inputStream.available());

        OutputStream outputStream = this.socket.getOutputStream();
        IOUtils.copy(inputStream, outputStream);
        outputStream.flush();
    }

    protected void setClosed(boolean closed)
    {
        this.closed = closed;
    }
}
