package org.dangcat.swing.fontchooser;

import javax.swing.text.JTextComponent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

class TextFieldFocusHandlerForTextSelection extends FocusAdapter
{
    private JFontChooser fontChooser;
    private JTextComponent textComponent;

    public TextFieldFocusHandlerForTextSelection(JFontChooser fontChooser, JTextComponent textComponent)
    {
        this.textComponent = textComponent;
        this.fontChooser = fontChooser;
    }

    @Override
    public void focusGained(FocusEvent e)
    {
        this.textComponent.selectAll();
    }

    @Override
    public void focusLost(FocusEvent e)
    {
        this.textComponent.select(0, 0);
        this.fontChooser.updateSampleFont();
    }
}
