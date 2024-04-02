package smu.poodle.smnavi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmnaviApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmnaviApplication.class, args);
	}

}
