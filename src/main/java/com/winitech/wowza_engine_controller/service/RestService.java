package com.winitech.wowza_engine_controller.service;

import com.winitech.wowza_engine_controller.dto.IncomingStreamResponse;
import com.winitech.wowza_engine_controller.dto.StreamFileGetAllResponse;
import com.winitech.wowza_engine_controller.dto.StreamFileGetResponse;
import com.winitech.wowza_engine_controller.dto.StreamFileRsponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;

/**
 * REST service class
 *
 * @date 2021.08.24
 * @author Junhee Park
 */
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

    /**
     * Stream file 생성 메서드
     *
     * @param applicationName : 애플리케이션 명
     * @param streamFileName : 스트림 파일 명
     */
    public ResponseEntity createStreamFile(String applicationName, String streamFileName, String resourceUri) {

        //HTTP request Header 정보 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(REQUEST_ID,REQUEST_PASSWORD);
        URI uri = null;

        // Application 존재 확인
        uri = UriComponentsBuilder
                .fromUriString(REQUEST_URL + ":" + REQUEST_PORT)
                .path("/v2/servers/_defaultServer_/vhosts/_defaultVHost_/applications/"+applicationName)
                .encode()
                .build()
                .toUri();

        //요청 -> XML 응답 -> String -> JSON 변환
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<HttpHeaders> entity = new HttpEntity<>(headers);
        StreamFileRsponse streamFileRsponse = new StreamFileRsponse();

        try {
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
        } catch (HttpStatusCodeException exception){
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                streamFileRsponse.setSuccess(false);
                streamFileRsponse.setMessage("Wowza engine에" + applicationName + "(application)이 등록되어 있지 않습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(streamFileRsponse);
            } else if (exception.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                streamFileRsponse.setSuccess(false);
                streamFileRsponse.setMessage("Wowza engine에서 알 수 없는 오류가 발생했습니다.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(streamFileRsponse);
            }

        }

        // Stream file 생성
        // URI & query 설정
        uri = UriComponentsBuilder
                .fromUriString(REQUEST_URL + ":" + REQUEST_PORT)
                .path("/v2/servers/_defaultServer_/vhosts/_defaultVHost_/applications/"+applicationName+"/streamfiles")
                .encode()
                .build()
                .toUri();

        HashMap<String, String> bodyParam = new HashMap<>();
        bodyParam.put("name", streamFileName);
        bodyParam.put("serverName","_defaultServer_");
        bodyParam.put("uri", resourceUri);
        entity = new HttpEntity(bodyParam, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
            streamFileRsponse.setSuccess(true);
            streamFileRsponse.setMessage(streamFileName + "(stream file)이 정상적으로 등록 되었습니다.");
            return ResponseEntity.status(HttpStatus.OK).body(streamFileRsponse);

        } catch (HttpStatusCodeException exception){
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                streamFileRsponse.setSuccess(false);
                streamFileRsponse.setMessage("Wowza engine에 " + applicationName + "(application)이 등록되어 있지 않습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(streamFileRsponse);
            } else if (exception.getStatusCode() == HttpStatus.CONFLICT) {
                streamFileRsponse.setSuccess(false);
                streamFileRsponse.setMessage("Wowza engine에 " + streamFileName + "(stream file) 이 이미 등록되어 있습니다.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(streamFileRsponse);
            } else if (exception.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                streamFileRsponse.setSuccess(false);
                streamFileRsponse.setMessage("Wowza engine에서 알 수 없는 오류가 발생했습니다.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(streamFileRsponse);
            }

        }
        streamFileRsponse.setSuccess(false);
        streamFileRsponse.setMessage("Engine controller에서 알 수 없는 오류가 발생했습니다.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(streamFileRsponse);
    }
    
    /**
     * Stream file 전체 조회 메서드
     *
     * @param applicationName : 애플리케이션 명
     */
    public ResponseEntity getAllStreamFiles(String applicationName) {

        //HTTP request Header 정보 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(REQUEST_ID,REQUEST_PASSWORD);
        //URI & query 설정
        URI uri = UriComponentsBuilder
                .fromUriString(REQUEST_URL + ":" + REQUEST_PORT)
                .path("/v2/servers/_defaultServer_/vhosts/_defaultVHost_/applications/"+applicationName+"/streamfiles")
                .encode()
                .build()
                .toUri();
        //요청 -> XML 응답 -> String -> JSON 변환
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<HttpHeaders> entity = new HttpEntity<>(headers);
        StreamFileGetAllResponse streamFileGetAllResponse = new StreamFileGetAllResponse();
        try {
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
            JSONObject rcvdStreamFiles = new JSONObject(response.getBody());
            if (rcvdStreamFiles.getJSONArray("streamFiles").length() > 1) {
                JSONArray rspStreamFileList = new JSONArray();
                JSONObject rspStreamFileTuple = null;
                for (int i =0; i < rcvdStreamFiles.getJSONArray("streamFiles").length(); i ++) {
                    rspStreamFileTuple = new JSONObject();
                    String stream_file_name = (String) rcvdStreamFiles.getJSONArray("streamFiles").getJSONObject(i).get("id");
                    rspStreamFileTuple.put("streamFileName",stream_file_name);
                    rspStreamFileTuple.put("href","/api/applications/rnd_test/stream_files/"+stream_file_name);
                    rspStreamFileList.put(rspStreamFileTuple);
                }
                streamFileGetAllResponse.setSuccess(true);
                streamFileGetAllResponse.setMessage("조회가 정상적으로 실행되었습니다.");
                streamFileGetAllResponse.setStreamFileList(rspStreamFileList.toList());
                return ResponseEntity.status(HttpStatus.OK).body(streamFileGetAllResponse);
            } else {
                streamFileGetAllResponse.setSuccess(false);
                streamFileGetAllResponse.setMessage("Wowza engine에서 해당 Apllication에 등록된 streamfile을 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(streamFileGetAllResponse);
            }
        } catch (HttpStatusCodeException exception){
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                streamFileGetAllResponse.setSuccess(false);
                streamFileGetAllResponse.setMessage("Wowza engine에 Application이 등록되어 있지 않습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(streamFileGetAllResponse);
            } else if (exception.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                streamFileGetAllResponse.setSuccess(false);
                streamFileGetAllResponse.setMessage("Wowza engine에서 알 수 없는 오류가 발생했습니다.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(streamFileGetAllResponse);
            }

        }
        streamFileGetAllResponse.setSuccess(false);
        streamFileGetAllResponse.setMessage("Engine controller에서 알 수 없는 오류가 발생했습니다.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(streamFileGetAllResponse);
    }

    /**
     * Stream file 조회 메서드
     *
     * @param applicationName : 애플리케이션 명
     */
    public ResponseEntity getStreamFile(String applicationName, String streamFileName) {

        //HTTP request Header 정보 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(REQUEST_ID,REQUEST_PASSWORD);
        //URI & query 설정
        URI uri = UriComponentsBuilder
                .fromUriString(REQUEST_URL + ":" + REQUEST_PORT)
                .path("/v2/servers/_defaultServer_/vhosts/_defaultVHost_/applications/"+applicationName+"/streamfiles/"+streamFileName)
                .encode()
                .build()
                .toUri();
        //요청 -> XML 응답 -> String -> JSON 변환
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<HttpHeaders> entity = new HttpEntity<>(headers);
        StreamFileGetResponse streamFileGetResponse = new StreamFileGetResponse();
        try {
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
            JSONObject rcvdStreamFile= new JSONObject(response.getBody());
            streamFileGetResponse.setSuccess(true);
            streamFileGetResponse.setMessage("조회가 정상적으로 실행되었습니다.");
            streamFileGetResponse.setStreamFileName((String)rcvdStreamFile.get("name"));
            streamFileGetResponse.setResourceUri((String)rcvdStreamFile.get("uri"));
            return ResponseEntity.status(HttpStatus.OK).body(streamFileGetResponse);

        } catch (HttpStatusCodeException exception){
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                streamFileGetResponse.setSuccess(false);
                streamFileGetResponse.setMessage("Wowza engine에 Application 혹은 stream file이 등록되어 있지 않습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(streamFileGetResponse);
            } else if (exception.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                streamFileGetResponse.setSuccess(false);
                streamFileGetResponse.setMessage("Wowza engine에서 알 수 없는 오류가 발생했습니다.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(streamFileGetResponse);
            }

        }
        streamFileGetResponse.setSuccess(false);
        streamFileGetResponse.setMessage("Engine controller에서 알 수 없는 오류가 발생했습니다.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(streamFileGetResponse);
    }

    /**
     * Stream file 삭제 메서드
     *
     * @param applicationName : 애플리케이션 명
     * @param streamFileName : 스트림 파일 명
     */
    public ResponseEntity deleteStreamFile(String applicationName, String streamFileName) {

        //HTTP request Header 정보 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(REQUEST_ID,REQUEST_PASSWORD);
        //URI & query 설정
        URI uri = UriComponentsBuilder
                .fromUriString(REQUEST_URL + ":" + REQUEST_PORT)
                .path("/v2/servers/_defaultServer_/vhosts/_defaultVHost_/applications/"+applicationName+"/streamfiles/"+streamFileName)
                .encode()
                .build()
                .toUri();
        //요청 -> XML 응답 -> String -> JSON 변환
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<HttpHeaders> entity = new HttpEntity<>(headers);
        IncomingStreamResponse incomingStreamResponse = new IncomingStreamResponse();
        try {
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.DELETE, entity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                incomingStreamResponse.setSuccess(true);
                incomingStreamResponse.setMessage(streamFileName + " (Stream file)이 정상적으로 삭제 되었습니다.");
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

    /**
     * Incoming stream 생성 메서드
     *
     * @param applicationName : 애플리케이션 명
     * @param streamFileName : 스트림 파일 명
     * @param mediaCasterType : 미디어 캐스터 타입 명 (스트림 파일의 포맷에 맞게 작성해야함. ex) applehls)
     */
    public ResponseEntity createIncomingStream(String applicationName, String streamFileName, String mediaCasterType) {

        // HTTP request Header 정보 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(REQUEST_ID,REQUEST_PASSWORD);
        // URI & query 설정
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

        IncomingStreamResponse incomingStreamResponse = new IncomingStreamResponse();
        try {
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.PUT, entity, String.class);
            JSONObject jsonObject= new JSONObject(response.getBody());
            if (response.getStatusCode() == HttpStatus.OK) {
                if((boolean)jsonObject.get("success")) {
                    incomingStreamResponse.setSuccess(true);
                    incomingStreamResponse.setMessage("Incoming stream이 정상적으로 연결 되었습니다.");
                } else {
                    incomingStreamResponse.setSuccess(false);
                    incomingStreamResponse.setMessage("Incoming stream이 이미 연결 되어 있거나, 미디어 캐스트 타입이 잘못되었습니다.");
                }
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
    /**
     * Incoming stream 삭제 메서드
     *
     * @param applicationName : 애플리케이션 명
     * @param streamFileName : 스트림 파일 명
     */
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
            if (response.getStatusCode() == HttpStatus.OK) {
                incomingStreamResponse.setSuccess(true);
                incomingStreamResponse.setMessage("Incoming stream이 정상적으로 연결 해제 되었습니다.");
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