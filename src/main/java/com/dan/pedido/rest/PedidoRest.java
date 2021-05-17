package com.dan.pedido.rest;

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

import com.dan.pedido.domain.DetallePedido;
import com.dan.pedido.domain.Pedido;
import com.dan.pedido.service.PedidoService;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/api/pedido")
@Api(value = "PedidoRest")
public class PedidoRest {
	
	private static final List<Pedido> listaPedidos = new ArrayList<>();
    //private static Integer ID_GEN = 1;
    
    @Autowired
    private PedidoService pedidoServ;
    
	
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Pedido nuevo){
    	
    	if(nuevo.getObra() == null ) {
    		return ResponseEntity.badRequest().body(("Debe elegir una obra"));
    	}
    	if( nuevo.getDetalle() == null || nuevo.getDetalle().isEmpty()) {
    		return ResponseEntity.badRequest().body(("Agregar item al pedido"));
    	}
    	
    	this.pedidoServ.crearPedido(nuevo);
    	
    	return ResponseEntity.status(HttpStatus.CREATED).body("OK");
    }
    
    @PostMapping(path = "/{idPedido}/detalle")
    @ApiOperation(value = "Agrega un item a un pedido")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Actualizado correctamente"),
        @ApiResponse(code = 401, message = "No autorizado"),
        @ApiResponse(code = 403, message = "Prohibido"),
        @ApiResponse(code = 404, message = "El ID no existe")
    })
    public ResponseEntity<Pedido> agregarItem(@RequestBody DetallePedido nuevo, @PathVariable Integer idPedido){
    	System.out.println(" agregar item al pedido: "+ nuevo);
        
    	OptionalInt indexOpt =   IntStream.range(0, listaPedidos.size())
    	        .filter(i -> listaPedidos.get(i).getId().equals(idPedido))
    	        .findFirst();
    	
    	
    	if(listaPedidos.get(indexOpt.getAsInt()).getDetalle() == null) {
    		
    		List<DetallePedido> listaDetalle = new ArrayList<>();
    		listaDetalle.add(nuevo);
    		
    		Pedido auxPedido = listaPedidos.get(indexOpt.getAsInt());
    		auxPedido.setDetalle(listaDetalle);
    		
    		listaPedidos.set(indexOpt.getAsInt(), auxPedido);
    		return ResponseEntity.ok(auxPedido);
    	}
    	else {
    		
    		Pedido auxPedido = listaPedidos.get(indexOpt.getAsInt());
    		auxPedido.getDetalle().add(nuevo);
    		
    		listaPedidos.set(indexOpt.getAsInt(), auxPedido);
    		return ResponseEntity.ok(auxPedido);
    	}
    	
    }
    
    @PutMapping(path = "/id/{id}")
    @ApiOperation(value = "Actualiza un pedido")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Actualizado correctamente"),
        @ApiResponse(code = 401, message = "No autorizado"),
        @ApiResponse(code = 403, message = "Prohibido"),
        @ApiResponse(code = 404, message = "El ID no existe")
    })
    public ResponseEntity<Pedido> actualizar(@RequestBody Pedido nuevo,  @PathVariable Integer id){
        
    	OptionalInt indexOpt =   IntStream.range(0, listaPedidos.size())
        .filter(i -> listaPedidos.get(i).getId().equals(id))
        .findFirst();

        if(indexOpt.isPresent()){
            listaPedidos.set(indexOpt.getAsInt(), nuevo);
            return ResponseEntity.ok(nuevo);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping(path = "/{idPedido}")
    public ResponseEntity<Pedido> borrar(@PathVariable Integer idPedido){
        OptionalInt indexOpt =   IntStream.range(0, listaPedidos.size())
        .filter(i -> listaPedidos.get(i).getId().equals(idPedido))
        .findFirst();

        if(indexOpt.isPresent()){
            listaPedidos.remove(indexOpt.getAsInt());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping(path = "/{idPedido}/detalle/{id}")
    public ResponseEntity<Pedido> borrarItem(@PathVariable Integer idPedido, @PathVariable Integer id){
        
    	OptionalInt indexOpt =   IntStream.range(0, listaPedidos.size())
        .filter(i -> listaPedidos.get(i).getId().equals(idPedido))
        .findFirst();

        if(indexOpt.isPresent()){
        	
        	Pedido auxPedido = listaPedidos.get(indexOpt.getAsInt());
        	List<DetallePedido> listaItems = auxPedido.getDetalle();
        	
        	OptionalInt indexOpt2 = IntStream.range(0, listaItems.size())
        	        .filter(j -> listaItems.get(j).getId().equals(id))
        	        .findFirst();
        	
        	if(indexOpt2.isPresent()) {
        		listaItems.remove(indexOpt2.getAsInt());
        		auxPedido.setDetalle(listaItems);
        	}
        	else {
                return ResponseEntity.notFound().build();
            }
        	
        	listaPedidos.set(indexOpt.getAsInt(), auxPedido);
            return ResponseEntity.ok(auxPedido);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    
    @GetMapping
    public ResponseEntity<List<Pedido>> todos(){
        return ResponseEntity.ok(listaPedidos);
    }
    
    @GetMapping(path = "/{idPedido}")
    @ApiOperation(value = "Busca un pedido por id")
    public ResponseEntity<Pedido> PedidoPorId(@PathVariable Integer idPedido){

        Optional<Pedido> c =  listaPedidos
                .stream()
                .filter(i -> i.getId().equals(idPedido))
                .findFirst();
        return ResponseEntity.of(c);
    }
    
    @GetMapping(path = "/obra/{idObra}")
    @ApiOperation(value = "Busca un pedido por id de una obra")
    public ResponseEntity<Pedido> PedidoPorIdDeObra(@PathVariable Integer idObra){

        Optional<Pedido> c =  listaPedidos
                .stream()
                .filter(i -> i.getObra().getId().equals(idObra))
                .findFirst();
        
        return ResponseEntity.of(c);
    }
    
    @GetMapping(path = "/{idPedido}/detalle/{id}")
    @ApiOperation(value = "Busca un detalle por id")
    public ResponseEntity<DetallePedido> DetallePorID(@PathVariable Integer idPedido, @PathVariable Integer id){
    	
    	OptionalInt indexOpt =   IntStream.range(0, listaPedidos.size())
    	        .filter(i -> listaPedidos.get(i).getId().equals(idPedido))
    	        .findFirst();
    	
    	if(indexOpt.isPresent()){
        	
        	Optional<DetallePedido> c =  listaPedidos.get(indexOpt.getAsInt()).getDetalle()
                    .stream()
                    .filter(i -> i.getId().equals(id))
                    .findFirst();
        	return ResponseEntity.of(c);
        	
        } else {
            return ResponseEntity.notFound().build();
        }
    	
    }
    
    //Falta el de buscar por Cuit o id de cliente
}
