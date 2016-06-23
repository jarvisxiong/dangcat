package org.dangcat.swing.fontchooser;

import org.dangcat.commons.resource.ResourceManager;
import org.dangcat.commons.resource.ResourceReader;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * The <code>JFontChooser</code> class is a swing component for font selection.
 * This class has <code>JFileChooser</code> like APIs. The following code pops
 * up a font chooser dialog.
 * 
 * <pre>
 *   JFontChooser fontChooser = new JFontChooser();
 *   int result = fontChooser.showDialog(parent);
 *   if (result == JFontChooser.OK_OPTION)
 *   {
 *      Font font = fontChooser.getSelectedFont(); 
 *      System.out.println("Selected Font : " + font);
 * }
 * 
 * <pre>
 **/
public class JFontChooser extends JComponent
{
    public static final int CANCEL_OPTION = 1;
    public static final int ERROR_OPTION = -1;
    public static final int OK_OPTION = 0;
    private static final Font DEFAULT_FONT = new Font("Dialog", Font.PLAIN, 12);
    private static final String[] DEFAULT_FONT_SIZE_STRINGS = { "8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "26", "28", "36", "48", "72", };
    private static final Font DEFAULT_SELECTED_FONT = new Font("Serif", Font.PLAIN, 12);
    private static final int[] FONT_STYLE_CODES = { Font.PLAIN, Font.BOLD, Font.ITALIC, Font.BOLD | Font.ITALIC };
    private static final long serialVersionUID = 1L;
    protected int dialogResultValue = ERROR_OPTION;
    private String[] fontFamilyNames = null;
    private JTextField fontFamilyTextField = null;
    @SuppressWarnings("unchecked")
    private JList fontNameList = null;
    private JPanel fontNamePanel = null;
    @SuppressWarnings("unchecked")
    private JList fontSizeList = null;
    private JPanel fontSizePanel = null;
    private String[] fontSizeStrings = null;
    private JTextField fontSizeTextField = null;
    @SuppressWarnings("unchecked")
    private JList fontStyleList = null;
    private String[] fontStyleNames = null;
    private JPanel fontStylePanel = null;
    private JTextField fontStyleTextField = null;
    private ResourceReader resourceReader = ResourceManager.getInstance().getResourceReader(this.getClass());
    private JPanel samplePanel = null;
    private JTextField sampleText = null;

    /**
     * Constructs a <code>JFontChooser</code> object.
     **/
    public JFontChooser()
    {
        this(DEFAULT_FONT_SIZE_STRINGS);
    }

    /**
     * Constructs a <code>JFontChooser</code> object using the given font size
     * array.
     * @param fontSizeStrings the array of font size string.
     **/
    public JFontChooser(String[] fontSizeStrings)
    {
        if (fontSizeStrings == null)
        {
            fontSizeStrings = DEFAULT_FONT_SIZE_STRINGS;
        }
        this.fontSizeStrings = fontSizeStrings;

        JPanel selectPanel = new JPanel();
        selectPanel.setLayout(new BoxLayout(selectPanel, BoxLayout.X_AXIS));
        selectPanel.add(this.getFontFamilyPanel());
        selectPanel.add(this.getFontStylePanel());
        selectPanel.add(this.getFontSizePanel());

        JPanel contentsPanel = new JPanel();
        contentsPanel.setLayout(new GridLayout(2, 1));
        contentsPanel.add(selectPanel, BorderLayout.NORTH);
        contentsPanel.add(this.getSamplePanel(), BorderLayout.CENTER);

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.add(contentsPanel);
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.setSelectedFont(DEFAULT_SELECTED_FONT);
    }

    protected JDialog createDialog(Component parent)
    {
        Frame frame = parent instanceof Frame ? (Frame) parent : (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
        JDialog dialog = new JDialog(frame, this.getResourceText("SelectFont"), true);

        Action okAction = new DialogOKAction(dialog, this);
        Action cancelAction = new DialogCancelAction(dialog, this);

        JButton okButton = new JButton(okAction);
        okButton.setFont(DEFAULT_FONT);
        okButton.setText(this.getResourceText("button.Ok"));
        okButton.setMnemonic(KeyEvent.VK_O);
        okButton.setDisplayedMnemonicIndex(okButton.getText().indexOf('O'));

        JButton cancelButton = new JButton(cancelAction);
        cancelButton.setFont(DEFAULT_FONT);
        cancelButton.setText(this.getResourceText("button.Cancel"));
        cancelButton.setMnemonic(KeyEvent.VK_C);
        cancelButton.setDisplayedMnemonicIndex(cancelButton.getText().indexOf('C'));

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(2, 1, 2, 3));
        buttonsPanel.add(okButton);
        buttonsPanel.add(cancelButton);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 10));

