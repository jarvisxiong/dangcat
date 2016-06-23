package org.dangcat.swing.fontchooser;

import javax.swing.*;

public class ListSelector implements Runnable
{
    private int index;
    @SuppressWarnings("unchecked")
    private JList targetList;

    @SuppressWarnings("unchecked")
    public ListSelector(int index, JList targetList)
    {
        this.index = index;
        this.targetList = targetList;
    }

    public void run()
    {
        this.targetList.setSelectedIndex(this.index);
    }
}
