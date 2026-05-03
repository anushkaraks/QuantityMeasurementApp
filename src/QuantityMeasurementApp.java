package com.apps.quantitymeasurement;

import java.util.Objects;

public class QuantityMeasurementApp {

    public enum LengthUnit {
        FEET(12.0),
        INCHES(1.0);

        private final double conversionFactor;

        LengthUnit(double conversionFactor) {
            this.conversionFactor = conversionFactor;
        }

        public double getConversionFactor() {
            return conversionFactor;
        }
    }

    /**
     * Generic Quantity class representing length
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
         * Convert value to base unit (inches)
         */
        private double toBaseUnit() {
            return this.value * this.unit.getConversionFactor();
        }

        /**
         * Compare two quantities using base unit
         */
        public boolean compare(Quantity other) {
            if (other == null) return false;
            return Double.compare(this.toBaseUnit(), other.toBaseUnit()) == 0;
        }

        /**
         * Overridden equals method (Value-based equality)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true; // Reflexive
            if (obj == null || getClass() != obj.getClass()) return false; // Type-safe

            Quantity other = (Quantity) obj;
            return compare(other);
        }

        /**
         * Required when equals is overridden
         */
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
    // DEMO METHODS (App behavior)
    // ============================

    public static boolean demonstrateEquality(Quantity q1, Quantity q2) {
        return q1.equals(q2);
    }

    public static void demonstrateFeetEquality() {
        Quantity f1 = new Quantity(1.0, LengthUnit.FEET);
        Quantity f2 = new Quantity(1.0, LengthUnit.FEET);

        System.out.println("Feet equality (1ft vs 1ft): " + f1.equals(f2));
    }

    public static void demonstrateInchesEquality() {
        Quantity i1 = new Quantity(1.0, LengthUnit.INCHES);
        Quantity i2 = new Quantity(1.0, LengthUnit.INCHES);

        System.out.println("Inches equality (1in vs 1in): " + i1.equals(i2));
    }

    public static void demonstrateCrossUnitComparison() {
        Quantity f = new Quantity(1.0, LengthUnit.FEET);
        Quantity i = new Quantity(12.0, LengthUnit.INCHES);

        System.out.println("Cross-unit (1ft vs 12in): " + f.equals(i));
    }

    // ============================
    // MAIN METHOD (RUN DEMO)
    // ============================

    public static void main(String[] args) {

        System.out.println("=== UC3 Quantity Measurement Demo ===");

        demonstrateFeetEquality();
        demonstrateInchesEquality();
        demonstrateCrossUnitComparison();

        Quantity q1 = new Quantity(1.0, LengthUnit.FEET);
        Quantity q2 = new Quantity(12.0, LengthUnit.INCHES);

        System.out.println("Generic equality check: " + demonstrateEquality(q1, q2));

        System.out.println("\n=== Manual Test Outputs ===");

        // Same reference
        System.out.println("Same reference: " + q1.equals(q1));

        // Null comparison
        System.out.println("Null comparison: " + q1.equals(null));

        // Inequality
        Quantity q3 = new Quantity(2.0, LengthUnit.FEET);
        System.out.println("1ft vs 2ft: " + q1.equals(q3));
    }

    // ============================
    // TEST CASES (JUnit Style)
    // ============================

    /**
     * NOTE: These are example test cases.
     * Move to a separate test file if using JUnit framework.
     */
    public static class QuantityTest {

        public void testEquality_FeetToFeet_SameValue() {
            Quantity a = new Quantity(1.0, LengthUnit.FEET);
            Quantity b = new Quantity(1.0, LengthUnit.FEET);

            assert a.equals(b);
        }

        public void testEquality_InchToInch_SameValue() {
            Quantity a = new Quantity(1.0, LengthUnit.INCHES);
            Quantity b = new Quantity(1.0, LengthUnit.INCHES);

            assert a.equals(b);
        }

        public void testEquality_FeetToInch_Equivalent() {
            Quantity feet = new Quantity(1.0, LengthUnit.FEET);
            Quantity inches = new Quantity(12.0, LengthUnit.INCHES);

            assert feet.equals(inches);
        }

        public void testEquality_InchToFeet_Equivalent() {
            Quantity inches = new Quantity(12.0, LengthUnit.INCHES);
            Quantity feet = new Quantity(1.0, LengthUnit.FEET);

            assert inches.equals(feet);
        }

        public void testInequality_DifferentValues() {
            Quantity a = new Quantity(1.0, LengthUnit.FEET);
            Quantity b = new Quantity(2.0, LengthUnit.FEET);

            assert !a.equals(b);
        }

        public void testNullComparison() {
            Quantity a = new Quantity(1.0, LengthUnit.FEET);

            assert !a.equals(null);
        }

        public void testSameReference() {
            Quantity a = new Quantity(1.0, LengthUnit.FEET);

            assert a.equals(a);
        }
    }
}