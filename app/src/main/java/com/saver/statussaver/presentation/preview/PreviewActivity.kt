package com.saver.statussaver.presentation.preview

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.material.snackbar.Snackbar
import com.saver.statussaver.R
import com.saver.statussaver.data.model.Status
import com.saver.statussaver.databinding.ActivityPreviewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PreviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreviewBinding
    private val viewModel: PreviewViewModel by viewModels()
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupButtons()
        observeViewModel()

        // Get status from intent
        intent.getParcelableExtra<Status>(EXTRA_STATUS)?.let { status ->
            viewModel.setStatus(status)
        } ?: finish()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupButtons() {
        binding.saveButton.setOnClickListener {
            viewModel.saveStatus()
        }

        binding.shareButton.setOnClickListener {
            viewModel.getStatus()?.let { status ->
                shareStatus(status)
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is PreviewUiState.Loading -> showLoading()
                    is PreviewUiState.Success -> showContent(state.status)
                    is PreviewUiState.Error -> showError(state.message)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.events.collectLatest { event ->
                when (event) {
                    is PreviewEvent.ShowMessage -> showMessage(event.message)
                    is PreviewEvent.StatusSaved -> {
                        showMessage(getString(R.string.msg_save_success))
                        binding.saveButton.isEnabled = false
                    }
                }
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.photoView.visibility = View.GONE
        binding.playerView.visibility = View.GONE
    }

    private fun showContent(status: Status) {
        binding.progressBar.visibility = View.GONE
        
        if (status.isVideo) {
            setupVideoPlayer(status)
        } else {
            setupImagePreview(status)
        }
    }

    private fun setupVideoPlayer(status: Status) {
        binding.playerView.visibility = View.VISIBLE
        binding.photoView.visibility = View.GONE

        player = ExoPlayer.Builder(this).build().apply {
            binding.playerView.player = this
            setMediaItem(MediaItem.fromUri(status.uri))
            repeatMode = Player.REPEAT_MODE_ONE
            playWhenReady = true
            prepare()
        }
    }

    private fun setupImagePreview(status: Status) {
        binding.photoView.visibility = View.VISIBLE
        binding.playerView.visibility = View.GONE

        Glide.with(this)
            .load(status.uri)
            .into(binding.photoView)
    }

    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        showMessage(message)
        finish()
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun shareStatus(status: Status) {
        try {
            val contentUri = FileProvider.getUriForFile(
                this,
                "${packageName}.provider",
                status.file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = if (status.isVideo) "video/*" else "image/*"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(Intent.createChooser(intent, getString(R.string.action_share)))
        } catch (e: Exception) {
            showMessage(getString(R.string.error_sharing))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            exoPlayer.stop()
            exoPlayer.release()
            player = null
        }
    }

    companion object {
        private const val EXTRA_STATUS = "extra_status"

        fun createIntent(context: Context, status: Status): Intent {
            return Intent(context, PreviewActivity::class.java).apply {
                putExtra(EXTRA_STATUS, status)
            }
        }
    }
}
