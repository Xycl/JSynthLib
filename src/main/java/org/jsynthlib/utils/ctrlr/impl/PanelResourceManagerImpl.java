package org.jsynthlib.utils.ctrlr.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.FilenameUtils;
import org.ctrlr.panel.PanelResourcesType;
import org.ctrlr.panel.PanelType;
import org.ctrlr.panel.ResourceType;
import org.jsynthlib.utils.ctrlr.CtrlrImageConverter;
import org.jsynthlib.utils.ctrlr.PanelResourceManager;
import org.jsynthlib.utils.ctrlr.ResourceContainer;
import org.jsynthlib.xmldevice.IntParamSpec;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class PanelResourceManagerImpl implements PanelResourceManager {

    private final CtrlrImageConverter imageConverter;

    @Inject
    public PanelResourceManagerImpl(CtrlrImageConverter imageConverter) {
        this.imageConverter = imageConverter;
    }

    @Override
    public ResourceContainer addImageResource(PanelType panel,
            IntParamSpec paramSpec) throws IOException,
            NoSuchAlgorithmException {
        PanelResourcesType panelResources = panel.getPanelResources();
        ResourceType resource = panelResources.addNewResource();

        ResourceContainer container =
                imageConverter.convertJslImageResources(paramSpec,
                        getOutdir(panel));
        File imageFile = container.getFile();
        long hash = getResourceHash(imageFile);
        resource.setResourceHash(hash);
        resource.setResourceSize(imageFile.length());
        resource.setResourceFile(imageFile.getName());
        resource.setResourceSourceFile(imageFile.getAbsolutePath());
        resource.setResourceType("Image");
        resource.setResourceLoadedTime(1416340364320L);
        resource.setResourceName(FilenameUtils.removeExtension(imageFile
                .getName()));

        container.setName(resource.getResourceName());
        return container;
    }

    File getOutdir(PanelType panel) {
        String appDataPath = System.getenv("APPDATA");
        File appData = new File(appDataPath);
        File ctrlr = new File(appData, "Ctrlr");
        return new File(ctrlr, panel.getPanelUID());
    }

    long getResourceHash(File file) throws NoSuchAlgorithmException,
    IOException {
        final MessageDigest messageDigest = MessageDigest.getInstance("SHA1");

        try (InputStream is =
                new BufferedInputStream(new FileInputStream(file))) {
            final byte[] buffer = new byte[1024];
            for (int read = 0; (read = is.read(buffer)) != -1;) {
                messageDigest.update(buffer, 0, read);
            }
        }

        byte[] by = messageDigest.digest();
        long value = 0;
        for (int i = 0; i < by.length; i++) {
            value += (by[i] & 0xffL) << (8 * i);
        }
        return value;
    }

}
