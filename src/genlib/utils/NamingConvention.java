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

package genlib.utils;

import genlib.abstractrepresentation.GenObject;
import genlib.abstractrepresentation.GenObject.Attribute;
import genlib.abstractrepresentation.GenObject.AttributeType.Recommended;
import genlib.abstractrepresentation.GenObject.AttributeType.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * this class defines the naming-convention of the toString()-method
 * of every GenObject.
 *
 * @author Hilmar
 */
public abstract class NamingConvention extends GenObject {

    /**
     * the global naming-convention, can be set and is a standard-value at start
     */
    private static NamingConvention namingConvention = new StandardNamingConvention();

    /**
     * set the global naming-convention
     *
     * @param _namingConvention the naming-convention
     * @throws NullPointerException if _namingConvention is null
     */
    public static void setGlobalNamingConvention(NamingConvention _namingConvention) {
        if (_namingConvention == null)
            throw new NullPointerException("namingConvention can't be null.");

        namingConvention = _namingConvention;
    }

    /**
     * get the global naming-convention
     *
     * @return the global naming-convention
     */
    public static NamingConvention getGlobalNamingConvention() {
        return namingConvention;
    }

    /**
     * apply the naming-convention to the given GenObject
     *
     * @param obj the gen-object, we want to print
     * @return the output string
     */
    public abstract String apply (GenObject obj);

    /**
     * this class has some standard-behavior for the naming-convention
     */
    public static class StandardNamingConvention extends NamingConvention {

        /**
         * How should the class-name at the start be printed?
         */
        public enum PrintClassName {Not, Simple, Full};

        /**
         * How should the attributes of the objects be printed?
         * None/All: None or all attributes are printed
         * JustMainAttributes: Just the attributes declared as main-attributes are printed
         * Standard: If the attribute says, it recommends printing (or not), it is printed always like the recommendation, otherwise it is printed if it's not declared unimportant
         * AttributesWithoutHierarchical: Not-unimportant attributes are printed, but without hierarchical attributes
         */
        public enum PrintAttributes {None, JustMainAttributes, Standard, AttributesWithoutHierarchical, All};

        /**
         * How should the class-name at the start be printed?
         */
        protected final PrintClassName printClassName;

        /**
         * How should the attributes of the objects be printed?
         * For more information look at the javadoc of PrintAttributes
         */
        protected final PrintAttributes printAttributes;

        /**
         * the maximal print-length of a value of an attribute,
         * as long as it is not another GenObject
         */
        protected final int maxLengthAttributes;

        /**
         * the constructor with standard-values
         */
        public StandardNamingConvention () {
            this(PrintClassName.Simple, PrintAttributes.Standard, 25);
        }

        /**
         * the constructor with individual options
         *
         * @param _printClassName How should the class-name at the start be printed?
         * @param _printAttributes How should the attributes of the objects be printed?
         * @param _maxLengthAttributes  the maximal print-length of a value of an attribute, as long as it is not another GenObject, -1 if there should be no bound
         * @throws NullPointerException if printClassName or printAttributes is null
         * @throws IllegalArgumentException if maxLengthAttributes is whether -1 nor larger or equal than 1
         */
        public StandardNamingConvention (PrintClassName _printClassName, PrintAttributes _printAttributes, int _maxLengthAttributes) {
            if (_printClassName == null)
                throw new NullPointerException("printClassName can't be null.");
            if (_printAttributes == null)
                throw new NullPointerException("printAttributes can't be null.");
            if (_maxLengthAttributes == 0 || _maxLengthAttributes < -1)
                throw new IllegalArgumentException("maxLengthAttributes has to be >= 1 or -1.");

            printClassName = _printClassName;
            printAttributes = _printAttributes;
            maxLengthAttributes = _maxLengthAttributes;
        }

