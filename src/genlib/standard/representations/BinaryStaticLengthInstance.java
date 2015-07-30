/*
 * The MIT License
 *
 * Copyright 2015 Hilmar.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package genlib.standard.representations;

import genlib.abstractrepresentation.GenInstance;
import genlib.utils.Utils;
import java.util.List;

/**
 * the corresponding instance for BinaryStaticLength
 *
 * @author Hilmar
 */
public class BinaryStaticLengthInstance extends GenInstance {

    /**
     * the array with the bits
     */
    protected final boolean [] array;

    /**
     * the constructor
     *
     * @param _parent the parent (the type)
     * @param _array the array with the bits
     */
    public BinaryStaticLengthInstance (BinaryStaticLength _parent, boolean [] _array) {
        super(_parent);
        array = _array;
    }

    /**
     * check, if an entry in the array is set
     *
     * @param index the index
     * @return true, if set
     * @throws IllegalArgumentException if index is outside the bounds
     */
    public boolean isSet (int index) {
        if (index < 0 || index >= array.length)
            throw new IllegalArgumentException("index '" + index + "' out of bounds.");

        return array[index];
    }

    /**
     * returns a deep-copy of the entries-array
     *
     * @return a deep-copy of the entries-array
     */
    public boolean[] getArray () {
        boolean [] ret = new boolean[array.length];
        System.arraycopy(array, 0, ret, 0, array.length);
        return ret;
    }

    @Override
    public List <Attribute> getAttributes() {
        return Utils.extendList( super.getAttributes(), new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "array", array) );
    }

}
