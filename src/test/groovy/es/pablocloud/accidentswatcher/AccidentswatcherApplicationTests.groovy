package es.pablocloud.accidentswatcher

import es.pablocloud.accidentswatcher.domain.Accident
import es.pablocloud.accidentswatcher.repositories.AccidentRepositoy
import es.pablocloud.accidentswatcher.services.TwitterSearchService
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import twitter4j.Query
import twitter4j.QueryResult

@RunWith(SpringRunner)
@SpringBootTest
class AccidentswatcherApplicationTests {

    int total = 40

    @Autowired
    TwitterSearchService twitterSearchService

    @Autowired
    AccidentRepositoy accidentRepositoy

    @Test
    void testTwitter() {
        ArrayList<Accident> list = new ArrayList()
        def actual = 0
        Query query = new Query('accidente OR accident')
        QueryResult queryResult = twitterSearchService.twitter.search(query)
        while (queryResult.hasNext() && actual < total) {
            if (actual != 0) {
                queryResult = twitterSearchService.twitter.search(queryResult.nextQuery())
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
                if ((accident.longitude && accident.latitude) || (accident.placeName && accident.countryName)) {
                    if (accidentRepositoy.findByTwitterId(accident.twitterId) == null) {
                        accidentRepositoy.save(accident)
                        list.add(accident)
                    }
                }
            }
            actual++
        }
        list.each {
            println it.googleMapsUrl
        }
    }

}
