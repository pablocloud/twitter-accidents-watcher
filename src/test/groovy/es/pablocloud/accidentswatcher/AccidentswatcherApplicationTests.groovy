package es.pablocloud.accidentswatcher

import es.pablocloud.accidentswatcher.domain.Accident
import es.pablocloud.accidentswatcher.repositories.AccidentRepositoy
import es.pablocloud.accidentswatcher.services.InstagramService
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
    InstagramService instagramService

    @Autowired
    AccidentRepositoy accidentRepository

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
                    if (accidentRepository.findByTwitterId(accident.twitterId) == null) {
                        accidentRepository.save(accident)
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

    @Test
    void testInstagram() {
        instagramService.search('accident').stream().filter({ it.location != null }).each {
            Accident accident = new Accident()
            accident.instagramId = it.id
            accident.tweetText = it.caption.get('text')
            accident.latitude = it.location.get('lat') as Double
            accident.placeName = it.location.get('city')
            accident.longitude = it.location.get('lng') as Double
            accident.googleMapsUrl = Statics.GOOGLE_MAPS_BASE_URL + accident.latitude + ',' + accident.longitude
            if ((accident.longitude && accident.latitude) || (accident.placeName && accident.countryName)) {
                if (accidentRepository.findByInstagramId(accident.instagramId) == null) {
                    accidentRepository.save(accident)
                }
            }
        }
    }

}
