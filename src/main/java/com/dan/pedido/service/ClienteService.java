package com.dan.pedido.service;

import com.dan.pedido.domain.Obra;

public interface ClienteService {
	
	public Double deudaCliente(Obra id);
	public Double maximoSaldoNegativo(Obra id);
	public Integer situacionCrediticiaBCRA(Obra id);
	
}
