package com.kstechnologies.NanoScan;

public class ScanData {

    private String wave;
    private String Intensity ;
    private String absorb;
    private String reflect;

    public String getWave() {
        return wave;
    }

    public void setWave(String wave) {
        this.wave = wave;
    }

    public String getIntensity() {
        return Intensity;
    }

    public void setIntensity(String intensity) {
        Intensity = intensity;
    }

    public String getAbsorb() {
        return absorb;
    }

    public void setAbsorb(String absorb) {
        this.absorb = absorb;
    }

    public String getReflect() {
        return reflect;
    }

    public void setReflect(String reflect) {
        this.reflect = reflect;
    }
}
