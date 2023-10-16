package io.github.mrsaraira.constants;

import io.github.mrsaraira.constants.containers.AbstractConstantContainer;
import io.github.mrsaraira.constants.containers.AbstractRelationConstantContainer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DemoTest {

    @Test
    void demo() {
        // Demo examples. Just follow :)

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

        var keyConstant = anonymousContainer.getKey("One");
        assertNotNull(keyConstant);
        assertTrue(keyConstant.isPresent());

        var constant = keyConstant.get();
        assertEquals(constant.getValue(), "One");

        var relations = anonymousContainer.getRelation("One");
        assertNotNull(relations);
        assertTrue(relations.isPresent());
        var oneRelation = relations.get();
        assertEquals("One", oneRelation.getValue());
        assertEquals("One", oneRelation.getKey().getValue());
        assertEquals(1, oneRelation.getRelations().size());
        assertEquals(1, oneRelation.getRelations().iterator().next().getValue());
        assertTrue(Constants.anyRelation(1, oneRelation)); // same as above check

        /*
        We can get container instance anytime using Constants.getInstance(containerClass).
        Unfortunately, Constants.getInstance(clazz) does not support anonymous classes, but it's not bad because you cannot
        use the class anywhere beside this method, also you have container instance already ☺ -> (var anonymousContainer)
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
        assertTrue(Constants.anyRelation(3, innerClassContainer));
        // Check if value exist in the relations of some key
        var optionalTwoRelation = innerClassContainer.getRelation("Two");
        assertTrue(optionalTwoRelation.isPresent());
        assertEquals("Two", optionalTwoRelation.get().getValue());
        assertTrue(Constants.anyRelation(2, optionalTwoRelation.get()));

        // Check if any value in the container
        assertTrue(Constants.anyValue("Three", Constants.getInstance(DemoConstantContainer.class))); // constant container
        assertTrue(Constants.anyValue("Three", Constants.getInstance(DemoRelationContainerWithStaticFinalFields.class))); // relation constant container

        // Any constant value matches the value
        assertTrue(Constants.anyValue("One", DemoRelationContainerWithStaticFinalFields.ONE, DemoRelationContainerWithStaticFinalFields.TWO, DemoRelationContainerWithStaticFinalFields.THREE));

        // Check any relation by static references of the constants
        assertTrue(Constants.anyRelation(1, DemoRelationContainerWithStaticFinalFields.ONE, DemoRelationContainerWithStaticFinalFields.TWO, DemoRelationContainerWithStaticFinalFields.THREE));
        // Check any value by container local refs
        var containerWithFields = Constants.getInstance(DemoContainerWithStaticFields.class);
        assertTrue(Constants.anyValue("One", DemoContainerWithStaticFields.ONE, DemoContainerWithStaticFields.TWO, DemoContainerWithStaticFields.THREE));

        // Get optional constant by value
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
        var oneValue = DemoRelationContainerWithStaticFinalFields.ONE.getKey().getValue();
        assertEquals("One", oneValue);
        var oneRelations = DemoRelationContainerWithStaticFinalFields.ONE.getRelations();
        assertEquals(1, oneRelations.size());
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
        assertEquals(2, Constants.getInstance(DemoConstantContainerWithDuplicatedKeys.class).getKeys().size());
    }


    // ------------------ Demo constant containers ------------------ //

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

        // local variables
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

        // static references
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

    // however this container will work, because values are initiated and not the constants in the local fields.
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
