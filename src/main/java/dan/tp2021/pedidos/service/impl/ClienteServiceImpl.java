package dan.tp2021.pedidos.service.impl;

import org.springframework.stereotype.Service;

import dan.tp2021.pedidos.models.Obra;
import dan.tp2021.pedidos.service.ClienteService;

@Service
public class ClienteServiceImpl implements ClienteService {

	@Override
	public Double deudaCliente(Obra id) {
		// TODO Auto-generated method stub
		return 0.0;
	}

	@Override
	public Double maximoSaldoNegativo(Obra id) {
		// TODO Auto-generated method stub
		return 10000.0;
	}

	@Override
	public Integer situacionCrediticiaBCRA(Obra id) {
		// TODO Auto-generated method stub
		return 4;
	}

}
