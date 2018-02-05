package es.pablocloud.accidentswatcher

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class AccidentswatcherApplication {

    static void main(String[] args) {
        SpringApplication.run AccidentswatcherApplication, args
    }

}
