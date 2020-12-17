package org.recap.config;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author Sheik Sahib on 5/28/20
 */
abstract class SwaggerConfigBase implements WebMvcConfigurer {

    protected static final String ERROR_INFORMATION_CLASS = "ErrorInfo";
    protected static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
    protected static final String GONE_ERROR_MESSAGE = "Gone";
    protected static final String NOT_FOUND_ERROR_MESSAGE = "Not Found";
    protected static final String BAD_REQUEST_ERROR_MESSAGE = "Bad Request";

    private final ResponseMessage INTERNAL_SERVER_ERROR_RESPONSE_MESSAGE = setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), INTERNAL_SERVER_ERROR_MESSAGE);

    private final ResponseMessage GONE_ERROR_RESPONSE_MESSAGE = setResponseMessage(HttpStatus.GONE.value(), GONE_ERROR_MESSAGE);

    private final ResponseMessage NOT_FOUND_RESPONSE_MESSAGE = setResponseMessage(HttpStatus.NOT_FOUND.value(), NOT_FOUND_ERROR_MESSAGE);

    private final ResponseMessage BAD_REQUEST_RESPONSE_MESSAGE = setResponseMessage(HttpStatus.BAD_REQUEST.value(), BAD_REQUEST_ERROR_MESSAGE);


    public static String getBadRequestErrorMessage() {
        return BAD_REQUEST_ERROR_MESSAGE;
    }

    @Autowired
    private TypeResolver typeResolver;

    /**
     * Get the {@link Docket} API.
     *
     * @return Docket.
     */
    @Bean
    @Required
    protected abstract ApiInfo apiInfo();
    private ResponseMessage setResponseMessage(int messageCode, String messageValue)
    {
        return new ResponseMessageBuilder().code(messageCode).message(messageValue).responseModel(new ModelRef(ERROR_INFORMATION_CLASS)).build();
   }
}

