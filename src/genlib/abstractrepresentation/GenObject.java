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

package genlib.abstractrepresentation;

import genlib.abstractrepresentation.GenObject.AttributeType.Recommended;
import genlib.abstractrepresentation.GenObject.AttributeType.Type;
import genlib.utils.NamingConvention;
import genlib.utils.Utils;
import java.util.Arrays;
import java.util.List;

/**
 * This is the abstract object, every genetic Object of this library should
 * inherit from. It defines basic functionality for a string-output (toString)
 * and equals-Check (equals). If a subclass has a special equals-behavior, that
 * cannot been modelled with this class, so just overwrite equals () again.
 * The toString() is final here. If there is a need for individual string-output,
 * use user-defined methods, as example getName(), ..
 *
 * @author Hilmar
 */
public abstract class GenObject {

    /**
     * gets all the attributes of the instance. Every Attribute has a type,
     * which defines the type of the attribute. A subclass should take the
     * list of the superclass and append its new attributes. Per Attribute
     * is the name and the recommendedOutput and recommendedEqualsCheck of
     * the type a key, and has to be defined the same for EVERY instance of
     * the class.
     *
     * @return a list of attributes of this object
     */
    public abstract List <Attribute> getAttributes();

    @Override
    public final String toString() {
        //the NamingConvention defines, how the output should look like
        return NamingConvention.getGlobalNamingConvention().apply(this);
    }

    @Override
    public int hashCode () {
        return 0;
    }

