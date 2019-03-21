package search.analyzers;

import datastructures.concrete.ChainedHashSet;
import datastructures.concrete.KVPair;
import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IList;
import datastructures.interfaces.ISet;
import search.models.Webpage;

import java.net.URI;

/**
 * This class is responsible for computing the 'page rank' of all available webpages.
 * If a webpage has many different links to it, it should have a higher page rank.
 * See the spec for more details.
 */
public class PageRankAnalyzer {
    private IDictionary<URI, Double> pageRanks;

    /**
     * Computes a graph representing the internet and computes the page rank of all
     * available webpages.
     *
     * @param webpages  A set of all webpages we have parsed.
     * @param decay     Represents the "decay" factor when computing page rank (see spec).
     * @param epsilon   When the difference in page ranks is less than or equal to this number,
     *                  stop iterating.
     * @param limit     The maximum number of iterations we spend computing page rank. This value
     *                  is meant as a safety valve to prevent us from infinite looping in case our
     *                  page rank never converges.
     */
    public PageRankAnalyzer(ISet<Webpage> webpages, double decay, double epsilon, int limit) {
        // Implementation note: We have commented these method calls out so your
        // search engine doesn't immediately crash when you try running it for the
        // first time.
        //
        // You should uncomment these lines when you're ready to begin working
        // on this class.

        // Step 1: Make a graph representing the 'internet'
        IDictionary<URI, ISet<URI>> graph = this.makeGraph(webpages);

        // Step 2: Use this graph to compute the page rank for each webpage
        this.pageRanks = this.makePageRanks(graph, decay, limit, epsilon);

        // Note: we don't store the graph as a field: once we've computed the
        // page ranks, we no longer need it!
    }

    /**
     * This method converts a set of webpages into an unweighted, directed graph,
     * in adjacency list form.
     *
     * You may assume that each webpage can be uniquely identified by its URI.
     *
     * Note that a webpage may contain links to other webpages that are *not*
     * included within set of webpages you were given. You should omit these
     * links from your graph: we want the final graph we build to be
     * entirely "self-contained".
     */
    private IDictionary<URI, ISet<URI>> makeGraph(ISet<Webpage> webpages) {
        IDictionary<URI, ISet<URI>> graph = new ChainedHashDictionary<>();
        ISet<URI> validLinks = new ChainedHashSet<>();
        for (Webpage page : webpages) {
            validLinks.add(page.getUri());
        }

        for (Webpage page : webpages) {
            ISet<URI> edges = new ChainedHashSet<>();
            IList<URI> links = page.getLinks();
            for (URI link : links) {
                if (validLinks.contains(link) && !edges.contains(link) &&
                    link != page.getUri()) {
                    edges.add(link);
                }
            }

            graph.put(page.getUri(), edges);
        }

        return graph;
    }

    /**
     * Computes the page ranks for all webpages in the graph.
     *
     * Precondition: assumes 'this.graphs' has previously been initialized.
     *
     * @param decay     Represents the "decay" factor when computing page rank (see spec).
     * @param epsilon   When the difference in page ranks is less than or equal to this number,
     *                  stop iterating.
     * @param limit     The maximum number of iterations we spend computing page rank. This value
     *                  is meant as a safety valve to prevent us from infinite looping in case our
     *                  page rank never converges.
     */
    private IDictionary<URI, Double> makePageRanks(IDictionary<URI, ISet<URI>> graph,
                                                   double decay,
                                                   int limit,
                                                   double epsilon) {
        // Step 1: The initialize step should go here
        double totalPages = (double) graph.size();
        IDictionary<URI, Double> oldRanks = new ChainedHashDictionary<>();
        for (KVPair<URI, ISet<URI>> pair : graph) {
            oldRanks.put(pair.getKey(), 1 / totalPages);
        }

        IDictionary<URI, Double> newRanks = new ChainedHashDictionary<>();
        for (int i = 0; i < limit; i++) {
            // Step 2: The update step should go here
            for (KVPair<URI, ISet<URI>> pair : graph) { // step 2.1
                URI uri = pair.getKey();
                newRanks.put(uri, 0.0);
            }

            for (KVPair<URI, ISet<URI>> pair : graph) { // step 2.2
                URI uri = pair.getKey();
                ISet<URI> links = pair.getValue();
                double oldRank = oldRanks.get(uri);

                if (links.size() == 0) { // updating all web pages by d * oldRank / N
                    for (KVPair<URI, ISet<URI>> pairTwo : graph) {
                        URI uriTwo = pairTwo.getKey();
                        double newRank = newRanks.getOrDefault(uriTwo, 0.0);
                        newRank += decay * (oldRank / totalPages);
                        newRanks.put(uriTwo, newRank);
                    }
                } else { // updating all outgoing links by d * oldRank / links.size()
                    for (URI link : links) {
                        double outgoingNewRank = newRanks.getOrDefault(link, 0.0);
                        outgoingNewRank += decay * (oldRank / links.size());
                        newRanks.put(link, outgoingNewRank);
                    }
                }

                double newRank = newRanks.getOrDefault(uri, 0.0);
                newRank += (1 - decay) / totalPages; // add (1-d)/N to every page
                newRanks.put(uri, newRank);
            }

            // Step 3: the convergence step should go here.
            // Return early if we've converged.
            boolean check = true;
            for (KVPair<URI, ISet<URI>> pair : graph) {
                URI uri = pair.getKey();
                double oldRank = oldRanks.get(uri);
                double newRank = newRanks.get(uri);
                if (Math.abs(newRank - oldRank) > epsilon) {
                    check = false;
                }

                oldRanks.put(uri, newRank);
            }

            if (check) { // if all pages converged
                i = limit;
            }
        }

        return newRanks;
    }

    /**
     * Returns the page rank of the given URI.
     *
     * Precondition: the given uri must have been one of the uris within the list of
     *               webpages given to the constructor.
     */
    public double computePageRank(URI pageUri) {
        // Implementation note: this method should be very simple: just one line!
        return this.pageRanks.get(pageUri);
    }
}
