package co.touchlab.droidcon.android.viewModel.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SpeakersViewModel : ViewModel(), KoinComponent {

    private val profileRepository by inject<ProfileRepository>()

    private val speakers = MutableStateFlow<List<Profile>?>(null)

    init {
        viewModelScope.launch {
            speakers.value = profileRepository.allSync()
        }
    }
}
