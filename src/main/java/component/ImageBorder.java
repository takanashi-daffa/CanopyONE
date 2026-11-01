package component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.border.Border;
import util.Images;

public class ImageBorder implements Border {

    private final Image image;
    private final Color lineColor;
    private final int lineHeight;

    public ImageBorder(String imageName) {
        this(imageName, null, 0);
    }

    public ImageBorder(String imageName, Color lineColor, int lineHeight) {
        this.image = Images.load(imageName);
        this.lineColor = lineColor;
        this.lineHeight = lineHeight;
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(0, 0, 0, 0);
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        if (image != null && width > 0 && height > 0) {
            double scale = Math.max(width / (double) image.getWidth(null), height / (double) image.getHeight(null));
            int drawWidth = (int) Math.ceil(image.getWidth(null) * scale);
            int drawHeight = (int) Math.ceil(image.getHeight(null) * scale);
            g2.drawImage(image, x + (width - drawWidth) / 2, y + (height - drawHeight) / 2, drawWidth, drawHeight, null);
        } else {
            g2.setColor(new Color(15, 107, 92));
            g2.fillRect(x, y, width, height);
        }
        if (lineColor != null && lineHeight > 0) {
            g2.setColor(lineColor);
            g2.fillRect(x, y + height - lineHeight, width, lineHeight);
        }
        g2.dispose();
    }
}
