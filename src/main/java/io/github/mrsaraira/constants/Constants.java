package io.github.mrsaraira.constants;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Utility class to operation on constants and containers.
 *
 * @author Takhsin Saraira
 * @see Constant
 * @see RelationConstant
 * @see RelationConstant
 * @see RelationConstantContainer
 */
@UtilityClass
public final class Constants {

    /**
     * Create a {@link Constant} of some value.
     *
     * @param value constant value
     * @param <T>   value type
     * @return constant of a value
     */
    public static <T> Constant<T> of(T value) {
        return new ConstantImpl<>(value);
    }

    /**
     * Concatenate constant values.
     *
     * @param <T> values type
     * @return constants array
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T> Constant<T>[] concat(@NonNull T... values) {
        return Stream.of(values)
                .map(ConstantImpl::new)
                .toArray(ConstantImpl[]::new);
    }

    /**
     * Concatenate relation constants.
     *
     * @param <V> constant value type
     * @param <R> relation constants value type
     * @param <C> array of relation constants
     * @return array of relation constants
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <V, R, C extends RelationConstant<V, R>> RelationConstant<V, R>[] concat(@NonNull C... relationConstants) {
        return Stream.of(relationConstants)
                .map(relationConstant -> Constants.of(relationConstant.getKey(), relationConstant.getRelations()))
                .toArray(RelationConstant[]::new);
    }

    /**
     * Collect any values into collection supplied.
     *
     * @param collectionSupplier collection supplier
     * @param any                any values
     * @param <T>                values type
     * @return collection of any's
     */
    @SafeVarargs
    public static <T> Collection<T> collect(@NonNull Supplier<Collection<T>> collectionSupplier, @NonNull T... any) {
        return Stream.of(any).collect(Collectors.toCollection(collectionSupplier));
    }

    /**
     * Create a {@link RelationConstant} with its relations.
     *
     * @param value          constant value
     * @param relationValues constant relation values
     * @param <L>            constant value type
     * @param <R>            constant relation values type
     * @return relation constant
     */
    @SafeVarargs
    public static <L, R> RelationConstant<L, R> of(@NonNull L value, @NonNull R... relationValues) {
        return new RelationConstantImpl<>(new ConstantImpl<>(value), Set.of(Constants.concat(relationValues)));
    }

    @SuppressWarnings("unchecked")
    public static <T> Constant<T>[] constantsToArray(@NonNull Collection<Constant<T>> constants) {
        return Constants.toArray(constants);
//        return constants.toArray(new Constant[0]);
    }

    @SuppressWarnings("unchecked")
    public static <L, R> RelationConstant<L, R>[] relationConstantsToArray(@NonNull Collection<RelationConstant<L, R>> constants) {
        return Constants.toArray(constants);
//        return constants.toArray(new RelationConstant[0]);
    }

    @SuppressWarnings("unchecked")
    public static <T, C> T[] toArray(@NonNull Collection<T> constants) {
        return (T[]) constants.toArray();
    }

    // ------------------------------ Test ------------------------------ //

    @SafeVarargs
    public static <T> boolean anyValue(T key, @NonNull Constant<T>... constant) {
        return Stream.of(constant).anyMatch(c -> Objects.equals(c.getValue(), key));
    }

    public static <T> boolean anyValue(T key, @NonNull Collection<Constant<T>> constant) {
        return constant.stream().anyMatch(c -> Objects.equals(c.getValue(), key));
    }

    @SafeVarargs
    public static <T> boolean anyValue(Predicate<Constant<T>> condition, @NonNull Constant<T>... constant) {
        return Stream.of(constant).anyMatch(condition);
    }

    @SafeVarargs
    public static <R, C extends RelationConstantContainer<?, R>> boolean anyRelation(R value, @NonNull C... containers) {
        return Stream.of(containers)
                .map(container -> container.getRelationValues())
                .flatMap(Collection::stream)
                .anyMatch(relations -> relations.contains(value));
    }

