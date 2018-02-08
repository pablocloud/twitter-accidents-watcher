package es.pablocloud.accidentswatcher.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Accident {

    @Id
    @GeneratedValue
    Integer id
    Long twitterId
    String instagramId

    @Column(columnDefinition = 'text')
    String tweetText
    String placeName
    String countryName
    Double latitude
    Double longitude
    String googleMapsUrl

}
