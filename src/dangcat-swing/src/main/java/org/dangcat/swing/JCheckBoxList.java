package org.dangcat.swing;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class JCheckBoxList extends JPanel {
    private static final long serialVersionUID = 1L;
    private Map<JCheckBox, CheckBoxEntry> checkBoxEntryMap = new HashMap<JCheckBox, CheckBoxEntry>();
    private JPanel content = null;
    private int flow = BoxLayout.Y_AXIS;
    private FocusListener listFocusListener = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent focusEvent) {
            JComponent component = (JComponent) focusEvent.getComponent();
            component.scrollRectToVisible(new Rectangle(0, 0, component.getWidth(), component.getHeight()));
        }
    };
    private JScrollPane scrollPane = null;
    private Action updateListAction = null;

    public JCheckBoxList() {
        super();

        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.content = new JPanel();
        this.content.setLayout(new BoxLayout(this.content, this.getFlow()));
        this.scrollPane = new JScrollPane(this.content);
        this.scrollPane.getVerticalScrollBar().setUnitIncrement(2);
        this.add(this.scrollPane);
    }

    public void addActionListener(ActionListener actionListener) {
        this.listenerList.add(ActionListener.class, actionListener);
    }

    public void addItem(Object value) {
        this.addItem(value, value.toString());
    }

    public void addItem(Object value, String title) {
        this.addItem(value, title, false);
    }

    public void addItem(Object value, String title, boolean selected) {
        CheckBoxEntry checkBoxEntry = new CheckBoxEntry();
        checkBoxEntry.value = value;
        checkBoxEntry.selected = selected;
        checkBoxEntry.title = title;
        if (this.updateListAction == null)
            this.updateListAction = new UpdateListAction(this);
        JCheckBox checkBox = (JCheckBox) this.content.add(new JCheckBox(checkBoxEntry.title));
        this.checkBoxEntryMap.put(checkBox, checkBoxEntry);
        checkBox.setSelected(checkBoxEntry.selected);
        checkBox.addActionListener(this.updateListAction);
        checkBox.addFocusListener(this.listFocusListener);
        this.content.updateUI();
    }

    public void clear() {
        this.checkBoxEntryMap.clear();
        this.content.removeAll();
    }

    protected void fireActionPerformed(ActionEvent event) {
        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ActionListener.class)
                ((ActionListener) listeners[i + 1]).actionPerformed(event);
        }
    }

    public int getFlow() {
        return this.flow;
    }

    public void setFlow(int flow) {
        this.flow = flow;
        this.content.setLayout(new BoxLayout(this.content, this.flow));
    }

    @SuppressWarnings("unchecked")
    public <T> Map<T, String> getSelectedMap() {
        Map<T, String> selectedMap = new LinkedHashMap<T, String>();
        for (CheckBoxEntry checkBoxEntry : this.checkBoxEntryMap.values()) {
            if (checkBoxEntry.selected)
                selectedMap.put((T) checkBoxEntry.value, checkBoxEntry.title);
        }
        return selectedMap;
    }

    @SuppressWarnings("unchecked")
    public <T> Collection<T> getSelectedValues() {
        List<T> selectedValues = new LinkedList<T>();
        for (CheckBoxEntry checkBoxEntry : this.checkBoxEntryMap.values()) {
            if (checkBoxEntry.selected)
                selectedValues.add((T) checkBoxEntry.value);
        }
        return selectedValues;
    }

    public void setSelectedValues(Collection<?> values) {
        for (CheckBoxEntry checkBoxEntry : this.checkBoxEntryMap.values())
            checkBoxEntry.selected = values != null && values.contains(checkBoxEntry.value);
    }

    @SuppressWarnings("unchecked")
    public <T> Collection<T> getValues() {
        List<T> values = new LinkedList<T>();
        for (CheckBoxEntry checkBoxEntry : this.checkBoxEntryMap.values())
            values.add((T) checkBoxEntry.value);
        return values;
    }

    public void removeActionListener(ActionListener actionListener) {
        this.listenerList.remove(ActionListener.class, actionListener);
    }

    @Override
    public void setBorder(Border border) {
        if (border != null)
            this.scrollPane.setBorder(null);

        super.setBorder(border);
    }

    class CheckBoxEntry {
        protected boolean selected = false;
        protected String title = null;
        protected Object value = null;
    }

    class UpdateListAction extends AbstractAction {
        private static final long serialVersionUID = 1L;
        private JCheckBoxList checkBoxList = null;

        protected UpdateListAction(JCheckBoxList checkBoxList) {
            this.checkBoxList = checkBoxList;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            JCheckBox checkBox = (JCheckBox) actionEvent.getSource();
            CheckBoxEntry checkBoxEntry = this.checkBoxList.checkBoxEntryMap.get(checkBox);
            if (checkBoxEntry != null)
                checkBoxEntry.selected = checkBox.isSelected();
            this.checkBoxList.fireActionPerformed(actionEvent);
        }
    }
}
