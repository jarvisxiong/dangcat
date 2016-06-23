package org.dangcat.net.rfc.template.xml;

import java.util.ArrayList;

public class Rules extends ArrayList<Rule>
{
    private static final long serialVersionUID = 1L;
    private String packetType;

    public String getPacketType()
    {
        return packetType;
    }

    public void setPacketType(String packetType)
    {
        this.packetType = packetType;
    }
}
