package com.fawvw.shutdownsystem;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fawvw.shutdownsystem.repository.FailureLogCARepository;

@SpringBootTest
class ShutdownSystemApplicationTests {
	private FailureLogCARepository failureLogCARepository;

	@Autowired
	public ShutdownSystemApplicationTests(FailureLogCARepository failureLogCARepository) {
		this.failureLogCARepository = failureLogCARepository;
	}

	@Test
	void contextLoads() {
		
	}

	

}
