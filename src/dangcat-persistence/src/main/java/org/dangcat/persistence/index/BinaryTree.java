package org.dangcat.persistence.index;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class BinaryTree<K, V>
{
    protected static final boolean BLACK = true;
    protected static final boolean RED = false;
    private Comparator<? super K> comparator;
    private Map<K, Entry<K, V>> entryMap = new HashMap<K, Entry<K, V>>();
    private Entry<K, V> root = null;
    private int size = 0;

    public BinaryTree()
    {
    }

    public BinaryTree(Comparator<Object> comparator)
    {
        this.comparator = comparator;
    }

    public void clear()
    {
        this.size = 0;
        this.root = null;
        this.entryMap.clear();
    }

    private boolean colorOf(Entry<K, V> entry)
    {
        return entry == null ? BLACK : entry.color;
    }

    @SuppressWarnings("unchecked")
    private int compare(K srcKey, K dstKey)
    {
        if (this.comparator != null)
            return this.comparator.compare(srcKey, dstKey);

        Comparable<? super K> comparable = (Comparable<? super K>) srcKey;
        return comparable.compareTo(dstKey);
    }

    public boolean containsKey(K key)
    {
        return this.getEntry(key) != null;
    }

    private void deleteEntry(Entry<K, V> entry)
    {
        this.size--;
        this.entryMap.remove(entry.key);

        if (entry.left != null && entry.right != null)
        {
            Entry<K, V> findEntry = this.successor(entry);
            entry.key = findEntry.key;
            entry.value = findEntry.value;
            this.entryMap.put(entry.key, entry);
            entry = findEntry;
        }

        Entry<K, V> replacement = (entry.left != null ? entry.left : entry.right);
        if (replacement != null)
        {
            replacement.parent = entry.parent;
            if (entry.parent == null)
                root = replacement;
            else if (entry == entry.parent.left)
                entry.parent.left = replacement;
            else
                entry.parent.right = replacement;

            entry.left = entry.right = entry.parent = null;

            if (entry.color == BLACK)
                this.fixAfterDeletion(replacement);
        }
        else if (entry.parent == null)
            this.root = null;
        else
        {
            if (entry.color == BLACK)
                this.fixAfterDeletion(entry);

            if (entry.parent != null)
            {
                if (entry == entry.parent.left)
                    entry.parent.left = null;
                else if (entry == entry.parent.right)
                    entry.parent.right = null;
                entry.parent = null;
            }
        }
    }

    public Set<Entry<K, V>> find(Comparable<Object> comparable)
    {
        Set<Entry<K, V>> entrySet = new LinkedHashSet<Entry<K, V>>();
        this.find(this.root, comparable, entrySet);
        return entrySet;
    }

    private void find(Entry<K, V> entry, Comparable<Object> comparable, Set<Entry<K, V>> entryList)
    {
        if (entry != null)
        {
            int result = comparable.compareTo(entry.key);
            if (result <= 0)
                this.find(entry.left, comparable, entryList);
            if (result == 0)
                entryList.add(entry);
            if (result >= 0)
                this.find(entry.right, comparable, entryList);
        }
    }

    /** From CLR */
    private void fixAfterDeletion(Entry<K, V> entry)
    {
        while (entry != root && colorOf(entry) == BLACK)
        {
            if (entry == this.leftOf(this.parentOf(entry)))
            {
                Entry<K, V> findEntry = this.rightOf(this.parentOf(entry));

                if (this.colorOf(findEntry) == RED)
                {
                    this.setColor(findEntry, BLACK);
                    this.setColor(this.parentOf(entry), RED);
                    this.rotateLeft(this.parentOf(entry));
                    findEntry = this.rightOf(this.parentOf(entry));
                }

                if (this.leftColorOf(findEntry) == BLACK && this.rightColorOf(findEntry) == BLACK)
                {
                    this.setColor(findEntry, RED);
                    entry = this.parentOf(entry);
                }
                else
                {
                    if (this.rightColorOf(findEntry) == BLACK)
                    {
                        this.setColor(this.leftOf(findEntry), BLACK);
                        this.setColor(findEntry, RED);
                        this.rotateRight(findEntry);
                        findEntry = this.rightOf(this.parentOf(entry));
                    }
                    this.setColor(findEntry, this.parentColorOf(entry));
                    this.setColor(this.parentOf(entry), BLACK);
                    this.setColor(this.rightOf(findEntry), BLACK);
                    this.rotateLeft(this.parentOf(entry));
                    entry = root;
                }
            }
            else
            {
                Entry<K, V> findEntry = this.leftOf(this.parentOf(entry));

                if (this.colorOf(findEntry) == RED)
                {
                    this.setColor(findEntry, BLACK);
                    this.setColor(this.parentOf(entry), RED);
                    this.rotateRight(this.parentOf(entry));
                    findEntry = this.leftOf(this.parentOf(entry));
                }

                if (this.rightColorOf(findEntry) == BLACK && this.leftColorOf(findEntry) == BLACK)
                {
                    this.setColor(findEntry, RED);
                    entry = this.parentOf(entry);
                }
                else
                {
                    if (this.leftColorOf(findEntry) == BLACK)
                    {
                        this.setColor(this.rightOf(findEntry), BLACK);
                        this.setColor(findEntry, RED);
                        this.rotateLeft(findEntry);
                        findEntry = this.leftOf(this.parentOf(entry));
                    }
                    this.setColor(findEntry, this.parentColorOf(entry));
                    this.setColor(this.parentOf(entry), BLACK);
                    this.setColor(this.leftOf(findEntry), BLACK);
                    this.rotateRight(this.parentOf(entry));
                    entry = this.root;
                }
            }
        }

        this.setColor(entry, BLACK);
    }

    /** From CLR */
    private void fixAfterInsertion(Entry<K, V> entry)
    {
        entry.color = RED;
        while (entry != null && entry != root && entry.parent.color == RED)
        {
            if (this.parentOf(entry) == this.leftOf(this.parentOf(this.parentOf(entry))))
            {
                Entry<K, V> findEntry = this.rightOf(this.parentOf(this.parentOf(entry)));
                if (this.colorOf(findEntry) == RED)
                {
                    this.setColor(this.parentOf(entry), BLACK);
                    this.setColor(findEntry, BLACK);
                    this.setColor(this.parentOf(this.parentOf(entry)), RED);
                    entry = this.parentOf(this.parentOf(entry));
                }
                else
                {
                    if (entry == this.rightOf(this.parentOf(entry)))
                    {
                        entry = this.parentOf(entry);
                        this.rotateLeft(entry);
                    }
                    this.setColor(this.parentOf(entry), BLACK);
                    this.setColor(this.parentOf(this.parentOf(entry)), RED);
                    this.rotateRight(this.parentOf(this.parentOf(entry)));
                }
            }
            else
            {
                Entry<K, V> findEntry = this.leftOf(this.parentOf(this.parentOf(entry)));
                if (this.colorOf(findEntry) == RED)
                {
                    this.setColor(this.parentOf(entry), BLACK);
                    this.setColor(findEntry, BLACK);
                    this.setColor(this.parentOf(this.parentOf(entry)), RED);
                    entry = this.parentOf(this.parentOf(entry));
                }
                else
                {
                    if (entry == this.leftOf(this.parentOf(entry)))
                    {
                        entry = this.parentOf(entry);
                        this.rotateRight(entry);
                    }
                    this.setColor(this.parentOf(entry), BLACK);
                    this.setColor(this.parentOf(this.parentOf(entry)), RED);
                    this.rotateLeft(this.parentOf(this.parentOf(entry)));
                }
            }
        }
        this.root.color = BLACK;
    }

    public V get(K key)
    {
        Entry<K, V> entry = this.getEntryByKey(key);
        return entry == null ? null : entry.value;
    }

    public Collection<Entry<K, V>> getEntities()
    {
        return entryMap.values();
    }

    public Entry<K, V> getEntry(K key)
    {
        return this.getEntryByKey(key);
    }

    private Entry<K, V> getEntryByKey(K key)
    {
        if (key == null)
            throw new NullPointerException();
        return this.entryMap.get(key);
    }

    public Set<K> getKeySet()
    {
        return this.entryMap.keySet();
    }

    private boolean leftColorOf(Entry<K, V> entry)
    {
        return this.colorOf(this.leftOf(entry));
    }

    private Entry<K, V> leftOf(Entry<K, V> entry)
    {
        return entry == null ? null : entry.left;
    }

    private boolean parentColorOf(Entry<K, V> entry)
    {
        return this.colorOf(this.parentOf(entry));
    }

    private Entry<K, V> parentOf(Entry<K, V> entry)
    {
        return entry == null ? null : entry.parent;
    }

    public V put(K key, V value)
    {
        if (key == null)
            throw new NullPointerException();

        Entry<K, V> entry = this.root;
        if (entry == null)
        {
            this.root = new Entry<K, V>(key, value, null);
            this.size = 1;
            this.entryMap.put(this.root.key, this.root);
            return null;
        }

        int result;
        Entry<K, V> parent;
        do
        {
            parent = entry;
            result = this.compare(key, entry.key);
            if (result < 0)
                entry = entry.left;
            else if (result > 0)
                entry = entry.right;
            else
                return entry.setValue(value);
        } while (entry != null);

        Entry<K, V> newentry = new Entry<K, V>(key, value, parent);
        if (result < 0)
            parent.left = newentry;
        else
            parent.right = newentry;
        this.fixAfterInsertion(newentry);
        this.size++;
        this.entryMap.put(newentry.key, newentry);
        return value;
    }

    public V remove(K key)
    {
        V value = null;
        Entry<K, V> entry = this.getEntryByKey(key);
        if (entry != null)
        {
            value = entry.value;
            this.deleteEntry(entry);
        }
        return value;
    }

    private boolean rightColorOf(Entry<K, V> entry)
    {
        return this.colorOf(this.rightOf(entry));
    }

    private Entry<K, V> rightOf(Entry<K, V> entry)
    {
        return entry == null ? null : entry.right;
    }

    /** From CLR */
    private void rotateLeft(Entry<K, V> entry)
    {
        if (entry != null)
        {
            Entry<K, V> right = entry.right;
            entry.right = right.left;
            if (right.left != null)
                right.left.parent = entry;
            right.parent = entry.parent;
            if (entry.parent == null)
                root = right;
            else if (entry.parent.left == entry)
                entry.parent.left = right;
            else
                entry.parent.right = right;
            right.left = entry;
            entry.parent = right;
        }
    }

    /** From CLR */
    private void rotateRight(Entry<K, V> entry)
    {
        if (entry != null)
        {
            Entry<K, V> left = entry.left;
            entry.left = left.right;
            if (left.right != null)
                left.right.parent = entry;
            left.parent = entry.parent;
            if (entry.parent == null)
                root = left;
            else if (entry.parent.right == entry)
                entry.parent.right = left;
            else
                entry.parent.left = left;
            left.right = entry;
            entry.parent = left;
        }
    }

    private void setColor(Entry<K, V> entry, boolean color)
    {
        if (entry != null)
            entry.color = color;
    }

    public int size()
    {
        return size;
    }

    private Entry<K, V> successor(Entry<K, V> entry)
    {
        Entry<K, V> findEntry = null;
        if (entry != null)
        {
            if (entry.right != null)
            {
                Entry<K, V> right = entry.right;
                while (right.left != null)
                    right = right.left;
                findEntry = right;
            }
            else
            {
                Entry<K, V> parent = entry.parent;
                Entry<K, V> child = entry;
                while (parent != null && child == parent.right)
                {
                    child = parent;
                    parent = parent.parent;
                }
                findEntry = parent;
            }
        }
        return findEntry;
    }
}
