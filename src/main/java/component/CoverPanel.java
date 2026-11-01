package component;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import util.Images;

public class CoverPanel extends JPanel {

    private static final int PADDING = 34;

    private final Image image;
    private final String title;
    private final String subtitle;
    private final String footer;

    public CoverPanel(String imageName, String title, String subtitle, String footer) {
        this.image = Images.load(imageName);
        this.title = title;
        this.subtitle = subtitle;
        this.footer = footer;
        setOpaque(true);
        setBackground(new Color(7, 43, 58));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        int width = getWidth();
        int height = getHeight();
        if (width <= 0 || height <= 0) {
            g2.dispose();
            return;
        }

        if (image != null) {
            double scale = Math.max(width / (double) image.getWidth(null), height / (double) image.getHeight(null));
            int drawWidth = (int) Math.ceil(image.getWidth(null) * scale);
            int drawHeight = (int) Math.ceil(image.getHeight(null) * scale);
            g2.drawImage(image, (width - drawWidth) / 2, (height - drawHeight) / 2, drawWidth, drawHeight, null);
        }

        g2.setPaint(new LinearGradientPaint(
                new Point2D.Float(0, height * 0.30f),
                new Point2D.Float(0, height),
                new float[]{0f, 1f},
                new Color[]{new Color(3, 26, 30, 0), new Color(3, 26, 30, 236)}));
        g2.fillRect(0, 0, width, height);

        int contentWidth = width - PADDING * 2;

        Image logo = Images.scaled("logo.png", 42, 42);
        if (logo != null) {
            g2.drawImage(logo, PADDING, PADDING, null);
        }

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        List<String> subtitleLines = wrap(subtitle, g2.getFontMetrics(), contentWidth);
        int lineHeight = 19;
        int subtitleFirstBaseline = height - 96 - Math.max(0, subtitleLines.size() - 1) * lineHeight;

        g2.setColor(new Color(214, 233, 227));
        for (int i = 0; i < subtitleLines.size(); i++) {
            g2.drawString(subtitleLines.get(i), PADDING, subtitleFirstBaseline + i * lineHeight);
        }

        int titleSize = 34;
        List<String> titleLines = wrap(title, g2.getFontMetrics(new Font("Segoe UI", Font.BOLD, titleSize)), contentWidth);
        while (titleSize > 20 && titleLines.size() > 2) {
            titleSize -= 2;
            titleLines = wrap(title, g2.getFontMetrics(new Font("Segoe UI", Font.BOLD, titleSize)), contentWidth);
        }
        g2.setFont(new Font("Segoe UI", Font.BOLD, titleSize));
        titleLines = wrap(title, g2.getFontMetrics(), contentWidth);
        int titleLineHeight = titleSize + 6;
        int titleFirstBaseline = (subtitleFirstBaseline - 20) - Math.max(0, titleLines.size() - 1) * titleLineHeight;

        g2.setColor(Color.WHITE);
        for (int i = 0; i < titleLines.size(); i++) {
            g2.drawString(titleLines.get(i), PADDING, titleFirstBaseline + i * titleLineHeight);
        }

        g2.setColor(new Color(255, 255, 255, 60));
        g2.fillRect(PADDING, height - 74, 54, 2);

        if (footer != null && !footer.isEmpty()) {
            g2.setColor(new Color(170, 201, 195));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.drawString(footer, PADDING, height - 46);
        }
        g2.dispose();
    }

    private static List<String> wrap(String text, FontMetrics metrics, int maxWidth) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) return lines;
        StringBuilder current = new StringBuilder();
        for (String word : text.trim().split("\\s+")) {
            String candidate = current.length() == 0 ? word : current + " " + word;
            if (metrics.stringWidth(candidate) > maxWidth && current.length() > 0) {
                lines.add(current.toString());
                current = new StringBuilder(word);
            } else {
                current = new StringBuilder(candidate);
            }
        }
        if (current.length() > 0) lines.add(current.toString());
        return lines;
    }
}
