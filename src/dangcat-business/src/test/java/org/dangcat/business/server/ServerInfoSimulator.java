package org.dangcat.business.server;

import org.dangcat.persistence.simulate.table.EntityData;

public class ServerInfoSimulator extends EntityData<ServerInfoQuery> {
    public ServerInfoSimulator() {
        super(ServerInfoQuery.class);
    }
}