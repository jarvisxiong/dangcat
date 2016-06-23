package org.dangcat.persistence.model;

import org.apache.log4j.Logger;
import org.dangcat.framework.pool.SessionException;
import org.dangcat.persistence.orm.Session;
import org.dangcat.persistence.orm.SessionFactory;

/**
 * ±Ìπ‹¿Ì∆˜°£
 *
 * @author dangcat
 */
class TableManagerBase {
    protected static final Logger logger = Logger.getLogger(TableManager.class);

    protected Session openSession(String databaseName) throws SessionException {
        return SessionFactory.getInstance().openSession(databaseName);
    }
}
