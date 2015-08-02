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
import genlib.abstractrepresentation.GenObject.AttributeType;
import genlib.abstractrepresentation.GenObject.AttributeType.Type;
import genlib.abstractrepresentation.GeneticAlgorithm;
import genlib.abstractrepresentation.GeneticAlgorithm.Individuum;
import genlib.examples.DistributionExampleExtended;
import genlib.examples.DistributionExampleMinimal;
import genlib.examples.ExampleViewer;
import genlib.examples.GraphExampleExtended;
import genlib.examples.GraphExampleMinimal;
import genlib.examples.StandardExampleMinimal;
import genlib.extended.distributions.BasicTypeDistributions;
import genlib.extended.distributions.LinearDistribution;
import genlib.output.Graph2DLogger;
import genlib.output.Graph2DLogger.AxisType;
import genlib.output.TextLogger.PopulationLogging;
import genlib.output.gui.Graph2D;
import genlib.output.gui.Graph2D.Plot2DContinuousX;
import genlib.output.gui.Graph2D.Plot2DDiscreteX;
import genlib.output.gui.Graph2D.PlotCollection;
import genlib.standard.algorithms.StaticAlgorithmPass;
import genlib.standard.algorithms.StaticAlgorithmStep;
import genlib.standard.algorithms.StaticGeneticAlgorithm;
import genlib.standard.operators.AverageFitness;
import genlib.standard.operators.GenoToPhenoIdentity;
import genlib.standard.operators.KPointCrossover;
import genlib.standard.representations.BooleanStaticLength;
import genlib.standard.representations.BooleanStaticLength.BooleanStaticLengthInstance;
import genlib.standard.representations.ByteStaticLength;
import genlib.standard.representations.ByteStaticLength.ByteStaticLengthInstance;
import genlib.standard.representations.CharStaticLength;
import genlib.standard.representations.CharStaticLength.CharStaticLengthInstance;
import genlib.standard.representations.DoubleStaticLength;
import genlib.standard.representations.DoubleStaticLength.DoubleStaticLengthInstance;
import genlib.standard.representations.FloatStaticLength;
import genlib.standard.representations.FloatStaticLength.FloatStaticLengthInstance;
import genlib.standard.representations.IntStaticLength;
import genlib.standard.representations.IntStaticLength.IntStaticLengthInstance;
import genlib.standard.representations.LongStaticLength;
import genlib.standard.representations.LongStaticLength.LongStaticLengthInstance;
import genlib.standard.representations.ShortStaticLength;
import genlib.standard.representations.ShortStaticLength.ShortStaticLengthInstance;
import genlib.utils.Exceptions.GeneticException;
import genlib.utils.Exceptions.GeneticInternalException;
import genlib.utils.Exceptions.GeneticRuntimeException;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class checks, whether the getAttributes() of all classes and subclasses
 * is complete and if every class, besides excluded ones, is a subclass of GenObject
 * 
 * @author Hilmar
 */
public class LibCompletenessTest {
    
    /**
     * instantiation not allowed
     */
    private LibCompletenessTest () {}
    
    /**
     * returns all *.java-files in all the sub-directories of the root-directory
     * 
     * @param prefix the prefix of the current package-names
     * @param directory the directory to search in
     * @return a list with all the strings in the form package1.package2.className
     * @throws GeneticRuntimeException, if the root directory is not existing
     */
    protected static List <String> getAllJavaFilesInDirRecursive (String prefix, File directory) {
        
        if (!directory.exists())
            throw new GeneticRuntimeException("direction not found: '" + directory.getAbsolutePath() + "'");
                
        List <String> ret = new ArrayList();
        File [] subFiles = directory.listFiles();
        for (File subFile : subFiles) {
            
            //directory => recursive call
            if (subFile.isDirectory())               
                ret.addAll(getAllJavaFilesInDirRecursive( prefix + subFile.getName() + ".", subFile));
            
            //otherwise, check if it is a java-file
            else if (subFile.getName().endsWith(".java"))
                ret.add(prefix + subFile.getName().substring(0, subFile.getName().indexOf(".")));
        }
        return ret;
    }
    