        ActionMap actionMap = buttonsPanel.getActionMap();
        actionMap.put(cancelAction.getValue(Action.DEFAULT), cancelAction);
        actionMap.put(okAction.getValue(Action.DEFAULT), okAction);
        InputMap inputMap = buttonsPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), cancelAction.getValue(Action.DEFAULT));
        inputMap.put(KeyStroke.getKeyStroke("ENTER"), okAction.getValue(Action.DEFAULT));

        JPanel dialogEastPanel = new JPanel();
        dialogEastPanel.setLayout(new BorderLayout());
        dialogEastPanel.add(buttonsPanel, BorderLayout.NORTH);

        dialog.getContentPane().add(this, BorderLayout.CENTER);
        dialog.getContentPane().add(dialogEastPanel, BorderLayout.EAST);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        return dialog;
    }

    protected String[] getFontFamilies()
    {
        if (this.fontFamilyNames == null)
        {
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            this.fontFamilyNames = env.getAvailableFontFamilyNames();
        }
        return this.fontFamilyNames;
    }

    @SuppressWarnings("unchecked")
    public JList getFontFamilyList()
    {
        if (this.fontNameList == null)
        {
            this.fontNameList = new JList(this.getFontFamilies());
            this.fontNameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            this.fontNameList.addListSelectionListener(new ListSelectionHandler(this, this.getFontFamilyTextField()));
            this.fontNameList.setSelectedIndex(0);
            this.fontNameList.setFont(DEFAULT_FONT);
            this.fontNameList.setFocusable(false);
        }
        return this.fontNameList;
    }

    protected JPanel getFontFamilyPanel()
    {
        if (this.fontNamePanel == null)
        {
            this.fontNamePanel = new JPanel();
            this.fontNamePanel.setLayout(new BorderLayout());
            this.fontNamePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            this.fontNamePanel.setPreferredSize(new Dimension(180, 130));

            JScrollPane scrollPane = new JScrollPane(this.getFontFamilyList());
            scrollPane.getVerticalScrollBar().setFocusable(false);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.add(this.getFontFamilyTextField(), BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);

            JLabel label = new JLabel(this.getResourceText("FontName"));
            label.setHorizontalAlignment(JLabel.LEFT);
            label.setHorizontalTextPosition(JLabel.LEFT);
            label.setLabelFor(this.getFontFamilyTextField());
            label.setDisplayedMnemonic('F');

            this.fontNamePanel.add(label, BorderLayout.NORTH);
            this.fontNamePanel.add(panel, BorderLayout.CENTER);

        }
        return this.fontNamePanel;
    }

    public JTextField getFontFamilyTextField()
    {
        if (this.fontFamilyTextField == null)
        {
            this.fontFamilyTextField = new JTextField();
            this.fontFamilyTextField.addFocusListener(new TextFieldFocusHandlerForTextSelection(this, this.fontFamilyTextField));
            this.fontFamilyTextField.addKeyListener(new TextFieldKeyHandlerForListSelectionUpDown(this.getFontFamilyList()));
            this.fontFamilyTextField.getDocument().addDocumentListener(new ListSearchTextFieldDocumentHandler(this.getFontFamilyList()));
            this.fontFamilyTextField.setFont(DEFAULT_FONT);
        }
        return this.fontFamilyTextField;
    }

    @SuppressWarnings("unchecked")
    public JList getFontSizeList()
    {
        if (this.fontSizeList == null)
        {
            this.fontSizeList = new JList(this.fontSizeStrings);
            this.fontSizeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            this.fontSizeList.addListSelectionListener(new ListSelectionHandler(this, this.getFontSizeTextField()));
            this.fontSizeList.setSelectedIndex(0);
            this.fontSizeList.setFont(DEFAULT_FONT);
            this.fontSizeList.setFocusable(false);
        }
        return this.fontSizeList;
    }

    protected JPanel getFontSizePanel()
    {
        if (this.fontSizePanel == null)
        {
            this.fontSizePanel = new JPanel();
            this.fontSizePanel.setLayout(new BorderLayout());
            this.fontSizePanel.setPreferredSize(new Dimension(70, 130));
            this.fontSizePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            JScrollPane scrollPane = new JScrollPane(this.getFontSizeList());
            scrollPane.getVerticalScrollBar().setFocusable(false);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.add(this.getFontSizeTextField(), BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);

            JLabel label = new JLabel(this.getResourceText("FontSize"));
            label.setHorizontalAlignment(JLabel.LEFT);
            label.setHorizontalTextPosition(JLabel.LEFT);
            label.setLabelFor(this.getFontSizeTextField());
            label.setDisplayedMnemonic('S');

            this.fontSizePanel.add(label, BorderLayout.NORTH);
            this.fontSizePanel.add(panel, BorderLayout.CENTER);
        }
        return this.fontSizePanel;
    }

    public JTextField getFontSizeTextField()
    {
        if (this.fontSizeTextField == null)
        {
            this.fontSizeTextField = new JTextField();
            this.fontSizeTextField.addFocusListener(new TextFieldFocusHandlerForTextSelection(this, this.fontSizeTextField));
            this.fontSizeTextField.addKeyListener(new TextFieldKeyHandlerForListSelectionUpDown(this.getFontSizeList()));
            this.fontSizeTextField.getDocument().addDocumentListener(new ListSearchTextFieldDocumentHandler(this.getFontSizeList()));
            this.fontSizeTextField.setFont(DEFAULT_FONT);
        }
        return this.fontSizeTextField;
    }

    @SuppressWarnings("unchecked")
    public JList getFontStyleList()
    {
        if (this.fontStyleList == null)
        {
            this.fontStyleList = new JList(this.getFontStyleNames());
            this.fontStyleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            this.fontStyleList.addListSelectionListener(new ListSelectionHandler(this, this.getFontStyleTextField()));
            this.fontStyleList.setSelectedIndex(0);
            this.fontStyleList.setFont(DEFAULT_FONT);
            this.fontStyleList.setFocusable(false);
        }
        return this.fontStyleList;
    }

    protected String[] getFontStyleNames()
    {
        if (this.fontStyleNames == null)
        {
            int i = 0;
            this.fontStyleNames = new String[4];
            this.fontStyleNames[i++] = this.getResourceText("Plain");
            this.fontStyleNames[i++] = this.getResourceText("Bold");
            this.fontStyleNames[i++] = this.getResourceText("Italic");
            this.fontStyleNames[i++] = this.getResourceText("BoldItalic");
        }
        return this.fontStyleNames;
    }

    protected JPanel getFontStylePanel()
    {
        if (this.fontStylePanel == null)
        {
            this.fontStylePanel = new JPanel();
            this.fontStylePanel.setLayout(new BorderLayout());
            this.fontStylePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            this.fontStylePanel.setPreferredSize(new Dimension(140, 130));

            JScrollPane scrollPane = new JScrollPane(this.getFontStyleList());
            scrollPane.getVerticalScrollBar().setFocusable(false);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.add(this.getFontStyleTextField(), BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);

            JLabel label = new JLabel(this.getResourceText("FontStyle"));
            label.setHorizontalAlignment(JLabel.LEFT);
            label.setHorizontalTextPosition(JLabel.LEFT);
            label.setLabelFor(this.getFontStyleTextField());
            label.setDisplayedMnemonic('Y');

            this.fontStylePanel.add(label, BorderLayout.NORTH);
            this.fontStylePanel.add(panel, BorderLayout.CENTER);
        }
        return this.fontStylePanel;
    }

    public JTextField getFontStyleTextField()
    {
        if (this.fontStyleTextField == null)
        {
            this.fontStyleTextField = new JTextField();
            this.fontStyleTextField.addFocusListener(new TextFieldFocusHandlerForTextSelection(this, this.fontStyleTextField));
            this.fontStyleTextField.addKeyListener(new TextFieldKeyHandlerForListSelectionUpDown(this.getFontStyleList()));
            this.fontStyleTextField.getDocument().addDocumentListener(new ListSearchTextFieldDocumentHandler(this.getFontStyleList()));
            this.fontStyleTextField.setFont(DEFAULT_FONT);
        }
        return this.fontStyleTextField;
    }

    private String getResourceText(String key)
    {
        return this.resourceReader.getText(key);
    }

    protected JPanel getSamplePanel()
    {
        if (this.samplePanel == null)
        {
            Border titledBorder = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), this.getResourceText("Sample"));
            Border empty = BorderFactory.createEmptyBorder(5, 10, 10, 10);
            Border border = BorderFactory.createCompoundBorder(titledBorder, empty);

            this.samplePanel = new JPanel();
            this.samplePanel.setLayout(new BorderLayout());
            this.samplePanel.setBorder(border);

            this.samplePanel.add(this.getSampleTextField(), BorderLayout.CENTER);
        }
        return this.samplePanel;
    }

    protected JTextField getSampleTextField()
    {
        if (this.sampleText == null)
        {
            Border lowered = BorderFactory.createLoweredBevelBorder();

            this.sampleText = new JTextField(this.getResourceText("SampleText"));
            this.sampleText.setBorder(lowered);
            this.sampleText.setPreferredSize(new Dimension(300, 100));
        }
        return this.sampleText;
    }

    /**
     * Get the selected font.
     * @return the selected font
     * 
     * @see #setSelectedFont
     * @see java.awt.Font
     **/
    public Font getSelectedFont()
    {
        Font font = new Font(this.getSelectedFontFamily(), this.getSelectedFontStyle(), this.getSelectedFontSize());
        return font;
    }

    /**
     * Set the selected font.
     * @param font the selected font
     *
     * @see #getSelectedFont
     * @see java.awt.Font
     **/
    public void setSelectedFont(Font font) {
        this.setSelectedFontFamily(font.getFamily());
        this.setSelectedFontStyle(font.getStyle());
        this.setSelectedFontSize(font.getSize());
    }

    /**
     * Get the family name of the selected font.
     * @return the font family of the selected font.
     *
     * @see #setSelectedFontFamily
     **/
    public String getSelectedFontFamily()
    {
        String fontName = (String) this.getFontFamilyList().getSelectedValue();
        return fontName;
    }

    /**
     * Set the family name of the selected font.
     * @param name the family name of the selected font.
     *
     * @see getSelectedFontFamily
     **/
    public void setSelectedFontFamily(String name) {
        String[] names = this.getFontFamilies();
        for (int i = 0; i < names.length; i++) {
            if (names[i].toLowerCase().equals(name.toLowerCase())) {
                this.getFontFamilyList().setSelectedIndex(i);
                break;
            }
        }
        this.updateSampleFont();
    }

    /**
     * Get the size of the selected font.
     * @return the size of the selected font
     *
     * @see #setSelectedFontSize
     **/
    public int getSelectedFontSize()
    {
        int fontSize = 1;
        String fontSizeString = this.getFontSizeTextField().getText();
        while (true)
        {
            try
            {
                fontSize = Integer.parseInt(fontSizeString);
                break;
            }
            catch (NumberFormatException e)
            {
                fontSizeString = (String) this.getFontSizeList().getSelectedValue();
                this.getFontSizeTextField().setText(fontSizeString);
            }
        }

        return fontSize;
    }

    /**
     * Set the size of the selected font.
     * @param size the size of the selected font
     *
     * @see #getSelectedFontSize
     **/
    public void setSelectedFontSize(int size)
    {
        String sizeString = String.valueOf(size);
        for (int i = 0; i < this.fontSizeStrings.length; i++)
        {
            if (this.fontSizeStrings[i].equals(sizeString))
            {
                this.getFontSizeList().setSelectedIndex(i);
                break;
            }
        }
        this.getFontSizeTextField().setText(sizeString);
        this.updateSampleFont();
    }

    /**
     * Get the style of the selected font.
     * @return the style of the selected font. <code>Font.PLAIN</code>,
     *         <code>Font.BOLD</code>, <code>Font.ITALIC</code>,
     *         <code>Font.BOLD|Font.ITALIC</code>
     *
     * @see java.awt.Font#PLAIN
     * @see java.awt.Font#BOLD
     * @see java.awt.Font#ITALIC
     * @see #setSelectedFontStyle
     **/
    public int getSelectedFontStyle() {
        int index = this.getFontStyleList().getSelectedIndex();
        return FONT_STYLE_CODES[index];
    }

    /**
     * Set the style of the selected font.
     * @param style the size of the selected font. <code>Font.PLAIN</code>,
     *            <code>Font.BOLD</code>, <code>Font.ITALIC</code>, or
     *            <code>Font.BOLD|Font.ITALIC</code>.
     *
     * @see java.awt.Font#PLAIN
     * @see java.awt.Font#BOLD
     * @see java.awt.Font#ITALIC
     * @see #getSelectedFontStyle
     **/
    public void setSelectedFontStyle(int style)
    {
        for (int i = 0; i < FONT_STYLE_CODES.length; i++)
        {
            if (FONT_STYLE_CODES[i] == style)
            {
                this.getFontStyleList().setSelectedIndex(i);
                break;
            }
        }
        this.updateSampleFont();
    }

    public String getVersionString() {
        return ("Version");
    }

    /**
     * Show font selection dialog.
     * @param parent Dialog's Parent component.
     * @return OK_OPTION, CANCEL_OPTION or ERROR_OPTION
     * 
     * @see #OK_OPTION
     * @see #CANCEL_OPTION
     * @see #ERROR_OPTION
     **/
    public int showDialog(Component parent)
    {
        this.dialogResultValue = ERROR_OPTION;
        JDialog dialog = this.createDialog(parent);
        dialog.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                JFontChooser.this.dialogResultValue = CANCEL_OPTION;
            }
        });

        dialog.setVisible(true);
        dialog.dispose();
        dialog = null;

        return this.dialogResultValue;
    }

    protected void updateSampleFont()
    {
        Font font = this.getSelectedFont();
        this.getSampleTextField().setFont(font);
    }
}