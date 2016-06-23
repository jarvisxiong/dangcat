package org.dangcat.business.server;

import org.dangcat.business.service.DataFilter;
import org.dangcat.commons.resource.Resources;
import org.dangcat.commons.serialize.annotation.Serialize;
import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Table;
import org.dangcat.persistence.filter.FilterExpress;
import org.dangcat.persistence.filter.FilterGroup;
import org.dangcat.persistence.filter.FilterType;
import org.dangcat.persistence.filter.FilterUnit;

/**
 * The service filter for ServerInfo.
 * @author ${authorName}
 * 
 */
@Table("ServerInfo")
@Resources( { ServerInfoQuery.class })
public class ServerInfoFilter extends DataFilter
{
    @Column(index = 1, displaySize = 32)
    private String ip;

    @Override
    @Serialize(ignore = true)
    public FilterExpress getFilterExpress()
    {
        FilterGroup filterGroup = new FilterGroup();
        if (this.getIp() != null)
            filterGroup.add(new FilterUnit(ServerInfoQuery.Ip, FilterType.like, this.getIp()));
        return this.getFilterExpress(filterGroup);
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }
}
