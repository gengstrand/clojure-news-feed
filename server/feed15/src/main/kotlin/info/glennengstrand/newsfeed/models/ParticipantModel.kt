package info.glennengstrand.newsfeed.models

import kotlin.text.Regex

data class ParticipantModel(val id: Long?, val name: String) {
    val link: String
        get() = "/participant/$id"

    companion object {
        val r = Regex("/participant/(?<id>[0-9]+)")

        fun unlink(link: String): Long {
            val mr = r.matchEntire(link)
            mr?.let { return it.groups["id"]?.value?.toLong() ?: 0L }
            return 0L
        }
    }
}
