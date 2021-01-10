package cn.sliew.milky.common.util;

/**
 * Some useful constants.
 **/
public final class Constants {
    private Constants() {
    }  // can't construct

    public static final String OS_ARCH = System.getProperty("os.arch");

    /**
     * True iff running on a 64bit JVM
     */
    public static final boolean JRE_IS_64BIT;

    static {
        boolean is64Bit = false;
        String datamodel = null;
        try {
            datamodel = System.getProperty("sun.arch.data.model");
            if (datamodel != null) {
                is64Bit = datamodel.contains("64");
            }
        } catch (SecurityException ex) {
        }
        if (datamodel == null) {
            if (OS_ARCH != null && OS_ARCH.contains("64")) {
                is64Bit = true;
            } else {
                is64Bit = false;
            }
        }
        JRE_IS_64BIT = is64Bit;
    }
}