# Wowza stream engine controller

---
Wowza stream engine에서 제공하는 REST API를 이용하여 incoming stream을 관리하는 서비스입니다. \
서비스의 자원은 stream file과 incoming stream입니다. \
Wowza stream engine의 stream file과 incoming stream연결을 쉽게 할 수 있도록 REST API를 지원합니다.

# Wowza stream engine controller work flow

---
## 1. Entity
Wowza 기반 동영상 스트리밍 시스템은 Figure 1과 같이 4가지의 Entity간 통신으로 이루어집니다.
* Client: 사용자가 스트리밍되는 동영상을 확인
* Engine controller: 본 프로젝트의 구현체로써 Wowza engine의 Streaming 기능을 제어
* Wowza engine: Wowza Media Systems에서 개발 한 통합 스트리밍 미디어 서버 소프트웨어 (https://www.wowza.com/)
* VMS(Video Management system): 카메라 혹은 기타 장비에서 비디오를 수집하는 비디오 관리 시스템

<p align="center"><img src="https://raw.githubusercontent.com/devlos0322/wowza_engine_controller/master/images/wowza_engine_controller_workflow.PNG"  width="60%"/></p>
<p align="center">Figure 1 동영상 스트리밍 기능의 동작 방식</p>


## 2. Wowza engine workflow

Wowza engine에서 동영상을 스트리밍 하려면 다음의 절차가 필요합니다. \
(본 구현체에서 제공하는 API는 2.2.Streaming file, 2.3 Incoming stream에 대한 기능만 지원합니다. 2.1 Application 생성이 완료 되었다는 가정하에 사용이 가용합니다.)
### 2.1. Application 생성
Application은 동영상 스트리밍을 받기 위한 채널의 개념입니다. 미디어 서버의 여러 고객을 대상으로 분리된 체널을 만들어줍니다. 스트리밍을 받기 위해서는 Application 생성이 우선되어야 합니다.

### 2.2. Stream file 생성
Stream file 은 MPEG-TS 인코더 또는 IP 카메라와 같은 수집장치로부터 전달되는 영상 스트림을 식별하기 위한 파일입니다. \
 Stream file은 http://exmaple.cm/cctv2401.stream/playlist.m3u8, rtmp:/example.cm/ch90.stream, rtsp://~/media.smp 등 다양한 URI를 포함할 수 있습니다.

### 2.3. Incoming stream 연결 
Incoming stream은 Application과 Stream file을 연결시켜주는 역할을 합니다. 쉽게 말해서 영상 리소스와 영상 체널을 연결해 주는 것입니다. 

## 3. Engine controller의 역할
Engine controller는 Client와 Wowza engine의 중간에서 stream file과 incoming stream을 관리하기 쉽도록 REST API를 제공합니다.

# API 설명

---

## 1. Overview
구현체의 REST API를 사용하기 위해서 CURL 혹은 Postman을 사용할때는 다음 옵션을 사용하셔야 합니다.

  <p align="center">Table 1 Engine controller REST API 기본 정보</p>

|공통 속성|속성값|설명|
|:------:|---|--------|
| URI           | http://localhost:8080/api/    |                         |
| PORT          | 8080        |                                           |
| Header Option |Content-Type: application/json |                         |
| Authorization |-                              | Engine contoller <-> Wowza engine (Basic auth)  |

## 2. Engine controller REST API
### 2.1. Stream file management
#### 2.1.1. stream file 생성
Stream file을 생성하기 위해서 아래와 같이 호출합니다.

POST /applications/{applicationName}/stream_files

BODY
```
{
    "streamFileName": {streamFileName},
    "resourceUri": {resouceUri}
}
```
* applicationName: 미리 생성된 애플리케이션 이름
* streamFileName: 미리 생성된 스트림 파일 이름
* resourceUri: 스트리밍 자원 URI

Example

* Request
```
curl -i --location --request POST 'http://localhost:8080/api/applications/rnd_test/stream_files' \
--header 'Content-Type: application/json' \
--data-raw '{
    "streamFileName": "rnd_test_stream_file",
    "resourceUri": "http://example.com/live1/_definst_/ch8.stream/playlist.m3u8"
}'
```
* Response
```
HTTP/1.1 200
Content-Type: application/json
Transfer-Encoding: chunked
Date: Thu, 26 Aug 2021 01:36:29 GMT

{
  "success": true,
  "message": "rnd_test_stream_file(stream file)이 정상적으로 등록 되었습니다."
}
```

#### 2.1.2. stream file 전체 조회
Wowza engine에 등록된 전체 stream file하기 위해서 아래와 같이 호출합니다.

GET /applications/{applicationName}/stream_files

BODY
```

```
* applicationName: 미리 생성된 애플리케이션 이름

Example

* Request
```
curl -i --location --request GET 'http://localhost:8080/api/applications/rnd_test/stream_files'
```
* Response
```
HTTP/1.1 200
Content-Type: application/json
Transfer-Encoding: chunked
Date: Thu, 26 Aug 2021 01:41:11 GMT

{
  "success": true,
  "message": "조회가 정상적으로 실행되었습니다.",
  "streamFileList": [
    {
      "streamFileName": "mola2",
      "href": "/api/applications/rnd_test/stream_files/mola2"
    },
    ...,
    {
      "streamFileName": "rnd_test_stream_file",
      "href": "/api/applications/rnd_test/stream_files/rnd_test_stream_file"
    }
  ]
}
```

#### 2.1.3. Stream file명을 이용한  개별 조회
Wowza engine에 등록된 전체 stream file하기 위해서 아래와 같이 호출합니다.

GET /applications/{applicationName}/stream_files/{steam file name}

BODY
```

```
* applicationName: 미리 생성된 애플리케이션 이름
* streamFileName: 미리 생성된 스트림 파일 이름

Example

* Request
```
curl -i --location --request GET 'http://localhost:8080/api/applications/rnd_test/stream_files/rnd_test_stream_file'
```
* Response
```
HTTP/1.1 200
Content-Type: application/json
Transfer-Encoding: chunked
Date: Thu, 26 Aug 2021 01:46:14 GMT

{
  "success": true,
  "message": "조회가 정상적으로 실행되었습니다.",
  "streamFileName": "rnd_test_stream_file",
  "resourceUri": "http://210.91.152.35:1935/live1/_definst_/ch8.stream/playlist.m3u8"
}
```

#### 2.1.4. Stream file 삭제
Wowza engine에 등록된stream file을 삭제하기 위해서 아래와 같이 호출합니다.

DELETE /applications/{applicationName}/stream_files/{steam file name}

BODY
```
{
    "applicationName": {applicationName},
    "streamFileName": {streamFileName}
}
```
* applicationName: 미리 생성된 애플리케이션 이름
* streamFileName: 미리 생성된 스트림 파일 이름

Example

* Request
```
curl -i --location --request DELETE 'http://localhost:8080/api/applications/rnd_test/stream_files/rnd_test_stream_file' \
--header 'Content-Type: application/json' \
--data-raw '{
    "applicationName": "rnd_test",
    "streamFileName": "rnd_test_stream_file"
}'
```
* Response
```
HTTP/1.1 200
Content-Type: application/json
Transfer-Encoding: chunked
Date: Thu, 26 Aug 2021 01:48:58 GMT

{
  "success": true,
  "message": "rnd_test_stream_file (Stream file)이 정상적으로 삭제 되었습니다."
}
```


### 2.2. Incoming stream management
#### 2.2.1. Incoming stream 연결
Wowza engine에 Incoming stream을 연결하기 위해서 아래와 같이 호출합니다.

PUT /applications/{applicationName}/stream_files/{streamFileName}/actions/connect

BODY
```
{
    "mediaCasterType": {mediaCasterType}
}
```
* applicationName: 미리 생성된 애플리케이션 이름
* streamFileName: 미리 생성된 스트림 파일 이름
* medeaCasterType: 스트림파일과 연결된 수집장치의 미디어 캐스터 타입 (Table 2 참고)


  <p align="center">Table 2 Incoming stream에서 지원하는 미디어 캐스터 타입</p>

  |Media caster type|설명|
  |:------:|---|
  | rtp               | RTSP/RTP stream을 지원하는 IP camera stream 혹은 native RTP, MPEG-TS encoder 용 (ex. ~.stream) |
  | rtp-record        | RTSP/RTP stream 녹화용 |
  | shoutcast         | SHOUTcast/Icecast stream 용 |
  | shoutcast-record  | SHOUTcast/Icecast stream 녹화용 |
  | liverepeater      | 다른 Wowza 스트리밍 엔진 서버에서 가져온 RTMP stream용 |
  | applehls          | HLS(HTTP Live Streaming) stream용 (ex. ~.m3u8) |
  | mpegtstcp         | TCP/IP 상의 MPEG-TS encoder 기반 stream용  |
  | srt               | SRT stream용  |

  더욱 자세한 내용은 아래의 링크를 참고하세요. \
  https://www.wowza.com/docs/wowza-streaming-engine-product-articles

Example
* Request
```
curl -i --location --request PUT 'http://localhost:8080/api/applications/rnd_test/stream_files/rnd_test_stream_file/actions/connect' \
--header 'Content-Type: application/json' \
--data-raw '{
    "mediaCasterType": "applehls"
}'
```
* Response
```
HTTP/1.1 200
Content-Type: application/json
Transfer-Encoding: chunked
Date: Thu, 26 Aug 2021 02:03:00 GMT

{
  "success": true,
  "message": "Incoming stream이 정상적으로 연결 되었습니다."
}
```

#### 2.2.1. Incoming stream 연결 해제
Wowza engine에 연결된 incoming stream을 연결 해제하기 위해서 아래와 같이 호출합니다.

PUT /applications/{applicationName}/stream_files/{streamFileName}/actions/disconnect

BODY
```

```
* applicationName: 미리 생성된 애플리케이션 이름
* streamFileName: 미리 생성된 스트림 파일 이름

Example
* Request
```
curl -i --location --request PUT 'http://localhost:8080/api/applications/rnd_test/stream_files/rnd_test_stream_file/actions/disconnect'
```
* Response
```
HTTP/1.1 200
Content-Type: application/json
Transfer-Encoding: chunked
Date: Thu, 26 Aug 2021 02:09:14 GMT

{
  "success": true,
  "message": "Incoming stream이 정상적으로 연결 해제 되었습니다."
}
```


-----

### 4. 주의 사항
구현체의 wowza_engine_server 접속정보는 아래의 경로에 있습니다. 만약 접속정보 파일이 존재하지 않는다면 담당자에게 문의하세요.
```
src/main/resources/application.yml
```