    /**
     * get all the declared fields (= attributes) of a class
     * 
     * @param cl
     * @return 
     */
    protected static Set <Field> getAllDeclaredFieldsOfClass (Class cl) {
        Set <Field> ret = new HashSet();
        for (Field field : cl.getDeclaredFields())
            if (    (field.getModifiers() & Modifier.STATIC) == 0 &&    //ignore static attributes
                    !field.isSynthetic())                               //as example a $this, if class is a subclass
                ret.add(field);
        
        //for fields of superclasses, we have to search bottom-up
        if (cl.getSuperclass() != null)
            ret.addAll(getAllDeclaredFieldsOfClass(cl.getSuperclass()));
        
        return ret;
    }
    
    /**
     * the main-method of this class => do the completeness test
     */
    protected static void doTest () {
        
        try {
            List <String> basicClasses = getAllJavaFilesInDirRecursive("", new File("src"));
            Set <Class> newClasses = new HashSet();     //all classes, that may have further subclasses
            Set <Class> allClasses = new HashSet();     //all final classes of this library

            for (String className : basicClasses)
                newClasses.add(Class.forName(className));
            
            //search for subclasses
            while (newClasses.size() > 0) {
                Class nextClass = newClasses.iterator().next();
                Class[] classes = nextClass.getClasses();
                newClasses.remove(nextClass);
                allClasses.add(nextClass);
                for (Class cl : classes)
                    if (!newClasses.contains(cl) && !allClasses.contains(cl))
                        newClasses.add(cl);
            }
            
            //if there is no standard-constructor (with 0 arguments), we need one instance of all these objects
            Map <Class, Object> noStandardConstructors = new HashMap();
            noStandardConstructors.put(Graph2DLogger.class, new Graph2DLogger(AxisType.averageFitness(), AxisType.averageFitness()));
            noStandardConstructors.put(LinearDistribution.class, new LinearDistribution(0,1));
            noStandardConstructors.put(StaticAlgorithmStep.class, new StaticAlgorithmStep(new StaticAlgorithmPass(1,1,1,0)));
            noStandardConstructors.put(Graph2DLogger.Pt.class, new Graph2DLogger.Pt(0,0));           
            noStandardConstructors.put(Plot2DContinuousX.class, new Plot2DContinuousX("title"));
            noStandardConstructors.put(Plot2DDiscreteX.class, new Plot2DDiscreteX("title"));
            noStandardConstructors.put(BooleanStaticLengthInstance.class, new BooleanStaticLengthInstance(new BooleanStaticLength(1), true));
            noStandardConstructors.put(ByteStaticLengthInstance.class, new ByteStaticLengthInstance(new ByteStaticLength(1), (byte)0));
            noStandardConstructors.put(CharStaticLengthInstance.class, new CharStaticLengthInstance(new CharStaticLength(1), 'A'));
            noStandardConstructors.put(DoubleStaticLengthInstance.class, new DoubleStaticLengthInstance(new DoubleStaticLength(1), 0));
            noStandardConstructors.put(FloatStaticLengthInstance.class, new FloatStaticLengthInstance(new FloatStaticLength(1), 0));
            noStandardConstructors.put(IntStaticLengthInstance.class, new IntStaticLengthInstance(new IntStaticLength(1), 0));
            noStandardConstructors.put(LongStaticLengthInstance.class, new LongStaticLengthInstance(new LongStaticLength(1), 0));
            noStandardConstructors.put(ShortStaticLengthInstance.class, new ShortStaticLengthInstance(new ShortStaticLength(1), (short)0));            
            noStandardConstructors.put(BooleanStaticLength.class, new BooleanStaticLength(1));
            noStandardConstructors.put(ByteStaticLength.class, new ByteStaticLength(1));
            noStandardConstructors.put(CharStaticLength.class, new CharStaticLength(1));
            noStandardConstructors.put(DoubleStaticLength.class, new DoubleStaticLength(1));
            noStandardConstructors.put(FloatStaticLength.class, new FloatStaticLength(1));
            noStandardConstructors.put(IntStaticLength.class, new IntStaticLength(1));
            noStandardConstructors.put(LongStaticLength.class, new LongStaticLength(1));
            noStandardConstructors.put(ShortStaticLength.class, new ShortStaticLength(1));
            noStandardConstructors.put(StaticAlgorithmPass.class, new StaticAlgorithmPass(1,1,1,0));
            noStandardConstructors.put(AttributeType.class, new AttributeType(Type.MainAttribute));
            noStandardConstructors.put(PopulationLogging.class, PopulationLogging.populationLogAll());
            noStandardConstructors.put(KPointCrossover.class, new KPointCrossover(1));            
            noStandardConstructors.put(AxisType.class, AxisType.averageFitness());
            noStandardConstructors.put(Attribute.class, new Attribute(new AttributeType(Type.MainAttribute), "attr", 0));
            noStandardConstructors.put(PlotCollection.class, PlotCollection.createBarChart("title", "xAxis", "yAxis", 0, true, true, new Plot2DDiscreteX("title")));
            GeneticAlgorithm gA = new StaticGeneticAlgorithm();
            gA.setGenoToPhenoOp(new GenoToPhenoIdentity());
            gA.setFitnessOp(new AverageFitness());
            noStandardConstructors.put(Individuum.class, gA.new Individuum(new BooleanStaticLengthInstance(new BooleanStaticLength(1), true), new StaticAlgorithmStep(new StaticAlgorithmPass(1,1,1,0))));
            
            //All the special classes, who are no subclasses of GenObject
            Set <Class> ignoredClasses = new HashSet();
            ignoredClasses.add(Utils.class);
            ignoredClasses.add(Exceptions.class);
            ignoredClasses.add(GeneticException.class);
            ignoredClasses.add(GeneticRuntimeException.class);
            ignoredClasses.add(GeneticInternalException.class);
            ignoredClasses.add(DistributionExampleExtended.class);
            ignoredClasses.add(DistributionExampleMinimal.class);
            ignoredClasses.add(ExampleViewer.class);
            ignoredClasses.add(GraphExampleExtended.class);
            ignoredClasses.add(GraphExampleMinimal.class);
            ignoredClasses.add(StandardExampleMinimal.class);
            ignoredClasses.add(BasicTypeDistributions.class);
            ignoredClasses.add(LibCompletenessTest.class);
            ignoredClasses.add(Graph2D.class);
            
            for (Class cl : allClasses) {
                if (    ignoredClasses.contains(cl) ||                      //ignored class, because it is no subclass of GenObject on purpose
                        cl.isInterface() ||                                 //interfaces are ignored
                        cl.isEnum() ||                                      //enums create classes too => ignore them
                        (cl.getModifiers() & Modifier.ABSTRACT) != 0)       //abstract classes are ignored
                    continue;
                
                //still no subclass of GenObject => error
                if (!GenObject.class.isAssignableFrom(cl))
                    System.err.println("Warning: class is not inherited from GenObject: '" + cl + "'");
                
                else {
                    try {
                        Method method = cl.getMethod("getAttributes");
                        List<Attribute> attributes = null;
                        
                        //we don't have a standard-constructor, use the special instantiated class
                        if (noStandardConstructors.containsKey(cl))
                            attributes = (List<Attribute>)method.invoke(noStandardConstructors.get(cl));                                                
                        else
                            attributes = (List<Attribute>)method.invoke(cl.newInstance());
                        
                        //keys are all the strings, the invoked method returns
                        Set <String> keys = new HashSet();
                        for (Attribute attr : attributes)
                            keys.add(attr.getKey());
                        
                        //go through all expected fields => write error if one not found
                        for (Field field : getAllDeclaredFieldsOfClass(cl)) 
                            if (!keys.remove(field.getName()))
                                System.err.println("Warning: There is a key missing in getAttributes in class '" + cl + "' (name of field: '" + field.getName() + "')");
                        
                        //keys still left => write error too
                        for (String key : keys)
                            System.err.println("Warning: There is a key of an unknown attribute in getAttributes in class '" + cl + "' (name of field: '" + key + "')");
                        
                    } catch (Exception e) {
                        
                        //we can't instantiate a class? She probably has no standard-constructor
                        if (e instanceof java.lang.InstantiationException)
                            System.err.println("Warning: No standard-constructor available: '" + cl + "'");
                        else
                            throw new GeneticRuntimeException(e);
                    }
                }              
            }
            
        } catch (ClassNotFoundException e) {
            throw new GeneticRuntimeException(e);
        }
    }
    
    /**
     * the main-entry-point of this testing routine
     * 
     * @param args the arguments are ignored
     */
    public static void main (String [] args) {
        doTest();
    }
    
}
