package br.iesb.carona.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
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

import com.google.gson.Gson;

import br.iesb.carona.core.modelo.Carona;
import br.iesb.carona.core.modelo.CaronaPendente;
import br.iesb.carona.core.modelo.Usuario;

@Path("/caronas")
public class CaronaResource {

	private static final Map<Long, Carona> caronas = new HashMap<Long, Carona>();
	private static final Map<Long, CaronaPendente> caronasPendentes = new HashMap<Long, CaronaPendente>();
	
	@POST
	@Path("/oferecer")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response oferecerCarona(final String caronaJson){
		
		Gson gson = new Gson();
		Carona caronaNova = gson.fromJson(caronaJson, Carona.class);
		
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
			throw new RuntimeException(erroMsg);
		}
		
		Long key = System.currentTimeMillis();
		caronaNova.setId(key);
		
		caronas.put(key, caronaNova);
		
		return Response.ok(gson.toJson(caronaNova)).build();
	}
	
	@GET
	@Path("/consulta")
	@Produces(MediaType.APPLICATION_JSON)
	public Response consultaCaronas(@QueryParam("partida") final String partida, @QueryParam("bairroDestino") final String bairroDestino,
			@QueryParam("localDestino")final String localDestino, @QueryParam("idCarona") final long idCarona){

<<<<<<< HEAD
		Map<Long, Carona> filtradoId = new HashMap<Long,Carona>();
		if(idCarona != 0l){
			Carona filtrada = caronas.get(idCarona);
			if(filtrada != null){
				filtradoId.put(idCarona, filtrada);
			}
		}
		else{
			filtradoId = new HashMap<Long,Carona>(caronas);
		}
=======
>>>>>>> refs/heads/desenv
		
<<<<<<< HEAD
		Map<Long, Carona> filtradoPartida = new HashMap<Long, Carona>(filtradoId);
=======
		List<Carona> filtradoId = new ArrayList<Carona>();
		if(idCarona != 0l){
			Carona filtrada = caronas.get(idCarona);
			if(filtrada != null){
				filtradoId.add(filtrada);
			}
		}
		else{
			filtradoId = new ArrayList<Carona>(caronas.values());
		}
		
		List<Carona> filtradoPartida = new LinkedList<Carona>(filtradoId);
>>>>>>> refs/heads/desenv
		if(partida != null && !partida.isEmpty()){
			List<Carona> auxiliar = new LinkedList<Carona>();
			for(Carona carona : filtradoPartida){
				if(partida.equalsIgnoreCase(carona.getPontoDePartida())){
					auxiliar.add(carona);
				}
			}
			filtradoPartida = auxiliar;
		}
		
		List<Carona> filtradoBairroDestino = new LinkedList<Carona>(filtradoPartida);
		if(bairroDestino != null && !bairroDestino.isEmpty()){
			List<Carona> auxiliar = new LinkedList<Carona>();
			for(Carona carona:filtradoPartida){
				if(bairroDestino.equalsIgnoreCase(carona.getDestino().getBairro())){
					auxiliar.add(carona);
				}
			}
			filtradoBairroDestino = auxiliar;
		}
		
		List<Carona> filtradoLocalDestino = new LinkedList<Carona>(filtradoBairroDestino);
		if(localDestino != null && !localDestino.isEmpty()){
			List<Carona>auxiliar = new LinkedList<Carona>();
			for(Carona carona:filtradoBairroDestino){
				if(localDestino.equalsIgnoreCase(carona.getDestino().getLocal())){
					auxiliar.add(carona);
				}
			}
			filtradoLocalDestino = auxiliar;
		}
		
		return Response.ok(filtradoLocalDestino).build();
	}
	
	@POST
	@Path("solicitarParticipacao/{idCarona}/{idUsuario}")
	public Response participarCarona(@PathParam("idCarona") final Long idCarona, 
			@PathParam("idUsuario") final String idUsuario){
		
		Usuario usuario = UsuarioResource.getUsuario(idUsuario);
		if(usuario == null){
			return Response.serverError().entity("Usuário " + idUsuario + " inexistente.").build();
		}
		Carona carona = caronas.get(idCarona);
		if(carona == null){
			return Response.serverError().entity("Carona inexistente").build();
		}
		int totalPassageirosESolicitantes = carona.getPassageiros().size(); 
		if(!(totalPassageirosESolicitantes < carona.getMaximoPasageiros())){
			return Response.serverError().entity("Carona já está lotada").build();
		}
<<<<<<< HEAD
=======
		if(carona.getMotorista().getEmail().equals(idUsuario) || carona.getPassageiros().contains(usuario)){
			return Response.serverError().entity("Usuario já participa da carona").build();
		}
>>>>>>> refs/heads/desenv
		
		Usuario aprovador = carona.getMotorista();
		
		CaronaPendente solicitacao = new CaronaPendente();
		solicitacao.setAprovador(aprovador.getEmail());
		solicitacao.setSolicitante(idUsuario);
		solicitacao.setIdCarona(carona.getId());
<<<<<<< HEAD
				
		List<CaronaPendente> caronasSolicitadas = getCaronasSolicitadas(idCarona);
		
		if(!caronasSolicitadas.isEmpty()){
			totalPassageirosESolicitantes += caronasSolicitadas.size();
			
			if(!(totalPassageirosESolicitantes < carona.getMaximoPasageiros())){
				return Response.serverError().entity("Todos os lugares já foram solicitados").build();
			}
			
			for(CaronaPendente caronaSolicitada : caronasSolicitadas){
				if(caronaSolicitada.getSolicitante().equals(idUsuario)){
					return Response.serverError().entity("Já existe solicitação do usuário para esta carona").build();
				}
			}
		}
		
		caronasPendentes.put(System.currentTimeMillis(), solicitacao);
=======
		solicitacao.setIdCaronaPendente(System.currentTimeMillis());
				
		List<CaronaPendente> caronasSolicitadas = getCaronasSolicitadas(idCarona);
		
		if(!caronasSolicitadas.isEmpty()){
			totalPassageirosESolicitantes += caronasSolicitadas.size();
			
			if(!(totalPassageirosESolicitantes < carona.getMaximoPasageiros())){
				return Response.serverError().entity("Todos os lugares já foram solicitados").build();
			}
			
			for(CaronaPendente caronaSolicitada : caronasSolicitadas){
				if(caronaSolicitada.getSolicitante().equals(idUsuario)){
					return Response.serverError().entity("Já existe solicitação do usuário para esta carona").build();
				}
			}
		}
		
		caronasPendentes.put(solicitacao.getIdCaronaPendente(), solicitacao);
>>>>>>> refs/heads/desenv
		
		return Response.ok("Solicitação incluída com sucesso").build();
	}
	
	private List<CaronaPendente> getCaronasSolicitadas(final long idCarona){
		List<CaronaPendente> retorno = new ArrayList<CaronaPendente>();
		for(Entry<Long, CaronaPendente>mapEntry : caronasPendentes.entrySet()){
			if(mapEntry.getValue().getIdCarona() == idCarona){
				retorno.add(mapEntry.getValue());
			}
		}
		return retorno;
	}
	
	@GET
	@Path("/listaSolicitacoes/{idAprovador}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listarSolicitacoes(@PathParam("idAprovador") final String idAprovador){

		List<CaronaPendente>retorno = new ArrayList<CaronaPendente>();
		
		for(Entry<Long,CaronaPendente> mapEntry : caronasPendentes.entrySet()){
			CaronaPendente caronaPendente = mapEntry.getValue();
			if(caronaPendente.getAprovador().equals(idAprovador)){
					retorno.add(caronaPendente);
			}
		}
		
		return Response.ok(retorno).build();
	}
	
	@GET
	@Path("/listaAguardandoAprovacao/{idSolicitante}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listarAguardandoAprovacao(@PathParam("idSolicitante") final String idSolicitante){

		List<CaronaPendente>retorno = new ArrayList<CaronaPendente>();
		
		for(Entry<Long,CaronaPendente> mapEntry : caronasPendentes.entrySet()){
			CaronaPendente caronaPendente = mapEntry.getValue();
			if(caronaPendente.getSolicitante().equals(idSolicitante)){
					retorno.add(caronaPendente);
			}
		}
		
		return Response.ok(retorno).build();
	}
	
	@POST
	@Path("/despacharSolicitacao/{idCaronaPendente}/{aprovado}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response despacharSolicitacao(@PathParam("idCaronaPendente") final long idCaronaPendente,
			@PathParam("aprovado")final boolean aprovado){
		
		String mensagemRetorno = "Solicitação de carona removida";
		CaronaPendente caronaDespachada = caronasPendentes.get(idCaronaPendente);
		if(aprovado){
			Usuario caroneiroNovo = UsuarioResource.getUsuario(caronaDespachada.getSolicitante());
			Carona carona = caronas.get(caronaDespachada.getIdCarona());
			carona.incluiCaroneiro(caroneiroNovo);
			mensagemRetorno = caroneiroNovo.getNome() + " incluído na carona";
		}
		
		caronasPendentes.remove(idCaronaPendente);
				
		return Response.ok(mensagemRetorno).build();
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
