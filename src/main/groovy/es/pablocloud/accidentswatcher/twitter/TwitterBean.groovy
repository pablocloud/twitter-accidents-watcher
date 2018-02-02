package es.pablocloud.accidentswatcher.twitter

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import twitter4j.Twitter
import twitter4j.TwitterFactory

@Component
class TwitterBean {

    @Bean
    Twitter twitter() {
        TwitterFactory.getSingleton()
    }

}
