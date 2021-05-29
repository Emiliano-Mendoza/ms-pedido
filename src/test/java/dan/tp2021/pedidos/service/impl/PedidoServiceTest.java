package dan.tp2021.pedidos.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

import dan.tp2021.pedidos.models.DetallePedido;
import dan.tp2021.pedidos.models.Obra;
import dan.tp2021.pedidos.models.Pedido;
import dan.tp2021.pedidos.models.Producto;
import dan.tp2021.pedidos.repository.PedidoRepositoryOld;
import dan.tp2021.pedidos.service.ClienteService;
import dan.tp2021.pedidos.service.MaterialService;
import dan.tp2021.pedidos.service.PedidoService;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PedidoServiceTest {

		@Autowired
		PedidoService pedidoService;
		
		@MockBean
		MaterialService materialService;
		
		@MockBean
		ClienteService clienteService;
		
		@MockBean
		PedidoRepositoryOld pedidoRepository;
		
		@Test
		public void crearPedidoOk() {
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
			when(clienteService.deudaCliente(unaObra)).thenReturn(100.0);
			when(pedidoRepository.save(any(Pedido.class))).thenReturn(unPedido);
			
			Pedido pedidoResultado = pedidoService.crearPedido(unPedido);
			Assertions.assertEquals(pedidoResultado.getId(), unPedido.getId());
			Assertions.assertEquals(pedidoResultado.getEstado().getEstado(),"Aceptado");
		}
		
		@Test
		public void crearUnPedidoSinStockDisponibleShouldBePendiente() {
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
			unDetalle.setCantidad(2);
			unDetalle.setProducto(unProducto);
			unDetalle.setPrecio(1.0);
			List<DetallePedido> detalles = new ArrayList<DetallePedido>();
			detalles.add(unDetalle);
			unPedido.setDetalle(detalles);
			
			when(materialService.stockDisponible(unProducto)).thenReturn(0);
			when(pedidoRepository.save(any(Pedido.class))).thenReturn(unPedido);
			
			Pedido pedidoResultado = pedidoService.crearPedido(unPedido);
			Assertions.assertEquals(pedidoResultado.getId(), unPedido.getId());
			Assertions.assertEquals(pedidoResultado.getEstado().getEstado(),"Pendiente");
		}
		
		@Test
		public void crearPedidoConSaldoEnNegativoShouldPass() {
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
			unDetalle.setPrecio(100.0);
			List<DetallePedido> detalles = new ArrayList<DetallePedido>();
			detalles.add(unDetalle);
			unPedido.setDetalle(detalles);
			
			when(materialService.stockDisponible(unProducto)).thenReturn(10);
			when(clienteService.deudaCliente(unaObra)).thenReturn(90.0);
			when(clienteService.maximoSaldoNegativo(unaObra)).thenReturn(200.0);
			when(clienteService.situacionCrediticiaBCRA(unaObra)).thenReturn(4);
			when(pedidoRepository.save(any(Pedido.class))).thenReturn(unPedido);
			
			Pedido pedidoResultado = pedidoService.crearPedido(unPedido);
			Assertions.assertEquals(pedidoResultado.getId(), unPedido.getId());
			Assertions.assertEquals(pedidoResultado.getEstado().getEstado(),"Aceptado");
		}
		
		@Test
		public void crearPedidoConMaximoSaldoNegativoShouldPass() {
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
			when(clienteService.deudaCliente(unaObra)).thenReturn(100.0);
			when(clienteService.maximoSaldoNegativo(unaObra)).thenReturn(0.0);
			when(clienteService.situacionCrediticiaBCRA(unaObra)).thenReturn(4);
			when(pedidoRepository.save(any(Pedido.class))).thenReturn(unPedido);
			
			Pedido pedidoResultado = pedidoService.crearPedido(unPedido);
			Assertions.assertEquals(pedidoResultado.getId(), unPedido.getId());
			Assertions.assertEquals(pedidoResultado.getEstado().getEstado(),"Aceptado");
		}
		
		@Test
		public void crearPedidoConSituacionCrediticiaDeAltoRiesgoShouldFail() {
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
			unDetalle.setPrecio(100.0);
			List<DetallePedido> detalles = new ArrayList<DetallePedido>();
			detalles.add(unDetalle);
			unPedido.setDetalle(detalles);
			
			when(materialService.stockDisponible(unProducto)).thenReturn(10);
			when(clienteService.deudaCliente(unaObra)).thenReturn(10.0);
			when(clienteService.maximoSaldoNegativo(unaObra)).thenReturn(100.0);
			when(clienteService.situacionCrediticiaBCRA(unaObra)).thenReturn(1);
			
			Assertions.assertThrows( 
					RuntimeException.class, 
				              () -> pedidoService.crearPedido(unPedido));
		}
}
