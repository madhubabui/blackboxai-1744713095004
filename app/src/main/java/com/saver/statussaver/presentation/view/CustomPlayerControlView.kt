package com.saver.statussaver.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.TimeBar
import com.saver.statussaver.databinding.CustomPlayerControlViewBinding
import com.saver.statussaver.util.VideoUtils

class CustomPlayerControlView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = CustomPlayerControlViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private var player: Player? = null
    private var controllerCallback: ControllerCallback? = null
    private var isAttachedToWindow = false
    private var shouldShowController = true

    private val updateProgressAction = Runnable { updateProgress() }

    init {
        initializeListeners()
    }

    private fun initializeListeners() {
        with(binding) {
            playPauseButton.setOnClickListener {
                player?.let {
                    if (it.isPlaying) {
                        it.pause()
                    } else {
                        it.play()
                    }
                    updatePlayPauseButton()
                }
            }

            progressBar.addListener(object : TimeBar.OnScrubListener {
                override fun onScrubStart(timeBar: TimeBar, position: Long) {
                    controllerCallback?.onScrubStart()
                }

                override fun onScrubMove(timeBar: TimeBar, position: Long) {
                    binding.currentTime.text = VideoUtils.formatDuration(position)
                }

                override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
                    if (!canceled) {
                        player?.seekTo(position)
                    }
                    controllerCallback?.onScrubStop()
                }
            })
        }
    }

    fun setPlayer(player: Player?) {
        if (this.player === player) return

        this.player?.let {
            it.removeListener(playerListener)
        }

        this.player = player
        player?.addListener(playerListener)
        updateAll()
    }

    fun setControllerCallback(callback: ControllerCallback?) {
        controllerCallback = callback
    }

    fun show() {
        if (!isVisible) {
            isVisible = true
            updateAll()
            controllerCallback?.onControllerVisibilityChanged(true)
        }
        delayedHide()
    }

    fun hide() {
        if (isVisible) {
            isVisible = false
            removeCallbacks(updateProgressAction)
            controllerCallback?.onControllerVisibilityChanged(false)
        }
    }

    private fun delayedHide() {
        removeCallbacks(hideAction)
        if (shouldShowController) {
            postDelayed(hideAction, HIDE_DELAY_MS)
        }
    }

    private val hideAction = Runnable { hide() }

    private fun updateAll() {
        updatePlayPauseButton()
        updateProgress()
    }

    private fun updatePlayPauseButton() {
        val isPlaying = player?.isPlaying == true
        binding.playPauseButton.isSelected = isPlaying
    }

    private fun updateProgress() {
        player?.let {
            val duration = it.duration
            val position = it.currentPosition

            binding.progressBar.setDuration(duration)
            binding.progressBar.setPosition(position)
            binding.progressBar.setBufferedPosition(it.bufferedPosition)

            binding.currentTime.text = VideoUtils.formatDuration(position)
            binding.totalTime.text = VideoUtils.formatDuration(duration)

            // Schedule next progress update
            removeCallbacks(updateProgressAction)
            if (isVisible && isAttachedToWindow) {
                postDelayed(updateProgressAction, PROGRESS_UPDATE_INTERVAL)
            }
        }
    }

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(state: Int) {
            updateAll()
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            updatePlayPauseButton()
            delayedHide()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isAttachedToWindow = true
        if (isVisible) {
            updateProgress()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isAttachedToWindow = false
        removeCallbacks(updateProgressAction)
        removeCallbacks(hideAction)
    }

    interface ControllerCallback {
        fun onScrubStart()
        fun onScrubStop()
        fun onControllerVisibilityChanged(isVisible: Boolean)
    }

    companion object {
        private const val HIDE_DELAY_MS = 3500L
        private const val PROGRESS_UPDATE_INTERVAL = 1000L
    }
}
