package dan.tp2021.pedidos.models;

import java.sql.Date;
import java.time.Instant;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Pedido {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	private Date fechaPedido;
	@OneToMany(mappedBy = "pedido")
	private List<DetallePedido> detallepedido;
	@OneToOne
	@JoinColumn(name = "estadopedido_id")
	private EstadoPedido estadopedido;
	@OneToOne
	@JoinColumn(name = "obra_id", referencedColumnName = "id")
	private Obra obra;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Date getFechaPedido() {
		return fechaPedido;
	}
	public void setFechaPedido(Date fechaPedido) {
		this.fechaPedido = fechaPedido;
	}
	public List<DetallePedido> getDetalle() {
		return detallepedido;
	}
	public void setDetalle(List<DetallePedido> detalle) {
		this.detallepedido = detalle;
	}
	public EstadoPedido getEstado() {
		return estadopedido;
	}
	public void setEstado(EstadoPedido estadoPedido) {
		this.estadopedido = estadoPedido;
	}
	public Obra getObra() {
		return obra;
	}
	public void setObra(Obra obra) {
		this.obra = obra;
	}
}
