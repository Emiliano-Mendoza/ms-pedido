package dan.tp2021.pedidos.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dan.tp2021.pedidos.models.DetallePedido;
import dan.tp2021.pedidos.service.DetallePedidoService;
import dan.tp2021.pedidos.service.dao.DetallePedidoRepository;

@Service
public class DetallePedidoServiceImpl implements DetallePedidoService {
	@Autowired
	DetallePedidoRepository detalleRepository;
	
	@Override
	public DetallePedido save(DetallePedido detalle) {
		// TODO Auto-generated method stub
		detalleRepository.save(detalle);
		return detalle;
	}

	@Override
	public void delete(DetallePedido detalle) {
		detalleRepository.deleteById(detalle.getId());
	}

}
