package org.dangcat.install.swing.toolbar;

import org.dangcat.install.swing.ToolBarPanel;

import java.awt.event.KeyEvent;


public class ConfigToolBar extends ToolBarPanel
{
    public static final String CHANGED = "Changed";
    public static final String SAVE = "save";
    private static final String NEXT = "next";
    private static final String PRIOR = "prior";
    private static final long serialVersionUID = 1L;

    @Override
    public void checkButtonStates()
    {
        Boolean isBof = (Boolean) this.getState("Bof");
        this.setEnabled(PRIOR, isBof != null && !isBof);
        Boolean isEof = (Boolean) this.getState("Eof");
        this.setEnabled(NEXT, isEof == null || !isEof);

        Boolean isChanged = (Boolean) this.getState("Changed");
        if (isChanged == null)
            isChanged = false;
        this.setEnabled(SAVE, isChanged);
        this.setEnabled(CANCEL, isChanged);
        this.setEnabled(EXIT, !isChanged);
    }

    @Override
    public void initialize()
    {
        this.addButton(PRIOR, KeyEvent.VK_P, "P");
        this.addButton(NEXT, KeyEvent.VK_N, "N");
        this.addSeparator(40);
        this.addButton(CANCEL, KeyEvent.VK_C, "C");
        this.addButton(SAVE, KeyEvent.VK_S, "S");
        super.initialize();
    }
}
