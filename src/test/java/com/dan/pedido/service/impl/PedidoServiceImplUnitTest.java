package com.dan.pedido.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.dan.pedido.domain.DetallePedido;
import com.dan.pedido.domain.Obra;
import com.dan.pedido.domain.Pedido;
import com.dan.pedido.domain.Producto;
import com.dan.pedido.repository.PedidoRepository;
import com.dan.pedido.service.ClienteService;
import com.dan.pedido.service.MaterialService;
import com.dan.pedido.service.PedidoService;


@SpringBootTest
public class PedidoServiceImplUnitTest {
	
	@Autowired
	PedidoService pedidoService;
	
	@MockBean
	PedidoRepository pedidoRepo;
	
	@MockBean
	ClienteService clienteService;

	@MockBean
	MaterialService materialService;
	
	Pedido unPedido;
	
	@BeforeEach
	void setUp() throws Exception {
		unPedido = new Pedido();
		Obra obra = new Obra();
		DetallePedido d1 = new DetallePedido(new Producto(),5,40.0);
		DetallePedido d2 = new DetallePedido(new Producto(),10,80.0);
		DetallePedido d3 = new DetallePedido(new Producto(),2,450.0);
		unPedido.setDetalle(new ArrayList<DetallePedido>());
		unPedido.getDetalle().add(d1);
		unPedido.getDetalle().add(d2);
		unPedido.getDetalle().add(d3);
		unPedido.setObra(obra);
	}
	
	@Test
	void testCrearPedidoConStockSinDeuda() {
		
		//when(materialService.stockDisponible(p1)).thenReturn(29);
		when(materialService.stockDisponible(any(Producto.class))).thenReturn(20);
		// el cliente no tiene deuda
		when(clienteService.deudaCliente(any(Obra.class))).thenReturn(0.0);
		// el saldo negativo maximo es 10000
		when(clienteService.maximoSaldoNegativo(any(Obra.class))).thenReturn(10000.0);
		// el saldo negativo maximo es 10000
		when(clienteService.situacionCrediticiaBCRA(any(Obra.class))).thenReturn(1);
		// retorno el pedido
		when(pedidoRepo.save(any(Pedido.class))).thenReturn(unPedido);
		//when(clienteService.deudaCliente(argThat( (Obra o) -> o.getId()>99))).thenReturn(0.0);

		Pedido pedidoResultado = pedidoService.crearPedido(unPedido);
		assertThat(pedidoResultado.getEstado().getId().equals(1));
		verify(pedidoRepo,times(1)).save(unPedido);
	}
	
	@Test
	void pedidoSinStockDeberiaDarEstado2() {
		
		//when(materialService.stockDisponible(p1)).thenReturn(29);
		when(materialService.stockDisponible(any(Producto.class))).thenReturn(3);
		// el cliente no tiene deuda
		when(clienteService.deudaCliente(any(Obra.class))).thenReturn(0.0);
		// el saldo negativo maximo es 10000
		when(clienteService.maximoSaldoNegativo(any(Obra.class))).thenReturn(10000.0);
		// el saldo negativo maximo es 10000
		when(clienteService.situacionCrediticiaBCRA(any(Obra.class))).thenReturn(1);
		// retorno el pedido
		when(pedidoRepo.save(any(Pedido.class))).thenReturn(unPedido);
		//when(clienteService.deudaCliente(argThat( (Obra o) -> o.getId()>99))).thenReturn(0.0);

		Pedido pedidoResultado = pedidoService.crearPedido(unPedido);
		assertThat(pedidoResultado.getEstado().getId().equals(2));
		verify(pedidoRepo,times(1)).save(unPedido);
	}
	@Test	
	void pedidoSinAprobacionCrediticia() {
		
		//when(materialService.stockDisponible(p1)).thenReturn(29);
		when(materialService.stockDisponible(any(Producto.class))).thenReturn(20);
		// el cliente no tiene deuda
		when(clienteService.deudaCliente(any(Obra.class))).thenReturn(0.0);
		// el saldo negativo maximo es 10000
		when(clienteService.maximoSaldoNegativo(any(Obra.class))).thenReturn(0.0);
		// el saldo negativo maximo es 10000
		when(clienteService.situacionCrediticiaBCRA(any(Obra.class))).thenReturn(1);
		// retorno el pedido
		when(pedidoRepo.save(any(Pedido.class))).thenReturn(unPedido);
		//when(clienteService.deudaCliente(argThat( (Obra o) -> o.getId()>99))).thenReturn(0.0);

		Exception exception = assertThrows(RuntimeException.class, () -> {
			pedidoService.crearPedido(unPedido);
	    });
		
		String expectedMessage = "No tiene aprobacion crediticia";
	    String actualMessage = exception.getMessage();
		
	    assertTrue(actualMessage.contains(expectedMessage));
	}
	
	
	@AfterEach
	void tearDown() throws Exception {
	}
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}
	
}
