package io.github.mrsaraira.constants;

import io.github.mrsaraira.constants.containers.AbstractConstantContainer;
import io.github.mrsaraira.constants.containers.AbstractRelationConstantContainer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

public class DemoTest {

    // Demo examples. Just follow :)

    // ------------------ 💎Enum containers💎 ------------------ //
    @RequiredArgsConstructor
    @Getter
    private enum DemoEnumConstantContainer implements EnumConstantContainer<Integer, DemoEnumConstantContainer> {
        ONE(Constants.of(1)),
        TWO(Constants.of(2)),
        THREE(Constants.of(3)),
        FOUR(Constants.of(4, 5));

        private final Constant<Integer> constant;
    }

    @RequiredArgsConstructor
    @Getter
    private enum DemoEnumRelationConstantContainer implements EnumRelationConstantContainer<String, Integer, DemoEnumRelationConstantContainer> {
        ONE(Constants.of("One", 1)),
        TWO(Constants.of("Two", 2)),
        THREE(Constants.of("Three", 3)),
        FOUR_FIVE(Constants.of("Four-five", 4, 5));

        private final RelationConstant<String, Integer> constant;
    }

    @Test
    void enumContainersDemo() {
        // Enum constant containers
        assertNotNull(DemoEnumConstantContainer.TWO.getConstant());
        assertEquals(2, DemoEnumConstantContainer.TWO.getConstant().getValue());

        // match by constant values
        assertTrue(Constants.anyValue(2, DemoEnumConstantContainer.TWO));
        assertTrue(Constants.anyValue(2, DemoEnumConstantContainer.ONE, DemoEnumConstantContainer.TWO));
        assertFalse(Constants.anyValue(5, DemoEnumConstantContainer.THREE, DemoEnumConstantContainer.FOUR));
        // match by value for relation containers
        assertTrue(Constants.anyValue("Three", DemoEnumRelationConstantContainer.THREE, DemoEnumRelationConstantContainer.FOUR_FIVE));
        assertFalse(Constants.anyValue("Seven", DemoEnumRelationConstantContainer.ONE, DemoEnumRelationConstantContainer.TWO));

        // Match by relation value
        assertTrue(Constants.anyRelationValue(5, DemoEnumRelationConstantContainer.THREE, DemoEnumRelationConstantContainer.FOUR_FIVE));
        assertFalse(Constants.anyRelationValue(2, DemoEnumRelationConstantContainer.ONE, DemoEnumRelationConstantContainer.TWO));

        // Other operations
        var twoRelationConstant = DemoEnumRelationConstantContainer.TWO.getConstant();
        assertEquals("Two", twoRelationConstant.getValue());
        assertEquals("Two", twoRelationConstant.getKey().getValue());
        assertEquals(1, twoRelationConstant.getRelations().length);
        assertEquals(2, twoRelationConstant.getRelations()[0].getValue());

        var optionalTwoEnum = Constants.getEnumByValue("Two", DemoEnumRelationConstantContainer.class);
        assertTrue(optionalTwoEnum.isPresent());
        assertEquals(DemoEnumRelationConstantContainer.TWO, optionalTwoEnum.get());

        var optionalFourFiveConstant = Constants.match(5, DemoEnumRelationConstantContainer.FOUR_FIVE);
        assertTrue(optionalFourFiveConstant.isPresent());
        assertEquals(DemoEnumRelationConstantContainer.FOUR_FIVE, optionalFourFiveConstant.get());
        assertEquals(DemoEnumRelationConstantContainer.FOUR_FIVE.getRelationValues(), optionalFourFiveConstant.get().getRelationValues());

        var constantWithRelationValueFive = Constants.match(5, DemoEnumRelationConstantContainer.FOUR_FIVE.getConstant().getRelations());
        assertTrue(constantWithRelationValueFive.isPresent());
        assertEquals(5, constantWithRelationValueFive.get().getValue());

        // Of course, you cannot get Enum class instance using Constants.getInstance(enumClass)
        assertThrows(IllegalStateException.class, () -> Constants.getInstance(DemoEnumConstantContainer.class));
    }

