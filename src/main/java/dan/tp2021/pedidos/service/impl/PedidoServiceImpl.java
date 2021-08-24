package dan.tp2021.pedidos.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import dan.tp2021.pedidos.models.DetallePedido;
import dan.tp2021.pedidos.models.EstadoPedido;
import dan.tp2021.pedidos.models.Pedido;
import dan.tp2021.pedidos.models.Producto;
import dan.tp2021.pedidos.service.dao.PedidoRepository;
import dan.tp2021.pedidos.service.ClienteService;
import dan.tp2021.pedidos.service.DetallePedidoService;
import dan.tp2021.pedidos.service.EstadoPedidoService;
import dan.tp2021.pedidos.service.MaterialService;
import dan.tp2021.pedidos.service.PedidoService;

@Service
//We mark beans with @Service to indicate that it's holding the business logic. 
//So there's not any other specialty except using it in the service layer.
public class PedidoServiceImpl implements PedidoService{
	@Autowired
	PedidoRepository pedidoRepository;
	
	@Autowired
	DetallePedidoService detalleService;
	
	@Autowired
	MaterialService materialService;
	
	@Autowired
	ClienteService clienteService;
	
	@Autowired
	EstadoPedidoService estadoService;
	
	@Autowired
	JmsTemplate jms;
	
	@Override
	@Transactional
	public Pedido crearPedido(Pedido nuevoPedido) {
		nuevoPedido.setEstado(getEstadoPorNombre("Nuevo"));			
		pedidoRepository.save(nuevoPedido);
		
		for (DetallePedido detalle : nuevoPedido.getDetalle()) { 
			detalle.setPedido(nuevoPedido);
			this.setearPrecioDetallePedido(detalle);
			detalleService.save(detalle); 
		};	
		return nuevoPedido;
	}
	
	@Override
	public String confirmarPedido(Pedido nuevoPedido) {
		nuevoPedido.setEstado(getEstadoPorNombre("CONFIRMADO"));
		pedidoRepository.save(nuevoPedido);
		
		jms.convertAndSend("COLA_PEDIDOS","Pedido confirmado - id:" + nuevoPedido.getId());
		return ResponseEntity.status(HttpStatus.ACCEPTED).toString();
	}
	
	@Override
	@Transactional
	public Pedido solicitarConfirmacionPedido(Pedido nuevoPedido) {
		//1- ver stock disponible
		//2- el pedido no genera saldo deudor
		//2.1 si el pedido tiene saldo deudor verificar que sea menor que el descubierto y que la situacion crediticia sea de bajo riesgo
		//=> si se cumple a y al menos b o c => pedido aceptado
		//si no se cumple a => pedido pendiente
		//si no se cumple b ni c => pedido rechazado y se lanza una excepcion
		boolean hayStock = nuevoPedido.getDetalle()
							.stream()
							.allMatch(det -> validarStockDisponible(det.getProducto(),det.getCantidad()));
		if(!hayStock) {
			//cargar pedido pendiente
			nuevoPedido.setEstado(getEstadoPorNombre("Pendiente"));
			pedidoRepository.save(nuevoPedido);
			
			for (DetallePedido detalle : nuevoPedido.getDetalle()) { 
				detalle.setPedido(nuevoPedido);
				detalleService.save(detalle); 
			};
			
			return nuevoPedido;
		}
		
		Double montoPedido = nuevoPedido.getDetalle()
								.stream()
								.mapToDouble(det -> det.getCantidad() * det.getPrecio())
								.sum();
				
		Double saldoCliente = clienteService.deudaCliente(nuevoPedido.getObra());
		Double nuevoSaldo = saldoCliente - montoPedido;
		
		if((nuevoSaldo < 0) && (clienteService.maximoSaldoNegativo(nuevoPedido.getObra()) >= nuevoSaldo)) {	
			if(tieneSituacionCrediticiaBajoRiesgo(nuevoPedido)) {
				throw new RuntimeException("El pedido tiene saldo negativo mayor al descubierto y su situacion es de riesgo");
			}
			nuevoPedido.setEstado(getEstadoPorNombre("Aceptado"));			
			pedidoRepository.save(nuevoPedido);
			
			for (DetallePedido detalle : nuevoPedido.getDetalle()) { 
				detalle.setPedido(nuevoPedido);
				detalleService.save(detalle); 
			};			
		}
		else {
			EstadoPedido estadoAceptado = new EstadoPedido();
			estadoAceptado.setEstado("Rechazado");
			nuevoPedido.setEstado(estadoAceptado);
			
			for (DetallePedido detalle : nuevoPedido.getDetalle()) { 
				detalleService.save(detalle); 
			};

			pedidoRepository.save(nuevoPedido);
		}
	
		return nuevoPedido;
	}
	
