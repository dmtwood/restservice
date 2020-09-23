package be.vdab.restservice.controllers;

import be.vdab.restservice.domain.Filiaal;
import be.vdab.restservice.exceptions.FiliaalNietGevondenException;
import be.vdab.restservice.services.FiliaalService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@RestController // requesting clients need response in (xml or) json, no html (xml >> @XmlRootElement   @XmlAccessorType(XmlAccessType.FIELD) EntityClass (Filiaal)
@RequestMapping("/filialen")
public class FiliaalController {
    private final FiliaalService filiaalService;

    public FiliaalController(FiliaalService filiaalService) {
        this.filiaalService = filiaalService;
    }


    /** method searching Filiaal with given id
     * @param id primary key in failialen table
     * @return Filiaal with id or Filiaal Niet Gevonden Exception
     */
    @GetMapping("{id}")
    public Filiaal get(
            @PathVariable long id
    ) {  return
            filiaalService
                    .findById(id)
//                    .get()
            .orElseThrow( FiliaalNietGevondenException::new ) ; }


            // if get() throws exception, handle it here
    @ExceptionHandler(FiliaalNietGevondenException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    void filiaalNietGevonden(){
    }


    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        filiaalService.delete(id);
    }


    @PostMapping
    void post(@RequestBody Filiaal filiaal) {
        filiaalService.create(filiaal);
    }
}
