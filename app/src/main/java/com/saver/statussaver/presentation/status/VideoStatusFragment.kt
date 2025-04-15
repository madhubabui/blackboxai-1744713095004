package com.saver.statussaver.presentation.status

import com.saver.statussaver.data.model.StatusType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoStatusFragment : StatusFragment() {
    override val statusType: StatusType = StatusType.VIDEO
}
