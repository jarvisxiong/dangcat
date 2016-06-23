package org.dangcat.swing;

import java.awt.Container;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

public class ProcessPanelDemo extends JFrameExt
{
    private static final long serialVersionUID = 1L;

    public static void main(String[] args)
    {
        show(new ProcessPanelDemo());
    }

    public ProcessPanelDemo()
    {
        super(ProcessPanelDemo.class.getSimpleName());
    }

    @Override
    protected Container createContentPane()
    {
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

        JProgressPanel progressPanel = new JProgressPanel(false);
        progressPanel.setBorder(new EtchedBorder());
        progressPanel.setMessage("This is a test progress...");
        progressPanel.setMin(0);
        progressPanel.setMax(100);
        progressPanel.setValue(60);
        contentPane.add(progressPanel);
        return contentPane;
    }
}
