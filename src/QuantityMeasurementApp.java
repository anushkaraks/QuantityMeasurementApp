package com.apps.quantitymeasurement;

import java.util.Objects;

public class QuantityMeasurementApp {

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

    /**
     * Generic Quantity class (unchanged from UC3)
     */
    public static class Quantity {
        private final double value;
        private final LengthUnit unit;

        public Quantity(double value, LengthUnit unit) {
            if (unit == null) {
                throw new IllegalArgumentException("Unit cannot be null");
            }
            this.value = value;
            this.unit = unit;
        }

        /**
         * Convert to base unit (inches)
         */
        private double toBaseUnit() {
            return this.value * this.unit.getConversionFactor();
        }

        /**
         * Compare two quantities
         */
        public boolean compare(Quantity other) {
            if (other == null) return false;
            return Double.compare(this.toBaseUnit(), other.toBaseUnit()) == 0;
        }

        /**
         * Equality override (value-based, cross-unit)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true; // reflexive
            if (obj == null || getClass() != obj.getClass()) return false;

            Quantity other = (Quantity) obj;
            return compare(other);
        }

        @Override
        public int hashCode() {
            return Objects.hash(toBaseUnit());
        }

        @Override
        public String toString() {
            return value + " " + unit;
        }
    }

    // ============================
    // DEMO METHODS
    // ============================

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

    // ============================
    // MAIN METHOD
    // ============================

    public static void main(String[] args) {

        System.out.println("=== UC4 Quantity Measurement Demo ===");

        // Basic comparisons
        Quantity q1 = new Quantity(1.0, LengthUnit.FEET);
        Quantity q2 = new Quantity(12.0, LengthUnit.INCHES);

        System.out.println("1 foot == 12 inches: " + q1.equals(q2));

        // UC4 demos
        demonstrateAllUnits();

        // Additional checks
        System.out.println("\n=== Edge Cases ===");

        Quantity a = new Quantity(1.0, LengthUnit.YARDS);
        Quantity b = new Quantity(2.0, LengthUnit.FEET);

        System.out.println("1 yard == 2 feet (false): " + a.equals(b));
        System.out.println("Same reference: " + a.equals(a));
        System.out.println("Null comparison: " + a.equals(null));

        // Transitive property
        Quantity c = new Quantity(36.0, LengthUnit.INCHES);
        System.out.println("\nTransitive:");
        System.out.println("1 yard == 3 feet: " + a.equals(q1));
        System.out.println("3 feet == 36 inches: " + q1.equals(c));
        System.out.println("1 yard == 36 inches: " + a.equals(c));
    }

    // ============================
    // TEST CASES (UC4 Coverage)
    // ============================

    public static class QuantityTest {

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