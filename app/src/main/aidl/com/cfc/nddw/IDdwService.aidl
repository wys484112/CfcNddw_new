// IDdwService.aidl
package com.cfc.nddw;

// Declare any non-default types here with import statements

interface IDdwService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
    void startPostData();
    void stopPostData();

}
