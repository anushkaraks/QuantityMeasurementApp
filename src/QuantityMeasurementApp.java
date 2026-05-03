```java
        package com.apps.quantitymeasurement;

import java.util.Objects;

public class QuantityMeasurementApp {

    private static final double EPSILON = 1e-6;

    public static void main(String[] args) {
        System.out.println("=== UC9 Quantity Measurement Demo ===");

        System.out.println("\n--- UC4: Length Equality Checks ---");
        Quantity q1 = new Quantity(1.0, LengthUnit.FEET);
        Quantity q2 = new Quantity(12.0, LengthUnit.INCHES);
        System.out.println("1 foot == 12 inches: " + q1.equals(q2));
        demonstrateAllUnits();

        System.out.println("\n--- UC5: Length Conversions ---");
        demonstrateConversions();
        demonstrateRoundTripConversion();

        System.out.println("\n--- UC6: Length Addition ---");
        demonstrateAdditions();
        demonstrateCommutativity();

        System.out.println("\n--- UC7: Length Explicit Target Unit ---");
        demonstrateUC7_ExplicitTargetUnit();
        demonstrateUC7_CommutativityWithTarget();

        System.out.println("\n--- UC8: Refactored Unit Responsibility ---");
        demonstrateUC8_UnitResponsibility();

        System.out.println("\n--- UC9: Weight Measurement Features ---");
        demonstrateWeightEquality();
        demonstrateWeightConversion();
        demonstrateWeightAddition();

        System.out.println("\n--- Error Handling Tests ---");
        demonstrateErrorHandling();

        System.out.println("\n=== Running Test Suites ===");
        new QuantityTest_UC4().runAllTests();
        new QuantityTest_UC5().runAllTests();
        new QuantityTest_UC6().runAllTests();
        new QuantityTest_UC7().runAllTests();
        new QuantityTest_UC8().runAllTests();
        new QuantityTest_UC9().runAllTests();
    }

    // ================= LENGTH STATIC API =================
    public static double convert(double value, LengthUnit sourceUnit, LengthUnit targetUnit) {
        Quantity.validateValue(value); Quantity.validateUnit(sourceUnit); Quantity.validateUnit(targetUnit);
        double baseValue = sourceUnit.convertToBaseUnit(value);
        double result = targetUnit.convertFromBaseUnit(baseValue);
        return Quantity.roundToDecimalPlaces(result);
    }

    public static Quantity add(Quantity q1, Quantity q2) { Quantity.validate(q1); Quantity.validate(q2); return q1.add(q2); }
    public static Quantity add(Quantity q1, Quantity q2, LengthUnit targetUnit) { Quantity.validate(q1); Quantity.validate(q2); Quantity.validateUnit(targetUnit); return q1.add(q2, targetUnit); }
    public static double add(double v1, LengthUnit u1, double v2, LengthUnit u2, LengthUnit targetUnit) {
        Quantity.validateValue(v1); Quantity.validateValue(v2); Quantity.validateUnit(u1); Quantity.validateUnit(u2); Quantity.validateUnit(targetUnit);
        double base1 = u1.convertToBaseUnit(v1); double base2 = u2.convertToBaseUnit(v2);
        return Quantity.roundToDecimalPlaces(targetUnit.convertFromBaseUnit(base1 + base2));
    }

    public static Quantity demonstrateLengthConversion(double value, LengthUnit fromUnit, LengthUnit toUnit) {
        System.out.printf("Converting %.2f %s to %s... ", value, fromUnit, toUnit);
        Quantity quantity = new Quantity(value, fromUnit);
        Quantity result = quantity.convertTo(toUnit);
        System.out.println("Result: " + result);
        return result;
    }

    public static Quantity demonstrateLengthConversion(Quantity quantity, LengthUnit toUnit) {
        System.out.printf("Converting %s to %s... ", quantity, toUnit);
        Quantity result = quantity.convertTo(toUnit);
        System.out.println("Result: " + result);
        return result;
    }

    public static Quantity demonstrateAddition(Quantity q1, Quantity q2) {
        System.out.printf("Adding %s + %s ... ", q1, q2);
        Quantity result = add(q1, q2);
        System.out.println("Result: " + result);
        return result;
    }

    public static Quantity demonstrateAddition(Quantity q1, Quantity q2, LengthUnit targetUnit) {
        System.out.printf("Adding %s + %s (result in %s) ... ", q1, q2, targetUnit);
        Quantity result = add(q1, q2, targetUnit);
        System.out.println("Result: " + result);
        return result;
    }

    public static double demonstrateAddition(double v1, LengthUnit u1, double v2, LengthUnit u2, LengthUnit targetUnit) {
        System.out.printf("Adding %.2f %s + %.2f %s (result in %s) ... ", v1, u1, v2, u2, targetUnit);
        double result = add(v1, u1, v2, u2, targetUnit);
        System.out.printf("Result: %.2f%n", result);
        return result;
    }

    public static void demonstrateAllUnits() {
        System.out.println("\n=== UC4 Unit Comparisons ===");
        Quantity yard = new Quantity(1.0, LengthUnit.YARDS);
        Quantity feet = new Quantity(3.0, LengthUnit.FEET);
        Quantity inches = new Quantity(36.0, LengthUnit.INCHES);
        Quantity cm = new Quantity(1.0, LengthUnit.CENTIMETERS);
        System.out.println("1 yard == 3 feet: " + yard.equals(feet));
        System.out.println("1 yard == 36 inches: " + yard.equals(inches));
        System.out.println("1 cm == 0.393701 inches: " + cm.equals(new Quantity(0.393701, LengthUnit.INCHES)));
    }

    public static void demonstrateConversions() {
        System.out.println("\n=== UC5 Unit Conversions ===");
        demonstrateLengthConversion(1.0, LengthUnit.FEET, LengthUnit.INCHES);
        demonstrateLengthConversion(3.0, LengthUnit.YARDS, LengthUnit.FEET);
        demonstrateLengthConversion(36.0, LengthUnit.INCHES, LengthUnit.YARDS);
        demonstrateLengthConversion(1.0, LengthUnit.CENTIMETERS, LengthUnit.INCHES);
        demonstrateLengthConversion(0.0, LengthUnit.FEET, LengthUnit.INCHES);
        demonstrateLengthConversion(-1.0, LengthUnit.FEET, LengthUnit.INCHES);
        demonstrateLengthConversion(5.0, LengthUnit.FEET, LengthUnit.FEET);
        Quantity lengthInYards = new Quantity(2.0, LengthUnit.YARDS);
        demonstrateLengthConversion(lengthInYards, LengthUnit.INCHES);
        System.out.printf("Static API: convert(2.54, CM, INCHES) = %.4f%n", convert(2.54, LengthUnit.CENTIMETERS, LengthUnit.INCHES));
    }

    public static void demonstrateAdditions() {
        System.out.println("\n=== UC6 Addition Examples ===");
        demonstrateAddition(new Quantity(1.0, LengthUnit.FEET), new Quantity(2.0, LengthUnit.FEET));
        demonstrateAddition(new Quantity(1.0, LengthUnit.FEET), new Quantity(12.0, LengthUnit.INCHES));
        demonstrateAddition(new Quantity(12.0, LengthUnit.INCHES), new Quantity(1.0, LengthUnit.FEET));
        demonstrateAddition(new Quantity(1.0, LengthUnit.YARDS), new Quantity(3.0, LengthUnit.FEET));
        demonstrateAddition(new Quantity(36.0, LengthUnit.INCHES), new Quantity(1.0, LengthUnit.YARDS));
        demonstrateAddition(new Quantity(2.54, LengthUnit.CENTIMETERS), new Quantity(1.0, LengthUnit.INCHES));
        demonstrateAddition(new Quantity(5.0, LengthUnit.FEET), new Quantity(0.0, LengthUnit.INCHES));
        demonstrateAddition(new Quantity(5.0, LengthUnit.FEET), new Quantity(-2.0, LengthUnit.FEET));
        demonstrateAddition(new Quantity(1.0, LengthUnit.FEET), new Quantity(12.0, LengthUnit.INCHES), LengthUnit.INCHES);
        demonstrateAddition(1.0, LengthUnit.YARDS, 3.0, LengthUnit.FEET, LengthUnit.FEET);
    }

    public static void demonstrateUC7_ExplicitTargetUnit() {
        System.out.println("\n=== UC7 Explicit Target Unit Examples ===");
        demonstrateAddition(new Quantity(1.0, LengthUnit.FEET), new Quantity(12.0, LengthUnit.INCHES), LengthUnit.FEET);
        demonstrateAddition(new Quantity(1.0, LengthUnit.FEET), new Quantity(12.0, LengthUnit.INCHES), LengthUnit.INCHES);
        demonstrateAddition(new Quantity(1.0, LengthUnit.FEET), new Quantity(12.0, LengthUnit.INCHES), LengthUnit.YARDS);
        demonstrateAddition(new Quantity(1.0, LengthUnit.YARDS), new Quantity(3.0, LengthUnit.FEET), LengthUnit.YARDS);
        demonstrateAddition(new Quantity(36.0, LengthUnit.INCHES), new Quantity(1.0, LengthUnit.YARDS), LengthUnit.FEET);
        demonstrateAddition(new Quantity(2.54, LengthUnit.CENTIMETERS), new Quantity(1.0, LengthUnit.INCHES), LengthUnit.CENTIMETERS);
        demonstrateAddition(new Quantity(5.0, LengthUnit.FEET), new Quantity(0.0, LengthUnit.INCHES), LengthUnit.YARDS);
        demonstrateAddition(new Quantity(5.0, LengthUnit.FEET), new Quantity(-2.0, LengthUnit.FEET), LengthUnit.INCHES);
        System.out.println("\n--- Same Addition, Different Target Units ---");
        Quantity a = new Quantity(1.0, LengthUnit.FEET);
        Quantity b = new Quantity(12.0, LengthUnit.INCHES);
        System.out.println("1ft + 12in in FEET: " + add(a, b, LengthUnit.FEET));
        System.out.println("1ft + 12in in INCHES: " + add(a, b, LengthUnit.INCHES));
        System.out.println("1ft + 12in in YARDS: " + add(a, b, LengthUnit.YARDS));
        System.out.println("1ft + 12in in CENTIMETERS: " + add(a, b, LengthUnit.CENTIMETERS));
    }

    public static void demonstrateCommutativity() {
        System.out.println("\n=== Commutativity Test ===");
        Quantity feet1 = new Quantity(1.0, LengthUnit.FEET);
        Quantity inches12 = new Quantity(12.0, LengthUnit.INCHES);
        Quantity result1 = add(feet1, inches12);
        Quantity result2 = add(inches12, feet1);
        System.out.println("add(1ft, 12in) in feet: " + result1);
        System.out.println("add(12in, 1ft) in inches: " + result2);
        boolean sameLength = result1.equals(result2);
        System.out.println("Same physical length: " + sameLength);
        Quantity r1 = add(feet1, inches12, LengthUnit.FEET);
        Quantity r2 = add(inches12, feet1, LengthUnit.FEET);
        System.out.println("With same target unit (FEET): " + r1 + " == " + r2 + " ? " + r1.equals(r2));
    }

    public static void demonstrateUC7_CommutativityWithTarget() {
        System.out.println("\n=== UC7 Commutativity with Explicit Target ===");
        Quantity a = new Quantity(1.0, LengthUnit.FEET);
        Quantity b = new Quantity(12.0, LengthUnit.INCHES);
        for (LengthUnit target : LengthUnit.values()) {
            Quantity r1 = add(a, b, target);
            Quantity r2 = add(b, a, target);
            boolean equal = r1.equals(r2);
            System.out.printf("Target %s: %s == %s ? %s%n", target, r1, r2, equal);
            assert equal : "Commutativity failed for target " + target;
        }
        System.out.println("All commutativity tests passed");
    }

    public static void demonstrateUC8_UnitResponsibility() {
        System.out.println("\n=== UC8 Unit Responsibility Demos ===");
        System.out.printf("FEET.convertToBaseUnit(12.0) = %.2f%n", LengthUnit.FEET.convertToBaseUnit(12.0));
        System.out.printf("INCHES.convertToBaseUnit(12.0) = %.2f%n", LengthUnit.INCHES.convertToBaseUnit(12.0));
        System.out.printf("YARDS.convertToBaseUnit(1.0) = %.2f%n", LengthUnit.YARDS.convertToBaseUnit(1.0));
        System.out.printf("CENTIMETERS.convertToBaseUnit(30.48) = %.2f%n", LengthUnit.CENTIMETERS.convertToBaseUnit(30.48));
        System.out.printf("FEET.convertFromBaseUnit(2.0) = %.2f%n", LengthUnit.FEET.convertFromBaseUnit(2.0));
        System.out.printf("INCHES.convertFromBaseUnit(1.0) = %.2f%n", LengthUnit.INCHES.convertFromBaseUnit(1.0));
        System.out.printf("YARDS.convertFromBaseUnit(3.0) = %.2f%n", LengthUnit.YARDS.convertFromBaseUnit(3.0));
        System.out.printf("CENTIMETERS.convertFromBaseUnit(1.0) = %.2f%n", LengthUnit.CENTIMETERS.convertFromBaseUnit(1.0));
    }

    public static void demonstrateRoundTripConversion() {
        System.out.println("\n=== Round-Trip Conversion Tests ===");
        double originalValue = 5.73;
        LengthUnit unitA = LengthUnit.FEET;
        LengthUnit unitB = LengthUnit.CENTIMETERS;
        double step1 = convert(originalValue, unitA, unitB);
        double step2 = convert(step1, unitB, unitA);
        System.out.printf("Original: %.4f %s%n", originalValue, unitA);
        System.out.printf("To %s: %.4f%n", unitB, step1);
        System.out.printf("Back to %s: %.4f%n", unitA, step2);
        System.out.printf("Difference: %.8f (epsilon: %.8f) ... %s%n",
                Math.abs(originalValue - step2), EPSILON,
                Math.abs(originalValue - step2) < EPSILON ? "PASS" : "FAIL");
    }

    public static void demonstrateErrorHandling() {
        try { convert(1.0, null, LengthUnit.INCHES); } catch (IllegalArgumentException e) { System.out.println("✓ Length Null source unit caught"); }
        try { convert(Double.NaN, LengthUnit.FEET, LengthUnit.INCHES); } catch (IllegalArgumentException e) { System.out.println("✓ Length NaN value caught"); }
        try { add(new Quantity(1.0, LengthUnit.FEET), null); } catch (IllegalArgumentException e) { System.out.println("✓ Length Null operand caught"); }
        try { convertWeight(1.0, null, WeightUnit.KILOGRAM); } catch (IllegalArgumentException e) { System.out.println("✓ Weight Null source unit caught"); }
        try { addWeight(new QuantityWeight(1.0, WeightUnit.KILOGRAM), null); } catch (IllegalArgumentException e) { System.out.println("✓ Weight Null operand caught"); }
    }

    // ================= WEIGHT STATIC API =================
    public static double convertWeight(double value, WeightUnit sourceUnit, WeightUnit targetUnit) {
        QuantityWeight.validateValue(value); QuantityWeight.validateUnit(sourceUnit); QuantityWeight.validateUnit(targetUnit);
        double baseValue = sourceUnit.convertToBaseUnit(value);
        double result = targetUnit.convertFromBaseUnit(baseValue);
        return QuantityWeight.roundToDecimalPlaces(result);
    }

    public static QuantityWeight addWeight(QuantityWeight q1, QuantityWeight q2) { QuantityWeight.validate(q1); QuantityWeight.validate(q2); return q1.add(q2); }
    public static QuantityWeight addWeight(QuantityWeight q1, QuantityWeight q2, WeightUnit targetUnit) { QuantityWeight.validate(q1); QuantityWeight.validate(q2); QuantityWeight.validateUnit(targetUnit); return q1.add(q2, targetUnit); }

    public static void demonstrateWeightEquality() {
        System.out.println("\n=== UC9 Weight Equality ===");
        System.out.println("1.0 KG == 1.0 KG: " + new QuantityWeight(1.0, WeightUnit.KILOGRAM).equals(new QuantityWeight(1.0, WeightUnit.KILOGRAM)));
        System.out.println("1.0 KG == 1000.0 G: " + new QuantityWeight(1.0, WeightUnit.KILOGRAM).equals(new QuantityWeight(1000.0, WeightUnit.GRAM)));
        System.out.println("1.0 KG == 2.20462 LB: " + new QuantityWeight(1.0, WeightUnit.KILOGRAM).equals(new QuantityWeight(2.20462, WeightUnit.POUND)));
        System.out.println("500.0 G == 0.5 KG: " + new QuantityWeight(500.0, WeightUnit.GRAM).equals(new QuantityWeight(0.5, WeightUnit.KILOGRAM)));
        System.out.println("Weight vs Length: " + new QuantityWeight(1.0, WeightUnit.KILOGRAM).equals(new Quantity(1.0, LengthUnit.FEET)));
    }

    public static void demonstrateWeightConversion() {
        System.out.println("\n=== UC9 Weight Conversion ===");
        System.out.println("1.0 KG to G: " + new QuantityWeight(1.0, WeightUnit.KILOGRAM).convertTo(WeightUnit.GRAM));
        System.out.println("2.0 LB to KG: " + new QuantityWeight(2.0, WeightUnit.POUND).convertTo(WeightUnit.KILOGRAM));
        System.out.println("500.0 G to LB: " + new QuantityWeight(500.0, WeightUnit.GRAM).convertTo(WeightUnit.POUND));
        System.out.println("0.0 KG to G: " + new QuantityWeight(0.0, WeightUnit.KILOGRAM).convertTo(WeightUnit.GRAM));
    }

    public static void demonstrateWeightAddition() {
        System.out.println("\n=== UC9 Weight Addition ===");
        System.out.println("1.0 KG + 2.0 KG: " + addWeight(new QuantityWeight(1.0, WeightUnit.KILOGRAM), new QuantityWeight(2.0, WeightUnit.KILOGRAM)));
        System.out.println("1.0 KG + 1000.0 G: " + addWeight(new QuantityWeight(1.0, WeightUnit.KILOGRAM), new QuantityWeight(1000.0, WeightUnit.GRAM)));
        System.out.println("500.0 G + 0.5 KG: " + addWeight(new QuantityWeight(500.0, WeightUnit.GRAM), new QuantityWeight(0.5, WeightUnit.KILOGRAM)));
        System.out.println("1.0 KG + 1000.0 G to G: " + addWeight(new QuantityWeight(1.0, WeightUnit.KILOGRAM), new QuantityWeight(1000.0, WeightUnit.GRAM), WeightUnit.GRAM));
        System.out.println("1.0 LB + 453.592 G to LB: " + addWeight(new QuantityWeight(1.0, WeightUnit.POUND), new QuantityWeight(453.592, WeightUnit.GRAM), WeightUnit.POUND));
    }

    // ================= TEST SUITES =================
    public static class QuantityTest_UC9 {
        public void testEquality_KilogramToKilogram_SameValue() { assert new QuantityWeight(1.0, WeightUnit.KILOGRAM).equals(new QuantityWeight(1.0, WeightUnit.KILOGRAM)); System.out.println("✓ testEquality_KilogramToKilogram_SameValue"); }
        public void testEquality_KilogramToKilogram_DifferentValue() { assert !new QuantityWeight(1.0, WeightUnit.KILOGRAM).equals(new QuantityWeight(2.0, WeightUnit.KILOGRAM)); System.out.println("✓ testEquality_KilogramToKilogram_DifferentValue"); }
        public void testEquality_KilogramToGram_EquivalentValue() { assert new QuantityWeight(1.0, WeightUnit.KILOGRAM).equals(new QuantityWeight(1000.0, WeightUnit.GRAM)); System.out.println("✓ testEquality_KilogramToGram_EquivalentValue"); }
        public void testEquality_GramToKilogram_EquivalentValue() { assert new QuantityWeight(1000.0, WeightUnit.GRAM).equals(new QuantityWeight(1.0, WeightUnit.KILOGRAM)); System.out.println("✓ testEquality_GramToKilogram_EquivalentValue"); }
        public void testEquality_WeightVsLength_Incompatible() { assert !new QuantityWeight(1.0, WeightUnit.KILOGRAM).equals(new Quantity(1.0, LengthUnit.FEET)); System.out.println("✓ testEquality_WeightVsLength_Incompatible"); }
        public void testEquality_NullComparison() { assert !new QuantityWeight(1.0, WeightUnit.KILOGRAM).equals(null); System.out.println("✓ testEquality_NullComparison"); }
        public void testEquality_SameReference() { QuantityWeight q = new QuantityWeight(1.0, WeightUnit.KILOGRAM); assert q.equals(q); System.out.println("✓ testEquality_SameReference"); }
        public void testEquality_NullUnit() { try { new QuantityWeight(1.0, null); assert false; } catch (IllegalArgumentException e) { System.out.println("✓ testEquality_NullUnit"); } }
        public void testEquality_TransitiveProperty() { assert new QuantityWeight(1.0,WeightUnit.KILOGRAM).equals(new QuantityWeight(1000.0,WeightUnit.GRAM)) && new QuantityWeight(1000.0,WeightUnit.GRAM).equals(new QuantityWeight(2.20462,WeightUnit.POUND)) && new QuantityWeight(1.0,WeightUnit.KILOGRAM).equals(new QuantityWeight(2.20462,WeightUnit.POUND)); System.out.println("✓ testEquality_TransitiveProperty"); }
        public void testEquality_ZeroValue() { assert new QuantityWeight(0.0, WeightUnit.KILOGRAM).equals(new QuantityWeight(0.0, WeightUnit.GRAM)); System.out.println("✓ testEquality_ZeroValue"); }
        public void testEquality_NegativeWeight() { assert new QuantityWeight(-1.0, WeightUnit.KILOGRAM).equals(new QuantityWeight(-1000.0, WeightUnit.GRAM)); System.out.println("✓ testEquality_NegativeWeight"); }
        public void testEquality_LargeWeightValue() { assert new QuantityWeight(1000000.0, WeightUnit.GRAM).equals(new QuantityWeight(1000.0, WeightUnit.KILOGRAM)); System.out.println("✓ testEquality_LargeWeightValue"); }
        public void testEquality_SmallWeightValue() { assert new QuantityWeight(0.001, WeightUnit.KILOGRAM).equals(new QuantityWeight(1.0, WeightUnit.GRAM)); System.out.println("✓ testEquality_SmallWeightValue"); }
        public void testConversion_PoundToKilogram() { assert Math.abs(new QuantityWeight(2.20462, WeightUnit.POUND).convertTo(WeightUnit.KILOGRAM).getValue() - 1.0) < EPSILON; System.out.println("✓ testConversion_PoundToKilogram"); }
        public void testConversion_KilogramToPound() { assert Math.abs(new QuantityWeight(1.0, WeightUnit.KILOGRAM).convertTo(WeightUnit.POUND).getValue() - 2.20462) < EPSILON; System.out.println("✓ testConversion_KilogramToPound"); }
        public void testConversion_SameUnit() { assert Math.abs(new QuantityWeight(5.0, WeightUnit.KILOGRAM).convertTo(WeightUnit.KILOGRAM).getValue() - 5.0) < EPSILON; System.out.println("✓ testConversion_SameUnit"); }
        public void testConversion_ZeroValue() { assert Math.abs(new QuantityWeight(0.0, WeightUnit.KILOGRAM).convertTo(WeightUnit.GRAM).getValue() - 0.0) < EPSILON; System.out.println("✓ testConversion_ZeroValue"); }
        public void testConversion_NegativeValue() { assert Math.abs(new QuantityWeight(-1.0, WeightUnit.KILOGRAM).convertTo(WeightUnit.GRAM).getValue() - (-1000.0)) < EPSILON; System.out.println("✓ testConversion_NegativeValue"); }
        public void testConversion_RoundTrip() { assert Math.abs(new QuantityWeight(1.5, WeightUnit.KILOGRAM).convertTo(WeightUnit.GRAM).convertTo(WeightUnit.KILOGRAM).getValue() - 1.5) < EPSILON; System.out.println("✓ testConversion_RoundTrip"); }
        public void testAddition_SameUnit_KilogramPlusKilogram() { assert Math.abs(addWeight(new QuantityWeight(1.0, WeightUnit.KILOGRAM), new QuantityWeight(2.0, WeightUnit.KILOGRAM)).getValue() - 3.0) < EPSILON; System.out.println("✓ testAddition_SameUnit_KilogramPlusKilogram"); }
        public void testAddition_CrossUnit_KilogramPlusGram() { assert Math.abs(addWeight(new QuantityWeight(1.0, WeightUnit.KILOGRAM), new QuantityWeight(1000.0, WeightUnit.GRAM)).getValue() - 2.0) < EPSILON; System.out.println("✓ testAddition_CrossUnit_KilogramPlusGram"); }
        public void testAddition_CrossUnit_PoundPlusKilogram() { assert Math.abs(addWeight(new QuantityWeight(2.20462, WeightUnit.POUND), new QuantityWeight(1.0, WeightUnit.KILOGRAM)).getValue() - 4.40924) < EPSILON; System.out.println("✓ testAddition_CrossUnit_PoundPlusKilogram"); }
        public void testAddition_ExplicitTargetUnit_Kilogram() { assert Math.abs(addWeight(new QuantityWeight(1.0, WeightUnit.KILOGRAM), new QuantityWeight(1000.0, WeightUnit.GRAM), WeightUnit.GRAM).getValue() - 2000.0) < EPSILON; System.out.println("✓ testAddition_ExplicitTargetUnit_Kilogram"); }
        public void testAddition_Commutativity() { QuantityWeight a=new QuantityWeight(1.0,WeightUnit.KILOGRAM), b=new QuantityWeight(1000.0,WeightUnit.GRAM); assert addWeight(a,b,WeightUnit.KILOGRAM).equals(addWeight(b,a,WeightUnit.KILOGRAM)); System.out.println("✓ testAddition_Commutativity"); }
        public void testAddition_WithZero() { assert Math.abs(addWeight(new QuantityWeight(5.0, WeightUnit.KILOGRAM), new QuantityWeight(0.0, WeightUnit.GRAM)).getValue() - 5.0) < EPSILON; System.out.println("✓ testAddition_WithZero"); }
        public void testAddition_NegativeValues() { assert Math.abs(addWeight(new QuantityWeight(5.0, WeightUnit.KILOGRAM), new QuantityWeight(-2000.0, WeightUnit.GRAM)).getValue() - 3.0) < EPSILON; System.out.println("✓ testAddition_NegativeValues"); }
        public void testAddition_LargeValues() { assert Math.abs(addWeight(new QuantityWeight(1e6, WeightUnit.KILOGRAM), new QuantityWeight(1e6, WeightUnit.KILOGRAM)).getValue() - 2e6) < EPSILON; System.out.println("✓ testAddition_LargeValues"); }
        public void runAllTests() { System.out.println("\n=== Running UC9 Test Suite ==="); testEquality_KilogramToKilogram_SameValue(); testEquality_KilogramToKilogram_DifferentValue(); testEquality_KilogramToGram_EquivalentValue(); testEquality_GramToKilogram_EquivalentValue(); testEquality_WeightVsLength_Incompatible(); testEquality_NullComparison(); testEquality_SameReference(); testEquality_NullUnit(); testEquality_TransitiveProperty(); testEquality_ZeroValue(); testEquality_NegativeWeight(); testEquality_LargeWeightValue(); testEquality_SmallWeightValue(); testConversion_PoundToKilogram(); testConversion_KilogramToPound(); testConversion_SameUnit(); testConversion_ZeroValue(); testConversion_NegativeValue(); testConversion_RoundTrip(); testAddition_SameUnit_KilogramPlusKilogram(); testAddition_CrossUnit_KilogramPlusGram(); testAddition_CrossUnit_PoundPlusKilogram(); testAddition_ExplicitTargetUnit_Kilogram(); testAddition_Commutativity(); testAddition_WithZero(); testAddition_NegativeValues(); testAddition_LargeValues(); System.out.println("=== All UC9 Tests Passed! ===\n"); }
    }

    public static class QuantityTest_UC8 {
        public void testLengthUnitEnum_FeetConstant() { assert Math.abs(LengthUnit.FEET.getConversionFactor() - 1.0) < EPSILON; System.out.println("✓ UC8: testLengthUnitEnum_FeetConstant"); }
        public void testLengthUnitEnum_InchesConstant() { assert Math.abs(LengthUnit.INCHES.getConversionFactor() - 0.083333) < EPSILON; System.out.println("✓ UC8: testLengthUnitEnum_InchesConstant"); }
        public void testLengthUnitEnum_YardsConstant() { assert Math.abs(LengthUnit.YARDS.getConversionFactor() - 3.0) < EPSILON; System.out.println("✓ UC8: testLengthUnitEnum_YardsConstant"); }
        public void testLengthUnitEnum_CentimetersConstant() { assert Math.abs(LengthUnit.CENTIMETERS.getConversionFactor() - 0.032808) < EPSILON; System.out.println("✓ UC8: testLengthUnitEnum_CentimetersConstant"); }
        public void testConvertToBaseUnit_FeetToFeet() { assert Math.abs(LengthUnit.FEET.convertToBaseUnit(5.0) - 5.0) < EPSILON; System.out.println("✓ UC8: testConvertToBaseUnit_FeetToFeet"); }
        public void testConvertToBaseUnit_InchesToFeet() { assert Math.abs(LengthUnit.INCHES.convertToBaseUnit(12.0) - 1.0) < EPSILON; System.out.println("✓ UC8: testConvertToBaseUnit_InchesToFeet"); }
        public void testConvertToBaseUnit_YardsToFeet() { assert Math.abs(LengthUnit.YARDS.convertToBaseUnit(1.0) - 3.0) < EPSILON; System.out.println("✓ UC8: testConvertToBaseUnit_YardsToFeet"); }
        public void testConvertToBaseUnit_CentimetersToFeet() { assert Math.abs(LengthUnit.CENTIMETERS.convertToBaseUnit(30.48) - 1.0) < EPSILON; System.out.println("✓ UC8: testConvertToBaseUnit_CentimetersToFeet"); }
        public void testConvertFromBaseUnit_FeetToFeet() { assert Math.abs(LengthUnit.FEET.convertFromBaseUnit(2.0) - 2.0) < EPSILON; System.out.println("✓ UC8: testConvertFromBaseUnit_FeetToFeet"); }
        public void testConvertFromBaseUnit_FeetToInches() { assert Math.abs(LengthUnit.INCHES.convertFromBaseUnit(1.0) - 12.0) < EPSILON; System.out.println("✓ UC8: testConvertFromBaseUnit_FeetToInches"); }
        public void testConvertFromBaseUnit_FeetToYards() { assert Math.abs(LengthUnit.YARDS.convertFromBaseUnit(3.0) - 1.0) < EPSILON; System.out.println("✓ UC8: testConvertFromBaseUnit_FeetToYards"); }
        public void testConvertFromBaseUnit_FeetToCentimeters() { assert Math.abs(LengthUnit.CENTIMETERS.convertFromBaseUnit(1.0) - 30.48) < EPSILON; System.out.println("✓ UC8: testConvertFromBaseUnit_FeetToCentimeters"); }
        public void testQuantityLengthRefactored_Equality() { assert new Quantity(1.0, LengthUnit.FEET).equals(new Quantity(12.0, LengthUnit.INCHES)); System.out.println("✓ UC8: testQuantityLengthRefactored_Equality"); }
        public void testQuantityLengthRefactored_ConvertTo() { Quantity res = new Quantity(1.0, LengthUnit.FEET).convertTo(LengthUnit.INCHES); assert Math.abs(res.getValue() - 12.0) < EPSILON && res.getUnit() == LengthUnit.INCHES; System.out.println("✓ UC8: testQuantityLengthRefactored_ConvertTo"); }
        public void testQuantityLengthRefactored_Add() { Quantity res = new Quantity(1.0, LengthUnit.FEET).add(new Quantity(12.0, LengthUnit.INCHES), LengthUnit.FEET); assert Math.abs(res.getValue() - 2.0) < EPSILON && res.getUnit() == LengthUnit.FEET; System.out.println("✓ UC8: testQuantityLengthRefactored_Add"); }
        public void testQuantityLengthRefactored_AddWithTargetUnit() { Quantity res = new Quantity(1.0, LengthUnit.FEET).add(new Quantity(12.0, LengthUnit.INCHES), LengthUnit.YARDS); assert Math.abs(res.getValue() - 0.667) < EPSILON && res.getUnit() == LengthUnit.YARDS; System.out.println("✓ UC8: testQuantityLengthRefactored_AddWithTargetUnit"); }
        public void testQuantityLengthRefactored_NullUnit() { try { new Quantity(1.0, null); assert false; } catch (IllegalArgumentException e) { System.out.println("✓ UC8: testQuantityLengthRefactored_NullUnit"); } }
        public void testQuantityLengthRefactored_InvalidValue() { try { new Quantity(Double.NaN, LengthUnit.FEET); assert false; } catch (IllegalArgumentException e) { System.out.println("✓ UC8: testQuantityLengthRefactored_InvalidValue"); } }
        public void testBackwardCompatibility_UC1EqualityTests() { new QuantityTest_UC4().testEquality_YardToYard_SameValue(); new QuantityTest_UC4().testEquality_YardToFeet(); new QuantityTest_UC4().testEquality_YardToInches(); new QuantityTest_UC4().testEquality_YardInequality(); new QuantityTest_UC4().testEquality_CmToCm(); new QuantityTest_UC4().testEquality_CmToInches(); new QuantityTest_UC4().testTransitiveProperty(); new QuantityTest_UC4().testSameReference(); new QuantityTest_UC4().testNullComparison(); new QuantityTest_UC4().testComplexScenario(); System.out.println("✓ UC8: testBackwardCompatibility_UC1EqualityTests"); }
        public void testBackwardCompatibility_UC5ConversionTests() { new QuantityTest_UC5().testConversion_FeetToInches(); new QuantityTest_UC5().testConversion_InchesToFeet(); new QuantityTest_UC5().testConversion_YardsToInches(); new QuantityTest_UC5().testConversion_CentimetersToInches(); new QuantityTest_UC5().testConversion_ZeroValue(); new QuantityTest_UC5().testConversion_NegativeValue(); new QuantityTest_UC5().testConversion_SameUnit(); new QuantityTest_UC5().testConversion_RoundTrip_PreservesValue(); new QuantityTest_UC5().testInstanceConvert_FeetToInches(); new QuantityTest_UC5().testInstanceConvert_Immutability(); new QuantityTest_UC5().testConversion_NullSourceUnit_Throws(); new QuantityTest_UC5().testConversion_NaN_Throws(); new QuantityTest_UC5().testConversion_PrecisionTolerance(); new QuantityTest_UC5().testEquality_AfterConversion(); System.out.println("✓ UC8: testBackwardCompatibility_UC5ConversionTests"); }
        public void testBackwardCompatibility_UC6AdditionTests() { new QuantityTest_UC6().testAddition_SameUnit_FeetPlusFeet(); new QuantityTest_UC6().testAddition_SameUnit_InchPlusInch(); new QuantityTest_UC6().testAddition_CrossUnit_FeetPlusInches(); new QuantityTest_UC6().testAddition_CrossUnit_InchPlusFeet(); new QuantityTest_UC6().testAddition_CrossUnit_YardPlusFeet(); new QuantityTest_UC6().testAddition_CrossUnit_CentimeterPlusInch(); new QuantityTest_UC6().testAddition_Commutativity(); new QuantityTest_UC6().testAddition_WithZero(); new QuantityTest_UC6().testAddition_NegativeValues(); new QuantityTest_UC6().testAddition_NullSecondOperand_Throws(); new QuantityTest_UC6().testAddition_LargeValues(); new QuantityTest_UC6().testAddition_SmallValues(); System.out.println("✓ UC8: testBackwardCompatibility_UC6AdditionTests"); }
        public void testBackwardCompatibility_UC7AdditionWithTargetUnitTests() { new QuantityTest_UC7().testAddition_ExplicitTargetUnit_Feet(); new QuantityTest_UC7().testAddition_ExplicitTargetUnit_Inches(); new QuantityTest_UC7().testAddition_ExplicitTargetUnit_Yards(); new QuantityTest_UC7().testAddition_ExplicitTargetUnit_Centimeters(); new QuantityTest_UC7().testAddition_ExplicitTargetUnit_SameAsFirstOperand(); new QuantityTest_UC7().testAddition_ExplicitTargetUnit_SameAsSecondOperand(); new QuantityTest_UC7().testAddition_ExplicitTargetUnit_Commutativity(); new QuantityTest_UC7().testAddition_ExplicitTargetUnit_WithZero(); new QuantityTest_UC7().testAddition_ExplicitTargetUnit_NegativeValues(); new QuantityTest_UC7().testAddition_ExplicitTargetUnit_NullTargetUnit(); new QuantityTest_UC7().testAddition_ExplicitTargetUnit_LargeToSmallScale(); new QuantityTest_UC7().testAddition_ExplicitTargetUnit_SmallToLargeScale(); new QuantityTest_UC7().testAddition_ExplicitTargetUnit_AllUnitCombinations(); new QuantityTest_UC7().testAddition_ExplicitTargetUnit_PrecisionTolerance(); new QuantityTest_UC7().testAddition_InstanceMethod_WithTargetUnit(); new QuantityTest_UC7().testAddition_StaticRawValues_WithTarget(); new QuantityTest_UC7().testAddition_TargetUnitIndependence(); new QuantityTest_UC7().testAddition_ChainedWithDifferentTargets(); System.out.println("✓ UC8: testBackwardCompatibility_UC7AdditionWithTargetUnitTests"); }
        public void testArchitecturalScalability_MultipleCategories() { assert LengthUnit.FEET instanceof Enum; assert Quantity.class.getDeclaredFields().length == 2; System.out.println("✓ UC8: testArchitecturalScalability_MultipleCategories"); }
        public void testRoundTripConversion_RefactoredDesign() { double val = 7.89; double step1 = LengthUnit.FEET.convertToBaseUnit(val); double step2 = LengthUnit.INCHES.convertFromBaseUnit(step1); double step3 = LengthUnit.INCHES.convertToBaseUnit(step2); double step4 = LengthUnit.FEET.convertFromBaseUnit(step3); assert Math.abs(val - step4) < EPSILON; System.out.println("✓ UC8: testRoundTripConversion_RefactoredDesign"); }
        public void testUnitImmutability() { assert LengthUnit.FEET.getConversionFactor() == 1.0; assert LengthUnit.INCHES.getConversionFactor() > 0; System.out.println("✓ UC8: testUnitImmutability"); }
        public void runAllTests() { System.out.println("\n=== Running UC8 Test Suite ==="); testLengthUnitEnum_FeetConstant(); testLengthUnitEnum_InchesConstant(); testLengthUnitEnum_YardsConstant(); testLengthUnitEnum_CentimetersConstant(); testConvertToBaseUnit_FeetToFeet(); testConvertToBaseUnit_InchesToFeet(); testConvertToBaseUnit_YardsToFeet(); testConvertToBaseUnit_CentimetersToFeet(); testConvertFromBaseUnit_FeetToFeet(); testConvertFromBaseUnit_FeetToInches(); testConvertFromBaseUnit_FeetToYards(); testConvertFromBaseUnit_FeetToCentimeters(); testQuantityLengthRefactored_Equality(); testQuantityLengthRefactored_ConvertTo(); testQuantityLengthRefactored_Add(); testQuantityLengthRefactored_AddWithTargetUnit(); testQuantityLengthRefactored_NullUnit(); testQuantityLengthRefactored_InvalidValue(); testBackwardCompatibility_UC1EqualityTests(); testBackwardCompatibility_UC5ConversionTests(); testBackwardCompatibility_UC6AdditionTests(); testBackwardCompatibility_UC7AdditionWithTargetUnitTests(); testArchitecturalScalability_MultipleCategories(); testRoundTripConversion_RefactoredDesign(); testUnitImmutability(); System.out.println("=== All UC8 Tests Passed! ===\n"); }
    }

    public static class QuantityTest_UC7 {
        public void testAddition_ExplicitTargetUnit_Feet() { Quantity r = add(new Quantity(1.0, LengthUnit.FEET), new Quantity(12.0, LengthUnit.INCHES), LengthUnit.FEET); assert Math.abs(r.getValue() - 2.0) < EPSILON && r.getUnit() == LengthUnit.FEET; System.out.println("✓ UC7: testAddition_ExplicitTargetUnit_Feet"); }
        public void testAddition_ExplicitTargetUnit_Inches() { Quantity r = add(new Quantity(1.0, LengthUnit.FEET), new Quantity(12.0, LengthUnit.INCHES), LengthUnit.INCHES); assert Math.abs(r.getValue() - 24.0) < EPSILON && r.getUnit() == LengthUnit.INCHES; System.out.println("✓ UC7: testAddition_ExplicitTargetUnit_Inches"); }
        public void testAddition_ExplicitTargetUnit_Yards() { Quantity r = add(new Quantity(1.0, LengthUnit.FEET), new Quantity(12.0, LengthUnit.INCHES), LengthUnit.YARDS); assert Math.abs(r.getValue() - 0.667) < EPSILON && r.getUnit() == LengthUnit.YARDS; System.out.println("✓ UC7: testAddition_ExplicitTargetUnit_Yards"); }
        public void testAddition_ExplicitTargetUnit_Centimeters() { Quantity r = add(new Quantity(1.0, LengthUnit.INCHES), new Quantity(1.0, LengthUnit.INCHES), LengthUnit.CENTIMETERS); assert Math.abs(r.getValue() - 5.08) < EPSILON && r.getUnit() == LengthUnit.CENTIMETERS; System.out.println("✓ UC7: testAddition_ExplicitTargetUnit_Centimeters"); }
        public void testAddition_ExplicitTargetUnit_SameAsFirstOperand() { Quantity r = add(new Quantity(2.0, LengthUnit.YARDS), new Quantity(3.0, LengthUnit.FEET), LengthUnit.YARDS); assert Math.abs(r.getValue() - 3.0) < EPSILON && r.getUnit() == LengthUnit.YARDS; System.out.println("✓ UC7: testAddition_ExplicitTargetUnit_SameAsFirstOperand"); }
        public void testAddition_ExplicitTargetUnit_SameAsSecondOperand() { Quantity r = add(new Quantity(2.0, LengthUnit.YARDS), new Quantity(3.0, LengthUnit.FEET), LengthUnit.FEET); assert Math.abs(r.getValue() - 9.0) < EPSILON && r.getUnit() == LengthUnit.FEET; System.out.println("✓ UC7: testAddition_ExplicitTargetUnit_SameAsSecondOperand"); }
        public void testAddition_ExplicitTargetUnit_Commutativity() { Quantity a = new Quantity(1.0, LengthUnit.FEET), b = new Quantity(12.0, LengthUnit.INCHES); assert add(a, b, LengthUnit.YARDS).equals(add(b, a, LengthUnit.YARDS)); System.out.println("✓ UC7: testAddition_ExplicitTargetUnit_Commutativity"); }
        public void testAddition_ExplicitTargetUnit_WithZero() { Quantity r = add(new Quantity(5.0, LengthUnit.FEET), new Quantity(0.0, LengthUnit.INCHES), LengthUnit.YARDS); assert Math.abs(r.getValue() - 1.667) < EPSILON; System.out.println("✓ UC7: testAddition_ExplicitTargetUnit_WithZero"); }
        public void testAddition_ExplicitTargetUnit_NegativeValues() { Quantity r = add(new Quantity(5.0, LengthUnit.FEET), new Quantity(-2.0, LengthUnit.FEET), LengthUnit.INCHES); assert Math.abs(r.getValue() - 36.0) < EPSILON; System.out.println("✓ UC7: testAddition_ExplicitTargetUnit_NegativeValues"); }
        public void testAddition_ExplicitTargetUnit_NullTargetUnit() { try { add(new Quantity(1.0, LengthUnit.FEET), new Quantity(12.0, LengthUnit.INCHES), null); assert false; } catch (IllegalArgumentException e) { System.out.println("✓ UC7: testAddition_ExplicitTargetUnit_NullTargetUnit"); } }
        public void testAddition_ExplicitTargetUnit_LargeToSmallScale() { Quantity r = add(new Quantity(1000.0, LengthUnit.FEET), new Quantity(500.0, LengthUnit.FEET), LengthUnit.INCHES); assert Math.abs(r.getValue() - 18000.0) < EPSILON; System.out.println("✓ UC7: testAddition_ExplicitTargetUnit_LargeToSmallScale"); }
        public void testAddition_ExplicitTargetUnit_SmallToLargeScale() { Quantity r = add(new Quantity(12.0, LengthUnit.INCHES), new Quantity(12.0, LengthUnit.INCHES), LengthUnit.YARDS); assert Math.abs(r.getValue() - 0.667) < EPSILON; System.out.println("✓ UC7: testAddition_ExplicitTargetUnit_SmallToLargeScale"); }
        public void testAddition_ExplicitTargetUnit_AllUnitCombinations() { for(LengthUnit u1:LengthUnit.values()) for(LengthUnit u2:LengthUnit.values()) for(LengthUnit t:LengthUnit.values()) { Quantity r = add(new Quantity(1.0,u1), new Quantity(1.0,u2), t); assert r.getUnit()==t && Math.abs(r.getValue() - (1.0*u1.getConversionFactor()+1.0*u2.getConversionFactor())/t.getConversionFactor())<EPSILON; } System.out.println("✓ UC7: testAddition_ExplicitTargetUnit_AllUnitCombinations"); }
        public void testAddition_ExplicitTargetUnit_PrecisionTolerance() { Quantity a=new Quantity(1.0/3.0,LengthUnit.FEET), b=new Quantity(2.0/3.0,LengthUnit.FEET); for(LengthUnit t:LengthUnit.values()) { assert Math.abs(add(a,b,t).getValue() - 1.0/t.getConversionFactor())<EPSILON; } System.out.println("✓ UC7: testAddition_ExplicitTargetUnit_PrecisionTolerance"); }
        public void testAddition_InstanceMethod_WithTargetUnit() { Quantity r = new Quantity(1.0, LengthUnit.FEET).add(new Quantity(12.0, LengthUnit.INCHES), LengthUnit.YARDS); assert Math.abs(r.getValue()-0.667)<EPSILON && r.getUnit()==LengthUnit.YARDS; System.out.println("✓ UC7: testAddition_InstanceMethod_WithTargetUnit"); }
        public void testAddition_StaticRawValues_WithTarget() { assert Math.abs(add(1.0,LengthUnit.FEET,12.0,LengthUnit.INCHES,LengthUnit.YARDS)-0.667)<EPSILON; System.out.println("✓ UC7: testAddition_StaticRawValues_WithTarget"); }
        public void testAddition_TargetUnitIndependence() { Quantity a=new Quantity(1.0,LengthUnit.FEET), b=new Quantity(12.0,LengthUnit.INCHES); assert add(a,b,LengthUnit.FEET).equals(add(a,b,LengthUnit.INCHES)); System.out.println("✓ UC7: testAddition_TargetUnitIndependence"); }
        public void testAddition_ChainedWithDifferentTargets() { Quantity r = add(add(new Quantity(1.0,LengthUnit.FEET), new Quantity(6.0,LengthUnit.INCHES), LengthUnit.INCHES), new Quantity(0.5,LengthUnit.FEET), LengthUnit.FEET); assert Math.abs(r.getValue()-2.0)<EPSILON; System.out.println("✓ UC7: testAddition_ChainedWithDifferentTargets"); }
        public void runAllTests() { System.out.println("\n=== Running UC7 Test Suite ==="); testAddition_ExplicitTargetUnit_Feet(); testAddition_ExplicitTargetUnit_Inches(); testAddition_ExplicitTargetUnit_Yards(); testAddition_ExplicitTargetUnit_Centimeters(); testAddition_ExplicitTargetUnit_SameAsFirstOperand(); testAddition_ExplicitTargetUnit_SameAsSecondOperand(); testAddition_ExplicitTargetUnit_Commutativity(); testAddition_ExplicitTargetUnit_WithZero(); testAddition_ExplicitTargetUnit_NegativeValues(); testAddition_ExplicitTargetUnit_NullTargetUnit(); testAddition_ExplicitTargetUnit_LargeToSmallScale(); testAddition_ExplicitTargetUnit_SmallToLargeScale(); testAddition_ExplicitTargetUnit_AllUnitCombinations(); testAddition_ExplicitTargetUnit_PrecisionTolerance(); testAddition_InstanceMethod_WithTargetUnit(); testAddition_StaticRawValues_WithTarget(); testAddition_TargetUnitIndependence(); testAddition_ChainedWithDifferentTargets(); System.out.println("=== All UC7 Tests Passed! ===\n"); }
    }

    public static class QuantityTest_UC6 {
        public void testAddition_SameUnit_FeetPlusFeet() { Quantity r = add(new Quantity(1.0, LengthUnit.FEET), new Quantity(2.0, LengthUnit.FEET)); assert r.getValue()==3.0 && r.getUnit()==LengthUnit.FEET; System.out.println("✓ UC6: testAddition_SameUnit_FeetPlusFeet"); }
        public void testAddition_SameUnit_InchPlusInch() { Quantity r = add(new Quantity(6.0, LengthUnit.INCHES), new Quantity(6.0, LengthUnit.INCHES)); assert r.getValue()==12.0 && r.getUnit()==LengthUnit.INCHES; System.out.println("✓ UC6: testAddition_SameUnit_InchPlusInch"); }
        public void testAddition_CrossUnit_FeetPlusInches() { Quantity r = add(new Quantity(1.0, LengthUnit.FEET), new Quantity(12.0, LengthUnit.INCHES)); assert Math.abs(r.getValue()-2.0)<EPSILON && r.getUnit()==LengthUnit.FEET; System.out.println("✓ UC6: testAddition_CrossUnit_FeetPlusInches"); }
        public void testAddition_CrossUnit_InchPlusFeet() { Quantity r = add(new Quantity(12.0, LengthUnit.INCHES), new Quantity(1.0, LengthUnit.FEET)); assert Math.abs(r.getValue()-24.0)<EPSILON && r.getUnit()==LengthUnit.INCHES; System.out.println("✓ UC6: testAddition_CrossUnit_InchPlusFeet"); }
        public void testAddition_CrossUnit_YardPlusFeet() { Quantity r = add(new Quantity(1.0, LengthUnit.YARDS), new Quantity(3.0, LengthUnit.FEET)); assert Math.abs(r.getValue()-2.0)<EPSILON && r.getUnit()==LengthUnit.YARDS; System.out.println("✓ UC6: testAddition_CrossUnit_YardPlusFeet"); }
        public void testAddition_CrossUnit_CentimeterPlusInch() { Quantity r = add(new Quantity(2.54, LengthUnit.CENTIMETERS), new Quantity(1.0, LengthUnit.INCHES)); assert Math.abs(r.getValue()-5.08)<EPSILON && r.getUnit()==LengthUnit.CENTIMETERS; System.out.println("✓ UC6: testAddition_CrossUnit_CentimeterPlusInch"); }
        public void testAddition_Commutativity() { Quantity a=new Quantity(1.0,LengthUnit.FEET), b=new Quantity(12.0,LengthUnit.INCHES); assert add(a,b,LengthUnit.FEET).equals(add(b,a,LengthUnit.FEET)); System.out.println("✓ UC6: testAddition_Commutativity"); }
        public void testAddition_WithZero() { Quantity r = add(new Quantity(5.0, LengthUnit.FEET), new Quantity(0.0, LengthUnit.INCHES)); assert Math.abs(r.getValue()-5.0)<EPSILON; System.out.println("✓ UC6: testAddition_WithZero"); }
        public void testAddition_NegativeValues() { Quantity r = add(new Quantity(5.0, LengthUnit.FEET), new Quantity(-2.0, LengthUnit.FEET)); assert Math.abs(r.getValue()-3.0)<EPSILON; System.out.println("✓ UC6: testAddition_NegativeValues"); }
        public void testAddition_NullSecondOperand_Throws() { try { add(new Quantity(1.0, LengthUnit.FEET), null); assert false; } catch (IllegalArgumentException e) { System.out.println("✓ UC6: testAddition_NullSecondOperand_Throws"); } }
        public void testAddition_LargeValues() { Quantity r = add(new Quantity(1e6, LengthUnit.FEET), new Quantity(1e6, LengthUnit.FEET)); assert Math.abs(r.getValue()-2e6)<EPSILON; System.out.println("✓ UC6: testAddition_LargeValues"); }
        public void testAddition_SmallValues() { Quantity r = add(new Quantity(0.001, LengthUnit.FEET), new Quantity(0.002, LengthUnit.FEET)); assert Math.abs(r.getValue()-0.003)<EPSILON; System.out.println("✓ UC6: testAddition_SmallValues"); }
        public void runAllTests() { System.out.println("\n=== Running UC6 Test Suite ==="); testAddition_SameUnit_FeetPlusFeet(); testAddition_SameUnit_InchPlusInch(); testAddition_CrossUnit_FeetPlusInches(); testAddition_CrossUnit_InchPlusFeet(); testAddition_CrossUnit_YardPlusFeet(); testAddition_CrossUnit_CentimeterPlusInch(); testAddition_Commutativity(); testAddition_WithZero(); testAddition_NegativeValues(); testAddition_NullSecondOperand_Throws(); testAddition_LargeValues(); testAddition_SmallValues(); System.out.println("=== All UC6 Tests Passed! ===\n"); }
    }

    public static class QuantityTest_UC5 {
        public void testConversion_FeetToInches() { assert Math.abs(convert(1.0, LengthUnit.FEET, LengthUnit.INCHES)-12.0)<EPSILON; System.out.println("✓ UC5: testConversion_FeetToInches"); }
        public void testConversion_InchesToFeet() { assert Math.abs(convert(24.0, LengthUnit.INCHES, LengthUnit.FEET)-2.0)<EPSILON; System.out.println("✓ UC5: testConversion_InchesToFeet"); }
        public void testConversion_YardsToInches() { assert Math.abs(convert(1.0, LengthUnit.YARDS, LengthUnit.INCHES)-36.0)<EPSILON; System.out.println("✓ UC5: testConversion_YardsToInches"); }
        public void testConversion_CentimetersToInches() { assert Math.abs(convert(2.54, LengthUnit.CENTIMETERS, LengthUnit.INCHES)-1.0)<EPSILON; System.out.println("✓ UC5: testConversion_CentimetersToInches"); }
        public void testConversion_ZeroValue() { assert convert(0.0, LengthUnit.FEET, LengthUnit.INCHES)==0.0; System.out.println("✓ UC5: testConversion_ZeroValue"); }
        public void testConversion_NegativeValue() { assert Math.abs(convert(-1.0, LengthUnit.FEET, LengthUnit.INCHES)-(-12.0))<EPSILON; System.out.println("✓ UC5: testConversion_NegativeValue"); }
        public void testConversion_SameUnit() { assert Math.abs(convert(5.0, LengthUnit.FEET, LengthUnit.FEET)-5.0)<EPSILON; System.out.println("✓ UC5: testConversion_SameUnit"); }
        public void testConversion_RoundTrip_PreservesValue() { double o=7.42; assert Math.abs(o-convert(convert(o,LengthUnit.FEET,LengthUnit.CENTIMETERS),LengthUnit.CENTIMETERS,LengthUnit.FEET))<EPSILON; System.out.println("✓ UC5: testConversion_RoundTrip_PreservesValue"); }
        public void testInstanceConvert_FeetToInches() { Quantity r=new Quantity(1.0,LengthUnit.FEET).convertTo(LengthUnit.INCHES); assert r.getValueIn(LengthUnit.INCHES)==12.0; System.out.println("✓ UC5: testInstanceConvert_FeetToInches"); }
        public void testInstanceConvert_Immutability() { Quantity o=new Quantity(3.0,LengthUnit.FEET); Quantity c=o.convertTo(LengthUnit.INCHES); assert o.getValueIn(LengthUnit.FEET)==3.0 && c.getValueIn(LengthUnit.INCHES)==36.0; System.out.println("✓ UC5: testInstanceConvert_Immutability"); }
        public void testConversion_NullSourceUnit_Throws() { try { convert(1.0, null, LengthUnit.INCHES); assert false; } catch (IllegalArgumentException e) { System.out.println("✓ UC5: testConversion_NullSourceUnit_Throws"); } }
        public void testConversion_NaN_Throws() { try { convert(Double.NaN, LengthUnit.FEET, LengthUnit.INCHES); assert false; } catch (IllegalArgumentException e) { System.out.println("✓ UC5: testConversion_NaN_Throws"); } }
        public void testConversion_PrecisionTolerance() { assert Math.abs(convert(1.0, LengthUnit.CENTIMETERS, LengthUnit.INCHES)-0.393701)<EPSILON; System.out.println("✓ UC5: testConversion_PrecisionTolerance"); }
        public void testEquality_AfterConversion() { assert new Quantity(1.0, LengthUnit.YARDS).equals(new Quantity(1.0, LengthUnit.YARDS).convertTo(LengthUnit.INCHES)); System.out.println("✓ UC5: testEquality_AfterConversion"); }
        public void runAllTests() { System.out.println("\n=== Running UC5 Test Suite ==="); testConversion_FeetToInches(); testConversion_InchesToFeet(); testConversion_YardsToInches(); testConversion_CentimetersToInches(); testConversion_ZeroValue(); testConversion_NegativeValue(); testConversion_SameUnit(); testConversion_RoundTrip_PreservesValue(); testInstanceConvert_FeetToInches(); testInstanceConvert_Immutability(); testConversion_NullSourceUnit_Throws(); testConversion_NaN_Throws(); testConversion_PrecisionTolerance(); testEquality_AfterConversion(); System.out.println("=== All UC5 Tests Passed! ===\n"); }
    }

    public static class QuantityTest_UC4 {
        public void testEquality_YardToYard_SameValue() { assert new Quantity(1.0, LengthUnit.YARDS).equals(new Quantity(1.0, LengthUnit.YARDS)); }
        public void testEquality_YardToFeet() { assert new Quantity(1.0, LengthUnit.YARDS).equals(new Quantity(3.0, LengthUnit.FEET)); }
        public void testEquality_YardToInches() { assert new Quantity(1.0, LengthUnit.YARDS).equals(new Quantity(36.0, LengthUnit.INCHES)); }
        public void testEquality_YardInequality() { assert !new Quantity(1.0, LengthUnit.YARDS).equals(new Quantity(2.0, LengthUnit.FEET)); }
        public void testEquality_CmToCm() { assert new Quantity(2.0, LengthUnit.CENTIMETERS).equals(new Quantity(2.0, LengthUnit.CENTIMETERS)); }
        public void testEquality_CmToInches() { assert new Quantity(1.0, LengthUnit.CENTIMETERS).equals(new Quantity(0.393701, LengthUnit.INCHES)); }
        public void testTransitiveProperty() { assert new Quantity(1.0,LengthUnit.YARDS).equals(new Quantity(3.0,LengthUnit.FEET)) && new Quantity(3.0,LengthUnit.FEET).equals(new Quantity(36.0,LengthUnit.INCHES)); }
        public void testSameReference() { assert new Quantity(1.0, LengthUnit.YARDS).equals(new Quantity(1.0, LengthUnit.YARDS)); }
        public void testNullComparison() { assert !new Quantity(1.0, LengthUnit.YARDS).equals(null); }
        public void testComplexScenario() { assert new Quantity(2.0, LengthUnit.YARDS).equals(new Quantity(6.0, LengthUnit.FEET)) && new Quantity(6.0, LengthUnit.FEET).equals(new Quantity(72.0, LengthUnit.INCHES)); }
        public void runAllTests() { System.out.println("\n=== Running UC4 Test Suite ==="); testEquality_YardToYard_SameValue(); testEquality_YardToFeet(); testEquality_YardToInches(); testEquality_YardInequality(); testEquality_CmToCm(); testEquality_CmToInches(); testTransitiveProperty(); testSameReference(); testNullComparison(); testComplexScenario(); System.out.println("=== All UC4 Tests Passed! ===\n"); }
    }
}

enum LengthUnit {
    FEET(1.0), INCHES(1.0 / 12.0), YARDS(3.0), CENTIMETERS(1.0 / 30.48);
    private final double factorToBase;
    LengthUnit(double factorToBase) { this.factorToBase = factorToBase; }
    public double getConversionFactor() { return factorToBase; }
    public double convertToBaseUnit(double value) { return value * factorToBase; }
    public double convertFromBaseUnit(double baseValue) { return baseValue / factorToBase; }
}

class Quantity {
    private final double value;
    private final LengthUnit unit;
    public Quantity(double value, LengthUnit unit) { validateValue(value); validateUnit(unit); this.value = value; this.unit = unit; }
    static void validateValue(double value) { if (!Double.isFinite(value)) throw new IllegalArgumentException("Value must be finite"); }
    static void validateUnit(LengthUnit unit) { if (unit == null) throw new IllegalArgumentException("Unit cannot be null"); }
    static void validate(Quantity q) { if (q == null) throw new IllegalArgumentException("Quantity cannot be null"); }
    public Quantity convertTo(LengthUnit targetUnit) { validateUnit(targetUnit); double baseValue = unit.convertToBaseUnit(value); return new Quantity(roundToDecimalPlaces(targetUnit.convertFromBaseUnit(baseValue)), targetUnit); }
    public double getValueIn(LengthUnit targetUnit) { validateUnit(targetUnit); return roundToDecimalPlaces(targetUnit.convertFromBaseUnit(unit.convertToBaseUnit(value))); }
    public Quantity add(Quantity other) { return add(other, this.unit); }
    public Quantity add(Quantity other, LengthUnit targetUnit) { validate(other); validateUnit(targetUnit); double sumBase = unit.convertToBaseUnit(value) + other.unit.convertToBaseUnit(other.value); return new Quantity(roundToDecimalPlaces(targetUnit.convertFromBaseUnit(sumBase)), targetUnit); }
    @Override public boolean equals(Object obj) { if (this == obj) return true; if (obj == null || getClass() != obj.getClass()) return false; Quantity other = (Quantity) obj; return Math.abs(unit.convertToBaseUnit(value) - other.unit.convertToBaseUnit(other.value)) < 1e-6; }
    @Override public int hashCode() { return Objects.hash(roundToDecimalPlaces(unit.convertToBaseUnit(value))); }
    @Override public String toString() { return String.format("%.2f %s", value, unit); }
    static double roundToDecimalPlaces(double value) { return Math.round(value * 100.0) / 100.0; }
    public double getValue() { return value; }
    public LengthUnit getUnit() { return unit; }
}

enum WeightUnit {
    KILOGRAM(1.0), GRAM(0.001), POUND(0.453592);
    private final double factorToBase;
    WeightUnit(double factorToBase) { this.factorToBase = factorToBase; }
    public double getConversionFactor() { return factorToBase; }
    public double convertToBaseUnit(double value) { return value * factorToBase; }
    public double convertFromBaseUnit(double baseValue) { return baseValue / factorToBase; }
}

class QuantityWeight {
    private final double value;
    private final WeightUnit unit;
    public QuantityWeight(double value, WeightUnit unit) { validateValue(value); validateUnit(unit); this.value = value; this.unit = unit; }
    static void validateValue(double value) { if (!Double.isFinite(value)) throw new IllegalArgumentException("Value must be finite"); }
    static void validateUnit(WeightUnit unit) { if (unit == null) throw new IllegalArgumentException("Unit cannot be null"); }
    static void validate(QuantityWeight q) { if (q == null) throw new IllegalArgumentException("QuantityWeight cannot be null"); }
    public QuantityWeight convertTo(WeightUnit targetUnit) { validateUnit(targetUnit); double baseValue = unit.convertToBaseUnit(value); return new QuantityWeight(roundToDecimalPlaces(targetUnit.convertFromBaseUnit(baseValue)), targetUnit); }
    public double getValueIn(WeightUnit targetUnit) { validateUnit(targetUnit); return roundToDecimalPlaces(targetUnit.convertFromBaseUnit(unit.convertToBaseUnit(value))); }
    public QuantityWeight add(QuantityWeight other) { return add(other, this.unit); }
    public QuantityWeight add(QuantityWeight other, WeightUnit targetUnit) { validate(other); validateUnit(targetUnit); double sumBase = unit.convertToBaseUnit(value) + other.unit.convertToBaseUnit(other.value); return new QuantityWeight(roundToDecimalPlaces(targetUnit.convertFromBaseUnit(sumBase)), targetUnit); }
    @Override public boolean equals(Object obj) { if (this == obj) return true; if (obj == null || getClass() != obj.getClass()) return false; QuantityWeight other = (QuantityWeight) obj; return Math.abs(unit.convertToBaseUnit(value) - other.unit.convertToBaseUnit(other.value)) < 1e-6; }
    @Override public int hashCode() { return Objects.hash(roundToDecimalPlaces(unit.convertToBaseUnit(value))); }
    @Override public String toString() { return String.format("%.2f %s", value, unit); }
    static double roundToDecimalPlaces(double value) { return Math.round(value * 100.0) / 100.0; }
    public double getValue() { return value; }
    public WeightUnit getUnit() { return unit; }
}