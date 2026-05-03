package com.apps.quantitymeasurement;

import java.util.Objects;

public class QuantityMeasurementApp {

    private static final double EPSILON = 1e-6;


    private static final int DEFAULT_DECIMAL_PLACES = 2;


    public enum LengthUnit {
        FEET(12.0),
        INCHES(1.0),
        YARDS(36.0),              // 1 yard = 36 inches
        CENTIMETERS(0.393701);    // 1 cm = 0.393701 inches

        private final double conversionFactor;

        LengthUnit(double conversionFactor) {
            this.conversionFactor = conversionFactor;
        }


        public double getConversionFactor() {
            return conversionFactor;
        }
    }


    public static class Quantity {
        private final double value;
        private final LengthUnit unit;


        public Quantity(double value, LengthUnit unit) {
            validateValue(value);
            validateUnit(unit);
            this.value = value;
            this.unit = unit;
        }


        private static void validateValue(double value) {
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException(
                        "Value must be finite, got: " + value);
            }
        }


        private static void validateUnit(LengthUnit unit) {
            if (unit == null) {
                throw new IllegalArgumentException("Unit cannot be null");
            }
        }


        private double toBaseUnit() {
            return this.value * this.unit.getConversionFactor();
        }


        public Quantity convertTo(LengthUnit targetUnit) {
            validateUnit(targetUnit);
            double baseValue = toBaseUnit();
            double targetValue = baseValue / targetUnit.getConversionFactor();
            targetValue = roundToDecimalPlaces(targetValue, DEFAULT_DECIMAL_PLACES);
            return new Quantity(targetValue, targetUnit);
        }


        public double getValueIn(LengthUnit targetUnit) {
            validateUnit(targetUnit);
            double baseValue = toBaseUnit();
            double targetValue = baseValue / targetUnit.getConversionFactor();
            return roundToDecimalPlaces(targetValue, DEFAULT_DECIMAL_PLACES);
        }


        public Quantity add(Quantity other) {
            if (other == null) {
                throw new IllegalArgumentException("Cannot add null Quantity");
            }
            // Convert both to base unit, add, convert back to this unit
            double thisBase = this.toBaseUnit();
            double otherBase = other.toBaseUnit();
            double sumBase = thisBase + otherBase;
            double resultValue = sumBase / this.unit.getConversionFactor();
            resultValue = roundToDecimalPlaces(resultValue, DEFAULT_DECIMAL_PLACES);
            return new Quantity(resultValue, this.unit);
        }


        public Quantity add(Quantity other, LengthUnit targetUnit) {
            if (other == null) {
                throw new IllegalArgumentException("Cannot add null Quantity");
            }
            validateUnit(targetUnit);
            // Convert both to base unit, add, convert to target unit
            double thisBase = this.toBaseUnit();
            double otherBase = other.toBaseUnit();
            double sumBase = thisBase + otherBase;
            double resultValue = sumBase / targetUnit.getConversionFactor();
            resultValue = roundToDecimalPlaces(resultValue, DEFAULT_DECIMAL_PLACES);
            return new Quantity(resultValue, targetUnit);
        }


