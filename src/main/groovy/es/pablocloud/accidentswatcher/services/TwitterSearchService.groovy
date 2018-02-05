package es.pablocloud.accidentswatcher.services

import es.pablocloud.accidentswatcher.Statics
import es.pablocloud.accidentswatcher.domain.Accident
import es.pablocloud.accidentswatcher.repositories.AccidentRepositoy
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import twitter4j.Query
import twitter4j.QueryResult
import twitter4j.Twitter

@Service
class TwitterSearchService {

    final int total = 20
    final Twitter twitter
    final AccidentRepositoy accidentRepositoy

    TwitterSearchService(Twitter twitter, AccidentRepositoy accidentRepositoy){
        this.twitter = twitter
        this.accidentRepositoy = accidentRepositoy
    }

    @Scheduled(fixedRate = 180000L)
    def search(){
        ArrayList<Accident> list = new ArrayList()
        def actual = 0
        Query query = new Query('accident')
        QueryResult queryResult = twitter.search(query)
        while (queryResult.hasNext() && actual < total) {
            if (actual != 0) {
                queryResult = twitter.search(queryResult.nextQuery())
            }
            queryResult.tweets.each {
                Accident accident = new Accident()
                accident.twitterId = it.id
                if (it.place != null) {
                    accident.tweetText = it.text
                    accident.placeName = it.place.name
                    accident.countryName = it.place.country
                    accident.googleMapsUrl = Statics.GOOGLE_MAPS_BASE_URL + accident.placeName + ',' + accident.countryName
                }
                if (it.geoLocation != null) {
                    accident.tweetText = it.text
                    accident.latitude = it.geoLocation.latitude
                    accident.longitude = it.geoLocation.longitude
                    accident.googleMapsUrl = Statics.GOOGLE_MAPS_BASE_URL + accident.latitude + ',' + accident.longitude
                }
                if (accident.longitude && accident.latitude) {
                    if (accidentRepositoy.findByTwitterId(accident.twitterId) == null) {
                        list.add(accident)
                    }
                }
            }
            actual++
        }
        list.each {
            println it.googleMapsUrl
            accidentRepositoy.save(it)
        }
    }

}
