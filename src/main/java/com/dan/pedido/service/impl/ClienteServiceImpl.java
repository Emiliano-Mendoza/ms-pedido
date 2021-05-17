package com.dan.pedido.service.impl;

import org.springframework.stereotype.Service;

import com.dan.pedido.domain.Obra;
import com.dan.pedido.service.ClienteService;

@Service
public class ClienteServiceImpl implements ClienteService {

	@Override
	public Double deudaCliente(Obra id) {
		// TODO Auto-generated method stub
		return 4000.0;
	}

	@Override
	public Integer situacionCrediticiaBCRA(Obra id) {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public Double maximoSaldoNegativo(Obra id) {
		// TODO Auto-generated method stub
		return 5000.0;
	}

}
