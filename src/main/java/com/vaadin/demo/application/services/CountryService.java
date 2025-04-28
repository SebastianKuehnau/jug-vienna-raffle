package com.vaadin.demo.application.services;

import com.vaadin.demo.application.domain.model.CountryRecord;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.client.HttpSyncGraphQlClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class CountryService {

    private final HttpSyncGraphQlClient httpSyncGraphQlClient;
    private RestClient restClient;

    public CountryService() {
        restClient = RestClient.create("https://countries.trevorblades.com/");
        httpSyncGraphQlClient = HttpSyncGraphQlClient.create(restClient);
    }

    public List<CountryRecord> fetchCountryList() {
        var document = """
                    {
                        countries{
                            name,
                            capital, 
                            code
                          }
                }
                """;

        List<CountryRecord> countries =
                httpSyncGraphQlClient.document(document)
                .retrieveSync("countries")
                .toEntity(new ParameterizedTypeReference<>() {});

        return countries ;
    }

    public List<CountryRecord> searchCountriesByPrefix(String prefix) {
        String queryDocument = """
                query($prefix: String!) {
                    countries(filter: {name: {regex: $prefix}}) {
                        name
                        capital,
                        code
                    }
                }
                """;

         return Objects.requireNonNull(httpSyncGraphQlClient.document(queryDocument)
                 .variables(Map.of("prefix", "^" + prefix))
                 .retrieve("countries")
                 .toEntityList(CountryRecord.class).block());


    }
}
