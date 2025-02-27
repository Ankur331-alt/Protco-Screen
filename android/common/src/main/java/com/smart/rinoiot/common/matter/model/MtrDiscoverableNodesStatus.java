package com.smart.rinoiot.common.matter.model;

import java.io.Serializable;

/**
 * @author edwin
 */
public class MtrDiscoverableNodesStatus implements Serializable {

    public static final  MtrDiscoverableNodesStatus DISCOVERED = new MtrDiscoverableNodesStatus(
            "discovered"
    );
    public static final  MtrDiscoverableNodesStatus ERROR = new MtrDiscoverableNodesStatus(
            "error");
    public static final  MtrDiscoverableNodesStatus NOT_DISCOVERED = new MtrDiscoverableNodesStatus(
            "not-discovered"
    );

    private final String value;

    public MtrDiscoverableNodesStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
