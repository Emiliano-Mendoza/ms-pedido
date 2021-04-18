package dan.tp2021.pedidos.models;

import java.util.List;

public class DetallePedido {
	private Integer id;
	private Integer cantidad;
	private Double precio;
	private List<Producto> listaProducto;
	
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
	public List<Producto> getListaProducto() {
		return listaProducto;
	}
	public void setListaProducto(List<Producto> listaProducto) {
		this.listaProducto = listaProducto;
	}
}
