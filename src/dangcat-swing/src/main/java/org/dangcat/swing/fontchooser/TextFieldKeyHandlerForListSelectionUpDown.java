package org.dangcat.swing.fontchooser;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JList;

class TextFieldKeyHandlerForListSelectionUpDown extends KeyAdapter
{
    @SuppressWarnings("unchecked")
    private JList targetList;

    @SuppressWarnings("unchecked")
    public TextFieldKeyHandlerForListSelectionUpDown(JList list)
    {
        this.targetList = list;
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        int i = this.targetList.getSelectedIndex();
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_UP:
                i = this.targetList.getSelectedIndex() - 1;
                if (i < 0)
                {
                    i = 0;
                }
                this.targetList.setSelectedIndex(i);
                break;
            case KeyEvent.VK_DOWN:
                int listSize = this.targetList.getModel().getSize();
                i = this.targetList.getSelectedIndex() + 1;
                if (i >= listSize)
                {
                    i = listSize - 1;
                }
                this.targetList.setSelectedIndex(i);
                break;
            default:
                break;
        }
    }
}