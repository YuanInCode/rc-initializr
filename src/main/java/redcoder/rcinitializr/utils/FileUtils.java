package redcoder.rcinitializr.utils;

import org.springframework.lang.Nullable;

import java.io.File;

/**
 * 支持文件查找、删除功能的工具类
 *
 * @author redcoder54
 * @since 2021-08-20
 */
public abstract class FileUtils {

    /**
     * 查找目标文件
     *
     * @param searchableFile 要搜索的文件（可能是具体文件也可能是目录文件）
     * @param targetFileName 要查找的文件名称
     * @return 目标文件，如果未找到，返回null
     */
    @Nullable
    public static File getFile(File searchableFile, String targetFileName) {
        if (searchableFile.isFile() && searchableFile.getName().equals(targetFileName)) {
            return searchableFile;
        }
        File[] subFiles = searchableFile.listFiles();
        if (subFiles != null) {
            for (File f : subFiles) {
                File target = getFile(f, targetFileName);
                if (target != null) {
                    return target;
                }
            }
        }
        return null;
    }

    /**
     * 查找目标目录文件
     *
     * @param searchableFile 要搜索的目录文件
     * @param targetFileName 要查找的文件名称
     * @return 目标文件，如果未找到，返回null
     */
    @Nullable
    public static File getDirFile(File searchableFile, String targetFileName) {
        if (searchableFile.isDirectory() && searchableFile.getName().equals(targetFileName)) {
            return searchableFile;
        }
        File[] subFiles = searchableFile.listFiles();
        if (subFiles != null) {
            for (File f : subFiles) {
                File target = getDirFile(f, targetFileName);
                if (target != null) {
                    return target;
                }
            }
        }
        return null;
    }

    /**
     * 删除目录文件，如果该目录下存在子目录或文件，会进行递归删除
     *
     * @param dirFile 目录文件
     * @return true：删除成功
     */
    public static boolean deleteDir(File dirFile) {
        File[] subFiles = dirFile.listFiles();
        if (subFiles != null) {
            // 先删除当前目录下所有文件，包含子目录
            for (File file : subFiles) {
                if (file.isDirectory()) {
                    deleteDir(file);
                } else {
                    file.delete();
                }
            }
        }
        // 再删除当前目录
        return dirFile.delete();
    }
}
