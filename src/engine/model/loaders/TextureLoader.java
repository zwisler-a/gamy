package engine.model.loaders;

import common.Logger.Logger;
import engine.model.Texture;
import engine.model.store.TextureStore;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public class TextureLoader {
    private static final int BYTES_PER_PIXEL = 4;//3 for RGB, 4 for RGBA

    /**
     * Loads and stores a texture
     *
     * @param path Location of the texture
     * @return Texture
     */
    public static Texture loadTexture(String path) {
        return loadTexture(path, TextureLoader.loadImage(path));
    }

    /**
     * Loads and stores a texture
     *
     * @param identifier most of the times the path of the texture. Used to retrieve a already loaded texture
     * @param image      Image
     * @return Texture
     */
    public static Texture loadTexture(String identifier, BufferedImage image) {

        // Check if already loaded
        Texture textureStoreTexture = TextureStore.getInstance().get(identifier);
        if (textureStoreTexture != null) {
            Logger.debug("Retrieved Texture: " + identifier);
            return textureStoreTexture;
        }

        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL); //4 for RGBA, 3 for RGB

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                buffer.put((byte) (pixel & 0xFF));               // Blue component
                buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
            }
        }

        buffer.flip(); //FOR THE LOVE OF GOD DO NOT FORGET THIS

        // You now have a ByteBuffer filled with the color data of each pixel.
        // Now just create a texture ID and bind it. Then you can load it using
        // whatever OpenGL method you want, for example:

        int textureID = glGenTextures(); //Generate texture ID
        glBindTexture(GL_TEXTURE_2D, textureID); //Bind texture ID

        //Setup wrap mode
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        //Setup texture scaling filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        //Send texel data to OpenGL
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        Texture texture = new Texture(textureID);

        // Register in TextureStore
        TextureStore.getInstance().add(identifier, texture);
        Logger.debug("Loaded Texture: " + identifier);

        //Return the texture ID so we can bind it later again
        return texture;
    }

    /**
     * Loads a image
     *
     * @param loc Location of the image
     * @return Buffered Image
     */
    public static BufferedImage loadImage(String loc) {
        try {
            return ImageIO.read(new File(loc));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
