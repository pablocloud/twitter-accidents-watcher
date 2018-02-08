package es.pablocloud.accidentswatcher.beans

import org.brunocvcunha.instagram4j.Instagram4j
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class InstagramBean {

    @Bean
    Instagram4j instagram(){
        Instagram4j instagram = Instagram4j.builder().username("").password("").build()
        instagram.setup()
        instagram.login()
        instagram
    }

}
