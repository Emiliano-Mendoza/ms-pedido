package dan.tp2021.pedidos.service.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import dan.tp2021.pedidos.models.Producto;

public interface MaterialRepository extends JpaRepository<Producto, Integer>{

}
