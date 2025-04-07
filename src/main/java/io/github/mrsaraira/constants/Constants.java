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
     * @param value              key value
     * @param collectionSupplier collection supplier
     * @param relationValues     relation values
     * @param <L>                value type
     * @param <R>                relation values type
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
     * Concatenate values to array of constants.
     *
     * @param values constant values
     * @param <T>    values type
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
     * @param relationConstants relation constants
     * @param <V>               constant value type
     * @param <R>               relation constants value type
     * @param <C>               array of relation constants
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
     * Convert a collection to array.
     *
     * @param any   any collection
     * @param array array for storing
     * @param <T>   collection values type
     * @return array of any's
     */
    static <T> T[] toArray(@NonNull Collection<T> any, T[] array) {
        return any.toArray(array);
    }

    /**
     * Returns first constant with a value equal to value parameter.
     *
     * @param value     value to match
     * @param constants constants to match
     * @param <T>       value type
     * @return optional constant with constant value equal to value
     */
    @SafeVarargs
    public static <T> Optional<Constant<T>> match(T value, @NonNull Constant<T>... constants) {
        return Stream.of(constants)
                .filter(constant -> Objects.equals(constant.getValue(), value))
                .findFirst();
    }

    /**
     * Returns first relation constant with a relation values having a value equal to value parameter.
     *
     * @param relationValue relation value to match
     * @param constants     relation constants to match
     * @param <R>           relation value type
     * @return optional relation constant with relation constant having a value equal to value
     */
    @SafeVarargs
    public static <R> Optional<RelationConstant<?, R>> match(R relationValue, @NonNull RelationConstant<?, R>... constants) {
        return Stream.of(constants)
                .filter(constant -> Constants.anyValue(relationValue, constant.getRelations()))
                .findFirst();
    }

    /**
     * Returns first constant of a container having a constant value equal to value parameter.
     *
     * @param value      value to match
     * @param containers containers to match
     * @param <T>        value type
     * @return optional constant container with constant value equal to value
     */
    @SafeVarargs
    public static <T> Optional<Constant<T>> match(T value, @NonNull ConstantContainer<T>... containers) {
        return Stream.of(containers)
                .flatMap(container -> container.getAllKeys().stream())
                .filter(constant -> Constants.anyValue(value, constant))
                .findFirst();
    }

    /**
     * Returns first relation constant container having a containing a relation constant value equal to value parameter.
     *
     * @param relationValue value to match
     * @param containers    enums to match
     * @param <R>           relation value type
     * @return optional relation constant container with relation constant value having value equal to value
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <R> Optional<RelationConstantContainer<?, R>> match(R relationValue, @NonNull RelationConstantContainer<?, R>... containers) {
        return Stream.of(containers)
                .filter(container -> {
                    var array = Constants.toArray(container.getAllRelations(), new RelationConstant[0]);
                    return Constants.match(relationValue, (RelationConstant<?, R>[]) array).isPresent();
                })
                .findFirst();
    }

    /**
     * Returns first enumeration having constant value equal to value parameter.
     *
     * @param value value to match
     * @param enums enums to match
     * @param <T>   value type
     * @return optional enumeration with constant value equal to value
     */
    @SafeVarargs
    public static <T> Optional<EnumConstantContainer<T, ?>> match(T value, @NonNull EnumConstantContainer<T, ?>... enums) {
        return Stream.of(enums)
                .filter(enumeration -> Objects.equals(enumeration.getConstant().getValue(), value))
                .findFirst();
    }

    /**
     * Returns first enumeration having a relation constant value containing value equal to value parameter.
     *
     * @param relationValue relation value to match
     * @param enums         enums to match
     * @param <R>           relation value type
     * @return optional enumeration with relation constant value containing value equal to value parameter
     */
    @SafeVarargs
    public static <R> Optional<EnumRelationConstantContainer<?, R, ? extends Enum<?>>> match(R relationValue, @NonNull EnumRelationConstantContainer<?, R, ?>... enums) {
        return Stream.of(enums)
                .filter(enumeration -> Constants.match(relationValue, enumeration.getConstant().getRelations()).isPresent())
                .findFirst();
    }

    /**
     * Returns first relation constant with a key value equal to value parameter.
     *
     * @param value     key value to match
     * @param constants relation constants to match
     * @param <T>       constant constant value type
     * @param <R>       relation constant relations value type
     * @return optional relation constant with key value equal to value
     */
    @SafeVarargs
    public static <T, R> Optional<RelationConstant<T, R>> getRelationByKeyValue(T value, @NonNull RelationConstant<T, R>... constants) {
        return Stream.of(constants)
                .filter(constant -> Objects.equals(constant.getValue(), value))
                .findFirst();
    }

    /**
     * Returns first relation constant with a key value equal to value parameter.
     *
     * @param value               key value to match
     * @param constantsCollection relation constants collection to match
     * @param <T>                 constant constant value type
     * @param <R>                 relation constant relations value type
     * @return optional relation constant with key value equal to value
     */
    public static <T, R> Optional<RelationConstant<T, R>> getRelationByKeyValue(T value, @NonNull Collection<RelationConstant<T, R>> constantsCollection) {
        return constantsCollection.stream()
                .filter(constant -> Objects.equals(constant.getValue(), value))
                .findFirst();
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
        return match(value, constant).isPresent();
    }

    /**
     * Check if any constant container has a constant with value equal to value parameter.
     *
     * @param value      value to match
     * @param containers containers to match
     * @param <T>        values type
     * @param <C>        constant container with constant values type T
     * @return true - if any container has a constant with value equal to value parameter
     */
    @SafeVarargs
    public static <T, C extends ConstantContainer<T>> boolean anyValue(T value, @NonNull C... containers) {
        return Constants.match(value, containers).isPresent();
    }

    /**
     * Check if any enum constant containers has a constant value equal to value parameter.
     *
     * @param value value to match
     * @param enums enum constant container to match
     * @param <T>   values type
     * @param <C>   enum constant container with constant value type T
     * @return true - if any container has a constant value equal to value parameter
     */
    @SafeVarargs
    public static <T, C extends EnumConstantContainer<T, ?>> boolean anyValue(T value, @NonNull C... enums) {
        return Constants.match(value, enums).isPresent();
    }

    /**
     * Check if any relation constant has a relation constant value equal to value parameter.
     *
     * @param relationValue value to match
     * @param constants     relation constants to match
     * @param <R>           value type
     * @return true - if any constant has a relation constant value equal to value parameter
     */
    @SafeVarargs
    public static <R> boolean anyRelationValue(R relationValue, @NonNull RelationConstant<?, R>... constants) {
        return Constants.match(relationValue, constants).isPresent();
    }

    /**
     * Check if any relation constant container has a constant value equal to value parameter.
     *
     * @param relationValue value to match
     * @param containers    relation containers to match
     * @param <R>           value type
     * @param <C>           relation container type
     * @return true - if any container has a constant value equal to value parameter
     */
    @SafeVarargs
    public static <R, C extends RelationConstantContainer<?, R>> boolean anyRelationValue(R relationValue, @NonNull C... containers) {
        return Constants.match(relationValue, containers).isPresent();
    }

    /**
     * Check if any enum relation constant container has a relation constant value equal to value parameter.
     *
     * @param relationValue value to match
     * @param enums         enum relation containers to match
     * @param <R>           relation constant value type
     * @param <C>           enum relation container type
     * @return true - if any container has a constant value equal to value parameter
     */
    @SafeVarargs
    public static <R, C extends EnumRelationConstantContainer<?, R, ?>> boolean anyRelationValue(R relationValue, @NonNull C... enums) {
        return Constants.match(relationValue, enums).isPresent();
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
     * Get optional enumeration by constant value of some {@link EnumConstantContainer}.
     *
     * @param value          value to match
     * @param containerClass enum container class
     * @param <T>            value type
     * @param <C>            enum constant container
     * @return optional of the enum constant container
     */
    public static <T, C extends Enum<?> & EnumConstantContainer<T, ?>> Optional<C> getEnumByValue(T value, @NonNull Class<C> containerClass) {
        return Stream.of(Inner.getEnumValues(containerClass))
                .filter(enumEntry -> Objects.equals(enumEntry.getConstant().getValue(), value))
                .findFirst();
    }

    /**
     * Get a collection of all constant values from the constants.
     *
     * @param constants constants
     * @param <T>       constant value type
     * @return set of constant values
     */
    @SafeVarargs
    public static <T> Set<T> getValues(@NonNull Constant<T>... constants) {
        return Arrays.stream(constants)
                .map(Constant::getValue)
                .collect(Collectors.toUnmodifiableSet());
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
                .flatMap(rRelationConstant -> Arrays.stream(rRelationConstant.getRelations()))
                .map(Constant::getValue)
                .collect(Collectors.toUnmodifiableList());
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
     * Get all enumeration relation constant container relation values for each key by container class.
     *
     * @param type enum relation constant container class
     * @param <R>  relation values type
     * @param <C>  enum relation constant container type
     * @return collection of all relation constant values collections for each constant
     */
    public static <R, C extends Enum<?> & EnumRelationConstantContainer<?, R, ?>> List<Collection<R>> getAllEnumRelationsValues(@NonNull Class<C> type) {
        return Arrays.stream(Inner.getEnumValues(type))
                .map(enumEntry -> enumEntry.getConstant())
                .map(relationConstant -> Constants.getValues(relationConstant.getRelations()))
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Get all constant container constant values by container class.
     *
     * @param type enum constant container class
     * @param <T>  constant values type
     * @param <C>  enum constant container type
     * @return set of all constant values of the container
     */
    public static <T, C extends Enum<?> & EnumConstantContainer<T, ?>> Set<T> getAllEnumConstantValues(@NonNull Class<C> type) {
        return Arrays.stream(Inner.getEnumValues(type))
                .map(enumEntry -> enumEntry.getConstant())
                .map(Constant::getValue)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Get map of enum to key of some enum constant container.
     *
     * @param enumClass enum constant container class
     * @return map of enum-key of enumClass parameter
     */
    public static <T, E extends Enum<E> & EnumConstantContainer<T, E>> Map<E, T> getEnumKeysMap(Class<E> enumClass) {
        Map<E, T> keyRelationsMap = new HashMap<>();

        E[] enumConstants = Constants.Inner.getEnumValues(enumClass);

        for (E constant : enumConstants) {
            T key = constant.getConstant().getValue();

            keyRelationsMap.put(constant, key);
        }

        return keyRelationsMap;
    }

    /**
     * Get map of key to relations of some enum constant container.
     *
     * @param enumClass enum constant container class
     * @return map of key-relations of enumClass
     */
    public static <T, R, E extends Enum<E> & EnumRelationConstantContainer<T, R, E>> Map<T, Collection<R>> getKeysRelationsMap(Class<E> enumClass) {
        Map<T, Collection<R>> keyRelationsMap = new HashMap<>();

        E[] enumConstants = Constants.Inner.getEnumValues(enumClass);

        for (E constant : enumConstants) {
            T key = constant.getConstant().getValue();

            List<R> relations = Arrays.stream(constant.getConstant().getRelations())
                    .map(Constant::getValue)
                    .collect(Collectors.toList());

            keyRelationsMap.put(key, relations);
        }

        return keyRelationsMap;
    }

    /**
     * Get map of enum to relations of some enum constant container.
     *
     * @param enumClass enum constant container class
     * @return map of enum-relations of enumClass parameter
     */
    public static <T, R, E extends Enum<E> & EnumRelationConstantContainer<T, R, E>> Map<E, Collection<R>> getEnumRelationsMap(Class<E> enumClass) {
        Map<E, Collection<R>> keyRelationsMap = new HashMap<>();

        E[] enumConstants = Constants.Inner.getEnumValues(enumClass);

        for (E constant : enumConstants) {
            List<R> relations = Arrays.stream(constant.getConstant().getRelations())
                    .map(Constant::getValue)
                    .collect(Collectors.toList());

            keyRelationsMap.put(constant, relations);
        }

        return keyRelationsMap;
    }

    /**
     * Get constant container instance by class. Internally the instances are cached thus every next call will be instant.
     * <br><b>Note</b>: Anonymous and Enum classes are not supported.
     *
     * @param type container class
     * @param <T>  container class type
     * @return instance of the container class
     */
    public static <T extends ConstantContainer<?>> T getInstance(@NonNull Class<T> type) {
        return (T) Inner.getInstance(type);
    }

    @UtilityClass
    final class Inner {

        private final Map<Class<?>, ConstantContainer<?>> CONSTANTS_CACHE = new HashMap<>();
        private final Map<Class<?>, Object[]> ENUM_CONSTANTS_CACHE = new HashMap<>();

        static <T, C extends ConstantContainer<T>> Set<T> getAllConstantsValues(C constantContainer) {
            if (constantContainer == null) {
                return Collections.emptySet();
            }

            saveToCache(constantContainer);

            return constantContainer.getAllKeys().stream()
                    .map(Constant::getValue)
                    .collect(Collectors.toUnmodifiableSet());
        }

        static <R, C extends RelationConstantContainer<?, R>> List<Collection<R>> getAllRelationsValues(C relationConstantContainer) {
            if (relationConstantContainer == null) {
                return Collections.emptyList();
            }

            saveToCache(relationConstantContainer);

            return relationConstantContainer.getAllRelations().stream()
                    .map(relationConstant -> Arrays.stream(relationConstant.getRelations())
                            .map(Constant::getValue)
                            .collect(Collectors.toUnmodifiableList()))
                    .collect(Collectors.toUnmodifiableList());
        }

        @SuppressWarnings("unchecked")
        static <E extends Enum<?> & EnumConstantContainer<?, ?>> E[] getEnumValues(@NonNull Class<E> enumClass) {
            return (E[]) ENUM_CONSTANTS_CACHE.computeIfAbsent(enumClass, Class::getEnumConstants);
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
