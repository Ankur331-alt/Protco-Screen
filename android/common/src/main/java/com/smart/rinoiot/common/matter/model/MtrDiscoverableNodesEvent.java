package com.smart.rinoiot.common.matter.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author edwin
 */
public class MtrDiscoverableNodesEvent implements Serializable {
    private final MtrDiscoverableNodesStatus status;
    private final List<MtrDiscoverableNode> nodes;

    public MtrDiscoverableNodesEvent(MtrDiscoverableNodesStatus status) {
        this.status = status;
        this.nodes = new ArrayList<>();
    }

    public MtrDiscoverableNodesEvent(MtrDiscoverableNodesStatus status, List<MtrDiscoverableNode> nodes) {
        this.status = status;
        this.nodes = nodes;
    }

    public MtrDiscoverableNodesStatus getStatus() {
        return status;
    }

    public List<MtrDiscoverableNode> getNodes() {
        return nodes;
    }
}
