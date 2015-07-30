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
import genlib.utils.Utils;
import java.util.List;


/**
 * The GenInstance defines an instance of a GenRepresentation. The GenRepresentation
 * defines the type of the genoType/phenoType.
 *
 * @author Hilmar
 */
public abstract class GenInstance extends GenObject {

    /**
     * the type of this instance
     */
    protected final GenRepresentation parent;

    /**
     * the constructor.
     *
     * @param _parent the type of this instance
     * @throws NullPointerException if _parent is null
     */
    public GenInstance (GenRepresentation _parent) {
        if (_parent == null)
            throw new NullPointerException("parent cannot be null.");

        parent = _parent;
    }

    /**
     * get the type of this instance
     *
     * @return the type
     */
    public GenRepresentation getRepresentation() {
        return parent;
    }

    @Override
    public List <Attribute> getAttributes() {
        return Utils.createList(new Attribute(new AttributeType(Type.HierarchicalParent, Recommended.No, Recommended.NotSpecified), "parent", parent));
    }

}
