package com.dan.pedido.repository;

import org.springframework.stereotype.Repository;

import com.dan.pedido.domain.Pedido;

import frsf.isi.dan.InMemoryRepository;

@Repository
public class PedidoRepository extends InMemoryRepository<Pedido>{
	
	@Override
	public Integer getId(Pedido entity) {
		return entity.getId();
	}
	
	@Override
	public void setId(Pedido entity, Integer id) {
		entity.setId(id);
	}
	
}
