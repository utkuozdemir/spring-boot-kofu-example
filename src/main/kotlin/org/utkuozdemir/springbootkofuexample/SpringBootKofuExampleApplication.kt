package org.utkuozdemir.springbootkofuexample

import org.springframework.boot.WebApplicationType
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.webmvc.webMvc
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.ServerResponse.ok

val app = application(WebApplicationType.SERVLET) {
    beans {
        //                bean<SampleService>()
        bean("service-a") { SampleService("a") }
        bean("service-b") { SampleService("b") }
//        bean<SampleHandler>()
        bean("handler-a") { SampleHandler(ref("service-a")) }
        bean("handler-b") { SampleHandler(ref("service-b")) }
    }
    webMvc {
        port = 8080
        router {
            val handlerA = ref<SampleHandler>("handler-a")
            val handlerB = ref<SampleHandler>("handler-b")
            GET("/a", handlerA::json)
            GET("/b", handlerB::json)
        }
        converters {
            jackson {
                indentOutput = true
            }
        }
    }
}


fun main() {
    app.run()
}

data class Sample(val message: String)

class SampleService(private val message: String) {
    fun generateMessage() = message
}

@Suppress("UNUSED_PARAMETER")
class SampleHandler(private val sampleService: SampleService) {
    fun json(request: ServerRequest): ServerResponse {
        val param1 = request.param("param1").orElse(null)
        return ok().body(Sample(sampleService.generateMessage() + " - " + param1))
    }
}