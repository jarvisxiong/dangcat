package org.dangcat.swing.fontchooser;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;

class ListSearchTextFieldDocumentHandler implements DocumentListener {
    @SuppressWarnings("unchecked")
    private JList targetList;

    @SuppressWarnings("unchecked")
    public ListSearchTextFieldDocumentHandler(JList targetList) {
        this.targetList = targetList;
    }

    public void changedUpdate(DocumentEvent e) {
        this.update(e);
    }

    public void insertUpdate(DocumentEvent e) {
        this.update(e);
    }

    public void removeUpdate(DocumentEvent e) {
        this.update(e);
    }

    private void update(DocumentEvent event) {
        String newValue = "";
        try {
            Document doc = event.getDocument();
            newValue = doc.getText(0, doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        if (newValue.length() > 0) {
            int index = this.targetList.getNextMatch(newValue, 0, Position.Bias.Forward);
            if (index < 0) {
                index = 0;
            }
            this.targetList.ensureIndexIsVisible(index);

            String matchedName = this.targetList.getModel().getElementAt(index).toString();
            if (newValue.equalsIgnoreCase(matchedName)) {
                if (index != this.targetList.getSelectedIndex()) {
                    SwingUtilities.invokeLater(new ListSelector(index, this.targetList));
                }
            }
        }
    }
}
