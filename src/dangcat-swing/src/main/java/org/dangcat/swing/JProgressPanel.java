package org.dangcat.swing;

import javax.swing.*;
import java.awt.*;

public class JProgressPanel extends JPanel
{
    private static final long serialVersionUID = 1L;
    private static Dimension MaximumSize = new Dimension(Short.MAX_VALUE, 22);
    private JLabel messageLabel = null;
    private JLabel percentLabel = null;
    private JProgressBar progressBar = null;
    private boolean showMessage = true;

    public JProgressPanel()
    {
        this(true);
    }

    public JProgressPanel(boolean showMessage)
    {
        super();

        this.showMessage = showMessage;
        this.createComponents();
    }

    private void createComponents()
    {
        this.setLayout(new GridBagLayout());

        int y = 0;
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        constraints.gridy = y;
        if (this.isShowMessage())
        {
            this.messageLabel = new JLabel();
            this.messageLabel.setMinimumSize(MaximumSize);
            this.add(this.messageLabel, constraints);
        }
        y++;

        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 0.95;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.gridy = y;
        this.progressBar = new JProgressBar(JProgressBar.HORIZONTAL);
        this.progressBar.setMinimumSize(MaximumSize);
        this.add(this.progressBar, constraints);

        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 0.05;
        constraints.gridx = 1;
        constraints.gridwidth = 1;
        constraints.gridy = y;
        this.percentLabel = new JLabel();
        this.percentLabel.setMinimumSize(MaximumSize);
        y++;
        this.add(this.percentLabel, constraints);
        this.setMaximumSize(new Dimension(Short.MAX_VALUE, this.isShowMessage() ? 50 : 30));
    }

    public int getMax()
    {
        return this.progressBar.getMaximum();
    }

    public void setMax(int max)
    {
        this.progressBar.setMaximum(max);
    }

    public String getMessage()
    {
        return this.messageLabel.getText();
    }

    public void setMessage(String message)
    {
        if (this.messageLabel != null)
            this.messageLabel.setText(message);
    }

    public int getMin()
    {
        return this.progressBar.getMinimum();
    }

    public void setMin(int min)
    {
        this.progressBar.setMinimum(min);
    }

    public int getValue()
    {
        return this.progressBar.getValue();
    }

    public void setValue(int value)
    {
        this.progressBar.setValue(value);
        int percent = 0;
        if (this.getMax() != 0)
            percent = (int) (this.getValue() * 100.0 / this.getMax());
        this.percentLabel.setText(percent + "%");
    }

    public boolean isShowMessage() {
        return this.showMessage;
    }
}
