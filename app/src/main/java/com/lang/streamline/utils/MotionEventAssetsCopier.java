package com.lang.streamline.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@SuppressLint("NewApi")
public class MotionEventAssetsCopier {
    private static final String TAG = "MotionEventAssetsCopier";
    private static final String TARGET_DIRECTORY = "MotionEvent";

    public boolean copyAssetsToFiles(Context context) {
        File filesDir = context.getFilesDir();
        AssetManager assetManager = context.getAssets();

        try {
            // Ensure the target directory exists
            if (!filesDir.exists() && !filesDir.mkdirs()) {
                Log.e(TAG, "Failed to create target directory: " + filesDir.getAbsolutePath());
                return false;
            }

            // Copy assets recursively
            copyAssetFolder(assetManager, "", filesDir.getAbsolutePath());
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error copying assets", e);
            return false;
        }
    }

    public boolean copyAssetsToFiles(Context context, AssetManager assetManager) {
        File filesDir = new File(context.getFilesDir(), TARGET_DIRECTORY);
        try {
            // Ensure the target directory exists
            if (!filesDir.exists() && !filesDir.mkdirs()) {
                Log.e(TAG, "Failed to create target directory: " + filesDir.getAbsolutePath());
                return false;
            }

            // Copy assets recursively
            copyAssetFolder(assetManager, "", filesDir.getAbsolutePath());
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error copying assets", e);
            return false;
        }
    }

    public boolean copyAssetsToFiles(Context context, Resources resources) {
        File filesDir = new File(context.getFilesDir(), TARGET_DIRECTORY);
        AssetManager assetManager = resources.getAssets();

        try {
            // Ensure the target directory exists
            if (!filesDir.exists() && !filesDir.mkdirs()) {
                Log.e(TAG, "Failed to create target directory: " + filesDir.getAbsolutePath());
                return false;
            }

            // Copy assets recursively
            copyAssetFolder(assetManager, "", filesDir.getAbsolutePath());
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error copying assets", e);
            return false;
        }
    }

    private void copyAssetFolder(AssetManager assetManager, String path, String targetPath) throws IOException {
        String[] assets = assetManager.list(path);

        if (assets == null || assets.length == 0) {
            // It's a file, copy it
            copyAssetFile(assetManager, path, targetPath);
        } else {
            // It's a folder, create it and recurse
            new File(targetPath).mkdirs();
            for (String asset : assets) {
                String subPath = path.isEmpty() ? asset : path + File.separator + asset;
                String subTargetPath = targetPath + File.separator + asset;
                copyAssetFolder(assetManager, subPath, subTargetPath);
            }
        }
    }

    private void copyAssetFile(AssetManager assetManager, String assetPath, String targetPath) throws IOException {
        File targetFile = new File(targetPath);

        // Ensure the parent directory exists
        Objects.requireNonNull(targetFile.getParentFile()).mkdirs();

        try (InputStream in = assetManager.open(assetPath);
             OutputStream out = Files.newOutputStream(targetFile.toPath())) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }

        Log.d(TAG, "Copied new asset file: " + assetPath + " to " + targetPath);
    }

    public static boolean deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            File[] children = fileOrDirectory.listFiles();
            if (children != null) {
                for (File child : children) {
                    // 递归删除子目录和文件
                    if (!deleteRecursive(child)) {
                        return false;
                    }
                }
            }
        }
        // 删除文件或空目录，返回删除结果
        Log.d(TAG, "delete file："+fileOrDirectory.getAbsolutePath());
        return fileOrDirectory.delete();
    }

    /**
     * 递归复制目录及其内容
     *
     * @param sourceDir 源目录（必须存在且为目录）
     * @param destDir   目标目录，如果目录不存在将创建
     * @return 如果复制成功返回 true，否则 false
     */
    public static boolean copyDirectory(File sourceDir, File destDir) {
        if (sourceDir == null || destDir == null) {
            return false;
        }
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            return false;
        }
        // 如果目标目录不存在，则尝试创建
        if (!destDir.exists() && !destDir.mkdirs()) {
            return false;
        }
        File[] files = sourceDir.listFiles();
        if (files != null) {
            for (File file : files) {
                File destFile = new File(destDir, file.getName());
                if (file.isDirectory()) {
                    // 递归复制子目录
                    if (!copyDirectory(file, destFile)) {
                        return false;
                    }
                } else {
                    // 复制文件
                    if (!copyFile(file, destFile)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean copyFile(File sourceFile, File destFile) {
        if (sourceFile == null || destFile == null) {
            return false;
        }
        if (!sourceFile.exists() || !sourceFile.isFile()) {
            return false;
        }

        // 创建目标目录
        File parentDir = destFile.getParentFile();
        if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
            return false;
        }

        try {
            // 复制并覆盖已有文件
            Log.d(TAG, "copyFile: " + sourceFile.toPath() + " to " + destFile.toPath());
            Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


}