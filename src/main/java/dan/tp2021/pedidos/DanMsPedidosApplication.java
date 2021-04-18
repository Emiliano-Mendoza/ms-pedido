package dan.tp2021.pedidos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import dan.tp2021.pedidos.rest.SwaggerConfiguration;

@SpringBootApplication
@Import(SwaggerConfiguration.class)
public class DanMsPedidosApplication {

	public static void main(String[] args) {
		SpringApplication.run(DanMsPedidosApplication.class, args);
	}

	public void addResourceHandlers(ResourceHandlerRegistry registry) {
 
           registry.addResourceHandler("swagger-ui.html")
                    .addResourceLocations("classpath:/META-INF/resources/");
    }
}