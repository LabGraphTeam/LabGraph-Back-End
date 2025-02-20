package leonardo.labutilities.qualitylabpro.configs.pagination;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

public class CustomPageableResolver extends PageableHandlerMethodArgumentResolver {
    private static final SortHandlerMethodArgumentResolver DEFAULT_SORT_RESOLVER =
            new SortHandlerMethodArgumentResolver() {
                @Override
                public @NonNull Sort resolveArgument(@NonNull MethodParameter parameter,
                        @Nullable ModelAndViewContainer mavContainer,
                        @NonNull NativeWebRequest webRequest,
                        @Nullable WebDataBinderFactory binderFactory) {

                    Sort originalSort = super.resolveArgument(parameter, mavContainer, webRequest,
                            binderFactory);
                    return Sort.by(originalSort.stream().map(order -> {
                        if ("date".equals(order.getProperty())) {
                            return new Sort.Order(order.getDirection(), "measurementDate");
                        }
                        return order;
                    }).toList());
                }
            };

    private final SortArgumentResolver sortResolver;

    public CustomPageableResolver() {
        this.sortResolver = DEFAULT_SORT_RESOLVER;
    }

    @Override
    public @NonNull Pageable resolveArgument(@NonNull MethodParameter methodParameter,
            @Nullable ModelAndViewContainer mavContainer, @NonNull NativeWebRequest webRequest,
            @Nullable WebDataBinderFactory binderFactory) {

        String page = webRequest
                .getParameter(getParameterNameToUse(getPageParameterName(), methodParameter));
        String pageSize = webRequest
                .getParameter(getParameterNameToUse(getSizeParameterName(), methodParameter));

        Sort sort = sortResolver.resolveArgument(methodParameter, mavContainer, webRequest,
                binderFactory);
        Pageable pageable = getPageable(methodParameter, page, pageSize);

        if (!sort.isSorted()) {
            return pageable;
        }

        return pageable.isPaged()
                ? PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort)
                : Pageable.unpaged(sort);
    }
}
