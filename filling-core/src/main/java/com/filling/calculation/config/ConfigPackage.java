package com.filling.calculation.config;

public class ConfigPackage {

    String packagePrefix;
    String upperEnginx;
    String sourcePackage;
    String transformPackage;
    String sinkPackage;
    String envPackage;
    String baseSourcePackage ;
    String baseTransformPackage;
    String baseSinkPackage ;
    public ConfigPackage(String engine) {
        packagePrefix = "com.filling.calculation.plugin.base." + engine;
        upperEnginx = engine.substring(0,1).toUpperCase() + engine.substring(1);
        sourcePackage = packagePrefix + ".source";
        transformPackage = packagePrefix + ".transform";
        sinkPackage = packagePrefix + ".sink";
        envPackage = packagePrefix + ".env";
        baseSourcePackage = packagePrefix + ".Base" + upperEnginx + "Source";
        baseTransformPackage = packagePrefix + ".Base" + upperEnginx + "Transform";
        baseSinkPackage = packagePrefix + ".Base" + upperEnginx + "Sink";
    }
    public ConfigPackage() {

    }

    public String getPackagePrefix() {
        return packagePrefix;
    }

    public void setPackagePrefix(String packagePrefix) {
        this.packagePrefix = packagePrefix;
    }

    public String getUpperEnginx() {
        return upperEnginx;
    }

    public void setUpperEnginx(String upperEnginx) {
        this.upperEnginx = upperEnginx;
    }

    public String getSourcePackage() {
        return sourcePackage;
    }

    public void setSourcePackage(String sourcePackage) {
        this.sourcePackage = sourcePackage;
    }

    public String getTransformPackage() {
        return transformPackage;
    }

    public void setTransformPackage(String transformPackage) {
        this.transformPackage = transformPackage;
    }

    public String getSinkPackage() {
        return sinkPackage;
    }

    public void setSinkPackage(String sinkPackage) {
        this.sinkPackage = sinkPackage;
    }

    public String getEnvPackage() {
        return envPackage;
    }

    public void setEnvPackage(String envPackage) {
        this.envPackage = envPackage;
    }

    public String getBaseSourcePackage() {
        return baseSourcePackage;
    }

    public void setBaseSourcePackage(String baseSourcePackage) {
        this.baseSourcePackage = baseSourcePackage;
    }

    public String getBaseTransformPackage() {
        return baseTransformPackage;
    }

    public void setBaseTransformPackage(String baseTransformPackage) {
        this.baseTransformPackage = baseTransformPackage;
    }

    public String getBaseSinkPackage() {
        return baseSinkPackage;
    }

    public void setBaseSinkPackage(String baseSinkPackage) {
        this.baseSinkPackage = baseSinkPackage;
    }
}
