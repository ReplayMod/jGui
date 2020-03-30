package de.johni0702.minecraft.gui.versions;

import net.minecraft.client.texture.NativeImageBackedTexture;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

//#if MC>=11400
import net.minecraft.client.texture.NativeImage;
//#else
//$$ import java.awt.Graphics2D;
//#endif

/**
 * As of LWJGL 3, AWT must never be initialized, otherwise GLFW will be broken on OSX.
 * **Any** usage of BufferedImage will initialize AWT (static initializer).
 * E.g. https://www.replaymod.com/forum/thread/2566
 */
public class Image implements AutoCloseable {
    //#if MC>=11400
    private NativeImage inner;
    //#else
    //$$ private BufferedImage inner;
    //#endif

    public Image(int width, int height) {
        this(
                //#if MC>=11400
                //#if FABRIC>=1
                new NativeImage(NativeImage.Format.RGBA, width, height, true)
                //#else
                //$$ new NativeImage(NativeImage.PixelFormat.RGBA, width, height, true)
                //#endif
                //#else
                //$$ new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
                //#endif
        );
    }

    public Image(
            //#if MC>=11400
            NativeImage inner
            //#else
            //$$ BufferedImage inner
            //#endif
    ) {
        this.inner = inner;
    }

    public
    //#if MC>=11400
    NativeImage
    //#else
    //$$ BufferedImage
    //#endif
    getInner() {
        return inner;
    }

    //#if MC>=11400
    @Override
    protected void finalize() throws Throwable {
        // Great, now we're using a language with GC but still need to take care of memory management.. thanks MC
        close();
        super.finalize();
    }
    //#endif

    @Override
    public void close() {
        if (inner != null) {
            //#if MC>=11400
            inner.close();
            //#endif

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
        //#if MC>=11400
        // actually takes ABGR, not RGBA
        inner.setPixelRgba(x, y, ((a & 0xff) << 24) | ((b & 0xff) << 16) | ((g & 0xff) << 8) | (r & 0xff));
        //#else
        //$$ inner.setRGB(x, y, ((a & 0xff) << 24) | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff));
        //#endif
    }

    public static Image read(Path path) throws IOException {
        return read(Files.newInputStream(path));
    }

    public static Image read(InputStream in) throws IOException {
        return new Image(
                //#if MC>=11400
                NativeImage.read(in)
                //#else
                //$$ ImageIO.read(in)
                //#endif
        );
    }

    public void writePNG(File file) throws IOException {
        //#if MC>=11400
        inner.writeFile(file);
        //#else
        //$$ ImageIO.write(inner, "PNG", file);
        //#endif
    }

    public void writePNG(OutputStream outputStream) throws IOException {
        //#if MC>=11400
        Path tmp = Files.createTempFile("tmp", ".png");
        try {
            inner.writeFile(tmp);
            Files.copy(tmp, outputStream);
        } finally {
            Files.delete(tmp);
        }
        //#else
        //$$ ImageIO.write(inner, "PNG", outputStream);
        //#endif
    }

    public Image scaledSubRect(int x, int y, int width, int height, int scaledWidth, int scaledHeight) {
        //#if MC>=11400
        NativeImage dst = new NativeImage(inner.getFormat(), scaledWidth, scaledHeight, false);
        inner.resizeSubRectTo(x, y, width, height, dst);
        //#else
        //$$ BufferedImage dst = new BufferedImage(scaledWidth, scaledHeight, inner.getType());
        //$$ Graphics2D graphics = dst.createGraphics();
        //$$ graphics.drawImage(inner, 0, 0, scaledWidth, scaledHeight,
        //$$         x, y, x + width, y + height, null);
        //$$ graphics.dispose();
        //#endif
        return new Image(dst);
    }

    @Deprecated // BufferedImage should not be used on 1.13+, see class docs
    public BufferedImage toBufferedImage() {
        //#if MC>=11400
        // Not very efficient but certainly the easiest solution.
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            writePNG(out);
            return ImageIO.read(new ByteArrayInputStream(out.toByteArray()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //#else
        //$$ return inner;
        //#endif
    }

    public NativeImageBackedTexture toTexture() {
        return new NativeImageBackedTexture(inner);
    }
}
