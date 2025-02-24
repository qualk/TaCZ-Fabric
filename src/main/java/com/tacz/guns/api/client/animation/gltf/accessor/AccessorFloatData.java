package com.tacz.guns.api.client.animation.gltf.accessor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Locale;

/**
 * A class for accessing the data that is described by an accessor.
 * It allows accessing the byte buffer of the buffer view of the
 * accessor, depending on the accessor parameters.<br>
 * <br>
 * This data consists of several elements (for example, 3D float vectors),
 * which consist of several components (for example, the 3 float values).
 */
public final class AccessorFloatData
        extends AbstractAccessorData
        implements AccessorData {
    /**
     * Creates a new instance for accessing the data in the given
     * byte buffer, according to the rules described by the given
     * accessor parameters.
     *
     * @param componentType           The component type
     * @param bufferViewByteBuffer    The byte buffer of the buffer view
     * @param byteOffset              The byte offset in the buffer view
     * @param numElements             The number of elements
     * @param numComponentsPerElement The number of components per element
     * @param byteStride              The byte stride between two elements. If this
     *                                is <code>null</code> or <code>0</code>, then the stride will
     *                                be the size of one element.
     * @throws NullPointerException     If the bufferViewByteBuffer is
     *                                  <code>null</code>
     * @throws IllegalArgumentException If the component type is not
     *                                  <code>GL_FLOAT</code>
     * @throws IllegalArgumentException If the given byte buffer does not
     *                                  have a sufficient capacity to provide the data for the accessor
     */
    public AccessorFloatData(int componentType,
                             ByteBuffer bufferViewByteBuffer, int byteOffset, int numElements,
                             int numComponentsPerElement, Integer byteStride) {
        super(float.class, bufferViewByteBuffer, byteOffset, numElements,
                numComponentsPerElement, Float.BYTES, byteStride);
        AccessorDatas.validateFloatType(componentType);

        AccessorDatas.validateCapacity(byteOffset, getNumElements(),
                getByteStridePerElement(), bufferViewByteBuffer.capacity());
    }

    /**
     * Returns the value of the specified component of the specified element
     *
     * @param elementIndex   The element index
     * @param componentIndex The component index
     * @return The value
     * @throws IndexOutOfBoundsException If the given indices cause the
     *                                   underlying buffer to be accessed out of bounds
     */
    public float get(int elementIndex, int componentIndex) {
        int byteIndex = getByteIndex(elementIndex, componentIndex);
        return getBufferViewByteBuffer().getFloat(byteIndex);
    }

    /**
     * Returns the value of the specified component
     *
     * @param globalComponentIndex The global component index
     * @return The value
     * @throws IndexOutOfBoundsException If the given index causes the
     *                                   underlying buffer to be accessed out of bounds
     */
    public float get(int globalComponentIndex) {
        int elementIndex =
                globalComponentIndex / getNumComponentsPerElement();
        int componentIndex =
                globalComponentIndex % getNumComponentsPerElement();
        return get(elementIndex, componentIndex);
    }

    /**
     * Set the value of the specified component of the specified element
     *
     * @param elementIndex   The element index
     * @param componentIndex The component index
     * @param value          The value
     * @throws IndexOutOfBoundsException If the given indices cause the
     *                                   underlying buffer to be accessed out of bounds
     */
    public void set(int elementIndex, int componentIndex, float value) {
        int byteIndex = getByteIndex(elementIndex, componentIndex);
        getBufferViewByteBuffer().putFloat(byteIndex, value);
    }

    /**
     * Set the value of the specified component
     *
     * @param globalComponentIndex The global component index
     * @param value                The value
     * @throws IndexOutOfBoundsException If the given index causes the
     *                                   underlying buffer to be accessed out of bounds
     */
    public void set(int globalComponentIndex, float value) {
        int elementIndex =
                globalComponentIndex / getNumComponentsPerElement();
        int componentIndex =
                globalComponentIndex % getNumComponentsPerElement();
        set(elementIndex, componentIndex, value);
    }


    /**
     * Returns an array containing the minimum component values of all elements
     * of this accessor data. This will be an array whose length is the
     * {@link #getNumComponentsPerElement() number of components per element}.
     *
     * @return The minimum values
     */
    public float[] computeMin() {
        float result[] = new float[getNumComponentsPerElement()];
        Arrays.fill(result, Float.MAX_VALUE);
        for (int e = 0; e < getNumElements(); e++) {
            for (int c = 0; c < getNumComponentsPerElement(); c++) {
                result[c] = Math.min(result[c], get(e, c));
            }
        }
        return result;
    }

    /**
     * Returns an array containing the maximum component values of all elements
     * of this accessor data. This will be an array whose length is the
     * {@link #getNumComponentsPerElement() number of components per element}.
     *
     * @return The minimum values
     */
    public float[] computeMax() {
        float result[] = new float[getNumComponentsPerElement()];
        Arrays.fill(result, -Float.MAX_VALUE);
        for (int e = 0; e < getNumElements(); e++) {
            for (int c = 0; c < getNumComponentsPerElement(); c++) {
                result[c] = Math.max(result[c], get(e, c));
            }
        }
        return result;
    }

    @Override
    public ByteBuffer createByteBuffer() {
        int totalNumComponents = getTotalNumComponents();
        int totalBytes = totalNumComponents * getNumBytesPerComponent();
        ByteBuffer result = ByteBuffer.allocateDirect(totalBytes)
                .order(ByteOrder.nativeOrder());
        for (int i = 0; i < totalNumComponents; i++) {
            float component = get(i);
            result.putFloat(component);
        }
        result.position(0);
        return result;
    }

    /**
     * Creates a (potentially large!) string representation of the data
     *
     * @param locale         The locale used for number formatting
     * @param format         The number format string
     * @param elementsPerRow The number of elements per row. If this
     *                       is not greater than 0, then all elements will be in a single row.
     * @return The data string
     */
    public String createString(
            Locale locale, String format, int elementsPerRow) {
        StringBuilder sb = new StringBuilder();
        int nc = getNumComponentsPerElement();
        sb.append("[");
        for (int e = 0; e < getNumElements(); e++) {
            if (e > 0) {
                sb.append(", ");
                if (elementsPerRow > 0 && (e % elementsPerRow) == 0) {
                    sb.append("\n ");
                }
            }
            if (nc > 1) {
                sb.append("(");
            }
            for (int c = 0; c < nc; c++) {
                if (c > 0) {
                    sb.append(", ");
                }
                float component = get(e, c);
                sb.append(String.format(locale, format, component));
            }
            if (nc > 1) {
                sb.append(")");
            }
        }
        sb.append("]");
        return sb.toString();
    }

}