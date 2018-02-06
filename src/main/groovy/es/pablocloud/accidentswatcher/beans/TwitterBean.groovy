package es.pablocloud.accidentswatcher.beans

import es.pablocloud.accidentswatcher.Statics
import es.pablocloud.accidentswatcher.domain.Accident
import es.pablocloud.accidentswatcher.repositories.AccidentRepositoy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import twitter4j.FilterQuery
import twitter4j.StallWarning
import twitter4j.Status
import twitter4j.StatusDeletionNotice
import twitter4j.StatusListener
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.TwitterStream
import twitter4j.TwitterStreamFactory

@Component
class TwitterBean {

    @Autowired
    AccidentRepositoy accidentRepository

    @Bean
    Twitter twitter() {
        TwitterFactory.getSingleton()
    }

    @Bean
    TwitterStream twitterStream() {
        TwitterStream stream = new TwitterStreamFactory().getInstance()
        stream.addListener(new StatusListener() {
            @Override
            void onStatus(Status status) {
                Accident accident = new Accident()
                accident.twitterId = status.id
                if (status.place != null) {
                    accident.tweetText = status.text
                    accident.placeName = status.place.name
                    accident.countryName = status.place.country
                    accident.googleMapsUrl = Statics.GOOGLE_MAPS_BASE_URL + accident.placeName + ',' + accident.countryName
                }
                if (status.geoLocation != null) {
                    accident.tweetText = status.text
                    accident.latitude = status.geoLocation.latitude
                    accident.longitude = status.geoLocation.longitude
                    accident.googleMapsUrl = Statics.GOOGLE_MAPS_BASE_URL + accident.latitude + ',' + accident.longitude
                }
                if (accident.longitude && accident.latitude) {
                    if (accidentRepository.findByTwitterId(accident.twitterId) == null) {
                        accidentRepository.save(accident)
                        println 'From stream : ' + accident.googleMapsUrl
                    }
                }
            }

            @Override
            void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

            }

            @Override
            void onTrackLimitationNotice(int numberOfLimitedStatuses) {

            }

            @Override
            void onScrubGeo(long userId, long upToStatusId) {

            }

            @Override
            void onStallWarning(StallWarning warning) {

            }

            @Override
            void onException(Exception ex) {

            }
        })
        FilterQuery tweetFilterQuery = new FilterQuery()
        tweetFilterQuery.track('accident', 'accidents', 'accidente')
        stream.filter(tweetFilterQuery)
        stream
    }

}
