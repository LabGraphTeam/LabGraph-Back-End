package leonardo.labutilities.qualitylabpro.utils.decoratos;

import leonardo.labutilities.qualitylabpro.providers.ApplicationContextProvider;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class InjectEmailListAspect {

    @Value("${EMAIL_TO_SEND_LIST}")
    private String emailListString;

    @Before("execution(* leonardo.labutilities.qualitylabpro..*(..)) && @annotation(org.springframework.stereotype.Service)")
    public void injectEmailList() throws IllegalAccessException {
        List<String> emailList = Arrays.asList(emailListString.split(","));
        for (Object bean : ApplicationContextProvider.getApplicationContext().getBeansWithAnnotation(Service.class).values()) {
            for (Field field : bean.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(InjectEmailList.class)) {
                    field.setAccessible(true);
                    field.set(bean, emailList);
                }
            }
        }
    }
}