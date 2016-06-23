package org.dangcat.swing;

import java.awt.Container;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class CheckBoxListDemo extends JFrameExt
{
    private static final long serialVersionUID = 1L;

    public static void main(String[] args)
    {
        show(new CheckBoxListDemo());
    }

    public CheckBoxListDemo()
    {
        super(CheckBoxListDemo.class.getSimpleName());
    }

    @Override
    protected Container createContentPane()
    {
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

        JCheckBoxList checkBoxList = new JCheckBoxList();
        checkBoxList.setFlow(BoxLayout.Y_AXIS);
        contentPane.add(checkBoxList);
        for (int i = 0; i < 20; i++)
            checkBoxList.addItem("CheckBox " + i);
        return contentPane;
    }
}
