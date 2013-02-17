package com.kakao.android.common;

import android.content.Context;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import com.kakao.api.imagecache.ImageCache;
import com.kakao.api.imagecache.ImageCache.ImageCacheParams;
import com.kakao.api.imagecache.ImageFetcher;
import com.kakao.api.imagecache.ImageResizer;
import com.kakao.android.R;

/**
 * ImageLoader
 * 이미지 다운로드 및 Memory/Disk(LRU) 캐싱
 */
public class ImageLoader {
    private static ImageLoader instance;
	
	/**
     * Directory for cached profile image 
     */
    private static final String IMAGE_CACHE_DIR = "thumbnail";
    
    /**
     * Memory cache size
     */
    private static final int MEM_CACHE_SIZE = 1024 * 1024 * 5;
    
    /**
     * Disk cache size
     */
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10;
    
    private static ImageCache thumbnailImageCache;
    private ImageResizer thumbnailImageWorker;
    
    private Context context;
    private int imageSize;
    
    /**
     * Get ImageLoader instance.
     * 
     * @return
     */
    public static ImageLoader getInstance(Context context) {
    	if (instance == null) {
    		synchronized (ImageLoader.class) {
				instance = new ImageLoader(context);
			}
    	}
    	
    	return instance;
    }
    
	private ImageLoader(Context context) {
		super();
		this.context = context;
		initImageWorker();
	}
	
	private void initImageWorker() {
		ImageCacheParams cacheParams = new ImageCacheParams(IMAGE_CACHE_DIR);
		cacheParams.memCacheSize = MEM_CACHE_SIZE;
		cacheParams.diskCacheSize = DISK_CACHE_SIZE;
		
		thumbnailImageCache = new ImageCache(context, cacheParams);
		
		thumbnailImageWorker = new ImageFetcher(context, getImageSize(context));
		thumbnailImageWorker.setLoadingImage(R.drawable.icon_kakao); // default thumbnail image
		thumbnailImageWorker.setImageCache(thumbnailImageCache);
	}

	private int getImageSize(Context context) {
		if (imageSize == 0) {
			Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			final int height = display.getHeight();
			final int width = display.getWidth();
			
			imageSize = height > width ? height : width;
		}
		return imageSize;
	}
	
	public void loadThumbnailImage(String url, ImageView imageView) {
		if (thumbnailImageWorker == null) {
			synchronized (ImageLoader.class) {
				initImageWorker();
			}
		}
		
		thumbnailImageWorker.loadImage(url, imageView);
	}
    
}
