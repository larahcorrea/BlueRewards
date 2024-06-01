package br.com.fiap.bluerewards.clients;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import br.com.fiap.bluerewards.dto.CupomDto;

@Component
public class CupomClient {

    @Autowired
    RestTemplate restTemplate;

    String URL_CUPONS = "http://localhost:5290/api/Cupom";

    public ResponseEntity<List<CupomDto>> getCupons(){
        ParameterizedTypeReference<List<CupomDto>> responseType = new ParameterizedTypeReference<List<CupomDto>>() {};
        return restTemplate.exchange(URL_CUPONS, HttpMethod.GET, null, responseType);

    }
    
}
