/**
 * 
 */
package edu.ucdenver.ccp.nlp.wrapper.conceptmapper;

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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.conceptMapper.support.stemmer.Stemmer;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.metadata.TypeSystemDescription;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.collections.CombinationsUtil;
import edu.ucdenver.ccp.common.string.StringUtil;
import edu.ucdenver.ccp.datasource.fileparsers.obo.OntologyUtil.SynonymType;
import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.ConceptMapperFactory.SearchStrategyParamValue;
import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.ConceptMapperFactory.TokenNormalizerConfigParam.CaseMatchParamValue;
import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.stemmer.ConceptMapperStemmerFactory;
import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.stemmer.ConceptMapperStemmerFactory.StemmerType;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class ConceptMapperPermutationFactory {
	private static Logger logger = Logger.getLogger(ConceptMapperPermutationFactory.class);

	/**
	 * 
	 */
	public static final String CASE_MATCH_KEY = "CaseMatch:";

	/**
	 * 
	 */
	public static final String SEARCH_STRATEGY_KEY = "SearchStrategy:";

	public static final String STEMMER_KEY = "Stemmer:";
	public static final String STOPWORDS_KEY = "Stopwords:";

	public static final String ORDER_INDEPENDENT_LOOKUP_KEY = "OrderIndependentLookup:";

	// public static final String REPLACE_COMMA_WITH_AND_KEY = "ReplaceCommaWithAnd:";
	public static final String SYNONYM_TYPE_KEY = "SynonymType:";

	public static final String FIND_ALL_MATCHES_KEY = "FindAllMatches:";

	public static final List<List<String>> PARAM_COMBINATIONS = CollectionsUtil.createList(CombinationsUtil
			.computeCombinations(buildParameterValueLists()));;

	/**
	 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
	 * 
	 */
	public enum ConceptMapperStemmerParam implements UimaConfigParamValue<Class<? extends Stemmer>> {
		BIOLEMMATIZER(StemmerType.BIOLEMMATIZER),
		PORTER(StemmerType.PORTER),
		NONE(StemmerType.NONE);

		private final StemmerType stemmerType;

		private ConceptMapperStemmerParam(StemmerType stemmerType) {
			this.stemmerType = stemmerType;
		}

		@Override
		public Class<? extends Stemmer> paramValue() {
			return ConceptMapperStemmerFactory.getStemmerClass(stemmerType);
		}
	}

	/**
	 * PubMed stoplist plus "or"
	 * 
	 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
	 * 
	 */
	public enum ConceptMapperStopWordsParam implements UimaConfigParamValue<String[]> {
		PUBMED(new String[] { "a", "about", "again", "all", "almost", "also", "although", "always", "among", "an",
				"and", "another", "any", "are", "as", "at", "be", "because", "been", "before", "being", "between",
				"both", "but", "by", "can", "could", "did", "do", "does", "done", "due", "during", "each", "either",
				"enough", "especially", "etc", "for", "found", "from", "further", "had", "has", "have", "having",
				"here", "how", "however", "i", "if", "in", "into", "is", "it", "its", "itself", "just", "kg", "km",
				"made", "mainly", "make", "may", "mg", "might", "ml", "mm", "most", "mostly", "must", "nearly",
				"neither", "no", "nor", "obtained", "of", "often", "on", "or", "our", "overall", "perhaps", "pmid",
				"quite", "rather", "really", "regarding", "seem", "seen", "several", "should", "show", "showed",
				"shown", "shows", "significantly", "since", "so", "some", "such", "than", "that", "the", "their",
				"theirs", "them", "then", "there", "therefore", "these", "they", "this", "those", "through", "thus",
				"to", "upon", "use", "used", "using", "various", "very", "was", "we", "were", "what", "when", "which",
				"while", "with", "within", "without", "would" }),
		NONE(new String[] {});

		private final String[] stopWords;

		private ConceptMapperStopWordsParam(String[] stopWords) {
			this.stopWords = stopWords;
		}

		@Override
		public String[] paramValue() {
			return stopWords;
		}
	}

	/**
	 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
	 * 
	 */
	public enum ConceptMapperOrderIndependentLookupParam implements UimaConfigParamValue<Boolean> {
		ON(true),
		OFF(false);

		private final Boolean doOrderIndependentLookup;

		private ConceptMapperOrderIndependentLookupParam(Boolean doOrderIndependentLookup) {
			this.doOrderIndependentLookup = doOrderIndependentLookup;
		}

		@Override
		public Boolean paramValue() {
			return doOrderIndependentLookup;
		}
	}

	// /**
	// * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
	// *
	// */
	// public enum ConceptMapperReplaceCommaWithAndParam implements UimaConfigParamValue<Boolean> {
	// ON(true),
	// OFF(false);
	//
	// private final Boolean replaceCommaWithAnd;
	//
	// private ConceptMapperReplaceCommaWithAndParam(Boolean replaceCommaWithAnd) {
	// this.replaceCommaWithAnd = replaceCommaWithAnd;
	// }
	//
	// @Override
	// public Boolean paramValue() {
	// return replaceCommaWithAnd;
	// }
	// }

	/**
	 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
	 * 
	 */
	public enum ConceptMapperSynonymTypeParam implements UimaConfigParamValue<SynonymType> {
		ALL(SynonymType.ALL),
		EXACT_ONLY(SynonymType.EXACT);

		private final SynonymType synonymType;

		private ConceptMapperSynonymTypeParam(SynonymType synonymType) {
			this.synonymType = synonymType;
		}

		@Override
		public SynonymType paramValue() {
			return synonymType;
		}
	}

	/**
	 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
	 * 
	 */
	public enum ConceptMapperFindAllMatchesParam implements UimaConfigParamValue<Boolean> {
		YES(true),
		NO(false);

		private final Boolean findAllMatches;

		private ConceptMapperFindAllMatchesParam(Boolean findAllMatches) {
			this.findAllMatches = findAllMatches;
		}

		@Override
		public Boolean paramValue() {
			return findAllMatches;
		}
	}

	/**
	 * @return
	 * 
	 */
	private static List<Collection<String>> buildParameterValueLists() {
		List<Collection<String>> paramValueLists = new ArrayList<Collection<String>>();
		paramValueLists.add(getSearchStrategyParamValues());
		paramValueLists.add(getCaseMatchParamValues());
		paramValueLists.add(getStemmerParamValues());
		paramValueLists.add(getStopwordListParamValues());
		paramValueLists.add(getOrderIndependentLookupParamValues());
		paramValueLists.add(getFindAllMatchesParamValues());
		// paramValueLists.add(getReplaceCommaWithAndParamValues());
		paramValueLists.add(getSynonymTypeParamValues());
		return paramValueLists;
	}

	/**
	 * @return a list of the different Concept Mapper "find all matches" parameter value options
	 */
	private static List<String> getFindAllMatchesParamValues() {
		List<String> paramValues = new ArrayList<String>();
		for (ConceptMapperFindAllMatchesParam value : ConceptMapperFindAllMatchesParam.values()) {
			paramValues.add(FIND_ALL_MATCHES_KEY + value.name());
		}
		return paramValues;
	}

	// /**
	// * @return a list of the different Concept Mapper replace comma with "and" parameter value
	// * options
	// */
	// private static List<String> getReplaceCommaWithAndParamValues() {
	// List<String> paramValues = new ArrayList<String>();
	// for (ConceptMapperReplaceCommaWithAndParam value :
	// ConceptMapperReplaceCommaWithAndParam.values()) {
	// paramValues.add(REPLACE_COMMA_WITH_AND_KEY + value.name());
	// }
	// return paramValues;
	// }

	/**
	 * @return a list of the different Concept Mapper replace comma with "and" parameter value
	 *         options
	 */
	private static List<String> getSynonymTypeParamValues() {
		List<String> paramValues = new ArrayList<String>();
		for (ConceptMapperSynonymTypeParam value : ConceptMapperSynonymTypeParam.values()) {
			paramValues.add(SYNONYM_TYPE_KEY + value.name());
		}
		return paramValues;
	}

	/**
	 * @return a list of the different Concept Mapper order independent lookup parameter value
	 *         options
	 */
	private static List<String> getOrderIndependentLookupParamValues() {
		List<String> paramValues = new ArrayList<String>();
		for (ConceptMapperOrderIndependentLookupParam value : ConceptMapperOrderIndependentLookupParam.values()) {
			paramValues.add(ORDER_INDEPENDENT_LOOKUP_KEY + value.name());
		}
		return paramValues;
	}

	/**
	 * @return a list of the different Concept Mapper stemmer parameter value options
	 */
	private static List<String> getStemmerParamValues() {
		List<String> paramValues = new ArrayList<String>();
		for (ConceptMapperStemmerParam value : ConceptMapperStemmerParam.values()) {
			paramValues.add(STEMMER_KEY + value.name());
		}
		return paramValues;
	}

	/**
	 * @return a list of the different Concept Mapper stopword list parameter value options
	 */
	private static List<String> getStopwordListParamValues() {
		List<String> paramValues = new ArrayList<String>();
		for (ConceptMapperStopWordsParam value : ConceptMapperStopWordsParam.values()) {
			paramValues.add(STOPWORDS_KEY + value.name());
		}
		return paramValues;
	}

	/**
	 * @return a list of the different Concept Mapper "search strategy" parameter value options
	 */
	private static List<String> getSearchStrategyParamValues() {
		List<String> searchStrategyParamValues = new ArrayList<String>();
		for (SearchStrategyParamValue value : SearchStrategyParamValue.values()) {
			searchStrategyParamValues.add(SEARCH_STRATEGY_KEY + value.name());
		}
		return searchStrategyParamValues;
	}

	/**
	 * @return a list of the different Concept Mapper "case match" parameter value options
	 */
	private static List<String> getCaseMatchParamValues() {
		List<String> paramValues = new ArrayList<String>();
		for (CaseMatchParamValue value : CaseMatchParamValue.values()) {
			paramValues.add(CASE_MATCH_KEY + value.name());
		}
		return paramValues;
	}

	/**
	 * @param conceptMapperPermutationNumber
	 * @param spanFeatureStructureClass
	 * @return
	 * @throws IOException
	 * @throws UIMAException
	 */
	public static AnalysisEngineDescription buildConceptMapperAggregatePermutation(int conceptMapperPermutationNumber,
			TypeSystemDescription tsd, File dictionaryFile, Class<? extends Annotation> spanFeatureStructureClass)
			throws UIMAException, IOException {
		List<String> params = PARAM_COMBINATIONS.get(conceptMapperPermutationNumber);
		return buildConceptMapperAggregate(params, tsd, dictionaryFile, spanFeatureStructureClass);
	}

	private static AnalysisEngineDescription buildConceptMapperAggregate(List<String> paramValues,
			TypeSystemDescription tsd, File dictionaryFile, Class<? extends Annotation> spanFeatureStructureClass)
			throws UIMAException, IOException {

		CaseMatchParamValue caseMatchParamValue = getCaseMatchParamValue(paramValues);
		SearchStrategyParamValue searchStrategyParamValue = getSearchStrategyParamValue(paramValues);
		Class<? extends Stemmer> stemmerClass = getStemmerClass(paramValues);
		String[] stopwordList = getStopWordList(paramValues);
		boolean orderIndependentLookup = getOrderIndependentLookup(paramValues);
		boolean findAllMatches = getFindAllMatches(paramValues);
		// boolean replaceCommaWithAnd = getReplaceCommaWithAnd(paramValues);
		boolean replaceCommaWithAnd = false; // this parameter doesn't appear to be enabled in
												// ConceptMapper

		return ConceptMapperAggregateFactory.getOffsetTokenizerConceptMapperAggregateDescription(tsd, dictionaryFile,
				caseMatchParamValue, searchStrategyParamValue, spanFeatureStructureClass, stemmerClass, stopwordList,
				orderIndependentLookup, findAllMatches, replaceCommaWithAnd);
	}

	/**
	 * @param paramValues
	 * @return
	 */
	private static SearchStrategyParamValue getSearchStrategyParamValue(List<String> paramValues) {
		String value = StringUtil.removePrefix(paramValues.get(0), SEARCH_STRATEGY_KEY);
		return SearchStrategyParamValue.valueOf(value);
	}

	/**
	 * @param paramValues
	 * @return
	 */
	private static CaseMatchParamValue getCaseMatchParamValue(List<String> paramValues) {
		String value = StringUtil.removePrefix(paramValues.get(1), CASE_MATCH_KEY);
		return CaseMatchParamValue.valueOf(value);
	}

	/**
	 * @param paramValues
	 * @return
	 */
	private static Class<? extends Stemmer> getStemmerClass(List<String> paramValues) {
		String value = StringUtil.removePrefix(paramValues.get(2), STEMMER_KEY);
		return ConceptMapperStemmerParam.valueOf(value).paramValue();
	}

	/**
	 * @param paramValues
	 * @return
	 */
	private static String[] getStopWordList(List<String> paramValues) {
		String value = StringUtil.removePrefix(paramValues.get(3), STOPWORDS_KEY);
		return ConceptMapperStopWordsParam.valueOf(value).paramValue();
	}

	/**
	 * @param paramValues
	 * @return
	 */
	private static boolean getOrderIndependentLookup(List<String> paramValues) {
		String value = StringUtil.removePrefix(paramValues.get(4), ORDER_INDEPENDENT_LOOKUP_KEY);
		return ConceptMapperOrderIndependentLookupParam.valueOf(value).paramValue();
	}

	/**
	 * @param paramValues
	 * @return
	 */
	private static boolean getFindAllMatches(List<String> paramValues) {
		String value = StringUtil.removePrefix(paramValues.get(5), FIND_ALL_MATCHES_KEY);
		return ConceptMapperFindAllMatchesParam.valueOf(value).paramValue();
	}

	// /**
	// * @param paramValues
	// * @return
	// */
	// private static boolean getReplaceCommaWithAnd(List<String> paramValues) {
	// String value = StringUtil.removePrefix(paramValues.get(6), REPLACE_COMMA_WITH_AND_KEY);
	// return ConceptMapperReplaceCommaWithAndParam.valueOf(value).paramValue();
	// }

	/**
	 * @param paramValues
	 * @return
	 */
	private static SynonymType getSynonymType(List<String> paramValues) {
		String value = StringUtil.removePrefix(paramValues.get(6), SYNONYM_TYPE_KEY);
		return ConceptMapperSynonymTypeParam.valueOf(value).paramValue();
	}

	/**
	 * @param conceptMapperPermutationNumber
	 * @return the {@link SynonymType} to use for the specified permutation number
	 */
	public static SynonymType getSynonymType(int conceptMapperPermutationNumber) {
		return getSynonymType(PARAM_COMBINATIONS.get(conceptMapperPermutationNumber));
	}

	public static void main(String[] args) {
		BasicConfigurator.configure();
		int index = 0;
		for (List<String> paramPermutation : PARAM_COMBINATIONS) {
			logger.info("Permutation " + index++ + ": " + paramPermutation.toString());
		}
	}

}
