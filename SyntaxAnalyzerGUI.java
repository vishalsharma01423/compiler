 import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class SyntaxAnalyzerGUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public SyntaxAnalyzerGUI() {
        setTitle("Syntax Analyzer for C");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(createWelcomePanel(), "welcome");
        cardPanel.add(createAnalyzerPanel(), "analyzer");

        add(cardPanel);
        cardLayout.show(cardPanel, "welcome");
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(236, 240, 241));

        JLabel heading = new JLabel("Welcome to C Syntax Analyzer", JLabel.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 28));
        heading.setForeground(Color.WHITE);
        heading.setOpaque(true);
        heading.setBackground(new Color(52, 73, 94));
        heading.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(heading, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(236, 240, 241));

        try {
            ImageIcon imageIcon = new ImageIcon("code_banner.png");
            Image scaledImage = imageIcon.getImage().getScaledInstance(880, 200, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
            imageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            centerPanel.add(imageLabel, BorderLayout.NORTH);
        } catch (Exception e) {
            centerPanel.add(new JLabel("[Image Not Found]", JLabel.CENTER), BorderLayout.NORTH);
        }

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 16));
        area.setMargin(new Insets(10, 10, 10, 10));
        area.setBackground(new Color(248, 248, 255));
        centerPanel.add(new JScrollPane(area), BorderLayout.CENTER);

        panel.add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(new Color(44, 62, 80));
        JButton lexBtn = new JButton(" Lex Code");
        JButton parseBtn = new JButton("Parser Code");
        JButton tokenBtn = new JButton(" Tokens");
        JButton nextBtn = new JButton("Next");

        for (JButton b : new JButton[] { lexBtn, parseBtn, tokenBtn, nextBtn }) {
            b.setFont(new Font("Segoe UI", Font.BOLD, 14));
            b.setBackground(new Color(41, 128, 185));
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            buttonPanel.add(b);
        }

        lexBtn.addActionListener(e -> area.setText(readFile("lex.l")));
        parseBtn.addActionListener(e -> area.setText(readFile("parser.y")));
        tokenBtn.addActionListener(e -> area.setText(getTokenList()));
        nextBtn.addActionListener(e -> cardLayout.show(cardPanel, "analyzer"));

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createAnalyzerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(236, 240, 241));
        JTextPane textPane = new JTextPane();
        textPane.setFont(new Font("Consolas", Font.PLAIN, 16));
        textPane.setEditable(false);
        textPane.setBackground(new Color(248, 248, 255));
        panel.add(new JScrollPane(textPane), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        bottomPanel.setBackground(new Color(44, 62, 80));
        JButton showErrorsButton = new JButton(" Show Errors");
        JButton closeButton = new JButton(" Exit");

        showErrorsButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        showErrorsButton.setBackground(new Color(39, 174, 96));
        showErrorsButton.setForeground(Color.WHITE);
        showErrorsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        closeButton.setBackground(new Color(192, 57, 43));
        closeButton.setForeground(Color.WHITE);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        bottomPanel.add(showErrorsButton);
        bottomPanel.add(closeButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        showErrorsButton.addActionListener(e -> {
            textPane.setText("Analyzing...");
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                protected Void doInBackground() {
                    try {
                        File exe = new File("syntax_analyzer.exe");
                        if (!exe.exists()) {
                            textPane.setText(" syntax_analyzer.exe not found.");
                            return null;
                        }

                        Process process = Runtime.getRuntime().exec(
                                new String[] { "cmd", "/c", "syntax_analyzer.exe < test.c" });

                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        StyledDocument doc = textPane.getStyledDocument();
                        doc.remove(0, doc.getLength());

                        Style errorStyle = textPane.addStyle("error", null);
                        StyleConstants.setForeground(errorStyle, Color.RED);
                        StyleConstants.setBold(errorStyle, true);

                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith("Line ")) {
                                doc.insertString(doc.getLength(), line + "\n", errorStyle);
                            }
                        }

                        process.waitFor();
                    } catch (Exception ex) {
                        try {
                            textPane.setText(" Error running analyzer.");
                        } catch (Exception ignored) {
                        }
                    }
                    return null;
                }
            };
            worker.execute();
        });

        closeButton.addActionListener(e -> System.exit(0));
        return panel;
    }

    private String readFile(String filename) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            sb.append(" Could not load ").append(filename);
        }
        return sb.toString();
    }

    private String getTokenList() {
        return """
                 Tokens Used in Syntax Analyzer:

                - TYPE       → int, float, char, void
                - IF         → if
                - ELSE       → else
                - FOR        → for
                - WHILE      → while
                - RETURN     → return

                - IDENTIFIER → variable/function names
                - NUMBER     → numeric constants (e.g., 123, 3.14)
                - STRING     → strings inside double quotes

                - ASSIGN     → =
                - OP         → +, -, *, /
                - EQ         → ==
                - NEQ        → !=
                - LT, GT     → <, >
                - LE, GE     → <=, >=

                - LPAREN     → (
                - RPAREN     → )
                - LBRACE     → {
                - RBRACE     → }
                - COMMA      → ,
                - SEMICOLON  → ;

                 """;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SyntaxAnalyzerGUI().setVisible(true));
    }
}
