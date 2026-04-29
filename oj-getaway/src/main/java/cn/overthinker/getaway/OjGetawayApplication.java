package cn.overthinker.getaway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class OjGetawayApplication {
    public static void main(String[] args) {
        SpringApplication.run(OjGetawayApplication.class, args);
    }
}
