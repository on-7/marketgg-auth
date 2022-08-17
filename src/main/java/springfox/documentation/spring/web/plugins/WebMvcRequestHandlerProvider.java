package springfox.documentation.spring.web.plugins;

import static java.util.stream.Collectors.toList;
import static springfox.documentation.builders.BuilderDefaults.nullToEmptyList;
import static springfox.documentation.spi.service.contexts.Orderings.byPatternsCondition;
import static springfox.documentation.spring.web.paths.Paths.ROOT;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.StreamSupport;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import springfox.documentation.RequestHandler;
import springfox.documentation.spi.service.RequestHandlerProvider;
import springfox.documentation.spring.web.OnServletBasedWebApplication;
import springfox.documentation.spring.web.WebMvcRequestHandler;
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;

/**
 * Spring Boot 2.7 이상의 버전에서 Swagger 설정을 위한 클래스.
 *
 * @author 윤동열
 * @see <a href="https://www.inflearn.com/questions/625844">참고 내용</a>
 * @see <a href="https://github.com/springfox/springfox/issues/3462#issuecomment-979548234">Spring Fox</a>
 * @see <a href="https://github.com/springfox/springfox/blob/master/springfox-spring-webmvc/src/main/java/springfox/documentation/spring/web/plugins/WebMvcRequestHandlerProvider.java">WebMvcRequestHandlerProvider.java</a>
 */
@Profile("local")
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Conditional(OnServletBasedWebApplication.class)
public class WebMvcRequestHandlerProvider implements RequestHandlerProvider {
    private final List<RequestMappingInfoHandlerMapping> handlerMappings;
    private final HandlerMethodResolver methodResolver;
    private final String contextPath;

    @Autowired
    public WebMvcRequestHandlerProvider(
        Optional<ServletContext> servletContext,
        HandlerMethodResolver methodResolver,
        List<RequestMappingInfoHandlerMapping> handlerMappings) {
        this.handlerMappings = handlerMappings.stream().filter(mapping -> mapping.getPatternParser() == null)
                                              .collect(toList());
        this.methodResolver = methodResolver;
        this.contextPath = servletContext
            .map(ServletContext::getContextPath)
            .orElse(ROOT);
    }

    @Override
    public List<RequestHandler> requestHandlers() {
        return nullToEmptyList(handlerMappings).stream()
                                               .filter(requestMappingInfoHandlerMapping ->
                                                   !("org.springframework.integration.http.inbound.IntegrationRequestMappingHandlerMapping"
                                                       .equals(requestMappingInfoHandlerMapping.getClass()
                                                                                               .getName()))
                                               )
                                               .map(toMappingEntries())
                                               .flatMap((entries -> StreamSupport.stream(entries.spliterator(), false)))
                                               .map(toRequestHandler())
                                               .sorted(byPatternsCondition())
                                               .collect(toList());
    }

    private Function<RequestMappingInfoHandlerMapping,
        Iterable<Map.Entry<RequestMappingInfo, HandlerMethod>>> toMappingEntries() {
        return input -> input.getHandlerMethods()
                             .entrySet();
    }

    private Function<Map.Entry<RequestMappingInfo, HandlerMethod>, RequestHandler> toRequestHandler() {
        return input -> new WebMvcRequestHandler(
            contextPath,
            methodResolver,
            input.getKey(),
            input.getValue());
    }

}
