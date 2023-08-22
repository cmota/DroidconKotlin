package co.touchlab.droidcon.domain.gateway

import co.touchlab.droidcon.domain.entity.Profile

interface SpeakerGateway {

    fun getAllSpeakers(): List<Profile>
}
