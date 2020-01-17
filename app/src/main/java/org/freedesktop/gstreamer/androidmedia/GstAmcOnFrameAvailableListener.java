/*
 * Copyright (C) 2015, Collabora Ltd.
 *   Author: Matthieu Bouron <matthieu.bouron@collabora.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA
 *
 */

package org.freedesktop.gstreamer.androidmedia;

import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.support.annotation.Size;


import java.util.Arrays;

public  class GstAmcOnFrameAvailableListener implements OnFrameAvailableListener
{
    private long context = 0;


    //检测视频播放的textureView的修正矩阵的检测次数。一般视频播放后，第二次的检测就能得到正确的修正矩阵
    private  static final int MAX_DETECT_COUNT = 5;
    private static int detectionCount = 0;

    private static OnSurfaceTextureMtxChangedListener onSurfaceTextureMtxChangedListener = null;

    //获取底层创建的surfaceTexture的矫正矩阵
    private static float mtx[] = new float[16];


    public static void init(OnSurfaceTextureMtxChangedListener listener) {
        //初始化为标准矫正矩阵
        Arrays.fill(mtx, 0.0f);
        mtx[0] = 1.0f;
        mtx[5] = -1.0f;
        mtx[10] = 1.0f;
        mtx[13] = 1.0f;
        mtx[15] = 1.0f;
        detectionCount = 0;
        onSurfaceTextureMtxChangedListener = listener;
    }

    //textureView销毁时（也就是RemoteCanvasActivity销毁时）需要释放资源
    public static void release() {
        detectionCount = 0;
        onSurfaceTextureMtxChangedListener = null;
    }



    private boolean isMatrixChanged(@Size(16) float[] newMtx) {
        for (int i = 0; i < 16; i++){
            if (mtx[i] != newMtx[i]) return true;
        }
        return false;
    }

    public synchronized void onFrameAvailable (SurfaceTexture surfaceTexture) {
        if (detectionCount < MAX_DETECT_COUNT) {
            float[] newMtx = new float[16];
            surfaceTexture.getTransformMatrix(newMtx);
            if (isMatrixChanged(newMtx)) {
                for (int i = 0; i <16; i++) {
                    mtx[i] = newMtx[i];
                }
                if (onSurfaceTextureMtxChangedListener != null) {
                    onSurfaceTextureMtxChangedListener.onMtxChanged(newMtx);
                }
            }
            detectionCount++;
        }
        native_onFrameAvailable(context, surfaceTexture);
    }

    public synchronized long getContext () {
        return context;
    }

    public synchronized void setContext (long c) {
        context = c;
    }

    private native void native_onFrameAvailable (long context, SurfaceTexture surfaceTexture);


    public interface  OnSurfaceTextureMtxChangedListener {
        void onMtxChanged(float[] mtx);
    }

}
