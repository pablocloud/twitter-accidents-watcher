package es.pablocloud.accidentswatcher.services

import org.springframework.stereotype.Service
import twitter4j.Twitter

@Service
class TwitterSearchService {

    final Twitter twitter

    TwitterSearchService(Twitter twitter){
        this.twitter = twitter
    }

}
