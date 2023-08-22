package co.touchlab.droidcon.domain.gateway.impl

import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.domain.gateway.SpeakerGateway
import co.touchlab.droidcon.domain.repository.ProfileRepository

class DefaultSpeakerGateway(
    private val profileRepository: ProfileRepository,
) : SpeakerGateway {

    override fun getAllSpeakers(): List<Profile> {
        return profileRepository.allSync()
    }
}
