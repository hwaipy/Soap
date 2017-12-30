package com.hwaipy.utilities.system;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 提供系统默认路径。数据从{@link com.hwaipy.systemutilities.Properties Properties}获取。
 *
 * @see com.hwaipy.systemutilities.Properties
 * @author Hwaipy
 */
public class PathsUtilities {

    private static final String KEY_PATH_DATA_STORAGY = "KeyPathDataStorage";
    private static final String DEF_PATH_DATA_STORAGY = ".";

    /**
     *
     * @return
     */
    public static Path getDataStoragyPath() {
        String directory = PropertiesUtilities.getProperty(KEY_PATH_DATA_STORAGY, DEF_PATH_DATA_STORAGY);
        Path path = Paths.get(directory);
        return path;
    }
}
