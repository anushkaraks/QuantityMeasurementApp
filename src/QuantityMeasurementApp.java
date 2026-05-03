package com.apps.quantitymeasurement;

import java.util.Objects;

public class QuantityMeasurementApp {

    /**
     * Precision epsilon for floating-point comparisons
     */
    private static final double EPSILON = 1e-6;

    /**
     * Default decimal places for rounding converted values
     */
    private static final int DEFAULT_DECIMAL_PLACES = 2;

    /**
     * Enum for all supported length units
     * Conversion factor is relative to base unit (inches)
     *
     * Design Pattern: Enum with encapsulated data and behavior
     * - Type-safe unit representation
     * - Immutable conversion factors
     * - Thread-safe by design
     * - Easy extension: add new constants with factors
     */
    public enum LengthUnit {
        FEET(12.0),
        INCHES(1.0),
        YARDS(36.0),              // 1 yard = 36 inches
        CENTIMETERS(0.393701);    // 1 cm = 0.393701 inches

        private final double conversionFactor;

        LengthUnit(double conversionFactor) {
            this.conversionFactor = conversionFactor;
        }

        /**
         * Gets the conversion factor relative to base unit (inches)
         * @return conversion factor as double
         */
        public double getConversionFactor() {
            return conversionFactor;
        }
    }

    /**
     * Generic Quantity class representing a length measurement
     *
     * Value Object Semantics:
     * - Immutable: state cannot change after creation
     * - Thread-safe: no mutable shared state
     * - Equals/hashCode based on normalized base unit value
     * - convertTo() returns new instance (never modifies this)
     */
    public static class Quantity {
        private final double value;
        private final LengthUnit unit;

        /**
         * Creates a new Quantity instance
         * @param value numeric measurement value (must be finite)
         * @param unit length unit (must not be null)
         * @throws IllegalArgumentException if value is NaN/infinite or unit is null
         */
        public Quantity(double value, LengthUnit unit) {
            validateValue(value);
            validateUnit(unit);
            this.value = value;
            this.unit = unit;
        }

        /**
         * Validates that a numeric value is finite (not NaN or infinite)
         * @param value the value to validate
         * @throws IllegalArgumentException if value is not finite
         */
        private static void validateValue(double value) {
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException(
                        "Value must be finite, got: " + value);
            }
        }

        /**
         * Validates that a unit is not null
         * @param unit the unit to validate
         * @throws IllegalArgumentException if unit is null
         */
        private static void validateUnit(LengthUnit unit) {
            if (unit == null) {
                throw new IllegalArgumentException("Unit cannot be null");
            }
        }

        /**
         * Converts this quantity's value to the base unit (inches)
         * Private helper method - encapsulates conversion logic
         * @return value expressed in inches
         */
        private double toBaseUnit() {
            return this.value * this.unit.getConversionFactor();
        }

        /**
         * UC5 FEATURE: Convert this quantity to a target unit
         *
         * Value Object Pattern: Returns new Quantity instance, never modifies this
         *
         * @param targetUnit the unit to convert to (must not be null)
         * @return new Quantity with value expressed in targetUnit
         * @throws IllegalArgumentException if targetUnit is null
         *
         * Conversion Logic:
         * 1. Convert source value to base unit (inches)
         * 2. Convert from base unit to target unit
         * 3. Apply precision rounding
         */
        public Quantity convertTo(LengthUnit targetUnit) {
            validateUnit(targetUnit);

            // Step 1: Convert to base unit (inches)
            double baseValue = toBaseUnit();

            // Step 2: Convert from base unit to target unit
            double targetValue = baseValue / targetUnit.getConversionFactor();

            // Step 3: Apply precision rounding
            targetValue = roundToDecimalPlaces(targetValue, DEFAULT_DECIMAL_PLACES);

            return new Quantity(targetValue, targetUnit);
        }

        /**
         * UC5 FEATURE: Get raw converted numeric value (without creating new Quantity)
         *
         * @param targetUnit the unit to convert to
         * @return numeric value expressed in targetUnit
         */
        public double getValueIn(LengthUnit targetUnit) {
            validateUnit(targetUnit);
            double baseValue = toBaseUnit();
            double targetValue = baseValue / targetUnit.getConversionFactor();
            return roundToDecimalPlaces(targetValue, DEFAULT_DECIMAL_PLACES);
        }

