package org.jsynthlib.device.viewcontroller.widgets;

import static org.junit.Assert.*;

import javax.swing.JPanel;

import org.jsynthlib.device.model.DefaultEnvelopeModel;
import org.jsynthlib.device.model.EnvelopeNode;
import org.jsynthlib.device.model.EnvelopeXParam;
import org.jsynthlib.device.model.EnvelopeYParam;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EnvelopeTest {

    private JPanel envelopeCanvas;
    private DefaultEnvelopeModel model;
    private Envelope envelope;

    @Before
    public void setUp() throws Exception {
        envelope = new Envelope();
        EnvelopeNode[] nodes =
                {
                        new EnvelopeNode(new EnvelopeXParam(0, 100, "Param1X",
                                false), new EnvelopeYParam(0, 0, "Param1Y", 0)),
                        new EnvelopeNode(new EnvelopeXParam(0, 100, "Param2X",
                                true), new EnvelopeYParam(0, 0, "Param2Y", 10)) };
        model = new DefaultEnvelopeModel(nodes);
        envelope.setModel(model);
        envelopeCanvas = envelope.getEnvelopeCanvas();
        envelopeCanvas.setSize(100, 100);
        Thread.sleep(100);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSetSize() throws Exception {
        float scaleX = envelope.getScaleX();
        float scaleY = envelope.getScaleY();
        assertEquals("ScaleX", 0.44, scaleX, 0.0001);
        assertEquals("ScaleY", 8.8, scaleY, 0.0001);
    }

    @Test
    public void testXPos() throws Exception {

        int result = envelope.xPos(0, 10);
        assertEquals("Set xpos 10", 10, result);
        result = envelope.xPos(0, 100);
        assertEquals("Set xpos 100", 50, result);

        result = envelope.xPos(1, 10);
        assertEquals("Set xpos 10", 45, result);
        result = envelope.xPos(1, 100);
        assertEquals("Set xpos 100", 6, result);
    }

}
