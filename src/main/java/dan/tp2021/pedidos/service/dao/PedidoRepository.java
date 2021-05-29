package dan.tp2021.pedidos.service.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import dan.tp2021.pedidos.models.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer>{

	Pedido findByObra(Integer idObra);
}
