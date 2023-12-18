package info.glennengstrand.newsfeed.models

data class ParticipantModel(val id: Long, val name: String) {
    val link: String 
        get() = "/participant/$id"
}
