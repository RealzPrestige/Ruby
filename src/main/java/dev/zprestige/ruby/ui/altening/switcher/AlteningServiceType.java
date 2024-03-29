// 
// Decompiled by Procyon v0.5.36
// 

package dev.zprestige.ruby.ui.altening.switcher;

public enum AlteningServiceType {
    MOJANG("https://authserver.mojang.com/", "https://sessionserver.mojang.com/"),
    THEALTENING("http://authserver.thealtening.com/", "http://sessionserver.thealtening.com/");

    private final String authServer;
    private final String sessionServer;

    AlteningServiceType(final String authServer, final String sessionServer) {
        this.authServer = authServer;
        this.sessionServer = sessionServer;
    }

    public String getAuthServer() {
        return this.authServer;
    }

    public String getSessionServer() {
        return this.sessionServer;
    }
}
