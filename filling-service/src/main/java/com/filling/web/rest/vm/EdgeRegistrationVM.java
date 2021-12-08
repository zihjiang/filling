package com.filling.web.rest.vm;

import com.filling.domain.FillingEdgeNodes;

import java.util.Objects;

/**
 * 注册edge的view
 */
public class EdgeRegistrationVM {

    // {"authToken":"123456789","componentId":"7991c301-5168-4925-85ed-fbad5532e138","attributes":{"baseHttpUrl":"http://MrJiangs-MacBook-Pro.local:18633","sdc2goGoVersion":"go1.17","sdc2goGoOS":"darwin","sdc2goGoArch":"arm64","sdc2goBuildDate":"","sdc2goRepoSha":"","sdc2goVersion":""}}
    private String authToken;
    private String componentId;
    private EdgeRegistrationAttributes attributes;

    // {"baseHttpUrl":"http://MrJiangs-MacBook-Pro.local:18633","sdc2goGoVersion":"go1.17","sdc2goGoOS":"darwin","sdc2goGoArch":"arm64","sdc2goBuildDate":"","sdc2goRepoSha":"","sdc2goVersion":""}
    public static class EdgeRegistrationAttributes {
        String baseHttpUrl;
        String sdc2goGoVersion;
        String sdc2goGoOS;
        String sdc2goGoArch;
        String sdc2goBuildDate;
        String sdc2goRepoSha;
        String sdc2goVersion;

        public String getBaseHttpUrl() {
            return baseHttpUrl;
        }

        public void setBaseHttpUrl(String baseHttpUrl) {
            this.baseHttpUrl = baseHttpUrl;
        }

        public String getSdc2goGoVersion() {
            return sdc2goGoVersion;
        }

        public void setSdc2goGoVersion(String sdc2goGoVersion) {
            this.sdc2goGoVersion = sdc2goGoVersion;
        }

        public String getSdc2goGoOS() {
            return sdc2goGoOS;
        }

        public void setSdc2goGoOS(String sdc2goGoOS) {
            this.sdc2goGoOS = sdc2goGoOS;
        }

        public String getSdc2goGoArch() {
            return sdc2goGoArch;
        }

        public void setSdc2goGoArch(String sdc2goGoArch) {
            this.sdc2goGoArch = sdc2goGoArch;
        }

        public String getSdc2goBuildDate() {
            return sdc2goBuildDate;
        }

        public void setSdc2goBuildDate(String sdc2goBuildDate) {
            this.sdc2goBuildDate = sdc2goBuildDate;
        }

        public String getSdc2goRepoSha() {
            return sdc2goRepoSha;
        }

        public void setSdc2goRepoSha(String sdc2goRepoSha) {
            this.sdc2goRepoSha = sdc2goRepoSha;
        }

        public String getSdc2goVersion() {
            return sdc2goVersion;
        }

        public void setSdc2goVersion(String sdc2goVersion) {
            this.sdc2goVersion = sdc2goVersion;
        }
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public EdgeRegistrationAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(EdgeRegistrationAttributes attributes) {
        this.attributes = attributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdgeRegistrationVM that = (EdgeRegistrationVM) o;
        return Objects.equals(authToken, that.authToken) && Objects.equals(componentId, that.componentId) && Objects.equals(attributes, that.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authToken, componentId, attributes);
    }

    public FillingEdgeNodes getEdgeNodes() {
        FillingEdgeNodes fillingEdgeNodes = new FillingEdgeNodes();

        fillingEdgeNodes.setBaseHttpUrl(attributes.getBaseHttpUrl());
        fillingEdgeNodes.setGoBuildDate(attributes.getSdc2goBuildDate());
        fillingEdgeNodes.setGoGoArch(attributes.getSdc2goGoArch());
        fillingEdgeNodes.setGoGoOS(attributes.getSdc2goGoOS());
        fillingEdgeNodes.setGoRepoSha(attributes.getSdc2goRepoSha());
        fillingEdgeNodes.setGoGoVersion(attributes.sdc2goGoVersion);
        fillingEdgeNodes.setUuid(componentId);
        return fillingEdgeNodes;
    }
}
