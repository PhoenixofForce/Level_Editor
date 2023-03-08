package window.elements;

import javax.swing.*;
import java.awt.*;

public class ErrorPanel extends JPanel {

    private long displayTime = 0;

    private JLabel errorDisplay;

    public ErrorPanel() {
        this.errorDisplay = new JLabel("Error");
        this.setLayout(new BorderLayout());
        this.add(errorDisplay, BorderLayout.CENTER);

        this.setBackground(new Color(243, 86, 57));
        errorDisplay.setFont(new Font(errorDisplay.getFont().getName(), errorDisplay.getFont().getStyle(), 14));
    }

    public void update(long dt) {
        displayTime = Math.max(displayTime - dt, 0);

        this.setVisible(displayTime != 0);
    }

    public void setError(String errorMessage, long displayTime) {
        this.errorDisplay.setText(errorMessage);
        this.displayTime = displayTime;
    }
}
