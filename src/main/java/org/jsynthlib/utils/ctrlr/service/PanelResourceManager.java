package org.jsynthlib.utils.ctrlr.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.ctrlr.panel.PanelType;
import org.jsynthlib.xmldevice.IntParamSpec;

public interface PanelResourceManager {

    ResourceContainer addImageResource(PanelType panel, IntParamSpec paramSpec)
            throws IOException, NoSuchAlgorithmException;
}
