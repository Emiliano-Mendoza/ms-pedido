package dan.tp2021.pedidos.service;

import dan.tp2021.pedidos.models.EstadoPedido;

public interface EstadoPedidoService {
	EstadoPedido getByEstado(String estado);
}
