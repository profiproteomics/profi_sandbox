/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.proline.mzscope.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author CB205360
 */
public class ExtractionRequest {

    public static class Builder<T extends Builder<T>> {

        double minMz = 0.0;
        double maxMz = 0.0;
        float elutionTime = -1.0f;
        float elutionTimeLowerBound = -1.0f;
        float elutionTimeUpperBound = -1.0f;

        double parentMz = 0.0;
        double minParentMz = 0.0;
        double maxParentMz = 0.0;

        double mz = 0.0;

        @SuppressWarnings("unchecked")  // Smell 1
        protected T self() {
            return (T) this;            // Unchecked cast!
        }

        public T setMinMz(double minMz) {
            this.minMz = minMz;
            return self();
        }

        public T setMaxMz(double maxMz) {
            this.maxMz = maxMz;
            return self();
        }

        public T setParentMz(double parentMz) {
            this.parentMz = parentMz;
            return self();
        }
        
        public T setMinParentMz(double minParentMz) {
            this.minParentMz = minParentMz;
            return self();
        }
        
        public T setMaxParentMz(double maxParentMz) {
            this.maxParentMz = maxParentMz;
            return self();
        }

        public T setElutionTimeLowerBound(float startRT) {
            this.elutionTimeLowerBound = startRT;
            return self();
        }

        public T setElutionTimeUpperBound(float stopRT) {
            this.elutionTimeUpperBound = stopRT;
            return self();
        }

        public T setMz(double mz) {
            this.mz = mz;
            return self();
        }

        public double getMinMz() {
            return minMz;
        }

        public double getMaxMz() {
            return maxMz;
        }

        public double getMz() {
            return mz;
        }

        public double getParentMz() {
            return parentMz;
        }

        public double getMinParentMz() {
            return minParentMz;
        }

        public double getMaxParentMz() {
            return maxParentMz;
        }

        public ExtractionRequest build() {
            return new ExtractionRequest(this);
        }
    }

    private final double minMz;
    private final double maxMz;

    private final double mz;
    // values in seconds !! 
    private final float elutionTimeLowerBound;
    private final float elutionTimeUpperBound;
    private final float elutionTime;

    private final double parentMz;
    private final double minParentMz;
    private final double maxParentMz;

    protected ExtractionRequest(Builder builder) {
        this.maxMz = builder.maxMz;
        this.minMz = builder.minMz;
        this.elutionTimeLowerBound = builder.elutionTimeLowerBound;
        this.elutionTimeUpperBound = builder.elutionTimeUpperBound;
        this.elutionTime = builder.elutionTime;
        this.mz = builder.mz;
        this.parentMz = builder.parentMz;
        this.minParentMz = builder.minParentMz;
        this.maxParentMz = builder.maxParentMz;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @SuppressWarnings("rawtypes")       // Smell 2
    public static Builder<?> builder() {
        return new Builder();           // Raw type - no type argument!
    }

    public double getMinMz() {
        return minMz;
    }

    public double getMaxMz() {
        return maxMz;
    }

    public double getMz() {
        return mz;
    }

    public float getElutionTimeLowerBound() {
        return elutionTimeLowerBound;
    }

    public float getElutionTimeUpperBound() {
        return elutionTimeUpperBound;
    }

    public float getElutionTime() {
        return elutionTime;
    }

    public double getParentMz() {
        return parentMz;
    }

    public double getMinParentMz() {
        return minParentMz;
    }

    public double getMaxParentMz() {
        return maxParentMz;
    }

}
