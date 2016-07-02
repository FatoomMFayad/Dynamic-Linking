package de;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;

public class TextPaneHighlighting {

    private static final long serialVersionUID = 1L;
    private Highlighter.HighlightPainter cyanPainter;
    private Highlighter.HighlightPainter redPainter;

    public TextPaneHighlighting() {
        JFrame frame = new JFrame();
        JTextPane textPane = new JTextPane();
        textPane.setText("one\ntwo\nthree\nfour\nfive\nsix\nseven\neight\n");
        JScrollPane scrollPane = new JScrollPane(textPane);
        frame.add(scrollPane, BorderLayout.CENTER);//  Highlight some text
        cyanPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.cyan);
        redPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.red);
        try {
            textPane.getHighlighter().addHighlight(0, 3, DefaultHighlighter.DefaultPainter);
            textPane.getHighlighter().addHighlight(8, 14, cyanPainter);
            textPane.getHighlighter().addHighlight(19, 24, redPainter);
        } catch (BadLocationException ble) {
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(300, 200));
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                TextPaneHighlighting tph = new TextPaneHighlighting();
            }
        });
    }
}