package org.dangcat.persistence.entity;

import java.util.List;

public interface EntityLoader<T>
{
    public List<T> load(EntityManager entityManager);
}
