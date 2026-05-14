package cn.overthinker.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("cn.overthinker.**.mapper")
public class OjJobApplication {

    public static void main(String[] args) {
        SpringApplication.run(OjJobApplication.class, args);
    }
}