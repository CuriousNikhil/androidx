/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.support.v17.leanback.media;

import android.content.Context;
import android.support.annotation.CallSuper;

/**
 * Base class for abstraction of media play/pause feature. A subclass of PlaybackGlue will contain
 * implementation of Media Player or a connection to playback Service. App initializes
 * PlaybackGlue subclass, associated it with a {@link PlaybackGlueHost}. {@link PlaybackGlueHost}
 * is typically implemented by a Fragment or an Activity, it provides the environment to render UI
 * for PlaybackGlue object, it optionally provides SurfaceHolder via {@link SurfaceHolderGlueHost}
 * to render video. A typical PlaybackGlue should release resources (e.g. MediaPlayer or connection
 * to playback Service) in {@link #onDetachedFromHost()}.
 * {@link #onDetachedFromHost()} is called in two cases:
 * <ul>
 * <li> app manually change it using {@link #setHost(PlaybackGlueHost)} call</li>
 * <li> When host (fragment or activity) is destroyed </li>
 * </ul>
 * In rare case if an PlaybackGlue wants to live outside fragment / activity life cycle, it may
 * manages resource release by itself.
 *
 * @see PlaybackGlueHost
 */
public abstract class PlaybackGlue {
    private final Context mContext;
    private PlaybackGlueHost mPlaybackGlueHost;

    /**
     * Interface to allow clients to take action once the video is ready to play.
     */
    public abstract static class PlayerCallback {
        /**
         * This method is fired when the video is ready for playback.
         */
        public abstract void onReadyForPlayback();
    }

    /**
     * Constructor.
     */
    public PlaybackGlue(Context context) {
        this.mContext = context;
    }

    /**
     * Returns the context.
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * Returns true when the media player is ready to start media playback. Subclasses must
     * implement this method correctly. When returning false, app may listen to
     * {@link PlayerCallback#onReadyForPlayback()} event.
     *
     * @see PlayerCallback#onReadyForPlayback()
     */
    public boolean isReadyForPlayback() {
        return true;
    }

    /**
     * Sets the {@link PlayerCallback} callback.
     */
    public void setPlayerCallback(PlayerCallback playerCallback) {
    }

    /**
     * Starts the media player.
     */
    public void play() {
    }

    /**
     * Pauses the media player.
     */
    public void pause() {
    }

    /**
     * Goes to the next media item. This method is optional.
     */
    public void next() {
    }

    /**
     * Goes to the previous media item. This method is optional.
     */
    public void previous() {
    }

    /**
     * This method is used to associate a PlaybackGlue with the {@link PlaybackGlueHost} which
     * provides UI and optional {@link SurfaceHolderGlueHost}.
     *
     * @param host The host for the PlaybackGlue. Set to null to detach from the host.
     */
    public final void setHost(PlaybackGlueHost host) {
        if (mPlaybackGlueHost == host) {
            return;
        }
        if (mPlaybackGlueHost != null) {
            mPlaybackGlueHost.attachToGlue(null);
        }
        mPlaybackGlueHost = host;
        if (mPlaybackGlueHost != null) {
            mPlaybackGlueHost.attachToGlue(this);
        }
    }

    /**
     * This method is called when {@link PlaybackGlueHost is started. Subclass may override.
     */
    protected void onHostStart() {
    }

    /**
     * This method is called when {@link PlaybackGlueHost is stopped. Subclass may override.
     */
    protected void onHostStop() {
    }

    /**
     * This method is called when {@link PlaybackGlueHost is resumed. Subclass may override.
     */
    protected void onHostResume() {
    }

    /**
     * This method is called when {@link PlaybackGlueHost is paused. Subclass may override.
     */
    protected void onHostPause() {
    }

    /**
     * This method is called attached to associated {@link PlaybackGlueHost}. Subclass may override
     * and call super.onAttachedToHost().
     */
    @CallSuper
    protected void onAttachedToHost(PlaybackGlueHost host) {
        mPlaybackGlueHost = host;
        mPlaybackGlueHost.setHostCallback(new PlaybackGlueHost.HostCallback() {
            @Override
            public void onHostStart() {
                PlaybackGlue.this.onHostStart();
            }

            @Override
            public void onHostStop() {
                PlaybackGlue.this.onHostStop();
            }

            @Override
            public void onHostResume() {
                PlaybackGlue.this.onHostResume();
            }

            @Override
            public void onHostPause() {
                PlaybackGlue.this.onHostPause();
            }

            @Override
            public void onHostDestroy() {
                if (mPlaybackGlueHost != null) {
                    mPlaybackGlueHost.attachToGlue(null);
                }
            }
        });
    }

    /**
     * This method is called when current associated {@link PlaybackGlueHost} is attached to a
     * different {@link PlaybackGlue} or {@link PlaybackGlueHost} is destroyed . Subclass may
     * override and call super.onDetachedFromHost() at last. A typical PlaybackGlue will release
     * resources (e.g. MediaPlayer or connection to playback service) in this method.
     */
    @CallSuper
    protected void onDetachedFromHost() {
        if (mPlaybackGlueHost != null) {
            mPlaybackGlueHost.setHostCallback(null);
            mPlaybackGlueHost = null;
        }
    }

    /**
     * @return Associated {@link PlaybackGlueHost} or null if not attached to host.
     */
    public PlaybackGlueHost getHost() {
        return mPlaybackGlueHost;
    }
}
