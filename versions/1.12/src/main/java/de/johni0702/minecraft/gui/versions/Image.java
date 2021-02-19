package de.johni0702.minecraft.gui.versions;

import net.minecraft.client.renderer.texture.DynamicTexture;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Image implements AutoCloseable {
    private BufferedImage inner;

    public Image(int width, int height) {
        this(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
    }

    public Image(BufferedImage inner) {
        this.inner = inner;
    }

    public BufferedImage getInner() {
        return inner;
    }

    @Override
    public void close() {
        if (inner != null) {
            inner = null;
        }
    }

    public int getWidth() {
        return inner.getWidth();
    }

    public int getHeight() {
        return inner.getHeight();
    }

    public void setRGBA(int x, int y, int r, int g, int b, int a) {
        inner.setRGB(x, y, ((a & 0xff) << 24) | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff));
    }

    public static Image read(Path path) throws IOException {
        return read(Files.newInputStream(path));
    }

    public static Image read(InputStream in) throws IOException {
        BufferedImage image = ImageIO.read(in);
        if (image == null) {
            throw new IOException("Cannot read image: ImageIO.read returned null");
        }
        return new Image(image);
    }

    public void writePNG(File file) throws IOException {
        ImageIO.write(inner, "PNG", file);
    }

    public void writePNG(OutputStream outputStream) throws IOException {
        ImageIO.write(inner, "PNG", outputStream);
    }

    public Image scaledSubRect(int x, int y, int width, int height, int scaledWidth, int scaledHeight) {
        BufferedImage dst = new BufferedImage(scaledWidth, scaledHeight, inner.getType());
        Graphics2D graphics = dst.createGraphics();
        graphics.drawImage(inner, 0, 0, scaledWidth, scaledHeight,
                x, y, x + width, y + height, null);
        graphics.dispose();
        return new Image(dst);
    }

    public BufferedImage toBufferedImage() {
        return inner;
    }

    public DynamicTexture toTexture() {
        return new DynamicTexture(inner);
    }
}
