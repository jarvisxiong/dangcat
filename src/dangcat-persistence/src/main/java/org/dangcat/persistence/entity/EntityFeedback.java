package org.dangcat.persistence.entity;

import org.dangcat.framework.exception.ServiceException;
import org.dangcat.framework.exception.ServiceInformation;

import java.util.Collection;

class EntityFeedback {
    protected static ServiceException findServiceException(EntityBase entityBase, Integer messageId) {
        ServiceException found = null;
        if (entityBase.getServiceExceptionList() != null) {
            for (ServiceException serviceException : entityBase.getServiceExceptionList()) {
                if (serviceException.getMessageId().equals(messageId)) {
                    found = serviceException;
                    break;
                }
            }
        }
        if (found == null) {
            EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entityBase.getClass());
            if (entityMetaData != null) {
                Collection<Relation> relationCollection = entityMetaData.getRelations();
                if (relationCollection != null) {
                    for (Relation relation : relationCollection) {
                        Collection<Object> children = relation.getMembers(entityBase);
                        if (children != null && children.size() > 0) {
                            for (Object child : children) {
                                if (child instanceof EntityBase) {
                                    found = findServiceException((EntityBase) child, messageId);
                                    if (found != null)
                                        return found;
                                }
                            }
                        }
                    }
                }
            }
        }
        return found;
    }

    protected static ServiceInformation findServiceInformation(EntityBase entityBase, Integer messageId) {
        ServiceInformation found = null;
        if (entityBase.getServiceInformationList() != null) {
            for (ServiceInformation serviceInformation : entityBase.getServiceInformationList()) {
                if (serviceInformation.getMessageId().equals(messageId)) {
                    found = serviceInformation;
                    break;
                }
            }
        }
        if (found == null) {
            EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entityBase.getClass());
            if (entityMetaData != null) {
                Collection<Relation> relationCollection = entityMetaData.getRelations();
                if (relationCollection != null) {
                    for (Relation relation : relationCollection) {
                        Collection<Object> children = relation.getMembers(entityBase);
                        if (children != null && children.size() > 0) {
                            for (Object child : children) {
                                if (child instanceof EntityBase) {
                                    found = findServiceInformation((EntityBase) child, messageId);
                                    if (found != null)
                                        return found;
                                }
                            }
                        }
                    }
                }
            }
        }
        return found;
    }

    protected static boolean hasError(EntityBase entityBase) {
        if (entityBase.getServiceExceptionList() != null && entityBase.getServiceExceptionList().size() > 0)
            return true;

        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entityBase.getClass());
        if (entityMetaData != null) {
            Collection<Relation> relationCollection = entityMetaData.getRelations();
            if (relationCollection != null) {
                for (Relation relation : relationCollection) {
                    Collection<Object> children = relation.getMembers(entityBase);
                    if (children != null && children.size() > 0) {
                        for (Object child : children) {
                            if (child instanceof EntityBase && hasError((EntityBase) child))
                                return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    protected static boolean hasInformation(EntityBase entityBase) {
        if (entityBase.getServiceInformationList() != null && entityBase.getServiceInformationList().size() > 0)
            return true;

        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entityBase.getClass());
        if (entityMetaData != null) {
            Collection<Relation> relationCollection = entityMetaData.getRelations();
            if (relationCollection != null) {
                for (Relation relation : relationCollection) {
                    Collection<Object> children = relation.getMembers(entityBase);
                    if (children != null && children.size() > 0) {
                        for (Object child : children) {
                            if (child instanceof EntityBase && hasInformation((EntityBase) child))
                                return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