        /**
         * Compare two quantities for equality (cross-unit comparison)
         * Uses base unit normalization for accurate comparison
         * @param other the quantity to compare with
         * @return true if both represent same physical length
         */
        public boolean compare(Quantity other) {
            if (other == null) return false;
            return Double.compare(this.toBaseUnit(), other.toBaseUnit()) == 0;
        }

        /**
         * Equality override: value-based, cross-unit comparison
         * Two quantities are equal if they represent same length in base unit
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true; // reflexive
            if (obj == null || getClass() != obj.getClass()) return false;
            Quantity other = (Quantity) obj;
            // Use epsilon comparison for floating-point tolerance
            return Math.abs(this.toBaseUnit() - other.toBaseUnit()) < EPSILON;
        }

        @Override
        public int hashCode() {
            // Hash based on rounded base unit value for consistency
            return Objects.hash(roundToDecimalPlaces(toBaseUnit(), DEFAULT_DECIMAL_PLACES));
        }

        @Override
        public String toString() {
            return String.format("%.2f %s", value, unit);
        }

        /**
         * Private utility: Round a double to specified decimal places
         * Centralizes precision handling logic
         * @param value the value to round
         * @param decimalPlaces number of decimal places
         * @return rounded value
         */
        private double roundToDecimalPlaces(double value, int decimalPlaces) {
            double multiplier = Math.pow(10, decimalPlaces);
            return Math.round(value * multiplier) / multiplier;
        }
    }

    // ============================
    // UC5 STATIC CONVERSION API
    // ============================

    /**
     * UC5 FEATURE: Static conversion method - primary API for unit conversion
     *
     * Converts a numeric value from source unit to target unit using
     * base unit normalization. Does not create Quantity objects.
     *
     * @param value the numeric value to convert (must be finite)
     * @param sourceUnit the unit of the input value (must not be null)
     * @param targetUnit the desired output unit (must not be null)
     * @return converted numeric value in targetUnit
     * @throws IllegalArgumentException for null units or non-finite values
     *
     * Formula: result = value × (sourceFactor / targetFactor)
     */
    public static double convert(double value, LengthUnit sourceUnit, LengthUnit targetUnit) {
        Quantity.validateValue(value);
        Quantity.validateUnit(sourceUnit);
        Quantity.validateUnit(targetUnit);

        // Convert to base unit (inches)
        double baseValue = value * sourceUnit.getConversionFactor();

        // Convert from base unit to target unit
        double result = baseValue / targetUnit.getConversionFactor();

        // Apply precision rounding
        return roundToDecimalPlaces(result, DEFAULT_DECIMAL_PLACES);
    }

    /**
     * Private utility method for rounding (shared by static and instance methods)
     */
    private static double roundToDecimalPlaces(double value, int decimalPlaces) {
        double multiplier = Math.pow(10, decimalPlaces);
        return Math.round(value * multiplier) / multiplier;
    }

    // ============================
    // UC5 DEMO METHODS (Method Overloading)
    // ============================

    /**
     * UC5 FEATURE: Method Overloading Example 1
     *
     * Demonstrates conversion using raw numeric value and units
     *
     * @param value numeric value to convert
     * @param fromUnit source unit
     * @param toUnit target unit
     * @return converted Quantity instance
     */
    public static Quantity demonstrateLengthConversion(double value, LengthUnit fromUnit, LengthUnit toUnit) {
        System.out.printf("Converting %.2f %s to %s... ", value, fromUnit, toUnit);
        Quantity quantity = new Quantity(value, fromUnit);
        Quantity result = quantity.convertTo(toUnit);
        System.out.println("Result: " + result);
        return result;
    }

    /**
     * UC5 FEATURE: Method Overloading Example 2
     *
     * Demonstrates conversion using existing Quantity instance
     *
     * @param quantity existing Quantity to convert
     * @param toUnit target unit
     * @return converted Quantity instance
     */
    public static Quantity demonstrateLengthConversion(Quantity quantity, LengthUnit toUnit) {
        System.out.printf("Converting %s to %s... ", quantity, toUnit);
        Quantity result = quantity.convertTo(toUnit);
        System.out.println("Result: " + result);
        return result;
    }

    /**
     * Demonstrates equality checking between two quantities
     */
    public static boolean demonstrateLengthEquality(Quantity q1, Quantity q2) {
        System.out.printf("Checking equality: %s == %s ... ", q1, q2);
        boolean result = q1.equals(q2);
        System.out.println(result);
        return result;
    }

    /**
     * Demonstrates comparison between two quantities
     */
    public static int demonstrateLengthComparison(Quantity q1, Quantity q2) {
        double base1 = q1.toBaseUnit();
        double base2 = q2.toBaseUnit();
        System.out.printf("Comparing %s vs %s (base units: %.4f vs %.4f) ... ",
                q1, q2, base1, base2);
        int result = Double.compare(base1, base2);
        System.out.println(result == 0 ? "equal" : result < 0 ? "less" : "greater");
        return result;
    }

    /**
     * UC4 Demo: Show all unit comparisons (unchanged)
     */
    public static void demonstrateAllUnits() {
        System.out.println("\n=== UC4 Unit Comparisons ===");

        Quantity yard = new Quantity(1.0, LengthUnit.YARDS);
        Quantity feet = new Quantity(3.0, LengthUnit.FEET);
        Quantity inches = new Quantity(36.0, LengthUnit.INCHES);
        Quantity cm = new Quantity(1.0, LengthUnit.CENTIMETERS);

        System.out.println("1 yard == 3 feet: " + yard.equals(feet));
        System.out.println("1 yard == 36 inches: " + yard.equals(inches));
        System.out.println("1 cm == 0.393701 inches: " +
                cm.equals(new Quantity(0.393701, LengthUnit.INCHES)));
    }

    /**
     * UC5 Demo: Show conversion examples
     */
    public static void demonstrateConversions() {
        System.out.println("\n=== UC5 Unit Conversions ===");

        // Basic conversions
        demonstrateLengthConversion(1.0, LengthUnit.FEET, LengthUnit.INCHES);      // 12.0
        demonstrateLengthConversion(3.0, LengthUnit.YARDS, LengthUnit.FEET);       // 9.0
        demonstrateLengthConversion(36.0, LengthUnit.INCHES, LengthUnit.YARDS);    // 1.0
        demonstrateLengthConversion(1.0, LengthUnit.CENTIMETERS, LengthUnit.INCHES); // ~0.39

        // Zero and negative values
        demonstrateLengthConversion(0.0, LengthUnit.FEET, LengthUnit.INCHES);      // 0.0
        demonstrateLengthConversion(-1.0, LengthUnit.FEET, LengthUnit.INCHES);     // -12.0

        // Same-unit conversion (identity)
        demonstrateLengthConversion(5.0, LengthUnit.FEET, LengthUnit.FEET);        // 5.0

        // Using existing Quantity instance (overload #2)
        Quantity lengthInYards = new Quantity(2.0, LengthUnit.YARDS);
        demonstrateLengthConversion(lengthInYards, LengthUnit.INCHES);             // 72.0

        // Static API usage
        System.out.printf("\nStatic API: convert(2.54, CM, INCHES) = %.4f%n",
                convert(2.54, LengthUnit.CENTIMETERS, LengthUnit.INCHES));
    }

    /**
     * UC5 Demo: Test round-trip conversion accuracy
     */
    public static void demonstrateRoundTripConversion() {
        System.out.println("\n=== Round-Trip Conversion Tests ===");

        double originalValue = 5.73;
        LengthUnit unitA = LengthUnit.FEET;
        LengthUnit unitB = LengthUnit.CENTIMETERS;

        // A -> B -> A
        double step1 = convert(originalValue, unitA, unitB);
        double step2 = convert(step1, unitB, unitA);

        System.out.printf("Original: %.4f %s%n", originalValue, unitA);
        System.out.printf("To %s: %.4f%n", unitB, step1);
        System.out.printf("Back to %s: %.4f%n", unitA, step2);
        System.out.printf("Difference: %.8f (epsilon: %.8f) ... %s%n",
                Math.abs(originalValue - step2), EPSILON,
                Math.abs(originalValue - step2) < EPSILON ? "PASS" : "FAIL");
    }

    /**
     * UC5 Demo: Test exception handling
     */
    public static void demonstrateErrorHandling() {
        System.out.println("\n=== Error Handling Tests ===");

        // Test null unit
        try {
            convert(1.0, null, LengthUnit.INCHES);
            System.out.println("ERROR: Should have thrown exception for null source unit");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Null source unit: " + e.getMessage());
        }

        // Test NaN value
        try {
            convert(Double.NaN, LengthUnit.FEET, LengthUnit.INCHES);
            System.out.println("ERROR: Should have thrown exception for NaN value");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ NaN value: " + e.getMessage());
        }

        // Test infinite value
        try {
            convert(Double.POSITIVE_INFINITY, LengthUnit.FEET, LengthUnit.INCHES);
            System.out.println("ERROR: Should have thrown exception for infinite value");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Infinite value: " + e.getMessage());
        }
    }

    // ============================
    // MAIN METHOD
    // ============================

    public static void main(String[] args) {
        System.out.println("=== UC5 Quantity Measurement Demo ===");

        // UC4: Basic equality comparisons
        System.out.println("\n--- UC4: Equality Checks ---");
        Quantity q1 = new Quantity(1.0, LengthUnit.FEET);
        Quantity q2 = new Quantity(12.0, LengthUnit.INCHES);
        System.out.println("1 foot == 12 inches: " + q1.equals(q2));
        demonstrateAllUnits();

        // UC5: Conversion demonstrations
        demonstrateConversions();
        demonstrateRoundTripConversion();
        demonstrateErrorHandling();

        // Edge cases
        System.out.println("\n=== Edge Cases ===");
        Quantity yard = new Quantity(1.0, LengthUnit.YARDS);
        Quantity twoFeet = new Quantity(2.0, LengthUnit.FEET);
        System.out.println("1 yard == 2 feet (false): " + yard.equals(twoFeet));
        System.out.println("Same reference: " + yard.equals(yard));
        System.out.println("Null comparison: " + yard.equals(null));

        // Transitive property
        Quantity inches36 = new Quantity(36.0, LengthUnit.INCHES);
        System.out.println("\nTransitive Property:");
        System.out.println("1 yard == 3 feet: " + yard.equals(q1));
        System.out.println("3 feet == 36 inches: " + q1.equals(inches36));
        System.out.println("1 yard == 36 inches: " + yard.equals(inches36));
    }

    // ============================
    // UC5 TEST CASES
    // ============================

    /**
     * Comprehensive test suite for UC5 conversion features
     * Run tests by calling individual methods or integrating with JUnit
     */
    public static class QuantityTest {

        // ===== Basic Conversion Tests =====

        public void testConversion_FeetToInches() {
            double result = convert(1.0, LengthUnit.FEET, LengthUnit.INCHES);
            assert Math.abs(result - 12.0) < EPSILON : "Expected 12.0, got " + result;
            System.out.println("✓ testConversion_FeetToInches");
        }

        public void testConversion_InchesToFeet() {
            double result = convert(24.0, LengthUnit.INCHES, LengthUnit.FEET);
            assert Math.abs(result - 2.0) < EPSILON : "Expected 2.0, got " + result;
            System.out.println("✓ testConversion_InchesToFeet");
        }

        public void testConversion_YardsToInches() {
            double result = convert(1.0, LengthUnit.YARDS, LengthUnit.INCHES);
            assert Math.abs(result - 36.0) < EPSILON : "Expected 36.0, got " + result;
            System.out.println("✓ testConversion_YardsToInches");
        }

        public void testConversion_CentimetersToInches() {
            double result = convert(2.54, LengthUnit.CENTIMETERS, LengthUnit.INCHES);
            assert Math.abs(result - 1.0) < EPSILON : "Expected ~1.0, got " + result;
            System.out.println("✓ testConversion_CentimetersToInches");
        }

        // ===== Edge Case Tests =====

        public void testConversion_ZeroValue() {
            double result = convert(0.0, LengthUnit.FEET, LengthUnit.INCHES);
            assert result == 0.0 : "Expected 0.0, got " + result;
            System.out.println("✓ testConversion_ZeroValue");
        }

        public void testConversion_NegativeValue() {
            double result = convert(-1.0, LengthUnit.FEET, LengthUnit.INCHES);
            assert Math.abs(result - (-12.0)) < EPSILON : "Expected -12.0, got " + result;
            System.out.println("✓ testConversion_NegativeValue");
        }

        public void testConversion_SameUnit() {
            double result = convert(5.0, LengthUnit.FEET, LengthUnit.FEET);
            assert Math.abs(result - 5.0) < EPSILON : "Expected 5.0, got " + result;
            System.out.println("✓ testConversion_SameUnit");
        }

        public void testConversion_LargeValue() {
            double result = convert(1000000.0, LengthUnit.FEET, LengthUnit.INCHES);
            assert Math.abs(result - 12000000.0) < EPSILON : "Expected 12000000.0, got " + result;
            System.out.println("✓ testConversion_LargeValue");
        }

        public void testConversion_SmallValue() {
            double result = convert(0.001, LengthUnit.FEET, LengthUnit.INCHES);
            assert Math.abs(result - 0.012) < EPSILON : "Expected 0.012, got " + result;
            System.out.println("✓ testConversion_SmallValue");
        }

        // ===== Round-Trip Tests =====

        public void testConversion_RoundTrip_PreservesValue() {
            double original = 7.42;
            double converted = convert(original, LengthUnit.FEET, LengthUnit.CENTIMETERS);
            double back = convert(converted, LengthUnit.CENTIMETERS, LengthUnit.FEET);
            assert Math.abs(original - back) < EPSILON :
                    "Round-trip failed: " + original + " -> " + converted + " -> " + back;
            System.out.println("✓ testConversion_RoundTrip_PreservesValue");
        }

        public void testConversion_MultiStep_PreservesValue() {
            double v = 3.14;
            double aToB = convert(v, LengthUnit.YARDS, LengthUnit.INCHES);
            double bToC = convert(aToB, LengthUnit.INCHES, LengthUnit.CENTIMETERS);
            double cToA = convert(bToC, LengthUnit.CENTIMETERS, LengthUnit.YARDS);
            assert Math.abs(v - cToA) < EPSILON : "Multi-step conversion failed";
            System.out.println("✓ testConversion_MultiStep_PreservesValue");
        }

        // ===== Instance Method Tests =====

        public void testInstanceConvert_FeetToInches() {
            Quantity q = new Quantity(1.0, LengthUnit.FEET);
            Quantity result = q.convertTo(LengthUnit.INCHES);
            assert result.getValueIn(LengthUnit.INCHES) == 12.0 : "Expected 12.0";
            assert result.unit == LengthUnit.INCHES : "Unit should be INCHES";
            System.out.println("✓ testInstanceConvert_FeetToInches");
        }

        public void testInstanceConvert_Immutability() {
            Quantity original = new Quantity(3.0, LengthUnit.FEET);
            Quantity converted = original.convertTo(LengthUnit.INCHES);
            // Original should be unchanged
            assert original.getValueIn(LengthUnit.FEET) == 3.0 : "Original was modified!";
            assert converted.getValueIn(LengthUnit.INCHES) == 36.0 : "Conversion failed";
            System.out.println("✓ testInstanceConvert_Immutability");
        }

        // ===== Exception Tests =====

        public void testConversion_NullSourceUnit_Throws() {
            try {
                convert(1.0, null, LengthUnit.INCHES);
                assert false : "Should have thrown IllegalArgumentException";
            } catch (IllegalArgumentException e) {
                System.out.println("✓ testConversion_NullSourceUnit_Throws");
            }
        }

        public void testConversion_NullTargetUnit_Throws() {
            try {
                convert(1.0, LengthUnit.FEET, null);
                assert false : "Should have thrown IllegalArgumentException";
            } catch (IllegalArgumentException e) {
                System.out.println("✓ testConversion_NullTargetUnit_Throws");
            }
        }

        public void testConversion_NaN_Throws() {
            try {
                convert(Double.NaN, LengthUnit.FEET, LengthUnit.INCHES);
                assert false : "Should have thrown IllegalArgumentException";
            } catch (IllegalArgumentException e) {
                System.out.println("✓ testConversion_NaN_Throws");
            }
        }

        public void testConversion_Infinite_Throws() {
            try {
                convert(Double.POSITIVE_INFINITY, LengthUnit.FEET, LengthUnit.INCHES);
                assert false : "Should have thrown IllegalArgumentException";
            } catch (IllegalArgumentException e) {
                System.out.println("✓ testConversion_Infinite_Throws");
            }
        }

        // ===== Precision Tests =====

        public void testConversion_PrecisionTolerance() {
            // Test that results are within epsilon tolerance
            double result = convert(1.0, LengthUnit.CENTIMETERS, LengthUnit.INCHES);
            double expected = 0.393701;
            assert Math.abs(result - expected) < EPSILON :
                    "Result " + result + " not within epsilon of " + expected;
            System.out.println("✓ testConversion_PrecisionTolerance");
        }

        public void testConversion_RoundingConsistency() {
            // Multiple conversions should round consistently
            double r1 = convert(1.0/3.0, LengthUnit.FEET, LengthUnit.INCHES);
            double r2 = convert(1.0/3.0, LengthUnit.FEET, LengthUnit.INCHES);
            assert r1 == r2 : "Rounding not consistent";
            System.out.println("✓ testConversion_RoundingConsistency");
        }

        // ===== Equality with Converted Values =====

        public void testEquality_AfterConversion() {
            Quantity q1 = new Quantity(1.0, LengthUnit.YARDS);
            Quantity q2 = q1.convertTo(LengthUnit.INCHES);
            // Should be equal even though units differ
            assert q1.equals(q2) : "Converted quantity should equal original";
            System.out.println("✓ testEquality_AfterConversion");
        }

        // Run all tests
        public void runAllTests() {
            System.out.println("\n=== Running UC5 Test Suite ===\n");

            // Basic conversions
            testConversion_FeetToInches();
            testConversion_InchesToFeet();
            testConversion_YardsToInches();
            testConversion_CentimetersToInches();

            // Edge cases
            testConversion_ZeroValue();
            testConversion_NegativeValue();
            testConversion_SameUnit();
            testConversion_LargeValue();
            testConversion_SmallValue();

            // Round-trip
            testConversion_RoundTrip_PreservesValue();
            testConversion_MultiStep_PreservesValue();

            // Instance methods
            testInstanceConvert_FeetToInches();
            testInstanceConvert_Immutability();

            // Exceptions
            testConversion_NullSourceUnit_Throws();
            testConversion_NullTargetUnit_Throws();
            testConversion_NaN_Throws();
            testConversion_Infinite_Throws();

            // Precision
            testConversion_PrecisionTolerance();
            testConversion_RoundingConsistency();

            // Equality
            testEquality_AfterConversion();

            System.out.println("\n=== All UC5 Tests Passed! ===\n");
        }
    }

    // ============================
    // UC4 TEST CASES (Preserved)
    // ============================

    public static class QuantityTest_UC4 {

        // Yard tests
        public void testEquality_YardToYard_SameValue() {
            assert new Quantity(1.0, LengthUnit.YARDS)
                    .equals(new Quantity(1.0, LengthUnit.YARDS));
        }

        public void testEquality_YardToFeet() {
            assert new Quantity(1.0, LengthUnit.YARDS)
                    .equals(new Quantity(3.0, LengthUnit.FEET));
        }

        public void testEquality_YardToInches() {
            assert new Quantity(1.0, LengthUnit.YARDS)
                    .equals(new Quantity(36.0, LengthUnit.INCHES));
        }

        public void testEquality_YardInequality() {
            assert !new Quantity(1.0, LengthUnit.YARDS)
                    .equals(new Quantity(2.0, LengthUnit.FEET));
        }

        // CM tests
        public void testEquality_CmToCm() {
            assert new Quantity(2.0, LengthUnit.CENTIMETERS)
                    .equals(new Quantity(2.0, LengthUnit.CENTIMETERS));
        }

        public void testEquality_CmToInches() {
            assert new Quantity(1.0, LengthUnit.CENTIMETERS)
                    .equals(new Quantity(0.393701, LengthUnit.INCHES));
        }

        public void testEquality_CmToFeet_Inequality() {
            assert !new Quantity(1.0, LengthUnit.CENTIMETERS)
                    .equals(new Quantity(1.0, LengthUnit.FEET));
        }

        // Transitive
        public void testTransitiveProperty() {
            Quantity yard = new Quantity(1.0, LengthUnit.YARDS);
            Quantity feet = new Quantity(3.0, LengthUnit.FEET);
            Quantity inches = new Quantity(36.0, LengthUnit.INCHES);

            assert yard.equals(feet);
            assert feet.equals(inches);
            assert yard.equals(inches);
        }

        // General
        public void testSameReference() {
            Quantity q = new Quantity(1.0, LengthUnit.YARDS);
            assert q.equals(q);
        }

        public void testNullComparison() {
            Quantity q = new Quantity(1.0, LengthUnit.YARDS);
            assert !q.equals(null);
        }

        public void testComplexScenario() {
            assert new Quantity(2.0, LengthUnit.YARDS)
                    .equals(new Quantity(6.0, LengthUnit.FEET));

            assert new Quantity(6.0, LengthUnit.FEET)
                    .equals(new Quantity(72.0, LengthUnit.INCHES));
        }
    }
}