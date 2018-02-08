package es.pablocloud.accidentswatcher.services

import es.pablocloud.accidentswatcher.Statics
import es.pablocloud.accidentswatcher.domain.Accident
import es.pablocloud.accidentswatcher.repositories.AccidentRepositoy
import org.brunocvcunha.instagram4j.Instagram4j
import org.brunocvcunha.instagram4j.requests.InstagramTagFeedRequest
import org.brunocvcunha.instagram4j.requests.payload.InstagramFeedItem
import org.brunocvcunha.instagram4j.requests.payload.InstagramFeedResult
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class InstagramService {

    final total = 40
    final Instagram4j instagram
    final AccidentRepositoy accidentRepository

    InstagramService(Instagram4j instagram, AccidentRepositoy accidentRepository) {
        this.instagram = instagram
        this.accidentRepository = accidentRepository
    }

    ArrayList<InstagramFeedItem> search(String text) {
        ArrayList<InstagramFeedItem> items = new ArrayList()
        def actual = 0
        def nextid = ''
        while (actual < total) {
            if (actual == 0) {
                InstagramFeedResult tagFeed = instagram.sendRequest(new InstagramTagFeedRequest(text))
                items.addAll(tagFeed.items)
                nextid = tagFeed.next_max_id
            } else {
                InstagramFeedResult tagFeed = instagram.sendRequest(new InstagramTagFeedRequest(text, nextid))
                items.addAll(tagFeed.items)
                nextid = tagFeed.next_max_id
            }
            actual = actual + 1
        }
        items
    }

    @Scheduled(fixedRate = 180000L)
    def searchDaemon() {
        this.search('accident').stream().filter({ it.location != null }).each {
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

    @Scheduled(fixedRate = 180000L)
    def searchSpanishDaemon() {
        this.search('accidente').stream().filter({ it.location != null }).each {
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
