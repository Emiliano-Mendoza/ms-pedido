package dan.tp2021.pedidos.service;

import org.springframework.http.ResponseEntity;

import dan.tp2021.pedidos.models.DetallePedido;
import dan.tp2021.pedidos.models.Pedido;

public interface PedidoService {
	
	public Pedido crearPedido(Pedido p);

	public Pedido getById(Integer id);

	public ResponseEntity<Pedido> actualizarPedido(Integer id, DetallePedido detalleNuevo);

	public String confirmarPedido(Pedido nuevoPedido);

	public ResponseEntity<String> actualizarEstado(String estadoNuevo, Integer id);

	public Pedido solicitarConfirmacionPedido(Pedido nuevoPedido);

	public ResponseEntity<Pedido> agregarProducto(Pedido unPedido, DetallePedido detalleNuevo);

	public ResponseEntity<String> borrarItemsPedido(Pedido unPedido, DetallePedido detalle);

	public ResponseEntity<String> cancelarPedido(Pedido unPedido);

	public Pedido getByObra(Integer idObra);

	public Pedido getByEstado(String estado);

	public Pedido getByCliente(Integer idCliente);
}
