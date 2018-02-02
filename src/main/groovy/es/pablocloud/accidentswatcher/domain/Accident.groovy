package es.pablocloud.accidentswatcher.domain

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Accident {

    @Id
    @GeneratedValue
    Integer id
    Long twitterId
    String tweetText
    String placeName
    String countryName
    Double latitude
    Double longitude
    String googleMapsUrl

}
