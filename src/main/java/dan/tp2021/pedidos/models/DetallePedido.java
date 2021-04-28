package dan.tp2021.pedidos.models;

public class DetallePedido {
	private Integer id;
	private Integer cantidad;
	private Double precio;
	private Producto producto;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getCantidad() {
		return cantidad;
	}
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
	public Double getPrecio() {
		return precio;
	}
	public void setPrecio(Double precio) {
		this.precio = precio;
	}
	public Producto getProducto() {
		return producto;
	}
	public void setProducto(Producto listaProducto) {
		this.producto = listaProducto;
	}
}
