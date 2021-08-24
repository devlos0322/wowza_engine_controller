package com.winitech.wowza_engine_controller.service;

import com.winitech.wowza_engine_controller.dto.IncomingStreamResponse;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class RestService {
    @Value("${wowza_engine_server.uri}")
    private String REQUEST_URL;

    @Value("${wowza_engine_server.port}")
    private String REQUEST_PORT;

    @Value("${wowza_engine_server.id}")
    private String REQUEST_ID;

    @Value("${wowza_engine_server.password}")
    private String REQUEST_PASSWORD;

    public ResponseEntity createIncomingStream(String applicationName, String streamFileName, String mediaCasterType) {

        //HTTP request Header 정보 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(REQUEST_ID,REQUEST_PASSWORD);
        //URI & query 설정
        URI uri = UriComponentsBuilder
                .fromUriString(REQUEST_URL + ":" + REQUEST_PORT)
                .path("/v2/servers/_defaultServer_/vhosts/_defaultVHost_/applications/"+applicationName+"/streamfiles/"+streamFileName+"/actions/connect")
                .queryParam("connectAppName", applicationName)
                .queryParam("appInstance", "_definst_")
                .queryParam("mediaCasterType", mediaCasterType)
                .encode()
                .build()
                .toUri();
        //요청 -> XML 응답 -> String -> JSON 변환
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<HttpHeaders> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.PUT, entity, String.class);
        JSONObject jsonObject= new JSONObject(response.getBody());
            IncomingStreamResponse incomingStreamResponse = new IncomingStreamResponse();
            if (response.getStatusCode() == HttpStatus.OK) {
                if((boolean)jsonObject.get("success")) {
                    incomingStreamResponse.setSuccess(true);
                    incomingStreamResponse.setMessage("Stream service가 정상적으로 실행 되었습니다.");
                } else {
                    incomingStreamResponse.setSuccess(false);
                    incomingStreamResponse.setMessage("Stream service가 이미 실행중이거나, 미디어 타입이 잘못되었습니다.");
                }
                return ResponseEntity.status(HttpStatus.OK).body(incomingStreamResponse);
            } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                incomingStreamResponse.setSuccess(false);
                incomingStreamResponse.setMessage("Wowza engine에 Application 혹은 streamfile이 등록되어 있지 않습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(incomingStreamResponse);
            } else if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                incomingStreamResponse.setSuccess(false);
                incomingStreamResponse.setMessage("Wowza engine에서 알 수 없는 오류가 발생했습니다.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(incomingStreamResponse);
            }
        incomingStreamResponse.setSuccess(false);
        incomingStreamResponse.setMessage("Engine controller에서 알 수 없는 오류가 발생했습니다.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(incomingStreamResponse);
    }

    public ResponseEntity deleteIncomingStream(String applicationName, String streamFileName) {

        //HTTP request Header 정보 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(REQUEST_ID,REQUEST_PASSWORD);
        //URI & query 설정
        URI uri = UriComponentsBuilder
                .fromUriString(REQUEST_URL + ":" + REQUEST_PORT)
                .path("/v2/servers/_defaultServer_/vhosts/_defaultVHost_/applications/"+applicationName+"/instances/_definst_/incomingstreams/"+streamFileName+".stream/actions/disconnectStream")
                .encode()
                .build()
                .toUri();
        //요청 -> XML 응답 -> String -> JSON 변환
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<HttpHeaders> entity = new HttpEntity<>(headers);
        IncomingStreamResponse incomingStreamResponse = new IncomingStreamResponse();
        try {
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.PUT, entity, String.class);
            System.out.println(response.toString());
            JSONObject jsonObject= new JSONObject(response.getBody());
            if (response.getStatusCode() == HttpStatus.OK) {
                incomingStreamResponse.setSuccess(true);
                incomingStreamResponse.setMessage("Stream service가 정상적으로 중지 되었습니다.");
                return ResponseEntity.status(HttpStatus.OK).body(incomingStreamResponse);
            }
        } catch (HttpStatusCodeException exception){
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                incomingStreamResponse.setSuccess(false);
                incomingStreamResponse.setMessage("Wowza engine에 Application 혹은 streamfile이 등록되어 있지 않습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(incomingStreamResponse);
            } else if (exception.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                incomingStreamResponse.setSuccess(false);
                incomingStreamResponse.setMessage("Wowza engine에서 알 수 없는 오류가 발생했습니다.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(incomingStreamResponse);
            }

        }
        incomingStreamResponse.setSuccess(false);
        incomingStreamResponse.setMessage("Engine controller에서 알 수 없는 오류가 발생했습니다.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(incomingStreamResponse);
    }
}