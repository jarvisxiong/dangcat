package org.dangcat.net.rfc.service.impl;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

class PacketIdentifier {
    private static final int INIT_VALUE = 1;
    private static final int MAX_VALUE = 255;
    private int identifier = INIT_VALUE;
    private Map<InetAddress, Set<Integer>> inProcessingMap = new HashMap<InetAddress, Set<Integer>>();
    private Integer packetType;

    PacketIdentifier(Integer packetType) {
        this.packetType = packetType;
    }

    protected synchronized boolean addProcess(InetAddress inetAddress, Integer identifier) {
        Set<Integer> inProcessingSet = this.inProcessingMap.get(inetAddress);
        if (inProcessingSet == null) {
            inProcessingSet = new TreeSet<Integer>();
            this.inProcessingMap.put(inetAddress, inProcessingSet);
        }
        if (!inProcessingSet.contains(identifier))
            return inProcessingSet.add(identifier);
        return true;
    }

    protected Integer getPacketType() {
        return packetType;
    }

    protected int nextIdentifier() {
        synchronized (this.packetType) {
            if (this.identifier >= MAX_VALUE)
                this.identifier = INIT_VALUE;
            return this.identifier++;
        }
    }

    protected synchronized void removeProcess(InetAddress inetAddress, Integer identifier) {
        Set<Integer> inProcessingSet = this.inProcessingMap.get(inetAddress);
        if (inProcessingSet != null) {
            inProcessingSet.remove(identifier);
            if (inProcessingSet.size() == 0)
                this.inProcessingMap.remove(inProcessingSet);
        }
    }
}