    @Override
    public boolean equals (Object other) {
        //check for null and if the instance matches
        if (other != null && other instanceof GenObject && other.getClass().equals(getClass())) {

            //we will compare every attribute with the attributess of other
            for (Attribute otherAttr : ((GenObject)other).getAttributes()) {
                Recommended equalsCheck = otherAttr.getType().getRecommendedEqualsCheck();
                AttributeType.Type type = otherAttr.getType().getType();

                //we have to compare this attribute, if the equals-check is recommended OR if it NotSpecified and the type is a typical equals-check-type
                if (    equalsCheck == Recommended.Yes ||
                        (equalsCheck == Recommended.NotSpecified && (type == AttributeType.Type.Descriptor || type == AttributeType.Type.NormalAttribute || type == AttributeType.Type.MainAttribute) )) {
                    for (Attribute attr : getAttributes()) {
                        if (otherAttr.getKey().equals(attr.getKey())) {
                            if ( !valueEquals(attr.getValue(), otherAttr.getValue()) )
                                return false;
                            break;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * does a equals-check between left and right. In the case of an
     * array, Arrays.equals() will be used.
     *
     * @param left the left array, null is not allowed
     * @param right the right array, null is not allowed
     * @return true, if equals
     */
    protected boolean valueEquals (Object left, Object right) {
        if (left instanceof Object[] && right instanceof Object[])
            return Arrays.equals((Object[])left, (Object[])right);
        else if (left instanceof int[] && right instanceof int[])
            return Arrays.equals((int[])left, (int[])right);
        else if (left instanceof long[] && right instanceof long[])
            return Arrays.equals((long[])left, (long[])right);
        else if (left instanceof short[] && right instanceof short[])
            return Arrays.equals((short[])left, (short[])right);
        else if (left instanceof char[] && right instanceof char[])
            return Arrays.equals((char[])left, (char[])right);
        else if (left instanceof boolean[] && right instanceof boolean[])
            return Arrays.equals((boolean[])left, (boolean[])right);
        else if (left instanceof float[] && right instanceof float[])
            return Arrays.equals((float[])left, (float[])right);
        else if (left instanceof double[] && right instanceof double[])
            return Arrays.equals((double[])left, (double[])right);
        else if (left instanceof byte[] && right instanceof byte[])
            return Arrays.equals((byte[])left, (byte[])right);
        else
            return left.equals(right);
    }

    /**
     * The Attribute of an attribute of an object. Defines the name,
     * the type and the attribute itself.
     */
    public static class Attribute extends GenObject {

        /**
         * the type of the attribute
         */
        protected final AttributeType type;

        /**
         * the name of this attribute
         */
        protected final String key;

        /**
         * the attribute itself, can be null
         */
        protected final Object value;

        /**
         * the constructor
         *
         * @param _type the type of this attribute
         * @param _key the name of this attribute
         * @param _value the attribute itself, can be null
         * @throws NullPointerException if _type or _key is null
         */
        public Attribute (AttributeType _type, String _key, Object _value) {
            if (_type == null)
                throw new NullPointerException("type cannot be null.");
            if (_key == null)
                throw new NullPointerException("key cannot be null.");

            type = _type;
            key = _key;
            value = _value;
        }

        /**
         * get the type
         *
         * @return the type
         */
        public AttributeType getType () {
            return type;
        }

        /**
         * get the name
         *
         * @return the name
         */
        public String getKey () {
            return key;
        }

        /**
         * get the attribute itself
         *
         * @return the attribute
         */
        public Object getValue () {
            return value;
        }

        @Override
        public List <Attribute> getAttributes() {
            return Utils.createList(new Attribute(new AttributeType(Type.MainAttribute), "type", type),
                                    new Attribute(new AttributeType(Type.MainAttribute), "key", key),
                                    new Attribute(new AttributeType(Type.MainAttribute), "value", value));
        }

    }

    /**
     * defines the type of an attribute of a class
     */
    public static class AttributeType extends GenObject {        

        /**
         * defines the basic type: HierarchicalParent and HierarchicalChild are reserved
         * for child- and parent-attributes. Descriptor is for names and titles, that are
         * given for the object. The other values can be used for different importance-states
         * of attributes.
         */
        public enum Type {HierarchicalParent, HierarchicalChild, MainAttribute, NormalAttribute, Descriptor, TemporaryOrUnimportant};

        /**
         * Is this behavior recommended?
         */
        public enum Recommended {Yes, No, NotSpecified};

        /**
         * the type of the attribute, for details look at the javadoc for Type
         */
        protected final Type type;

        /**
         * is the output recommended?
         */
        protected final Recommended recommendedOutput;

        /**
         * should this attribute be considered in the equals-check?
         */
        protected final Recommended recommendedEqualsCheck;

        /**
         * the standard constructor, the recommended-values will be set to NotSpecified
         *
         * @param _type the type of the attribute
         */
        public AttributeType (Type _type) {
            this(_type, Recommended.NotSpecified, Recommended.NotSpecified);
        }

        /**
         * the constructor with all parameters
         *
         * @param _type the type of the attribute
         * @param _recommendedOutput is the output recommended?
         * @param _recommendedEqualsCheck should this attribute be considered in the equals-check?
         */
        public AttributeType (Type _type, Recommended _recommendedOutput, Recommended _recommendedEqualsCheck) {
            type = _type;
            recommendedOutput = _recommendedOutput;
            recommendedEqualsCheck = _recommendedEqualsCheck;
        }

        /**
         * returns the type
         *
         * @return the type
         */
        public Type getType () {
            return type;
        }

        /**
         * returns, if the output is recommended
         *
         * @return if the output is recommended
         */
        public Recommended getRecommendedOutput () {
            return recommendedOutput;
        }

        /**
         * returns, if the equals-check is recommended
         *
         * @return if the equals-check is recommended
         */
        public Recommended getRecommendedEqualsCheck () {
            return recommendedEqualsCheck;
        }
        
        @Override
        public List<Attribute> getAttributes() {
            return Utils.createList(new Attribute(new AttributeType(Type.MainAttribute), "type", type),
                                    new Attribute(new AttributeType(Type.NormalAttribute), "recommendedOutput", recommendedOutput),
                                    new Attribute(new AttributeType(Type.NormalAttribute), "recommendedEqualsCheck", recommendedEqualsCheck));
        }

    }

}
