package io.github.mrsaraira.constants;

import io.github.mrsaraira.constants.containers.AbstractConstantContainer;
import io.github.mrsaraira.constants.containers.AbstractRelationConstantContainer;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
@UtilityClass
public final class Constants {

    //  ------------------------------ Construct ------------------------------ //

    public static <T> Constant<T> of(T key) {
        return new ConstantImpl<>(key);
    }

    @SafeVarargs
    public static <L, R> RelationConstant<L, R> of(@NonNull L key, @NonNull R... relations) {
        return new RelationConstantImpl<L, R>(new ConstantImpl<>(key), Set.of(Constants.concat(relations)));
    }

    @SafeVarargs
    public static <L, R> RelationConstant<L, R> of(@NonNull Constant<L> key, @NonNull R... relations) {
        return new RelationConstantImpl<>(key, Set.of(Constants.concat(relations)));
    }

    @SafeVarargs
    public static <L, R> RelationConstant<L, R> of(@NonNull Constant<L> key, Constant<R>... relations) {
        return new RelationConstantImpl<>(key, Set.of(relations));
    }

    @SafeVarargs
    public static <T, R> RelationConstant<T, R> relate(@NonNull Constant<T> key, Constant<R>... relations) {
        return new RelationConstantImpl<>(key, Set.of(relations));
    }

    @SafeVarargs
    public static <T, R> RelationConstant<T, R> relate(@NonNull T key, R... relations) {
        return new RelationConstantImpl<>(new ConstantImpl<>(key), Set.of(Constants.concat(relations)));
    }

    @SafeVarargs
    public static <T, R> RelationConstantContainer<T, R> createRelationContainer(@NonNull T key, R... relations) {
        return new AbstractRelationConstantContainer<T, R>() {
            @Override
            protected List<RelationConstant<T, R>> initialConstants() {
                return List.of(
                        Constants.relate(key, relations)
                );
            }
        };
    }

    @SafeVarargs
    public static <T> ConstantContainer<T> createConstantContainer(@NonNull T... keys) {
        return new AbstractConstantContainer<T>() {
            @Override
            protected List<Constant<T>> initialConstants() {
                return List.of(Constants.concat(keys));
            }
        };
    }

    // ------------------------------ Transform ------------------------------ //
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T> Constant<T>[] concat(@NonNull T... constants) {
        return Stream.of(constants).map(ConstantImpl::new).toArray(ConstantImpl[]::new);
    }

    @SafeVarargs
    public static <T, R extends Constant<T>, C extends Collection<Constant<T>>> C concat(@NonNull Supplier<C> collectionSupplier, @NonNull T... constants) {
        return Stream.of(Constants.concat(constants)).collect(Collectors.toCollection(collectionSupplier));
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <L, R, C extends RelationConstant<L, R>> RelationConstant<L, R>[] concat(@NonNull C... relationConstants) {
        return Stream.of(relationConstants).map(c -> of(c.getKey(), c.getRelations())).toArray(RelationConstant[]::new);
    }

    @SafeVarargs
    public static <L, R, C extends Collection<RelationConstant<L, R>>> Collection<RelationConstant<L, R>> concat(@NonNull Supplier<C> collectionSupplier, @NonNull RelationConstant<L, R>... relationConstants) {
        return Stream.of(Constants.concat(relationConstants)).collect(Collectors.toCollection(collectionSupplier));
    }

    @SuppressWarnings("unchecked")
    public static <T> Constant<T>[] constantsToArray(@NonNull Collection<Constant<T>> constants) {
        return constants.toArray(new Constant[0]);
    }

    @SuppressWarnings("unchecked")
    public static <L, R> RelationConstant<L, R>[] relationConstantsToArray(@NonNull Collection<RelationConstant<L, R>> constants) {
        return constants.toArray(new RelationConstant[0]);
    }

    // ------------------------------ Test ------------------------------ //

    @SafeVarargs
    public static <T> boolean anyMatch(T key, @NonNull Constant<T>... constant) {
        return Stream.of(constant).anyMatch(c -> Objects.equals(c.getValue(), key));
    }

    public static <T> boolean anyMatch(T key, @NonNull Collection<Constant<T>> constant) {
        return constant.stream().anyMatch(c -> Objects.equals(c.getValue(), key));
    }

    @SafeVarargs
    public static <T> boolean anyMatch(Predicate<Constant<T>> condition, @NonNull Constant<T>... constant) {
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

    public static <T, C extends ConstantContainer<T>> Collection<T> getConstantTypeKeys(@NonNull Class<C> type) {
        return Inner.getConstantTypeKeys(getInstance(type));
    }


    public static <T extends ConstantContainer<?>> T getInstance(@NonNull Class<T> type) {
        return (T) Inner.getInstance(type);
    }


    @UtilityClass
    class Inner {

        private final Map<Class<?>, ConstantContainer<?>> CONSTANTS_CACHE = new HashMap<>();

        static <T, C extends ConstantContainer<T>> Collection<T> getConstantTypeKeys(C constantContainer) {
            if (constantContainer == null) {
                return Collections.emptyList();
            }

            saveToCache(constantContainer);

            return constantContainer.getKeys().stream()
                    .map(Inner::mapToValueFunction)
                    .collect(Collectors.toUnmodifiableSet());
        }

        static <R, C extends RelationConstantContainer<?, R>> Collection<Collection<R>> getConstantTypeRelationValues(C relationConstantContainer) {
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

        private static void saveToCache(ConstantContainer<?> constantContainer) {
            CONSTANTS_CACHE.putIfAbsent(constantContainer.getClass(), constantContainer);
        }


        @SuppressWarnings("unchecked")
        public static <T extends ConstantContainer<?>> T getInstance(@NonNull Class<T> type) {
            return (T) CONSTANTS_CACHE.computeIfAbsent(type, clazz -> createInstance(type));
        }

        private static <T extends ConstantContainer<?>> T createInstance(@NonNull Class<T> type) {
            try {
                return type.getConstructor().newInstance();
            } catch (Exception e) {
                log.error("Cannot create constant container: {} due to: {}", type, e.getMessage(), e);
                return null;
            }
        }

    }

}
