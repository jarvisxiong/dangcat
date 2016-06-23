package org.dangcat.persistence.index;

class Entry<K, V>
{
    protected boolean color = BinaryTree.BLACK;
    protected K key;
    protected Entry<K, V> left = null;
    protected Entry<K, V> parent;
    protected Entry<K, V> right = null;
    protected V value;

    Entry(K key, V value, Entry<K, V> parent)
    {
        this.key = key;
        this.value = value;
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof Entry<?, ?>)
        {
            Entry<?, ?> entry = (Entry<?, ?>) o;
            return valEquals(key, entry.getKey()) && valEquals(value, entry.getValue());
        }
        return false;
    }

    protected K getKey()
    {
        return key;
    }

    protected V getValue()
    {
        return value;
    }

    public int hashCode()
    {
        int keyHash = (key == null ? 0 : key.hashCode());
        int valueHash = (value == null ? 0 : value.hashCode());
        return keyHash ^ valueHash;
    }

    protected V setValue(V value)
    {
        V oldValue = this.value;
        this.value = value;
        return oldValue;
    }

    @Override
    public String toString()
    {
        return key + "=" + value;
    }

    private boolean valEquals(Object o1, Object o2)
    {
        return (o1 == null ? o2 == null : o1.equals(o2));
    }
}