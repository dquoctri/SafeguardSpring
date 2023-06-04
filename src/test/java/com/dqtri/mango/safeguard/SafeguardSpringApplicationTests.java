package com.dqtri.mango.safeguard;

import io.jsonwebtoken.lang.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {SafeguardSpringApplication.class})
class SafeguardSpringApplicationTests {
	@Test
	 void contextLoads() {
		Assert.isTrue(true);
	}
}
