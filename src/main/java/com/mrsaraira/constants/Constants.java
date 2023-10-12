package com.mrsaraira.constants;

import com.mrsaraira.constants.containers.AbstractConstantContainer;
import com.mrsaraira.constants.containers.AbstractRelationConstantContainer;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@UtilityClass
public final class Constants {

    //  ------------------------------ Construct ------------------------------ //

    public static <T> IConstant<T> of(T key) {
        return new Constant<>(key);
    }

    @SafeVarargs
    public static <L, R> IRelationConstant<L, R> of(@NonNull L key, @NonNull R... relations) {
        return new RelationConstant<L, R>(new Constant<>(key), Set.of(Constants.concat(relations)));
    }

    @SafeVarargs
    public static <L, R> IRelationConstant<L, R> of(@NonNull IConstant<L> key, @NonNull R... relations) {
        return new RelationConstant<>(key, Set.of(Constants.concat(relations)));
    }

    @SafeVarargs
    public static <L, R> IRelationConstant<L, R> of(@NonNull IConstant<L> key, IConstant<R>... relations) {
        return new RelationConstant<>(key, Set.of(relations));
    }

    @SafeVarargs
    public static <T, R> IRelationConstant<T, R> relate(@NonNull IConstant<T> key, IConstant<R>... relations) {
        return new RelationConstant<>(key, Set.of(relations));
    }

    @SafeVarargs
    public static <T, R> IRelationConstant<T, R> relate(@NonNull T key, R... relations) {
        return new RelationConstant<>(new Constant<>(key), Set.of(Constants.concat(relations)));
    }

    @SafeVarargs
    public static <T, R> RelationConstantContainer<T, R> createRelationContainer(@NonNull T key, R... relations) {
        return new AbstractRelationConstantContainer<T, R>() {
            @Override
            protected List<IRelationConstant<T, R>> initialConstants() {
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
            protected List<IConstant<T>> initialConstants() {
                return List.of(Constants.concat(keys));
            }
        };
    }

    // ------------------------------ Transform ------------------------------ //
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T> IConstant<T>[] concat(@NonNull T... constants) {
        return Stream.of(constants).map(Constant::new).toArray(Constant[]::new);
    }

    @SafeVarargs
    public static <T, R extends IConstant<T>, C extends Collection<IConstant<T>>> C concat(@NonNull Supplier<C> collectionSupplier, @NonNull T... constants) {
        return Stream.of(Constants.concat(constants)).collect(Collectors.toCollection(collectionSupplier));
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <L, R, C extends IRelationConstant<L, R>> IRelationConstant<L, R>[] concat(@NonNull C... relationConstants) {
        return Stream.of(relationConstants).map(c -> Constants.of(c.getKey(), c.getRelations())).toArray(IRelationConstant[]::new);
    }

    @SafeVarargs
    public static <L, R, C extends Collection<IRelationConstant<L, R>>> Collection<IRelationConstant<L, R>> concat(@NonNull Supplier<C> collectionSupplier, @NonNull IRelationConstant<L, R>... relationConstants) {
        return Stream.of(Constants.concat(relationConstants)).collect(Collectors.toCollection(collectionSupplier));
    }

    @SuppressWarnings("unchecked")
    public static <T> IConstant<T>[] constantsToArray(@NonNull Collection<IConstant<T>> constants) {
        return constants.toArray(new IConstant[0]);
    }

    @SuppressWarnings("unchecked")
    public static <L, R> IRelationConstant<L, R>[] relationConstantsToArray(@NonNull Collection<IRelationConstant<L, R>> constants) {
        return constants.toArray(new IRelationConstant[0]);
    }

    // ------------------------------ Test ------------------------------ //

    @SafeVarargs
    public static <T> boolean anyMatch(T key, @NonNull IConstant<T>... constant) {
        return Stream.of(constant).anyMatch(c -> Objects.equals(c.getValue(), key));
    }

    public static <T> boolean anyMatch(T key, @NonNull Collection<IConstant<T>> constant) {
        return constant.stream().anyMatch(c -> Objects.equals(c.getValue(), key));
    }

    @SafeVarargs
    public static <T> boolean anyMatch(Predicate<IConstant<T>> condition, @NonNull IConstant<T>... constant) {
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
    public static <R, C extends IRelationConstant<?, R>> boolean anyRelation(R value, @NonNull C... constants) {
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

    public static <R> Collection<R> getRelationValues(@NonNull IRelationConstant<?, R> constant) {
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

        private static <R, T extends IConstant<R>> R mapToValueFunction(T constant) {
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
