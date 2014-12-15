package org.jsynthlib.utils.ctrlr.service.impl;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.jsynthlib.utils.ctrlr.service.CtrlrImageConverter;
import org.jsynthlib.utils.ctrlr.service.ResourceContainer;
import org.jsynthlib.xmldevice.IntParamSpec;
import org.jsynthlib.xmldevice.PatchParamResources;

import com.google.inject.Singleton;

@Singleton
public class CtrlrImageConverterImpl implements CtrlrImageConverter {

    @Override
    public ResourceContainer convertJslImageResources(IntParamSpec paramSpec,
            File outdir) throws IOException {
        if (!paramSpec.isSetPatchParamResources()) {
            throw new IllegalArgumentException("Found no resources");
        }

        ArrayList<Image> list = new ArrayList<Image>();

        PatchParamResources resources = paramSpec.getPatchParamResources();
        String[] paramResourceArray = resources.getPatchParamResourceArray();
        ClassLoader classLoader = getClass().getClassLoader();

        int maxWidth = 0;
        int maxHeight = 0;
        int imgType = 0;

        for (String string : paramResourceArray) {
            URL url = classLoader.getResource(string);
            BufferedImage image = ImageIO.read(url);
            if (image.getWidth() > maxWidth) {
                maxWidth = image.getWidth();
            }
            if (image.getHeight() > maxHeight) {
                maxHeight = image.getHeight();
            }
            imgType = image.getType();
            list.add(image);
        }

        BufferedImage image =
                new BufferedImage(maxWidth * list.size(), maxHeight,
                        BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        for (int i = 0; i < list.size(); i++) {
            Image img = list.get(i);
            int x = i * maxWidth;
            graphics.drawImage(img, x, 0, x + maxWidth, maxHeight, 0, 0,
                    img.getWidth(null), img.getHeight(null), null);
        }
        File output =
                new File(outdir, paramSpec.getName().replace(' ', '_') + ".png");
        ImageIO.write(image, "png", output);

        graphics.dispose();
        ResourceContainer container = new ResourceContainer();
        container.setFile(output);
        container.setHeight(maxHeight);
        container.setWidth(maxWidth);
        return container;
    }

    Image transformGrayToTransparency(Image image) {
        ImageFilter filter = new RGBImageFilter() {
            @Override
            public final int filterRGB(int x, int y, int rgb) {
                return (rgb << 8) & 0xFF000000;
            }
        };

        ImageProducer ip = new FilteredImageSource(image.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }
}
