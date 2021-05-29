package dan.tp2021.pedidos.rest;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import dan.tp2021.pedidos.models.DetallePedido;
import dan.tp2021.pedidos.models.Obra;
import dan.tp2021.pedidos.models.Pedido;
import dan.tp2021.pedidos.models.Producto;
import dan.tp2021.pedidos.service.ClienteService;
import dan.tp2021.pedidos.service.MaterialService;
import dan.tp2021.pedidos.service.PedidoService;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PedidoRestTest {
	private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private TestRestTemplate testRestTemplate;
    
    @Autowired
    PedidoService pedidoService;
    
    @MockBean
    ClienteService clienteService;
    
    @MockBean
    MaterialService materialService;
    
    @LocalServerPort
	String puerto;	  
	
	private final String urlServer= "http://localhost";
	private final String apiPedidos = "api/pedidos";
    int randomServerPort;
    
    @Test
    public void agregarPedidoOk() {
    	String postUri = "/crearPedido";
    	String server = urlServer + ":" + puerto + "/" + apiPedidos + postUri;
    	System.out.println("SERVER "+ server);
    	
    	Pedido unPedido = new Pedido();
		unPedido.setId(1);
		Obra unaObra = new Obra();
		unaObra.setId(1);
		unaObra.setDescripcion("Obra test");
		unPedido.setObra(unaObra);
		
		Producto unProducto = new Producto();
		unProducto.setId(1);
		unProducto.setDescripcion("producto test");
		unProducto.setPrecio(1.0);
		
		DetallePedido unDetalle = new DetallePedido();
		unDetalle.setId(1);
		unDetalle.setCantidad(1);
		unDetalle.setProducto(unProducto);
		unDetalle.setPrecio(1.0);
		List<DetallePedido> detalles = new ArrayList<DetallePedido>();
		detalles.add(unDetalle);
		unPedido.setDetalle(detalles);
		
		when(materialService.stockDisponible(unProducto)).thenReturn(10);
		when(clienteService.deudaCliente(unaObra)).thenReturn(10.0);
		when(clienteService.maximoSaldoNegativo(unaObra)).thenReturn(100.0);
		when(clienteService.situacionCrediticiaBCRA(unaObra)).thenReturn(4);
		
		HttpEntity<Pedido> requestPedido = new HttpEntity<>(unPedido);
    	
    	ResponseEntity<String> respuesta = restTemplate.exchange(server, HttpMethod.POST,requestPedido, String.class);
  
    	Assertions.assertEquals(ResponseEntity.status(HttpStatus.CREATED).build().getStatusCode().value(), respuesta.getStatusCodeValue());
    	Assertions.assertEquals("OK Pedido creado", respuesta.getBody());
    }
    
    @Test
    public void agregarPedidoSinObraShouldFail() {
    	String postUri = "/crearPedido";
    	String server = urlServer + ":" + puerto + "/" + apiPedidos + postUri;
    	System.out.println("SERVER "+ server);
    	
    	Pedido unPedido = new Pedido();
		unPedido.setId(1);
		
		HttpEntity<Pedido> requestPedido = new HttpEntity<>(unPedido);
    	
    	ResponseEntity<String> respuesta = testRestTemplate.exchange(server, HttpMethod.POST,requestPedido, String.class);
  
    	Assertions.assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST).build().getStatusCode().value(), respuesta.getStatusCodeValue());
    	Assertions.assertEquals("El pedido no tiene una obra asociada", respuesta.getBody());
    }
    
    @Test
    public void agregarPedidoSinDetalleShouldFail() {
    	String postUri = "/crearPedido";
    	String server = urlServer + ":" + puerto + "/" + apiPedidos + postUri;
    	System.out.println("SERVER "+ server);
    	
    	Pedido unPedido = new Pedido();
		unPedido.setId(1);
		
		Obra unaObra = new Obra();
		unaObra.setId(1);
		unaObra.setDescripcion("Obra test");
		unPedido.setObra(unaObra);
		
		HttpEntity<Pedido> requestPedido = new HttpEntity<>(unPedido);
    	
    	ResponseEntity<String> respuesta = testRestTemplate.exchange(server, HttpMethod.POST,requestPedido, String.class);
  
    	Assertions.assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST).build().getStatusCode().value(), respuesta.getStatusCodeValue());
    	Assertions.assertEquals("El pedido no tiene detalle asociado", respuesta.getBody());
    }
}
