package br.iesb.carona.resource;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.iesb.carona.core.modelo.Usuario;


@Path("/usuarios")
public class UsuarioResource {
	
	private static final Map<String, Usuario> usuarios = new HashMap<String, Usuario>();
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/inclui")
	public Response cadastraCliente(Usuario usuario){
		
		if(!usuarios.containsKey(usuario.getEmail())){
			
			if(usuario.getEmail() == null || usuario.getEmail().isEmpty()){
				return Response.serverError().entity("Email é obrigatório").build();
			}
			usuarios.put(usuario.getEmail(), usuario);
			return Response.ok("Usuario incluído com sucesso").build();
		}
		
		return Response.serverError().entity("Usuário já existente ou inválido").build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/lista")
	public Response listaClientes(){
		
		return Response.ok(usuarios.values()).build();
		
	}

	public static Usuario getUsuario(String email){
		return usuarios.get(email);
	}
	

	
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/consulta/{id}")
	public Response consultaUsuario(@PathParam("id") final String email){
	
		if(usuarios.containsKey(email)){
			return Response.ok(usuarios.get(email)).build();
		}
		
		return Response.serverError().entity("Usuário inexistente").build();
	}
}
