package demo.tuning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class TuningApplication {
    public static void main(String[] args) {
        SpringApplication.run(TuningApplication.class, args);
    }
}
