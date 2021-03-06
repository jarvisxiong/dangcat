package org.dangcat.business.code;

import org.dangcat.business.code.swing.BusinessCodePanel;
import org.dangcat.business.code.swing.DomainCodePanel;
import org.dangcat.business.code.swing.ServerCodePanel;
import org.dangcat.commons.resource.ResourceManager;
import org.dangcat.swing.JFrameExt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class CodeGeneratorFrame extends JFrameExt {
    private static final long serialVersionUID = 1L;
    private static Dimension PERFECT_SIZE = new Dimension(800, 600);

    public CodeGeneratorFrame() {
        super(ResourceManager.getInstance().getResourceReader(CodeGeneratorFrame.class).getText(CodeGeneratorFrame.class.getSimpleName()));
    }

    public static void main(final String[] args) {
        show(new CodeGeneratorFrame());
    }

    @Override
    protected Container createContentPane() {
        JPanel content = new JPanel(new BorderLayout());
        content.setPreferredSize(PERFECT_SIZE);

        JTabbedPane tabbedPane = new JTabbedPane();

        BusinessCodePanel businessCodePanel = new BusinessCodePanel();
        businessCodePanel.initialize();
        String businessCodePanelTitle = businessCodePanel.getTitle();
        tabbedPane.add(businessCodePanelTitle, businessCodePanel);
        tabbedPane.setMnemonicAt(tabbedPane.getComponentCount() - 1, KeyEvent.VK_1);
        tabbedPane.setDisplayedMnemonicIndexAt(tabbedPane.getComponentCount() - 1, businessCodePanelTitle.indexOf("1"));

        DomainCodePanel domainCodePanel = new DomainCodePanel();
        domainCodePanel.initialize();
        String domainCodePanelTitle = domainCodePanel.getTitle();
        tabbedPane.add(domainCodePanelTitle, domainCodePanel);
        tabbedPane.setMnemonicAt(tabbedPane.getComponentCount() - 1, KeyEvent.VK_2);
        tabbedPane.setDisplayedMnemonicIndexAt(tabbedPane.getComponentCount() - 1, domainCodePanelTitle.indexOf("2"));

        ServerCodePanel serverCodePanel = new ServerCodePanel();
        serverCodePanel.initialize();
        String serverCodePanelTitle = serverCodePanel.getTitle();
        tabbedPane.add(serverCodePanelTitle, serverCodePanel);
        tabbedPane.setMnemonicAt(tabbedPane.getComponentCount() - 1, KeyEvent.VK_3);
        tabbedPane.setDisplayedMnemonicIndexAt(tabbedPane.getComponentCount() - 1, serverCodePanelTitle.indexOf("3"));

        content.add(tabbedPane);
        return content;
    }
}
