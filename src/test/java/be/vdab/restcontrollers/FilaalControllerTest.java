package be.vdab.restcontrollers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.web.servlet.MockMvc;

// !!
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


// INTEGRATION TEST >>                                               << ( NO UNIT TEST )
// inject MOCKMVC OBJECT                                                ( NO domain objects needed )
// && use  perform( GET...)   .andExpect( status() | jsonPath()  )      ( NO assertThat( obj.meth() ).is...(testObj) )


@SpringBootTest         // auto creation of all beans (controllers, services, repos,...)
@AutoConfigureMockMvc   // sends http requests from within the test
@Sql("/insertFiliaal.sql")
public class FilaalControllerTest extends AbstractTransactionalJUnit4SpringContextTests {

    private final MockMvc mockMvc;

    public FilaalControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    private long idVanTestFiliaal() {
        return super.jdbcTemplate.queryForObject(
                "select id from filialen where naam = 'test'",
                Long.class
        );
    }

    @Test
    void nonExistingFiliaalThrowsException() throws Exception {
        mockMvc
                .perform(
                        get("/filialen/{id}", -1)
                ).andExpect(
                        status().isNotFound()
        );
    }

    @Test
    void filiaalLezen() throws Exception {
        var id = idVanTestFiliaal();
        mockMvc.perform(
                get("/filialen/{id}", id)
        ).andExpect(
                status().isOk()
        ).andExpect(
                jsonPath("id").value(id)
        );
    }

}
