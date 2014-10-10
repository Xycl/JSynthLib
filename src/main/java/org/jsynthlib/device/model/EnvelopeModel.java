package org.jsynthlib.device.model;

public interface EnvelopeModel extends Iterable<EnvelopeNode> {

    EnvelopeNode getNode(int i);

    int getNumNodes();

    int getNumFaders();

}
