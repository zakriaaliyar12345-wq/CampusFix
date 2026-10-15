package com.example.campusfix.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.InputStream;

/**
 * Photo handling for report attachments.
 *
 * Photos are chosen with SAF (GetContent) or captured with the system camera
 * through a FileProvider content:// grant — no storage permissions are
 * required, and captured files live only in the app's private cache
 * directory. Decoding must happen on a background thread by the caller
 * because full-size decode is too heavy for the UI thread.
 */
public final class PhotoUtils {

    private static final int MAX_DIMENSION = 1280;

    private PhotoUtils() {
    }

    /** Creates the private directory where camera captures are written. */
    public static File getPhotoDir(Context context) {
        File dir = new File(context.getCacheDir(), "photos");
        if (!dir.exists() && !dir.mkdirs()) {
            // Fall back to cache root; still private to the app.
            return context.getCacheDir();
        }
        return dir;
    }

    /**
     * Reserves a new capture file and returns the FileProvider content:// URI
     * string the camera app should write to (required on API 24+ — passing
     * raw file:// URIs to other apps throws FileUriExposedException).
     */
    public static String newCaptureUri(Context context) {
        try {
            File file = File.createTempFile("report_", ".jpg", getPhotoDir(context));
            return FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".fileprovider",
                    file).toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Decodes and downsamples a URI string to at most {@link #MAX_DIMENSION}px
     * on the longest edge.
     *
     * @return the bitmap, or null when the URI is missing/unreadable —
     * callers must treat null as "no photo" instead of crashing.
     */
    @Nullable
    public static Bitmap decodeSampled(Context context, @Nullable String uriString) {
        if (uriString == null || uriString.isEmpty()) return null;

        try (InputStream in = context.getContentResolver()
                .openInputStream(Uri.parse(uriString))) {
            if (in == null) return null;

            // First pass: read dimensions only.
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, bounds);

            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null;

            // Choose the largest inSampleSize that keeps the image >= MAX_DIMENSION.
            int sample = 1;
            while (Math.max(bounds.outWidth, bounds.outHeight) / (sample * 2)
                    >= MAX_DIMENSION) {
                sample *= 2;
            }

            // Second pass: real decode with the computed sample size.
            try (InputStream in2 = context.getContentResolver()
                    .openInputStream(Uri.parse(uriString))) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = sample;
                return BitmapFactory.decodeStream(in2, null, options);
            }
        } catch (Exception e) {
            // Unreadable/malformed URI: degrade to "no photo" (never crash).
            return null;
        }
    }
}
