package org.gluu.oxtrust.api.server.api.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gluu.oxauth.model.uma.persistence.UmaResource;
import org.gluu.oxtrust.api.server.util.ApiConstants;
import org.gluu.oxtrust.ldap.service.ClientService;
import org.gluu.oxtrust.ldap.service.uma.ResourceSetService;
import org.gluu.oxtrust.ldap.service.uma.UmaScopeService;
import org.gluu.oxtrust.model.OxAuthClient;
import org.gluu.oxtrust.service.filter.ProtectedApi;
import org.oxauth.persistence.model.Scope;
import org.slf4j.Logger;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path(ApiConstants.BASE_API_URL + ApiConstants.UMA + ApiConstants.RESOURCES)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class UmaResourceWebResource extends BaseWebResource {

	@Inject
	private Logger logger;

	@Inject
	private ResourceSetService umaResourcesService;

	@Inject
	private UmaScopeService scopeDescriptionService;

	@Inject
	private ClientService clientService;

	@GET
	@ApiOperation(value = "Get uma resources")
	@ApiResponses(value = { @ApiResponse(code = 200, response = UmaResource[].class, message = "Success"),
			@ApiResponse(code = 500, message = "Server error") })
	@ProtectedApi(scopes = { READ_ACCESS })
	public Response listUmaResources() {
		try {
			log(logger, "Get uma resources");
			return Response.ok(umaResourcesService.getAllResources(1000)).build();
		} catch (Exception e) {
			log(logger, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GET
	@Path(ApiConstants.SEARCH)
	@ApiOperation(value = "Search uma resources")
	@ApiResponses(value = { @ApiResponse(code = 200, response = UmaResource[].class, message = "Success"),
			@ApiResponse(code = 500, message = "Server error") })
	@ProtectedApi(scopes = { READ_ACCESS })
	public Response searchUmaResources(@QueryParam(ApiConstants.SEARCH_PATTERN) @NotNull String pattern,
			@QueryParam(ApiConstants.SIZE) @NotNull int size) {
		try {
			log(logger, "Search uma resources with pattern = " + pattern + " and size = " + size);
			List<UmaResource> ressources = umaResourcesService.findResources(pattern, size);
			return Response.ok(ressources).build();
		} catch (Exception e) {
			log(logger, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GET
	@Path(ApiConstants.ID_PARAM_PATH)
	@ApiOperation(value = "Get a uma resource by id")
	@ApiResponses(value = { @ApiResponse(code = 200, response = UmaResource.class, message = "Success"),
			@ApiResponse(code = 500, message = "Server error") })
	@ProtectedApi(scopes = { READ_ACCESS })
	public Response getUmaResourceById(@PathParam(ApiConstants.ID) @NotNull String id) {
		try {
			log(logger, "Get uma resource by id " + id);
			Objects.requireNonNull(id, "id should not be null");
			List<UmaResource> resources = umaResourcesService.findResourcesById(id);
			if (resources != null && !resources.isEmpty()) {
				return Response.ok(resources.get(0)).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
		} catch (Exception e) {
			log(logger, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GET
	@Path(ApiConstants.ID_PARAM_PATH + ApiConstants.CLIENTS)
	@ApiOperation(value = "Get clients of uma resource")
	@ProtectedApi(scopes = { READ_ACCESS })
	public Response getUmaResourceClients(@PathParam(ApiConstants.ID) @NotNull String id) {
		try {
			log(logger, "Get clients of uma resource having id " + id);
			Objects.requireNonNull(id, "id should not be null");
			List<UmaResource> resources = umaResourcesService.findResourcesById(id);
			if (resources != null && !resources.isEmpty()) {
				UmaResource resource = resources.get(0);
				List<String> clientsDn = resource.getClients();
				List<OxAuthClient> clients = new ArrayList<OxAuthClient>();
				if (clientsDn != null) {
					for (String clientDn : clientsDn) {
						clients.add(clientService.getClientByDn(clientDn));
					}
				}
				return Response.ok(clients).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
		} catch (Exception e) {
			log(logger, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GET
	@Path(ApiConstants.ID_PARAM_PATH + ApiConstants.SCOPES)
	@ApiOperation(value = "Get scopes of uma resource")
	@ProtectedApi(scopes = { READ_ACCESS })
	public Response getUmaResourceScopes(@PathParam(ApiConstants.ID) @NotNull String id) {
		try {
			log(logger, "Get scopes of uma resource having id " + id);
			Objects.requireNonNull(id, "id should not be null");
			List<UmaResource> resources = umaResourcesService.findResourcesById(id);
			if (resources != null && !resources.isEmpty()) {
				UmaResource resource = resources.get(0);
				List<String> scopesDn = resource.getScopes();
				List<Scope> scopes = new ArrayList<Scope>();
				if (scopesDn != null) {
					for (String scopeDn : scopesDn) {
						scopes.add(scopeDescriptionService.getUmaScopeByDn(scopeDn));
					}
				}
				return Response.ok(scopes).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
		} catch (Exception e) {
			log(logger, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@ApiOperation(value = "add client to uma resource")
	@ApiResponses(value = { @ApiResponse(code = 200, response = UmaResource.class, message = "Success"),
			@ApiResponse(code = 500, message = "Server error") })
	@Path(ApiConstants.ID_PARAM_PATH + ApiConstants.CLIENTS + ApiConstants.INUM_PARAM_PATH)
	@ProtectedApi(scopes = { WRITE_ACCESS })
	public Response addClientToUmaResource(@PathParam(ApiConstants.ID) @NotNull String id,
			@PathParam(ApiConstants.INUM) @NotNull String clientInum) {
		try {
			log(logger, "Add client " + clientInum + " to uma resource " + id);
			Objects.requireNonNull(id, "Uma id should not be null");
			Objects.requireNonNull(clientInum, "Client inum should not be null");
			List<UmaResource> resources = umaResourcesService.findResourcesById(id);
			OxAuthClient client = clientService.getClientByInum(clientInum);
			if (resources != null && !resources.isEmpty() && client != null) {
				UmaResource umaResource = resources.get(0);
				List<String> clientsDn = new ArrayList<String>();
				if (umaResource.getClients() != null) {
					clientsDn.addAll(umaResource.getClients());
				}
				clientsDn.add(clientService.getDnForClient(clientInum));
				umaResource.setClients(clientsDn);
				umaResourcesService.updateResource(umaResource);
				return Response.ok(umaResourcesService.findResourcesById(id).get(0)).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
		} catch (Exception e) {
			log(logger, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@DELETE
	@ApiOperation(value = "Remove client from uma resource")
	@ApiResponses(value = { @ApiResponse(code = 200, response = UmaResource.class, message = "Success"),
			@ApiResponse(code = 500, message = "Server error") })
	@Path(ApiConstants.ID_PARAM_PATH + ApiConstants.CLIENTS + ApiConstants.INUM_PARAM_PATH)
	@ProtectedApi(scopes = { WRITE_ACCESS })
	public Response removeClientToUmaResource(@PathParam(ApiConstants.ID) @NotNull String id,
			@PathParam(ApiConstants.INUM) @NotNull String clientInum) {
		try {
			log(logger, "Remove client " + clientInum + " from uma resource " + id);
			Objects.requireNonNull(id, "Uma id should not be null");
			Objects.requireNonNull(clientInum, "Client inum should not be null");
			List<UmaResource> resources = umaResourcesService.findResourcesById(id);
			OxAuthClient client = clientService.getClientByInum(clientInum);
			if (resources != null && !resources.isEmpty() && client != null) {
				UmaResource umaResource = resources.get(0);
				List<String> clientsDn = new ArrayList<String>();
				if (umaResource.getClients() != null) {
					clientsDn.addAll(umaResource.getClients());
				}
				clientsDn.remove(clientService.getDnForClient(clientInum));
				umaResource.setClients(clientsDn);
				umaResourcesService.updateResource(umaResource);
				return Response.ok(umaResourcesService.findResourcesById(id).get(0)).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
		} catch (Exception e) {
			log(logger, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@ApiOperation(value = "add scope to uma resource")
	@ApiResponses(value = { @ApiResponse(code = 200, response = UmaResource.class, message = "Success"),
			@ApiResponse(code = 500, message = "Server error") })
	@Path(ApiConstants.ID_PARAM_PATH + ApiConstants.SCOPES + ApiConstants.INUM_PARAM_PATH)
	@ProtectedApi(scopes = { WRITE_ACCESS })
	public Response addScopeToUmaResource(@PathParam(ApiConstants.ID) @NotNull String id,
			@PathParam(ApiConstants.INUM) @NotNull String scopeInum) {
		log(logger, "Add scope " + scopeInum + " to uma resource " + id);
		try {
			Objects.requireNonNull(id, "Uma id should not be null");
			Objects.requireNonNull(scopeInum, "scope inum should not be null");
			List<UmaResource> resources = umaResourcesService.findResourcesById(id);
			Scope umaScope = scopeDescriptionService.getUmaScopeByInum(scopeInum);
			if (resources != null && !resources.isEmpty() && umaScope != null) {
				UmaResource umaResource = resources.get(0);
				List<String> scopesDn = new ArrayList<String>();
				if (umaResource.getScopes() != null) {
					scopesDn.addAll(umaResource.getScopes());
				}
				scopesDn.add(scopeDescriptionService.getDnForScope(scopeInum));
				umaResource.setScopes(scopesDn);
				umaResourcesService.updateResource(umaResource);
				return Response.ok(umaResourcesService.findResourcesById(id).get(0)).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
		} catch (Exception e) {
			log(logger, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@DELETE
	@ApiOperation(value = "remove a scope from uma resource")
	@ApiResponses(value = { @ApiResponse(code = 200, response = UmaResource.class, message = "Success"),
			@ApiResponse(code = 500, message = "Server error") })
	@Path(ApiConstants.ID_PARAM_PATH + ApiConstants.SCOPES + ApiConstants.INUM_PARAM_PATH)
	@ProtectedApi(scopes = { WRITE_ACCESS })
	public Response removeScopeToUmaResource(@PathParam(ApiConstants.ID) @NotNull String id,
			@PathParam(ApiConstants.INUM) @NotNull String scopeInum) {
		try {
			log(logger, "Remove scope " + scopeInum + " from uma resource " + id);
			Objects.requireNonNull(id, "Uma id should not be null");
			Objects.requireNonNull(scopeInum, "scope inum should not be null");
			List<UmaResource> resources = umaResourcesService.findResourcesById(id);
			Scope umaScope = scopeDescriptionService.getUmaScopeByInum(scopeInum);
			if (resources != null && !resources.isEmpty() && umaScope != null) {
				UmaResource umaResource = resources.get(0);
				List<String> scopesDn = new ArrayList<String>();
				if (umaResource.getScopes() != null) {
					scopesDn.addAll(umaResource.getScopes());
				}
				scopesDn.remove(scopeDescriptionService.getDnForScope(scopeInum));
				umaResource.setScopes(scopesDn);
				umaResourcesService.updateResource(umaResource);
				return Response.ok(umaResourcesService.findResourcesById(id).get(0)).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
		} catch (Exception e) {
			log(logger, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@ApiOperation(value = "Add new uma resource")
	@ApiResponses(value = { @ApiResponse(code = 200, response = UmaResource.class, message = "Success"),
			@ApiResponse(code = 500, message = "Server error") })
	@ProtectedApi(scopes = { WRITE_ACCESS })
	public Response createUmaResource(UmaResource umaResource) {
		try {
			log(logger, "Add new uma resource");
			Objects.requireNonNull(umaResource, "Attempt to create null resource");
			if (umaResource.getId() == null) {
				umaResource.setId(UUID.randomUUID().toString());
			}
			String inum = umaResourcesService.generateInumForNewResource();
			umaResource.setDn(umaResourcesService.getDnForResource(umaResource.getId()));
			umaResource.setInum(inum);
			umaResourcesService.addResource(umaResource);
			List<UmaResource> umaResources = umaResourcesService.findResourcesById(umaResource.getId());
			return Response.ok(umaResources.get(0)).build();
		} catch (Exception e) {
			log(logger, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PUT
	@ApiOperation(value = "Update uma resource")
	@ApiResponses(value = { @ApiResponse(code = 200, response = UmaResource.class, message = "Success"),
			@ApiResponse(code = 500, message = "Server error") })
	@ProtectedApi(scopes = { WRITE_ACCESS })
	public Response updateUmaResource(UmaResource umaResource) {
		try {
			String id = umaResource.getId();
			log(logger, "Update uma resource having id " + id);
			Objects.requireNonNull(id, " id should not be null");
			Objects.requireNonNull(umaResource, "Attempt to update null uma resource");
			List<UmaResource> existingResources = umaResourcesService.findResourcesById(id);
			if (existingResources != null && !existingResources.isEmpty()) {
				umaResource.setDn(umaResourcesService.getDnForResource(id));
				umaResourcesService.updateResource(umaResource);
				return Response.ok(umaResourcesService.findResourcesById(id).get(0)).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
		} catch (Exception e) {
			log(logger, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@DELETE
	@Path(ApiConstants.ID_PARAM_PATH)
	@ApiOperation(value = "Delete a uma resource")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 500, message = "Server error") })
	@ProtectedApi(scopes = { WRITE_ACCESS })
	public Response deleteUmaResource(@PathParam(ApiConstants.ID) @NotNull String id) {
		try {
			log(logger, "Delete uma resource with id = " + id);
			List<UmaResource> resources = umaResourcesService.findResourcesById(id);
			if (resources != null && !resources.isEmpty()) {
				umaResourcesService.removeResource(resources.get(0));
				log(logger, "Delete a uma resource having id " + id + " done");
				return Response.ok().build();
			} else {
				log(logger, "No uma scope found with " + id);
				return Response.status(Response.Status.NOT_FOUND).build();
			}
		} catch (Exception e) {
			log(logger, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}
}