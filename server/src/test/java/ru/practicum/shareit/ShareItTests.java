package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
		classes = ShareItServerApp.class,
		properties = {
				"spring.liquibase.enabled=false",
				"spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false",
				"spring.datasource.driverClassName=org.h2.Driver",
				"spring.datasource.username=sa",
				"spring.datasource.password=",
				"spring.jpa.hibernate.ddl-auto=none"
		}
)
class ShareItTests {
	@Test
	void contextLoads() {
		// smoke test
	}
}
