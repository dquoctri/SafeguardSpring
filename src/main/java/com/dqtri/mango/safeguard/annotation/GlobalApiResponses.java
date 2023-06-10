package com.dqtri.mango.safeguard.annotation;

import com.dqtri.mango.safeguard.model.dto.response.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ProblemDetail;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "No Authentication found or expired",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "403", description = "Forbidden",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))})
})
public @interface GlobalApiResponses {
}
