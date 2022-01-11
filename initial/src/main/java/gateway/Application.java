package gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	private static final String HTTP_BIN = "http://httpbin.org";

	@Bean
	public RouteLocator myRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(p->p
						.path("/get")
						.filters(f->f.addRequestHeader("Header", "Value"))
						.uri(HTTP_BIN))
				.route(p->p
						.host("*.circuitbreaker.com")
						.filters(f->f.circuitBreaker(config->config
								.setName("mycmd")
								.setFallbackUri("forward:/fallback")))
						.uri(HTTP_BIN))
				.build();
	}
	
	@RequestMapping("/fallback")
	public Mono<String> fallback(){
		return Mono.just("fallback");
	}
}
