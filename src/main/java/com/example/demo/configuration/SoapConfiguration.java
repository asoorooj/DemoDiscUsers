package com.example.demo.configuration;

import com.example.demo.endpoint.UserEndpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.xml.ws.Endpoint;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@Configuration
public class SoapConfiguration {

    @EnableWs
    @Configuration
    public class SoapConfig extends WsConfigurerAdapter {

//        http://localhost:8080/ws/users.wsdl
//        http://localhost:8080/ws
        @Bean
        public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext context) {
            MessageDispatcherServlet servlet = new MessageDispatcherServlet();
            servlet.setApplicationContext(context);
            servlet.setTransformWsdlLocations(true);
            return new ServletRegistrationBean<>(servlet, "/ws/*"); // <--- Base path
        }

        @Bean(name = "users")
        public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema userSchema) {
            DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
            wsdl11Definition.setPortTypeName("UserServicePort");
            wsdl11Definition.setLocationUri("/ws");  // <--- SOAP endpoint path
            wsdl11Definition.setTargetNamespace("http://soap.jee.mcnz.com/");
            wsdl11Definition.setSchema(userSchema);
            return wsdl11Definition;
        }

        @Bean
        public XsdSchema userSchema() {
            return new SimpleXsdSchema(new ClassPathResource("user.xsd"));
        }
    }
}

