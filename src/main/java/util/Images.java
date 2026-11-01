package util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public final class Images {

    private static final String[] FOLDERS = {"assets", "images", "../assets", "../images"};

    private Images() {
    }

    public static Image load(String name) {
        if (name == null || name.trim().isEmpty()) return null;
        String property = System.getProperty("canopy.assets");
        if (property != null) {
            Image image = readFile(new File(property, name));
            if (image != null) return image;
        }
        for (String folder : FOLDERS) {
            Image image = readFile(new File(folder, name));
            if (image != null) return image;
        }
        Image image = readFile(new File(name));
        if (image != null) return image;
        URL resource = Images.class.getResource("/images/" + name);
        if (resource != null) {
            try {
                return ImageIO.read(resource);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public static Image scaled(String name, int width, int height) {
        return scale(load(name), width, height);
    }

    public static ImageIcon icon(String name, int size) {
        Image image = scaled(name, size, size);
        return image == null ? null : new ImageIcon(image);
    }

    public static Image scale(Image image, int width, int height) {
        if (image == null || width <= 0 || height <= 0) return image;
        BufferedImage target = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = target.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return target;
    }

    private static Image readFile(File file) {
        try {
            if (file != null && file.isFile()) {
                return ImageIO.read(file);
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
