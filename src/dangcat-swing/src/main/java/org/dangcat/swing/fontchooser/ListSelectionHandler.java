package org.dangcat.swing.fontchooser;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;

class ListSelectionHandler implements ListSelectionListener
{
    private JFontChooser fontChooser;
    private JTextComponent textComponent;

    ListSelectionHandler(JFontChooser fontChooser, JTextComponent textComponent)
    {
        this.fontChooser = fontChooser;
        this.textComponent = textComponent;
    }

    @SuppressWarnings("unchecked")
    public void valueChanged(ListSelectionEvent e)
    {
        if (e.getValueIsAdjusting() == false)
        {
            JList list = (JList) e.getSource();
            String selectedValue = (String) list.getSelectedValue();

            String oldValue = this.textComponent.getText();
            this.textComponent.setText(selectedValue);
            if (!oldValue.equalsIgnoreCase(selectedValue))
            {
                this.textComponent.selectAll();
                this.textComponent.requestFocus();
            }

            this.fontChooser.updateSampleFont();
        }
    }
}
