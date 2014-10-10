package org.jsynthlib.device.model;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class DefaultEnvelopeModel implements EnvelopeModel {

    private final EnvelopeNode[] nodes;

    public DefaultEnvelopeModel(EnvelopeNode[] nodes) {
        super();
        this.nodes = nodes;
    }

    @Override
    public EnvelopeNode getNode(int i) {
        return nodes[i];
    }

    @Override
    public int getNumNodes() {
        return nodes.length;
    }

    @Override
    public int getNumFaders() {
        // count the number of parameters (faders)
        int j = 0;
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i].isVariableX()) {
                j++;
            }
            if (nodes[i].isVariableY()) {
                j++;
            }
        }
        return j;
    }

    @Override
    public Iterator<EnvelopeNode> iterator() {
        List<EnvelopeNode> list = Arrays.asList(nodes);
        return list.iterator();
    }

}
