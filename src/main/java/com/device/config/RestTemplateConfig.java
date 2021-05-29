package com.device.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * RestTemplate Config
 *
 * @author 宁海博
 * @since 2019/10/8 16:57
 */
@Configuration
public class RestTemplateConfig {


    @Bean
    @ConditionalOnMissingBean({ClientHttpRequestFactory.class})
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        //单位为ms
        factory.setReadTimeout(60000);
        //单位为ms
        factory.setConnectTimeout(60000);
        return factory;
    }

    @Bean
    @ConditionalOnMissingBean({RestOperations.class, RestTemplate.class})
    public RestTemplate restTemplate(ClientHttpRequestFactory simpleClientHttpRequestFactory) {
        RestTemplate restTemplate = new RestTemplate(simpleClientHttpRequestFactory);
        // 使用 utf-8 编码集的 converter 替换默认的 converter（默认的 string converter 的编码集为"ISO-8859-1"）
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        Iterator<HttpMessageConverter<?>> iterator = messageConverters.iterator();
        while (iterator.hasNext()) {
            HttpMessageConverter<?> converter = iterator.next();
            if (converter instanceof StringHttpMessageConverter) {
                iterator.remove();
            }
        }
        messageConverters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
        return restTemplate;
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(Charset.forName("GBK"))); // StandardCharsets.UTF_8
        return restTemplate;
    }


    @SuppressWarnings("unchecked")
    public void setRestTemplateEncode(RestTemplate restTemplate) {
        if (restTemplate != null){
            StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
            List supportedMediaTypes = new ArrayList();
            supportedMediaTypes.add(MediaType.TEXT_HTML);
            supportedMediaTypes.add(MediaType.TEXT_PLAIN);
            supportedMediaTypes.add(MediaType.MULTIPART_FORM_DATA);
            supportedMediaTypes.add(MediaType.APPLICATION_XML);
            stringConverter.setSupportedMediaTypes(supportedMediaTypes);
            FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
            List formSupportedMediaTypes = new ArrayList();
            formSupportedMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
            formSupportedMediaTypes.add(MediaType.MULTIPART_FORM_DATA);
            formSupportedMediaTypes.add(MediaType.TEXT_PLAIN);
            formSupportedMediaTypes.add(MediaType.APPLICATION_XML);
            formHttpMessageConverter.setSupportedMediaTypes(formSupportedMediaTypes);
            List formPartConverters = new ArrayList();
            formPartConverters.add(stringConverter);
            formPartConverters.add(new ByteArrayHttpMessageConverter());
            formPartConverters.add(new ResourceHttpMessageConverter());
            formPartConverters.add(new SourceHttpMessageConverter());
            formPartConverters.add(new Jaxb2RootElementHttpMessageConverter());
            formHttpMessageConverter.setPartConverters(formPartConverters);
            List partConverters = new ArrayList();
            partConverters.add(formHttpMessageConverter);
            partConverters.add(stringConverter);
            restTemplate.setMessageConverters(partConverters);
        }
    }

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

}
