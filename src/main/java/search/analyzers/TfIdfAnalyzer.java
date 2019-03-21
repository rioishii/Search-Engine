package search.analyzers;

import datastructures.concrete.KVPair;
import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IList;
import datastructures.interfaces.ISet;
import search.models.Webpage;

import java.net.URI;

/**
 * This class is responsible for computing how "relevant" any given document is
 * to a given search query.
 *
 * See the spec for more details.
 */
public class TfIdfAnalyzer {
    // This field must contain the IDF score for every single word in all
    // the documents.
    private IDictionary<String, Double> idfScores;

    // This field must contain the TF-IDF vector for each webpage you were given
    // in the constructor.
    //
    // We will use each webpage's page URI as a unique key.
    private IDictionary<URI, IDictionary<String, Double>> documentTfIdfVectors;

    // Feel free to add extra fields and helper methods.
    private IDictionary<URI, Double> documentNormVector;

    public TfIdfAnalyzer(ISet<Webpage> webpages) {
        // Implementation note: We have commented these method calls out so your
        // search engine doesn't immediately crash when you try running it for the
        // first time.
        //
        // You should uncomment these lines when you're ready to begin working
        // on this class.

        this.idfScores = this.computeIdfScores(webpages);
        this.documentTfIdfVectors = this.computeAllDocumentTfIdfVectors(webpages);
        this.documentNormVector = new ChainedHashDictionary<>();
        for (KVPair<URI, IDictionary<String, Double>> pair : this.documentTfIdfVectors) {
            URI uri = pair.getKey();
            double norm = norm(pair.getValue());
            documentNormVector.put(uri, norm);
        }
    }

    // Note: this method, strictly speaking, doesn't need to exist. However,
    // we've included it so we can add some unit tests to help verify that your
    // constructor correctly initializes your fields.
    public IDictionary<URI, IDictionary<String, Double>> getDocumentTfIdfVectors() {
        return this.documentTfIdfVectors;
    }

    // Note: these private methods are suggestions or hints on how to structure your
    // code. However, since they're private, you're not obligated to implement exactly
    // these methods: feel free to change or modify these methods however you want. The
    // important thing is that your 'computeRelevance' method ultimately returns the
    // correct answer in an efficient manner.

    /**
     * Return a dictionary mapping every single unique word found
     * in every single document to their IDF score.
     */
    private IDictionary<String, Double> computeIdfScores(ISet<Webpage> pages) {
        IDictionary<String, Integer> wordCount = new ChainedHashDictionary<>();
        IDictionary<String, URI> checkedWordInURI = new ChainedHashDictionary<>();
        int totalDocs = pages.size();

        for (Webpage page : pages) {
            IList<String> words = page.getWords();
            for (String word : words) {
                if (!wordCount.containsKey(word)) {
                    wordCount.put(word, 1);
                    checkedWordInURI.put(word, page.getUri());
                } else if (checkedWordInURI.get(word) != page.getUri()) { //first time seeing on this URI
                    wordCount.put(word, wordCount.get(word) + 1);
                    checkedWordInURI.put(word, page.getUri());
                }
            }
        }

        IDictionary<String, Double> scores = new ChainedHashDictionary<>();
        for (KVPair<String, Integer> pair : wordCount) {
            String word = pair.getKey();
            double idf = Math.log((double) totalDocs / (double) pair.getValue());
            scores.put(word, idf);
        }

        return scores;
    }

    /**
     * Returns a dictionary mapping every unique word found in the given list
     * to their term frequency (TF) score.
     *
     * The input list represents the words contained within a single document.
     */
    private IDictionary<String, Double> computeTfScores(IList<String> words) {
        int totalWords = words.size();

        IDictionary<String, Double> scores = new ChainedHashDictionary<>();
        for (String word : words) {
            if (!scores.containsKey(word)) {
                scores.put(word, 1.0 / totalWords);
            } else {
                scores.put(word, (scores.get(word) * totalWords + 1) / totalWords);
            }
        }

        return scores;
    }

    /**
     * See spec for more details on what this method should do.
     */
    private IDictionary<URI, IDictionary<String, Double>> computeAllDocumentTfIdfVectors(ISet<Webpage> pages) {
        // Hint: this method should use the idfScores field and
        // call the computeTfScores(...) method.

        IDictionary<URI, IDictionary<String, Double>> computedTfIdfVectors = new ChainedHashDictionary<>();

        for (Webpage page : pages) {
            IDictionary<String, Double> tfScores = computeTfScores(page.getWords());
            IDictionary<String, Double> tfIdfVectors = new ChainedHashDictionary<>();
            for (KVPair<String, Double> pair : tfScores) {
                String word = pair.getKey();
                double tfScore = pair.getValue();
                double idfScore = this.idfScores.get(word);
                tfIdfVectors.put(word, tfScore * idfScore);
            }

            computedTfIdfVectors.put(page.getUri(), tfIdfVectors);
        }

        return computedTfIdfVectors;
    }

    /**
     * Returns the cosine similarity between the TF-IDF vector for the given query and the
     * URI's document.
     *
     * Precondition: the given uri must have been one of the uris within the list of
     *               webpages given to the constructor.
     */
    public Double computeRelevance(IList<String> query, URI pageUri) {
        IDictionary<String, Double> documentVector = this.documentTfIdfVectors.get(pageUri);
        IDictionary<String, Double> queryTfVector = this.computeTfScores(query);
        IDictionary<String, Double> queryTfIdfVector = new ChainedHashDictionary<>();

        for (KVPair<String, Double> pair : queryTfVector) {
            String word = pair.getKey();
            if (this.idfScores.containsKey(word)) {
                double tfScore = pair.getValue();
                double idfScore = this.idfScores.get(word);
                queryTfIdfVector.put(word, tfScore * idfScore);
            } else {
                queryTfIdfVector.put(word, 0.0);
            }
        }

        double numerator = 0.0;
        double docWordScore;
        for (KVPair<String, Double> pair : queryTfIdfVector) {
            String word = pair.getKey();
            if (documentVector.containsKey(word)) {
                docWordScore = documentVector.get(word);
            } else {
                docWordScore = 0.0;
            }
            double queryWordScore = queryTfIdfVector.get(word);
            numerator += docWordScore * queryWordScore;
        }

        double denominator = this.documentNormVector.get(pageUri) * norm(queryTfIdfVector);

        if (denominator != 0) {
            return numerator / denominator;
        }

        return 0.0;
    }

    private Double norm(IDictionary<String, Double> vector) {
        double output = 0.0;
        for (KVPair<String, Double> pair : vector) {
            double score = pair.getValue();
            output += (score * score);
        }

        return Math.sqrt(output);
    }
}
