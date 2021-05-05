package dan.tp2021.pedidos.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dan.tp2021.pedidos.models.EstadoPedido;
import dan.tp2021.pedidos.models.Pedido;
import dan.tp2021.pedidos.models.Producto;
import dan.tp2021.pedidos.repository.PedidoRepository;
import dan.tp2021.pedidos.service.ClienteService;
import dan.tp2021.pedidos.service.MaterialService;
import dan.tp2021.pedidos.service.PedidoService;

@Service
//We mark beans with @Service to indicate that it's holding the business logic. 
//So there's not any other specialty except using it in the service layer.
public class PedidoServiceImpl implements PedidoService{
	@Autowired
	PedidoRepository pedidoRepository;
	
	@Autowired
	MaterialService materialService;
	
	@Autowired
	ClienteService clienteService;
	
	@Override
	public Pedido crearPedido(Pedido nuevoPedido) {
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
			EstadoPedido estadoPendiente = new EstadoPedido();
			estadoPendiente.setEstado("Pendiente");
			nuevoPedido.setEstado(estadoPendiente);
			pedidoRepository.save(nuevoPedido);
			
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
			EstadoPedido estadoPendiente = new EstadoPedido();
			estadoPendiente.setEstado("Aceptado");
			nuevoPedido.setEstado(estadoPendiente);
			
			pedidoRepository.save(nuevoPedido);
			//hacer pedido			
		}
		else {
			EstadoPedido estadoPendiente = new EstadoPedido();
			estadoPendiente.setEstado("Aceptado");
			nuevoPedido.setEstado(estadoPendiente);
			pedidoRepository.save(nuevoPedido);
		}
	
		return nuevoPedido;
	}
	
	private boolean validarStockDisponible(Producto producto, Integer cantidad) {
		
		return materialService.stockDisponible(producto) >= cantidad;
	}
	
	private boolean tieneSituacionCrediticiaBajoRiesgo(Pedido pedido) {
		int situacion = clienteService.situacionCrediticiaBCRA(pedido.getObra());
		
		return ((situacion == 1) || (situacion == 2));
	}
}
