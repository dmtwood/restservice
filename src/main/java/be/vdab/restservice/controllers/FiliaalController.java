package be.vdab.restservice.controllers;

import be.vdab.restservice.domain.Filiaal;
import be.vdab.restservice.exceptions.FiliaalNietGevondenException;
import be.vdab.restservice.services.FiliaalService;
import com.sun.jdi.Method;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
// requesting clients need response in (xml or) json, no html (xml >> @XmlRootElement   @XmlAccessorType(XmlAccessType.FIELD) EntityClass (Filiaal)
@RequestMapping("/filialen")
@ExposesResourceFor(Filiaal.class)
class FiliaalController {
    private final FiliaalService filiaalService;
    private final EntityLinks entityLinks;

    public FiliaalController(FiliaalService filiaalService, EntityLinks entityLinks) {
        this.filiaalService = filiaalService;
        this.entityLinks = entityLinks;
    }

    /**
     * method searching database for Filiaal with given id
     *
     * @param id primary key in failialen table
     * @return Filiaal with id or Filiaal Niet Gevonden Exception
     */
    @GetMapping("{id}")
//    public Filiaal get(
    // when there must be links included in json data, use EntityModel
    EntityModel<Filiaal> get(@PathVariable long id) {
        return
                filiaalService.findById(id)
                        // map the filiaal to its EntityModel
                        .map(filiaal -> EntityModel.of(
                                filiaal,

                                // create link " /filiaal/id " To the Filiaal with the given id
                                entityLinks.linkToItemResource(Filiaal.class, filiaal.getId()),

                                // create link " /filiaal/id/werknemers "
                                entityLinks.linkForItemResource(Filiaal.class, filiaal.getId()).slash("werknemers")
                                        // create LinkRelation "werknemers:" in response | to werknemers of the searched filiaal
                                        .withRel("werknemers")
                        ))
//                    .get()
                        .orElseThrow(FiliaalNietGevondenException::new);
    }


    @GetMapping
    // CollectionModel creates a response holding a collection of all
        // 'FiliaalIdNaam'-EntityModel objects          (basic info / performant )
        // and hyperlink(s) to related Filiaal objects  (full info)
    CollectionModel< EntityModel< FiliaalIdNaam > > findAll() {
        return CollectionModel.of(
                filiaalService.findAll().stream()
                        .map(filiaal -> EntityModel.of(
                                new FiliaalIdNaam(filiaal),
                                entityLinks.linkToItemResource( Filiaal.class, filiaal.getId() ) )  )
                        .collect( Collectors.toList() ),
                entityLinks.linkToCollectionResource(Filiaal.class));
    }


    // if get() throws exception, handle it here
    @ExceptionHandler(FiliaalNietGevondenException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    void filiaalNietGevonden() {
    }

    /**
     * method deleting Filiaal from database with given id
     *
     * @param id primary key in failialen table
     */
    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        filiaalService.delete(id);
    }

    /**
     * method to post a Filiaal creation to the database
     *
     * @param filiaal to be saved in db-table filialen
     * @Valid checks validity of a new Filiaal before saving it to databse
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    // spring generates response status code 201

    // for EntitiyLinks Objects
    // HttpHeaders builts response headers
    HttpHeaders create(@RequestBody @Valid Filiaal filiaal) {
        //    void post(@RequestBody @Valid Filiaal filiaal) {

        filiaalService.create(filiaal);

        // create a link object referencing the URI of the created Filiaal
        var link = entityLinks.linkToItemResource(Filiaal.class, filiaal.getId());
        var headers = new HttpHeaders();
        // set the URI to the created Filiaal in the Location header
        headers.setLocation(link.toUri());
        return headers;
    }


    /**
     * method handling invalid attributes of Filiaal objects from @PostMapping post method
     *
     * @param ex exception thrown when using method post with invalid Filiaal attribute(s)
     * @return a Map holding wrong attributes as key(s) and corresponding descriptions as value(s)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> verkeerdeData(MethodArgumentNotValidException ex) {
        return ex
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(
                        Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage)
                );
    }

    /**
     * method to post a Filiaal update to the database
     *
     * @param filiaal to be saved in db-table filialen
     * @Valid checks validity of an updated Filiaal before saving it to database
     */
    @PutMapping("{id}")
    void put(@RequestBody @Valid Filiaal filiaal) {
        filiaalService.update(filiaal);
    }


    // nested class collect the main attributes of the the Filiaal objects >> for response body holding a collection of entities
    private static class FiliaalIdNaam {
        private final long id;
        private final String naam;

        public FiliaalIdNaam(Filiaal filiaal) {
            id = filiaal.getId();
            naam = filiaal.getNaam();
        }

        public long getId() {
            return id;
        }

        public String getNaam() {
            return naam;
        }
    }
}

