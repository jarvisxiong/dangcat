package org.dangcat.persistence.entity;

import org.dangcat.persistence.domain.UserServerBind;
import org.dangcat.persistence.model.DataState;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.simulate.SimulateUtils;

import java.util.List;

class EntityInheritUtils extends SimulateUtils {
    protected static void createData(List<UserServerBind> entityList, int size) {
        for (int i = 1; i <= size; i++) {
            UserServerBind userServerBind = new UserServerBind();
            String account = "0000000000" + i;
            userServerBind.setAccount("ACCOUNT " + account.substring(account.length() - 10));
            userServerBind.setServiceId(i);
            userServerBind.setBindItemId(i);
            userServerBind.setBindTime("BindTime " + i);
            userServerBind.setValue("value " + i);
            userServerBind.setDataState(DataState.Insert);
            entityList.add(userServerBind);
        }
    }

    public static Table getTable() {
        return EntityHelper.getEntityMetaData(UserServerBind.class).getTable();
    }
}