	@Override
	public Pedido getById(Integer id) {
		return pedidoRepository.findById(id).get();
	}
	
	@Override
	public ResponseEntity<Pedido> agregarProducto(Pedido unPedido, DetallePedido detalleNuevo) {
		// TODO Auto-generated method stub
		detalleNuevo.setPedido(unPedido);
		unPedido.getDetalle().add(detalleNuevo);
		
		detalleService.save(detalleNuevo);
		pedidoRepository.save(unPedido);
		return ResponseEntity.ok(unPedido);
	}
	
	@Override
	@Transactional
	public ResponseEntity<Pedido> actualizarPedido(Integer id, DetallePedido detalleNuevo) {
		Pedido pedidoActualizar = this.getById(id);
		List<DetallePedido> detalles = pedidoActualizar.getDetalle();
		
		//Obtener Precio del producto:
		this.setearPrecioDetallePedido(detalleNuevo);
		detalleNuevo.setPedido(pedidoActualizar);
		pedidoActualizar.getDetalle().add(detalleNuevo);
		
		//detalles.add(detalleNuevo);
		pedidoActualizar.setDetalle(detalles);
		
		detalleService.save(detalleNuevo);
		pedidoRepository.save(pedidoActualizar);

		return ResponseEntity.ok(pedidoActualizar);
	}
	
	@Override
	@Transactional
	public ResponseEntity<String> actualizarEstado(String estadoNuevo, Integer id) {
		Pedido pedidoActualizar = this.getById(id);
		if(pedidoActualizar.getId() != null) {
			pedidoActualizar.setEstado(getEstadoPorNombre(estadoNuevo));
			
			pedidoRepository.save(pedidoActualizar);
			return ResponseEntity.ok().build();
		}
		else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@Override
	public ResponseEntity<String> borrarItemsPedido(Pedido unPedido, DetallePedido detalle) {
		OptionalInt indexOpt = IntStream.range(0, unPedido.getDetalle().size())
									.filter(det -> unPedido.getDetalle().get(det).getId().equals(detalle.getId()))
									.findFirst(); 
		if (indexOpt.isPresent()) { 
			unPedido.getDetalle().remove(indexOpt.getAsInt());
			pedidoRepository.save(unPedido);   
		}
		
		detalleService.delete(detalle);
		return null;
	}
	
	@Override
	public ResponseEntity<String> cancelarPedido(Pedido unPedido) {
		unPedido.setEstado(getEstadoPorNombre("CANCELADO"));
		pedidoRepository.save(unPedido);
		return ResponseEntity.ok("Pedido cancelado");
	}
	
	@Override
	public Pedido getByObra(Integer idObra) {
		Pedido pedido = pedidoRepository.findAll()
											.stream()
											.filter(pe -> pe.getObra().getId().equals(idObra))
											.findFirst()
											.get();
		return pedido;
	}
	
	@Override
	public Pedido getByEstado(String estado) {
		Pedido pedido = pedidoRepository.findAll()
				.stream()
				.filter(pe -> pe.getEstado().getEstado().equals(estado))
				.findFirst()
				.get();
		return pedido;
	}
	
	@Override
	public Pedido getByCliente(Integer idCliente) {
		Pedido pedido = pedidoRepository.findAll()
				.stream()
				.filter(pe -> pe.getObra().getCliente().getId().equals(idCliente))
				.findFirst()
				.get();
		return pedido;
	}
	
	private EstadoPedido getEstadoPorNombre(String estado) {
		return estadoService.getByEstado(estado);
	}

	private boolean validarStockDisponible(Producto producto, Integer cantidad) {
		
		return materialService.stockDisponible(producto) >= cantidad;
	}
	
	private boolean tieneSituacionCrediticiaBajoRiesgo(Pedido pedido) {
		int situacion = clienteService.situacionCrediticiaBCRA(pedido.getObra());
		
		return ((situacion == 1) || (situacion == 2));
	}
	
	private DetallePedido setearPrecioDetallePedido(DetallePedido detalle) {
		Double precioProducto = materialService.getPrecioById(detalle.getProducto().getId());
		detalle.setPrecio(detalle.getCantidad() * precioProducto);
		return detalle;
	}

	@Override
	public List<Pedido> getPorObra(Integer idObra) {
		List<Pedido> pedidos = pedidoRepository.findAll()
				.stream()
				.filter(pe -> pe.getObra().getId().equals(idObra)).collect(Collectors.toList())
				;
		return pedidos;
	}
}
