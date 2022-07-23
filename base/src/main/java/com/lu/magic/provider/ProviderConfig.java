package com.lu.magic.provider;

public class ProviderConfig {
    /**
     * 读取缓存
     */
    private boolean enableReadCache;
    private String authority;
    private String baseUri;

    public ProviderConfig(String authority) {
        setAuthority(authority);
    }

    public String getAuthority() {
        return authority;
    }

    public String getBaseUri() {
        return baseUri;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
        this.baseUri = "content://" + authority;
    }

    public boolean isEnableReadCache() {
        return enableReadCache;
    }

    public void setEnableReadCache(boolean enableReadCache) {
        this.enableReadCache = enableReadCache;
    }

}
