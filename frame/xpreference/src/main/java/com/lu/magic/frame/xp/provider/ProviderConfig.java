package com.lu.magic.frame.xp.provider;

public class ProviderConfig {
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


}
