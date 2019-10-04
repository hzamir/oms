package com.baliset

import org.slf4j.LoggerFactory

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.*
import org.springframework.context.ApplicationContext
import org.springframework.scheduling.annotation.EnableScheduling


@EnableScheduling
@SpringBootApplication
open class Application {

    internal lateinit var applicationContext: ApplicationContext
    //do as much as possible inside an instance of Application rather than in a static method
    private fun stuff() {
        logger.info("Kudos, the server is ready") // todo: replace with pattern that checks all parts are ready

    }

    companion object {
        private val logger = LoggerFactory.getLogger(Application::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            val ctx = SpringApplication.run(Application::class.java, *args)

            val application = ctx.getBean<Application>(Application::class.java)  // the application might as well be a singleton too, no?
            application.applicationContext = ctx  // todo: can this be injected straight into Application instance

            application.stuff()                   // todo: maybe just use initializationaware lifecycle routine for this?

        }
    }
}
