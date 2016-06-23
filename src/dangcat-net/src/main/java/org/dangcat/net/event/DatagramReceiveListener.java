package org.dangcat.net.event;

/**
 * 接收数据侦听接口。
 *
 * @author dangcat
 */
public interface DatagramReceiveListener {
    /**
     * 接收数据。
     *
     * @param datagramEvent 接收事件。
     */
    void onReceive(DatagramEvent datagramEvent);
}
