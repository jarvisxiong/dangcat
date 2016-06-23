package org.dangcat.persistence.index;

import junit.framework.Assert;
import org.dangcat.persistence.filter.FilterType;
import org.dangcat.persistence.filter.FilterUnit;
import org.junit.Test;

import java.util.Set;

public class TestBinaryTree {
    private void testFind(int count, FilterComparable filterComparable, int expected, int assertIndex) {
        BinaryTree<Integer, String> binaryTree = new BinaryTree<Integer, String>();
        for (int i = 0; i < count; i++)
            binaryTree.put(i, "value " + i);

        Assert.assertEquals(count, binaryTree.size());

        Set<Entry<Integer, String>> entrySet = binaryTree.find(filterComparable);
        Assert.assertEquals(expected, entrySet.size());

        for (Entry<Integer, String> entry : entrySet) {
            Assert.assertEquals(assertIndex, entry.getKey().intValue());
            Assert.assertEquals("value " + assertIndex, entry.getValue());
            assertIndex++;
        }
    }

    @Test
    public void testFindBetween() {
        FilterComparable filterComparable = new FilterComparable();
        filterComparable.add(new FilterUnit(null, FilterType.between, 10, 19));
        this.testFind(100, filterComparable, 10, 10);
    }

    @Test
    public void testFindEq() {
        FilterComparable filterComparable = new FilterComparable();
        filterComparable.add(new FilterUnit(null, FilterType.eq, 56));
        this.testFind(100, filterComparable, 1, 56);
    }

    @Test
    public void testFindGe() {
        FilterComparable filterComparable = new FilterComparable();
        filterComparable.add(new FilterUnit(null, FilterType.ge, 80));
        this.testFind(100, filterComparable, 20, 80);
    }

    @Test
    public void testFindLe() {
        FilterComparable filterComparable = new FilterComparable();
        filterComparable.add(new FilterUnit(null, FilterType.le, 14));
        this.testFind(100, filterComparable, 15, 0);
    }

    @Test
    public void testFindLike() {
        BinaryTree<String, Integer> binaryTree = new BinaryTree<String, Integer>();
        for (int i = 0; i < 1000; i++)
            binaryTree.put("value " + i, i);

        FilterComparable filterComparable = new FilterComparable();
        filterComparable.add(new FilterUnit(null, FilterType.like, "value 10"));

        Set<Entry<String, Integer>> entrySet = binaryTree.find(filterComparable);
        Assert.assertEquals(12, entrySet.size());
    }
}
