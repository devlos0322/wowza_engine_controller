# Wowza stream engine controller

---------------------------------------

Wowza stream engine에서 제공하는 REST API를 이용하여 incoming stream을 관리하는 서비스입니다. \
서비스의 자원은 incoming stream입니다. \
Wowza stream engine incoming stream을 Create, Delete 하는 기능을 지원합니다.


## Wowza stream engine controller work flow

---------------------------------------

### 1. Entity
Wowza 기반 동영상 스트리밍 시스템은 Figure 1과 같이 4가지의 Entity간 통신으로 이루어집니다.
* Client: 사용자가 스트리밍되는 동영상을 확인
* Engine controller: 본 프로젝트의 구현체로써 Wowza engine의 Streaming 기능을 제어
* Wowza engine: Wowza Media Systems에서 개발 한 통합 스트리밍 미디어 서버 소프트웨어 (https://www.wowza.com/)
* VMS(Video Management system): 카메라 혹은 기타 장비로 부터 비디오를 수집하는 비디오 관리 시스템

<p align="center"><img src="https://raw.githubusercontent.com/devlos0322/wowza_engine_controller/master/images/wowza_engine_controller_workflow.PNG"  width="60%"/></p>
<center>Figure 1 동영상 스트리밍 기능의 동작 방식</center>


### 2. Wowza engine workflow

Wowza engine에서 동영상을 스트리밍 하려면 다음의 절차가 필요합니다. \
(본 구현체에서 제공하는 API는 2.3 Incoming stream에 대한 기능만 지원합니다. 2.1 Application 생성과, 2.2. Stream file 등록이
Wowza engine에 완료 되었다는 가정하에 사용이 가능합니다.)
#### 2.1. Application 생성
Application은 동영상 스트리밍을 받기 위한 채널의 개념입니다. 미디어 서버의 여러 고객을 대상으로 분리된 체널을 만들어줍니다. 스트리밍을 받기 위해서는 Application 생성이 우선되어야 합니다.

#### 2.2. Stream file 생성
Stream file 은 MPEG-TS 인코더 또는 IP 카메라와 같은 수집장치로부터 전달되는 영상 스트림을 식별하기 위한 파일입니다. \
 Stream file은 http://~/cctv2401.stream/playlist.m3u8, rtmp:/~/ch90.stream, rtsp://~/media.smp 등 다양한 URI를 포함할 수 있습니다.

#### 2.3. Incoming stream 생성 
Incoming stream은 Application과 Stream file을 연결시켜주는 역할을 합니다. 쉽게 말해서 영상 리소스와 영상 체널을 연결해 주는 것입니다. 



## API 설명

---------------------------------------

### 1. Overview
구현체의 API를 사용하기 위해서 CURL 혹은 Postman을 사용할때는 다음 옵션을 사용하셔야 합니다.

|공통 속성|속성값|설명|
|:------:|---|--------|
| URI           | http://localhost:8080/api/incoming_stream   | Engine controller를 실행하는 로컬 환경 |
| PORT          | 8080        |                                           |
| Header Option |Content-Type: application/json |                         |

### 2. Incoming stream 추가
Incoming stream을 추가하기 위해서 다음의 파라메터를 사용합니다.
* applicationName: 미리 생성된 애플리케이션 이름 
* streamFileName: 미리 생성된 스트림 파일 이름 
* medeaCasterType: 스트림파일과 연결된 수집장치의 미디어 캐스터 타입  

  (rtp. rtp-record, shoutcast, shoutcast-record, liverepeater, applehls, mpegtstcp, srt)

  |Media caster type|설명|
  |:------:|---|
  | rtp               | RTSP/RTP stream을 지원하는 IP camera stream 혹은 native RTP, MPEG-TS encoder 용 (ex. ~.stream) |
  | rtp-record        | RTSP/RTP stream 녹화용 |
  | shoutcast         | SHOUTcast/Icecast streams용 |
  | shoutcast-record  | SHOUTcast/Icecast streams 녹화용 |
  | applehls          | HLS(HTTP Live Streaming) stream용 (ex. ~.m3u8) |
  | mpegtstcp         | TCP/IP 상의 MPEG-TS encoder 기반 stream용  |

더욱 자세한 내용은 아래의 링크를 참고하세요. \
https://www.wowza.com/docs/wowza-streaming-engine-product-articles

#### Request
```shell
curl --location --request POST 'http://localhost:8080/api/incoming_stream' \
--header 'Content-Type: application/json' \
--data-raw '{
"applicationName": "rnd_test",
"streamFileName": "rnd_test_stream_file",
"mediaCasterType": "applehls"
}'
```

#### Response
```shell
HTTP/1.1 200
Content-Type: application/json
Transfer-Encoding: chunked
Date: Tue, 24 Aug 2021 09:13:59 GMT

{"success":true,"message":"Stream service가 정상적으로 실행 되었습니다."}
```

### 3. Incoming stream 삭제
#### Request
Incoming stream을 삭제하기 위해서 다음의 파라메터를 사용합니다.
* applicationName: 미리 생성된 애플리케이션 이름
* streamFileName: 미리 생성된 스트림 파일 이름

```shell
curl -i --location --request DELETE 'http://localhost:8080/api/incoming_stream' \
--header 'Content-Type: application/json' \
--data-raw '{
"applicationName": "rnd_test",
"streamFileName": "rnd_test_stream_file"
}'
```

#### Response
```shell
HTTP/1.1 200
Content-Type: application/json
Transfer-Encoding: chunked
Date: Tue, 24 Aug 2021 09:21:24 GMT

{"success":true,"message":"Stream service가 정상적으로 중지 되었습니다."}
```
### 4. 주의 사항
구현체의 wowza_engine_server 접속정보는 아래의 경로에 있습니다. 만약 접속정보 파일이 존재하지 않는다면 담당자에게 문의하세요.
```
src/main/resources/application.yml
```