    @Test
    void demo() {
        // Anonymous relation constant container
        var anonymousContainer = new AbstractRelationConstantContainer<String, Integer>() {
            @Override
            protected List<RelationConstant<String, Integer>> initialConstants() {
                return List.of(
                        Constants.of("One", 1),
                        Constants.of("Two", 2),
                        Constants.of("Three", 3),
                        Constants.of("FOUR_FIVE", 4, 5)
                );
            }
        };

        var keyConstant = Constants.match("One", anonymousContainer);
        assertNotNull(keyConstant);
        assertTrue(keyConstant.isPresent());

        var constant = keyConstant.get();
        assertEquals(constant.getValue(), "One");

        var relations = Constants.getRelationByKeyValue("One", anonymousContainer.getAllRelations());
        assertNotNull(relations);
        assertTrue(relations.isPresent());
        var oneRelation = relations.get();
        assertEquals("One", oneRelation.getValue());
        assertEquals("One", oneRelation.getKey().getValue());
        assertEquals(1, oneRelation.getRelations().length);
        assertTrue(Constants.anyRelationValue(1, oneRelation)); // same as above statement

        /*
        We can get container instance anytime using Constants.getInstance(containerClass).
        Unfortunately, Constants.getInstance(clazz) does not support anonymous classes, but it's not bad because you cannot
        use the anonymous class anywhere beside this method, also you have container instance already ☺ -> (var anonymousContainer)
        */
        assertThrows(IllegalStateException.class, () -> Constants.getInstance(anonymousContainer.getClass()));

        // We will use inner classes containers for the next examples
        var innerClassContainer = Constants.getInstance(DemoRelationContainerWithStaticFinalFields.class);
        assertNotNull(innerClassContainer);

        // The container instances are cached once and then reused
        assertEquals(innerClassContainer, Constants.getInstance(DemoRelationContainerWithStaticFinalFields.class));

        // Get all the keys values of the container. Same as Enum.values()
        // Set<String> [ "One", "Two", "Three" ]
        var allKeyValues = innerClassContainer.getAllValues();
        assertNotNull(allKeyValues);
        assertTrue(allKeyValues.containsAll(Set.of("One", "Two", "Three")));

        // Get all relations values for each key ordered as defined in initialConstants().
        // List<Collection<Integer>> - [ [1], [2], [3], [3,4] ]
        var relationValues = Constants.getInstance(DemoRelationContainerWithStaticFinalFields.class).getAllRelationsValues();
        assertNotNull(relationValues);
        assertEquals(4, relationValues.size()); // 4 items == 4 relation values collections

        // Check first relation collection "One"
        var firstCollection = relationValues.get(0);
        assertEquals(1, firstCollection.size());
        assertEquals(1, firstCollection.iterator().next());

        // Last element "FOUR_FIVE"
        assertTrue(relationValues.get(relationValues.size() - 1).containsAll(List.of(4, 5)));

        // Check if value exist in the containers relation values
        assertTrue(Constants.anyRelationValue(3, innerClassContainer));
        // Check if value exist in the relations of some key
        var optionalTwoRelation = Constants.getRelationByKeyValue("Two", innerClassContainer.getAllRelations());
        assertTrue(optionalTwoRelation.isPresent());
        assertEquals("Two", optionalTwoRelation.get().getValue());
        assertTrue(Constants.anyRelationValue(2, optionalTwoRelation.get()));

        // Check if any value in the container
        assertTrue(Constants.anyValue("Three", Constants.getInstance(DemoConstantContainer.class))); // constant container
        assertTrue(Constants.anyValue("Three", Constants.getInstance(DemoRelationContainerWithStaticFinalFields.class))); // relation constant container

        // Any constant value matches the value
        assertTrue(Constants.anyValue("One", DemoRelationContainerWithStaticFinalFields.ONE, DemoRelationContainerWithStaticFinalFields.TWO, DemoRelationContainerWithStaticFinalFields.THREE));

        // Check any relation by static references of the constants
        assertTrue(Constants.anyRelationValue(1, DemoRelationContainerWithStaticFinalFields.ONE, DemoRelationContainerWithStaticFinalFields.TWO, DemoRelationContainerWithStaticFinalFields.THREE));
        // Check any value by container constants references
        var containerWithFields = Constants.getInstance(DemoContainerWithStaticFields.class);
        assertTrue(Constants.anyValue("One", DemoContainerWithStaticFields.ONE, DemoContainerWithStaticFields.TWO, DemoContainerWithStaticFields.THREE));
        // Check any value by combining different constants from different containers as long they have same generic value type
        assertTrue(Constants.anyValue("One", DemoContainerWithStaticFields.ONE, DemoRelationContainerWithStaticFinalFields.ONE.getKey()));

        // Get optional constant value by value
        var one = Constants.getKeyValue("One", DemoRelationContainerWithStaticFinalFields.class);
        assertTrue(one.isPresent());
        assertEquals(one.get(), DemoRelationContainerWithStaticFinalFields.ONE.getValue());

        // Get optional constant by custom condition
        var optionalThree = Constants.getKeyValue((Predicate<String>) value -> value.equalsIgnoreCase("three"), DemoConstantContainer.class);
        assertTrue(optionalThree.isPresent());
        assertEquals("Three", optionalThree.get());

        // Operations on the constants themselves
        var oneKey = DemoRelationContainerWithStaticFinalFields.ONE.getKey();
        assertEquals(Constants.of("One"), oneKey);
        assertEquals("One", DemoRelationContainerWithStaticFinalFields.ONE.getValue());
        assertEquals("One", DemoRelationContainerWithStaticFinalFields.ONE.getKey().getValue());
        var oneRelations = DemoRelationContainerWithStaticFinalFields.ONE.getRelations();
        assertEquals(1, oneRelations.length);
        assertTrue(Constants.anyValue(1, oneRelations));

        // Negative tests on wrong examples and fine ones

        // This will fail because non-static class fields are used for constants passed to initialConstants()
        assertThrows(IllegalStateException.class, () -> Constants.getInstance(WrongDemoContainerWithNonStaticConstantsFields.class));
        // However this example works fine because values are the local non-static fields not the Constants
        // Although it's not very useful to have the static values when you work with Constants
        assertNotNull(Constants.getInstance(DemoContainerWithNonStaticValuesFields.class));

        // Duplicated keys in relation constant container exception example
        assertThrows(IllegalStateException.class, () -> Constants.getInstance(WrongDemoRelationContainerWithDuplicatedKeys.class));
        // Duplicated keys in constant container works fine
        assertEquals(2, Constants.getInstance(DemoConstantContainerWithDuplicatedKeys.class).getAllKeys().size());

        // These are very simple examples of what can be done with Constants
        // Thanks for your attention :)
    }


