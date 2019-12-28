package com.ma.vodhmsdemo.huawei.common.utils;

public class Key {
    private static final String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgcRiLGZFWpVHasudJfGzvxKccN2T3tyx+BjU5sOn/NVpgkdVue13fzjLgKY18TaPB3LUmHvmKzQDa3duhYbV0tBllLGnZBEysfcoZwv8wVL9+QRp6N34IfU+0Fw1Ds1/6vKbCwzWte9bYnwP36i7Mdxc9UtwY+hrsO18tBXnAZzIUPGc36u69/ky+km3mkss7wRdImexN2Y+ErlmkUVakGZbOWmZyTAt7pvvu59s6/9ajGhPOljWhP0R0V26NW8hDmQTEKVL/R4aFf9ztLeAl/1i1slGeCiBnyN2VxQreQ017uGDfUVPG14/xYpjbq1T7E6uMTQdlif/31x1RMYeHQIDAQAB";

    /**
     * get the publicKey of the application
     * During the encoding process, avoid storing the public key in clear text.
     * @return
     */
    public static String getPublicKey(){
        return publicKey;
    }
}
