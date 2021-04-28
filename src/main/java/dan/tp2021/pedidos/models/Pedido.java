package dan.tp2021.pedidos.models;

import java.time.Instant;
import java.util.List;

public class Pedido {
	private Integer id;
	private Instant fechaPedido;
	private List<DetallePedido> detalle;
	private EstadoPedido estado;
	private Obra obra;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Instant getFechaPedido() {
		return fechaPedido;
	}
	public void setFechaPedido(Instant fechaPedido) {
		this.fechaPedido = fechaPedido;
	}
	public List<DetallePedido> getDetalle() {
		return detalle;
	}
	public void setDetalle(List<DetallePedido> detalle) {
		this.detalle = detalle;
	}
	public EstadoPedido getEstado() {
		return estado;
	}
	public void setEstado(EstadoPedido estadoPedido) {
		this.estado = estadoPedido;
	}
	public Obra getObra() {
		return obra;
	}
	public void setObra(Obra obra) {
		this.obra = obra;
	}
}
