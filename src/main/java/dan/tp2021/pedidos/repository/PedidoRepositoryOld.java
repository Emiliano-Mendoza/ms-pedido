package dan.tp2021.pedidos.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dan.tp2021.pedidos.models.Pedido;
import frsf.isi.dan.*;

@Repository
public class PedidoRepositoryOld extends InMemoryRepository<Pedido> {

	@Override
	public Integer getId(Pedido entity) {
		// TODO Auto-generated method stub
		return entity.getId();
	}

	@Override
	public void setId(Pedido entity, Integer id) {
		// TODO Auto-generated method stub
		entity.setId(id);
	}
	

}
