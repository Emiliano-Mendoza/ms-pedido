package dan.tp2021.pedidos.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dan.tp2021.pedidos.models.DetallePedido;
import dan.tp2021.pedidos.models.Pedido;
import dan.tp2021.pedidos.service.PedidoService;

@RestController
@RequestMapping("/api/pedidos")
//@Api(value = "PedidosRest", description="")
public class PedidoRest {
	//se trata de la anotación que permite inyectar unas dependencias con otras dentro de Spring
	@Autowired
	PedidoService pedidoSrv;
	
	private static final List<Pedido> listaPedidos = new ArrayList<>();
	private static Integer ID_GEN = 1;
	
	@GetMapping(path="{idPedido}")
	public ResponseEntity<Pedido> pedidoPorId(@PathVariable Integer idPedido){
		Optional<Pedido> pedidos = listaPedidos
				.stream()
				.filter(p -> p.getId().equals(idPedido))
				.findFirst();
		
		return ResponseEntity.of(pedidos);		
	}
	
	@GetMapping(path="{idObra}")
	public ResponseEntity<Pedido> pedidoPorObra(@PathVariable Integer idObra){
		Optional<Pedido> pedidos = listaPedidos
				.stream()
				.filter(p -> p.getObra().getId().equals(idObra))
				.findFirst();
		
		return ResponseEntity.of(pedidos);		
	}
	
//	@GetMapping(path="{cuit}")
//	public ResponseEntity<Pedido> pedidoPorCuitIdCliente(@PathVariable Integer cuit){
//		Optional<Pedido> pedidos = listaPedidos
//				.stream()
//				.filter(p -> p.get.getId().equals(cuit))
//				.findFirst();
//		
//		return ResponseEntity.of(pedidos);		
//	}
	
	@GetMapping(path="{idPedido}/detalle/{id}")
	public ResponseEntity<DetallePedido> buscarDetalle(@PathVariable Integer idPedido, @PathVariable Integer id){
		Optional<Pedido> pedido = listaPedidos
				.stream()
				.filter(p -> p.getId().equals(idPedido))
				.findFirst();
		
		List<DetallePedido> listaDetalles = pedido.get().getDetalle();
		Optional<DetallePedido> detalle = listaDetalles
											.stream()
											.filter(det -> det.getId().equals(id))
											.findFirst();
		if(detalle.isPresent()) {
			return ResponseEntity.of(detalle);
		}
		else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@GetMapping
	public ResponseEntity<List<Pedido>> todos(){
		return ResponseEntity.ok(listaPedidos);
	}
	
	@PostMapping
	public ResponseEntity<String> agregarPedido(@RequestBody Pedido nuevoPedido){
		if(nuevoPedido.getObra() == null) {
			return ResponseEntity.badRequest().body("Debe tener una obra");
		}
		
		if(nuevoPedido.getDetalle().isEmpty() || nuevoPedido.getDetalle() == null) {
			return ResponseEntity.badRequest().body("Debe tener un detalle");
		}
		else {
			Optional<DetallePedido> detallesVacios = nuevoPedido.getDetalle()
														.stream()
														.filter(det -> det.getCantidad() == null)
														.findFirst();
			if(detallesVacios.isEmpty()) {
				return ResponseEntity.badRequest().body("Los detalles deben tener una cantidad y lista de productos");
			}
		}
		
		pedidoSrv.crearPedido(nuevoPedido);
		return ResponseEntity.accepted().build();
	}
	
	@PostMapping(path="/crearPedido")
	public ResponseEntity<String> crearPedido(@RequestBody Pedido unPedido){
		if (unPedido.getObra() == null) {
			return ResponseEntity.badRequest().body("El pedido no tiene una obra asociada");
		}
		if(unPedido.getDetalle() == null || unPedido.getDetalle().isEmpty()) {
			return ResponseEntity.badRequest().body("El pedido no tiene detalle asociado");
		}
		
		pedidoSrv.crearPedido(unPedido);
		return ResponseEntity.status(HttpStatus.CREATED).body("OK Pedido creado");
	}
	
	@PostMapping(path="{idPedido}/detalle")
	public ResponseEntity<Pedido> agregarDetalle(@PathVariable Integer idPedido, @RequestBody List<DetallePedido> detalle){
		Optional<Pedido> pedido = listaPedidos.stream().filter(p -> p.getId().equals(idPedido)).findFirst();
		if (pedido.isPresent()) {
			pedido.get().setDetalle(detalle);
			return ResponseEntity.of(pedido);
		}
		else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@PutMapping(path="{id}")
	public ResponseEntity<Pedido> actualizarPedido(@PathVariable Integer id, @RequestBody Pedido pedidoNuevo){
		OptionalInt indexOpt = IntStream.range(0, listaPedidos.size())
				.filter(emp -> listaPedidos.get(emp).getId().equals(id))
				.findFirst();
		if (indexOpt.isPresent()) {
			listaPedidos.set(indexOpt.getAsInt(), pedidoNuevo);
			return ResponseEntity.ok(pedidoNuevo);
		}
		else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@DeleteMapping(path="{idPedido}")
	public ResponseEntity<Pedido> borrarPedido(@PathVariable Integer idPedido){
		OptionalInt indexOpt = IntStream.range(0, listaPedidos.size())
				.filter(p -> listaPedidos.get(p).getId().equals(idPedido))
				.findFirst();
		if (indexOpt.isPresent()) {
			listaPedidos.remove(indexOpt.getAsInt());
			return ResponseEntity.ok().build();
		}
		else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@DeleteMapping(path="{idPedido}/detalle/{id}")
	public ResponseEntity<Pedido> borrarDetalle(@PathVariable Integer idPedido, @PathVariable Integer id){
		Optional<Pedido> pedido = listaPedidos
				.stream()
				.filter(p -> p.getId().equals(idPedido))
				.findFirst();
		
		List<DetallePedido> listaDetalles = pedido.get().getDetalle();
		OptionalInt detalleIndex = IntStream.range(0, listaDetalles.size()) 
									.filter(det -> listaDetalles.get(det).getId().equals(id))
									.findFirst();
		
		if (detalleIndex.isPresent()) {
			pedido.get().getDetalle().remove(detalleIndex.getAsInt());
			return ResponseEntity.ok().build();
		}
		else {
			return ResponseEntity.notFound().build();
		}
	}
}
