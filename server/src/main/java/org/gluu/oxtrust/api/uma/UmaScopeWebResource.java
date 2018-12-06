package org.gluu.oxtrust.api.uma;

import java.util.List;

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

import com.google.common.base.Preconditions;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.gluu.oxtrust.api.openidconnect.BaseWebResource;
import org.gluu.oxtrust.ldap.service.uma.ScopeDescriptionService;
import org.gluu.oxtrust.util.OxTrustApiConstants;
import org.slf4j.Logger;
import org.xdi.oxauth.model.uma.persistence.UmaScopeDescription;

import com.wordnik.swagger.annotations.ApiOperation;

@Path(OxTrustApiConstants.BASE_API_URL + OxTrustApiConstants.UMA + OxTrustApiConstants.SCOPES)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = OxTrustApiConstants.BASE_API_URL +OxTrustApiConstants.UMA + OxTrustApiConstants.SCOPES, description = "Uma scope webservice")
public class UmaScopeWebResource extends BaseWebResource {

	@Inject
	private Logger logger;

	@Inject
	private ScopeDescriptionService scopeDescriptionService;

	@GET
	@ApiOperation(value = "Get uma scopes")
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, response = UmaScopeDescription[].class, message = "Success"),
					@ApiResponse(code = 500, message = "Server error")
			}
	)
	public Response listUmaScopes() {
		try {
			List<UmaScopeDescription> umaScopeDescriptions = scopeDescriptionService.getAllScopeDescriptions(100);
			return Response.ok(umaScopeDescriptions).build();
		} catch (Exception e) {
			log(logger, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GET
	@Path(OxTrustApiConstants.SEARCH)
	@ApiOperation(value = "Search uma scopes")
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, response = UmaScopeDescription[].class, message = "Success"),
					@ApiResponse(code = 500, message = "Server error")
			}
	)
	public Response searchUmaScopes(@QueryParam(OxTrustApiConstants.SEARCH_PATTERN) @NotNull String pattern) {
		try {
			List<UmaScopeDescription> scopes = scopeDescriptionService.findScopeDescriptions(pattern, 100);
			return Response.ok(scopes).build();
		} catch (Exception e) {
			log(logger, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GET
	@Path(OxTrustApiConstants.INUM_PARAM_PATH)
	@ApiOperation(value = "Get a uma scope by inum")
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, response = UmaScopeDescription.class, message = "Success"),
					@ApiResponse(code = 404, message = "Not found"),
					@ApiResponse(code = 500, message = "Server error")
			}
	)
	public Response getUmaScopeByInum(@PathParam(OxTrustApiConstants.INUM) @NotNull String inum) {
		try {
			Preconditions.checkNotNull(inum, "inum should not be null");
			UmaScopeDescription scope = scopeDescriptionService.getUmaScopeByInum(inum);
			if (scope != null) {
				return Response.ok(scope).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
		} catch (Exception e) {
			log(logger, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@ApiOperation(value = "Add new uma scope")
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, response = UmaScopeDescription.class, message = "Success"),
					@ApiResponse(code = 500, message = "Server error")
			}
	)
	public Response createUmaScope(UmaScopeDescription umaScopeDescription) {
		try {
			Preconditions.checkNotNull(umaScopeDescription, "Attempt to create null uma scope");
			String inum = scopeDescriptionService.generateInumForNewScopeDescription();
			umaScopeDescription.setDn(scopeDescriptionService.getDnForScopeDescription(inum));
			umaScopeDescription.setInum(inum);
			scopeDescriptionService.addScopeDescription(umaScopeDescription);
			return Response.ok(scopeDescriptionService.getUmaScopeByInum(inum)).build();
		} catch (Exception e) {
			log(logger, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PUT
	@ApiOperation(value = "Update uma scope")
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, response = UmaScopeDescription.class, message = "Success"),
					@ApiResponse(code = 404, message = "Not found"),
					@ApiResponse(code = 500, message = "Server error")
			}
	)
	public Response updateUmaScopeDescription(UmaScopeDescription umaScopeDescription) {
		String inum = umaScopeDescription.getInum();
		try {
			Preconditions.checkNotNull(inum, "inum should not be null");
			Preconditions.checkNotNull(umaScopeDescription, "Attempt to update null uma scope");
			UmaScopeDescription existingScope = scopeDescriptionService.getUmaScopeByInum(inum);
			if (existingScope != null) {
				umaScopeDescription.setDn(scopeDescriptionService.getDnForScopeDescription(inum));
				scopeDescriptionService.updateScopeDescription(umaScopeDescription);
				return Response.ok(scopeDescriptionService.getUmaScopeByInum(inum)).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
		} catch (Exception e) {
			log(logger, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@DELETE
	@Path(OxTrustApiConstants.INUM_PARAM_PATH)
	@ApiOperation(value = "Delete a uma scope")
	@ApiResponses(
			value = {
					@ApiResponse(code = 204, response = UmaScopeDescription.class, message = "Success"),
					@ApiResponse(code = 404, message = "Not found"),
					@ApiResponse(code = 500, message = "Server error")
			}
	)
	public Response deleteUmaScope(@PathParam(OxTrustApiConstants.INUM) @NotNull String inum) {
		try {
			UmaScopeDescription existingScope = scopeDescriptionService.getUmaScopeByInum(inum);
			if (existingScope != null) {
				scopeDescriptionService.removeScopeDescription(existingScope);
				return Response.noContent().build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
		} catch (Exception e) {
			log(logger, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

}
