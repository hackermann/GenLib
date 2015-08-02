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

package genlib.extended.diversity;

import genlib.abstractrepresentation.GenInstance;
import genlib.abstractrepresentation.GenObject;
import genlib.abstractrepresentation.GenObject.AttributeType.Type;
import genlib.abstractrepresentation.GenRepresentation;
import genlib.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * With this class, you can measure the diversity of a population
 * 
 * @author Hilmar
 */
public abstract class AbstractDiversity extends GenObject {
    
    /**
     * the percent of the population, we do the diversity-measuring with.
     * has to be between 0 and 1 but not equal to 0
     */
    protected final double percentPopulation;
    
    /**
     * the constructor
     */
    public AbstractDiversity() {
        this(1.0);
    }
    
    /**
     * the constructor
     * 
     * @param _percentPopulation the percent of a population, we do the diversity measuring with. The individuums will be chosen randomly.
     * @throws IllegalArgumentException, if percentPopulation is not between 0 and 1 or equal to 0
     */
    public AbstractDiversity (double _percentPopulation) {
        if (!Double.isFinite(_percentPopulation) || _percentPopulation <= 0.0 || _percentPopulation > 1.0)
            throw new IllegalArgumentException("illegal percenPopulation: has to be > 0 and <= 1");
        
        percentPopulation = _percentPopulation;
    }
    
    /**
     * returns the diversity of a population
     * 
     * @param population the population
     * @param random a random-object
     * @return the calulated diversity
     */
    public double getDiversity (List <GenInstance> population, Random random) {
        
        List <GenInstance> copiedPopulation = new ArrayList();
        copiedPopulation.addAll(population);
        
        while (copiedPopulation.size() > 0 && copiedPopulation.size() > percentPopulation*population.size())
            copiedPopulation.remove(random.nextInt(copiedPopulation.size()));
        
        return calculateDiversity(copiedPopulation);
    }
    
    /**
     * returns the diversity of a population. It may be not the whole algorithm,
     * but just a subset of the whole population
     * 
     * @param population the population or a subset of it
     * @return the calculated value
     */
    protected abstract double calculateDiversity (List <GenInstance> population);    
    
    /**
     * Is this diversity-operator compatible with the given GenRepresentation?
     * 
     * @param representation the GenRepresentation
     * @return true, if compatible
     */
    public abstract boolean isCompatibleWith (GenRepresentation representation);
    
    /**
     * get the name of this type of diversity
     * 
     * @return a name
     */
    public abstract String getName ();
    
    @Override
    public List <Attribute> getAttributes() {
        return Utils.createList( new Attribute(new AttributeType(Type.MainAttribute), "percentPopulation", percentPopulation));
    }
    
}
