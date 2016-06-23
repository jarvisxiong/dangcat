package org.dangcat.swing;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;

public class ImagePanelDemo extends JFrameExt
{
    private static Dimension PERFECT_SIZE = new Dimension(400, 300);
    private static final long serialVersionUID = 1L;

    public static void main(String[] args)
    {
        show(new ImagePanelDemo());
    }

    public ImagePanelDemo()
    {
        super(ImagePanelDemo.class.getSimpleName());
    }

    @Override
    protected Container createContentPane()
    {
        JPanel contentPane = new JPanel();
        contentPane.setPreferredSize(PERFECT_SIZE);
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

        JTabbedPane tabbedPane = new JTabbedPane();

        Font font = new Font("宋体", Font.BOLD, 20);
        File imageFile = new File("target//test-classes//error.png");
        JImagePanel imagePanel1 = new JImagePanel();
        imagePanel1.setBorder(new EtchedBorder());
        imagePanel1.setImageFile(imageFile);
        JLabel textLabel1 = new JLabel("这是一个测试的文字。", JLabel.RIGHT);
        textLabel1.setForeground(Color.RED);
        textLabel1.setFont(font);
        imagePanel1.add(textLabel1);
        tabbedPane.add("ImagePanel1", imagePanel1);

        JImagePanel imagePanel2 = new JImagePanel();
        imagePanel2.setBorder(new EtchedBorder());
        imagePanel2.setImageFile(imageFile);
        imagePanel2.setImageStretch(false);
        JLabel textLabel2 = new JLabel("这是一个测试的文字。", JLabel.RIGHT);
        textLabel2.setForeground(Color.BLUE);
        textLabel2.setFont(font);
        imagePanel2.add(textLabel2);
        tabbedPane.add("ImagePanel2", imagePanel2);

        contentPane.add(tabbedPane);
        return contentPane;
    }
}
