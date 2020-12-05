# Simple Chat System

## Requirements

1. ```Kafka``` running on ```localhost:9092``` 
2. ```Java 11.0.9```
3. ```IntelliJ```

---

## Kafka 실행 방법

1. 압축파일 설치
    ```
    curl –O https://downloads.apache.org/kafka/2.6.0/kafka_2.13-2.6.0.tgz
    ```

2. 압축 해제	
    ```
    tar -xzf kafka_2.13-2.6.0.tgz
   ```
   
3. 해제된 디렉토리로 이동
    ```
    cd kafka_2.13-2.6.0
   ```

4. 프로세스 실행
    ```
    // Zookeeper 실행
    bin/zookeeper-server-start.sh config/zookeeper.properties

    // Kafka 실행
    bin/kafka-server-start.sh config/server.properties
    ```
   
---

## Cacaotalk 실행 방법

> JAVA version 11.0.9

1. IntelliJ 실행

2. 프로젝트 열기

    ```File > Open > "cacaotalk_yckim"```
    
3. 프로젝트 실행

    ```Run > Run "main"```
    
4. 필요할 경우, 모듈 경로 설정 후 실행

    ```use classpath of module > cacaotalk_yckim 선택```
    
    ```RUN ```
    
5. IntelliJ Terminal 에서 이용




> 혹은 별도로 java dependency 설치 후 실행
>
> dependency 설치 경로 확인 필
>
>    ```
>    java -classpath {프로젝트 디렉토리 경로}/target/classes:/{Dependency 설치 경로}/.m2/repository/org/apache/kafka/kafka-clients/2.6.0/kafka-clients-2.6.0.jar:/{Dependency 설치 경로}/.m2/repository/com/github/luben/zstd-jni/1.4.4-7/zstd-jni-1.4.4-7.jar:/{Dependency 설치 경로}/.m2/repository/org/lz4/lz4-java/1.7.1/lz4-java-1.7.1.jar:/{Dependency 설치 경로}/.m2/repository/org/xerial/snappy/snappy-java/1.1.7.3/snappy-java-1.1.7.3.jar:/{Dependency 설치 경로}/.m2/repository/org/slf4j/slf4j-api/1.7.30/slf4j-api-1.7.30.jar:/{Dependency 설치 경로}/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.11.3/jackson-databind-2.11.3.jar:/{Dependency 설치 경로}/.m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.11.3/jackson-annotations-2.11.3.jar:/{Dependency 설치 경로}/.m2/repository/com/fasterxml/jackson/core/jackson-core/2.11.3/jackson-core-2.11.3.jar cacaotalk.Main
>    ```