        @Override
        public String apply (GenObject obj) {
            StringBuilder ret = new StringBuilder();

            //the printing of the class-name
            switch (printClassName) {
                case Not:
                    break;
                case Simple:
                    ret.append("[").append(obj.getClass().getSimpleName()).append("]   ");
                    break;
                case Full:
                    ret.append("[").append(obj.getClass().getName()).append("]   ");
                    break;
                default:
                    throw new AssertionError(printClassName.name());
            }

            //print the attributes
            boolean firstAttribute = true;
            Set <Type> printTypes = new HashSet(Arrays.asList(getToPrintTypes()));
            for (Attribute attr : obj.getAttributes()) {
                //just Standard-attributes do, what the recommendation says
                if (printAttributes == PrintAttributes.Standard && attr.getType().getRecommendedOutput() == Recommended.No)
                    continue;

                if ( (printAttributes == PrintAttributes.Standard && attr.getType().getRecommendedOutput() == Recommended.Yes) ||
                        printTypes.contains(attr.getType().getType()) ) {
                    //for the case of arrays or null, we need this toString()-helper function
                    String valueStr = valueToString(attr.getValue());
                    if (maxLengthAttributes != -1 && !(attr.getValue() instanceof GenObject) && valueStr.length() > maxLengthAttributes)
                        valueStr = valueStr.substring(0, maxLengthAttributes) + "..";
                    ret.append((firstAttribute ? "'" : ", '")).append(attr.getKey()).append("': ").append(valueStr);
                    firstAttribute = false;
                }
            }

            if (firstAttribute)
                ret.append("- no Attributes -");

            return ret.toString();
        }

        /**
         * convert the printAttributes in an array of Types who get print
         *
         * @return an array of Type
         */
        protected Type [] getToPrintTypes () {
            switch (printAttributes) {
                case None:
                    return new Type [] {};
                case JustMainAttributes:
                    return new Type [] {Type.MainAttribute};
                case Standard:
                    return new Type [] {Type.Descriptor, Type.HierarchicalChild, Type.HierarchicalParent, Type.MainAttribute, Type.NormalAttribute};
                case AttributesWithoutHierarchical:
                    return new Type [] {Type.Descriptor, Type.MainAttribute, Type.NormalAttribute};
                case All:
                    return new Type [] {Type.Descriptor, Type.HierarchicalChild, Type.HierarchicalParent, Type.MainAttribute, Type.NormalAttribute, Type.TemporaryOrUnimportant};
                default:
                    throw new AssertionError(printAttributes.name());
            }
        }

        /**
         * a helper-function, so we can support printing of arrays and null
         *
         * @param input the object to print
         * @return the string of the object
         */
        protected String valueToString(Object input) {

            //in the boolean case, we want to print 0101010, instead of true, false, etc..
            if (input instanceof boolean []) {
                StringBuilder ret = new StringBuilder();
                for (boolean b : (boolean[])input)
                    ret.append((b ? "1" : "0"));
                return ret.toString();

            //all other arrays use Arrays.toString()
            } else if (input instanceof Object[])
                return Arrays.toString((Object[])input);
            else if (input instanceof int[])
                return Arrays.toString((int[])input);
            else if (input instanceof long[])
                return Arrays.toString((long[])input);
            else if (input instanceof char[])
                return Arrays.toString((char[])input);
            else if (input instanceof byte[])
                return Arrays.toString((byte[])input);
            else if (input instanceof short[])
                return Arrays.toString((short[])input);
            else if (input instanceof float[])
                return Arrays.toString((float[])input);
            else if (input instanceof double[])
                return Arrays.toString((double[])input);

            //the null-case
            else if (input == null)
                return "'null'";
            else
                return input.toString();
        }

        @Override
        public List <Attribute> getAttributes() {
            return Utils.createList( new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "printClassName", printClassName),
                                    new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "printAttributes", printAttributes),
                                    new Attribute(new AttributeType( (maxLengthAttributes == -1 ? AttributeType.Type.TemporaryOrUnimportant : AttributeType.Type.MainAttribute) ), "maxLengthAttributes", maxLengthAttributes));
        }

    }

}
