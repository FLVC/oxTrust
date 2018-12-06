package org.gluu.oxtrust.api.oxauth;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.gluu.oxtrust.api.configuration.oxauth.OxAuthConfig;
import org.gluu.oxtrust.api.errors.ApiError;
import org.gluu.oxtrust.service.config.oxauth.OxAuthConfigurationService;
import org.gluu.oxtrust.util.OxTrustApiConstants;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path(OxTrustApiConstants.BASE_API_URL + "/configurations/oxauth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = OxTrustApiConstants.BASE_API_URL + "/configurations/oxauth", description = "oxAuth configuration webservice")
public class OxAuthConfigurationWebService {

    @Inject
    private OxAuthConfigurationService oxAuthConfigurationService;

    @GET
    @ApiOperation(value = "Get the existing configuration")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, response = OxAuthConfig.class, message = "Success"),
                    @ApiResponse(code = 500, message = "Server error")
            }
    )
    public Response read() throws IOException {
        OxAuthConfig oxAuthConfig = oxAuthConfigurationService.find();
        return Response.ok(oxAuthConfig).build();
    }

    @PUT
    @ApiOperation(value = "Update the oxAuth configuration")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, response = OxAuthConfig.class, message = "Success"),
                    @ApiResponse(code = 400, response = ApiError.class, message = "invalid configuration"),
                    @ApiResponse(code = 500, message = "Server error")
            }
    )
    public Response update(@Valid OxAuthConfig oxAuthConfig) throws IOException {
        oxAuthConfigurationService.save(oxAuthConfig);
        return read();
    }

}
