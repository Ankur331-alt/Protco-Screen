package com.smart.rinoiot.common.bean;

import java.io.Serializable;
import java.util.HashSet;

/**
 * @author edwin
 */
public class Metadata implements Serializable {

    private long deviceId;
    private long deviceType;
    private int vendorId;
    private int productId;
    private int version;
    private int discriminator;
    private long setupPinCode;
    private int commissioningFlow;
    private boolean isShortDiscriminator;
    private HashSet<Integer> discoveryCapabilities;

    public Metadata(
            long deviceId,
            long deviceType,
            int vendorId,
            int productId,
            int version,
            int discriminator,
            long setupPinCode,
            int commissioningFlow,
            boolean isShortDiscriminator,
            HashSet<Integer> discoveryCapabilities
    ) {
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.vendorId = vendorId;
        this.productId = productId;
        this.version = version;
        this.discriminator = discriminator;
        this.setupPinCode = setupPinCode;
        this.commissioningFlow = commissioningFlow;
        this.isShortDiscriminator = isShortDiscriminator;
        this.discoveryCapabilities = discoveryCapabilities;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public long getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(long deviceType) {
        this.deviceType = deviceType;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getDiscriminator() {
        return discriminator;
    }

    public void setDiscriminator(int discriminator) {
        this.discriminator = discriminator;
    }

    public long getSetupPinCode() {
        return setupPinCode;
    }

    public void setSetupPinCode(long setupPinCode) {
        this.setupPinCode = setupPinCode;
    }

    public int getCommissioningFlow() {
        return commissioningFlow;
    }

    public void setCommissioningFlow(int commissioningFlow) {
        this.commissioningFlow = commissioningFlow;
    }

    public boolean isShortDiscriminator() {
        return isShortDiscriminator;
    }

    public void setShortDiscriminator(boolean shortDiscriminator) {
        isShortDiscriminator = shortDiscriminator;
    }

    public HashSet<Integer> getDiscoveryCapabilities() {
        return discoveryCapabilities;
    }

    public void setDiscoveryCapabilities(HashSet<Integer> discoveryCapabilities) {
        this.discoveryCapabilities = discoveryCapabilities;
    }
}
