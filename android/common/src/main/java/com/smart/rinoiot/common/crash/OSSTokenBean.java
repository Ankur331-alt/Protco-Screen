package com.smart.rinoiot.common.crash;

import java.io.Serializable;

/**
 * @author tw
 * @time 2022/12/19 16:22
 * @description 获取oss token
 */
public class OSSTokenBean implements Serializable {
    private AuthBean auth;
    private String bucket;
    private String dir;
    private String region;

    public AuthBean getAuthBean() {
        return auth;
    }

    public void setAuthBean(AuthBean authBean) {
        this.auth = authBean;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public static class AuthBean implements Serializable {
        private Credentials credentials;
        private String expiration;
        private String expiredTime;
        private String requestId;
        private String startTime;

        public Credentials getCredentials() {
            return credentials;
        }

        public void setCredentials(Credentials credentials) {
            this.credentials = credentials;
        }

        public String getExpiration() {
            return expiration;
        }

        public void setExpiration(String expiration) {
            this.expiration = expiration;
        }

        public String getExpiredTime() {
            return expiredTime;
        }

        public void setExpiredTime(String expiredTime) {
            this.expiredTime = expiredTime;
        }

        public String getRequestId() {
            return requestId;
        }

        public void setRequestId(String requestId) {
            this.requestId = requestId;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public static class Credentials implements Serializable {
            private String sessionToken;
            private String tmpSecretId;
            private String tmpSecretKey;
            private String token;

            public String getSessionToken() {
                return sessionToken;
            }

            public void setSessionToken(String sessionToken) {
                this.sessionToken = sessionToken;
            }

            public String getTmpSecretId() {
                return tmpSecretId;
            }

            public void setTmpSecretId(String tmpSecretId) {
                this.tmpSecretId = tmpSecretId;
            }

            public String getTmpSecretKey() {
                return tmpSecretKey;
            }

            public void setTmpSecretKey(String tmpSecretKey) {
                this.tmpSecretKey = tmpSecretKey;
            }

            public String getToken() {
                return token;
            }

            public void setToken(String token) {
                this.token = token;
            }
        }
    }
}
