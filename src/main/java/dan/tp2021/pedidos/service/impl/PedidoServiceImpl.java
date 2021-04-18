package dan.tp2021.pedidos.service.impl;

import org.springframework.stereotype.Service;

import dan.tp2021.pedidos.models.Pedido;
import dan.tp2021.pedidos.service.PedidoService;

@Service
//We mark beans with @Service to indicate that it's holding the business logic. 
//So there's not any other specialty except using it in the service layer.
public class PedidoServiceImpl implements PedidoService{

	@Override
	public Pedido crearPedido(Pedido p) {
		// TODO Auto-generated method stub
		return null;
	}

}
