package org.dangcat.persistence.entity;

class RelationLink
{
    protected Class<?> classType = null;
    protected RelationLink next = null;

    protected RelationLink(Class<?> classType)
    {
        this.classType = classType;
    }

    protected boolean contains(Class<?> classType)
    {
        if (this.classType.equals(classType))
            return true;
        return this.next != null && this.next.contains(classType);
    }

    protected RelationLink last()
    {
        RelationLink last = this;
        while (last.next != null)
            last = last.next;
        return last;
    }

    protected RelationLink remove(Class<?> classType)
    {
        if (this.classType.equals(classType))
            return this.next;
        if (this.next != null)
            this.next = this.next.remove(classType);
        return this;
    }
}