        public boolean compare(Quantity other) {
            if (other == null) return false;
            return Double.compare(this.toBaseUnit(), other.toBaseUnit()) == 0;
        }


        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Quantity other = (Quantity) obj;
            return Math.abs(this.toBaseUnit() - other.toBaseUnit()) < EPSILON;
        }

        @Override
        public int hashCode() {
            return Objects.hash(roundToDecimalPlaces(toBaseUnit(), DEFAULT_DECIMAL_PLACES));
        }

        @Override
        public String toString() {
            return String.format("%.2f %s", value, unit);
        }

        /**
         * Private utility: Round a double to specified decimal places
         * @param value the value to round
         * @param decimalPlaces number of decimal places
         * @return rounded value
         */
        private double roundToDecimalPlaces(double value, int decimalPlaces) {
            double multiplier = Math.pow(10, decimalPlaces);
            return Math.round(value * multiplier) / multiplier;
        }

        // Getters for testing
        public double getValue() { return value; }
        public LengthUnit getUnit() { return unit; }
    }

    // ============================
    // UC5/UC6 STATIC CONVERSION & ARITHMETIC API
    // ============================

    /**
     * UC5 FEATURE: Static conversion method
     */
    public static double convert(double value, LengthUnit sourceUnit, LengthUnit targetUnit) {
        Quantity.validateValue(value);
        Quantity.validateUnit(sourceUnit);
        Quantity.validateUnit(targetUnit);
        double baseValue = value * sourceUnit.getConversionFactor();
        double result = baseValue / targetUnit.getConversionFactor();
        return roundToDecimalPlaces(result, DEFAULT_DECIMAL_PLACES);
    }

    /**
     * UC6 FEATURE: Static addition method - result in first operand's unit
     *
     * @param q1 first quantity (determines result unit)
     * @param q2 second quantity to add
     * @return new Quantity with sum in q1's unit
     * @throws IllegalArgumentException if either quantity is null
     */
    public static Quantity add(Quantity q1, Quantity q2) {
        if (q1 == null || q2 == null) {
            throw new IllegalArgumentException("Quantities cannot be null");
        }
        return q1.add(q2); // Delegate to instance method
    }

    /**
     * UC6 FEATURE: Static addition method - result in specified target unit
     *
     * @param q1 first quantity
     * @param q2 second quantity to add
     * @param targetUnit unit for the result
     * @return new Quantity with sum in targetUnit
     * @throws IllegalArgumentException if any parameter is null
     */
    public static Quantity add(Quantity q1, Quantity q2, LengthUnit targetUnit) {
        if (q1 == null || q2 == null) {
            throw new IllegalArgumentException("Quantities cannot be null");
        }
        Quantity.validateUnit(targetUnit);
        return q1.add(q2, targetUnit); // Delegate to instance method
    }

    /**
     * UC6 FEATURE: Static addition with raw values
     *
     * @param v1 value of first operand
     * @param u1 unit of first operand
     * @param v2 value of second operand
     * @param u2 unit of second operand
     * @param targetUnit unit for result
     * @return numeric sum in targetUnit
     */
    public static double add(double v1, LengthUnit u1, double v2, LengthUnit u2, LengthUnit targetUnit) {
        Quantity.validateValue(v1);
        Quantity.validateValue(v2);
        Quantity.validateUnit(u1);
        Quantity.validateUnit(u2);
        Quantity.validateUnit(targetUnit);

        double base1 = v1 * u1.getConversionFactor();
        double base2 = v2 * u2.getConversionFactor();
        double sumBase = base1 + base2;
        double result = sumBase / targetUnit.getConversionFactor();
        return roundToDecimalPlaces(result, DEFAULT_DECIMAL_PLACES);
    }

    /**
     * Private utility method for rounding
     */
    private static double roundToDecimalPlaces(double value, int decimalPlaces) {
        double multiplier = Math.pow(10, decimalPlaces);
        return Math.round(value * multiplier) / multiplier;
    }

    // ============================
    // UC5/UC6 DEMO METHODS
    // ============================

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

    /**
     * UC6 FEATURE: Demonstrate addition with two Quantities (result in first unit)
     */
    public static Quantity demonstrateAddition(Quantity q1, Quantity q2) {
        System.out.printf("Adding %s + %s ... ", q1, q2);
        Quantity result = add(q1, q2);
        System.out.println("Result: " + result);
        return result;
    }

    /**
     * UC6 FEATURE: Demonstrate addition with target unit specification
     */
    public static Quantity demonstrateAddition(Quantity q1, Quantity q2, LengthUnit targetUnit) {
        System.out.printf("Adding %s + %s (result in %s) ... ", q1, q2, targetUnit);
        Quantity result = add(q1, q2, targetUnit);
        System.out.println("Result: " + result);
        return result;
    }

    /**
     * UC6 FEATURE: Demonstrate addition with raw values
     */
    public static double demonstrateAddition(double v1, LengthUnit u1, double v2, LengthUnit u2, LengthUnit targetUnit) {
        System.out.printf("Adding %.2f %s + %.2f %s (result in %s) ... ", v1, u1, v2, u2, targetUnit);
        double result = add(v1, u1, v2, u2, targetUnit);
        System.out.printf("Result: %.2f%n", result);
        return result;
    }

    public static boolean demonstrateLengthEquality(Quantity q1, Quantity q2) {
        System.out.printf("Checking equality: %s == %s ... ", q1, q2);
        boolean result = q1.equals(q2);
        System.out.println(result);
        return result;
    }

    public static int demonstrateLengthComparison(Quantity q1, Quantity q2) {
        double base1 = q1.toBaseUnit();
        double base2 = q2.toBaseUnit();
        System.out.printf("Comparing %s vs %s (base: %.4f vs %.4f) ... ", q1, q2, base1, base2);
        int result = Double.compare(base1, base2);
        System.out.println(result == 0 ? "equal" : result < 0 ? "less" : "greater");
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
        System.out.println("1 cm == 0.393701 inches: " +
                cm.equals(new Quantity(0.393701, LengthUnit.INCHES)));
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
        System.out.printf("Static API: convert(2.54, CM, INCHES) = %.4f%n",
                convert(2.54, LengthUnit.CENTIMETERS, LengthUnit.INCHES));
    }

    /**
     * UC6 FEATURE: Demonstrate addition examples
     */
    public static void demonstrateAdditions() {
        System.out.println("\n=== UC6 Addition Examples ===");

        // Same-unit addition
        demonstrateAddition(new Quantity(1.0, LengthUnit.FEET), new Quantity(2.0, LengthUnit.FEET));
        // Expected: 3.0 FEET

        // Cross-unit: feet + inches -> result in feet
        demonstrateAddition(new Quantity(1.0, LengthUnit.FEET), new Quantity(12.0, LengthUnit.INCHES));
        // Expected: 2.0 FEET

        // Cross-unit: inches + feet -> result in inches
        demonstrateAddition(new Quantity(12.0, LengthUnit.INCHES), new Quantity(1.0, LengthUnit.FEET));
        // Expected: 24.0 INCHES

        // Cross-unit: yards + feet -> result in yards
        demonstrateAddition(new Quantity(1.0, LengthUnit.YARDS), new Quantity(3.0, LengthUnit.FEET));
        // Expected: 2.0 YARDS

        // Cross-unit: inches + yards -> result in inches
        demonstrateAddition(new Quantity(36.0, LengthUnit.INCHES), new Quantity(1.0, LengthUnit.YARDS));
        // Expected: 72.0 INCHES

        // Cross-unit: cm + inches -> result in cm
        demonstrateAddition(new Quantity(2.54, LengthUnit.CENTIMETERS), new Quantity(1.0, LengthUnit.INCHES));
        // Expected: ~5.08 CENTIMETERS

        // Identity element (adding zero)
        demonstrateAddition(new Quantity(5.0, LengthUnit.FEET), new Quantity(0.0, LengthUnit.INCHES));
        // Expected: 5.0 FEET

        // Negative values
        demonstrateAddition(new Quantity(5.0, LengthUnit.FEET), new Quantity(-2.0, LengthUnit.FEET));
        // Expected: 3.0 FEET

        // Addition with explicit target unit
        demonstrateAddition(
                new Quantity(1.0, LengthUnit.FEET),
                new Quantity(12.0, LengthUnit.INCHES),
                LengthUnit.INCHES
        );
        // Expected: 24.0 INCHES

        // Static raw value addition
        demonstrateAddition(1.0, LengthUnit.YARDS, 3.0, LengthUnit.FEET, LengthUnit.FEET);
        // Expected: 6.0 FEET (1 yard = 3 feet, + 3 feet = 6 feet)
    }

    /**
     * UC6 FEATURE: Test commutativity of addition
     */
    public static void demonstrateCommutativity() {
        System.out.println("\n=== Commutativity Test ===");
        Quantity feet1 = new Quantity(1.0, LengthUnit.FEET);
        Quantity inches12 = new Quantity(12.0, LengthUnit.INCHES);

        Quantity result1 = add(feet1, inches12); // Result in FEET
        Quantity result2 = add(inches12, feet1); // Result in INCHES

        System.out.println("add(1ft, 12in) in feet: " + result1);
        System.out.println("add(12in, 1ft) in inches: " + result2);

        // Verify they represent same physical length
        boolean sameLength = result1.equals(result2);
        System.out.println("Same physical length: " + sameLength);

        // Test with same target unit
        Quantity r1 = add(feet1, inches12, LengthUnit.FEET);
        Quantity r2 = add(inches12, feet1, LengthUnit.FEET);
        System.out.println("With same target unit (FEET): " + r1 + " == " + r2 + " ? " + r1.equals(r2));
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
        System.out.println("\n=== Error Handling Tests ===");
        try {
            convert(1.0, null, LengthUnit.INCHES);
            System.out.println("ERROR: Should have thrown exception for null source unit");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Null source unit: " + e.getMessage());
        }
        try {
            convert(Double.NaN, LengthUnit.FEET, LengthUnit.INCHES);
            System.out.println("ERROR: Should have thrown exception for NaN value");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ NaN value: " + e.getMessage());
        }
        try {
            convert(Double.POSITIVE_INFINITY, LengthUnit.FEET, LengthUnit.INCHES);
            System.out.println("ERROR: Should have thrown exception for infinite value");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Infinite value: " + e.getMessage());
        }
        // UC6: Test null operand in addition
        try {
            add(new Quantity(1.0, LengthUnit.FEET), null);
            System.out.println("ERROR: Should have thrown exception for null operand");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Null operand in add: " + e.getMessage());
        }
    }

    // ============================
    // MAIN METHOD
    // ============================

    public static void main(String[] args) {
        System.out.println("=== UC6 Quantity Measurement Demo ===");

        // UC4: Basic equality comparisons
        System.out.println("\n--- UC4: Equality Checks ---");
        Quantity q1 = new Quantity(1.0, LengthUnit.FEET);
        Quantity q2 = new Quantity(12.0, LengthUnit.INCHES);
        System.out.println("1 foot == 12 inches: " + q1.equals(q2));
        demonstrateAllUnits();

        // UC5: Conversion demonstrations
        demonstrateConversions();
        demonstrateRoundTripConversion();

        // UC6: Addition demonstrations
        demonstrateAdditions();
        demonstrateCommutativity();

        // Error handling
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

        // Run tests
        System.out.println("\n=== Running Test Suites ===");
        new QuantityTest_UC5().runAllTests();
        new QuantityTest_UC6().runAllTests();
    }

    // ============================
    // UC6 TEST CASES
    // ============================

    /**
     * Comprehensive test suite for UC6 addition features
     */
    public static class QuantityTest_UC6 {

        // ===== Same-Unit Addition Tests =====

        public void testAddition_SameUnit_FeetPlusFeet() {
            Quantity result = add(
                    new Quantity(1.0, LengthUnit.FEET),
                    new Quantity(2.0, LengthUnit.FEET)
            );
            assert result.getValue() == 3.0 : "Expected 3.0, got " + result.getValue();
            assert result.getUnit() == LengthUnit.FEET : "Expected FEET unit";
            System.out.println("✓ testAddition_SameUnit_FeetPlusFeet");
        }

        public void testAddition_SameUnit_InchPlusInch() {
            Quantity result = add(
                    new Quantity(6.0, LengthUnit.INCHES),
                    new Quantity(6.0, LengthUnit.INCHES)
            );
            assert result.getValue() == 12.0 : "Expected 12.0, got " + result.getValue();
            assert result.getUnit() == LengthUnit.INCHES : "Expected INCHES unit";
            System.out.println("✓ testAddition_SameUnit_InchPlusInch");
        }

        public void testAddition_SameUnit_YardPlusYard() {
            Quantity result = add(
                    new Quantity(2.0, LengthUnit.YARDS),
                    new Quantity(3.0, LengthUnit.YARDS)
            );
            assert result.getValue() == 5.0 : "Expected 5.0, got " + result.getValue();
            System.out.println("✓ testAddition_SameUnit_YardPlusYard");
        }

        // ===== Cross-Unit Addition Tests =====

        public void testAddition_CrossUnit_FeetPlusInches() {
            Quantity result = add(
                    new Quantity(1.0, LengthUnit.FEET),
                    new Quantity(12.0, LengthUnit.INCHES)
            );
            // 1ft + 12in = 1ft + 1ft = 2ft (result in first operand's unit: FEET)
            assert Math.abs(result.getValue() - 2.0) < EPSILON : "Expected 2.0, got " + result.getValue();
            assert result.getUnit() == LengthUnit.FEET : "Expected FEET unit";
            System.out.println("✓ testAddition_CrossUnit_FeetPlusInches");
        }

        public void testAddition_CrossUnit_InchPlusFeet() {
            Quantity result = add(
                    new Quantity(12.0, LengthUnit.INCHES),
                    new Quantity(1.0, LengthUnit.FEET)
            );
            // 12in + 1ft = 12in + 12in = 24in (result in first operand's unit: INCHES)
            assert Math.abs(result.getValue() - 24.0) < EPSILON : "Expected 24.0, got " + result.getValue();
            assert result.getUnit() == LengthUnit.INCHES : "Expected INCHES unit";
            System.out.println("✓ testAddition_CrossUnit_InchPlusFeet");
        }

        public void testAddition_CrossUnit_YardPlusFeet() {
            Quantity result = add(
                    new Quantity(1.0, LengthUnit.YARDS),
                    new Quantity(3.0, LengthUnit.FEET)
            );
            // 1yd + 3ft = 3ft + 3ft = 6ft = 2yd (result in YARDS)
            assert Math.abs(result.getValue() - 2.0) < EPSILON : "Expected 2.0, got " + result.getValue();
            assert result.getUnit() == LengthUnit.YARDS : "Expected YARDS unit";
            System.out.println("✓ testAddition_CrossUnit_YardPlusFeet");
        }

        public void testAddition_CrossUnit_CentimeterPlusInch() {
            Quantity result = add(
                    new Quantity(2.54, LengthUnit.CENTIMETERS),
                    new Quantity(1.0, LengthUnit.INCHES)
            );
            // 2.54cm + 1in = 1in + 1in = 2in = ~5.08cm (result in CENTIMETERS)
            assert Math.abs(result.getValue() - 5.08) < EPSILON : "Expected ~5.08, got " + result.getValue();
            assert result.getUnit() == LengthUnit.CENTIMETERS : "Expected CENTIMETERS unit";
            System.out.println("✓ testAddition_CrossUnit_CentimeterPlusInch");
        }

        public void testAddition_WithExplicitTargetUnit() {
            Quantity result = add(
                    new Quantity(1.0, LengthUnit.FEET),
                    new Quantity(12.0, LengthUnit.INCHES),
                    LengthUnit.INCHES
            );
            // Result forced to INCHES: 1ft + 12in = 24in
            assert Math.abs(result.getValue() - 24.0) < EPSILON : "Expected 24.0, got " + result.getValue();
            assert result.getUnit() == LengthUnit.INCHES : "Expected INCHES unit";
            System.out.println("✓ testAddition_WithExplicitTargetUnit");
        }

        public void testAddition_StaticRawValues() {
            double result = add(1.0, LengthUnit.YARDS, 3.0, LengthUnit.FEET, LengthUnit.FEET);
            // 1yd + 3ft = 3ft + 3ft = 6ft
            assert Math.abs(result - 6.0) < EPSILON : "Expected 6.0, got " + result;
            System.out.println("✓ testAddition_StaticRawValues");
        }

        // ===== Mathematical Property Tests =====

        public void testAddition_Commutativity() {
            Quantity a = new Quantity(1.0, LengthUnit.FEET);
            Quantity b = new Quantity(12.0, LengthUnit.INCHES);

            // With same target unit, results should be equal
            Quantity r1 = add(a, b, LengthUnit.FEET);
            Quantity r2 = add(b, a, LengthUnit.FEET);

            assert r1.equals(r2) : "Commutativity failed: " + r1 + " != " + r2;
            System.out.println("✓ testAddition_Commutativity");
        }

        public void testAddition_Associativity() {
            Quantity a = new Quantity(1.0, LengthUnit.FEET);
            Quantity b = new Quantity(6.0, LengthUnit.INCHES);
            Quantity c = new Quantity(0.5, LengthUnit.FEET);

            // (a + b) + c vs a + (b + c) - both in FEET
            Quantity left = add(add(a, b, LengthUnit.FEET), c, LengthUnit.FEET);
            Quantity right = add(a, add(b, c, LengthUnit.FEET), LengthUnit.FEET);

            assert left.equals(right) : "Associativity failed";
            System.out.println("✓ testAddition_Associativity");
        }

        public void testAddition_WithZero() {
            Quantity result = add(
                    new Quantity(5.0, LengthUnit.FEET),
                    new Quantity(0.0, LengthUnit.INCHES)
            );
            assert Math.abs(result.getValue() - 5.0) < EPSILON : "Expected 5.0, got " + result.getValue();
            System.out.println("✓ testAddition_WithZero");
        }

        public void testAddition_NegativeValues() {
            Quantity result = add(
                    new Quantity(5.0, LengthUnit.FEET),
                    new Quantity(-2.0, LengthUnit.FEET)
            );
            assert Math.abs(result.getValue() - 3.0) < EPSILON : "Expected 3.0, got " + result.getValue();
            System.out.println("✓ testAddition_NegativeValues");
        }

        public void testAddition_NegativeResult() {
            Quantity result = add(
                    new Quantity(2.0, LengthUnit.FEET),
                    new Quantity(-5.0, LengthUnit.FEET)
            );
            assert Math.abs(result.getValue() - (-3.0)) < EPSILON : "Expected -3.0, got " + result.getValue();
            System.out.println("✓ testAddition_NegativeResult");
        }

        // ===== Edge Case Tests =====

        public void testAddition_LargeValues() {
            Quantity result = add(
                    new Quantity(1e6, LengthUnit.FEET),
                    new Quantity(1e6, LengthUnit.FEET)
            );
            assert Math.abs(result.getValue() - 2e6) < EPSILON : "Expected 2e6, got " + result.getValue();
            System.out.println("✓ testAddition_LargeValues");
        }

        public void testAddition_SmallValues() {
            Quantity result = add(
                    new Quantity(0.001, LengthUnit.FEET),
                    new Quantity(0.002, LengthUnit.FEET)
            );
            assert Math.abs(result.getValue() - 0.003) < EPSILON : "Expected 0.003, got " + result.getValue();
            System.out.println("✓ testAddition_SmallValues");
        }

        public void testAddition_MixedPrecision() {
            Quantity result = add(
                    new Quantity(1.0/3.0, LengthUnit.FEET),
                    new Quantity(2.0/3.0, LengthUnit.FEET)
            );
            // Should be close to 1.0
            assert Math.abs(result.getValue() - 1.0) < EPSILON : "Expected ~1.0, got " + result.getValue();
            System.out.println("✓ testAddition_MixedPrecision");
        }

        // ===== Instance Method Tests =====

        public void testInstanceAdd_SameUnit() {
            Quantity q1 = new Quantity(3.0, LengthUnit.FEET);
            Quantity q2 = new Quantity(2.0, LengthUnit.FEET);
            Quantity result = q1.add(q2);
            assert result.getValue() == 5.0 : "Expected 5.0";
            assert result.getUnit() == LengthUnit.FEET : "Unit should be FEET";
            System.out.println("✓ testInstanceAdd_SameUnit");
        }

        public void testInstanceAdd_CrossUnit() {
            Quantity feet = new Quantity(1.0, LengthUnit.FEET);
            Quantity inches = new Quantity(12.0, LengthUnit.INCHES);
            Quantity result = feet.add(inches); // Result in FEET
            assert Math.abs(result.getValue() - 2.0) < EPSILON : "Expected 2.0";
            System.out.println("✓ testInstanceAdd_CrossUnit");
        }

        public void testInstanceAdd_WithTargetUnit() {
            Quantity feet = new Quantity(1.0, LengthUnit.FEET);
            Quantity inches = new Quantity(12.0, LengthUnit.INCHES);
            Quantity result = feet.add(inches, LengthUnit.INCHES);
            assert Math.abs(result.getValue() - 24.0) < EPSILON : "Expected 24.0";
            assert result.getUnit() == LengthUnit.INCHES : "Unit should be INCHES";
            System.out.println("✓ testInstanceAdd_WithTargetUnit");
        }

        public void testInstanceAdd_Immutability() {
            Quantity original = new Quantity(3.0, LengthUnit.FEET);
            Quantity other = new Quantity(1.0, LengthUnit.FEET);
            Quantity result = original.add(other);
            // Original should be unchanged
            assert original.getValue() == 3.0 : "Original was modified!";
            assert result.getValue() == 4.0 : "Result should be 4.0";
            System.out.println("✓ testInstanceAdd_Immutability");
        }

        // ===== Exception Tests =====

        public void testAddition_NullFirstOperand_Throws() {
            try {
                add(null, new Quantity(1.0, LengthUnit.FEET));
                assert false : "Should have thrown IllegalArgumentException";
            } catch (IllegalArgumentException e) {
                System.out.println("✓ testAddition_NullFirstOperand_Throws");
            }
        }

        public void testAddition_NullSecondOperand_Throws() {
            try {
                add(new Quantity(1.0, LengthUnit.FEET), null);
                assert false : "Should have thrown IllegalArgumentException";
            } catch (IllegalArgumentException e) {
                System.out.println("✓ testAddition_NullSecondOperand_Throws");
            }
        }

        public void testInstanceAdd_NullOperand_Throws() {
            try {
                new Quantity(1.0, LengthUnit.FEET).add(null);
                assert false : "Should have thrown IllegalArgumentException";
            } catch (IllegalArgumentException e) {
                System.out.println("✓ testInstanceAdd_NullOperand_Throws");
            }
        }

        public void testAddition_NullTargetUnit_Throws() {
            try {
                add(
                        new Quantity(1.0, LengthUnit.FEET),
                        new Quantity(1.0, LengthUnit.FEET),
                        null
                );
                assert false : "Should have thrown IllegalArgumentException";
            } catch (IllegalArgumentException e) {
                System.out.println("✓ testAddition_NullTargetUnit_Throws");
            }
        }

        // ===== Integration Tests =====

        public void testAddition_FollowedByConversion() {
            Quantity sum = add(
                    new Quantity(1.0, LengthUnit.FEET),
                    new Quantity(12.0, LengthUnit.INCHES)
            ); // Result: 2.0 FEET
            Quantity converted = sum.convertTo(LengthUnit.INCHES);
            assert Math.abs(converted.getValue() - 24.0) < EPSILON : "Expected 24.0";
            System.out.println("✓ testAddition_FollowedByConversion");
        }

        public void testAddition_ChainedOperations() {
            Quantity a = new Quantity(1.0, LengthUnit.FEET);
            Quantity b = new Quantity(6.0, LengthUnit.INCHES);
            Quantity c = new Quantity(0.5, LengthUnit.FEET);

            // Chain: ((a + b) + c) in FEET
            Quantity result = a.add(b).add(c);
            // 1ft + 0.5ft + 0.5ft = 2ft
            assert Math.abs(result.getValue() - 2.0) < EPSILON : "Expected 2.0, got " + result.getValue();
            System.out.println("✓ testAddition_ChainedOperations");
        }

        public void testAddition_EqualityAfterAddition() {
            Quantity q1 = new Quantity(1.0, LengthUnit.YARDS);
            Quantity q2 = new Quantity(3.0, LengthUnit.FEET);
            Quantity sum1 = q1.add(q2); // 2.0 YARDS

            Quantity q3 = new Quantity(2.0, LengthUnit.YARDS);
            assert sum1.equals(q3) : "Sum should equal 2 yards";
            System.out.println("✓ testAddition_EqualityAfterAddition");
        }

        // Run all UC6 tests
        public void runAllTests() {
            System.out.println("\n=== Running UC6 Test Suite ===\n");

            // Same-unit
            testAddition_SameUnit_FeetPlusFeet();
            testAddition_SameUnit_InchPlusInch();
            testAddition_SameUnit_YardPlusYard();

            // Cross-unit
            testAddition_CrossUnit_FeetPlusInches();
            testAddition_CrossUnit_InchPlusFeet();
            testAddition_CrossUnit_YardPlusFeet();
            testAddition_CrossUnit_CentimeterPlusInch();
            testAddition_WithExplicitTargetUnit();
            testAddition_StaticRawValues();

            // Mathematical properties
            testAddition_Commutativity();
            testAddition_Associativity();
            testAddition_WithZero();
            testAddition_NegativeValues();
            testAddition_NegativeResult();

            // Edge cases
            testAddition_LargeValues();
            testAddition_SmallValues();
            testAddition_MixedPrecision();

            // Instance methods
            testInstanceAdd_SameUnit();
            testInstanceAdd_CrossUnit();
            testInstanceAdd_WithTargetUnit();
            testInstanceAdd_Immutability();

            // Exceptions
            testAddition_NullFirstOperand_Throws();
            testAddition_NullSecondOperand_Throws();
            testInstanceAdd_NullOperand_Throws();
            testAddition_NullTargetUnit_Throws();

            // Integration
            testAddition_FollowedByConversion();
            testAddition_ChainedOperations();
            testAddition_EqualityAfterAddition();

            System.out.println("\n=== All UC6 Tests Passed! ===\n");
        }
    }

    // ============================
    // UC5 TEST CASES (Preserved)
    // ============================

    public static class QuantityTest_UC5 {
        public void testConversion_FeetToInches() {
            double result = convert(1.0, LengthUnit.FEET, LengthUnit.INCHES);
            assert Math.abs(result - 12.0) < EPSILON : "Expected 12.0, got " + result;
            System.out.println("✓ UC5: testConversion_FeetToInches");
        }
        public void testConversion_InchesToFeet() {
            double result = convert(24.0, LengthUnit.INCHES, LengthUnit.FEET);
            assert Math.abs(result - 2.0) < EPSILON : "Expected 2.0, got " + result;
            System.out.println("✓ UC5: testConversion_InchesToFeet");
        }
        public void testConversion_YardsToInches() {
            double result = convert(1.0, LengthUnit.YARDS, LengthUnit.INCHES);
            assert Math.abs(result - 36.0) < EPSILON : "Expected 36.0, got " + result;
            System.out.println("✓ UC5: testConversion_YardsToInches");
        }
        public void testConversion_CentimetersToInches() {
            double result = convert(2.54, LengthUnit.CENTIMETERS, LengthUnit.INCHES);
            assert Math.abs(result - 1.0) < EPSILON : "Expected ~1.0, got " + result;
            System.out.println("✓ UC5: testConversion_CentimetersToInches");
        }
        public void testConversion_ZeroValue() {
            double result = convert(0.0, LengthUnit.FEET, LengthUnit.INCHES);
            assert result == 0.0 : "Expected 0.0, got " + result;
            System.out.println("✓ UC5: testConversion_ZeroValue");
        }
        public void testConversion_NegativeValue() {
            double result = convert(-1.0, LengthUnit.FEET, LengthUnit.INCHES);
            assert Math.abs(result - (-12.0)) < EPSILON : "Expected -12.0, got " + result;
            System.out.println("✓ UC5: testConversion_NegativeValue");
        }
        public void testConversion_SameUnit() {
            double result = convert(5.0, LengthUnit.FEET, LengthUnit.FEET);
            assert Math.abs(result - 5.0) < EPSILON : "Expected 5.0, got " + result;
            System.out.println("✓ UC5: testConversion_SameUnit");
        }
        public void testConversion_RoundTrip_PreservesValue() {
            double original = 7.42;
            double converted = convert(original, LengthUnit.FEET, LengthUnit.CENTIMETERS);
            double back = convert(converted, LengthUnit.CENTIMETERS, LengthUnit.FEET);
            assert Math.abs(original - back) < EPSILON : "Round-trip failed";
            System.out.println("✓ UC5: testConversion_RoundTrip_PreservesValue");
        }
        public void testInstanceConvert_FeetToInches() {
            Quantity q = new Quantity(1.0, LengthUnit.FEET);
            Quantity result = q.convertTo(LengthUnit.INCHES);
            assert result.getValueIn(LengthUnit.INCHES) == 12.0 : "Expected 12.0";
            System.out.println("✓ UC5: testInstanceConvert_FeetToInches");
        }
        public void testInstanceConvert_Immutability() {
            Quantity original = new Quantity(3.0, LengthUnit.FEET);
            Quantity converted = original.convertTo(LengthUnit.INCHES);
            assert original.getValueIn(LengthUnit.FEET) == 3.0 : "Original was modified!";
            assert converted.getValueIn(LengthUnit.INCHES) == 36.0 : "Conversion failed";
            System.out.println("✓ UC5: testInstanceConvert_Immutability");
        }
        public void testConversion_NullSourceUnit_Throws() {
            try { convert(1.0, null, LengthUnit.INCHES); assert false; }
            catch (IllegalArgumentException e) { System.out.println("✓ UC5: testConversion_NullSourceUnit_Throws"); }
        }
        public void testConversion_NaN_Throws() {
            try { convert(Double.NaN, LengthUnit.FEET, LengthUnit.INCHES); assert false; }
            catch (IllegalArgumentException e) { System.out.println("✓ UC5: testConversion_NaN_Throws"); }
        }
        public void testConversion_PrecisionTolerance() {
            double result = convert(1.0, LengthUnit.CENTIMETERS, LengthUnit.INCHES);
            assert Math.abs(result - 0.393701) < EPSILON : "Precision check failed";
            System.out.println("✓ UC5: testConversion_PrecisionTolerance");
        }
        public void testEquality_AfterConversion() {
            Quantity q1 = new Quantity(1.0, LengthUnit.YARDS);
            Quantity q2 = q1.convertTo(LengthUnit.INCHES);
            assert q1.equals(q2) : "Converted quantity should equal original";
            System.out.println("✓ UC5: testEquality_AfterConversion");
        }
        public void runAllTests() {
            System.out.println("\n=== Running UC5 Test Suite ===\n");
            testConversion_FeetToInches();
            testConversion_InchesToFeet();
            testConversion_YardsToInches();
            testConversion_CentimetersToInches();
            testConversion_ZeroValue();
            testConversion_NegativeValue();
            testConversion_SameUnit();
            testConversion_RoundTrip_PreservesValue();
            testInstanceConvert_FeetToInches();
            testInstanceConvert_Immutability();
            testConversion_NullSourceUnit_Throws();
            testConversion_NaN_Throws();
            testConversion_PrecisionTolerance();
            testEquality_AfterConversion();
            System.out.println("\n=== All UC5 Tests Passed! ===\n");
        }
    }

    // ============================
    // UC4 TEST CASES (Preserved)
    // ============================

    public static class QuantityTest_UC4 {
        public void testEquality_YardToYard_SameValue() {
            assert new Quantity(1.0, LengthUnit.YARDS).equals(new Quantity(1.0, LengthUnit.YARDS));
        }
        public void testEquality_YardToFeet() {
            assert new Quantity(1.0, LengthUnit.YARDS).equals(new Quantity(3.0, LengthUnit.FEET));
        }
        public void testEquality_YardToInches() {
            assert new Quantity(1.0, LengthUnit.YARDS).equals(new Quantity(36.0, LengthUnit.INCHES));
        }
        public void testEquality_YardInequality() {
            assert !new Quantity(1.0, LengthUnit.YARDS).equals(new Quantity(2.0, LengthUnit.FEET));
        }
        public void testEquality_CmToCm() {
            assert new Quantity(2.0, LengthUnit.CENTIMETERS).equals(new Quantity(2.0, LengthUnit.CENTIMETERS));
        }
        public void testEquality_CmToInches() {
            assert new Quantity(1.0, LengthUnit.CENTIMETERS).equals(new Quantity(0.393701, LengthUnit.INCHES));
        }
        public void testTransitiveProperty() {
            Quantity yard = new Quantity(1.0, LengthUnit.YARDS);
            Quantity feet = new Quantity(3.0, LengthUnit.FEET);
            Quantity inches = new Quantity(36.0, LengthUnit.INCHES);
            assert yard.equals(feet) && feet.equals(inches) && yard.equals(inches);
        }
        public void testSameReference() {
            Quantity q = new Quantity(1.0, LengthUnit.YARDS);
            assert q.equals(q);
        }
        public void testNullComparison() {
            Quantity q = new Quantity(1.0, LengthUnit.YARDS);
            assert !q.equals(null);
        }
        public void testComplexScenario() {
            assert new Quantity(2.0, LengthUnit.YARDS).equals(new Quantity(6.0, LengthUnit.FEET));
            assert new Quantity(6.0, LengthUnit.FEET).equals(new Quantity(72.0, LengthUnit.INCHES));
        }
    }
}