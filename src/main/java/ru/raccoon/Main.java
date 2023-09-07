package ru.raccoon;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Main {
    public static final String REMOTE_SERVICE_URI = "https://api.nasa.gov/planetary/apod?api_key=fPwmgjfc9ML9U4fFixoxqnTdVwd3eQD02BSfcu0K";

    public static ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); //ненужные нам свойства не будем десериализовать

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();

        HttpGet request = new HttpGet(REMOTE_SERVICE_URI);
        CloseableHttpResponse response = httpClient.execute(request);

        NASAData nasaData = mapper.readValue(response.getEntity().getContent(), NASAData.class); //десериализуем

        request = new HttpGet(nasaData.getUrl());
        response = httpClient.execute(request); //выполняем запрос по полученному url

        InputStream inputStream = response.getEntity().getContent(); //входящий поток

        //сохраняем входящий поток в файл
        try (FileOutputStream fileOutputStream = new FileOutputStream(setFileName(nasaData.getUrl()))) {
            int inByte;
            while((inByte = inputStream.read()) != -1)
                fileOutputStream.write(inByte);
        }
    }

    //метод для определения имени сохраняемого файла
    public static String setFileName(String inputString) {
        int lastIndex = inputString.lastIndexOf("/");
        return lastIndex == -1 ? "unnamedFile" : inputString.substring(lastIndex + 1);
    }
}