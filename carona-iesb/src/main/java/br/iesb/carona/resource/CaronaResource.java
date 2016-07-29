package br.iesb.carona.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import br.iesb.carona.core.modelo.Carona;
import br.iesb.carona.core.modelo.Usuario;

@Path("/caronas")
public class CaronaResource {

	private static Map<Long, Carona> caronas = new HashMap<Long, Carona>();
	
	@POST
	@Path("/oferecer")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response oferecerCarona(final Carona caronaNova){
		
		boolean erro = false;
		String erroMsg = "";
		
		if(caronaNova.getHorario() == null){
			erro = true;
			erroMsg= erroMsg + "Horário é obrigatório. ";
		}
		if(caronaNova.getDestino() == null){
			erro = true;
			erroMsg = erroMsg + "Destino é obrigatório. ";
		}
		if(caronaNova.getMaximoPasageiros() == 0){
			erro = true;
			erroMsg = erroMsg + "Numero de passageiros é obrigatório. ";
		}
		if(caronaNova.getMotorista() == null){
			erro = true;
			erroMsg = erroMsg + "Motorista é obrigatório. ";
		}
		if(caronaNova.getPontoDePartida() == null || caronaNova.getPontoDePartida().isEmpty()){
			erro = true;
			erroMsg = erroMsg + "Ponto de partida é obrigatório. ";
		}
		if(UsuarioResource.getUsuario(caronaNova.getMotorista().getEmail()) == null)
		{
			erro = true;
			erroMsg = erroMsg + "Motorista não cadastrado. ";
		}
		if(erro){
			return Response.serverError().entity(erroMsg).build();
		}
		
		Long key = System.currentTimeMillis();
		
		caronas.put(key, caronaNova);
		
		JSONObject retorno = new JSONObject();
		retorno.put("keyCarona", key);
		
		return Response.ok(retorno).build();
	}
	
	@GET
	@Path("/lista")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getListaCaronas(){
		return Response.ok(caronas).build();
	}
	
	@GET
	@Path("/consulta")
	@Produces(MediaType.APPLICATION_JSON)
	public Response consultaCaronas(@QueryParam("partida") final String partida, @QueryParam("bairroDestino") final String bairroDestino,
			@QueryParam("localDestino")final String localDestino){
		
		Map<Long, Carona> filtradoPartida = new HashMap<Long, Carona>(caronas);
		if(partida != null && !partida.isEmpty()){
			for(Entry<Long,Carona>caronaEntry : caronas.entrySet()){
				if(!partida.equalsIgnoreCase(caronaEntry.getValue().getPontoDePartida())){
					filtradoPartida.remove(caronaEntry.getKey());
				}
			}
		}
		
		Map<Long, Carona> filtradoBairroDestino = new HashMap<Long, Carona>(filtradoPartida);
		if(bairroDestino != null && !bairroDestino.isEmpty()){
			for(Entry<Long,Carona>bairroEntry:filtradoPartida.entrySet()){
				if(!bairroDestino.equalsIgnoreCase(bairroEntry.getValue().getDestino().getBairro())){
					filtradoBairroDestino.remove(bairroEntry.getKey());
				}
			}
		}
		
		Map<Long, Carona> filtradoLocalDestino = new HashMap<Long, Carona>(filtradoBairroDestino);
		if(localDestino != null && !localDestino.isEmpty()){
			for(Entry<Long,Carona>localEntry:filtradoBairroDestino.entrySet()){
				if(!localDestino.equalsIgnoreCase(localEntry.getValue().getDestino().getLocal())){
					filtradoLocalDestino.remove(localEntry.getKey());
				}
			}
		}
		
		return Response.ok(filtradoLocalDestino).build();
	}
	
	@POST
	@Path("participar/{idCarona}/{idUsuario}")
	public Response participarCarona(@PathParam("idCarona") final Long idCarona, 
			@PathParam("idUsuario") final String idUsuario){
		
		Usuario usuario = UsuarioResource.getUsuario(idUsuario);
		Carona carona = caronas.get(idCarona);
		carona.incluiCaroneiro(usuario);
		
		return Response.ok().build();
	}
	
	
	@GET
	@Path("/listaMinhasCaronas/{idUsuario}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listarMinhasCaronas(@PathParam("idUsuario") final String idUsuario){
		
		List<Carona> retorno = new ArrayList<Carona>();
		Usuario usuario = UsuarioResource.getUsuario(idUsuario);
		
		for(Entry<Long, Carona> caronaEntry : caronas.entrySet()){
			if(caronaEntry.getValue().getMotorista().equals(usuario) || 
					caronaEntry.getValue().getPassageiros().contains(usuario)){
				retorno.add(caronaEntry.getValue());
			}
		}
		
		return Response.ok(retorno).build();
	}
	
}
