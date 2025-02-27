package com.smart.rinoiot.scan.decode;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.smart.rinoiot.common.utils.LgUtils;
import com.smart.rinoiot.scan.listener.CaptureCallback;
import com.smart.rinoiot.scan.utils.Constants;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Objects;

/**
 * @author tw
 * @time 2022/10/20 11:22
 * @description
 */
public class DecodeHandler extends Handler {

    private final CaptureCallback activity;
    private final MultiFormatReader multiFormatReader;

    DecodeHandler(CaptureCallback activity, Map<DecodeHintType, Object> hints) {
        multiFormatReader = new MultiFormatReader();
        multiFormatReader.setHints(hints);
        this.activity = activity;
    }

    @Override
    public void handleMessage(Message message) {
        if (message.what == Constants.DECODE) {
            decode((byte[]) message.obj);
        } else if (message.what == Constants.QUIT) {
            Objects.requireNonNull(Looper.myLooper()).quit();
        }
    }

    /**
     * Decode the data within the viewfinder rectangle, and time how long it
     * took. For efficiency, reuse the same reader objects from one DECODE to
     * the next.
     *
     * @param data
     *            The YUV preview frame.
     */
    private void decode(byte[] data) {
        try {
        int tmpH;
        Camera.Size size = activity.getCameraManager().getPreviewSize();

        // 这里需要将获取的data翻转一下，因为相机默认拿的的横屏的数据
        byte[] rotatedData = new byte[data.length];
        for (int y = 0; y < size.height; y++) {
            for (int x = 0; x < size.width; x++)
                rotatedData[x * size.height + size.height - y - 1] = data[x + y * size.width];
        }

        // 宽高也要调整
        int tmpW = size.width;
        tmpH = size.height;
        size.width = tmpH;
        size.height = tmpW;

        Result rawResult = null;
        PlanarYUVLuminanceSource source = buildLuminanceSource(rotatedData, size.width, size.height);
        if (source != null) {
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            try {
                rawResult = multiFormatReader.decodeWithState(bitmap);
            } catch (ReaderException re) {
                // continue
            } finally {
                multiFormatReader.reset();
            }
        }

        Handler handler = activity.getHandler();
        if (rawResult != null) {
            // Don't log the barcode contents for security.
            if (handler != null) {
                Message message = Message.obtain(handler, Constants.DECODE_SUCCEEDED, rawResult);
                Bundle bundle = new Bundle();
                bundleThumbnail(source, bundle);
                message.setData(bundle);
                message.sendToTarget();
            }
        } else {
            if (handler != null) {
                Message message = Message.obtain(handler, Constants.DECODE_FAILED);
                message.sendToTarget();
            }
        }
        } catch (Exception e) {
            e.printStackTrace();
            LgUtils.e("扫码解析异常   e="+e.getMessage());
        }
    }

    private static void bundleThumbnail(PlanarYUVLuminanceSource source, Bundle bundle) {
        int[] pixels = source.renderThumbnail();
        int width = source.getThumbnailWidth();
        int height = source.getThumbnailHeight();
        Bitmap bitmap = Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.ARGB_8888);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
        bundle.putByteArray(DecodeThread.BARCODE_BITMAP, out.toByteArray());
    }

    /**
     * A factory method to build the appropriate LuminanceSource object based on
     * the format of the preview buffers, as described by Camera.Parameters.
     *
     * @param data
     *            A preview frame.
     * @param width
     *            The width of the image.
     * @param height
     *            The height of the image.
     * @return A PlanarYUVLuminanceSource instance.
     */
    private PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
        Rect rect = activity.getCropRect();
        if (rect == null) {
            return null;
        }
        // Go ahead and assume it's YUV rather than die.
        return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top, rect.width(), rect.height(), false);
    }

}
