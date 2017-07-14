package com.example.mrxu.base;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by MrXu on 2017/5/12.
 */

public class MApplication extends Application {

    public static boolean isDebug;
    public static String APP_NAME;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TAGss", "initImageLoader: ");
        initImageLoader(getApplicationContext());

    }


    public static void initImageLoader(Context context) {
        File cacheDir = StorageUtils.getIndividualCacheDirectory(context);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
                // 如果图片尺寸大于了这个参数，那么就会这按照这个参数对图片大小进行限制并缓�?


                .memoryCacheExtraOptions(480, 800)
                .discCacheExtraOptions(480, 800, Bitmap.CompressFormat.JPEG, 75, null)
                .threadPoolSize(5)
                // 异步线程�?
                .threadPriority(Thread.NORM_PRIORITY - 1)
                // 线程优先�?
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024)) // 内存缓存大小
                .memoryCacheSize(2 * 1024 * 1024) // 内存缓存大小
                .discCache(new UnlimitedDiscCache(cacheDir)) // 硬盘缓存路径
                .discCacheSize(50 * 1024 * 1024) // 硬盘缓存大小
                .discCacheFileCount(100) // 缓存文件�?
                .discCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(context)) // default
                .imageDecoder(new BaseImageDecoder(true)) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .build();
        ImageLoader.getInstance().init(config);
    }


}
