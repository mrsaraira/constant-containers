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
     * @param value value
     * @param <T>   value type
     * @return a constant with value
     */
    public static <T> Constant<T> of(T value) {
        return new ConstantImpl<>(value);
    }

    /**
     * Create a {@link RelationConstant} with its relations.
     *
     * @param value          key value
     * @param relationValues relation values
     * @param <L>            value type
     * @param <R>            relation values type
     * @return relation constant
     */
    @SafeVarargs
    public static <L, R> RelationConstant<L, R> of(@NonNull L value, @NonNull R... relationValues) {
        return new RelationConstantImpl<>(new ConstantImpl<>(value), List.of(Constants.concat(relationValues)));
    }

    /**
     * Create a {@link RelationConstant} with its relations stored in supplied collection.
     *
     * @param value          key value
     * @param relationValues relation values
     * @param <L>            value type
     * @param <R>            relation values type
     * @return relation constant
     */
    @SafeVarargs
    public static <L, R> RelationConstant<L, R> of(@NonNull Supplier<Collection<Constant<R>>> collectionSupplier, @NonNull L value, @NonNull R... relationValues) {
        var collection = collectionSupplier.get();
        Objects.requireNonNull(collection);
        collection.addAll(List.of(Constants.concat(relationValues)));

        return new RelationConstantImpl<>(new ConstantImpl<>(value), Collections.unmodifiableCollection(collection));
    }

    /**
     * Concatenate constant values to array.
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
     * Concatenate relation constants to array.
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
     * Convert a collection to array.
     *
     * @param any any collection
     * @param <T> collection values type
     * @return array of any's
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(@NonNull Collection<T> any) {
        return (T[]) any.toArray();
    }

    /**
     * Check if any constants values match the passed value.
     *
     * @param value    value to match
     * @param constant constants to match
     * @param <T>      value type
     * @return true - if any constant value equals value parameter
     */
    @SafeVarargs
    public static <T> boolean anyValue(T value, @NonNull Constant<T>... constant) {
        return Stream.of(constant).anyMatch(c -> Objects.equals(c.getValue(), value));
    }

    /**
     * Check if any constants values match the passed value.
     *
     * @param value    value to match
     * @param constant collection of constants to match
     * @param <T>      value type
     * @return true - if any constant value in the collection equals value parameter
     */
    public static <T> boolean anyValue(T value, @NonNull Collection<Constant<T>> constant) {
        return constant.stream().anyMatch(c -> Objects.equals(c.getValue(), value));
    }

    /**
     * Check if any constant container has a constant values equal to value parameter.
     *
     * @param value      value to match
     * @param containers containers to match
     * @param <T>        values type
     * @return true - if any container has a constant value equal to value parameter
     */
    @SafeVarargs
    public static <T, C extends ConstantContainer<T>> boolean anyValue(T value, @NonNull C... containers) {
        return Stream.of(containers)
                .anyMatch(c -> c.getKeys().stream()
                        .map(Constant::getValue)
                        .anyMatch(t -> Objects.equals(t, value)));
    }

    /**
     * Check if any constant has a relation constant value equal to value parameter.
     *
     * @param value     value to match
     * @param constants relation constants to match
     * @param <R>       value type
     * @param <C>       relation container type
     * @return true - if any constant has a relation constant value equal to value parameter
     */
    @SafeVarargs
    public static <R, C extends RelationConstant<?, R>> boolean anyRelationValue(R value, @NonNull C... constants) {
        return Stream.of(constants).anyMatch(constant -> getRelationValues(constant).contains(value));
    }

    /**
     * Check if any relation constant container has a constant value equal to value parameter.
     *
     * @param value      value to match
     * @param containers relation containers to match
     * @param <R>        value type
     * @param <C>        relation container type
     * @return true - if any container has a constant value equal to value parameter
     */
    @SafeVarargs
    public static <R, C extends RelationConstantContainer<?, R>> boolean anyRelationValue(R value, @NonNull C... containers) {
        return Stream.of(containers)
                .map(container -> container.getAllRelationsValues())
                .flatMap(Collection::stream)
                .anyMatch(relations -> relations.contains(value));
    }

    /**
     * Search a constant value equal to value parameter in the container by the container class.
     *
     * @param value          value to match
     * @param containerClass container class
     * @param <T>            value type
     * @param <C>            constant container values type
     * @return optional of the constant value
     */
    public static <T, C extends ConstantContainer<T>> Optional<T> getKeyValue(T value, @NonNull Class<C> containerClass) {
        return Constants.getAllConstantValues(containerClass).stream()
                .filter(constantValue -> Objects.equals(constantValue, value))
                .findFirst();
    }

    /**
     * Search a constant value that matches the condition parameter in the container by the container class.
     *
     * @param condition      condition to match
     * @param containerClass container class
     * @param <T>            value type
     * @param <C>            constant container values type
     * @return optional of the constant value
     */
    public static <T, C extends ConstantContainer<T>> Optional<T> getKeyValue(@NonNull Predicate<T> condition, @NonNull Class<C> containerClass) {
        return Constants.getAllConstantValues(containerClass).stream()
                .filter(condition)
                .findFirst();
    }

    /**
     * Get a collection of all relation values from the relation constants.
     *
     * @param constants relation constants
     * @param <R>       relation constant value type
     * @return list of relation constants values
     */
    @SafeVarargs
    public static <R> List<R> getRelationValues(@NonNull RelationConstant<?, R>... constants) {
        return Arrays.stream(constants)
                .flatMap(rRelationConstant -> rRelationConstant.getRelations().stream())
                .map(Constant::getValue)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Get a collection of all constant values from the constants.
     *
     * @param constants constants
     * @param <T>       constant value type
     * @return list of constant values
     */
    @SafeVarargs
    public static <T> List<T> getValues(@NonNull Constant<T>... constants) {
        return Arrays.stream(constants)
                .map(Constant::getValue)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns an ordered list of all the relation values from the relation constant container by its type for each constant key.
     *
     * @param type relation constant container class
     * @param <R>  containers relation constant value type
     * @param <C>  relation constant container with relations type R
     * @return ordered list of relation constant values for each constant key of the container
     */
    public static <R, C extends RelationConstantContainer<?, R>> List<Collection<R>> getAllRelationsValues(@NonNull Class<C> type) {
        return Inner.getAllRelationsValues(getInstance(type));
    }

    /**
     * Returns a set of all constant values from the constant container by its type.
     *
     * @param type constant container
     * @param <T>  constant value type
     * @param <C>  constant container with a value of type T
     * @return set of constant values of the container
     */
    public static <T, C extends ConstantContainer<T>> Set<T> getAllConstantValues(@NonNull Class<C> type) {
        return Inner.getAllConstantsValues(getInstance(type));
    }

    /**
     * Get constant container instance by class. Internally the instances are cached thus every next call will be instant.
     * <br><b>Note</b>: Anonymous classes are not supported.
     *
     * @param type container class
     * @param <T>  container class type
     * @return instance of the container class
     */
    public static <T extends ConstantContainer<?>> T getInstance(@NonNull Class<T> type) {
        return (T) Inner.getInstance(type);
    }

    @UtilityClass
    class Inner {

        private final Map<Class<?>, ConstantContainer<?>> CONSTANTS_CACHE = new HashMap<>();

        static <T, C extends ConstantContainer<T>> Set<T> getAllConstantsValues(C constantContainer) {
            if (constantContainer == null) {
                return Collections.emptySet();
            }

            saveToCache(constantContainer);

            return constantContainer.getKeys().stream()
                    .map(Constant::getValue)
                    .collect(Collectors.toUnmodifiableSet());
        }

        static <R, C extends RelationConstantContainer<?, R>> List<Collection<R>> getAllRelationsValues(C relationConstantContainer) {
            if (relationConstantContainer == null) {
                return Collections.emptyList();
            }

            saveToCache(relationConstantContainer);


            return relationConstantContainer.getRelations().stream()
                    .map(relationConstant -> relationConstant.getRelations().stream().map(Constant::getValue).collect(Collectors.toUnmodifiableList()))
                    .collect(Collectors.toUnmodifiableList());
        }

        private static void saveToCache(ConstantContainer<?> constantContainer) {
            CONSTANTS_CACHE.putIfAbsent(constantContainer.getClass(), constantContainer);
        }


        @SuppressWarnings("unchecked")
        private static <T extends ConstantContainer<?>> T getInstance(@NonNull Class<T> type) {
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
