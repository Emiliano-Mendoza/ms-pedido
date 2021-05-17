package com.dan.pedido.service.impl;

import org.springframework.stereotype.Service;

import com.dan.pedido.domain.Producto;
import com.dan.pedido.service.MaterialService;

@Service
public class MaterialServiceImpl implements MaterialService {

	@Override
	public Integer stockDisponible(Producto m) {
		// TODO Auto-generated method stub
		return 50;
	}

}