package info.glennengstrand.newsfeed.daos

import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

interface Cachable<M> {
    fun parse(value: Mono<String>): Mono<M>

    fun format(value: Mono<M>): Mono<String>

    fun read(id: Long): Mono<M>
}

@Component
class CacheDao {
    fun <M> get(
        id: Long,
        c: Cachable<M>,
    ): Mono<M> {
        return c.read(id)
    }
}
