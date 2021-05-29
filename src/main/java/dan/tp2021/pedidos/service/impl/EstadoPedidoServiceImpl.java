package dan.tp2021.pedidos.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dan.tp2021.pedidos.models.EstadoPedido;
import dan.tp2021.pedidos.service.EstadoPedidoService;
import dan.tp2021.pedidos.service.dao.EstadoPedidoRepository;

@Service
public class EstadoPedidoServiceImpl implements EstadoPedidoService{
	@Autowired
	EstadoPedidoRepository estadoRepository;
	
	@Override
	public EstadoPedido getByEstado(String estado) {
		// TODO Auto-generated method stub
		return estadoRepository.findAll()
				.stream()
				.filter(e -> e.getEstado().toUpperCase().equals(estado.toUpperCase()))
				.findFirst()
				.get();
	}

}