    // ------------------ Other demo constant containers ------------------ //

    private static class DemoConstantContainer extends AbstractConstantContainer<String> {

        @Override
        protected List<Constant<String>> initialConstants() {
            return List.of(Constants.concat(
                    "One",
                    "Two",
                    "Three"
            ));
        }

    }

    private static class DemoContainerWithStaticFields extends AbstractConstantContainer<String> {

        public static Constant<String> ONE = Constants.of("One");
        public static Constant<String> TWO = Constants.of("Two");
        public static Constant<String> THREE = Constants.of("Three");

        @Override
        protected List<Constant<String>> initialConstants() {
            // use ctrl + alt + c in INTELLIJ IDEA to define constants static final without typing the types for each field
            return List.of(
                    ONE,
                    TWO,
                    THREE
            );
        }

    }

    private static class DemoRelationContainerWithStaticFinalFields extends AbstractRelationConstantContainer<String, Integer> {

        public static final RelationConstant<String, Integer> ONE = Constants.of("One", 1);
        public static final RelationConstant<String, Integer> TWO = Constants.of("Two", 2);
        public static final RelationConstant<String, Integer> THREE = Constants.of("Three", 3);
        public static final RelationConstant<String, Integer> FOUR_FIVE = Constants.of("FOUR_FIVE", 4, 5);

        @Override
        protected List<RelationConstant<String, Integer>> initialConstants() {
            // use ctrl + alt + c in INTELLIJ IDEA to define constants static final without typing the types for each field
            return List.of(
                    ONE,
                    TWO,
                    THREE,
                    FOUR_FIVE
            );
        }

    }

    // This is a wrong example, you should not do it if you want to refer to this container using Constants.getInstance
    private static class WrongDemoContainerWithNonStaticConstantsFields extends AbstractConstantContainer<String> {

        public final Constant<String> ONE = Constants.of("One");
        public final Constant<String> TWO = Constants.of("Two");
        public final Constant<String> THREE = Constants.of("Three");

        @Override
        protected List<Constant<String>> initialConstants() {
            return List.of(ONE, TWO, THREE);
        }

    }

    // However this container will work, because values are initiated and not the constants in the local fields.
    // But this container is not as useful as other containers.
    private static class DemoContainerWithNonStaticValuesFields extends AbstractConstantContainer<String> {

        public final String ONE = "One";
        public final String TWO = "Two";
        public final String THREE = "Three";

        @Override
        protected List<Constant<String>> initialConstants() {
            return List.of(Constants.concat(ONE, TWO, THREE));
        }

    }

    // Instantiating this relation constant container with duplicated keys will lead to exception
    private static class WrongDemoRelationContainerWithDuplicatedKeys extends AbstractRelationConstantContainer<String, String> {

        @Override
        protected List<RelationConstant<String, String>> initialConstants() {
            return List.of(Constants.concat(
                    Constants.of("KEY", "VALUE1"),
                    Constants.of("KEY", "VALUE2")  // will fail at runtime
            ));
        }

    }

    // However this constant container with duplicated keys will store same keys once and will work fine
    private static class DemoConstantContainerWithDuplicatedKeys extends AbstractConstantContainer<String> {

        @Override
        protected List<Constant<String>> initialConstants() {
            return List.of(Constants.concat("ONE", "ONE", "TWO", "TWO"));
        }

    }

}
