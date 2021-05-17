package com.dan.pedido.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dan.pedido.service.*;
import com.dan.pedido.domain.*;
import com.dan.pedido.repository.PedidoRepository;

@Service
public class PedidoServiceImpl implements PedidoService {

	@Autowired
	PedidoRepository repo;
	
	@Autowired
	ClienteService clienteSrv;
	
	@Autowired
	MaterialService materialSrv;
	
	@Override
	public Pedido crearPedido(Pedido p) {
		
		//para cada detalle pedido verifica si hay stock usando un servicio mockeado
		boolean hayStock = p.getDetalle()
		.stream()
		.allMatch(dp -> verificarStock(dp.getProducto(),dp.getCantidad()));
		
		//Suma el precio total entre todos los detalles pedidos del pedido
		Double totalOrden = p.getDetalle()
				.stream()
				.mapToDouble( dp -> dp.getCantidad() * dp.getPrecio())
				.sum();
		
		Double saldoCliente = clienteSrv.deudaCliente(p.getObra());		
		Double nuevoSaldo = saldoCliente - totalOrden;
		
		Boolean generaDeuda= nuevoSaldo<0;
		if(hayStock ) {
				if(!generaDeuda || (generaDeuda && this.esDeBajoRiesgo(p.getObra(),nuevoSaldo) ))  {
					p.setEstado(new EstadoPedido(1,"ACEPTADO"));
				} else {
					throw new RuntimeException("No tiene aprobacion crediticia");
				}
		} else {
			p.setEstado(new EstadoPedido(2,"PENDIENTE"));
		}
		return this.repo.save(p);
	}
	
	
	boolean verificarStock(Producto p,Integer cantidad) {
		return materialSrv.stockDisponible(p)>=cantidad;
	}

	boolean esDeBajoRiesgo(Obra o,Double saldoNuevo) {
		Double maximoSaldoNegativo = clienteSrv.maximoSaldoNegativo(o);
		Boolean tieneSaldo = Math.abs(saldoNuevo) < maximoSaldoNegativo;
		return tieneSaldo;
	}

}