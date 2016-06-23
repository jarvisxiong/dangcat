package org.dangcat.persistence.entity;

import org.apache.log4j.Logger;
import org.dangcat.persistence.domain.EntityData;

public class MultiThreadSaver extends Thread {
    private static final Logger logger = Logger.getLogger(MultiThreadSaver.class);
    private int count = 1000;
    private EntityManager entityManager = null;
    private boolean finished = false;

    public MultiThreadSaver(EntityManager entityManager, int count) {
        this.entityManager = entityManager;
        this.count = count;
    }

    public boolean isFinished() {
        return this.finished;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < this.count; i++) {
                EntityData entityData = EntityDataUtils.createEntityData(i);
                this.entityManager.save(entityData);
            }
        } catch (Exception e) {
            logger.error(this, e);
        } finally {
            this.finished = true;
        }
    }
}
