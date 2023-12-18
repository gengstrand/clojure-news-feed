package info.glennengstrand.newsfeed

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NewsfeedApplication

fun main(args: Array<String>) {
        runApplication<NewsfeedApplication>(*args)
}
