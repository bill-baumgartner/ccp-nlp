package edu.ucdenver.ccp.nlp.uima.mention.impl;

/*
 * #%L
 * Colorado Computational Pharmacology's nlp module
 * %%
 * Copyright (C) 2012 - 2014 Regents of the University of Colorado
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Regents of the University of Colorado nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.DoubleArray;
import org.apache.uima.jcas.cas.FloatArray;
import org.apache.uima.jcas.cas.IntegerArray;
import org.apache.uima.jcas.cas.StringArray;

import edu.ucdenver.ccp.nlp.core.annotation.impl.KnowledgeRepresentationWrapperException;
import edu.ucdenver.ccp.nlp.core.mention.PrimitiveSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPBooleanSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPDoubleSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPFloatSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPIntegerSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPPrimitiveSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPStringSlotMention;

/**
 * Factory class for creating {@link PrimitiveSlotMention} objects based on the inputted slot
 * values.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class CCPPrimitiveSlotMentionFactory {
	private static Logger logger = Logger.getLogger(CCPPrimitiveSlotMentionFactory.class);

	public static PrimitiveSlotMention createPrimitiveSlotMention(String slotMentionName, Object slotValue, JCas jcas)
			throws KnowledgeRepresentationWrapperException {
		if (slotValue == null) {
			return null;
		}
		PrimitiveSlotMention ccpPSM = null;
		if (slotValue instanceof String) {
			ccpPSM = getWrappedCCPStringSlotMention(slotMentionName, (String) slotValue, jcas);
		} else if (slotValue instanceof Integer) {
			ccpPSM = getWrappedCCPIntegerSlotMention(slotMentionName, (Integer) slotValue, jcas);
		} else if (slotValue instanceof Float) {
			ccpPSM = getWrappedCCPFloatSlotMention(slotMentionName, (Float) slotValue, jcas);
		} else if (slotValue instanceof Double) {
			ccpPSM = getWrappedCCPDoubleSlotMention(slotMentionName, (Double) slotValue, jcas);
		} else if (slotValue instanceof Boolean) {
			ccpPSM = getWrappedCCPBooleanSlotMention(slotMentionName, (Boolean) slotValue, jcas);
		} else {
			throw new KnowledgeRepresentationWrapperException(
					"Cannot create a UIMA Wrapped Primitive Slot Mention with a slot value of type: "
							+ slotValue.getClass().getName());
		}

		return ccpPSM;
	}

	public static PrimitiveSlotMention createPrimitiveSlotMention(CCPPrimitiveSlotMention ccpPrimitiveSlotMention)
			throws KnowledgeRepresentationWrapperException {
		if (ccpPrimitiveSlotMention instanceof CCPBooleanSlotMention) {
			return new WrappedCCPBooleanSlotMention((CCPBooleanSlotMention) ccpPrimitiveSlotMention);
		} else if (ccpPrimitiveSlotMention instanceof CCPStringSlotMention) {
			return new WrappedCCPStringSlotMention((CCPStringSlotMention) ccpPrimitiveSlotMention);
		} else if (ccpPrimitiveSlotMention instanceof CCPIntegerSlotMention) {
			return new WrappedCCPIntegerSlotMention((CCPIntegerSlotMention) ccpPrimitiveSlotMention);
		} else if (ccpPrimitiveSlotMention instanceof CCPFloatSlotMention) {
			return new WrappedCCPFloatSlotMention((CCPFloatSlotMention) ccpPrimitiveSlotMention);
		} else if (ccpPrimitiveSlotMention instanceof CCPDoubleSlotMention) {
			return new WrappedCCPDoubleSlotMention((CCPDoubleSlotMention) ccpPrimitiveSlotMention);
		} else {
			throw new KnowledgeRepresentationWrapperException("Unknown CCPrimitiveSlotMention type: "
					+ ccpPrimitiveSlotMention.getClass().getName()
					+ " . Cannot create a UIMA Wrapped Primitive Slot Mention for this type.");
		}
	}

	public static CCPPrimitiveSlotMention createCCPPrimitiveSlotMention(String slotMentionName, Object slotValue,
			JCas jcas) throws KnowledgeRepresentationWrapperException {
		if (slotValue == null) {
			return null;
		}
		CCPPrimitiveSlotMention ccpPSM = null;
		if (slotValue instanceof String) {
			ccpPSM = initializeStringSlotMention(slotMentionName, (String) slotValue, jcas);
		} else if (slotValue instanceof Integer) {
			ccpPSM = initializeIntegerSlotMention(slotMentionName, (Integer) slotValue, jcas);
		} else if (slotValue instanceof Float) {
			ccpPSM = initializeFloatSlotMention(slotMentionName, (Float) slotValue, jcas);
		} else if (slotValue instanceof Double) {
			ccpPSM = initializeDoubleSlotMention(slotMentionName, (Double) slotValue, jcas);
		} else if (slotValue instanceof Boolean) {
			ccpPSM = initializeBooleanSlotMention(slotMentionName, (Boolean) slotValue, jcas);
		} else {
			throw new KnowledgeRepresentationWrapperException(
					"Cannot create a UIMA Primitive Slot Mention with a slot value of type: "
							+ slotValue.getClass().getName());
		}

		return ccpPSM;
	}

	public static CCPPrimitiveSlotMention createCCPPrimitiveSlotMention(String slotMentionName,
			Collection<Object> slotValues, JCas jcas) throws KnowledgeRepresentationWrapperException {

		if (slotValues.size() == 0) {
			return null;
		}
		CCPPrimitiveSlotMention ccpPSM = null;
		Object firstSlotValue = Collections.list(Collections.enumeration(slotValues)).get(0);
		if (firstSlotValue instanceof String) {
			ccpPSM = initializeStringSlotMention(slotMentionName, castToString(slotValues), jcas);
		} else if (firstSlotValue instanceof Integer) {
			ccpPSM = initializeIntegerSlotMention(slotMentionName, castToInteger(slotValues), jcas);
		} else if (firstSlotValue instanceof Float) {
			ccpPSM = initializeFloatSlotMention(slotMentionName, castToFloat(slotValues), jcas);
		} else if (firstSlotValue instanceof Double) {
			ccpPSM = initializeDoubleSlotMention(slotMentionName, castToDouble(slotValues), jcas);
		} else if (firstSlotValue instanceof Boolean) {
			ccpPSM = initializeBooleanSlotMention(slotMentionName, castToBoolean(slotValues), jcas);
		} else {
			throw new KnowledgeRepresentationWrapperException(
					"Cannot create a UIMA Primitive Slot Mention with a slot value of type: "
							+ firstSlotValue.getClass().getName());
		}

		return ccpPSM;
	}

	public static CCPStringSlotMention createCCPStringSlotMention(String slotMentionName,
			Collection<String> slotValues, JCas jcas) throws KnowledgeRepresentationWrapperException {
		return initializeStringSlotMention(slotMentionName, slotValues, jcas);
	}

	public static CCPFloatSlotMention createCCPFloatSlotMention(String slotMentionName, Collection<Float> slotValues,
			JCas jcas) throws KnowledgeRepresentationWrapperException {
		return initializeFloatSlotMention(slotMentionName, slotValues, jcas);
	}

	public static CCPIntegerSlotMention createCCPIntegerSlotMention(String slotMentionName,
			Collection<Integer> slotValues, JCas jcas) throws KnowledgeRepresentationWrapperException {
		return initializeIntegerSlotMention(slotMentionName, slotValues, jcas);
	}

	public static CCPBooleanSlotMention createCCPBooleanSlotMention(String slotMentionName, Boolean slotValue, JCas jcas)
			throws KnowledgeRepresentationWrapperException {
		return initializeBooleanSlotMention(slotMentionName, slotValue, jcas);
	}

	private static Collection<String> castToString(Collection<Object> input)
			throws KnowledgeRepresentationWrapperException {
		Collection<String> strings = new ArrayList<String>();
		for (Object obj : input) {
			if (obj instanceof String) {
				strings.add((String) obj);
			} else {
				throw new KnowledgeRepresentationWrapperException("Cannot cast from " + obj.getClass().getName()
						+ " to String!!!");
			}
		}
		return strings;
	}

	private static Collection<Integer> castToInteger(Collection<Object> input)
			throws KnowledgeRepresentationWrapperException {
		Collection<Integer> strings = new ArrayList<Integer>();
		for (Object obj : input) {
			if (obj instanceof Integer) {
				strings.add((Integer) obj);
			} else {
				throw new KnowledgeRepresentationWrapperException("Cannot cast from " + obj.getClass().getName()
						+ " to Integer!!!");
			}
		}
		return strings;
	}

	private static Collection<Float> castToFloat(Collection<Object> input)
			throws KnowledgeRepresentationWrapperException {
		Collection<Float> strings = new ArrayList<Float>();
		for (Object obj : input) {
			if (obj instanceof Float) {
				strings.add((Float) obj);
			} else {
				throw new KnowledgeRepresentationWrapperException("Cannot cast from " + obj.getClass().getName()
						+ " to Float!!!");
			}
		}
		return strings;
	}

	private static Collection<Double> castToDouble(Collection<Object> input)
			throws KnowledgeRepresentationWrapperException {
		Collection<Double> strings = new ArrayList<Double>();
		for (Object obj : input) {
			if (obj instanceof Double) {
				strings.add((Double) obj);
			} else {
				throw new KnowledgeRepresentationWrapperException("Cannot cast from " + obj.getClass().getName()
						+ " to Double!!!");
			}
		}
		return strings;
	}

	private static Collection<Boolean> castToBoolean(Collection<Object> input)
			throws KnowledgeRepresentationWrapperException {
		Collection<Boolean> strings = new ArrayList<Boolean>();
		for (Object obj : input) {
			if (obj instanceof Boolean) {
				strings.add((Boolean) obj);
			} else {
				throw new KnowledgeRepresentationWrapperException("Cannot cast from " + obj.getClass().getName()
						+ " to Boolean!!!");
			}
		}
		return strings;
	}

	private static WrappedCCPStringSlotMention getWrappedCCPStringSlotMention(String slotMentionName, String slotValue,
			JCas jcas) {
		return new WrappedCCPStringSlotMention(initializeStringSlotMention(slotMentionName, slotValue, jcas));
	}

	private static WrappedCCPIntegerSlotMention getWrappedCCPIntegerSlotMention(String slotMentionName,
			Integer slotValue, JCas jcas) {
		return new WrappedCCPIntegerSlotMention(initializeIntegerSlotMention(slotMentionName, slotValue, jcas));
	}

	private static WrappedCCPFloatSlotMention getWrappedCCPFloatSlotMention(String slotMentionName, Float slotValue,
			JCas jcas) {
		return new WrappedCCPFloatSlotMention(initializeFloatSlotMention(slotMentionName, slotValue, jcas));
	}

	private static WrappedCCPDoubleSlotMention getWrappedCCPDoubleSlotMention(String slotMentionName, Double slotValue,
			JCas jcas) {
		return new WrappedCCPDoubleSlotMention(initializeDoubleSlotMention(slotMentionName, slotValue, jcas));
	}

	private static WrappedCCPBooleanSlotMention getWrappedCCPBooleanSlotMention(String slotMentionName,
			Boolean slotValue, JCas jcas) {
		return new WrappedCCPBooleanSlotMention(initializeBooleanSlotMention(slotMentionName, slotValue, jcas));
	}

	/**
	 * Initializes a new CCPStringSlotMention
	 * 
	 * @param slotMentionName
	 * @param slotValue
	 * @param jcas
	 * @return
	 */
	private static CCPStringSlotMention initializeStringSlotMention(String slotMentionName, String slotValue, JCas jcas) {
		Collection<String> slotValues = new ArrayList<String>();
		slotValues.add(slotValue);
		return initializeStringSlotMention(slotMentionName, slotValues, jcas);
	}

	/**
	 * Initializes a new CCPStringSlotMention
	 * 
	 * @param slotMentionName
	 * @param slotValue
	 * @param jcas
	 * @return
	 */
	private static CCPStringSlotMention initializeStringSlotMention(String slotMentionName,
			Collection<String> slotValues, JCas jcas) {
		StringArray slotValuesArray = new StringArray(jcas, slotValues.size());
		int index = 0;
		for (String slotValue : slotValues) {
			slotValuesArray.set(index++, slotValue);
		}

		CCPStringSlotMention ccpSSM = new CCPStringSlotMention(jcas);
		ccpSSM.setSlotValues(slotValuesArray);
		ccpSSM.setMentionName(slotMentionName);
		return ccpSSM;
	}

	/**
	 * Initializes a new CCPIntegerSlotMention
	 * 
	 * @param slotMentionName
	 * @param slotValue
	 * @param jcas
	 * @return
	 */
	private static CCPIntegerSlotMention initializeIntegerSlotMention(String slotMentionName, Integer slotValue,
			JCas jcas) {
		Collection<Integer> slotValues = new ArrayList<Integer>();
		slotValues.add(slotValue);
		return initializeIntegerSlotMention(slotMentionName, slotValues, jcas);
	}

	private static CCPIntegerSlotMention initializeIntegerSlotMention(String slotMentionName,
			Collection<Integer> slotValues, JCas jcas) {
		IntegerArray slotValuesArray = new IntegerArray(jcas, slotValues.size());
		int index = 0;
		for (Integer slotValue : slotValues) {
			slotValuesArray.set(index++, slotValue);
		}

		CCPIntegerSlotMention ccpSSM = new CCPIntegerSlotMention(jcas);
		ccpSSM.setSlotValues(slotValuesArray);
		ccpSSM.setMentionName(slotMentionName);
		return ccpSSM;
	}

	/**
	 * Initializes a new CCPFloatSlotMention
	 * 
	 * @param slotMentionName
	 * @param slotValue
	 * @param jcas
	 * @return
	 */
	private static CCPFloatSlotMention initializeFloatSlotMention(String slotMentionName, Float slotValue, JCas jcas) {
		Collection<Float> slotValues = new ArrayList<Float>();
		slotValues.add(slotValue);
		return initializeFloatSlotMention(slotMentionName, slotValues, jcas);
	}

	private static CCPFloatSlotMention initializeFloatSlotMention(String slotMentionName, Collection<Float> slotValues,
			JCas jcas) {
		FloatArray slotValuesArray = new FloatArray(jcas, slotValues.size());
		int index = 0;
		for (Float slotValue : slotValues) {
			slotValuesArray.set(index++, slotValue);
		}

		CCPFloatSlotMention ccpSSM = new CCPFloatSlotMention(jcas);
		ccpSSM.setSlotValues(slotValuesArray);
		ccpSSM.setMentionName(slotMentionName);
		return ccpSSM;
	}

	private static CCPDoubleSlotMention initializeDoubleSlotMention(String slotMentionName, Double slotValue, JCas jcas) {
		Collection<Double> slotValues = new ArrayList<Double>();
		slotValues.add(slotValue);
		return initializeDoubleSlotMention(slotMentionName, slotValues, jcas);
	}

	private static CCPDoubleSlotMention initializeDoubleSlotMention(String slotMentionName,
			Collection<Double> slotValues, JCas jcas) {
		DoubleArray slotValuesArray = new DoubleArray(jcas, slotValues.size());
		int index = 0;
		for (Double slotValue : slotValues) {
			slotValuesArray.set(index++, slotValue);
		}

		CCPDoubleSlotMention ccpSSM = new CCPDoubleSlotMention(jcas);
		ccpSSM.setSlotValues(slotValuesArray);
		ccpSSM.setMentionName(slotMentionName);
		return ccpSSM;
	}

	/**
	 * Initializes a new CCPBooleanSlotMention
	 * 
	 * @param slotMentionName
	 * @param slotValue
	 * @param jcas
	 * @return
	 */
	private static CCPBooleanSlotMention initializeBooleanSlotMention(String slotMentionName, Boolean slotValue,
			JCas jcas) {
		CCPBooleanSlotMention ccpSSM = new CCPBooleanSlotMention(jcas);
		ccpSSM.setSlotValue(slotValue);
		ccpSSM.setMentionName(slotMentionName);
		return ccpSSM;
	}

	private static CCPBooleanSlotMention initializeBooleanSlotMention(String slotMentionName,
			Collection<Boolean> slotValues, JCas jcas) throws KnowledgeRepresentationWrapperException {
		if (slotValues.size() != 1) {
			throw new KnowledgeRepresentationWrapperException(
					"Boolean slot mentions can only store a single boolean value. Number of slot values trying to be used: "
							+ slotValues.size());
		} else {
			Boolean slotValue = Collections.list(Collections.enumeration(slotValues)).get(0);
			return initializeBooleanSlotMention(slotMentionName, slotValue, jcas);
		}
	}
}
