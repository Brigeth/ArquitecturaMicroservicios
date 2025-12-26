package com.brigeth.api_customer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.brigeth.infraestructure.adapter.output.persistence.repository.CustomerJpaRepository;


@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class ApiCustomerApplicationTests {

	@MockBean
	private CustomerJpaRepository customerJpaRepository;

	@Test
	void contextLoads() {
	}

}
