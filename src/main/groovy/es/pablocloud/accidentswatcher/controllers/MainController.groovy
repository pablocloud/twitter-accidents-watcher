package es.pablocloud.accidentswatcher.controllers

import es.pablocloud.accidentswatcher.domain.Accident
import es.pablocloud.accidentswatcher.domain.Feature
import es.pablocloud.accidentswatcher.domain.GeoJson
import es.pablocloud.accidentswatcher.domain.Geometry
import es.pablocloud.accidentswatcher.repositories.AccidentRepositoy
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping('/api')
class MainController {

    final AccidentRepositoy accidentRepositoy

    MainController(AccidentRepositoy accidentRepositoy) {
        this.accidentRepositoy = accidentRepositoy
    }

    @RequestMapping
    Collection<Accident> get() {
        accidentRepositoy.findAll(new Sort(Sort.Direction.DESC, 'id')).take(100)
    }

    @RequestMapping('/geojson')
    GeoJson geoJson() {
        def accidents = accidentRepositoy.findAll(new Sort(Sort.Direction.DESC, 'id')).take(100)
        GeoJson geoJson = new GeoJson()
        geoJson.type = 'FeatureCollection'
        geoJson.features = new ArrayList<>()
        accidents.each {
            if (it.latitude && it.longitude) {
                geoJson.features.add(new Feature(
                        type: 'Feature',
                        geometry: new Geometry(type: 'Point', coordinates: [it.longitude, it.latitude]),
                        properties: ['tweet': it.tweetText]
                ))
            }
        }
        geoJson
    }


}
