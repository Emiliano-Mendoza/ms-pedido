package dan.tp2021.pedidos.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dan.tp2021.pedidos.models.Producto;
import dan.tp2021.pedidos.service.MaterialService;
import dan.tp2021.pedidos.service.dao.MaterialRepository;

@Service
public class MaterialServiceImpl implements MaterialService {
	@Autowired
	MaterialRepository materialRepository;
	
	@Override
	public Integer stockDisponible(Producto m) {
		// TODO Auto-generated method stub
		return 10;
	}

	@Override
	public Double getPrecioById(Integer id) {
		return materialRepository.findById(id).get().getPrecio();
	}

}
