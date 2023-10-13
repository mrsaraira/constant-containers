package io.github.mrsaraira.constants;

import io.github.mrsaraira.constants.containers.AbstractConstantContainer;
import io.github.mrsaraira.constants.containers.AbstractRelationConstantContainer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DemoTest {

    private static class DemoContainer extends AbstractConstantContainer<String> {

        @Override
        protected List<Constant<String>> initialConstants() {
            return List.of(Constants.concat(
                    "One",
                    "Two",
                    "Three"
            ));
        }

    }

    private static class DemoContainerWithConstants extends AbstractConstantContainer<String> {

// the constant refs can be public final and referenced by Constants.getInstance(DemoContainerWithConstants.class).ONE 
        public static final Constant<String> ONE = Constants.of("One");
        public static final Constant<String> TWO = Constants.of("Two");
        public static final Constant<String> THREE = Constants.of("Three");

        @Override
        protected List<Constant<String>> initialConstants() {
            // ctrl + alt + c in INTELLIJ to define constants with left types without writing types in fields section
            return List.of(
                    ONE,
                    TWO,
                    THREE
            );
        }

    }

    private static class DemoRelationContainerWithConstants extends AbstractRelationConstantContainer<String, Integer> {


        public static final RelationConstant<String, Integer> ONE = Constants.of("One", 1);
        public static final RelationConstant<String, Integer> TWO = Constants.of("Two", 2);
        public static final RelationConstant<String, Integer> THREE = Constants.of("Three", 3);
        public static final RelationConstant<String, Integer> FOUR_FIVE = Constants.of("FOUR_FIVE", 4, 5);

        @Override
        protected List<RelationConstant<String, Integer>> initialConstants() {
            // ctrl + alt + c in INTELLIJ to define constants with left types without writing types in fields section
            return List.of(
                    ONE,
                    TWO,
                    THREE,
                    FOUR_FIVE
            );
        }

    }

    @Test
    void demo() {
        // Demo examples. Just follow

        var container = new AbstractRelationConstantContainer<String, Integer>() {
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

        var keyConstant = container.getKey("One");
        var relations = container.getRelations("One");

        assertNotNull(keyConstant);
        assertNotNull(relations);
        assertTrue(keyConstant.isPresent());

        var constant = keyConstant.get();
        assertEquals(constant.getValue(), "One");
        assertEquals(1, relations.size());
        assertEquals(1, relations.iterator().next().getValue());

        // Same as Enum.values();
        var allKeyValues = Constants.getInstance(container.getClass()).getKeyValues();
        assertNotNull(allKeyValues);
        assertTrue(allKeyValues.containsAll(Set.of("One", "Two", "Three")));


        // Get all relation values for each key
        var relationValues = Constants.getInstance(container.getClass()).getRelationValues();
        assertNotNull(relationValues);
        assertEquals(4, relationValues.size()); // 4 items == 4 relation values collections

        // Check first relation collection "One"
        var firstCollection = relationValues.get(0);
        assertEquals(1, firstCollection.size());
        assertEquals(1, firstCollection.iterator().next());

        // Find last FOUR_FIVE element
        var lastRelationCollection = relationValues.get(relationValues.size() - 1);
        assertTrue(lastRelationCollection.containsAll(List.of(4, 5)));

        var anyRelationInTheContainer1 = Constants.anyRelation(3, container); // by the container
        assertTrue(anyRelationInTheContainer1);
        var anyRelationInTheContainer2 = Constants.anyRelation(3, Constants.getInstance(container.getClass()));  // by the container class
        assertTrue(anyRelationInTheContainer2);
        var anyValueInTheContainerKeysByClass = Constants.anyValue("Three", Constants.getInstance(DemoContainer.class).getKeys());
        assertTrue(anyValueInTheContainerKeysByClass);

        // Any relation in the whole class
        var anyRelationInTheContainerByClass = Constants.anyRelation(4, Constants.getInstance(DemoRelationContainerWithConstants.class));
        assertTrue(anyRelationInTheContainerByClass);

        // Any constant value matches the value
        var anyValueByConstants = Constants.anyValue("One", DemoRelationContainerWithConstants.ONE, DemoRelationContainerWithConstants.TWO, DemoRelationContainerWithConstants.THREE);
        assertTrue(anyValueByConstants);
        // Very handy in finding any relation by static references of the constants
        var anyRelationByRelationConstants = Constants.anyRelation(1, DemoRelationContainerWithConstants.ONE, DemoRelationContainerWithConstants.TWO, DemoRelationContainerWithConstants.THREE);
        assertTrue(anyRelationByRelationConstants);

        var one = Constants.getKeyValue("One", DemoRelationContainerWithConstants.class);
        assertTrue(one.isPresent());
        assertEquals(one.get(), DemoRelationContainerWithConstants.ONE.getValue());

        // Operations on the constants
        var oneKey = DemoRelationContainerWithConstants.ONE.getKey();
        assertEquals(Constants.of("One"), oneKey);
        var oneValue = DemoRelationContainerWithConstants.ONE.getKey().getValue();
        assertEquals("One", oneValue);
        var oneRelations = DemoRelationContainerWithConstants.ONE.getRelations();
        assertEquals(1, oneRelations.size());
        assertTrue(Constants.anyValue(1, oneRelations));
    }

}