    @SafeVarargs
    public static <R, C extends RelationConstant<?, R>> boolean anyRelation(R value, @NonNull C... constants) {
        return Stream.of(constants).anyMatch(constant -> getRelationValues(constant).contains(value));
    }

    // ------------------------------ Getters ------------------------------ //

    public static <T, C extends ConstantContainer<T>> Optional<T> getKeyValue(T key, @NonNull Class<C> containerClass) {
        return Constants.<T, C>getConstantTypeKeys(containerClass).stream()
                .filter(constantValue -> Objects.equals(constantValue, key))
                .findFirst();
    }

    public static <T, C extends ConstantContainer<T>> Optional<T> getKeyValue(@NonNull Predicate<T> condition, @NonNull Class<C> containerClass) {
        return Constants.<T, C>getConstantTypeKeys(containerClass).stream()
                .filter(condition)
                .findFirst();
    }

    public static <R> Collection<R> getRelationValues(@NonNull RelationConstant<?, R> constant) {
        return constant.getRelations().stream().map(Inner::mapToValueFunction).collect(Collectors.toUnmodifiableSet());
    }

    public static <R, C extends RelationConstantContainer<?, R>> Collection<Collection<R>> getConstantTypeRelationValues(@NonNull Class<C> type) {
        return Inner.getConstantTypeRelationValues(getInstance(type));
    }

    public static <T, C extends ConstantContainer<T>> Set<T> getConstantTypeKeys(@NonNull Class<C> type) {
        return Inner.getConstantTypeKeys(getInstance(type));
    }


    public static <T extends ConstantContainer<?>> T getInstance(@NonNull Class<T> type) {
        return (T) Inner.getInstance(type);
    }


    @UtilityClass
    class Inner {

        private final Map<Class<?>, ConstantContainer<?>> CONSTANTS_CACHE = new HashMap<>();

        static <T, C extends ConstantContainer<T>> Set<T> getConstantTypeKeys(C constantContainer) {
            if (constantContainer == null) {
                return Collections.emptySet();
            }

            saveToCache(constantContainer);

            return constantContainer.getKeys().stream()
                    .map(Inner::mapToValueFunction)
                    .collect(Collectors.toUnmodifiableSet());
        }

        static <R, C extends RelationConstantContainer<?, R>> List<Collection<R>> getConstantTypeRelationValues(C relationConstantContainer) {
            if (relationConstantContainer == null) {
                return Collections.emptyList();
            }

            saveToCache(relationConstantContainer);


            return relationConstantContainer.getRelations().stream()
                    .map(relationConstant -> relationConstant.getRelations().stream().map(Inner::mapToValueFunction).collect(Collectors.toUnmodifiableList()))
                    .collect(Collectors.toUnmodifiableList());
        }

        private static <R, T extends Constant<R>> R mapToValueFunction(T constant) {
            return constant.getValue();
        }

        @SafeVarargs
        private static <T> Collection<T> collect(@NonNull Supplier<Collection<T>> collectionSupplier, @NonNull T... any) {
            return Stream.of(any).collect(Collectors.toCollection(collectionSupplier));
        }

        private static void saveToCache(ConstantContainer<?> constantContainer) {
            CONSTANTS_CACHE.putIfAbsent(constantContainer.getClass(), constantContainer);
        }


        @SuppressWarnings("unchecked")
        public static <T extends ConstantContainer<?>> T getInstance(@NonNull Class<T> type) {
            return (T) CONSTANTS_CACHE.computeIfAbsent(type, clazz -> createInstance(type));
        }

        private static <T extends ConstantContainer<?>> T createInstance(@NonNull Class<T> type) {
            try {
                var constructor = type.getDeclaredConstructor();
                constructor.setAccessible(true);
                return constructor.newInstance();
            } catch (Exception e) {
                throw new IllegalStateException(String.format("Cannot instantiate constant class of type: %s", type), e);
            }
        }

    }

}
