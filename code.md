#### PredicateUtils from ControllerTemplate
``` java
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.*;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.*;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author zhihai
 **/
public class PredicateUtils {


    public static Predicate buildPredicate(Function<Builder, Builder> f) {
        Builder builder = f.apply(new Builder());
        return ExpressionUtils.allOf(builder.predicates);
    }

    public static <T extends EntityPathBase<?>> Predicate buildPredicate(Class<T> qClass, BiFunction<T, Builder, Builder> f) {
        T q = null;
        for (Field field : qClass.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && field.getType().equals(qClass)) {
                try {
                    q = qClass.cast(field.get(null));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

            }
        }
        Builder builder = f.apply(q, new Builder());

        Predicate predicate = ExpressionUtils.allOf(builder.predicates);
        if (predicate == null) {
            predicate = Expressions.asBoolean(true);
        }
        return predicate;
    }

    public static class Builder {

        private static boolean isEmptyString(Object value) {
            return (value instanceof String && ((String) value).isEmpty());
        }

        final List<Predicate> predicates = new ArrayList<>();

        private ZoneOffset zoneOffset;

        Builder() {}

        public Builder withTimeZone(TimeZone timeZone) {
            Instant instant = Instant.now();
            this.zoneOffset = timeZone.toZoneId().getRules().getOffset(instant);
            return this;
        }

        public Builder withZoneOffset(ZoneOffset zoneOffset) {
            this.zoneOffset = zoneOffset;
            return this;
        }

        public <T> Builder bind(@Nullable T value, Function<T, Predicate> toPredicate) {
            if (value == null) return this;
            if (isEmptyString(value)) return this;
            predicates.add(toPredicate.apply(value));
            return this;
        }

        public <T1, T2> Builder bind(@Nullable T1 value1,
                                     @Nullable T2 value2,
                                     BiFunction<T1, T2, Predicate> toPredicate) {
            if (value1 == null || value2 == null) return this;
            if (isEmptyString(value1) || isEmptyString(value2)) return this;
            predicates.add(toPredicate.apply(value1, value2));
            return this;
        }


        public <T> Builder bind(@Nullable T value, SimpleExpression<T> expression) {
            if (value == null) return this;
            if (isEmptyString(value)) return this;
            predicates.add(expression.eq(value));
            return this;
        }

        public Builder bind(@Nullable LocalDate date, LocalTime time, Function<OffsetDateTime, Predicate> toPredicate) {
            if (zoneOffset == null) {
                zoneOffset = getDefaultZoneOffset();
            }
            if (date == null) return this;
            predicates.add(toPredicate.apply(OffsetDateTime.of(date, time, zoneOffset)));
            return this;
        }

        private ZoneOffset getDefaultZoneOffset() {
            TimeZone zone = TimeZone.getDefault();
            Instant instant = Instant.now();
            return zone.toZoneId().getRules().getOffset(instant);
        }


    }

}
```