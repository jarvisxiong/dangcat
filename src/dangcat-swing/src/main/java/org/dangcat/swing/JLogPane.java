package org.dangcat.swing;

import org.apache.log4j.Level;
import org.dangcat.commons.log.LogCallback;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class JLogPane extends JTextPane implements LogCallback
{
    private static final long serialVersionUID = 1L;
    private boolean autoLocateBottom = true;
    private Map<Level, AttributeSet> levelMap = new HashMap<Level, AttributeSet>();

    public JLogPane()
    {
        this.setEditable(false);
        this.setContentType("text/html");
        this.createAttributeSets();
    }

    private void append(String text, AttributeSet attributeSet)
    {
        Document document = this.getDocument();
        try
        {
            document.insertString(document.getLength(), text, attributeSet);
            if (this.isAutoLocateBottom())
                this.setCaretPosition(document.getLength());
        }
        catch (BadLocationException e)
        {
            System.out.println("BadLocationException: " + e);
        }
    }

    public void clear()
    {
        try
        {
            Document document = this.getDocument();
            if (document.getLength() > 0)
                document.remove(0, document.getLength());
        }
        catch (BadLocationException e)
        {
            System.out.println("BadLocationException: " + e);
        }
    }

    private void createAttributeSets()
    {
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        StyleConstants.setForeground(attributeSet, Color.BLUE);
        this.levelMap.put(Level.DEBUG, attributeSet);

        attributeSet = new SimpleAttributeSet();
        StyleConstants.setForeground(attributeSet, Color.BLACK);
        this.levelMap.put(Level.INFO, attributeSet);

        attributeSet = new SimpleAttributeSet();
        StyleConstants.setForeground(attributeSet, Color.ORANGE);
        this.levelMap.put(Level.WARN, attributeSet);

        attributeSet = new SimpleAttributeSet();
        StyleConstants.setForeground(attributeSet, Color.RED);
        this.levelMap.put(Level.ERROR, attributeSet);

        attributeSet = new SimpleAttributeSet();
        StyleConstants.setForeground(attributeSet, Color.RED);
        StyleConstants.setBold(attributeSet, true);
        this.levelMap.put(Level.FATAL, attributeSet);
    }

    @Override
    public void debug(String message)
    {
        this.append(message, this.getAttributeSet(Level.DEBUG));
    }

    @Override
    public void error(String message)
    {
        this.append(message, this.getAttributeSet(Level.ERROR));
    }

    @Override
    public void fatal(String message)
    {
        this.append(message, this.getAttributeSet(Level.FATAL));
    }

    public AttributeSet getAttributeSet(Level level)
    {
        return this.levelMap.get(level);
    }

    @Override
    public void info(String message)
    {
        this.append(message, this.getAttributeSet(Level.INFO));
    }

    public boolean isAutoLocateBottom()
    {
        return this.autoLocateBottom;
    }

    public void setAutoLocateBottom(boolean autoLocateBottom)
    {
        this.autoLocateBottom = autoLocateBottom;
    }

    @Override
    public void warn(String message)
    {
        this.append(message, this.getAttributeSet(Level.WARN));
    }
}
