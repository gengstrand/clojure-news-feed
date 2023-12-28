package info.glennengstrand.newsfeed.daos

import org.springframework.stereotype.Component

interface Cachable<M> {
    fun parse(value: String): M

    fun format(value: M): String

    fun read(id: Long): M?
}

@Component
class CacheDao {
    fun <M> get(
        id: Long,
        c: Cachable<M>,
    ): M? {
        return c.read(id)
    }
}
